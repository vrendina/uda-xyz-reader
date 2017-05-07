package io.levelsoftware.xyzreader.data;

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class ArticleDateUtil {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss", Locale.US);

    public static String formatArticleDate(String dateString) {
        String formattedString = "";
        try {
            Date date = dateFormat.parse(dateString);

            formattedString = DateUtils.getRelativeTimeSpanString(
                    date.getTime(),
                    System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_ALL).toString();
        } catch (ParseException e) {
            Timber.e("Could not parse date information.");
        }

        return formattedString;
    }

}
