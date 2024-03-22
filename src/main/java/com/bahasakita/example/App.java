package com.bahasakita.example;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.bahasakita.segmentor.*;

public class App {

    public static byte[] audioPadding(byte[] audio){
        int pad = 6400;
        int size = audio.length;
        if ( size < pad){
            ByteBuffer buffer = ByteBuffer.allocate(pad);
            buffer.put(audio);
            System.out.println( "bytes is lacking, size : "+(pad - size));
        
            for(int i = 0; i < pad - size; i++){
             
                buffer.put((byte)0);
            }
            return buffer.array();
        }

        return audio;
    }

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

        //AudioProcessing audioProcess = new AudioProcessing(handler);
        //audioProcess.processing("silent.wav");
        //audioProcess.processing("bca.wav");
        //audioProcess.processing("stereo.wav");
        // audioProcess.processing("/home/satria/Documents/QMT/TAR-AUG-11-16khz.wav");

        // Example Audio Padding 
        byte[] buff = new byte[10];        
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("test-0.wav"));        
            audioInputStream.read(buff);
        } catch (IOException | UnsupportedAudioFileException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Size : "+buff.length);
        buff = audioPadding(buff);
        System.out.println("Size : "+buff.length);
        
    }

}
