# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
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

# This is to remove Log statements from the source code
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# This might only be necessary if features making use of Retrofit stop working in the release build.
## ===============================
## MINIMAL KOTLIN SERIALIZATION RULES
## ===============================
#
## Keep Retrofit interfaces (Retrofit still uses reflection)
#-keep interface com.drsapps.trackerforlegominifiguresseries.data.remote.** { *; }
#
## Keep generated serializer classes
#-keep,includedescriptorclasses class com.drsapps.trackerforlegominifiguresseries.**$serializer { *; }
#
## Keep companion objects (they contain serializers)
#-keepclassmembers class com.drsapps.trackerforlegominifiguresseries.** {
#    *** Companion;
#}
#
## Keep signature for generic types
#-keepattributes Signature