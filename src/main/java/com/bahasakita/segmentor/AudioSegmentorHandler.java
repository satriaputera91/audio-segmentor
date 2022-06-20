package com.bahasakita.segmentor;

 public interface AudioSegmentorHandler {

    public void onUtterance(byte[] audio,int channel);

    public void onFinish();

    public void onTrehsold(double treshold);

 }
