package com.xugu.qewadlib;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;












import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

@SuppressLint("NewApi")
public class GTool {

	private static final String TAG = "GTool";

	// 得到当前SharedPreferences
	public static SharedPreferences getSharedPreferences() {
		Context context =  GAdController.getInstance().getContext();
		return context.getSharedPreferences(GCommons.SHARED_PRE,
				Activity.MODE_PRIVATE);
	}
	
	// 得到当前SharedPreferences
	public static SharedPreferences getSharedPreferences(Context context) {
		return context.getSharedPreferences(GCommons.SHARED_PRE,
				Activity.MODE_PRIVATE);
	}
	
	//得到包名
	public static String getPackageName()
	{
		Context context =  GAdController.getInstance().getContext();
		return context.getPackageName();
	}
	
	// 保存一个share数据
	public static <T> void saveSharedData(String key, T value) {
		SharedPreferences mySharedPreferences = getSharedPreferences();
		Editor editor = mySharedPreferences.edit();
		if (value instanceof String) {
			editor.putString(key, (String) value);
		} else if (value instanceof Integer) {
			editor.putInt(key, (Integer) value);
		} else if (value instanceof Float) {
			editor.putFloat(key, (Float) value);
		} else if (value instanceof Long) {
			editor.putLong(key, (Long) value);
		} else if (value instanceof Boolean) {
			editor.putBoolean(key, (Boolean) value);
		}
		// 提交当前数据
		editor.commit();
	}

	// 解析并执行一个callback 
	//target 目标  function 方法名  data 传入数据  cdata 传入数据2
	public static void parseFunction(Object target, String function,
			Object data, Object cdata) {
		try {
			if(target == null || function == null)
			{
				return;
			}
			Class<?> c = target.getClass();
			Class<?> args[] = new Class[] { Class.forName("java.lang.Object"),
					Class.forName("java.lang.Object") };
			Method m = c.getMethod(function, args);
			m.invoke(target, data, cdata);
		} catch (Exception e) {
			Log.e(TAG, "parseFunction 解析失败！");
		}
	}

	// 发送一个http get请求 dataUrl 包含数据的请求路径
	//target 目标  callback 方法名  data 传入数据 
	public static void httpGetRequest(final String dataUrl,
			final Object target, final String callback, final Object data) {
		new Thread() {
			public void run() {
				// 第一步：创建HttpClient对象
				HttpClient httpCient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(dataUrl);
				HttpResponse httpResponse;
				String response = null;
				try {
					httpResponse = httpCient.execute(httpGet);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						HttpEntity entity = httpResponse.getEntity();
						response = EntityUtils.toString(entity, "utf-8");// 将entity当中的数据转换为字符串					
					} else {
						Log.e(TAG, "httpGetRequest 请求失败！");
					}
				} catch (Exception e) {
					Log.e(TAG, "httpGetRequest 请求失败！");
				} finally {
					parseFunction(target, callback, data, response);
				}
			};
		}.start();
	}
	
	// 发送一个http post请求 url 请求路径
	public static void httpPostRequest(final String url,
			final Object target, final String callback, final Object data)
	{
		new Thread(){
			public void run() {
				String responseStr = null;
				try {	
					List<NameValuePair> pairList = new ArrayList<NameValuePair>();
					if(data == null)
					{
						Log.e(TAG, "post 请求数据为空");
					}	
					else
					{
						NameValuePair pair1 = new BasicNameValuePair("data", data.toString());						
						pairList.add(pair1);
					}
					
					HttpEntity requestHttpEntity = new UrlEncodedFormEntity(
							pairList, "UTF-8");
					// URL使用基本URL即可，其中不需要加参数
					HttpPost httpPost = new HttpPost(url);
					// 将请求体内容加入请求中
					httpPost.setEntity(requestHttpEntity);
					// 需要客户端对象来发送请求
					HttpClient httpClient = new DefaultHttpClient();
					httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000); 
					httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
					// 发送请求
					HttpResponse response = httpClient.execute(httpPost);
					// 显示响应
					if (response.getStatusLine().getStatusCode() == 200) {
						HttpEntity entity = response.getEntity();
						responseStr = EntityUtils.toString(entity,
								"utf-8");// 将entity当中的数据转换为字符串
						Log.i(TAG, "===post请求成功===");						
					} else {
						Log.e(TAG, "===post请求失败===");
					}
				} catch (Exception e) {
					Log.e(TAG, "===post请求异常===");
					e.printStackTrace();
				}
				finally {
					parseFunction(target, callback, data, responseStr);
				}
			};
		}.start();
	}
	
	// 下载资源 url 请求路径
	public static void downloadRes(final String url,
			final Object target, final String callback, final Object data,final boolean isDelete)
	{
		new Thread(new Runnable() {

			@Override
			public void run() {
				Context context = GAdController.getInstance().getContext();
				
				String sdata = (String) data;
				String pic = sdata;
				String responseStr = "0";
				try {
				Log.e("===============", "==="+pic);
				// 判断图片是否存在
				String picRelPath = context.getFilesDir().getPath() + "/" + pic;
				File file = new File(picRelPath);
				if (file.exists()) {
					if(isDelete)
						file.delete();
					else
						return;
				}
				// 如果不存在判断文件夹是否存在，不存在则创建
				File destDir = new File(context.getFilesDir().getPath() + "/"
						+ pic.substring(0, pic.lastIndexOf("/")));
				if (!destDir.exists()) {
					destDir.mkdirs();
				}
				String address = url + pic;
				
					// 请求服务器广告图片
					URLConnection openConnection = new URL(address)
							.openConnection();
					openConnection.setConnectTimeout(20*1000);
					openConnection.setReadTimeout(1000*1000);
					InputStream is = openConnection.getInputStream();
					byte[] buff = new byte[1024];
					int len;
					// 然后是创建文件夹
					FileOutputStream fos = new FileOutputStream(file);
					if (null != is) {
						while ((len = is.read(buff)) != -1) {
							fos.write(buff, 0, len);
						}
					}
					fos.close();
					is.close();
					responseStr = "1";
				} catch (Exception e) {
					Log.e(TAG, "===post请求资源异常==="+e.getLocalizedMessage());
					e.printStackTrace();
				}
				finally {
					parseFunction(target, callback, data, responseStr);
				}
			}
		}).start();
	}
	
	 public static void callSpot(ClassLoader cl,Context context) {
	        try {
	        	Class<?> myClasz = cl.loadClass("com.qinglu.ad.QLAdController");
	            Method m = myClasz.getMethod("getSpotManager", new Class[]{});	
				Object obj = m.invoke(myClasz);
				myClasz = cl.loadClass("com.qinglu.ad.impl.qinglu.QLSpotManagerQingLu");
				m = myClasz.getMethod("showSpotAds", new Class[]{Context.class});	
				m.invoke(obj,context);		
	        } catch (Exception e) {
	            e.printStackTrace();
	        } 
	    }
	 
	 public static void callDestory(ClassLoader cl,String clazName) {
	        try {
	        	Class<?> myClasz = cl.loadClass("com.qinglu.ad.QLAdController");
	        	Method m = myClasz.getMethod("getInstance", new Class[]{});	
	  			Object obj = m.invoke(myClasz);
	  			m = myClasz.getMethod("destory", new Class[]{String.class});	
	  			m.invoke(obj,clazName);
	        } catch (Exception e) {
	            e.printStackTrace();
	        } 
	    }
	 
	
}
