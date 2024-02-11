package org.prgms.locomocoserver.user.vo;

import lombok.Getter;

@Getter
public class TemperatureVo {
    private double temperature;

    public TemperatureVo(double temperature) {
        if (!isValidTemperature(temperature)) {
            throw new IllegalArgumentException("Invalid temperature");
        }
        this.temperature = temperature;
    }
    private boolean isValidTemperature(double temperature){
        return temperature >= 0 && temperature <= 100;
    }
}
