package com.example.watermarkvideo;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FFmpeg fFmpeg;
    EditText upTestVideoName;
    EditText upTestImageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        upTestVideoName = findViewById(R.id.videoName);
        upTestImageName = findViewById(R.id.imageName);
        try {
            loadFFmpegLibrary();
            loadVideo();
            loadWaterMark();
        } catch (FFmpegNotSupportedException e) {
            e.printStackTrace();
        }

        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    124);
        } else {
            try {
                executeCommand("ffmpeg -i" + upTestVideoName.getText().toString() + "-i" +
                        upTestImageName.getText().toString() + " -filter_complex \"overlay=10:10\"  test1.mp4");
            } catch (FFmpegCommandAlreadyRunningException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadWaterMark() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), 123);
    }


    private void loadVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), 124);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 124 || requestCode == 123) {
                // Get the Video from data
                Uri selectedVideo = data.getData();
                Cursor cursor = getContentResolver().query(selectedVideo, null, null, null);
                if (cursor != null) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    cursor.moveToFirst();
                    if (requestCode == 123) {
                        upTestImageName.setText(cursor.getString(nameIndex));
                    } else {
                        upTestVideoName.setText(cursor.getString(nameIndex));
                    }
                    cursor.close();
                }
            }
        }
    }

    public void loadFFmpegLibrary() throws FFmpegNotSupportedException {
        if (fFmpeg == null) {
            fFmpeg = FFmpeg.getInstance(this);
            fFmpeg.loadBinary(new FFmpegLoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    Toast.makeText(getApplicationContext(), "Library Failed to load", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess() {
                    Toast.makeText(getApplicationContext(), "Library loaded successfully!", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {

                }
            });
        }
    }

    public void executeCommand(final String command) throws
            FFmpegCommandAlreadyRunningException {
        fFmpeg.execute(new String[]{command}, new ExecuteBinaryResponseHandler() {
            @Override
            public void onSuccess(String message) {
                super.onSuccess(message);
            }

            @Override
            public void onProgress(String message) {
                super.onProgress(message);
            }

            @Override
            public void onFailure(String message) {
                super.onFailure(message);
            }

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }
}