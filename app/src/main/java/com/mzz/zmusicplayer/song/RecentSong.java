package com.mzz.zmusicplayer.song;

import com.mzz.zmusicplayer.config.AppSetting;
import com.mzz.zmusicplayer.model.LocalSongModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import lombok.Getter;

/**
 * 最近播放的歌曲
 *
 * @author : Mzz
 * date : 2019 2019/6/8 15:37
 * description :
 */
public class RecentSong {
    private static RecentSong recentSong = new RecentSong();
    private int recentSongMaxCount;
    private PriorityQueue<SongInfo> minHeap;
    @Getter
    private LinkedList<SongInfo> recentSongs;

    private RecentSong() {
        recentSongMaxCount = AppSetting.getRecentSongMaxCount();
        initRecentSongs();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static RecentSong getInstance() {
        return recentSong;
    }

    /**
     * Get recent count values string [ ].
     *
     * @return the string [ ]
     */
    public static String[] getRecentCountValues() {
        int count = 10;
        String[] recentValues = new String[count];
        for (int i = 0; i < count; i++) {
            recentValues[i] = String.valueOf(10 * (i + 1));
        }
        return recentValues;
    }

    /**
     * Update recent song.
     *
     * @param song the song
     */
    void updateRecentSong(SongInfo song) {
        recentSongs.remove(song);
        recentSongs.addFirst(song);
        removeRecentSong();
        LocalSongModel.update(song);
    }

    /**
     * Remove list .
     *
     * @param keys the keys
     * @return the list
     */
    public List<SongInfo> remove(Collection<Long> keys) {
        List<SongInfo> removeSongs = new ArrayList<>();
        for (int i = recentSongs.size() - 1; i >= 0 && !keys.isEmpty(); i--) {
            SongInfo song = recentSongs.get(i);
            Long id = song.getId();
            if (keys.contains(id)) {
                song.setLastPlayTime(0L);
                recentSongs.remove(i);
                keys.remove(id);
                removeSongs.add(song);
            }
        }
        LocalSongModel.updateInTx(removeSongs);
        return recentSongs;
    }

    /**
     * Remove.
     *
     * @param song the song
     */
    public void remove(SongInfo song) {
        if (song == null) {
            return;
        }
        song.setLastPlayTime(0L);
        LocalSongModel.update(song);
    }

    /**
     * Update recent max count.
     */
    public void updateRecentMaxCount(int maxCount) {
        recentSongMaxCount = maxCount;
        AppSetting.setRecentSongMaxCount(recentSongMaxCount);
        removeRecentSong();
    }

    private void removeRecentSong() {
        while (recentSongs.size() > recentSongMaxCount) {
            recentSongs.removeLast();
        }
    }

    private void initRecentSongs() {
        initMinHeap();
        buildMinHeap();
        recentSongs = new LinkedList<>();
        while (!minHeap.isEmpty()) {
            recentSongs.addFirst(minHeap.poll());
        }
    }

    private void initMinHeap() {
        minHeap = new PriorityQueue<>(recentSongMaxCount,
                (o1, o2) -> Long.compare(o1.getLastPlayTime(), o2.getLastPlayTime()));
    }

    private void buildMinHeap() {
        List<SongInfo> allSongs = LocalSong.getInstance().getAllLocalSongs();
        //构建最小堆，获取前n个最近播放的歌曲
        for (SongInfo localSong : allSongs) {
            Long lastPlayTime = localSong.getLastPlayTime();
            //忽略未播放或被删除歌曲
            if (lastPlayTime == null || lastPlayTime == 0) {
                continue;
            }
            if (minHeap.size() < recentSongMaxCount) {
                minHeap.add(localSong);
            }
            //晚于堆顶的最早时间，则删除堆顶，新增该歌曲
            else {
                SongInfo peek = minHeap.peek();
                if (peek == null) {
                    continue;
                }
                long minTime = peek.getLastPlayTime();
                if (lastPlayTime > minTime) {
                    minHeap.poll();
                    minHeap.add(localSong);
                }
            }
        }
    }

}
