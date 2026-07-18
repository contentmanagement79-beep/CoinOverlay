-keepattributes Signature
-keepattributes *Annotation*
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class com.coinoverlay.data.model.** { *; }