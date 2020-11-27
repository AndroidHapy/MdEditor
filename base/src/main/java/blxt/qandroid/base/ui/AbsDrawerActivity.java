package blxt.qandroid.base.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.blxt.quickactivity.QBaseActivity;

import blxt.qandroid.base.R;


/**
 * 带侧滑抽屉的active
 */
public abstract class AbsDrawerActivity extends QBaseActivity {
    private DrawerLayout mDrawerLayout;
    /** 抽屉的Frame  */
    private FrameLayout contentFrameLayout;
    CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_activity);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setScrimColor(Color.TRANSPARENT); // 菜单滑动时content不被阴影覆盖

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(""); // 不显示程序应用名
        toolbar.setNavigationIcon(R.drawable.ic_permiss); // 在toolbar最左边添加icon
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 表示从左边打卡开
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        cardView = findViewById(R.id.card_view);

        contentFrameLayout =  findViewById(R.id.content_view);

        // 监听抽屉的滑动事件
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View mContent = mDrawerLayout.getChildAt(0);
                View mMenu = drawerView;
                float scale = 1 - slideOffset;
                float rightScale = 0.8f + scale * 0.2f;
                float leftScale = 0.5f + slideOffset * 0.5f;
                mMenu.setAlpha(leftScale);
                mMenu.setScaleX(leftScale);
                mMenu.setScaleY(leftScale);
                mContent.setPivotX(0);
                mContent.setPivotY(mContent.getHeight() * 1/2);
                mContent.setScaleX(rightScale);
                mContent.setScaleY(rightScale);
                mContent.setTranslationX(mMenu.getWidth() * slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                cardView.setRadius(50); // 拉开菜单时，主内容视图的边缘能呈现出卡片式的圆角和阴影
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                cardView.setRadius(0); // 菜单关闭，圆角消失
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

       // replaceFragment(new HomeFragment());

    }

    public void replaceFragmentNav(Fragment fragment) { // 动态加载fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.nav_view, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void replaceFragmentContent(Fragment fragment) { // 动态加载fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content_view, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
