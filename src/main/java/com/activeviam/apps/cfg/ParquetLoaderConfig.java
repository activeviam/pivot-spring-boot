package com.activeviam.apps.cfg;

import com.activeviam.apps.parquet.ParquetLoaderService;
import com.qfs.store.IDatastore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParquetLoaderConfig {
	@Autowired
	protected IDatastore datastore;

	@Bean
	public ParquetLoaderService parquetService() {
		return new ParquetLoaderService(datastore);
	}
}
