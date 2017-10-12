package com.example;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Objects;

/**
 * Created by ysq on 2017/10/9.
 */

public class XlsxToJson {
    public static void main(String[] args) {
        FileInputStream inputStream = null;
        FileWriter writer = null;
        try {
            inputStream = new FileInputStream("/Users/ysq/Documents/faq.xls");
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            writer = new FileWriter("/Users/ysq/Documents/jp.json");
            JSONObject jsonData = transferXlsxToJson(workbook);
            jsonData.write(writer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static JSONObject transferXlsxToJson(XSSFWorkbook workbook) throws Exception {
        //获取sheet数目，判断excel是否可用
        int sheetCount = workbook.getNumberOfSheets();
        if (sheetCount < 1) {
            throw new Exception("Not any available sheet");
        }
        //获取第一个sheet对象
        XSSFSheet sheet = workbook.getSheetAt(0);

        Boolean isTable = false;
        Boolean isTableBody = false;

        //得到总共的行数
        int lastRowNum = sheet.getLastRowNum();
        JSONArray dataArr = new JSONArray();

        String question = "";
        //拼接一个大问题（总共是三个）
        JSONArray questionsObj = new JSONArray();

        JSONArray answersArr = new JSONArray();
        JSONObject jsonObj = new JSONObject();
        JSONArray tableBody = new JSONArray();

        String title = sheet.getRow(1).getCell(0).getStringCellValue();

        for (int rowIndex = 2; rowIndex <= lastRowNum; rowIndex++) {
            XSSFRow xRow = sheet.getRow(rowIndex);
            if (xRow == null) {
                continue;
            }

            int column = xRow.getLastCellNum();
            if (column > 3 && !isTable && !isTableBody) {
                isTable = true;
                jsonObj.accumulate("type", "table");
            } else if (column > 3) {
                jsonObj.accumulate("body", tableBody);
                tableBody = new JSONArray();
                isTableBody = true;
            } else if (isTable) {
                jsonObj.accumulate("body", tableBody);
                tableBody = new JSONArray();
                answersArr.put(jsonObj);
                jsonObj = new JSONObject();
                isTable = false;
                isTableBody = false;
            }

            for (int i = 1; i < column; i++) {
                XSSFCell cell = xRow.getCell(i);
                if (cell == null) {
                    continue;
                }

                //说明是表格
                if (column > 3) {
                    if (isTable && !isTableBody) {
                        jsonObj.accumulate("header", cell.getStringCellValue());
                    } else
                        tableBody.put(cell.getStringCellValue());
                } else {
                    //判断值的类型是不是数字
                    if (cell.getCellType() == 0) {
                        jsonObj.accumulate("level", cell.getNumericCellValue());
                        //判断值的类型是不是字符串
                    } else if (cell.getCellType() == 1) {
                        String str = cell.getStringCellValue();
                        //_表示是图片
                        if (str.contains("_"))
                            jsonObj.accumulate("id", str).accumulate("type", "image");
                        else
                            jsonObj.accumulate("content", str).accumulate("type", "string");
                    }
                }
            }
            String s = "";
            if (xRow.getCell(0) != null)
                s = xRow.getCell(0).getStringCellValue();
            if (!Objects.equals(s, "") || rowIndex == lastRowNum) {
                if (!Objects.equals(question, "")) {
                    if (xRow.getLastCellNum() != 1) {
                        if (rowIndex == lastRowNum) {
                            answersArr.put(jsonObj);
                        }
                        JSONObject questionJson = new JSONObject().accumulate("question", question).accumulate("answers", answersArr);
                        questionsObj.put(questionJson);
                    }
                    answersArr = new JSONArray();
                }
                question = s;
            }

            if (!isTable && xRow.getLastCellNum() > 1) {
                answersArr.put(jsonObj);
                jsonObj = new JSONObject();
            }

            //如果一行只有一个，那就是标题行
            if (xRow.getLastCellNum() == 1 || rowIndex == lastRowNum) {
                JSONObject json = new JSONObject().accumulate("title", title).accumulate("questions", questionsObj);
                title = xRow.getCell(0).getStringCellValue();
                question = "";
                questionsObj = new JSONArray();
                dataArr.put(json);
            }
        }

        JSONObject Table = new JSONObject().accumulate("title", "FAQ").accumulate("sections", dataArr);

        return Table;
    }

}

