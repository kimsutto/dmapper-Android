package com.fixer.dmapper.BottomBarFragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fixer.dmapper.Lookup_detail;
import com.fixer.dmapper.MainActivity;
import com.fixer.dmapper.R;

import java.util.ArrayList;


class LookUpDataViewHolder extends RecyclerView.ViewHolder {

    protected ImageView place_img;
    protected TextView place_name;
    protected TextView place_address;

    public LookUpDataViewHolder(View itemView) {
        super(itemView);
        this.place_img = (ImageView) itemView.findViewById(R.id.place_img);
        this.place_name = (TextView) itemView.findViewById(R.id.place_name);
        this.place_address = (TextView) itemView.findViewById(R.id.address_name);

    }
}
class LookupDataAdapter extends RecyclerView.Adapter<LookUpDataViewHolder>{

    private static final String TAG = "RecyclerAdapter";
    ArrayList<LookupDataProducts> place_list;
    ArrayList<LookupDataProducts> place_list_All;
    LookUpDataViewHolder holder;
    Context context;

    public interface OnItemClickListener{
        void onItemClick(View v, int pos);
    }
    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }


    public void setArrayList(ArrayList<LookupDataProducts> place_list,Context context) {
        this.place_list = place_list;
        this.place_list_All = new ArrayList<>(place_list);
        this.context = context;
    }

    @NonNull
    @Override
    public LookUpDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lookup_list_item, parent, false);
        holder = new LookUpDataViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final LookUpDataViewHolder holder, int i) {
        LookupDataProducts data = place_list.get(holder.getAdapterPosition());
        //이미지 없으면 기본 마커이미지로
        if(data.getImage()!=null){
            holder.place_img.setImageBitmap(data.getImage());
        }else{
            holder.place_img.setImageResource(R.drawable.defaultimage);
        }
        holder.place_name.setText(data.getPlace_name());
        holder.place_address.setText(data.getPlace_address());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            int itemposition = holder.getAdapterPosition();
            @Override
            public void onClick(View view) {
                if(itemposition != RecyclerView.NO_POSITION){
                    if(mListener != null){
                        mListener.onItemClick(view,itemposition);
                        Log.i("클릭!","클릭");
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if( place_list != null){
            return place_list.size();
        }
        return 0;
    }

    public void filter(String text){

        if(text.isEmpty()){
            place_list.clear();
            place_list.addAll(place_list_All);
        } else{
            ArrayList<LookupDataProducts> result = new ArrayList<>();
            text = text.toLowerCase();
            for(LookupDataProducts item: place_list_All){
                //match by name or phone
                if(item.getCategory().toLowerCase().contains(text)||item.getPlace_name().toLowerCase().contains(text)){
                    result.add(item);
                }
            }
            place_list.clear();
            place_list.addAll(result);
        }
        notifyDataSetChanged();
    }

}
public class lookupdatatab extends Fragment {
    View view;
    RecyclerView recyclerView;
    LookupDataAdapter lookupDataAdapter;

    public lookupdatatab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_lookupdatatab, container, false);

        init_variable(view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onListItemButtonClick();
    }

    private  void init_variable(View view){
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.show();
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>"+"조회"+"</font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#829FD9")));
        setHasOptionsMenu(true);

        ((MainActivity)getActivity()).StartAnimationgone();

        recyclerView = (RecyclerView) view.findViewById(R.id.place_recy);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        lookupDataAdapter = new LookupDataAdapter();
        if(MainActivity.arrayLoc != null){
            lookupDataAdapter.setArrayList(MainActivity.arrayLoc,getContext());
        }
        recyclerView.setAdapter(lookupDataAdapter);
        lookupDataAdapter.notifyDataSetChanged();

    }

    //누를 시 상세페이지 ...간단하게 바꿔도되는데..굳이굳이임..
    public void onListItemButtonClick(){
        lookupDataAdapter.setOnItemClickListener(new LookupDataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Intent intent = new Intent(getActivity(),Lookup_detail.class);
                String name =lookupDataAdapter.place_list.get(pos).getPlace_name();
                String address = lookupDataAdapter.place_list.get(pos).getPlace_address();

                String user = lookupDataAdapter.place_list.get(pos).getUser_name();
                String date = lookupDataAdapter.place_list.get(pos).getUpload_date();

                String category = lookupDataAdapter.place_list.get(pos).getCategory();
                String phone = lookupDataAdapter.place_list.get(pos).getPhone();
                String etc = lookupDataAdapter.place_list.get(pos).getEct_info();

                double lat = lookupDataAdapter.place_list.get(pos).getLatitude();
                double lon = lookupDataAdapter.place_list.get(pos).getLongitude();

                boolean entrance = lookupDataAdapter.place_list.get(pos).isEntrance();
                boolean seat = lookupDataAdapter.place_list.get(pos).isSeat();
                boolean parking = lookupDataAdapter.place_list.get(pos).isParking();

                boolean toilet = lookupDataAdapter.place_list.get(pos).isToilet();
                boolean elevator = lookupDataAdapter.place_list.get(pos).isElevator();

                boolean kakao = lookupDataAdapter.place_list.get(pos).isKakao();
                boolean google = lookupDataAdapter.place_list.get(pos).isGoogle();

                Bitmap img = lookupDataAdapter.place_list.get(pos).getImage();

                intent.putExtra("name",name);
                intent.putExtra("address",address);
                intent.putExtra("date",date);
                intent.putExtra("userName",user);
                intent.putExtra("category",category);
                intent.putExtra("phone",phone);
                intent.putExtra("etc",etc);
                intent.putExtra("lat",lat);
                intent.putExtra("lon",lon);
                intent.putExtra("entrance",entrance);
                intent.putExtra("seat",seat);
                intent.putExtra("parking",parking);
                intent.putExtra("toilet",toilet);
                intent.putExtra("elevator",elevator);
                intent.putExtra("kakao",kakao);
                intent.putExtra("google",google);
                intent.putExtra("img", img);

                startActivity(intent);
            }
        });
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + "장소명을 입력하세요" + "</font>"));
        ImageView searchClose = (ImageView)searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        searchClose.setColorFilter(Color.argb(255, 255, 255, 255));
        searchClose.setAlpha(255);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                lookupDataAdapter.filter(s);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.category1:
                lookupDataAdapter.filter("");
                break;
            case R.id.category2:
                lookupDataAdapter.filter(item.getTitle().toString());
                break;
            case R.id.category3:
                lookupDataAdapter.filter(item.getTitle().toString());
                break;
            case R.id.category4:
                lookupDataAdapter.filter(item.getTitle().toString());
                break;
            case R.id.category5:
                lookupDataAdapter.filter(item.getTitle().toString());
                break;
            case R.id.category6:
                lookupDataAdapter.filter(item.getTitle().toString());
                break;
            case R.id.category7:
                lookupDataAdapter.filter(item.getTitle().toString());
                break;
            case R.id.category8:
                lookupDataAdapter.filter(item.getTitle().toString());
                break;
            case R.id.category9:
                lookupDataAdapter.filter(item.getTitle().toString());
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}