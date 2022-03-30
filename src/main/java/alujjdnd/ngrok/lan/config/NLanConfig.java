package alujjdnd.ngrok.lan.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "ngroklan")
public class NLanConfig implements ConfigData
{
    public boolean enabledCheckBox = true;
    public String authToken = "AuthToken";

    public enum regionSelectEnum {
        US, EU, AP, AU, SA, JP, IN
    }

    public regionSelectEnum regionSelect = regionSelectEnum.US;

}