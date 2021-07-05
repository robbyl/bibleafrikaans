package tz.co.wadau.bibleinafrikaans;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.NativeAd;

import tz.co.wadau.bibleinafrikaans.utils.Utils;

import static tz.co.wadau.bibleinafrikaans.alarm.AlarmReceiver.DAILY_VERSE_NO;
import static tz.co.wadau.bibleinafrikaans.alarm.AlarmReceiver.DAILY_VERSE_TEXT;

public class TodayVerseActivity extends AppCompatActivity {
    private final String TAG = TodayVerseActivity.class.getSimpleName();
    private String dailyVerseNo;
    private String dailyVerseText;
    private NativeAd mNativeAd;
    private Context mContext;
    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setupTheme(this);
        setContentView(R.layout.activity_today_verse);

        Toolbar toolbar = findViewById(R.id.today_verse_toolbar);
        TextView dailyVerseTextView = findViewById(R.id.daily_verse_text);
        TextView dailyVerseNoView =  findViewById(R.id.daily_verse_no);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        onNewIntent(getIntent());

        dailyVerseTextView.setText(dailyVerseText);
        dailyVerseNoView.setText(dailyVerseNo);
        mContext = this;

        showBannerAd();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            dailyVerseNo = extras.getString(DAILY_VERSE_NO);
            dailyVerseText = extras.getString(DAILY_VERSE_TEXT);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_today_verse, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share_verse:
                shareTodayVerse();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adView != null) {
            adView.destroy();
        }
    }

    public void shareTodayVerse() {
        String shareText = dailyVerseNo + "\n" + dailyVerseText;
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.setType("text/plain");

        String title = getString(R.string.share_verse_of_the_day);
        Intent chooser = Intent.createChooser(shareIntent, title);
        chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(new Intent(chooser));
        }
    }

    private void showBannerAd() {
        adView = new AdView(this, "619312825220910_1197922457359941", AdSize.BANNER_HEIGHT_50);
        LinearLayout adContainer = findViewById(R.id.banner_container);

        AdListener adListener = new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.d(TAG, "Error loading ad " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad loaded callback
                Log.d(TAG, "Ad loaded ");
                if(adView.getParent() != null){
                    ((ViewGroup) adView.getParent()).removeView(adView);
                }
                adContainer.addView(adView); //Show banner ad
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        };

        AdSettings.addTestDevice("f073bf73-8753-40da-ad17-98920a6620b0");
        adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build());
    }
}
