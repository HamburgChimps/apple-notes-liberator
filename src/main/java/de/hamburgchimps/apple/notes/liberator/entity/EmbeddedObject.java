package de.hamburgchimps.apple.notes.liberator.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "ZICCLOUDSYNCINGOBJECT")
public class EmbeddedObject extends Base {
    public String zTypeUti;

    public Integer zNote;

    public LocalDateTime zCreationDate;

    public LocalDateTime zModificationDate;

    public String zIdentifier;

    public String zTitle;

    public byte[] zServerRecordData;

    public byte[] zMergeableData;
}
