package solitour_backend.solitour.place.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solitour_backend.solitour.place.entity.Place;


public interface PlaceRepository extends JpaRepository<Place, Long> {

}