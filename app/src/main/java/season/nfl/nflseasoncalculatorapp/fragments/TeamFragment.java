package season.nfl.nflseasoncalculatorapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import nfl.season.league.League;
import nfl.season.league.NFLTeamEnum;
import nfl.season.league.Team;
import season.nfl.nflseasoncalculatorapp.MainActivity;
import season.nfl.nflseasoncalculatorapp.R;
import season.nfl.nflseasoncalculatorapp.input.HomeAwayWinModeSpinner;
import season.nfl.nflseasoncalculatorapp.input.NeutralWinModeSpinner;
import season.nfl.nflseasoncalculatorapp.input.WinChanceEditText;
import season.nfl.nflseasoncalculatorapp.util.InputSetter;
import season.nfl.nflseasoncalculatorapp.util.MatchupTableTask;
import season.nfl.nflseasoncalculatorapp.util.MessageDisplayer;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TeamFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TeamFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeamFragment extends Fragment {

    private League nfl;
    private String selectedTeam;
    private Team team;
    private MatchupTableTask matchupTableTask;

    public TeamFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param nfl NFL League
     * @param selectedTeam Selected Team
     * @return A new instance of fragment TeamFragment.
     */
    public static TeamFragment newInstance(League nfl, String selectedTeam) {
        TeamFragment fragment = new TeamFragment();
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
            selectedTeam = getArguments().getString(TeamsFragment.SELECTED_TEAM_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_team, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
        Activity activity = getActivity();

        team = nfl.getTeam(selectedTeam);

        TextView textView = (TextView) activity.findViewById(R.id.team_name);
        textView.setText(selectedTeam);

        setUpInputFields(activity);
        setUpButtons(activity);
        ProgressBar matchupProgress = (ProgressBar) activity.findViewById(R.id.matchupProgress);
        matchupTableTask = new MatchupTableTask(activity, matchupProgress);
        matchupTableTask.execute(team);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof OnFragmentInteractionListener)) {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        if (matchupTableTask != null) {
            matchupTableTask.cancel(true);
        }
        super.onDestroyView();
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

    private void setUpButtons(final Activity activity) {
        setUpDefaultButton(activity);
        setUpBackButton(activity);
        setUpSaveButton(activity);
        setUpSaveTeamSettingsButton(activity);
        setUpPowerRankingsAllButton(activity);
        setUpEloRatingsAllButton(activity);
        setUpHomeFieldAllButton(activity);
    }

    private void setUpBackButton(final Activity activity) {
        final Resources resources = activity.getResources();

        final Button backButton = (Button) activity.findViewById(R.id.cancelButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final EditText powerRankingsInput = (EditText) activity.findViewById(R.id.powerRankingsInput);
                int powerRanking = team.getPowerRanking();
                InputSetter.setTextToInputNumber(resources, powerRankingsInput, powerRanking);
                powerRankingsInput.setOnFocusChangeListener(null);
                goBackToAllTeamsFragment();
            }
        });
    }

    private void setUpSaveButton(final Activity activity) {
        final Button saveButton = (Button) activity.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (validateInput(activity)) {
                    setTeamSettingsOntoTeam(activity);
                    setMatchupSettingsOntoMatchups(activity);
                    goBackToAllTeamsFragment();
                }
            }
        });
    }

    private void setUpDefaultButton(final Activity activity) {
        final Button defaultButton = (Button) activity.findViewById(R.id.defaultButton);
        defaultButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                nfl.resetTeamToDefault(selectedTeam);
                setUpInputFields(activity);
                setMatchupSettingsOntoMatchups(activity);
            }
        });
    }

    private void setUpSaveTeamSettingsButton(final Activity activity) {
        final Button saveTeamSettingsButton = (Button) activity.findViewById(R.id.saveTeamSettings);
        saveTeamSettingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (validateInput(activity)) {
                    setTeamSettingsOntoTeam(activity);
                    List<View> matchupCells = getAllMatchupCells(activity);
                    for (View matchupCell : matchupCells) {
                        if (matchupCell instanceof WinChanceEditText) {
                            WinChanceEditText winChanceEditText = (WinChanceEditText) matchupCell;
                            Resources resources = activity.getResources();
                            winChanceEditText.setTextFromMatchup(resources);
                        }
                    }
                }
            }
        });
    }

    private void setUpPowerRankingsAllButton(final Activity activity) {
        final Button powerRankingsAllButton = (Button) activity.findViewById(R.id.powerRankingsAllButton);
        powerRankingsAllButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setAllNeutralSpinnersToSelection(activity, R.string.power_rankings_win_mode);
            }
        });
    }

    private void setUpEloRatingsAllButton(final Activity activity) {
        final Button eloRatingsAllButton = (Button) activity.findViewById(R.id.eloRatingsAllButton);
        eloRatingsAllButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setAllNeutralSpinnersToSelection(activity, R.string.elo_ratings_win_mode);
            }
        });
    }

    private void setUpHomeFieldAllButton(final Activity activity) {
        final Button homeFieldAllButton = (Button) activity.findViewById(R.id.homeFieldAdvantageAllButton);
        homeFieldAllButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setAllHomeAwaySpinnersToSelection(activity);
            }
        });
    }

    private void goBackToAllTeamsFragment() {
        getFragmentManager().popBackStackImmediate();
    }

    private boolean validateInput(Activity activity) {
        boolean isValid = true;

        final EditText powerRankingsInput = (EditText) activity.findViewById(R.id.powerRankingsInput);
        if (!validatePowerRanking(powerRankingsInput)) {
            isValid = false;
        }

        return isValid;
    }

    private boolean validatePowerRanking(EditText powerRankingsInput) {
        boolean isValid = true;

        int newPowerRanking = Integer.parseInt(powerRankingsInput.getText().toString());

        int maxPowerRanking = NFLTeamEnum.values().length;
        if (newPowerRanking < 1 || newPowerRanking > maxPowerRanking) {
            powerRankingsInput.setError("Power Ranking must be between 1 and " + maxPowerRanking);
            isValid = false;
        }

        return isValid;
    }

    private void setTeamSettingsOntoTeam(Activity activity) {
        final EditText powerRankingsInput = (EditText) activity.findViewById(R.id.powerRankingsInput);
        int oldPowerRanking = team.getPowerRanking();
        int newPowerRanking = Integer.parseInt(powerRankingsInput.getText().toString());
        Team teamWithNewRanking = nfl.getTeamWithPowerRanking(newPowerRanking);
        if (teamWithNewRanking != null) {
            String teamWithNewRankingName = teamWithNewRanking.getName();
            if (!selectedTeam.equals(teamWithNewRankingName)) {
                teamWithNewRanking.setPowerRanking(oldPowerRanking);
            }
        }
        team.setPowerRanking(newPowerRanking);

        final EditText eloRatingsInput = (EditText) activity.findViewById(R.id.eloRatingInput);
        int newEloRating = Integer.parseInt(eloRatingsInput.getText().toString());
        team.setEloRating(newEloRating);

        final EditText homeFieldInput = (EditText) activity.findViewById(R.id.homeFieldInput);
        int newHomeField = Integer.parseInt(homeFieldInput.getText().toString());
        team.setHomeFieldAdvantage(newHomeField);
    }

    private void setMatchupSettingsOntoMatchups(Activity activity) {
        List<View> matchupCellList = getAllMatchupCells(activity);
        for (int j = 0; j < matchupCellList.size(); j++) {
            View cell = matchupCellList.get(j);
            if (cell instanceof NeutralWinModeSpinner) {
                NeutralWinModeSpinner winModeSpinner = (NeutralWinModeSpinner) cell;
                winModeSpinner.saveNeutralWinChance();
            } else if (cell instanceof HomeAwayWinModeSpinner) {
                HomeAwayWinModeSpinner winModeSpinner = (HomeAwayWinModeSpinner) cell;
                winModeSpinner.saveHomeAwayWinChance();
            }
        }
    }

    private void setUpInputFields(final Activity activity) {
        Resources resources = activity.getResources();

        setUpPowerRankingsInput(activity, resources);

        final EditText eloRatingsInput = (EditText) activity.findViewById(R.id.eloRatingInput);
        InputSetter.setTextToInputNumber(resources, eloRatingsInput, team.getEloRating());

        final EditText homeFieldInput = (EditText) activity.findViewById(R.id.homeFieldInput);
        InputSetter.setTextToInputNumber(resources, homeFieldInput, team.getHomeFieldAdvantage());
    }

    private void setAllNeutralSpinnersToSelection(Activity activity, int stringId) {
        int selectionId = getIndexOfSelectionInArray(activity, R.array.neutral_mode_array,
                stringId);

        List<View> matchupCells = getAllMatchupCells(activity);
        for (View matchupCell : matchupCells) {
            if (matchupCell instanceof NeutralWinModeSpinner) {
                NeutralWinModeSpinner neutralSpinner = (NeutralWinModeSpinner) matchupCell;
                neutralSpinner.setSelection(selectionId);
            }
        }
    }

    private void setAllHomeAwaySpinnersToSelection(Activity activity) {
        int selectionId = getIndexOfSelectionInArray(activity, R.array.home_mode_array,
                R.string.home_field_advantage_mode);

        List<View> matchupCells = getAllMatchupCells(activity);
        for (View matchupCell : matchupCells) {
            if (matchupCell instanceof HomeAwayWinModeSpinner) {
                HomeAwayWinModeSpinner homeAwaySpinner = (HomeAwayWinModeSpinner) matchupCell;
                homeAwaySpinner.setSelection(selectionId);
            }
        }
    }

    private List<View> getAllMatchupCells(Activity activity) {
        List<View> cellList = new ArrayList<>();

        TableLayout winChanceTable = (TableLayout) activity.findViewById(R.id.matchupTable);
        int numberOfRows = winChanceTable.getChildCount();
        for (int i = 0; i < numberOfRows; i++) {
            TableRow tableRow = (TableRow) winChanceTable.getChildAt(i);
            int numberOfCells = tableRow.getChildCount();
            for (int j = 0; j < numberOfCells; j++) {
                View cell = tableRow.getChildAt(j);
                cellList.add(cell);
            }
        }

        return cellList;
    }

    private void setUpPowerRankingsInput(final Activity activity, final Resources resources) {
        final EditText powerRankingsInput = (EditText) activity.findViewById(R.id.powerRankingsInput);
        InputSetter.setTextToInputNumber(resources, powerRankingsInput, team.getPowerRanking());
        powerRankingsInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String powerRankingText = powerRankingsInput.getText().toString();
                    try {
                        int newPowerRanking = Integer.parseInt(powerRankingText);
                        int oldPowerRanking = team.getPowerRanking();
                        if (newPowerRanking != oldPowerRanking) {
                            Team teamWithRanking = nfl.getTeamWithPowerRanking(newPowerRanking);
                            if (teamWithRanking != null) {
                                String teamWithRankingName = teamWithRanking.getName();
                                String messageToDisplay = "Will switch rankings with " +
                                        teamWithRankingName + " upon saving";
                                MessageDisplayer.displayMessage(activity, messageToDisplay);
                            }
                        }
                    } catch (Exception e) {
                        MessageDisplayer.displayMessage(activity, "Error setting Power Ranking");
                    }
                }
            }
        });
    }

    private int getIndexOfSelectionInArray(Activity activity, int arrayId, int stringId) {
        int returnIndex = -1;

        final Resources resources = activity.getResources();
        final String[] modeSettingsArray = resources.getStringArray(arrayId);

        int selectionId = 0;
        String stringToFind = resources.getString(stringId);
        for (String modeSetting : modeSettingsArray) {
            if (modeSetting.equals(stringToFind)) {
                returnIndex = selectionId;
                break;
            }
            selectionId++;
        }

        return returnIndex;
    }

}
