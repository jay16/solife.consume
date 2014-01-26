package us.solife.consumes.adapter;

import java.util.ArrayList;
import java.math.BigDecimal;

import us.solife.consumes.TabList;
import us.solife.consumes.entity.ConsumeInfo;

import com.yyx.mconsumes.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import java.util.Calendar;
import java.text.SimpleDateFormat;
//import java.sql.Date;
import java.util.Date;
import android.net.ParseException;
import android.widget.RelativeLayout;

public class ConsumeListAdapter extends BaseAdapter{
	ArrayList<ConsumeInfo> consumeInfos;
	private Context        context;

	public ConsumeListAdapter(ArrayList<ConsumeInfo> consumeInfos, Context context) {
		this.consumeInfos = consumeInfos;
		this.context      = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return consumeInfos.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return consumeInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ConsumeInfo consumeInfo = consumeInfos.get(position);
					
		ViewHolder holder;
		if (convertView != null) {
			holder = (ViewHolder) convertView.getTag();
		} else {
			holder        = new ViewHolder();
			convertView   = View.inflate(context, R.layout.tab_list_item, null);
			
			holder.item_value   = (TextView) convertView.findViewById(R.id.TextView_item_value);
			holder.item_date   = (TextView) convertView.findViewById(R.id.TextView_item_date);

			convertView.setTag(holder);
		}

		//消费值四舍五入，保留一位小数
		BigDecimal volue = new BigDecimal(consumeInfo.getVolue()).setScale(1, BigDecimal.ROUND_HALF_UP);
		holder.item_value.setText(volue + "元");
		holder.item_date.setText(consumeInfo.getCreated_at());
		
		SimpleDateFormat sdf =   new SimpleDateFormat("yyyy-MM-dd");
		String week = "F";
		try {
		    Date date = (Date)sdf.parse(consumeInfo.getCreated_at());
		    Calendar calendar = Calendar.getInstance();
		    calendar.setTime(date);  
		    int week_index = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		    String[] weeks = {"日","一","二","三","四","五","六"};
		    week =  weeks[week_index];
		} catch (ParseException e) {
			e.printStackTrace();
		}catch(java.text.ParseException e){
			e.printStackTrace();
		}
		//holder.week.setText(week);
	

		return convertView;
	}

	class ViewHolder {
		private TextView volue, msg, created_at, item_id,list_id, week, count;
		private TextView item_date,item_value,first_value,first_date,last_value,last_date;
		//private ImageView sync_true,sync_false;
	}

}
