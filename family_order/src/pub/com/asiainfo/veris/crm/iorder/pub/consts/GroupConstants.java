package com.asiainfo.veris.crm.iorder.pub.consts;

import java.util.HashMap;
import java.util.Map;

public final class GroupConstants
{
    public static final String DEFULT_EC_CLASS = "D"; // EC客户级别

    public static final String DEFULT_EC_TYPE = "1"; // EC客户类别
    
    public static final String DEFULT_EC_CALLINGTYPE = "00"; // EC客户行业类别
    
    public static final String EC_CLASS = "CUSTGROUP_CLASSID"; // EC客户级别

    public static final String EC_TYPE = "CUSTGROUP_GROUPTYPE"; // EC客户类别

    public static final String EC_CALLINGTYPE = "TD_S_CALLINGTYPE"; // EC行业类别
    
    public static final String EC_SIZE = "CUSTGROUP_SIZE"; // EC企业规模
    
    public static final Map<String, String> EC_LEVEL = new HashMap<String, String>()
            {
                {
                    put("A1", "5");
                    put("A2", "4");
                    put("A", "4");
                    put("B1", "3");
                    put("B2", "2");
                    put("B", "2");
                    put("C", "1");
                    put("D", "0");
                }
            };
}
