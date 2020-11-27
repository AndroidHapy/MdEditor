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

package ren.qinc.markdowneditors.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import androidx.core.widget.NestedScrollView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.blxt.quickfile4a.QFile4a;
import com.blxt.quicklog.QLog;
import com.blxt.utils.Converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import blxt.android.editormd.util.FilePathTools;
import blxt.qandroid.base.DataPool;
import ren.qinc.markdowneditors.base.BaseWebActivity;

/**
 * Markdown View
 * The type Markdown preview view.
 */
public class MarkdownPreviewView extends NestedScrollView {
    public WebView mWebView;
    private Context mContext;
    private OnLoadingFinishListener mLoadingFinishListener;
    private ContentListener mContentListener;
    String format;

    public MarkdownPreviewView(Context context) {
        super(context);
        init(context);
    }

    public MarkdownPreviewView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public MarkdownPreviewView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    private void init(Context context) {
        if (!isInEditMode()) {
            this.mContext = context;
//            setOrientation(VERTICAL);
            if (VERSION.SDK_INT >= 21) {
                WebView.enableSlowWholeDocumentDraw();
            }
            this.mWebView = new WebView(this.mContext);
            this.mWebView.getSettings().setJavaScriptEnabled(true);
            this.mWebView.setVerticalScrollBarEnabled(false);
            this.mWebView.setHorizontalScrollBarEnabled(false);
            this.mWebView.addJavascriptInterface(new JavaScriptInterface(this), "handler");
            this.mWebView.setWebViewClient(new MdWebViewClient(this));
            this.mWebView.loadUrl("file:///android_asset/markdown.html");
            addView(this.mWebView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }
        format = QFile4a.Assets.getStr4Assets(getContext(), "index.html");
    }


    // 从本地文件加预览 md
    public void showMdFromFile(String filePath){
        String mdStr = null;
       // filePath = FilePathTools.getRealPath(filePath);
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
        String mdCache = QLog.PATH.getAppCachePath(getContext()) + "/" + filePath.hashCode() + file.getName() + "/_" + file.getName() + ".html";
        // html文件
        File fileHtml = new File(mdCache);
        // 创建历史文件夹
        QFile4a.MFolder.createDirectory(fileHtml.getParent());

        String mdHtml = String.format(format, file.getName(), file.getName(), mdStr);
        QFile4a.Write.save(fileHtml, mdHtml);
        mWebView.loadUrl( "file:///" + mdCache);
    }

    // 直接从文本编辑器里面加载
    public void showMdFromStr(String mdStr){
        // 文件路径
        String filePath =  (String) DataPool.getInstance().get("file_select");
        // 文件标题
        String title = "null";
        // 文件缓存路径
        String cachePath = QLog.PATH.getAppCachePath(getContext());

        if(filePath == null){ // 没有文件的话,就用默认的
            cachePath +=  "/" + "00_default/_default.html";
        }
        else{   // 创建对应文件夹
            title = new File(filePath).getName();
            cachePath +=  "/" + filePath.hashCode() + title + "/_" + title + ".html";
        }

        // html文件
        File fileHtml = new File(cachePath);
        // 创建历史文件夹
        QFile4a.MFolder.createDirectory(fileHtml.getParent());

        String mdHtml = String.format(format, title, title, mdStr);
        QFile4a.Write.save(fileHtml, mdHtml);
        mWebView.loadUrl( "file:///" + cachePath);
    }

    public final void parseMarkdown(String str, boolean z) {
        this.mWebView.loadUrl("javascript:parseMarkdown(\"" + str.replace("\n", "\\n").replace("\"", "\\\"").replace("'", "\\'") + "\", " + z + ")");
    }


    public void setContentListener(ContentListener contentListener) {
        this.mContentListener = contentListener;
    }

    public void setOnLoadingFinishListener(OnLoadingFinishListener loadingFinishListener) {
        this.mLoadingFinishListener = loadingFinishListener;
    }

    public interface ContentListener {
    }

    public interface OnLoadingFinishListener {
        void onLoadingFinish();
    }

    final class JavaScriptInterface {
        final MarkdownPreviewView a;

        private JavaScriptInterface(MarkdownPreviewView markdownPreviewView) {
            this.a = markdownPreviewView;
        }

        @JavascriptInterface
        public void none() {

        }
    }


    final class MdWebViewClient extends WebViewClient {
        final MarkdownPreviewView mMarkdownPreviewView;

        private MdWebViewClient(MarkdownPreviewView markdownPreviewView) {
            this.mMarkdownPreviewView = markdownPreviewView;
        }

        public final void onPageFinished(WebView webView, String str) {
            if (this.mMarkdownPreviewView.mLoadingFinishListener != null) {
                this.mMarkdownPreviewView.mLoadingFinishListener.onLoadingFinish();
            }
        }

        public final void onReceivedError(WebView webView, int i, String str, String str2) {
            new StringBuilder("onReceivedError :errorCode:").append(i).append("description:").append(str).append("failingUrl").append(str2);
        }

        public final boolean shouldOverrideUrlLoading(WebView webView, String url) {
            if (!TextUtils.isEmpty(url))
                BaseWebActivity.loadUrl(webView.getContext(), url, null);
            return true;
        }
    }

    /**
     * 截屏
     *
     * @return
     */
    public Bitmap getScreen() {
        Bitmap bmp = Bitmap.createBitmap(mWebView.getWidth(), mWebView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        mWebView.draw(canvas);
        return bmp;
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
