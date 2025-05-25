package src.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import src.application.model.Brewery;

@Repository
public interface BreweryRepository extends JpaRepository<Brewery, Long> {
}
