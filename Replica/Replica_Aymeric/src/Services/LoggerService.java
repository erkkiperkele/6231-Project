package Services;

import Contracts.IFileLogger;
import Contracts.ILoggerService;
import IO.FileLogger;

import java.io.*;

public class LoggerService implements ILoggerService, Closeable {
    //    private Map<Integer, IFileLogger> _loggers;
    private IFileLogger logger;
    private final String rootPath = "./LogsServer/";

    public LoggerService() {
    }


    @Override
    public void close() throws IOException {
        this.logger.close();
    }

    @Override
    public IFileLogger getLogger() {

        return getLazyLogger();
    }

    private IFileLogger getLazyLogger() {


        if (this.logger == null) {
            this.logger = createLogger();
        }
        return this.logger;
    }

    private IFileLogger createLogger() {

        String serverName = SessionService.getInstance().getBank().name();
        String path = this.rootPath
                + serverName
                + ".txt";

        createFile(path);

        IFileLogger newLogger = new FileLogger(path);

        return newLogger;
    }

    private void createFile(String path) {
        try {
            File f = new File(path);

            if (!f.exists()) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
