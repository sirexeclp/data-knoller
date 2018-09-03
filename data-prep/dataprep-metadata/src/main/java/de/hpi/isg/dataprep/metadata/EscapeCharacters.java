package de.hpi.isg.dataprep.metadata;

import de.hpi.isg.dataprep.exceptions.RuntimeMetadataException;
import de.hpi.isg.dataprep.model.repository.MetadataRepository;
import de.hpi.isg.dataprep.model.target.object.Metadata;
import de.hpi.isg.dataprep.model.target.object.OperatedObject;

import java.util.Set;

/**
 * @author Lan Jiang
 * @since 2018/8/28
 */
public class EscapeCharacters extends Metadata {

    private final String name = "escape-characters";

    private OperatedObject target;

    private Set<String> escapeCharacters;

    public EscapeCharacters(Set<String> escapeCharacters) {
        this.escapeCharacters = escapeCharacters;
    }

    @Override
    public void checkMetadata(MetadataRepository metadataRepository) throws RuntimeMetadataException {

    }

    @Override
    public String getTargetName() {
        return null;
    }

    public Set<String> getEscapeCharacters() {
        return escapeCharacters;
    }

    @Override
    public String getName() {
        return name;
    }
}
