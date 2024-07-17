package kr.go.me.egis.egsp.rsync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ServletComponentScan(basePackages = "kr.go.me.egis.egsp.rsync")
public class EgspRsyncApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(EgspRsyncApplication.class);
		app.setWebApplicationType(WebApplicationType.SERVLET);
		app.run(args);
	}

}
