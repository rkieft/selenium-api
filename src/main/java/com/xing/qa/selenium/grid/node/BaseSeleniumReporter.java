package com.xing.qa.selenium.grid.node;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.openqa.grid.internal.ExternalSessionKey;
import org.openqa.grid.internal.TestSession;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * BaseSeleniumReporter
 *
 * @author Jens Hausherr (jens.hausherr@xing.com)
 */
public abstract class BaseSeleniumReporter implements Runnable {

    protected final String remoteHostName;
    protected final Logger log = Logger.getLogger(getClass().getName());
    private final String database;
    private final InfluxDB influxdb;

    public BaseSeleniumReporter(String remoteHostName, InfluxDB influxdb, String database) {
        this.remoteHostName = remoteHostName;
        this.influxdb = influxdb;
        this.database = database;
    }

    @Override
    public final void run() {
        try {
            report();
        } catch (Exception e) {
            log.warning(e.getMessage());
        }
    }

    protected abstract void report();

    protected void write(Point... points) {
        for (Point p : points) {
            influxdb.write(p);
        }
    }
}
