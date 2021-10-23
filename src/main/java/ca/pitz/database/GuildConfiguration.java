package ca.pitz.database;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "guild_configuration")
public class GuildConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "guild")
    private int guild;

    @Column(name = "config")
    private int config;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "extra")
    private String extra;

}
