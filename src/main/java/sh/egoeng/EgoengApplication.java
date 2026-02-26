package sh.egoeng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableFeignClients(basePackages = {"sh.egoeng.feign"})
@SpringBootApplication
public class EgoengApplication {

    public static void main(String[] args) {
        SpringApplication.run(EgoengApplication.class, args);
    }

}
