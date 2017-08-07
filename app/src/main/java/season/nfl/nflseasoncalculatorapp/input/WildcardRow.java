package season.nfl.nflseasoncalculatorapp.input;

import android.widget.TableRow;

public class WildcardRow {

    private TableRow wildcardRowView;

    private String previousTeam;

    public WildcardRow(TableRow wildcardRowView) {
        this.wildcardRowView = wildcardRowView;
    }

    public TableRow getWildcardRowView() {
        return wildcardRowView;
    }

    public String getPreviousTeam() {
        return previousTeam;
    }

    public void setPreviousTeam(String previousTeam) {
        this.previousTeam = previousTeam;
    }

}
