package solitour_backend.solitour;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SolitourApplication {

  public static void main(String[] args) {
    SpringApplication.run(SolitourApplication.class, args);
  }

}
