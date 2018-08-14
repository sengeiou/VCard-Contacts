# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Administrator\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
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

-keep class com.android.contacts.model.Sources {
  public <init>(...);
}

# Xml files containing onClick (menus and layouts) require that proguard not
# remove their handlers.
-keepclassmembers class * extends android.app.Activity {
  public void *(android.view.View);
  public void *(android.view.MenuItem);
}

# Any class or method annotated with NeededForTesting or NeededForReflection.
-keep @com.android.contacts.common.testing.NeededForTesting class *
-keep @com.android.contacts.test.NeededForReflection class *
-keepclassmembers class * {
@com.android.contacts.common.testing.NeededForTesting *;
@com.android.contacts.test.NeededForReflection *;
}

-verbose
