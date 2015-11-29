package IO;

import Contracts.IFileLogger;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * A simple implementation for a file logger.
 * It's responsibility is to format a log request depending on its category (info, warn, ...)
 */
public class FileLogger implements IFileLogger {

    private FileOutputStream logger;

    public FileLogger(String path) {

        try {
            this.logger = new FileOutputStream(path, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        this.logger.close();
    }

    @Override
    public void info(String message) {
        write(" INFO: " + message);
    }

    @Override
    public void warn(String message) {
        write(" WARN: " + message);
    }

    @Override
    public void error(String message) {
        write(" ERROR: " + message);
    }

    private void write(String message) {

        String outputMessage = new Date() + message + "\n";

        try {
            this.logger.write(outputMessage.getBytes());
            System.out.println(outputMessage.trim());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
