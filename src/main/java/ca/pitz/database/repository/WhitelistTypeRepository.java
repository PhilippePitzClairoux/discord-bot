package ca.pitz.database.repository;

import ca.pitz.database.WhitelistType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface WhitelistTypeRepository extends JpaRepository<WhitelistTypeRepository, Integer> {

    WhitelistType findByType(String type);

}
