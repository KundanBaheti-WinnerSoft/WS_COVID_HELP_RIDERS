package com.ws.gms.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.ws.gms.R;

import java.util.ArrayList;

public class ImagesAdapter extends BaseAdapter {

    private Context ctx;
    private int pos;
    private LayoutInflater inflater;
    private ImageView ivGallery;
    ArrayList<String> mArrayUri;
    public ImagesAdapter(Context ctx, ArrayList<String> mArrayUri) {

        this.ctx = ctx;
        this.mArrayUri = mArrayUri;
    }

    @Override
    public int getCount() {
        return mArrayUri.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrayUri.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        pos = position;
        inflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.gv_item, parent, false);

        ivGallery = (ImageView) itemView.findViewById(R.id.ivGallery);

//        ivGallery.se(mArrayUri.get(position));

        String imageUrl = mArrayUri.get(position);
        Picasso.with(ctx).load(imageUrl).into(ivGallery);

        return itemView;
    }
}
