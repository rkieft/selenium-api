package com.xing.qa.selenium.grid.node;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.openqa.grid.internal.ExternalSessionKey;
import org.openqa.grid.internal.TestSession;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

/**
* Session Reporter
*
* @author Jens Hausherr (jens.hausherr@xing.com)
*/
class SessionReporter extends BaseSeleniumReporter {
    private final TestSession session;
    private final ReportType type;
    private final InfluxDB influxdb;
    private final String database;

    public SessionReporter(String remoteHostName, InfluxDB influxdb, String database, TestSession session, ReportType type) {
        super(remoteHostName, influxdb, database);
        this.session = session;
        this.type = type;
        this.influxdb = influxdb;
        this.database = database;
    }

    @Override
    protected void report() {
        ExternalSessionKey esk = session.getExternalKey();
        String sessionKey = null;

        if (esk != null) {
            sessionKey = esk.getKey();
        }

        final Boolean forwardingRequest = session.isForwardingRequest();
        final Boolean orphaned = session.isOrphaned();
        final Long inactivityTime =session.getInactivityTime();
        final long time = System.currentTimeMillis();
        

        final Point.Builder sessionEvent =  Point.measurement("session.event.measure")
        .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        
        sessionEvent.addField("time", time)
        .addField("host", remoteHostName)
        .addField("type", type.toString())
        .addField("ext_key", sessionKey)
        .addField("int_key", session.getInternalKey())
        .addField("forwarding", forwardingRequest)
        .addField("orphaned", orphaned)
        .addField("inactivity", inactivityTime);        
        if (ReportType.timeout == type) {
                sessionEvent.addField("browser_starting", String.valueOf(session.getInternalKey() == null));
        }
        
        final Point.Builder sessionCapReq = Point.measurement(format("session.cap.requested.%s.measure", type));
        
        for (Map.Entry<String, Object> rcap : session.getRequestedCapabilities().entrySet()) {
                sessionCapReq.addField("time", time)
                .addField("host", remoteHostName)
                .addField("type", type.toString())
                .addField("ext_key", sessionKey)
                .addField("int_key", session.getInternalKey())
                .addField("forwarding", forwardingRequest)
                .addField("orphaned", orphaned)
                .addField("inactivity", inactivityTime)
                .addField("capability", rcap.getKey())
                .addField("val", rcap.getValue().toString()); //TODO: Check this, used to take an object, now it has to be a string
        
        }

        final Point.Builder sessionCapProv = Point.measurement(format("session.cap.provided.%s.measure", type));
        
        for (Map.Entry<String, Object> scap : session.getSlot().getCapabilities().entrySet()) {
                sessionCapReq.addField("time", time)
                .addField("host", remoteHostName)
                .addField("type", type.toString())
                .addField("ext_key", sessionKey)
                .addField("int_key", session.getInternalKey())
                .addField("forwarding", forwardingRequest)
                .addField("orphaned", orphaned)
                .addField("inactivity", inactivityTime)
                .addField("capability", scap.getKey())
                .addField("val", scap.getValue().toString()); //TODO: Check this, used to take an object, now it has to be a string
        }

        write(sessionEvent.build(), sessionCapReq.build(), sessionCapProv.build());
    }

}
