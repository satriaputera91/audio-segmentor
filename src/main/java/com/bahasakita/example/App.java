package com.bahasakita.example;

import java.io.IOException;
import java.util.UUID;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;

import com.bahasakita.segmentor.*;

public class App {
    public static void main(String[] args) {
     

        AudioSegmentorHandler handler = new AudioSegmentorHandler() {
            static final private String folderAudio = "audio/";
            static final private boolean LITTLE_ENDIAN = false;
            static final private int MONO = 1;
            static final private float SAMPLE_RATE = 16000.0f;
            static final private int SAMPLE_SIZE = 16;
            static final private boolean SIGNED = true;
            private String uuid;
            private int utterance;

            @Override
            public void onStart() {
                utterance = 0;
                uuid = UUID.randomUUID().toString();
            }

            @Override
            public void onUtterance(byte[] audio, int channel,float startTime,float length) {

                AudioFormat audioFormat = new AudioFormat(SAMPLE_RATE,
                        SAMPLE_SIZE, MONO, SIGNED, LITTLE_ENDIAN);

                String fileName = folderAudio+ uuid + "-" + utterance + "-" + channel + ".wav";

                try {
                    System.out.println("Filename :"+fileName+", StartTime :"+startTime+",<==> Length : "+length);
                    AudioProcessing.save(fileName, AudioFileFormat.Type.WAVE, audioFormat, audio);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                utterance++;
            }

            @Override
            public void onTrehsold(double treshold) {

            }

            @Override
            public void onFinish() {
                utterance = 0;

            }

        };

        AudioProcessing audioProcess = new AudioProcessing(handler);
        audioProcess.processing("silent.wav");
        //audioProcess.processing("bca.wav");
        //audioProcess.processing("stereo.wav");
        // audioProcess.processing("/home/satria/Documents/QMT/TAR-AUG-11-16khz.wav");

    }

}
