package com.first.planning.persistent.common.service;

import android.content.Context;

import com.first.planning.persistent.common.service.impl.RoomDataService;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

public class DataServiceResolver {

    public static RoomDataService resolve(Properties properties, Context context) {
        RoomDataService dataService;
        try {
            dataService = (RoomDataService) Class.forName(properties.getProperty("database.service-class")).getConstructor().newInstance();
            dataService.init(context);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            //dataService = new MockDataServiceImpl();
            return null;
        }
        return dataService;
    };
}
