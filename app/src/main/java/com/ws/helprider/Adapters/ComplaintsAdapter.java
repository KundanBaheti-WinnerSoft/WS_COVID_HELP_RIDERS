package com.ws.helprider.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ws.helprider.R;

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

        SimpleDateFormat s1 = new SimpleDateFormat("dd/MM/yyyy");

        long complaintDate = Long.parseLong(arraymap.get(position).get("created_date"));
        String date = s1.format(new Date(complaintDate));

        holder.tvToken.setText(arraymap.get(position).get("tokennumber"));
        holder.tvType.setText(arraymap.get(position).get("complaintType"));
        holder.tvPatientName.setText(arraymap.get(position).get("patient_name"));
        holder.tvPatientAge.setText(arraymap.get(position).get("age"));
        holder.tvContactPersonName.setText(arraymap.get(position).get("contact_person_name"));
        holder.tvContactPersonMobNo.setText(arraymap.get(position).get("relative_mobile"));

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
        lostReq.setRequestId(Integer.parseInt(arraymap.get(position).get("complaintId")));
        lostReq.setvCommDate(arraymap.get(position).get("vCommDate"));
        lostReq.setuCommDate(arraymap.get(position).get("uCommDate"));
        lostReq.setStatus(arraymap.get(position).get("status"));
        lostReq.setVcomment(arraymap.get(position).get("vcomment"));
        lostReq.setUcomment(arraymap.get(position).get("ucomment"));

        lostReq.setPatient_name(arraymap.get(position).get("patient_name"));
        lostReq.setAge(arraymap.get(position).get("age"));
        lostReq.setCreated_date(arraymap.get(position).get("created_date"));

        lostReq.setLocation(arraymap.get(position).get("location"));
        lostReq.setBed_required(arraymap.get(position).get("bed_required"));
        lostReq.setDate_of_test(arraymap.get(position).get("date_of_test"));
        lostReq.setRequestTypeId(Integer.parseInt(arraymap.get(position).get("complaintTypeId")));
        lostReq.setComplaintType(arraymap.get(position).get("complaintType"));
        lostReq.setComplaintdescription(arraymap.get(position).get("complaintdescription"));

        lostReq.setRtpcr_or_antigen(arraymap.get(position).get("rtpcr_or_antigen"));
        lostReq.setGovt_or_private(arraymap.get(position).get("govt_or_private"));
        lostReq.setOxy_room_level(arraymap.get(position).get("oxy_room_level"));
        lostReq.setOxy_with_support(arraymap.get(position).get("oxy_with_support"));
        lostReq.setHrct_score(arraymap.get(position).get("hrct_score"));
        lostReq.setCurrent_situation(arraymap.get(position).get("current_situation"));
        lostReq.setBlood_group(arraymap.get(position).get("blood_group"));
        lostReq.setOther_disease(arraymap.get(position).get("other_disease"));
        lostReq.setHelp_detail(arraymap.get(position).get("help_detail"));
        lostReq.setHospital_name(arraymap.get(position).get("hospital_name"));
        lostReq.setHospital_address(arraymap.get(position).get("hospital_address"));
        lostReq.setDoc_mobile(arraymap.get(position).get("doc_mobile"));
        lostReq.setRelative_mobile(arraymap.get(position).get("relative_mobile"));
        lostReq.setWard(arraymap.get(position).get("ward"));
        lostReq.setDoc_name(arraymap.get(position).get("doc_name"));
        lostReq.setContact_person_name(arraymap.get(position).get("contact_person_name"));
        lostReq.setOxygen(arraymap.get(position).get("oxygen"));
        lostReq.setTokennumber(arraymap.get(position).get("tokennumber"));
        lostReq.setUnits_required(arraymap.get(position).get("units_required"));
        lostReq.setContact_person_mobile(arraymap.get(position).get("contact_person_mobile"));

        lostReq.setAssignToId(arraymap.get(position).get("assignToId"));
        lostReq.setAssignFname(arraymap.get(position).get("assignFname"));
        lostReq.setAssignMname(arraymap.get(position).get("assignMname"));
        lostReq.setAssignLname(arraymap.get(position).get("assignLname"));
        lostReq.setAssignMono(arraymap.get(position).get("assignMono"));
        lostReq.setAssignEmailid(arraymap.get(position).get("assignEmailid"));

        lostReq.setFirstname(arraymap.get(position).get("firstname"));

        holder.tvStatus.setTag(lostReq);
        holder.cvComplaint.setTag(lostReq);
    }

    @Override
    public int getItemCount() {
        return arraymap.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvToken, tvStatus, tvType, tvPatientName, tvPatientAge, tvContactPersonName,tvContactPersonMobNo, tvDate;
        CardView cvComplaint;

        public MyViewHolder(View view) {
            super(view);

            tvToken = view.findViewById(R.id.tv_token);
            tvStatus = view.findViewById(R.id.tv_status);
            tvType = view.findViewById(R.id.tv_type);
            tvPatientName = view.findViewById(R.id.tv_patient_name);
            tvPatientAge = view.findViewById(R.id.tv_patient_age);
            tvContactPersonName = view.findViewById(R.id.tv_contact_person_name);
            tvContactPersonMobNo = view.findViewById(R.id.tv_contact_person_mob_no);
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

