package com.movienow.org.controller;

public class ScreenController {
    /**
     * 1. add screen wit movie
     * 2. delete screen with movie
     *
     * APP-FLOW
     * after select city user will get list of (theatres from that city[theatre + city] /movies in the city[movie + city])
     *
     * 1. user will select theatre, then he will select movie                                  , {P1}then he will select screen, then time slot..
     * 2. user will select movie, then he will get a theatre list ,then theatre                , {P1}then he will select screen, then time slot..
     *
     * so at point {P1} we will have theatre, movie , so it common API
     *
     *
     * Here screen API is common with no data frequent change, so this can be cached later.
     */
}
