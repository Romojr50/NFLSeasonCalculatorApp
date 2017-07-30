package season.nfl.nflseasoncalculatorapp.input;

import android.app.Activity;
import android.content.res.Resources;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.widget.TextView;

import nfl.season.league.Matchup;
import season.nfl.nflseasoncalculatorapp.util.InputSetter;
import season.nfl.nflseasoncalculatorapp.validators.TextValidator;

public class WinChanceEditText extends AppCompatEditText {

    public enum WinChanceTypeEnum {
        NEUTRAL, HOME, AWAY
    }

    private String selectedTeam;
    private Matchup matchup;
    private WinChanceTypeEnum type;
    private HomeAwayWinModeSpinner homeSpinner;
    private HomeAwayWinModeSpinner awaySpinner;

    public WinChanceEditText(final Activity activity, Matchup matchup, String selectedTeam, WinChanceTypeEnum type) {
        super(activity);

        this.selectedTeam = selectedTeam;
        this.matchup = matchup;
        this.type = type;

        setInputType(InputType.TYPE_CLASS_NUMBER);

        Resources resources = activity.getResources();
        setTextFromMatchup(resources);

        setUpTextChangedListener(resources);
    }

    public void setHomeSpinner(HomeAwayWinModeSpinner homeSpinner) {
        this.homeSpinner = homeSpinner;
    }

    public void setAwaySpinner(HomeAwayWinModeSpinner awaySpinner) {
        this.awaySpinner = awaySpinner;
    }

    public static boolean validate(TextView textView) {
        boolean isValid = true;

        String text = textView.getText().toString();
        try {
            int winChance = Integer.parseInt(text);
            if (winChance < 1 || winChance > 99) {
                textView.setError("Win Chance must be between 1 and 99");
                isValid = false;
            }
        } catch (Exception e) {
            textView.setError("Win Chance must be a number");
            isValid = false;
        }

        return isValid;
    }

    public void setTextFromMatchup(Resources resources) {
        if (type == WinChanceTypeEnum.NEUTRAL) {
            InputSetter.setTextToInputNumber(resources, this, matchup.getTeamNeutralWinChance(selectedTeam));
        } else if (type == WinChanceTypeEnum.HOME) {
            InputSetter.setTextToInputNumber(resources, this, matchup.getTeamHomeWinChance(selectedTeam));
        } else if (type == WinChanceTypeEnum.AWAY) {
            InputSetter.setTextToInputNumber(resources, this, matchup.getTeamAwayWinChance(selectedTeam));
        }
    }

    private void setUpTextChangedListener(final Resources resources) {
        addTextChangedListener(new TextValidator(this) {
            @Override public void validate(TextView textView, String text) {
                if (WinChanceEditText.validate(textView)) {
                    int newWinChance = Integer.parseInt(text);

                    if (homeSpinner != null && homeSpinner.homeFieldAdvantageIsSelected()) {
                        homeSpinner.resetTextWithHomeFieldAdvantageCalculation(resources, newWinChance);
                    }
                    if (awaySpinner != null && awaySpinner.homeFieldAdvantageIsSelected()) {
                        awaySpinner.resetTextWithHomeFieldAdvantageCalculation(resources, (100 - newWinChance));
                    }
                }
            }
        });
    }

}
