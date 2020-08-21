package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.common;

public final class LayoutConstants 
{
	public static final String JWCID = "jwcid";//jwcid
	
	public static final String EXTRA_START = "${";//extra替换开始标记 带属性
	
	public static final String EXTRA_END = "}";//extra替换结束标记
	
	public static final String EXTRA_START_N = "#{";//extra替换开始标记 不带属性
	
	public static final String EXTRA_END_N = "}";//extra替换结束标记 不带属性
	
	public static final String EXTRA_EXTRA = "$$";//extra替换标记
	
	public static final String TEMPLET_PAGE = "1"; //页面模版
	
	public static final String TEMPLET_PART = "2";//块模版
	
	public static final String TEMPLET_ELEMENT = "3";//元素模版
	
	public static final String TEMPLET_JS = "4";//js模版
	
	public static final String SPLIT_TAG = ";";//分割标识
	
	public static final String EXTRA_PAGE = "1"; //页面扩展属性
	
	public static final String EXTRA_PART = "2";//块扩展属性
	
	public static final String EXTRA_ELEMENT = "3";//元素扩展属性
	
	public static final String STATUS_VALID = "0"; // 有效状态

	public static final String STATUS_UNVALID = "1"; // 无效状态
	
	public static final String _LAYOUTS = "_layouts";
	
	public static final String _LAYOUT = "layout";
	
	public static final String _LAYOUT_NAME = "name";
	
	public static final String _PAGE = "page";
	
	public static final String _TYPE = "_type";
	
	public static final String _IF = "if";
	
	public static final String _CONDTIONS = "_conditons";

    public static final String _CONDITION = "condition";
    
    public static final String _COMPONENT_NAME = "jwcid";
    
    public static final int _PARAMERTER_Int = 0;

    public static final int _PARAMERTER_Long = 1;

    public static final int _PARAMERTER_Float = 2;

    public static final int _PARAMERTER_Double = 3;

    public static final int _PARAMERTER_Boolean = 4;

    public static final int _PARAMERTER_Char = 5;

    public static final int _PARAMERTER_String = 6;

    public static final int _PARAMERTER_Object = 7;
    
    public static final String _PARTID = "_partId";

    public static final String _POSITION = "position";
    
    public final static String SPLIT_EXPRESS = ",";

    public final static String SPLIT_KEY = "&";

    public final static String[] USE_TAGS_INPUT = new String[]
    { "11", "23", "24", "36", "4A" };

    public final static String[] USE_TAGS_HISTORY = new String[]
    { "12", "23", "25", "36", "4A" };

    public final static String[] USE_TAGS_HINT = new String[]
    { "13", "24", "25", "36", "4A" };

    public final static String[] USE_TAGS_TRANSFER = new String[]
    { "14", "4A" };
    
    public static final String STATIC_ZERO = "0";

    public static final String STATIC_ONE = "1";
    
    public static final String STR_START = "G,B,E";
    
    public static final String PART_TYPE_PART = "P";//块类型，默认P，PART_ID对应PART_ID 
    
    public static final String PART_TYPE_AREA = "A";//块类型，默认P，PART_ID对应AREA_ID
}
