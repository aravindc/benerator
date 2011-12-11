package org.databene.model.data;

public class IdDescriptor extends ComponentDescriptor {

    public IdDescriptor(String name, DescriptorProvider owner) {
        this(name, owner, (String) null);
    }

    public IdDescriptor(String name, DescriptorProvider owner, String type) {
        super(name, owner, type);
    }

    public IdDescriptor(String name, DescriptorProvider owner, TypeDescriptor type) {
        super(name, owner, type);
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