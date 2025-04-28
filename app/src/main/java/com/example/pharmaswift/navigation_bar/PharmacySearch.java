package com.example.pharmaswift.navigation_bar;

import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.RankBy;
import com.google.maps.model.LatLng;
import java.io.IOException;

public class PharmacySearch {



    public PlacesSearchResponse run(){
        PlacesSearchResponse request = new PlacesSearchResponse();
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyDOQDFlL2-2I7isdg28-IHWO1Q6cV1pEzs")
                .build();

        LatLng location = new LatLng(14.952189579031904, 120.76563816940988);

        try {
            request = PlacesApi.nearbySearchQuery(context, location)
                    .radius(5000)
                    .rankby(RankBy.PROMINENCE)
                    .keyword("pharmacies")
                    .language("en")
                    .type(PlaceType.RESTAURANT)
                    .await();
        } catch (ApiException | IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            return request;
        }
    }
}
