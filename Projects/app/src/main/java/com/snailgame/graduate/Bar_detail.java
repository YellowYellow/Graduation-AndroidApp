package com.snailgame.graduate;
 	   
	import org.json.JSONException;
	import org.json.JSONObject;
	 
	import com.android.volley.DefaultRetryPolicy;  
	import com.android.volley.RequestQueue;
	import com.android.volley.Response; 
	import com.android.volley.VolleyError;   
	import com.android.volley.toolbox.ImageLoader;
	import com.android.volley.toolbox.JsonObjectRequest; 
	import com.android.volley.toolbox.Volley;
	import com.android.volley.toolbox.ImageLoader.ImageCache;
	   
	import android.app.Activity;
	import android.app.ProgressDialog; 
import android.content.Intent;
	import android.graphics.Bitmap;
	import android.os.Bundle;   
	import android.support.v4.util.LruCache;
	import android.view.View;
	import android.widget.Button;
	import android.widget.TextView;
import android.widget.Toast;

public class Bar_detail extends Activity{
		 
		 private ProgressDialog mProgress;
	 
	 
		JsonObjectRequest jsonObjRequest;
		
	    public RequestQueue mVolleyQueue;
		
	    public ImageLoader mImageLoader; 
		private String name;
		 
	  @Override
	  protected void onCreate(Bundle savedInstanceState)
	  {
		    super.onCreate(savedInstanceState);
			setContentView(R.layout.bar_detail);

		  	Button order = (Button)findViewById(R.id.order);
		  	order.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(Bar_detail.this,ChairMapActivity.class));
				}
			});

			Intent intent=getIntent();  
	        name=intent.getStringExtra("id");
			
			mVolleyQueue = Volley.newRequestQueue(this);

			int max_cache_size = 1000000;
		 	
			//Memory cache is always faster than DiskCache. Check it our for yourself.
			mImageLoader = new ImageLoader(mVolleyQueue, new BitmapCache(max_cache_size));
 	
			showProgress();
			makeSampleHttpRequest();
	  }

	  public class BitmapCache extends LruCache<String,Bitmap> implements ImageCache {
		    public BitmapCache(int maxSize) {
		        super(maxSize);
		    }
		 
		    @Override
		    public Bitmap getBitmap(String url) {
		        return (Bitmap)get(url);
		    }
		 
		    @Override
		    public void putBitmap(String url, Bitmap bitmap) {
		        put(url, bitmap);
		    }
		}
		public void onDestroy() {
			super.onDestroy();
			
		}
		
		public void onStop() {
			super.onStop();
			if(mProgress != null)
				mProgress.dismiss();
			// Keep the list of requests dispatched in a List<Request<T>> mRequestList;
			/*
			 for( Request<T> req : mRequestList) {
			 	req.cancel();
			 }
			 */
			//jsonObjRequest.cancel();
			//( or )
			//mVolleyQueue.cancelAll(TAG_REQUEST);
		}
			 
		private void showProgress() {
			mProgress = ProgressDialog.show(this, "", "Loading...");
		}
		
		private void stopProgress() {
			mProgress.cancel();
		}
		
		private void showToast(String msg) {
			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		}
		 
	  private void parseFlickrImageResponse(JSONObject response) throws JSONException {
			
			if(response.has("data")) {
				try {
					JSONObject bar = response.getJSONObject("data");

					TextView barname = (TextView)findViewById(R.id.bar_name);
					TextView goodbads = (TextView)findViewById(R.id.goodbads);
					TextView cpu = (TextView)findViewById(R.id.cpu);
					TextView gpu = (TextView)findViewById(R.id.gpu);
					TextView ssd = (TextView)findViewById(R.id.ssd);
					TextView price = (TextView)findViewById(R.id.price);
					TextView place = (TextView)findViewById(R.id.place);
					// ImageView tupian = (ImageView)findViewById(R.id.imageView1);


					barname.setText(bar.getString("bar_name"));
					price.setText(bar.getString("price")+"￥/h");
					place.setText(bar.getString("location_name")+bar.getString("location_detail"));
					goodbads.setText("赞："+bar.getString("good")+"踩："+bar.getString("bad"));
					cpu.setText("CPU配置："+bar.getString("CPU"));
					gpu.setText("GPU配置："+bar.getString("GPU"));
					ssd.setText("SSD配置："+bar.getString("SSD"));
				//	 tupian.setImageURI("http://192.168.191.1/image/sick/"+jsonObj.getString("img")+".png");
				 
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	  private void makeSampleHttpRequest() {
			
			String url = "http://121.42.191.9:8088/Graduation-Project/Netbar/index.php/MyNetBar/getBardetail?id=" + name;

			JSONObject jsonObject = new JSONObject();
			jsonObjRequest = new JsonObjectRequest(url, jsonObject, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
					try { 
						parseFlickrImageResponse(response);
					 	} catch (Exception e) {
						e.printStackTrace();
						showToast("JSON parse error");
					}
					stopProgress();
				}
			}, new Response.ErrorListener() { 
				@Override
				public void onErrorResponse(VolleyError error) {
					error.getMessage();
					stopProgress(); 
				}
			});  
			//Set a retry policy in case of SocketTimeout & ConnectionTimeout Exceptions. Volley does retry for you if you have specified the policy.
			jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
			jsonObjRequest.setTag("Content-Type: application/x-www-form-urlencoded");	
		 
			mVolleyQueue.add(jsonObjRequest);
		}

		 
	}
