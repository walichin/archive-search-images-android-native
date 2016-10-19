package com.walichin.archivoalejos.buscafichas.adapters;

import com.walichin.archivoalejos.buscafichas.models.Item;

/**
 * Created by lenovo on 2/23/2016.
 */
public interface ItemClickListener {
    void itemClicked(Item item);
    void itemClicked(Section section);
}
