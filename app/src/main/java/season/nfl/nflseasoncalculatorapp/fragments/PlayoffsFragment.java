package season.nfl.nflseasoncalculatorapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import nfl.season.league.Conference;
import nfl.season.league.Division;
import nfl.season.league.League;
import nfl.season.league.Team;
import nfl.season.playoffs.NFLPlayoffConference;
import nfl.season.playoffs.NFLPlayoffs;
import season.nfl.nflseasoncalculatorapp.MainActivity;
import season.nfl.nflseasoncalculatorapp.R;
import season.nfl.nflseasoncalculatorapp.util.InputSetter;

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

    private League nfl;

    private NFLPlayoffs playoffs;

    private OnFragmentInteractionListener mListener;

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
    // TODO: Rename and change types and number of parameters
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
        Activity activity = getActivity();

        LinearLayout selectPlayoffTeamsLayout = (LinearLayout) view.findViewById(R.id.selectPlayoffTeamsLayout);

        List<NFLPlayoffConference> playoffConferences = playoffs.getConferences();
        for (NFLPlayoffConference playoffConference : playoffConferences) {
            Conference leagueConference = playoffConference.getConference();
            String conferenceName = leagueConference.getName();

            LinearLayout conferenceLayout = new LinearLayout(activity);
            conferenceLayout.setOrientation(LinearLayout.VERTICAL);

            TextView conferenceLabel = new TextView(activity);
            conferenceLabel.setText(conferenceName);
            conferenceLayout.addView(conferenceLabel);

            TableLayout conferenceTable = new TableLayout(activity);
            conferenceLayout.addView(conferenceTable);

            addLabelsRowToConferenceTable(activity, conferenceTable);
            addDivisionChampRowsToConferenceTable(activity, conferenceTable, leagueConference);

            for (int i = 0; i < 2; i++) {
                TableRow wildcardRow = new TableRow(activity);

                TextView wildcardLabel = new TextView(activity);
                Resources resources = getResources();
                String wildcardLabelString = resources.getString(R.string.wildcard_label, conferenceName);
                wildcardLabel.setText(wildcardLabelString);
                wildcardRow.addView(wildcardLabel);

                Spinner selectWildcard = new Spinner(activity);
                List<Team> conferenceTeams = leagueConference.getTeams();
                String[] conferenceTeamNames = new String[conferenceTeams.size()];
                for (int j = 0; j < conferenceTeams.size(); j++) {
                    Team conferenceTeam = conferenceTeams.get(j);
                    conferenceTeamNames[j] = conferenceTeam.getName();
                }
                ArrayAdapter<String> wildcardAdapter = new ArrayAdapter<>(activity,
                        android.R.layout.simple_spinner_item, conferenceTeamNames);
                selectWildcard.setAdapter(wildcardAdapter);
                wildcardRow.addView(selectWildcard);

                TextView wildcardSeed = new TextView(activity);
                String wildcardSeedString = resources.getString(R.string.display_one_number, (i + 5));
                wildcardSeed.setText(wildcardSeedString);
                wildcardRow.addView(wildcardSeed);

                conferenceTable.addView(wildcardRow);
            }

            selectPlayoffTeamsLayout.addView(conferenceLayout);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void addLabelsRowToConferenceTable(Activity activity, TableLayout conferenceTable) {
        TableRow labelsRow = new TableRow(activity);
        conferenceTable.addView(labelsRow);

        TextView divisionWildcardLabel = new TextView(activity);
        divisionWildcardLabel.setText(R.string.division_wildcard);
        labelsRow.addView(divisionWildcardLabel);

        TextView selectTeamLabel = new TextView(activity);
        selectTeamLabel.setText(R.string.select_team_short);
        labelsRow.addView(selectTeamLabel);

        TextView seedLabel = new TextView(activity);
        seedLabel.setText(R.string.seed);
        labelsRow.addView(seedLabel);
    }

    private void addDivisionChampRowsToConferenceTable(Activity activity, TableLayout conferenceTable,
                                                       Conference leagueConference) {
        Resources resources = activity.getResources();
        String conferenceName = leagueConference.getName();

        List<Division> divisions = leagueConference.getDivisions();
        for (int i = 0; i < divisions.size(); i++) {
            Division division = divisions.get(i);
            String divisionName = division.getName();

            TableRow divisionRow = new TableRow(activity);
            conferenceTable.addView(divisionRow);

            String divisionChampLabelString = resources.getString(R.string.division_champion,
                    (conferenceName+ " " + divisionName));
            TextView divisionChampLabel = new TextView(activity);
            divisionChampLabel.setText(divisionChampLabelString);
            divisionRow.addView(divisionChampLabel);

            addDivisionChampSpinnersToDivisionRow(activity, divisionRow, divisions, i);
        }
    }

    private void addDivisionChampSpinnersToDivisionRow(Activity activity, TableRow divisionRow,
                                                       List<Division> divisions, int divisionIndex) {
        Division division = divisions.get(divisionIndex);

        List<Team> teams = division.getTeams();
        Spinner selectDivisionChamp = new Spinner(activity);
        divisionRow.addView(selectDivisionChamp);

        String[] teamNames = new String[teams.size()];
        for (int j = 0; j < teams.size(); j++) {
            Team team = teams.get(j);
            teamNames[j] = team.getName();
        }
        ArrayAdapter<String> teamAdapter = new ArrayAdapter<>(activity,
                android.R.layout.simple_spinner_item, teamNames);
        selectDivisionChamp.setAdapter(teamAdapter);

        Spinner selectSeed = new Spinner(activity);
        Integer[] divisionChampSeeds = new Integer[divisions.size()];
        for (int j = 1; j <= divisions.size(); j++) {
            divisionChampSeeds[j - 1] = j;
        }
        ArrayAdapter<Integer> seedAdapter = new ArrayAdapter<Integer>(activity,
                android.R.layout.simple_spinner_item, divisionChampSeeds);
        selectSeed.setAdapter(seedAdapter);
        selectSeed.setSelection(divisionIndex);
        divisionRow.addView(selectSeed);
    }
}
