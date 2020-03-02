package com.jovieites.tutiempo.ListItem;

public class Item {
    private String ciudad;

    public Item(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getCiudad() {
        return ciudad;
    }

    @Override
    public String toString() {
        return ciudad;
    }
}
