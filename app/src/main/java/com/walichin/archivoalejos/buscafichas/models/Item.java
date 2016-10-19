package com.walichin.archivoalejos.buscafichas.models;

/**
 * Created by bpncool on 2/23/2016.
 */
public class Item {

    private final String name;
    private final String id;

    public Item(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
