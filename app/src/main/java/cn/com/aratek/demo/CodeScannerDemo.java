package cn.com.aratek.demo;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.UnsupportedEncodingException;

import cn.com.aratek.qrc.CodeScanner;
import cn.com.aratek.util.Result;

public class CodeScannerDemo extends AbstractBaseActivity implements View.OnClickListener {

    private static final int MSG_UPDATE_FIRMWARE_VERSION = 100;
    private static final int MSG_UPDATE_SERIAL_NUMBER = 101;
    private static final int MSG_UPDATE_OUTPUT_DATA = 102;
    private static final int MSG_ENABLE_BUTTONS = 103;

    private TextView mSerialNumber;
    private TextView mFirmwareVersion;
    private TextView mScanInfo;
    private Button mBtnScan;
    private CodeScanner mCodeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        mCodeScanner = CodeScanner.getInstance(this);

        mSerialNumber = findViewById(R.id.serial_number);
        mFirmwareVersion = findViewById(R.id.firmware_version);
        mScanInfo = findViewById(R.id.data_output);
        mScanInfo.setMovementMethod(ScrollingMovementMethod.getInstance());
        mBtnScan = findViewById(R.id.bt_scan);

        enableButtons(false);
    }

    @Override
    protected void handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case MSG_UPDATE_FIRMWARE_VERSION: {
                mFirmwareVersion.setText((String) msg.obj);
                break;
            }
            case MSG_UPDATE_SERIAL_NUMBER: {
                mSerialNumber.setText((String) msg.obj);
                break;
            }
            case MSG_UPDATE_OUTPUT_DATA: {
                mScanInfo.setText((String) msg.obj);
                break;
            }
            case MSG_ENABLE_BUTTONS: {
                Boolean enable = (Boolean) msg.obj;
                mBtnScan.setEnabled(enable);
                break;
            }
            default: {
                super.handleMessage(msg);
                break;
            }
        }
    }

    @Override
    protected synchronized void open() {
        if (STATUS_CLOSED != mStatus.get()) {
            return;
        }

        mStatus.set(STATUS_OPENING);

        showProgressDialog(getString(R.string.loading), getString(R.string.preparing_device));

        createNewExecutor();

        int error;
        Result res;

        mCodeScanner.powerOn(); // ignore power on errors
        error = mCodeScanner.open();
        if (error == CodeScanner.RESULT_OK) {
            mStatus.set(STATUS_OPENED);

            showInformation(getString(R.string.hardware_barcode_scanner_open_success), null);
            res = mCodeScanner.getFirmwareVersion();
            updateFirmwareVersion((String) res.data);
            res = mCodeScanner.getSerial();
            updateSerialNumber((String) res.data);
            enableButtons(true);
        } else {
            mCodeScanner.powerOff(); // ignore power off errors
            mStatus.set(STATUS_CLOSED);

            showError(getString(R.string.hardware_barcode_scanner_open_failed),
                    getErrorString(error));
            updateFirmwareVersion(null);
            updateSerialNumber(null);
        }

        dismissProgressDialog();
    }

    @Override
    protected synchronized void close() {
        if (STATUS_CLOSED == mStatus.get()) {
            return;
        }

        mStatus.set(STATUS_CLOSING);

        showProgressDialog(getString(R.string.loading), getString(R.string.closing_device));

        enableButtons(false);

        shutdownExecutorAndAwaitTermination();

        int error;

        error = mCodeScanner.close();
        if (error == CodeScanner.RESULT_OK) {
            showInformation(getString(R.string.hardware_barcode_scanner_close_success), null);
        } else {
            showError(getString(R.string.hardware_barcode_scanner_close_failed),
                    getErrorString(error));
        }
        mCodeScanner.powerOff(); // ignore power off errors
        mStatus.set(STATUS_CLOSED);

        dismissProgressDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_scan:
                scan();
                break;
        }
    }

    private void updateFirmwareVersion(String fwVer) {
        getHandler().sendMessage(getHandler().obtainMessage(MSG_UPDATE_FIRMWARE_VERSION,
                TextUtils.isEmpty(fwVer) ? "null" : fwVer));
    }

    private void updateSerialNumber(String serial) {
        getHandler().sendMessage(getHandler().obtainMessage(MSG_UPDATE_SERIAL_NUMBER,
                TextUtils.isEmpty(serial) ? "null" : serial));
    }

    private void updateOutputData(String info) {
        getHandler().sendMessage(getHandler().obtainMessage(MSG_UPDATE_OUTPUT_DATA, info));
    }

    private void enableButtons(boolean enable) {
        getHandler().sendMessage(getHandler().obtainMessage(MSG_ENABLE_BUTTONS, enable));
    }

    private void scan() {
        getExecutor().execute(new CodeScannerTask("scan"));
    }

    private String getErrorString(int error) {
        int strid;
        switch (error) {
            case CodeScanner.RESULT_OK:
                strid = R.string.operation_successful;
                break;
            case CodeScanner.RESULT_FAIL:
                strid = R.string.error_operation_failed;
                break;
            case CodeScanner.WRONG_CONNECTION:
                strid = R.string.error_wrong_connection;
                break;
            case CodeScanner.DEVICE_BUSY:
                strid = R.string.error_device_busy;
                break;
            case CodeScanner.DEVICE_NOT_OPEN:
                strid = R.string.error_device_not_open;
                break;
            case CodeScanner.TIMEOUT:
                strid = R.string.error_timeout;
                break;
            case CodeScanner.NO_PERMISSION:
                strid = R.string.error_no_permission;
                break;
            case CodeScanner.WRONG_PARAMETER:
                strid = R.string.error_wrong_parameter;
                break;
            case CodeScanner.DECODE_ERROR:
                strid = R.string.error_decode;
                break;
            case CodeScanner.INIT_FAIL:
                strid = R.string.error_initialization_failed;
                break;
            case CodeScanner.UNKNOWN_ERROR:
                strid = R.string.error_unknown;
                break;
            case CodeScanner.NOT_SUPPORT:
                strid = R.string.error_not_support;
                break;
            case CodeScanner.NOT_ENOUGH_MEMORY:
                strid = R.string.error_not_enough_memory;
                break;
            case CodeScanner.DEVICE_NOT_FOUND:
                strid = R.string.error_device_not_found;
                break;
            case CodeScanner.DEVICE_REOPEN:
                strid = R.string.error_device_reopen;
                break;
            default:
                return getString(R.string.error_other, error);
        }
        return getString(strid);
    }

    private class CodeScannerTask implements Runnable {
        private String mTask;

        CodeScannerTask(String task) {
            mTask = task;
        }

        @Override
        public void run() {
            Result res;

            enableButtons(false);

            if (mTask.equals("scan")) {
                showProgressDialog(getString(R.string.loading), getString(R.string.align_barcode));

                updateOutputData("");

                do {
                    res = mCodeScanner.scan();
                } while (res.error == CodeScanner.TIMEOUT && !getExecutor().isShutdown());

                if (!getExecutor().isShutdown()) {
                    if (res.error == CodeScanner.RESULT_OK) {
                        // Below is a sample for data processing, you need to process these data,
                        // for example, decode or save.
                        try {
                            updateOutputData(new String((byte[]) res.data, "UTF-8").trim());
                            showInformation(getString(R.string.barcode_scan_success), null);
                        } catch (UnsupportedEncodingException e) {
                            showError(getString(R.string.unsupported_encoding), null);
                        }
                    } else {
                        showError(getString(R.string.barcode_scan_failed),
                                getErrorString(res.error));
                    }
                }

                dismissProgressDialog();
            }

            enableButtons(true);
        }
    }
}
