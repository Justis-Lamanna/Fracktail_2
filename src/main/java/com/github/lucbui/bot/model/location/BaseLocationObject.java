package com.github.lucbui.bot.model.location;

import com.github.lucbui.bot.dao.LocationDao;

public abstract class BaseLocationObject {
    private LocationDao locationDao;

    public BaseLocationObject(LocationDao locationDao) {
        this.locationDao = locationDao;
    }

    public LocationDao getLocationDao() {
        return locationDao;
    }
}
