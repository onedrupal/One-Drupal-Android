package com.prominentdev.blog.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.prominentdev.blog.R;
import com.prominentdev.blog.models.ModelFanPosts;

import java.util.List;

/**
 * Created by Narender Kumar on 9/30/2018.
 * Promiment Developers, Faridabad (India).
 */
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
            /*Glide
                    .with(itemListHolder.iv_row_fan_post_image)
                    .load(singleModelFanPosts.getField_image())
                    .into(itemListHolder.iv_row_fan_post_image);*/
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

        ItemListHolder(View view) {
            super(view);

            this.iv_row_fan_post_user_image = view.findViewById(R.id.iv_row_fan_post_user_image);
            this.iv_row_fan_post_image = view.findViewById(R.id.iv_row_fan_post_image);
            this.tv_row_fan_post_title = view.findViewById(R.id.tv_row_fan_post_title);
            this.tv_row_fan_post_category = view.findViewById(R.id.tv_row_fan_post_category);
            this.tv_row_fan_post_body = view.findViewById(R.id.tv_row_fan_post_body);
            view.setOnClickListener(this);
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