package com.activeviam.apps.trades;

import com.qfs.store.IDatastore;
import com.quartetfs.biz.pivot.IActivePivot;
import com.quartetfs.biz.pivot.IActivePivotManager;
import com.quartetfs.biz.pivot.IActivePivotVersion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Configuration
public class TradesConfiguration {

    @Bean
    public BlockingQueue<IActivePivotVersion> activePivotVersionQueue(){
        return new ArrayBlockingQueue<>(5);
    }

    @Bean
    public TradeInserterService tradeGeneratorController(IActivePivotManager activePivotManager, IDatastore datastore,BlockingQueue<IActivePivotVersion> activePivotVersionQueue){
        return new TradeInserterService(activePivotManager,datastore,activePivotVersionQueue);
    }

    @Bean
    public TradeQueryService tradeQueryService(BlockingQueue<IActivePivotVersion> activePivotVersionQueue){
        return new TradeQueryService(activePivotVersionQueue);
    }
}
