package org.prgms.locomocoserver.user.vo;

import lombok.Getter;

@Getter
public class TemperatureVo {
    private int temperature;

    public TemperatureVo(int temperature) {
        if (!isValidTemperature(temperature)) {
            throw new IllegalArgumentException("Invalid temperature");
        }
        this.temperature = temperature;
    }
    private boolean isValidTemperature(int temperature){
        return temperature >= 0 && temperature <= 100;
    }
}
