package us.solife.consumes.db;

import java.util.ArrayList;
import android.util.Log;
import java.util.Date;
import java.text.SimpleDateFormat;

import us.solife.consumes.TabList;
import us.solife.consumes.entity.ConsumeInfo;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;
import us.solife.consumes.util.NetUtils;
import us.solife.consumes.util.SPManager;
import us.solife.consumes.util.ToolUtils;

import java.util.Calendar;
import java.text.ParseException;

/**
 * ���Ѽ�¼ʵ���
 * @author jay (http://solife.us/resume)
 * @version 1.0
 * @created 2014-02-25
 */
public class ConsumeTb {
	public static final String KEY_ROWID = "id";
	private static final String DATABASE_TABLE = "consumes";

	private Context context;
	public ConsumeDatabaseHelper consumeDatabaseHelper;
	static ConsumeTb consumeDao;

	private ConsumeTb(Context context) {
		this.context = context;
		this.consumeDatabaseHelper = new ConsumeDatabaseHelper(context);
	}

	public static ConsumeTb getConsumeTb(Context context) {
		if (consumeDao != null) {
		} else {
			consumeDao = new ConsumeTb(context);

		}
		return consumeDao;

	}

	/**
	 *  ���뵱ǰ��½�û��������Ѽ�¼
	 *  ����ǰ�������ʵ���
	 * @param context
	 * @param consumeInfos
	 */
	public void insert_all_record(Context context, ArrayList<ConsumeInfo> consumeInfos) {
		SQLiteDatabase database = consumeDatabaseHelper.getWritableDatabase();
		database.beginTransaction();
		database.execSQL("delete from consumes");
		database.setTransactionSuccessful();
		database.endTransaction();
		database.close();
		
		for (int i = 0; i < consumeInfos.size(); i++) {
			ConsumeInfo info = consumeInfos.get(i);
			//log������
            Log.w("ConsumeDao",info.get_msg());
            
			insert_record(info.get_user_id(), info.get_consume_id(), info.get_volue(),
					info.get_msg(), info.get_created_at(),info.get_updated_at(),true);
		}

	}

	// ����һ�����Ѽ�¼
	public long insert_record(Integer user_id, Integer consume_id, Double volue,
			String msg, String created_at, String updated_at, Boolean sync) {
		SQLiteDatabase database = consumeDatabaseHelper.getWritableDatabase();
		database.beginTransaction();

		ContentValues values = new ContentValues();
		values.put("user_id", user_id);
		values.put("consume_id", consume_id);
		values.put("volue", volue);
		values.put("msg", msg);
		values.put("created_at", created_at);
		values.put("updated_at", created_at);
		//�Ƿ��������������ͬ��
		values.put("sync", sync);
		long rowid = database.insert("consumes", null, values);
		//log������
        Log.w("ConsumeDao","�������ݿ⶯�����id:["+rowid+"]");
		database.setTransactionSuccessful();
		database.endTransaction();
		database.close();

		return rowid;
	}
	//ȡ���������Ѽ�¼
	public ArrayList<ConsumeInfo> get_all_records(Context context) {
		// public Integer getAllRecords(Context context) {
		SQLiteDatabase database = consumeDatabaseHelper.getWritableDatabase();
		String sql = "select * from consumes where user_id not null and state <> 'delete' " +
				" order by created_at desc";
		Cursor cursor = database.rawQuery(sql, null);
	
		ArrayList<ConsumeInfo> consumeInfos = new ArrayList<ConsumeInfo>();
		if (cursor.getCount() > 0) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				ConsumeInfo consumeInfo = new ConsumeInfo();
				
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				int user_id    = cursor.getInt(cursor.getColumnIndex("user_id"));
				int consume_id = cursor.getInt(cursor.getColumnIndex("consume_id"));
				double volue = cursor.getDouble(cursor.getColumnIndex("volue"));
				String msg = cursor.getString(cursor.getColumnIndex("msg")).toString();
				String created_at = cursor.getString(cursor.getColumnIndex("created_at")).toString();
			    String updated_at = cursor.getString(cursor.getColumnIndex("updated_at"));
			    Long sync         = cursor.getLong(cursor.getColumnIndex("sync"));
			    
				consumeInfo.set_id(id);
				consumeInfo.set_user_id(user_id);
				consumeInfo.set_consume_id(consume_id);
				consumeInfo.set_volue(volue);
				consumeInfo.set_msg(msg);
				consumeInfo.set_created_at(created_at);
				consumeInfo.set_updated_at(updated_at);
				consumeInfo.set_sync(sync);
				consumeInfos.add(consumeInfo);
			}
		}
		cursor.close();
		database.close();
		return consumeInfos;
	}


  
	//ȡ������δͬ���������������Ѽ�¼
	public ArrayList<ConsumeInfo> get_unsync_records() {
		// public Integer getAllRecords(Context context) {
		SQLiteDatabase database = consumeDatabaseHelper.getWritableDatabase();
		Cursor cursor = database.rawQuery("select * from consumes where sync = 0", null);
		
		ArrayList<ConsumeInfo> consumeInfos = new ArrayList<ConsumeInfo>();
		if (cursor.getCount() > 0) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				consumeInfos.add(get_consume_info_from_cursor(cursor));
			}
		}
		cursor.close();
		database.close();
		return consumeInfos;
	}


	
	public ConsumeInfo get_consume_info_from_cursor(Cursor cursor){
		ConsumeInfo consume_info = new ConsumeInfo();
	    
	    consume_info.set_id(cursor.getInt(cursor.getColumnIndex("id")));
	    consume_info.set_user_id(cursor.getInt(cursor.getColumnIndex("user_id")));
	    consume_info.set_consume_id(cursor.getInt(cursor.getColumnIndex("consume_id")));
		consume_info.set_volue(cursor.getDouble(cursor.getColumnIndex("volue")));
		consume_info.set_msg(cursor.getString(cursor.getColumnIndex("msg")).toString());
		consume_info.set_created_at(cursor.getString(cursor.getColumnIndex("created_at")).toString());
		consume_info.set_updated_at(cursor.getString(cursor.getColumnIndex("updated_at")));
		consume_info.set_sync(cursor.getLong(cursor.getColumnIndex("sync")));
		consume_info.set_state(cursor.getString(cursor.getColumnIndex("state")));
		
		return consume_info;
	}
	public int update_record(ConsumeInfo consume_info) {
		SQLiteDatabase db = consumeDatabaseHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("user_id", consume_info.get_user_id());
		cv.put("consume_id", consume_info.get_consume_id());
		cv.put("msg", consume_info.get_msg());
		cv.put("volue", consume_info.get_volue());
		cv.put("created_at", consume_info.get_created_at());
		cv.put("sync", consume_info.get_sync());
		cv.put("state", consume_info.get_state());
		
		String[] args = {String.valueOf(consume_info.get_id())};

		return db.update(DATABASE_TABLE, cv, "id=?",args);
	}

	public void delete_record_with_rowid(Integer row_id) {		
		SQLiteDatabase database = consumeDatabaseHelper.getWritableDatabase();
		database.beginTransaction();
		database.execSQL("delete from consumes where id = "+row_id);
		database.setTransactionSuccessful();
		database.endTransaction();
		database.close();
	}
	
	
}