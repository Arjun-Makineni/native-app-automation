package com.riverisland.app.automation.pojos;

/**
 * Created by Prashant Ramcharan on 18/10/2017
 */
public class Environment {
    private String name;
    private boolean forceChange;

    public Environment() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = System.getProperty("environment.name", name);
    }

    public boolean forceChange() {
        return forceChange;
    }

    public void setForceChange(boolean forceChange) {
        this.forceChange = Boolean.parseBoolean(System.getProperty("environment.forceChange", String.valueOf(forceChange)));
    }
}
