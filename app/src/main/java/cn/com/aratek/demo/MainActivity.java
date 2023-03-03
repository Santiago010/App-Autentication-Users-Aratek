package cn.com.aratek.demo;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.com.aratek.demo.utils.SharedPreferencesUtils;
import cn.com.aratek.dev.Terminal;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {


    private static final String[] PERMISSION_LIST = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private static final int REQUEST_PERMISSIONS_CODE = 0;
    private static final int REQUEST_IMPORT_CONFIGURATIONS = 1;
    private static final String TERMINAL_CONFIGURATIONS_FILENAME = "terminal-configurations.xml";

    private PackageManager mPackageManager;
    private SharedPreferences mSharedPreferences;
    private List<ComponentName> mDemoActivities;
    private GridView mGrid;
    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mDemoActivities.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.text_icon, container, false);
            }

            try {
                ActivityInfo info = mPackageManager
                        .getActivityInfo(mDemoActivities.get(position), 0);

                ImageView imageView = convertView.findViewById(R.id.icon_image);
                imageView.setImageResource(info.icon);

                TextView textView = convertView.findViewById(R.id.icon_text);
                textView.setText(info.labelRes);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return convertView;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPackageManager = getPackageManager();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mDemoActivities = new ArrayList<>();

        sharedPreferencesInit();

        mGrid = findViewById(R.id.main_grid);
        mGrid.setAdapter(mAdapter);
        mGrid.setOnItemClickListener(this);


        try (FileInputStream fis = new FileInputStream(new File(getFilesDir(),
                TERMINAL_CONFIGURATIONS_FILENAME))) {
            Terminal.loadSettings(fis);
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            Toast.makeText(this, R.string.configuration_file_load_failed,
                    Toast.LENGTH_SHORT).show();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, PERMISSION_LIST, REQUEST_PERMISSIONS_CODE);
        }

        String message = getIntent().getStringExtra("INFOFP");
        setTitle("Autenticar a: " + message);

    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshDemoActivities();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        for (int result : grantResults) {
            if (PackageManager.PERMISSION_GRANTED != result) {
                Toast.makeText(this,
                        R.string.permission_denied_info, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMPORT_CONFIGURATIONS && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    try (InputStream is = getContentResolver().openInputStream(uri)) {
                        byte[] buffer = new byte[is.available()];
                        is.read(buffer);
                        Terminal.loadSettings(buffer);
                        try (FileOutputStream fos = new FileOutputStream(new File(getFilesDir(),
                                TERMINAL_CONFIGURATIONS_FILENAME))) {
                            fos.write(buffer);
                        } catch (IOException e) {
                            Toast.makeText(this, R.string.configuration_file_save_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (FileNotFoundException e) {
                        Toast.makeText(this, R.string.configuration_file_not_exist,
                                Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(this, R.string.configuration_file_load_failed,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.import_configurations:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("text/xml");
                startActivityForResult(intent, REQUEST_IMPORT_CONFIGURATIONS);
                return true;
            case R.id.settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;
            case R.id.about:
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.setComponent(mDemoActivities.get(position));
        startActivity(intent);
    }

    private void sharedPreferencesInit() {
        Set<String> demos = mSharedPreferences.getStringSet(
                SharedPreferencesUtils.DISPLAYED_DEMO_KEY, null);
        if (demos == null) {
            demos = new HashSet<>();
            String[] values = getResources().getStringArray(R.array.displayed_demo_values);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            for (String value : values) {
                demos.add(value);
            }
            editor.putStringSet(SharedPreferencesUtils.DISPLAYED_DEMO_KEY, demos);
            editor.commit();
        }
    }

    private void refreshDemoActivities() {
        mDemoActivities.clear();

        String[] values = getResources().getStringArray(R.array.displayed_demo_values);

        Set<String> demos = mSharedPreferences.getStringSet(
                SharedPreferencesUtils.DISPLAYED_DEMO_KEY, null);
        if (demos == null) {
            demos = new HashSet<>();
            for (String value : values) {
                demos.add(value);
            }
        }

        for (String cls : values) {
            if (demos.contains(cls)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mDemoActivities.add(
                            ComponentName.createRelative(getApplicationContext(), cls));
                } else {
                    String pkg = getApplicationContext().getPackageName();

                    final String fullName;
                    if (cls.charAt(0) == '.') {
                        // Relative to the package. Prepend the package name.
                        fullName = pkg + cls;
                    } else {
                        // Fully qualified package name.
                        fullName = cls;
                    }
                    mDemoActivities.add(new ComponentName(pkg, fullName));
                }
            }
        }

        mAdapter.notifyDataSetChanged();
    }
}
