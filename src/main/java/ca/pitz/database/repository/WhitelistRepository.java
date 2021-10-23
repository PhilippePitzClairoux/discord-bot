package ca.pitz.database.repository;

import ca.pitz.database.Whitelist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface WhitelistRepository extends JpaRepository<Whitelist, Integer> {

    Whitelist findByGuild(String guild);

    List<Whitelist> findByGuild(int guild);

}
