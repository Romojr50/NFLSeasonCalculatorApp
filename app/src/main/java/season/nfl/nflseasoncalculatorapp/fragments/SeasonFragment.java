package season.nfl.nflseasoncalculatorapp.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import nfl.season.league.League;
import nfl.season.league.Team;
import nfl.season.season.NFLSeason;
import nfl.season.season.SeasonGame;
import nfl.season.season.SeasonWeek;
import season.nfl.nflseasoncalculatorapp.MainActivity;
import season.nfl.nflseasoncalculatorapp.R;
import season.nfl.nflseasoncalculatorapp.util.LoadSeasonTask;
import season.nfl.nflseasoncalculatorapp.util.MessageDisplayer;
import season.nfl.nflseasoncalculatorapp.util.SimulateSeasonTask;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SeasonFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SeasonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SeasonFragment extends Fragment {

    private static final String SEASON_KEY = "NFL_Season";

    private NFLSeason season;

    private String folderPath;

    public SeasonFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param nfl League
     * @return A new instance of fragment SeasonFragment.
     */
    public static SeasonFragment newInstance(League nfl, NFLSeason season) {
        SeasonFragment fragment = new SeasonFragment();
        Bundle args = new Bundle();
        args.putSerializable(MainActivity.LEAGUE_KEY, nfl);
        args.putSerializable(SEASON_KEY, season);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            season = (NFLSeason) getArguments().getSerializable(SEASON_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_season, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
        final Activity activity = getActivity();

        setLoadSeasonButtonListener(activity);
        setUpPickWeekSpinner(activity);
        setViewWeekButton(activity);
        setSimulateSeasonsButton(activity);
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

    private void setLoadSeasonButtonListener(final Activity activity) {
        Button loadSeasonButton = (Button) activity.findViewById(R.id.loadSeasonButton);
        loadSeasonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progress = new ProgressDialog(activity);
                progress.setTitle("Loading");
                progress.setMessage("Loading NFL season from internet...");
                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress.show();
                LoadSeasonTask.LoadAsyncResponse loadAsyncResponse = new LoadSeasonTask.LoadAsyncResponse(){
                    @Override
                    public void processFinish(String output){
                        progress.dismiss();
                        MessageDisplayer.displayMessage(activity, output);
                    }
                };
                LoadSeasonTask loadSeasonTask = new LoadSeasonTask(loadAsyncResponse, activity, folderPath);
                loadSeasonTask.execute(season);
            }
        });
    }

    private void setUpPickWeekSpinner(Activity activity) {
        Spinner pickWeekSpinner = (Spinner) activity.findViewById(R.id.pickWeekSpinner);
        SeasonWeek[] weeks = season.getWeeks();
        Integer[] weekNumbers = new Integer[weeks.length];
        for (int i = 1; i <= weeks.length; i++) {
            weekNumbers[i - 1] = i;
        }
        ArrayAdapter<Integer> pickWeekAdapter = new ArrayAdapter<>(
                activity, android.R.layout.simple_spinner_item, weekNumbers);
        pickWeekSpinner.setAdapter(pickWeekAdapter);
    }

    private void setViewWeekButton(final Activity activity) {
        Button viewWeekButton = (Button) activity.findViewById(R.id.viewWeekButton);
        viewWeekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner pickWeekSpinner = (Spinner) activity.findViewById(R.id.pickWeekSpinner);
                Object selectedWeekObject = pickWeekSpinner.getSelectedItem();
                Integer selectedWeekNumber = (Integer) selectedWeekObject;

                SeasonWeek selectedWeek = season.getWeek(selectedWeekNumber);

                LinearLayout weekLayout = (LinearLayout) activity.findViewById(R.id.weekLayout);
                weekLayout.setVisibility(View.VISIBLE);

                Resources resources = activity.getResources();
                TextView weekLabel = (TextView) activity.findViewById(R.id.weekNumberLabel);
                String weekNumberText = resources.getString(R.string.week_number, selectedWeekNumber);
                weekLabel.setText(weekNumberText);

                TableLayout weekTable = (TableLayout) activity.findViewById(R.id.weekTable);
                while(weekTable.getChildCount() > 1) {
                    weekTable.removeViewAt(1);
                }

                addGameRowsToWeekTable(selectedWeek, weekTable, activity);
            }
        });
    }

    private void setSimulateSeasonsButton(final Activity activity) {
        Button simulateSeasonsButton = (Button) activity.findViewById(R.id.simulateSeasonsButton);
        simulateSeasonsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressBar simulateProgress = (ProgressBar) activity.findViewById(R.id.seasonProgress);
                TableLayout simulateSeasonTable = (TableLayout) activity.findViewById(R.id.simulateSeasonsTable);
                SimulateSeasonTask simulateSeasonTask = new SimulateSeasonTask(simulateProgress, activity, simulateSeasonTable);
                simulateSeasonTask.execute(season);
            }
        });
    }

    private void addGameRowsToWeekTable(SeasonWeek selectedWeek, TableLayout weekTable, Activity activity) {
        Resources resources = activity.getResources();

        List<SeasonGame> games = selectedWeek.getSeasonGames();
        for (SeasonGame game : games) {
            TableRow gameRow = new TableRow(activity);

            TextView awayTeamText = new TextView(activity);
            awayTeamText.setText(game.getAwayTeam().getName());
            gameRow.addView(awayTeamText);

            TextView awayScoreText = createTextViewWithPadding(activity, "" + game.getAwayScore());
            gameRow.addView(awayScoreText);

            TextView homeTeamText = createTextViewWithPadding(activity, game.getHomeTeam().getName());
            gameRow.addView(homeTeamText);
            TextView homeScoreText = createTextViewWithPadding(activity, "" + game.getHomeScore());
            gameRow.addView(homeScoreText);

            addWinnerToGameRow(activity, game, gameRow);

            weekTable.addView(gameRow);
        }
    }

    private void addWinnerToGameRow(Activity activity, SeasonGame game, TableRow gameRow) {
        Resources resources = activity.getResources();

        Team winner = game.getWinner();
        if (winner != null) {
            TextView winnerText = createTextViewWithPadding(activity, winner.getName());
            gameRow.addView(winnerText);
        } else {
            if (game.wasATie()) {
                TextView tieText = createTextViewWithPadding(activity, "Tie");
                gameRow.addView(tieText);
            }
        }
    }

    private TextView createTextViewWithPadding(Activity activity, String text) {
        TextView textView = new TextView(activity);
        textView.setText(text);
        textView.setPadding(MainActivity.tablePaddingInPx, 0, 0, 0);
        return textView;
    }
}
