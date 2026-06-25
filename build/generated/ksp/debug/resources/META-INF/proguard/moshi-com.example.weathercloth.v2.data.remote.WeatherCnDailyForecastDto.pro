-if class com.example.weathercloth.v2.data.remote.WeatherCnDailyForecastDto
-keepnames class com.example.weathercloth.v2.data.remote.WeatherCnDailyForecastDto
-if class com.example.weathercloth.v2.data.remote.WeatherCnDailyForecastDto
-keep class com.example.weathercloth.v2.data.remote.WeatherCnDailyForecastDtoJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.example.weathercloth.v2.data.remote.WeatherCnDailyForecastDto
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-if class com.example.weathercloth.v2.data.remote.WeatherCnDailyForecastDto
-keepclassmembers class com.example.weathercloth.v2.data.remote.WeatherCnDailyForecastDto {
    public synthetic <init>(com.example.weathercloth.v2.data.remote.WeatherCnDailyTemperatureDto,int,kotlin.jvm.internal.DefaultConstructorMarker);
}
