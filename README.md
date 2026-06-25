# 今天穿什么 — Android 天气穿搭应用 v2

基于 Kotlin + Jetpack Compose + MVVM 的原生 Android 项目。根据城市天气、当前时间、用户偏好和衣橱数据智能生成穿搭建议，支持多城市、每日提醒、定位、衣橱颜色管理和免费图片识别。

## 天气数据来源

- **Open-Meteo**：免费天气 API（预报、城市搜索、空气质量）
- **华风爱科 WeatherCn**：补充数据源（更精准的实况和逐小时预报），需配置 API Key

## API Key 配置

在 `local.properties` 中配置 WeatherCn API Key：

```properties
WEATHERCN_API_KEY=your_api_key
```

不配置不影响正常使用，应用会回退到 Open-Meteo。

## Android 权限

```xml
INTERNET / ACCESS_FINE_LOCATION / ACCESS_COARSE_LOCATION
CAMERA / POST_NOTIFICATIONS / ACCESS_NETWORK_STATE / SCHEDULE_EXACT_ALARM
```

## 衣橱与拍照识别

- 衣物支持可选颜色、状态管理（可穿/洗涤中/待修补/不想穿/已借出）和数量追踪
- 拍照后使用 ML Kit Image Labeling 免费识别衣物标签，自动填充名称、分类和颜色

## 如何运行

1. 用 Android Studio 打开 `new model/` 目录
2. 安装 Android SDK 35
3. 复制 `local.properties.example` → `local.properties`，确认 `sdk.dir`
4. Gradle Sync → Run

## 项目结构

```
app/src/main/java/com/example/weathercloth/v2
├── MainActivity.kt             入口 Activity
├── WeatherClothApp.kt          Application + DI
├── data/
│   ├── local/                  Room 实体、DAO、数据库
│   ├── remote/                 Retrofit 天气 API DTO
│   └── repository/             数据聚合、默认数据、错误处理
├── domain/                     天气快照、建议模型、穿搭规则引擎
├── location/                   FusedLocationProvider 封装
├── notification/               通知渠道、WorkManager 每日提醒
└── ui/                         ViewModel、Compose 页面、主题
```

## 主要逻辑

- **WeatherRepository**：聚合定位、城市、天气、空气质量、用户偏好和衣橱数据
- **OutfitAdvisor**：根据温度、体感温度、湿度、风速、降雨概率、UV、空气质量、未来12小时趋势、昼夜温差生成建议
- **ReminderScheduler**：使用 AlarmManager 创建每日提醒任务
