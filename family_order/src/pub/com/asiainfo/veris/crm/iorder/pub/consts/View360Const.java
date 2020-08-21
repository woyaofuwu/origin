package com.asiainfo.veris.crm.iorder.pub.consts;

import java.util.HashMap;
import java.util.Map;

public class View360Const {
    public static final Map<Integer, String> BILL_IMG = new HashMap<Integer, String>() {
        {
            put(1, "package2"); // 套餐及固定费
            put(2, "call");     // 语音通信费
            put(3, "msg");      // 短彩信费
            put(4, "net");      // 上网费
            put(5, "diamond");  // 自有增值业务费用
            put(6, "bill");     // 代收费业务费用
            put(7, "money");    // 其他费用
            put(8, "fare");     // 优惠及减免
        }
    };

    public static final String BILL_COMMON = "e_navy-lighter e_ico-pic-xxs e_hide-phone e_ico-";

    public static final Map<String, String> EC_LEVEL = new HashMap<String, String>() {
        {
            put("A1", "5");
            put("A2", "4");
            put("A" , "4");
            put("B1", "3");
            put("B2", "2");
            put("B" , "2");
            put("C" , "1");
            put("D" , "0");
        }
    };

    public static final Map<String, String> PRODUCT_COLOR = new HashMap<String, String>() {
        {
            put("0", "blue");   // 新增
            put("1", "red");    // 删除
            put("2", "orange"); // 修改
        }
    };

    public static final String PRODUCT_COMMON = "e_ico-pic-r e_ico-pic-";

    public static final Map<String, String> PRODUCT_MODIFY_TAG = new HashMap<String, String>() {
        {
            put("0", "新增");
            put("1", "删除");
            put("2", "修改");
        }
    };

    public static final Map<String, String> ELEMENT_COLOR = new HashMap<String, String>() {
        {
            put("0", "blue");   // 新增
            put("1", "red");    // 删除
            put("2", "orange"); // 修改
            put("U", "navy");   // 继承
        }
    };

    public static final String ELEMENT_COMMON = "side e_size-m e_";

    public static final Map<String, String> ELEMENT_MODIFY_TAG = new HashMap<String, String>() {
        {
            put("0", "新增");
            put("1", "删除");
            put("2", "修改");
            put("U", "继承");
        }
    };

    public static final Map<String, String> PLATSVC_COLOR = new HashMap<String, String>() {
        {
            put("A", "green");  // 正常
            put("N", "gray");   // 暂停
            put("E", "red");    // 退订
            put("L", "orange"); // 挂失
            put("P", "navy");   // 预退订
        }
    };

    public static final Map<String, String> PLATSVC_STATE = new HashMap<String, String>() {
        {
            put("正常",   "A");
            put("暂停",   "N");
            put("终止",   "E");
            put("锁定",   "L");
            put("预退订", "P");
        }
    };

    public static final String PLATSVC_COMMON = "statu statu-right e_size-s statu-";
}
