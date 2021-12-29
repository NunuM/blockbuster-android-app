package me.nunum.blockbuster.services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import androidx.annotation.Nullable;
import me.nunum.blockbuster.R;
import me.nunum.blockbuster.dao.MovieDao;
import me.nunum.blockbuster.model.Movie;
import me.nunum.blockbuster.model.MovieQuality;

public class MovieRemoteSource extends Thread {

    private static final String TAG = MovieRemoteSource.class.getName();

    private final Context context;
    private final RequestQueue queue;
    private final MovieDao movieDao;
    private final AtomicInteger counter = new AtomicInteger(0);

    public MovieRemoteSource(Context context, RequestQueue queue, MovieDao movieDao) {
        this.context = context;
        this.queue = queue;
        this.movieDao = movieDao;
    }

    private static String normalizeTitle(String title) {
        return title.replaceAll("[ |:|\\.|\\-]+", "_").trim();
    }


    public void getMovieDetails(final String titleName,
                                final String year,
                                final String magnet,
                                final String quality,
                                final String originalTitle) {

        final String titleKey = normalizeTitle(titleName);
        final String url = String.format(ApplicationConstants.MOVIE_DETAILS_API, titleName.toLowerCase().trim());

        boolean exists = movieDao.hasTitleName(titleKey) > 0;

        if (!exists) {

            final StringRequest movieDetailsRequest = new CustomRequest(url,
                    response -> {

                        final Movie movie;

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            int yearAsInteger = 2021;


                            if (!jsonObject.has("Year")) {
                                try {
                                    yearAsInteger = Integer.parseInt(year);
                                } catch (NumberFormatException e) {
                                    //
                                }
                                movie = new Movie(titleName, quality, originalTitle, magnet, yearAsInteger);

                            } else {


                                try {
                                    yearAsInteger = jsonObject.getInt("Year");
                                } catch (NumberFormatException e) {
                                    //Ignore
                                }

                                String title = jsonObject.getString("Title");
                                String rated = jsonObject.getString("Rated");
                                String released = jsonObject.getString("Released");
                                String runtime = jsonObject.getString("Runtime");
                                String genre = jsonObject.getString("Genre");
                                String director = jsonObject.getString("Director");
                                String writer = jsonObject.getString("Writer");
                                String actors = jsonObject.getString("Actors");
                                String plot = jsonObject.getString("Plot");
                                String language = jsonObject.getString("Language");
                                String country = jsonObject.getString("Country");
                                String awards = jsonObject.getString("Awards");
                                String poster = jsonObject.getString("Poster");
                                String metascore = jsonObject.getString("Metascore");

                                float imdbRating = 0.0f;
                                try {
                                    imdbRating = Float.parseFloat(jsonObject.getString("imdbRating"));
                                } catch (Throwable e) {
                                    //Ignore
                                }

                                String imdbVotes = jsonObject.getString("imdbVotes");
                                String imdbID = jsonObject.getString("imdbID");
                                String type = jsonObject.getString("Type");
                                String dvd = "N/A";

                                try {
                                    dvd = jsonObject.getString("DVD");
                                } catch (Throwable e) {
                                    //ignore
                                }

                                String boxOffice = jsonObject.getString("BoxOffice");
                                String production = jsonObject.getString("Production");
                                String website = jsonObject.getString("Website");

                                movie = new Movie(titleKey,
                                        title,
                                        originalTitle,
                                        magnet,
                                        yearAsInteger,
                                        rated,
                                        released,
                                        runtime,
                                        genre,
                                        director,
                                        writer,
                                        actors,
                                        plot,
                                        language,
                                        country,
                                        awards,
                                        poster,
                                        metascore,
                                        imdbRating,
                                        imdbVotes,
                                        imdbID,
                                        type,
                                        dvd,
                                        boxOffice,
                                        production,
                                        website,
                                        new Date());
                            }

                            movieDao.insertAll(movie);

                            counter.incrementAndGet();

                            Log.i(TAG, "run: inserting movie" + movie);

                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: ", e);
                        }
                    }, response -> {
                Log.d(TAG, "getMovieDetails: Response OK");
            }, (error) -> {

                Log.e(TAG, "onErrorResponse: ", error);

            });

            queue.add(movieDetailsRequest);

        }

