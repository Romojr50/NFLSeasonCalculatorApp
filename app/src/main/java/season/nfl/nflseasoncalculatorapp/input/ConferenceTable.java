package season.nfl.nflseasoncalculatorapp.input;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.List;

import nfl.season.league.Conference;

public class ConferenceTable {

    private static final int POSITION_OF_SEED_SELECT = 2;

    private TableLayout conferenceTableLayout;

    private Conference leagueConference;

    private List<DivisionChampRow> divisionChampRows;

    private List<WildcardRow> wildcardRows;

    public ConferenceTable(Conference leagueConference, TableLayout conferenceTableLayout) {
        this.conferenceTableLayout = conferenceTableLayout;
        this.leagueConference = leagueConference;
        divisionChampRows = new ArrayList<>();
        wildcardRows = new ArrayList<>();
    }

    public Conference getConference() {
        return this.leagueConference;
    }

    public TableLayout getTableLayout() {
        return this.conferenceTableLayout;
    }

    public void addDivisionChampRow(DivisionChampRow divisionChampRow) {
        divisionChampRows.add(divisionChampRow);
    }

    public void addWildcardRow(WildcardRow wildcardRow) {
        wildcardRows.add(wildcardRow);
    }

    public void setInputListeners() {
        for (int i = 0; i < divisionChampRows.size(); i++) {
            final DivisionChampRow divisionChampRow = divisionChampRows.get(i);
            TableRow divisionRow = divisionChampRow.getDivisionRowView();
            View seedSelectView = divisionRow.getChildAt(POSITION_OF_SEED_SELECT);

            final int currentSpinnerIndex = i;
            if (seedSelectView instanceof Spinner) {
                Spinner seedSelect = (Spinner) seedSelectView;
                seedSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        String selected = parentView.getItemAtPosition(position).toString();
                        Spinner previousSpinnerWithSelected = getPreviousSpinnerWtihSelected(selected, currentSpinnerIndex);
                        if (previousSpinnerWithSelected != null) {
                            int previousSeed = divisionChampRow.getPreviousSeed();
                            previousSpinnerWithSelected.setSelection(previousSeed - 1);
                        }
                        divisionChampRow.setPreviousSeed(Integer.parseInt(selected));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                    }
                });
            }
        }
    }

    private Spinner getPreviousSpinnerWtihSelected(String selected, int currentSpinnerIndex) {
        Spinner previousSpinnerWithSelected = null;

        for (int i = 0; i < divisionChampRows.size(); i++) {
            if (i != currentSpinnerIndex) {
                DivisionChampRow divisionChampRow = divisionChampRows.get(i);
                TableRow divisionRow = divisionChampRow.getDivisionRowView();
                View seedView = divisionRow.getChildAt(POSITION_OF_SEED_SELECT);
                if (seedView instanceof Spinner) {
                    Spinner seedSpinner = (Spinner) seedView;
                    String selectedSeed = seedSpinner.getSelectedItem().toString();
                    if (selected.equals(selectedSeed)) {
                        previousSpinnerWithSelected = seedSpinner;
                        break;
                    }
                }
            }
        }

        return previousSpinnerWithSelected;
    }

}
