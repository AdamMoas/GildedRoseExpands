package gilded.rose.expands.config;


import gilded.rose.expands.utils.SurgePriceUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SurgeConfig {

    @Bean
    public SurgePriceUtil surgePrice(){
        long millisecondsInHour = 60 * 60 * 1000;
        int surgeViewThreshold = 11;
        int surgePricePercent = 10;
        return new SurgePriceUtil(millisecondsInHour, surgeViewThreshold, surgePricePercent);
    }

}
