package com.walichin.archivoalejos.buscafichas.adapters;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.walichin.archivoalejos.buscafichas.models.Image;
import com.walichin.archivoalejos.buscafichas.models.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by bpncool on 2/23/2016.
 */
public class SectionedExpandableLayoutHelper implements SectionStateChangeListener {

    //data list
    public LinkedHashMap<Section, ArrayList<Object>> mSectionDataMap = new LinkedHashMap<Section, ArrayList<Object>>();
    private ArrayList<Object> mDataArrayList = new ArrayList<Object>();

    //section map
    //TODO : look for a way to avoid this
    public HashMap<String, Section> mSectionMap = new HashMap<String, Section>();

    //adapter
    private SectionedExpandableGridAdapter mSectionedExpandableGridAdapter;

    //recycler view
    RecyclerView mRecyclerView;

    public SectionedExpandableLayoutHelper(Context context, RecyclerView recyclerView, ItemClickListener itemClickListener,
                                           int gridSpanCount) {

        //setting the recycler view
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, gridSpanCount);
        recyclerView.setLayoutManager(gridLayoutManager);
        mSectionedExpandableGridAdapter = new SectionedExpandableGridAdapter(context, mDataArrayList,
                gridLayoutManager, itemClickListener, this);
        recyclerView.setAdapter(mSectionedExpandableGridAdapter);

        mRecyclerView = recyclerView;
    }

    public void notifyDataSetChanged() {
        //TODO : handle this condition such that these functions won't be called if the recycler view is on scroll
        generateDataList();
        mSectionedExpandableGridAdapter.notifyDataSetChanged();
    }

    public void addSection(String section, Boolean isExpanded, ArrayList<Object> objects) {
        Section newSection;
        mSectionMap.put(section, (newSection = new Section(section, isExpanded)));
        mSectionDataMap.put(newSection, objects);
    }

    public void addItem(String section, Item item) {
        mSectionDataMap.get(mSectionMap.get(section)).add(item);
    }

    public void addImage(String section, Image image) {
        mSectionDataMap.get(mSectionMap.get(section)).add(image);
    }

    public void removeSection(String section) {
        mSectionDataMap.remove(mSectionMap.get(section));
        mSectionMap.remove(section);
    }

    public void removeItem(String section, Item item) {
        mSectionDataMap.get(mSectionMap.get(section)).remove(item);
    }

    public void removeImage(String section, Image image) {
        mSectionDataMap.get(mSectionMap.get(section)).remove(image);
    }

    private void generateDataList () {
        mDataArrayList.clear();
        for (Map.Entry<Section, ArrayList<Object>> entry : mSectionDataMap.entrySet()) {
            Section key;
            mDataArrayList.add((key = entry.getKey()));

            if (!entry.getValue().isEmpty() && key.isExpanded)
                mDataArrayList.addAll(entry.getValue());
        }
    }

    @Override
    public void onSectionStateChanged(Section section, boolean isOpen) {
        if (!mRecyclerView.isComputingLayout()) {
            section.isExpanded = isOpen;
            notifyDataSetChanged();
        }
    }
}
