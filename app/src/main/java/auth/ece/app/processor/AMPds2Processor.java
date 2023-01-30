package auth.ece.app.processor;

import auth.ece.app.model.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class AMPds2Processor extends DatasetProcessor {
    private MetricType metricType;

    public AMPds2Processor(int householdId, MetricType metricType) {
        super(householdId);
        setMetricType(metricType);
    }

    private void setMetricType(MetricType metricType) {
        if (!(metricType.equals(MetricType.GAS) || metricType.equals(MetricType.WATER))) {
            throw new RuntimeException("AMPds2Processor only supports GAS ans WATER metrics");
        } else {
            this.metricType = metricType;
        }
    }

    @Override
    public List<Metric> transform(DatasetMetric aMPds2Metric) {
        return aMPds2ToMetrics((AMPds2Metric) aMPds2Metric);
    }

    private List<Metric> aMPds2ToMetrics(AMPds2Metric metric) {
        Instant timestamp = getTimestamp(metric);
        ArrayList<Metric> metrics = new ArrayList<>();
        if (metricType.equals(MetricType.WATER)) {
            metrics.add(getWaterIdd(timestamp, metric));
            metrics.add(getWaterSummation(timestamp, metric));
        } else {
            metrics.add(getGasIdd(timestamp, metric));
            metrics.add(getGasSummation(timestamp, metric));
        }
        return metrics;
    }

    private Instant getTimestamp(AMPds2Metric metric) {
        return Instant.ofEpochSecond(metric.getUnixTimestamp());
    }

    private Metric getWaterIdd(Instant timestamp, AMPds2Metric metric) {
        double coEff = 0.001;
        return Metric.builder()
                .metricType(MetricType.WATER)
                .metricAttribute(MetricAttribute.WATER_IDD)
                .timestamp(timestamp)
                .value(getAdjustedValue(metric.getInstRate().doubleValue(), coEff))
                .build();
    }

    private Metric getWaterSummation(Instant timestamp, AMPds2Metric metric) {
        double coEff = 0.001;
        return Metric.builder()
                .metricType(MetricType.WATER)
                .metricAttribute(MetricAttribute.WATER_SUMMATION)
                .timestamp(timestamp)
                .value(getAdjustedValue(metric.getCounter().doubleValue(), coEff))
                .build();
    }

    private Metric getGasIdd(Instant timestamp, AMPds2Metric metric) {
        double coEff = 0.001;
        return Metric.builder()
                .metricType(MetricType.GAS)
                .metricAttribute(MetricAttribute.GAS_IDD)
                .timestamp(timestamp)
                .value(getAdjustedValue(metric.getInstRate().doubleValue(), coEff))
                .build();
    }

    private Metric getGasSummation(Instant timestamp, AMPds2Metric metric) {
        double coEff = 0.001;
        return Metric.builder()
                .metricType(MetricType.GAS)
                .metricAttribute(MetricAttribute.GAS_SUMMATION)
                .timestamp(timestamp)
                .value(getAdjustedValue(metric.getCounter().doubleValue(), coEff))
                .build();
    }
}
