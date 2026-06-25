-if class com.example.weathercloth.v2.data.remote.CurrentWeatherDto
-keepnames class com.example.weathercloth.v2.data.remote.CurrentWeatherDto
-if class com.example.weathercloth.v2.data.remote.CurrentWeatherDto
-keep class com.example.weathercloth.v2.data.remote.CurrentWeatherDtoJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.example.weathercloth.v2.data.remote.CurrentWeatherDto
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-if class com.example.weathercloth.v2.data.remote.CurrentWeatherDto
-keepclassmembers class com.example.weathercloth.v2.data.remote.CurrentWeatherDto {
    public synthetic <init>(java.lang.String,double,double,int,java.lang.Double,java.lang.Integer,double,java.lang.Double,java.lang.Double,int,int,kotlin.jvm.internal.DefaultConstructorMarker);
}
