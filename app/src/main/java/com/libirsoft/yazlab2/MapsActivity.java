package com.libirsoft.yazlab2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public final int DIALOG_ISTER2 = 1;
    public final int DIALOG_ISTER3 = 2;
    private GoogleMap mMap;
    FirebaseFirestore db;
    ArrayList<Hist_way> list = new ArrayList<>();
    String Firstdate = "";
    String Seconddate = "";
    Geocoder geocoder;
    String zone_1="",zone_2="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
       /* LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        try {
            List<Address> addresses = geocoder.getFromLocationName("rize", 1);
            Address adres = addresses.get(0);
            LatLng rize = new LatLng(adres.getLatitude(), adres.getLongitude());
            mMap.addMarker(new MarkerOptions().position(rize).title("Marker in rize"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(rize));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection("taxi_data");
        switch (item.getItemId()) {
            case R.id.ister1:

                //  Toast.makeText(getApplicationContext(),"merhabalar",Toast.LENGTH_SHORT).show();

                reference.orderBy("trip_distance", Query.Direction.DESCENDING).limit(5).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        String show_text = "";
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("ister1", document.getId() + " >>>>> " + "Mesafe  : " + document.getData().get("trip_distance") +
                                        " «« Alınan gün ve saat :: " + document.getData().get("tpep_pickup_datetime"));
                                show_text = show_text + document.getId() + " >>> " + "Mesafe : " + document.getData().get("trip_distance") +
                                        " «« Alınan gün ve saat :: " + document.getData().get("tpep_pickup_datetime") + "\n";
                            }
                        } else {
                            Log.d("ister1", "Error getting documents: ", task.getException());
                        }
                        showToast(show_text);
                    }
                });
                break;

            case R.id.ister3:

                showDialog(DIALOG_ISTER3);

                break;
            case R.id.ister2_arayuz:

                showDialog(DIALOG_ISTER2);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);


        return true;
    }

    public void showToast(String text) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_root));
        TextView toast_text = layout.findViewById(R.id.toast_text);

        toast_text.setText(text);
        Toast toast = new Toast(MapsActivity.this);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public void show_list(ArrayList<Hist_way> e) {
        String show_text = "";
        for (int i = 0; i < 5; i++) {
            Log.d("Veri: ", "Mesafe: " + e.get(i).getDistance() + "Tarih: " + e.get(i).getDate());
            show_text = show_text + " Mesafe: " + e.get(i).getDistance() + " Tarih: " + e.get(i).getDate() + "\n";

        }
        showToast(show_text);
    }

    public Dialog onCreateDialog(int id) {
        Dialog dialog = null;

        switch (id) {
            case DIALOG_ISTER2:
                dialog = getAddDialog();
                break;
            case DIALOG_ISTER3:
                dialog = getsecondDialog();
                break;

        }


        return dialog;
    }

    private Dialog getsecondDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.ister3_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        Button gönder = view.findViewById(R.id.sendbtn);
        EditText firstdate = view.findViewById(R.id.firstdate);
        EditText seconddate = view.findViewById(R.id.seconddate);

        gönder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Firstdate = firstdate.getText().toString();
                Seconddate = seconddate.getText().toString();
                ister3();
                dialog.dismiss();
            }
        });

        return dialog;
    }

    private Dialog getAddDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.ister2_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        Button gönder = view.findViewById(R.id.sendbtn);
        EditText firstdate = view.findViewById(R.id.firstdate);
        EditText seconddate = view.findViewById(R.id.seconddate);

        gönder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Firstdate = firstdate.getText().toString();
                Seconddate = seconddate.getText().toString();
                Ister_2();
                dialog.dismiss();
            }
        });

        return dialog;
    }

    public void Ister_2() {
        Query q1 = db.collection("taxi_data")
                .orderBy("tpep_pickup_datetime")
                .startAt(Firstdate);
        q1.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                DocumentSnapshot firstdata = queryDocumentSnapshots.getDocuments().get(0);

                Query q2 = db.collection("taxi_data")
                        .orderBy("tpep_pickup_datetime")
                        .startAfter(firstdata)
                        .endAt(Seconddate);
                q2.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Hist_way hist_way = new Hist_way();
                                hist_way.setDate(document.getData().get("tpep_pickup_datetime").toString());
                                hist_way.setDistance(Double.parseDouble(document.getData().get("trip_distance").toString()));
                                list.add(hist_way);

                            }
                            list.sort(new Comparator<Hist_way>() {
                                @Override
                                public int compare(Hist_way o1, Hist_way o2) {
                                    return (o1.getDistance() < o2.getDistance()) ? -1 : (o1.getDistance() > o2.getDistance() ? 1 : 0);
                                }
                            });

                            show_list(list);
                        } else {
                            Log.d("Error", "Error getting documents: ", task.getException());
                        }

                    }
                });
            }
        });

    }

    public void  ister3(){

        Query q1 = db.collection("taxi_data")
                .orderBy("tpep_pickup_datetime")
                .startAt(Firstdate);
        q1.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                DocumentSnapshot firstdata = queryDocumentSnapshots.getDocuments().get(0);

                Query q2 = db.collection("taxi_data")
                        .orderBy("tpep_pickup_datetime")
                        .startAfter(firstdata)
                        .endAt(Seconddate);
                q2.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Hist_way hist_way = new Hist_way();
                                hist_way.setDate(document.getData().get("tpep_pickup_datetime").toString());
                                hist_way.setDistance(Double.parseDouble(document.getData().get("trip_distance").toString()));
                                hist_way.setDolLOC(Integer.parseInt(document.getData().get("DOLocationID").toString()));
                                hist_way.setPulLOC(Integer.parseInt(document.getData().get("PULocationID").toString()));
                                list.add(hist_way);

                            }
                            list.sort(new Comparator<Hist_way>() {
                                @Override
                                public int compare(Hist_way o1, Hist_way o2) {
                                    return (o1.getDistance() < o2.getDistance()) ? -1 : (o1.getDistance() > o2.getDistance() ? 1 : 0);
                                }
                            });
                            Log.d("olsun","dol Loc id :"+list.get(0).DolLOC+"Pul loc id : "+list.get(0).getPulLOC());
                            Query q3 = db.collection("Locations")
                                   .whereEqualTo("LocationID",list.get(0).getPulLOC());
                            q3.get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                   // Log.d("zone1", document.getId() + " => " + document.getData());
                                                   zone_1= ""+document.getData().get("Zone").toString();
                                                    Log.d("zone1", zone_1);
                                                }
                                            } else {
                                                //Log.d("asd", "Error getting documents: ", task.getException());
                                            }
                                            Query q4 = db.collection("Locations")
                                                    .whereEqualTo("LocationID",list.get(0).getDolLOC());
                                            q4.get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                                   // Log.d("zone2", document.getId() + " => " + document.getData());
                                                                    zone_2= ""+document.getData().get("Zone").toString();
                                                                    Log.d("zone2", zone_2);
                                                                }
                                                            } else {
                                                                //Log.d("asd", "Error getting documents: ", task.getException());
                                                            }
                                                            try {
                                                                List<Address> addresses = geocoder.getFromLocationName(zone_1, 1);
                                                                Address adres = addresses.get(0);
                                                                LatLng zone1 = new LatLng(adres.getLatitude(), adres.getLongitude());
                                                                mMap.addMarker(new MarkerOptions().position(zone1).title("Marker in "+zone_1));
                                                              //  mMap.moveCamera(CameraUpdateFactory.newLatLng(rize));
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }
                                                            try {
                                                                List<Address> addresses = geocoder.getFromLocationName(zone_2, 1);
                                                                Address adres = addresses.get(0);
                                                                LatLng zone2 = new LatLng(adres.getLatitude(), adres.getLongitude());
                                                                mMap.addMarker(new MarkerOptions().position(zone2).title("Marker in "+zone_2));
                                                                //mMap.moveCamera(CameraUpdateFactory.newLatLng(rize));

                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }

                                                        }

                                                    });
                                        }

                                    });

                        } else {
                            Log.d("Error", "Error getting documents: ", task.getException());
                        }

                    }
                });
            }
        });


    }
}