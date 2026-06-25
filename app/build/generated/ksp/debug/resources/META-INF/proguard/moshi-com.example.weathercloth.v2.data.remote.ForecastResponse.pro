-if class com.example.weathercloth.v2.data.remote.ForecastResponse
-keepnames class com.example.weathercloth.v2.data.remote.ForecastResponse
-if class com.example.weathercloth.v2.data.remote.ForecastResponse
-keep class com.example.weathercloth.v2.data.remote.ForecastResponseJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.example.weathercloth.v2.data.remote.ForecastResponse
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-if class com.example.weathercloth.v2.data.remote.ForecastResponse
-keepclassmembers class com.example.weathercloth.v2.data.remote.ForecastResponse {
    public synthetic <init>(java.lang.String,com.example.weathercloth.v2.data.remote.CurrentWeatherDto,com.example.weathercloth.v2.data.remote.HourlyWeatherDto,com.example.weathercloth.v2.data.remote.DailyWeatherDto,int,kotlin.jvm.internal.DefaultConstructorMarker);
}
