package com.technikh.onedrupal.adapter;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.technikh.onedrupal.R;
import com.technikh.onedrupal.models.ModelFanPosts;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<ModelFanPosts> modelFanPosts;
    private RecyclerViewClickListener itemListener;
    public static final int VIEW_ITEM = 1;
    public static final int VIEW_LOAD = 0;

    public GalleryAdapter(Context context, List<ModelFanPosts> modelFanPosts, RecyclerViewClickListener itemListener) {
        this.context = context;
        this.modelFanPosts = modelFanPosts;
        this.itemListener = itemListener;
    }

    public interface RecyclerViewClickListener {
        void onItemClickListener(View v, int position);
    }

    public void addOneRequestData(ModelFanPosts model) {
        modelFanPosts.add(model);
        notifyDataSetChanged();
    }

    public void clearAll() {
        modelFanPosts.clear();

    }

    public List<ModelFanPosts> getAllModelPost() {
        return modelFanPosts;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        if (i == VIEW_LOAD) {
            return new LoadingViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_progress, viewGroup, false));
        }
        return new ItemListHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_gallery, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vho, int i) {
        if (vho instanceof ItemListHolder) {
            ModelFanPosts singleModelFanPosts = modelFanPosts.get(i);

            ItemListHolder itemListHolder = (ItemListHolder) vho;
            Glide.with(context)
                    .load(singleModelFanPosts.getField_image())
                    .into(itemListHolder.ivImage);
            Log.e("Image", singleModelFanPosts.getField_image());
            itemListHolder.tvTitle.setText(singleModelFanPosts.getTitle());

        } else {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) vho;
            //       loadingViewHolder.pr_progress.isSpinning();
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
        ImageView ivImage;
        TextView tvTitle;

        ItemListHolder(View view) {
            super(view);

            this.tvTitle = view.findViewById(R.id.tvTitle);
            this.ivImage = view.findViewById(R.id.ivImage);

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