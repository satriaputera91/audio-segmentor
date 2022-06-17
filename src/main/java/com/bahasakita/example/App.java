package com.bahasakita.example;

import java.io.IOException;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;

import com.bahasakita.segmentor.*;

public class App {
    public static void main(String[] args) {

        AudioSegmentorHandler handler = new AudioSegmentorHandler() {

            static final private boolean LITTLE_ENDIAN = false;
            static final private int MONO = 1;
            static final private float SAMPLE_RATE = 16000.0f;
            static final private int SAMPLE_SIZE = 16;
            static final private boolean SIGNED = true;

            private int utterance;
            @Override
            public void onUtterance(byte[] audio) {
                
                AudioFormat audioFormat = new AudioFormat(SAMPLE_RATE,
                        SAMPLE_SIZE, MONO, SIGNED, LITTLE_ENDIAN);
                
                        String fileName = "utt-"+utterance+".wav";

                try {
                    AudioSegmentor.save(fileName, AudioFileFormat.Type.WAVE, audioFormat, audio);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                utterance++;
            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onTrehsold(double treshold) {

            }

        };

        AudioSegmentor segmentor = new AudioSegmentor(handler);
        segmentor.processing("/home/satria/Music/FS_2MIN/FS_001.wav");
    }

}
