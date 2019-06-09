package com.mzz.zmusicplayer.song;

import com.mzz.zmusicplayer.model.LocalSongModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * 最近播放的歌曲
 * author : Mzz
 * date : 2019 2019/6/8 15:37
 * description :
 */
public class RecentSong {
    private static final int RECENT_MAX_COUNT = 50;
    private static RecentSong recentSong = new RecentSong();
    private PriorityQueue <SongInfo> minHeap;
    private LinkedList <SongInfo> recentSongs;

    private RecentSong() {
    }

    public static RecentSong getInstance() {
        return recentSong;
    }

    /**
     * Gets recent songs.
     *
     * @return the recent songs
     */
    public LinkedList <SongInfo> getRecentSongs() {
        if (recentSongs == null) {
            initRecentSongs();
        }
        return recentSongs;
    }

    /**
     * Update recent song.
     *
     * @param song the song
     */
    void updateRecentSong(SongInfo song) {
        recentSongs = getRecentSongs();
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
    public List <SongInfo> remove(Collection <Long> keys) {
        List <SongInfo> removeSongs = new ArrayList <>();
        for (int i = recentSongs.size() - 1; i >= 0 && !keys.isEmpty(); i--) {
            SongInfo song = recentSongs.get(i);
            Long id = song.getId();
            if (keys.contains(id)) {
                song.setLastPlayTime(null);
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
        song.setLastPlayTime(null);
        LocalSongModel.update(song);
    }

    private void initRecentSongs() {
        initMinHeap();
        buildMinHeap();
        recentSongs = new LinkedList <>();
        while (!minHeap.isEmpty()) {
            recentSongs.addFirst(minHeap.poll());
        }
    }

    private void removeRecentSong() {
        while (recentSongs.size() > RECENT_MAX_COUNT) {
            recentSongs.removeLast();
        }
    }

    private void buildMinHeap() {
        List <SongInfo> allSongs = LocalSong.getInstance().getAllLocalSongs();
        //构建最小堆，获取前n个最近播放的歌曲
        for (SongInfo localSong : allSongs) {
            Date lastPlayTime = localSong.getLastPlayTime();
            if (lastPlayTime == null) {
                continue;
            }
            if (minHeap.size() < RECENT_MAX_COUNT) {
                minHeap.add(localSong);
            }
            //晚于堆顶的最早时间，则删除堆顶，新增该歌曲
            else {
                Date minTime = minHeap.peek().getLastPlayTime();
                if (minTime != null && lastPlayTime.after(minTime)) {
                    minHeap.poll();
                    minHeap.add(localSong);
                }
            }
        }
    }

    private void initMinHeap() {
        minHeap = new PriorityQueue <>(RECENT_MAX_COUNT, (o1, o2) -> {
            Date lastPlayTime1 = o1.getLastPlayTime();
            if (lastPlayTime1 == null) {
                return -1;
            }
            Date lastPlayTime2 = o2.getLastPlayTime();
            if (lastPlayTime2 == null) {
                return 1;
            }
            return lastPlayTime1.compareTo(lastPlayTime2);
        });
    }
}
