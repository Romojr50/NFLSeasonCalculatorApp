package season.nfl.nflseasoncalculatorapp.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import java.io.File;

import nfl.season.input.NFLFileWriterFactory;
import nfl.season.input.NFLRegularSeasonSave;
import nfl.season.league.League;
import nfl.season.playoffs.NFLPlayoffs;
import nfl.season.scorestrip.ScoreStripMapper;
import nfl.season.scorestrip.ScoreStripReader;
import nfl.season.season.NFLManySeasonSimulator;
import nfl.season.season.NFLSeason;
import nfl.season.season.NFLTiebreaker;
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

    private League nfl;

    private NFLSeason season;

    private String folderPath;

    private OnFragmentInteractionListener mListener;

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
            nfl = (League) getArguments().getSerializable(MainActivity.LEAGUE_KEY);
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
                String selectedWeekString = season.getWeekString(selectedWeek);

                TextView viewWeekDisplay = (TextView) activity.findViewById(R.id.viewWeekDisplay);
                viewWeekDisplay.setText(selectedWeekString);
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
}
