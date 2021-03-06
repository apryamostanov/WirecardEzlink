package com.wirecard.ezlink.webservices.debitCommand;

//----------------------------------------------------
//
// Generated by www.easywsdl.com
// Version: 4.1.5.2
//
// Created by Quasar Development at 30-03-2015
//
//---------------------------------------------------


import java.util.Hashtable;

import org.ksoap2.serialization.*;

import java.math.BigDecimal;
import java.math.BigInteger;

public class DebitCommandReq extends AttributeContainer implements KvmSerializable
{
    
    public String MERCHANTNO;
    
    public String ORDERNO;
    
    public String MERCHANTREFNO;
    
    public BigDecimal AMOUNT=BigDecimal.ZERO;
    
    public String CURRENCY;
    
    public String TERMINALRANDOMNO;
    
    public String CARDRANDOMNO;
    
    public String CAN;
    
    public String CARDSNO;
    
    public String PURSEDATA;
    
    public BigInteger RETRYCNT=BigInteger.ZERO;

    public DebitCommandReq ()
    {
    }

    public DebitCommandReq(String mERCHANTNO, String oRDERNO,
			String mERCHANTREFNO, BigDecimal aMOUNT, String cURRENCY,
			String tERMINALRANDOMNO, String cARDRANDOMNO, String cAN,
			String cARDSNO, String pURSEDATA, BigInteger rETRYCNT) {
		MERCHANTNO = mERCHANTNO;
		ORDERNO = oRDERNO;
		MERCHANTREFNO = mERCHANTREFNO;
		AMOUNT = aMOUNT;
		CURRENCY = cURRENCY;
		TERMINALRANDOMNO = tERMINALRANDOMNO;
		CARDRANDOMNO = cARDRANDOMNO;
		CAN = cAN;
		CARDSNO = cARDSNO;
		PURSEDATA = pURSEDATA;
		RETRYCNT = rETRYCNT;
	}

