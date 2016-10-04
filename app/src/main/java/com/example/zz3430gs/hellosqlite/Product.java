package com.example.zz3430gs.hellosqlite;

/**
 * Created by zz3430gs on 10/4/16.
 */
public class Product {

    String name;
    int quantity;

    public Product(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Name: " + name + " quantity: " + quantity;
    }

}
