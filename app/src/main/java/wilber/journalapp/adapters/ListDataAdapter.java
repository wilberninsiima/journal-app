package wilber.journalapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import wilber.journalapp.R;


public class ListDataAdapter extends ArrayAdapter {
    List list=new ArrayList();
    public ListDataAdapter(Context context, int resource) {
        super(context, resource);
    }

    static  class LayoutHandler{
        TextView TIME,TITLE,BODY,LAST_EDITED;
    }

    @Override
    public void add(Object object) {
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutHandler layoutHandler;
        View row=convertView;
        if(row==null){
            LayoutInflater layoutInflater=(LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutInflater.inflate(R.layout.diary_list_layout,parent,false);
            layoutHandler=new LayoutHandler();
            layoutHandler.TIME=(TextView) row.findViewById(R.id.timeTextView);
            layoutHandler.TITLE=(TextView) row.findViewById(R.id.titleTextView);
            layoutHandler.BODY=(TextView) row.findViewById(R.id.bodyTextView);
            layoutHandler.LAST_EDITED=(TextView) row.findViewById(R.id.lastEditedView);
            row.setTag(layoutHandler);
        }else{
            layoutHandler=(LayoutHandler)row.getTag();
        }
        DataProvider dataProvider=(DataProvider) this.getItem(position);
        layoutHandler.TIME.setText(dataProvider.getTime());
        layoutHandler.TITLE.setText(dataProvider.getTitle());
        layoutHandler.BODY.setText(dataProvider.getBody());
        layoutHandler.LAST_EDITED.setText(dataProvider.getLast_edited());

        return row;
    }
}
