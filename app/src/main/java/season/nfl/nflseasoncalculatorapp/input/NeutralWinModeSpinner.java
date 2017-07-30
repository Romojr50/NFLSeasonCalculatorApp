package season.nfl.nflseasoncalculatorapp.input;

import android.app.Activity;
import android.content.res.Resources;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import nfl.season.league.Matchup;
import season.nfl.nflseasoncalculatorapp.R;
import season.nfl.nflseasoncalculatorapp.util.InputSetter;

public class NeutralWinModeSpinner extends WinModeSpinner {

    private final String powerRankingsMode;

    private final String eloRatingsMode;

    private Matchup matchup;

    private String selectedTeam;

    private WinChanceEditText winChanceEdit;

    public NeutralWinModeSpinner(final Activity activity, final Matchup matchup, final String selectedTeam,
                                 final WinChanceEditText winChanceEdit) {
        super(activity);
        powerRankingsMode = activity.getString(R.string.power_rankings_win_mode);
        eloRatingsMode = activity.getString(R.string.elo_ratings_win_mode);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(activity, R.array.neutral_mode_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setAdapter(spinnerAdapter);

        Matchup.WinChanceModeEnum winChanceMode = matchup.getWinChanceMode();
        int selectionIndex = mapWinChanceModeToSpinnerIndex(winChanceMode);
        setSelection(selectionIndex);

        this.matchup = matchup;
        this.selectedTeam = selectedTeam;
        this.winChanceEdit = winChanceEdit;

        setUpOnItemSelectedListener(activity);
    }

    public void setHomeSpinner(HomeAwayWinModeSpinner homeSpinner) {
        winChanceEdit.setHomeSpinner(homeSpinner);
    }

    public void setAwaySpinner(HomeAwayWinModeSpinner awaySpinner) {
        winChanceEdit.setAwaySpinner(awaySpinner);
    }

    public void saveNeutralWinChance() {
        if (WinChanceEditText.validate(winChanceEdit)) {
            String selected = (String) getSelectedItem();
            if (customMode.equals(selected)) {
                Editable winChanceText = winChanceEdit.getText();
                if (winChanceText != null) {
                    String winChanceTextString = winChanceText.toString();
                    try {
                        int winChance = Integer.parseInt(winChanceTextString);
                        matchup.setTeamNeutralWinChance(selectedTeam, winChance);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (powerRankingsMode.equals(selected)) {
                matchup.calculateTeamWinChancesFromPowerRankings();
            } else if (eloRatingsMode.equals(selected)) {
                matchup.calculateTeamWinChancesFromEloRatings();
            }
        }
    }

    private int mapWinChanceModeToSpinnerIndex(Matchup.WinChanceModeEnum winChanceMode) {
        String[] winChanceArray = getResources().getStringArray(R.array.neutral_mode_array);
        char firstCharOfMode = winChanceMode.name().charAt(0);
        return matchCharToStringArray(firstCharOfMode, winChanceArray);
    }

    private void setUpOnItemSelectedListener(final Activity activity) {
        setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selected = parentView.getItemAtPosition(position).toString();
                setEditToDisabledIfCustomChosen(winChanceEdit, selected);

                int newWinChance = -1;
                if (powerRankingsMode.equals(selected)) {
                    newWinChance = matchup.calculateSingleTeamWinChanceFromPowerRankings(selectedTeam);
                } else if (eloRatingsMode.equals(selected)) {
                    newWinChance = matchup.calculateSingleTeamWinChanceFromEloRatings(selectedTeam);
                }

                if (newWinChance > 0) {
                    Resources resources = activity.getResources();
                    InputSetter.setTextToInputNumber(resources, winChanceEdit, newWinChance);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

}
