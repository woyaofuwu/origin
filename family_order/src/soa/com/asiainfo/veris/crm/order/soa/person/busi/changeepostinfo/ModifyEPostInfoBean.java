package com.asiainfo.veris.crm.order.soa.person.busi.changeepostinfo;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ModifyEPostInfoBean{
	
	/**
     * 电子发票推送信息信息查询
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset qryEPostInfo(IData param) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
    	return Dao.qryByCode("TF_F_USER_EPOSTINFO", "SEL_BY_SERIAL", inparams);
    }
    
    /**
     * 查看用户是否存在无效数据
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset qryEPostInfoByMon(IData param) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
    	return Dao.qryByCode("TF_F_USER_EPOSTINFO", "SEL_BY_SERIAL_MON", inparams);
    }
    

    /**
     * 电子发票推送信息信息查询BY userId
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset qryEPostInfoByUserId(IData param) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", param.getString("USER_ID"));
    	return Dao.qryByCode("TF_F_USER_EPOSTINFO", "SEL_BY_USERID", inparams);
    }
    
    /**
     * 电子发票推送信息信息查询根据POSTTAG
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset qryEPostInfoByTag(IData param) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
        inparams.put("POST_TAG", param.getString("POST_TAG"));
    	return Dao.qryByCode("TF_F_USER_EPOSTINFO", "SEL_BY_SERIALBYTAG", inparams);
    }
    
    /**
     * 增加日常电子发票
     */
    public static void addBusiCashPost(IData tabData,String postTag,String postChannel,String reNumber,String postAdr) throws Exception
    {
    	IData data = new DataMap();
    	data.put("SERIAL_NUMBER", tabData.getString("SERIAL_NUMBER"));
		data.put("USER_ID", tabData.getString("USER_ID"));
		data.put("PARTITION_ID", tabData.getString("PARTITION_ID")); 
		data.put("EPARCHY_CODE", tabData.getString("EPARCHY_CODE"));
		data.put("UPDATE_STAFF_ID", tabData.getString("UPDATE_STAFF_ID"));
		data.put("UPDATE_DEPART_ID", tabData.getString("UPDATE_DEPART_ID")); 
		data.put("UPDATE_TIME", tabData.getString("UPDATE_TIME"));
		data.put("POST_TAG", postTag);
		data.put("POST_CHANNEL", postChannel);
		data.put("RECEIVE_NUMBER", reNumber);
		data.put("POST_ADR", postAdr);
		data.put("RSRV_STR1", "1");
		data.put("RSRV_STR2", tabData.getString("RSRV_STR2"));

		Dao.insert("TF_F_USER_EPOSTINFO", data);
    }
    
    /**
     * 修改日常电子发票
     */
    public static boolean upBusiCashPost(IData tabData,String postTag,String postChannel,String reNumber,String postAdr) throws Exception
    {
    	IData data = new DataMap();
    	data.put("SERIAL_NUMBER", tabData.getString("SERIAL_NUMBER"));
		data.put("USER_ID", tabData.getString("USER_ID"));
		data.put("PARTITION_ID", tabData.getString("PARTITION_ID")); 
		data.put("EPARCHY_CODE", tabData.getString("EPARCHY_CODE"));
		data.put("UPDATE_STAFF_ID", tabData.getString("UPDATE_STAFF_ID"));
		data.put("UPDATE_DEPART_ID", tabData.getString("UPDATE_DEPART_ID")); 
		data.put("UPDATE_TIME", tabData.getString("UPDATE_TIME"));
		data.put("POST_TAG", postTag);
		data.put("POST_CHANNEL", postChannel);
		data.put("RECEIVE_NUMBER", reNumber);
		data.put("POST_ADR", postAdr);
		data.put("RSRV_STR1", "1");
		data.put("RSRV_STR2", tabData.getString("RSRV_STR2"));
		
		return Dao.update("TF_F_USER_EPOSTINFO", data,new String[]
		{"SERIAL_NUMBER","POST_TAG"});
    }
    
    public static boolean upBusiCashPost(String serialNumber,String postTag,String rsrvStr3,String rsrvStr4) throws Exception
    {
    	IData data = new DataMap();
    	data.put("SERIAL_NUMBER", serialNumber);
		data.put("POST_TAG", postTag);
		data.put("RSRV_STR3", rsrvStr3);
		data.put("RSRV_STR4", rsrvStr4);
		
		return Dao.save("TF_F_USER_EPOSTINFO", data,new String[]
		{"SERIAL_NUMBER","POST_TAG"});
    }
    
    /**
     * 删除日常电子发票
     */
    public static void delBusiCashPost(String serialNumber,String postTag) throws Exception
    {
    	IData data = new DataMap();
    	data.put("SERIAL_NUMBER", serialNumber);
		data.put("POST_TAG", postTag);
	    Dao.delete("TF_F_USER_EPOSTINFO", data,new String[]
	    {"SERIAL_NUMBER","POST_TAG"});
    }
    
    /**
     * 增加月结电子发票
     */
    public static void addMonPost(IData tabData,String postTag,String postChannelMon,String reNumberMon,String postAdrMon,String postDate) throws Exception
    {
    	IData data = new DataMap();
    	data.put("SERIAL_NUMBER", tabData.getString("SERIAL_NUMBER"));
		data.put("USER_ID", tabData.getString("USER_ID"));
		data.put("PARTITION_ID", tabData.getString("PARTITION_ID")); 
		data.put("EPARCHY_CODE", tabData.getString("EPARCHY_CODE"));
		data.put("UPDATE_STAFF_ID", tabData.getString("UPDATE_STAFF_ID"));
		data.put("UPDATE_DEPART_ID", tabData.getString("UPDATE_DEPART_ID")); 
		data.put("UPDATE_TIME", tabData.getString("UPDATE_TIME"));
		data.put("POST_TAG", postTag);
		data.put("POST_CHANNEL", postChannelMon);
		data.put("RECEIVE_NUMBER", reNumberMon);
		data.put("POST_ADR", postAdrMon);
		data.put("POST_DATE", postDate);
		data.put("RSRV_STR1", "1");
		data.put("RSRV_STR2", tabData.getString("RSRV_STR2"));
	
		Dao.insert("TF_F_USER_EPOSTINFO", data);
    }
    
    /**
     * 增加一条无效月结数据
     */
    public static void addUnMonPost(IData tabData,String postAdrMon) throws Exception
    {
    	IData data = new DataMap();
    	data.put("SERIAL_NUMBER", tabData.getString("SERIAL_NUMBER"));
		data.put("USER_ID", tabData.getString("USER_ID"));
		data.put("PARTITION_ID", tabData.getString("PARTITION_ID")); 
		data.put("EPARCHY_CODE", tabData.getString("EPARCHY_CODE"));
		data.put("UPDATE_STAFF_ID", tabData.getString("UPDATE_STAFF_ID"));
		data.put("UPDATE_DEPART_ID", tabData.getString("UPDATE_DEPART_ID")); 
		data.put("UPDATE_TIME", tabData.getString("UPDATE_TIME"));
		data.put("POST_TAG", "0");
		data.put("POST_CHANNEL", "1");
		data.put("POST_ADR", postAdrMon);
		data.put("RSRV_STR1", "0");
	
		Dao.insert("TF_F_USER_EPOSTINFO", data);
    }
    
    /**
     * 修改一条无效月结数据（修改邮箱地址）
     */
    public static void upUnMonPost(IData tabData,String postAdrMon) throws Exception
    {
    	IData data = new DataMap();
    	data.put("SERIAL_NUMBER", tabData.getString("SERIAL_NUMBER")); 
		data.put("USER_ID", tabData.getString("USER_ID"));
		data.put("PARTITION_ID", tabData.getString("PARTITION_ID")); 
		data.put("EPARCHY_CODE", tabData.getString("EPARCHY_CODE"));
		data.put("UPDATE_STAFF_ID", tabData.getString("UPDATE_STAFF_ID"));
		data.put("UPDATE_DEPART_ID", tabData.getString("UPDATE_DEPART_ID")); 
		data.put("UPDATE_TIME", tabData.getString("UPDATE_TIME"));
		data.put("POST_TAG", "0");
		data.put("POST_CHANNEL", "1");
		data.put("POST_ADR", postAdrMon);
		data.put("RSRV_STR1", "0");
		
	
		Dao.update("TF_F_USER_EPOSTINFO", data,new String[]
		{"SERIAL_NUMBER","RSRV_STR1"});
    }
    
    /**
     * 删除一条无效月结数据
     */
    public static void delUnMonPost(String serialNumber) throws Exception
    {
    	IData data = new DataMap();
    	data.put("SERIAL_NUMBER", serialNumber);
		data.put("POST_TAG", "0");
		data.put("RSRV_STR1", "0");
	    Dao.delete("TF_F_USER_EPOSTINFO", data,new String[]
	    {"SERIAL_NUMBER","POST_TAG","RSRV_STR1"});
    }
   
    /**
     * 修改正常月结电子发票
     */
    public static void upMonPost(IData tabData,String postChannelMon,String reNumberMon,String postAdrMon,String postDate) throws Exception
    {
    	IData data = new DataMap();
    	data.put("SERIAL_NUMBER", tabData.getString("SERIAL_NUMBER")); 
		data.put("USER_ID", tabData.getString("USER_ID"));
		data.put("PARTITION_ID", tabData.getString("PARTITION_ID")); 
		data.put("EPARCHY_CODE", tabData.getString("EPARCHY_CODE"));
		data.put("UPDATE_STAFF_ID", tabData.getString("UPDATE_STAFF_ID"));
		data.put("UPDATE_DEPART_ID", tabData.getString("UPDATE_DEPART_ID")); 
		data.put("UPDATE_TIME", tabData.getString("UPDATE_TIME"));
		data.put("POST_TAG", "0");
		data.put("POST_CHANNEL", postChannelMon);
		data.put("RECEIVE_NUMBER", reNumberMon);
		data.put("POST_ADR", postAdrMon);
		data.put("POST_DATE", postDate);
		data.put("RSRV_STR1", "1");
		data.put("RSRV_STR2", tabData.getString("RSRV_STR2"));
	
		Dao.update("TF_F_USER_EPOSTINFO", data,new String[]
		{"SERIAL_NUMBER","POST_TAG","RSRV_STR1"});
    }
    
    /**
     * 删除正常月结电子发票
     */
    public static void delMonPost(String serialNumber) throws Exception
    {
    	IData data = new DataMap();
    	data.put("SERIAL_NUMBER", serialNumber);
		data.put("POST_TAG", "0");
		data.put("RSRV_STR1", "1");
	    Dao.delete("TF_F_USER_EPOSTINFO", data,new String[]
	    {"SERIAL_NUMBER","POST_TAG","RSRV_STR1"});
    }
    
    /**
     * 用户销户时删除电子发票设置信息
     */
    public static void destroyUserdelMonPost(String serialNumber,String user_id) throws Exception
    {
    	IData data = new DataMap();
    	data.put("SERIAL_NUMBER", serialNumber);
		data.put("USER_ID", user_id);
	    Dao.delete("TF_F_USER_EPOSTINFO", data,new String[]{"SERIAL_NUMBER","USER_ID"});
    }
    /**
     * 删除任意的发票类型k3
     * @param serialNumber
     * @param postTag
     * @throws Exception
     */
    public static void delPostType(String serialNumber,String postTag) throws Exception
    {
    	IData data = new DataMap();
    	data.put("SERIAL_NUMBER", serialNumber);
    	data.put("POST_TAG", postTag);
    	data.put("RSRV_STR1", "1");
    	Dao.delete("TF_F_USER_EPOSTINFO", data,new String[]
    			{"SERIAL_NUMBER","POST_TAG","RSRV_STR1"});
    }
}
