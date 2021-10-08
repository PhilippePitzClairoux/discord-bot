package ca.pitz.database;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "guild_configuration")
public class GuildConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
