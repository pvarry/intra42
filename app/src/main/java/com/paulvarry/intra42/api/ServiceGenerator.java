package com.paulvarry.intra42.api;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.BuildConfig;
import com.paulvarry.intra42.Credential;
import com.paulvarry.intra42.api.model.AccessToken;
import com.paulvarry.intra42.api.model.Messages;
import com.paulvarry.intra42.api.model.Slots;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.Token;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    public static final String API_BASE_URL = "https://api.intra.42.fr";
    public static final String API_BASE_URL_42TOOLS = "https://f7681272.ngrok.io/";
    private static OkHttpClient.Builder httpClient;
    private static AccessToken accessTokenIntra42;
    private static com.paulvarry.intra42.api.tools42.AccessToken accessToken42Tools;
    private static AppClass app;

    private static String HEADER_KEY_USER_AGENT = "User-Agent";
    private static String HEADER_KEY_ACCEPT = "Accept";
    private static String HEADER_KEY_API_AUTH = "Authorization";
    private static String HEADER_VALUE_ACCEPT = "application/json";
    private static String HEADER_CONTENT_TYPE = "Content-type";

    public static void init(AppClass app) {
        ServiceGenerator.app = app;
        accessTokenIntra42 = Token.getIntra42TokenFromShared(app);
        accessToken42Tools = Token.get42ToolsTokenFromShared(app);
    }

    public static boolean have42Token() {
        return accessTokenIntra42 != null;
    }

    public static boolean have42ToolsToken() {
        return accessToken42Tools != null;
    }

    public static void logout() {
        accessTokenIntra42 = null;
        accessToken42Tools = null;
    }

    public static <S> S createService(Class<S> serviceClass) {

        Retrofit.Builder builder;
        builder = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(getGson()));

        if (serviceClass == ApiService42Tools.class)
            builder.baseUrl(API_BASE_URL_42TOOLS);

        httpClient = getBaseClient(true);
        httpClient.addInterceptor(getHeaderInterceptor());

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();

        return retrofit.create(serviceClass);
    }

    public static <S> S createService(Class<S> serviceClass, AppClass app, boolean allowRedirectWrongAuth) {
        ServiceGenerator.app = app;
        Retrofit.Builder builder;

        builder = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(getGson()));

        httpClient = getBaseClient(allowRedirectWrongAuth);

        if (serviceClass == ApiService.class) {
            httpClient.addInterceptor(getHeaderInterceptor(accessTokenIntra42));
            httpClient.authenticator(getAuthenticatorIntra42(app));
        } else if (serviceClass == ApiService42Tools.class) {
            httpClient.addInterceptor(getHeaderInterceptor(accessToken42Tools));
            httpClient.authenticator(getAuthenticator42Tools(app));
            builder.baseUrl(API_BASE_URL_42TOOLS);
        } else
            httpClient.addInterceptor(getHeaderInterceptor());

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();

        return retrofit.create(serviceClass);
    }

    private static int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }

    private static OkHttpClient.Builder getBaseClient(boolean allowRedirectWrongAuth) {
        httpClient = new OkHttpClient.Builder();

        getLogInterceptor(httpClient);
        if (allowRedirectWrongAuth)
            httpClient.addNetworkInterceptor(new AuthInterceptorRedirectActivity());

        httpClient.readTimeout(20, TimeUnit.SECONDS);
        httpClient.connectTimeout(5, TimeUnit.SECONDS);

        return httpClient;
    }

    private static Authenticator getAuthenticatorIntra42(final Context context) {
        return new Authenticator() {
            @Override
            public Request authenticate(Route route, Response response) throws IOException {

                if (responseCount(response) >= 2) {
                    // If both the original call and the call with refreshed token failed,
                    // it will probably keep failing, so don't try again.

                    return null;
                }

                if (accessTokenIntra42 == null)
                    return null;

                //noinspection SynchronizeOnNonFinalField
                synchronized (accessTokenIntra42.refreshToken) {
                    // We need a new client, since we don't want to make another call using our client with access token
                    ApiService tokenClient = createService(ApiService.class);
                    Call<AccessToken> call = tokenClient.getRefreshAccessToken(accessTokenIntra42.refreshToken, Credential.UID, Credential.SECRET, Credential.API_OAUTH_REDIRECT, "refresh_token");
                    try {
                        retrofit2.Response<AccessToken> tokenResponse = call.execute();
                        if (tokenResponse.code() == 200) {
                            AccessToken newToken = tokenResponse.body();
                            accessTokenIntra42 = newToken;
                            Token.save(context, accessTokenIntra42);
                            if (app != null)
                                accessTokenIntra42 = newToken;

                            return response.request().newBuilder()
                                    .header(HEADER_KEY_API_AUTH, newToken.tokenType + " " + newToken.accessToken)
                                    .header(HEADER_KEY_USER_AGENT, getUserAgent())
                                    .header(HEADER_KEY_ACCEPT, HEADER_VALUE_ACCEPT)
                                    .header(HEADER_CONTENT_TYPE, HEADER_VALUE_ACCEPT)
                                    .build();
                        } else {
                            return null;
                        }
                    } catch (IOException e) {
                        return null;
                    } catch (NullPointerException e) {
                        return null;
                    }
                }
            }
        };
    }

    //TODO
    private static Authenticator getAuthenticator42Tools(final Context context) {
        return new Authenticator() {
            @Override
            public Request authenticate(Route route, Response response) throws IOException {

                if (responseCount(response) >= 2) {
                    // If both the original call and the call with refreshed token failed,
                    // it will probably keep failing, so don't try again.

                    return null;
                }

                if (accessTokenIntra42 == null)
                    return null;

                //noinspection SynchronizeOnNonFinalField
                synchronized (accessToken42Tools) {
                    // We need a new client, since we don't want to make another call using our client with access token
                    ApiService42Tools tokenClient = createService(ApiService42Tools.class);
                    Call<com.paulvarry.intra42.api.tools42.AccessToken> call = tokenClient.getAccessToken(accessTokenIntra42.refreshToken);
                    try {
                        retrofit2.Response<com.paulvarry.intra42.api.tools42.AccessToken> tokenResponse = call.execute();
                        if (tokenResponse.code() == 200) {
                            com.paulvarry.intra42.api.tools42.AccessToken newToken = tokenResponse.body();
                            accessToken42Tools = newToken;
                            Token.save(context, accessTokenIntra42);

                            return response.request().newBuilder()
                                    .header(HEADER_KEY_API_AUTH, "Bearer " + newToken.accessToken)
                                    .header(HEADER_KEY_USER_AGENT, getUserAgent())
                                    .header(HEADER_KEY_ACCEPT, HEADER_VALUE_ACCEPT)
                                    .header(HEADER_CONTENT_TYPE, HEADER_VALUE_ACCEPT)
                                    .build();
                        } else {
                            return null;
                        }
                    } catch (IOException e) {
                        return null;
                    } catch (NullPointerException e) {
                        return null;
                    }
                }
            }
        };
    }

    private static Interceptor getHeaderInterceptor(final AccessToken accessToken) {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .header(HEADER_KEY_ACCEPT, HEADER_VALUE_ACCEPT)
                        .header(HEADER_CONTENT_TYPE, HEADER_VALUE_ACCEPT)
                        .header(HEADER_KEY_API_AUTH, accessToken.tokenType + " " + accessToken.accessToken)
                        .header(HEADER_KEY_USER_AGENT, getUserAgent())
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };
    }

    private static Interceptor getHeaderInterceptor(final com.paulvarry.intra42.api.tools42.AccessToken accessToken) {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .header(HEADER_KEY_ACCEPT, HEADER_VALUE_ACCEPT)
                        .header(HEADER_CONTENT_TYPE, HEADER_VALUE_ACCEPT)
                        .header(HEADER_KEY_API_AUTH, "Bearer " + accessToken.accessToken)
                        .header(HEADER_KEY_USER_AGENT, getUserAgent())
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };
    }

    private static Interceptor getHeaderInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                Request.Builder requestBuilder = original.newBuilder()
                        .header(HEADER_KEY_ACCEPT, HEADER_VALUE_ACCEPT)
                        .header(HEADER_CONTENT_TYPE, HEADER_VALUE_ACCEPT)
                        .header(HEADER_KEY_USER_AGENT, getUserAgent())
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };
    }

    private static void getLogInterceptor(OkHttpClient.Builder httpClient) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG)
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        else
            logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        if (BuildConfig.DEBUG || AppSettings.Advanced.getAllowSaveLogs(app))
            httpClient.addInterceptor(logging);// add logging as last interceptor

    }

    static public Gson getGson() {

        class DateDeserializer implements JsonDeserializer<Date> {

            private final String TAG = DateDeserializer.class.getSimpleName();

            @Override
            public java.util.Date deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
                String date = element.getAsString();
                Date returnDate = null;

                try {
                    returnDate = ISO8601Utils.parse(date, new ParsePosition(0));
                } catch (ParseException | IllegalArgumentException e) {
                    Log.e(TAG, "Failed to parse dateString: (" + date + "),  due to:", e);
                }
                return returnDate;
            }
        }

        class DateSerializer implements JsonSerializer<Date> {

            @Override
            public JsonElement serialize(java.util.Date src, Type typeOfSrc, JsonSerializationContext context) {
                String dateFormatAsString = ISO8601Utils.format(src, false, TimeZone.getTimeZone("GMT"));
                return new JsonPrimitive(dateFormatAsString);
            }

        }

        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .registerTypeAdapter(Slots.class, new Slots.SlotsDeserializer())
                .registerTypeAdapter(Slots.class, new Slots.SlotsSerializer())
                .registerTypeAdapter(java.util.Date.class, new DateDeserializer())
                .registerTypeAdapter(java.util.Date.class, new DateSerializer())
                .registerTypeAdapter(UsersLTE.class, new UsersLTE.UserLTEDeserializer())

                .registerTypeAdapter(Messages.UserVotes.class, new Messages.UserVotes.UserVotesDeserializer())

                .registerTypeAdapter(UsersLTE.getListType(), new UsersLTE.ListUserLTEDeserializer())

//                .registerTypeAdapter(ScaleTeams.class, new ScaleTeams.ScaleTeamsDeserializer())
                .create();
    }

    private static String getUserAgent() {
        return "Intra42Android/" + BuildConfig.VERSION_NAME + "/" + BuildConfig.VERSION_CODE +
                " (Android/" + Build.VERSION.RELEASE + " ; " + Build.MODEL + ") retrofit2/2.1.0";
    }

    public static AccessToken getToken() {
        return accessTokenIntra42;
    }

    public static void setToken(AccessToken token) {
        ServiceGenerator.accessTokenIntra42 = token;
    }

    public static void setToken(com.paulvarry.intra42.api.tools42.AccessToken token) {
        ServiceGenerator.accessToken42Tools = token;
    }

    private static class AuthInterceptorRedirectActivity implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {

            Response response = chain.proceed(chain.request());
            if (response.code() == 401 && chain.request().url().encodedPath().contains("/oauth/token") && app != null)
                app.logoutAndRedirect();
            return response;
        }
    }
}