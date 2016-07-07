/*
 * Copyright 2016 nghiatc.
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

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import java.io.FileInputStream;
import java.util.*;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Apr 12, 2016
 */
public class ParseExcelEmail {
    private static Logger logger = LoggerFactory.getLogger(ParseExcelEmail.class);
    
    private int indexColData = 0;
    private int indexRowData = 0;
    private int numCol = 1;
    private int indexSheet = 0;

    public ParseExcelEmail() {
    }

    public ParseExcelEmail(int indexSheet, int indexColData, int indexRowData, int numCol) {
        this.indexSheet = indexSheet;
        this.indexColData = indexColData;
        this.indexRowData = indexRowData;
        this.numCol = numCol;
    }
    
    public List<String> parseExcel2ListEmail(String filePath){
        List<String> ret = new ArrayList<String>();
        try{
            if(filePath != null && !filePath.isEmpty()){
                HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(filePath));
                if(wb != null && indexSheet < wb.getNumberOfSheets()){
                    //Duyệt sheet.
                    for (int k = 0; k < wb.getNumberOfSheets(); k++) {
                        if(k == indexSheet){
                            HSSFSheet sheet = wb.getSheetAt(k);
                            int rows = sheet.getPhysicalNumberOfRows();
                            System.out.println("Sheet " + k + " \"" + wb.getSheetName(k) + "\" has " + rows + " row(s).");
                            
                            // Duyệt Rows.
                            if(rows >= indexRowData){
                                int countRowNull = 0;
                                for (int r = 0; r < rows; r++) {
                                    HSSFRow row = sheet.getRow(r);
                                    if (row == null || r < indexRowData) {
                                        continue;
                                    }
                                    
                                    String email = "";
                                    
                                    int cells = row.getPhysicalNumberOfCells();
                                    System.out.println("\nROW " + row.getRowNum() + " has " + cells + " cell(s).");
                                    
                                    int countCellNull = 0;
                                    // Duyệt index column.
                                    for (int c = 0; c < cells; c++) {
                                        if(c < indexColData){
                                            continue;
                                        }
                                        if(c >= indexColData + numCol){
                                            break;
                                        }
                                        
                                        HSSFCell cell = row.getCell(c);
                                        String value = null;

                                        switch (cell.getCellType()) {

                                            case HSSFCell.CELL_TYPE_FORMULA:
                                                value = cell.getCellFormula();
                                                break;

                                            case HSSFCell.CELL_TYPE_NUMERIC:
                                                value = "" + cell.getNumericCellValue();
                                                break;

                                            case HSSFCell.CELL_TYPE_STRING:
                                                value = cell.getStringCellValue();
                                                break;

                                            default:
                                        }
                                        System.out.println("CELL col=" + cell.getColumnIndex() + " VALUE="+ value);
                                        
                                        if(value == null){
                                            countCellNull++;
                                        }
                                        
                                        switch(c){
                                            case 0:{
                                                email = value;
                                                break;
                                            }
                                            default:{
                                                
                                            }
                                        }
                                        if(countCellNull >= numCol){
                                            countRowNull++;
                                        }
                                    }
                                    // Nếu găp row null thì dừng.
                                    if(countRowNull >= 1){
                                        break;
                                    }

                                    //validate and add list.
                                    if(email != null && !email.isEmpty()){
                                        ret.add(email);
                                    }
                                }
                            }
                        } else if (k > indexSheet){
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return ret;
    }
    
    
    
}
