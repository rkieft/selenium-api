package com.xing.qa.selenium.grid.node;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;

/**
 * NodeReporter
 *
 * @author Jens Hausherr (jens.hausherr@xing.com)
 */
class NodeReporter extends BaseSeleniumReporter {

    private final MonitoringWebProxy proxy;

    public NodeReporter(String remoteHostName, InfluxDB influxdb, String database,
            MonitoringWebProxy monitoringWebProxy) {
        super(remoteHostName, influxdb, database);
        this.proxy = monitoringWebProxy;
    }

    @Override
    protected void report() {
        log.finer(String.format("Reporting: node.%s.measure", SerieNames.utilization));
        final Point load = Point.measurement(String.format("node.%s.measure", SerieNames.utilization))
                .addField("time", System.currentTimeMillis()).addField("host", remoteHostName)
                .addField("used", proxy.getTotalUsed()).addField("total", proxy.getMaxNumberOfConcurrentTestSessions())
                .addField("normalized", proxy.getResourceUsageInPercent()).build();
        write(load);
    }
}
