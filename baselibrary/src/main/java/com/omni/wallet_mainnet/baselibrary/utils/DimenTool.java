package com.omni.wallet_mainnet.baselibrary.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class DimenTool {


    public static void gen() {

//        File file = new File("./app/src/main/res/values/dimens.xml");//这里如果找不到使用绝对路径即可
////        BufferedReader reader = null;
        //添加分辨率
//        StringBuilder sw480 = new StringBuilder();
//        StringBuilder sw600 = new StringBuilder();
//        StringBuilder sw720 = new StringBuilder();
//        StringBuilder sw800 = new StringBuilder();
//        StringBuilder w820 = new StringBuilder();
        StringBuilder baseBuilder = new StringBuilder();
        String baseOutFile = "./baselibrary/src/main/temp.txt";

//        try {
//            System.out.println("生成不同分辨率：");
//            reader = new BufferedReader(new FileReader(file));
//            String tempString;
//        int line = 1;
        // 一次读入一行，直到读入null为文件结束

//            while ((tempString = reader.readLine()) != null) {
        float index = 0.5f;
        while (index <= 500) {
            index = index + 0.5f;
            baseBuilder.append("<dimen name=\"main_" + index + "\">" + index + "dp</dimen>").append("\n");
        }
        try {
            FileUtils.writeStrToFile(baseBuilder.toString(), baseOutFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
//            if (tempString.contains("</dimen>")) {
//                //tempString = tempString.replaceAll(" ", "");
//                String start = tempString.substring(0, tempString.indexOf(">") + 1);
//                String end = tempString.substring(tempString.lastIndexOf("<") - 2);
//                int num = Integer.valueOf(tempString.substring(tempString.indexOf(">") + 1, tempString.indexOf("</dimen>") - 2));
//
//                //主要核心就再这里了，如下3种分辨率分别缩小 0.6、0.75、0.9倍(根据实际情况自己随意DIY)
//                sw480.append(start).append((int) Math.round(num * 0.6)).append(end).append("\n");
//                sw600.append(start).append((int) Math.round(num * 0.75)).append(end).append("\n");
//                sw720.append(start).append((int) Math.round(num * 0.9)).append(end).append("\n");
//                sw800.append(tempString).append("\n");
//                w820.append(tempString).append("\n");
//
//            } else {
//                sw480.append(tempString).append("\n");
//                sw600.append(tempString).append("\n");
//                sw720.append(tempString).append("\n");
//                sw800.append(tempString).append("\n");
//                w820.append(tempString).append("\n");
//            }
//            line++;
////            }
//            reader.close();
//            System.out.println("<!--  sw480 -->");
//            System.out.println(sw480);
//            System.out.println("<!--  sw600 -->");
//            System.out.println(sw600);
//
//            System.out.println("<!--  sw720 -->");
//            System.out.println(sw720);
//            System.out.println("<!--  sw800 -->");
//            System.out.println(sw800);

//            String sw480file = "./app/src/main/res/values-sw480dp-land/dimens.xml";
//            String sw600file = "./app/src/main/res/values-sw600dp-land/dimens.xml";
//            String sw720file = "./app/src/main/res/values-sw720dp-land/dimens.xml";
//            String sw800file = "./app/src/main/res/values-sw800dp-land/dimens.xml";
//            String w820file = "./app/src/main/res/values-w820dp/dimens.xml";
//            writeFile(sw480file, sw480.toString());
//            writeFile(sw600file, sw600.toString());
//            writeFile(sw720file, sw720.toString());
//            writeFile(sw800file, sw800.toString());
//            writeFile(w820file, w820.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
//            }
//        }
    }

    public static void writeFile(String file, String text) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            out.println(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.close();
    }

    public static void main(String[] args) {
        gen();
    }
}
