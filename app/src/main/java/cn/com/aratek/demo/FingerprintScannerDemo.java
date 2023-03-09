package cn.com.aratek.demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.regex.Pattern;

import cn.com.aratek.demo.featuresrequest.DataForLogin;
import cn.com.aratek.demo.featuresrequest.FingerprintService;
import cn.com.aratek.demo.featuresrequest.Newuser;
import cn.com.aratek.demo.featuresrequest.User;
import cn.com.aratek.demo.utils.Prefs;
import cn.com.aratek.fp.Bione;
import cn.com.aratek.fp.FingerprintImage;
import cn.com.aratek.fp.FingerprintScanner;
import cn.com.aratek.util.Result;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FingerprintScannerDemo extends AbstractBaseActivity implements View.OnClickListener,
        AdapterView.OnItemSelectedListener {

    private static final String TAG = FingerprintScannerDemo.class.getSimpleName();
    private static final String FP_DB_PATH = "/sdcard/fp.db";
    private static final int MSG_UPDATE_FIRMWARE_VERSION = 100;
    private static final int MSG_UPDATE_SERIAL_NUMBER = 101;
    private static final int MSG_UPDATE_FINGERPRINT = 102;
    private static final int MSG_UPDATE_TIME_INFORMATIONS = 103;
    private static final int MSG_ENABLE_BUTTONS = 104;

    private TextView mSerialNumber;
    private TextView mFirmwareVersion;
    private Spinner mSpLfd;
    private ImageView mFingerprintImage;
    private TextView mCaptureTime;
    private TextView mExtractTime;
    private TextView mGeneralizeTime;
    private TextView mVerifyTime;
    private Button mBtnEnroll;
    private Button mBtnVerify;
    private Button mBtnIdentify;
    private Button mBtnClear;
    private Button mBtnShow;

    private FingerprintScanner mFingerprintScanner;
    private int mId;
    private int mLfdLevel = FingerprintScanner.LFD_LEVEL_OFF;

    public byte[] fpAcid;

    private Intent infoIntent;
    private User user;

    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);

        mFingerprintScanner = FingerprintScanner.getInstance(this);

        prefs = new Prefs(this);

        infoIntent = getIntent();

        user = (User) infoIntent.getSerializableExtra("INFOFP");




        mSerialNumber = findViewById(R.id.serial_number);
        mFirmwareVersion = findViewById(R.id.firmware_version);
        mSpLfd = findViewById(R.id.lfd_level);
        mSpLfd.setOnItemSelectedListener(this);
        mCaptureTime = findViewById(R.id.captureTime);
        mExtractTime = findViewById(R.id.extractTime);
        mGeneralizeTime = findViewById(R.id.generalizeTime);
        mVerifyTime = findViewById(R.id.verifyTime);
        mFingerprintImage = findViewById(R.id.fingerimage);


        mBtnEnroll = findViewById(R.id.bt_enroll);
        mBtnVerify = findViewById(R.id.bt_verify);
        mBtnIdentify = findViewById(R.id.bt_identify);
        mBtnClear = findViewById(R.id.bt_clear);
        mBtnShow = findViewById(R.id.bt_show);

        enableButtons(false);

        updateTimeInformations(-1, -1, -1, -1);

        changeBtnsVisibility();
    }

    //TODO:METODO TEMPORAL PARA CAMBIAR LA VISIBILDAD DE LOS BTNS
    private void changeBtnsVisibility(){
        Pattern p = Pattern.compile("[0-9]+");
        if(p.matcher(user.getName()).find() == false){
            mBtnVerify.setVisibility(View.GONE);
            mBtnIdentify.setVisibility(View.GONE);
            mBtnClear.setVisibility(View.GONE);

        }else{
            mBtnEnroll.setVisibility(View.GONE);

        }
    }

    private GsonConverterFactory makeConfGson(){
        Gson gsonC = new GsonBuilder().create();
        GsonConverterFactory gsonConverterFactoryC = GsonConverterFactory.create(gsonC);
        return gsonConverterFactoryC;
    }

    private Retrofit makeConfRequest(){

        Retrofit retrofit = new Retrofit.Builder().baseUrl(prefs.getUrl())
                .addConverterFactory(makeConfGson()).build();
        return retrofit;
    }

    private void sendData(String name){
        Log.d("NNAME",name);
        FingerprintService fps = makeConfRequest().create(FingerprintService.class);

        DataForLogin dataForLogin = new DataForLogin(name,user.getPassword(),"admin",user.getAvatar(),user.getEmail());
        Call<Newuser> call = fps.putUser(String.valueOf(user.getId()),dataForLogin);
        call.enqueue(new Callback<Newuser>() {
            @Override
            public void onResponse(Call<Newuser> call, Response<Newuser> response) {
                try {
                    if(response.isSuccessful()){
                        Newuser userRes = response.body();
                        Log.d("PUTU",userRes.getName());


                    }

                }catch (Exception ex){
                    Log.d("APIER",ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Newuser> call, Throwable t) {
                Log.d("ONFAILURE",t.getMessage());
            }
        });
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
            case MSG_UPDATE_FINGERPRINT: {
                mFingerprintImage.setImageBitmap((Bitmap) msg.obj);
                break;
            }
            case MSG_UPDATE_TIME_INFORMATIONS: {
                String[] texts = (String[]) msg.obj;
                mCaptureTime.setText(texts[0]);
                mExtractTime.setText(texts[1]);
                mGeneralizeTime.setText(texts[2]);
                mVerifyTime.setText(texts[3]);
                break;
            }
            case MSG_ENABLE_BUTTONS: {
                Boolean enable = (Boolean) msg.obj;
                mBtnEnroll.setEnabled(enable);
                mBtnVerify.setEnabled(enable);
                mBtnIdentify.setEnabled(enable);
                mBtnClear.setEnabled(enable);
                mBtnShow.setEnabled(enable);
                mSpLfd.setEnabled(enable);
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

        //TODO:muestra el cuadro de dialogo de carga
        showProgressDialog(getString(R.string.loading), getString(R.string.preparing_device));

        createNewExecutor();

        //TODO:inicializa una variable error y un res que es de tipo Result
        int error;
        Result res;

        //TODO: prender el scanner de huella
        mFingerprintScanner.powerOn(); // ignore power on errors

        error = mFingerprintScanner.open();
        //TODO: inicia el scanner de huella y devuelve un numero que representa el estado de dicha acción
        //TODO: en caso de que haya salido correcta devolvera un 0
        Log.d("RESUlT", String.valueOf(error)  );
        if (error == FingerprintScanner.RESULT_OK) {
            mStatus.set(STATUS_OPENED);

            //TODO:se muestra que el dispositivo esta listo para usarse
            showInformation(getString(R.string.fingerprint_device_open_success), null);

            //TODO:aca se settea la version del firmware el serial number y se activan los botones
            res = mFingerprintScanner.getFirmwareVersion();
            updateFirmwareVersion((String) res.data);
            res = mFingerprintScanner.getSerial();
            updateSerialNumber((String) res.data);
            mFingerprintScanner.setLfdLevel(mLfdLevel);
            enableButtons(true);

            // initialize fingerprint algorithm
            Log.i(TAG, "Fingerprint algorithm version: " + Bione.getVersion());
            if ((error = Bione.initialize(FingerprintScannerDemo.this, FP_DB_PATH))
                    != Bione.RESULT_OK) {
                showError(getString(R.string.algorithm_initialization_failed),
                        getErrorString(error));
            }
        } else {
            //TODO:caso de que haya dado un error se apagara el sensor y se mostrara el error
            mFingerprintScanner.powerOff(); // ignore power off errors
            mStatus.set(STATUS_CLOSED);

            showError(getString(R.string.fingerprint_device_open_failed), getErrorString(error));
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

        Bione.exit();
        error = mFingerprintScanner.close();
        if (error == FingerprintScanner.RESULT_OK) {
            showInformation(getString(R.string.fingerprint_device_close_success), null);
        } else {
            showError(getString(R.string.fingerprint_device_close_failed), getErrorString(error));
        }
        mFingerprintScanner.powerOff(); // ignore power off errors
        mStatus.set(STATUS_CLOSED);

        dismissProgressDialog();
    }

    //TODO:funcion que se encarga de ejecutar una accion dependiendo el btn que se haya presionado
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_enroll:
                enroll();
                break;
            case R.id.bt_verify:
                verify();
                break;
            case R.id.bt_identify:
                identify();
                break;
            case R.id.bt_clear:
                clearFingerprintDatabase();
                break;
            case R.id.bt_show:
                showFingerprintImage();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switchLfd(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void updateFirmwareVersion(String fwVer) {
        getHandler().sendMessage(getHandler().obtainMessage(MSG_UPDATE_FIRMWARE_VERSION,
                TextUtils.isEmpty(fwVer) ? "null" : fwVer));
    }

    private void updateSerialNumber(String serial) {
        getHandler().sendMessage(getHandler().obtainMessage(MSG_UPDATE_SERIAL_NUMBER,
                TextUtils.isEmpty(serial) ? "null" : serial));
    }

    //TODO:esta funcion actualiza la imagen de la huella
    private void updateFingerprintImage(FingerprintImage fi) {

        byte[] fpBmp;
        Bitmap bitmap;
        if (fi == null || (fpBmp = fi.convert2Bmp()) == null ||
                (bitmap = BitmapFactory.decodeByteArray(fpBmp, 0, fpBmp.length)) == null) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.nofinger);
        }

        getHandler().sendMessage(getHandler().obtainMessage(MSG_UPDATE_FINGERPRINT, bitmap));



    }


    private void updateTimeInformations(long captureTime, long extractTime, long generalizeTime,
                                        long verifyTime) {
        String[] texts = new String[4];
        if (captureTime < 0) {
            texts[0] = getString(R.string.not_done);
        } else if (captureTime < 1) {
            texts[0] = "< 1ms";
        } else {
            texts[0] = captureTime + "ms";
        }

        if (extractTime < 0) {
            texts[1] = getString(R.string.not_done);
        } else if (extractTime < 1) {
            texts[1] = "< 1ms";
        } else {
            texts[1] = extractTime + "ms";
        }

        if (generalizeTime < 0) {
            texts[2] = getString(R.string.not_done);
        } else if (generalizeTime < 1) {
            texts[2] = "< 1ms";
        } else {
            texts[2] = generalizeTime + "ms";
        }

        if (verifyTime < 0) {
            texts[3] = getString(R.string.not_done);
        } else if (verifyTime < 1) {
            texts[3] = "< 1ms";
        } else {
            texts[3] = verifyTime + "ms";
        }

        getHandler().sendMessage(getHandler().obtainMessage(MSG_UPDATE_TIME_INFORMATIONS, texts));
    }

    private void enableButtons(boolean enable) {
        getHandler().sendMessage(getHandler().obtainMessage(MSG_ENABLE_BUTTONS, enable));
    }

    private void switchLfd(int level) {
        mLfdLevel = level;
        if (STATUS_OPENED == mStatus.get()) {
            getExecutor().execute(new FingerprintScannerTask("switch_lfd"));
        }
    }

    //TODO:cuando esta funcion es llamada por el boton de enroll lo que hace es ejecutar un hilo que instancia la clase
    //TODO:FingerprintScannerTask y le pasa por parametro la accion enroll
    private void enroll() {
        getExecutor().execute(new FingerprintScannerTask("enroll"));
    }

    private void verify() {
        getExecutor().execute(new FingerprintScannerTask("verify"));
    }

    private void identify() {
        getExecutor().execute(new FingerprintScannerTask("identify"));
    }

    private void clearFingerprintDatabase() {
        getExecutor().execute(new FingerprintScannerTask("clear_database"));
    }

    private void showFingerprintImage() {
        getExecutor().execute(new FingerprintScannerTask("show"));
    }

    private String getErrorString(int error) {
        int strid;
        switch (error) {
            case FingerprintScanner.RESULT_OK:
                strid = R.string.operation_successful;
                break;
            case FingerprintScanner.RESULT_FAIL:
                strid = R.string.error_operation_failed;
                break;
            case FingerprintScanner.WRONG_CONNECTION:
                strid = R.string.error_wrong_connection;
                break;
            case FingerprintScanner.DEVICE_BUSY:
                strid = R.string.error_device_busy;
                break;
            case FingerprintScanner.DEVICE_NOT_OPEN:
                strid = R.string.error_device_not_open;
                break;
            case FingerprintScanner.TIMEOUT:
                strid = R.string.error_timeout;
                break;
            case FingerprintScanner.NO_PERMISSION:
                strid = R.string.error_no_permission;
                break;
            case FingerprintScanner.WRONG_PARAMETER:
                strid = R.string.error_wrong_parameter;
                break;
            case FingerprintScanner.DECODE_ERROR:
                strid = R.string.error_decode;
                break;
            case FingerprintScanner.INIT_FAIL:
                strid = R.string.error_initialization_failed;
                break;
            case FingerprintScanner.UNKNOWN_ERROR:
                strid = R.string.error_unknown;
                break;
            case FingerprintScanner.NOT_SUPPORT:
                strid = R.string.error_not_support;
                break;
            case FingerprintScanner.NOT_ENOUGH_MEMORY:
                strid = R.string.error_not_enough_memory;
                break;
            case FingerprintScanner.DEVICE_NOT_FOUND:
                strid = R.string.error_device_not_found;
                break;
            case FingerprintScanner.DEVICE_REOPEN:
                strid = R.string.error_device_reopen;
                break;
            case FingerprintScanner.NO_FINGER:
                strid = R.string.error_no_finger;
                break;
            case Bione.INITIALIZE_ERROR:
                strid = R.string.error_algorithm_initialization_failed;
                break;
            case Bione.INVALID_FEATURE_DATA:
                strid = R.string.error_invalid_feature_data;
                break;
            case Bione.BAD_IMAGE:
                strid = R.string.error_bad_image;
                break;
            case Bione.NOT_MATCH:
                strid = R.string.error_not_match;
                break;
            case Bione.LOW_POINT:
                strid = R.string.error_low_point;
                break;
            case Bione.NO_RESULT:
                strid = R.string.error_no_result;
                break;
            case Bione.OUT_OF_BOUND:
                strid = R.string.error_out_of_bound;
                break;
            case Bione.DATABASE_FULL:
                strid = R.string.error_database_full;
                break;
            case Bione.LIBRARY_MISSING:
                strid = R.string.error_library_missing;
                break;
            case Bione.UNINITIALIZE:
                strid = R.string.error_algorithm_uninitialize;
                break;
            case Bione.REINITIALIZE:
                strid = R.string.error_algorithm_reinitialize;
                break;
            case Bione.REPEATED_ENROLL:
                strid = R.string.error_repeated_enroll;
                break;
            case Bione.NOT_ENROLLED:
                strid = R.string.error_not_enrolled;
                break;
            default:
                return getString(R.string.error_other, error);
        }
        return getString(strid);
    }

    //TODO:esta clase extiende del Runnable la cual solo tiene un metodo que se va llamar cuando es instaciada
    ///TODO: el cual es run
    private class FingerprintScannerTask implements Runnable {
        private String mTask;

        FingerprintScannerTask(String task) {
            mTask = task;
        }

        @Override
        public void run() {
            long startTime, captureTime = -1, extractTime = -1, generalizeTime = -1,
                    verifyTime = -1;
            //TODO:se inicializa esta variable fi que es de tipo FingerprintImage
            //TODO:este tipo representa informacion de la huella tomada
            FingerprintImage fi = null;
            //TODO: se inicializa dos variables de tipo byte[] que en realidad es un arreglo de bytes
            byte[] fpFeat = null, fpTemp = null;
            //TODO: se inicializa una variables res que es de tipo Result
            Result res;

            enableButtons(false);


            do {
                //TODO:esta condicion va verificar si la tarea que esta en cola es alguna de las que esta evaluando
                if (mTask.equals("show") || mTask.equals("enroll") || mTask.equals("verify") ||
                        mTask.equals("identify")) {
                    //TODO:aca muestra un mensaje para poner el dedo en el lector de huellas
                    showProgressDialog(getString(R.string.loading),
                            getString(R.string.press_finger));

                    //TODO:Prepara el scanner
                    mFingerprintScanner.prepare();
                    //TODO:se ejecuta este do while el cual se va detener si no se pone el dedo
                    do {
                        startTime = System.currentTimeMillis();

                        //TODO:la variable res va obtener el valor que le devuelva el metodo capture
                        //TODO: el cual va capturar una imagen de la huella
                        //TODO:el valor de res va ser un error de tipo int y una data de tipo object
                        res = mFingerprintScanner.capture();
                        captureTime = System.currentTimeMillis() - startTime;


                        //TODO:aca la variable fi va tomar el valor de res.data
                        fi = (FingerprintImage) res.data;
                        if (fi != null) {

                            Log.i(TAG, "Fingerprint image quality is " + Bione.getFingerprintQuality(fi));
                        }

                        if (res.error != FingerprintScanner.NO_FINGER ||
                                getExecutor().isShutdown()) {
                            break;
                        }
                    } while (true);
                    //TODO:aca se apaga el scanner de huellas
                    mFingerprintScanner.finish();

                    if (getExecutor().isShutdown()) {
                        break;
                    }

                    //TODO: si la variable fi es diferente de null entonces va llamar a updateFingerprintImage y le pasa el valor de fi
                    if (fi != null) {
                        updateFingerprintImage(fi);
                    }

                    //TODO: aca verifica si res.error tienen un error el cual hace referencia a que la huella es falsa
                    if (res.error == FingerprintScanner.FAKE_FINGER) {
                        showError(getString(R.string.fake_finger_detected), null);
                        break;
                    }

                    //TODO: aca verifica si res.error tiene un codigo de error diferente de ok y muestra un mensaje de error
                    if (res.error != FingerprintScanner.RESULT_OK) {
                        showError(getString(R.string.capture_image_failed),
                                getErrorString(res.error));
                        break;
                    }
                }

                //TODO:estos if es para mostrar un mensaje de la accion que esta accion en el momento
                if (mTask.equals("show")) {
                    showInformation(getString(R.string.capture_image_success), null);
                    break;
                } else if (mTask.equals("enroll")) {
                    showProgressDialog(getString(R.string.loading), getString(R.string.enrolling));
                } else if (mTask.equals("verify")) {
                    showProgressDialog(getString(R.string.loading), getString(R.string.verifying));
                } else if (mTask.equals("identify")) {
                    showProgressDialog(getString(R.string.loading),
                            getString(R.string.identifying));
                }

                //TODO:este if verifica si la tarea corresponde a una de las siguientes acciones
                //TODO:para si extraer las caracteristicas de la imagen previamente obtenida por el sensor
                if (mTask.equals("enroll") || mTask.equals("verify") || mTask.equals("identify")) {
                    startTime = System.currentTimeMillis();
                    //TODO:aca es cuando extrae las caracteristicas
                    res = Bione.extractFeature(fi);
                    extractTime = System.currentTimeMillis() - startTime;
                    //TODO:esta condicion es para mostrar un error y detener la ejecucion del ciclo do while
                    if (res.error != Bione.RESULT_OK) {
                        showError(getString(R.string.enroll_failed_because_of_extract_feature),
                                getErrorString(res.error));
                        break;
                    }

                    //TODO:fpFeat va guardar en bytes las caracteristicas de la huella
                    fpFeat = (byte[]) res.data;

                }

                //TODO:este if se va ejectutar solo si la tarea es enroll
                if (mTask.equals("enroll")) {
                    startTime = System.currentTimeMillis();
                    //TODO: se va llamar a la funciona makeTemplate la cual va crear plantilla de la huella a partir de pasarle por parametros la huella
                    //TODO: la funcion va devolver un Result que se guarda en res
                    res = Bione.makeTemplate(fpFeat, fpFeat, fpFeat);
                    generalizeTime = System.currentTimeMillis() - startTime;

                    //TODO:va a mostrar un mensaje de error en caso de que no sea OK
                    if (res.error != Bione.RESULT_OK) {
                        showError(getString(R.string.enroll_failed_because_of_make_template),
                                getErrorString(res.error));
                        break;
                    }
                    //TODO:fpTemp va tomar el valor de res.data ya que res va ser de tipo Result gracia a la funcion MakeTemplate llamada anteriormente
                    fpTemp = (byte[]) res.data;
                    fpAcid = (byte[]) res.data;

                    //TODO: obtiene una identificador no utilizado en la base de datos
                    int id = Bione.getFreeID();

                    ///TODO: en caso de que id sea menor que 0 va mostrar un mensaje de error diciendo que la base de datos esta llena y se detiene el ciclo
                    if (id < 0) {
                        showError(getString(R.string.enroll_failed_because_of_get_id),
                                getErrorString(id));
                        break;
                    }

                    //TODO: se llama a la funcion enroll la cual va guadar en la base de datos un registro con un id unico y los datos de la huella
                    int ret = Bione.enroll(id, fpTemp);
                    //TODO:aca el if verifica si fue correcta el guardado y detiene el ciclo
                    if (ret != Bione.RESULT_OK) {
                        showError(getString(R.string.enroll_failed_because_of_error),
                                getErrorString(ret));
                        break;
                    }

//                    String str = new String(fpTemp);
                    sendData(String.valueOf(fpFeat));

                    //TODO:aca va mostrar un mensaje diciendo que se guardo correctamente en la base de datos con el id
                    mId = id;
                    showInformation(getString(R.string.enroll_success),
                            getString(R.string.enrolled_id, id));
                    break;
                }

                //TODO:este if se va ejectutar solo si la tarea es verify
                if (mTask.equals("verify")) {

                    startTime = System.currentTimeMillis();
                    //TODO: aca llamada al metodo verify el cual va hacer concidir la plantilla de huella que le estamos pasando con un id y nos devolvera en res el resultado de esto
                    //TODO: en tipo Result en donde res.data va ser true si se encuentra una concidencia y si no false
//                    Log.d("ACID",String.valueOf(fpAcid));
                    byte[] nFP = user.getName().getBytes();
                    res = Bione.verify( nFP,fpFeat);
//                    Log.d("mid",String.valueOf(res));
//                    Log.d("VERIFY",fpFeat.toString());


                    verifyTime = System.currentTimeMillis() - startTime;

                    //TODO:este if muestra un mensaje de error de que fallo al verificar
                    if (res.error != Bione.RESULT_OK) {
                        showError(getString(R.string.verify_failed_because_of_error),
                                getErrorString(res.error));

                        break;
                    }

                    //TODO:este if muestra un mensaje de huella verificada si hacer math y si no pues un mensaje de que no hizo match
                    if ((Boolean) res.data) {
                        Log.d("VERIFY",String.valueOf(res.data));
                        showInformation(getString(R.string.fingerprint_match),
                                getString(R.string.fingerprint_similarity, res.arg1));
                    } else {
                        showError(getString(R.string.fingerprint_not_match),
                                getString(R.string.fingerprint_similarity, res.arg1));
                    }
                    break;
                }

                if (mTask.equals("identify")) {
                    startTime = System.currentTimeMillis();
                    int id = Bione.identify(fpFeat);
                    verifyTime = System.currentTimeMillis() - startTime;
                    if (id < 0) {
                        showError(getString(R.string.identify_failed_because_of_error),
                                getErrorString(id));
                    } else {
                        showInformation(getString(R.string.identify_match),
                                getString(R.string.matched_id, id));
                    }
                    break;
                }

                if (mTask.equals("switch_lfd")) {
                    showProgressDialog(getString(R.string.loading),
                            getString(R.string.switching_lfd));

                    int ret = mFingerprintScanner.setLfdLevel(mLfdLevel);
                    if (ret == FingerprintScanner.RESULT_OK) {
                        showInformation(getString(R.string.switch_lfd_success), null);
                    } else {
                        showError(getString(R.string.switch_lfd_failed), null);
                    }
                    break;
                }

                if (mTask.equals("clear_database")) {
                    showProgressDialog(getString(R.string.loading), getString(R.string.clearing));

                    int error = Bione.clear();
                    if (error == Bione.RESULT_OK) {
                        showInformation(getString(R.string.clear_fingerprint_database_success),
                                null);
                    } else {
                        showError(getString(R.string.clear_fingerprint_database_failed),
                                getErrorString(error));
                    }
                    break;
                }
            } while (false);

            updateTimeInformations(captureTime, extractTime, generalizeTime, verifyTime);
            enableButtons(true);
            dismissProgressDialog();
        }
    }
}
