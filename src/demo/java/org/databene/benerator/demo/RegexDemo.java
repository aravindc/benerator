package org.databene.benerator.demo;

import org.databene.benerator.Generator;
import org.databene.benerator.factory.GeneratorFactory;

/**
 * Demonstrates the use of the regular expression generator
 * by generating phone numbers and email addresses.<br/>
 * <br/>
 * Created: 07.09.2006 21:01:53
 */
public class RegexDemo {

    private static final String PHONE_PATTERN = "\\+[1-9][0-9]{1,2}/[1-9][0-9]{0,4}/[1-9][0-9]{4,8}";
    private static final String EMAIL_PATTERN = "[a-z][a-z0-9\\.]{3,12}[a-z0-9]@[a-z0-9]{3,12}\\.com";

    public static void main(String[] args) {
        Generator<String> phoneGenerator = GeneratorFactory.getRegexStringGenerator(PHONE_PATTERN, 1, 16, null, 0);
        for (int i = 0; i < 5; i++)
            System.out.println(phoneGenerator.generate());
        Generator<String> emailGenerator = GeneratorFactory.getRegexStringGenerator(EMAIL_PATTERN, 1, 16, null, 0);
        for (int i = 0; i < 5; i++)
            System.out.println(emailGenerator.generate());
    }
}
