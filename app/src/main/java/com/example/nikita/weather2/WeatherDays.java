package com.example.nikita.weather2;

import android.content.Context;

import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.lang.reflect.Array;
import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;



import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class WeatherDays extends AppCompatActivity implements LocationListener, View.OnClickListener{

    private LocationManager locationManager;
    private String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_days2);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);



        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }
    @Override
    public void onLocationChanged(Location location) {
        new RequestApiLoc().execute(location.getLatitude(),location.getLongitude());



    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onClick(View v) {

    }


    public class RequestApiLoc extends AsyncTask<Double, Integer, Document> {

        @Override
        protected Document doInBackground(Double... params) {
            String uri =
                    "http://api.openweathermap.org/data/2.5/forecast/daily?lat=" +params[0] + "&lon=" + params[1]  + "&appid=959aac71583a3ac51ed71051dbaf2e3d&mode=xml&units=metric";
            Document doc = null;
            try {
                URL url = new URL(uri);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();


            }catch (Exception e) {
                e.printStackTrace();
            };

            return doc;
        }
        private ExpandListAdapter ExpAdapter;
        private ArrayList<Group> ExpListItems;
        private ExpandableListView ExpandList;
        @Override
        protected void onPostExecute(Document s) {
            ExpandList = (ExpandableListView) findViewById(R.id.expandableListView);
            ExpListItems = SetStandardGroups(s);
            ExpAdapter = new ExpandListAdapter(WeatherDays.this, ExpListItems);
            ExpandList.setAdapter(ExpAdapter);

        }
        public ArrayList<Group> SetStandardGroups(Document d) {

            ArrayList<String> group_names = new ArrayList<String>();
            for(int i = 0;i<7;i++){
                group_names.add(d.getElementsByTagName("time").item(i).getAttributes().getNamedItem("day").getTextContent());
            }
            String country_names[] = { "Brazil", "Mexico", "Croatia", "Cameroon",
                    "Netherlands", "chile", "Spain", "Australia", "Colombia",
                    "Greece", "Ivory Coast", "Japan", "Costa Rica", "Uruguay",
                    "Italy", "England", "France", "Switzerland", "Ecuador",
                    "Honduras", "Agrentina", "Nigeria", "Bosnia and Herzegovina",
                    "Iran", "Germany", "United States", "Portugal", "Ghana",
                    "Belgium", "Algeria", "Russia", "Korea Republic" };



            ArrayList<Group> list = new ArrayList<Group>();

            ArrayList<Child> ch_list;

            int size = 4;
            int j = 0;

            for (String group_name : group_names) {
                Group gru = new Group();
                gru.setName(group_name);

                ch_list = new ArrayList<Child>();
                for (; j < size; j++) {
                    Child ch = new Child();
                    ch.setName(country_names[j]);

                    ch_list.add(ch);
                }
                gru.setItems(ch_list);
                list.add(gru);

                size = size + 4;
            }

            return list;
        }



    }
    public class Group {

        private String Name;
        private ArrayList<Child> Items;

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            this.Name = name;
        }

        public ArrayList<Child> getItems() {
            return Items;
        }

        public void setItems(ArrayList<Child> Items) {
            this.Items = Items;
        }

    }
    public class Child {

        private String Name;
        private int Image;

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }


    }
    public class ExpandListAdapter extends BaseExpandableListAdapter {

        private Context context;
        private ArrayList<Group> groups;

        public ExpandListAdapter(Context context, ArrayList<Group> groups) {
            this.context = context;
            this.groups = groups;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            ArrayList<Child> chList = groups.get(groupPosition).getItems();
            return chList.get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            Child child = (Child) getChild(groupPosition, childPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) context
                        .getSystemService(context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.child_view, null);
            }
            TextView tv = (TextView) convertView.findViewById(R.id.country_name);


            tv.setText(child.getName().toString());


            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            ArrayList<Child> chList = groups.get(groupPosition).getItems();
            return chList.size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groups.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return groups.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            Group group = (Group) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater inf = (LayoutInflater) context
                        .getSystemService(context.LAYOUT_INFLATER_SERVICE);
                convertView = inf.inflate(R.layout.group_view, null);
            }
            TextView tv = (TextView) convertView.findViewById(R.id.group_name);
            tv.setText(group.getName());
            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }


}
