package com.softeem.jdbc.dbprovider.exceptions;

public class IllegalParameterException extends RuntimeException{
	@Override
	public String toString() {
		return this.getClass().getName()+":非法的参数值";
	}
}
