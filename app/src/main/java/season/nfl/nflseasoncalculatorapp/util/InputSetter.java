package season.nfl.nflseasoncalculatorapp.util;

import android.content.res.Resources;
import android.widget.EditText;

import season.nfl.nflseasoncalculatorapp.R;

public class InputSetter {

    public static void setTextToInputNumber(Resources resources, EditText input, int numberToDisplay) {
        String powerRankingDisplay = resources.getQuantityString(R.plurals.display_number,
                numberToDisplay, numberToDisplay);
        input.setText(powerRankingDisplay);
    }

}
