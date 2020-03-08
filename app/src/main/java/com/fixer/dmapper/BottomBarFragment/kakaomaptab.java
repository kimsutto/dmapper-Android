package com.fixer.dmapper.BottomBarFragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fixer.dmapper.GetGpsCoordinates.GetGpsCoordinates;
import com.fixer.dmapper.KakaoGeoCodingManager.KakaoContainer;
import com.fixer.dmapper.KakaoGeoCodingManager.MapApiConst;
import com.fixer.dmapper.KakaoGeoCodingManager.OnFinishSearchListener;
import com.fixer.dmapper.KakaoGeoCodingManager.Searcher;
import com.fixer.dmapper.MainActivity;
import com.fixer.dmapper.PlaceRequest.LatLngCarrier;
import com.fixer.dmapper.PlaceRequest.PlaceAddRequest;
import com.fixer.dmapper.PlaceRequest.PlaceUpdateRequest;
import com.fixer.dmapper.R;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.List;

class KakaoMapDataProduct {
    private String place_name;
    private String place_address;
    private double x;
    private double y;

    public KakaoMapDataProduct(String place_name, String place_address, double x , double y) {
        this.place_name = place_name;
        this.place_address = place_address;
        this.x = x;
        this.y = y;
    }

    public String getPlace_name() {
        return place_name;
    }

    public String getPlace_address() {
        return place_address;
    }
    public double getx(){
        return x;
    }
    public double gety(){
        return y;
    }

}
class KakaoMapDataViewHolder extends RecyclerView.ViewHolder {

    public TextView place_nameTV;
    public TextView address_nameTV;
    public KakaoMapDataViewHolder(View itemView) {
        super(itemView);

        place_nameTV = (TextView) itemView.findViewById(R.id.place_name);
        address_nameTV = (TextView) itemView.findViewById(R.id.address_name);
    }
}

class KakaoMapDataAdapter extends RecyclerView.Adapter<KakaoMapDataViewHolder>{

    ArrayList<KakaoMapDataProduct> arrayList;
    Context context;
    KakaoMapDataViewHolder holder;
    public void setArrayList(ArrayList<KakaoMapDataProduct> list, Context context) {
        this.arrayList = list;
        this.context = context;
    }

