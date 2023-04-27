package hu.polnik.adam.fakeneptun;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.ViewHolder> implements Filterable {
    private ArrayList<Teacher> mTeacherData = new ArrayList<>();
    private ArrayList<Teacher> mTeacherDataAll = new ArrayList<>();
    private Context mContext;
    private int lastPosition = -1;

    TeacherAdapter(Context context, ArrayList<Teacher> TeachersData) {
        this.mTeacherData = TeachersData;
        this.mTeacherDataAll = TeachersData;
        this.mContext = context;
    }

    @Override
    public TeacherAdapter.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.list_teacher, parent, false));
    }

    @Override
    public void onBindViewHolder(TeacherAdapter.ViewHolder holder, int position) {
        Teacher currentTeacher = mTeacherData.get(position);
        holder.bindTo(currentTeacher);


        if(holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mTeacherData.size();
    }


    /**
     * RecycleView filter
     * **/
    @Override
    public Filter getFilter() {
        return teacherFilter;
    }

    private Filter teacherFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Teacher> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if(charSequence == null || charSequence.length() == 0) {
                results.count = mTeacherDataAll.size();
                results.values = mTeacherDataAll;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for(Teacher item : mTeacherDataAll) {
                    if(item.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }

                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mTeacherData = (ArrayList)filterResults.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {

        // Member Variables for the TextViews
        private TextView mTitleText;
        private TextView mInfoText;
        private TextView mPriceText;
        private ImageView mItemImage;
        private RatingBar mRatingBar;

        ViewHolder(View itemView) {
            super(itemView);

            // Initialize the views.
            mTitleText = itemView.findViewById(R.id.itemTitle);
            mInfoText = itemView.findViewById(R.id.subTitle);
            mItemImage = itemView.findViewById(R.id.itemImage);
            mRatingBar = itemView.findViewById(R.id.ratingBar);
            mPriceText = itemView.findViewById(R.id.price);
        }

        void bindTo(Teacher currentTeacher){
            mTitleText.setText(currentTeacher.getName());
            mInfoText.setText(currentTeacher.getInfo());
            mPriceText.setText(currentTeacher.getOrganization());
            mRatingBar.setRating(currentTeacher.getRateInfo());

            // Load the images into the ImageView using the Glide library.
            itemView.findViewById(R.id.add_to_cart).setOnClickListener(view -> ((StudentActivity)mContext).upRateTeacher(currentTeacher));
            itemView.findViewById(R.id.downrate).setOnClickListener(view -> ((StudentActivity)mContext).downRateTeacher(currentTeacher));
            itemView.findViewById(R.id.delete).setOnClickListener(view -> ((StudentActivity)mContext).deleteTeacher(currentTeacher));
            Glide.with(mContext).load(currentTeacher.getImageResource()).into(mItemImage);

        }
    }
}
