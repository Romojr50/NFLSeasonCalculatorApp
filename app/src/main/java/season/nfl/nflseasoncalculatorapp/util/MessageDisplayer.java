package season.nfl.nflseasoncalculatorapp.util;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

public class MessageDisplayer {

    public static void displayMessage(Activity activity, String messsageToDisplay) {
        Context context = activity.getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, messsageToDisplay, duration);
        toast.show();
    }

}
