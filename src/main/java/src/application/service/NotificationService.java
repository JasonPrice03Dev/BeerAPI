package src.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import src.application.DTO.NotificationDTO;
import src.application.model.Beer;
import src.application.model.Notifications;
import src.application.model.User;
import src.application.repository.BeerRepository;
import src.application.repository.CustomerRepository;
import src.application.repository.NotificationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private BeerRepository beerRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    // Ensuring information is being passed as a DTO
    public NotificationDTO convertToDTO(Notifications notification) {
        return new NotificationDTO(
                notification.getId(),
                notification.getMessage(),
                notification.isRead());
    }

    // Method to notify users about a beer
    public void notifyUsersAboutBeer(Beer beer) {
        List<User> users = customerRepository.findUsersWithBeerInWishlist(beer);
        System.out.println("Found " + users.size() + " users with this beer in their wishlist.");

        for (User user : users) {
            Notifications notification = new Notifications();
            notification.setUser(user);
            notification.setBeer(beer);
            notification.setMessage("The beer " + beer.getName() + " is now in stock!");
            notification.setRead(false);
            notificationRepository.save(notification);
        }
    }
}

