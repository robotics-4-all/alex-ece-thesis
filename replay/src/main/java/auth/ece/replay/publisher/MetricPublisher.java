package auth.ece.replay.publisher;

import auth.ece.replay.model.DatasetMetric;
import auth.ece.replay.processor.DatasetProcessor;
import auth.ece.common.model.Metric;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.log4j.Log4j2;

import java.util.Iterator;
import java.util.List;

@Log4j2
public abstract class MetricPublisher {

    private long totalMessages;
    protected RateLimiter rateLimiter;
    protected DatasetProcessor processor;

    public MetricPublisher(long totalMessages, double permitsPerSecond, DatasetProcessor datasetProcessor) {
        this.totalMessages = totalMessages;
        rateLimiter = RateLimiter.create(permitsPerSecond);
        processor = datasetProcessor;
    }

    public void publishMetrics(Iterator<DatasetMetric> metricIterator) {
        long count = 0;
        while (metricIterator.hasNext() && count < totalMessages) {
            DatasetMetric datasetMetric = metricIterator.next();
            List<Metric> metricList = processor.transform(datasetMetric);
            for(Metric metric: metricList) {
                if (count < totalMessages) {
                    publishMetric(metric);
                    count++;
                }
            }
        }
        log.info("Exiting after publishing message count: " + count);
    }

    public void publishMetric(Metric metric) {
        rateLimiter.acquire();
        publish(metric);
    }

    abstract void publish(Metric metric);
}
