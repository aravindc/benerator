package org.databene.benerator.wrapper;

import org.databene.benerator.Generator;
import org.databene.id.IdProvider;

public class IdGenerator<E> implements Generator<E> {

    private IdProvider<E> source;
    
    public IdGenerator(IdProvider<E> source) {
        this.source = source;
    }
    
    // properties ------------------------------------------------------------------------------------------------------
    
    /**
     * @return the source
     */
    public IdProvider<E> getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(IdProvider<E> source) {
        this.source = source;
    }
    
    // Generator interface ---------------------------------------------------------------------------------------------

    public Class<E> getGeneratedType() {
        return source.getType();
    }

    public void validate() {
        if (source == null)
            throw new IllegalArgumentException("source is null");
    }

    public boolean available() {
        return source.hasNext();
    }

    public void close() {
        source.close();
    }

    public E generate() {
        return source.next();
    }

    public void reset() {
        // ignored
    }

}
