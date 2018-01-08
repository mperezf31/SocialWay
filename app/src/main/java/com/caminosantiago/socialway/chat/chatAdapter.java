package com.caminosantiago.socialway.chat;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caminosantiago.socialway.R;

import java.util.ArrayList;

public class chatAdapter extends ArrayAdapter<ItemsPersonalizados> {
	 
    private final Context context;
    private final ArrayList<ItemsPersonalizados> itemsArrayList;

    public chatAdapter(Context context, ArrayList<ItemsPersonalizados> itemsArrayList) {
        super(context, R.layout.row_chat, itemsArrayList);
        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater 
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.row_chat, parent, false);

        // 3. Get the two text view from the rowView
        LinearLayout layoutTextOther = (LinearLayout) rowView.findViewById(R.id.layoutTextOther);
        layoutTextOther.setVisibility(View.GONE);
        TextView textViewDateOther = (TextView) rowView.findViewById(R.id.textViewDateOther);
        TextView labelView = (TextView) rowView.findViewById(R.id.label);


        LinearLayout layoutText = (LinearLayout) rowView.findViewById(R.id.layoutText);
        layoutText.setVisibility(View.GONE);
        TextView textViewDate = (TextView) rowView.findViewById(R.id.textViewDate);
        TextView valueView = (TextView) rowView.findViewById(R.id.value);

        // 4. Set the text for textView
        if (!itemsArrayList.get(position).getTitle().equals("")){
            layoutTextOther.setVisibility(View.VISIBLE);
            labelView.setText(itemsArrayList.get(position).getTitle());
            textViewDateOther.setText(itemsArrayList.get(position).getDate());
        }

        if (!itemsArrayList.get(position).getDescription().equals("")){
            layoutText.setVisibility(View.VISIBLE);
            valueView.setText(itemsArrayList.get(position).getDescription());
            textViewDate.setText(itemsArrayList.get(position).getDate());
        }

        // 5. retrn rowView
        return rowView;
    }
}