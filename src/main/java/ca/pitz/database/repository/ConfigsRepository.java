package ca.pitz.database.repository;

import ca.pitz.database.Config;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigsRepository extends JpaRepository<Config, Integer> {
}
