package com.wise.BaseClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.wise.asd.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

public class UpdateManager extends Context{

	private Context mContext;
	private String mUrl;
	//提示语
	private String updateMsg = "系统检测到有最新的版本，是否下载？";	
	
	private Dialog noticeDialog;
	
	private Dialog downloadDialog;
	 /* 下载包安装路径 */
    private static final String savePath = Environment.getExternalStorageDirectory().getPath() +"/updatedemo/";
    
    private static final String saveFileName = savePath + "UpdateDemoRelease.apk";

    /* 进度条与通知ui刷新的handler和msg常量 */
    private ProgressBar mProgress;

    
    private static final int DOWN_UPDATE = 1;
    
    private static final int DOWN_OVER = 2;
    
    private int progress;
    
    private Thread downLoadThread;
    
    private boolean interceptFlag = false;
    
    private Handler mHandler = new Handler(){
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				break;
			case DOWN_OVER:
				installApk();
				break;
			default:
				break;
			}
    	};
    };
    
	public UpdateManager(Context context,String url) {
		this.mContext = context;
		this.mUrl = url;
	}
	
	//外部接口让主Activity调用
	public void checkUpdateInfo(){
		showNoticeDialog();
	}
	//这里来检测版本是否需要更新

	
	
	private void showNoticeDialog(){
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(getString(R.string.update_soft));
		builder.setMessage(updateMsg);
		builder.setPositiveButton(getString(R.string.download), new OnClickListener() {			
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				showDownloadDialog();			
			}
		});
		builder.setNegativeButton(getString(R.string.later), new OnClickListener() {			
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();				
			}
		});
		noticeDialog = builder.create();
		noticeDialog.show();
	}
	
	private void showDownloadDialog(){
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(getString(R.string.update_soft));
		
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.progress, null);
		mProgress = (ProgressBar)v.findViewById(R.id.progress);
		
		builder.setView(v);
		builder.setNegativeButton(getString(R.string.cancle), new OnClickListener() {	
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				interceptFlag = true;
			}
		});
		downloadDialog = builder.create();
		downloadDialog.show();
		
		downloadApk();
	}
	
	private Runnable mdownApkRunnable = new Runnable() {	
		public void run() {
			try {
				URL url = new URL(mUrl);			
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();
				
				File file = new File(savePath);
				if(!file.exists()){
					file.mkdir();
				}
				String apkFile = saveFileName;
				File ApkFile = new File(apkFile);
				FileOutputStream fos = new FileOutputStream(ApkFile);
				
				int count = 0;
				byte buf[] = new byte[1024];
				
				do{   		   		
		    		int numread = is.read(buf);
		    		count += numread;
		    	    progress =(int)(((float)count / length) * 100);
		    	    //更新进度
		    	    mHandler.sendEmptyMessage(DOWN_UPDATE);
		    		if(numread <= 0){	
		    			//下载完成通知安装
		    			mHandler.sendEmptyMessage(DOWN_OVER);
		    			break;
		    		}
		    		fos.write(buf,0,numread);
		    	}while(!interceptFlag);//点击取消就停止下载.
				
				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch(IOException e){
				e.printStackTrace();
			}
			
		}
	};
	
	 /**
     * 下载apk
     * @param url
     */
	
	private void downloadApk(){
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}
	 /**
     * 安装apk
     * @param url
     */
	private void installApk(){
		File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }    
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive"); 
        mContext.startActivity(i);
	
	}
	public boolean bindService(Intent service, ServiceConnection conn, int flags) {
		return false;
	}
	public int checkCallingOrSelfPermission(String permission) {
		return 0;
	}
	public int checkCallingOrSelfUriPermission(Uri uri, int modeFlags) {
		return 0;
	}
	public int checkCallingPermission(String permission) {
		return 0;
	}
	public int checkCallingUriPermission(Uri uri, int modeFlags) {
		return 0;
	}
	public int checkPermission(String permission, int pid, int uid) {
		return 0;
	}
	public int checkUriPermission(Uri uri, int pid, int uid, int modeFlags) {
		return 0;
	}
	public int checkUriPermission(Uri uri, String readPermission,
			String writePermission, int pid, int uid, int modeFlags) {
		return 0;
	}
	public void clearWallpaper() throws IOException {
	}
	public Context createPackageContext(String packageName, int flags)
			throws NameNotFoundException {
		return null;
	}
	public String[] databaseList() {
		return null;
	}
	public boolean deleteDatabase(String name) {
		return false;
	}
	public boolean deleteFile(String name) {
		return false;
	}
	public void enforceCallingOrSelfPermission(String permission, String message) {
	}
	public void enforceCallingOrSelfUriPermission(Uri uri, int modeFlags,
			String message) {
	}
	public void enforceCallingPermission(String permission, String message) {
	}
	public void enforceCallingUriPermission(Uri uri, int modeFlags,
			String message) {
	}
	public void enforcePermission(String permission, int pid, int uid,
			String message) {
	}
	public void enforceUriPermission(Uri uri, int pid, int uid, int modeFlags,
			String message) {
	}
	public void enforceUriPermission(Uri uri, String readPermission,
			String writePermission, int pid, int uid, int modeFlags,
			String message) {
	}
	public String[] fileList() {
		return null;
	}
	public Context getApplicationContext() {
		return null;
	}
	public ApplicationInfo getApplicationInfo() {
		return null;
	}
	public AssetManager getAssets() {
		return null;
	}
	public File getCacheDir() {
		return null;
	}
	public ClassLoader getClassLoader() {
		return null;
	}
	public ContentResolver getContentResolver() {
		return null;
	}
	public File getDatabasePath(String name) {
		return null;
	}
	public File getDir(String name, int mode) {
		return null;
	}
	public File getExternalCacheDir() {
		return null;
	}
	public File getExternalFilesDir(String type) {
		return null;
	}
	public File getFileStreamPath(String name) {
		return null;
	}
	public File getFilesDir() {
		return null;
	}
	public Looper getMainLooper() {
		return null;
	}
	public File getObbDir() {
		return null;
	}
	public String getPackageCodePath() {
		return null;
	}
	public PackageManager getPackageManager() {
		return null;
	}
	public String getPackageName() {
		return null;
	}
	public String getPackageResourcePath() {
		return null;
	}
	public Resources getResources() {
		return null;
	}
	public SharedPreferences getSharedPreferences(String name, int mode) {
		return null;
	}
	public Object getSystemService(String name) {
		return null;
	}
	public Theme getTheme() {
		return null;
	}
	public Drawable getWallpaper() {
		return null;
	}
	public int getWallpaperDesiredMinimumHeight() {
		return 0;
	}
	public int getWallpaperDesiredMinimumWidth() {
		return 0;
	}
	public void grantUriPermission(String toPackage, Uri uri, int modeFlags) {
	}
	public FileInputStream openFileInput(String name)
			throws FileNotFoundException {
		return null;
	}
	public FileOutputStream openFileOutput(String name, int mode)
			throws FileNotFoundException {
		return null;
	}
	public SQLiteDatabase openOrCreateDatabase(String name, int mode,
			CursorFactory factory) {
		return null;
	}
	public SQLiteDatabase openOrCreateDatabase(String name, int mode,
			CursorFactory factory, DatabaseErrorHandler errorHandler) {
		return null;
	}
	public Drawable peekWallpaper() {
		return null;
	}
	public Intent registerReceiver(BroadcastReceiver receiver,
			IntentFilter filter) {
		return null;
	}
	public Intent registerReceiver(BroadcastReceiver receiver,
			IntentFilter filter, String broadcastPermission, Handler scheduler) {
		return null;
	}
	public void removeStickyBroadcast(Intent intent) {
	}
	public void revokeUriPermission(Uri uri, int modeFlags) {
	}
	public void sendBroadcast(Intent intent) {
	}
	public void sendBroadcast(Intent intent, String receiverPermission) {
	}
	public void sendOrderedBroadcast(Intent intent, String receiverPermission) {
	}
	public void sendOrderedBroadcast(Intent intent, String receiverPermission,
			BroadcastReceiver resultReceiver, Handler scheduler,
			int initialCode, String initialData, Bundle initialExtras) {
	}
	public void sendStickyBroadcast(Intent intent) {	
	} 
	public void sendStickyOrderedBroadcast(Intent intent,
			BroadcastReceiver resultReceiver, Handler scheduler,
			int initialCode, String initialData, Bundle initialExtras) {	
	}	 
	public void setTheme(int resid) {	
	}
	public void setWallpaper(Bitmap bitmap) throws IOException {
	}
	public void setWallpaper(InputStream data) throws IOException {
	}
	public void startActivities(Intent[] intents) {
	}
	public void startActivity(Intent intent) {
	}
	public boolean startInstrumentation(ComponentName className,
			String profileFile, Bundle arguments) {
		return false;
	}
	public void startIntentSender(IntentSender intent, Intent fillInIntent,
			int flagsMask, int flagsValues, int extraFlags)
			throws SendIntentException {
	}
	public ComponentName startService(Intent service) {
		return null;
	}
	public boolean stopService(Intent service) {
		return false;
	}
	public void unbindService(ServiceConnection conn) {
	}
	public void unregisterReceiver(BroadcastReceiver receiver) {
	}
}
