package blxt.qandroid.base.ui;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import blxt.qandroid.base.R;


public class AppInfoFragment extends Fragment {

    private AppInfoViewModel mViewModel;

    public static AppInfoFragment newInstance() {
        return new AppInfoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.app_info_fragment, container, false);
        mViewModel = new AppInfoViewModel(getActivity(), view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
        mViewModel.init();
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.onResume();
    }


}