package com.example.weathercloth.v2.domain

import com.example.weathercloth.v2.data.local.WardrobeItemEntity
import kotlin.math.roundToInt

// 子分类→大类映射，兼容旧数据
private val SUB_TO_BROAD: Map<String, String> = mapOf(
    "T恤" to "上衣", "衬衫" to "上衣", "Polo衫" to "上衣", "卫衣" to "上衣", "毛衣" to "上衣", "针织衫" to "上衣", "长袖打底" to "上衣", "背心" to "上衣", "吊带" to "上衣", "短袖" to "上衣",
    "牛仔裤" to "下装", "休闲裤" to "下装", "西裤" to "下装", "运动裤" to "下装", "短裤" to "下装", "半身裙" to "下装", "阔腿裤" to "下装", "工装裤" to "下装", "璑伽裤" to "下装",
    "运动鞋" to "鞋子", "帆布鞋" to "鞋子", "皮鞋" to "鞋子", "靴子" to "鞋子", "凉鞋" to "鞋子", "拖鞋" to "鞋子", "板鞋" to "鞋子", "乐福鞋" to "鞋子", "高跟鞋" to "鞋子",
    "夹克" to "外套", "风衣" to "外套", "羽绒服" to "外套", "棉服" to "外套", "大衣" to "外套", "西装" to "外套", "牛仔外套" to "外套", "冲锋衣" to "外套", "针织开衫" to "外套", "马甲" to "外套",
    "棒球帽" to "帽子", "渔夫帽" to "帽子", "毛线帽" to "帽子", "贝雷帽" to "帽子", "遮阳帽" to "帽子",
    "雨伞" to "雨具", "雨衣" to "雨具",
    "围巾" to "配饰", "手套" to "配饰", "腰带" to "配饰", "墨镜" to "配饰", "手表" to "配饰",
    "泳衣" to "其他", "睡衣" to "其他", "家居服" to "其他", "袜子" to "其他", "内衣" to "其他"
)

private fun broadCategory(category: String): String = SUB_TO_BROAD[category] ?: category

