package season.nfl.nflseasoncalculatorapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import nfl.season.league.Conference;
import nfl.season.league.League;
import nfl.season.playoffs.NFLPlayoffConference;
import nfl.season.playoffs.NFLPlayoffs;
import season.nfl.nflseasoncalculatorapp.MainActivity;
import season.nfl.nflseasoncalculatorapp.R;

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
        for (NFLPlayoffConference playoffconference : playoffConferences) {
            Conference leagueConference = playoffconference.getConference();
            String conferenceName = leagueConference.getName();

            LinearLayout conferenceLayout = new LinearLayout(activity);

            TextView conferenceLabel = new TextView(activity);
            conferenceLabel.setText(conferenceName);
            conferenceLayout.addView(conferenceLabel);

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
}
