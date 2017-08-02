package season.nfl.nflseasoncalculatorapp.input;

import android.widget.TableLayout;

import java.util.ArrayList;
import java.util.List;

import nfl.season.league.Conference;

public class ConferenceTable {

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

}
