
package com.asiainfo.veris.crm.order.pub.frame.bcf.util;

import java.text.DecimalFormat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;

public final class FeeUtils
{
    public static final String FEE_REGEX = "\\-?[0-9]+";

    /** 浮点数转RMB大写 */
    public static String doubleToRMB(double money)
    {

        double absMoney = Math.abs(money);
        String StrTemp = null;
        String Number[] =
        { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
        String MonetaryUnit[] =
        { "万", "仟", "佰", "拾", "亿", "仟", "佰", "拾", "万", "仟", "佰", "拾", "元", "角", "分" };
        DecimalFormat deciformat = (DecimalFormat) DecimalFormat.getInstance();
        deciformat.applyPattern("#######");
        String m = String.valueOf(deciformat.format(absMoney * 100));
        int i;
        if ((i = m.indexOf('.')) != -1)
        {
            m = m.substring(0, i);
        }
        char[] p = new char[m.length()];
        m.getChars(0, m.length(), p, 0);
        if (absMoney > 100000000000.00)
        {
            StrTemp = "";
            return StrTemp;
        }
        if (absMoney < 0.01)
        {
            StrTemp = "零";
            return StrTemp;
        }
        if (money < 0)
        {
            StrTemp = "负";
        }
        else
        {
            StrTemp = "";
        }
        int flag = 1;
        int len = p.length;
        for (int idx = (15 - len); idx < 15; idx++)
        {
            if (p[idx - 15 + len] != '0')
            {
                StrTemp = StrTemp + Number[Integer.parseInt(String.valueOf(p[idx - 15 + len]))];
                StrTemp = StrTemp + MonetaryUnit[idx];
            }
            else
            {
                if (idx == 5)
                {
                    if ((p[idx - 14 + len] != '0') || (p[idx - 13 + len] != '0'))
                    {
                        StrTemp = StrTemp + MonetaryUnit[idx + 3];
                        flag = 0;
                    }
                }
                else
                {
                    if ((idx == 12) || ((idx == 8) && (flag == 1)) || (idx == 4))
                    {
                        StrTemp = StrTemp + MonetaryUnit[idx];
                    }
                    if ((p[idx - 15 + len] != '0') && (idx != 14))
                    {
                        StrTemp = StrTemp + Number[Integer.parseInt(String.valueOf(p[idx - 15 + len]))];
                    }
                }
            }
        }
        if (p[m.length() - 1] == '0')
        {
            StrTemp = StrTemp + "整";
        }
        return StrTemp;
    }

    /**
     * 分转化为元，上面的有问题
     * 
     * @param fen
     * @return
     * @throws Exception
     */
    public static String Fen2Yuan(String fen) throws Exception
    {

        DecimalFormat format = new DecimalFormat("#0.00");
        return format.format(toDouble(fen) / 100);
    }

    public static String floatToRMB(double money)
    {

        String sRMBUnit = "分角元拾佰仟万拾佰仟亿拾百";
        String sRMBNumber = "零壹贰叁肆伍陆柒捌玖";
        String sMoney;
        String sChar, sNumber, sUnit;
        StringBuilder sRMBMoney = new StringBuilder();
        int iMoneyLength, iPosDecimal;
        double fAfterDecimal; // 小数部分
        int iBeforeDecimal; // 整数部分
        boolean bIsZeroEnd;
        iBeforeDecimal = (int) money;
        fAfterDecimal = Math.abs(money - iBeforeDecimal);
        if (fAfterDecimal == 0)
        {
            sRMBMoney.append("整");
        }
        sMoney = String.format("%1$+3.2f", money);
        // 删除小数点
        iPosDecimal = sMoney.indexOf('.');
        if (iPosDecimal > 0)
            sMoney = sMoney.substring(1, iPosDecimal) + sMoney.substring(iPosDecimal + 1);
        iMoneyLength = sMoney.length();
        int i = 0;
        bIsZeroEnd = true;
        while (iMoneyLength > 0)
        {
            sChar = sMoney.substring(iMoneyLength - 1, iMoneyLength);
            sNumber = sRMBNumber.substring(Integer.valueOf(sChar), Integer.valueOf(sChar) + 1);
            sUnit = sRMBUnit.substring(i, i + 1);
            if (sChar.equals("0"))
            {
                if (i != 2 && i != 6 && i != 10)
                    sUnit = "";
                if (bIsZeroEnd || i == 2 || i == 6 || i == 10)
                    sNumber = "";
                if (Math.abs(money) < 1 && i <= 2)
                {
                    sUnit = "";
                    sNumber = "";
                }
                bIsZeroEnd = true;
            }
            else
                bIsZeroEnd = false;
            sRMBMoney.insert(0, sNumber + sUnit);
            iMoneyLength--;
            i++;
        }
        if (money < 0)
        {
            sRMBMoney.insert(0, "负");
        }
        return sRMBMoney.toString();
    }

    public static String formatMoney(String momey, String formatstr) throws Exception
    {

        DecimalFormat format = new DecimalFormat(formatstr);
        return format.format(Double.parseDouble(momey));
    }

    /**
     * 返回各费用Map
     * 
     * @param ids
     * @return
     * @throws Exception
     * @throws Exception
     */
    public static IData getFeeData(IDataset ids) throws Exception
    {

        int iOperFee = 0;
        int iForegift = 0;
        int iAdvancePay = 0;

        if (IDataUtil.isNotEmpty(ids))
        {
            IData map = null;

            for (int size = ids.size(), i = 0; i < size; i++)
            {
                map = ids.getData(i);

                int fee = Integer.parseInt((String) map.get("FEE"));

                String feeMode = map.getString("FEE_MODE");

                if ("0".equals(feeMode))
                {
                    iOperFee += fee;
                }
                else if ("1".equals(feeMode))
                {
                    iForegift += fee;
                }
                else if ("2".equals(feeMode))
                {
                    iAdvancePay += fee;
                }
            }
        }

        IData result = new DataMap();
        result.put("OPER_FEE", iOperFee);
        result.put("FOREGIFT", iForegift);
        result.put("ADVANCE_PAY", iAdvancePay);

        int totalFee = iOperFee + iForegift + iAdvancePay;

        result.put("TOTAL_FEE", totalFee);

        return result;
    }

    /**
     * String 转化成 double 并进行格式化
     * 
     * @param obj
     * @return
     */
    public static double toDouble(Object obj)
    {
        String value;
        if (obj == null)
            return 0.00;
        value = obj.toString().trim();
        if (value.length() == 0)
            return 0.00;
        else
            return Double.parseDouble(value);

    }
}
