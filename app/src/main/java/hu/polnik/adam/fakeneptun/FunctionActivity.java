package hu.polnik.adam.fakeneptun;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class FunctionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);
    }

    public void listTeachers(View view) {
        Intent intent = new Intent(this, StudentActivity.class);
        startActivity(intent);

    }
}