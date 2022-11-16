/*
 * (C) ActiveViam 2022
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package com.activeviam.apps.activepivot.pivot.tickers;

import com.activeviam.apps.activepivot.configurers.IMeasuresConfigurer;
import com.activeviam.apps.activepivot.configurers.InCubes;
import com.activeviam.copper.api.Copper;
import org.springframework.context.annotation.Bean;

import static com.activeviam.apps.activepivot.pivot.CubeConstants.TICKERS_CUBE_NAME;

/**
 * @author ActiveViam
 */
public class InLineMeasuresConfig {


	@Bean("tickerMeasures")
	@InCubes(TICKERS_CUBE_NAME)
	IMeasuresConfigurer tickerMeasures(){
		return context -> Copper.count().publish(context);
	}

}
