package org.databene.id;

import org.databene.commons.iterator.IteratorTest;

/**
 * Tests the {@link IncrementIdProvider}<br/><br/>
 * Created: 13.02.2008 09:14:23
 * @author Volker Bergmann
 */
public class IncrementIdProviderTest extends IteratorTest {

    public void testDefault() {
        checkIteration(new IncrementIdProvider(), 1L);
    }

    public void testInitialTen() {
        checkIteration(new IncrementIdProvider(10L), 10L);
    }

    private void checkIteration(IncrementIdProvider p, long initialValue) {
        assertTrue(p.hasNext());
        assertEquals(initialValue, (long) p.next());
        assertTrue(p.hasNext());
        assertEquals(initialValue + 1, (long) p.next());
        checkUniqueIteration(p, 10);
    }
}
