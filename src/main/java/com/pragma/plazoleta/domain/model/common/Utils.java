package com.pragma.plazoleta.domain.model.common;

import com.pragma.plazoleta.aplication.dto.TraceabilityByEmployedResponse;

import java.util.List;

public class Utils {
    public static final String ACCEPT = "Accept";
    public static final String CONTENT_TYPE = "Content-Type";

    public static  String ORDER_PENDING = "PENDING";
    public static  String ORDER_READY = "READY";
    public static  String ORDER_PREPARATION = "IN PREPARATION";
    public static  String ORDER_DELIVERED = "DELIVERED";
    public static  String INVALID_OTP = "Invalid OTP, please try again!";
    public static  String ORDER_COMPLETED = "ORDER COMPLETED!";
    public static  String ORDER_CANCELLED = "CANCELLED";
    public static  String ORDER_CANCELLED_SUCCESS = "ORDER CANCELLED SUCCESSFULLY";
    public static  String USER = "javatechie";

    public static String calculateAverageEfficiency(List<TraceabilityByEmployedResponse> traceabilityByEmployedResponses) {
        long totalEfficiency = 0;
        for (TraceabilityByEmployedResponse response : traceabilityByEmployedResponses) {
            totalEfficiency += Long.parseLong(response.getEfficiency());
        }
        long averageEfficiency = totalEfficiency / traceabilityByEmployedResponses.size();
        return calculateEfficiency(averageEfficiency);
    }
    public static String calculateEfficiency(long averageTime) {
        long minutes = averageTime / (1000 * 60); // Convert average time to minutes
        long hours = minutes / 60; // Get complete hours
        long remainingMinutes = minutes % 60; // Get remaining minutes
        if (hours == 0) {
            return remainingMinutes + " minutes";
        } else if (remainingMinutes == 0) {
            return hours + " hours";
        } else {
            return hours + " hours and " + remainingMinutes + " minutes";
        }
    }
}
