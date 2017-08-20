package season.nfl.nflseasoncalculatorapp.util;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import nfl.season.league.Conference;
import nfl.season.league.Division;
import nfl.season.league.League;
import nfl.season.league.NFLTeamEnum;
import nfl.season.league.Team;
import nfl.season.playoffs.NFLPlayoffs;
import nfl.season.season.NFLManySeasonSimulator;
import nfl.season.season.NFLSeason;
import nfl.season.season.NFLSeasonTeam;
import nfl.season.season.NFLTiebreaker;
import season.nfl.nflseasoncalculatorapp.R;

public class SimulateSeasonTask extends AsyncTask<NFLSeason, Integer, String> {

    public static final double NUMBER_OF_HUNDRED_SIMULATIONS = 20.0;

    public static final double NUMBER_OF_SIMULATIONS = NUMBER_OF_HUNDRED_SIMULATIONS * 100.0;

    private final int padding_in_px;

    private ProgressBar progressBar;

    private Activity activity;

    private TableLayout simulateSeasonsTable;

    private NFLSeason season;

    public SimulateSeasonTask(ProgressBar progressBar, Activity activity, TableLayout simulateSeasonsTable) {
        this.progressBar = progressBar;
        this.activity = activity;
        this.simulateSeasonsTable = simulateSeasonsTable;

        int padding_in_dp = 8;  // 6 dps
        final float scale = activity.getResources().getDisplayMetrics().density;
        padding_in_px = (int) (padding_in_dp * scale + 0.5f);
    }

    @Override
    protected String doInBackground(NFLSeason... seasons) {
        season = seasons[0];
        League nfl = season.getLeague();
        season.clearSimulatedResults();
        NFLManySeasonSimulator manySeasonSimulator = season.createManySeasonsSimulator();
        manySeasonSimulator.clearSimulations();
        NFLTiebreaker tiebreaker = new NFLTiebreaker(season);
        NFLPlayoffs playoffs = new NFLPlayoffs(nfl);
        playoffs.initializeNFLPlayoffs();

        for (int i = 0; i < 100; i++) {
            publishProgress(i);
            manySeasonSimulator.simulateManySeasons(tiebreaker, playoffs, (int) NUMBER_OF_HUNDRED_SIMULATIONS);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        progressBar.setProgress(values[0]);
    }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(String result) {
        progressBar.setVisibility(View.GONE);

        HorizontalScrollView simulateScroll = (HorizontalScrollView) activity.findViewById(R.id.simulateScroll);
        simulateScroll.setVisibility(View.VISIBLE);

        while(simulateSeasonsTable.getChildCount() > 1) {
            simulateSeasonsTable.removeViewAt(1);
        }

        League nfl = season.getLeague();
        List<Conference> conferences = nfl.getConferences();
        for (int i = 0; i < conferences.size(); i++) {
            Conference conference = conferences.get(i);
            List<Division> divisions = conference.getDivisions();
            for (int j = 0; j < divisions.size(); j++) {
                Division division = divisions.get(j);
                List<Team> teams = division.getTeams();
                for (int k = 0; k < teams.size(); k++) {
                    Team team = teams.get(k);
                    String teamName = team.getName();
                    NFLSeasonTeam seasonTeam = season.getTeam(teamName);

                    TableRow teamRow = new TableRow(activity);

                    TextView teamLabel = new TextView(activity);
                    teamLabel.setText(teamName);
                    teamRow.addView(teamLabel);

                    addCellToRowWithValue(teamRow, activity, seasonTeam.getWasBottomTeam());
                    addCellToRowWithValue(teamRow, activity, seasonTeam.getWasInDivisionCellar());
                    addCellToRowWithValue(teamRow, activity, seasonTeam.getHadWinningSeason());
                    addCellToRowWithValue(teamRow, activity, seasonTeam.getMadePlayoffs());
                    addCellToRowWithValue(teamRow, activity, seasonTeam.getWonDivision());
                    addCellToRowWithValue(teamRow, activity, seasonTeam.getGotRoundOneBye());
                    addCellToRowWithValue(teamRow, activity, seasonTeam.getGotOneSeed());
                    addCellToRowWithPercent(teamRow, activity, seasonTeam.getChanceToMakeDivisionalRound());
                    addCellToRowWithPercent(teamRow, activity, seasonTeam.getChanceToMakeConferenceRound());
                    addCellToRowWithPercent(teamRow, activity, seasonTeam.getChanceToWinConference());
                    addCellToRowWithPercent(teamRow, activity, seasonTeam.getChanceToWinSuperBowl());

                    if (k >= (teams.size() - 1)) {
                        if (j >= (divisions.size() - 1)) {
                            teamRow.setPadding(0, 0, 0, 40);
                        } else {
                            teamRow.setPadding(0, 0, 0, 20);
                        }
                    }
                    simulateSeasonsTable.addView(teamRow);
                }
            }
        }
    }

    private void addCellToRowWithValue(TableRow teamRow, Activity activity, int value) {
        TextView percentLabel = new TextView(activity);
        String percentText = getPercentText(value);
        percentLabel.setText(percentText);
        percentLabel.setPadding(padding_in_px, 0, 0, 0);
        teamRow.addView(percentLabel);
    }

    private void addCellToRowWithPercent(TableRow teamRow, Activity activity, int value) {
        TextView percentLabel = new TextView(activity);
        String percentText = getPercentTextFromPercent(value);
        percentLabel.setText(percentText);
        percentLabel.setPadding(padding_in_px, 0, 0, 0);
        teamRow.addView(percentLabel);
    }

    private String getPercentText(int numberOfOccurrences) {
        //TODO: Rounding errors keep resulting is lower percents?
        int percent = (int) Math.round(numberOfOccurrences / NUMBER_OF_HUNDRED_SIMULATIONS);
        return getTextFromPercent(percent);
    }

    private String getPercentTextFromPercent(int totalPercent) {
        int percent = (int) Math.round(totalPercent / NUMBER_OF_SIMULATIONS);
        return getTextFromPercent(percent);
    }

    @NonNull
    private String getTextFromPercent(int percent) {
        String percentText = "" + percent;
        if (percent == 0) {
            percentText = "< 1";
        } else if (percent == 100) {
            percentText = "> 99";
        }
        return percentText;
    }

}
