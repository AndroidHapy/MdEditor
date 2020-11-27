/*
 * Copyright 2016. SHENQINCI(沈钦赐)<dev@qinc.me>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ren.qinc.markdowneditors.view;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;

import com.blxt.quickfile4a.QFile4a;
import com.blxt.quicklog.QLog;
import com.blxt.utils.check.CheckUtils;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;

import java.io.File;

import blxt.android.editormd.util.ZipUtils;
import ren.qinc.markdowneditors.AppContext;
import ren.qinc.markdowneditors.R;
import ren.qinc.markdowneditors.base.BaseDrawerLayoutActivity;
import ren.qinc.markdowneditors.base.BaseFragment;
import ren.qinc.markdowneditors.utils.Toast;

/**
 * The type Main activity.
 */
public class MainActivity extends BaseDrawerLayoutActivity {
    private BaseFragment mCurrentFragment;


    private int currentMenuId;


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    /**
     *  阴影的高度
     */
    @Override
    protected float getElevation() {
        return 0;
    }

    @Override
    public void onCreateAfter(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            setDefaultFragment(R.id.content_fragment_container);
        }

        String htmlPath = QLog.PATH.getAppFilesPath(this);

        boolean fal = CheckUtils.isEmpty(new File(htmlPath + "/edit/version.txt"));

        if(fal) {
            QLog.i("初始化解压:{}", htmlPath);
            try {
                //   ZipUtils.UnZipAssetsFolder(this, "mdEditorer.zip", htmlPath + "/edit");
                ZipUtils.unZip(this,  "mdEditorer.zip", htmlPath + "/edit", true);

                QFile4a.Assets.copyFile4Asstes(this, "index.html", new File(htmlPath + "/index.html"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 检查更新
        //initUpdate(false);
        if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED
        ) {
            verifyStoragePermissions(this);
        }

    }

    private void setDefaultFragment(@IdRes int fragmentId) {
        mCurrentFragment = new FolderManagerFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(fragmentId, mCurrentFragment)
                .commit();
    }

    @Override
    public void initData() {
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.localhost) {//|| id == R.id.other
            if (id == currentMenuId) {
                return false;
            }
            currentMenuId = id;
            getDrawerLayout().closeDrawer(GravityCompat.START);
            return true;
        }

        if (onOptionsItemSelected(item)) {
            getDrawerLayout().closeDrawer(GravityCompat.START);
        }
        return false;
    }

    @Override
    protected int getDefaultMenuItemId() {
        currentMenuId = R.id.localhost;
        return currentMenuId;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_helper:
                CommonMarkdownActivity.startHelper(this);
                return true;
            case R.id.menu_about:
                AboutActivity.startAboutActivity(this);
                return true;
            case R.id.menu_update:
                initUpdate(true);
                return true;
            case R.id.other:
                AppContext.showSnackbar(getWindow().getDecorView(), "敬请期待");
                return true;
                default:break;
        }
        return super.onOptionsItemSelected(item);// || mCurrentFragment.onOptionsItemSelected(item);
    }

    private long customTime = 0;

    /**
     * 返回按钮
     */
    @Override
    public void onBackPressed() {
        if (getDrawerLayout().isDrawerOpen(GravityCompat.START)) {//侧滑菜单打开，关闭菜单
            getDrawerLayout().closeDrawer(GravityCompat.START);
            return;
        }

        if (mCurrentFragment != null && mCurrentFragment.onBackPressed()) {//如果Fragment有处理，则不据需执行
            return;
        }

        //没有东西可以返回了，剩下软件退出逻辑
        if (Math.abs(customTime - System.currentTimeMillis()) < 2000) {
            finish();
        } else {// 提示用户退出
            customTime = System.currentTimeMillis();
            Toast.showShort(mContext, "再按一次退出软件");
        }
    }


    /**
     * 更新检查
     * @param isShow
     */
    private void initUpdate(boolean isShow) {
        PgyUpdateManager.register(MainActivity.this,
                new UpdateManagerListener() {
                    @Override
                    public void onUpdateAvailable(final String result) {
                        final AppBean appBean = getAppBeanFromString(result);

                        if (appBean.getReleaseNote().startsWith("####")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.DialogTheme);
                            builder
                                    .setTitle("当前版本已经停用了")
                                    .setCancelable(false)
                                    .setMessage("更新到最新版?")
                                    .setNegativeButton("取消", (dialog, which) -> {
                                       finish();
                                    })
                                    .setPositiveButton("确定", (dialog1, which) -> {
                                        startDownloadTask(
                                                MainActivity.this,
                                                appBean.getDownloadURL());
                                        dialog1.dismiss();
                                    }).show();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.DialogTheme);
                            builder
                                    .setTitle("更新")
                                    .setMessage(appBean.getReleaseNote() + "")
                                    .setNegativeButton("先不更新", (dialog, which) -> {
                                        dialog.dismiss();
                                    })
                                    .setPositiveButton("更新", (dialog1, which) -> {
                                        startDownloadTask(
                                                MainActivity.this,
                                                appBean.getDownloadURL());
                                        dialog1.dismiss();
                                    }).show();

                        }
                    }

                    @Override
                    public void onNoUpdateAvailable() {
                        if (isShow) {
                            android.widget.Toast.makeText(application, "已经是最新版", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permission,
                                           int[] grantResults) {
        //requestCode就是requestPermissions()的第三个参数
        //permission就是requestPermissions()的第二个参数
        //grantResults是结果，0调试通过，-1表示拒绝
        if(requestCode == REQUEST_EXTERNAL_STORAGE){
            QLog.i("权限授权结果{},{}", grantResults, permission);
            int res = 0;
            for(int i : grantResults){
                res += i;
            }
//            if(res == 0){
//                startActivity(PreviewActivity.class);
//            }
        }
    }


    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};


}
