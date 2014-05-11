package ar.com.guiagratis;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

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
		}	
	}

	public void btnSendClick(View v) {
		if (!photoUploaded) {	       
			Toast.makeText(getApplicationContext(), "Todavía no subiste una foto.", Toast.LENGTH_SHORT).show();
			postData();
		} else {
			Toast.makeText(getApplicationContext(), "Subiendo ... ", Toast.LENGTH_SHORT).show();
			postData();
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
	
	public void postData() {
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    // TODO: change this
	    HttpPost httppost = new HttpPost("http://192.168.1.35/script.php");

	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("id", "12345"));
	        nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        
	        HttpEntity entity = response.getEntity();
	        InputStream is = entity.getContent();
	        
	        String responseString = convertStreamToString(is);
	        
	        JSONObject jObject = new JSONObject(responseString);
	        
	        boolean success = jObject.getBoolean("success");
	        
	        if (success) {
	        	Toast.makeText(getApplicationContext(), "¡Genial! ¡Se subió tu pedido!", Toast.LENGTH_SHORT).show();
	        } else {
	        	Toast.makeText(getApplicationContext(), "Hubo un problema al comunicarse con el servidor, intenta nuevamente.", Toast.LENGTH_SHORT).show();
	        }	       
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    	System.out.println(e.getMessage());
	    } catch (JSONException e) {
	    	
	    }
	} 
	
	private String convertStreamToString(InputStream is) {

	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append((line + "\n"));
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return sb.toString();
	}
}
