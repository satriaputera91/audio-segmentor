package com.bahasakita.segmentor;

 public interface AudioSegmentorHandler {

    public void onStart();
    
    public void onUtterance(byte[] audio,int channel,float startTime,float length);
    
    public void onFinish();

    public void onTrehsold(double treshold);

 }
