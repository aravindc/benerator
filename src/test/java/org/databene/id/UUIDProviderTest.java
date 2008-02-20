package org.databene.id;

import org.databene.test.IteratorTest;

/**
 * Tests the {@link UUIDProvider}.<br/><br/>
 * Created: 13.02.2008 09:14:03
 * @author Volker Bergmann
 */
public class UUIDProviderTest extends IteratorTest {
    
    public void testDefaultFormat() {
        checkUniqueIteration(new UUIDProvider(), 10);
    }

    public void testEmptySeparator() {
        checkUniqueIteration(new UUIDProvider(""), 10);
    }

    public void testMinusSeparator() {
        checkUniqueIteration(new UUIDProvider("-"), 10);
    }

}