    public interface OnItemClickListener{
        void onItemClick(View v, int pos);
    }
    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    @NonNull
    @Override
    public KakaoMapDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        holder = new KakaoMapDataViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final KakaoMapDataViewHolder holder, int position) {

        KakaoMapDataProduct data = arrayList.get(holder.getAdapterPosition());
        holder.place_nameTV.setText(data.getPlace_name());
        holder.address_nameTV.setText(data.getPlace_address());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(""+holder.getAdapterPosition());
                int itemposition = holder.getAdapterPosition();
                if(itemposition != RecyclerView.NO_POSITION){
                    if(mListener != null){
                        mListener.onItemClick(view,itemposition);
                    }
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

}

public class kakaomaptab extends Fragment implements MapView.MapViewEventListener,MapView.POIItemEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener {

    RecyclerView recyclerView;
    KakaoMapDataAdapter adapter;
    ArrayList<KakaoMapDataProduct> products;

    Button button;
    MapView mapview;
    Button searchbutton;

    View view;
    Animation kakaosearch_translate_up, kakaosearch_translate_down;
    FrameLayout kakaosearchlayout;
    FrameLayout emptyspace;
    ImageView backbutton;
    ImageView query_searchbutton;
    ImageView mylocationbutton;
    EditText editText;
    InputMethodManager imm;

    private MapPOIItem mDefaultMarker;
    GetGpsCoordinates getGpsCoordinates;
    private MapReverseGeoCoder mReverseGeoCoder = null;

    public static String place_name_query_result="";
    public static String address_name_query_result="";
    public static String ReverseGeoCodeValue;

    private static final MapPoint DEFAULT_MARKER_POINT = MapPoint.mapPointWithGeoCoord(37.4020737, 127.1086766);

    public static kakaomaptab newInstance() {
        return new kakaomaptab();
    }

    public kakaomaptab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_kakaomaptab, container, false);

        MainActivity.Map_foreground_selector_kakao = true;

        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.hide();

        ((MainActivity) getActivity()).StartAnimationvisible();
        button = (Button) view.findViewById(R.id.googlebutton);
        mylocationbutton = (ImageView) view.findViewById(R.id.mylocationbutton);
        searchbutton = (Button) view.findViewById(R.id.searchbutton);
        kakaosearch_translate_up = AnimationUtils.loadAnimation(getContext(),R.anim.kakaosearch_translate_up);
        kakaosearch_translate_down = AnimationUtils.loadAnimation(getContext(),R.anim.kakaosearch_translate_down);
        kakaosearchlayout = (FrameLayout)view.findViewById(R.id.searchview_kakaolayout);
        emptyspace = (FrameLayout)kakaosearchlayout.findViewById(R.id.emptyspace);
        backbutton = (ImageView)kakaosearchlayout.findViewById(R.id.backbutton);
        editText = (EditText) kakaosearchlayout.findViewById(R.id.input_search);
        query_searchbutton = (ImageView) kakaosearchlayout.findViewById(R.id.query_searchbutton);
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        recyclerView = (RecyclerView) kakaosearchlayout.findViewById(R.id.List_queryresult);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        products = new ArrayList<>();
        adapter = new KakaoMapDataAdapter();

        mapview = new MapView(getActivity());

        mDefaultMarker = new MapPOIItem();
        mapview.setMapViewEventListener(this);
        mapview.setPOIItemEventListener(this);
        ViewGroup mapViewContainer = (ViewGroup) view.findViewById(R.id.kakao_map_view);

        getGpsCoordinates = new GetGpsCoordinates(getContext());
        mapViewContainer.addView(mapview);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //클릭하면 구글maptab으로 이동하겠다.
                MainActivity.Map_foreground_selector_kakao = false;
                ((MainActivity) getActivity()).replaceFragment(googlemaptab.newInstance());
            }
        });

        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAnimationup();
            }
        });

        mylocationbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapview.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(getGpsCoordinates.getLatitude(), getGpsCoordinates.getLongitude()), 1, true);
                setMarkerCenter(mapview,getGpsCoordinates.getLatitude(),getGpsCoordinates.getLongitude());
            }
        });

        emptyspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                products.clear();
                editText.setText("");
                imm.hideSoftInputFromWindow(editText.getWindowToken(),0);
                startAnimationdown();
            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                products.clear();
                editText.setText("");
                imm.hideSoftInputFromWindow(editText.getWindowToken(),0);
                startAnimationdown();
            }
        });

        query_searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    products.clear();
                    String query = editText.getText().toString();
                    int radius = 20000; // 중심 좌표부터의 반경거리. 특정 지역을 중심으로 검색하려고 할 경우 사용. meter 단위 (0 ~ 10000)
                    int page = 1; // 페이지 번호 (1 ~ 3). 한페이지에 15개
                    //MapPoint.GeoCoordinate geoCoordinate = mapview.getMapCenterPoint().getMapPointGeoCoord(); //좌표 정보 지오코딩.
                    //double latitude = geoCoordinate.latitude; // 위도
                    //double longitude = geoCoordinate.longitude; // 경도
                    String apikey = MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY;
                    //Toast.makeText(getActivity(), "latitude : "+latitude + "longitude : "+longitude , Toast.LENGTH_SHORT).show();
                    Searcher searcher = new Searcher(); // Searcher
                    searcher.searchKeyword(query,radius, page, apikey, new OnFinishSearchListener() {
                        @Override
                        public void onSuccess(List<KakaoContainer> itemList) {
                            showResult(itemList);
                        }

                        @Override
                        public void onFail() {
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        adapter.setOnItemClickListener(new KakaoMapDataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                //System.out.println(products.get(pos).getPlace_name() + " " +products.get(pos).getPlace_name() + " "+ products.get(pos).getx() + " "+ products.get(pos).gety());
                editText.setText("");
                imm.hideSoftInputFromWindow(editText.getWindowToken(),0);
                startAnimationdown();
                onPlaceSelected(products.get(pos).gety(),products.get(pos).getx());
                //System.out.println("name : " + products.get(pos).getPlace_name() + "latitude : " + products.get(pos).gety() + "longitude : " + products.get(pos).getx());
                LatLngCarrier.latitude = products.get(pos).gety();
                LatLngCarrier.longitude = products.get(pos).getx();
                onGetinfo(products.get(pos).getPlace_name(),products.get(pos).getPlace_address());
            }
        });
    }
    private void showResult(List<KakaoContainer> itemList) {
        for (int i = 0; i < itemList.size(); i++) {
            products.add(new KakaoMapDataProduct(itemList.get(i).place_name,itemList.get(i).address_name,itemList.get(i).x, itemList.get(i).y));
        }
        adapter.setArrayList(products, getContext());
        adapter.notifyDataSetChanged();
        adapter.setArrayList(products,getContext());
        recyclerView.setAdapter(adapter);
    }

    public void onGetinfo(String place_name, String address_name){
        place_name_query_result = place_name;
        address_name_query_result = address_name;
    }

    public void onPlaceSelected(double latitude, double longitude){
        mapview.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(latitude,longitude),0,true);
        setMarkerCenter(mapview,latitude,longitude);
    }


    private void startAnimationup(){
        kakaosearchlayout.setVisibility(View.VISIBLE);
        kakaosearchlayout.startAnimation(kakaosearch_translate_up);
    }

    private void startAnimationdown(){
        kakaosearchlayout.setVisibility(View.INVISIBLE);
        kakaosearchlayout.startAnimation(kakaosearch_translate_down);
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(getGpsCoordinates.getLatitude(), getGpsCoordinates.getLongitude()), 1, true);
        setMarkerCenter(mapview,getGpsCoordinates.getLatitude(),getGpsCoordinates.getLongitude());
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapCenterPoint) {
        MapPoint.GeoCoordinate mapPointGeo = mapCenterPoint.getMapPointGeoCoord();
        setMarkerCenter(mapview,mapPointGeo.latitude,mapPointGeo.longitude);
        LatLngCarrier.latitude= mapPointGeo.latitude;
        LatLngCarrier.longitude = mapPointGeo.longitude;
        Log.i("gg", String.format("MapView onMapViewCenterPointMoved (%f,%f)", mapPointGeo.latitude, mapPointGeo.longitude));
    }

    private void setMarkerCenter(MapView mapView, double latitude, double longitude) {
        //private static final MapPoint DEFAULT_MARKER_POINT = MapPoint.mapPointWithGeoCoord(37.4020737, 127.1086766);
        String name = "마커를 움직여 지정하세요";
        mDefaultMarker.setItemName(name);
        mDefaultMarker.setTag(0);
        mDefaultMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));
        mDefaultMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);

        mapview.removeAllPOIItems();
        mapview.addPOIItem(mDefaultMarker);
        mapview.selectPOIItem(mDefaultMarker, true);
        mapview.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), false);
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
        ((MainActivity)getActivity()).StartAnimationDown();
    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
        ((MainActivity)getActivity()).StartAnimationUp();
        mReverseGeoCoder = new MapReverseGeoCoder(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY, mapview.getMapCenterPoint(), kakaomaptab.this, getActivity());
        mReverseGeoCoder.startFindingAddress();

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }


    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        mapReverseGeoCoder.toString();
        onFinishReverseGeoCoding(s);
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
        onFinishReverseGeoCoding("Fail");
    }

    public void onFinishReverseGeoCoding(String result){
        this.ReverseGeoCodeValue = result;
    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
