package com.fixer.dmapper.BottomBarFragment;

import android.graphics.Bitmap;

public class LookupDataProducts {

   private String place_name;
   private String place_address;
   private Bitmap place_image;
   private String user_name;
   private String upload_date;
   private String platform;
   private double latitude,longitude;
   private String category;
   private String phone, ect_info;
   private boolean entrance , elevator, parking, toilet, seat;
   private boolean kakao,google;


   public LookupDataProducts() {
   }

   public LookupDataProducts(Bitmap place_image, String place_name, String place_address) {
       this.place_image = place_image;
       this.place_name = place_name;
       this.place_address = place_address;
   }

    public LookupDataProducts(String place_name, String place_address, Bitmap place_image, String user_name, String upload_date, String platform, double latitude, double longitude, String category, String phone, String ect_info, boolean entrance, boolean elevator, boolean parking, boolean toilet, boolean seat, boolean kakao, boolean google) {
        this.place_name = place_name;
        this.place_address = place_address;
        this.place_image = place_image;
        this.user_name = user_name;
        this.upload_date = upload_date;
        this.platform = platform;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
        this.phone = phone;
        this.ect_info = ect_info;
        this.entrance = entrance;
        this.elevator = elevator;
        this.parking = parking;
        this.toilet = toilet;
        this.seat = seat;
        this.kakao = kakao;
        this.google = google;
    }

    public String getUser_name() {
       return user_name;
   }

   public void setUser_name(String user_name) {
       this.user_name = user_name;
   }

   public String getUpload_date() {
       return upload_date;
   }

   public void setUpload_date(String upload_date) {
       this.upload_date = upload_date;
   }

   public String getPlatform() {
       return platform;
   }

   public void setPlatform(String platform) {
       this.platform = platform;
   }

   public double getLatitude() {
       return latitude;
   }

   public void setLatitude(double latitude) {
       this.latitude = latitude;
   }

   public double getLongitude() {
       return longitude;
   }

   public void setLongitude(double longitude) {
       this.longitude = longitude;
   }

   public String getCategory() {
       return category;
   }

   public void setCategory(String category) {
       this.category = category;
   }

   public String getPhone() {
       return phone;
   }

   public void setPhone(String phone) {
       this.phone = phone;
   }

   public String getEct_info() {
       return ect_info;
   }

   public void setEct_info(String ect_info) {
       this.ect_info = ect_info;
   }

   public boolean isEntrance() {
       return entrance;
   }

   public void setEntrance(boolean entrance) {
       this.entrance = entrance;
   }

   public boolean isElevator() {
       return elevator;
   }

   public void setElevator(boolean elevator) {
       this.elevator = elevator;
   }

   public boolean isParking() {
       return parking;
   }

   public void setParking(boolean parking) {
       this.parking = parking;
   }

   public boolean isToilet() {
       return toilet;
   }

   public void setToilet(boolean toilet) {
       this.toilet = toilet;
   }

   public boolean isSeat() {
       return seat;
   }

   public void setSeat(boolean seat) {
       this.seat = seat;
   }

   public boolean isKakao() {
       return kakao;
   }

   public void setKakao(boolean kakao) {
       this.kakao = kakao;
   }

   public boolean isGoogle() {
       return google;
   }

   public void setGoogle(boolean google) {
       this.google = google;
   }

   public String getPlace_name() {
       return place_name;
   }

   public void setPlace_name(String place_name) {
       this.place_name = place_name;
   }

   public String getPlace_address() {
       return place_address;
   }

   public void setPlace_address(String place_address) {
       this.place_address = place_address;
   }

   public Bitmap getImage() {
       return place_image;
   }

   public void setImage(Bitmap place_image) {
       this.place_image = place_image;
   }
}
