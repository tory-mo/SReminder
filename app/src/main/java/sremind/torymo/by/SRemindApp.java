package sremind.torymo.by;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sremind.torymo.by.service.MDBService;

public class SRemindApp extends Application {

    private Retrofit retrofit;
    private static MDBService mdbService;

    private static final String[] DATE_FORMATS = new String[]{
            "yyyy-MM-dd",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ssZ",
            "EEE MMM dd HH:mm:ss z yyyy",
            "HH:mm:ss",
            "MM/dd/yyyy HH:mm:ss aaa",
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS",
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'",
            "MMM d',' yyyy H:mm:ss a",
            "dd MMM. yyyy",
            "dd MMMM yyyy",
            ""
    };

    @Override
    public void onCreate() {
        super.onCreate();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateTypeDeserializer())
                .create();

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()

                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(new ApiKeyInterceptor())
                .build();

        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(Utility.BASE_URL)
                .client(client)
                .build();

        mdbService = retrofit.create(MDBService.class); //Создаем объект, при помощи которого будем выполнять запросы
    }

    public class DateTypeDeserializer implements JsonDeserializer<Date> {
        @Override
        public Date deserialize(JsonElement jsonElement, Type typeOF, JsonDeserializationContext context) throws JsonParseException {
            for (String format : DATE_FORMATS) {
                try {
                    if(jsonElement.getAsString().length() == 0)
                        return null;
                    return new SimpleDateFormat(format, Locale.UK).parse(jsonElement.getAsString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            throw new JsonParseException("Unparseable date: \"" + jsonElement.getAsString());
        }
    }

    private class ApiKeyInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            HttpUrl originalUrl = originalRequest.url();
            HttpUrl url = originalUrl.newBuilder()
                    .addQueryParameter(Utility.APPKEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                    .build();

            Request requestBuilder = originalRequest.newBuilder().url(url).build();
            return chain.proceed(requestBuilder);
        }
    }

    public static MDBService getMdbService(){
        return mdbService;
    }
}