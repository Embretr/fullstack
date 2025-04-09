package stanism.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stanism.marketplace.model.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
} 