package me.wcy.music.executor;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.webkit.MimeTypeMap;

import me.wcy.music.R;
import me.wcy.music.application.AppCache;
import me.wcy.music.utils.FileUtils;
import me.wcy.music.utils.NetworkUtils;
import me.wcy.music.utils.Preferences;


public abstract class DownloadMusic implements IExecutor<Void> {
    private Activity mActivity;

    public DownloadMusic(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void execute() {
        checkNetwork();
    }

    private void checkNetwork() {
        boolean mobileNetworkDownload = Preferences.enableMobileNetworkDownload();
        if (NetworkUtils.isActiveNetworkMobile(mActivity) && !mobileNetworkDownload) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle(R.string.tips);
            builder.setMessage(R.string.download_tips);
            builder.setPositiveButton(R.string.download_tips_sure, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    downloadWrapper();
                }
            });
            builder.setNegativeButton(R.string.cancel, null);
            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } else {
            downloadWrapper();
        }
    }

    private void downloadWrapper() {
        onPrepare();
        download();
    }

    protected abstract void download();

    protected static void downloadMusic(String url, String artist, String song) {
        String fileName = FileUtils.getMp3FileName(artist, song);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(FileUtils.getFileName(artist, song));
        request.setDescription("???????????????");
        request.setDestinationInExternalPublicDir(FileUtils.getRelativeMusicDir(), fileName);
        request.setMimeType(MimeTypeMap.getFileExtensionFromUrl(url));
        request.allowScanningByMediaScanner();
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);// ???????????????
        DownloadManager downloadManager = (DownloadManager) AppCache.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        long id = downloadManager.enqueue(request);
        AppCache.getDownloadList().put(id, song);
    }
}
