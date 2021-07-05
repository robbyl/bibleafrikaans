package tz.co.wadau.bibleinafrikaans.utils;

import android.content.Context;
import android.util.Log;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

import static tz.co.wadau.bibleinafrikaans.BibleActivity.SHOW_AD_WHEN_LOADED;

public class AdManager {
    // Static fields are shared between all instances.
    private static InterstitialAd interstitialAd;
    public static final String TAG = AdManager.class.getSimpleName();
    public static boolean adShowed = false;

    public static void initialize(Context activity) {
        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(activity);
        interstitialAd = new InterstitialAd(activity, "619312825220910_1197919644026889");
    }

    public static void createAd() {
        // Create listeners for the Interstitial Ad
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                SHOW_AD_WHEN_LOADED = false;
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                createAd();
                Log.e(TAG, "Interstitial ad dismissed.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        };

        AdSettings.addTestDevice("1370910e-d203-4aa6-a5cd-7297d89cfef9");
        interstitialAd.loadAd(interstitialAd.buildLoadAdConfig()
                .withAdListener(interstitialAdListener)
                .build());
    }

    public static InterstitialAd getAd() {
        if (interstitialAd != null && interstitialAd.isAdLoaded()) {
            return interstitialAd;
        } else return null;
    }
}