class OutfitAdvisor {
    fun build(input: OutfitInput): OutfitAdvice {
        val w = input.weather
        val p = input.preference
        fun availableQuantity(item: WardrobeItemEntity): Int {
            return if (item.status == "可穿") item.quantity else (item.quantity - item.statusQuantity).coerceAtLeast(0)
        }

        val wearableWardrobe = input.wardrobe.filter { availableQuantity(it) > 0 }
        val climate = climateProfile(w)
        val adjustedFeels = w.feelsLike - p.thermalSensitivity * 1.5 + climate.warmBias
        val rainRisk = w.rainProbability >= 45 || w.condition.contains("雨")
        val windy = w.windSpeed >= 8
        val polluted = w.airQualityIndex >= 4
        val strongSun = w.uvIndex >= 6
        val bigGap = w.dayNightGap >= 9

        fun wardrobeLabel(item: WardrobeItemEntity): String {
            val base = listOfNotNull(item.color, item.name).joinToString(" ")
            val available = availableQuantity(item)
            return if (available > 1) "$base x$available" else base
        }

        fun targetWarmth(category: String): Int {
            return when {
                adjustedFeels < 5 -> if (category == "外套") 5 else 4
                adjustedFeels < 14 -> if (category == "外套") 4 else 3
                adjustedFeels < climate.shortSleeveThreshold -> if (climate.prefersLightLayer && category == "上衣") 1 else 2
                else -> 1
            }
        }

        fun candidates(category: String): List<WardrobeItemEntity> {
            val target = targetWarmth(category)
            return wearableWardrobe
                .filter { it.category == category }
                .sortedWith(
                    compareByDescending<WardrobeItemEntity> { it.style == p.style }
                        .thenBy { kotlin.math.abs(it.warmth - target) }
                        .thenByDescending { it.warmth }
                )
        }

        fun fromWardrobe(category: String, fallback: String): String {
            return candidates(category).firstOrNull()?.let(::wardrobeLabel) ?: fallback
        }

        fun alternatives(category: String, fallback: List<String>): List<String> {
            val options = candidates(category).drop(1).take(3).map(::wardrobeLabel)
            return if (options.isNotEmpty()) options else fallback
        }

        fun unavailableNote(category: String): String? {
            val unavailable = input.wardrobe.filter { broadCategory(it.category) == category && it.status != "可穿" && it.statusQuantity > 0 }
            if (unavailable.isEmpty()) return null
            return unavailable.joinToString("，") { "${it.name}（${it.status} ${it.statusQuantity}/${it.quantity}）" }
        }

        val top = when {
            adjustedFeels < 5 -> fromWardrobe("上衣", "保暖内搭 + 厚毛衣")
            adjustedFeels < 14 -> fromWardrobe("上衣", "长袖针织衫或卫衣")
            adjustedFeels < climate.shortSleeveThreshold -> fromWardrobe("上衣", "透气长袖或薄衬衫")
            else -> fromWardrobe("上衣", "短袖或轻薄速干上衣")
        }
        val bottom = when {
            adjustedFeels < 10 -> fromWardrobe("下装", "加绒长裤")
            p.scene == "运动" -> fromWardrobe("下装", "弹力运动裤")
            adjustedFeels > 27 -> fromWardrobe("下装", "轻薄长裤或短裤")
            else -> fromWardrobe("下装", "常规长裤")
        }
        val shoes = when {
            rainRisk -> fromWardrobe("鞋子", "防滑防水鞋")
            p.scene == "运动" -> fromWardrobe("鞋子", "缓震运动鞋")
            else -> fromWardrobe("鞋子", "舒适通勤鞋")
        }
        val coat = adjustedFeels < 17 || bigGap || windy
        val hat = p.likesHat || strongSun || adjustedFeels < 8 || windy
        val sunscreen = strongSun || (w.uvIndex >= 3 && p.scene in listOf("运动", "旅行", "约会"))

        val risks = buildList {
            if (w.temperature >= 35) add("高温风险：减少户外暴晒并补水")
            if (w.temperature <= 0) add("低温风险：注意手脚和颈部保暖")
            if (strongSun) add("暴晒风险：紫外线指数 ${w.uvIndex.roundToInt()}，建议防晒")
            if (rainRisk) add("降雨风险：未来降雨概率 ${w.rainProbability}%")
            if (windy) add("强风风险：风速 ${w.windSpeed} m/s")
            if (polluted) add("空气污染：AQI 等级 ${w.airQualityIndex}，减少剧烈户外活动")
        }

        val items = listOf(
            AdviceItem(
                "上半身",
                top,
                listOfNotNull("体感约 ${w.feelsLike.roundToInt()}°C，${climate.description}，结合 ${p.scene} 场景和 ${p.style} 风格选择上衣。", unavailableNote("上衣")?.let { "不可用衣物：$it。" }).joinToString(" "),
                alternatives("上衣", if (climate.prefersLightLayer) listOf("短袖 + 轻薄防雨外层", "换成保暖值 1 的速干短袖") else listOf("换成相近厚度的上衣", "用内搭叠穿补足温度"))
            ),
            AdviceItem(
                "下半身",
                bottom,
                listOfNotNull("昼夜温差约 ${w.dayNightGap.roundToInt()}°C，湿度 ${w.humidity}%；下装以活动便利和温差适应为主。", unavailableNote("下装")?.let { "不可用衣物：$it。" }).joinToString(" "),
                alternatives("下装", listOf("换成常规长裤", "运动场景可改穿弹力裤"))
            ),
            AdviceItem(
                "鞋子",
                shoes,
                listOfNotNull(if (rainRisk) "未来 6 到 12 小时有降雨风险，鞋底防滑和鞋面防水更稳妥。" else "当前降雨概率较低，优先选择适合${p.scene}的舒适鞋。", unavailableNote("鞋子")?.let { "不可用衣物：$it。" }).joinToString(" "),
                alternatives("鞋子", if (rainRisk) listOf("选择鞋底防滑的替代鞋", "短途可带鞋套") else listOf("选择另一双舒适通勤鞋", "运动场景改穿缓震鞋"))
            ),
            AdviceItem(
                "帽子",
                if (hat) "建议戴帽子" else "可以不戴帽子",
                listOfNotNull("风速、紫外线、个人喜好共同决定；当前风速 ${w.windSpeed} m/s，UV ${w.uvIndex.roundToInt()}。", unavailableNote("帽子")?.let { "不可用衣物：$it。" }).joinToString(" "),
                alternatives("帽子", listOf("用伞或墨镜替代遮阳", "风大时选择更贴合的帽子"))
            ),
            AdviceItem(
                "外套",
                if (coat) fromWardrobe("外套", "建议带外套") else "不必带厚外套",
                listOfNotNull("体感温度、强风和昼夜温差会影响实际冷暖；${w.trend}。", unavailableNote("外套")?.let { "不可用衣物：$it。" }).joinToString(" "),
                alternatives("外套", listOf("用薄外套或开衫替代", "早晚冷时增加内搭"))
            ),
            AdviceItem(
                "雨具",
                if (rainRisk) fromWardrobe("雨具", "带雨伞或轻便雨衣") else "一般不用带雨具",
                listOfNotNull("降雨概率为 ${w.rainProbability}%，天气描述：${w.condition}。", unavailableNote("雨具")?.let { "不可用衣物：$it。" }).joinToString(" "),
                alternatives("雨具", listOf("没有雨伞时带轻便雨衣", "短途可选择防水外套"))
            ),
            AdviceItem("防晒", if (sunscreen) "使用防晒霜墨镜" else "常规通勤可简化防晒", "紫外线指数 ${w.uvIndex.roundToInt()}，户外时间越长越需要加强防晒。")
        )

        val summary = when {
            climate.name.contains("青藏") -> "今天按高海拔分层穿：白天防晒透气，早晚注意加外套。"
            climate.name.contains("东北") && adjustedFeels < 18 -> "今天按东北风寒体感处理，保暖层和抗风外套更重要。"
            climate.name.contains("华北") && bigGap -> "今天早晚温差明显，内搭轻便，外层方便加减。"
            rainRisk && coat -> "今天重点是防雨和抗风，外套与雨具别漏。"
            strongSun -> "今天日照强，清爽透气和防晒优先。"
            adjustedFeels < 12 -> "今天偏冷，采用分层保暖更舒服。"
            adjustedFeels > 28 -> "今天偏热，轻薄透气是主线。"
            climate.prefersLightLayer -> "今天在${climate.name}体感偏闷，优先轻薄透气，雨天用防雨层而不是加厚。"
            else -> "今天温度适中，按${p.scene}场景选择轻便搭配即可。"
        }
        return OutfitAdvice(summary, items, risks)
    }

