package cj.tzw.base_uwb_m.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cj.tzw.base_uwb_m.utils.ConstantUtil;

public class PageAdapter extends PagerAdapter {
    private ArrayList<View> viewList;
    int deviceType = -1;

    public PageAdapter(ArrayList<View> viewList,int deviceType) {
        this.viewList = viewList;
        this.deviceType = deviceType;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = viewList.get(position);
        if(container!=null){
            container.addView(view);
        }
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(deviceType==1){
            return ConstantUtil.FORKLIFT_TAB[position];
        }else if(deviceType==2){
            return ConstantUtil.FIX_TAB[position];
        }
        return  ConstantUtil.TAB_VALS[position];
    }



}
