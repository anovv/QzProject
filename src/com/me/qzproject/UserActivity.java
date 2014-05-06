package com.me.qzproject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import eu.janmuller.android.simplecropimage.CropImage;


public class UserActivity extends FragmentActivity {

	private boolean isOwnersProfile;
	public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    private File mFileTemp;
    
    public static final int REQUEST_CODE_GALLERY = 0x1;
    public static final int REQUEST_CODE_CROP_IMAGE = 0x2;

    
	private TextView user_name;
	private TextView user_info;
	private ImageView user_img;
	private Button add_friend;
	private Button unfriend;
	private Button play;
	private Button upload_img;
	private ProgressBar spinner;
	private ProgressBar img_spinner;
	//private ProgressBar full_img_spinner;
	private View content;
	
	int duration;
	
	User user = null;
	
	private String id;
	
	private String img_url;
	private String full_img_url;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);
		//TODO setid
		id = getIntent().getExtras().getString("id");
		
		isOwnersProfile = (id.equals(APIHandler.user_id));
		
		img_url = "users/thumbnails/user_" + id + "_thumbnail.jpg";
		full_img_url = "users/profiles/user_" + id + "_profile.jpg";
		duration = getResources().getInteger(android.R.integer.config_shortAnimTime);
		
		user_name = (TextView) findViewById(R.id.user_name);
		user_info = (TextView) findViewById(R.id.user_info);
		user_img = (ImageView) findViewById(R.id.user_img);
		add_friend = (Button) findViewById(R.id.user_add_friend);
		unfriend = (Button) findViewById(R.id.user_unfriend);
		play = (Button) findViewById(R.id.user_play_game);
		upload_img = (Button) findViewById(R.id.user_upload_img);
		content = findViewById(R.id.user_ll);		
		content.setVisibility(View.INVISIBLE);
		
		spinner = (ProgressBar) findViewById(R.id.user_spinner);
		img_spinner = (ProgressBar) findViewById(R.id.user_img_spinner);
		
		if(isOwnersProfile){
			play.setVisibility(View.GONE);
			add_friend.setVisibility(View.GONE);
			unfriend.setVisibility(View.GONE);
		}else{
			upload_img.setVisibility(View.GONE);
		}
		
		add_friend.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				new AddFriend().execute();
			}
			
		});
		
		unfriend.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				new Unfriend().execute();
			}
			
		});
		
		upload_img.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				String state = Environment.getExternalStorageState();
		    	if (Environment.MEDIA_MOUNTED.equals(state)) {
		    		mFileTemp = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME);
		    	}
		    	else {
		    		mFileTemp = new File(getFilesDir(), TEMP_PHOTO_FILE_NAME);
		    	}
				
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		        photoPickerIntent.setType("image/*");
		        startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY);
			}
			
		});
		
		user_img.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				//showFullSizePhoto2((ImageView) view, UserActivity.this, full_img_spinner, "http://" + APIHandler.ip + ":80/laravel/public/images/" + full_img_url);
				new ShowFullPic("http://" + APIHandler.ip + ":80/laravel/public/images/" + full_img_url, UserActivity.this).execute();
			}
			
		});
		
		new GetUser().execute();
		new DownloadImage(user_img, "http://" + APIHandler.ip + ":80/laravel/public/images/" + img_url, img_spinner).execute();
	}
	
	private void startCropImage() {
		
		
        Intent intent = new Intent(this, CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, mFileTemp.getPath());
        intent.putExtra(CropImage.SCALE, true);

        intent.putExtra(CropImage.ASPECT_X, 2);
        intent.putExtra(CropImage.ASPECT_Y, 2);

        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);

	}
	
	class ShowFullPic extends AsyncTask<String, String, String>{
		
		private String url;
		private Bitmap bitmap;
		private Context context;
		
	    public ShowFullPic(String url, Context context) {
	        this.url = url;
	        this.context = context;
	    }
	    
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        if(spinner != null){
	        	//
	        	spinner.animate().alpha(1f).setDuration(duration).setListener(null);
	        	spinner.setVisibility(View.VISIBLE);
	        }
	    }

	    protected String doInBackground(String... urls) {
	        try {
	            InputStream in = new java.net.URL(url).openStream();
	            bitmap = BitmapFactory.decodeStream(in);
	        } catch (Exception e) {
	        	
	        }
	        return null;
	    }

	    protected void onPostExecute(String result) {
	    	if(bitmap != null){
	    		Dialog dialog = new Dialog(context);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.image_dialog);
				
				ImageView image = (ImageView) dialog.findViewById(R.id.user_fullimage);		
	
				image.setImageBitmap(bitmap);
				dialog.getWindow().setBackgroundDrawable(null);
	
	        	spinner.setVisibility(View.GONE);
				dialog.show();	
	    	}
	    }		
	}
	
	/*public static void showFullSizePhoto(ImageView imageView, Context context) {
		
        //ImageView tempImageView = imageView;

        AlertDialog.Builder imageDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.image_dialog,
                (ViewGroup) ((Activity) context).findViewById(R.id.layout_root));

        //ImageView image = (ImageView) layout.findViewById(R.id.user_fullimage);
        //image.setImageDrawable(tempImageView.getDrawable());        
        imageDialog.setView(layout);         
        AlertDialog dialog = imageDialog.create();  
        dialog.show();          
    }*/
	
	
	
	/*@SuppressLint("NewApi")
	public static void showFullSizePhoto2(ImageView imageView, Context context, ProgressBar spinner, String url) {
		
		
		Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.image_dialog);
		
		ImageView image = (ImageView) dialog.findViewById(R.id.user_fullimage);		

		new DownloadImage(image, url, spinner);	
		
		//image.setBackground(imageView.getDrawable());

		//dialog.getWindow().setBackgroundDrawable(null);

		dialog.show();			
	}*/
	
	class UploadImage extends AsyncTask<String, String, String>{
		
		private Uri uri;
		private Bitmap original;
		private Bitmap scaled;
		private Context context;		
		private boolean res;
		
		UploadImage(Bitmap original, Context context){
			//this.uri = uri;
			this.context = context;
			this.original = original;
		}
		
		@Override
		protected String doInBackground(String... path) {		      
            
            //try{
				//original = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
			//}catch(Exception e){						
			//}
            if(original != null){	            	
            	scaled = getScaledBitmap(original, context);
            	String encoded = getBase64EncodedBitmap(scaled, 100);
            	res = APIHandler.uploadImage(encoded);
            }
            
			return null;			
		}     	
		
		protected void onPostExecute(String unused) {
			if(res){
				//user_img.setImageBitmap(scaled);
				//TODO download thumbnail;
				new DownloadImage(user_img, "http://" + APIHandler.ip + ":80/laravel/public/images/" + img_url, img_spinner).execute();
			}else{
				//hz;
			}
		}
	}
	
	public String getBase64EncodedBitmap(Bitmap bitmap, int quality){
		//quality 0 - 100;
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bao);
        byte[] ba = bao.toByteArray();
        
        String res = new String(Base64.encodeBase64(ba));
        
        return res;
	}
	
	@SuppressLint("NewApi")
	public Bitmap getScaledBitmap(Bitmap original, Context context){
		
		double width = original.getWidth();
        double height = original.getHeight();
        
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int display_width = size.x;
        int display_height = size.y;
        
        int newwidth = Math.min(display_width, display_height) - 100;//TODO make depend on a screen size
        
        double ratio = newwidth/width;
        int newheight = (int)(ratio*height);
                         
        Bitmap result = Bitmap.createScaledBitmap(original, newwidth, newheight, true);
        
        return result;
	}
		
	
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode != RESULT_OK) {
            return;
        }
        Bitmap bitmap;
        
        switch (requestCode){
            case REQUEST_CODE_GALLERY:
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
                    copyStream(inputStream, fileOutputStream);
                    fileOutputStream.close();
                    inputStream.close();

                    startCropImage();
                } catch (Exception e) {}

                break;

            case REQUEST_CODE_CROP_IMAGE:
                String path = data.getStringExtra(CropImage.IMAGE_PATH);
                if (path == null) {
                    return;
                }

                bitmap = BitmapFactory.decodeFile(mFileTemp.getPath());
				new UploadImage(bitmap, this).execute();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);        
    }


    public static void copyStream(InputStream input, OutputStream output) throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }
		
	class GetUser extends AsyncTask<String, String, String>{

		@Override
		protected String doInBackground(String... params) {
			Map<String, String> res = APIHandler.getUserById(id);
			if(res != null){
				user = new User(res);			
			}
			return null;
		}
		
		protected void onPostExecute(String unused) {
			if(user != null){
				user_name.setText(user.name);
				user_info.setText(user.email);
				if(user.isFriend){
					add_friend.setVisibility(View.GONE);
				}else{
					unfriend.setVisibility(View.GONE);
				}
				FriendsActivity.showMessage("", (TextView) findViewById(R.id.user_message), duration);
				FriendsActivity.crossfade(content, spinner, duration);
				
	        }else{
	        	spinner.setVisibility(View.INVISIBLE);//TODO spinner not spinning
	        	FriendsActivity.showMessage("No Connection", (TextView) findViewById(R.id.user_message), duration);
	        }		        	
		}		
	}
	
	class AddFriend extends AsyncTask<String, String, String>{
		
		boolean res;
		
		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        if(spinner != null){
	        	//
	        	spinner.animate().alpha(1f).setDuration(duration).setListener(null);
	        	spinner.setVisibility(View.VISIBLE);
	        }
	    }
		
		@Override
		protected String doInBackground(String... params) {
			res = APIHandler.makeFriendRequest(id);			
			return null;
		}
		
		protected void onPostExecute(String unused) {
        	spinner.setVisibility(View.INVISIBLE);
			if(res){
				//TODO request sent;				
	        }else{
	        	//TODO show error
	        }		        	
		}		
	}
	
	public static class DownloadImage extends AsyncTask<String, String, String> {
		private ImageView bmImage;
		private String url;
		private Bitmap bitmap;
		private ProgressBar img_spinner;
		
	    public DownloadImage(ImageView bmImage, String url, ProgressBar img_spinner) {
	        this.bmImage = bmImage;
	        this.url = url;
	        this.img_spinner = img_spinner;
	    }
	    
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        if(bmImage != null){
	        	bmImage.setVisibility(View.INVISIBLE);
	        }
	        if(img_spinner != null){
	        	img_spinner.setVisibility(View.VISIBLE);
	        }
	    }

	    protected String doInBackground(String... urls) {
	        try {
	            InputStream in = new java.net.URL(url).openStream();
	            bitmap = BitmapFactory.decodeStream(in);
	        } catch (Exception e) {
	        	
	        }
	        return null;
	    }

	    protected void onPostExecute(String result) {
	    	if(img_spinner != null){
    			img_spinner.setVisibility(View.INVISIBLE);
    		}
	    	
	    	if(bitmap != null){	    		
	    		bmImage.setImageBitmap(bitmap);
	    		bmImage.setVisibility(View.VISIBLE);
	    	}else{
	    		//TODO
	    	}
	    }
	}
	
	class Unfriend extends AsyncTask<String, String, String>{
		
		boolean res;		

		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        if(spinner != null){
	        	//
	        	spinner.animate().alpha(1f).setDuration(duration).setListener(null);
	        	spinner.setVisibility(View.VISIBLE);
	        }
	    }
		
		@Override
		protected String doInBackground(String... params) {
			res = APIHandler.unfriend(id);
			return null;
		}
		
		protected void onPostExecute(String unused) {
        	spinner.setVisibility(View.INVISIBLE);
			if(res){
				//TODO request sent;	
				add_friend.setVisibility(View.VISIBLE);
				unfriend.setVisibility(View.GONE);
	        }else{
	        	//TODO show error
	        }		        	
		}		
	}
}
