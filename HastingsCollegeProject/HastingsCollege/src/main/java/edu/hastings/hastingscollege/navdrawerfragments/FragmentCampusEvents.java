package edu.hastings.hastingscollege.navdrawerfragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.hastings.hastingscollege.Event;
import edu.hastings.hastingscollege.R;
import edu.hastings.hastingscollege.ServiceHandler;
import edu.hastings.hastingscollege.adapter.CustomEventsAdapter;

public class FragmentCampusEvents extends Fragment {

    private ProgressDialog pDialog;

    private static String url;

    private static final String TAG_EVENTS = "Events";
    private static final String TAG_EVENT = "event";
    private static final String TAG_EVENT_WEEK = "event_week";
    private static final String TAG_EVENT_INFO = "event_info";
    private static final String TAG_EVENTS_OF_DAY = "events_of_day";
    private static final String TAG_DAY = "event_day";
    private static final String TAG_TITLE = "event_title";
    private static final String TAG_LOCATION = "event_location";
    private static final String TAG_TIME = "event_time";

    JSONArray events = null;

    ArrayList<HashMap<String, String>> eventsList;
    private ListView eventsListView;
    View view;

    public static Fragment newInstance(Context context) {return new FragmentCampusEvents(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventsList = new ArrayList<HashMap<String, String>>();
        url = getString(R.string.campus_events_url);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.campus_events, null);

        eventsListView = (ListView) view.findViewById(R.id.event_list);
        eventsListView.addHeaderView(new View(getActivity()));
        eventsListView.addFooterView(new View(getActivity()));
        new GetEvents().execute(url);
        return view;
    }

    private class GetEvents extends AsyncTask<String, Void, ArrayList<Event>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading Events...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected ArrayList<Event> doInBackground(String... urls) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            //String jsonStr = sh.makeServiceCall(urls[0], ServiceHandler.GET);
            String jsonStr = loadJsonFromAssets();

            Log.d("Response: ", "> " + jsonStr);
            ArrayList<Event> eventsArrayList = new ArrayList<Event>();
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    events = jsonObj.getJSONArray(TAG_EVENTS);

                    // looping through All Contacts
                    for (int i = 0; i < events.length(); i++) {
                        JSONObject c = events.getJSONObject(i);

                        List<List<HashMap<String, String>>> eventInformationList = new ArrayList<List<HashMap<String, String>>>();

                        String eventName = c.getString(TAG_EVENT);
                        String eventWeek = c.getString(TAG_EVENT_WEEK);
                        JSONArray eventInfo = c.getJSONArray(TAG_EVENT_INFO);
                        String[] eventDayNames = new String[eventInfo.length()];

                        for (int j = 0; j < eventInfo.length(); j++) {
                            List<HashMap<String, String>> eventDaysList = new ArrayList<HashMap<String, String>>();

                            JSONObject eventDay = eventInfo.getJSONObject(j);
                            String eventDayName = eventDay.getString(TAG_DAY);
                            eventDayNames[j] = eventDayName;

                            JSONArray eventDays = eventDay.getJSONArray(TAG_EVENTS_OF_DAY);
                            for (int k = 0; k < eventDays.length(); k++) {
                                HashMap<String, String> eventOfDayInfoMap = new HashMap<String, String>();
                                JSONObject eventOfDayInfo = eventDays.getJSONObject(k);
                                String eventOfDayTitle = eventOfDayInfo.getString(TAG_TITLE);
                                String eventOfDayLocation = eventOfDayInfo.getString(TAG_LOCATION);
                                String eventOfDayTime = eventOfDayInfo.getString(TAG_TIME);
                                eventOfDayInfoMap.put(TAG_TITLE, eventOfDayTitle);
                                eventOfDayInfoMap.put(TAG_LOCATION, eventOfDayLocation);
                                eventOfDayInfoMap.put(TAG_TIME, eventOfDayTime);
                                eventDaysList.add(eventOfDayInfoMap);
                            }
                            eventInformationList.add(eventDaysList);
                        }
                        Event event = new Event(eventName, eventWeek, eventDayNames, eventInformationList);
                        eventsArrayList.add(event);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
                return null;
            }

            return eventsArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<Event> eventsArrayList) {
            super.onPostExecute(eventsArrayList);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            if (eventsArrayList != null) {
                CustomEventsAdapter adapter = new CustomEventsAdapter(eventsArrayList);
                eventsListView.setAdapter(adapter);
            }
            else {
                eventsListView.setVisibility(View.GONE);
                view.findViewById(R.id.error_text).setVisibility(View.VISIBLE);
            }

        }

        protected String loadJsonFromAssets(){
            String json = null;
            try {
                InputStream is = getActivity().getAssets().open("campus_events.json");
                int size = is.available();
                byte[] buffer = new byte[size];

                is.read(buffer);

                is.close();

                json = new String(buffer, "UTF-8");


            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
            return json;
        }
    }
}