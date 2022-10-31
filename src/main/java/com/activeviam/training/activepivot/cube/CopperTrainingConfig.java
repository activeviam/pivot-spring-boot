package com.activeviam.training.activepivot.cube;

import com.activeviam.copper.api.Copper;
import com.activeviam.copper.api.CopperMeasure;
import com.activeviam.copper.api.MultiLevelHierarchyBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CopperTrainingConfig {

    @Bean
    public CopperMeasure count() {
        return Copper.count();
    }

    // ================================
    // TODO Task 1: Simple aggregations
    // ================================

    /**
     * 1.1 Create a SUM aggregation on field X
     *
     * @return the measure definition
     */
    @Bean
    public CopperMeasure sumOfX() {
        return null;
    }

    /**
     * 1.2 Create a MIN aggregation on field Y
     *
     * @return the measure definition
     */
    @Bean
    public CopperMeasure minOfY() {
        return null;
    }

    /**
     * 1.3 Create an AVG aggregation on field Z
     *
     * @return the measure definition
     */
    @Bean
    public CopperMeasure avgOfZ() {
        return null;
    }


    // ================================
    // TODO Task 2: Complex formulas
    // ================================

    /**
     * 2.1 Calculate the product of measure 1.1 and measure 1.2
     * HINT: You need to input both measures into the method!
     *
     * @return the measure definition
     */
    @Bean
    public CopperMeasure productOf2Measures(CopperMeasure sumOfX, CopperMeasure minOfY) {
        return null;
    }

    /**
     * 2.2 Scale measure 1.3 by 10
     *
     * @return the measure definition
     */
    @Bean
    public CopperMeasure scaleMeasureBy10(CopperMeasure avgOfZ) {
        return null;
    }

    /**
     * 2.3 Calculate the square of measure 1.1
     *
     * @return the measure definition
     */
    @Bean
    public CopperMeasure squareOfMeasure(CopperMeasure sumOfX) {
        return null;
    }

    /**
     * 2.5 Calculate the following formula ((Measure 1.1) / 2) * (Measure 2.3)
     *
     * @return the measure definition
     */
    @Bean
    public CopperMeasure formulaMeasure(CopperMeasure sumOfX, CopperMeasure squareOfMeasure) {
        return null;
    }


    // ================================
    // TODO Task 3: Dynamic aggregations
    // ================================


    /**
     * 3.1 On level X, do a sum of Measure 1.1 and Measure 1.2
     * Above that level, take the average
     * @return the measure definition
     */
    @Bean
    public CopperMeasure dynAgg(CopperMeasure sumOfX, CopperMeasure minOfY) {
        return null;
    }

    /**
     * 3.2 Take the sum of field Y per currency level
     * Above that level, do not aggregate
     *
     * @return the measure definition
     */
    @Bean
    public CopperMeasure hideAbove() {
        return null;
    }

    // ================================
    // TODO Task 4: Lookups
    // ================================

    /**
     * 4.1 Get a value by its key
     *
     * @return the measure definition
     */
    @Bean
    public CopperMeasure getByKey() {
        return null;
    }

    /**
     * 4.2 Get a value by a condition on a set of fields
     *
     * @return the measure definition
     */
    @Bean
    public CopperMeasure conditionQuery(CopperMeasure hideAbove) {
        return null;
    }

    // ================================
    // TODO Task 5: Mapping functions
    // ================================

    /**
     * 5.1 Take level X and measure 1.1, if X == (value1) then return measure * 2, otherwise
     *
     * @return the measure definition
     */
    @Bean
    public CopperMeasure mappingFunction(CopperMeasure sumOfX) {
        return null;
    }

    // ================================
    // TODO Task 6: Copper Joins
    // ================================

    /**
     * 6.1 Join store X to the cube, and create a SUM aggregation on field A
     *
     * @return the measure definition
     */
    @Bean
    public CopperMeasure joinMeasure() {
        return null;
    }

    /**
     * 6.2 Join store X to the cube, and create a hierarchy on field B
     *
     * @return the hierarchy definition
     */
    @Bean
    // public MultiLevelHierarchyBuilder.SingleLevelBuilder joinHierarchy() {
    public MultiLevelHierarchyBuilder.LevelFromStoreBuilder joinHierarchy() {
        return null;
    }


    // ================================
    // TODO Task 7: Analysis hierarchies
    // ================================

    /**
     * 7.2 Take level X from the cube, and create an analysis hierarchy
     *
     * @return the hierarchy definition
     */
    @Bean
    public MultiLevelHierarchyBuilder.SingleLevelBuilder analysisHierarchy() {
        return null;
    }


}