	public DebitCommandReq (java.lang.Object paramObj,ExtendedSoapSerializationEnvelope __envelope)
    {
	    
	    if (paramObj == null)
            return;
        AttributeContainer inObj=(AttributeContainer)paramObj;


        SoapObject soapObject=(SoapObject)inObj;  
        if (soapObject.hasProperty("MERCHANTNO"))
        {	
	        java.lang.Object obj = soapObject.getProperty("MERCHANTNO");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class))
            {
                SoapPrimitive j =(SoapPrimitive) obj;
                if(j.toString()!=null)
                {
                    this.MERCHANTNO = j.toString();
                }
	        }
	        else if (obj!= null && obj instanceof String){
                this.MERCHANTNO = (String)obj;
            }    
        }
        if (soapObject.hasProperty("ORDERNO"))
        {	
	        java.lang.Object obj = soapObject.getProperty("ORDERNO");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class))
            {
                SoapPrimitive j =(SoapPrimitive) obj;
                if(j.toString()!=null)
                {
                    this.ORDERNO = j.toString();
                }
	        }
	        else if (obj!= null && obj instanceof String){
                this.ORDERNO = (String)obj;
            }    
        }
        if (soapObject.hasProperty("MERCHANTREFNO"))
        {	
	        java.lang.Object obj = soapObject.getProperty("MERCHANTREFNO");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class))
            {
                SoapPrimitive j =(SoapPrimitive) obj;
                if(j.toString()!=null)
                {
                    this.MERCHANTREFNO = j.toString();
                }
	        }
	        else if (obj!= null && obj instanceof String){
                this.MERCHANTREFNO = (String)obj;
            }    
        }
        if (soapObject.hasProperty("AMOUNT"))
        {	
	        java.lang.Object obj = soapObject.getProperty("AMOUNT");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class))
            {
                SoapPrimitive j =(SoapPrimitive) obj;
                if(j.toString()!=null)
                {
                    this.AMOUNT = new BigDecimal(j.toString());
                }
	        }
	        else if (obj!= null && obj instanceof BigDecimal){
                this.AMOUNT = (BigDecimal)obj;
            }    
        }
        if (soapObject.hasProperty("CURRENCY"))
        {	
	        java.lang.Object obj = soapObject.getProperty("CURRENCY");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class))
            {
                SoapPrimitive j =(SoapPrimitive) obj;
                if(j.toString()!=null)
                {
                    this.CURRENCY = j.toString();
                }
	        }
	        else if (obj!= null && obj instanceof String){
                this.CURRENCY = (String)obj;
            }    
        }
        if (soapObject.hasProperty("TERMINALRANDOMNO"))
        {	
	        java.lang.Object obj = soapObject.getProperty("TERMINALRANDOMNO");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class))
            {
                SoapPrimitive j =(SoapPrimitive) obj;
                if(j.toString()!=null)
                {
                    this.TERMINALRANDOMNO = j.toString();
                }
	        }
	        else if (obj!= null && obj instanceof String){
                this.TERMINALRANDOMNO = (String)obj;
            }    
        }
        if (soapObject.hasProperty("CARDRANDOMNO"))
        {	
	        java.lang.Object obj = soapObject.getProperty("CARDRANDOMNO");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class))
            {
                SoapPrimitive j =(SoapPrimitive) obj;
                if(j.toString()!=null)
                {
                    this.CARDRANDOMNO = j.toString();
                }
	        }
	        else if (obj!= null && obj instanceof String){
                this.CARDRANDOMNO = (String)obj;
            }    
        }
        if (soapObject.hasProperty("CAN"))
        {	
	        java.lang.Object obj = soapObject.getProperty("CAN");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class))
            {
                SoapPrimitive j =(SoapPrimitive) obj;
                if(j.toString()!=null)
                {
                    this.CAN = j.toString();
                }
	        }
	        else if (obj!= null && obj instanceof String){
                this.CAN = (String)obj;
            }    
        }
        if (soapObject.hasProperty("CARDSNO"))
        {	
	        java.lang.Object obj = soapObject.getProperty("CARDSNO");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class))
            {
                SoapPrimitive j =(SoapPrimitive) obj;
                if(j.toString()!=null)
                {
                    this.CARDSNO = j.toString();
                }
	        }
	        else if (obj!= null && obj instanceof String){
                this.CARDSNO = (String)obj;
            }    
        }
        if (soapObject.hasProperty("PURSEDATA"))
        {	
	        java.lang.Object obj = soapObject.getProperty("PURSEDATA");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class))
            {
                SoapPrimitive j =(SoapPrimitive) obj;
                if(j.toString()!=null)
                {
                    this.PURSEDATA = j.toString();
                }
	        }
	        else if (obj!= null && obj instanceof String){
                this.PURSEDATA = (String)obj;
            }    
        }
        if (soapObject.hasProperty("RETRYCNT"))
        {	
	        java.lang.Object obj = soapObject.getProperty("RETRYCNT");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class))
            {
                SoapPrimitive j =(SoapPrimitive) obj;
                if(j.toString()!=null)
                {
                    this.RETRYCNT = new BigInteger(j.toString());
                }
	        }
	        else if (obj!= null && obj instanceof BigInteger){
                this.RETRYCNT = (BigInteger)obj;
            }    
        }


    }

    @Override
    public java.lang.Object getProperty(int propertyIndex) {
        //!!!!! If you have a compilation error here then you are using old version of ksoap2 library. Please upgrade to the latest version.
        //!!!!! You can find a correct version in Lib folder from generated zip file!!!!!
        if(propertyIndex==0)
        {
            return MERCHANTNO;
        }
        if(propertyIndex==1)
        {
            return ORDERNO;
        }
        if(propertyIndex==2)
        {
            return MERCHANTREFNO;
        }
        if(propertyIndex==3)
        {
            return this.AMOUNT!=null?this.AMOUNT.toString():SoapPrimitive.NullSkip;
        }
        if(propertyIndex==4)
        {
            return CURRENCY;
        }
        if(propertyIndex==5)
        {
            return TERMINALRANDOMNO;
        }
        if(propertyIndex==6)
        {
            return CARDRANDOMNO;
        }
        if(propertyIndex==7)
        {
            return CAN;
        }
        if(propertyIndex==8)
        {
            return CARDSNO;
        }
        if(propertyIndex==9)
        {
            return PURSEDATA;
        }
        if(propertyIndex==10)
        {
            return this.RETRYCNT!=null?this.RETRYCNT.toString():SoapPrimitive.NullSkip;
        }
        return null;
    }


    @Override
    public int getPropertyCount() {
        return 11;
    }

    @Override
    public void getPropertyInfo(int propertyIndex, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info)
    {
        if(propertyIndex==0)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "MERCHANTNO";
            info.namespace= "http://ezlinkwebservices.com/service/DebitCommand/request";
        }
        if(propertyIndex==1)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "ORDERNO";
            info.namespace= "http://ezlinkwebservices.com/service/DebitCommand/request";
        }
        if(propertyIndex==2)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "MERCHANTREFNO";
            info.namespace= "http://ezlinkwebservices.com/service/DebitCommand/request";
        }
        if(propertyIndex==3)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "AMOUNT";
            info.namespace= "http://ezlinkwebservices.com/service/DebitCommand/request";
        }
        if(propertyIndex==4)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "CURRENCY";
            info.namespace= "http://ezlinkwebservices.com/service/DebitCommand/request";
        }
        if(propertyIndex==5)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "TERMINALRANDOMNO";
            info.namespace= "http://ezlinkwebservices.com/service/DebitCommand/request";
        }
        if(propertyIndex==6)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "CARDRANDOMNO";
            info.namespace= "http://ezlinkwebservices.com/service/DebitCommand/request";
        }
        if(propertyIndex==7)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "CAN";
            info.namespace= "http://ezlinkwebservices.com/service/DebitCommand/request";
        }
        if(propertyIndex==8)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "CARDSNO";
            info.namespace= "http://ezlinkwebservices.com/service/DebitCommand/request";
        }
        if(propertyIndex==9)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "PURSEDATA";
            info.namespace= "http://ezlinkwebservices.com/service/DebitCommand/request";
        }
        if(propertyIndex==10)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "RETRYCNT";
            info.namespace= "http://ezlinkwebservices.com/service/DebitCommand/request";
        }
    }
    
    @Override
    public void setProperty(int arg0, java.lang.Object arg1)
    {
    }

    @Override
    public String getInnerText() {
        return null;
    }

    @Override
    public void setInnerText(String s) {

    }
}
