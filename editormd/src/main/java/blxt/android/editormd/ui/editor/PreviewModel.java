package blxt.android.editormd.ui.editor;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;

import com.blxt.qfile.QFile;
import com.blxt.quickfile4a.QFile4a;
import com.blxt.quicklog.QLog;
import com.blxt.utils.Converter;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

import androidx.annotation.NonNull;

import com.blxt.quickactivity.AbstractViewHolder;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.WebViewClient;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import blxt.android.editormd.R;
import blxt.android.editormd.util.FilePathTools;


public class PreviewModel extends AbstractViewHolder {

    private WebView webPreview;
    private AVLoadingIndicatorView aviLogin;


    private String htmlPath;
    String format;

    public PreviewModel(@NonNull View view) {
        super(view);

        webPreview = findViewById(R.id.web_preview);
        aviLogin = (AVLoadingIndicatorView) findViewById(R.id.aviLogin);

        init();
        htmlPath = QLog.PATH.getAppFilesPath(getContext());

        String url = "file:///" + htmlPath + "/index.html";
        webPreview.loadUrl(url);

        format = QFile4a.Assets.getStr4Assets(getContext(), "index.html");
        readme();

    }

    private void init(){

        aviLogin.setVisibility(View.GONE);

        //非wifi情况下，主动下载x5内核
        QbSdk.setDownloadWithoutWifi(true);
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
            }

            @Override
            public void onCoreInitFinished() {

            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getContext(), cb);

        webPreview.getSettings().setJavaScriptEnabled(true);//支持js脚本
        webPreview.getSettings().setSupportZoom(false); //支持缩放，默认为true。是下面那个的前提。
        webPreview.getSettings().setBuiltInZoomControls(false); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webPreview.getSettings().setDisplayZoomControls(true); //隐藏原生的缩放控件
        webPreview.getSettings().setBlockNetworkImage(false);//解决图片不显示
        webPreview.getSettings().setLoadsImagesAutomatically(true); //支持自动加载图片
        webPreview.getSettings().setDefaultTextEncodingName("utf-8");//设置编码格式

        webPreview.getSettings().setPluginsEnabled(true) ;//支持插件
        // webView.getSettings().setUserWideViewPort(false) ;//将图片调整到适合webview的大小
        // webView.getSettings().setLayoutAlgorithm(LayoutAlgrithm.SINGLE_COLUMN) ;//支持内容从新布局
        // supportMultipleWindows() ;//多窗口
        // setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK) ;//关闭webview中缓存
        webPreview.getSettings().setAllowFileAccess(true) ;//设置可以访问文件
        //  setNeedInitialFocus(true) ;//当webview调用requestFocus时为webview设置节点
        //webPreview.getSettings().setjavaScriptCanOpenWindowsAutomatically(true) ;//支持通过JS打开新窗口
        //  webView.getSettings().setBuiltInZoomControls(false);//bu支持缩放
        //  webView.setInitialScale(35);//设置缩放比例
        //  webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);//设置滚动条隐藏
        webPreview.getSettings().setGeolocationEnabled(true);//启用地理定位
        // webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);//设置渲染优先级
        webPreview.getSettings().setDomStorageEnabled(true); // //设置DOM Storage缓存
        webPreview.setWebContentsDebuggingEnabled(true); // 开启 Chrome DevTools 远程调试

        //该界面打开更多链接
        webPreview.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                webView.loadUrl(s);
                return true;
            }

            @Override
            public void onPageStarted(WebView var1, String var2, Bitmap var3) {
                super.onPageStarted(var1, var2, var3);
                aviLogin.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                QLog.i("加载完成{}", url);
                aviLogin.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView var1, int var2, String var3, String var4) {
                super.onReceivedError(var1, var2, var3, var4);
                QLog.i("加载失败{}, {}, {}", var2, var3, var4);
                aviLogin.setVisibility(View.GONE);
            }

        });
        //监听网页的加载进度
        webPreview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webView, int i) {
              //  QLog.i("进度{}", i + "");
            }


            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                QLog.i("输入框拦截{}", url);
                return true;
            }

            // 拦截JS的警告框
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            // 拦截JS的确认框
            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }

            //For Android  >= 5.0
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                QLog.i("文件选择框拦截{}", fileChooserParams);
                return true;
            }


            //For Android  >= 4.1
            public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
                QLog.i("文件选择框拦截{}, {}", acceptType, capture);
            }

        });

        // 按键监听
        webPreview.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    //按返回键操作并且能回退网页
                    if (keyCode == KeyEvent.KEYCODE_BACK && webPreview.canGoBack()) {
                        //后退
                        webPreview.goBack();
                        return true;
                    }
                }
                return false;
            }
        });


    }

    private void readme(){

        String mdStr = QFile4a.Assets.getStr4Assets(getContext(), "readme.md");
        String mdHtml = String.format(format, "readme", "readme", mdStr);
        String mdCache = QLog.PATH.getAppCachePath(getContext()) + "/readme/readme.html";
        // html文件
        File fileHtml = new File(mdCache);
        // 创建历史文件夹
        QFile4a.MFolder.createDirectory(fileHtml.getParent());

        QFile4a.Write.save(fileHtml, mdHtml);
        webPreview.loadUrl( "file:///" + mdCache);
    }

    // 从本地文件加预览 md
    public void showMdFromFile(String filePath){
        String mdStr = null;
        filePath = FilePathTools.getRealPath(filePath);
        File file = new File(filePath);
        try {
            mdStr = getStr(file);
        } catch (IOException e) {
            mdStr = "请给予文件读写权限,否则无法查看本地文件";
            String error =  e.getMessage();
            QLog.i("文件读取失败{}", error);
            e.printStackTrace();
        }

        // 创建对应文件夹
        String mdCache = QLog.PATH.getAppCachePath(getContext()) + "/" + filePath.hashCode() + file.getName() + "/_" + file.getName() + Converter.getTimeStr(".yyyy-MM-dd_HH:mm:ss")+ ".html";
        // html文件
        File fileHtml = new File(mdCache);
        // 创建历史文件夹
        QFile4a.MFolder.createDirectory(fileHtml.getParent());

        String mdHtml = String.format(format, file.getName(), file.getName(), mdStr);
        QFile4a.Write.save(fileHtml, mdHtml);
        webPreview.loadUrl( "file:///" + mdCache);
    }

    @Override
    public void onResume() {

    }

    public static String getStr(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(file.getPath()));
        char[] buff = new char[1024];

        while(br.read(buff) != -1) {
            sb.append(buff);
            buff = new char[1024];
        }

        return sb.toString();
    }

}