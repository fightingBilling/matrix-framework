package org.matrix.framework.core.collection.converter;

import java.util.Stack;

import org.matrix.framework.core.platform.exception.MatrixException;

public class DecimalHexConvert {

    private final DecimalHexAlgorithm hexAlgorithm;

    public DecimalHexConvert() {
        this.hexAlgorithm = new DecimalHexAlgorithm() {
            public String[] getHexCharacters() {
                return new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
            }
        };
    }

    public DecimalHexConvert(DecimalHexAlgorithm hexAlgorithm) {
        this.hexAlgorithm = hexAlgorithm;
    }

    public String toDecimalHex(long value) {
        StringBuilder builder = new StringBuilder();
        Stack<Long> stack = new Stack<Long>();
        String[] characters = this.hexAlgorithm.getHexCharacters();
        int bitSize = characters.length;
        if (bitSize < 11)
            throw new MatrixException("Bit size must gt 10.");
        decimalHexConvert(stack, value, bitSize);
        int length = stack.size();
        for (int k = 0; k < length; k++) {
            long index = ((Long) stack.pop()).longValue();
            builder.append(characters[((int) index)]);
        }
        return builder.toString();
    }

    private void decimalHexConvert(Stack<Long> stack, long value, int bitSize) {
        long modValue = value % bitSize;
        long resultValue = value / bitSize;
        stack.push(Long.valueOf(modValue));
        if (resultValue != 0L)
            decimalHexConvert(stack, resultValue, bitSize);
    }

    public static abstract interface DecimalHexAlgorithm {
        public abstract String[] getHexCharacters();
    }
}
