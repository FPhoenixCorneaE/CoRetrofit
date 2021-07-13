#okhttp3 start ----------------------------------------------------------------------------
-keep class okhttp3.internal.*{*;}
-dontwarn okhttp3.**
-dontwarn okio.**
#okhttp3 end ----------------------------------------------------------------------------

#retrofit2 start ----------------------------------------------------------------------------
-keep class retrofit2.* {*;}
-keepattributes Signature
-keepattributes Exceptions
-dontwarn retrofit2.**
#retrofit2 end ----------------------------------------------------------------------------
