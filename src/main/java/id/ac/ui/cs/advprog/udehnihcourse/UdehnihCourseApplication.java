package id.ac.ui.cs.advprog.udehnihcourse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class UdehnihCourseApplication {

    public static void main(String[] args) {
        SpringApplication.run(UdehnihCourseApplication.class, args);
    }

}