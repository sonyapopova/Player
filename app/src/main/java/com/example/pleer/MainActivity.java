package com.example.pleer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
@SuppressLint("UseSwitchCompatOrMaterialCode")

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private final String DATA_STREAM = "https://radiopotok.mobi/yumor-fm";
    private final String DATA_SD = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "/music.mp3";
    private String nameAudio = "";

    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private Toast toast;

    private TextView textOut;
    private Switch switchLoop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textOut = findViewById(R.id.textOut);
        switchLoop = findViewById(R.id.switchLoop);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        switchLoop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mediaPlayer != null)
                    mediaPlayer.setLooping(isChecked);
            }
        });
    }

    public void onClickSource(View view) throws IOException {
        releaseMediaPlayer();

        try{
        switch (view.getId()){
            case R.id.btnStream:
                toast = Toast.makeText(this, "Запущен поток аудио", Toast.LENGTH_SHORT);
                toast.show();
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(DATA_STREAM);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.prepareAsync();
                nameAudio = "Радио";
                break;
            case R.id.btnRaw:
                toast = Toast.makeText(this, "Запущен аудио-файл с памяти телефона", Toast.LENGTH_SHORT);
                toast.show();
                mediaPlayer = MediaPlayer.create(this, R.raw.audiofale);
                mediaPlayer.start();
                nameAudio = "Запрос";
                break;
            case R.id.btnSD:
                toast = Toast.makeText(this, "Запущен аудио-файл с SD-карты", Toast.LENGTH_SHORT);
                toast.show();
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(DATA_SD);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepare();
                mediaPlayer.start();
                break;
        }
    }catch (IOException e){
            e.printStackTrace();
            toast = Toast.makeText(this, "Источник информации не найден", Toast.LENGTH_SHORT);
            toast.show();
        }
        if (mediaPlayer == null) return;
        mediaPlayer.setLooping(switchLoop.isChecked());
        mediaPlayer.setOnCompletionListener(this);

    }

    public void onClick(View view){
        if (mediaPlayer == null) return;

        switch (view.getId()){
            case R.id.btnResume:
                if (!mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                }
                break;
            case R.id.bntPause:
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                }
                break;
            case R.id.btnStop:
                mediaPlayer.stop();
                break;
            case R.id.btnForward:
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 5000);
                break;
            case R.id.btnBack:
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 5000);
                break;
        }
        textOut.setText(nameAudio + "\n(проигрывание ) " + mediaPlayer.isPlaying() + ", время " + mediaPlayer.getCurrentPosition()
                + "\nповтор " + mediaPlayer.isLooping() + ", громкость " + audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + ")");
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        toast = Toast.makeText(this, "Старт медиа-плейера", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        toast = Toast.makeText(this, "Отключение медиа-плейера", Toast.LENGTH_SHORT);
        toast.show();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        releaseMediaPlayer();
    }
    private void releaseMediaPlayer(){
        if (mediaPlayer != null){
            try{
                mediaPlayer.release();
                mediaPlayer = null;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                onBackPressed();
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                event.startTracking();
                return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    Toast.makeText(this, "Нажата кнопка громкости", Toast.LENGTH_SHORT)
                .show();
        return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                onBackPressed();
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                event.startTracking();
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                Toast.makeText(this, "Нажата кнопка громкости", Toast.LENGTH_SHORT)
                        .show();
                return false;
        }
        return super.onKeyUp(keyCode, event);
    }

}