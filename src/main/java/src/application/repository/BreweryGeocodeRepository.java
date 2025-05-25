package src.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import src.application.model.Breweries_Geocode;

@Repository
public interface BreweryGeocodeRepository extends JpaRepository<Breweries_Geocode, Long> {
}
