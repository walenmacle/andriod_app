package edu.neu.myapplication5_25.util;

import java.util.HashMap;
import java.util.Map;

public class CityNameMapper {
    private static final Map<String, String> CITY_MAP = new HashMap<>();
    
    static {
        // 中国主要城市映射
        CITY_MAP.put("北京", "Beijing");
        CITY_MAP.put("上海", "Shanghai");
        CITY_MAP.put("广州", "Guangzhou");
        CITY_MAP.put("深圳", "Shenzhen");
        CITY_MAP.put("杭州", "Hangzhou");
        CITY_MAP.put("南京", "Nanjing");
        CITY_MAP.put("武汉", "Wuhan");
        CITY_MAP.put("成都", "Chengdu");
        CITY_MAP.put("重庆", "Chongqing");
        CITY_MAP.put("西安", "Xi'an");
        CITY_MAP.put("天津", "Tianjin");
        CITY_MAP.put("青岛", "Qingdao");
        CITY_MAP.put("大连", "Dalian");
        CITY_MAP.put("厦门", "Xiamen");
        CITY_MAP.put("苏州", "Suzhou");
        CITY_MAP.put("无锡", "Wuxi");
        CITY_MAP.put("宁波", "Ningbo");
        CITY_MAP.put("郑州", "Zhengzhou");
        CITY_MAP.put("长沙", "Changsha");
        CITY_MAP.put("哈尔滨", "Harbin");
        CITY_MAP.put("沈阳", "Shenyang");
        CITY_MAP.put("长春", "Changchun");
        CITY_MAP.put("石家庄", "Shijiazhuang");
        CITY_MAP.put("太原", "Taiyuan");
        CITY_MAP.put("济南", "Jinan");
        CITY_MAP.put("南昌", "Nanchang");
        CITY_MAP.put("福州", "Fuzhou");
        CITY_MAP.put("合肥", "Hefei");
        CITY_MAP.put("昆明", "Kunming");
        CITY_MAP.put("南宁", "Nanning");
        CITY_MAP.put("海口", "Haikou");
        CITY_MAP.put("三亚", "Sanya");
        CITY_MAP.put("拉萨", "Lhasa");
        CITY_MAP.put("银川", "Yinchuan");
        CITY_MAP.put("西宁", "Xining");
        CITY_MAP.put("乌鲁木齐", "Urumqi");
        
        // 国际城市
        CITY_MAP.put("纽约", "New York");
        CITY_MAP.put("伦敦", "London");
        CITY_MAP.put("巴黎", "Paris");
        CITY_MAP.put("东京", "Tokyo");
        CITY_MAP.put("首尔", "Seoul");
        CITY_MAP.put("新加坡", "Singapore");
        CITY_MAP.put("悉尼", "Sydney");
        CITY_MAP.put("多伦多", "Toronto");
        CITY_MAP.put("洛杉矶", "Los Angeles");
        CITY_MAP.put("旧金山", "San Francisco");
        CITY_MAP.put("芝加哥", "Chicago");
        CITY_MAP.put("华盛顿", "Washington");
        CITY_MAP.put("迈阿密", "Miami");
        CITY_MAP.put("拉斯维加斯", "Las Vegas");
        CITY_MAP.put("温哥华", "Vancouver");
        CITY_MAP.put("墨尔本", "Melbourne");
        CITY_MAP.put("曼谷", "Bangkok");
        CITY_MAP.put("吉隆坡", "Kuala Lumpur");
        CITY_MAP.put("雅加达", "Jakarta");
        CITY_MAP.put("马尼拉", "Manila");
        CITY_MAP.put("孟买", "Mumbai");
        CITY_MAP.put("新德里", "New Delhi");
        CITY_MAP.put("迪拜", "Dubai");
        CITY_MAP.put("开罗", "Cairo");
        CITY_MAP.put("莫斯科", "Moscow");
        CITY_MAP.put("圣彼得堡", "Saint Petersburg");
        CITY_MAP.put("柏林", "Berlin");
        CITY_MAP.put("法兰克福", "Frankfurt");
        CITY_MAP.put("慕尼黑", "Munich");
        CITY_MAP.put("米兰", "Milan");
        CITY_MAP.put("罗马", "Rome");
        CITY_MAP.put("马德里", "Madrid");
        CITY_MAP.put("巴塞罗那", "Barcelona");
        CITY_MAP.put("阿姆斯特丹", "Amsterdam");
        CITY_MAP.put("布鲁塞尔", "Brussels");
        CITY_MAP.put("苏黎世", "Zurich");
        CITY_MAP.put("维也纳", "Vienna");
        CITY_MAP.put("布拉格", "Prague");
        CITY_MAP.put("华沙", "Warsaw");
        CITY_MAP.put("斯德哥尔摩", "Stockholm");
        CITY_MAP.put("哥本哈根", "Copenhagen");
        CITY_MAP.put("赫尔辛基", "Helsinki");
        CITY_MAP.put("奥斯陆", "Oslo");
    }
    
    /**
     * 将中文城市名转换为英文城市名
     * @param chineseName 中文城市名
     * @return 英文城市名，如果没有找到映射则返回原名称
     */
    public static String toEnglish(String chineseName) {
        if (chineseName == null || chineseName.trim().isEmpty()) {
            return "Shenyang"; // 默认返回沈阳
        }
        
        String englishName = CITY_MAP.get(chineseName.trim());
        return englishName != null ? englishName : chineseName;
    }
    
    /**
     * 检查是否支持该城市
     * @param cityName 城市名（中文或英文）
     * @return 是否支持
     */
    public static boolean isSupported(String cityName) {
        if (cityName == null || cityName.trim().isEmpty()) {
            return false;
        }
        
        // 检查是否在中文映射中
        if (CITY_MAP.containsKey(cityName.trim())) {
            return true;
        }
        
        // 检查是否在英文值中
        for (String englishName : CITY_MAP.values()) {
            if (englishName.equalsIgnoreCase(cityName.trim())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 获取所有支持的中文城市名
     * @return 中文城市名数组
     */
    public static String[] getSupportedChineseCities() {
        return CITY_MAP.keySet().toArray(new String[0]);
    }
} 