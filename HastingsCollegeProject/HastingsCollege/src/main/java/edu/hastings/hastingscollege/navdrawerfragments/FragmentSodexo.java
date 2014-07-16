package edu.hastings.hastingscollege.navdrawerfragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.hastings.hastingscollege.Data;
import edu.hastings.hastingscollege.R;
import edu.hastings.hastingscollege.tabfragments.SodexoMenu;

public class FragmentSodexo extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sodexo, container, false);
        ListView mDaysOfWeekList = (ListView) view.findViewById(R.id.days_of_week_list);

        if (Data.mondayMenu.size() > 0) {
            String[] mDaysOfWeek = getResources().getStringArray(R.array.days_of_week);

            final String KEY_ITEM_DATE = "menudate";
            final String KEY_DAY = "dayname";
            final String[] dates = formatDates(Data.dates);
            final List<HashMap<String, String>> daysList = new ArrayList<HashMap<String, String>>();
            for (int i = 0; i < mDaysOfWeek.length; i++) {
                HashMap<String, String> dayOfWeek = new HashMap<String, String>();
                dayOfWeek.put(KEY_DAY, mDaysOfWeek[i]);
                dayOfWeek.put(KEY_ITEM_DATE, dates[i]);
                daysList.add(dayOfWeek);
            }

            String[] from = {KEY_DAY, KEY_ITEM_DATE};
            int[] to = {R.id.day_of_week, R.id.date};

            SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(),
                    daysList,
                    R.layout.list_item_sodexo_week,
                    from,
                    to);
            mDaysOfWeekList.setAdapter(adapter);
            mDaysOfWeekList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String dayName = daysList.get(position).get(KEY_DAY);
                    Intent i = new Intent(getActivity(), SodexoMenu.class);
                    i.putExtra(KEY_DAY, dayName);
                    startActivity(i);
                }
            });

            TextView txtHeaderText = (TextView) view.findViewById(R.id.list_item_menu_header_textview);
            String headerDate = dates[0];
            String headerText = "Menu for the week of: " + headerDate;
            txtHeaderText.setText(headerText);
        }
        else {
            mDaysOfWeekList.setVisibility(View.GONE);
            view.findViewById(R.id.header_layout).setVisibility(View.GONE);
            view.findViewById(R.id.error_text).setVisibility(View.VISIBLE);
        }

        return view;
    }

    private String[] formatDates(String[] dates) {
        SimpleDateFormat fromDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat toDate = new SimpleDateFormat("MMMM dd, yyyy");
        String[] formattedDates = new String[7];

        for (int i=0; i< dates.length; i++) {
            try {
                String reformattedDate = toDate.format(fromDate.parse(dates[i]));
                formattedDates[i] = reformattedDate;
            } catch (ParseException e) {
                Log.v("Sodexo Fragment", e.toString());
            }
        }
        return formattedDates;
    }
}
