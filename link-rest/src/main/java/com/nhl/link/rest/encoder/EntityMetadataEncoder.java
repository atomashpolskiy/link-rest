package com.nhl.link.rest.encoder;

import com.fasterxml.jackson.core.JsonGenerator;
import com.nhl.link.rest.meta.LrAttribute;
import com.nhl.link.rest.meta.LrEntity;
import com.nhl.link.rest.meta.LrRelationship;
import org.apache.cayenne.dba.TypesMapping;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class EntityMetadataEncoder extends AbstractEncoder {

    private LrEntity<?> entity;
    private Map<String, PropertyMetadataEncoder> propertyMetadataEncoders;
    private Map<String, PropertyHelper> properties;

    public EntityMetadataEncoder(LrEntity<?> entity, Map<String, PropertyMetadataEncoder> propertyMetadataEncoders) {
        this.entity = entity;
        this.propertyMetadataEncoders = propertyMetadataEncoders;

        this.properties = new TreeMap<>();
        for (LrAttribute attribute : entity.getAttributes()) {
            properties.put(attribute.getName(), new AttributeProperty(attribute));
        }
        for (LrRelationship relationship : entity.getRelationships()) {
            properties.put(relationship.getName(), new RelationshipProperty(relationship));
        }
    }

    @Override
    protected boolean encodeNonNullObject(Object object, JsonGenerator out) throws IOException {
        // sanity check
        if (!entity.equals(object)) {
            throw new IllegalArgumentException(
                    "Expected entity: " + entity.getName() + ", was object of class: " + object.getClass().getName()
            );
        }

        out.writeStartObject();

        out.writeStringField("name", entity.getName());

        out.writeArrayFieldStart("properties");
        for (Map.Entry<String, PropertyHelper> e : properties.entrySet()) {

            String typeName;
            if (byte[].class.equals(e.getValue().getType())) {
                typeName = TypesMapping.JAVA_BYTES;
            } else {
                typeName = e.getValue().getType().getName();
            }
            Encoder encoder = propertyMetadataEncoders.get(typeName);
            if (encoder == null) {
                encoder = PropertyMetadataEncoder.encoder();
            }
            encoder.encode(null, e.getValue().getProperty(), out);
        }
        out.writeEndArray();

        out.writeEndObject();

        return true;
    }

    private static abstract class PropertyHelper {
        abstract Class<?> getType();
        abstract Object getProperty();
    }

    private static class AttributeProperty extends PropertyHelper {
        private LrAttribute attribute;

        AttributeProperty(LrAttribute attribute) {
            this.attribute = attribute;
        }

        @Override
        Class<?> getType() {
            return attribute.getType();
        }

        @Override
        Object getProperty() {
            return attribute;
        }
    }

    private static class RelationshipProperty extends PropertyHelper {
        private LrRelationship relationship;

        RelationshipProperty(LrRelationship relationship) {
            this.relationship = relationship;
        }

        @Override
        Class<?> getType() {
            return relationship.getTargetEntityType();
        }

        @Override
        Object getProperty() {
            return relationship;
        }
    }

}
