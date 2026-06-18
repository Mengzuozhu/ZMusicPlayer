# ZMusicPlayer（梦音乐）

本地音乐播放器，支持扫描设备音频、管理播放列表、后台播放与通知栏控制。

![image](https://github.com/Mengzuozhu/ZMusicPlayer/blob/master/demo/demo.jpg)

## 核心功能

| 功能 | 说明 |
|------|------|
| 本地歌曲管理 | 通过 `MediaStore` 扫描设备 MP3，写入 GreenDAO 数据库，支持添加、编辑、删除 |
| 多列表浏览 | 四个 Tab：**播放**（当前队列）、**最近**（按播放时间）、**本地**（全部歌曲）、**喜欢**（收藏） |
| 播放控制 | 播放/暂停、上一首/下一首、进度拖动、三种播放模式（顺序 / 单曲循环 / 随机） |
| 后台播放 | `PlaybackService` 前台服务，通知栏显示歌曲信息与控制按钮 |
| 收藏 | 收藏/取消收藏，同步更新列表与通知栏图标 |
| 搜索 | `MusicSearchActivity` 按歌名搜索并播放 |
| 批量编辑 | 长按列表进入 `SongEditActivity`，支持多选删除 |
| 应用配置 | 歌曲排序方式、最近播放数量上限、播放模式持久化 |
| 配置导出 | 将本地歌曲库导出为 JSON 文件 |
| 系统联动 | 耳机拔出自动暂停、来电暂停、音频焦点管理 |

## 技术栈

- **语言**：Java 8
- **UI**：Material Design、ViewPager2 + TabLayout、ButterKnife
- **架构**：MVP（Presenter / Contract / View）
- **数据库**：GreenDAO（`SongInfo` 实体）
- **通信**：EventBus（列表点击 → 播放栏联动）
- **其他**：RxPermissions、TinyPinyin（中文排序）、Bugly 崩溃上报

## 项目结构

```
app/src/main/java/com/mzz/zmusicplayer/
├── MainActivity.java              # 主界面，Tab + 底部播放栏
├── MusicApplication.java          # Application 入口
├── play/                          # 播放核心
│   ├── Player.java                # 单例播放器（MediaPlayer）
│   ├── PlayList.java              # 播放队列与切歌逻辑
│   └── PlayObserver.java          # 播放状态回调
├── song/                          # 歌曲数据域
│   ├── SongInfo.java              # GreenDAO 实体
│   ├── LocalSong.java             # 本地歌曲库单例
│   ├── FavoriteSong.java          # 收藏列表
│   └── RecentSong.java            # 最近播放列表
├── model/
│   └── LocalSongModel.java        # 数据库 CRUD
├── manage/
│   └── FileManager.java           # MediaStore 扫描
├── service/
│   ├── PlaybackService.java       # 前台播放服务 + 通知栏
│   └── SeekBarService.java        # 进度条刷新
├── view/
│   ├── ui/                        # Activity / Fragment
│   ├── presenter/                 # MVP Presenter
│   ├── adapter/                   # RecyclerView 适配器
│   └── header/                    # 列表头部（播放全部、编辑入口）
├── config/AppSetting.java         # SharedPreferences 配置
├── receiver/HeadsetReceiver.java  # 耳机插拔监听
└── enums/                         # PlayedMode、SongListType 等
```

## 核心链路

### 1. 应用启动

```
MusicApplication.onCreate()
  └── CrashReport 初始化

MainActivity.onCreate()
  ├── initTabPage()          → ViewPager2 加载 4 个 SongFragment
  ├── initSongChangeListeners() → 注册列表高亮监听
  ├── HeadsetReceiver 注册
  └── EventBus 注册（接收播放/列表变更事件）
```

主界面布局：`TabLayout` + `ViewPager2`（歌曲列表）+ `MusicControlFragment`（底部播放栏）。

### 2. 添加歌曲

```
菜单「添加歌曲」→ SongPickerActivity
  ├── RxPermissions 申请存储权限
  ├── FileManager.getAllSongInfos()
  │     └── MediaStore.Audio.Media 扫描 MP3，解析歌名/歌手/时长
  ├── 用户勾选 → 返回 MainActivity
  └── LocalSongFragment.addToLocalSongs()
        └── LocalSongModel.insertOrReplaceInTx() 写入数据库
```

### 3. 播放歌曲（UI → 播放器）

```
用户点击列表项
  └── EventBus.post(SongInfo)
        └── MainActivity.updatePlayingSong()
              └── MusicControlFragment.updatePlayingSong()
                    └── Player.play(song)
                          ├── 若不在队列则加入 PlayList
                          ├── MediaPlayer.setDataSource(path)
                          ├── prepare() + start()
                          └── recordPlayingSong()（更新播放次数、最近播放、持久化 songId）
```

点击「播放全部」时，`SongListHeader` 会额外 `post` 整个播放列表，同步更新「播放」Tab 的队列内容。

### 4. 播放队列与切歌

```
PlayList
  ├── playSongs        ← isChecked=true 的本地歌曲（当前队列）
  ├── playingIndex     ← 当前播放位置
  └── playMode         ← ORDER / SINGLE / RANDOM

Player.onCompletion() → playNext()
Player.playPrevious() / playNext()
  └── PlayList.previous() / next()  按模式计算下一首索引
        └── startNewSong() → MediaPlayer 加载新文件
```

文件不存在或播放失败时，自动从队列移除并尝试播放下一首。

### 5. 后台服务与通知栏

```
MusicControlPresenter.subscribe()
  └── bindService(PlaybackService)

PlaybackService.onCreate()
  ├── Player.registerCallback(this)
  └── startForeground() 显示自定义通知 RemoteViews

通知栏按钮 → Intent Action → onStartCommand()
  ├── PLAY_TOGGLE / PLAY_PRE / PLAY_NEXT
  ├── FAVORITE / PLAY_MODE
  └── STOP_SERVICE → 退出应用
```

`Player` 为全局单例，`MusicControlFragment` 与 `PlaybackService` 共用同一实例，保证 UI 与后台状态一致。

### 6. 数据持久化

| 存储 | 内容 |
|------|------|
| GreenDAO（`SongInfo`） | 歌名、路径、歌手、拼音、时长、播放次数、最后播放时间、是否在队列、是否收藏 |
| SharedPreferences（`AppSetting`） | 播放模式、排序方式、上次播放 songId、最近列表上限 |

启动时 `PlayList.updatePlayingIndexBySettingId()` 根据上次 songId 恢复播放位置。

### 7. 列表联动（EventBus）

| 事件 | 发布方 | 订阅方 | 作用 |
|------|--------|--------|------|
| `SongInfo` | 列表点击 / 搜索 | `MainActivity` | 切换当前播放歌曲 |
| `List<SongInfo>` | 播放全部 | `MainActivity` → `PlayListFragment` | 更新播放队列 |
| `RemovedSongInfo` | 搜索页删除 | `MainActivity` → 对应 Fragment | 从列表移除歌曲 |

## 播放模式

| 模式 | 行为 |
|------|------|
| 顺序播放 | 列表循环，到末尾回到第一首 |
| 单曲循环 | 重复当前歌曲 |
| 随机播放 | 随机选取，避免连续重复同一首 |

## 构建

```bash
./gradlew assembleRelease
```

输出 APK：`梦音乐-v{versionName}.apk`（当前 v1.8.0，minSdk 24，targetSdk 29）。
