package id.t12uetatay.crud.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitService {
    private static final String base_url = "http://192.168.137.1/ci3/";
    public static String base_url_image = base_url+"assets/upload/images/";
    private static RetrofitService instance;
    private Retrofit retrofit;

    private RetrofitService() {
        retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    public static synchronized RetrofitService getInstance() {
        if (instance == null) {
            instance = new RetrofitService();
        }
        return instance;

    }

    public ApiService getapi() { //defining api function
        return retrofit.create(ApiService.class);
    }
}
