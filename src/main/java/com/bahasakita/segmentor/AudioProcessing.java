/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bahasakita.segmentor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.bahasakita.segmentor.AudioSegmentor;
import com.bahasakita.segmentor.AudioSegmentorHandler;

public class AudioProcessing {

    private AudioSegmentorHandler handler;
    private Map<Integer, AudioSegmentor> segmentors;
    

    public byte[] audioPadding(byte[] audio){
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

    public AudioProcessing(AudioSegmentorHandler handler) {

        this.handler = handler;
        this.segmentors = new HashMap<Integer, AudioSegmentor>();

    }

    public void processing(String filename) {

        try {
            // System.out.println("filename :" + filename);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filename));
            int bytesPerFrame = audioInputStream.getFormat().getFrameSize();

            if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
                // some audio formats may have unspecified frame size
                // in that case we may read any amount of bytes
                bytesPerFrame = 1;
            }

            System.out.println(audioInputStream.getFormat().toString());

            float sampleRate = audioInputStream.getFormat().getSampleRate();
            int channels = audioInputStream.getFormat().getChannels();            
            byte[] bytes = new byte[(audioInputStream.getFormat().getSampleSizeInBits() / 8) * channels];
            this.handler.onStart();

            if (sampleRate == 16000.0f && bytesPerFrame >= 2 && bytesPerFrame <= 4) {

                ByteArrayOutputStream leftbaos = new ByteArrayOutputStream();
                ByteArrayOutputStream rightbaos = new ByteArrayOutputStream();
                boolean detectionZero = true;
                int detectMinBytes = 0;
                //System.out.println("size : "+bytes.length);
                while (audioInputStream.read(bytes) != -1) {

                    int channel_potition = 1;

                    while (channel_potition < audioInputStream.getFormat().getChannels() + 1) {

                        if (!this.segmentors.containsKey(channel_potition)) {
                            AudioSegmentor segmentor = new AudioSegmentor(this.handler);
                            segmentor.properties(audioInputStream.getFormat());
                            segmentor.set_channel(channel_potition);

                            this.segmentors.put(channel_potition, segmentor);
                            detectionZero = false;
                        }

                        if (channel_potition == 1) {
                            leftbaos.write(bytes, 0, bytes.length / channels);

                            if (leftbaos.size() >= AudioSegmentor.MAX_BUFFER) {
                                detectMinBytes = detectMinBytes + leftbaos.size();
                                this.segmentors.get(channel_potition).segmentation(leftbaos.toByteArray());
                                leftbaos.reset();

                            }
                        } else if (channel_potition == 2) {
                            rightbaos.write(bytes, bytes.length / channels, bytes.length / channels);

                            if (rightbaos.size() >= AudioSegmentor.MAX_BUFFER) {
                                detectMinBytes = detectMinBytes + leftbaos.size();                              
                                this.segmentors.get(channel_potition).segmentation(rightbaos.toByteArray());
                                rightbaos.reset();
                            }

                        }
                        channel_potition++;
                    }
                }

                int channel_potition = 1;
                while (channel_potition < audioInputStream.getFormat().getChannels() + 1) {

                    if (channel_potition == 1) {
                                                                         
                        if (detectionZero || detectMinBytes <= 6400) {

                           System.out.println("DetectZero :"+ detectionZero+", or minimum bytes :"+ detectMinBytes);
                           byte[] leftPadBytes = audioPadding(leftbaos.toByteArray());
                           this.handler.onUtterance(leftPadBytes, channel_potition, 0,0 );
                           leftbaos.reset();                             
                        
                        }  else{
                            if (leftbaos.size() > 0) {
                                this.segmentors.get(channel_potition).segmentation(leftbaos.toByteArray());
                                leftbaos.reset(); 
                            }
                            this.segmentors.get(channel_potition).stopAudio();                            
                        }  

                    } else if (channel_potition == 2) {

                        if (detectionZero || detectMinBytes <= 6400) {
                            System.out.println("DetectZero :"+ detectionZero+", or minimum bytes :"+ detectMinBytes);
                            byte[] rightPadBytes = audioPadding(rightbaos.toByteArray());                           
                            this.handler.onUtterance(rightPadBytes, channel_potition, 0,0 );
                            rightbaos.reset();
                            
                        } else {
                            if (rightbaos.size() > 0) {
                                this.segmentors.get(channel_potition).segmentation(rightbaos.toByteArray());
                                rightbaos.reset();
                            }

                            this.segmentors.get(channel_potition).stopAudio();
                        }
                    }

                    channel_potition++;
                }

                leftbaos.close();
                rightbaos.close();
                this.handler.onFinish();
                
            } else {
                System.out.println("format file not compatible!!!");

            }
            audioInputStream.close();
            this.segmentors.clear();
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(AudioSegmentor.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AudioSegmentor.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void save(String fileName, AudioFileFormat.Type fileFormat, AudioFormat audioFormat, byte[] audio)
            throws IOException {
        try {
            File file = new File(fileName);
            // byte audio[] = new byte[baos.size()];
            // audio = baos.toByteArray();
            // baos.reset();
            // System.out.println("create file wav :" + fileName + "size:" + audio.length);
            AudioInputStream ais = new AudioInputStream((new ByteArrayInputStream(audio)), audioFormat,
                    (audio.length / 2));
            AudioSystem.write(ais, fileFormat, file);

            ais.close();

        } catch (Exception e) {
            // TODO: handle exception
        }

    }
}