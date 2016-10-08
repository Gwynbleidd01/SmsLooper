package app.sms.com.smslooper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {

    private CollectionPagerAdapter collectionPagerAdapter;
    private ViewPager viewPager;
    private List<Fragment> fragmentList;
    private PagerTabStrip pagerTabStrip;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pager_layout);

        LogWindow logWindow = new LogWindow();
        MainFragment mainFragment = new MainFragment();

        fragmentList = new ArrayList<>();
        fragmentList.add(mainFragment);
        fragmentList.add(logWindow);

        collectionPagerAdapter =
                new CollectionPagerAdapter(
                        getSupportFragmentManager() , this);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(collectionPagerAdapter);

        pagerTabStrip = (PagerTabStrip) findViewById(R.id.sliding_tabs);


    }

    public Fragment getFragment(int id)
    {
        return fragmentList.get(id);
    }

    public int getSizeOfFragmentList()
    {
        return fragmentList.size();
    }

    public String getTitle(int id){
        Title t = (Title)fragmentList.get(id);
        return t.getTitle();
    }

}
// Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
class CollectionPagerAdapter extends FragmentStatePagerAdapter {

    private MainActivity main;
    public CollectionPagerAdapter(FragmentManager fm , MainActivity main) {
        super(fm);
        this.main = main;
    }

    @Override
    public Fragment getItem(int i) {
        return main.getFragment(i);
    }

    @Override
    public int getCount() {
        return main.getSizeOfFragmentList();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return main.getTitle(position);
    }
}
