package helper;


import com.activeviam.copper.api.Publishable;
import com.activeviam.copper.testing.CubeTesterBuilder;
import com.activeviam.desc.build.dimensions.ICanStartBuildingDimensions;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CubeTesterHelper {

    CubeTesterBuilder builder;

    public CubeTesterHelper(ICanStartBuildingDimensions.DimensionsAdder dimensions,
                            List<Publishable<?>> publishables) {

//        builder = new CubeTesterBuilder(
//                new DatastoreSchemaDescription(dimensions, publishables)
//        )
    }

//    @Bean
//    CubeTesterBuilder getBuilder(ITransactionsBuilder initialTransactions) {
//
//        CubeTesterBuilder builder = new CubeTesterBuilder(
//                d
//        );
//
//        IDatastoreSchemaDescription datastoreDescription = new DatastoreSchemaDescription(
//
//        );
//        ISelectionDescription selectionDescription,
//
//                cubeDescription)
//    }


}
