package com.technikh.onedrupal.adapter;


    import android.support.v7.widget.RecyclerView;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.TextView;
    import com.technikh.onedrupal.R;
    import com.technikh.onedrupal.models.BreadcumModel;

    import java.util.ArrayList;
    import java.util.List;


public class BreadcumAdapter extends RecyclerView.Adapter<BreadcumAdapter.MyViewHolder> {

        private buttonEventListenr buttonEventListenr;
        private List<BreadcumModel> itemList;


        public interface buttonEventListenr {

            void buttonEvent(int position,String SiteTitle,String titleid,String vocabularyid);

        }

        public BreadcumAdapter(List<BreadcumModel>itemList , buttonEventListenr listner) {
            this.itemList = itemList;
            this.buttonEventListenr = listner;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.breadcum_list, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            holder.title.setText(itemList.get(position).getXYZ());


            holder.title.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    buttonEventListenr.buttonEvent(position,itemList.get(position).getXYZ(), itemList.get(position).getTitleId(),itemList.get(position).getVocabId());

                }
            });

        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            public TextView title;

            public MyViewHolder(View view) {
                super(view);
                title = (TextView) view.findViewById(R.id.item);
            }
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }
    }