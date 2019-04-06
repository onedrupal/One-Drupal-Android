package com.technikh.onedrupal.adapter;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.technikh.onedrupal.R;
import com.technikh.onedrupal.activities.ActivityFanPostDetails;
import com.technikh.onedrupal.app.MyApplication;
import com.technikh.onedrupal.models.ModelFanPosts;

import java.util.List;

public class AdapterFanPosts extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<ModelFanPosts> modelFanPosts;
    private RecyclerViewClickListener itemListener;
    public static final int VIEW_ITEM = 1;
    public static final int VIEW_LOAD = 0;

    public AdapterFanPosts(Context context, List<ModelFanPosts> modelFanPosts, RecyclerViewClickListener itemListener) {
        this.context = context;
        this.modelFanPosts = modelFanPosts;
        this.itemListener = itemListener;
    }

    public interface RecyclerViewClickListener {
        void onItemClickListener(View v, int position);
    }

    public void addOneRequestData(ModelFanPosts model)
    {
        modelFanPosts.add(model);
        notifyDataSetChanged();
    }

    public void clearAll()
    {
        modelFanPosts.clear();

    }

    public List<ModelFanPosts> getAllModelPost()
    {
        return modelFanPosts;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        if (i == VIEW_LOAD) {
            return new LoadingViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_progress, viewGroup, false));
        }
        return new ItemListHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_fan_posts, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vho, int i) {
        if (vho instanceof ItemListHolder) {
            ModelFanPosts singleModelFanPosts = modelFanPosts.get(i);

            ItemListHolder itemListHolder = (ItemListHolder) vho;
            Glide
                    .with(context)
                    .load(R.mipmap.ic_launcher)
                    .into(itemListHolder.iv_row_fan_post_user_image);
            itemListHolder.tv_row_fan_post_title.setText(Html.fromHtml(singleModelFanPosts.getTitle()));
            itemListHolder.tv_row_fan_post_category.setText(singleModelFanPosts.getField_text_category());
            itemListHolder.tv_row_fan_post_body.setText(Html.fromHtml(singleModelFanPosts.getBody()));
            // Unpublished pink background color
            if(!singleModelFanPosts.isPublished()){
                Log.d("singleModelFanPosts", "onBindViewHolder: background color pink"+singleModelFanPosts.getTitle());
                itemListHolder.cl_row_layout.setBackgroundResource(R.color.LightPink);
                itemListHolder.tv_row_fan_post_title.setBackgroundColor(Color.MAGENTA);
            }
            String image_url = singleModelFanPosts.getField_image();
            if(!image_url.isEmpty()){
                itemListHolder.iv_row_fan_post_image.setVisibility(View.VISIBLE);
                //Log.e("getField_image", "getField_image " + singleModelFanPosts.getField_image());
                Glide
                        .with(itemListHolder.iv_row_fan_post_image)
                        .load(singleModelFanPosts.getField_image())
                        .into(itemListHolder.iv_row_fan_post_image);
            }
            String video_url = singleModelFanPosts.field_video;
            if(!video_url.isEmpty()) {
                itemListHolder.tv_row_read_more.setText("Watch Video...");
                itemListHolder.tv_row_read_more.setVisibility(View.VISIBLE);
                itemListHolder.tv_row_read_more.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(video_url));
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        MyApplication.getAppContext().startActivity(i);
                    }
                });
            }
            String remote_page_url = singleModelFanPosts.field_remote_page;
            if(!remote_page_url.isEmpty()) {
                itemListHolder.tv_row_read_more.setText("Read More...");
                itemListHolder.tv_row_read_more.setVisibility(View.VISIBLE);
                itemListHolder.tv_row_read_more.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(remote_page_url));
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        MyApplication.getAppContext().startActivity(i);
                    }
                });
            }
            /*itemListHolder.tv_row_view_post.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //ModelFanPosts singleModelFanPosts = adapter.getAllModelPost().get(position);
                    MyApplication.getAppContext().startActivity(new Intent(context, ActivityFanPostDetails.class)
                            .putExtra("nid", singleModelFanPosts.getNid())
                            .putExtra("SiteProtocol", )
                            .putExtra("SiteDomain", mSiteDomain)
                    );
                }
            });*/


        } else {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) vho;
            loadingViewHolder.pr_progress.isSpinning();
        }
    }

    @Override
    public int getItemCount() {
        return ((null != modelFanPosts && modelFanPosts.size() > 0) ? modelFanPosts.size() : 0);
    }

    @Override
    public int getItemViewType(int position) {
        return modelFanPosts.get(position) != null ? VIEW_ITEM : VIEW_LOAD;
    }

    public class ItemListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView iv_row_fan_post_user_image, iv_row_fan_post_image;
        private TextView tv_row_fan_post_title, tv_row_fan_post_category, tv_row_fan_post_body;
        private ConstraintLayout cl_row_layout;
        private Button tv_row_read_more,tv_row_view_post;

        ItemListHolder(View view) {
            super(view);

            this.iv_row_fan_post_user_image = view.findViewById(R.id.iv_row_fan_post_user_image);
            this.iv_row_fan_post_image = view.findViewById(R.id.iv_row_fan_post_image);
            this.cl_row_layout = view.findViewById(R.id.cl_row_layout);
            this.tv_row_fan_post_title = view.findViewById(R.id.tv_row_fan_post_title);
            this.tv_row_fan_post_category = view.findViewById(R.id.tv_row_fan_post_category);
            this.tv_row_read_more = view.findViewById(R.id.tv_row_read_more);
            this.tv_row_view_post = view.findViewById(R.id.tv_row_view_post);
            this.tv_row_fan_post_body = view.findViewById(R.id.tv_row_fan_post_body);
            view.setOnClickListener(this);
            this.tv_row_read_more.setOnClickListener(this);
            this.tv_row_view_post.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemListener.onItemClickListener(view, getLayoutPosition());
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressWheel pr_progress;

        LoadingViewHolder(View v) {
            super(v);
            pr_progress = v.findViewById(R.id.pr_progress);
        }
    }
}