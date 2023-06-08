package de.hamburgchimps.apple.notes.liberator.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "ZICNOTEDATA")
public class Note extends Base {
    public int zNote;

    public byte[] zData;
}
