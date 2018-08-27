package de.hpi.isg.dataprep.metadata;

import de.hpi.isg.dataprep.model.target.Metadata;
import de.hpi.isg.dataprep.util.DatePattern;

/**
 * @author Lan Jiang
 * @since 2018/8/26
 */
public class PropertyDatePattern extends Metadata {

    private final String name = "property-date-pattern";

    private String propertyName;
    private DatePattern.DatePatternEnum datePattern;

    public PropertyDatePattern(String propertyName, DatePattern.DatePatternEnum datePattern) {
        this.propertyName = propertyName;
        this.datePattern = datePattern;
    }

    @Override
    public String getName() {
        return name;
    }
}