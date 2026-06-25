-if class com.example.weathercloth.v2.data.remote.GeocodeResponse
-keepnames class com.example.weathercloth.v2.data.remote.GeocodeResponse
-if class com.example.weathercloth.v2.data.remote.GeocodeResponse
-keep class com.example.weathercloth.v2.data.remote.GeocodeResponseJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.example.weathercloth.v2.data.remote.GeocodeResponse
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-if class com.example.weathercloth.v2.data.remote.GeocodeResponse
-keepclassmembers class com.example.weathercloth.v2.data.remote.GeocodeResponse {
    public synthetic <init>(java.util.List,int,kotlin.jvm.internal.DefaultConstructorMarker);
}
