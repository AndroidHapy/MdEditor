package blxt.qandroid.base.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blxt.quickactivity.AbstractApplication;
import com.blxt.quickactivity.AbstractViewHolder;
import com.blxt.utils.check.CheckUtils;

import blxt.qandroid.base.DataPool;
import blxt.qandroid.base.R;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class AppInfoViewModel extends AbstractViewHolder implements View.OnClickListener {

    private TextView tvAppInfo;

    private TextView tvAppCopyright;
    private TextView tvFilePath;
    private Button btnChoose;

    Activity activity;

    public AppInfoViewModel(Activity activity, @NonNull View view) {
        super(view);
        this.activity = activity;

        tvAppInfo =  findViewById(R.id.tv_app_info);
        tvAppCopyright =  findViewById(R.id.tv_app_copyright);
        tvFilePath = (TextView) findViewById(R.id.tv_file_path);

        btnChoose = (Button) findViewById(R.id.btn_choose);

        addOnClickListener();
    }

    public void init(){

        // 显示版本号
        PackageManager pm = getContext().getPackageManager();// 获得包管理器
        PackageInfo pi = null;// 得到该应用的信息，即主Activity
        try {
            pi = pm.getPackageInfo(getContext().getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            String versionName = pi.versionName == null ? "null"
                    : pi.versionName;
            String versionCode = pi.versionCode + "";

            String tips = "\n版本号" + versionCode + ":"
                    + versionName + (AbstractApplication.isDebugVersion(getContext()) ? "\n(Debug)" : "");

            tvAppInfo.append(tips);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void onRes(){
    }

    private void addOnClickListener(){
        tvAppCopyright.setOnClickListener(this);
        btnChoose.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        String path = (String) DataPool.getInstance().get("currentFilePath_old");
        if(CheckUtils.isEmpty(path)) {
            path = "";
        }

        tvFilePath.setText(path);
    }
    public static final int FILE_SELECT_CODEB = 202;
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.tv_app_copyright){
            openWeb("https://github.com/AndroidHapy/MdEditor", getContext());
        }
        else if(R.id.btn_choose == v.getId()){
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");//设置类型
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                startActivityForResult(activity, Intent.createChooser(intent, "选择文件"), FILE_SELECT_CODEB, null);
            } catch (android.content.ActivityNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 打开网页
     * @param path
     * @param context
     */
    public static void openWeb(String path, Context context){
        Uri uri = Uri.parse(path);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }
}