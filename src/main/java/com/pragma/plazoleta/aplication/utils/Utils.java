package com.pragma.plazoleta.aplication.utils;

import com.pragma.plazoleta.aplication.dto.TraceabilityByEmployedResponse;

import java.util.List;

public class Utils {
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
