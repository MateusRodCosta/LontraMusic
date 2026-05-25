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

# Obfuscation provides absolutely zero size reduction.
-dontobfuscate

# Hack to fix VerifyError for Jetpack Compose. This doesn't seem to impact binary size anyway.
-keep class org.sunsetware.phocid.** { *; }

# This library uses reflection.
-keep class org.jaudiotagger.** { *; }
-keep class org.jcodec.** { *; }

# WorkManager and Room
-keep class androidx.work.impl.WorkDatabase_Impl { *; }
-keep class * extends androidx.room.RoomDatabase
-keep class androidx.work.impl.** { *; }
-keep class androidx.room.** { *; }
-keep class androidx.sqlite.** { *; }
-keep class androidx.startup.** { *; }

# ICU4J
-keep class com.ibm.icu.** { *; }

-keepattributes *Annotation*
-keepattributes Signature
-keepattributes EnclosingMethod
-keepattributes InnerClasses

-dontwarn java.awt.Graphics2D
-dontwarn java.awt.Image
-dontwarn java.awt.geom.AffineTransform
-dontwarn java.awt.image.BufferedImage
-dontwarn java.awt.image.ImageObserver
-dontwarn java.awt.image.RenderedImage
-dontwarn javax.imageio.ImageIO
-dontwarn javax.imageio.ImageWriter
-dontwarn javax.imageio.stream.ImageInputStream
-dontwarn javax.imageio.stream.ImageOutputStream
-dontwarn javax.swing.filechooser.FileFilter
-dontwarn sun.security.action.GetPropertyAction

# Rules to suppress warnings for missing classes, as suggested by R8
-dontwarn com.sun.source.doctree.DocTree
-dontwarn com.sun.source.doctree.DocTreeVisitor
-dontwarn com.sun.source.doctree.TextTree
-dontwarn com.sun.source.doctree.UnknownBlockTagTree
-dontwarn com.sun.source.doctree.UnknownInlineTagTree
-dontwarn com.sun.source.util.SimpleDocTreeVisitor
-dontwarn javax.lang.model.element.Element
-dontwarn javax.lang.model.element.ElementKind
-dontwarn jdk.javadoc.doclet.Taglet$Location
-dontwarn jdk.javadoc.doclet.Taglet
