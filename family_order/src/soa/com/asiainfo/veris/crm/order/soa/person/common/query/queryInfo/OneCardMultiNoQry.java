package com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo;



import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class OneCardMultiNoQry
{
	/**
	 * 根据主号查询所有的关联副号码
	 */
	public static IDataset qryRelationList(String user_id,String relation_type_code,String serial_number_b,String orderNo) throws Exception {
		IData inParam = new DataMap();
		inParam.put("USER_ID", user_id);
		inParam.put("SERIAL_NUMBER_B",serial_number_b );
		inParam.put("RELATION_TYPE_CODE", relation_type_code);
		inParam.put("ORDERNO", orderNo);
		IDataset relationList = Dao.qryByCodeParser("TF_F_RELATION_UU","SEL_BY_SN_RELATION_TYPE_CODE",inParam);
		return relationList;
	}
	/**
	 * 根据副号码查询主号码和USER_ID
	 * @param serial_number_b
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryNumberAbyB(String serial_number_b) throws Exception{
		IData inParam = new DataMap();
		inParam.put("SERIAL_NUMBER_B", serial_number_b);
		IDataset numberAidataset = Dao.qryByCodeParser("TF_F_RELATION_UU","SEL_NUMA_BY_NUMB",inParam);
		return numberAidataset;
	}
	
	/**
	 * 根据主号查询所有的关联副号码
	 */
	public static IDataset qryRelationListNew(String user_id,String relation_type_code,String serial_number_b) throws Exception {
		IData inParam = new DataMap();
		inParam.put("USER_ID", user_id);
		inParam.put("SERIAL_NUMBER_B",serial_number_b );
		inParam.put("RELATION_TYPE_CODE", relation_type_code);
		IDataset relationList = Dao.qryByCodeParser("TF_F_RELATION_UU","SEL_BY_SN_RELATION_TYPE_CODE_NEW",inParam);
		return relationList;
	}
	
	/**
	 * 获取对应的 服务
	 */
	public static IDataset getPlatSVCByUserId(String user_id,String service_id,String org_domain,String sp_code,String biz_code,String biz_type_code) throws Exception{
		IData inParam = new DataMap();
		inParam.put("USER_ID", user_id);
		inParam.put("SERVICE_ID", service_id);
		inParam.put("ORG_DOMAIN", org_domain);
		inParam.put("SP_CODE", sp_code);
		inParam.put("BIZ_CODE", biz_code);
		inParam.put("BIZ_TYPE_CODE", biz_type_code);	
		IDataset platSVCList = Dao.qryByCodeParser("TF_F_USER_PLATSVC","SEL_BY_USERID3",inParam);
		return platSVCList;
	}
	/**
	 * 获取对应的 优惠
	 */
	public static IDataset getDiscntByUserId(String user_id,String discnt_code,String relation_type_code) throws Exception{
		IData inParam = new DataMap();
		inParam.put("USER_ID", user_id);
		inParam.put("DISCNT_CODE", discnt_code);
		inParam.put("RELATION_TYPE_CODE", relation_type_code);
		IDataset discntList = Dao.qryByCodeParser("TF_F_USER_DISCNT","SEL_BY_USERID3",inParam);
		return discntList;
	}
	/**
	 * 查询是否存在二次短信确认记录
	 */
	public static IDataset qryTwoSmsCheck(String serial_number,String outer_trade_id) throws Exception{
		IData inParam = new DataMap();
		inParam.put("SERIAL_NUMBER", serial_number);
		inParam.put("OUTER_TRADE_ID", outer_trade_id);
		return Dao.qryByCodeParser("TF_B_TWO_CHECK","SEL_BY_SN3",inParam);
	}
	/**
	 * 实名制查询
	 */
	public static IDataset getCustInfoBySn(String serial_number) throws Exception{
		IData inParam = new DataMap();
		inParam.put("SERIAL_NUMBER", serial_number);
		IDataset resultList = Dao.qryByCodeParser("TF_F_CUSTOMER","SEL_BY_SN3",inParam);
		return resultList;
	}
	/**
	 * 查询服务局数据
	 */
	public static IDataset getPlatSVCBySPInfo(IData inParam) throws Exception{
		IDataset infos = UpcCall.querySpServiceBySpCodeAndBizCodeAndBizStateCode(inParam.getString("SP_CODE",""), inParam.getString("BIZ_CODE",""));
        return infos;
	}
	
	public static IDataset queryPlatSVCInfoBySPInfo(String spCode,
			String bizCode) throws Exception {
		IData inParam = new DataMap();
		inParam.put("SP_CODE", spCode);
		inParam.put("BIZ_CODE", bizCode);
		return Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_SP_INFO", inParam);
	}
	
    public static IDataset getPlatInfoByRelateInstId(IData input) throws Exception
    {
        return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_RELATE_INST_ID", input);
    }
    
	public static IDataset getUserDiscntRelate(String userId, String relationTypeCode, String relate) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		param.put("RSRV_STR4", relate);

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_DISCNT_RELATE", param);
	}
	
	/**
	 * 查询服务局数据
	 */
	public static IDataset getPlatSVCBillTypeByPK(IData inParam) throws Exception{
		return UpcCall.qrySpServiceSpInfo(inParam.getString("SERVICE_ID"),"Z");
	}
	
	/**
	 * 根据主号查询所有的关联副号码,包括已经失效的
	 */
	public static IDataset qryRelationListAll(String user_id,String relation_type_code,String serial_number_b,String orderNo) throws Exception {
		IData inParam = new DataMap();
		inParam.put("USER_ID", user_id);
		inParam.put("SERIAL_NUMBER_B",serial_number_b );
		inParam.put("RELATION_TYPE_CODE", relation_type_code);
		inParam.put("ORDERNO", orderNo);
		IDataset relationList = Dao.qryByCodeParser("TF_F_RELATION_UU","SEL_BY_SN_RELATION_TYPE_CODE_ALL",inParam);
		return relationList;
	}
	
	/**
	 * 根据主号和预留字段5查询所有的关联副号码
	 */
	public static IDataset qryRelationListByRelateInstId(String user_id,String relation_type_code,String relateInstId) throws Exception {
		IData inParam = new DataMap();
		inParam.put("USER_ID", user_id);
		inParam.put("RELATION_TYPE_CODE", relation_type_code);
		inParam.put("RSRV_STR5",relateInstId );
		IDataset relationList = Dao.qryByCodeParser("TF_F_RELATION_UU","SEL_BY_SN_RELATION_TYPE_CODE_RELATE_INST_ID",inParam);
		return relationList;
	}
}