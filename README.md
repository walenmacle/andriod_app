# 天气预报应用

一个功能完整的Android天气预报应用，集成了天气查询、音乐播放、日记管理和个人设置功能。

## 功能特色

### 🌤️ 天气功能
- 实时天气查询
- 多日天气预报
- 逐时天气展示
- 城市切换功能
- 自动定位获取当地天气
- 详细天气信息展示

### 🎵 音乐播放
- 内置三首音乐循环播放
- 完整的播放控制（播放、暂停、上一首、下一首）
- 进度条和时间显示
- 播放列表管理
- 后台播放服务

### 📔 日记功能
- 日记增删改查
- 心情记录（开心、难过、平静、兴奋、生气）
- 天气状态记录
- 搜索和排序功能
- 统计信息展示
- 日程提醒和通知

### ⚙️ 个人设置
- 个人信息管理
- 主题切换（日间/夜间模式）
- 通知设置
- 帮助和关于信息

## 技术栈

- **开发语言**: Java 1.8
- **构建工具**: Gradle
- **最低版本**: Android 7.0 (API 24)
- **目标版本**: API 33
- **网络请求**: OkHttp + JSON解析
- **数据存储**: SQLite
- **UI框架**: Material Design
- **架构模式**: Fragment + 多线程 + Service

## 详细模块功能实现

### 🏗️ 核心架构模块

#### MainActivity.java (19KB)
**功能**: 应用主入口和导航控制中心
- 管理底部导航栏和Fragment切换
- 处理权限请求（位置、存储、通知）
- 协调各个模块间的通信
- 管理应用生命周期

### 🖼️ 界面层 (Fragment模块)

#### WeatherFragment.java (24KB)
**功能**: 天气主页面显示
- 显示当前天气基本信息
- 实时温度、湿度、风速等数据展示
- 天气图标和背景动态切换
- 下拉刷新天气数据
- 城市切换入口

#### ForecastFragment.java (11KB)
**功能**: 天气预报页面
- 7日天气预报列表展示
- 24小时逐时天气预报
- 支持列表滚动查看
- 天气趋势图表显示

#### WeatherDetailFragment.java (15KB)
**功能**: 天气详情页面
- 点击天气列表项查看详细信息
- 图文并茂的天气状况说明
- 更多气象参数显示
- 天气预警信息

#### CitySelectionFragment.java (5.4KB)
**功能**: 城市选择管理
- 城市搜索和选择
- 常用城市管理
- GPS定位获取当前城市
- 城市天气快速切换

#### MusicFragment.java (8.2KB)
**功能**: 音乐播放器界面
- 音乐播放控制界面
- 播放进度条和时间显示
- 播放列表展示
- 播放模式切换

#### DiaryFragment.java (34KB)
**功能**: 日记管理系统
- 日记的增删改查操作
- 按日期、心情、天气筛选
- 日记搜索功能
- 统计信息展示
- 日程提醒设置

#### SettingsFragment.java (11KB)
**功能**: 个人设置中心
- 主题切换（日间/夜间）
- 个人信息编辑
- 通知开关设置
- 应用信息和帮助

### 🔧 适配器层 (Adapter模块)

#### DailyWeatherAdapter.java (6.4KB)
**功能**: 多日天气列表适配器
- 7日天气预报数据绑定
- 天气图标和温度显示
- 列表项点击事件处理
- 自定义ViewHolder优化

#### HourlyWeatherAdapter.java (5.0KB)
**功能**: 逐时天气列表适配器
- 24小时天气数据展示
- 温度曲线图绘制
- 时间轴显示
- 滑动性能优化

#### CityAdapter.java (2.3KB)
**功能**: 城市列表适配器
- 城市搜索结果展示
- 城市选择状态管理
- 定位城市标识

#### MusicAdapter.java (4.1KB)
**功能**: 音乐列表适配器
- 播放列表展示
- 当前播放状态指示
- 播放时长显示
- 音乐封面加载

