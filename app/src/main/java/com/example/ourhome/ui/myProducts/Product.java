package com.example.ourhome.ui.myProducts;

public class Product {

    private String userID;
    private String userName;
    private String products;
    private double price;

    public Product(String userID, String userName, String products, double price) {
        this.userID = userID;
        this.userName = userName;
        this.products = products;
        this.price = price;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public String getProducts() {
        return products;
    }

    public double getPrice() {
        return price;
    }
}
