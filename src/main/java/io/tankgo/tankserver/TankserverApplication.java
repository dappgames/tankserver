package io.tankgo.tankserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication(scanBasePackages = {"io.tankgo","org.dyn4j"})
@Slf4j
public class TankserverApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
		SpringApplication.run(TankserverApplication.class, args);
		log.info("================== Application success !!!!!!!!!!!!!======================");
	}

}
