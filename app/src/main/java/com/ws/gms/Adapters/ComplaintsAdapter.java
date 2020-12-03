package com.ws.gms.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ws.gms.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ComplaintsAdapter extends RecyclerView.Adapter<ComplaintsAdapter.MyViewHolder> {
    private ArrayList<HashMap<String, String>> arraymap;
    private Context context;
    private ItemClickListener clickListener;

    public ComplaintsAdapter(Context context, ArrayList<HashMap<String, String>> productname) {
        arraymap = productname;
        this.context = context;
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    @Override
    public ComplaintsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.complaints_adapter, parent, false);
        return new ComplaintsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ComplaintsAdapter.MyViewHolder holder, final int position) {

        //  final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
       // SimpleDateFormat s1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        SimpleDateFormat s1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        long complaintDate = Long.parseLong(arraymap.get(position).get("created_date"));
        String date = s1.format(new Date(complaintDate));

        holder.tvToken.setText(arraymap.get(position).get("tokennumber"));
        holder.tvType.setText(arraymap.get(position).get("complaintType"));
        holder.tvTitle.setText(arraymap.get(position).get("title"));
        holder.tvDesc.setText(arraymap.get(position).get("complaintDescription"));
        holder.tvArea.setText(arraymap.get(position).get("area"));

        holder.tvDate.setText(date);

        if (arraymap.get(position).get("status").equals("Open")) {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.white));
            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.gray_roundback));

            // holder.tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.initial, 0, 0, 0);
            holder.tvStatus.setText(context.getResources().getString(R.string.open));

        } else if (arraymap.get(position).get("status").equals("In Progress")) {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.white));
//            holder.tvStatus.setBackgroundColor(context.getResources().getColor(R.color.startblue));
            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.startblue_roundback));

// holder.tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.send, 0, 0, 0);
            holder.tvStatus.setText(context.getResources().getString(R.string.inProgress));

        } else if (arraymap.get(position).get("status").equals("Resolved")) {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.white));
//            holder.tvStatus.setBackgroundColor(context.getResources().getColor(R.color.green));
            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.green_roundback));

            //  holder.tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.success, 0, 0, 0);
            holder.tvStatus.setText(context.getResources().getString(R.string.resolved));

        } else if (arraymap.get(position).get("status").equals("Closed")) {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.white));
//            holder.tvStatus.setBackgroundColor(context.getResources().getColor(R.color.orange));
            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.orange_roundback));

            //  holder.tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.success, 0, 0, 0);
            holder.tvStatus.setText(context.getResources().getString(R.string.closed));

        } else if (arraymap.get(position).get("status").equals("Not Resolved")) {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.white));
//            holder.tvStatus.setBackgroundColor(context.getResources().getColor(R.color.red));
            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.red_roundback));

            //  holder.tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.success, 0, 0, 0);
            holder.tvStatus.setText(context.getResources().getString(R.string.notResolved));

        }


        ComplaintsDetails lostReq = new ComplaintsDetails();
        lostReq.setComplaintId(Integer.parseInt(arraymap.get(position).get("complaintId")));
        lostReq.setFirstname(arraymap.get(position).get("firstname"));
        lostReq.setMiddlename(arraymap.get(position).get("middlename"));
        lostReq.setLastname(arraymap.get(position).get("lastname"));
        lostReq.setMobileno(arraymap.get(position).get("mobileno"));
        lostReq.setUserImage(arraymap.get(position).get("userImage"));

        lostReq.setTokenNumber(arraymap.get(position).get("tokennumber"));
        lostReq.setComplaintType(arraymap.get(position).get("complaintType"));
        lostReq.setTitle(arraymap.get(position).get("title"));

        lostReq.setComplaintTypeId(Integer.parseInt(arraymap.get(position).get("complaintTypeId")));
        lostReq.setDesc(arraymap.get(position).get("complaintDescription"));
        lostReq.setArea(arraymap.get(position).get("area"));
        lostReq.setStatus(arraymap.get(position).get("status"));
        lostReq.setComplaintDate(arraymap.get(position).get("created_date"));
        lostReq.setVcomment(arraymap.get(position).get("vcomment"));
        lostReq.setUcomment(arraymap.get(position).get("ucomment"));

        lostReq.setAssignToId(arraymap.get(position).get("assignToId"));
        lostReq.setAssignFname(arraymap.get(position).get("assignFname"));
        lostReq.setAssignMname(arraymap.get(position).get("assignMname"));
        lostReq.setAssignLname(arraymap.get(position).get("assignLname"));
        lostReq.setAssignMono(arraymap.get(position).get("assignMono"));
        lostReq.setAssignEmailid(arraymap.get(position).get("assignEmailid"));

        holder.tvStatus.setTag(lostReq);
        holder.cvComplaint.setTag(lostReq);
    }

    @Override
    public int getItemCount() {
        return arraymap.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvToken, tvStatus, tvType, tvTitle, tvDesc, tvArea, tvDate;
        CardView cvComplaint;

        public MyViewHolder(View view) {
            super(view);

            tvToken = view.findViewById(R.id.tv_token);
            tvStatus = view.findViewById(R.id.tv_status);
            tvType = view.findViewById(R.id.tv_type);
            tvTitle = view.findViewById(R.id.tv_title);
            tvDesc = view.findViewById(R.id.tv_desc);
            tvArea = view.findViewById(R.id.tv_area);
            tvDate = view.findViewById(R.id.tv_date);
            cvComplaint = view.findViewById(R.id.cv_complaint);

            tvStatus.setOnClickListener(this);
            cvComplaint.setOnClickListener(this);
            //   tvReqStatus.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) clickListener.onClick(v, getAdapterPosition());
        }
    }
}

