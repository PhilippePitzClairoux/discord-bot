package ca.pitz.database.repository;

import ca.pitz.database.Guild;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface GuildsRepository extends JpaRepository<Guild, Integer> {
    Guild findByName(String name);
}
