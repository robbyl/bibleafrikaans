# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\admin\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

-keep class android.support.v7.widget.SearchView { *; }
-keep class android.app.Application
-keep class * extends android.app.Application
-keepattributes *Annotation*

-keep class com.firebase.** { *; }
-keep class org.apache.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }
-dontwarn org.apache.**
-dontwarn org.w3c.dom.**

-ignorewarnings
-keep class * {
    public private *;
}

# GOOGLE MOBILE ADS (ADMOB) - START
-keep public class com.google.android.gms.ads.** {
   public *;
}
-keep public class com.google.ads.** {
   public *;
}
-keep class com.google.ads.mediation.admob.AdMobAdapter {
    *;
}
-keep class com.google.ads.mediation.AdUrlAdapter {
    *;
}
# GOOGLE MOBILE ADS (ADMOB) - END

# FACEBOOK AUDIENCE NETWORK - START
-keep public class com.facebook.ads.** {
   public *;
}
-keep class com.google.ads.mediation.facebook.FacebookAdapter {
    *;
}
-dontwarn com.facebook.ads.internal.**
# FACEBOOK AUDIENCE NETWORK - END

# GOOGLE ANALYTICS - START
-keep public class com.google.android.gms.analytics.** {
    public *;
}
# GOOGLE ANALYTICS - END