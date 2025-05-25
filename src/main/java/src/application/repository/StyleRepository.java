package src.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import src.application.model.Style;

@Repository
public interface StyleRepository extends JpaRepository<Style, Integer> {
}
