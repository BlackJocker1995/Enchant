package me.wcy.music.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import me.wcy.music.R;
import me.wcy.music.adapter.FragmentAdapter;
import me.wcy.music.application.AppCache;
import me.wcy.music.application.MusicApplication;
import me.wcy.music.constants.Extras;
import me.wcy.music.executor.NaviMenuExecutor;
import me.wcy.music.fragment.LocalMusicFragment;
import me.wcy.music.fragment.PlayFragment;
import me.wcy.music.fragment.SongListFragment;
import me.wcy.music.model.Music;
import me.wcy.music.service.OnPlayerEventListener;
import me.wcy.music.service.PlayService;
import me.wcy.music.utils.CoverLoader;
import me.wcy.music.utils.SystemUtils;
import me.wcy.music.utils.ToastUtils;
import me.wcy.music.utils.binding.Bind;
import me.wcy.music.widget.CircleImageView;


public class MusicActivity extends BaseActivity implements View.OnClickListener, OnPlayerEventListener,
        NavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {
    public static final int REQUEST_CODE_PROFILE = 0x997;
    @Bind(R.id.drawer_layout)
    private DrawerLayout drawerLayout;
    @Bind(R.id.navigation_view)
    private NavigationView navigationView;
    @Bind(R.id.iv_menu)
    private ImageView ivMenu;
    @Bind(R.id.iv_search)
    private ImageView ivSearch;
    @Bind(R.id.tv_local_music)
    private TextView tvLocalMusic;
    @Bind(R.id.tv_online_music)
    private TextView tvOnlineMusic;
    @Bind(R.id.viewpager)
    private ViewPager mViewPager;
    @Bind(R.id.fl_play_bar)
    private FrameLayout flPlayBar;
    @Bind(R.id.iv_play_bar_cover)
    private ImageView ivPlayBarCover;
    @Bind(R.id.tv_play_bar_title)
    private TextView tvPlayBarTitle;
    @Bind(R.id.tv_play_bar_artist)
    private TextView tvPlayBarArtist;
    @Bind(R.id.iv_play_bar_play)
    private ImageView ivPlayBarPlay;
    @Bind(R.id.iv_play_bar_next)
    private ImageView ivPlayBarNext;
    @Bind(R.id.pb_play_bar)
    private ProgressBar mProgressBar;


    public TextView profile_tv;
    private View vNavigationHeader;
    private LocalMusicFragment mLocalMusicFragment;
    private SongListFragment mSongListFragment;
    private PlayFragment mPlayFragment;
    private AudioManager mAudioManager;
    private ComponentName mRemoteReceiver;
    private boolean isPlayFragmentShow = false;
    private MenuItem timerItem;
    private MenuItem loginItem;
    private String username;
    private CircleImageView circleimg;
    private int avatar = -1;
    private static final String TAG = "MusicActivity";
    public static final int REQUEST_CODE_LOGIN = 0x999;
    private SharedPreferences sp;
    ArrayList<String> urlList;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        if (!checkServiceAlive()) {
            return;
        }

        getPlayService().setOnPlayEventListener(this);

        setupView();
        onChange(getPlayService().getPlayingMusic());
        parseIntent();
        initList();
        initProfile();
    }

    private void initList() {
        urlList = new ArrayList<String>();
        urlList.add("http://www.lovexn.top/img/80948.jpg");
        urlList.add("http://www.lovexn.top/img/80949.jpg");
        urlList.add("http://www.lovexn.top/img/80950.jpg");
        urlList.add("http://www.lovexn.top/img/80951.jpg");
        urlList.add("http://www.lovexn.top/img/80952.jpg");
        urlList.add("http://www.lovexn.top/img/80953.jpg");
        urlList.add("http://www.lovexn.top/img/80954.jpg");
        urlList.add("http://www.lovexn.top/img/80955.jpg");
        urlList.add("http://www.lovexn.top/img/80956.jpg");
        urlList.add("http://www.lovexn.top/img/80957.jpg");
        urlList.add("http://www.lovexn.top/img/80958.jpg");
        urlList.add("http://www.lovexn.top/img/80959.jpg");
        urlList.add("http://www.lovexn.top/img/80960.jpg");
        urlList.add("http://www.lovexn.top/img/80961.jpg");
        urlList.add("http://www.lovexn.top/img/80962.jpg");
        urlList.add("http://www.lovexn.top/img/80963.jpg");
        urlList.add("http://www.lovexn.top/img/80964.jpg");
        urlList.add("http://www.lovexn.top/img/80965.jpg");
    }

    /*??????????????????*/
    private void initProfile() {
        if (MusicApplication.getLoginState() == 1) {
            sp = getSharedPreferences("proFile", MODE_PRIVATE);//??????????????????
            name = sp.getString("name", "defaultname");
            profile_tv.setText(name);
            int id = sp.getInt("id", 0);
            int avatar = sp.getInt("avatar", 0);
            Log.d(TAG, "initProfile: " + name);
            Log.d(TAG, "initProfile: " + id);
            Log.d(TAG, "initProfile: initUi" + avatar);
            if (avatar != -1) {
                Log.d(TAG, "initProfile: 1111" + avatar);
                Glide.with(this)
                        .load(urlList.get(avatar))
                        .into(circleimg);
            } else {
                Log.d(TAG, "initProfile: mg/80948.jpg" + avatar);
                Glide.with(this)
                        .load("http://www.lovexn.top/img/80948.jpg")
                        .into(circleimg);
            }
        } else {
            Log.d(TAG, "initProfile: mg/80948.jpg" + avatar);
            Glide.with(this)
                    .load("http://www.lovexn.top/img/80948.jpg")
                    .into(circleimg);
        }


    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        parseIntent();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void setListener() {
        ivMenu.setOnClickListener(this);
        ivSearch.setOnClickListener(this);
        tvLocalMusic.setOnClickListener(this);
        tvOnlineMusic.setOnClickListener(this);
        mViewPager.setOnPageChangeListener(this);
        flPlayBar.setOnClickListener(this);
        ivPlayBarPlay.setOnClickListener(this);
        ivPlayBarNext.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupView() {
        // add navigation header
        vNavigationHeader = LayoutInflater.from(this).inflate(R.layout.drawer_header, navigationView, false);
        navigationView.addHeaderView(vNavigationHeader);
        if (MusicApplication.getLoginState() == 1) {
            if (loginItem == null) {
                loginItem = navigationView.getMenu().findItem(R.id.action_login);
            }
            loginItem.setTitle("??????");
        }
        profile_tv = (TextView) vNavigationHeader.findViewById(R.id.profile_tv);
        circleimg = (CircleImageView) vNavigationHeader.findViewById(R.id.circleimg);
        // setup view pager
        mLocalMusicFragment = new LocalMusicFragment();
        mSongListFragment = new SongListFragment();
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(mLocalMusicFragment);
        adapter.addFragment(mSongListFragment);
        mViewPager.setAdapter(adapter);
        tvLocalMusic.setSelected(true);
        /*????????????*/
        vNavigationHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawers();
                Intent intent = new Intent(MusicActivity.this, ProfileAcitivity.class);
                intent.putExtra("name", username);
                if (MusicApplication.getLoginState() == 0) {
                    ToastUtils.show("????????????");
//                    return;
                }
                if (avatar == -1) {
                    avatar = 0;
                }
                intent.putExtra("avatar", avatar);
                startActivityForResult(intent, REQUEST_CODE_PROFILE);
            }
        });
    }

    /*????????????*/
    private void parseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(Extras.EXTRA_NOTIFICATION)) {
            showPlayingFragment();
            setIntent(new Intent());
        }
    }

    /**
     * ??????????????????
     */
    @Override
    public void onPublish(int progress) {
        if (mProgressBar != null)
            mProgressBar.setProgress(progress);
        if (mPlayFragment != null) {
            mPlayFragment.onPublish(progress);
        }
    }

    @Override
    public void onChange(Music music) {
        onPlay(music);
        if (mPlayFragment != null) {
            mPlayFragment.onChange(music);
        }
    }

    @Override
    public void onPlayerPause() {
        ivPlayBarPlay.setSelected(false);
        if (mPlayFragment != null) {
            mPlayFragment.onPlayerPause();
        }
    }

    @Override
    public void onPlayerResume() {
        ivPlayBarPlay.setSelected(true);
        if (mPlayFragment != null) {
            mPlayFragment.onPlayerResume();
        }
    }

    @Override
    public void onTimer(long remain) {
        if (timerItem == null) {
            timerItem = navigationView.getMenu().findItem(R.id.action_timer);
        }
        String title = getString(R.string.menu_timer);
        timerItem.setTitle(remain == 0 ? title : SystemUtils.formatTime(title + "(mm:ss)", remain));
    }

    @Override
    public void onMusicListUpdate() {
        if (mLocalMusicFragment != null) {
            mLocalMusicFragment.onMusicListUpdate();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_menu:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.iv_search:

                startActivity(new Intent(this, SearchMusicActivity.class));
                break;
            case R.id.tv_local_music:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.tv_online_music:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.fl_play_bar:
                showPlayingFragment();
                break;
            case R.id.iv_play_bar_play:
                play();
                break;
            case R.id.iv_play_bar_next:
                next();
                break;
        }
    }


    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        drawerLayout.closeDrawers();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                item.setChecked(false);
            }
        }, 500);
        return NaviMenuExecutor.onNavigationItemSelected(item, this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            tvLocalMusic.setSelected(true);
            tvOnlineMusic.setSelected(false);
        } else {
            tvLocalMusic.setSelected(false);
            tvOnlineMusic.setSelected(true);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public void onPlay(Music music) {
        if (music == null) {
            return;
        }

        Bitmap cover = CoverLoader.getInstance().loadThumbnail(music);
        ivPlayBarCover.setImageBitmap(cover);
        tvPlayBarTitle.setText(music.getTitle());
        tvPlayBarArtist.setText(music.getArtist());
        if (getPlayService().isPlaying() || getPlayService().isPreparing()) {
            ivPlayBarPlay.setSelected(true);
        } else {
            ivPlayBarPlay.setSelected(false);
        }
        if (mProgressBar != null) {
            mProgressBar.setMax((int) music.getDuration());
            mProgressBar.setProgress(0);
        }


        if (mLocalMusicFragment != null) {
            mLocalMusicFragment.onItemPlay();
        }
    }

    private void play() {
        getPlayService().playPause();
    }

    private void next() {
        getPlayService().next();
    }

    private void showPlayingFragment() {
        if (isPlayFragmentShow) {
            return;
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_slide_up, 0);
        if (mPlayFragment == null) {
            mPlayFragment = new PlayFragment();
            ft.replace(android.R.id.content, mPlayFragment);
        } else {
            ft.show(mPlayFragment);
        }
        ft.commitAllowingStateLoss();
        isPlayFragmentShow = true;
    }

    private void hidePlayingFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(0, R.anim.fragment_slide_down);
        ft.hide(mPlayFragment);
        ft.commitAllowingStateLoss();
        isPlayFragmentShow = false;
    }

    @Override
    public void onBackPressed() {
        if (mPlayFragment != null && isPlayFragmentShow) {
            hidePlayingFragment();
            return;
        }
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }

        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // ?????????????????????????????????
    }

    @Override
    protected void onDestroy() {
        if (mRemoteReceiver != null) {
            mAudioManager.unregisterMediaButtonEventReceiver(mRemoteReceiver);
        }
        PlayService service = AppCache.getPlayService();
        if (service != null) {
            service.setOnPlayEventListener(null);
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: 111");
        if (data == null) {
            return;
        }
        if (requestCode == REQUEST_CODE_LOGIN) {
            Bundle b = data.getExtras(); //data???B????????????Intent
            String result = b.getString("result");//str??????????????????
//            String usernames = b.getString("name");
            Log.d(TAG, "onActivityResult: " + result);
            if (MusicApplication.getLoginState() == 1) {
                if (loginItem == null) {
                    loginItem = navigationView.getMenu().findItem(R.id.action_login);
                }
                loginItem.setTitle("??????");
                Log.d(TAG, "onActivityResult: " + name);
//                profile_tv.setText(name);
//                name = usernames;
                initProfile();
            } else {
                Log.d(TAG, "onActivityResult: sss" + MusicApplication.getLoginState());
                if (loginItem == null) {
                    loginItem = navigationView.getMenu().findItem(R.id.action_login);
                }
                loginItem.setTitle("??????");
                profile_tv.setText("");
            }


        } else if (requestCode == REQUEST_CODE_PROFILE) {
            Bundle b = data.getExtras(); //data???B????????????Intent
            avatar = b.getInt("avatar", 0);//str??????????????????
            Log.d(TAG, "onActivityResult: 111");
            Log.d(TAG, "onActivityResult: 11" + avatar);
            changeProfile();
        }
    }

    private void changeProfile() {
        Log.d(TAG, "changeProfile: " + avatar);
        Glide.with(this)
                .load(urlList.get(avatar))
                .into(circleimg);
    }
}
