package src.application.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NotificationDTO {
    // Data Transfer Object for notifications
    private Long id;
    private String message;
    private boolean isRead;

    public NotificationDTO(Long id, String message, boolean isRead) {
        this.id = id;
        this.message = message;
        this.isRead = isRead;
    }
}

