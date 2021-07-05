package tz.co.wadau.bibleinafrikaans;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;
import androidx.appcompat.app.AppCompatDelegate;
import android.util.Log;

public class BikansApp extends Application {
    public final String TAG = BikansApp.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        checkAppReplacingState();
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void checkAppReplacingState() {
        Log.d(TAG, "app start...");
        if (getResources() == null) {
            Log.d(TAG, "app is replacing...kill");
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}
