package com.geocomply.techx_app.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.geocomply.techx_app.AbortedOrderFragment;
import com.geocomply.techx_app.AllOrderFragment;
import com.geocomply.techx_app.DeliveredOrderFragment;
import com.geocomply.techx_app.DeliveringOrderFragment;
import com.geocomply.techx_app.PendingOrderFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AllOrderFragment();
            case 1:
                return new PendingOrderFragment();
            case 2:
                return new DeliveringOrderFragment();
            case 3:
                return new DeliveredOrderFragment();
            case 4:
                return new AbortedOrderFragment();
            default:
                return new AllOrderFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
