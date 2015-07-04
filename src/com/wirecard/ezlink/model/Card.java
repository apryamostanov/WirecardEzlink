package com.wirecard.ezlink.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

public class Card {
	
	public String getPurseBal(String result) {
		String purseBal = result.substring(4, 10);
		int balanceStr = Integer.parseInt(purseBal, 16);
		BigDecimal balance = BigDecimal.valueOf((long) balanceStr, 2);
		return String.valueOf(balance);
	}

	public String getCardNo(String result) {
		String cardNo = result.substring(16, 32);
		return cardNo;
	}

	public String getCardSN(String result) {
		String cardSN = result.substring(32, 48);
		return cardSN;
	}
	
	public String getPurseCreationDate(String result) {
		String purseExpiryDate = getDate(result.substring(52, 56));
		return purseExpiryDate;
	}
	
	public String getPurseExpiryDate(String result) {
		String purseExpiryDate = getDate(result.substring(48, 52));
		return purseExpiryDate;
	}
	
	public String getDate(String s)
    {
        long l = 1000L * (0x15180L * (9131L + Long.parseLong(s, 16)));
        Date date = new Date();
        date.setTime(l);
        return (new SimpleDateFormat("dd/MM/yyyy")).format(date);
    }
	
	public boolean checkExpiryDate(String creationDate, String expiryDate) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
		boolean cardExpired = true;
		try {
			Date creation = dateFormat.parse(creationDate);
			Date expiry = dateFormat.parse(expiryDate);
			Date now = dateFormat.parse(dateFormat.format(new Date()));
			if((now.compareTo(creation) >= 0) && (now.compareTo(expiry) <= 0)) {
				cardExpired = false;
			}
		} catch (ParseException e) {
		}
		return cardExpired;
	}
	
	public boolean checkCardBin(String cardNo) {
		boolean result = false;
		int card = Integer.parseInt(cardNo.substring(0, 4));
		if(card >= 1000 && card <= 1010) {
			result = true;
		}
		return result;
	}
	
	public String getPusrseStatus(String result)
    {
        if ((byte)(1 & Byte.parseByte(result.substring(2, 4), 16)) == 0)
        {
            return "Not Enabled";
        } else
        {
            return "Enabled";
        }
    }

	public boolean checkCurrentBalance(String currentBalance,
			String purseBalance, String paymentAmt) {
		boolean check = false;
		BigDecimal currentB = BigDecimal.valueOf(Double.parseDouble(currentBalance));
		BigDecimal oldB = BigDecimal.valueOf(Double.parseDouble(purseBalance));
		BigDecimal paymentA = BigDecimal.valueOf(Double.parseDouble(paymentAmt));
		BigDecimal checkB = oldB.subtract(paymentA);
		if(currentB.compareTo(checkB) == 0) {
			check = true;
		} else {
			Log.d("Subtract two value be wrong: ", checkB.toString());
		}
		return check;
	}
}
