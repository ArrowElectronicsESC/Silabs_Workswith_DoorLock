package com.siliconlabs.eic.doorlock.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.siliconlabs.eic.doorlock.R;
import com.siliconlabs.eic.doorlock.adapters.ViewPagerAdapter;
import com.siliconlabs.eic.doorlock.dialogs.AboutMappingsDictionaryDialog;
import com.siliconlabs.eic.doorlock.fragment.CharacteristicMappingsFragment;
import com.siliconlabs.eic.doorlock.fragment.ServiceMappingsFragment;

public class MappingDictionaryActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView charactertisticsTabTV;
    private TextView servicesTabTV;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mappings);

        toolbar = findViewById(R.id.toolbar);
        charactertisticsTabTV = findViewById(R.id.characteristic_tab_textview);
        servicesTabTV = findViewById(R.id.services_tab_textview);
        viewPager = findViewById(R.id.view_pager);

        setupViewPager(viewPager);
        initViewPagerBehavior(viewPager);

        setSupportActionBar(toolbar);

        charactertisticsTabTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTabSelected(charactertisticsTabTV);
                setTabUnselected(servicesTabTV);
                viewPager.setCurrentItem(0);
            }
        });

        servicesTabTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTabSelected(servicesTabTV);
                setTabUnselected(charactertisticsTabTV);
                viewPager.setCurrentItem(1);
            }
        });

        findViewById(R.id.go_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mappings_dictionary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mappings_about:
                AboutMappingsDictionaryDialog dialog = new AboutMappingsDictionaryDialog();
                dialog.show(getSupportFragmentManager(), "about_mappings_dictionary_dialog");
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // Set Services textview width same as Characteristics
        servicesTabTV.setWidth(charactertisticsTabTV.getWidth());
    }

    private void initViewPagerBehavior(ViewPager viewPager) {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0) {
                    setTabSelected(charactertisticsTabTV);
                    setTabUnselected(servicesTabTV);
                } else if (position == 1) {
                    setTabSelected(servicesTabTV);
                    setTabUnselected(charactertisticsTabTV);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void setTabSelected(TextView textView) {
        textView.setBackground(ContextCompat.getDrawable(MappingDictionaryActivity.this, R.drawable.rounded_button_white));
        textView.setTextColor(ContextCompat.getColor(MappingDictionaryActivity.this, R.color.silabs_red));
        textView.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
    }

    private void setTabUnselected(TextView textView) {
        textView.setBackground(ContextCompat.getDrawable(MappingDictionaryActivity.this, R.drawable.rounded_button_red_dark));
        textView.setTextColor(ContextCompat.getColor(MappingDictionaryActivity.this, R.color.silabs_white));
        textView.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CharacteristicMappingsFragment(), getString(R.string.title_Characteristics));
        adapter.addFragment(new ServiceMappingsFragment(), getString(R.string.title_Services));
        viewPager.setAdapter(adapter);

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

}
