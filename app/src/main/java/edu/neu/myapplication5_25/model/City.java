package edu.neu.myapplication5_25.model;

import java.util.ArrayList;
import java.util.List;

public class City {
    private String name;
    private String province;
    private String pinyin;
    private double latitude;
    private double longitude;

    public City(String name, String province, String pinyin, double latitude, double longitude) {
        this.name = name;
        this.province = province;
        this.pinyin = pinyin;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return name + " (" + province + ")";
    }

    // 静态方法：获取中国主要城市列表
    public static List<City> getChinaCities() {
        List<City> cities = new ArrayList<>();
        
        // 直辖市
        cities.add(new City("北京", "北京市", "Beijing", 39.9042, 116.4074));
        cities.add(new City("上海", "上海市", "Shanghai", 31.2304, 121.4737));
        cities.add(new City("天津", "天津市", "Tianjin", 39.3434, 117.3616));
        cities.add(new City("重庆", "重庆市", "Chongqing", 29.4316, 106.9123));
        
        // 省会城市
        cities.add(new City("广州", "广东省", "Guangzhou", 23.1291, 113.2644));
        cities.add(new City("深圳", "广东省", "Shenzhen", 22.3193, 114.1694));
        cities.add(new City("杭州", "浙江省", "Hangzhou", 30.2741, 120.1551));
        cities.add(new City("南京", "江苏省", "Nanjing", 32.0603, 118.7969));
        cities.add(new City("苏州", "江苏省", "Suzhou", 31.2989, 120.5853));
        cities.add(new City("武汉", "湖北省", "Wuhan", 30.5928, 114.3055));
        cities.add(new City("成都", "四川省", "Chengdu", 30.5728, 104.0668));
        cities.add(new City("西安", "陕西省", "Xian", 34.3416, 108.9398));
        cities.add(new City("沈阳", "辽宁省", "Shenyang", 41.8057, 123.4315));
        cities.add(new City("青岛", "山东省", "Qingdao", 36.0986, 120.3719));
        cities.add(new City("大连", "辽宁省", "Dalian", 38.9140, 121.6147));
        cities.add(new City("厦门", "福建省", "Xiamen", 24.4798, 118.0819));
        cities.add(new City("宁波", "浙江省", "Ningbo", 29.8683, 121.5440));
        cities.add(new City("无锡", "江苏省", "Wuxi", 31.4912, 120.3124));
        cities.add(new City("福州", "福建省", "Fuzhou", 26.0745, 119.2965));
        cities.add(new City("济南", "山东省", "Jinan", 36.6512, 117.1201));
        cities.add(new City("长沙", "湖南省", "Changsha", 28.2282, 112.9388));
        cities.add(new City("石家庄", "河北省", "Shijiazhuang", 38.0428, 114.5149));
        cities.add(new City("郑州", "河南省", "Zhengzhou", 34.7466, 113.6254));
        cities.add(new City("长春", "吉林省", "Changchun", 43.8171, 125.3235));
        cities.add(new City("合肥", "安徽省", "Hefei", 31.8206, 117.2272));
        cities.add(new City("昆明", "云南省", "Kunming", 25.0389, 102.7183));
        cities.add(new City("太原", "山西省", "Taiyuan", 37.8570, 112.5489));
        cities.add(new City("南昌", "江西省", "Nanchang", 28.6820, 115.8579));
        cities.add(new City("贵阳", "贵州省", "Guiyang", 26.6470, 106.6302));
        cities.add(new City("南宁", "广西壮族自治区", "Nanning", 22.8170, 108.3669));
        cities.add(new City("海口", "海南省", "Haikou", 20.0458, 110.3417));
        cities.add(new City("呼和浩特", "内蒙古自治区", "Hohhot", 40.8414, 111.7519));
        cities.add(new City("兰州", "甘肃省", "Lanzhou", 36.0611, 103.8343));
        cities.add(new City("西宁", "青海省", "Xining", 36.6171, 101.7782));
        cities.add(new City("银川", "宁夏回族自治区", "Yinchuan", 38.4872, 106.2309));
        cities.add(new City("乌鲁木齐", "新疆维吾尔自治区", "Urumqi", 43.8256, 87.6168));
        cities.add(new City("拉萨", "西藏自治区", "Lhasa", 29.6520, 91.1722));
        cities.add(new City("哈尔滨", "黑龙江省", "Harbin", 45.8038, 126.5349));
        
        // 广东省其他重要城市
        cities.add(new City("佛山", "广东省", "Foshan", 23.0218, 113.1219));
        cities.add(new City("东莞", "广东省", "Dongguan", 23.0489, 113.7447));
        cities.add(new City("中山", "广东省", "Zhongshan", 22.5158, 113.3927));
        cities.add(new City("珠海", "广东省", "Zhuhai", 22.2712, 113.5767));
        cities.add(new City("惠州", "广东省", "Huizhou", 23.0794, 114.4152));
        cities.add(new City("江门", "广东省", "Jiangmen", 22.5751, 113.0946));
        cities.add(new City("汕头", "广东省", "Shantou", 23.3540, 116.6690));
        cities.add(new City("湛江", "广东省", "Zhanjiang", 21.2707, 110.3594));
        cities.add(new City("肇庆", "广东省", "Zhaoqing", 23.0786, 112.4721));
        cities.add(new City("梅州", "广东省", "Meizhou", 24.2886, 116.1177));
        cities.add(new City("韶关", "广东省", "Shaoguan", 24.8138, 113.5987));
        cities.add(new City("河源", "广东省", "Heyuan", 23.7467, 114.6975));
        cities.add(new City("清远", "广东省", "Qingyuan", 23.6819, 113.0306));
        cities.add(new City("云浮", "广东省", "Yunfu", 22.9297, 112.0444));
        cities.add(new City("潮州", "广东省", "Chaozhou", 23.6566, 116.6226));
        cities.add(new City("揭阳", "广东省", "Jieyang", 23.5491, 116.3728));
        cities.add(new City("阳江", "广东省", "Yangjiang", 21.8456, 111.9827));
        cities.add(new City("茂名", "广东省", "Maoming", 21.6583, 110.9255));
        
        // 浙江省重要城市
        cities.add(new City("温州", "浙江省", "Wenzhou", 27.9944, 120.6669));
        cities.add(new City("嘉兴", "浙江省", "Jiaxing", 30.7469, 120.7507));
        cities.add(new City("湖州", "浙江省", "Huzhou", 30.8703, 120.0873));
        cities.add(new City("绍兴", "浙江省", "Shaoxing", 30.0023, 120.5782));
        cities.add(new City("金华", "浙江省", "Jinhua", 29.0895, 119.6491));
        cities.add(new City("衢州", "浙江省", "Quzhou", 28.9709, 118.8686));
        cities.add(new City("舟山", "浙江省", "Zhoushan", 29.9853, 122.2072));
        cities.add(new City("台州", "浙江省", "Taizhou", 28.6569, 121.4206));
        cities.add(new City("丽水", "浙江省", "Lishui", 28.4676, 119.9219));
        
        // 江苏省重要城市
        cities.add(new City("常州", "江苏省", "Changzhou", 31.7728, 119.9462));
        cities.add(new City("徐州", "江苏省", "Xuzhou", 34.2616, 117.1841));
        cities.add(new City("南通", "江苏省", "Nantong", 32.0146, 120.8659));
        cities.add(new City("连云港", "江苏省", "Lianyungang", 34.5969, 119.1789));
        cities.add(new City("淮安", "江苏省", "Huaian", 33.6105, 119.0306));
        cities.add(new City("盐城", "江苏省", "Yancheng", 33.3478, 120.1251));
        cities.add(new City("扬州", "江苏省", "Yangzhou", 32.3932, 119.4215));
        cities.add(new City("镇江", "江苏省", "Zhenjiang", 32.1882, 119.4543));
        cities.add(new City("泰州", "江苏省", "Taizhou", 32.4849, 119.9073));
        cities.add(new City("宿迁", "江苏省", "Suqian", 33.9630, 118.2751));
        
        // 山东省重要城市
        cities.add(new City("淄博", "山东省", "Zibo", 36.8133, 118.0549));
        cities.add(new City("枣庄", "山东省", "Zaozhuang", 34.8107, 117.3238));
        cities.add(new City("东营", "山东省", "Dongying", 37.4345, 118.6746));
        cities.add(new City("烟台", "山东省", "Yantai", 37.4638, 121.4481));
        cities.add(new City("潍坊", "山东省", "Weifang", 36.7067, 119.1019));
        cities.add(new City("济宁", "山东省", "Jining", 35.4154, 116.5870));
        cities.add(new City("泰安", "山东省", "Taian", 36.1943, 117.0881));
        cities.add(new City("威海", "山东省", "Weihai", 37.5128, 122.1201));
        cities.add(new City("日照", "山东省", "Rizhao", 35.4164, 119.5269));
        cities.add(new City("临沂", "山东省", "Linyi", 35.1041, 118.3563));
        cities.add(new City("德州", "山东省", "Dezhou", 37.4341, 116.3594));
        cities.add(new City("聊城", "山东省", "Liaocheng", 36.4571, 115.9856));
        cities.add(new City("滨州", "山东省", "Binzhou", 37.3835, 117.9708));
        cities.add(new City("菏泽", "山东省", "Heze", 35.2332, 115.4697));
        
        // 辽宁省重要城市（特别加强东北地区）
        cities.add(new City("鞍山", "辽宁省", "Anshan", 41.1106, 122.9944));
        cities.add(new City("抚顺", "辽宁省", "Fushun", 41.8654, 123.9574));
        cities.add(new City("本溪", "辽宁省", "Benxi", 41.2944, 123.7657));
        cities.add(new City("丹东", "辽宁省", "Dandong", 40.1290, 124.3541));
        cities.add(new City("锦州", "辽宁省", "Jinzhou", 41.0957, 121.1260));
        cities.add(new City("营口", "辽宁省", "Yingkou", 40.6674, 122.2352));
        cities.add(new City("阜新", "辽宁省", "Fuxin", 42.0118, 121.6708));
        cities.add(new City("辽阳", "辽宁省", "Liaoyang", 41.2694, 123.1815));
        cities.add(new City("盘锦", "辽宁省", "Panjin", 41.1245, 122.0709));
        cities.add(new City("铁岭", "辽宁省", "Tieling", 42.2906, 123.7266));
        cities.add(new City("朝阳", "辽宁省", "Chaoyang", 41.5708, 120.4417));
        cities.add(new City("葫芦岛", "辽宁省", "Huludao", 40.7115, 120.8367));
        
        // 吉林省重要城市
        cities.add(new City("吉林", "吉林省", "Jilin", 43.8436, 126.5561));
        cities.add(new City("四平", "吉林省", "Siping", 43.1703, 124.3507));
        cities.add(new City("辽源", "吉林省", "Liaoyuan", 42.9027, 125.1357));
        cities.add(new City("通化", "吉林省", "Tonghua", 41.7214, 125.9365));
        cities.add(new City("白山", "吉林省", "Baishan", 41.9393, 126.4357));
        cities.add(new City("松原", "吉林省", "Songyuan", 45.1415, 124.8253));
        cities.add(new City("白城", "吉林省", "Baicheng", 45.6196, 122.8397));
        
        // 黑龙江省重要城市
        cities.add(new City("齐齐哈尔", "黑龙江省", "Qiqihar", 47.3543, 123.9180));
        cities.add(new City("鸡西", "黑龙江省", "Jixi", 45.2951, 130.9759));
        cities.add(new City("鹤岗", "黑龙江省", "Hegang", 47.3322, 130.2977));
        cities.add(new City("双鸭山", "黑龙江省", "Shuangyashan", 46.6434, 131.1571));
        cities.add(new City("大庆", "黑龙江省", "Daqing", 46.5896, 125.1031));
        cities.add(new City("伊春", "黑龙江省", "Yichun", 47.7248, 128.8414));
        cities.add(new City("佳木斯", "黑龙江省", "Jiamusi", 46.7996, 130.3184));
        cities.add(new City("七台河", "黑龙江省", "Qitaihe", 45.7719, 131.0154));
        cities.add(new City("牡丹江", "黑龙江省", "Mudanjiang", 44.5517, 129.6330));
        cities.add(new City("黑河", "黑龙江省", "Heihe", 50.2496, 127.4990));
        cities.add(new City("绥化", "黑龙江省", "Suihua", 46.6374, 126.9927));
        
        // 其他重要城市
        cities.add(new City("洛阳", "河南省", "Luoyang", 34.6196, 112.4542));
        cities.add(new City("宜昌", "湖北省", "Yichang", 30.7324, 111.2900));
        cities.add(new City("株洲", "湖南省", "Zhuzhou", 27.8274, 113.1513));
        cities.add(new City("绵阳", "四川省", "Mianyang", 31.4678, 104.6794));
        cities.add(new City("三亚", "海南省", "Sanya", 18.2479, 109.5146));
        cities.add(new City("泉州", "福建省", "Quanzhou", 24.8744, 118.6751));
        cities.add(new City("包头", "内蒙古自治区", "Baotou", 40.6576, 109.8403));
        cities.add(new City("唐山", "河北省", "Tangshan", 39.6243, 118.1944));
        cities.add(new City("秦皇岛", "河北省", "Qinhuangdao", 39.9312, 119.6000));
        cities.add(new City("邯郸", "河北省", "Handan", 36.6253, 114.4897));
        cities.add(new City("保定", "河北省", "Baoding", 38.8671, 115.4648));
        cities.add(new City("张家口", "河北省", "Zhangjiakou", 40.7679, 114.8751));
        cities.add(new City("承德", "河北省", "Chengde", 40.9543, 117.9385));
        cities.add(new City("廊坊", "河北省", "Langfang", 39.5196, 116.7038));
        cities.add(new City("沧州", "河北省", "Cangzhou", 38.3037, 116.8286));
        cities.add(new City("衡水", "河北省", "Hengshui", 37.7161, 115.6659));
        
        return cities;
    }

    // 根据名称搜索城市
    public static List<City> searchCities(String keyword) {
        List<City> allCities = getChinaCities();
        List<City> filteredCities = new ArrayList<>();
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return allCities;
        }
        
        String lowerKeyword = keyword.toLowerCase();
        for (City city : allCities) {
            if (city.getName().toLowerCase().contains(lowerKeyword) ||
                city.getProvince().toLowerCase().contains(lowerKeyword) ||
                city.getPinyin().toLowerCase().contains(lowerKeyword)) {
                filteredCities.add(city);
            }
        }
        
        return filteredCities;
    }
} 