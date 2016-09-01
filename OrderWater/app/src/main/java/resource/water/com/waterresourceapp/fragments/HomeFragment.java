package resource.water.com.waterresourceapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import resource.water.com.waterresourceapp.R;
import resource.water.com.waterresourceapp.activities.OrderPaymentActivity;
import resource.water.com.waterresourceapp.gps.GPSTracker;
import resource.water.com.waterresourceapp.model.Order;
import resource.water.com.waterresourceapp.util.Const;
import resource.water.com.waterresourceapp.util.Utils;

public class HomeFragment extends Fragment implements OnMapReadyCallback, AdapterView.OnItemSelectedListener, View.OnClickListener {


    private GoogleMap mMap;
    private String currentAddress;
    private EditText mEdtSelectCanCount;

    private Spinner mSpnSelectType;
    private Location latLags;
    private TextView mEdtDestinationEdtLocation;
    private String numberOfBottlesCount, typeOfOrder;
    private View view;
    private Button mBtnSubmitOrder;
    private Marker destinationMarker;
    private View fetchCurrentLocation;
    private GPSTracker gpsTracker;
    private ImageView clear;
    private BroadcastReceiver localBroadcastReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_home, container, false);

        localBroadcastReceiver = new LocalBroadcastReceiver();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                localBroadcastReceiver,
                new IntentFilter("DATA"));
        loadMap();
        init();
        loadSpinnerData();
        listeners();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (latLags == null) {
            getLocation();
        }
    }

    private void getLocation() {

        gpsTracker = new GPSTracker(getActivity());
        latLags = gpsTracker.getLocation();
    }

    private void loadMap() {

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void init() {

        mEdtDestinationEdtLocation = (TextView) view.findViewById(R.id.destinationEdtLocation);
        mEdtSelectCanCount = (EditText) view.findViewById(R.id.can_count);
        mSpnSelectType = (Spinner) view.findViewById(R.id.selectType);
        mBtnSubmitOrder = (Button) view.findViewById(R.id.submitOrder);
        fetchCurrentLocation = (View) view.findViewById(R.id.fetchCurrentLocation);
        clear = (ImageView) view.findViewById(R.id.clear);

    }

    private void listeners() {

        mSpnSelectType.setOnItemSelectedListener(this);
        mBtnSubmitOrder.setOnClickListener(this);
        fetchCurrentLocation.setOnClickListener(this);
        mEdtDestinationEdtLocation.setOnClickListener(this);
        clear.setOnClickListener(this);
    }

    private void loadSpinnerData() {

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.type, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnSelectType.setAdapter(typeAdapter);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapSet();
    }

    private String getAddressFromLatLong(Location location) {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        StringBuilder builder = new StringBuilder();

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            //String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            String my = addresses.get(0).getAddressLine(1);
            for (int i = 1; i <= 2; i++) {

                Log.e(Const.TAG, "=====" + addresses.get(0).getAddressLine(i));
                builder.append(addresses.get(0).getAddressLine(i) + "\t");
            }
            Log.e(Const.TAG, "address:" + address + "city:" + city + "state:" + state + "country:" + country + "knownName:" + knownName + "postalCode:" + my);
            return builder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";

    }

    public void mapSet() {

        Log.e(Const.TAG, "In MAP mapSet");
        LatLng mylocation = null;
        if (latLags != null) {
            // Add a marker in Sydney and move the camera
            mylocation = new LatLng(latLags.getLatitude(), latLags.getLongitude());
            currentAddress = getAddressFromLatLong(latLags);
            Log.e(Const.TAG, "" + currentAddress);
            if (currentAddress != null) {
                mEdtDestinationEdtLocation.setText(currentAddress);
            }


        } else {

            mylocation = new LatLng(-34, 151);
        }
        setDestinationMap(mylocation, currentAddress);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        String str = (String) adapterView.getItemAtPosition(i);

        //  Toast.makeText(getActivity(), "inii" + str, Toast.LENGTH_SHORT).show();
        //if (view == mSpnSelectType) {
            typeOfOrder = str;
            // Toast.makeText(getActivity(), "" + typeOfOrder, Toast.LENGTH_SHORT).show();
      //  }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {

        if (view == mBtnSubmitOrder) {

            if(validate()) {

                Intent intent = new Intent(getActivity(), OrderPaymentActivity.class);
                intent.putExtra(Const.ADDRESS, mEdtDestinationEdtLocation.getText().toString());
                intent.putExtra(Const.QUANTTIY, mEdtSelectCanCount.getText().toString());
                intent.putExtra(Const.CUS_TYPE, typeOfOrder);
                this.startActivity(intent);
            }
        } else if (view == fetchCurrentLocation) {

            if (gpsTracker != null) {

                latLags = gpsTracker.getLocation();

                if (latLags != null) {
                    mapSet();
                }
            }
        } else if (view == clear) {

            mEdtDestinationEdtLocation.setText("");
            if(destinationMarker != null)
            destinationMarker.remove();
        } else if (view == mEdtDestinationEdtLocation) {

            loadAutoCompleteFragment();

        }

    }

    private boolean validate() {

        if (mEdtDestinationEdtLocation.getText().toString().length()<=0 || mEdtDestinationEdtLocation.getText().toString() == null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.valid_destination), Toast.LENGTH_SHORT).show();
            return false;
        } else if (mEdtSelectCanCount.getText().toString().length()<=0 || mEdtSelectCanCount.getText().toString().length()>4) {
            Toast.makeText(getActivity(), getResources().getString(R.string.valid_quantity), Toast.LENGTH_SHORT).show();
            return false;
        } else if (typeOfOrder.toString().length()<=0 || typeOfOrder == null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.valid_order), Toast.LENGTH_SHORT).show();
            return false;
        } else {

            return true;
        }
    }

    private void loadAutoCompleteFragment() {

        AutoCompleteDialogFragment g = new AutoCompleteDialogFragment();
        g.show(getFragmentManager(), "g");

    }

    public void convertAddress(String address) {
        Geocoder coder = new Geocoder(getActivity());
        if (address != null && !address.isEmpty()) {
            try {
                List<Address> addressList = coder.getFromLocationName(address, 1);
                if (addressList != null && addressList.size() > 0) {
                    double lat = addressList.get(0).getLatitude();
                    double lng = addressList.get(0).getLongitude();

                    Log.e("TAGA", "" + lat + "--" + lng);
                    LatLng latlang = new LatLng(lat, lng);
                    setDestinationMap(latlang, address);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } // end catch
        } // end if
    }

    private void setDestinationMap(LatLng destinationLatLong, String address) {

        BitmapDescriptor iconDesc = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        destinationMarker = mMap.addMarker(new MarkerOptions().position(destinationLatLong).title(address).icon(iconDesc));
        destinationMarker.showInfoWindow();
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destinationLatLong, 16));

       /* Drawable circle = getResources().getDrawable(R.drawable.android_location_icon);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(circle.getIntrinsicWidth(), circle.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        circle.setBounds(0, 0, circle.getIntrinsicWidth(), circle.getIntrinsicHeight());
        circle.draw(canvas);
        BitmapDescriptor bd = BitmapDescriptorFactory.fromBitmap(bitmap);
       // if(source.equalsIgnoreCase("source")) {
            destinationMarker = mMap.addMarker(new MarkerOptions().position(destinationLatLong).title(address).icon(bd));
            destinationMarker.showInfoWindow();*/

        /*/}else {
            destinationMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLong).title(address).icon(bd));
            destinationMarker.showInfoWindow();
        }*/

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destinationLatLong, 13));

    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (localBroadcastReceiver != null) {

            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(localBroadcastReceiver);
        }
    }

    public class LocalBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // safety check
            if (intent == null || intent.getAction() == null) {
                return;
            }

            if (intent.getAction().equals("DATA")) {
                Toast.makeText(getActivity(), "$$$$$$" + intent.getStringExtra("result"), Toast.LENGTH_SHORT).show();
                 mEdtDestinationEdtLocation.setText(intent.getStringExtra("result"));
                convertAddress(intent.getStringExtra("result"));
            }
        }
    }
}
