package org.prgms.locomocoserver.user.vo;

import lombok.Getter;
import org.prgms.locomocoserver.user.exception.UserErrorType;
import org.prgms.locomocoserver.user.exception.UserException;

@Getter
public class TemperatureVo {
    private double temperature;

    public TemperatureVo(double temperature) {
        if (!isValidTemperature(temperature)) {
            throw new UserException(UserErrorType.TEMPERATURE_TYPE_ERROR);
        }
        this.temperature = temperature;
    }
    private boolean isValidTemperature(double temperature){
        return temperature >= 0 && temperature <= 100;
    }
}
