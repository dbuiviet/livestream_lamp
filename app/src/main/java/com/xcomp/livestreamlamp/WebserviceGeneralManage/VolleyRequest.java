package com.xcomp.livestreamlamp.WebserviceGeneralManage;

import android.app.ActivityManager;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;


public class VolleyRequest {

	private static VolleyRequest instance;
	private static RequestQueue mRequestQueue;
	private static ImageLoader mImageLoader;

	private VolleyRequest() {
		// no instances
	}

	public static VolleyRequest getInstance(Context context) {
		if (instance == null) {
			instance = new VolleyRequest();
			mRequestQueue = Volley.newRequestQueue(context);

			int memClass = ((ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
			// Use 1/8th of the available memory for this memory cache.
			int cacheSize = 1024 * 1024 * memClass / 8;
//			mImageLoader = new ImageLoader(mRequestQueue, new BitmapLruCache(
//					cacheSize));

//            mImageLoader = new ImageLoader(mRequestQueue,
//                    new ImageLoader.ImageCache() {
//                        private final LruCache<String, Bitmap>
//                                cache = new LruCache<String, Bitmap>(20);
//
//                        @Override
//                        public Bitmap getBitmap(String url) {
//                            return cache.get(url);
//                        }
//
//                        @Override
//                        public void putBitmap(String url, Bitmap bitmap) {
//                            cache.put(url, bitmap);
//                        }
//                    });
		}

        return instance;

	}

	public static RequestQueue getRequestQueue() {
		if (mRequestQueue != null) {
			return mRequestQueue;
		} else {
			throw new IllegalStateException("RequestQueue not initialized");
		}
	}

	public static ImageLoader getImageLoader() {
		if (mImageLoader != null) {
			return mImageLoader;
		} else {
			throw new IllegalStateException("ImageLoader not initialized");
		}
	}
}
