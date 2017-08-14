package season.nfl.nflseasoncalculatorapp.input;

import android.view.View;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

public class WildcardRow {

    private TableRow wildcardRowView;

    private String previousTeam;

    public WildcardRow(TableRow wildcardRowView) {
        this.wildcardRowView = wildcardRowView;
    }

    TableRow getWildcardRowView() {
        return wildcardRowView;
    }

    public String getWildcardName() {
        String wildcardName = null;

        View wildcardView = wildcardRowView.getChildAt(ConferenceTable.POSITION_OF_TEAM_SELECT);
        if (wildcardView instanceof Spinner) {
            Spinner wildcardSpinner = (Spinner) wildcardView;
            wildcardName = wildcardSpinner.getSelectedItem().toString();
        }

        return wildcardName;
    }

    public int getSeed() {
        int seed = -1;

        View seedView = wildcardRowView.getChildAt(ConferenceTable.POSITION_OF_SEED_SELECT);
        if (seedView instanceof TextView) {
            TextView seedText = (TextView) seedView;
            String selectedSeed = seedText.getText().toString();
            seed = Integer.parseInt(selectedSeed);
        }

        return seed;
    }

    String getPreviousTeam() {
        return previousTeam;
    }

    void setPreviousTeam(String previousTeam) {
        this.previousTeam = previousTeam;
    }

}
