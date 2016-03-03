package sremind.torymo.by;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

import io.karim.MaterialTabs;
import io.karim.Utils;

public class MainActivity extends AppCompatActivity {

	Toolbar mToolbar;
	MaterialTabs mMaterialTabs;
	ViewPager mViewPager;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mToolbar = (Toolbar)findViewById(R.id.toolbar);
		mMaterialTabs = (MaterialTabs)findViewById(R.id.material_tabs);
		mViewPager = (ViewPager)findViewById(R.id.view_pager);

		setSupportActionBar(mToolbar);

		SeriesPagerAdapter adapter = new SeriesPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(adapter);

		mMaterialTabs.setViewPager(mViewPager);

		mMaterialTabs.setOnTabSelectedListener(new MaterialTabs.OnTabSelectedListener() {
			@Override
			public void onTabSelected(int position) {

			}
		});

		mMaterialTabs.setOnTabReselectedListener(new MaterialTabs.OnTabReselectedListener() {
			@Override
			public void onTabReselected(int position) {

			}
		});

		applyParametersFromIntentExtras();

		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
		mViewPager.setPageMargin(pageMargin);
	}

	private void applyParametersFromIntentExtras() {
		Intent intent = getIntent();
		if (intent != null) {
			Bundle extras = intent.getExtras();
			if (extras != null) {
				Resources resources = getResources();

				int showToolbar = View.VISIBLE;
				int indicatorColor = resources.getColor(R.color.accent);
				int underlineColor = resources.getColor(R.color.accent);
				int indicatorHeightDp = 3;
				int underlineHeightDp = 3;
				int tabPaddingDp = 3;

				mToolbar.setVisibility(showToolbar);

				mMaterialTabs.setIndicatorColor(indicatorColor);
				mMaterialTabs.setUnderlineColor(underlineColor);
				mMaterialTabs.setIndicatorHeight(Utils.dpToPx(resources, indicatorHeightDp));
				mMaterialTabs.setUnderlineHeight(Utils.dpToPx(resources, underlineHeightDp));
				mMaterialTabs.setTabPaddingLeftRight(Utils.dpToPx(resources, tabPaddingDp));


				boolean paddingMiddle = true;
				boolean sameWeightTabs = true;
				boolean textAllCaps = true;

				mMaterialTabs.setPaddingMiddle(paddingMiddle);
				mMaterialTabs.setSameWeightTabs(sameWeightTabs);
				mMaterialTabs.setAllCaps(textAllCaps);

				int toolbarColor = resources.getColor(R.color.primary);
				int tabBackgroundColor = resources.getColor(R.color.primary);
				mToolbar.setBackgroundColor(toolbarColor);
				mMaterialTabs.setBackgroundColor(tabBackgroundColor);

				/*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					Window window = getWindow();
					window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
					window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
					window.setStatusBarColor(Color.argb(Color.alpha(toolbarColor), Color.red(toolbarColor) / 2, Color.green(toolbarColor) / 2,
							Color.blue(toolbarColor) / 2));
				}*/

				int textColorSelected = resources.getColor(R.color.text_primary);
				int textColorUnselected = resources.getColor(R.color.text_primary);
				//int tabStyleSelected = extras.getInt(TabsSettingsFragment.TEXT_STYLE_SELECTED);
				//int tabStyleUnselected = extras.getInt(TabsSettingsFragment.TEXT_STYLE_UNSELECTED);

				mMaterialTabs.setTextColorSelected(textColorSelected);
				mMaterialTabs.setTextColorUnselected(textColorUnselected);

				//mMaterialTabs.setTabTypefaceSelectedStyle(tabStyleSelected);
				//mMaterialTabs.setTabTypefaceUnselectedStyle(tabStyleUnselected);

				int rippleDuration = 250;
				float rippleAlphaFloat = 0.2f;
				int rippleColor = resources.getColor(R.color.primary_dark);
				boolean rippleDelayClick = false;
				float rippleDiameterDp = 20.0f;
				int rippleFadeDuration = 100;
				int rippleHighlightColor = R.color.primary_dark;
				boolean rippleOverlay = false;
				boolean ripplePersistent = false;
				int rippleRoundedCornusRadiusDp = 0;

				mMaterialTabs.setRippleDuration(rippleDuration);
				mMaterialTabs.setRippleAlphaFloat(rippleAlphaFloat);
				mMaterialTabs.setRippleColor(rippleColor);
				mMaterialTabs.setRippleDelayClick(rippleDelayClick);
				mMaterialTabs.setRippleDiameterDp(rippleDiameterDp);
				mMaterialTabs.setRippleFadeDuration(rippleFadeDuration);
				mMaterialTabs.setRippleHighlightColor(rippleHighlightColor);
				mMaterialTabs.setRippleInAdapter(false);
				mMaterialTabs.setRippleOverlay(rippleOverlay);
				mMaterialTabs.setRipplePersistent(ripplePersistent);
				mMaterialTabs.setRippleRoundedCornersDp(rippleRoundedCornusRadiusDp);
			}
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){

            default:
                return super.onOptionsItemSelected(item);
        }
    }

	public class SeriesPagerAdapter extends FragmentPagerAdapter {
		private final String[] TITLES = {getResources().getString(R.string.calendar), getResources().getString(R.string.series), getResources().getString(R.string.watchlist)};

		public SeriesPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position){
				case 0:
				default:
					return new CalendarFragment();
				case 1:
					return new SeriesFragment();
				case 2:
					return new WatchlistFragment();
			}
		}
	}
	


}
