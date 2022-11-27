package de.hamburgchimps.apple.notes.liberator.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "ZICNOTEDATA")
// it appears we have to implement serializable when using assocations
// that reference a non-primary key column.
// Found a related hibernate bug here: https://hibernate.atlassian.net/browse/HHH-7668
public class Note extends Base implements Serializable {
    public int zNote;

    public byte[] zData;

    @OneToMany(mappedBy = "note", fetch = FetchType.LAZY)
    public Set<EmbeddedObject> embeddedObjects;
}
