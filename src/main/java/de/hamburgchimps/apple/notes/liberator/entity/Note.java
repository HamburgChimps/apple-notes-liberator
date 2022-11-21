package de.hamburgchimps.apple.notes.liberator.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ZICNOTEDATA")
public class Note extends Base {
    @Column
    public int zNote;

    @Override
    public String toString() {
        return "Note{" +
                "zNote=" + zNote +
                ", zPk=" + zPk +
                ", zEnt=" + zEnt +
                ", zOpt=" + zOpt +
                '}';
    }
}
