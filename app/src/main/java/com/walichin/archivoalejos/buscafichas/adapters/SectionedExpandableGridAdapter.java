package com.walichin.archivoalejos.buscafichas.adapters;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.walichin.archivoalejos.buscafichas.R;
import com.walichin.archivoalejos.buscafichas.models.Image;
import com.walichin.archivoalejos.buscafichas.models.Item;

import java.util.ArrayList;

/**
 * Created by walteralejosgongora on 3/3/16.
 */
public class SectionedExpandableGridAdapter extends RecyclerView.Adapter<SectionedExpandableGridAdapter.ViewHolder> {

    //data array
    private ArrayList<Object> mDataArrayList;

    //context
    private final Context mContext;

    //listeners
    private final ItemClickListener mItemClickListener;
    private final SectionStateChangeListener mSectionStateChangeListener;

    //view type
    private static final int VIEW_TYPE_SECTION = R.layout.layout_section;
    private static final int VIEW_TYPE_ITEM = R.layout.layout_item; //TODO : change this
    private static final int VIEW_TYPE_IMAGE = R.layout.layout_image;

    public SectionedExpandableGridAdapter (Context context, ArrayList<Object> dataArrayList,
                                            final GridLayoutManager gridLayoutManager, ItemClickListener itemClickListener,
                                            SectionStateChangeListener sectionStateChangeListener) {

        mContext = context;
        mItemClickListener = itemClickListener;
        mSectionStateChangeListener = sectionStateChangeListener;
        mDataArrayList = dataArrayList;

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return isImage(position)?1:gridLayoutManager.getSpanCount();
            }
        });
    }

    private boolean isSection(int position) {
        return mDataArrayList.get(position) instanceof Section;
    }

    private boolean isItem(int position) {
        return mDataArrayList.get(position) instanceof Item;
    }

    private boolean isImage(int position) {
        return mDataArrayList.get(position) instanceof Image;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //return new ViewHolder(LayoutInflater.from(mContext).inflate(viewType, parent, false), viewType);

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(viewType, parent, false);
        return new ViewHolder(view, viewType);

    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (holder.viewType) {
            case VIEW_TYPE_ITEM :
                final Item item = (Item) mDataArrayList.get(position);
                holder.itemTextView.setText(item.getName());
                holder.labelTextView.setText(item.getId());
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.itemClicked(item);
                    }
                });
                break;
            case VIEW_TYPE_SECTION :
                final Section section = (Section) mDataArrayList.get(position);
                holder.sectionTextView.setText(section.getName());
                holder.sectionTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.itemClicked(section);
                    }
                });
                holder.sectionToggleButton.setChecked(section.isExpanded);
                holder.sectionToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mSectionStateChangeListener.onSectionStateChanged(section, isChecked);
                    }
                });
                break;
            case VIEW_TYPE_IMAGE :
                final Image image = (Image) mDataArrayList.get(position);
                holder.imageView.setImageBitmap(image.getBitmap());

//                holder.view.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mItemClickListener.itemClicked(item);
//                    }
//                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mDataArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isSection(position))
            return VIEW_TYPE_SECTION;
        else {
            if (isItem(position)) {
                return VIEW_TYPE_ITEM;
            } else {
                return VIEW_TYPE_IMAGE;
            }
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        //common
        View view;
        int viewType;

        //for section
        TextView sectionTextView;
        ToggleButton sectionToggleButton;

        //for item
        TextView itemTextView;
        TextView labelTextView;

        //for image
        ImageView imageView;

        public ViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;
            this.view = view;
            if (viewType == VIEW_TYPE_ITEM) {
                itemTextView = (TextView) view.findViewById(R.id.text_item);
                labelTextView = (TextView) view.findViewById(R.id.label_item);
            } else {
                if (viewType == VIEW_TYPE_SECTION) {
                    sectionTextView = (TextView) view.findViewById(R.id.text_section);
                    sectionToggleButton = (ToggleButton) view.findViewById(R.id.toggle_button_section);
                } else {
                    imageView = (ImageView) view.findViewById(R.id.imageView);
                }
            }
        }
    }
}
