package org.databene.model.data;

public class IdDescriptor extends ComponentDescriptor {

    public IdDescriptor(String name) {
        this(name, (String) null);
    }

    public IdDescriptor(String name, String type) {
        super(name, type);
    }

    public IdDescriptor(String name, TypeDescriptor type) {
        super(name, type);
    }

    @Override
    public Uniqueness getUniqueness() {
        return Uniqueness.ORDERED;
    }
    
    @Override
    public Boolean isUnique() {
        return true;
    }
    
}