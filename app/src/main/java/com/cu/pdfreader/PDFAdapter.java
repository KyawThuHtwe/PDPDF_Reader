package com.cu.pdfreader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ColorSpace;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;

public class PDFAdapter extends ArrayAdapter<File> {
    Context context;
    ViewHolder viewHolder;
    ArrayList<File> al_pdf;
    ArrayList<File> arrayList;
    public PDFAdapter(Context context, ArrayList<File> al_pdf) {
        super(context, R.layout.list_item,al_pdf);
        this.context = context;
        this.al_pdf = al_pdf;
        this.arrayList = new ArrayList<>();
        this.arrayList.addAll(al_pdf);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return al_pdf.size();
    }

    @NonNull
    @Override
    public View getView(final int position,View convertView,ViewGroup parent) {
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.title=convertView.findViewById(R.id.title);
            viewHolder.location=convertView.findViewById(R.id.location);
            viewHolder.size=convertView.findViewById(R.id.size);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(al_pdf.get(position).getName());
        viewHolder.location.setText(al_pdf.get(position).getPath());
        viewHolder.size.setText(getFolderSizeLabel(al_pdf.get(position)));

        return convertView;
    }
    @SuppressLint("SetTextI18n")
    public String getFolderSizeLabel(File file) {
        long size = getFolderSize(file) / 1024; // Get size and convert bytes into Kb.
        if (size >= 1024) {
            double ans=size/1024.0;
            TextView textView=new TextView(context);
            int maxLength = 4;
            InputFilter[] fArray = new InputFilter[1];
            fArray[0] = new InputFilter.LengthFilter(maxLength);
            textView.setFilters(fArray);
            textView.setText(ans+"");
            return textView.getText()+ " Mb";
        } else {
            return size + " Kb";
        }
    }
    public static long getFolderSize(File file) {
        long size = 0;
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                size += getFolderSize(child);
            }
        } else {
            size = file.length();
        }
        return size;
    }

    public class ViewHolder{
        TextView title,location,size;
    }
    public void filter(String charText){
        charText = charText.toLowerCase(Locale.getDefault());
        al_pdf.clear();
        if (charText.length()==0){
            al_pdf.addAll(arrayList);
        }
        else {
            for (File model : arrayList){
                if (model.getName().toLowerCase(Locale.getDefault())
                        .contains(charText)){
                    al_pdf.add(model);
                }
            }
        }
        notifyDataSetChanged();
    }
}
