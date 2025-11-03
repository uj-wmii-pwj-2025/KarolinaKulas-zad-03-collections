package uj.wmii.pwj.collections;

import java.io.InputStream;
import java.io.IOException;
import java.io.PrintStream;

public interface Brainfuck {

    void execute();

    static Brainfuck createInstance(String program) {
        return createInstance(program, System.out, System.in, 1024);
    }

    static Brainfuck createInstance(String program, PrintStream out, InputStream in, int stackSize) {
        if (program == null || program.isEmpty() || out == null || in == null || stackSize < 1) {
            throw new IllegalArgumentException("error");
        }
        return new BrainfuckInterpreter(program, out, in, stackSize);
    }
}

class BrainfuckInterpreter implements Brainfuck {

    private final String program;
    private final PrintStream out;
    private final InputStream in;
    private final byte[] memory;
    private int dataPointer;

    public BrainfuckInterpreter(String program, PrintStream out, InputStream in, int stackSize) {
        this.program = program;
        this.out = out;
        this.in = in;
        this.memory = new byte[stackSize];
        this.dataPointer = 0;
    }

    @Override
    public void execute() {
        int programPointer = 0;

        while (programPointer < program.length()) {
            char instruction = program.charAt(programPointer);

            switch (instruction) {
                case '>':
                    dataPointer++;
                    if (dataPointer >= memory.length) {
                        throw new IllegalStateException("error");
                    }
                    break;
                case '<':
                    dataPointer--;
                    if (dataPointer < 0) {
                        throw new IllegalStateException("error");
                    }
                    break;
                case '+':
                    memory[dataPointer]++;
                    break;
                case '-':
                    memory[dataPointer]--;
                    break;
                case '.':
                    out.write(memory[dataPointer]);
                    break;
                case ',':
                    try {
                        int readByte = in.read();
                        if (readByte != -1) {
                            memory[dataPointer] = (byte) readByte;
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("error", e);
                    }
                    break;
                case '[':
                    if (memory[dataPointer] == 0) {
                        programPointer = findMatchingBracket(programPointer, 1);
                    }
                    break;
                case ']':
                    if (memory[dataPointer] != 0) {
                        programPointer = findMatchingBracket(programPointer, -1);
                    }
                    break;
                default:
                    break;
            }

            programPointer++;
        }
    }

    private int findMatchingBracket(int startPointer, int direction) {
        char targetBracket = (direction == 1) ? ']' : '[';
        char searchBracket = (direction == 1) ? '[' : ']';
        int balance = 1;
        int currentPointer = startPointer + direction;

        while (currentPointer >= 0 && currentPointer < program.length()) {
            char current = program.charAt(currentPointer);

            if (current == searchBracket) {
                balance++;
            } else if (current == targetBracket) {
                balance--;
                if (balance == 0) {
                    return (direction == 1) ? currentPointer : currentPointer - 1;
                }
            }

            currentPointer += direction;
        }

        throw new IllegalStateException("error ");
    }
}