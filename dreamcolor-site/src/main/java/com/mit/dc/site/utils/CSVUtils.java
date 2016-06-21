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

package com.mit.dc.site.utils;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
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
    
    //CSV file header
    private static final String [] FILE_HEADER_MAPPING = {"type","cate","mfg","name","desc"};
    
    public static void readFileCSVStream(byte[] bytes) throws UnsupportedEncodingException, IOException{
		Reader reader = new InputStreamReader(new BOMInputStream(new ByteArrayInputStream(bytes)), "UTF-8");
        CSVParser csvFileParser = null;
		
		//Create the CSVFormat object with the header mapping
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(FILE_HEADER_MAPPING);
        try {
            //initialize CSVParser object
            csvFileParser = new CSVParser(reader, csvFileFormat);
            
            //Get a list of CSV file records
            List csvRecords = csvFileParser.getRecords(); 
            
            //Read the CSV file records starting from the second record to skip the header
            for (int i = 1; i < csvRecords.size(); i++) {
            	CSVRecord record = (CSVRecord) csvRecords.get(i);
            	//Create a new product object and fill his data
                String type = record.get("type");
                String cate = record.get("cate");
                String mfg = record.get("mfg");
                String name = record.get("name");
                String desc = record.get("desc");
                if(type != null && !type.isEmpty() && cate != null && !cate.isEmpty() && mfg != null && !mfg.isEmpty() && name != null && !name.isEmpty()){
                    desc = desc != null ? desc : "";
                    
                }
			}
        } catch (Exception e) {
        	System.out.println("Error in CsvFileReader !!!");
            e.printStackTrace();
        } finally {
            try {
                if(reader != null){
                    reader.close();
                }
                if(csvFileParser != null){
                    csvFileParser.close();
                }
            } catch (IOException e) {
            	System.out.println("Error while closing fileReader/csvFileParser !!!");
                e.printStackTrace();
            }
        }
    }
    
}