    private data class ClimateProfile(
        val name: String,
        val warmBias: Double,
        val shortSleeveThreshold: Double,
        val prefersLightLayer: Boolean,
        val description: String
    )

    private fun climateProfile(w: WeatherSnapshot): ClimateProfile {
        val city = w.cityName
        val southCoastalCity = listOf("广州", "深圳", "佛山", "东莞", "珠海", "中山", "惠州", "汕头", "湛江", "南宁", "北海", "海口", "三亚", "香港", "澳门")
            .any { city.contains(it) }
        val jiangnanCity = listOf("上海", "杭州", "南京", "苏州", "无锡", "宁波", "合肥", "南昌", "长沙", "武汉", "福州", "厦门", "泉州")
            .any { city.contains(it) }
        val southwestCity = listOf("成都", "重庆", "贵阳", "昆明", "绵阳", "乐山", "遵义")
            .any { city.contains(it) }
        val northCity = listOf("北京", "天津", "石家庄", "太原", "济南", "郑州", "青岛", "西安", "兰州", "银川", "乌鲁木齐", "呼和浩特")
            .any { city.contains(it) }
        val northeastCity = listOf("哈尔滨", "长春", "沈阳", "大连", "齐齐哈尔", "吉林")
            .any { city.contains(it) }
        val plateauCity = listOf("拉萨", "西宁", "格尔木", "日喀则", "林芝", "香格里拉")
            .any { city.contains(it) }
        val hotHumid = southCoastalCity || (w.latitude < 25.5 && w.humidity >= 65)
        val humidSouth = jiangnanCity || (w.latitude < 31.5 && w.humidity >= 70)
        return when {
            plateauCity || (w.latitude > 27 && w.latitude < 37 && w.longitude < 103 && w.dayNightGap >= 10) -> ClimateProfile(
                name = "青藏高原/高海拔地区",
                warmBias = -2.0,
                shortSleeveThreshold = 25.0,
                prefersLightLayer = false,
                description = "高海拔昼夜温差和风感明显，白天晒、早晚冷，需要分层"
            )
            northeastCity || w.latitude >= 41.5 -> ClimateProfile(
                name = "东北寒冷地区",
                warmBias = -2.0,
                shortSleeveThreshold = 25.0,
                prefersLightLayer = false,
                description = "东北体感更受冷空气和风影响，保暖阈值需要上调"
            )
            hotHumid -> ClimateProfile(
                name = "华南湿热地区",
                warmBias = 3.0,
                shortSleeveThreshold = 21.0,
                prefersLightLayer = true,
                description = "当地湿热体感会放大闷热感"
            )
            humidSouth -> ClimateProfile(
                name = "江南/华东湿冷湿热地区",
                warmBias = 1.5,
                shortSleeveThreshold = 22.5,
                prefersLightLayer = w.temperature >= 20,
                description = "湿度较高，春夏偏闷、秋冬湿冷，穿搭要兼顾透气和防潮"
            )
            southwestCity -> ClimateProfile(
                name = "西南盆地/高湿地区",
                warmBias = if (w.humidity >= 75) 1.0 else 0.0,
                shortSleeveThreshold = 23.0,
                prefersLightLayer = w.temperature >= 21 && w.humidity >= 70,
                description = "西南湿度和阴雨会影响体感，适合轻薄分层"
            )
            northCity || (w.latitude > 34 && w.humidity < 55) -> ClimateProfile(
                name = "华北/西北干燥温差地区",
                warmBias = -1.0,
                shortSleeveThreshold = 24.0,
                prefersLightLayer = false,
                description = "空气偏干、早晚温差更明显，建议保留一层可加减外搭"
            )
            else -> ClimateProfile(
                name = "中国常规地区",
                warmBias = 0.0,
                shortSleeveThreshold = 24.0,
                prefersLightLayer = false,
                description = "按当前体感温度和场景综合判断"
            )
        }
    }
}
