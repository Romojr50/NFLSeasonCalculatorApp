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

public class HomeAwayWinModeSpinner extends WinModeSpinner {

    private final String homeFieldAdvantageMode;

    private Matchup matchup;

    private String selectedTeam;

    private String homeTeam;

    private WinChanceEditText winChanceEdit;

    public HomeAwayWinModeSpinner(final Activity activity, final Matchup matchup, final String selectedTeam,
                                  final String homeTeam, final WinChanceEditText winChanceEdit) {
        super(activity);
        this.matchup = matchup;
        this.selectedTeam = selectedTeam;
        this.homeTeam = homeTeam;
        this.winChanceEdit = winChanceEdit;
        homeFieldAdvantageMode = activity.getString(R.string.home_field_advantage_mode);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(activity, R.array.home_mode_array,
                android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setAdapter(spinnerAdapter);

        Matchup.HomeAwayWinChanceModeEnum winChanceMode = matchup.getHomeAwayWinChanceMode(homeTeam);
        int selectionIndex = mapHomeWinChanceModeToSpinnerIndex(winChanceMode);
        setSelection(selectionIndex);

        setUpOnItemSelectedListener(activity);
    }

    public boolean homeFieldAdvantageIsSelected() {
        boolean isSelected = false;

        Object currentSelected = getSelectedItem();
        if (currentSelected instanceof String) {
            String currentSelectedString = (String) currentSelected;
            if (homeFieldAdvantageMode.equalsIgnoreCase(currentSelectedString)) {
                isSelected = true;
            }
        }

        return isSelected;
    }

    public void resetTextWithHomeFieldAdvantageCalculation(Resources resources) {
        int newWinChance = matchup.calculateSingleHomeWinChanceFromHomeFieldAdvantage(homeTeam);
        if (!selectedTeam.equals(homeTeam)) {
            newWinChance = 100 - newWinChance;
        }
        InputSetter.setTextToInputNumber(resources, winChanceEdit, newWinChance);
    }

    public void resetTextWithHomeFieldAdvantageCalculation(Resources resources, int neutralWinChance) {
        int newWinChance = matchup.calculateSingleHomeWinChanceFromHomeFieldAdvantage(homeTeam, neutralWinChance);
        if (!selectedTeam.equals(homeTeam)) {
            newWinChance = 100 - newWinChance;
        }
        InputSetter.setTextToInputNumber(resources, winChanceEdit, newWinChance);
    }

    public void saveHomeAwayWinChance() {
        if (WinChanceEditText.validate(winChanceEdit)) {
            boolean selectedTeamIsHome = selectedTeam.equals(homeTeam);

            String selected = (String) getSelectedItem();
            if (customMode.equals(selected)) {
                Editable winChanceText = winChanceEdit.getText();
                if (winChanceText != null) {
                    String winChanceTextString = winChanceText.toString();
                    try {
                        int winChance = Integer.parseInt(winChanceTextString);
                        if (selectedTeamIsHome) {
                            matchup.setTeamHomeWinChance(selectedTeam, winChance);
                        } else {
                            matchup.setTeamAwayWinChance(selectedTeam, winChance);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (homeFieldAdvantageMode.equals(selected)) {
                matchup.calculateHomeWinChanceFromHomeFieldAdvantage(homeTeam);
            }
        }
    }

    private int mapHomeWinChanceModeToSpinnerIndex(Matchup.HomeAwayWinChanceModeEnum winChanceMode) {
        String[] winChanceArray = getResources().getStringArray(R.array.home_mode_array);
        char firstCharOfMode = winChanceMode.name().charAt(0);

        return matchCharToStringArray(firstCharOfMode, winChanceArray);
    }

    private void setUpOnItemSelectedListener(final Activity activity) {
        setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selected = parentView.getItemAtPosition(position).toString();
                setEditToDisabledIfCustomChosen(winChanceEdit, selected);

                if (homeFieldAdvantageMode.equals(selected)) {
                    Resources resources = activity.getResources();
                    resetTextWithHomeFieldAdvantageCalculation(resources);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

}
