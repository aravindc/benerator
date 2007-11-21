package org.databene.domain.person;

import junit.framework.TestCase;

import java.util.Locale;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 09.06.2006 22:14:08
 */
public class SalutationProviderTest extends TestCase {

    public void test() {
        check(Locale.GERMAN,  "Frau", "Herr");
        check(Locale.FRENCH,  "Mme",  "Mr");
        check(Locale.ENGLISH, "Mrs.", "Mr.");
    }

    private void check(Locale locale, String femaleSalutation, String maleSalutation) {
        SalutationProvider provider = new SalutationProvider(locale);
        assertEquals(femaleSalutation, provider.salutation(Gender.FEMALE));
        assertEquals(maleSalutation, provider.salutation(Gender.MALE));
    }
}
