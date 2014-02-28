package us.solife.consumes;

import java.util.ArrayList;
import java.util.HashMap;

import us.solife.consumes.R;
import us.solife.consumes.adapter.ListViewConsumeAdapter;
import us.solife.consumes.db.ConsumeTb;
import us.solife.consumes.db.CurrentUser;
import us.solife.consumes.entity.ConsumeInfo;
import us.solife.consumes.entity.URLs;
import us.solife.consumes.util.NetUtils;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import us.solife.consumes.parseJson.ConsumeListParse;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * 个人消费记录列表
 * @author jay (http://solife.us/resume)
 * @version 1.0
 * @created 2014-02-25
 */
public class TabList extends BaseActivity{
	ListView listView;
    Spinner  spinner=null;  
	int      precursor;
	int      index;
	//String url = "http://solife.us/api/consumes/list";
	SharedPreferences      preferences;
	ArrayList<ConsumeInfo> consumeInfos;
	ConsumeTb             consumeDao;
	CurrentUser           current_user;
	SharedPreferences sharedPreferences;

	@Override
	public void init() { // TODO Auto-generated method stub
		setContentView(R.layout.tab_list);

		/**
		 * 消费记录列表展示方式
		 * 年/月/周/天
		 */
		spinner = (Spinner)findViewById(R.id.Spinnered);  
		ArrayAdapter <CharSequence> adapter = ArrayAdapter.createFromResource(this,  
                R.array.consume_item_list_view_array, android.R.layout.simple_spinner_item);  
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);  
        spinner.setSelection(3,true);
        spinner.setPrompt("列表显示方式"); 
        spinner.setOnItemSelectedListener(new SpinnerOnItemSelectListener()); 
		
        /**
         * 初始化本地数据库
         */
		sharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE);
		long current_user_id = sharedPreferences.getLong("current_user_id", -1);
		consumeDao = ConsumeTb.getConsumeTb(getApplication());
		current_user = CurrentUser.getCurrentUser(getApplication(), current_user_id);
		/**
		 * 同步/下载数据按钮
		 */
		ImageButton imageButton_download = (ImageButton) findViewById(R.id.imageButton_download);		
		ImageButton imageButton_refresh  = (ImageButton) findViewById(R.id.imageButton_refresh);
		imageButton_download.setOnClickListener(imageButton_download_listener);
		imageButton_refresh.setOnClickListener(imageButton_refresh_listener);
	}
	
	/**
	 * 每当跳转到该界面时加载该项
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		init_view_list("day");
	}

	/**
	 * 初始化视图控件
	 * @param: ShowType 显示格式
	 * year/month/week/day
	 */
	public void init_view_list(String show_type) {

		listView = (ListView) findViewById(R.id.consumeListView);
        consumeInfos = current_user.get_all_consume_item(show_type);
        
        //无消费记录时提示错误
		if (consumeInfos == null && consumeInfos.size() == 0) {
			Toast.makeText(TabList.this, "No Data", 0).show();
			return;
		}
		
		//listView.addFooterView(lvQuestion_footer);//添加底部视图  必须在setAdapter前
		listView.setAdapter(new ListViewConsumeAdapter(consumeInfos,TabList.this));
		listView.setClickable(true);
		listView.setOnItemClickListener(new OnItemClickListener(){
			 @Override
	         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ConsumeInfo consumeinfo = consumeInfos.get(position);
        		if(consumeinfo == null){
	        		//判断是否是TextView
	        		if(view instanceof TextView){
	        			consumeinfo = (ConsumeInfo)view.getTag();
	        		}else{
	        			TextView tv = (TextView)view.findViewById(R.id.TextView_item_value);
	        			consumeinfo = (ConsumeInfo)tv.getTag();
	        		}
        		}
        		if(consumeinfo == null) return;
        		
                //提示消费内容
				Toast.makeText(TabList.this, "["+consumeinfo.get_created_at()+"]消费记录", 0).show();
				
				// 界面切换，显示具体记录
				Intent intent = new Intent(TabList.this, ConsumeItem.class);
				intent.putExtra("created_at",  consumeinfo.get_created_at());
				startActivity(intent);
			 }
		});
		listView.invalidate();
	}
	
	/**
	 * 下拉列表spinner点击响应
	 */
	class SpinnerOnItemSelectListener implements OnItemSelectedListener{  
	    @Override  
	    public void onItemSelected(AdapterView<?> AdapterView, View view, int position, long arg3) {  
	        String selected = AdapterView.getItemAtPosition(position).toString();  
	        Toast.makeText(TabList.this, selected, 0).show();
	        
	        //点击下拉列表spinner不同选项，响应不同信息
	        switch(position) {
	        case 0:
	        	init_view_list("year"); break;
	        case 1:
	        	init_view_list("month"); break;
	        case 2:
	        	init_view_list("week"); break;
	        case 3:
	        	init_view_list("day"); break;
	        default:
		        init_view_list("day"); break;
	        }
	    }
	    
	    @Override  
	    public void onNothingSelected(AdapterView<?> arg0) {  
	        // TODO Auto-generated method stub   
	        Toast.makeText(TabList.this, "NothingSelected", 0).show();
	    }  
	      
	}

	// 句柄-数据插入
		Handler handler = new Handler() {
			public void handleMessage(android.os.Message message) {
				switch (message.what) {
				case 1000:
					Toast.makeText(TabList.this, "同步数据"+ ((ArrayList<ConsumeInfo>)message.obj).size(), 0).show();
					Toast.makeText(TabList.this, "数据库同步成功", 0).show();
					break;
				case 2000:
					//Toast.makeText(TabList.this, "数据库同步失败！", 0).show();
					Toast.makeText(TabList.this, message.obj.toString(), 0).show();

					break;
				case 3000:
					Toast.makeText(TabList.this, "错误:未连接网络！", 0).show();
					break;
				default:
					break;
				}
			};
		};
		DataCallback<HashMap<String, Object>> callback = new DataCallback<HashMap<String, Object>>() {

			@Override
			public void processData(HashMap<String, Object> paramObject, boolean paramBoolean) {
				// TODO Auto-generated method stub
				boolean result = (Boolean) paramObject.get("result");
				if (result) {
					final ArrayList<ConsumeInfo> consume_infos = (ArrayList<ConsumeInfo>) paramObject.get("consume_infos");
					
					if (consume_infos != null && consume_infos.size() != 0) {
						Toast.makeText(TabList.this, "同步数据"+consume_infos.size(), 0).show();
						
						Message message = new Message();
						try {
							Toast.makeText(TabList.this, "开始同步数据2", 0).show();
							//log调试用
				            Log.w("TabList callback","消费列表数量:"+consume_infos.size());
							consumeDao.insert_all_record(TabList.this, consume_infos);
				            Log.w("TabList callback","消费列表插入数据库完毕");
							message.what = 1000;
							message.obj  = consume_infos;
							handler.sendMessage(message);
						} catch (Exception e) {
							message.what = 2000;
							message.obj = e;
							handler.sendMessage(message);
							e.printStackTrace();
						}

					} else {
						//ConsumeDao consumeDao = ConsumeDao.getConsumeDao(TabList.this);
						Toast.makeText(TabList.this, "同步数据为空1！", 0).show();
					}

				} else {
					Toast.makeText(getApplicationContext(), "获取数据为空2", 0).show();
				}

			}
		};
		
		//同步数据库至本地
		Button.OnClickListener imageButton_download_listener = new Button.OnClickListener(){//创建监听对象  
			public void onClick(View v){  
				Toast.makeText(TabList.this, "开始同步数据1", 0).show();
				
				ConsumeListParse consumeListParse = new ConsumeListParse();
				getDataFromServer(getApplicationContext(), consumeListParse, URLs.CONSUME_LIST, callback);
				init_view_list("day");
			}
		};
		//同步本地数据至服务器
		Button.OnClickListener imageButton_refresh_listener = new Button.OnClickListener(){//创建监听对象  
			public void onClick(View v){  
				consumeInfos = consumeDao.get_unsync_records();
				sharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE);
				
		    	//Toast.makeText(TabList.this, "更新未同步数据", 0).show();
		    	
				if (sharedPreferences.contains("current_user_email")
						&& !sharedPreferences.getString("current_user_email", "").equals("")) {
					String login_email = sharedPreferences.getString("current_user_email", "");
					long current_user_id = sharedPreferences.getLong("current_user_id", -1);
					

			    	Toast.makeText(TabList.this, "更新数据", 0).show();
					//后台同步更新未同步的数据
					NetUtils.upload_unsync_consumes_background(TabList.this,login_email);
					Toast.makeText(TabList.this, "更新完毕", 0).show();
					init_view_list("day");
			    } else {
			    	Toast.makeText(TabList.this, "更新失败", 0).show();
			    }
		};
	
	
	};
	
	//设置标题栏右侧按钮的作用
	public void btnmainright(View v) {  
		Intent intent = new Intent (TabList.this,MenuConsumeItemListType.class);			
		startActivity(intent);	
		//Toast.makeText(getApplicationContext(), "点击了功能按钮", Toast.LENGTH_LONG).show();
      }  
}
