
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.DataUtils;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class StaticInfoQry
{

    public static void delTaskByCode(String typeId, String dataId) throws Exception
    {
        IData param = new DataMap();
        param.put("VTYPE_ID", typeId);
        param.put("VDATA_ID", dataId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" DELETE TD_S_STATIC WHERE TYPE_ID = :VTYPE_ID AND DATA_ID = :VDATA_ID ");
        Dao.executeUpdate(parser, Route.CONN_CRM_CEN);
    }

    /**
     * 获取酬金返还金额
     * 
     * @param pd
     *            页面数据
     * @throws Exception
     */
    public static IDataset getMoneyOpTypeDs(IData input, String epachyid) throws Exception
    {

        IData inparam = input;
        inparam.put("EPARCHY_CODE", epachyid);
        inparam.put("TYPE_ID", "RETURN_MONEY");
        return Dao.qryByCode("TD_S_STATIC", "SEL_BY_TYPEID_EPARCHY", inparam, Route.CONN_CRM_CEN);
    }

    /**
     * 获取酬金返还参与原因
     * 
     * @param pd
     *            页面数据
     * @throws Exception
     */
    public static IDataset getReasonTypeDs(IData input, String epachyid) throws Exception
    {

        IData inparam = input;
        inparam.put("EPARCHY_CODE", epachyid);
        inparam.put("TYPE_ID", "RETURN_MONEY_REASON");
        return Dao.qryByCode("TD_S_STATIC", "SEL_BY_TYPEID_EPARCHY", inparam, Route.CONN_CRM_CEN);

    }

    public static IDataset getRefuseInfo(String epachyid) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("REFUSE_REASON_CODE", "REFUSE_REASON_CODE");
        inparam.put("EPARCHY_CODE", epachyid);
        return Dao.qryByCode("TD_S_STATIC", "SEL_REFUSE_REASON_CODE", inparam, Route.CONN_CRM_CEN);

    }

    /**
     * 获取用户同一证件办理手机损坏保障服务的次数
     * 
     * @param params
     *            DATA_ID TYPE_ID 查询所需参数
     * @return IDataset
     * @throws Exception
     */
    public static IData getSHPsptNumInfo() throws Exception
    {
        return getStaticInfoByTypeIdDataId("SJBZSH_PSPT_NUM", "1");
    }

    /**
     * 通过DATA_ID TYPE_ID查找static里的详细信息
     * 
     * @param dataId
     * @param typeId
     * @return
     * @throws Exception
     */
    public static IData getStaticInfoByTypeIdDataId(String typeId, String dataId) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("TYPE_ID", typeId);
        inparam.put("DATA_ID", dataId);

        IDataset ids = Dao.qryByCode("TD_S_STATIC", "SEL_BY_TYPEID_DATAID", inparam, Route.CONN_CRM_CEN);

        if (IDataUtil.isEmpty(ids))
        {
            return null;
        }

        return ids.getData(0);
    }

    /**
     * 根据type_id、eparchy_code获取静态参数表数据
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset getStaticListByTypeIdEparchy(String eparchyCode, String TYPE_ID, Pagination page) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("EPARCHY_CODE", eparchyCode);
        inparams.put("TYPE_ID", TYPE_ID);
        return Dao.qryByCode("TD_S_STATIC", "SEL_STATIC_BY_EPARCHY", inparams, page, Route.CONN_CRM_CEN);
    }

    public static IDataset getStaticValidValueByTypeId(String typeId, String validFlag) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("TYPE_ID", typeId);
        inparams.put("VALID_FLAG", validFlag);
        return Dao.qryByCode("TD_S_STATIC", "SEL_VALID_BY_TYPEID", inparams, Route.CONN_CRM_CEN);
    }

    /**
     * 获取静态参数表信息
     */
    public static IDataset getStaticValue(String eparchyCode, String PDATA_ID, String DATA_ID, Pagination page) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("EPARCHY_CODE", eparchyCode);
        inparams.put("PDATA_ID", PDATA_ID);
        inparams.put("DATA_ID", DATA_ID);
        return Dao.qryByCode("TD_S_STATIC", "SEL_BY_PDATA_DATA", inparams, page, Route.CONN_CRM_CEN);
    }

    public static IDataset getStaticValueByTypeId(String TYPE_ID) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("TYPE_ID", TYPE_ID);
        IDataset dataset = Dao.qryByCode("TD_S_STATIC", "SEL_BY_TYPEID", inparams, Route.CONN_CRM_CEN);
        return dataset;
    }

    public static IDataset getUserRecommStatic(String eparchy_code) throws Exception
    {

        IData param = new DataMap();
        param.put("EPARCHY_CODE", eparchy_code);
        return Dao.qryByCode("TD_S_STATIC", "SEL_DELAY_DAY", param);
    }

    public static IDataset getWideNetTypeList(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" Select distinct(PDATA_ID),DATA_NAME FROM td_s_static  t Where t.type_id='BPM_ID2TRADE_CODE' ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static String qryProvCode(String provinceCode) throws Exception
    {
        IData data = getStaticInfoByTypeIdDataId("PROVINCE_CODE", provinceCode);

        if (IDataUtil.isEmpty(data))
        {
            return null;
        }

        String provCode = data.getString("PDATA_ID");

        return provCode;
    }

    /*
     * 查询渠道区域类型信息
     */
    public static IDataset queryAreaTypeTree(IData data) throws Exception
    {

        SQLParser parser = new SQLParser(data);
        parser.addSQL("select a.DATA_ID data_code, a.DATA_NAME data_name ,'0' NODE_COUNT ");
        parser.addSQL(" from td_s_static a ");
        parser.addSQL(" where a.type_id = 'CHNL_CHNL_AREATYPE' ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static IDataset queryCampnType(IData param) throws Exception
    {

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select data_id key, data_name value from td_s_static ");
        parser.addSQL(" where 1 = 1 ");
        parser.addSQL(" and type_id = :TYPE_ID ");
        parser.addSQL(" and data_id like :PRE_DATA_ID || '%'");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /**
     * 根据条件查询费用项目
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset queryNonBossFeeItems(IData inparams) throws Exception
    {

        SQLParser parser = new SQLParser(inparams);

        parser.addSQL(" SELECT A.TYPE_ID,A.DATA_ID,A.DATA_NAME,A.SUBSYS_CODE,A.EPARCHY_CODE,A.REMARK,A.UPDATE_STAFF_ID,A.UPDATE_DEPART_ID, ");
        parser.addSQL(" TO_CHAR(A.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,A.VALID_FLAG FROM TD_S_STATIC A WHERE A.TYPE_ID = 'PAY_ITEM_NAME' ");
        parser.addSQL(" AND A.DATA_NAME LIKE '%' || :DATA_NAME || '%' ");
        parser.addSQL("	AND A.UPDATE_TIME BETWEEN TO_DATE(:START_DATE, 'yyyy-mm-dd') AND TO_DATE(:END_DATE, 'yyyy-mm-dd') + 1 ");
        parser.addSQL(" ORDER BY to_number(A.DATA_ID) ASC ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static IDataset queryStaticInfos(String TYPE_ID, String PDATA_ID, String DATA_ID) throws Exception
    {
        IData cond = new DataMap();
        cond.put("TYPE_ID", TYPE_ID);
        cond.put("PDATA_ID", PDATA_ID);
        cond.put("DATA_ID", DATA_ID);
        return Dao.qryByCode("TD_S_STATIC", "SEL_BY_TYPEID_PDATA_DATAID", cond);
    }

    /**
     * 根据TYPE_ID和PDATA_ID查静态数据
     * 
     * @author wanglq
     * @version 创建时间：2011-1-11 下午03:52:12
     */
    public static IData queryStaticValueByPdataId(String PDATA_ID, String TYPE_ID) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("PDATA_ID", PDATA_ID);
        inparams.put("TYPE_ID", TYPE_ID);
        IDataset dataset = Dao.qryByCode("TD_S_STATIC", "SEL_BY_PDATA", inparams, Route.CONN_CRM_CEN);
        return (IData) (dataset.size() > 0 ? dataset.get(0) : null);
    }

    public static IDataset queryStaticValueByTypeDataName(String dataId, String typeId, String dataName, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("VDATA_ID", dataId);
        param.put("VTYPE_ID", typeId);
        param.put("VDATA_NAME", dataName);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT TYPE_ID,DATA_ID,DATA_NAME,SUBSYS_CODE,PDATA_ID,EPARCHY_CODE, ");
        parser.addSQL(" REMARK,UPDATE_STAFF_ID,UPDATE_DEPART_ID,TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME FROM TD_S_STATIC ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND DATA_ID LIKE '%' || :VDATA_ID || '%' ");
        parser.addSQL(" AND TYPE_ID = :VTYPE_ID ");
        parser.addSQL(" AND DATA_NAME LIKE '%' || :VDATA_NAME || '%' ");
        return Dao.qryByParse(parser, page, Route.CONN_CRM_CEN);
    }

    public static IDataset queryStaticValuesByPdataId(String PDATA_ID, String TYPE_ID) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("PDATA_ID", PDATA_ID);
        inparams.put("TYPE_ID", TYPE_ID);
        return Dao.qryByCode("TD_S_STATIC", "SEL_BY_PDATA", inparams, Route.CONN_CRM_CEN);
    }

    public static void updatePdataByDataId(String pDataId, String typeId, String dataId) throws Exception
    {
        IData param = new DataMap();
        param.put("VPDATA_ID", pDataId);
        param.put("VTYPE_ID", typeId);
        param.put("VDATA_ID", dataId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" UPDATE TD_S_STATIC SET PDATA_ID = :VPDATA_ID WHERE TYPE_ID = :VTYPE_ID AND DATA_ID = :VDATA_ID ");
        Dao.executeUpdate(parser, Route.CONN_CRM_CEN);
    }
    
    
    public static IDataset getStaticValueByPDType(IData param) throws Exception{
    	String typeId = param.getString("TYPE_ID");
    	String pData = param.getString("PDATA_ID");
    	IDataset offerList = param.getDataset("OFFER_DATA");
   
        
    	
    	for(int i=0;i<offerList.size();i++){
    	IData offerData = offerList.getData(i);
    	IData myParam = new DataMap();
    	myParam.put("TYPE_ID", typeId);
    	myParam.put("PDATA_ID", pData);
    	myParam.put("DATA_ID", offerData.get("OFFER_ID"));
 		    
 		IDataset returnData = Dao.qryByCode("TD_S_STATIC", "SEL_BY_PDATA_TYPEID_DATA", myParam);
 		
 		if(DataUtils.isNotEmpty(returnData))
 			offerData.put("IS_DISCOUNT_TIME_CHANGE", 1);
 		else offerData.put("IS_DISCOUNT_TIME_CHANGE", 0); 
 			
    	}
    	
 		return offerList;
    	
    }
    
    
    public static IDataset getStaticValueByPDTypeMEM(IData param) throws Exception{
    	String typeId = param.getString("TYPE_ID");
    	String pData = param.getString("PDATA_ID");
    	String subData = param.getString("DATA_ID");
   
        

    	IData myParam = new DataMap();
    	myParam.put("TYPE_ID", typeId);
    	myParam.put("PDATA_ID", pData);
    	myParam.put("DATA_ID", subData);
 		    
 		IDataset returnData = Dao.qryByCode("TD_S_STATIC", "SEL_BY_PDATA_TYPEID_DATA", myParam);
 	
    	
 		return returnData;
    	
    }
    
  
    
}
