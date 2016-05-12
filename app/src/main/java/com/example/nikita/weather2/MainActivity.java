package com.example.nikita.weather2;


import android.Manifest;
import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class MainActivity extends AppCompatActivity implements LocationListener, View.OnClickListener {


    private LocationManager locationManager;
    private String provider;
    Button btnActTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);



        }
        btnActTwo = (Button) findViewById(R.id.button);
        btnActTwo.setOnClickListener((View.OnClickListener) this);


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
        TextView myAwesomeTextView = (TextView) findViewById(R.id.textView);


        //myAwesomeTextView.setText(s);

         new RequestApi().execute(location.getLatitude(),location.getLongitude());
        //myAwesomeTextView.setText((CharSequence) doc.getElementsByTagName("temperature").item(0).getAttributes().getNamedItem("value"));


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
        switch (v.getId()) {
            case R.id.button:
                Intent intent = new Intent(this, WeatherDays.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
    public class RequestApi extends AsyncTask<Double, Integer, Document> {

        @Override
        protected Document doInBackground(Double... params) {
            String uri =
                    "http://api.openweathermap.org/data/2.5/weather?lat=" +params[0] + "&lon=" + params[1]  + "&appid=959aac71583a3ac51ed71051dbaf2e3d&mode=xml&units=metric";
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
        @Override
        protected void onPostExecute(Document s) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            TextView myAwesomeTextView = (TextView) findViewById(R.id.textView);
            String t = (s.getElementsByTagName("temperature").item(0).getAttributes().getNamedItem("value").getTextContent().toString());
            myAwesomeTextView.setText(t + " C");
            TextView city = (TextView) findViewById(R.id.textView2);
            city.setText(s.getElementsByTagName("city").item(0).getAttributes().getNamedItem("name").getTextContent().toString());
            TextView date = (TextView) findViewById(R.id.textView6);
            date.setText(s.getElementsByTagName("lastupdate").item(0).getAttributes().getNamedItem("value").getTextContent().toString().split("T")[0]);
            String img = s.getElementsByTagName("weather").item(0).getAttributes().getNamedItem("icon").getTextContent().toString();
            new RequestApiImg().execute("http://openweathermap.org/img/w/" + img + ".png");
        }




    }
    public class RequestApiImg extends AsyncTask<String, Integer, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {

            Bitmap bm = null;
            try {
                URL aURL = new URL(params[0]);
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (IOException e) {

            }
            return bm;
        }
        @Override
        protected void onPostExecute(Bitmap s) {
            ImageView img = (ImageView)findViewById(R.id.imageView);
            img.setImageBitmap(s);
        }




    }
}


