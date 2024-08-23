package com.alfie.logger.restlogger;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.EncoderBase;

import java.nio.charset.StandardCharsets;

public class CustomLogEncoder extends EncoderBase<ILoggingEvent> {

    @Override
    public byte[] encode(ILoggingEvent event) {
        String formattedMessage = String.format(
                "{\"timestamp\":\"%s\", \"level\":\"%s\", \"logger\":\"%s\", \"message\":\"%s\"}\n",
                event.getTimeStamp(),
                event.getLevel(),
                event.getLoggerName(),
                event.getFormattedMessage()
        );
        return formattedMessage.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] headerBytes() {
        return null; // Optionally, define a header if needed
    }

    @Override
    public byte[] footerBytes() {
        return null; // Optionally, define a footer if needed
    }
}
