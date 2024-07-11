package com.smsipl.googlemap.apiServices;


import com.smsipl.googlemap.util.DirectionsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DirectionsApiService {
    @GET("route/v1/driving/{coordinates}")
    Call<DirectionsResponse> getDirections(
            @Path("coordinates") String coordinates,
            @Query("overview") String overview,
            @Query("geometries") String geometries
    );
}


