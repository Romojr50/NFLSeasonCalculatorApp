package season.nfl.nflseasoncalculatorapp.util;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import nfl.season.league.Matchup;
import nfl.season.league.Team;
import season.nfl.nflseasoncalculatorapp.R;
import season.nfl.nflseasoncalculatorapp.input.HomeAwayWinModeSpinner;
import season.nfl.nflseasoncalculatorapp.input.NeutralWinModeSpinner;
import season.nfl.nflseasoncalculatorapp.input.WinChanceEditText;

public class MatchupTableTask extends AsyncTask<Team, TableRow, String> {

    private Activity activity;

    private ProgressBar progress;

    private int numberOfRows;

    public MatchupTableTask(Activity activity, ProgressBar progress) {
        this.activity = activity;
        this.progress = progress;
    }

    @Override
    protected String doInBackground(Team... teams) {
        if (teams.length > 0) {
            setUpMatchupTable(activity, teams[0]);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(TableRow... tableRows) {
        if (tableRows.length > 0) {
            TableLayout matchupTable = (TableLayout) activity.findViewById(R.id.matchupTable);
            matchupTable.addView(tableRows[0]);
            numberOfRows++;
            int currentProgress = (int) Math.round(100.0 / 31.0 * numberOfRows);
            progress.setProgress(currentProgress);
        }
    }

    @Override
    protected void onPostExecute(String result) {
        progress.setVisibility(View.GONE);
    }

    private void setUpMatchupTable(Activity activity, Team team) {
        String selectedTeam = team.getName();
        List<Matchup> matchups = team.getMatchups();

        for (int i = 0; i < matchups.size(); i++) {
            Matchup matchup = matchups.get(i);
            TableRow matchupRow = new TableRow(activity);

            String opponentName = matchup.getOpponentName(selectedTeam);
            TextView opponentLabel = new TextView(activity);
            opponentLabel.setText(opponentName);
            matchupRow.addView(opponentLabel);

            WinChanceEditText neutralEdit = new WinChanceEditText(activity, matchup, selectedTeam,
                    WinChanceEditText.WinChanceTypeEnum.NEUTRAL);
            matchupRow.addView(neutralEdit);

            NeutralWinModeSpinner neutralModeEdit = new NeutralWinModeSpinner(activity, matchup, selectedTeam,
                    neutralEdit);
            matchupRow.addView(neutralModeEdit);

            WinChanceEditText homeEdit = new WinChanceEditText(activity, matchup, selectedTeam,
                    WinChanceEditText.WinChanceTypeEnum.HOME);
            matchupRow.addView(homeEdit);

            HomeAwayWinModeSpinner homeModeEdit = new HomeAwayWinModeSpinner(activity, matchup, selectedTeam,
                    selectedTeam, homeEdit);
            matchupRow.addView(homeModeEdit);

            WinChanceEditText awayEdit = new WinChanceEditText(activity, matchup, selectedTeam,
                    WinChanceEditText.WinChanceTypeEnum.AWAY);
            matchupRow.addView(awayEdit);

            HomeAwayWinModeSpinner awayModeEdit = new HomeAwayWinModeSpinner(activity, matchup, selectedTeam,
                    opponentName, awayEdit);
            matchupRow.addView(awayModeEdit);

            neutralModeEdit.setHomeSpinner(homeModeEdit);
            neutralModeEdit.setAwaySpinner(awayModeEdit);

            publishProgress(matchupRow);
        }
    }

}
