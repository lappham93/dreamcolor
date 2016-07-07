/*
 * Copyright 2015 nghiatc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mit.dreamcolor.admin.utils;

import java.io.FileWriter;
import java.util.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Nov 12, 2015
 */
public class CSVUtils {
    private static Logger logger = LoggerFactory.getLogger(CSVUtils.class);
    
    //Delimiter used in CSV file
	public static final String NEW_LINE_SEPARATOR = "\n";
	
	//CSV file header
	//private static final Object [] FILE_HEADER = {"id","firstName","lastName","gender","age"};
    
    public static void writeCsvFile(String filename, Map<Integer, List<String>> mapData) {
		FileWriter fileWriter = null;
		CSVPrinter csvFilePrinter = null;
		
		//Create the CSVFormat object with "\n" as a record delimiter
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
		try {
			//initialize FileWriter object
			fileWriter = new FileWriter(filename);
			//initialize CSVPrinter object 
	        csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
			//Write a row(list) to the CSV file
			for (int id : mapData.keySet()) {
				List<String> row = mapData.get(id);
	            csvFilePrinter.printRecord(row);
			}
			System.out.println(filename + " CSV file was created successfully !!!");
		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
				csvFilePrinter.close();
			} catch (Exception e) {
				System.out.println("Error while flushing/closing fileWriter/csvPrinter !!!");
                e.printStackTrace();
			}
		}
	}
    
	public static void writeCsvFile(String filename, Object [] FILE_HEADER, Map<Integer, List<String>> mapData) {
		FileWriter fileWriter = null;
		CSVPrinter csvFilePrinter = null;
		
		//Create the CSVFormat object with "\n" as a record delimiter
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
		try {
			//initialize FileWriter object
			fileWriter = new FileWriter(filename);
			//initialize CSVPrinter object 
	        csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
	        //Create CSV file header
	        csvFilePrinter.printRecord(FILE_HEADER);
			//Write a row(list) to the CSV file
			for (int id : mapData.keySet()) {
				List<String> row = mapData.get(id);
	            csvFilePrinter.printRecord(row);
			}
			System.out.println("CSV file was created successfully !!!");
		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
				csvFilePrinter.close();
			} catch (Exception e) {
				System.out.println("Error while flushing/closing fileWriter/csvPrinter !!!");
                e.printStackTrace();
			}
		}
	}
    
}
