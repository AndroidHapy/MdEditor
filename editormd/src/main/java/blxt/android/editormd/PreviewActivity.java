package blxt.android.editormd;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;

import com.blxt.quicklog.QLog;

import blxt.qandroid.base.ui.AppInfoFragment;
import blxt.android.editormd.ui.editor.PreviewFragment;
import blxt.qandroid.base.DataPool;
import blxt.qandroid.base.ui.AbsDrawerActivity;

import static android.content.ContentResolver.SCHEME_FILE;
import static blxt.qandroid.base.ui.AppInfoViewModel.FILE_SELECT_CODEB;

public class PreviewActivity extends AbsDrawerActivity {

    private String currentFilePath;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.previewactivity);
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.container, PreviewFragment.newInstance().setCallbckHandler(handler))
//                    .commitNow();
//        }
//
//        hideActionBar();
//        onCreateAfter(savedInstanceState);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        replaceFragmentContent(new PreviewFragment());

        replaceFragmentNav(new AppInfoFragment());
        onCreateAfter(savedInstanceState);
    }

    public void onCreateAfter(Bundle savedInstanceState) {

        getIntentData(savedInstanceState);
    }


    private void getIntentData(Bundle bundle) {
        Intent intent = this.getIntent();
        int flags = intent.getFlags();
        if ((flags & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == 0) {
            if (intent.getAction() != null && Intent.ACTION_VIEW.equals(intent.getAction())) {
                if (SCHEME_FILE.equals(intent.getScheme())) {
                    //文件
                    String type = getIntent().getType();
                    // mImportingUri=file:///storage/emulated/0/Vlog.xml
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri uri = intent.getData();

                    if (uri != null && SCHEME_FILE.equalsIgnoreCase(uri.getScheme())) {
                        //这是一个文件
                        currentFilePath = uri2FilePath(getBaseContext(), uri);
                        QLog.i("打开文件{}", currentFilePath);
                        DataPool.getInstance().put("currentFilePath", currentFilePath);
                    }
                }
            } else {
                QLog.i("打开文件未知");
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FILE_SELECT_CODEB) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    //高于API19版本
                    String[] split = data.getData().getPath().split("\\:");
                    String p = "";
                    //if (split.length >= 2)
                    {
                        p = Environment.getExternalStorageDirectory() + "/" + split[1];
                        QLog.i(p);
                        boolean mainthread = Looper.getMainLooper() == Looper.myLooper();
                        QLog.i(mainthread + "");
                        // new ReadFileTask().execute(p);
                    }
                } else {
                    //低于API19版本
                    Uri uri = data.getData();
                    QLog.i("文件路径{}", uri.getPath());
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    /**
     * Try to return the absolute file path from the given Uri
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String uri2FilePath(final Context context, final Uri uri) {
        if (null == uri) {
            return null;
        }
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }


}