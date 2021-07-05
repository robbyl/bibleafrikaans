package tz.co.wadau.bibleinafrikaans;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import tz.co.wadau.bibleinafrikaans.utils.Utils;


public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setupTheme(this);
        setContentView(R.layout.activity_about);

        Toolbar toolbar =  findViewById(R.id.toolbar_about);
        TextView appVersion = findViewById(R.id.app_version);
        TextView privacyPolicy =  findViewById(R.id.privacy_policy);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        privacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());

        PackageManager packageManager = getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String version = getString(R.string.version) + " " + packageInfo.versionName;
        appVersion.setText(version);
    }
}
