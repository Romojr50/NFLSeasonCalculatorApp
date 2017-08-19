package season.nfl.nflseasoncalculatorapp.util;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import nfl.season.league.League;
import nfl.season.league.NFLTeamEnum;
import nfl.season.playoffs.NFLPlayoffs;
import nfl.season.season.NFLManySeasonSimulator;
import nfl.season.season.NFLSeason;
import nfl.season.season.NFLSeasonTeam;
import nfl.season.season.NFLTiebreaker;

public class SimulateSeasonTask extends AsyncTask<NFLSeason, Integer, String> {

    public static final int NUMBER_OF_HUNDRED_SIMULATIONS = 20;

    public static final double NUMBER_OF_SIMULATIONS = NUMBER_OF_HUNDRED_SIMULATIONS * 100.0;

    private ProgressBar progressBar;

    private Activity activity;

    private TableLayout simulateSeasonsTable;

    private NFLSeason season;

    public SimulateSeasonTask(ProgressBar progressBar, Activity activity, TableLayout simulateSeasonsTable) {
        this.progressBar = progressBar;
        this.activity = activity;
        this.simulateSeasonsTable = simulateSeasonsTable;
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
            manySeasonSimulator.simulateManySeasons(tiebreaker, playoffs, NUMBER_OF_HUNDRED_SIMULATIONS);
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
        while(simulateSeasonsTable.getChildCount() > 1) {
            simulateSeasonsTable.removeViewAt(1);
        }

        for (NFLTeamEnum teamEnum : NFLTeamEnum.values()) {
            String teamName = teamEnum.getTeamName();
            NFLSeasonTeam seasonTeam = season.getTeam(teamName);

            TableRow teamRow = new TableRow(activity);

            TextView teamLabel = new TextView(activity);
            teamLabel.setText(teamName);
            teamRow.addView(teamLabel);

            TextView bottomLabel = new TextView(activity);
            String bottomPercent = getPercentText(seasonTeam.getWasBottomTeam());
            bottomLabel.setText(bottomPercent);
            teamRow.addView(bottomLabel);

            TextView cellarLabel = new TextView(activity);
            String cellarPercent = getPercentText(seasonTeam.getWasInDivisionCellar());
            cellarLabel.setText(cellarPercent);
            teamRow.addView(cellarLabel);

            TextView winningLabel = new TextView(activity);
            String winningPercent = getPercentText(seasonTeam.getHadWinningSeason());
            winningLabel.setText(winningPercent);
            teamRow.addView(winningLabel);

            TextView playoffsLabel = new TextView(activity);
            String playoffsPercent = getPercentText(seasonTeam.getMadePlayoffs());
            playoffsLabel.setText(playoffsPercent);
            teamRow.addView(playoffsLabel);

            TextView divisionChampLabel = new TextView(activity);
            String divisionChampPercent = getPercentText(seasonTeam.getWonDivision());
            divisionChampLabel.setText(divisionChampPercent);
            teamRow.addView(divisionChampLabel);

            TextView roundOneLabel = new TextView(activity);
            String roundOnePercent = getPercentText(seasonTeam.getGotRoundOneBye());
            roundOneLabel.setText(roundOnePercent);
            teamRow.addView(roundOneLabel);

            TextView oneSeedLabel = new TextView(activity);

            simulateSeasonsTable.addView(teamRow);
        }
    }

    private String getPercentText(int numberOfOccurrences) {
        int percent = Math.round(numberOfOccurrences / NUMBER_OF_HUNDRED_SIMULATIONS);
        String percentText = "" + percent;
        if (percent == 0) {
            percentText = "< 1";
        } else if (percent == 100) {
            percentText = "> 99";
        }
        return percentText;
    }

}
