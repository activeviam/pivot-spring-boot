/*
 * (C) ActiveViam 2023
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of ActiveViam. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */

package com.activeviam.apps;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import com.activeviam.fwk.ActiveViamRuntimeException;

public class DataCreator {

    // TODO: update this path
    private static final String DATA_FOLDER =  "/home/fabien/workspace/pivot-spring-boot/src/main/resources/data/";
    private static final String TRADES_FOLDER = DATA_FOLDER + "trades/";
    private static final String DESKS_FOLDER = DATA_FOLDER + "desks/";

    private static final String LINE_RETURN = "\n";

    private static final String TRADES_HEADER = "AsOf,TradeId,Desk,Notional" + LINE_RETURN;
    private static final int NUMBER_OF_DATES = 5;
    private static final int NUMBER_OF_TRADES_PER_DAY = 100;
    private static final int NUMBER_OF_DESKS = 2;
    private static final Random RANDOM = new Random(2023_11_09L);

    private static final List<String> DESKS = IntStream.range(0, NUMBER_OF_DESKS).mapToObj(i -> "Desk_" + i).toList();
    private static final List<String> COUNTRIES = List.of("France", "USA", "UK", "Singapore");

    private static final List<String> DATES = generateDates(NUMBER_OF_DATES);

    public static void main(final String[] args) {
        createTradesFiles();
        createDeskFile();
    }

    private static void createTradesFiles() {
        for (final String date : DATES) {
            createTradesFile(date);
            System.out.println("Generated trades for date " + date);
        }
    }

    private static void createDeskFile() {
        final String path = DESKS_FOLDER + "desk.csv";
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (int id =0; id<NUMBER_OF_DESKS; id++) {
                final String desk = DESKS.get(id);
                final String country = COUNTRIES.get(id % COUNTRIES.size());
                final String line = desk + "," + country + LINE_RETURN;
                writer.write(line);
            }
        } catch (IOException e) {
            throw new ActiveViamRuntimeException(e);
        }
        System.out.println("Generated desks");
    }

    private static void createTradesFile(final String date) {
        final String path = TRADES_FOLDER + "trades_" + date + ".csv";
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(TRADES_HEADER);
            for (int id =0; id<NUMBER_OF_TRADES_PER_DAY; id++) {
                final String desk = DESKS.get(id % DESKS.size());
                final double notional = Math.round(RANDOM.nextDouble(0, 100000)) / 100.0;
                final String line = date + "," + id + "," + desk  + "," + notional + LINE_RETURN;
                writer.write(line);
            }
        } catch (IOException e) {
            throw new ActiveViamRuntimeException(e);
        }
    }

    private static List<String> generateDates(final int numberOfDates) {
        LocalDate currentDate = LocalDate.now();
        final List<String> dates = new ArrayList<>(numberOfDates);
        for (int i =0; i<numberOfDates; i++) {
            dates.add(currentDate.toString());
            currentDate = currentDate.minusDays(1);
        }
        return dates;
     }

}
