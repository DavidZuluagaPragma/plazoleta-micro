package com.pragma.plazoleta.domain.model.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Order {
    private Integer id;
    private Integer clientId;
    private Integer chefId;
    private Integer restaurantId;
    private String status;
    private Date date;

    public String calculateOrderTime(Date orderCompleted) {
        Duration duration = Duration.parse(Duration.between(date.toInstant(), orderCompleted.toInstant()).toString());
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        long millis = duration.toMillisPart();
        String formattedDuration = String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis);
        System.out.println(formattedDuration);
        return duration.toString()
                .substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase();
    }


}
