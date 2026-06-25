-if class com.example.weathercloth.v2.data.remote.WeatherCnDailyForecastResponse
-keepnames class com.example.weathercloth.v2.data.remote.WeatherCnDailyForecastResponse
-if class com.example.weathercloth.v2.data.remote.WeatherCnDailyForecastResponse
-keep class com.example.weathercloth.v2.data.remote.WeatherCnDailyForecastResponseJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.example.weathercloth.v2.data.remote.WeatherCnDailyForecastResponse
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-if class com.example.weathercloth.v2.data.remote.WeatherCnDailyForecastResponse
-keepclassmembers class com.example.weathercloth.v2.data.remote.WeatherCnDailyForecastResponse {
    public synthetic <init>(java.util.List,int,kotlin.jvm.internal.DefaultConstructorMarker);
}
