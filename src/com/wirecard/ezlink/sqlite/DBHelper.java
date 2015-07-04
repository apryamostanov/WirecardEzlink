package com.wirecard.ezlink.sqlite;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import com.wirecard.ezlink.model.ReceiptRequest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	
	// Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "ReceiptRequestDB";
    private static final String TABLE_RECEIPT_REQUEST = "RECEIPT_REQUEST";
    
    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_MERCHANT_NO = "merchantno";
    private static final String KEY_ORDER_NO = "orderno";
    private static final String KEY_MERCHANT_REF_NO = "merchantrefno";
    private static final String KEY_CAN = "can";
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_RECEIPT_DATA = "receiptdata";
    private static final String KEY_ERROR_CODE = "errorcode";
    private static final String KEY_ERROR_DESC = "errordesc";
    
	private static final String[] COLUMNS = { KEY_ID, KEY_MERCHANT_NO,
			KEY_ORDER_NO, KEY_MERCHANT_REF_NO, KEY_CAN, KEY_AMOUNT,
			KEY_RECEIPT_DATA, KEY_ERROR_CODE, KEY_ERROR_DESC };

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);	
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_RECEIPT_REQUEST_TABLE = "CREATE TABLE RECEIPT_REQUEST ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
				"merchantno TEXT, "+
				"orderno TEXT, "+
				"merchantrefno TEXT, "+
				"can TEXT, "+
				"amount TEXT, "+
				"receiptdata TEXT, "+
				"errorcode TEXT, "+
				"errordesc TEXT )";
		
		db.execSQL(CREATE_RECEIPT_REQUEST_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS RECEIPT_REQUEST");
        this.onCreate(db);
	}

	public void addReceiptRequest(ReceiptRequest receiptRequest){
		Log.d("add receiptRequest", receiptRequest.toString());
		// 1. get reference to writable DB
		SQLiteDatabase db = this.getWritableDatabase();
		 
		// 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_MERCHANT_NO, receiptRequest.getMerchantNo()); // get merchant no 
        values.put(KEY_ORDER_NO, receiptRequest.getOrderNo()); // get order no
        values.put(KEY_MERCHANT_REF_NO, receiptRequest.getMerchantRefNo()); // get merchant ref no
        values.put(KEY_CAN, receiptRequest.getCan()); // get can
        values.put(KEY_AMOUNT, String.valueOf(receiptRequest.getAmount())); // get amount
        values.put(KEY_RECEIPT_DATA, receiptRequest.getReceiptData()); // get receipt data
        values.put(KEY_ERROR_CODE, receiptRequest.getErrorCode()); // get error code data
        values.put(KEY_ERROR_DESC, receiptRequest.getErrorDescript()); // get error desc data
 
        // 3. insert
        db.insert(TABLE_RECEIPT_REQUEST, // table
        		null, //nullColumnHack
        		values); // key/value -> keys = column names/ values = column values
        
        // 4. close
        db.close(); 
	}
	
	public ReceiptRequest getReceiptRequest(int id){

		// 1. get reference to readable DB
		SQLiteDatabase db = this.getReadableDatabase();
		 
		// 2. build query
        Cursor cursor = 
        		db.query(TABLE_RECEIPT_REQUEST, // a. table
        		COLUMNS, // b. column names
        		" id = ?", // c. selections 
                new String[] { String.valueOf(id) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        
        try {
	        // 3. if we got results get the first one
	        if (cursor != null)
	            cursor.moveToFirst();
	 
	        // 4. build ReceiptRequest object
	        ReceiptRequest receiptRequest = new ReceiptRequest();
	        receiptRequest.setId(Integer.parseInt(cursor.getString(0)));
	        receiptRequest.setMerchantNo(cursor.getString(1));
	        receiptRequest.setOrderNo(cursor.getString(2));
	        receiptRequest.setMerchantRefNo(cursor.getString(3));
	        receiptRequest.setCan(cursor.getString(4));
	        receiptRequest.setAmount(BigDecimal.valueOf(Double.parseDouble(cursor.getString(5))));
	        receiptRequest.setReceiptData(cursor.getString(6));
	        receiptRequest.setErrorCode(cursor.getString(7));
	        receiptRequest.setErrorDescript(cursor.getString(8));
	
			Log.d("get receiptRequest("+id+")", receiptRequest.toString());
			
			// 5. return receiptRequest
			return receiptRequest;
        
        } finally {
        	cursor.close();
        }
	}
	
	// Get All Books
    public List<ReceiptRequest> getAllReceiptRequest() {
        List<ReceiptRequest> receiptRequestList = new LinkedList<ReceiptRequest>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_RECEIPT_REQUEST;
 
    	// 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
 
        try {
	        // 3. go over each row, build book and add it to list
	        ReceiptRequest receiptRequest = null;
	        if (cursor.moveToFirst()) {
	            do {
	            	receiptRequest = new ReceiptRequest();
	                receiptRequest.setId(Integer.parseInt(cursor.getString(0)));
	                receiptRequest.setMerchantNo(cursor.getString(1));
	                receiptRequest.setOrderNo(cursor.getString(2));
	                receiptRequest.setMerchantRefNo(cursor.getString(3));
	                receiptRequest.setCan(cursor.getString(4));
	                receiptRequest.setAmount(BigDecimal.valueOf(Double.parseDouble(cursor.getString(5))));
	                receiptRequest.setReceiptData(cursor.getString(6));
	                receiptRequest.setErrorCode(cursor.getString(7));
	                receiptRequest.setErrorDescript(cursor.getString(8));
	                receiptRequestList.add(receiptRequest);
	            } while (cursor.moveToNext());
	        }
	        
			Log.d("getAllReceiptRequest()", receiptRequestList.toString());
	
	        // return books
	        return receiptRequestList;
        } finally {
        	cursor.close();
        }
    }
	
    public int updateReceiptRequest(ReceiptRequest receiptRequest) {

    	// 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
 
		// 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_MERCHANT_NO, receiptRequest.getMerchantNo()); // get merchant no 
        values.put(KEY_ORDER_NO, receiptRequest.getOrderNo()); // get order no
        values.put(KEY_MERCHANT_REF_NO, receiptRequest.getMerchantRefNo()); // get merchant ref no
        values.put(KEY_CAN, String.valueOf(receiptRequest.getAmount())); // get can
        values.put(KEY_AMOUNT, String.valueOf(receiptRequest.getAmount())); // get amount 
        values.put(KEY_RECEIPT_DATA, receiptRequest.getReceiptData()); // get receipt data
        values.put(KEY_ERROR_CODE, receiptRequest.getErrorCode()); // get error code data
        values.put(KEY_ERROR_DESC, receiptRequest.getErrorDescript()); // get error desc data
 
        // 3. updating row
        int i = db.update(TABLE_RECEIPT_REQUEST, //table
        		values, // column/value
        		KEY_ID+" = ?", // selections
                new String[] { String.valueOf(receiptRequest.getId()) }); //selection args
        
        // 4. close
        db.close();
        
        return i;
        
    }

    public void deleteReceiptRequest(ReceiptRequest receiptRequest) {

    	// 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        
        // 2. delete
        db.delete(TABLE_RECEIPT_REQUEST,
        		KEY_ID+" = ?",
                new String[] { String.valueOf(receiptRequest.getId()) });
        
        // 3. close
        db.close();
        
		Log.d("delete ReceiptRequest", receiptRequest.toString());

    }
}
