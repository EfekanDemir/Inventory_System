# ── Retrofit 2 ──────────────────────────────────────────────────────────
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature, InnerClasses, EnclosingMethod

# ── Gson ──────────────────────────────────────────────────────────────
-keep class com.google.gson.** { *; }
-keep class com.envanter.android.model.** { *; }
-keep class com.envanter.android.api.** { *; }

# ── OkHttp 3 / 4 ──────────────────────────────────────────────────────
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# ── Android X / Material ─────────────────────────────────────────────
-keep class androidx.appcompat.** { *; }
-keep class com.google.android.material.** { *; }

# ── Custom Views ─────────────────────────────────────────────────────
-keep class com.envanter.mobile.view.** { *; }
-keepclassmembers class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}
