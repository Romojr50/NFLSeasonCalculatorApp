package season.nfl.nflseasoncalculatorapp;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.MenuItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nfl.season.input.NFLFileReaderFactory;
import nfl.season.input.NFLTeamSettings;
import nfl.season.league.League;
import season.nfl.nflseasoncalculatorapp.fragments.AllTeamsFragment;
import season.nfl.nflseasoncalculatorapp.fragments.PlayoffsFragment;
import season.nfl.nflseasoncalculatorapp.fragments.SeasonFragment;
import season.nfl.nflseasoncalculatorapp.fragments.TeamFragment;
import season.nfl.nflseasoncalculatorapp.fragments.TeamsFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TeamsFragment.OnFragmentInteractionListener,
        SeasonFragment.OnFragmentInteractionListener, PlayoffsFragment.OnFragmentInteractionListener,
        AllTeamsFragment.OnFragmentInteractionListener, TeamFragment.OnFragmentInteractionListener{

    public static final String LEAGUE_KEY = "LEAGUE_NFL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        League nfl = new League(League.NFL);
        nfl.initializeNFL();

        Context context = getApplicationContext();
        File fileDir = context.getFilesDir();
        String folderPath = fileDir.getAbsolutePath();

        NFLFileReaderFactory fileReaderFactory = new NFLFileReaderFactory();
        NFLTeamSettings teamSettings = new NFLTeamSettings();
        try {
            String teamSettingsString = teamSettings.loadSettingsFile(folderPath, fileReaderFactory);
            if (teamSettingsString != null && !"".equals(teamSettingsString)) {
                teamSettings.setTeamsSettingsFromTeamSettingsFileString(nfl,
                        teamSettingsString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bundle fragmentArgs = new Bundle();
        fragmentArgs.putSerializable(LEAGUE_KEY, nfl);

        setUpAdapter(adapter, nfl);
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        private ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        private void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void setUpAdapter(ViewPagerAdapter adapter, League nfl) {
        TeamsFragment teamsFragment = TeamsFragment.newInstance(nfl, null);
        SeasonFragment seasonFragment = new SeasonFragment();
        PlayoffsFragment playoffsFragment = new PlayoffsFragment();

        adapter.addFragment(teamsFragment, "Teams");
        adapter.addFragment(seasonFragment, "Season");
        adapter.addFragment(playoffsFragment, "Playoffs");
    }
}
