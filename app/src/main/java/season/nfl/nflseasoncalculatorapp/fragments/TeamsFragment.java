package season.nfl.nflseasoncalculatorapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nfl.season.league.League;
import season.nfl.nflseasoncalculatorapp.MainActivity;
import season.nfl.nflseasoncalculatorapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TeamsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TeamsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeamsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String SELECTED_TEAM_KEY = "selectedTeam";

    private League nfl;

    public TeamsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param nfl NFL
     * @param selectedTeam Selected Team
     * @return A new instance of fragment TeamsFragment.
     */
    public static TeamsFragment newInstance(League nfl, String selectedTeam) {
        TeamsFragment fragment = new TeamsFragment();
        Bundle args = new Bundle();
        args.putSerializable(MainActivity.LEAGUE_KEY, nfl);
        args.putString(SELECTED_TEAM_KEY, selectedTeam);
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
        return inflater.inflate(R.layout.fragment_teams, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Fragment allTeamsFragment = AllTeamsFragment.newInstance(nfl, null);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.add(R.id.teams_fragment_holder, allTeamsFragment);
        transaction.commit();
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
}
