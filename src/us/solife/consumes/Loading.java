package us.solife.consumes;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import us.solife.consumes.R;
import us.solife.consumes.BaseActivity.DataCallback;
import us.solife.consumes.api.URLs;
import us.solife.consumes.db.ConsumeTb;
import us.solife.consumes.entity.ConsumeInfo;
import us.solife.consumes.parse.ConsumeListParse;
import us.solife.consumes.recevier.TimerService;

//����consume�Զ�������

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class Loading extends BaseActivity {
	SharedPreferences sharedPreferences;
	ImageView backGround;
	TextView promot;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// �������䲻�ɵ������������ִ���
		setContentView(R.layout.loading);
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	// ��дBaseActivity�е�Init()
	@Override
	public void init() {
		// TODO Auto-generated method stub
		sharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE);

		// �ж��Ƿ��Ѿ���½
		// δ��½��ֱ����ʾ��½����
		Intent intent;
		if (sharedPreferences.contains("is_login")
				&& sharedPreferences.getBoolean("is_login", false)) {
			intent = new Intent(Loading.this, Main.class);
			//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
			//������activity�Ѿ������ˣ��Ͳ������µ�activity��
			//��ֻ�ǰ����activityʵ���ӵ�ջ�����Ϳ�����
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			
			//ѭ����ʱִ��
			Intent intent1 =new Intent(getApplication(), TimerService.class);
		    intent1.setAction(sharedPreferences.getString("current_user_email", ""));
		    PendingIntent sender=PendingIntent.getBroadcast(getApplication(), 0, intent1, 0);
		    //��ʼʱ��
		    long now =SystemClock.elapsedRealtime();
		    //long now = System.currentTimeMillis();  

		    AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);//5��һ�����ڣ���ͣ�ķ��͹㲥
		    am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, now, 600000, sender);
		} else {
			intent = new Intent(Loading.this, Login.class);
		}
		
		//��������Ŀ¼�ļ���
		File storage_base = new File(URLs.STORAGE_BASE);
		if(!storage_base.exists()) storage_base.mkdir(); 
		File storage_apk = new File(URLs.STORAGE_APK);
		if(!storage_apk.exists()) storage_apk.mkdir(); 
		File storage_gravatar = new File(URLs.STORAGE_GRAVATAR);
		if(!storage_gravatar.exists()) storage_gravatar.mkdir(); 
	    
		startActivity(intent);
		finish();

	}
}