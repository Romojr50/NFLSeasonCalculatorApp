package season.nfl.nflseasoncalculatorapp.input;

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
