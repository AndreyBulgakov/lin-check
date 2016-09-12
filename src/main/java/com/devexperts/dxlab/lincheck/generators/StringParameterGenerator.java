package com.devexperts.dxlab.lincheck.generators;


import com.devexperts.dxlab.lincheck.Checker;

/**
 * Float numbers generator
 * Constructor parameters
 * <ul>
 *     <li>maxWordLength default value = 100</li>
 *     <li>alphabet default value = letters in two registers + numbers</li>
 * </ul>
 */
public class StringParameterGenerator implements ParameterGenerator {
    private String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
    private int maxWordLength = 100;
    public StringParameterGenerator(String maxWordLength, String alphabet) {
        this.maxWordLength = Integer.parseInt(maxWordLength);
        this.alphabet = alphabet.split(",");
    }

    public StringParameterGenerator(String maxWordLength) {
        this.maxWordLength = Integer.parseInt(maxWordLength);
    }

    public StringParameterGenerator() {
    }

    public String generate() {
        int l = Checker.r.nextInt(maxWordLength);
        StringBuilder stringBuilder = new StringBuilder();
        for (int j = 0; j < l; j++) {
            stringBuilder.append(alphabet[Checker.r.nextInt(alphabet.length)]);
        }
        return stringBuilder.toString();
    }
}
