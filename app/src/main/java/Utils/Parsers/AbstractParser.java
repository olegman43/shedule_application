package Utils.Parsers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Utils.Constants;
import Utils.Pair;
import Utils.Utilities;
import data.DataBaseHelper;

/**
 * Created by Mr.Nobody43 on 15.01.2018.
 */

public class AbstractParser extends AsyncTask<String, Void, Void> implements IParser {

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public  AbstractParser(){}

    public AbstractParser(Context mContext)
    {
        this.mContext = mContext;
        myDb = new DataBaseHelper(mContext);
    }

    @Override
    protected Void doInBackground(String... params) {
        Document doc = null;

        if(isNetworkAvailable()){
            doc = DownloadSchedule(params[0], params[1]);
        }
        else {
            db = myDb.getReadableDatabase();

            Cursor c = db.query(DataBaseHelper.TABLE_NAME, null, null, null, null, null, null);

            if (c.moveToFirst()) {
                boolean flag = true;
                while (true) {
                    if (c.isAfterLast()) break;

                    int idIndex = c.getColumnIndex(DataBaseHelper.ID);
                    int htmlIndex = c.getColumnIndex(DataBaseHelper.HTML_CODE);

                    String offlinedata = c.getString(htmlIndex);
                    String bdId = c.getString(idIndex);
                    if (params[0].equals(bdId)) {
                        doc = Jsoup.parse(offlinedata);
                        flag = false;
                        break;
                    } else c.moveToNext();
                }
            }
        }

        if(doc != null) {
            ParseDocument(doc);
        }

        return null;
    }

    public Document DownloadSchedule(String queury, String semestr) {
        Document doc = null;
        try {
            doc = Jsoup.connect(Constants.URL + queury + Constants.POTOK + Constants.getCurPot() + Constants.SEMESTR + semestr).get();

            db = myDb.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put(DataBaseHelper.ID, queury);
            cv.put(DataBaseHelper.HTML_CODE, doc.html());

            db.insert(DataBaseHelper.TABLE_NAME, null, cv);

            myDb.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return doc;
    }

    public void ParseDocument(Document doc) {
        Element tableWeek = doc.getElementById(Constants.WEEK_SCHEDULE);
        Element tableExams = doc.getElementById(Constants.EXAMS_SCHEDULE);

        _times = new ArrayList<String>();

        _schedule_main = ParseTable(tableWeek);
        _schedule_exams = ParseTable(tableExams);

        return;
    }

    private List<List<Pair<String, String>>> ParseTable(Element table)
    {
        Elements trs = table.getElementsByTag(Constants.PARSE_TAG_DAYS);

        List<List<Pair<String, String>>> schedule = new ArrayList<>();

        /*
            Цикл идёт по количеству тегов tr, делим на два, потому что
            две недели и минус один из-за шапки.
         */
        for (int cnt = Constants.DEFAULT_VALUE_CNT_PARSER; cnt < trs.size()  / 2 - 1; ++cnt)
            schedule.add(new ArrayList<Pair<String, String>>());

        int cntI = Constants.DEFAULT_VALUE_CNT_PARSER;
        int curDay = Constants.DEFAULT_VALUE_CNT_PARSER;

        for (Element curTr : trs) {
            Elements tds = curTr.getElementsByTag(Constants.PARSE_TAG_ELEMENTS);
            int cntJ = Constants.DEFAULT_VALUE_CNT_PARSER;

            for (Element curTd : tds) {
                if (cntI == Constants.DATE_INDEX) {
                    if (cntJ >= Constants.BEGIN_TIME && _times.size() <= Constants.DAYS_ON_WEEK) {
                        Elements strtTime = curTd.getElementsByClass(Constants.START_TIME);
                        Elements endTime = curTd.getElementsByClass(Constants.END_TIME);

                        String s = strtTime.get(0).html().concat(Constants.SEPARATOR).concat(endTime.get(0).html());

                        _times.add(s);
                    }
                } else if (cntI > Constants.DATE_INDEX) {
                    if (Utilities.isEven(cntI)) {
                        Pair<String, String> curPair = new Pair<String, String>(Constants.SEPARATOR, Constants.SEPARATOR);

                        if (curTd.attr(Constants.CLASS).equals(Constants.TOP_WEEK)) {
                            curPair.setFirst(curTd.html());
                            curPair.setSecond(Constants.RESERVED);
                        } else {
                            curPair.setFirst(curTd.html());
                            curPair.setSecond(curTd.html());
                        }

                        schedule.get(curDay).add(curPair);
                    } else {
                        int cntPair = Constants.DEFAULT_VALUE_CNT_PARSER;

                        for (Pair<String, String> curP : schedule.get(curDay)) {
                            if (curP.getSecond().equals(Constants.RESERVED)) {
                                schedule.get(curDay).get(cntPair).setSecond(curTd.html());
                                break;
                            }

                            cntPair++;
                        }
                    }
                }

                cntJ++;
            }

            if (cntI > Constants.DATE_INDEX && !Utilities.isEven(cntI)) curDay++;

            cntI++;
        }

        return schedule;
    }

    private ArrayList<String> _times;
    private List<List<Pair<String, String>>> _schedule_main;
    private List<List<Pair<String, String>>> _schedule_exams;
    DataBaseHelper myDb;
    SQLiteDatabase db;
    Context mContext;

    public ArrayList<String> get_times() {
        return _times;
    }

    public void set_times(ArrayList<String> _times) {
        this._times = _times;
    }

    public List<List<Pair<String, String>>> get_schedule_main() {
        return _schedule_main;
    }

    public void set_schedule_main(List<List<Pair<String, String>>> _schedule_main) {
        this._schedule_main = _schedule_main;
    }

    public List<List<Pair<String, String>>> get_schedule_exams() {
        return _schedule_exams;
    }

    public void set_schedule_exams(List<List<Pair<String, String>>> _schedule_exams) {
        this._schedule_exams = _schedule_exams;
    }
}