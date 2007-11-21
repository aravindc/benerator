package org.databene.benerator.primitive;

import org.databene.benerator.GeneratorClassTest;
import org.databene.commons.ArrayUtil;

import java.util.Locale;
import java.util.HashSet;
import java.util.Set;

/**
 * (c) Copyright 2006 by Volker Bergmann
 * Created: 09.06.2006 21:03:42
 */
public class CharacterGeneratorTest extends GeneratorClassTest {

    public CharacterGeneratorTest() {
        super(CharacterGenerator.class);
    }

    public void testDefaultConstructor() {
        new CharacterGenerator();
    }

    public void testDigit() throws Exception {
        checkProductSet(new CharacterGenerator("\\d"), 1000,
                ArrayUtil.toSet('0', '1', '2', '3', '4', '5', '6', '7','8', '9'));
    }

    public void testRange() throws Exception {
        checkProductSet(new CharacterGenerator("[1-2]"), 1000, ArrayUtil.toSet('1', '2'));
        checkProductSet(new CharacterGenerator("[12]"), 1000, ArrayUtil.toSet('1', '2'));
    }

    public void testLocale() throws Exception {
        HashSet<Character> expectedSet = new HashSet<Character>();
        for (char c = 'A'; c <= 'Z'; c++)
            expectedSet.add(c);
        for (char c = 'a'; c <= 'z'; c++)
            expectedSet.add(c);
        for (char c = '0'; c <= '9'; c++)
            expectedSet.add(c);
        expectedSet.add('_');
        expectedSet.add('Ä');
        expectedSet.add('ä');
        expectedSet.add('Ö');
        expectedSet.add('ö');
        expectedSet.add('Ü');
        expectedSet.add('ü');
        expectedSet.add('ß');

        checkProductSet(new CharacterGenerator("\\w", Locale.GERMAN), 10000, expectedSet);
    }

    public void testSet() {
        Set<Character> values = ArrayUtil.toSet('A', 'B');
        checkProductSet(new CharacterGenerator(values), 1000, values);
    }
}
