package utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import skycaster.cdradio_rxdemo.R;

/**
 * Created by Leo on 2016/12/19.
 */
public class SlideAdapter  extends BaseAdapter{
    private ArrayList<String> listt;
    private Context context;
    private LayoutInflater inflater;

    public SlideAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.listt = list;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return listt.size();
    }

    @Override
    public Object getItem(int position) {
        return listt.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {



        if(convertView==null){
            convertView=new TextView(context);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.content = (TextView) convertView
                    .findViewById(R.id.textViewid);
            convertView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.content.setText(listt.get(position));
           /* String s = listt.get(position);
            StringBuffer sb=new StringBuffer();
            sb.append(s);
            ((TextView)convertView).setText(sb.toString());*/
        return convertView;
    }
    private class ViewHolder {
        private TextView content;
    }
}



