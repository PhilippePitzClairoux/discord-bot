package ca.pitz.database;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "guilds")
public class Guild {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "name")
    private String name;
}
