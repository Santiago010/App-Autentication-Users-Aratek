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

-keep class cn.com.aratek.dev.DeviceSecurityEventListener { *; }
-keep class cn.com.aratek.dev.DeviceSecurityManager { *; }
-keep class cn.com.aratek.dev.ExportedGpio { *; }
-keep class cn.com.aratek.dev.Terminal { *; }
-keep class cn.com.aratek.dev.WatchdogManager { *; }
-keep class cn.com.aratek.dev.Wiegand { *; }
-keep class cn.com.aratek.fp.Bione { *; }
-keep class cn.com.aratek.fp.FingerprintImage { *; }
-keep class cn.com.aratek.fp.FingerprintScanner { *; }
-keep class cn.com.aratek.iccard.ICCardReader { *; }
-keep class cn.com.aratek.idcard.IDCard { *; }
-keep class cn.com.aratek.idcard.IDCardReader { *; }
-keep class cn.com.aratek.lights.Light { *; }
-keep class cn.com.aratek.printer.PaperExhaustedException { *; }
-keep class cn.com.aratek.printer.Printer { *; }
-keep class cn.com.aratek.qrc.CodeScanner { *; }
-keep class cn.com.aratek.util.Result { *; }
-keep class com.cognaxon.WSQlib { *; }
