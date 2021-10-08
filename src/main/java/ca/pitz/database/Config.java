package ca.pitz.database;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "configs")
public class Config {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @Column(name = "config")
    private String config;

}
