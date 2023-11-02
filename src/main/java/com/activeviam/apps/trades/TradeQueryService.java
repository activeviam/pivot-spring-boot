package com.activeviam.apps.trades;

import com.quartetfs.biz.pivot.IActivePivotVersion;
import com.quartetfs.biz.pivot.cube.hierarchy.ILevel;
import com.quartetfs.biz.pivot.impl.Location;
import com.quartetfs.biz.pivot.query.IGetAggregatesQuery;
import com.quartetfs.biz.pivot.query.impl.GetAggregatesQuery;
import com.quartetfs.fwk.query.QueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static com.activeviam.apps.cfg.pivot.Measures.NOTIONAL_SUM;

@ManagedResource
public class TradeQueryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradeQueryService.class.getName());
    private final BlockingQueue<IActivePivotVersion> queue;

    public TradeQueryService(BlockingQueue<IActivePivotVersion> queue) {
        this.queue = queue;
    }

    @ManagedOperation
    public void printQueue(){
        LOGGER.info("Printing queue");
        var copyQueue = new LinkedList<>(queue);
        copyQueue.forEach(version->{
            LOGGER.info("epochId:{} ", version.getEpochId());
        });
    }


    @ManagedOperation
    public void queryPendingVersions(){
        printQueue();
        final var copy= new ArrayList<IActivePivotVersion>(queue.size());
        queue.drainTo(copy);
        var query=createQuery();
        copy.forEach(version->{
            try {
                LOGGER.info("executing for epochId:{} ", version.getEpochId());
                final var cellSet = version.execute(query);
                cellSet.forEachCell((location, measureName, value) ->{
                    LOGGER.info("Nominal:{} for location:{}",value,location);
                    return true;
                });
            } catch (QueryException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private IGetAggregatesQuery createQuery(){
        final var arrayLocation =
                new Object[][] {{ILevel.ALLMEMBER, null}};
        return new GetAggregatesQuery("Cube",List.of(new Location(arrayLocation)),List.of(NOTIONAL_SUM), List.of());
    }
}
