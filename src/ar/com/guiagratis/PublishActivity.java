package ar.com.guiagratis;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class PublishActivity extends Activity {
	final int TAKE_PICTURE_REQUEST_CODE = 115;
	boolean photoUploaded = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.publish); 
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.
		savedInstanceState.putBoolean("photoUploaded", photoUploaded);	 
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// Restore UI state from the savedInstanceState.
		// This bundle has also been passed to onCreate.
		photoUploaded = savedInstanceState.getBoolean("photoUploaded");

		if (photoUploaded) {
			File photoFile = new File(Environment.getExternalStorageDirectory(),  "Photo.png");
			Uri imageUri = Uri.fromFile(photoFile);

			Bitmap myBitmap = BitmapFactory.decodeFile(imageUri.getPath());
			BitmapDrawable ob = new BitmapDrawable(myBitmap);
			ImageView myImage = (ImageView) findViewById(R.id.uploadedImage);
			myImage.setBackgroundDrawable(ob);		

            // android:layout_width="50dp"
            // android:layout_height="50dp"
		}	
	}

	public void btnSendClick(View v) {
		if (!photoUploaded) {	       
			Toast.makeText(getApplicationContext(), "Todavía no subiste una foto.", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(), "Verifica tus datos. ¿Seguro que quieres subir? ", Toast.LENGTH_SHORT).show();
		}	
	}

	public void btnUploadPhotoClick(View v) {    			
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		File photoFile = new File(Environment.getExternalStorageDirectory(),  "Photo.png");
		Uri imageUri = Uri.fromFile(photoFile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(intent, TAKE_PICTURE_REQUEST_CODE);		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {        
		switch (requestCode) {
		case TAKE_PICTURE_REQUEST_CODE:
			if (resultCode == Activity.RESULT_OK) {
				photoUploaded = true;
			} else {
				Toast.makeText(getApplicationContext(), "Hubo un problema al subir la imágen... ", Toast.LENGTH_SHORT).show();                	
			}
		}
	}
}