        final Movie movie = this.movieDao.findByTitle(titleKey);
        if (movie != null) {
            if (this.movieDao.hasQuality(movie.getId(), quality) == 0) {
                this.movieDao.insertQuality(new MovieQuality(quality, movie.getId()));
            }
        }

    }

    public int numberOfInsertedMovies() {
        return counter.get();
    }

    private static class CustomRequest extends StringRequest {

        private final Response.Listener<String> backgroundThreadListener;

        public CustomRequest(String url,
                             Response.Listener<String> backgroundThreadListener,
                             Response.Listener<String> listener,
                             @Nullable Response.ErrorListener errorListener) {
            super(Method.GET, url, listener, errorListener);
            this.backgroundThreadListener = backgroundThreadListener;
        }

        @Override
        protected Response<String> parseNetworkResponse(NetworkResponse response) {

            Response<String> stringResponse = super.parseNetworkResponse(response);

            backgroundThreadListener.onResponse(stringResponse.result);

            return stringResponse;
        }
    }


    public void getMoviesFromPirate() {

        final StringRequest piratebayXMLFeedRequest = new CustomRequest(ApplicationConstants.PIRATE_BAY_MOVIE_FEED_URL,
                response -> {

                    try {

                        final XPathFactory xPathFactory = XPathFactory.newInstance();

                        XPath xPath = xPathFactory.newXPath();

                        XPathExpression xPathExpression = xPath.compile(".//item");

                        NodeList movies = (NodeList) xPathExpression.evaluate(new InputSource(new StringReader(response)), XPathConstants.NODESET);

                        for (int i = 0; i < movies.getLength(); i++) {
                            Node item = movies.item(i);
                            Log.e(TAG, "onResponse:" + item.getNodeName());

                            if (item instanceof Element) {

                                Element e = (Element) item;

                                String title = e
                                        .getElementsByTagName("title")
                                        .item(0)
                                        .getFirstChild()
                                        .getNodeValue()
                                        .replace(".", " ");

                                String magnet = e
                                        .getElementsByTagName("link")
                                        .item(0)
                                        .getFirstChild()
                                        .getNodeValue();


                                Pattern pattern = Pattern.compile("(.*)(\\(?[0-9]{4}[\\)?|\\.]? )(.*)");

                                Matcher matcher = pattern.matcher(title);

                                if (matcher.find() && matcher.groupCount() >= 3) {

                                    String m0 = matcher.group(0);
                                    String m1 = matcher.group(1);
                                    String m2 = matcher.group(2);
                                    String m3 = matcher.group(3);


                                    if (m0 != null && m0.length() > 0
                                            && m1 != null && m1.length() > 0
                                            && m2 != null && m2.length() > 0
                                            && m3 != null && m3.length() > 0) {

                                        String cleanedTitle = m1
                                                .replace("(", " ")
                                                .replace(")", " ")
                                                .trim();
                                        String cleanedYear = m2.trim();
                                        String quality = m3.trim();

                                        this.getMovieDetails(cleanedTitle,
                                                cleanedYear,
                                                magnet,
                                                quality,
                                                title);
                                    }
                                }
                            }
                        }

                        if (this.counter.get() > 0) {
                            this.movieDao.rebuild();
                        }

                    } catch (Throwable e) {
                        Log.e(TAG, "onResponse: Error parsing XML", e);

                        Toast.makeText(this.context, this.context.getString(R.string.general_error, e.getMessage()), Toast.LENGTH_LONG).show();
                    }
                }, response -> {

            Log.d(TAG, "getMoviesFromPirate: Response OK");

        }, (error) -> {

            Log.e(TAG, "onErrorResponse: Error requesting XML", error);

            Toast.makeText(this.context, this.context.getString(R.string.general_error, error.getMessage()), Toast.LENGTH_LONG).show();

        });

        // Add the request to the RequestQueue.
        queue.add(piratebayXMLFeedRequest);
    }

    @Override
    public void run() {
        this.getMoviesFromPirate();
    }
}
