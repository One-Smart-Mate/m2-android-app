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
# Gson uses reflection
-keep class com.ih.osm.domain.model.** { *; }
-keep class com.ih.osm.data.model.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keepattributes Signature
-keepattributes *Annotation*
# Keep Room entities and DAOs
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.RoomOpenHelper
-keep class * extends androidx.room.EntityInsertionAdapter
-keep class * extends androidx.room.SharedSQLiteStatement
-keep class * extends androidx.room.EntityDeletionOrUpdateAdapter
-keep class * extends androidx.room.util.TableInfo
-keep class * extends androidx.room.util.TableInfo$Column
-keep class * extends androidx.room.util.TableInfo$ForeignKey
-keep class * extends androidx.room.util.TableInfo$Index

# Keep your own entities and DAOs
-keep class com.ih.osm.data.database.** { *; }
-keep class com.ih.osm.data.database.dao.** { *; }
-keep class com.ih.osm.data.database.entities.** { *; }

# Keep Room annotations
-keepattributes *Annotation*