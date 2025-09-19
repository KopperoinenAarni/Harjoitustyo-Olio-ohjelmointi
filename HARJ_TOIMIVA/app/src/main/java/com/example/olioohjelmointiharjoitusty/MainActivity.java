//Read me
//This class is created for switching between fragments.

package com.example.olioohjelmointiharjoitusty;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.olioohjelmointiharjoitusty.ShowData.InformationFragment;
import com.example.olioohjelmointiharjoitusty.quiz.QuizFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ImageView appIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        appIcon = findViewById(R.id.appIcon);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Haku");
            } else if (position == 1) {
                tab.setText("Tiedot");
            } else if (position == 2) {
                tab.setText("Visa");
            } else if (position == 3) {
                tab.setText("Vertaa");
            }
        }).attach();
    }


    public void switchToTab(int index) {
        viewPager.setCurrentItem(index, true);
    }
}
