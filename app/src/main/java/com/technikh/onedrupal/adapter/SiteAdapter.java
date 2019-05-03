package com.technikh.onedrupal.adapter;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import com.technikh.onedrupal.R;
import com.technikh.onedrupal.activities.SiteContentTabsActivity;
import com.technikh.onedrupal.app.MyApplication;
import com.technikh.onedrupal.models.SettingsType;
import com.technikh.onedrupal.models.SettingsTypeList;
import com.technikh.onedrupal.models.Site;
import com.technikh.onedrupal.network.GetSiteDataService;
import com.technikh.onedrupal.network.RetrofitSiteInstance;
import com.technikh.onedrupal.widgets.ProgressDialogAsync;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.technikh.onedrupal.app.MyApplication.gblSettingsSection;

public class SiteAdapter extends RecyclerView.Adapter<SiteAdapter.EmployeeViewHolder> {

    private ArrayList<Site> dataList;
    private String TAG = "SiteAdapter";
    private Context mContext;
    ProgressDialogAsync _progressDialogAsync;

    public SiteAdapter(ArrayList<Site> dataList, Context context) {
        this.mContext = context;
        this.dataList = dataList;
    }

    @Override
    public EmployeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_site, parent, false);
        _progressDialogAsync = new ProgressDialogAsync(mContext);

        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EmployeeViewHolder holder, int position) {
        holder.txtEmpName.setText(dataList.get(position).getProtocol());
        holder.txtEmpEmail.setText(dataList.get(position).getDomain());
        holder.txtEmpPhone.setText(dataList.get(position).getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create handle for the getRetrofitSiteInstance interface
                GetSiteDataService service = RetrofitSiteInstance.getRetrofitSiteInstance(dataList.get(position).getProtocol(), dataList.get(position).getDomain(), null).create(GetSiteDataService.class);
                //Call the method with parameter in the interface to get the employee data
                Call<SettingsTypeList> call = service.getTypeData();
                //Log the URL called
                Log.d("URL Called", call.request().url() + "");

                //_progressDialogAsync.show();
                /*ProgressDialog pd = new ProgressDialog(mContext);
                pd.setMessage(mContext.getString(R.string.dialog_api_progress));
                pd.show();*/
                call.enqueue(new Callback<SettingsTypeList>() {
                    @Override
                    public void onResponse(Call<SettingsTypeList> call, Response<SettingsTypeList> response) {
                        if(response.isSuccessful()) {
                            Log.d(TAG, "onResponse: " + response.toString());
                            Log.d(TAG, "onResponse: body " + response.body().toString());
                            ArrayList<SettingsType> nodeTypesList = response.body().getTypesArrayList();
                            MyApplication.gblNodeTypeSettings.clear();
                            for (int j = 0; j < nodeTypesList.size(); j++) {
                                MyApplication.gblNodeTypeSettings.add(nodeTypesList.get(j));
                            }
                            gblSettingsSection = response.body().getSettingsSection();

                            Bundle b = new Bundle();
                            b.putBoolean("publicViewMode", true);
                            b.putInt("nodeTypesSize", nodeTypesList.size());
                            Log.d(TAG, "onResponse: nodeTypesList.size() " + nodeTypesList.size());
                            b.putString("SiteProtocol", dataList.get(position).getProtocol());
                            b.putString("SiteDomain", dataList.get(position).getDomain());

                            Intent intent = new Intent(mContext, SiteContentTabsActivity.class);
                            intent.putExtras(b);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                        }else{
                            new AlertDialog.Builder(mContext)
                                    .setTitle(mContext.getString(R.string.dialog_api_failed_title))
                                    .setMessage(mContext.getString(R.string.dialog_api_failed_message_prefix)+response.message()+"Code: "+response.code())
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                        //_progressDialogAsync.cancel();
                        //pd.dismiss();
                    }

                    @Override
                    public void onFailure(Call<SettingsTypeList> call, Throwable t) {
                        //pd.dismiss();
                        //_progressDialogAsync.cancel();
                        new AlertDialog.Builder(mContext)
                                .setTitle(mContext.getString(R.string.dialog_api_failed_title))
                                .setMessage(mContext.getString(R.string.dialog_api_failed_message_prefix)+t.getMessage())
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        //Toast.makeText(mContext, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class EmployeeViewHolder extends RecyclerView.ViewHolder {

        TextView txtEmpName, txtEmpEmail, txtEmpPhone;

        EmployeeViewHolder(View itemView) {
            super(itemView);
            txtEmpName = (TextView) itemView.findViewById(R.id.txt_site_name);
            txtEmpEmail = (TextView) itemView.findViewById(R.id.txt_site_email);
            txtEmpPhone = (TextView) itemView.findViewById(R.id.txt_site_phone);
        }
    }
}