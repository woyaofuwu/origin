package com.asiainfo.veris.crm.iorder.pub.consts;

import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;

public class EcEsopConstants 
{
	 public final static String TABLE_EOP_SUBSCRIBE = "biz"; //对应TF_B_EOP_SUBSCRIBE表数据
	 
	 public final static String TABLE_EOP_NODE = "node"; //对应TF_B_EOP_NODE表数据
	 
	 public final static String TABLE_EOP_PRODUCT = "pro"; //对应TF_B_EOP_PRODUCT表数据
	 
	 public final static String TABLE_EOP_PRODUCT_SUB = "prosub"; //对应TF_B_EOP_PRODUCT_SUB表数据
	 
	 public final static String TABLE_EOP_PAGE_SAVE = "page"; //对应TF_B_EOP_PAGE_SAVE表数据
	 
	 public final static String TABLE_EOP_OTHER = "other"; //对应TF_B_EOP_OTHER表数据
	 
	 public final static String TABLE_EOP_DIS = "dis"; //对应TF_B_EOP_DIS表数据
	 
	 public final static String TABLE_EOP_SVC = "svc"; //对应TF_B_EOP_SVC表数据
	 
	 public final static String TABLE_EOP_ATTR = "attr"; //对应TF_B_EOP_ATTR表数据
	 
	 public final static String TABLE_EOP_EOMS = "eoms"; //对应TF_B_EOP_EOMS表数据
	 
	 public final static String TABLE_EOP_ATTACH = "attach"; //对应TF_B_EOP_ATTACH表数据
	 
	 public final static String TABLE_EOP_QUICKORDER_COND = "quickorder"; //对应TF_B_EOP_QUICKORDER_COND表数据

	 public final static String TABLE_EOP_QUICKORDER_MEB = "quickordermeb"; //对应TF_B_EOP_QUICKORDER_MEB表数据

	 public final static String TABLE_EOP_QUICKORDER_DATA = "quickorderdata"; //对应TF_B_EOP_QUICKORDER_DATA表数据

    public final static String TABLE_EOP_PRODUCT_EXT = "proext"; //对应  TF_B_EOP_PRODUCT_EXT  表数据

	 public final static String DEFAULT_BUSI_TYPE = "P";  //默认BUSI_TYPE
	 
	 public final static String TAG_ADD = "0";
	 
	 public final static String TAG_DEL = "1";
	 
	 public final static String TAG_MODI = "2";
	 
	 public final static String TAG_EXIST = "3";
	 
	 public final static String TAG_MDISCNT = "4";
	 
	 public final static String STATE_VALID= "0";
	 
	 public final static String STATE_UNVALID= "1";
	 
	 public final static String APPLY_ADD = "20";// 20	开通
	 public final static String APPLY_MPARAM = "21";//21	资源变更
	 public final static String APPLY_MDISCNT = "22";//22	资费变更
	 public final static String APPLY_MMEB = "23";// 23	产品成员变更
	 public final static String APPLYT_DEL = "25";// 25	注销
	 
	public static int RESOURCES = 31;
	public static int CHANGERESOURCES = 35;
	public static int OPEN = 32;
	public static int CHANGE =33;
	public static int CANCEL =34;
	
	public static final String WORKINFO_STATUS_UNDO = "1";  //待处理
    public static final String WORKINFO_STATUS_DONE = "9";  //已处理
    
    public static final String TASK_LEVEL_1 = "1"; //一般
    public static final String TASK_LEVEL_2 = "2"; //重要
    public static final String TASK_LEVEL_3 = "3"; //紧急
    
    public static final String TASK_TYPE_CODE_MESSAGE = "1";   //消息
    public static final String TASK_TYPE_CODE_BULLETIN = "2";  //公告
    public static final String TASK_TYPE_CODE_WORKINFO = "3";  //待办工作消息
    
