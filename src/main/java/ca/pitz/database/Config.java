package ca.pitz.database;

import javax.persistence.*;

@Entity
@Table(name = "configs")
public class Config {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "config")
    private String config;

}
