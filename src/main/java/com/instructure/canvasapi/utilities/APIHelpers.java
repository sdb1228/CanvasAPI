package com.instructure.canvasapi.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.instructure.canvasapi.model.User;
import retrofit.client.Header;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Joshua Dutton on 8/9/13.
 *
 * Copyright (c) 2014 Instructure. All rights reserved.
 */
public class APIHelpers {

    /**
     * SharedPreferences tags
     */

    //We would need migration code to update NAME and MASQUERADED_USER to snake case, so we will leave them as is for now.
    private final static String SHARED_PREFERENCES_NAME = "canvas-kit-sp";
    private final static String SHARED_PREFERENCES_MASQUERADED_USER = "masq-user";

    private final static String SHARED_PREFERENCES_USER = "user";
    private final static String SHARED_PREFERENCES_DOMAIN = "domain";
    private final static String SHARED_PREFERENCES_KALTURA_DOMAIN = "kaltura_domain";
    private final static String SHARED_PREFERENCES_TOKEN = "token";
    private final static String SHARED_PREFERENCES_KALTURA_TOKEN = "kaltura_token";
    private final static String SHARED_PREFERENCES_USER_AGENT = "user_agent";
    private final static String SHARED_PREFERENCES_API_PROTOCOL = "api_protocol";
    private final static String SHARED_PREFERENCES_KALTURA_PROTOCOL = "kaltura_protocol";
    private final static String SHARED_PREFERENCES_ERROR_DELEGATE_CLASS_NAME = "error_delegate_class_name";


    /**
     * Log Tag
     */
    public final static String LOG_TAG = "canvas-api";

    /**
     *
     * GetAssetsFile allows you to open a file that exists in the Assets directory.
     *
     * @param context
     * @param fileName
     * @return the contents of the file.
     */
    public static String getAssetsFile(Context context, String fileName) {
        try {
            String file = "";
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(fileName)));

            // do reading
            String line = "";
            while (line != null) {
                file += line;
                line = reader.readLine();
            }

