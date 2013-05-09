package com.v2ex.v2droid;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NodeDB {

	MySqlite mySqlite;

	public NodeDB(Context context) {
		mySqlite = new MySqlite(context, "node_db", null, 1);
	}

	public void insert(String node_name, String node_id) {
		mySqlite.insertNodeDB(node_name, node_id);
	}

	public void delete(String node_name) {
		mySqlite.deleteFromNodeDB(node_name);
	}

	public String getID(String node_name) {
		Cursor result = mySqlite.search_byName(node_name);
		result.moveToFirst();
		String value = result.getString(1);
		return value;
	}
	
	public String[] getAllNames() {
		
		Cursor result = mySqlite.search_all();
		String[] nodes = new String[result.getCount()];
		result.moveToFirst();
		int i = 0;
		while (!result.isAfterLast()) { 
	        String name=result.getString(0); 
	        nodes[i] = name;
	        result.moveToNext(); 
	      } 
		result.close(); 
		
		return nodes;
	}

	class MySqlite extends SQLiteOpenHelper {

		/**
		 * 创建的表名
		 */
		public static final String tableName = "node_db";

		/**
		 * 数据库唯一标示的id
		 */
		public static final String NODE_NAME = "node_name";
		public static final String NODE_ID = "node_id";

		private SQLiteDatabase db;

		/**
		 * 数据库构造
		 */
		public MySqlite(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);

			// TODO Auto-generated constructor stub
			db = getWritableDatabase();
			db = getReadableDatabase();
			// db.execSQL("CREATE TABLE IF NOT EXISTS " + imgtableName +
			// "(imageId  INTEGER PRIMARY KEY,bitmapValues BLOB )");

		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			// 删除以前的旧表，创建一张新的空表
			db.execSQL("DROP TABLE IF EXISTS " + tableName);
			onCreate(db);
		}

		/**
		 * 从text表中删除id为id的那条数据
		 */
		public void deleteFromNodeDB(String name) {
			try {
				db.execSQL("delete from " + tableName + " where node_name="
						+ name);
			} catch (SQLException e) {
				Log.d("delete", "已经删除");
			}

		}

		public Cursor search_all() {
			Cursor reCursor = db.rawQuery("select * from " + tableName, null);
			return reCursor;
		}

		public Cursor search_byName(String node_name) {
			Cursor reCursor = null;
			try {
				reCursor = db.rawQuery("select * from " + tableName
						+ " where node_name='" + node_name + "' ", null);
			} catch (SQLException e) {
				Log.e("error", e.getMessage());
			}

			return reCursor;
		}

		/**
		 * 向表node_db中插入一条数据
		 */
		public void insertNodeDB(String node_name, String node_id) {
			try {
				String sqlString = "INSERT INTO " + tableName + " VALUES('"
						+ node_name + "','" + node_id + "')";
				db.execSQL(sqlString);
			} catch (SQLException e) {
				// TODO: handle exception
				Log.e("error", e.getMessage());
			}

		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			Log.e("sqlite", "create table");
			db.execSQL("CREATE TABLE IF NOT EXISTS " + tableName
					+ " (node_name Text,node_id Text)");
			Log.e("sqlite", "create table  succeed");
		}

	}
}