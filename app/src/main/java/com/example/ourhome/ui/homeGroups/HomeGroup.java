package com.example.ourhome.ui.homeGroups;

public class HomeGroup {

    private String name;
    private String address;
    private String[] users;
    private boolean isChecked;

    public HomeGroup(String name, String address, String[] users, boolean isChecked) {
        this.name = name;
        this.address = address;
        this.users = users;
        this.isChecked = isChecked;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String[] getUsers() {
        return users;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean value) {
        isChecked = value;
    }
}
