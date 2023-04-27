package hu.polnik.adam.fakeneptun;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;

public class StudentActivity extends AppCompatActivity {
    private static final String LOG_TAG = StudentActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    private FirebaseUser user;

    private FrameLayout redCircle;
    private TextView countTextView;
    private int cartItems = 0;
    private int gridNumber = 1;
    private Integer teacherLimit = 10;

    // Member variables.
    private RecyclerView mRecyclerView;
    private ArrayList<Teacher> mItemsData;
    private TeacherAdapter mAdapter;
    private FirebaseFirestore mFirestore;
    private CollectionReference mTeachers;
    private NotificationHandler notificationHandler;

    private SharedPreferences preferences;


    private boolean viewRow = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "Authenticated user!");
        } else {
            Log.d(LOG_TAG, "Unauthenticated user!");
            finish();
        }

        // preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        // if(preferences != null) {
        //     cartItems = preferences.getInt("cartItems", 0);
        //     gridNumber = preferences.getInt("gridNum", 1);
        // }

        // recycle view
        mRecyclerView = findViewById(R.id.recyclerView);
        // Set the Layout Manager.
        mRecyclerView.setLayoutManager(new GridLayoutManager(
                this, gridNumber));
        // Initialize the ArrayList that will contain the data.
        mItemsData = new ArrayList<>();
        // Initialize the adapter and set it to the RecyclerView.
        mAdapter = new TeacherAdapter(this, mItemsData);
        mRecyclerView.setAdapter(mAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        mTeachers = mFirestore.collection("Teacher");
        queryData();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        this.registerReceiver(powerReceiver, filter);
        notificationHandler=new NotificationHandler(this);

    }

    BroadcastReceiver powerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();

            if (intentAction == null)
                return;

            switch (intentAction) {
                case Intent.ACTION_POWER_CONNECTED:
                    teacherLimit = 10;
                    queryData();
                    break;
                case Intent.ACTION_POWER_DISCONNECTED:
                    teacherLimit = 5;
                    queryData();
                    break;
            }
        }
    };

    private void queryData() {
        mItemsData.clear();
        mTeachers.orderBy("name", Query.Direction.DESCENDING)
                .limit(teacherLimit)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Teacher teacher = document.toObject(Teacher.class);
                        teacher.setId(document.getId());
                        mItemsData.add(teacher);
                    }

                    if (mItemsData.size() == 0) {
                        initializeData();
                        queryData();
                    }

                    // Notify the adapter of the change.
                    mAdapter.notifyDataSetChanged();
                });
    }

    private void initializeData() {
        // Get the resources from the XML file.
        String[] teahcherNames = getResources()
                .getStringArray(R.array.teacher_names);
        String[] itemsInfo = getResources()
                .getStringArray(R.array.teacher_desc);
        String[] teacherOrganization = getResources()
                .getStringArray(R.array.teacher_organization);
        TypedArray teacherImageResources =
                getResources().obtainTypedArray(R.array.teacher_images);
        TypedArray teacherRate = getResources().obtainTypedArray(R.array.teacher_rates);
        String[] rateInNumber = getResources().getStringArray(R.array.teacher_rates);

        for (int i = 0; i < teahcherNames.length; i++) {
            mTeachers.add(new Teacher(
                    teahcherNames[i],
                    itemsInfo[i],
                    teacherOrganization[i],
                    teacherRate.getFloat(i, 0),
                    teacherImageResources.getResourceId(i, 0)));
        }

        // Recycle the typed array.
        teacherImageResources.recycle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.teacher_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG, s);
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out_button:
                Log.d(LOG_TAG, "Logout clicked!");
                FirebaseAuth.getInstance().signOut();
                finish();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.settings_button:
                Log.d(LOG_TAG, "Setting clicked!");
                finish();
                return true;
            case R.id.view_selector:
                if (viewRow) {
                    changeSpanCount(item, R.drawable.ic_view_grid, 1);
                } else {
                    changeSpanCount(item, R.drawable.ic_view_row, 2);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }

    public void deleteTeacher(Teacher teacher) {
        DocumentReference ref = mTeachers.document(teacher._getId());
        ref.delete().addOnSuccessListener(success -> {
            Log.d(LOG_TAG, "Deleted");
        }).addOnFailureListener(fail -> {
            Toast.makeText(this, "Item cannot be deleted", Toast.LENGTH_LONG).show();
        });
        queryData();
    }

    public void upRateTeacher(Teacher teacher) {
        mTeachers.document(teacher._getId()).update("rateInfo", teacher.getRateInfo() + 0.2).addOnFailureListener(fail ->{Toast.makeText(this,"Rate cannot be updated",Toast.LENGTH_LONG).show();});
        notificationHandler.send("Na jó kegyelem kettes XD");
        queryData();
    }
    public void downRateTeacher(Teacher teacher) {
        mTeachers.document(teacher._getId()).update("rateInfo", teacher.getRateInfo() - 0.2).addOnFailureListener(fail ->{Toast.makeText(this,"Rate cannot be updated",Toast.LENGTH_LONG).show();});
        notificationHandler.send("Szerintem bukta van!");
        queryData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(powerReceiver);
    }
}