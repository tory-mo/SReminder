package sremind.torymo.by;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

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

        retrofit = new Retrofit.Builder()
                .baseUrl(Utility.BASE_URL) //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create(gson))
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
            throw new JsonParseException("Unparseable date: \"" + jsonElement.getAsString()
                    + "\". Supported formats: \n" + Arrays.toString(DATE_FORMATS));
        }
    }

    public static MDBService getMdbService(){
        return mdbService;
    }
}