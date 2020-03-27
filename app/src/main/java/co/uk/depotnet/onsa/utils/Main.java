package co.uk.depotnet.onsa.utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Iterator;

/**
 * @author sqlitetutorial.net
 */
public class Main {

    private static StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS ");

    public static void createNewDatabase(String fileName) {

        String url = "jdbc:sqlite:/home/suntec/options.sqlite";

        try {
            Connection conn = DriverManager.getConnection(url);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void connect(String url) {
        Connection conn = null;
        try {

            // create a connection to the database
            conn = DriverManager.getConnection(url);

            System.out.println("Connection to SQLite has been established.");
            Statement stmt = conn.createStatement();
            stmt.execute(sql.toString());

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public static void createNewTable(String url, String tableName) {


        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (\n"
                + "	id integer PRIMARY KEY AUTOINCREMENT,\n"
                + "	name text NOT NULL,\n"
                + "	capacity real\n"
                + ");";

        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String url = "jdbc:sqlite:/home/suntec/options.sqlite";
        String tableName = args[0];

        String jsonPath = "file.json";
        JSONObject json = getJsonFromFile(jsonPath);

        sql.append(tableName+" (\n");
        sql.append("id integer PRIMARY KEY AUTOINCREMENT,\n");
        parseJson(json);
        connect(url);
    }

    private static JSONObject getJsonFromFile(String path) {
        JSONObject jsonObject = null;
        File file = new File(path);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String st;
            while ((st = br.readLine()) != null)
                sb.append(st);
            jsonObject = new JSONObject(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static void parseJson(JSONObject jsonObject) {

        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String type = "text";

            String obj = iterator.next();
            if (jsonObject.opt(obj) instanceof Integer) {
                System.out.println("navin "+obj.toString()+" type int");
                type = "integer";
            }else if (jsonObject.opt(obj) instanceof Double) {
                System.out.println("navin "+obj.toString()+" type double");
                type = "real";
            }else if (jsonObject.opt(obj) instanceof String) {
                type = "text";
                System.out.println("navin "+obj.toString()+" type String");
            }else if (jsonObject.opt(obj) instanceof Boolean) {
                type = "int";
                System.out.println("navin "+obj.toString()+" type Boolean");
            }
            sql.append(obj+" "+type+" ,\n");
        }
        sql.setCharAt(sql.lastIndexOf(",") , ' ');
        sql.append(");");

        System.out.println(sql.toString());

    }
}