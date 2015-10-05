package com.example.realtalk.realtalk;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.StringBuilderPrinter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class ExploreFragment extends android.support.v4.app.Fragment {

    TextView titleExplore;
    ImageButton btnClose;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        titleExplore = (TextView)getActivity().findViewById(R.id.titleExplore);
        titleExplore.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.JustAnotherHandRegular));

        btnClose = (ImageButton)getActivity().findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(ExploreFragment.this).commit();
            }
        });

        GridView gridview = (GridView) getActivity().findViewById(R.id.exploreGridView);
        ArrayList<String> item = new ArrayList<String>();
        for (int i = 0; i < 50; i++) {
            item.add("HELLO"+i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_expandable_list_item_1,item);

        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getActivity(), "Grid" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
class GridAdapter extends BaseAdapter{

    private Context context;
    private String[] itemText;
    private int[] image;
    private static LayoutInflater inflater = null;

    public GridAdapter(Context context,String[] text,int[] image){
        this.context = context;
        this.itemText = text;
        this.image = image;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 0;
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
    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder = new Holder();
        convertView = inflater.inflate(R.layout.explore_single_grid_item,null);
        holder.icon = (TextView)convertVi;
        return convertView;
    }
    public class Holder{
        TextView itemName;
        ImageView icon;
    }
}
