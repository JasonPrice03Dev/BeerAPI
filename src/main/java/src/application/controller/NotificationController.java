package src.application.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import src.application.DTO.NotificationDTO;
import src.application.model.Notifications;
import src.application.model.User;
import src.application.repository.CustomerRepository;
import src.application.repository.NotificationRepository;
import org.springframework.web.bind.annotation.*;
import src.application.service.NotificationService;

import java.util.List;

import java.util.Optional;

@RestController
@Tag(name = "Notifications", description = "Tasks related to notifications")
@RequestMapping("/notifications")
@SecurityRequirement(name = "BearerAuth")
public class NotificationController {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private NotificationService notificationService;


    // End-point to view notifications for a customer
    @GetMapping("/{customerId}")
    public ResponseEntity<Page<NotificationDTO>> getNotifications(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Optional<User> user = customerRepository.findById(customerId);

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<Notifications> notificationsPage = notificationRepository.findByUserAndIsReadFalse(user.get(), pageable);

        Page<NotificationDTO> notificationDTOs = notificationsPage.map(notificationService::convertToDTO);

        return ResponseEntity.ok(notificationDTOs);
    }

    // End-point to mark a notification as read
    @PostMapping("/mark-as-read/{notificationId}")
    public ResponseEntity<NotificationDTO> markAsRead(@PathVariable Long notificationId) {
        Optional<Notifications> notification = notificationRepository.findById(notificationId);
        if (notification.isPresent()) {
            Notifications notif = notification.get();
            notif.setRead(true);
            notificationRepository.save(notif);

            NotificationDTO updatedNotification = new NotificationDTO(
                    notif.getId(),
                    notif.getMessage(),
                    notif.isRead()
            );

            return ResponseEntity.ok(updatedNotification);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
}

