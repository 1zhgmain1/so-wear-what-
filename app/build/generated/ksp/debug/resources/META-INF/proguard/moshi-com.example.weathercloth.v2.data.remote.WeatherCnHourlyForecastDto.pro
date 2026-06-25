-if class com.example.weathercloth.v2.data.remote.WeatherCnHourlyForecastDto
-keepnames class com.example.weathercloth.v2.data.remote.WeatherCnHourlyForecastDto
-if class com.example.weathercloth.v2.data.remote.WeatherCnHourlyForecastDto
-keep class com.example.weathercloth.v2.data.remote.WeatherCnHourlyForecastDtoJsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
}
-if class com.example.weathercloth.v2.data.remote.WeatherCnHourlyForecastDto
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-if class com.example.weathercloth.v2.data.remote.WeatherCnHourlyForecastDto
-keepclassmembers class com.example.weathercloth.v2.data.remote.WeatherCnHourlyForecastDto {
    public synthetic <init>(java.lang.String,java.lang.Long,java.lang.String,com.example.weathercloth.v2.data.remote.WeatherCnMetricDto,java.lang.Integer,int,kotlin.jvm.internal.DefaultConstructorMarker);
}
