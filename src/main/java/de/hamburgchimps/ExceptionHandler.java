package de.hamburgchimps;

import picocli.CommandLine;

public class ExceptionHandler implements CommandLine.IExecutionExceptionHandler {
    @Override
    public int handleExecutionException(Exception e, CommandLine commandLine, CommandLine.ParseResult parseResult) {
        System.out.println(CommandLine.Help.Ansi.AUTO.string(String.format("@|bold,red %s |@", e.getMessage())));
        return -1;
    }
}
