package season.nfl.nflseasoncalculatorapp.input;

import android.view.View;
import android.widget.Spinner;
import android.widget.TableRow;

import nfl.season.league.Division;

public class DivisionChampRow {

    private Division division;

    private TableRow divisionRow;

    private String previousTeam;

    private int previousSeed;

    public DivisionChampRow(Division division, TableRow divisionRow) {
        this.division = division;
        this.divisionRow = divisionRow;
    }

    public String getDivisionChampName() {
        String divisionChampName = null;

        View divisionChampView = divisionRow.getChildAt(ConferenceTable.POSITION_OF_TEAM_SELECT);
        if (divisionChampView instanceof Spinner) {
            Spinner divisionChampSpinner = (Spinner) divisionChampView;
            divisionChampName = divisionChampSpinner.getSelectedItem().toString();
        }

        return divisionChampName;
    }

    public String getDivisionName() {
        return division.getName();
    }

    public int getSeed() {
        int seed = -1;

        View seedView = divisionRow.getChildAt(ConferenceTable.POSITION_OF_SEED_SELECT);
        if (seedView instanceof Spinner) {
            Spinner seedSpinner = (Spinner) seedView;
            String selectedSeed = seedSpinner.getSelectedItem().toString();
            seed = Integer.parseInt(selectedSeed);
        }

        return seed;
    }

    public TableRow getDivisionRowView() {
        return divisionRow;
    }

    public int getPreviousSeed() {
        return previousSeed;
    }

    public void setPreviousSeed(int previousSeed) {
        this.previousSeed = previousSeed;
    }

    public String getPreviousTeam() {
        return previousTeam;
    }

    public void setPreviousTeam(String previousTeam) {
        this.previousTeam = previousTeam;
    }

}
