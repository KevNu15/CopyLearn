# ---- ProGuard / R8 rules for future OCR (ML Kit) ----

# ML Kit text recognition (keep public APIs & internals used via reflection)
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.internal.mlkit_vision_text_common.** { *; }
-keep class com.google.android.gms.internal.mlkit_vision_common.** { *; }

# (Optional) Coroutines Play Services await()
-dontwarn kotlinx.coroutines.**

# Keep annotations/signatures (often useful for reflection)
-keepattributes *Annotation*
-keepattributes Signature
