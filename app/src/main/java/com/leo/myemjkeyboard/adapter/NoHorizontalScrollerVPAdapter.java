package com.leo.myemjkeyboard.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by leo
 * Time：2019/1/22
 * Description: 最外层底部tab 和 fragment 的viewPager适配器
 */
public class NoHorizontalScrollerVPAdapter extends FragmentPagerAdapter {

    private List<Fragment> datas = null;

    public NoHorizontalScrollerVPAdapter(FragmentManager fm, List<Fragment> datas) {
        super(fm);
        this.datas = datas;

    }

    @Override
    public Fragment getItem(int position) {
        return datas.get(position);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // 这里Destroy的是Fragment的视图层次，并不是Destroy Fragment对象
        super.destroyItem(container, position, object);
    }
}
