package ca.pitz.database;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "whitelist_type")
public class WhitelistType {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @Column(name = "type")
    private String type;

    @Column(name = "description")
    private String description;
}

