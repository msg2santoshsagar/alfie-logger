package com.alfie.logger.restlogger;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

public class RestLogbackAppender extends AppenderBase<ILoggingEvent> {

    @Override
    protected void append(ILoggingEvent eventObject) {

        // Custom logic for handling log events
        String logMessage = eventObject.getFormattedMessage();

        // Example: Append the log message to a file, send it to a service, etc.
        System.out.println("[CustomAppender] " + logMessage);

        // Add your custom logic here
    }

}
