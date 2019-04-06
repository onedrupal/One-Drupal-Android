package treeutil;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.johnkil.print.PrintView;
import com.technikh.onedrupal.R;
import com.unnamed.b.atv.model.TreeNode;

/**
 * Created by Bogdan Melnychuk on 2/15/15, modified by Szigeti Peter 2/2/16.
 */
public class ArrowExpandSelectableHeaderHolder extends TreeNode.BaseNodeViewHolder<MyObject> {
    private TextView tvValue;
    private PrintView arrowView;
    private CheckBox nodeSelector;
    int level;

    public ArrowExpandSelectableHeaderHolder(Context context, int level) {
        super(context);
        this.level = level;
    }

    @Override
    public View createNodeView(final TreeNode node, MyObject value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_node, null, false);

        if (level != 0) {
            View leadingView = view.findViewById(R.id.leadingView);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dpToPx(25*level),dpToPx(25));

            leadingView.setLayoutParams(layoutParams);
        }
        tvValue = (TextView) view.findViewById(R.id.node_value);
        tvValue.setText(value.name);

        /*final PrintView iconView = (PrintView) view.findViewById(R.id.icon);
        iconView.setIconText(context.getResources().getString(value.icon));
*/
        arrowView = (PrintView) view.findViewById(R.id.arrow_icon);
        arrowView.setPadding(20,10,10,10);
        if (node.isLeaf()) {
            arrowView.setVisibility(View.GONE);
        }
        arrowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tView.toggleNode(node);
            }
        });

        nodeSelector = (CheckBox) view.findViewById(R.id.node_selector);
        nodeSelector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                node.setSelected(isChecked);
                value.setSelected(isChecked);
                /*for (TreeNode n : node.getChildren()) {
                    getTreeView().selectNode(n, isChecked);
                }*/
            }
        });
        nodeSelector.setChecked(node.isSelected());

        return view;
    }

    @Override
    public void toggle(boolean active) {
        arrowView.setIconText(context.getResources().getString(active ? R.string.ic_keyboard_arrow_down : R.string.ic_keyboard_arrow_right));
    }

    @Override
    public void toggleSelectionMode(boolean editModeEnabled) {
        nodeSelector.setVisibility(editModeEnabled ? View.VISIBLE : View.GONE);
        nodeSelector.setChecked(mNode.isSelected());
    }

    public int dpToPx(int dp) {
        float density = context.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }
}
