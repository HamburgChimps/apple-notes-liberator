package de.hamburgchimps.apple.notes.liberator.service;

import de.hamburgchimps.apple.notes.liberator.entity.EmbeddedObject;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class EmbeddedObjectService {
    public List<EmbeddedObject> getAllEmbeddedObjects() {
        return EmbeddedObject.listAll();
    }
}
