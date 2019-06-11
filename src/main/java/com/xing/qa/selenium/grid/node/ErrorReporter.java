package com.xing.qa.selenium.grid.node;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.openqa.grid.common.exception.RemoteException;
import org.openqa.grid.internal.TestSession;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * ErrorReporter
 *
 * @author Jens Hausherr (jens.hausherr@xing.com)
 */
class ErrorReporter extends BaseSeleniumReporter {

    private final RemoteException exception;

    public ErrorReporter(String remoteHostName, InfluxDB influxdb, String database, RemoteException ex) {
        super(remoteHostName, influxdb, database);
        this.exception = ex;
    }

    @Override
    protected void report() {
        final Point exRep = Point.measurement("node.errors").addField("time", System.currentTimeMillis())
                .addField("host", remoteHostName).addField("error", exception.getClass().getName())
                .addField("message", exception.getMessage()).build();
        write(exRep);
    }

}
