package com.project.csed.smartlearning;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class CourseSearchAdapter extends ArrayAdapter<CourseModel> implements Filterable {

    private ArrayList<CourseModel> originalData;
    private ArrayList<CourseModel> filteredData;
    private LayoutInflater mInflater;
    private ItemFilter mFilter = new ItemFilter();

    public CourseSearchAdapter(Activity context, ArrayList<CourseModel> data) {
        super(context, 0);
        this.filteredData = data ;
        this.originalData = data ;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return filteredData.size();
    }

    public CourseModel getItem(int position) {
        return filteredData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // A ViewHolder keeps references to children views to avoid unnecessary calls
        // to findViewById() on each row.
        ViewHolder holder;

        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.course_search_raw, null);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.name = convertView.findViewById(R.id.course_name);
            holder.year = convertView.findViewById(R.id.course_year);
            holder.teacherName = convertView.findViewById(R.id.teacher_name);

            // Bind the data efficiently with the holder.
            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            holder = (ViewHolder) convertView.getTag();
        }

        // If weren't re-ordering this you could rely on what you set last time
        holder.name.setText(filteredData.get(position).getCourseName());
        holder.year.setText(filteredData.get(position).getYearDate());
        holder.teacherName.setText(filteredData.get(position).getTeacherName());

        return convertView;
    }

    static class ViewHolder {
        TextView name, year, teacherName;
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final ArrayList<CourseModel> list = originalData;

            int count = list.size();
            final ArrayList<CourseModel> nlist = new ArrayList<>(count);

            CourseModel filterableObject ;
            // compare each course name to see if it contains the the entered search keyword
            for (int i = 0; i < count; i++) {
                filterableObject = list.get(i);
                if (filterableObject.getCourseName().toLowerCase().contains(filterString)) {
                    nlist.add(filterableObject);
                }
            }

            //only return courses which contain the entered keyword
            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        //only show courses which contain the entered keyword in the list view
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<CourseModel>) results.values;
            notifyDataSetChanged();
        }

    }
}
