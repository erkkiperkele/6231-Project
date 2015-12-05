package shared.udp.console;

import java.io.InputStream;
import java.util.Scanner;

/**
 * a simple helper decorator to read and write from the console.
 */
public class Console {

    private static Scanner console;

    public Console(InputStream in) {
        this.console = new Scanner(in);
    }

    /**
     * Provides a new line in a platform independent manner
     *
     * @return
     */
    public String newLine() {
        return System.lineSeparator();
    }

    /**
     * Reads a single char from the console input
     *
     * @return
     */
    public char readChar() {
        String answer = this.readLine().trim();

        if (answer.equals("")) {
            return '0';
        }

        return answer.charAt(0);
    }

    /**
     * reads a string from the console input
     *
     * @return
     */
    public String readString() {
        return this.readLine().trim();
    }

    /**
     * reads an integer from the console input
     *
     * @return
     */
    public int readint() throws NumberFormatException {
        String input = this.readLine().trim();
        int answer = 0;

        if (input.equals("")) {
            return answer;
        }

        answer = Integer.parseInt(input);
        return answer;
    }

    /**
     * outputs a message to the console
     *
     * @param message
     */
    public void println(String message) {
        System.out.println(message);
    }

    private String readLine() {
        return this.console.nextLine();
    }
}
