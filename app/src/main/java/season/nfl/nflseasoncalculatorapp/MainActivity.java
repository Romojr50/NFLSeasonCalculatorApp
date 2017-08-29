package season.nfl.nflseasoncalculatorapp;

import android.content.Context;
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
import nfl.season.input.NFLPlayoffSettings;
import nfl.season.input.NFLTeamSettings;
import nfl.season.league.League;
import nfl.season.playoffs.NFLPlayoffs;
import nfl.season.season.NFLSeason;
import season.nfl.nflseasoncalculatorapp.fragments.AllTeamsFragment;
import season.nfl.nflseasoncalculatorapp.fragments.HelpFragment;
import season.nfl.nflseasoncalculatorapp.fragments.PlayoffsFragment;
import season.nfl.nflseasoncalculatorapp.fragments.SeasonFragment;
import season.nfl.nflseasoncalculatorapp.fragments.TeamFragment;
import season.nfl.nflseasoncalculatorapp.fragments.TeamsFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TeamsFragment.OnFragmentInteractionListener,
        SeasonFragment.OnFragmentInteractionListener, PlayoffsFragment.OnFragmentInteractionListener,
        AllTeamsFragment.OnFragmentInteractionListener, TeamFragment.OnFragmentInteractionListener,
        HelpFragment.OnFragmentInteractionListener {

    public static final String LEAGUE_KEY = "LEAGUE_NFL";

    private static final int INDEX_OF_TEAM_TAB = 0;

    private ViewPager viewPager;

    @Override
    public void onBackPressed() {
        if (viewPager != null) {
            int currentTab = viewPager.getCurrentItem();
            if (currentTab == INDEX_OF_TEAM_TAB) {
                super.onBackPressed();
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        League nfl = new League(League.NFL);
        nfl.initializeNFL();

        NFLPlayoffs playoffs = new NFLPlayoffs(nfl);
        playoffs.initializeNFLPlayoffs();

        NFLSeason season = new NFLSeason();
        season.initializeNFLRegularSeason(nfl);

        loadSettingsFiles(nfl, playoffs);

        Bundle fragmentArgs = new Bundle();
        fragmentArgs.putSerializable(LEAGUE_KEY, nfl);

        setUpAdapter(adapter, nfl, playoffs, season);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
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

    private void loadSettingsFiles(League nfl, NFLPlayoffs playoffs) {
        Context context = getApplicationContext();
        File fileDir = context.getFilesDir();
        String folderPath = fileDir.getAbsolutePath();

        NFLFileReaderFactory fileReaderFactory = new NFLFileReaderFactory();
        NFLTeamSettings teamSettings = new NFLTeamSettings();
        NFLPlayoffSettings playoffSettings = new NFLPlayoffSettings();
        try {
            String teamSettingsString = teamSettings.loadSettingsFile(folderPath, fileReaderFactory);
            if (teamSettingsString != null && !"".equals(teamSettingsString)) {
                teamSettings.setTeamsSettingsFromTeamSettingsFileString(nfl,
                        teamSettingsString);
            }

            String playoffSettingsString = playoffSettings.loadSettingsFile(folderPath, fileReaderFactory);
            if (playoffSettingsString != null && !"".equals(playoffSettingsString)) {
                playoffSettings.loadPlayoffSettingsString(playoffs, nfl, playoffSettingsString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setUpAdapter(
            ViewPagerAdapter adapter, League nfl, NFLPlayoffs playoffs, NFLSeason season) {
        TeamsFragment teamsFragment = TeamsFragment.newInstance(nfl, null);
        SeasonFragment seasonFragment = SeasonFragment.newInstance(nfl, season);
        PlayoffsFragment playoffsFragment = PlayoffsFragment.newInstance(nfl, playoffs);
        HelpFragment helpFragment = HelpFragment.newInstance();

        adapter.addFragment(teamsFragment, "Teams");
        adapter.addFragment(seasonFragment, "Season");
        adapter.addFragment(playoffsFragment, "Playoffs");
        adapter.addFragment(helpFragment, "Help");
    }
}
