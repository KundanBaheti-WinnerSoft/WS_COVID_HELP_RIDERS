package com.ws.gms.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ws.gms.Consatants.Urllink;
import com.ws.gms.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminsAdapter extends RecyclerView.Adapter<AdminsAdapter.MyViewHolder> {
    private ArrayList<HashMap<String, String>> arraymap;
    private Context context;
    private ItemClickListener clickListener;

    public AdminsAdapter(Context context, ArrayList<HashMap<String, String>> productname) {
        arraymap = productname;
        this.context = context;
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    @Override
    public AdminsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admins_adapter, parent, false);
        return new AdminsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AdminsAdapter.MyViewHolder holder, final int position) {

        Urllink urllink = new Urllink();

        String imageUrl = urllink.downloadProfilePic + arraymap.get(position).get("image");

        Picasso.with(context).load(imageUrl).into(holder.ivProfilePic);

        holder.tvName.setText(arraymap.get(position).get("firstname")+" "+arraymap.get(position).get("lastname"));
        holder.tvNumber.setText(arraymap.get(position).get("mobileno"));

        ProfileDetails profileDetails = new ProfileDetails();
        profileDetails.setId(Integer.parseInt(arraymap.get(position).get("id")));
        profileDetails.setRole(arraymap.get(position).get("role"));
        profileDetails.setFirstName(arraymap.get(position).get("firstname"));
        profileDetails.setMiddleName(arraymap.get(position).get("middlename"));
        profileDetails.setLastName(arraymap.get(position).get("lastname"));
        profileDetails.setMobileno(arraymap.get(position).get("mobileno"));
        profileDetails.setEmailId(arraymap.get(position).get("emailid"));
        profileDetails.setAddress(arraymap.get(position).get("address"));
        profileDetails.setArea(arraymap.get(position).get("area"));
        profileDetails.setCity(arraymap.get(position).get("city"));
        profileDetails.setPincode(arraymap.get(position).get("pincode"));
        profileDetails.setState(arraymap.get(position).get("state"));
        profileDetails.setCountry(arraymap.get(position).get("country"));

        profileDetails.setImageName(arraymap.get(position).get("image"));
        profileDetails.setId(Integer.parseInt(arraymap.get(position).get("id")));
        profileDetails.setActiveDeactive(Boolean.valueOf(arraymap.get(position).get("activeDeactive")));

        holder.llAdmin.setTag(profileDetails);
        //   holder.tvAdminStatus.setTag(lostReq);
    }

    @Override
    public int getItemCount() {
        return arraymap.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvName, tvNumber;
        LinearLayout llAdmin;
        CircleImageView ivProfilePic;

        public MyViewHolder(View view) {
            super(view);

            tvName = view.findViewById(R.id.tv_name);
            tvNumber = view.findViewById(R.id.tv_number);
            llAdmin = view.findViewById(R.id.ll_admin);
            ivProfilePic = view.findViewById(R.id.iv_profile);

              llAdmin.setOnClickListener(this);
            //   tvReqStatus.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) clickListener.onClick(v, getAdapterPosition());
        }
    }
}

