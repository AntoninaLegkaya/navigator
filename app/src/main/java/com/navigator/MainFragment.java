/**
 * Copyright 2015-present Amberfog
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navigator;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;


import com.navigator.customElements.MarkerPlaceInfoFrame;
import com.navigator.interfaces.Observer;
import com.navigator.model.LocationModel;
import com.navigator.model.MarkerPlace;
import com.navigator.model.Place;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainFragment extends Fragment implements Observer, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        SlidingUpPanelLayout.PanelSlideListener, LocationListener, HeaderAdapter.ItemClickListener {

    private static final String ARG_LOCATION = "arg.location";
    private static final String TAG = MainFragment.class.getSimpleName();
    public static final int TYPE_MAIN_POINT = 0;
    public static final int TYPE_LAST_POIN = 1;
    public static final int TYPE_MEDIAT_POIN = 2;
    public static final int TYPE_NONE = 3;
    @Bind(R.id.list)
    LockableRecyclerView mListView;
    @Bind(R.id.slidingLayout)
    SlidingUpPanelLayout mSlidingUpPanelLayout;
    @Bind(R.id.transparentView)
    View mTransparentView;
    @Bind(R.id.whiteSpaceView)
    View mWhiteSpaceView;


    private HeaderAdapter mHeaderAdapter;
    private LatLng mLocation;
    private MarkerPlace mPlaceMarkerA;
    private MarkerPlace mPlaceMarkerB;
    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private boolean mIsNeedLocationUpdate = true;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationModel mLocationModel;



    private ArrayList<Place> mPlaces = new ArrayList<Place>();

    public MainFragment() {

    }


    public static MainFragment newInstance(LatLng location) {
        Log.i(TAG, "Create new Instance Main Fragment");

        MainFragment f = new MainFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_LOCATION, location);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: compose viewGroup fragment_main");
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        Log.i(TAG, "onCreateView: add listView to fragment_main");
        mListView = ButterKnife.findById(rootView, R.id.list);
        mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
        Log.i(TAG, "onCreateView: add sliding Panel to fragment_main");
        mSlidingUpPanelLayout = ButterKnife.findById(rootView, R.id.slidingLayout);
        mSlidingUpPanelLayout.setEnableDragViewTouchEvents(true);
//        mProgress = ButterKnife.findById(rootView, R.id.progressInd);


        int mapHeight = 250;
        //getResources().getDimensionPixelSize(R.dimen.map_height);

        Log.i(TAG, "onCreateView: Set height sliding Panel: " + mapHeight);
        mSlidingUpPanelLayout.setPanelHeight(mapHeight); // you can use different height here
        mSlidingUpPanelLayout.setScrollableView(mListView, mapHeight);
        mSlidingUpPanelLayout.setPanelSlideListener(this);

        // transparent view at the top of ListView
        Log.i(TAG, "onCreateView: transparent view at the top of RecycleView ");
        mTransparentView = ButterKnife.findById(rootView, R.id.transparentView);
        mWhiteSpaceView = ButterKnife.findById(rootView, R.id.whiteSpaceView);
        Log.i(TAG, "onCreateView: expand Map ");

        expandMap();


        mSlidingUpPanelLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mSlidingUpPanelLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mSlidingUpPanelLayout.onPanelDragged(0);
            }
        });

        ButterKnife.bind(this, rootView);
        Toast.makeText(TheApp.getAppContext(), "Wait, Map is initializing....Composing Map", Toast.LENGTH_SHORT).show();


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.i(TAG, "onActivityCreated: Added Map into mapContainer");
        mMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mapContainer, mMapFragment, "map");
        fragmentTransaction.commit();


        Log.i(TAG, "onActivityCreated: getMapAsync(this)");
        mMapFragment.getMapAsync(this);
        Log.i(TAG, "onActivityCreated: Compose GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        Toast.makeText(TheApp.getAppContext(), "Wait, Map is initializing....GoogleApiClient", Toast.LENGTH_SHORT).show();

    }

    private void composePlaceRecycleView(LatLng latLng, String placeInfo) {
        Log.i(TAG, "composePlaceRecycleView: Compose test data for RecycleView");


        if (mPlaces.isEmpty()) {
            if (placeInfo == null) {
                placeInfo = "Откуда...";
            }
            Place place = new Place(placeInfo, TYPE_MAIN_POINT, latLng);
            mPlaces.add(0, place);
        } else {

            int type = TYPE_NONE;

            boolean mainTypePointPresent = false;
            Log.i(TAG, "Check List item for type MAIN POINT!");
            for (Place pl : mPlaces) {
                if (pl.getType() == TYPE_MAIN_POINT) {
                    Log.i(TAG, " MAIN POINT Finded!");
                    mainTypePointPresent = true;

                }

            }

            if (mainTypePointPresent) {

                for (Place pl : mPlaces) {
                    switch (pl.getType()) {
                        case TYPE_MAIN_POINT: {
                            Log.i(TAG, "Find MAIN POINT");
                            if (pl.getAddress() == "Откуда..." && placeInfo != null) {
                                type = TYPE_MAIN_POINT;
                                Place place = new Place(placeInfo, type, latLng);
                                mPlaces.remove(pl);
                                mPlaces.add(0, place);
                                type = TYPE_NONE;
                                Log.i(TAG, "Update MAIN POINT");
                                break;
                            }
                            Log.i(TAG, "Change MAIN POINT type --->TYPE LAST POIN ");
                            type = TYPE_LAST_POIN;
                            if (placeInfo == null) {
                                placeInfo = "Куда...";
                            }

                            break;
                        }
                        case TYPE_LAST_POIN: {
                            Log.i(TAG, "Find LAST POINT");

                            if (pl.getAddress() == "Kуда..." && placeInfo != null) {
                                type = TYPE_LAST_POIN;
                                Place place = new Place(placeInfo, type, latLng);
                                mPlaces.remove(pl);
                                mPlaces.add(mPlaces.size() - 1, place);
                                type = TYPE_NONE;
                                break;
                            }
                            Log.i(TAG, "Change LAST POINT type --->TYPE MEDIAT POIN ");
                            type = TYPE_MEDIAT_POIN;
                            if (placeInfo == null) {
                                placeInfo = "Could not get Address";
                            }

                        }
                    }
                }


                if (type != TYPE_NONE) {
                    Log.i(TAG, "Created Place Item with type: " + type);
                    Place place = new Place(placeInfo, type, latLng);
                    mPlaces.add(place);
                }
            } else {

                if (placeInfo == null) {
                    placeInfo = "Откуда...";
                }
                Place place = new Place(placeInfo, TYPE_MAIN_POINT, latLng);
                mPlaces.add(0, place);

            }


        }


        mHeaderAdapter = new HeaderAdapter(getActivity(), mPlaces, this);

        mHeaderAdapter.setOnItemClickListener(new HeaderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                String address = mPlaces.get(position - 1).getAddress();
                Log.i(TAG, "HeaderAdapter On Item Click: Move to possition for address " + mPlaces.get(position - 1).getAddress());
//                moveToLocation(mPlaces.get(position - 1).getLatLng(), true);
//                mHeaderAdapter.deleteItem(position);
                Toast.makeText(TheApp.getAppContext(), address, Toast.LENGTH_SHORT).show();
            }
        });
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mListView.setLayoutManager(layoutManager);
        mListView.setAdapter(mHeaderAdapter);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
// MapWrapperLayout initialization
        // 39 - default marker height
        // 20 - offset between the default InfoWindow bottom edge and it's content bottom edge
//        mapWrapperLayout.init(mMap, getPixelsFromDp(TheApp.getAppContext(), 39 + 20));
        Activity activity = getActivity();
        Log.i(TAG, "onMapReady: Check if we were successful in obtaining the map");

        if (mMap != null) {
            Toast.makeText(TheApp.getAppContext(), "Wait, Map is ready....", Toast.LENGTH_SHORT).show();
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            if (ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                //false
                Log.i(TAG, "onMapReady: Set My LocationEnabled: TRUE");
                mMap.setMyLocationEnabled(true);
            }
            Log.i(TAG, "onMapReady: Compose Map Setting");
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setRotateGesturesEnabled(true);
            mMap.getUiSettings().setTiltGesturesEnabled(true);
            LatLng update = getLastKnownLocation();
            if (update != null) {
                Log.i(TAG, "onMapReady: Move Camera into new mPlacePosition");
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(update, 19f)));
            }
            Log.i(TAG, "onMapReady: Set Listener on Map");
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    Log.i(TAG, "onMapReady: Set  mIsNeedLocationUpdate: TRUE");
                    mIsNeedLocationUpdate = true;
                    Log.i(TAG, "onMapReady: (moveToLocation): moveCamera flag: TRUE");
                    moveToLocation(latLng, true);
                }
            });
        }

    }

    private LatLng getLastKnownLocation() {
        Log.i(TAG, "getLastKnownLocation(): call--> getLastKnownLocation(isMoveMarker: TRUE");
        return getLastKnownLocation(true);
    }

    private LatLng getLastKnownLocation(boolean isMoveMarker) {


        LocationManager lm = (LocationManager) TheApp.getAppContext().getSystemService(Context.LOCATION_SERVICE);

        mLocationModel = LocationModel.getInstanceLocationModel();
        if (mLocationModel != null) {
            Log.i(TAG, "Register Observer for get Location");

            mLocationModel.registerObserver(this);
            Log.i(TAG, "Start get Location");
            mLocationModel.startGetLocation(getActivity(), lm);
        }

        if (mLocationModel != null) {

            Log.i(TAG, "getLastKnownLocation(boolean isMoveMarker): Get Location! ");
            LatLng latLng = new LatLng(mLocationModel.getLatitude(), mLocationModel.getLongitude());
            Log.i(TAG, "getLastKnownLocation(boolean isMoveMarker): Check Move Marker: " + isMoveMarker);
            if (isMoveMarker) {
                moveMarker(latLng);
            }
            return latLng;
        }
        return null;
    }

    private void moveMarker(LatLng latLng) {
        Log.i(TAG, "moveMarker: Lat:" + latLng.latitude + " Lon: " + latLng.longitude);
        initMapMarkerOptions(latLng);
    }

    private void initMapMarkerOptions(final LatLng latLng) {
        final Address address = getPlaceInfo(latLng);
        String addInfo = null;
        String addressLine = "";
        if (address != null) {
            addInfo = address.getAddressLine(0);
            addressLine = address.getAddressLine(0) +
                    ',' + address.getAddressLine(1) + "," + address.getAdminArea() + "," + address.getCountryName();
//            Log.i(TAG, "Address line for search coordinats: " + "\n" + addressLine);
        } else {

            Toast.makeText(TheApp.getAppContext(), "Could not get Adderss", Toast.LENGTH_SHORT).show();
        }

        if (mPlaceMarkerA != null) {
            if (mPlaceMarkerA.isFlag()) {
                mPlaceMarkerA.getPoint().remove();
                mPlaceMarkerA.updateMarkerOptions(latLng, addInfo);
            }
        } else {
            mPlaceMarkerA = new MarkerPlace(TheApp.getAppContext(), BitmapDescriptorFactory.HUE_MAGENTA, latLng, mMap,
                    addInfo);

            composePlaceRecycleView(latLng, addInfo);

            mPlaceMarkerA.setOnItemClickListener(new MarkerPlace.OnItemClickListener() {
                @Override
                public void onInfoFrameClick(View infoFrame, Button buttonView) {
                    MarkerPlaceInfoFrame frame = (MarkerPlaceInfoFrame) infoFrame.getRootView();
                    if (frame.isClickable()) {
                        if (mPlaceMarkerA.getAddress() != null) {
                            Log.i(TAG, "Added new Place in List! address: " + mPlaceMarkerA.getAddress());
//                            buttonView.setTextColor(Color.TRANSPARENT);
                            frame.setClickable(false);
                            frame.getButtonView().setTextColor(Color.TRANSPARENT);

                            composePlaceRecycleView(mPlaceMarkerA.getLatLng(), mPlaceMarkerA.getAddress());
                        }
                    }
                }
            });

        }

//        if (addInfo != null) {

//        }


//        if (mPlaceMarkerB != null) {
//
//            if (mPlaceMarkerB.isFlag()) {
//                mPlaceMarkerB.getPoint().remove();
//                mPlaceMarkerB.updateMarkerOptions(latLng, mPlaceMarkerB.getPoint(), addInfo);
//            }
//        } else {
//
//            LatLng coord = getLocationByAddress(addressLine);
//            if (coord != null) {
//                Log.i(TAG, "Get coordinats for Second point: "+"\n" +"Latitude: " +coord.latitude + "Lontitude: " +coord.longitude);
//                mPlaceMarkerB = new MarkerPlace(BitmapDescriptorFactory.HUE_GREEN, coord, mMap,
//                        addInfo);
//                mPlaceMarkerB.initMarkerPoint();
//            }
//        }


    }


    private void moveToLocation(Location location) {
        if (location == null) {
            return;
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        moveToLocation(latLng);
    }

    private void moveToLocation(LatLng latLng) {
        moveToLocation(latLng, true);
    }

    private void moveToLocation(LatLng latLng, final boolean moveCamera) {

        if (latLng == null) {
            return;
        }
        moveMarker(latLng);
        mLocation = latLng;
        mListView.post(new Runnable() {
            @Override
            public void run() {
                if (mMap != null && moveCamera) {
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(mLocation, 11.0f)));
                }
            }
        });
    }

    private void collapseMap() {
        if (mHeaderAdapter != null) {
            mHeaderAdapter.showSpace();
        }
        mTransparentView.setVisibility(View.GONE);
        if (mMap != null && mLocation != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLocation, 11f), 1000, null);
        }
        mListView.setScrollingEnabled(true);
    }

    private void expandMap() {
        if (mHeaderAdapter != null) {
            mHeaderAdapter.hideSpace();
        }
        mTransparentView.setVisibility(View.INVISIBLE);
        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14f), 1000, null);
        }
        mListView.setScrollingEnabled(false);
    }

    @Override
    public void onPanelSlide(View view, float v) {
    }

    @Override
    public void onPanelCollapsed(View view) {
        expandMap();
    }

    @Override
    public void onPanelExpanded(View view) {
        collapseMap();
    }

    @Override
    public void onPanelAnchored(View view) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (mIsNeedLocationUpdate) {
            moveToLocation(location);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setNumUpdates(1);
        Activity activity = getActivity();

        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onItemClicked(int position) {
        mSlidingUpPanelLayout.collapsePane();
    }

    public LatLng getLocationByAddress(String address) {
        List<Address> geocodeMatches = null;

        try {
            geocodeMatches =
                    new Geocoder(getActivity()).getFromLocationName(
                            address, 1);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (!geocodeMatches.isEmpty()) {
            double latitude = geocodeMatches.get(0).getLatitude();
            double longitude = geocodeMatches.get(0).getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            return latLng;
        }


        return null;

    }

    public Address getPlaceInfo(LatLng latLng) {

        List<Address> geocodeMatches = null;
        String Address1;
        String Address2;
        String State;
        String Zipcode;
        String Country;
        String address;
        Activity activity = getActivity();
        // 1- int maxResults
        try {
            geocodeMatches = new Geocoder(activity).getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (geocodeMatches != null) {
            if (!geocodeMatches.isEmpty()) {
                Address1 = geocodeMatches.get(0).getAddressLine(0);
                Address2 = geocodeMatches.get(0).getAddressLine(1);
                State = geocodeMatches.get(0).getAdminArea();
                Zipcode = geocodeMatches.get(0).getPostalCode();
                Country = geocodeMatches.get(0).getCountryName();


                return geocodeMatches.get(0);
            }
        }
        return null;
    }

    private void showProgress(final boolean show) {

//        mProgress = (ProgressBar) getActivity().findViewById(R.id.progressInd);
//        if (mProgress != null) {
//            mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
//        } else {
//
//            Log.i(TAG, "showProgress: fail");
//        }
    }

    @Override
    public void onStarted(Object model) {
//        showProgress(true);
    }

    @Override
    public void onSucceeded(Object model) {
        Log.i(TAG, "onLocationSucceeded");
        Toast.makeText(TheApp.getAppContext(), "Location gets", Toast.LENGTH_SHORT).show();
//        showProgress(false);

        mLocationModel.stopGetLocation();


    }

    @Override
    public void onFailed(Object model) {
        Log.i(TAG, "onLocationFailed");
//        showProgress(false);
        Toast.makeText(TheApp.getAppContext(), "Could not get location. Start task again!", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onFailedProvider(Object model) {

        Toast.makeText(TheApp.getAppContext(), "Provider absent", Toast.LENGTH_SHORT).show();


    }

    @Override
    public LoaderManager getLoaderManager() {
        return super.getLoaderManager();
    }


    @Override
    public void onResume() {
        super.onResume();
        // In case Google Play services has since become available.

    }

    @Override
    public void onStart() {
        super.onStart();
        // Connect the client.
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        // Disconnecting the client invalidates it.
        mGoogleApiClient.disconnect();
        mLocationModel.getmObservable().unregisterAll();
        super.onStop();
    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
