package com.jonthanhsia.flashplayer.adapter;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jonthanhsia.flashplayer.R;
import com.jonthanhsia.flashplayer.domain.Mp3Info;

/**
 * 音乐列表的的数据适配器
 * 
 * @author JonathanHsia
 */
public class MySongListAdapter extends BaseAdapter {
	private List<Mp3Info> sMp3InfosFilter;// 过滤后的歌曲数据集
	private Activity act;

	public MySongListAdapter(List<Mp3Info> sMp3InfosFilter, Activity ctx) {
		this.sMp3InfosFilter = sMp3InfosFilter;
		this.act = ctx;
	}

	@Override
	public int getCount() {
		return sMp3InfosFilter.size();
	}

	@Override
	public Mp3Info getItem(int position) {
		// 如果歌曲超过list的大小的话就返回null
		if (position < 0 || position >= sMp3InfosFilter.size()) {
			return null;
		}
		return sMp3InfosFilter.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null) {
			convertView = View.inflate(act, R.layout.listitem_songlist_item, null);

			vh = new ViewHolder();

			vh.tvSongName = (TextView) convertView
					.findViewById(R.id.lt_tv_song_name);
			vh.tvSongArtist = (TextView) convertView
					.findViewById(R.id.lt_tv_song_artist);
			vh.tvSongAlbum = (TextView) convertView
					.findViewById(R.id.lt_tv_song_album);
			vh.ivSongState = (ImageView) convertView.findViewById(R.id.songlist_iv_item_state);

			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		// 设置view的数据
		Mp3Info info = sMp3InfosFilter.get(position);

		vh.tvSongName.setText(info.title);
		vh.tvSongArtist.setText(info.artist);
		vh.tvSongAlbum.setText(info.album);
		vh.ivSongState.setVisibility(View.INVISIBLE);//音乐的默认状态时隐藏的
		
		return convertView;
	}

	/**
	 * View的数据保持者
	 * 
	 * @author JonathanHsia
	 */
	 class ViewHolder {
		TextView tvSongName;
		TextView tvSongArtist;
		TextView tvSongAlbum;
		ImageView ivSongState;
	}
}