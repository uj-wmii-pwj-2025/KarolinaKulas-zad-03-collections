package uj.wmii.pwj.collections;

import java.io.InputStream;
import java.io.IOException;
import java.io.PrintStream;

public interface Brainfuck {

    void execute();

    /**
     * Creates a new instance of Brainfuck interpreter with given program, using
     * standard IO and stack of 1024 size.
     * 
     * @param program brainfuck program to interpret
     * @return new instance of the interpreter
     * @throws IllegalArgumentException if program is null or empty
     */
    static Brainfuck createInstance(String program) {
        return createInstance(program, System.out, System.in, 1024);
    }

    /**
     * Creates a new instance of Brainfuck interpreter with given parameters.
     * 
     * @param program   brainfuck program to interpret
     * @param out       output stream to be used by interpreter implementation
     * @param in        input stream to be used by interpreter implementation
     * @param stackSize maximum stack size, that is allowed for this interpreter
     * @return new instance of the interpreter
     * @throws IllegalArgumentException if: program is null or empty, OR out is
     *                                  null, OR in is null, OR stackSize is below
     *                                  1.
     */
    static Brainfuck createInstance(String program, PrintStream out, InputStream in, int stackSize) {
        if (program == null || program.isEmpty() || out == null || in == null || stackSize < 1) {
            throw new IllegalArgumentException("Invalid arguments for Brainfuck interpreter initialization.");
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
                        throw new IllegalStateException("Data pointer overflow. Reached end of memory array.");
                    }
                    break;
                case '<':
                    dataPointer--;
                    if (dataPointer < 0) {
                        throw new IllegalStateException("Data pointer underflow. Reached beginning of memory array.");
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
                        throw new RuntimeException("Error reading input", e);
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

    /**
     * Wyszukuje pasujący nawias kwadratowy ('[' dla ']' lub ']' dla '[').
     * 
     * @param startPointer Indeks rozpoczęcia przeszukiwania.
     * @param direction    Kierunek przeszukiwania: 1 dla ']' (w przód), -1 dla '['
     *                     (w tył).
     * @return Indeks pasującego nawiasu lub (dla pętli) indeks instrukcji po nim.
     */
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

        throw new IllegalStateException("Unmatched bracket encountered at index: " + startPointer);
    }
}