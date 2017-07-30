package season.nfl.nflseasoncalculatorapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;
import java.util.List;

import nfl.season.input.NFLFileWriterFactory;
import nfl.season.input.NFLTeamSettings;
import nfl.season.league.League;
import nfl.season.league.Matchup;
import nfl.season.league.NFLTeamEnum;
import nfl.season.league.Team;
import season.nfl.nflseasoncalculatorapp.MainActivity;
import season.nfl.nflseasoncalculatorapp.R;
import season.nfl.nflseasoncalculatorapp.util.MessageDisplayer;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AllTeamsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AllTeamsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllTeamsFragment extends Fragment {

    private League nfl;

    private NFLFileWriterFactory fileWriterFactory;
    private NFLTeamSettings teamSettings;
    private String folderPath;

    public AllTeamsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param nfl NFL League
     * @param selectedTeam Selected Team
     * @return A new instance of fragment AllTeamsFragment.
     */
    public static AllTeamsFragment newInstance(League nfl, String selectedTeam) {
        AllTeamsFragment fragment = new AllTeamsFragment();
        Bundle args = new Bundle();
        args.putSerializable(MainActivity.LEAGUE_KEY, nfl);
        args.putString(TeamsFragment.SELECTED_TEAM_KEY, selectedTeam);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            nfl = (League) getArguments().getSerializable(MainActivity.LEAGUE_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_teams, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
        final Activity activity = getActivity();

        fileWriterFactory = new NFLFileWriterFactory();
        teamSettings = new NFLTeamSettings();

        createSingleTeamButtons(activity, view);
        setUpTeamSettingsButton(activity);
        setUpAllToPowerRankingsButton(activity);
        setUpAllToEloRatingsButton(activity);
        setUpAllToHomeFieldButton(activity);
    }

    @Override
    public void onAttach(final Context context) {
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

    private void createSingleTeamButtons(Activity activity, View view) {
        LinearLayout teamsListLayout = (LinearLayout) view.findViewById(R.id.teams_list);
        for (NFLTeamEnum nflTeamEnum : NFLTeamEnum.values()) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            Button newTeamButton = new Button(activity);

            final String teamName = nflTeamEnum.getTeamName();
            newTeamButton.setText(teamName);

            newTeamButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Fragment newTeamFragment = TeamFragment.newInstance(nfl, teamName);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.addToBackStack(null);
                    transaction.hide(AllTeamsFragment.this);
                    transaction.add(R.id.teams_fragment_holder, newTeamFragment);

                    transaction.commit();
                }
            });

            teamsListLayout.addView(newTeamButton, lp);
        }
    }

    private void setUpTeamSettingsButton(final Activity activity) {
        final Button saveTeamSettingsButton = (Button) activity.findViewById(R.id.saveTeamSettingsButton);
        saveTeamSettingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    teamSettings.saveToSettingsFile(nfl, folderPath, fileWriterFactory);
                    MessageDisplayer.displayMessage(activity, "All Team Settings Saved");
                } catch (IOException e) {
                    MessageDisplayer.displayMessage(activity, "Team Settings Save FAILED");
                }
            }
        });
    }

    private void setUpAllToPowerRankingsButton(final Activity activity) {
        final Button setAllToPowerRankingsButton = (Button) activity.findViewById(R.id.powerRankingsButton);
        setAllToPowerRankingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MessageDisplayer.displayMessage(activity, "All Team Matchups Set To Use Power Rankings");
                List<Team> teams = nfl.getTeams();
                for (Team team : teams) {
                    team.calculateAllMatchupsUsingPowerRankings();
                }
            }
        });
    }

    private void setUpAllToEloRatingsButton(final Activity activity) {
        final Button setAllToEloRatingsButton = (Button) activity.findViewById(R.id.eloRatingsButton);
        setAllToEloRatingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MessageDisplayer.displayMessage(activity, "All Team Matchups Set To Use Elo Ratings");
                List<Team> teams = nfl.getTeams();
                for (Team team : teams) {
                    team.calculateAllMatchupsUsingEloRatings();
                }
            }
        });
    }

    private void setUpAllToHomeFieldButton(final Activity activity) {
        final Button setAllToHomeFieldButton = (Button) activity.findViewById(R.id.homeFieldButton);
        setAllToHomeFieldButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MessageDisplayer.displayMessage(activity, "All Team Matchups Set To Use Home Field Advantage");
                List<Team> teams = nfl.getTeams();
                for (Team team : teams) {
                    String teamName = team.getName();
                    List<Matchup> matchups = team.getMatchups();
                    for (Matchup matchup : matchups) {
                        matchup.calculateHomeWinChanceFromHomeFieldAdvantage(teamName);
                    }
                }
            }
        });
    }

}
