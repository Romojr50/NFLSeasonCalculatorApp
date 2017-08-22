package season.nfl.nflseasoncalculatorapp.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;

import java.io.IOException;
import java.net.URL;

import nfl.season.input.NFLFileReaderFactory;
import nfl.season.input.NFLFileWriterFactory;
import nfl.season.input.NFLRegularSeasonSave;
import nfl.season.league.League;
import nfl.season.scorestrip.ScoreStripMapper;
import nfl.season.scorestrip.ScoreStripReader;
import nfl.season.season.NFLSeason;
import season.nfl.nflseasoncalculatorapp.R;

public class LoadSeasonTask extends AsyncTask<NFLSeason, Integer, String> {

    public interface LoadAsyncResponse {
        void processFinish(String output);
    }

    public LoadAsyncResponse delegate = null;

    private Activity activity;

    private String folderPath;

    private boolean success = false;

    public LoadSeasonTask(LoadAsyncResponse delegate, Activity activity, String folderPath){
        this.delegate = delegate;
        this.activity = activity;
        this.folderPath = folderPath;
    }

    protected String doInBackground(NFLSeason... seasons) {
        String result = "Load Season FAILED";

        NFLSeason season = null;
        if (seasons.length == 1) {
            season = seasons[0];
        }

        if (season != null) {
            League nfl = season.getLeague();
            ScoreStripReader scoreStripReader = new ScoreStripReader();
            ScoreStripMapper scoreStripMapper = new ScoreStripMapper(nfl);
            NFLRegularSeasonSave seasonSave = new NFLRegularSeasonSave();
            NFLFileWriterFactory fileWriterFactory = new NFLFileWriterFactory();
            try {
                season.loadSeason(scoreStripReader, scoreStripMapper, seasonSave, fileWriterFactory, folderPath);
                success = true;
                result = "Season Loaded from Internet";
            } catch (Exception e) {
                e.printStackTrace();
                NFLFileReaderFactory fileReaderFactory = new NFLFileReaderFactory();
                try {
                    String seasonString = seasonSave.loadSeasonSave(fileReaderFactory, folderPath);
                    seasonSave.populateSeasonFromSeasonString(seasonString, season);
                    success = true;
                    result = "Connection failed; Season Loaded from saved file";
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        if (success) {
            LinearLayout seasonOperationLayout = (LinearLayout) activity.findViewById(R.id.seasonOperationLayout);
            seasonOperationLayout.setVisibility(View.VISIBLE);
        }
        delegate.processFinish(result);
    }

}
