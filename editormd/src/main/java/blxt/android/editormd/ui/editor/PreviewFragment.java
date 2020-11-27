package blxt.android.editormd.ui.editor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blxt.qfile.QFile;
import com.blxt.quicklog.QLog;

import blxt.android.editormd.R;
import blxt.qandroid.base.DataPool;
import blxt.qandroid.base.ui.BaseFragment;

public class PreviewFragment extends BaseFragment {

    View view;
    private PreviewModel mViewModel;

    public static PreviewFragment newInstance() {
        return new PreviewFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.preview_fragment, container, false);
        mViewModel = new PreviewModel(view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String path = (String)DataPool.getInstance().poll("currentFilePath");
        if(path != null){
            QLog.i("接收{}", path);
            boolean isexe = QFile.MFile.isExists(path);
            if(!isexe){
                QLog.i("文件不存在{}", path);
                QLog.e("文件不存在{}", path);
                return;
            }
            mViewModel.showMdFromFile(path);
            DataPool.getInstance().put("currentFilePath_old", path);
        }

    }

    // 状态保存
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }



    @Override
    public boolean doMessage(Message paramMessage) {
        switch (paramMessage.what){
        }
        return false;
    }

}