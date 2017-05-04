package fr.delicatessences.delicatessences;

import android.app.Application;

import net.mediavrog.irr.DefaultRuleEngine;

/**
 * Created by Tom on 27/04/2017.
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DefaultRuleEngine.trackAppStart(getApplicationContext());
    }
}
