package com.example;


import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Schema;

public class MyGenerator {
    private static final String PROJECT_DIR = System.getProperty("user.dir").replace("\\", "/");
    private static final String OUT_DIR = PROJECT_DIR + "/app/src/main/java";

    public static void main(String[] args) throws Exception{

        Schema schema = new Schema(1, "jp.hanatoya.ipcam");
        // Call the addTables method which appends our Objects definition to the schema
        Entity camDb = schema.addEntity("Cam");
        camDb.addIdProperty().primaryKey().autoincrement();
        camDb.addStringProperty("type").notNull();
        camDb.addStringProperty("name").notNull();
        camDb.addStringProperty("host").notNull();
        camDb.addStringProperty("protocol").notNull();
        camDb.addIntProperty("port").notNull();
        camDb.addStringProperty("username");
        camDb.addStringProperty("password");
        camDb.addStringProperty("node");
        camDb.addIntProperty("status");

        Entity switchDb = schema.addEntity("Switch");
        switchDb.addIdProperty().primaryKey().autoincrement();
        switchDb.addStringProperty("name").notNull();
        switchDb.addStringProperty("cgi").notNull();
        switchDb.addIntProperty("port").notNull();
        switchDb.addLongProperty("camId").notNull();

        new DaoGenerator().generateAll(schema, OUT_DIR);



    }
}
