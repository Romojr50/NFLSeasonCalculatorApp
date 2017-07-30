package season.nfl.nflseasoncalculatorapp.input;

import android.app.Activity;
import android.support.v7.widget.AppCompatSpinner;

import season.nfl.nflseasoncalculatorapp.R;

public class WinModeSpinner extends AppCompatSpinner {

    public final String customMode;

    public WinModeSpinner(final Activity activity) {
        super(activity);
        customMode =  activity.getString(R.string.custom_win_mode);
    }

    protected int matchCharToStringArray(char charToMatch, String[] stringArray) {
        int index = -1;
        for (int i = 0; i < stringArray.length; i++) {
            String modeFromArray = stringArray[i];
            char firstCharOfModeFromArray = modeFromArray.charAt(0);
            if (charToMatch == firstCharOfModeFromArray) {
                index = i;
                break;
            }
        }
        return index;
    }

    protected void setEditToDisabledIfCustomChosen(WinChanceEditText winChanceEdit, String selected) {
        if (selected != null && selected.equals(customMode)) {
            winChanceEdit.setEnabled(true);
        } else {
            winChanceEdit.setEnabled(false);
        }
    }

}
