package tz.co.wadau.bibleafrikaans;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeAdView;
import com.facebook.ads.NativeAdViewAttributes;

import tz.co.wadau.bibleafrikaans.utils.Utils;

import static tz.co.wadau.bibleafrikaans.alarm.AlarmReceiver.DAILY_VERSE_NO;
import static tz.co.wadau.bibleafrikaans.alarm.AlarmReceiver.DAILY_VERSE_TEXT;

public class TodayVerseActivity extends AppCompatActivity {
    private final String TAG = TodayVerseActivity.class.getSimpleName();
    private String dailyVerseNo;
    private String dailyVerseText;
    private CardView nativeAdContainer;
    private NativeAd mNativeAd;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setupTheme(this);
        setContentView(R.layout.activity_today_verse);

        Toolbar toolbar = (Toolbar) findViewById(R.id.today_verse_toolbar);
        TextView dailyVerseTextView = (TextView) findViewById(R.id.daily_verse_text);
        TextView dailyVerseNoView = (TextView) findViewById(R.id.daily_verse_no);
        nativeAdContainer = (CardView) findViewById(R.id.native_ad_container);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        onNewIntent(getIntent());

        dailyVerseTextView.setText(dailyVerseText);
        dailyVerseNoView.setText(dailyVerseNo);
        mContext = this;

        showNativeAd();
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

    public void showNativeAd() {
        mNativeAd = new NativeAd(this, "1162476090570459_1162477160570352");
        mNativeAd.setAdListener(new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.d(TAG, adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {

                // Set the Native Ad attributes
                NativeAdViewAttributes viewAttributes = new NativeAdViewAttributes()
                        .setButtonColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                        .setButtonBorderColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark))
                        .setButtonTextColor(Color.WHITE);

                if (Utils.isBlackThemeEnabled(mContext)) {
                    viewAttributes.setBackgroundColor(Color.parseColor("#424242"));
                    viewAttributes.setTitleTextColor(Color.WHITE);
                    viewAttributes.setDescriptionTextColor(Color.parseColor("#c7c7c7"));
                } else {
                    viewAttributes.setBackgroundColor(Color.WHITE);
                }

                // Render the Native Ad Template
                View adView = NativeAdView.render(TodayVerseActivity.this, mNativeAd,
                        NativeAdView.Type.HEIGHT_300, viewAttributes);
                // Add the Native Ad View to your ad container
                nativeAdContainer.addView(adView);
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });

        //Initialize a request to load ad
        AdSettings.addTestDevice("2ca460b475e01e764dfc2aeee5febde2");
        AdSettings.addTestDevice("4514a75e-cd6c-438e-82a0-9b3a9ae0c1ea");
        AdSettings.addTestDevice("a3392fabec7b0521cef85148f4b8c39f"); //My Nexus 5
        AdSettings.addTestDevice("dea1edc7-4a56-47fc-8c48-ba0c8fdffa5a"); //My nexus 5x
        mNativeAd.loadAd();
    }
}
