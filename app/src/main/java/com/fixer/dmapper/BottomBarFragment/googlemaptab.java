package com.fixer.dmapper.BottomBarFragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.fixer.dmapper.GetGpsCoordinates.GetGpsCoordinates;
import com.fixer.dmapper.MainActivity;
import com.fixer.dmapper.PlaceRequest.LatLngCarrier;
import com.fixer.dmapper.PlaceRequest.PlaceAddRequest;
import com.fixer.dmapper.PlaceRequest.PlaceUpdateRequest;
import com.fixer.dmapper.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.List;

public class googlemaptab extends Fragment implements OnMapReadyCallback {

    Button autocomplete;

    GetGpsCoordinates getGpsCoordinates;
    PlacesFieldSelector fieldSelector;
    //PlaceAutocompleteFragment autocompleteFragment;
    MapView mapview;
    ImageView mylocationbutton;
    Button kakaobutton;
    public GoogleMap gMap;
    public LatLng SEOUL;
    public static LatLng selectLatLng;
    private LatLng CenterLocation = new LatLng(0,0);
    private Marker markerCenter;
    public static View view;

    public static String ReverseGeoCodeValue;//핀 위치에서 얻어낸 addressname

    public static String place_name_query_result="";//결과를 통해 얻어낸 Place_name
    public static String address_name_query_result="";//결과를 통해 얻어낸 addressname

    public int ERRORCOMPLETECODE = 1;
    public static googlemaptab newInstance() {
        return new googlemaptab();
    }

    public googlemaptab() {
        // Required empty public constructor
    }

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /**
         * registerFragment 내에 kakaoFragment googleFragment 즉, 이중Fragment이다 보니까
         * 생명주기 관리, inflate 관리가 매우 까다로움
         * 특히 카카오tab을 Init으로 하고 구글tab은 버튼클릭으로 이동해야 할때는
         * PlaceAutocompleteFragment 검색 이후 destroy돼서 kakaomap으로 이동해버림 그래서 그냥 googlemap을 init으로함
         * 이때도 문제가 굉장히 많이 발생했으나 밑에로 해결 하지만 완전 해결은 아니다
         * kakaotab을 다녀왔을때 검색이 한번에 되지 않는 문제가 있다(다른 fragment다녀올땐 한번에 됌)
         * 두번 해야 검색이 되지만 critical하지는 않다. 그러므로 일단 다른 기능부터 구현하자
         */
        view = inflater.inflate(R.layout.fragment_googlemaptab, container, false);
        init_bindView();

