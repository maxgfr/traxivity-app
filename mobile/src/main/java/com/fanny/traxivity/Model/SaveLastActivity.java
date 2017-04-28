package com.fanny.traxivity.Model;

import com.fanny.traxivity.Database.ActivityType;
import com.fanny.traxivity.Database.DbActivity;

import java.util.Date;

/**
 * Created by 1707795 on 28/04/2017.
 */

public class SaveLastActivity {
    public static DbActivity lastActivity = new DbActivity(new Date(), new Date(),0,ActivityType.Null,0);
}
