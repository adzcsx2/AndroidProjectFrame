package com.iim.ego.model;

/**
 * Created by Hoyn on 17/3/23.
 */

public class NameBean extends BaseBean{
    String name;

    public NameBean(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