        if(!Places.isInitialized()){
            Places.initialize(getContext(),getString(R.string.google_app_key));//보완성때문에 바꿈
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        kakaobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //클릭하면 카카오maptab으로 이동하겠다.
                MainActivity.Map_foreground_selector_google = false;
                ((MainActivity) getActivity()).replaceFragment(kakaomaptab.newInstance());
            }
        });
        mylocationbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMyLocation();
            }
        });
        autocomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent autocompleteIntent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldSelector.getAllFields())
                        .build(getActivity());
                startActivityForResult(autocompleteIntent, 10);
                ERRORCOMPLETECODE = 0;
            }
        });

        mapview.getMapAsync(this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        if (resultCode == AutocompleteActivity.RESULT_OK) {

            MainActivity.Map_foreground_selector_kakao= false;
            Place place = Autocomplete.getPlaceFromIntent(intent);
            selectLatLng = place.getLatLng();
            place_name_query_result = place.getName().toString();
            address_name_query_result = place.getAddress().toString();
            mapview.getMapAsync(this);
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(intent);
        } else if (resultCode == AutocompleteActivity.RESULT_CANCELED) {
            // The user canceled the operation.
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    public void init_bindView(){
        autocomplete = (Button)view.findViewById(R.id.autocomplete_button);
        mapview = (MapView) view.findViewById(R.id.google_map_view);
        fieldSelector = new PlacesFieldSelector();
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.hide();

        ((MainActivity) getActivity()).StartAnimationvisible();

        getGpsCoordinates = new GetGpsCoordinates(getContext());

        MainActivity.Map_foreground_selector_google = true;
        SEOUL = new LatLng(getGpsCoordinates.getLatitude(), getGpsCoordinates.getLongitude());
        mylocationbutton = (ImageView) view.findViewById(R.id.mylocationbutton);
        kakaobutton = (Button) view.findViewById(R.id.kakaobutton);

    }

    @Override
    public void onStart() {
        super.onStart();
        mapview.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        //mapview.onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mapview != null) {
            mapview.onCreate(savedInstanceState);
        }

    }

    //getMapAsync호출시 호출되는 method
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        Log.i("omMapReady호출","onMapReady호출"+SEOUL);

        if(SEOUL == null){
            Toast.makeText(getActivity(), "위치 정보가 들어오지 않음", Toast.LENGTH_SHORT).show();
        }else {

            if(ERRORCOMPLETECODE == 1) {
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 20.0f));
                gMap.animateCamera(CameraUpdateFactory.zoomTo(20));
                setMarkerCenter();
                getCenterLocation();
                gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        //Toast.makeText(getActivity(), "가운데"+CenterLocation.latitude, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });
                ERRORCOMPLETECODE = 0;
            }else{
                if(selectLatLng == null){
                    LatLng mylocation = new LatLng(getGpsCoordinates.getLatitude(), getGpsCoordinates.getLongitude());
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation, 20.0f));
                    gMap.animateCamera(CameraUpdateFactory.zoomTo(20));
                    setMarkerCenter();
                    getCenterLocation();
                }else {
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectLatLng, 20.0f));
                    gMap.animateCamera(CameraUpdateFactory.zoomTo(20));
                    setMarkerCenter();
                    getCenterLocation();
                }
            }
        }
    }
    //mylocation으로 이동하는 method
    private void getMyLocation() {
        selectLatLng = new LatLng(getGpsCoordinates.getLatitude(), getGpsCoordinates.getLongitude());
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectLatLng,20));
        gMap.animateCamera(CameraUpdateFactory.zoomTo(17));
    }

    private void getCenterLocation(){
        gMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                ((MainActivity)getActivity()).StartAnimationUp();
                CenterLocation = cameraPosition.target;
                getAddressname(markerCenter.getPosition().latitude,markerCenter.getPosition().longitude);
            }
        });
    }
    //마커를 무조건 가운데에 놓는 method
    public void setMarkerCenter(){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(gMap.getCameraPosition().target);
        markerCenter = gMap.addMarker(new MarkerOptions()
                .position(gMap.getCameraPosition().target)
                //.title("Spot")
                //.snippet("gg")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        gMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                ((MainActivity)getActivity()).StartAnimationInvisible();
                markerCenter.setPosition(gMap.getCameraPosition().target);
                Log.i("position","latitude"+markerCenter.getPosition().latitude+" longitude"+markerCenter.getPosition().longitude);

                LatLngCarrier.latitude = markerCenter.getPosition().latitude;
                LatLngCarrier.longitude = markerCenter.getPosition().longitude;
                //핀을 가운데에 놓고 핀의 위치를 이용해서 위도 경도 뽑아옴 카카오맵이랑 다름.
            }
        });
    }

    public void getAddressname(double latitude, double longitude){
        Geocoder gc = new Geocoder(getContext());

        if(gc.isPresent()){
            try {
                List<Address> list = gc.getFromLocation(latitude,longitude,10);

                if(list != null && list.size()!=0 ) {
                    ReverseGeoCodeValue = list.get(0).getAddressLine(0);
                }else{
                    Toast.makeText(getActivity(), "핀을 이동시켜 위치를 지정하세요", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //PlaceAutocompleteFragment가 누적되는 오류 binary xml inflate exception 해결을 위해서 화면 나가면 제거함.
    /*
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Toast.makeText(getContext(), "googledestroy", Toast.LENGTH_SHORT).show();
        if(autocompleteFragment!= null){
            getActivity().getFragmentManager().beginTransaction().remove(autocompleteFragment).commit();
        }
    }
*/
}
