package com.technikh.onedrupal.models;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.util.Log;

public class nodeData {

    final String id;
    final String name;
    final int age;

    nodeData(String id, String name, int age) {
        Log.d("nodeData", "nodeData: ");
        this.id = id;
        this.name = name;
        this.age = age;
    }
}