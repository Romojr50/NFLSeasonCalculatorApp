package season.nfl.nflseasoncalculatorapp.input;

import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.List;

import nfl.season.league.Conference;

public class ConferenceTable {

    private static final int POSITION_OF_TEAM_SELECT = 1;

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
            setOnItemSelectedListenerOfTeamSelect(divisionChampRow, i);
            setOnItemSelectedListenerOfSeedSelect(divisionChampRow, i);
        }
    }

    private void setOnItemSelectedListenerOfTeamSelect(final DivisionChampRow divisionChampRow, final int currentSpinnerIndex) {
        TableRow divisionRow = divisionChampRow.getDivisionRowView();
        View teamSelectView = divisionRow.getChildAt(POSITION_OF_TEAM_SELECT);

        //TODO: Fix test case switching Texans and Jaguars
        if (teamSelectView instanceof Spinner) {
            Spinner teamSelect = (Spinner) teamSelectView;
            teamSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    String selected = parentView.getItemAtPosition(position).toString();
                    Spinner previousSpinnerWithSelected = getPreviousWildcardSpinnerWtihSelected(
                            selected, -1);
                    if (previousSpinnerWithSelected != null) {
                        String previousTeam = divisionChampRow.getPreviousTeam();
                        if (previousTeam != null) {
                            int previousTeamIndex = -1;
                            int previousSpinnerLength = previousSpinnerWithSelected.getChildCount();
                            for (int i = 0; i < previousSpinnerLength; i++) {
                                String option = previousSpinnerWithSelected.getItemAtPosition(i).toString();
                                if (previousTeam.equals(option)) {
                                    previousTeamIndex = i;
                                    break;
                                }
                            }
                            previousSpinnerWithSelected.setSelection(previousTeamIndex);
                        }
                    }
                    divisionChampRow.setPreviousTeam(selected);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                }
            });
        }
    }

    private void setOnItemSelectedListenerOfSeedSelect(final DivisionChampRow divisionChampRow, final int currentSpinnerIndex) {
        TableRow divisionRow = divisionChampRow.getDivisionRowView();
        View seedSelectView = divisionRow.getChildAt(POSITION_OF_SEED_SELECT);

        if (seedSelectView instanceof Spinner) {
            Spinner seedSelect = (Spinner) seedSelectView;
            seedSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    String selected = parentView.getItemAtPosition(position).toString();
                    Spinner previousSpinnerWithSelected = getPreviousDivisionChampSpinnerWtihSelected(
                            selected, currentSpinnerIndex);
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

    private Spinner getPreviousDivisionChampSpinnerWtihSelected(String selected, int currentSpinnerIndex) {
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

    private Spinner getPreviousWildcardSpinnerWtihSelected(String selected, int currentSpinnerIndex) {
        Spinner previousSpinnerWithSelected = null;

        for (int i = 0; i < wildcardRows.size(); i++) {
            if (i != currentSpinnerIndex) {
                WildcardRow wildcardRow = wildcardRows.get(i);
                TableRow wildcardRowView = wildcardRow.getWildcardRowView();
                View teamView = wildcardRowView.getChildAt(POSITION_OF_TEAM_SELECT);
                if (teamView instanceof Spinner) {
                    Spinner teamSpinner = (Spinner) teamView;
                    Object selectedTeamObject = teamSpinner.getSelectedItem();
                    if (selectedTeamObject != null) {
                        String selectedTeam = selectedTeamObject.toString();
                        if (selected.equals(selectedTeam)) {
                            previousSpinnerWithSelected = teamSpinner;
                            break;
                        }
                    }
                }
            }
        }

        return previousSpinnerWithSelected;
    }

}
