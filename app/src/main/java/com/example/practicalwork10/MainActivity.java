package com.example.practicalwork10;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {
    private ImageView coverImageView;
    private VideoView coverVideoView;
    private ImageView avatarImageView;
    private ImageView moreOptionsImageView;
    private Boolean changeAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        coverImageView = findViewById(R.id.coverImageView);
        coverVideoView = findViewById(R.id.coverVideoView);
        avatarImageView = findViewById(R.id.avatarImageView);
        moreOptionsImageView = findViewById(R.id.moreOptionsImageView);

        moreOptionsImageView.setOnClickListener(v -> {
            showContextMenu();
        });
    }

    private void showContextMenu() {
        PopupMenu popupMenu = new PopupMenu(this, moreOptionsImageView);
        popupMenu.getMenuInflater().inflate(R.menu.context_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.getAvatarImage:
                    changeAvatar = true;
                    takePhoto();
                    return true;
                case R.id.chooseAvatarImage:
                    chooseFromGallery();
                    changeAvatar = true;
                    return true;
                case R.id.getCoverImage:
                    coverVideoView.setElevation(0);
                    coverImageView.setElevation(1);
                    takePhoto();
                    changeAvatar = false;
                    return true;
                case R.id.chooseCoverImage:
                    coverVideoView.setElevation(0);
                    coverImageView.setElevation(1);
                    chooseFromGallery();
                    changeAvatar = false;
                    return true;
                case R.id.recordVideo:
                    recordVideo();
                    coverVideoView.setElevation(1);
                    coverImageView.setElevation(0);
                    return true;
                default:
                    return false;
            }
        });
        popupMenu.show();
    }

    private final ActivityResultLauncher<Intent> takePhotoLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();
            if (data != null) {
                handleTakenPhoto(data);
            }
        }
    });

    private final ActivityResultLauncher<String> chooseFromGalleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
        if (result != null) {
            handleChosenPhoto(result);
        }
    });

    private final ActivityResultLauncher<Intent> recordVideoLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();
            if (data != null) {
                handleRecordedVideo(data);
            }
        }
    });

    private void takePhoto() {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {
            takePhotoLauncher.launch(takePhotoIntent);
        }
    }

    private void chooseFromGallery() {
        chooseFromGalleryLauncher.launch("image/*");
    }

    private void recordVideo() {
        Intent recordVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (recordVideoIntent.resolveActivity(getPackageManager()) != null) {
            recordVideoLauncher.launch(recordVideoIntent);
        }
    }

    private void handleTakenPhoto(Intent data) {
        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("data");
        if (changeAvatar) {
            avatarImageView.setImageBitmap(imageBitmap);
        } else {
            coverImageView.setImageBitmap(imageBitmap);
        }
    }

    private void handleChosenPhoto(Uri selectedImageUri) {
        if (changeAvatar) {
            avatarImageView.setImageURI(selectedImageUri);
        } else {
            coverImageView.setImageURI(selectedImageUri);
        }
    }

    private void handleRecordedVideo(Intent data) {
        Uri videoUri = data.getData();
        if (videoUri != null) {
            coverVideoView.setVideoURI(videoUri);
            coverVideoView.setOnCompletionListener(mediaPlayer -> coverVideoView.start());
            coverVideoView.start();
        }
    }
}