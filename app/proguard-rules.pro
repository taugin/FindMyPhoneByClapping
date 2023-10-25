# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#-keep class com.clapping.find.phone.** {*;}

-classobfuscationdictionary ./proguard-keys.txt
-packageobfuscationdictionary ./proguard-keys.txt
-obfuscationdictionary ./proguard-keys.txt

#litepal
-keep class * extends org.litepal.** {*;}
-keep class org.litepal.** {*;}

-dontshrink
-dontoptimize

#-keep class androidx.**{*;}
#-keep class android.view.**{*;}
-keep @androidx.annotation.Keep class *
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}

-keepattributes *Annotation*

-keep class android.support.** { *; }
#保留v、d、e的log移除w、i的log
 -assumenosideeffects class android.util.Log{
#     public static *** v(...);
     public static *** i(...);
#     public static *** d(...);
     public static *** w(...);
#     public static *** e(...);
 }