package com.example.eng_mahnoud83coffey.embeatitserver.Remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClint
{


    private static Retrofit retrofit=null;


    public static Retrofit getClint(String basUrl)
    {


        if (retrofit==null)
        {

            retrofit=new Retrofit.Builder()
                                 .baseUrl(basUrl)
                                 .addConverterFactory(GsonConverterFactory.create())
                                 .build();

        }

        return retrofit;
    }

}
