package com.devexperts.dxlab.lincheck.generators;


import com.devexperts.dxlab.lincheck.util.MyRandom;
import com.devexperts.dxlab.lincheck.util.ParameterizedGenerator;

public class StringGenerator implements ParameterizedGenerator {
    private String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    private int length = 10;
    private int maxWordLength = 10;

    public StringGenerator() {
    }

    public String[] generate() {
        String[] strings = new String[length];
        for (int i = 0; i < length; i++) {
            int l = MyRandom.nextInt(maxWordLength);
            StringBuilder stringBuilder = new StringBuilder();
            for (int j = 0; j < l; j++) {
                stringBuilder.append(alphabet[MyRandom.nextInt(alphabet.length)]);
            }
            strings[i] = stringBuilder.toString();
        }
        return strings;
    }

    public void setParameters(String... params) {
        this.length = Integer.parseInt(params[0]);
        this.maxWordLength = Integer.parseInt(params[1]);
        this.alphabet = params[2].split(",");
    }
}
