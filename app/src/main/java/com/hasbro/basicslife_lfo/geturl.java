package com.hasbro.basicslife_lfo;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class geturl {
    static Retrofit retrofit = null;
    public static Retrofit getClient() {

        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.Base_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

}
