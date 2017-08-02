package season.nfl.nflseasoncalculatorapp.input;

import android.widget.TableRow;

import nfl.season.league.Division;

public class DivisionChampRow {

    private Division division;

    private TableRow divisionRow;

    public DivisionChampRow(Division division, TableRow divisionRow) {
        this.division = division;
        this.divisionRow = divisionRow;
    }

}
