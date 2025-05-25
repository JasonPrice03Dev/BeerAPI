package src.application.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import src.application.model.Beer;
import src.application.repository.BeerRepository;
import src.application.service.BeerWishListService;
import src.application.service.NotificationService;

import java.util.List;

@Component
public class NotificationScheduler {
    @Autowired
    private BeerWishListService beerWishListService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private BeerRepository beerRepository;

    // Scheduling checking of stock - can be done once a day, week or month to alert users of stock or sales
    // @Scheduled(cron = "0 * * * * *")
    @Scheduled(cron = "0 0 0 1 * *") // Notification each month regarding wishlist stock
    public void checkStockAndNotifyUsers() {
        System.out.println("Scheduled task is running...");
        List<Beer> beers = beerRepository.findBeersOnSaleOrInStock();
        System.out.println("Found " + beers.size() + " beers in stock or on sale.");
        for (Beer beer : beers) {
            notificationService.notifyUsersAboutBeer(beer);
        }
    }
}

