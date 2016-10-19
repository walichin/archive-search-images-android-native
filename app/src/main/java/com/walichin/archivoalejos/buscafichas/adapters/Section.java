package com.walichin.archivoalejos.buscafichas.adapters;

/**
 * Created by bpncool on 2/23/2016.
 */
public class Section {

    private final String name;

    public boolean isExpanded;

    public Section(String name, Boolean isExpanded) {
        this.name = name;
        this.isExpanded = isExpanded;
    }

    public String getName() {
        return name;
    }
}
