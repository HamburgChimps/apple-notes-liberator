package de.hamburgchimps.apple.notes.liberator.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ZICNOTEDATA")
public class Note extends Base {
    public int zNote;

    public byte[] zData;
}
