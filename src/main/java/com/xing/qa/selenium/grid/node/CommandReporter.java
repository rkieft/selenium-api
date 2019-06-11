package com.xing.qa.selenium.grid.node;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.openqa.grid.internal.ExternalSessionKey;
import org.openqa.grid.internal.TestSession;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * CommandReporter
 *
 * @author Jens Hausherr (jens.hausherr@xing.com)
 */
class CommandReporter extends BaseSeleniumReporter {

    protected final TestSession session;
    protected final ContentSnoopingRequest request;
    protected final HttpServletResponse response;
    protected final ReportType type;

    public CommandReporter(String remoteHostName, InfluxDB influxdb, String database, TestSession session, ContentSnoopingRequest request, HttpServletResponse response, ReportType type) {
        super(remoteHostName, influxdb, database);
        this.type = type;
        this.request = request;
        this.session = session;
        this.response = response;
    }

    protected void report() {
        ExternalSessionKey esk = session.getExternalKey();
        String sessionKey = null;
        if (esk != null) {
            sessionKey = esk.getKey();
        }

        Point s = Point.measurement(String.format("session.cmd.%s.measure", type))
                        .addField("time",System.currentTimeMillis())
                        .addField("host",remoteHostName)
                        .addField("ext_key",sessionKey)
                        .addField("int_key",session.getInternalKey())
                        .addField("forwarding",session.isForwardingRequest())
                        .addField("orphaned",session.isOrphaned())
                        .addField("inactivity",session.getInactivityTime())
                        .addField("cmd_method",request.getMethod())
                        .addField("cmd_action",request.getPathInfo())
                        .addField("cmd",request.getContent())
                        .build();
        write(s);
    }
}