    public static final String INST_STATUS_NOREAD = "0";
    public static final String INST_STATUS_READED = "1";
    
    public static final String BUSI_TYPE_CODE_31 = "31";//账务待办 
    public static final String BUSI_TYPE_CODE_32 = "32";//电子渠道待办
    public static final String BUSI_TYPE_CODE_33 = "33";//营销待办
    public static final String BUSI_TYPE_CODE_34 = "34";//信控待办
    public static final String BUSI_TYPE_CODE_35 = "35";//电子工单待办
    public static final String BUSI_TYPE_CODE_39 = "39";//ESOP1.0BI的工作
    
    public static final String BUSI_TYPE_CODE_40 = "40";//信控待阅
    public static final String BUSI_TYPE_CODE_41 = "41";//电子工单待阅
    public static final String BUSI_TYPE_CODE_42 = "42";//备用
    public static final String BUSI_TYPE_CODE_43 = "43";//合同到期 提醒   //省级客户经理规范新增
    public static final String BUSI_TYPE_CODE_44 = "44";//合同续约 提醒
    public static final String BUSI_TYPE_CODE_45 = "45";//预警提醒(集团收入、通信行为、集团统付到期，集团欠费、营销案捆绑到期)
    
    public static final String RECE_OBJ_TYPE_AREA = "1";
    public static final String RECE_OBJ_TYPE_STAFF = "2";
    
    public static final String BBOSS_NODE_APPLY = "apply";
    public static final String BBOSS_MANAGE_BPM = "OrganizedManagement";//管理流程 流程名
    public static final String BBOSS_MANAGE_OPER_TYPE = "50";//管理流程 流程名
    
    public  static String eopOperTypeToCrmOperCode(String operType) throws Exception
    {
        if(APPLY_ADD.equals(operType))//开通
        {
            return TAG_ADD;
        }
        if(APPLY_MPARAM.equals(operType))//资源变更
        {
            return TAG_MODI;
        }
        if(APPLY_MDISCNT.equals(operType))//资费变更
        {
            return TAG_MODI;
        }
        if(APPLY_MMEB.equals(operType))//产品成员变更
        {
            return TAG_MODI;
        }
        if(APPLYT_DEL.equals(operType))//注销
        {
            return TAG_DEL;
        }
        return  "";
    }
    
    public  static String BbossOperTypeToCrmOperCode(String operType) throws Exception
    {
        if(APPLY_ADD.equals(operType))//开通
        {
            return TAG_ADD;
        }
        else//资源变更,/资费变更//注销,bboss注销在CRM处走的是变更操作
        {
            return TAG_MODI;
        }
    }
    
    
    public  static String bbossEopMOperTypeToOperCode(String operType) throws Exception
    {
        if(APPLY_ADD.equals(operType))//开通
        {
            return EcConstants.MERCH_PRODUCT_STATUS.PRODUCT_PREDEAL.getValue();
        }
        if(APPLY_MPARAM.equals(operType))//资源变更
        {
            return EcConstants.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_PARAM.getValue();//修改订购产品属性
        }
        if(APPLY_MDISCNT.equals(operType))//资费变更
        {
            return EcConstants.MERCH_PRODUCT_STATUS.PRODUCT_PREDEAL.getValue();
        }
        if(APPLYT_DEL.equals(operType))//注销
        {
            return EcConstants.MERCH_PRODUCT_STATUS.PRODUCT_CANCLE.getValue();
        }
        return  EcConstants.MERCH_PRODUCT_STATUS.PRODUCT_ADD.getValue();
    }
    
    public static String transOperTypeToNodeId(String operType) throws Exception
    {
        if(APPLY_ADD.equals(operType))
        {
            return BizCtrlType.CreateUser;
        }
        else if(APPLYT_DEL.equals(operType))
        {
            return BizCtrlType.DestoryUser;
        }
        else
        {
            return BizCtrlType.ChangeUserDis;
        }
    }
}
