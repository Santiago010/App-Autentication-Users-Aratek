package cn.com.aratek.demo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractBaseActivity extends AppCompatActivity {

    protected static final int STATUS_CLOSED = 0;
    protected static final int STATUS_OPENED = 1;
    protected static final int STATUS_CLOSING = 2;
    protected static final int STATUS_OPENING = 3;

    protected AtomicInteger mStatus;

    private static final int MSG_SHOW_PROGRESS_DIALOG = 0;
    private static final int MSG_DISMISS_PROGRESS_DIALOG = 1;
    private static final int MSG_SHOW_INFORMATION = 2;
    private static final int MSG_SHOW_DETAILS = 3;

    private ProgressDialog mProgressDialog;
    private TextView mInformation;
    private TextView mDetails;
    private ThreadPoolExecutor mExecutor;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStatus = new AtomicInteger(STATUS_CLOSED);

        mHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                AbstractBaseActivity.this.handleMessage(msg);
            }
        };

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setIcon(android.R.drawable.ic_dialog_info);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread() {
            @Override
            public void run() {
                open();
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        new Thread() {
            @Override
            public void run() {
                close();
            }
        }.start();
    }

    @CallSuper
    @Override
    public void onContentChanged() {
        super.onContentChanged();

        mInformation = findViewById(R.id.info);
        mDetails = findViewById(R.id.details);
    }

    @CallSuper
    protected void handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case MSG_SHOW_PROGRESS_DIALOG: {
                CharSequence[] info = (CharSequence[]) msg.obj;
                mProgressDialog.setTitle(info[0]);
                mProgressDialog.setMessage(info[1]);
                mProgressDialog.show();
                break;
            }
            case MSG_DISMISS_PROGRESS_DIALOG: {
                mProgressDialog.dismiss();
                break;
            }
            case MSG_SHOW_INFORMATION: {
                mInformation.setTextColor(msg.arg1);
                mInformation.setText((CharSequence) msg.obj);
                break;
            }
            case MSG_SHOW_DETAILS: {
                mDetails.setTextColor(msg.arg1);
                mDetails.setText((CharSequence) msg.obj);
                break;
            }
        }
    }

    protected final Handler getHandler() {
        return mHandler;
    }

    protected abstract void open();

    protected abstract void close();

    protected final void createNewExecutor() {
        mExecutor = new ThreadPoolExecutor(1, 1, 3, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

    protected final ThreadPoolExecutor getExecutor() {
        return mExecutor;
    }

    protected final void shutdownExecutorAndAwaitTermination() {
        mExecutor.shutdownNow();
        try {
            mExecutor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        mExecutor = null;
    }

    protected void showProgressDialog(CharSequence title, CharSequence message) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SHOW_PROGRESS_DIALOG,
                new CharSequence[]{title, message}));
    }

    protected void dismissProgressDialog() {
        mHandler.sendEmptyMessage(MSG_DISMISS_PROGRESS_DIALOG);
    }

    protected void showError(@NonNull String info, @Nullable String details) {
        showMessage(info, details, getResources().getColor(R.color.error_text_color),
                getResources().getColor(R.color.error_details_text_color));
    }

    protected void showInformation(@NonNull String info, @Nullable String details) {
        showMessage(info, details, getResources().getColor(R.color.information_text_color),
                getResources().getColor(R.color.information_details_text_color));
    }

    @Override
    public boolean onSupportNavigateUp() {
        closeAndFinishAsync();
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            closeAndFinishAsync();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void closeAndFinishAsync() {
        new Thread() {
            @Override
            public void run() {
                close();
                finish();
            }
        }.start();
    }

    private void showMessage(@NonNull String info, @Nullable String details,
                             int infoColor, int detailsColor) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SHOW_INFORMATION, infoColor, 0, info));
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SHOW_DETAILS, detailsColor, 0,
                details == null ? "" : details));
    }

}
