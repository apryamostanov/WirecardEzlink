package com.wirecard.ezlink.model;

public class Scanner {
	private int drawableId;
	private String function;
	private String functionDetail;
	public Scanner(int drawableId, String function, String functionDetail) {
		this.drawableId = drawableId;
		this.function = function;
		this.functionDetail = functionDetail;
	}
	public int getDrawableId() {
		return drawableId;
	}
	public void setDrawableId(int drawableId) {
		this.drawableId = drawableId;
	}
	public String getFunction() {
		return function;
	}
	public void setFunction(String function) {
		this.function = function;
	}
	public String getFunctionDetail() {
		return functionDetail;
	}
	public void setFunctionDetail(String functionDetail) {
		this.functionDetail = functionDetail;
	}
	
}
