package com.example.shouhei.mlkitdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.shouhei.mlkitdemo.model.Run;
import com.example.shouhei.mlkitdemo.model.RunList;
import com.example.shouhei.mlkitdemo.util.PictureUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";

  private Button mGalleryButton;
  private Button mPhotoButton;
  private ImageView mPhotoView;
  private File mPhotoFile;
  private static final int REQUEST_PHOTO = 0;
  private static final int REQUEST_GALLERY = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate(Bundle) called");
    setContentView(R.layout.activity_main);

    mGalleryButton = findViewById(R.id.gallery_button);
    mGalleryButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Log.d(TAG, "GalleryButton clicked");
            //            Intent pickupImage = new Intent(Intent.ACTION_PICK);
            Intent pickupImage = new Intent(Intent.ACTION_GET_CONTENT);
            pickupImage.setType("image/*");
            startActivityForResult(pickupImage, REQUEST_GALLERY);
          }
        });

    mPhotoButton = findViewById(R.id.photo_button);
    final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    // Verify that the intent will resolve to an activity
    boolean canTakePhoto = captureImage.resolveActivity(getPackageManager()) != null;
    mPhotoButton.setEnabled(canTakePhoto);

    mPhotoButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Log.d(TAG, "PhotoButton clicked");

            Run run = new Run();
            RunList.get(MainActivity.this).addRun(run);
            mPhotoFile = RunList.get(MainActivity.this).getPhotoFile(run);
            Uri uri =
                FileProvider.getUriForFile(
                    MainActivity.this, "com.example.shouhei.mlkitdemo.fileprovider", mPhotoFile);

            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            // TODO change to way to grant permission that is compatible to Android5.0-?
            captureImage.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(captureImage, REQUEST_PHOTO);
          }
        });

    mPhotoView = findViewById(R.id.run_photo);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_PHOTO) {

      Uri uri =
          FileProvider.getUriForFile(
              MainActivity.this, "com.example.shouhei.mlkitdemo.fileprovider", mPhotoFile);
      MainActivity.this.revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
      updatePhotoView();
    } else if (requestCode == REQUEST_GALLERY) {
      Uri uri = data.getData();
      Log.d(TAG, "Schema : " + data.getScheme()); // content
      Log.d(TAG, "Type : " + data.getType()); // null
      Log.d(TAG, "Flag : " + data.getFlags()); // 1
      Log.d(
          TAG,
          "URI : "
              + uri
                  .toString()); // content://com.google.android.apps.photos.contentprovider/0/1/mediakey%3A%2Flocal%253A9ff410cd-a6d7-4f90-8aaf-285b8ce54161/ORIGINAL/NONE/151343972
      Log.d(
          TAG,
          "Authority : " + uri.getAuthority()); // com.google.android.apps.photos.contentprovider
      //      Bitmap bitmap = null;
      //      try {
      //        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
      //      } catch (IOException e) {
      //        e.printStackTrace();
      //      }

      try {
        InputStream stream = this.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(new BufferedInputStream(stream));
        mPhotoView.setImageBitmap(bitmap);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
      // TODO implement copy the file into shared local storage same as photo.
    }
  }

  private void updatePhotoView() {

    if (mPhotoFile == null || !mPhotoFile.exists()) {
      mPhotoView.setImageDrawable(null);
    } else {
      Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), MainActivity.this);
      mPhotoView.setImageBitmap(bitmap);
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
    Log.d(TAG, "onStart() called");
  }

  @Override
  protected void onResume() {
    super.onResume();
    Log.d(TAG, "onResume() called");
  }

  @Override
  protected void onPause() {
    super.onPause();
    Log.d(TAG, "onPause() called");
  }

  @Override
  protected void onStop() {
    super.onStop();
    Log.d(TAG, "onStop() called");
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Log.d(TAG, "onDestroy() called");
  }
}