            reader.close();
            return file;

        } catch (Exception e) {
            return "";
        }
    }

    /**
     * clearAllData is essentially a Logout.
     * Clears all data including credentials and cache.
     *
     * @param context
     * @return
     */
    public static boolean clearAllData(Context context) {
        if(context == null){
            return false;
        }

        //Clear credentials.
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();

        boolean sharedPreferencesDeleted =  editor.commit();

        //Delete cache.
        File cacheDir = new File(context.getFilesDir(), FileUtilities.FILE_DIRECTORY);
        boolean cacheDeleted = FileUtilities.deleteAllFilesInDirectory(cacheDir);

        return sharedPreferencesDeleted && cacheDeleted;
    }


    /**
     * setCacheUser saves the currently signed in user to cache.
     * @param context
     * @param user
     * @return
     */

    public static boolean setCacheUser(Context context, User user) {

        if (user == null) {
            return false;
        } else {
            Gson gson = CanvasRestAdapter.getGSONParser();
            String userString = gson.toJson(user);
            if (userString == null) {
                return false;
            }
            SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            String sharedPrefsKey = SHARED_PREFERENCES_USER;
            if(Masquerading.isMasquerading(context)){
                sharedPrefsKey = SHARED_PREFERENCES_MASQUERADED_USER;
            }

            editor.putString(sharedPrefsKey, userString);
            return  editor.commit();
        }
    }

    /**
     * setCachedAvatarURL is a helper to set a value on the cached user.
     * @param context
     * @param avatarURL
     * @return
     */
    public static boolean setCachedAvatarURL(Context context, String avatarURL){
        User user = getCacheUser(context);

        if(user == null){
            return false;
        }

        user.setAvatarURL(avatarURL);
        return setCacheUser(context, user);
    }

    /**
     * setCachedShortName is a helper to set a value on the cached user.
     * @param context
     * @param shortName
     * @return
     */
    public static boolean setCachedShortName(Context context, String shortName){
        User user = getCacheUser(context);

        if(user == null){
            return false;
        }

        user.setShortName(shortName);
        return setCacheUser(context, user);
    }

    /**
     * setCachedEmail is a helper to set a value on the cached user.
     * @param context
     * @param email
     * @return
     */

    public static boolean setCachedEmail(Context context, String email){
        User user = getCacheUser(context);

        if(user == null){
            return false;
        }

        user.setEmail(email);
        return setCacheUser(context, user);
    }

    /**
     * setCachedName is a helper to set a value on the cached user.
     * @param context
     * @param name
     * @return
     */

    public static boolean setCachedName(Context context, String name){
        User user = getCacheUser(context);

        if(user == null){
            return false;
        }

        user.setName(name);
        return setCacheUser(context, user);
    }


    /**
     * getCacheUser returns the signed-in user from cache. Returns null if there isn't one.
     * @param context
     * @return
     */

    public static User getCacheUser(Context context) {

        if(context == null){
            return null;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        String sharedPrefsKey = SHARED_PREFERENCES_USER;
        if(Masquerading.isMasquerading(context)){
            sharedPrefsKey = SHARED_PREFERENCES_MASQUERADED_USER;
        }

        String userString = sharedPreferences.getString(sharedPrefsKey, null);
        if (userString == null) {
            return null;
        } else {
            Gson gson = CanvasRestAdapter.getGSONParser();
            return gson.fromJson(userString, User.class);
        }
    }


    /**
     * getUserAgent returns the current user agent.
     * @param context
     * @return
     */
    public static String getUserAgent(Context context) {

        if(context == null){
            return "";
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SHARED_PREFERENCES_USER_AGENT, "");

    }

    /**
     * setUserAgent sets the user agent
     * @param context
     * @param userAgent
     * @return
     */
    public static boolean setUserAgent(Context context, String userAgent) {

        if(userAgent == null || userAgent.equals("")){
            return false;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_USER_AGENT, userAgent);
        return editor.commit();
    }

    /**
     * getFullDomain returns the protocol plus the domain.
     *
     * Returns "" if context is null or if the domain/token isn't set.
     * @return
     */
    public static String getFullDomain(Context context){
        String protocol = loadProtocol(context);
        String domain = getDomain(context);

        if (protocol == null || domain == null || protocol.equals("") || domain.equals("") ){
            return "";
        }

        return protocol + "://" + domain;
    }

    /**
     * getDomain returns the current domain. This function strips off all trailing / characters and the protocol.
     * @link APIHelpers.loadProtocol(context)
     * @param context
     * @return
     */
    public static String getDomain(Context context) {

        if(context == null){
            return "";
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        String domain =  sharedPreferences.getString(SHARED_PREFERENCES_DOMAIN, "");

        while (domain != null && domain.endsWith("/")) {
            domain = domain.substring(0, domain.length() - 1);
        }

        return domain;
    }

    /**
     * getFullKalturaDomain returns the protocol plus the domain.
     *
     * Returns "" if context is null or if the domain/token isn't set.
     * @return
     */
    public static String getFullKalturaDomain(Context context){
        String protocol = loadProtocol(context);
        String domain = getKalturaDomain(context);

        if (protocol == null || domain == null || protocol.equals("") || domain.equals("") ){
            return "";
        }

        return protocol + "://" + domain;
    }

    /**
     * getKalturaDomain returns the current domain. This function strips off all trailing / characters and the protocol.
     * @link APIHelpers.loadProtocol(context)
     * @param context
     * @return
     */
    public static String getKalturaDomain(Context context){
        if(context == null){
            return "";
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        String domain =  sharedPreferences.getString(SHARED_PREFERENCES_KALTURA_DOMAIN, "");

        while (domain != null && domain.endsWith("/")) {
            domain = domain.substring(0, domain.length() - 1);
        }

        return domain;
    }

    /**
     * setDomain sets the current domain. It strips off the protocol.
     *
     * @param context
     * @param domain
     * @return
     */

    public static boolean setDomain(Context context, String domain) {


        if(domain == null || domain.equals("")){
            return false;
        }

       domain = removeProtocol(domain);

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_DOMAIN, domain);
        return editor.commit();
    }

    /**
     * setDomain sets the current Kaltura domain. It strips off the protocol.
     *
     * @param context
     * @param kalturaDomain
     * @return
     */

    public static boolean setKalturaDomain(Context context, String kalturaDomain) {


        if(kalturaDomain == null || kalturaDomain.equals("")){
            return false;
        }

        kalturaDomain = removeProtocol(kalturaDomain);

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_KALTURA_DOMAIN, kalturaDomain);
        return editor.commit();
    }

    /**
     * getToken returns the OAuth token or "" if there isn't one.
     * @param context
     * @return
     */
    public static String getToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SHARED_PREFERENCES_TOKEN, "");
    }

    /**
     * setToken sets the OAuth token
     * @param context
     * @param token
     * @return
     */
    public static boolean setToken(Context context, String token) {
        if(token == null || token.equals("")){
            return false;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_TOKEN, token);
        return editor.commit();
    }

    /**
     * getToken returns the OAuth token or "" if there isn't one.
     * @param context
     * @return
     */
    public static String getKalturaToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SHARED_PREFERENCES_KALTURA_TOKEN, "");
    }

    /**
     * setToken sets the OAuth token
     * @param context
     * @param token
     * @return
     */
    public static boolean setKalturaToken(Context context, String token) {
        if(token == null || token.equals("")){
            return false;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_KALTURA_TOKEN, token);
        return editor.commit();
    }

    /**
     * loadProtocol returns the protocol or 'https' if there isn't one.
     * @param context
     * @return
     */
    public static String loadProtocol(Context context) {

        if(context == null){
            return "https";
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SHARED_PREFERENCES_API_PROTOCOL, "https");
    }

    /**
     * setProtocol sets the protocol
     * @param protocol
     * @param context
     * @return
     */
    public static boolean setProtocol(String protocol, Context context) {

        if(protocol == null || protocol.equals("")){
            return false;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_API_PROTOCOL, protocol);
        return editor.commit();
    }


    /**
     * Sets the default error delegate. This is the default if one isn't specified in the constructor
     *
     * @param errorDelegateClassName
     */
    public static void setDefaultErrorDelegateClass(Context context, String errorDelegateClassName) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_ERROR_DELEGATE_CLASS_NAME, errorDelegateClassName);
        editor.apply();
    }

    /**
     * Get the default error delegate.
     *
     * @param context
     */
    public static String getDefaultErrorDelegateClass(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SHARED_PREFERENCES_ERROR_DELEGATE_CLASS_NAME, null);

    }
    /**
     * booleanToInt is a Helper function for Converting boolean to URL booleans (ints)
     */
    public static int booleanToInt(boolean bool) {
        if (bool) {
            return 1;
        }
        return 0;
    }

    /**
     * removeDomainFromUrl is a helper function for removing the domain from a url. Used for pagination/routing
     * @param url
     * @return
     */
    public static String removeDomainFromUrl(String url) {
        if(url == null){
            return null;
        }

        String prefix = "/api/v1/";
        int index = url.indexOf(prefix);
        if (index != -1) {
            url = url.substring(index + prefix.length());
        }
        return url;
    }


    /**
     * Helper methods for handling ISO 8601 strings of the following format:
     * "2008-03-01T13:00:00+01:00". It also supports parsing the "Z" timezone.
     */

    /**
     * Transform Calendar to ISO 8601 string.
     */
    public static String dateToString(final Date date) {
        if (date == null){
            return null;
        }

        String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US).format(date);
        return formatted.substring(0, 22) + ":" + formatted.substring(22);
    }

    /**
     * Transform Calendar to ISO 8601 string.
     */
    public static String dateToDayMonthYearString(Context context, final Date date) {
        if (date == null){
            return null;
        }

        return DateHelpers.getFormattedDate(context, date);
    }

    /**
     * Helper methods for handling ISO 8601 strings of the following format:
     * "2008-03-01T13:00:00+01:00". It also supports parsing the "Z" timezone.
     */


    /**
     * Transform ISO 8601 string to Calendar.
     */
    public static Date stringToDate(final String iso8601string) {
        try {
            String s = iso8601string.replace("Z", "+00:00");
            s = s.substring(0, 22) + s.substring(23);
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US).parse(s);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * parseLinkHeaderResponse parses HTTP headers to return the first, next, prev, and last urls. Used for pagination.
     * @param context
     * @param headers
     * @return
     */
    public static LinkHeaders parseLinkHeaderResponse(Context context, List<Header> headers) {
        LinkHeaders linkHeaders = new LinkHeaders();

        for (int i = 0; i < headers.size(); i++) {
            if ("link".equalsIgnoreCase(headers.get(i).getName())) {
                String[] split = headers.get(i).getValue().split(",");
                for (int j = 0; j < split.length; j++) {
                    int index = split[j].indexOf(">");
                    String url = split[j].substring(0, index);
                    url = url.substring(1);

                    //Remove the domain.
                    url = removeDomainFromUrl(url);

                    if (split[j].contains("rel=\"next\"")) {
                        linkHeaders.nextURL = url;
                    } else if (split[j].contains("rel=\"prev\"")) {
                        linkHeaders.prevURL = url;
                    } else if (split[j].contains("rel=\"first\"")) {
                        linkHeaders.firstURL = url;
                    } else if (split[j].contains("rel=\"last\"")) {
                        linkHeaders.lastURL = url;
                    }
                }

                break;
            }
        }

        return linkHeaders;
    }

    public static APIStatusDelegate statusDelegateWithContext(final Context context) {
        return new APIStatusDelegate() {
            @Override public void onCallbackStarted() { }
            @Override public void onCallbackFinished(CanvasCallback.SOURCE source) { }
            @Override public void onNoNetwork() { }

            @Override public Context getContext() {
                return context;
            }
        };
    }

    /**
     * paramIsNull is a helper function for determining if callbacks/other objects are null;
     * @param callback
     * @param args
     * @return
     */
    public static boolean paramIsNull(CanvasCallback<?> callback, Object... args) {
        if (callback == null || callback.getContext() == null) {
            logParamsNull();
            return true;
        }
        return paramIsNull(args);
    }

    /**
     * paramIsNull is a helper function for determining if callbacks/other objects are null;
     * @param args
     * @return
     */
    public static boolean paramIsNull(Object... args) {

        for (Object arg : args) {
            if (arg == null) {
                logParamsNull();
                return true;
            }
        }
        return false;
    }


    /**
     * logParamsNull is a logging function helper
     */
    private static void logParamsNull() {
        Log.d(APIHelpers.LOG_TAG, "One or more parameters is null");
    }

    private static String removeProtocol(String domain){
        if (domain.contains("https://")) {
          return domain.substring(8);
        }
        if (domain.startsWith("http://")) {
            return domain.substring(7);
        }
        else return domain;
    }



}
