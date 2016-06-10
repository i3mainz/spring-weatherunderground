/**
 * 
 */
package de.i3mainz.springframework.xd.modules.weatherunderground;

import static de.i3mainz.springframework.xd.modules.weatherunderground.WeatherUndergroundOptionsMetadata.Mode.LIVE;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

import org.springframework.xd.module.options.spi.ModuleOption;
import org.springframework.xd.module.options.spi.ProfileNamesProvider;

/**
 * @author Nikolai Bock
 *
 */
public class WeatherUndergroundOptionsMetadata implements ProfileNamesProvider {

    private String apikey;
    private String query;
    private boolean usenearbypws = false;
    private String position;
    private Mode mode = LIVE;
    private String date;
    private String dateFormat = new String("\"yyyy-MM-dd'T'HH:mm:ss Z\"");
    private int filterRange = 5;
    private HistoryResult historyResult = HistoryResult.TIMEFIT;

    public static enum Mode {
        LIVE("use-live-data"), HISTORY("use-history-data"), FORECAST("use-forecast-data"), HOURLY(
                "use-forecast-hourly-data");

        private String profile;

        private Mode(String profile) {
            this.profile = profile;
        }

        public String getProfile() {
            return profile;
        }
    }

    public static enum HistoryResult {
        TIMEFIT("use-timeFit"), TIMERANGE("use-timeRange"), SUMMARY("use-dailySummary");

        private String profile;

        private HistoryResult(String profile) {
            this.profile = profile;
        }

        public String getProfile() {
            return profile;
        }
    }

    /**
     * @return the apikey
     */
    @NotNull
    public String getApikey() {
        return apikey;
    }

    /**
     * @param apikey
     *            the apikey to set
     */
    @ModuleOption("API-Key of Weather Underground")
    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    /**
     * @return the query
     */
    @NotNull
    public String getQuery() {
        if (this.query == null && isUsenearbypws()) {
            this.setQuery("headers['PWSQuery']");
        }
        return query;
    }

    /**
     * @param query
     *            the query to set
     */
    @ModuleOption("Query for API request (SpEL)")
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * @return the usenearbypws
     */
    public boolean isUsenearbypws() {
        return usenearbypws;
    }

    /**
     * @param usenearbypws
     *            the usenearbypws to set
     */
    @ModuleOption("Whether to use a PWS nearby by position")
    public void setUsenearbypws(boolean usenearbypws) {
        this.usenearbypws = usenearbypws;
    }

    /**
     * @return the position
     */
    public String getPosition() {
        return position;
    }

    /**
     * @param position
     *            the position to set
     */
    @ModuleOption("Position to search for nearby PWS (SpEL)")
    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * @return the mode
     */
    @NotNull
    public Mode getMode() {
        return mode;
    }

    /**
     * @param mode
     *            the mode to set
     */
    @ModuleOption("Which data should be loaded (LIVE, HISTORY, FORECAST, HOURLY).")
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date
     *            the date to set
     */
    @ModuleOption("Date the information is searched for (SpEL)")
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return the dateFormat
     */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * @param dateFormat
     *            the dateFormat to set
     */
    @ModuleOption("Dateformat of the 'date' parameter (SpEL)")
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    /**
     * @return the filterRange
     */
    public int getFilterRange() {
        return filterRange;
    }

    /**
     * @param filterRange
     *            the filterRange to set
     */
    @ModuleOption("TimeRange in minutes to filter history data")
    public void setFilterRange(int filterRange) {
        this.filterRange = filterRange;
    }

    /**
     * @return the historyResult
     */
    public HistoryResult getHistoryResult() {
        return historyResult;
    }

    /**
     * @param historyResult
     *            the historyResult to set
     */
    @ModuleOption("Which information the history request should return")
    public void setHistoryResult(HistoryResult historyResult) {
        this.historyResult = historyResult;
    }

    @Override
    public String[] profilesToActivate() {

        List<String> profile = new ArrayList<>();

        if (isUsenearbypws()) {
            this.setQuery("headers['PWSQuery']");
            profile.add("use-nearby-pws");
        } else {
            profile.add("use-query");
        }
        profile.add(this.mode.getProfile());
        profile.add(this.historyResult.getProfile());
        System.out.println(profile.size());
        return profile.toArray(new String[profile.size()]);
    }

    @AssertTrue(message = "position has to be defined when nearby PWS should be used")
    private boolean isValidWithPosition() {
        return isUsenearbypws() && (getPosition() == null || getPosition().isEmpty()) ? false : true;
    }

    @AssertTrue(message = "In History-Mode a date and format information is required")
    private boolean isValidWithDate() {
        return this.mode.equals(Mode.HISTORY) && ((getDate() == null || getDate().isEmpty())
                || (getDateFormat() == null || getDateFormat().isEmpty())) ? false : true;
    }
}
