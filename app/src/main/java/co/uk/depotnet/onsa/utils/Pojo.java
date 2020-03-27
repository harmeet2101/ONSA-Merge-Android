package co.uk.depotnet.onsa.utils;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.DatabaseMetaData;
import java.text.ParseException;
import java.util.Iterator;

public class Pojo{

	DatabaseMetaData databaseMetaData;

	private static StringBuilder classBuilder = new StringBuilder();
	private static StringBuilder fieldBuilder = new StringBuilder();
	private static StringBuilder getterSetterBuilder = new StringBuilder();
	private static String className;



	private static StringBuilder DbTableBuilder = new StringBuilder();
	private static StringBuilder ContentValuesBuilder = new StringBuilder();
	private static StringBuilder fromCursurBuilder = new StringBuilder();
	private static StringBuilder ParcelBuilder = new StringBuilder();
	private static StringBuilder ParcelCreater = new StringBuilder();
	private static StringBuilder ParcelCunstructor = new StringBuilder();
	private static StringBuilder DefaultCunstructor = new StringBuilder();
	private static StringBuilder writeParcel = new StringBuilder();
	private static StringBuilder readParcel = new StringBuilder();

	public  void initBuilders(){

	}


	public static void main(String[] args){
		className = args[0];

		String jsonPath = "file.json";
		JSONObject json = getJsonFromFile(jsonPath);


//		JSONObject json = new JSONObject();
//		try {
//			json.put("stringValue" , "StringType");
//			json.put("IntegerValue" , 1);
//			json.put("BooleanValue" , true);
//			json.put("DoubleValue" , 1.23);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}

		try {
			parseJson(json);
			createFile();
		} catch (ParseException e) {
			e.printStackTrace();
		}

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

	public static void parseJson(JSONObject jsonObject) throws ParseException {
		buildParcelCunstructor("User");
		writeToParcelinit();
		buildCreate();
		buildDataTable();
		buildDataCunstructor(className);
		initContentValueBuilder();

		Iterator<String> iterator = jsonObject.keys();
		while (iterator.hasNext()) {
			String getPrefix = "get";
			String setPrefix = "set";
			String type = "Object";
			String methodType = "Object";
			String obj = iterator.next();
			if (jsonObject.opt(obj) instanceof Integer) {
				System.out.println("navin "+obj.toString()+" type int");
				type = "int";
				methodType = "Int";

			}else if (jsonObject.opt(obj) instanceof Double) {
				System.out.println("navin "+obj.toString()+" type double");
				type = "double";
				methodType = "Double";
			}else if (jsonObject.opt(obj) instanceof String) {
				type = "String";
				methodType = "String";
				System.out.println("navin "+obj.toString()+" type String");
			}else if (jsonObject.opt(obj) instanceof Boolean) {
				type = "boolean";
				methodType = "Boolean";
				getPrefix = "is";
				System.out.println("navin "+obj.toString()+" type Boolean");
			}

			fieldBuilder.append("\tprivate "+type+" "+obj+";\n");
			buildParcelCunstructorLines(methodType , obj);
			writeToParcelLines(methodType , obj);
			buildGetterSetter(type , obj);
			buildDataTableFields(type , obj);
			buildDataCunstructorLines(methodType , obj);
			buildContentValueBuilder(type , obj);
		}
	}

	private static void buildGetterSetter(String type , String key){
		String getPrefix = "get";
		if(type == "boolean"){
			getPrefix = "is";
		}
		getterSetterBuilder.append("\tpublic void set"+key+"("+ type+" "+key+"){\n");
		getterSetterBuilder.append("\t\tthis."+key+" = "+key+";\n\t}\n");

		getterSetterBuilder.append("\tpublic "+type+" "+getPrefix+key+"(){\n");
		getterSetterBuilder.append("\t\treturn this."+key+";\n\t}\n");
	}

	private static void buildCreate(){
		ParcelCreater.append("\tpublic static final Creator<"+className+"> CREATOR = new Creator<"+className+">() {\n");
		ParcelCreater.append("\t\t@Override\n");
		ParcelCreater.append("\t\tpublic "+className+" createFromParcel(Parcel in) {\n");
		ParcelCreater.append("\t\t\treturn new "+className+"(in); \n");
		ParcelCreater.append("\t\t}\n\n");
		ParcelCreater.append("\t\t@Override\n");
		ParcelCreater.append("\tpublic "+className+"[] newArray(int size) {\n");
		ParcelCreater.append("\t\t\treturn new "+className+"[size]; \n\n");
		ParcelCreater.append("\t\t}\n\n");
		ParcelCreater.append("\t};\n\n");

		ParcelCreater.append("\t@Override\n");
		ParcelCreater.append("\tpublic int describeContents() {\n");
		ParcelCreater.append("\t\treturn 0;\n");
		ParcelCreater.append("\t}\n");




	}

	private static void writeToParcelinit(){
		writeParcel.append("\t@Override\n");
		writeParcel.append("\tpublic void writeToParcel(Parcel parcel, int i) {\n");

	}

	private static void writeToParcelLines(String type , String key){
		if(type == "Boolean"){
			type = "Byte";
			writeParcel.append("\tparcel.write"+type+"((byte) ("+key+" ? 1 : 0));\n");
			return;
		}
		writeParcel.append("\tparcel.write"+type+"("+key+");\n");


	}


	private static void buildParcelCunstructor(String name){
		ParcelCunstructor.append("\tprotected "+name+"(Parcel in) {\n");
	}

	private static void buildParcelCunstructorLines(String type , String key){
		if(type.equalsIgnoreCase("Boolean")){
			type = "Byte";
			ParcelCunstructor.append("\t\t"+key+" = in.read"+type+"() != 0;\n");
			return;
		}

		ParcelCunstructor.append("\t\t"+key+" = in.read"+type+"();\n");

	}

	private static void initContentValueBuilder(){
		ContentValuesBuilder.append("\n\tpublic ContentValues toContentValues(){\n");
		ContentValuesBuilder.append("\n\t\tContentValues cv = new ContentValues();\n");
	}

	private static void buildContentValueBuilder(String type , String key){

		ContentValuesBuilder.append("\n\t\tcv.put(DBTable."+key+" , this."+key+");\n");
	}


	private static void buildDataTable(){
		DbTableBuilder.append("\tpublic static class DBTable{\n");
		DbTableBuilder.append("\t\tpublic static final String NAME = \""+className+"\";\n");

	}

	private static void buildDataTableFields(String type , String key){

		DbTableBuilder.append("\t\tpublic static final String "+key+" = \""+key+"\";\n");

	}

	private static void buildDataCunstructor(String name){
		fromCursurBuilder.append("\n\tpublic "+name+"(Cursor cursor) {\n");
	}

	private static void buildDataCunstructorLines(String type , String key){
		if(type == "Boolean"){
			type = "Int";
			fromCursurBuilder.append("\t\t"+key+" = cursor.get"+type+"(cursor.getColumnIndex(DBTable."+key+"))!=0;\n");
			return;
		}

		fromCursurBuilder.append("\t\t"+key+" = cursor.get"+type+"(cursor.getColumnIndex(DBTable."+key+"));\n");

	}



	private static void createFile(){
		classBuilder = new StringBuilder();
		classBuilder.append("//TODO: package name");
		classBuilder.append("\n");
		classBuilder.append("import android.content.ContentValues;");
		classBuilder.append("\n");
		classBuilder.append("\n");
		classBuilder.append("import android.database.Cursor;");
		classBuilder.append("\n");
		classBuilder.append("import android.os.Parcel;");
		classBuilder.append("\n");
		classBuilder.append("import android.os.Parcelable;");
		classBuilder.append("\n");
		classBuilder.append("\n");

		classBuilder.append("public class "+className+" implements Parcelable");
		classBuilder.append("{");
		classBuilder.append("\n");

		classBuilder.append(fieldBuilder.toString());
		classBuilder.append("\n");

		classBuilder.append(getterSetterBuilder.toString());
		classBuilder.append("\n");


		classBuilder.append(ParcelCunstructor.toString());
		classBuilder.append("\n");
		classBuilder.append("\t}");

		classBuilder.append(ParcelCreater.toString());
		classBuilder.append("\n");


		classBuilder.append(DbTableBuilder.toString());
		classBuilder.append("\n");
		classBuilder.append("\t}");

		classBuilder.append(writeParcel.toString());
		classBuilder.append("\n");
		classBuilder.append("\t}");

		classBuilder.append(fromCursurBuilder.toString());
		classBuilder.append("\n");
		classBuilder.append("\t}");

		classBuilder.append(ContentValuesBuilder.toString());
		classBuilder.append("\n");
		classBuilder.append("\t\t return cv;");
		classBuilder.append("\t}");

		classBuilder.append("\n");
		classBuilder.append("}");

		String data = classBuilder.toString();

		OutputStream os = null;
		try {
			os = new FileOutputStream(new File(className+".java"));
			os.write(data.getBytes(), 0, data.length());
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(os != null) {
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


}