#### DiaryAdapter.java (5.7KB)
**功能**: 日记列表适配器
- 日记条目展示
- 心情图标显示
- 天气状态标识
- 分页加载支持

### 📊 数据模型层 (Model模块)

#### WeatherData.java (3.3KB)
**功能**: 当前天气数据模型
- 温度、湿度、风速等基本信息
- 天气状况和图标代码
- JSON解析和数据封装

#### DailyWeather.java (3.7KB)
**功能**: 多日天气数据模型
- 日期和星期信息
- 最高最低温度
- 天气描述和图标

#### HourlyWeather.java (3.6KB)
**功能**: 逐时天气数据模型
- 小时级天气数据
- 温度变化趋势
- 降水概率

#### City.java (14KB)
**功能**: 城市信息模型
- 城市名称和编码
- 地理位置坐标
- 时区信息
- 完整的中国城市数据库

#### MusicItem.java (1.6KB)
**功能**: 音乐项目模型
- 音乐文件信息
- 播放时长
- 艺术家和专辑信息

#### DiaryEntry.java (1.8KB)
**功能**: 日记条目模型
- 日记内容和标题
- 创建和修改时间
- 心情和天气状态

#### ScheduleEntry.java (6.9KB)
**功能**: 日程提醒模型
- 提醒时间和内容
- 重复规则设置
- 通知状态管理

### 🌐 服务层 (Service模块)

#### WeatherService.java (24KB)
**功能**: 天气数据服务
- 和风天气API接口调用
- HTTP请求和JSON解析
- 数据缓存机制
- 网络异常处理
- 多线程数据获取

#### MusicService.java (6.9KB)
**功能**: 音乐播放服务
- 后台音乐播放管理
- MediaPlayer控制
- 播放状态广播
- 播放列表管理

#### NotificationService.java (13KB)
**功能**: 通知服务
- 日程提醒通知
- 天气预警通知
- 通知渠道管理
- 定时任务调度

### 🗄️ 数据存储层 (Database模块)

#### WeatherDatabaseHelper.java (30KB)
**功能**: SQLite数据库管理
- 数据库创建和版本管理
- 天气数据缓存表
- 城市信息表
- 日记数据表
- 用户设置表
- CRUD操作封装
- 数据迁移处理

### 🛠️ 工具层 (Util模块)

#### NetworkUtils.java (7.4KB)
**功能**: 网络工具类
- HTTP请求封装
- 网络状态检测
- 请求重试机制
- 响应数据解析

#### CityNameMapper.java (5.3KB)
**功能**: 城市名称映射工具
- 中英文城市名转换
- 城市代码映射
- 地理位置解析

#### ThemeManager.java (3.9KB)
**功能**: 主题管理工具
- 日间/夜间主题切换
- 主题配置保存
- 动态主题应用

## 项目结构

