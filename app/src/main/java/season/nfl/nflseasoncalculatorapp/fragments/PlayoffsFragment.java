package season.nfl.nflseasoncalculatorapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nfl.season.input.NFLFileWriterFactory;
import nfl.season.input.NFLPlayoffSettings;
import nfl.season.league.Conference;
import nfl.season.league.Division;
import nfl.season.league.League;
import nfl.season.league.Team;
import nfl.season.playoffs.NFLPlayoffConference;
import nfl.season.playoffs.NFLPlayoffTeam;
import nfl.season.playoffs.NFLPlayoffs;
import season.nfl.nflseasoncalculatorapp.MainActivity;
import season.nfl.nflseasoncalculatorapp.R;
import season.nfl.nflseasoncalculatorapp.input.ConferenceTable;
import season.nfl.nflseasoncalculatorapp.input.DivisionChampRow;
import season.nfl.nflseasoncalculatorapp.input.WildcardRow;
import season.nfl.nflseasoncalculatorapp.util.MessageDisplayer;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlayoffsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlayoffsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayoffsFragment extends Fragment {

    private static final String PLAYOFFS_KEY = "NFL Playoffs";

    private static final int PLAYOFF_TABLE_PADDING = 40;

    private static final int CONFERENCE_PADDING = 20;

    private League nfl;

    private NFLPlayoffs playoffs;

    private NFLPlayoffSettings playoffSettings;

    private NFLFileWriterFactory fileWriterFactory;

    private List<ConferenceTable> conferenceTables;

    private String folderPath;

    public PlayoffsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param nfl League
     * @return A new instance of fragment PlayoffsFragment.
     */
    public static PlayoffsFragment newInstance(League nfl, NFLPlayoffs playoffs) {
        PlayoffsFragment fragment = new PlayoffsFragment();
        Bundle args = new Bundle();
        args.putSerializable(MainActivity.LEAGUE_KEY, nfl);
        args.putSerializable(PLAYOFFS_KEY, playoffs);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            nfl = (League) getArguments().getSerializable(MainActivity.LEAGUE_KEY);
            playoffs = (NFLPlayoffs) getArguments().getSerializable(PLAYOFFS_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_playoffs, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
        final Activity activity = getActivity();
        fileWriterFactory = new NFLFileWriterFactory();
        playoffSettings = new NFLPlayoffSettings();

        conferenceTables = new ArrayList<>();
        populateConferenceTables(activity, view);

        setSavePlayoffButtonListener(activity);
        setGeneratePlayoffTableListener(activity);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            File fileDir = context.getFilesDir();
            folderPath = fileDir.getAbsolutePath();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
    }

    private void populateConferenceTables(Activity activity, View view) {
        LinearLayout selectPlayoffTeamsLayout = (LinearLayout) view.findViewById(R.id.selectPlayoffTeamsLayout);
        List<NFLPlayoffConference> playoffConferences = playoffs.getConferences();
        for (NFLPlayoffConference playoffConference : playoffConferences) {
            Conference leagueConference = playoffConference.getConference();
            final String conferenceName = leagueConference.getName();

            LinearLayout conferenceLayout = new LinearLayout(activity);
            conferenceLayout.setOrientation(LinearLayout.VERTICAL);

            addConferenceLabelAboveConferenceTable(activity, conferenceLayout, conferenceName);

            TableLayout conferenceTableLayout = new TableLayout(activity);
            conferenceLayout.addView(conferenceTableLayout);

            addLabelsRowToConferenceTable(activity, conferenceTableLayout);
            final ConferenceTable conferenceTable = new ConferenceTable(leagueConference, conferenceTableLayout);
            addDivisionChampRowsToConferenceTable(activity, conferenceTable);
            addWildcardRowsToConferenceTable(activity, conferenceTable);

            conferenceTable.setInputListeners();

            selectPlayoffTeamsLayout.addView(conferenceLayout);
            conferenceLayout.setPadding(0, 0, 0, CONFERENCE_PADDING);
            conferenceTables.add(conferenceTable);
        }
    }

    private void setSavePlayoffButtonListener(final Activity activity) {
        Button savePlayoffSettingsButton = (Button) activity.findViewById(R.id.savePlayoffSettingsButton);
        savePlayoffSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populatePlayoffsFromSelections();
                try {
                    playoffSettings.saveToSettingsFile(playoffs, folderPath, fileWriterFactory);
                    MessageDisplayer.displayMessage(activity, "Playoff Teams Saved");
                } catch (IOException e) {
                    MessageDisplayer.displayMessage(activity, "Playoff Teams Save FAILED");
                }
            }
        });
    }

    private void setGeneratePlayoffTableListener(final Activity activity) {
        Button generateTableButton = (Button) activity.findViewById(R.id.generatePlayoffTableButton);
        generateTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TableLayout playoffTable = (TableLayout) activity.findViewById(R.id.playoffTable);
                playoffTable.removeAllViewsInLayout();
                TableRow playoffTableLabelRow = createPlayoffTableLabelRow(activity);
                playoffTable.addView(playoffTableLabelRow);

                populatePlayoffsFromSelections();

                List<NFLPlayoffConference> playoffConferences = playoffs.getConferences();
                for (NFLPlayoffConference playoffConference : playoffConferences) {
                    createPlayoffTablesRowsForConference(activity, playoffTable, playoffConference);
                }
            }
        });
    }

    private void addConferenceLabelAboveConferenceTable(
            Activity activity, LinearLayout conferenceLayout, String conferenceName) {
        TextView conferenceLabel = new TextView(activity);
        conferenceLabel.setText(conferenceName);
        conferenceLabel.setTextSize(16);
        conferenceLabel.setTypeface(null, Typeface.BOLD);
        conferenceLayout.addView(conferenceLabel);
    }

    private void addLabelsRowToConferenceTable(Activity activity, TableLayout conferenceTable) {
        TableRow labelsRow = new TableRow(activity);
        conferenceTable.addView(labelsRow);

        TextView divisionWildcardLabel = new TextView(activity);
        divisionWildcardLabel.setText(R.string.division_wildcard);
        labelsRow.addView(divisionWildcardLabel);

        TextView selectTeamLabel = new TextView(activity);
        selectTeamLabel.setText(R.string.select_team_short);
        selectTeamLabel.setPadding(18, 0, 0, 0);
        labelsRow.addView(selectTeamLabel);

        TextView seedLabel = new TextView(activity);
        seedLabel.setText(R.string.seed);
        labelsRow.addView(seedLabel);
    }

    private void addDivisionChampRowsToConferenceTable(Activity activity, ConferenceTable conferenceTable) {
        Conference leagueConference = conferenceTable.getConference();
        TableLayout conferenceTableLayout = conferenceTable.getTableLayout();

        Resources resources = activity.getResources();
        String conferenceName = leagueConference.getName();

        List<Division> divisions = leagueConference.getDivisions();
        for (int i = 0; i < divisions.size(); i++) {
            Division division = divisions.get(i);
            String divisionName = division.getName();

            TableRow divisionRow = new TableRow(activity);
            conferenceTableLayout.addView(divisionRow);

            String divisionChampLabelString = resources.getString(R.string.division_champion,
                    (conferenceName+ " " + divisionName));
            TextView divisionChampLabel = new TextView(activity);
            divisionChampLabel.setText(divisionChampLabelString);
            divisionRow.addView(divisionChampLabel);

            addDivisionChampSpinnersToDivisionRow(activity, divisionRow, leagueConference, i);

            DivisionChampRow divisionChampRow = new DivisionChampRow(division, divisionRow);
            conferenceTable.addDivisionChampRow(divisionChampRow);
        }
    }

    private void addWildcardRowsToConferenceTable(Activity activity, ConferenceTable conferenceTable) {
        Conference leagueConference = conferenceTable.getConference();
        TableLayout conferenceTableLayout = conferenceTable.getTableLayout();

        for (int i = 0; i < 2; i++) {
            TableRow wildcardRowView = new TableRow(activity);
            addViewsToWildcardRow(activity, wildcardRowView, leagueConference, i);
            conferenceTableLayout.addView(wildcardRowView);

            WildcardRow wildcardRow = new WildcardRow(wildcardRowView);
            conferenceTable.addWildcardRow(wildcardRow);
        }
    }

    private TableRow createPlayoffTableLabelRow(Activity activity) {
        TableRow playoffTableLabelRow = new TableRow(activity);

        TextView playoffTableTeamLabel = new TextView(activity);
        playoffTableTeamLabel.setText(R.string.select_team_short);
        playoffTableLabelRow.addView(playoffTableTeamLabel);

        TextView divisionalLabel = new TextView(activity);
        divisionalLabel.setText(R.string.divisional_label);
        divisionalLabel.setPadding(PLAYOFF_TABLE_PADDING, 0, 0, 0);
        playoffTableLabelRow.addView(divisionalLabel);

        TextView conferenceLabel = new TextView(activity);
        conferenceLabel.setText(R.string.conference_label);
        conferenceLabel.setPadding(PLAYOFF_TABLE_PADDING, 0, 0, 0);
        playoffTableLabelRow.addView(conferenceLabel);

        TextView conferenceChampLabel = new TextView(activity);
        conferenceChampLabel.setText(R.string.conference_champ_label);
        conferenceChampLabel.setPadding(PLAYOFF_TABLE_PADDING, 0, 0, 0);
        playoffTableLabelRow.addView(conferenceChampLabel);

        TextView superBowlLabel = new TextView(activity);
        superBowlLabel.setText(R.string.super_bowl_label);
        superBowlLabel.setPadding(PLAYOFF_TABLE_PADDING, 0, 0, 0);
        playoffTableLabelRow.addView(superBowlLabel);

        return playoffTableLabelRow;
    }

    private void populatePlayoffsFromSelections() {
        playoffs.clearPlayoffTeams();
        for (ConferenceTable conferenceTable : conferenceTables) {
            String conferenceName = conferenceTable.getConferenceName();
            List<DivisionChampRow> divisionChampRows = conferenceTable.getDivisionChampRows();
            for (DivisionChampRow divisionChampRow : divisionChampRows) {
                String divisionChampName = divisionChampRow.getDivisionChampName();
                String divisionName = divisionChampRow.getDivisionName();
                int seed = divisionChampRow.getSeed();
                Team divisionChamp = nfl.getTeam(divisionChampName);
                NFLPlayoffTeam playoffDivisionChamp = playoffs.createPlayoffTeam(divisionChamp);
                playoffs.setDivisionWinner(
                        conferenceName, divisionName, playoffDivisionChamp);
                playoffs.setTeamConferenceSeed(playoffDivisionChamp, seed);
            }

            List<WildcardRow> wildcardRows = conferenceTable.getWildcardRows();
            for (WildcardRow wildcardRow : wildcardRows) {
                String wildcardName = wildcardRow.getWildcardName();
                int seed = wildcardRow.getSeed();
                Team wildcardTeam = nfl.getTeam(wildcardName);
                NFLPlayoffTeam playoffWildcard = playoffs.createPlayoffTeam(wildcardTeam);
                playoffs.addWildcardTeam(conferenceName, playoffWildcard);
                playoffs.setTeamConferenceSeed(playoffWildcard, seed);
            }
        }
        playoffs.calculateChancesByRoundForAllPlayoffTeams();
    }

    private void createPlayoffTablesRowsForConference(
            Activity activity, TableLayout playoffTable, NFLPlayoffConference playoffConference) {
        List<NFLPlayoffTeam> playoffTeams = playoffConference.getTeamsInSeedOrder();
        for (int i = 0; i < playoffTeams.size(); i++) {
            NFLPlayoffTeam playoffTeam = playoffTeams.get(i);
            TableRow teamRow = new TableRow(activity);
            if (i == playoffTeams.size() - 1) {
                teamRow.setPadding(0, 0, 0, CONFERENCE_PADDING);
            }

            Team leagueTeam = playoffTeam.getTeam();
            String teamName = leagueTeam.getName();
            TextView teamNameView = new TextView(activity);
            teamNameView.setText(teamName);
            teamRow.addView(teamNameView);

            int chanceToMakeDivisional = playoffTeam.getChanceOfMakingDivisionalRound();
            TextView divisionalView = createTextViewWithNumber(activity, chanceToMakeDivisional);
            divisionalView.setPadding(PLAYOFF_TABLE_PADDING, 0, 0, 0);
            teamRow.addView(divisionalView);

            int chanceToMakeConference = playoffTeam.getChanceOfMakingConferenceRound();
            TextView conferenceView = createTextViewWithNumber(activity, chanceToMakeConference);
            conferenceView.setPadding(PLAYOFF_TABLE_PADDING, 0, 0, 0);
            teamRow.addView(conferenceView);

            int chanceToWinConference = playoffTeam.getChanceOfMakingSuperBowl();
            TextView conferenceChampView = createTextViewWithNumber(activity, chanceToWinConference);
            conferenceChampView.setPadding(PLAYOFF_TABLE_PADDING, 0, 0, 0);
            teamRow.addView(conferenceChampView);

            int chanceToWinSuperBowl = playoffTeam.getChanceOfWinningSuperBowl();
            TextView superBowlView = createTextViewWithNumber(activity, chanceToWinSuperBowl);
            superBowlView.setPadding(PLAYOFF_TABLE_PADDING, 0, 0, 0);
            teamRow.addView(superBowlView);

            playoffTable.addView(teamRow);
        }
    }

    private TextView createTextViewWithNumber(Activity activity, int number) {
        Resources resources = activity.getResources();
        TextView textView = new TextView(activity);
        String textViewString = resources.getString(R.string.display_one_number, number);
        textView.setText(textViewString);

        return textView;
    }

    private void addDivisionChampSpinnersToDivisionRow(
            Activity activity, TableRow divisionRow, Conference conference, int divisionIndex) {
        String conferenceName = conference.getName();
        List<Division> divisions = conference.getDivisions();
        Division division = divisions.get(divisionIndex);

        List<Team> teams = division.getTeams();

        String divisionWinnerName = "";
        int divisionWinnerSeed = -1;
        NFLPlayoffTeam divisionWinner = playoffs.getDivisionWinner(conferenceName, division.getName());
        if (divisionWinner != null) {
            Team divisionWinnerLeague = divisionWinner.getTeam();
            divisionWinnerName = divisionWinnerLeague.getName();
            divisionWinnerSeed = divisionWinner.getConferenceSeed();
        }

        int divisionWinnerIndex = 0;
        String[] teamNames = new String[teams.size()];
        for (int j = 0; j < teams.size(); j++) {
            Team team = teams.get(j);
            teamNames[j] = team.getName();
            if (divisionWinnerSeed > -1 && divisionWinnerName.equals(team.getName())) {
                divisionWinnerIndex = j;
            }
        }
        Spinner selectDivisionChamp = createSelectDivisionChampSpinner(
                activity, divisionWinnerIndex, teamNames);
        divisionRow.addView(selectDivisionChamp);

        Spinner selectSeed = createSeedSpinner(activity, divisions, divisionIndex, divisionWinnerSeed);
        divisionRow.addView(selectSeed);
    }

    private void addViewsToWildcardRow(Activity activity, TableRow wildcardRow, Conference leagueConference,
                                       int wildcardIndex) {
        String conferenceName = leagueConference.getName();

        TextView wildcardLabel = new TextView(activity);
        Resources resources = getResources();
        String wildcardLabelString = resources.getString(R.string.wildcard_label, conferenceName);
        wildcardLabel.setText(wildcardLabelString);
        wildcardRow.addView(wildcardLabel);

        String wildcardName = "";
        NFLPlayoffTeam playoffWildcard = playoffs.getTeamByConferenceSeed(conferenceName, 5 + wildcardIndex);
        if (playoffWildcard != null) {
            Team leagueWildcard = playoffWildcard.getTeam();
            wildcardName = leagueWildcard.getName();
        }

        List<Team> conferenceTeams = leagueConference.getTeams();
        String[] conferenceTeamNames = new String[conferenceTeams.size()];

        int selectedIndex = -1;
        for (int j = 0; j < conferenceTeams.size(); j++) {
            Team conferenceTeam = conferenceTeams.get(j);
            if (!"".equals(wildcardName) && wildcardName.equals(conferenceTeam.getName())) {
                selectedIndex = j;
            }
            conferenceTeamNames[j] = conferenceTeam.getName();
        }

        Spinner selectWildcard = createSelectWildcardSpinner(
                activity, conferenceTeamNames, wildcardIndex, selectedIndex);
        wildcardRow.addView(selectWildcard);

        TextView wildcardSeed = new TextView(activity);
        String wildcardSeedString = resources.getString(R.string.display_one_number, (wildcardIndex + 5));
        wildcardSeed.setText(wildcardSeedString);
        wildcardSeed.setPadding(20, 0, 0, 0);

        wildcardRow.addView(wildcardSeed);
    }

    private Spinner createSelectDivisionChampSpinner(
            Activity activity, int divisionWinnerIndex, String[] teamNames) {
        Spinner selectDivisionChamp = new Spinner(activity);

        ArrayAdapter<String> teamAdapter = new ArrayAdapter<>(activity,
                android.R.layout.simple_spinner_item, teamNames);
        selectDivisionChamp.setAdapter(teamAdapter);
        selectDivisionChamp.setSelection(divisionWinnerIndex);

        return selectDivisionChamp;
    }

    private Spinner createSeedSpinner(Activity activity, List<Division> divisions, int divisionIndex, int divisionWinnerSeed) {
        Spinner selectSeed = new Spinner(activity);
        Integer[] divisionChampSeeds = new Integer[divisions.size()];
        for (int j = 1; j <= divisions.size(); j++) {
            divisionChampSeeds[j - 1] = j;
        }
        ArrayAdapter<Integer> seedAdapter = new ArrayAdapter<>(activity,
                android.R.layout.simple_spinner_item, divisionChampSeeds);
        selectSeed.setAdapter(seedAdapter);
        if (divisionWinnerSeed > -1) {
            selectSeed.setSelection(divisionWinnerSeed - 1);
        } else {
            selectSeed.setSelection(divisionIndex);
        }
        return selectSeed;
    }

    private Spinner createSelectWildcardSpinner(
            Activity activity, String[] conferenceTeamNames, int wildcardIndex, int selectedIndex) {
        Spinner selectWildcard = new Spinner(activity);
        ArrayAdapter<String> wildcardAdapter = new ArrayAdapter<>(activity,
                android.R.layout.simple_spinner_item, conferenceTeamNames);
        selectWildcard.setAdapter(wildcardAdapter);
        if (selectedIndex > -1) {
            selectWildcard.setSelection(selectedIndex);
        } else {
            selectWildcard.setSelection(wildcardIndex + 1);
        }
        return selectWildcard;
    }
}
