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
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
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
import com.technikh.onedrupal.app.MyApplication;
import com.technikh.onedrupal.models.TeaserModel;

import java.util.List;

public class TeaserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<TeaserModel> teaserModel;
    private RecyclerViewClickListener itemListener;
    public static final int VIEW_ITEM = 1;
    public static final int VIEW_TERM_ITEM = 2;
    public static final int VIEW_LOAD = 0;

    public TeaserAdapter(Context context, List<TeaserModel> teaserModel, RecyclerViewClickListener itemListener) {
        this.context = context;
        this.teaserModel = teaserModel;
        this.itemListener = itemListener;
    }

    public interface RecyclerViewClickListener {
        void onItemClickListener(View v, int position);
    }

    public void addOneRequestData(TeaserModel model, boolean topPosition)
    {
        if(topPosition) {
            teaserModel.add(0, model);
        }else{
            teaserModel.add(model);
        }
        notifyDataSetChanged();
    }

    public void clearAll()
    {
        teaserModel.clear();

    }

    public List<TeaserModel> getAllModelPost()
    {
        return teaserModel;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        if (i == VIEW_LOAD) {
            return new LoadingViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_progress, viewGroup, false));
        }
        if (i == VIEW_TERM_ITEM) {
            return new TermListHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_teaser_term_item, viewGroup, false));
        }
        return new ItemListHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_fan_posts, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vho, int i) {
        if (vho instanceof ItemListHolder) {
            TeaserModel singleModelFanPosts = teaserModel.get(i);

            ItemListHolder itemListHolder = (ItemListHolder) vho;
            Glide
                    .with(context)
                    .load(R.mipmap.ic_launcher)
                    .into(itemListHolder.iv_row_fan_post_user_image);
            itemListHolder.tv_row_fan_post_title.setText(Html.fromHtml(singleModelFanPosts.getTitle()));
            itemListHolder.tv_row_fan_post_category.setText(singleModelFanPosts.getField_text_category());
            itemListHolder.tv_row_fan_post_body.setText(Html.fromHtml(singleModelFanPosts.getBody()));
            // Unpublished pink background color
            if (!singleModelFanPosts.isPublished()) {
                Log.d("singleModelFanPosts", "onBindViewHolder: background color pink" + singleModelFanPosts.getTitle());
                itemListHolder.cl_row_layout.setBackgroundResource(R.color.LightPink);
                itemListHolder.tv_row_fan_post_title.setBackgroundColor(Color.MAGENTA);
            }
            String image_url = singleModelFanPosts.getField_image();
            if (!image_url.isEmpty()) {
                itemListHolder.iv_row_fan_post_image.setVisibility(View.VISIBLE);
                //Log.e("getField_image", "getField_image " + singleModelFanPosts.getField_image());
                Glide
                        .with(itemListHolder.iv_row_fan_post_image)
                        .load(singleModelFanPosts.getField_image())
                        .into(itemListHolder.iv_row_fan_post_image);
            }
            String video_url = singleModelFanPosts.field_video;
            if (!video_url.isEmpty()) {
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
            if (!remote_page_url.isEmpty()) {
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
                    //TeaserModel singleModelFanPosts = adapter.getAllModelPost().get(position);
                    MyApplication.getAppContext().startActivity(new Intent(context, ActivityFanPostDetails.class)
                            .putExtra("nid", singleModelFanPosts.getNid())
                            .putExtra("SiteProtocol", )
                            .putExtra("SiteDomain", mSiteDomain)
                    );
                }
            });*/

        }else if (vho instanceof TermListHolder) {
            TeaserModel singleModelFanPosts = teaserModel.get(i);

            TermListHolder termListHolder = (TermListHolder) vho;
            termListHolder.tv_row_fan_post_title.setText(Html.fromHtml(singleModelFanPosts.getTitle()));
        } else {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) vho;
            loadingViewHolder.pr_progress.isSpinning();
        }
    }

    @Override
    public int getItemCount() {
        return ((null != teaserModel && teaserModel.size() > 0) ? teaserModel.size() : 0);
    }

    @Override
    public int getItemViewType(int position) {
        if(teaserModel.get(position) != null){
            if(teaserModel.get(position).getEntityType().equals("taxonomy")){
                return VIEW_TERM_ITEM;
            }
            return VIEW_ITEM;
        }
        return VIEW_LOAD;
    }

    public class TermListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tv_row_fan_post_title;
        public TermListHolder(View itemView){
            super(itemView);
            this.tv_row_fan_post_title = itemView.findViewById(R.id.tv_row_fan_post_title);
            itemView.setOnClickListener(this);
            this.tv_row_fan_post_title.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemListener.onItemClickListener(view, getLayoutPosition());
        }
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