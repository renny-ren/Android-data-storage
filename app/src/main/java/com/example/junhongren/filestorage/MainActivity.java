package com.example.junhongren.filestorage;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Button mPublicExternal = null;
    private Button mPrivateExternal = null;
    private Button mCheckExternal = null;
    private Button mDelete = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPublicExternal = (Button)findViewById(R.id.external_public);
        mPublicExternal.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ExternalStoragePublic("mypublicfile");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mPrivateExternal = (Button)findViewById(R.id.external_private);
        mPrivateExternal.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ExternalStoragePrivate(getApplicationContext(), "myfile");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mCheckExternal = (Button)findViewById(R.id.check_external_storage);
        mCheckExternal.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isExternalStorageWritable();
            }
        });

        mDelete = (Button)findViewById(R.id.delete_file);
        mDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFile();
            }
        });
    }

    // 保存到内部存储
    public File saveToInternalStorage(View view) {
        File fileDir = getFilesDir();
        String filename = "test_file";
        String string = "Hello nihao!";
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("======存储成功，位置为" + fileDir + "======");
        return fileDir;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mCheckExternal.setText("是");
        }else{
            mCheckExternal.setText("否");
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    // 以public形式保存到外部存储
    public File ExternalStoragePublic(String albumName) throws IOException {
        if (isStoragePermissionGranted()) {
            // Get the directory for the user's public pictures directory.
            File f = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), albumName);
            if (!f.exists()) {
                f.mkdirs();
                System.out.println(f + "======");
            }
            String fileName = "external_public_file";
            File file = new File(f, fileName);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                    System.out.println("======存储成功，位置为" + file + "======");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return file;
        }
        return new File("null");
    }

    // 以private形式保存到内部存储
    public File ExternalStoragePrivate(Context context, String albumName) throws IOException {
        // Get the directory for the app's private pictures directory.
        File f = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!f.exists()) {
            f.mkdirs();
        }
        String fileName = "external_private_file";
        File file = new File(f, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
                System.out.println("======存储成功，位置为" + file + "======");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public boolean deleteFile(){
        File file = new File("/data/user/0/com.example.junhongren.filestorage/files/test_file");
        File externalFile = new File("storage/emulated/0/Android/data/com.example.junhongren.filestorage/files/Pictures/myfile/external_private_file");
        File publicFile = new File("/storage/emulated/0/Pictures/mypublicfile/external_public_file");
        if (file.exists() && externalFile.exists() && publicFile.exists()) {
            if (file.delete() && externalFile.delete() && publicFile.delete()) {
                System.out.println("删除成功");
                return true;
            } else {
                System.out.println("删除失败~");
                return false;
            }
        }else {
            System.out.println("文件不存在！");
            return false;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            System.out.println("Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
            try {
                ExternalStoragePublic("mypublicfile");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isStoragePermissionGranted() {
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            System.out.println("permission is granted");
            return true;
        }else {
            System.out.println("permission is denied");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return false;
        }
    }
}
