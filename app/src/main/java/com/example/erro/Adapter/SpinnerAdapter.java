package com.example.erro.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.erro.R;


public class SpinnerAdapter extends BaseAdapter {
    Context context;
    String[] names;
    LayoutInflater inflter;

    public SpinnerAdapter(Context applicationContext, String[] names) {
        this.context = applicationContext;
        this.names = names;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        view = inflter.inflate(R.layout.spinner_itmes, null);
        TextView tvNames = (TextView) view.findViewById(R.id.textView);
        tvNames.setText(names[i]);
        return view;
    }
}
