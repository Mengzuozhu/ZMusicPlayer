package com.mzz.zmusicplayer.song;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * author : Mzz
 * date : 2019 2019/6/8 15:37
 * description :
 */
class RecentSong {
    private static final int RECENT_MAX_COUNT = 50;
    private PriorityQueue <SongInfo> minHeap;
    private List <SongInfo> allSongs;

    RecentSong(List <SongInfo> allSongs) {
        this.allSongs = allSongs;
    }

    /**
     * Sort recent songs linked list .
     *
     * @return the linked list
     */
    LinkedList <SongInfo> sortRecentSongs() {
        initMinHeap();
        buildMinHeap();
        LinkedList <SongInfo> recentSongs = new LinkedList <>();
        for (SongInfo songInfo : minHeap) {
            recentSongs.addFirst(songInfo);
        }
        return recentSongs;
    }

    private void buildMinHeap() {
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
