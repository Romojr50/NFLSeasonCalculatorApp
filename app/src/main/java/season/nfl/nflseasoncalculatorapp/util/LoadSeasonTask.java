package season.nfl.nflseasoncalculatorapp.util;

import android.os.AsyncTask;

import java.net.URL;

import nfl.season.input.NFLFileWriterFactory;
import nfl.season.input.NFLRegularSeasonSave;
import nfl.season.league.League;
import nfl.season.scorestrip.ScoreStripMapper;
import nfl.season.scorestrip.ScoreStripReader;
import nfl.season.season.NFLSeason;

public class LoadSeasonTask extends AsyncTask<NFLSeason, Integer, String> {

    public interface LoadAsyncResponse {
        void processFinish(String output);
    }

    public LoadAsyncResponse delegate = null;

    public LoadSeasonTask(LoadAsyncResponse delegate){
        this.delegate = delegate;
    }

    protected String doInBackground(NFLSeason... seasons) {
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
                season.loadSeason(scoreStripReader, scoreStripMapper, seasonSave, fileWriterFactory);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }

}
