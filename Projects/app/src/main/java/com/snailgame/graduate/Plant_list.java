package com.snailgame.graduate;
  
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List; 

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 
import com.android.volley.DefaultRetryPolicy;  
import com.android.volley.RequestQueue;
import com.android.volley.Response; 
import com.android.volley.VolleyError;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView; 
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.hzw.toolbox.BitmapUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context; 
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.TraceCompat;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater; 
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Plant_list extends Activity{


	private ListView mListView;
	private EfficientAdapter mAdapter;
	private ProgressDialog mProgress;
	private List<DataModel> mDataList;
	private int fadeInDuration = 3000;
	private Animation fadeIn = new AlphaAnimation(0, 1);

	private class DataModel {
		private String mImageUrl;
		private String mId;
		private String mName;
		private String mGood;
		private String mBad;
		private String comment;
		private String place;

		public String getplace() {
			return place;
		}
		public void setplace(String place) {
			this.place = place;
		}
		public String getmBad() {
			return mBad;
		}
		public void setmBad(String mBad) {
			this.mBad = mBad;
		}
		public String getmId() {
			return mId;
		}
		public void setmId(String mId) {
			this.mId = mId;
		}
		public String getImageUrl() {
			return mImageUrl;
		}
		public void setImageUrl(String mImageUrl) {
			this.mImageUrl = mImageUrl;
		}
		public String getName() {
			return mName;
		}
		public void setName(String mName) {
			this.mName = mName;
		}
		public String getGood() {
			return mGood;
		}
		public void setGood(String mGood) {
			this.mGood = mGood;
		}
		public String getcomment() {
			return comment;
		}
		public void setcomment(String comment) {
			this.comment = comment;
		}
	}
 
	JsonObjectRequest jsonObjRequest;
	
    public RequestQueue mVolleyQueue;
	
    public ImageLoader mImageLoader; 
	
    public  class DiskBitmapCache extends DiskBasedCache implements ImageCache {
		 
	    public DiskBitmapCache(File rootDirectory, int maxCacheSizeInBytes) {
	        super(rootDirectory, maxCacheSizeInBytes);
	    }
	 
	    public DiskBitmapCache(File cacheDir) {
	        super(cacheDir);
	    }
	 
	    public Bitmap getBitmap(String url) {
	        final Entry requestedItem = get(url);
	 
	        if (requestedItem == null)
			     return null;
	 
	        return BitmapFactory.decodeByteArray(requestedItem.data, 0, requestedItem.data.length);
	    }
	 
	    public void putBitmap(String url, Bitmap bitmap) {
	        
	    	final Entry entry = new Entry();
	        
			//Down size the bitmap.If not done, OutofMemoryError occurs while decoding large bitmaps.
 			// If w & h is set during image request ( using ImageLoader ) then this is not required.
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Bitmap downSized = BitmapUtil.downSizeBitmap(bitmap, 50);
			
			downSized.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			byte[] data = baos.toByteArray();
	        entry.data = data ; 
			
	        entry.data = BitmapUtil.convertBitmapToBytes(bitmap) ;
	        put(url, entry);
	    }
	} 
    
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.plant_search);
		
		mVolleyQueue = Volley.newRequestQueue(this);

	    fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
	    fadeIn.setDuration(fadeInDuration);



		int max_cache_size = 1000000;
	 	
		//mImageLoader = new ImageLoader(mVolleyQueue, new DiskBitmapCache(getCacheDir(),max_cache_size));
		
		//Memory cache is always faster than DiskCache. Check it our for yourself.
		mImageLoader = new ImageLoader(mVolleyQueue, new BitmapCache(max_cache_size));

		mDataList = new ArrayList<DataModel>();
		
		mListView = (ListView) findViewById(R.id.qingdan);
	 	
		mAdapter = new EfficientAdapter(this);
		mListView.setAdapter(mAdapter);

		
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
				JSONArray items = response.getJSONArray("data");

				mDataList.clear();

				for(int index = 0 ; index < items.length()-1; index++) {
				
					JSONObject jsonObj = items.getJSONObject(index);

				 	DataModel model = new DataModel();
					model.setImageUrl(jsonObj.getString("Sofa"));
					model.setmId(jsonObj.getString("id"));
					model.setName(jsonObj.getString("bar_name"));
					model.setGood(jsonObj.getString("good"));
					model.setmBad(jsonObj.getString("bad"));
					model.setcomment(jsonObj.getString("self_comment"));
					model.setplace(jsonObj.getString("location_detail"));

			     	mDataList.add(model);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
  private void makeSampleHttpRequest() {
		
		String url = "http://172.17.108.1:80/Graduation-Project/Netbar/index.php/MyNetBar?location=suzhou";
		 
		JSONObject jsonObject = new JSONObject();
		 	
		jsonObjRequest = new JsonObjectRequest(url, jsonObject, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try { 
					parseFlickrImageResponse(response);
					mAdapter.notifyDataSetChanged();
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

	private  class EfficientAdapter extends BaseAdapter {
		
      private LayoutInflater mInflater;
      
      public EfficientAdapter(Context context) {
          mInflater = LayoutInflater.from(context);
      }

      public int getCount() {
          return mDataList.size();
      }
      
      public Object getItem(int position) {
          return position;
      }

      public long getItemId(int position) {
          return position;
      }

      public View getView(int position, View convertView, ViewGroup parent) {

          ViewHolder holder;
          if (convertView == null) {
              convertView = mInflater.inflate(R.layout.plant_list, null);
              holder = new ViewHolder();
              holder.image = (ImageView) convertView.findViewById(R.id.plant_image);
              holder.name = (TextView) convertView.findViewById(R.id.plant_name);
              holder.infor = (TextView) convertView.findViewById(R.id.plant_infor);
			  holder.goodbad = (TextView) convertView.findViewById(R.id.goodbad);
			  holder.place = (TextView) convertView.findViewById(R.id.place);

              convertView.setTag(holder);
          } else {
              holder = (ViewHolder) convertView.getTag();
          } 
          holder.name.setText(mDataList.get(position).getName());
          holder.infor.setText(mDataList.get(position).getcomment());
		  holder.place.setText(mDataList.get(position).getplace());
		  holder.goodbad.setText("赞: " + mDataList.get(position).getGood() + "  踩: " + mDataList.get(position).getmBad());

		  AnimationSet animation = new AnimationSet(false); // change to false
		  animation.addAnimation(fadeIn);
		  animation.setRepeatCount(1);

		  Bundle para = new Bundle();
          para.putString("id", mDataList.get(position).getmId());
          final Intent i = new Intent(Plant_list.this,Sick_detail.class);
          i.putExtras(para);


		  holder.image.setAnimation(animation);
          holder.name.setOnClickListener(new View.OnClickListener() { 
  			@Override
  			public void onClick(View v) { 
  				startActivity(i);
  			}
  		});
          holder.infor.setOnClickListener(new View.OnClickListener() { 
  			@Override
  			public void onClick(View v) { 
  				startActivity(i);
  			}
  		});
          holder.image.setOnClickListener(new View.OnClickListener() {
  			@Override
  			public void onClick(View v) {
  				startActivity(i);
  			}
  		});
            return convertView;
      }
      
      class ViewHolder{
            TextView infor;
			TextView name;
		  	TextView goodbad;
		 	 TextView place;
		 	 ImageView image;
      }	
      
	}	
}
