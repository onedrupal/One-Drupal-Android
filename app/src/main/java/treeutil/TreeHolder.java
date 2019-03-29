package treeutil;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.technikh.onedrupal.R;
import com.unnamed.b.atv.model.TreeNode;

import java.util.logging.Level;

public class TreeHolder extends TreeNode.BaseNodeViewHolder<String> {
    int level;

    public TreeHolder(Context context, int level) {
        super(context);
        this.level = level;
    }

    @Override
    public View createNodeView(TreeNode node, String value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_node, null, false);
        View leadingView = view.findViewById(R.id.leadingView);
        if (level != 0) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dpToPx(25*level),dpToPx(25));

            leadingView.setLayoutParams(layoutParams);
        }
        TextView tvValue = (TextView) view.findViewById(R.id.node_value);
        tvValue.setText(value + level);
        return view;
    }

    public int dpToPx(int dp) {
        float density = context.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }
}