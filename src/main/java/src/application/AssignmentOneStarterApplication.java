package src.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import src.application.service.UpdateToBCrypt;

// On start-up the project runs the update password to BCrypt and enables scheduling for cron
@EnableScheduling
@SpringBootApplication
public class AssignmentOneStarterApplication implements CommandLineRunner {

    @Autowired
    private UpdateToBCrypt updateToBCrypt;

    public static void main(String[] args) {
        SpringApplication.run(AssignmentOneStarterApplication.class, args);
    }

    public void run(String[] args){
        updateToBCrypt.updatePasswordsToBCrypt();
    }

}
