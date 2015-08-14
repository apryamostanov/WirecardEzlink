package com.wirecard.ezlink.handle;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

public class CardInfoHandler {
	
	public String getPurseBal(String result) {
		String purseBal = result.substring(4, 10);
		int balanceStr = Integer.parseInt(purseBal, 16);
		BigDecimal balance = BigDecimal.valueOf((long) balanceStr, 2);
		return String.valueOf(balance);
	}
	
	public boolean isSurrenderedCard(String result) {
		
		byte[] s = Util.hexStringToByteArray(result.substring(134, 136));
		if(s[0]==0xC0) {
			return true;
		}
		String purseStatus = result.substring(2, 4);
		byte[] b = Util.hexStringToByteArray(purseStatus);
		if((b[0] != 0x01) && (b[0] != 0x03)) {
			return true;
		}
		return false;
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
		String previousBalance, String paymentAmt) {
		boolean check = false;
		BigDecimal currentB = BigDecimal.valueOf(Double.parseDouble(currentBalance));
		BigDecimal oldB = BigDecimal.valueOf(Double.parseDouble(previousBalance));
		BigDecimal paymentA = BigDecimal.valueOf(Double.parseDouble(paymentAmt));
		BigDecimal subBalance = oldB.subtract(paymentA);
		if(currentB.compareTo(subBalance) == 0) {
			check = true;
		} else {
			Log.d("Subtract two value be wrong: ", subBalance.toString());
		}
		return check;
	}
	
	public String getALStatus(String result)
    {
        byte byte0 = Byte.parseByte(result.substring(2, 4), 16);
        if ((byte)(byte0 & 1) == 0)
        {
            return "N.A.";
        }
        if ((byte)(byte0 & 2) == 0)
        {
            return "Not Enabled";
        } else
        {
            return "Enabled";
        }
    }
	
	public String getALAmount(String result)
    {
        byte byte0 = Byte.parseByte(result.substring(2, 4), 16);
        if ((byte)(byte0 & 1) == 0)
        {
            return "N.A.";
        }
        if ((byte)(byte0 & 2) == 0)
        {
            return "N.A.";
        } else
        {
            return getAmount(result.substring(10, 16), "");
        }
    }
	
	public String getAmount(String s, String s1)
    {
        if (s1.startsWith("F0"))
        {
            return "N.A.";
        }
        if (s1.startsWith("11"))
        {
            return "N.A.";
        }
        if (s1.startsWith("83"))
        {
            return "N.A.";
        }
        if (s.startsWith("0"))
        {
        	for(int i=0;i<s.length();i++) {
        		if(!s.substring(i).startsWith("0")) {
        			int j = Integer.parseInt(s.substring(i), 16);
                    String s3 = dotAppending((new DecimalFormat("###,###,###.##")).format(0.01D * (double)j));
                    return s3;
        		}
        	}
        	return "balance null";
        } else
        {
            long l = Long.parseLong("FFFFFF", 16);
            long l1 = Long.parseLong(s, 16);
            s = dotAppending((new DecimalFormat("###,###,###.##")).format((double)((l - l1) + 1L) * 0.01D));
            return (new StringBuilder("-")).append(s).toString();
        }
    }
	
	private String dotAppending(String s)
    {
        int i = s.indexOf(".");
        String s1;
        if (i < 0)
        {
            s1 = (new StringBuilder(String.valueOf(s))).append(".00").toString();
        } else
        {
            s1 = s;
            if (i == s.length() - 2)
            {
                return (new StringBuilder(String.valueOf(s))).append("0").toString();
            }
        }
        return s1;
    }

	public boolean checkCurrentBalanceWithAutoLoadAmt(String currentBalance,
			String previousBalance, String paymentAmt, String autoLoadAmt) {
		boolean check = false;
		BigDecimal currentB = BigDecimal.valueOf(Double.parseDouble(currentBalance));
		BigDecimal oldB = BigDecimal.valueOf(Double.parseDouble(previousBalance));
		BigDecimal paymentA = BigDecimal.valueOf(Double.parseDouble(paymentAmt));
		BigDecimal autoloadA = BigDecimal.valueOf(Double.parseDouble(autoLoadAmt));
		BigDecimal subBalance = (oldB.add(autoloadA)).subtract(paymentA);
		if(currentB.compareTo(subBalance) == 0) {
			check = true;
		} else {
			Log.d("Subtract two value be wrong: ", subBalance.toString());
		}
		return check;
	}
}
