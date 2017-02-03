package com.devexperts.dxlab.lincheck.generators;

import com.devexperts.dxlab.lincheck.ParameterGenerator;

import java.util.Random;

public class StringGen implements ParameterGenerator<String> {
    private static final int DEFAULT_MAX_WORD_LENGTH = 15;
    private static final String DEFAULT_ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_ ";

    private final Random random = new Random();
    private final int maxWordLength;
    private final String alphabet;

    public StringGen(String configuration) {
        if (configuration.isEmpty()) { // use default configuration
            maxWordLength = DEFAULT_MAX_WORD_LENGTH;
            alphabet = DEFAULT_ALPHABET;
            return;
        }
        int firstCommaIndex = configuration.indexOf(':');
        if (firstCommaIndex < 0) { // maxWordLength only
            maxWordLength = Integer.parseInt(configuration);
            alphabet = DEFAULT_ALPHABET;
        } else { // maxWordLength:alphabet
            maxWordLength = Integer.parseInt(configuration.substring(0, firstCommaIndex));
            alphabet = configuration.substring(firstCommaIndex + 1);
        }
    }

    public String generate() {
        char[] cs = new char[random.nextInt(maxWordLength)];
        for (int i = 0; i < cs.length; i++)
            cs[i] = alphabet.charAt(random.nextInt(alphabet.length()));
        return new String(cs);
    }
}
