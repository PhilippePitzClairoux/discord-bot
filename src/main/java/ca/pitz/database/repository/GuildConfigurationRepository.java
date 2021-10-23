package ca.pitz.database.repository;

import ca.pitz.database.GuildConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuildConfigurationRepository extends JpaRepository<GuildConfiguration, Integer> {
    List<GuildConfiguration> findByGuild(int guild);

    List<GuildConfiguration> findByConfig(Integer config);

    boolean existsByGuildAndEnabledAndConfig(int guild, boolean enabled, int config);

}
