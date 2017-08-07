package season.nfl.nflseasoncalculatorapp.input;

import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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
            setOnItemSelectedListenerOfDivisionChampTeamSelect(divisionChampRow);
            setOnItemSelectedListenerOfSeedSelect(divisionChampRow, i);
        }
        for (int i = 0; i < wildcardRows.size(); i++) {
            final WildcardRow wildcardRow = wildcardRows.get(i);
            setOnItemSelectedListenerOfWildcardTeamSelect(wildcardRow);
        }
    }

    private void setOnItemSelectedListenerOfWildcardTeamSelect(final WildcardRow wildcardRow) {
        TableRow wildcardRowView = wildcardRow.getWildcardRowView();
        View teamSelectView = wildcardRowView.getChildAt(POSITION_OF_TEAM_SELECT);

        if (teamSelectView instanceof Spinner) {
            Spinner teamSelect = (Spinner) teamSelectView;
            teamSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    String selected = parentView.getItemAtPosition(position).toString();
                    Spinner previousSpinnerWithSelected = getPreviousDivisionChampSpinnerWtihSelected(
                            selected, -1, POSITION_OF_TEAM_SELECT);
                    if (previousSpinnerWithSelected != null) {
                        //TODO: What if we select a wildcard that was selected as Div Champ but same Div doesn't have existing wildcard to switch with?
                        String previousTeam = wildcardRow.getPreviousTeam();
                        if (previousTeam != null) {
                            int previousTeamIndex = getIndexOfItemInSpinner(previousSpinnerWithSelected,
                                    previousTeam);
                            previousSpinnerWithSelected.setSelection(previousTeamIndex);
                        }
                    }
                    wildcardRow.setPreviousTeam(selected);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                }
            });
        }
    }

    private void setOnItemSelectedListenerOfDivisionChampTeamSelect(
            final DivisionChampRow divisionChampRow) {
        TableRow divisionRow = divisionChampRow.getDivisionRowView();
        View teamSelectView = divisionRow.getChildAt(POSITION_OF_TEAM_SELECT);

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
                            int previousTeamIndex = getIndexOfItemInSpinner(previousSpinnerWithSelected,
                                    previousTeam);
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
                            selected, currentSpinnerIndex, POSITION_OF_SEED_SELECT);
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

    private Spinner getPreviousDivisionChampSpinnerWtihSelected(
            String selected, int currentSpinnerIndex, int positionOfSpinnerInRow) {
        Spinner previousSpinnerWithSelected = null;

        for (int i = 0; i < divisionChampRows.size(); i++) {
            if (i != currentSpinnerIndex) {
                DivisionChampRow divisionChampRow = divisionChampRows.get(i);
                TableRow divisionRow = divisionChampRow.getDivisionRowView();
                View seedView = divisionRow.getChildAt(positionOfSpinnerInRow);
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

    private int getIndexOfItemInSpinner(Spinner spinner, String item) {
        int indexOfItem = -1;
        SpinnerAdapter previousAdapter = spinner.getAdapter();
        int previousSpinnerLength = previousAdapter.getCount();
        for (int i = 0; i < previousSpinnerLength; i++) {
            String option = (String) previousAdapter.getItem(i);
            if (item.equals(option)) {
                indexOfItem = i;
                break;
            }
        }
        return indexOfItem;
    }

}
