package de.hamburgchimps.apple.notes.liberator.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "ZICCLOUDSYNCINGOBJECT")
public class NotesCloudObject extends Base {
    public String zTypeUti;
    public LocalDateTime zCreationDate;
    public LocalDateTime zModificationDate;
    public String zIdentifier;
    public String zTitle1;
    public String zTitle2;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zMedia")
    public NotesCloudObject zMedia;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zFolder")
    public NotesCloudObject zFolder;
    public String zFileName;
    public byte[] zMergeableData;
}
