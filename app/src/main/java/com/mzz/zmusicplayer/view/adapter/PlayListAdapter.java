package com.mzz.zmusicplayer.view.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.mzz.zmusicplayer.R;
import com.mzz.zmusicplayer.common.MusicConstant;
import com.mzz.zmusicplayer.common.SongColorHandler;
import com.mzz.zmusicplayer.common.util.SongUtil;
import com.mzz.zmusicplayer.manage.AdapterManager;
import com.mzz.zmusicplayer.model.LocalSongModel;
import com.mzz.zmusicplayer.play.PlayList;
import com.mzz.zmusicplayer.play.Player;
import com.mzz.zmusicplayer.song.SongInfo;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import lombok.Getter;

/**
 * @author : Mzz
 * date : 2019 2019/6/1 15:56
 * description :
 */
public class PlayListAdapter extends SongInfoAdapter {
    int itemSongNameId = R.id.tv_item_song_name;
    @Getter
    private PlayList playList;
    private SongInfo currentColorSong;
    private Context context;
    private SongColorHandler songColorHandler;

    /**
     * Instantiates a new Song info adapter.
     *
     * @param playList     the play list
     * @param recyclerView the recycler view
     */
    protected PlayListAdapter(PlayList playList, RecyclerView recyclerView) {
        super(R.layout.item_song_list, playList.getPlaySongs(), recyclerView);
        this.playList = playList;
        context = recyclerView.getContext();
        int selectColor = context.getColor(R.color.colorGreen);
        songColorHandler = new SongColorHandler(playList, selectColor);
        setOnItemChildClickListener((adapter, view, position) -> showSongMoreMenu(view, position));
        setPlaySongClickListener();
    }

    /**
     * Update play song background color.
     *
     * @param song the song
     */
    public void updatePlaySongBackgroundColor(SongInfo song) {
        //重置上一次选中的歌曲
        if (currentColorSong != null) {
            currentColorSong.setPlayListSelected(false);
        }
        if (song != null) {
            song.setPlayListSelected(true);
            currentColorSong = song;
            notifyDataSetChanged();
        }
    }

    /**
     * Remove song at.
     *
     * @param position the position
     */
    public void removeSongAt(int position) {
        remove(position);
        notifyDataSetChanged();
    }

    /**
     * Locate to selected song.
     */
    public void locateToSelectedSong() {
        int adapterPosition = playList.getPlayingIndex() + 1;
        scrollToPosition(adapterPosition);
    }

    @Override
    protected void convert(BaseViewHolder helper, SongInfo songInfo) {
        super.convert(helper, songInfo);
        songColorHandler.setSongColor(helper, songInfo);
        helper.addOnClickListener(R.id.iv_song_more);
        helper.setText(itemSongNameId, songInfo.getSongName());
    }

    void updatePlaySongs(List<SongInfo> songs) {
        playList.setPlaySongs(songs);
        setNewData(songs);
    }

    private void setPlaySongClickListener() {
        this.setOnItemClickListener((adapter, view, position) -> EventBus.getDefault().post(getItem(position)));
    }

    private void showSongMoreMenu(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.menu_song_more);
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.action_song_delete) {
                removeSongAt(position);
                return true;
            } else if (itemId == R.id.action_song_detail) {
                SongInfo songInfo = PlayListAdapter.this.getItem(position);
                if (songInfo == null) {
                    return true;
                }
                showSongDetail(songInfo);
            }
            return false;
        });
        popupMenu.show();
    }

    private void showSongDetail(SongInfo songInfo) {
        String name = songInfo.getName();
        String artist = songInfo.getArtist();
        View songDetailView = View.inflate(mContext, R.layout.content_song_detail, null);

        EditText etSongName = songDetailView.findViewById(R.id.et_song_name);
        etSongName.setText(name);
        EditText etArtistName = songDetailView.findViewById(R.id.et_artist_name);
        etArtistName.setText(artist);
        TextView tvDetailFilePath = songDetailView.findViewById(R.id.tv_detail_file_path);
        tvDetailFilePath.setText(songInfo.getPath());

        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(MusicConstant.SONG_DETAIL_TITLE)
                .setPositiveButton(MusicConstant.SAVE_NAME, (dialog, which) -> {
                    updateSongName(songInfo, name, etSongName);
                    updateArtist(songInfo, artist, etArtistName);
                    AdapterManager.notifyDataSetChanged();
                    Player.getInstance().notifySongNameChanged(songInfo);
                    dialog.dismiss();
                })
                .setNegativeButton(MusicConstant.CANCEL_NAME, (dialog, which) -> dialog.dismiss())
                .setView(songDetailView)
                .create();
        alertDialog.show();
    }

    private void updateArtist(SongInfo songInfo, String artist, EditText etArtistName) {
        String newArtist = etArtistName.getText().toString();
        if (!artist.equals(newArtist)) {
            songInfo.setArtist(newArtist);
            LocalSongModel.update(songInfo);
        }
    }

    private void updateSongName(SongInfo songInfo, String name, EditText etSongName) {
        String newSongName = etSongName.getText().toString();
        if (!name.equals(newSongName)) {
            songInfo.setName(newSongName);
            songInfo.setNameSpell(SongUtil.getUpperSpell(newSongName));
            LocalSongModel.update(songInfo);
        }
    }

}
