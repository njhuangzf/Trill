package com.bird.trill.fragment;

import android.databinding.DataBindingUtil;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bird.trill.R;
import com.bird.trill.databinding.FragmentShortVideoBinding;
import com.bird.trill.base.BaseAdapter;
import com.bird.trill.databinding.ItemVideoBinding;
import com.bird.trill.base.BaseFragment;
import com.bird.trill.bean.VideoBean;
import com.bird.trill.widget.OnViewPagerListener;
import com.bird.trill.widget.ShortVideoPlayerView;
import com.bird.trill.widget.ViewPagerLayoutManager;
import com.bumptech.glide.Glide;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;

import java.util.ArrayList;
import java.util.List;

public class ShortVideoFragment extends BaseFragment<FragmentShortVideoBinding> {
    private static final String TAG = "ShortVideoFragment";

    private ViewPagerLayoutManager mLayoutManager;
    private VideoAdapter mAdapter;

    @Override
    public void onResume() {
        if (mAdapter.getItemCount() > 0) {
            View firstView = binding.recyclerView.getChildAt(0);
            if (null != firstView) {
                ShortVideoPlayerView videoPlayer = firstView.findViewById(R.id.video_player);
                if (null != videoPlayer) {
                    videoPlayer.resumePlayerState();
                }
            }
        } else {
            load(false);
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (mAdapter.getItemCount() > 0) {
            ShortVideoPlayerView videoPlayer = binding.recyclerView.getChildAt(0).findViewById(R.id.video_player);
            if (null != videoPlayer) {
                videoPlayer.onPause();
            }
        }
        super.onPause();
    }

    @Override
    protected int layoutRes() {
        return R.layout.fragment_short_video;
    }

    @Override
    protected void initView() {
        mLayoutManager = new ViewPagerLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new VideoAdapter();
        binding.recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initListener() {
        binding.refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                load(false);
                binding.refreshLayout.finishRefresh();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                load(true);
                binding.refreshLayout.finishRefreshLoadMore();
            }
        });

        mLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {

            @Override
            public void onInitComplete() {
                Log.i(TAG, "onInitComplete");
                playVideo();
            }

            @Override
            public void onPageRelease(boolean isNext, int position) {
                Log.i(TAG, "释放位置:" + position + " 下一页:" + isNext);
                int index;
                if (isNext) {
                    index = 0;
                } else {
                    index = 1;
                }
                releaseVideo(index);
            }

            @Override
            public void onPageSelected(int position, boolean isBottom) {
                Log.i(TAG, "选中位置:" + position + "  是否是滑动到底部:" + isBottom);
                playVideo();
            }
        });
    }

    private void playVideo() {
        View itemView = binding.recyclerView.getChildAt(0);
        ShortVideoPlayerView videoPlayer = itemView.findViewById(R.id.video_player);
        ImageView imgPlay = itemView.findViewById(R.id.img_play);
        ImageView imgThumb = itemView.findViewById(R.id.img_thumb);

        videoPlayer.start();
        videoPlayer.setOnInfoListener((arg0, arg1) -> imgThumb.animate().alpha(0).setDuration(200).start());

        imgPlay.setOnClickListener(new View.OnClickListener() {
            boolean isPlaying = true;

            @Override
            public void onClick(View v) {
                if (videoPlayer.isPlaying()) {
                    imgPlay.animate().alpha(1f).start();
                    videoPlayer.pause();
                    isPlaying = false;
                } else {
                    imgPlay.animate().alpha(0f).start();
                    videoPlayer.start();
                    isPlaying = true;
                }
            }
        });
    }

    private void releaseVideo(int index) {
        View itemView = binding.recyclerView.getChildAt(index);
        ItemVideoBinding itemVideoBinding = DataBindingUtil.findBinding(itemView);
        itemVideoBinding.videoPlayer.stop();
        itemVideoBinding.imgThumb.animate().alpha(1).start();
        itemVideoBinding.imgPlay.animate().alpha(0f).start();
    }

    private void load(boolean more) {
        List<VideoBean> list = new ArrayList<>();
        list.add(new VideoBean());
        list.add(new VideoBean());
        list.add(new VideoBean());
        list.add(new VideoBean());
        list.add(new VideoBean());
        if (more) {
            mAdapter.addAll(list);
        } else {
            mAdapter.refresh(list);
        }
    }

    class VideoAdapter extends BaseAdapter<VideoBean, ItemVideoBinding> {

        @Override
        protected int layoutResId(int viewType) {
            return R.layout.item_video;
        }

        @Override
        protected void convert(SimpleViewHolder holder, int position, VideoBean item) {
            Glide.with(getContext())
                    .load(item.getThumb())
                    .into(holder.binding.imgThumb);
            Glide.with(getContext())
                    .load(R.mipmap.hp_1)
                    .into(holder.binding.headPortrait);
            holder.binding.videoPlayer.setVideoUrl(item.getVideoUrl());
        }
    }
}
