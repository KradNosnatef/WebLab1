package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import assets.FileKit;
import pretreatment.EnumImporter;
import pretreatment.Inverter;

public class Service {
    public Service() {

    }

    public void gotoMainMenu() throws IOException, InterruptedException {
        String[] optionArray = {    "exit", 
                                    "clean room", 
                                    "import", 
                                    "establish or load index to ready for search"};
        manuPrinter("Main Manu", optionArray);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        char option = (char) bufferedReader.read();
        switch (option) {
            case '0': {
                break;
            }
            case '1': {
                gotoCleanRoomMenu();
                break;
            }
            case '2': {
                gotoPretreatmentMenu();
                break;
            }
            case '3':{
                gotoInvertedIndexMenu();
                break;
            }
            default: {
                System.out.println("Invalid input");
                gotoMainMenu();
                break;
            }
        }
    }

    private void manuPrinter(String title, String[] optionArray) {
        System.out.println("----" + title + "----");
        for (int i = 0; i < optionArray.length; i++) {
            System.out.println(" --" + i + ":" + optionArray[i]);
        }
    }

    private void gotoPretreatmentMenu() throws InterruptedException, IOException {
        String[] optionArray = { "return to last menu", "testbud1(small)", "testbud2(all)" };
        manuPrinter("Pretreatment Menu", optionArray);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        char option = (char) bufferedReader.read();
        switch (option) {
            case '0': {
                gotoMainMenu();
                break;
            }
            case '1': {
                EnumImporter enumImporter = new EnumImporter("T:\\maildir\\shackleton-s");
                enumImporter.saveAt("T:\\TEMP-Workspace", 16);
                gotoMainMenu();
                break;
            }
            case '2': {
                EnumImporter enumImporter = new EnumImporter("T:\\maildir");
                enumImporter.saveAt("T:\\TEMP-Workspace", 16);
                gotoMainMenu();
                break;
            }
            default: {
                System.out.println("Invalid input");
                gotoPretreatmentMenu();
                break;
            }
        }
    }

    private void gotoCleanRoomMenu() throws IOException, InterruptedException {
        String[] optionArray = { "return to last menu", "default" };
        manuPrinter("Clean Room Menu", optionArray);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        char option = (char) bufferedReader.read();
        switch (option) {
            case '0': {
                gotoMainMenu();
                break;
            }
            case '1': {

            }
            default: {
                FileKit.cleanRoom("T:\\TEMP-Workspace", 16);
                FileKit.cleanRoom("T:\\IndexSpace", 16);
                gotoMainMenu();
                break;
            }
        }
    }

    private void gotoInvertedIndexMenu() throws IOException, InterruptedException {// 本页面下由于有状态参量所以用循环来维持页面
        Inverter inverter=null;
        for(;;){
            String[] optionArray={  "return to last menu",
                                    "create new index by pretreated file",
                                    "load already-exist index file from",
                                    "save index as file"};
            if(inverter==null)manuPrinter("please create or load index first", optionArray);
            else manuPrinter("index established,now chose your option",optionArray);
            
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            char option = (char) bufferedReader.read();

            switch(option){
                case '0':{
                    inverter=null;
                    gotoMainMenu();
                    return;
                }
                case '1':{
                    inverter=new Inverter("T:\\TEMP-Workspace");
                    inverter.invertAll();
                    break;
                }
                case '2':{
                    inverter=new Inverter("T:\\TEMP-Workspace");
                    inverter.invertedIndexTree.loadFrom("T:\\IndexSpace",12);
                    break;
                }
                case '3':{
                    if(inverter!=null)inverter.invertedIndexTree.saveAt("T:\\IndexSpace",12);
                    else System.out.println("invalid input");
                    break;
                }
                default:{
                    System.out.println("invalid input");
                    break;
                }
            }
        }

    }

}
