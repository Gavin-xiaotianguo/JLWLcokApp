package com.example.gxt.jlwlcokapp.mylock;

import java.io.Serializable;

/**
 * Created by GXT on 2017/8/9.
 */

class Beacon implements Serializable{
    public String name;
    public String address;

    Beacon(String name, String address){
        this.name=name;
        this.address=address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}
