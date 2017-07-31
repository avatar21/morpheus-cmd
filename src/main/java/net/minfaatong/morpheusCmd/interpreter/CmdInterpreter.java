package net.minfaatong.morpheusCmd.interpreter;

import net.minfaatong.morpheusCmd.TerminalErrorOutputStream;
import net.minfaatong.morpheusCmd.TerminalOutputStream;
import org.apache.commons.exec.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class CmdInterpreter {
    private static final Logger logger = Logger.getLogger(CmdInterpreter.class.getCanonicalName());
    private static final Long RUNAWAY_TIMEOUT = 60 * 1000L;
    private final boolean isWindows = System.getProperty("os.name").startsWith("Windows");
    private final String shell = isWindows ? "cmd /c" : "bash -c";
    ConcurrentHashMap<String, DefaultExecutor> executors;

    public CmdInterpreter() {
    }

    public Boolean interpret(String cmd, TerminalOutputStream infoTermOutStream, TerminalErrorOutputStream errorTermOutStream) {
        Boolean isSuccess = false;
        CommandLine cmdLine = CommandLine.parse(shell);
        String taskId = "";
        // the Windows CMD shell doesn't handle multiline statements,
        // they need to be delimited by '&&' instead
        if (isWindows) {
            String[] lines = StringUtils.split(cmd, "\n");
            cmd = StringUtils.join(lines, " && ");
        }
        cmdLine.addArgument(cmd, false);
        int exitVal = -1;

        try {
            DefaultExecutor executor = new DefaultExecutor();
            DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
            executor.setStreamHandler(new PumpStreamHandler(
                    infoTermOutStream, errorTermOutStream));
            executor.setExitValue(1);
            executor.setWatchdog(new ExecuteWatchdog(RUNAWAY_TIMEOUT));
            //executors.put(contextInterpreter.getParagraphId(), executor);
//            executor.execute(cmdLine, resultHandler);
//            resultHandler.waitFor();
            exitVal = executor.execute(cmdLine);
            if (exitVal == 1) {
                isSuccess = true;
            }
            //logger.info("Paragraph " + .getParagraphId()
            //        + " return with exit value: " + exitVal);
            //return new InterpreterResult(Code.SUCCESS, outStream.toString());
        } catch (ExecuteException e) {
            int execExitVal = e.getExitValue();
            logger.warning(String.format("Can not run %s (exit=%d). %s", cmd, execExitVal, e.getLocalizedMessage()));
        } catch (IOException e) {
            logger.warning(e.getLocalizedMessage());
        } finally {
            //executors.remove(taskId);
            return isSuccess;
        }
    }
}
