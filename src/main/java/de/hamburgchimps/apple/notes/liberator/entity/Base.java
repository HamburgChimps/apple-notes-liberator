package de.hamburgchimps.apple.notes.liberator.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class Base extends PanacheEntityBase {
    @Id
    @Column(name = "Z_PK")
    public Integer zPk;

    @Column(name = "Z_ENT")
    public Integer zEnt;

    @Column(name = "Z_OPT")
    public Integer zOpt;
}
