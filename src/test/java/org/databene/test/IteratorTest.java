package org.databene.test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;

public abstract class IteratorTest extends TestCase {

    public static <T> void checkUniqueIteration(Iterator<T> iterator, int count) {
        Set<T> items = new HashSet<T>(count);
        for (int i = 0; i < count; i++) {
            assertTrue(iterator.hasNext());
            T item = iterator.next();
            assertFalse(items.contains(item));
            items.add(item);
        }
    }

}
