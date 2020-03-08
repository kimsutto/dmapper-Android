package com.fixer.dmapper.ImageViewPager;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.fixer.dmapper.R;

public class ImageViewPager extends Fragment {

    ImageButton imageButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);

        ImageView imageView = view.findViewById(R.id.imageView);
        imageButton = view.findViewById(R.id.image_remove_btn);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.btn_x);
        requestOptions.error(R.drawable.account);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);
        requestOptions.override(1200,1000);
        if (getArguments() != null) {
            Bundle args = getArguments();
            // MainActivity에서 받아온 Resource를 ImageView에 셋팅
            Glide.with(getActivity()).load("file://" + Uri.parse(args.getString("imgRes"))).thumbnail(0.1f).apply(requestOptions).into(imageView);
            //imageView.setImageURI(Uri.parse(args.getString("imgRes")));
            //imageView.setImageResource(args.getInt("imgRes"));
        }

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return view;
    }
}