```
app/
├── src/main/
│   ├── java/edu/neu/myapplication5_25/
│   │   ├── MainActivity.java              # 主活动，应用入口
│   │   ├── adapter/                       # 适配器层
│   │   │   ├── DailyWeatherAdapter.java   # 多日天气列表适配器
│   │   │   ├── HourlyWeatherAdapter.java  # 逐时天气列表适配器
│   │   │   ├── CityAdapter.java           # 城市列表适配器
│   │   │   ├── MusicAdapter.java          # 音乐列表适配器
│   │   │   └── DiaryAdapter.java          # 日记列表适配器
│   │   ├── fragment/                      # 界面层
│   │   │   ├── WeatherFragment.java       # 天气主页面
│   │   │   ├── ForecastFragment.java      # 天气预报页面
│   │   │   ├── WeatherDetailFragment.java # 天气详情页面
│   │   │   ├── CitySelectionFragment.java # 城市选择页面
│   │   │   ├── MusicFragment.java         # 音乐播放页面
│   │   │   ├── DiaryFragment.java         # 日记管理页面
│   │   │   └── SettingsFragment.java      # 设置页面
│   │   ├── model/                         # 数据模型层
│   │   │   ├── WeatherData.java           # 当前天气数据模型
│   │   │   ├── DailyWeather.java          # 多日天气数据模型
│   │   │   ├── HourlyWeather.java         # 逐时天气数据模型
│   │   │   ├── City.java                  # 城市信息模型
│   │   │   ├── MusicItem.java             # 音乐项目模型
│   │   │   ├── DiaryEntry.java            # 日记条目模型
│   │   │   └── ScheduleEntry.java         # 日程提醒模型
│   │   ├── service/                       # 服务层
│   │   │   ├── WeatherService.java        # 天气数据服务
│   │   │   ├── MusicService.java          # 音乐播放服务
│   │   │   └── NotificationService.java   # 通知服务
│   │   ├── database/                      # 数据存储层
│   │   │   └── WeatherDatabaseHelper.java # SQLite数据库管理
│   │   └── util/                          # 工具层
│   │       ├── NetworkUtils.java          # 网络工具类
│   │       ├── CityNameMapper.java        # 城市名称映射工具
│   │       └── ThemeManager.java          # 主题管理工具
│   ├── res/
│   │   ├── layout/                        # 布局文件
│   │   ├── drawable/                      # 图标资源
│   │   ├── raw/                          # 音乐文件
│   │   ├── values/                       # 字符串和颜色
│   │   ├── menu/                         # 菜单配置
│   │   └── navigation/                   # 导航配置
│   └── AndroidManifest.xml               # 应用配置文件
└── build.gradle.kts                      # 构建配置
```

## 运行说明

### 环境要求
- Android Studio 4.2+
- Android SDK API 24-33
- Java 1.8+

### 构建步骤
1. 克隆项目到本地
2. 使用Android Studio打开项目
3. 等待Gradle同步完成
4. 连接Android设备或启动模拟器
5. 点击运行按钮

### 注意事项
- 确保网络连接正常（用于天气API调用）
- 首次运行需要授予位置和存储权限
- 音乐文件已内置在应用中

## API配置

项目使用和风天气API，已预配置API密钥：
- **API Host**: p63fbxfvwb.re.qweatherapi.com
- **API Key**: 367ab1fc296f4981bdec40bc3a40ca03

## 权限说明

应用需要以下权限：
- `INTERNET` - 网络访问
- `ACCESS_FINE_LOCATION` - 精确位置
- `ACCESS_COARSE_LOCATION` - 粗略位置
- `READ_EXTERNAL_STORAGE` - 读取存储
- `POST_NOTIFICATIONS` - 通知权限

## 功能截图

应用包含5个主要界面：
- **天气页面**: 显示当前天气和基本信息
- **预报页面**: 展示多日和逐时天气预报
- **音乐页面**: 音乐播放器和播放列表
- **日记页面**: 日记管理和统计信息
- **设置页面**: 个人设置和应用配置

## 数据存储

使用SQLite数据库存储：
- 天气数据缓存
- 城市信息
- 日记条目
- 用户设置
- 播放列表

## 技术特点

### 🎨 UI设计
- 扁平化设计风格
- 柔和的色彩搭配
- Material Design组件
- 响应式布局
- 夜间模式支持

### 🏗️ 架构设计
- 模块化开发
- 清晰的分层架构
- MVC模式应用
- Fragment导航
- Service后台服务
- SQLite数据持久化

### 🛡️ 稳定性
- 异常处理机制
- 网络错误重试
- 数据验证
- 内存优化
- 多线程处理

### 🚀 性能优化
- RecyclerView列表优化
- 图片加载优化
- 数据库查询优化
- 内存泄漏防护
- 网络请求缓存

## 开发团队

Weather Team - Android开发专家团队

## 版本信息

- **版本号**: 1.0.0
- **发布日期**: 2024年5月25日
- **支持系统**: Android 7.0+

## 许可证

本项目遵循MIT许可证。

## 联系方式

如有问题或建议，请联系开发团队。 