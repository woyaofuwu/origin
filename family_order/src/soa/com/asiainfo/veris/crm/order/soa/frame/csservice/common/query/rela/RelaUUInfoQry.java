package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.URelaRoleInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.URelaTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;

public class RelaUUInfoQry
{
	public static IDataset check_byuserida_idbzm(String userIdB, Pagination pagination) throws Exception
	{

		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select *  FROM TF_F_RELATION_UU ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" and USER_ID_B = to_number(:USER_ID_B) ");
		parser.addSQL(" and partition_id=MOD(to_number(:USER_ID_B),10000)");
		parser.addSQL(" and RELATION_TYPE_CODE =:RELATION_TYPE ");
		parser.addSQL(" AND START_DATE<=SYSDATE ");
		parser.addSQL(" AND END_DATE>SYSDATE ");
		return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
	}

	public static IDataset check_byuserida_idbzm(String userIdB, String relationType, Pagination pagination, String conn) throws Exception
	{

		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);
		param.put("RELATION_TYPE", relationType);

		SQLParser parser = new SQLParser(param);

		parser.addSQL(" select *  FROM TF_F_RELATION_UU ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" and USER_ID_B = to_number(:USER_ID_B) ");
		parser.addSQL(" and partition_id=MOD(to_number(:USER_ID_B),10000)");
		parser.addSQL(" and RELATION_TYPE_CODE =:RELATION_TYPE ");
		parser.addSQL(" AND START_DATE<=SYSDATE ");
		parser.addSQL(" AND END_DATE>SYSDATE ");

		return Dao.qryByParse(parser, pagination, conn);
	}

	public static IDataset check_byuserida_idbzm_A(String userIdA, String relationType, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("RELATION_TYPE", relationType);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select *  FROM TF_F_RELATION_UU ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" and USER_ID_A = to_number(:USER_ID_A) ");
		parser.addSQL(" and RELATION_TYPE_CODE =:RELATION_TYPE ");
		parser.addSQL(" AND START_DATE<=SYSDATE ");
		parser.addSQL(" AND END_DATE>SYSDATE ");
		return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
	}

	public static IDataset check_byuserida_idbzm_A(String userIdA, String relationType, Pagination pagination, String conn) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("RELATION_TYPE", relationType);

		SQLParser parser = new SQLParser(param);

		parser.addSQL(" select *  FROM TF_F_RELATION_UU ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" and USER_ID_A = to_number(:USER_ID_A) ");
		parser.addSQL(" and RELATION_TYPE_CODE =:RELATION_TYPE ");
		parser.addSQL(" AND START_DATE<=SYSDATE ");
		parser.addSQL(" AND END_DATE>SYSDATE ");

		return Dao.qryByParse(parser, pagination, conn);
	}

	public static IDataset checkMemRelaByUserIdb(String userIdA, String userIdB, String relationType, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("USER_ID_B", userIdB);
		param.put("RELATION_TYPE_CODE", relationType);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT partition_id,to_char(user_id_a) user_id_a,serial_number_a,to_char(user_id_b) user_id_b,serial_number_b,relation_type_code,role_code_a,role_code_b,orderno,short_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(inst_id) inst_id ");
		parser.addSQL(" FROM tf_f_relation_uu where 1=1");
		parser.addSQL(" and user_id_a = TO_NUMBER(:USER_ID_A) ");
		parser.addSQL(" AND relation_type_code = :RELATION_TYPE_CODE");
		parser.addSQL(" and user_id_b = :USER_ID_B");
		parser.addSQL(" and partition_id = mod(to_number(:USER_ID_B),10000)");
		parser.addSQL(" AND sysdate between start_date and end_date ");
		IDataset dataset = Dao.qryByParse(parser, pagination);
		return dataset;
	}

	public static IDataset checkCarServiceMemRelaByUserIdb(String userIdA, String userIdB, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT partition_id,to_char(user_id_a) user_id_a,serial_number_a,to_char(user_id_b) user_id_b,serial_number_b,relation_type_code,role_code_a,role_code_b,orderno,short_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(inst_id) inst_id ");
		parser.addSQL(" FROM tf_f_relation_uu where 1=1");
		parser.addSQL(" AND relation_type_code  in ('28','LC','GS','T5')");
		parser.addSQL(" and user_id_b = :USER_ID_B");
		parser.addSQL(" and partition_id = mod(to_number(:USER_ID_B),10000)");
		parser.addSQL(" AND sysdate between start_date and end_date ");
		IDataset dataset = Dao.qryByParse(parser, pagination);
		return dataset;
	}

	/**
	 * @param inData
	 * @param pagination
	 * @param shortnum
	 * @param conName
	 * @return
	 * @throws Exception
	 */
	public static IDataset CheckVUserShortCode(String userIdA, String shortCode, Pagination pagination, String shortnum, String conName) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("SHORT_CODE", shortCode);

		SQLParser sp = new SQLParser(param);
		sp.addSQL("select count(1) short_count from TF_F_RELATION_UU where 1=1");
		sp.addSQL(" and USER_ID_A=:USER_ID_A");
		sp.addSQL(" and short_code is not null");
		sp.addSQL(" and END_DATE > last_day(trunc(sysdate))+1-1/24/3600");
		sp.addSQL(" and short_code=:SHORT_CODE");
		sp.addSQL(" and relation_type_code='20'");
		sp.addSQL(" and rownum<2");
		return Dao.qryByParse(sp, pagination, conName);
	}

	/**
	 * 查询集团用户下的使用某短号的订购关系信息
	 * 
	 * @param userIdA
	 * @param shortCode
	 * @param relationTypeCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset chkShortCodeByUserIdAAndShortCodeAllCrm(String userIdA, String shortCode, String relationTypeCode) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("USER_ID_A", userIdA);
		inparams.put("SHORT_CODE", shortCode);
		inparams.put("RELATION_TYPE_CODE", relationTypeCode);

		return Dao.qryByCodeParserAllCrm("TF_F_RELATION_UU", "SEL_CHECK_SHORTCODE", inparams, null, false);
	}

	/**
	 * 确定成员为所属集团的VPMN成员
	 */
	public static IDataset fn_isVpmnMEM(String userIdA, String userIdMem, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("USER_ID_B", userIdMem);

		SQLParser parser = new SQLParser(param);

		parser.addSQL(" SELECT * FROM TF_F_RELATION_UU ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" AND USER_ID_A=:USER_ID_A ");
		parser.addSQL(" AND USER_ID_B=:USER_ID_B");
		parser.addSQL(" AND RELATION_TYPE_CODE IN('20','21')");
		parser.addSQL(" AND PARTITION_ID=MOD(to_number(:USER_ID_B), 10000)");
		parser.addSQL(" AND ROWNUM=1");

		return Dao.qryByParse(parser, pagination);
	}

	/**
	 * @Function: getAllFamilyByUserIdA
	 * @Description: 根据userida获取所有的uu关系和所有优惠 信息
	 * @param:参数描述
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: lijm3
	 * @date: 2013-9-5 上午10:17:14 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-9-5 lijm3 v1.0.0 修改原因
	 */
	public static IDataset getAllFamilyByUserIdA(String user_id_a) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", user_id_a);
		return Dao.qryByCodeParser("TF_F_RELATION_UU", "SEL_ALLFAMILY_NEW_1", param);
	}

	/**
	 * 依据user_id_a/relation_type_code 查询所有成员
	 * 
	 * @param userIdA
	 * @param relationTypeCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset getAllMebByUserIdA(String userIdA, String relationTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("RELATION_TYPE_CODE", relationTypeCode);

		return Dao.qryByCodeAllCrm("TF_F_RELATION_UU", "SEL_USER_A_COUNT", param, true);
	}

	/**
	 * 依据user_id_a/relation_type_code 查询所有成员
	 * 
	 * @param inparam
	 * @return
	 * @throws Exception
	 */
	public static IDataset getAllMebByUSERIDA(String userIdA, String relationTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("RELATION_TYPE_CODE", relationTypeCode);

		return Dao.qryByCodeAllCrm("TF_F_RELATION_UU", "SEL_USER_AUU", param, true);
	}

	/**
	 * 依据user_id_a/relation_type_code 查询所有未完工成员
	 * 
	 * @param userIdA
	 * @param relationTypeCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset getAllMebTradeUUByuserIdA(String userIdA, String relationTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("RELATION_TYPE_CODE", relationTypeCode);

		return Dao.qryByCodeAllCrm("TF_B_TRADE_RELATION", "SEL_BY_USER_IDA_COUNT", param, true);
	}

	/**
	 * 根据USER_ID获取集团关系表数据
	 */
	public static IDataset getAllRelaByUserIdForGrp(String userId, Pagination page) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_ALL_GROUP_BYID", param, page, Route.CONN_CRM_CG);
	}

	public static IDataset getAllRelationByUidBRelaTypeRoleB(String userIdB, String relationTypeCode, String roleCodeB) throws Exception
	{
		IData relaParam = new DataMap();
		relaParam.put("USER_ID_B", userIdB);
		relaParam.put("RELATION_TYPE_CODE", relationTypeCode);
		relaParam.put("ROLE_CODE_B", roleCodeB);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_ALL_BY_UIDB_RT_RB", relaParam);
	}

	/**
	 * @Function: getAllRelationUserIDAByUserID
	 * @Description: 查询用户办理的生效失效亲情业务userida
	 * @param:参数描述
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: lijm3
	 * @date: 2013-9-5 上午9:57:47 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-9-5 lijm3 v1.0.0 修改原因
	 */
	public static IDataset getAllRelationUserIDAByUserID(String userid) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userid);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_ALLFAMILYUA_BYSB_1", param);
	}

	/**
	 * 据userIda和relation_type_code查询所有字不需要分页
	 * 
	 * @author fengsl
	 * @version 创建时间：2013-04-19
	 * @param inparams
	 * @return
	 * @throws Exception
	 */

	public static IDataset getAllRelaUUInfoByUserIda(String userIdA, String relationTypeCode) throws Exception
	{

		IData inparams = new DataMap();
		inparams.put("USER_ID_A", userIdA);
		inparams.put("RELATION_TYPE_CODE", relationTypeCode);

		if (CSBizBean.getVisit().getProvinceCode().equals(ProvinceUtil.HNAN))
		{
			return Dao.qryByCode("TF_F_RELATION_UU", "SEL_COMP_BY_USERA", inparams);
		} else
		{
			return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_A_YHT", inparams);
		}
	}

	/**
	 * 根据userIda和relation_type_code查询所有字段
	 * 
	 * @author luoyong
	 * @version 创建时间：2010-8-16
	 */
	public static IDataset getAllRelaUUInfoByUserIda(String userIdA, String relationTypeCode, Pagination page) throws Exception
	{

		IData inparams = new DataMap();
		inparams.put("USER_ID_A", userIdA);
		inparams.put("RELATION_TYPE_CODE", relationTypeCode);
		if (CSBizBean.getVisit().getProvinceCode().equals(ProvinceUtil.HNAN))
		{
			return Dao.qryByCode("TF_F_RELATION_UU", "SEL_COMP_BY_USERA", inparams);
		} else
		{
			return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_A_YHT", inparams);
		}
	}

	/**
	 * 宽带子账号开户 查询主账号下所有子账号
	 * 
	 * @param param
	 * @return IDataset
	 * @throws Exception
	 * @author huangsl
	 */
	public static IDataset getAllSubAcct(String userIdB, String relaTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userIdB);
		param.put("RELATION_TYPE_CODE", relaTypeCode);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USERUU_BY_USERIDB", param);
	}

	/**
	 * 根据USER_ID_A查询所有有效的关系数据
	 * 
	 * @param userIdA
	 * @return
	 * @throws Exception
	 */
	public static IDataset getAllValidRelaByUserIDA(String userIdA) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_ALL_VALID_BY_USER_IDA", param);
	}

	/**
	 * 查询所有库下面闭合群成员列表
	 * 
	 * @param userIdA
	 * @param relationTypeCode
	 * @param whereSub
	 * @return
	 * @throws Exception
	 */
	public static IDataset getCloseGrpMemAllDb(String userIdA, String relationTypeCode, String whereSub) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("USER_ID_A", userIdA);
		inparams.put("RELATION_TYPE_CODE", relationTypeCode);

		SQLParser parser = new SQLParser(inparams);

		parser.addSQL(" select a.partition_id,to_char(a.user_id_a) user_id_a,a.serial_number_a,to_char(a.user_id_b) user_id_b,a.serial_number_b,a.relation_type_code,a.role_code_a,a.role_code_b,a.orderno,a.short_code, ");
		parser.addSQL(" to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,a.inst_id,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5 ");
		parser.addSQL(" from tf_F_relation_uu a");
		parser.addSQL(" WHERE 1 = 1 ");
		parser.addSQL(" AND a.RELATION_TYPE_CODE = :RELATION_TYPE_CODE ");
		parser.addSQL(" and a.USER_ID_B = to_number(:USER_ID_B)");
		parser.addSQL(" and a.partition_id = mod(to_number(:USER_ID_B),10000)");
		parser.addSQL(" AND a.START_DATE <= SYSDATE ");
		parser.addSQL(" AND a.END_DATE > SYSDATE ");
		parser.addSQL(" AND a.USER_ID_A = to_number(:USER_ID_A) ");

		// 附加条件
		// String whereSub = data.getString("USER_ID_LIST", "");

		if (!whereSub.equals(""))
		{
			parser.addSQL(" AND a.USER_ID_A IN (" + whereSub + ") ");
		}

		IDataset dataset = Dao.qryByParseAllCrm(parser, true);
		return dataset;
	}

	public static IDataset getEnableRelationByUidBRelaTypeRoleB(String userIdB, String relationTypeCode, String roleCodeB) throws Exception
	{
		IData relaParam = new DataMap();
		relaParam.put("USER_ID_B", userIdB);
		relaParam.put("RELATION_TYPE_CODE", relationTypeCode);
		relaParam.put("ROLE_CODE_B", roleCodeB);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_BY_UIDB_RELATYPE_ROLEB", relaParam);
	}

	public static IDataset getEnableRelationUusByUserIdBTypeCode(String userIdB, String relationTypeCode) throws Exception
	{

		IData iparam = new DataMap();
		iparam.put("USER_ID_B", userIdB);
		iparam.put("RELATION_TYPE_CODE", relationTypeCode);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_IDB_RELA_TYPE", iparam);
	}

	/**
	 * 取得已经存在的网外信息
	 * 
	 * @param param
	 * @throws Exception
	 */
	public static IDataset getExistGrpOutinfo(String userIdA, String relationTypeCode, String serialNumberB, Pagination pagination) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("USER_ID_A", userIdA);
		inparams.put("RELATION_TYPE_CODE", relationTypeCode);
		inparams.put("SERIAL_NUMBER_B", serialNumberB);

		SQLParser parser = new SQLParser(inparams);

		parser.addSQL(" SELECT SERIAL_NUMBER_A, ");
		parser.addSQL(" SERIAL_NUMBER_B, ");
		parser.addSQL(" USER_ID_A, ");
		parser.addSQL(" USER_ID_B, ");
		parser.addSQL(" SHORT_CODE, ");
		parser.addSQL(" to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date, ");
		parser.addSQL(" to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date ");
		parser.addSQL(" FROM tf_f_relation_uu ");
		parser.addSQL(" WHERE 1 = 1 ");
		parser.addSQL(" AND RELATION_TYPE_CODE =:REATION_TYPE_CODE ");
		parser.addSQL(" AND START_DATE <= SYSDATE ");
		parser.addSQL(" AND END_DATE > SYSDATE ");
		parser.addSQL(" AND USER_ID_A =:USER_ID_A ");
		parser.addSQL(" AND SERIAL_NUMBER_B =:SERIAL_NUMBER_B ");
		parser.addSQL(" AND PARTITION_ID = MOD(USER_ID_B, 10000) ");
		return Dao.qryByParse(parser, pagination);
	}

	/**
	 * 依据user_id_a/relation_type_code 查询是否存在成员
	 * 
	 * @param userIdA
	 * @param relationTypeCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset getExistsByUserIdA(String userIdA, String relationTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		return Dao.qryByCodeAllCrm("TF_F_RELATION_UU", "SEL_EXISTS_BY_USERA", param, false);
	}

	/**
	 * @Function: getFamilyByUserIdA
	 * @Description: 根据userida获取有效的uu关系和优惠 信息
	 * @param:参数描述
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: lijm3
	 * @date: 2013-9-5 上午10:10:52 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-9-5 lijm3 v1.0.0 修改原因
	 */
	public static IDataset getFamilyByUserIdA(String user_id_a) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", user_id_a);
		return Dao.qryByCodeParser("TF_F_RELATION_UU", "SEL_FAMILY_NEW_1", param);
	}

	public static IDataset getFamilyByUserIdA(String user_id_a, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", user_id_a);
		return Dao.qryByCodeParser("TF_F_RELATION_UU", "SEL_FAMILY_NEW_1", param, pagination);
	}

	public static IDataset getFamilyMebByUserIdA(String userIdA) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_BY_USERIDA_FOR_FAMILYNET", param);
	}

	/**
	 * 查询用户是否有集团彩讯
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getGrpColorInfoUser(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userId);
		param.put("RELATION_TYPE_CODE", "73");

		return qryRelaByUserIdBRelaTypeCode(userId, "73", null);
	}

	/**
	 * 根据USER_ID_A、成员短号查询成员关系
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 * @author chenlei
	 */
	public static IDataset getGrpMebByShort(IData data) throws Exception
	{

		String eparchyCode = data.getString("EPARCHY_CODE");
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_BY_UIDA_SHORT", data, eparchyCode);
	}

	/**
	 * 根据USER_ID_A、成员号码查询成员关系
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 * @author chenlei
	 */
	public static IDataset getGrpMebBySNUIdA(IData data) throws Exception
	{

		String eparchyCode = data.getString("EPARCHY_CODE");
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_BY_USERIDA_SN", data, eparchyCode);
	}

	/**
	 * 根据USER_ID_A、成员号码查询成员关系
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 * @author chenlei
	 */
	public static IDataset getGrpMebBySNUIdA(String userIdA, String serialNumber, Pagination page, String routeId) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("USER_ID_A", userIdA);
		inparams.put("SERIAL_NUMBER", serialNumber);

		// getVisit().setRouteEparchyCode( eparchyCode);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_BY_USERIDA_SN", inparams, page, routeId);
	}

	/**
	 * @description:查询用户订购的集团产品
	 * @author wusf
	 * @date 2010-5-14
	 * @param inparam
	 * @return
	 * @throws Exception
	 */
	public static IDataset getGrpMebOrderInfo(String serialNumber, Pagination page, String routeId) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("SERIAL_NUMBER", serialNumber);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_MEMORDER_BY_SN", inparams, page, routeId);
	}

	/**
	 * 查询集团用户的网外信息
	 * 
	 * @param param
	 * @throws Exception
	 */
	public static IDataset getGrpOutinfo(String userIdA, Pagination pagination) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("USER_ID_A", userIdA);

		SQLParser parser = new SQLParser(inparams);
		parser.addSQL(" SELECT SERIAL_NUMBER_A, ");
		parser.addSQL(" SERIAL_NUMBER_B, ");
		parser.addSQL(" USER_ID_A, ");
		parser.addSQL(" USER_ID_B, ");
		parser.addSQL(" SHORT_CODE, ");
		parser.addSQL(" to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date, ");
		parser.addSQL(" to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date ");
		parser.addSQL(" FROM tf_f_relation_uu ");
		parser.addSQL(" WHERE 1 = 1 ");
		parser.addSQL(" AND RELATION_TYPE_CODE = 'VO' ");
		parser.addSQL(" AND START_DATE <= SYSDATE ");
		parser.addSQL(" AND END_DATE > SYSDATE ");
		parser.addSQL(" AND USER_ID_A = :USER_ID_A ");
		return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
	}

	/**
	 * 查询集团用户的网外信息
	 * 
	 * @param param
	 * @throws Exception
	 */
	public static IDataset getGrpOutinfo(String userIdA, Pagination pagination, String conn) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("USER_ID_A", userIdA);

		SQLParser parser = new SQLParser(inparams);

		parser.addSQL(" SELECT SERIAL_NUMBER_A, ");
		parser.addSQL(" SERIAL_NUMBER_B, ");
		parser.addSQL(" USER_ID_A, ");
		parser.addSQL(" USER_ID_B, ");
		parser.addSQL(" SHORT_CODE, ");
		parser.addSQL(" to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date, ");
		parser.addSQL(" to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date ");
		parser.addSQL(" FROM tf_f_relation_uu ");
		parser.addSQL(" WHERE 1 = 1 ");
		parser.addSQL(" AND RELATION_TYPE_CODE = 'VO' ");
		parser.addSQL(" AND START_DATE <= SYSDATE ");
		parser.addSQL(" AND END_DATE > SYSDATE ");
		parser.addSQL(" AND USER_ID_A = :USER_ID_A ");

		return Dao.qryByParse(parser, pagination, conn);
	}

	public static IDataset getGrpRelaUUInfoByUserIda(String userIdA, String relationTypeCode) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID_A", userIdA);
		inparam.put("RELATION_TYPE_CODE", relationTypeCode);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_A", inparam, Route.CONN_CRM_CG);
	}

	/**
	 * 获取UU关系
	 * 
	 * @param user_id_b
	 * @param relation_type_code
	 * @return
	 * @throws Exception
	 */
	public static IDataset getGrpRelaUUInfoByUserIdBAndRelaTypeCode(String user_id_b, String relation_type_code) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", user_id_b);
		param.put("RELATION_TYPE_CODE", relation_type_code);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_IDB_ALL", param, Route.CONN_CRM_CG);
	}

	/**
	 * 获取UU关系
	 * 
	 * @param user_id_b
	 * @param relation_type_code
	 * @return
	 * @throws Exception
	 */
	public static IDataset getGrpRelaUUInfoByUserIdBAndRelaTypeCode(String user_id_b, String relation_type_code, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", user_id_b);
		param.put("RELATION_TYPE_CODE", relation_type_code);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_IDB_ALL", param, routeId);
	}

	/**
	 * 查询用户订购集团V网产品
	 * 
	 * @author fengsl
	 * @date 2013-02-26
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset getGrpVOrderInfoByNumber(String userIdA, String groupId, String productId, Pagination pagination) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("USER_ID_A", userIdA);
		inparams.put("GROUP_ID", groupId);
		inparams.put("PRODUCT_ID", productId);

		SQLParser parser = new SQLParser(inparams);
		parser.addSQL(" Select mu.user_id,uu.user_id_a, cg.group_id,cg.group_contact_phone PHONE,cg.cust_name GROUP_CUST_NAME,cp.cust_name MEM_CUST_NAME,pcg.product_id,pcg.product_name GROUP_PRODUCT_NAME,pmb.product_name MEM_PRODUCT_NAME,mu.serial_number MEM_SERIAL_NUMBER,uu.short_code SHORT_NUMBER,scg.data_name ROL_TYPE,smb.data_name ROL_NAME ");
		parser.addSQL(" From tf_f_cust_group cg,tf_f_user cgu,td_s_static scg,td_b_product pcg,tf_f_relation_uu uu,tf_f_user mu,tf_f_cust_person cp,td_s_static smb,td_b_product pmb ");
		parser.addSQL(" Where 1=1 ");
		parser.addSQL(" And uu.serial_number_b=:SERIAL_NUMBER ");
		// parser.addSQL(" And uu.relation_type_code='20' ");
		parser.addSQL("And (uu.relation_type_code='20' Or uu.relation_type_code='21' ) ");
		parser.addSQL(" And uu.User_Id_a=cgu.user_id ");
		parser.addSQL(" And cgu.cust_id=cg.cust_id ");
		parser.addSQL(" And uu.user_id_b=mu.user_id ");
		parser.addSQL(" And cp.cust_id=mu.cust_id ");
		parser.addSQL(" And cgu.remove_tag='0' ");
		parser.addSQL(" And mu.remove_tag='0' ");
		parser.addSQL(" And uu.end_date>last_day(trunc(sysdate))+1-1/24/3600 ");
		// parser.addSQL(" And scg.type_id='TD_S_RELATION_ROLE_1_20' ");
		parser.addSQL(" And scg.type_id='TD_S_RELATION_ROLE_1_'||uu.relation_type_code ");
		parser.addSQL(" And scg.data_id=uu.role_code_a ");
		// parser.addSQL(" And smb.type_id='TD_S_RELATION_ROLE_1_20' ");
		parser.addSQL(" And smb.type_id='TD_S_RELATION_ROLE_1_'||uu.relation_type_code ");
		parser.addSQL(" And smb.data_id=uu.role_code_b ");
		parser.addSQL(" And pcg.product_id=cgu.product_id ");
		parser.addSQL(" And pmb.product_id=mu.product_id ");
		parser.addSQL(" And uu.User_Id_a=:USER_ID_A ");
		parser.addSQL(" And cg.GROUP_ID=:GROUP_ID ");
		parser.addSQL(" And cgu.PRODUCT_ID=:PRODUCT_ID ");
		return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);

	}

	/**
	 * @Description:根据user_id_a统计该集团下的VPMN成员数
	 * @author wusf
	 * @date 2009-12-5
	 * @param userIdA
	 * @return
	 * @throws Exception
	 */
	public static IData getGrpVpmnMebCount(String userIdA, Pagination pagination) throws Exception
	{

		IData inparam = new DataMap();
		inparam.put("USER_ID_A", userIdA);
		IDataset infos = Dao.qryByCode("TF_F_RELATION_UU", "SEL_MEBMUN_BY_USERIDA", inparam, pagination);
		if (infos != null && infos.size() > 0)
		{
			return infos.getData(0);
		} else
		{
			return new DataMap();
		}
	}

	/**
	 * @Function: getInvalidFamilyByUserIdA
	 * @Description: 根据userida获取有效的uu关系和所有优惠 信息
	 * @param:参数描述
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: lijm3
	 * @date: 2013-9-5 上午10:15:40 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-9-5 lijm3 v1.0.0 修改原因
	 */
	public static IDataset getInvalidFamilyByUserIdA(String user_id_a) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", user_id_a);
		return Dao.qryByCodeParser("TF_F_RELATION_UU", "SEL_INVALIDFAMILY_NEW_1", param);
	}

	public static IDataset getMebRelaCoutByUserIdA(String userIdA, String relationTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("RELATION_TYPE_CODE", relationTypeCode);

		IDataset result = Dao.qryByCodeAllCrm("TF_F_RELATION_UU", "SEL_COUNT_UU_USERIDA", param, true);

		return result;
	}

	/**
	 * 根据USER_ID_B、PRODUCT_ID_A、集团CUST_ID、RELATION_TYPE_CODE获取成员已订购组合产品相关数据
	 */
	/*public static IDataset getMebRelaProductComp(String custId, String userIdB, String productIdA, Pagination pagination) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("CUST_ID", custId);
		inparam.put("USER_ID_B", userIdB);
		inparam.put("PRODUCT_ID_A", productIdA);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_MEB_PRODUCT_COMP", inparam, pagination);
	}*/

	/**
	 * 取集团成员关系
	 */
	public static IDataset getMemberUserRelation(IData inparams) throws Exception
	{
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_BY_PK_1", inparams);
	}

	/**
	 * 集团成员关系查询 通过用户A的用户标识和用户B的服务号码及关系类型获取用户与用户的关系
	 * 
	 * @param serialNumberA
	 * @param serialNumberB
	 * @param relationTypeCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset getMemberUserRelation(String serialNumberA, String serialNumberB, String relationTypeCode, String routeId) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("SERIAL_NUMBER_A", serialNumberA);
		inparam.put("SERIAL_NUMBER_B", serialNumberB);
		inparam.put("RELATION_TYPE_CODE", relationTypeCode);

		SQLParser parser = new SQLParser(inparam);
		parser.addSQL(" SELECT SERIAL_NUMBER_B,SERIAL_NUMBER_A,uu.USER_ID_A , uu.user_id_b, ");
		parser.addSQL(" RELATION_TYPE_CODE,ROLE_CODE_A,ROLE_CODE_B,SHORT_CODE,START_DATE,END_DATE ");
		parser.addSQL(" FROM tf_f_relation_uu uu  ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" AND SERIAL_NUMBER_A=:SERIAL_NUMBER_A ");
		parser.addSQL(" AND SERIAL_NUMBER_B=:SERIAL_NUMBER_B ");
		parser.addSQL(" AND uu.relation_type_code = :RELATION_TYPE_CODE ");
		parser.addSQL(" AND sysdate BETWEEN start_date AND end_date ");
		parser.addSQL(" AND end_date>last_day(trunc(sysdate))+1-1/24/3600 ");
		IDataset ds = Dao.qryByParse(parser, routeId);
		if (IDataUtil.isNotEmpty(ds))
		{
			for (int i = 0; i < ds.size(); i++)
			{
				IData data = ds.getData(i);
				data.put("RELATION_TYPE_NAME", URelaTypeInfoQry.getRoleTypeNameByRelaTypeCode(data.getString("RELATION_TYPE_CODE")));
			}
		}
		return ds;
	}

	/**
	 * @Function: getMemForOther
	 * @Description: 查询不包括传入userid的uu数据
	 * @param userIdA
	 * @param userIdB
	 * @param relationTypeCode
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: sunxin
	 */
	public static IDataset getMemForOther(String user_id_a, String user_id_b, String relation_type_code) throws Exception
	{

		IData param = new DataMap();
		param.put("USER_ID_A", user_id_a);
		param.put("USER_ID_B", user_id_b);
		param.put("RELATION_TYPE_CODE", relation_type_code);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT a.partition_id, ");
		parser.addSQL(" to_char(a.user_id_a) user_id_a, ");
		parser.addSQL(" a.serial_number_a, ");
		parser.addSQL(" to_char(a.user_id_b) user_id_b, ");
		parser.addSQL(" a.serial_number_b, ");
		parser.addSQL(" a.relation_type_code, ");
		parser.addSQL(" a.role_code_a, ");
		parser.addSQL(" a.role_code_b, ");
		parser.addSQL(" a.orderno, ");
		parser.addSQL(" a.short_code, ");
		parser.addSQL(" to_char(a.start_date, 'yyyy-mm-dd hh24:mi:ss') start_date, ");
		parser.addSQL(" to_char(a.end_date, 'yyyy-mm-dd hh24:mi:ss') end_date, ");
		parser.addSQL(" a.inst_id, ");
		parser.addSQL(" a.rsrv_str1, ");
		parser.addSQL(" a.rsrv_str2, ");
		parser.addSQL(" a.rsrv_str3, ");
		parser.addSQL(" a.rsrv_str4, ");
		parser.addSQL(" a.rsrv_str5 ");
		parser.addSQL(" FROM tf_F_relation_uu a ");
		parser.addSQL(" WHERE 1 = 1 ");
		parser.addSQL(" AND a.user_id_a = to_number(:USER_ID_A) ");
		parser.addSQL(" AND a.USER_ID_B <> to_number(:USER_ID_B) ");
		parser.addSQL(" AND a.partition_id <> MOD(to_number(:USER_ID_B), 10000) ");
		parser.addSQL(" AND a.START_DATE <= SYSDATE ");
		parser.addSQL(" AND a.END_DATE > SYSDATE ");
		parser.addSQL(" AND a.RELATION_TYPE_CODE = :RELATION_TYPE_CODE ");
		return Dao.qryByParse(parser);
	}

	/**
	 * 查询集团成员的网外信息
	 * 
	 * @param param
	 * @throws Exception
	 */
	public static IDataset getMemOutGrp(String serialNumberA, Pagination pagination) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("SERIAL_NUMBER_A", serialNumberA);

		SQLParser parser = new SQLParser(inparam);
		parser.addSQL(" SELECT  SERIAL_NUMBER_B,USER_ID_A,serial_number_a, SHORT_CODE,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date FROM tf_f_relation_uu  ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" AND RELATION_TYPE_CODE='MO'");
		parser.addSQL(" AND START_DATE<=SYSDATE ");
		parser.addSQL(" AND END_DATE>SYSDATE ");
		parser.addSQL(" AND SERIAL_NUMBER_A=:SERIAL_NUMBER_A ");
		return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
	}

	/**
	 * @Function: getMemOutGrp
	 * @Description: 查询集团成员的网外信息
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午10:17:36 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset getMemOutGrp(String user_id_a, String user_id_b, String relation_type_code, String serial_number_b, String serial_number_a, Pagination pagination) throws Exception
	{

		IData param = new DataMap();
		param.put("USER_ID_A", user_id_a);
		param.put("USER_ID_B", user_id_b);
		param.put("RELATION_TYPE_CODE", relation_type_code);
		param.put("SERIAL_NUMBER_B", serial_number_b);
		param.put("SERIAL_NUMBER_A", serial_number_a);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT a.partition_id, ");
		parser.addSQL(" to_char(a.user_id_a) user_id_a, ");
		parser.addSQL(" a.serial_number_a, ");
		parser.addSQL(" to_char(a.user_id_b) user_id_b, ");
		parser.addSQL(" a.serial_number_b, ");
		parser.addSQL(" a.relation_type_code, ");
		parser.addSQL(" a.role_code_a, ");
		parser.addSQL(" a.role_code_b, ");
		parser.addSQL(" a.orderno, ");
		parser.addSQL(" a.short_code, ");
		parser.addSQL(" to_char(a.start_date, 'yyyy-mm-dd hh24:mi:ss') start_date, ");
		parser.addSQL(" to_char(a.end_date, 'yyyy-mm-dd hh24:mi:ss') end_date, ");
		parser.addSQL(" a.inst_id, ");
		parser.addSQL(" a.rsrv_str1, ");
		parser.addSQL(" a.rsrv_str2, ");
		parser.addSQL(" a.rsrv_str3, ");
		parser.addSQL(" a.rsrv_str4, ");
		parser.addSQL(" a.rsrv_str5 ");
		parser.addSQL(" FROM tf_F_relation_uu a ");
		parser.addSQL(" WHERE 1 = 1 ");
		parser.addSQL(" AND a.user_id_a = to_number(:USER_ID_A) ");
		parser.addSQL(" AND a.USER_ID_B = to_number(:USER_ID_B) ");
		parser.addSQL(" AND a.partition_id = MOD(to_number(:USER_ID_B), 10000) ");
		parser.addSQL(" AND a.START_DATE <= SYSDATE ");
		parser.addSQL(" AND a.END_DATE > SYSDATE ");
		parser.addSQL(" AND a.RELATION_TYPE_CODE = :RELATION_TYPE_CODE ");
		parser.addSQL(" AND a.SERIAL_NUMBER_B = :SERIAL_NUMBER_B ");
		parser.addSQL(" AND a.SERIAL_NUMBER_A = :SERIAL_NUMBER_A ");
		return Dao.qryByParse(parser, pagination);
	}

	/**
	 * 查询成员网外号码UU关系
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static IDataset getMemOutGrpNumber(String userIdA, String userIdB, String serialNumberA, String serialNumberB, String relationTypeCode, Pagination pagination) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID_A", userIdA);
		inparam.put("USER_ID_B", userIdB);
		inparam.put("SERIAL_NUMBER_A", serialNumberA);
		inparam.put("SERIAL_NUMBER_B", serialNumberB);
		inparam.put("RELATION_TYPE_CODE", relationTypeCode);

		SQLParser parser = new SQLParser(inparam);

		parser.addSQL(" SELECT c.cust_name mem_cust_name, ");
		parser.addSQL(" a.partition_id, ");
		parser.addSQL(" to_char(a.user_id_a) user_id_a, ");
		parser.addSQL(" a.serial_number_a, ");
		parser.addSQL(" to_char(a.user_id_b) user_id_b, ");
		parser.addSQL(" a.serial_number_b, ");
		parser.addSQL(" a.relation_type_code, ");
		parser.addSQL(" a.role_code_a, ");
		parser.addSQL(" a.role_code_b, ");
		parser.addSQL(" a.orderno, ");
		parser.addSQL(" a.short_code, ");
		parser.addSQL(" to_char(a.start_date, 'yyyy-mm-dd hh24:mi:ss') start_date, ");
		parser.addSQL(" to_char(a.end_date, 'yyyy-mm-dd hh24:mi:ss') end_date, ");
		parser.addSQL(" a.inst_id, ");
		parser.addSQL(" a.rsrv_str1, ");
		parser.addSQL(" a.rsrv_str2, ");
		parser.addSQL(" a.rsrv_str3, ");
		parser.addSQL(" a.rsrv_str4, ");
		parser.addSQL(" a.rsrv_str5 ");
		parser.addSQL(" FROM tf_F_relation_uu a, tf_F_user b, TF_F_CUST_PERSON c ");
		parser.addSQL(" WHERE 1 = 1 ");
		parser.addSQL(" AND c.cust_id = b.cust_id ");
		parser.addSQL(" AND c.partition_id = MOD(to_number(b.cust_id), 10000) ");
		parser.addSQL(" AND b.USER_ID=a.USER_ID_A ");
		parser.addSQL(" AND a.user_id_a = to_number(:USER_ID_A) ");
		parser.addSQL(" AND a.USER_ID_B = to_number(:USER_ID_B) ");
		parser.addSQL(" AND a.partition_id = MOD(to_number(:USER_ID_B), 10000) ");
		parser.addSQL(" AND a.START_DATE <= SYSDATE ");
		parser.addSQL(" AND a.END_DATE > SYSDATE ");
		parser.addSQL(" AND a.RELATION_TYPE_CODE = :RELATION_TYPE_CODE ");
		parser.addSQL(" AND a.SERIAL_NUMBER_B = :SERIAL_NUMBER_B ");
		parser.addSQL(" AND a.SERIAL_NUMBER_A = :SERIAL_NUMBER_A ");

		return Dao.qryByParse(parser, pagination);
	}

	/**
	 * 通过母vpmn取该母下的所有短号信息
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset getMotherVpmnShortCode(IData param, Pagination pagination) throws Exception
	{
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT r.SHORT_CODE,'未用' STATE ");
		parser.addSQL(" FROM (select to_char(SHORT_CODE) SHORT_CODE from td_b_vshortcode ");
		parser.addSQL(" where 1 = 1 ");
		parser.addSQL(" and (contain_4= :CONTAIN_4 OR :CONTAIN_4 IS NULL) ");
		parser.addSQL(" and (contain_7= :CONTAIN_7 OR :CONTAIN_7 IS NULL) ");
		parser.addSQL(" and (SHORT_CODE LIKE '6'|| :SHORT_LENGTH ||'%' OR :SHORT_LENGTH IS NULL)");
		parser.addSQL(" and (length = :SHORT_TYPE OR :SHORT_TYPE IS NULL) ");
		parser.addSQL(" minus select SHORT_CODE from TF_F_RELATION_UU A "); // 根据母vpmn的userid，查出所有子vpmn的成员短号
		parser.addSQL(" where 1 = 1 ");
		parser.addSQL(" AND exists(select 1 from TF_F_USER_VPN C where C.VPN_NO = A.SERIAL_NUMBER_A ) ");
		parser.addSQL(" AND A.RELATION_TYPE_CODE = '20' ");
		parser.addSQL(" AND A.END_DATE > SYSDATE ");
		parser.addSQL(" AND length(A.SHORT_CODE)= :SHORT_TYPE ");
		parser.addSQL(" AND exists(SELECT 1 ");
		parser.addSQL(" FROM tf_f_user_vpn vpn,tf_f_relation_uu uu ");
		parser.addSQL(" WHERE vpn.USER_ID=uu.user_id_b ");
		parser.addSQL(" AND uu.user_id_a = to_number( :USER_ID_A) ");
		parser.addSQL(" and vpn.PARTITION_ID=uu.partition_id ");
		parser.addSQL(" AND uu.relation_type_code = '40' ");
		parser.addSQL(" AND uu.end_date>SYSDATE ");
		parser.addSQL(" and vpn.Remove_Tag <> '1' and uu.user_id_b=A.USER_ID_A) ");
		parser.addSQL(" minus ");
		parser.addSQL(" select SHORT_CODE ");
		parser.addSQL(" from TF_F_RELATION_UU uua "); // 根据母vpmn的userid，查出所有母vpmn的网外号码短号
		parser.addSQL(" where USER_ID_A=to_number(:USER_ID_A) ");
		parser.addSQL(" and sysdate>=start_date ");
		parser.addSQL(" and sysdate<=end_date ");
		parser.addSQL(" and relation_type_code = '41' ");
		parser.addSQL(" AND length(uua.SHORT_CODE)= :SHORT_TYPE ");
		parser.addSQL(" minus ");
		parser.addSQL(" select SHORT_CODE "); // 根据母vpmn的userid，查出所有子vpmn的网外号码短号
		parser.addSQL(" from TF_F_RELATION_UU uua ");
		parser.addSQL(" where 1 = 1 ");
		parser.addSQL(" and sysdate>=start_date  ");
		parser.addSQL(" and sysdate<=end_date  ");
		parser.addSQL(" and relation_type_code = '41' ");
		parser.addSQL(" AND length(uua.SHORT_CODE)= :SHORT_TYPE ");
		parser.addSQL(" AND exists(SELECT 1 ");
		parser.addSQL(" FROM tf_f_user_vpn vpn,tf_f_relation_uu uu ");
		parser.addSQL(" WHERE vpn.USER_ID=uu.user_id_b ");
		parser.addSQL(" AND uu.user_id_a = to_number( :USER_ID_A) ");
		parser.addSQL(" and vpn.PARTITION_ID=uu.partition_id ");
		parser.addSQL(" AND uu.relation_type_code = '40' ");
		parser.addSQL(" AND uu.end_date>SYSDATE ");
		parser.addSQL(" and vpn.Remove_Tag <> '1' and uua.USER_ID_A = uu.USER_ID_B) ) r ");

		IDataset resultset = Dao.qryByParse(parser, pagination);
		if (IDataUtil.isNotEmpty(resultset))
		{
			for (int i = 0, ilen = resultset.size(); i < ilen; i++)
			{
				IData resultData = resultset.getData(i);
				resultData.put("VPN_NAME", param.getString("VPN_NAME"));
				resultData.put("SERIAL_NUMBER_A", param.getString("SERIAL_NUMBER_M"));
			}
		}
		return resultset;

	}

	/**
	 * 通过母vpmn取该母下的所有短号信息
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset getMotherVpmnShortCodeExport(IData param, Pagination pagination) throws Exception
	{
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT r.SHORT_CODE,'未用' STATE ");
		parser.addSQL(" FROM (select to_char(SHORT_CODE) SHORT_CODE from td_b_vshortcode ");
		parser.addSQL(" where 1 = 1 ");
		parser.addSQL(" and (contain_4= :CONTAIN_4 OR :CONTAIN_4 IS NULL) ");
		parser.addSQL(" and (contain_7= :CONTAIN_7 OR :CONTAIN_7 IS NULL) ");
		parser.addSQL(" and (SHORT_CODE LIKE '6'|| :SHORT_LENGTH ||'%' OR :SHORT_LENGTH IS NULL)");
		parser.addSQL(" and (length = :SHORT_TYPE OR :SHORT_TYPE IS NULL) ");
		parser.addSQL(" minus select SHORT_CODE from TF_F_RELATION_UU A "); // 根据母vpmn的userid，查出所有子vpmn的成员短号
		parser.addSQL(" where 1 = 1 ");
		parser.addSQL(" AND exists(select 1 from TF_F_USER_VPN C where C.VPN_NO = A.SERIAL_NUMBER_A ) ");
		parser.addSQL(" AND A.RELATION_TYPE_CODE = '20' ");
		parser.addSQL(" AND A.END_DATE > SYSDATE ");
		parser.addSQL(" AND length(A.SHORT_CODE)= :SHORT_TYPE ");
		parser.addSQL(" AND exists(SELECT 1 ");
		parser.addSQL(" FROM tf_f_user_vpn vpn,tf_f_relation_uu uu ");
		parser.addSQL(" WHERE vpn.USER_ID=uu.user_id_b ");
		parser.addSQL(" AND uu.user_id_a = to_number( :USER_ID_A) ");
		parser.addSQL(" and vpn.PARTITION_ID=uu.partition_id ");
		parser.addSQL(" AND uu.relation_type_code = '40' ");
		parser.addSQL(" AND uu.end_date>SYSDATE ");
		parser.addSQL(" and vpn.Remove_Tag <> '1' and uu.user_id_b=A.USER_ID_A) ");
		parser.addSQL(" minus ");
		parser.addSQL(" select SHORT_CODE ");
		parser.addSQL(" from TF_F_RELATION_UU uua "); // 根据母vpmn的userid，查出所有母vpmn的网外号码短号
		parser.addSQL(" where USER_ID_A=to_number(:USER_ID_A) ");
		parser.addSQL(" and sysdate>=start_date ");
		parser.addSQL(" and sysdate<=end_date ");
		parser.addSQL(" and relation_type_code = '41' ");
		parser.addSQL(" AND length(uua.SHORT_CODE)= :SHORT_TYPE ");
		parser.addSQL(" minus ");
		parser.addSQL(" select SHORT_CODE "); // 根据母vpmn的userid，查出所有子vpmn的网外号码短号
		parser.addSQL(" from TF_F_RELATION_UU uua ");
		parser.addSQL(" where 1 = 1 ");
		parser.addSQL(" and sysdate>=start_date  ");
		parser.addSQL(" and sysdate<=end_date  ");
		parser.addSQL(" and relation_type_code = '41' ");
		parser.addSQL(" AND length(uua.SHORT_CODE)= :SHORT_TYPE ");
		parser.addSQL(" AND exists(SELECT 1 ");
		parser.addSQL(" FROM tf_f_user_vpn vpn,tf_f_relation_uu uu ");
		parser.addSQL(" WHERE vpn.USER_ID=uu.user_id_b ");
		parser.addSQL(" AND uu.user_id_a = to_number( :USER_ID_A) ");
		parser.addSQL(" and vpn.PARTITION_ID=uu.partition_id ");
		parser.addSQL(" AND uu.relation_type_code = '40' ");
		parser.addSQL(" AND uu.end_date>SYSDATE ");
		parser.addSQL(" and vpn.Remove_Tag <> '1' and uua.USER_ID_A = uu.USER_ID_B) ) r ");

		IDataset resultset = Dao.qryByParse(parser, pagination, Route.getCrmDefaultDb());
		if (IDataUtil.isNotEmpty(resultset))
		{
			for (int i = 0, ilen = resultset.size(); i < ilen; i++)
			{
				IData resultData = resultset.getData(i);
				resultData.put("VPN_NAME", param.getString("VPN_NAME"));
				resultData.put("SERIAL_NUMBER_A", param.getString("SERIAL_NUMBER_M"));
			}
		}
		return resultset;

	}

	//
	public static IDataset getNormalMemNumAllCrm(String userId) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID_A", userId);

		SQLParser parser = new SQLParser(params);
		parser.addSQL(" SELECT COUNT(1) MEM_NUM  ");
		parser.addSQL(" FROM TF_F_RELATION_UU A, TF_F_USER B  ");
		parser.addSQL(" WHERE A.USER_ID_B = B.USER_ID   AND A.USER_ID_A = :USER_ID_A ");
		parser.addSQL(" AND B.USER_TYPE_CODE <> 'E'  AND B.USER_TYPE_CODE <> 'F'  AND B.USER_STATE_CODESET = '0' ");
		parser.addSQL(" AND sysdate between A.start_date and A.end_date ");
		return Dao.qryByParseAllCrm(parser, true);
	}

	/**
	 * 根据子集团或母集团服务号码查询集团用户老的子母集团关系
	 * 
	 * @author zengzb
	 * @param param
	 * @throws Exception
	 */
	public static IDataset getOldSmgrpinfo(String userIdA, String userIdB, Pagination pagination) throws Exception
	{

		IData inparam = new DataMap();
		inparam.put("USER_ID_A", userIdA);
		inparam.put("USER_ID_B", userIdB);

		SQLParser parser = new SQLParser(inparam);
		parser.addSQL(" SELECT c.group_id, c.cust_id, ");
		parser.addSQL(" e.group_id son_group_id, ");
		parser.addSQL(" c.cust_name, ");
		parser.addSQL(" b.EPARCHY_CODE, ");
		parser.addSQL(" e.cust_name son_cust_name, ");
		parser.addSQL(" a.USER_ID_A, ");
		parser.addSQL(" a.SERIAL_NUMBER_A, ");
		parser.addSQL(" a.USER_ID_B, ");
		parser.addSQL(" a.SERIAL_NUMBER_B, ");
		parser.addSQL(" to_char(a.start_date, 'yyyy-mm-dd hh24:mi:ss') start_date, ");
		parser.addSQL(" to_char(a.end_date, 'yyyy-mm-dd hh24:mi:ss') end_date ");
		parser.addSQL(" FROM tf_f_relation_uu a, ");
		parser.addSQL(" tf_f_user        b, ");
		parser.addSQL(" tf_f_cust_group  c, ");
		parser.addSQL(" tf_f_user        d, ");
		parser.addSQL(" tf_f_cust_group  e ");
		parser.addSQL(" WHERE 1 = 1 ");
		parser.addSQL(" AND b.user_id = a.user_id_a ");
		parser.addSQL(" AND b.PARTITION_ID = MOD(a.USER_ID_A, 10000) ");
		parser.addSQL(" AND b.cust_id = c.cust_id ");
		parser.addSQL(" AND d.user_id = a.user_id_b ");
		parser.addSQL(" AND d.PARTITION_ID = MOD(a.USER_ID_b, 10000) ");
		parser.addSQL(" AND d.cust_id = e.cust_id ");
		parser.addSQL(" AND a.RELATION_TYPE_CODE = 'ZM' ");
		parser.addSQL(" AND a.START_DATE <= SYSDATE ");
		parser.addSQL(" AND a.END_DATE > SYSDATE ");
		parser.addSQL(" AND a.USER_ID_A = :USER_ID_A ");
		parser.addSQL(" AND a.USER_ID_B = :USER_ID_B ");
		parser.addSQL(" AND a.PARTITION_ID = MOD(:USER_ID_B, 10000) ");

		return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
	}

	/**
	 * 根据子集团或母集团服务号码查询集团用户老的子母集团关系
	 * 
	 * @author zengzb
	 * @param param
	 * @throws Exception
	 */
	public static IDataset getOldSmgrpinfo(String userIdA, String userIdB, Pagination pagination, String conn) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID_A", userIdA);
		inparam.put("USER_ID_B", userIdB);

		SQLParser parser = new SQLParser(inparam);

		parser.addSQL(" SELECT c.group_id, c.cust_id, ");
		parser.addSQL(" e.group_id son_group_id, ");
		parser.addSQL(" c.cust_name, ");
		parser.addSQL(" e.cust_name son_cust_name, ");
		parser.addSQL(" a.USER_ID_A, ");
		parser.addSQL(" a.SERIAL_NUMBER_A, ");
		parser.addSQL(" a.USER_ID_B, ");
		parser.addSQL(" a.SERIAL_NUMBER_B, ");
		parser.addSQL(" to_char(a.start_date, 'yyyy-mm-dd hh24:mi:ss') start_date, ");
		parser.addSQL(" to_char(a.end_date, 'yyyy-mm-dd hh24:mi:ss') end_date ");
		parser.addSQL(" FROM tf_f_relation_uu a, ");
		parser.addSQL(" tf_f_user        b, ");
		parser.addSQL(" tf_f_cust_group  c, ");
		parser.addSQL(" tf_f_user        d, ");
		parser.addSQL(" tf_f_cust_group  e ");
		parser.addSQL(" WHERE 1 = 1 ");
		parser.addSQL(" AND b.user_id = a.user_id_a ");
		parser.addSQL(" AND b.PARTITION_ID = MOD(a.USER_ID_A, 10000) ");
		parser.addSQL(" AND b.cust_id = c.cust_id ");
		parser.addSQL(" AND d.user_id = a.user_id_b ");
		parser.addSQL(" AND d.PARTITION_ID = MOD(a.USER_ID_b, 10000) ");
		parser.addSQL(" AND d.cust_id = e.cust_id ");
		parser.addSQL(" AND a.RELATION_TYPE_CODE = 'ZM' ");
		parser.addSQL(" AND a.START_DATE <= SYSDATE ");
		parser.addSQL(" AND a.END_DATE > SYSDATE ");
		parser.addSQL(" AND a.USER_ID_A = :USER_ID_A ");
		parser.addSQL(" AND a.USER_ID_B = :USER_ID_B ");
		parser.addSQL(" AND a.PARTITION_ID = MOD(:USER_ID_B, 10000) ");

		return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
	}

	public static IDataset getOutPhoneList(String groupId, Pagination pagination) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("GROUP_ID", groupId);

		SQLParser parser = new SQLParser(inparam);
		parser.addSQL(" select user_id_a OUT_GROUP_ID,user_id_b,serial_number_b PHONE_CODE,rsrv_str1 NET_TYPE_CODE,to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE,to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE ");
		parser.addSQL(" from tf_f_relation_uu ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" and user_id_a = to_number(:GROUP_ID) ");
		parser.addSQL(" and RELATION_TYPE_CODE ='03' ");
		parser.addSQL(" AND START_DATE<=SYSDATE ");
		parser.addSQL(" AND END_DATE>SYSDATE ");

		return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
	}

	public static IDataset getOutPhoneList(String groupId, Pagination pagination, String conn) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("GROUP_ID", groupId);

		SQLParser parser = new SQLParser(inparam);

		parser.addSQL(" select user_id_a OUT_GROUP_ID,user_id_b,serial_number_b PHONE_CODE,rsrv_str1 NET_TYPE_CODE,to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE,to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE ");
		parser.addSQL(" from tf_f_relation_uu ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" and user_id_a = to_number(:GROUP_ID) ");
		parser.addSQL(" and RELATION_TYPE_CODE ='03' ");
		parser.addSQL(" AND START_DATE<=SYSDATE ");
		parser.addSQL(" AND END_DATE>SYSDATE ");

		return Dao.qryByParse(parser, pagination, conn);
	}

	/**
	 * 查询合同资料和RELATION_UU
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getPactBankAcctNo(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_PACT_BANKACCTNO", param);
	}

	/**
	 * 集团成员的长短号互查
	 * 
	 * @param paraCode1
	 * @param paraCode2
	 * @param paraCode3
	 * @return
	 * @throws Exception
	 */

	public static IDataset getRelaAndVpnInfoBySnOrShortcode(String paraCode1, String paraCode2, String paraCode3) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("PARA_CODE1", paraCode1);
		inparam.put("PARA_CODE2", paraCode2);
		inparam.put("PARA_CODE3", paraCode3);

		IDataset ds = Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_VPMN_BY_SHORTCODE", inparam);
		return ds;
	}

	public static IData getRelaByPK(String userIdA, String userIdB, String relationTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("USER_ID_B", userIdB);
		param.put("RELATION_TYPE_CODE", relationTypeCode);

		IDataset result = Dao.qryByCode("TF_F_RELATION_UU", "SEL_BY_PK", param);

		return result.size() > 0 ? result.getData(0) : null;
	}

	/**
	 * 查询所有成员，不包括删除成员@yanwu
	 * 
	 * @param userIdA
	 * @param role_code_b
	 * @param relationTypeCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset getRelaByGood(String userIdA, String role_code_b, String relationTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("RELATION_TYPE_CODE", relationTypeCode);

		IDataset result = Dao.qryByCode("TF_F_RELATION_UU", "SEL_RELATION_GOOD", param);

		return result;
	}

	/**
	 * 根据USER_ID_B、RELATION_TYPE_CODE获取关系表数据
	 */
	public static IDataset getRelaByUserIdA(String userIdA, Pagination pagination) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID_A", userIdA);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_ROLE_IDA", inparam, Route.CONN_CRM_CG);
	}

	/**
	 * 根据userId、relationTypeCode查uu关系
	 * 
	 * @param userId
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset getRelaByUserIdAndRelaTypeCode(String user_id_b, String relation_type_code) throws Exception
	{
		IDataset result = new DatasetList();
		IData param = new DataMap();
		param.put("USER_ID_B", user_id_b);
		param.put("RELATION_TYPE_CODE", relation_type_code);
		if ("E1".equalsIgnoreCase(relation_type_code))
		{
			result = Dao.qryByCodeParser("TF_F_RELATION_UU", "SEL_USERRELA_BY_IDB", param);
		} else if ("FG".equalsIgnoreCase(relation_type_code))
		{
			param.put("ROLE_CODE_B", "2");
			result = Dao.qryByCodeParser("TF_F_RELATION_UU", "SEL_USER_IDA", param);
		}

		return result;
	}

	public static IDataset getRelaByUserIdAndRelaTypeCode(String user_id_b, String rela_type_code, String role_code_b) throws Exception
	{
		IData relaParam = new DataMap();
		relaParam.put("USER_ID_B", user_id_b);
		relaParam.put("RELATION_TYPE_CODE", rela_type_code);
		relaParam.put("ROLE_CODE_B", role_code_b);

		IDataset result = Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_IDA_HAIN", relaParam);

		return result;
	}

	public static IDataset getRelaByUserIdbAndRelaTypeCode(String userIdB, String relaTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);
		param.put("RELATION_TYPE_CODE", relaTypeCode);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_ONE_USERIDA_BY_USERIDB", param);
	}

	/**
	 * 根据USER_ID_B、RELATION_TYPE_CODE获取关系表数据
	 */
	public static IDataset getRelaByUserIdBForSqlff(String userIdB, Pagination pagination) throws Exception
	{

		IData data = new DataMap();
		data.put("USER_ID_B", userIdB);

		SQLParser parser = new SQLParser(data);

		parser.addSQL(" SELECT UU.USER_ID_A, ");
		parser.addSQL(" UU.RELATION_TYPE_CODE, ");
		parser.addSQL(" TO_CHAR(UU.START_DATE, 'yyyy-MM-dd hh24:mi:ss') START_DATE, ");
		parser.addSQL(" TO_CHAR(UU.END_DATE, 'yyyy-MM-dd hh24:mi:ss') END_DATE ");
		parser.addSQL(" FROM TF_F_RELATION_UU UU ");
		parser.addSQL(" WHERE 1 = 1 ");
		parser.addSQL(" AND UU.USER_ID_B = TO_NUMBER(:USER_ID_B) ");
		parser.addSQL(" AND UU.PARTITION_ID = MOD(TO_NUMBER(:USER_ID_B), 10000) ");
		parser.addSQL(" AND SYSDATE BETWEEN UU.START_DATE AND UU.END_DATE ");

		IDataset idsDataset = Dao.qryByParse(parser, pagination);

		return idsDataset;
	}

	/**
	 * todo getVisit().setRouteEparchyCode(eparchyCode);怎么处理
	 * 根据USER_ID_B、RELATION_TYPE_CODE获取关系表数据
	 */
	public static IDataset getRelaByUserIdBRsrvdate2(IData inparam, String eparchyCode) throws Exception
	{

		// TODO getVisit().setRouteEparchyCode(eparchyCode);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USERIDB_RSRVDATE2", inparam);
	}

	/**
	 * 成员当月订购次数
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static int getRelaCountThisMonthByUserIdbAndRelaTypeCode(String userIdB, String relaTypeCode, String routeId) throws Exception
	{
		int num = 0;
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);
		param.put("RELATION_TYPE_CODE", relaTypeCode);
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select count(1) RECORDCOUNT ");
		parser.addSQL(" from tf_f_relation_uu uu  ");
		parser.addSQL(" where uu.partition_id = mod(to_number(:USER_ID_B),10000)  ");
		parser.addSQL(" and uu.user_id_b=:USER_ID_B  ");
		parser.addSQL(" and uu.relation_type_code=:RELATION_TYPE_CODE ");
		parser.addSQL(" and uu.start_date > TRUNC(sysdate,'MM') ");
		IDataset ids = Dao.qryByParse(parser, routeId);
		if (IDataUtil.isNotEmpty(ids))
		{
			num = ids.getData(0).getInt("RECORDCOUNT");
		}
		return num;
	}

	public static IDataset getRelaCoutByPK(String userIdA, String relationTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("RELATION_TYPE_CODE", relationTypeCode);

		IDataset result = Dao.qryByCode("TF_F_RELATION_UU", "SEL_COUNT_UU_USERIDA", param);

		return result;
	}

	// 查询统一付费副卡号码
	public static IDataset getRelaFKByUserIdB(String userIdB, String relationTypeCode, String roleCodeB) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		param.put("ROLE_CODE_B", roleCodeB);

		IDataset result = Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_IDA_HAIN", param);

		return result;
	}

	/**
	 * 根据user_id_b和product_id 查uu关系
	 * 
	 * @param user_id_b
	 * @param product_id
	 * @return
	 * @throws Exception
	 */
	public static IDataset getRelaInfoByUserIdbAndProId(String user_id_b, String product_id) throws Exception
	{
		IData param = new DataMap();
		param.put("PRODUCT_ID", "8000");
		param.put("USER_ID_B", user_id_b);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_BY_GrpMebUU", param);
	}

	public static IDataset getRelaInfoByUserIdbAndRelaTypeCode(String userIdB, String relaTypeCode, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);
		param.put("RELATION_TYPE_CODE", relaTypeCode);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_VPMN_MEB_ADDCOUNT", param, routeId);
	}

	public static IDataset getRelatInfosBySelAllGroupbyIdB(String user_id, String product_id) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("PRODUCT_ID", product_id);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_ALL_GROUP_BYIDB", param);
	}

	public static IDataset getRelatInfosBySelAllGroupbyIdBNoenddate(String user_id, String product_id) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("PRODUCT_ID", product_id);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_ALL_GROUP_BYIDB_NOENDDATE", param);
	}

	public static IDataset getRelatInfosBySelFmymemberInfo(String user_id_a) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", user_id_a);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_FMYMEMBER_INFO", param);
	}

	/**
	 * @Function: getRelatInfosBySelHisrelation
	 * @Description: 注意不要给它TD_S_CPARAM骗了，SEL_HISRELATION这个确实查UU表
	 * @param:参数描述
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: lijm3
	 * @date: 2013-7-25 下午3:15:31 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-7-25 lijm3 v1.0.0 修改原因
	 */
	public static IDataset getRelatInfosBySelHisrelation(String user_id_a, String role_code_b, String relation_type_code) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", user_id_a);
		param.put("RELATION_TYPE_CODE", relation_type_code);
		param.put("ROLE_CODE_B", role_code_b);

		return Dao.qryByCode("TD_S_CPARAM", "SEL_HISRELATION", param);
	}

	public static IDataset getRelatInfosBySelUserIdA(String user_id_b, String relation_type_code, String role_code_b) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", user_id_b);
		param.put("RELATION_TYPE_CODE", relation_type_code);
		param.put("ROLE_CODE_B", role_code_b);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_IDA", param);
	}

	public static IDataset getRelatInfosBySelVicecardInfo(String user_id_a) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", user_id_a);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_VICECARD_INFO", param);
	}

	public static IDataset getRelatInfosByUserIdRelatTypeCode(String user_id, String relationTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", user_id);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_BY_USERID_RELATIONTYPECODE", param);
	}

	public static IData getRelationByInstId(String instId) throws Exception
	{
		IData param = new DataMap();
		param.put("INST_ID", instId);

		IDataset result = Dao.qryByCode("TF_F_RELATION_UU", "SEL_BY_INSTID", param);

		return result.size() > 0 ? result.getData(0) : null;
	}

	// add yangsh6
	public static IDataset getRelationsByUserIdA(String relationTypeCode, String userIdA, String roleCodeB) throws Exception
	{
		IData param = new DataMap();
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		param.put("USER_ID_A", userIdA);
		param.put("ROLE_CODE_B", roleCodeB);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_ROLE_IDA_OPNC", param);
	}

	public static IDataset getRelationsByUserIdAndTypeAndRoleCodeB(String relationTypeCode, String userIdB, String roleCodeB) throws Exception
	{
		IData param = new DataMap();
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		param.put("USER_ID_B", userIdB);
		param.put("ROLE_CODE_B", roleCodeB);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_ROLE_IDBHAI", param);
	}

	/**
	 * @author yanwu
	 * @param relationTypeCode
	 * @param userIdB
	 * @param roleCodeB
	 * @return
	 * @throws Exception
	 */
	public static IDataset getRelationsByCount(String relationTypeCode, String userIdB, String roleCodeB) throws Exception
	{
		IData param = new DataMap();
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		param.put("USER_ID_B", userIdB);
		param.put("ROLE_CODE_B", roleCodeB);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_ROLE_COUNT", param);
	}

	/**
	 * 集团下没有短号的成员
	 * 
	 * @param userIdA
	 * @param relationType
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset getRelationShortCode60ByUserIdARelationgType(String userIdA, String relationType) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("RELATION_TYPE_CODE", relationType);
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select count(1)  NUM from tf_f_relation_uu ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" and USER_ID_A = to_number(:USER_ID_A) ");
		parser.addSQL(" and RELATION_TYPE_CODE =:RELATION_TYPE_CODE ");
		parser.addSQL(" AND end_date > sysdate  ");
		parser.addSQL(" AND substr(short_code,1,2) ='60' ");
		return Dao.qryByParseAllCrm(parser, true);
	}

	/**
	 * 集团下短号以6打头的成员
	 * 
	 * @param userIdA
	 * @param relationType
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset getRelationShortCode6ByUserIdARelationgType(String userIdA, String relationType) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("RELATION_TYPE_CODE", relationType);
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select count(1)  NUM from tf_f_relation_uu ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" and USER_ID_A = to_number(:USER_ID_A) ");
		parser.addSQL(" and RELATION_TYPE_CODE =:RELATION_TYPE_CODE ");
		parser.addSQL(" AND end_date > sysdate  ");
		parser.addSQL(" AND substr(short_code,1,1) !='6' ");
		return Dao.qryByParseAllCrm(parser, true);
	}

	/**
	 * 集团下没有短号的成员
	 * 
	 * @param userIdA
	 * @param relationType
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset getRelationShortCodeIsNullByUserIdARelationgType(String userIdA, String relationType) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("RELATION_TYPE_CODE", relationType);
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select count(1) NUM from tf_f_relation_uu ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" and USER_ID_A = to_number(:USER_ID_A) ");
		parser.addSQL(" and RELATION_TYPE_CODE =:RELATION_TYPE_CODE ");
		parser.addSQL(" AND end_date > sysdate  ");
		parser.addSQL(" AND trim(short_code) is null ");
		return Dao.qryByParseAllCrm(parser, true);
	}

	/**
	 * 集团下长度不符合的短号的成员
	 * 
	 * @param userIdA
	 * @param relationType
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset getRelationShortLenIsErrByUserIdARelationgType(String userIdA, String relationType) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("RELATION_TYPE_CODE", relationType);
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select count(1) NUM from tf_f_relation_uu ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" and USER_ID_A = to_number(:USER_ID_A) ");
		parser.addSQL(" and RELATION_TYPE_CODE =:RELATION_TYPE_CODE ");
		parser.addSQL(" AND end_date > sysdate  ");
		parser.addSQL(" AND (length(short_code) < 3 or length(short_code) > 6) ");
		return Dao.qryByParseAllCrm(parser, true);
	}

	/**
	 * @Function: getRelationUserIDAByUserID
	 * @Description:查询用户办理的生效亲情业务userida
	 * @param:参数描述
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: lijm3
	 * @date: 2013-9-5 上午9:56:06 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-9-5 lijm3 v1.0.0 修改原因
	 */
	public static IDataset getRelationUserIDAByUserID(String userid) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userid);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_FAMILYUA_BYSB_1", param);
	}

	/**
	 * 查询UU表中是否存在当前月份中注销的号码
	 * 
	 * @param pd
	 * @param td
	 * @return
	 * @throws Exception
	 */
	public static IDataset getRelationUU(IData param) throws Exception
	{
		// TODO
		String sql = param.getString("sql", "");

		return Dao.qryByCode("TF_F_RELATION_UU", sql, param);
	}

	/**
	 * 根据user_id_a,user_id_b,relation_type_code查询uu表
	 */
	public static IData getRelationUUByPk(String userIdA, String userIdB, String relationTypeCode, Pagination pagination) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID_A", userIdA);
		inparam.put("USER_ID_B", userIdB);
		inparam.put("RELATION_TYPE_CODE", relationTypeCode);

		IDataset datas = qryUU(userIdA, userIdB, relationTypeCode, pagination, null);
		if (datas == null || datas.size() == 0)
		{
			return null;
		} else
		{
			return datas.getData(0);
		}
	}

	public static IDataset getRelationUUByTimeUidb(String userId, String endDate, Pagination pagination) throws Exception
	{

		IData inparam = new DataMap();
		inparam.put("USER_ID", userId);
		inparam.put("END_DATE", endDate);

		SQLParser parser = new SQLParser(inparam);
		parser.addSQL(" select count(1) LAST_TIME_UU FROM TF_F_RELATION_UU uu ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" and uu.user_id_b = to_number(:USER_ID) ");
		parser.addSQL(" AND uu.PARTITION_ID=MOD(to_number(:USER_ID), 10000) ");
		parser.addSQL(" and uu.RELATION_TYPE_CODE ='20' ");
		parser.addSQL(" AND uu.END_DATE=to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss') ");
		return Dao.qryByParse(parser, pagination);
	}

	/**
	 * 通过USERIDB和relation_type_code查询
	 * 
	 * @param params
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset getRelationUUByUserB(String userIdA, String userIdB, String relationTypeCode, Pagination pagination, String routeId) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID_A", userIdA);
		inparam.put("USER_ID_B", userIdB);
		inparam.put("RELATION_TYPE_CODE", relationTypeCode);
		// getVisit().setRouteEparchyCode( eparchyCode);
		IDataset result = Dao.qryByCodeParser("TF_F_RELATION_UU", "SEL_BY_IDAB", inparam, pagination, routeId);
		return result;
	}

	public static IDataset getRelationUUbYUserIDa(String userIdA, String userIdB, String serialCumberB, String relationType, Pagination pagination) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID_A", userIdA);
		inparam.put("USER_ID_B", userIdB);
		inparam.put("SERIAL_NUMBER_B", serialCumberB);
		inparam.put("RELATION_TYPE", relationType);

		SQLParser parser = new SQLParser(inparam);
		parser.addSQL(" select partition_id,to_char(user_id_a) user_id_a,serial_number_a,to_char(user_id_b) user_id_b,serial_number_b,relation_type_code,role_code_a,role_code_b,orderno,short_code, ");
		parser.addSQL(" to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,inst_id,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5 ");
		parser.addSQL(" FROM TF_F_RELATION_UU ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" and user_id_a = to_number(:USER_ID_A) ");
		parser.addSQL(" and USER_ID_B = to_number(:USER_ID_B) ");
		parser.addSQL(" and partition_id=MOD(to_number(:USER_ID_B),10000)");
		parser.addSQL(" and serial_number_b = to_number(:SERIAL_NUMBER_B) ");
		parser.addSQL(" and RELATION_TYPE_CODE =:RELATION_TYPE ");
		parser.addSQL(" AND START_DATE<=SYSDATE ");
		parser.addSQL(" AND END_DATE>SYSDATE ");
		return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
	}

	public static IDataset getRelationUUbYUserIDa2(String userId, Pagination pagination) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID", userId);

		SQLParser parser = new SQLParser(inparam);

		parser.addSQL(" select partition_id,to_char(user_id_a) user_id_a,serial_number_a,to_char(user_id_b) user_id_b,serial_number_b,relation_type_code,role_code_a,role_code_b,orderno,short_code, ");
		parser.addSQL(" to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,inst_id ");
		parser.addSQL(" FROM TF_F_RELATION_UU ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" and user_id_a = to_number(:USER_ID) ");
		parser.addSQL(" and RELATION_TYPE_CODE ='04' ");
		parser.addSQL(" AND START_DATE<=SYSDATE ");
		parser.addSQL(" AND END_DATE>SYSDATE ");

		return Dao.qryByParse(parser, pagination);
	}

	public static IDataset getRelationUUInfo(String userId, String userIdA, Pagination pagination) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID", userId);
		inparam.put("USER_ID_A", userIdA);

		SQLParser parser = new SQLParser(inparam);

		parser.addSQL(" select * FROM TF_F_RELATION_UU uu ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" and uu.user_id_b = to_number(:USER_ID) ");
		parser.addSQL(" and uu.user_id_a = to_number(:USER_ID_A)");
		parser.addSQL(" AND uu.PARTITION_ID=MOD(to_number(:USER_ID), 10000) ");
		parser.addSQL(" and uu.RELATION_TYPE_CODE ='20' ");
		parser.addSQL(" AND uu.END_DATE>last_day(trunc(sysdate))+1-1/24/3600");

		return Dao.qryByParse(parser, pagination);
	}

	// 查询该副卡号码的relation_uu记录
	public static IDataset getRelationUUInfoByDeputySn(String userIdB, String relationTypeCode, Pagination pagination) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID_B", userIdB);
		inparam.put("RELATION_TYPE_CODE", relationTypeCode);

		IDataset relationUUInfo = Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_IDB_ALL", inparam);
		return relationUUInfo;
	}

	public static IDataset getRelationUusBySnBTypeCode(String snB, String relationTypeCode) throws Exception
	{
		IData iparam = new DataMap();
		iparam.put("SERIAL_NUMBER_B", snB);
		iparam.put("RELATION_TYPE_CODE", relationTypeCode);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_RELATION_BY_SNB_TYPE", iparam);
	}

	public static IDataset getRelationUusByUserIdBTypeCode(String userIdB, String relationTypeCode) throws Exception
	{
		IData iparam = new DataMap();
		iparam.put("USER_ID_B", userIdB);
		iparam.put("RELATION_TYPE_CODE", relationTypeCode);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_IDB_RELA_TYPE", iparam);
	}

	public static IDataset getRelationUusByUserSnRole(String serialNumberB, String relationTypeCode, String roleCodeB) throws Exception
	{
		IData iparam = new DataMap();
		iparam.put("SERIAL_NUMBER_B", serialNumberB);
		iparam.put("RELATION_TYPE_CODE", relationTypeCode);
		iparam.put("ROLE_CODE_B", roleCodeB);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_SN_ROLE", iparam);
	}

	/**
	 * 查集团下校园卡成员
	 * 
	 * @param userIdA
	 * @param relationType
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset getRelationXykByUserIdARelationgType(String userIdA, String relationType) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("RELATION_TYPE_CODE", relationType);
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select count(1) NUM from tf_f_relation_uu a,tf_f_user b,tf_F_user_product c ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" and b.user_id=c.user_id ");
		parser.addSQL(" and a.user_id_b=b.user_id ");
		parser.addSQL(" and c.partition_id=mod(a.user_id_b,10000) ");
		parser.addSQL(" and a.partition_id=mod(a.user_id_b,10000) ");
		parser.addSQL(" and b.partition_id=mod(a.user_id_b,10000) ");
		parser.addSQL(" and a.USER_ID_A = to_number(:USER_ID_A) ");
		parser.addSQL(" and a.RELATION_TYPE_CODE =:RELATION_TYPE_CODE ");
		parser.addSQL(" AND a.end_date > sysdate  ");
		parser.addSQL(" AND c.end_date > sysdate  ");
		parser.addSQL(" AND b.remove_tag='0' ");
		parser.addSQL(" AND c.product_id in (10001005,10001139) ");
		return Dao.qryByParseAllCrm(parser, true);
	}

	public static IDataset getRelatsBySNB(String serial_number_b) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER_B", serial_number_b);
		return Dao.qryByCodeParser("TF_F_RELATION_UU", "SEL_ALL_BY_SNB", param);
	}

	public static IDataset getRelatsByUserIdA(String user_id_a) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", user_id_a);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_ALL_BY_USERIDA_NODATE", param);
	}

	public static IDataset getRelatsByUserIdADate(String user_id_a) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", user_id_a);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_BY_USERIDA_DATE", param);
	}

	/**
	 * 根据userIdA和relationTypeCode统计UU关系表中的数量
	 * 
	 * @param userIdA
	 * @param relationTypeCode
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public static IDataset getRelaUUCountByUserIdARela(String userIdA, String relationTypeCode, Pagination page) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID_A", userIdA);
		inparam.put("RELATION_TYPE_CODE", relationTypeCode);

		return Dao.qryByCode("TD_S_CPARAM", "ExistsRelationUUA", inparam, page);
	}

	/**
	 *
	 */
	public static IDataset getRelaUUInfoByCustIdProId(String custId, String productId, Pagination page) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("CUST_ID", custId);
		inparam.put("PRODUCT_ID", productId);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_BY_CUST_ID_PROD_ID", inparam, page);
	}

	/**
	 * 根据user_id_b查询家庭统付关系角色 relationTypeCode =56
	 * 
	 * @param userIdB
	 * @param relationTypeCode
	 * @return
	 * @throws Exception
	 * @CREATE BY GONGP@2014-5-27
	 */
	public static IDataset getRelaUUInfoByRol(String userIdB, String relationTypeCode) throws Exception
	{
		IData iparam = new DataMap();
		iparam.put("USER_ID_B", userIdB);
		iparam.put("RELATION_TYPE_CODE", relationTypeCode);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_UUROL_HAIN", iparam);
	}

	/**
	 * 根据USER_ID_B和RELATION_TYPE_CODE查询UU用户关系表信息
	 */
	public static IDataset getRelaUUInfoByRol(String userIdB, String relationTypeCode, String routeId, Pagination page) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID_B", userIdB);
		inparam.put("RELATION_TYPE_CODE", relationTypeCode);

		// getVisit().setRouteEparchyCode( eparchyCode);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_UUROL", inparam, page, routeId);
	}

	/**
	 * 根据USER_ID_B和RELATION_TYPE_CODE查询UU用户关系表信息
	 */
	public static IDataset getRelaUUInfoByRolForGrp(String userIdB, String relationTypeCode, Pagination page) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID_B", userIdB);
		inparam.put("RELATION_TYPE_CODE", relationTypeCode);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_UUROL", inparam, Route.CONN_CRM_CG);
	}

	/**
	 * 根据USER_ID_A查询UU关系
	 * 
	 * @author tengg
	 * @param inparams
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset getRelaUUInfoByUserida(String userIdA, String routeId) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID_A", userIdA);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_MEMBER_BY_IDA", inparam, routeId);
	}

	/**
	 * 根据userIda和relation_type_code查询
	 * 
	 * @author shixb
	 * @version 创建时间：2009-5-15 下午01:45:12
	 */
	public static IDataset getRelaUUInfoByUserIda(String userIdA, String relationTypeCode, Pagination page) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID_A", userIdA);
		inparam.put("RELATION_TYPE_CODE", relationTypeCode);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_A", inparam, page);
	}

	public static IDataset getRelaUUInfoByUserIdA(String user_id_a, String relation_type_code) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", user_id_a);
		param.put("RELATION_TYPE_CODE", relation_type_code);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_ALL_BY_USERIDA_LAST", param);
	}

	/**
	 * 根据userIda和relation_type_code查询
	 * 
	 * @author
	 * @version 创建时间：2009-5-15 下午01:45:12
	 */
	public static IDataset getRelaUUInfoByUserIdaForGrp(String userIdA, String relationTypeCode, Pagination page) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID_A", userIdA);
		inparam.put("RELATION_TYPE_CODE", relationTypeCode);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_A", inparam, Route.CONN_CRM_CG);
	}

	/**
	 * 根据USER_ID_A查询UU关系
	 * 
	 * @author tengg
	 * @param inparams
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset getRelaUUInfoByUseridB(String userIdB, String routeId, Pagination page) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID_B", userIdB);

		return Dao.qryByCodeParser("TF_F_RELATION_UU", "SEL_LAST_BY_USERIDB", inparam, page, routeId);
	}

	/**
	 * 获取UU关系
	 * 
	 * @param user_id_b
	 * @param relation_type_code
	 * @return
	 * @throws Exception
	 */
	public static IDataset getRelaUUInfoByUserIdBAndRelaTypeCode(String user_id_b, String relation_type_code) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", user_id_b);
		param.put("RELATION_TYPE_CODE", relation_type_code);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_IDB_ALL", param);
	}

	public static IDataset getRelaUUInfoByUserIdBAndRelaTypeCode(String user_id_b, String relation_type_code, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", user_id_b);
		param.put("RELATION_TYPE_CODE", relation_type_code);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_IDB_ALL", param, routeId);
	}

	public static IDataset getRelaUUInfoByUserIdBAndSDate(String user_id_b, String relation_type_code, String start_date, String end_date) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", user_id_b);
		param.put("RELATION_TYPE_CODE", relation_type_code);
		param.put("START_DATE", start_date);
		param.put("END_DATE", end_date);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_UU_BY_USERID_SDATE", param);
	}

	/**
	 * 根据USER_ID_B查询UU关系
	 * 
	 * @author fuzn
	 * @date 2013-03-20
	 * @param inparams
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset getRelaUUInfoByUserRelarIdB(String userIdB, String relationTypeCode, Pagination page) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID_B", userIdB);
		inparam.put("RELATION_TYPE_CODE", relationTypeCode);

		return Dao.qryByCodeParser("TF_F_RELATION_UU", "SEL_USERRELA_BY_IDB", inparam, page);
	}

	public static IDataset getRelaUUInfoByUserRelarIdBAllCrm(String userIdB, String relationTypeCode, Pagination page) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID_B", userIdB);
		inparam.put("RELATION_TYPE_CODE", relationTypeCode);

		return Dao.qryByCodeParserAllCrm("TF_F_RELATION_UU", "SEL_USERRELA_BY_IDB", inparam, page, true);
	}

	/**
	 * 通过USER_ID_A查询UU表
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 *             wangjx 2013-3-25
	 */
	public static IDataset getRelaUUOtherByUserIdA(String userIdA, String relationTypeCode, String rsrvValueCode, Pagination page) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID_A", userIdA);
		inparam.put("RELATION_TYPE_CODE", relationTypeCode);
		inparam.put("RSRV_VALUE_CODE", rsrvValueCode);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_UU_OTHER_BY_USERIDA", inparam, page);
	}

	/**
	 * 返回手机是否是主号码代码 为空则不存在一卡多号返回是主号码
	 * 
	 * @param pd
	 * @param param
	 * @return 1： 是 ； 其他：不是
	 * @throws Exception
	 */
	public static String getRoleIdbOneCN(String userIdB, String roleCodeB) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);
		param.put("ROLE_CODE_B", roleCodeB);
		IDataset list = Dao.qryByCode("TF_F_RELATION_UU", "SEL_ROLE_IDB_OneCN", param);
		if (list != null && list.size() > 0)
		{
			return list.getData(0).getString("ROLE_CODE_B");
		} else
			return "1";
	}

	/*
	 * 查询UU关系，没有判当前时间在start和end之间
	 */
	public static IDataset getSEL_BY_PK1(String userIdA, String userIdB, String relationTypeCode, Pagination page, String routeId) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID_A", userIdA);
		inparam.put("USER_ID_B", userIdB);
		inparam.put("RELATION_TYPE_CODE", relationTypeCode);

		return Dao.qryByCodeParser("TF_F_RELATION_UU", "SEL_BY_PK1", inparam, routeId);
	}

	public static IDataset getSEL_USER_ROLEA(String userIdA, String roleCodeB, String relationTypeCode) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID_A", userIdA);
		inparam.put("ROLE_CODE_B", roleCodeB);
		inparam.put("RELATION_TYPE_CODE", relationTypeCode);

		return Dao.qryByCodeParser("TF_F_RELATION_UU", "SEL_USER_ROLEA", inparam);
	}

	public static IDataset getSEL_USER_ROLEA(String userIdA, String roleCodeB, String relationTypeCode, Pagination page) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID_A", userIdA);
		inparam.put("ROLE_CODE_B", roleCodeB);
		inparam.put("RELATION_TYPE_CODE", relationTypeCode);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_ROLEA", inparam, page);
	}

	/*
	 * 根据USER_ID_A、RELATION_TYPE_CODE、ROLE_CODE_B获取关系表数据
	 */
	public static IDataset getSEL_USER_ROLEA(String userIdA, String roleCodeB, String relationTypeCode, Pagination page, String routeId) throws Exception
	{

		IData inparam = new DataMap();
		inparam.put("USER_ID_A", userIdA);
		inparam.put("ROLE_CODE_B", roleCodeB);
		inparam.put("RELATION_TYPE_CODE", relationTypeCode);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_ROLEA", inparam, page, routeId);
	}

	/**
	 * 根据集团用户ID和短号长度自动生成短号
	 * 
	 * @param userIdA
	 *            集团用户ID
	 * @param shortCodeLen
	 *            短号长度
	 * @return
	 * @throws Exception
	 */
	public static IDataset getShortCodeByUserIdA(String userIdA, int shortCodeLen) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID_A", userIdA);
		inparam.put("SHORT_CODE_LEN", shortCodeLen);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_MINSHORTCODE_BY_USERIDA", inparam);
	}

	/**
	 * 根据USER_ID_B、PRODUCT_ID_A、集团CUST_ID、RELATION_TYPE_CODE获取成员已订购组合产品相关数据
	 */
	public static IDataset getSubProductByUserIdA(String userIdA, String custId, String relationTypeCode, Pagination page) throws Exception
	{

		IData params = new DataMap();
		params.put("USER_ID_A", userIdA);
		params.put("CUST_ID", custId);
		params.put("RELATION_TYPE_CODE", relationTypeCode);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_BBOSS_SUB_BY_USERIDA", params, page);
	}

	/**
	 * 根据USER_ID_B、PRODUCT_ID_A、集团CUST_ID、RELATION_TYPE_CODE获取成员已订购组合产品相关数据
	 */
	public static IDataset getSubProductByUserIdAForGrp(String userIdA, String custId, String relationTypeCode, Pagination page) throws Exception
	{

		IData params = new DataMap();
		params.put("USER_ID_A", userIdA);
		params.put("CUST_ID", custId);
		params.put("RELATION_TYPE_CODE", relationTypeCode);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_BBOSS_SUB_BY_USERIDA", params, page, Route.CONN_CRM_CG);
	}

	/**
	 * @param user_id_a
	 * @return
	 * @throws Exception
	 * @author wangww3
	 */
	public static IDataset getTdRelatInfo(String userIdA) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_TDNUMBER_INFO", param);
	}

	/**
	 * @param userIdB
	 * @param relationTypeCode
	 * @return
	 * @throws Exception
	 * @author wangww
	 */
	public static IDataset getTdRelatInfos(String userIdB, String relationTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_TDINFONUMBER_INFO_BY_USERIDB", param);
	}

	/**
	 * @Function: getUserFamilyRelations
	 * @Description: 该函数的功能描述
	 * @param:参数描述
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: lijm3
	 * @date: 2013-10-20 下午9:27:00 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-10-20 lijm3 v1.0.0 修改原因
	 */
	public static IDataset getUserFamilyRelations(String user_id) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		SQLParser sp = new SQLParser(param);
		sp.addSQL("select a.user_id_a,a.user_id_b, b.relation_type_name, a.role_code_b, a.orderno, to_char(a.start_date, 'yyyy-mm-dd hh24:mi:ss') start_date, to_char(a.end_date, 'yyyy-mm-dd hh24:mi:ss') end_date,to_char(a.update_time, 'yyyy-mm-dd hh24:mi:ss') update_time, a.remark ");
		sp.addSQL("  from tf_f_relation_uu a, td_s_relation b ");
		sp.addSQL(" where a.relation_type_code = b.relation_type_code ");
		sp.addSQL("   and a.end_date > sysdate and b.relation_kind = 'F' ");
		sp.addSQL("   and a.user_id_b = :USER_ID ");
		sp.addSQL("   and a.partition_id = mod(:USER_ID,10000) ");
		return Dao.qryByParse(sp);
	}

	/**
	 * @Function: getUserFamilyRelationsByIdA
	 * @Description: 该函数的功能描述
	 * @param:参数描述
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: lijm3
	 * @date: 2013-10-20 下午9:28:32 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-10-20 lijm3 v1.0.0 修改原因
	 */
	public static IDataset getUserFamilyRelationsByIdA(String user_id_a) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", user_id_a);
		SQLParser sp = new SQLParser(param);

		sp.addSQL("select a.user_id_a, a.user_id_b, c.serial_number, b.relation_type_name, a.role_code_b, a.orderno, to_char(a.start_date, 'yyyy-mm-dd hh24:mi:ss') start_date, to_char(a.end_date, 'yyyy-mm-dd hh24:mi:ss') end_date, to_char(a.update_time, 'yyyy-mm-dd hh24:mi:ss') update_time, a.remark ");
		sp.addSQL("  from TF_F_RELATION_UU a, td_s_relation b, tf_f_user c ");
		sp.addSQL(" where a.relation_type_code = b.relation_type_code and a.user_id_b = c.user_id(+) ");
		sp.addSQL("   and a.end_date > sysdate ");
		sp.addSQL("   and a.user_id_a = :USER_ID_A ");
		sp.addSQL(" order by a.orderno ");
		return Dao.qryByParse(sp);
	}

	public static IDataset getUserIdA(String userIdB, String typeCode1, String typeCode2) throws Exception
	{
		IData param = new DataMap();
		param.put("VUSER_ID_B", userIdB);
		param.put("VRELATION_TYPE_CODE1", typeCode1);
		param.put("VRELATION_TYPE_CODE2", typeCode2);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USERIDA_BY_USERIDB", param);
	}

	/**
	 * 查询该成员是否有当月结束的VPCN的uu关系
	 */
	public static IDataset getUserLastRelation(String user_id_b, String product_id) throws Exception
	{
		String relationTypeCode = UProductCompInfoQry.getRelationTypeCodeByProductId(product_id);
		if (StringUtils.isBlank(relationTypeCode))
		{
			CSAppException.apperr(ProductException.CRM_PRODUCT_522, "根据产品ID【" + product_id + "】查询RELATION_TYPE_CODE为空");
		}
		IData param = new DataMap();
		param.put("USER_ID_B", user_id_b);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_LAST_BY_UID_RELATION", param);
	}

	/**
	 * 用户当月订购同一业务次数
	 * 
	 * @param user_id_b
	 * @param relation_type_code
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserOrderInCurMonthByUserIdB(String user_id_b, String relation_type_code) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", user_id_b);
		param.put("RELATION_TYPE_CODE", relation_type_code);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_NOTEXIST_UU_BY_IDB", param);
	}

	public static IDataset getUserRelaByUserIdB(String userIdB) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_BBOSSPRODUCT_USERID", param);
	}

	/**
	 * 根据END_DATE查询用户【TF_F_RELATION_UU】信息
	 * 
	 * @param user_id_b
	 * @param user_id_a
	 *            【传空查USER_ID_A所有】
	 * @param relation_type_code
	 * @param end_date
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserRelaInfoByEndByPK(String user_id_b, String user_id_a, String relation_type_code, String end_date) throws Exception
	{
		IData param = new DataMap();

		if (!"".equals(user_id_b) && user_id_b != null)
		{
			param.put("USER_ID_B", user_id_b);
		}
		if (!"".equals(user_id_a) && user_id_a != null)
		{
			param.put("USER_ID_A", user_id_a);
		}
		if (!"".equals(end_date) && end_date != null)
		{
			param.put("END_DATE", end_date);
		}

		param.put("RELATION_TYPE_CODE", relation_type_code);

		return Dao.qryByCodeParser("TF_F_RELATION_UU", "SEL_BY_ENDDATE_BY_PK", param);
	}

	public static IDataset getUserRelation(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("RELATION_TYPE_CODE", "30");
		param.put("USER_ID_B", userId);

		IDataset userRelationInfos = Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_UUROL", param);
		return userRelationInfos;
	}

	public static IDataset getUserRelationAll(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("RELATION_TYPE_CODE", "30");
		param.put("USER_ID_A", userId);

		IDataset userRelationInfos = Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_AUU", param);
		return userRelationInfos;
	}

	public static IDataset getUserRelationAll(String user_id_a, String relation_type_code) throws Exception
	{
		IData param = new DataMap();
		param.put("RELATION_TYPE_CODE", relation_type_code);
		param.put("USER_ID_A", user_id_a);

		IDataset userRelationInfos = Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_AUU", param);
		return userRelationInfos;
	}

	public static IDataset getUserRelationByBBOSSUserIdA(String user_id_a) throws Exception
	{
		IData param = new DataMap();
		param.put("RELATION_TYPE_CODE", "97");
		param.put("USER_ID_A", user_id_a);

		IDataset userRelationInfos = Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_AUU", param, Route.CONN_CRM_CG);
		return userRelationInfos;
	}

	public static IDataset getUserRelationByIDB(IData params) throws Exception
	{
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USERRELA_BY_IDB", params);
	}

	/**
	 * 取集团成员关系
	 */
	public static IDataset getUserRelationByTradeId(String tradeId) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("TRADE_ID", tradeId);

		return Dao.qryByCode("TF_B_TRADE_RELATION", "SEL_BY_PK", inparams, Route.CONN_CRM_CG);
	}

	/**
	 * 根据USER_ID_B和RELATION_TYPE_CODE查询UU用户关系表信息wangww3
	 */
	public static IDataset getUserRelationByUR(String userId, String relationTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		param.put("USER_ID_B", userId);

		IDataset userRelationInfos = Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_UUROL", param);
		return userRelationInfos;
	}

	public static IDataset getUserRelationByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_BY_USERID", param);
	}

	public static IDataset getUserRelationByUserIDA(String userId, String relationTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		param.put("USER_ID_A", userId);

		IDataset userRelationInfos = Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_AUU", param);
		return userRelationInfos;
	}

	public static IDataset getUserRelationByUserIdB(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userId);

		return Dao.qryByCodeAllCrm("TF_F_RELATION_UU", "SEL_ALL_BY_USERIDB", param, true);
	}

	/**
	 * 根据USER_ID_B和RELATION_TYPE_CODE查询UU用户关系表信息 返回比较全包括 INST_ID
	 * 
	 * @param userIdb
	 * @param relationTypeCode
	 * @return
	 * @throws Exception
	 * @CREATE BY GONGP@2014-6-12
	 */
	public static IDataset getUserRelationByUserIdBRe(String userIdb, String relationTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		param.put("USER_ID_B", userIdb);

		IDataset userRelationInfos = Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_IDB_RELATION", param);
		return userRelationInfos;
	}

	/**
	 * 根据USER_ID_B和RELATION_TYPE_CODE查询UU用户关系表信息wangww3
	 */
	public static IDataset getUserRelationRole(String userId, String relationTypeCode, String roleCodeB) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userId);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		param.put("ROLE_CODE_B", roleCodeB);

		IDataset userRelationInfos = Dao.qryByCode("TF_F_RELATION_UU", "SEL_USERA_ROLEB", param);
		return userRelationInfos;
	}

	/**
	 * @Function: getUserRelationsByUserId
	 * @Description: 该函数的功能描述
	 * @param:参数描述
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: lijm3
	 * @date: 2013-10-21 下午7:35:22 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-10-21 lijm3 v1.0.0 修改原因
	 */
	public static IDataset getUserRelationsByUserId(String user_id_a, String user_id_b) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", user_id_a);
		param.put("USER_ID_B", user_id_b);
		SQLParser sp = new SQLParser(param);
		sp.addSQL("select  a.PARTITION_ID,a.USER_ID_A,a.SERIAL_NUMBER_A,a.USER_ID_B,a.SERIAL_NUMBER_B, ");
		sp.addSQL(" a.RELATION_TYPE_CODE, a.ROLE_TYPE_CODE,a.ROLE_CODE_A,a.ROLE_CODE_B,a.ORDERNO, ");
		sp.addSQL(" a.SHORT_CODE,a.INST_ID,to_char(a.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");

		sp.addSQL(" to_char(a.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
		sp.addSQL(" to_char(a.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
		sp.addSQL(" a.UPDATE_STAFF_ID, a.UPDATE_DEPART_ID, a.REMARK, a.RSRV_NUM1, ");

		sp.addSQL(" a.RSRV_NUM2, a.RSRV_NUM3, a.RSRV_NUM4, a.RSRV_NUM5, a.RSRV_STR1, a.RSRV_STR2, ");
		sp.addSQL(" a.RSRV_STR3, a.RSRV_STR4, a.RSRV_STR5, ");

		sp.addSQL(" to_char(a.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
		sp.addSQL(" to_char(a.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
		sp.addSQL(" to_char(a.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
		sp.addSQL(" a.RSRV_TAG1, a.RSRV_TAG2, a.RSRV_TAG3 ");

		sp.addSQL("  from TF_F_RELATION_UU a ");
		sp.addSQL(" where 1=1 ");
		sp.addSQL("   and a.end_date > sysdate ");
		sp.addSQL("   and a.user_id_a = :USER_ID_A ");
		sp.addSQL("   and a.user_id_b = :USER_ID_B ");
		sp.addSQL("   and a.partition_id = MOD(TO_NUMBER(:USER_ID_B), 10000) ");
		sp.addSQL(" order by a.orderno ");
		return Dao.qryByParse(sp);
	}

	public static IDataset getUserRelationSub(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("RELATION_TYPE_CODE", "30");
		param.put("USER_ID_A", userId);

		IDataset userRelationInfos = Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_SUB_INFO", param);
		return userRelationInfos;
	}

	/**
	 * 根据UserIdB 查询用户关系UU资料
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserRelationVpmnByUserIdB(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userId);

		return Dao.qryByCodeAllCrm("TF_F_RELATION_UU", "SEL_BY_GrpMebUUVpmn", param, true);
	}

	public static IDataset getUserRelByRolecode(String user_id_a, String relation_type_code, Pagination pagination) throws Exception
	{
		IData params3 = new DataMap();
		params3.put("USER_ID_A", user_id_a);
		params3.put("RELATION_TYPE_CODE", relation_type_code);
		params3.put("ROLE_CODE_B", "2");
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_RELATION_UU", params3, pagination);

	}

	public static IDataset getUserRoleACount(String userIdA, String roleCodeB, String relationTypeCode, Pagination page) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID_A", userIdA);
		inparam.put("ROLE_CODE_B", roleCodeB);
		inparam.put("RELATION_TYPE_CODE", relationTypeCode);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_ROLEA_COUNT", inparam, page);
	}
	 public static IDataset getUUInfoByUserEparchy(String userIdA, String roleCodeB, String relationTypeCode, String eparchyCode) throws Exception
	    {
	    	
	        IData inparam = new DataMap();
	        inparam.put("USER_ID_A", userIdA);
	        inparam.put("ROLE_CODE_B", roleCodeB);
	        inparam.put("RELATION_TYPE_CODE", relationTypeCode);
	        return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_ROLEA_COUNT", inparam, eparchyCode);
	    }
	/**
	 * 宽带子账号开户 选择平行账号主账号下不能有家庭子账号
	 * 
	 * @param param
	 * @return IDataset
	 * @throws Exception
	 * @author huangsl
	 */
	public static IDataset getUserUU(String userIdB, String roleCodeB, String relationTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		param.put("ROLE_CODE_B", roleCodeB);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USERINFO_BY_RELATION", param);
	}

	/*
	 * 根据用户ID查询, 服务ID查询 用户平台服务信息 imparams 需要传入字符串：USER_ID,SERVICE_ID
	 */
	/**
	 * 根据USER_ID_A、USER_ID_B、ROLE_CODE_B、RELATION_TYPE_CODE查询uu关系
	 * 
	 * @param params
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUUByUserIdAB(String userIdA, String userIdB, String roleCodeB, String relationTypeCode, Pagination page, String routeId) throws Exception
	{

		IData params = new DataMap();
		params.put("USER_ID_A", userIdA);
		params.put("USER_ID_B", userIdB);
		params.put("ROLE_CODE_B", roleCodeB);
		params.put("RELATION_TYPE_CODE", relationTypeCode);

		// getVisit().setRouteEparchyCode( eparchyCode);
		return Dao.qryByCodeParser("TF_F_RELATION_UU", "SEL_BY_USERIDAB", params, page, routeId);
	}

	public static IDataset getUUInfo(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_ALL_BY_USERIDAB", param);
	}

	public static IDataset getUUInfo_A(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_UU_ALL_BY_USERID_A", param);
	}

	public static IDataset getUUInfo_B(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_UU_ALL_BY_USERID_B", param);
	}

	/**
	 * 根据USER_ID_A,RELATION_TYPE_CODE 查询所有
	 * 
	 * @param userIdA
	 * @param relationTypeCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUUInfoAllByIdA(String userIdA, String relationTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("RELATION_TYPE_CODE", relationTypeCode);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_ALL_BY_USERIDA_BUNDLE", param);
	}

	/**
	 * 查询UU关系
	 * 
	 * @param userIdA
	 * @param relationTypeCode
	 * @param shortCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUUInfoAllCrm(String userIdA, String relationTypeCode, String shortCode) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID_A", userIdA);
		inparam.put("RELATION_TYPE_CODE", relationTypeCode);
		inparam.put("SHORT_CODE", shortCode);

		return Dao.qryByCodeAllCrm("TF_F_RELATION_UU", "SEL_BY_UU_SHORTCODEEXIST", inparam, false);
	}

	// 根据userIdA查询UU关系
	public static IDataset getUUInfoByUserIdA(String userIdA) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_ROLE_IDA_R", param);
	}

	public static IDataset getUUInfoByUserIdAB(String user_id_b, String user_id_a, String relation_type_code) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", user_id_b);
		param.put("USER_ID_A", user_id_a);
		param.put("RELATION_TYPE_CODE", relation_type_code);
		return Dao.qryByCodeParser("TF_F_RELATION_UU", "SEL_BY_USERIDAB", param);
	}

	/**
	 * 根据USER_ID_A、USER_ID_B、RELAITON_TYPE_CODE、ROLE_CODE_B 灵活查询所需UU关系数据
	 * 
	 * @param user_id_a
	 * @param user_id_b
	 * @param relation_type_code
	 * @param role_code_b
	 * @return
	 * @throws Exception
	 * @author Maoke
	 */
	public static IDataset getUUInfoByUserIdAB(String user_id_a, String user_id_b, String relation_type_code, String role_code_b) throws Exception
	{
		IData param = new DataMap();

		param.put("USER_ID_A", user_id_a);
		param.put("USER_ID_B", user_id_b);
		param.put("RELATION_TYPE_CODE", relation_type_code);
		param.put("ROLE_CODE_B", role_code_b);

		return Dao.qryByCodeParser("TF_F_RELATION_UU", "SEL_BY_USERIDAB", param);
	}

	/**
	 * 根据USER_ID_A、USER_ID_B、RELAITON_TYPE_CODE、END_DATE 灵活查询所需UU关系数据
	 * 
	 * @param user_id_a
	 * @param user_id_b
	 * @param relation_type_code
	 * @param end_date
	 * @return
	 * @throws Exception
	 * @author Maoke
	 */
	public static IDataset getUUInfoByUserIdABDate(String user_id_a, String user_id_b, String relation_type_code, String end_date) throws Exception
	{
		IData param = new DataMap();

		if (!"".equals(user_id_a) && user_id_a != null)
		{
			param.put("USER_ID_A", user_id_a);
		}
		if (!"".equals(user_id_b) && user_id_b != null)
		{
			param.put("USER_ID_B", user_id_b);
		}
		if (!"".equals(relation_type_code) && relation_type_code != null)
		{
			param.put("RELATION_TYPE_CODE", relation_type_code);
		}
		if (!"".equals(end_date) && end_date != null)
		{
			param.put("END_DATE", end_date);
		}

		return Dao.qryByCodeParser("TF_F_RELATION_UU", "SEL_BY_USERIDAB_DATE", param);
	}

	/**
	 * 根据userIdA、serialNumberB查询用户信息
	 */
	public static IDataset getUUInfoByUserIdASerB(IData inparams) throws Exception
	{
		// TODO
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_BY_USERIDA_SERNUMB", inparams);
	}

	// 根据userIdB查询UU关系
	public static IDataset getUUInfoByUserIdB(String userIdB) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);

		StringBuilder sql = new StringBuilder(1000);

		sql.append("SELECT PARTITION_ID, TO_CHAR(USER_ID_A) USER_ID_A, ");
		sql.append("SERIAL_NUMBER_A, TO_CHAR(USER_ID_B) USER_ID_B, ");
		sql.append("SERIAL_NUMBER_B, RELATION_TYPE_CODE, ROLE_CODE_A, ");
		sql.append("ROLE_CODE_B, ORDERNO, SHORT_CODE, ");
		sql.append("TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
		sql.append("TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE ");
		sql.append("FROM TF_F_RELATION_UU ");
		sql.append("WHERE USER_ID_B = TO_NUMBER(:USER_ID_B) ");
		sql.append("AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID_B), 10000) ");
		sql.append("AND SYSDATE BETWEEN START_DATE AND END_DATE ");

		return Dao.qryBySql(sql, param);
	}

	/**
	 * 查用户关系表
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUUInfoByUserIdBAndRelTypeCode(String userId, String relation_type_code, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userId);
		param.put("RELATION_TYPE_CODE", relation_type_code);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_BY_GrpMebUU", param, eparchyCode);
	}

	// 根据用户IDB和角色类型B查询对应的uuInfo信息
	public static IDataset getUUInfoByUserIDBAndRoleCodeB(String userIDB, String roleCodeB) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userIDB);
		param.put("ROLE_CODE_B", roleCodeB);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_ROLE_IDB_1", param);
	}

	/**
	 * 查询用户UU关系
	 * 
	 * @param inparam
	 *            [USER_ID_A,RELATION_TYPE_CODE,ROLE_CODE_B]
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUUInfoForGrp(IData inparam) throws Exception
	{
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_ROLEA", inparam, Route.CONN_CRM_CG);
	}

	/**
	 * 查询该成员是否有当月结束的VPCN的uu关系
	 */
	public static IDataset getVPCNuu(String userIdA, String userIdB, Pagination page) throws Exception
	{

		IData params = new DataMap();
		params.put("USER_ID_A", userIdA);
		params.put("USER_ID_B", userIdB);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_VPCN_UU", params, page);
	}

	/**
	 * 根据USER_ID_B,RELATION_TYPE_CODE查询该成员本月加入vpmn集团次数
	 * 
	 * @param params
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset getVpmnMebCount(IData params) throws Exception
	{
		// TODO
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_VPMN_MEB_ADDCOUNT", params);
	}

	/**
	 * 获取VPMN下所有短号
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset getVpmnShortCode(IData param, Pagination pagination) throws Exception
	{
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT r.SHORT_CODE,'未用' STATE  FROM ( ");
		parser.addSQL(" select to_char(SHORT_CODE) SHORT_CODE from td_b_vshortcode ");
		parser.addSQL(" where 1 = 1 ");
		parser.addSQL(" and (contain_4= :CONTAIN_4 OR :CONTAIN_4 IS NULL) ");
		parser.addSQL(" and (contain_7= :CONTAIN_7 OR :CONTAIN_7 IS NULL) ");
		parser.addSQL(" and (SHORT_CODE LIKE '6'|| :SHORT_LENGTH ||'%' OR :SHORT_LENGTH IS NULL) ");
		parser.addSQL(" and (length = :SHORT_TYPE OR :SHORT_TYPE IS NULL) ");
		parser.addSQL(" minus ");
		parser.addSQL(" select SHORT_CODE from TF_F_RELATION_UU A "); // 根据vpmn的userid，查出所有网外号码的成员短号
		parser.addSQL(" where 1 = 1 ");
		parser.addSQL(" AND exists(select 1 from TF_F_USER_VPN C where C.VPN_NO = A.SERIAL_NUMBER_A ) ");
		parser.addSQL(" AND A.RELATION_TYPE_CODE = '20' ");
		parser.addSQL(" AND A.END_DATE > SYSDATE ");
		parser.addSQL(" AND length(A.SHORT_CODE)= :SHORT_TYPE ");
		// parser.addSQL(" AND (A.SERIAL_NUMBER_A = :SERIAL_NUMBER_A OR :SERIAL_NUMBER_A IS NULL) ");
		parser.addSQL(" AND  A.USER_ID_A = :USER_ID_A ");
		parser.addSQL(" minus ");
		parser.addSQL(" select SHORT_CODE from TF_F_RELATION_UU uua where 1 = 1 "); // 根据vpmn的userid，查出所有网外号码的成员短号
		parser.addSQL(" and sysdate>=start_date and sysdate<=end_date ");
		parser.addSQL(" and relation_type_code = '41' "); // 网外号码
		parser.addSQL(" AND length(uua.SHORT_CODE)=  :SHORT_TYPE ");
		parser.addSQL(" AND uua.user_id_a = to_number( :USER_ID_A) ");
		parser.addSQL(" ) r ");

		IDataset resultset = Dao.qryByParse(parser, pagination);
		if (IDataUtil.isNotEmpty(resultset))
		{
			for (int i = 0, ilen = resultset.size(); i < ilen; i++)
			{
				IData resultData = resultset.getData(i);
				resultData.put("VPN_NAME", param.getString("VPN_NAME"));
				resultData.put("SERIAL_NUMBER_A", param.getString("SERIAL_NUMBER_M"));
			}
		}
		return resultset;

	}

	/**
	 * 获取VPMN下所有短号
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset getVpmnShortCodeExport(IData param, Pagination pagination) throws Exception
	{
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT r.SHORT_CODE,'未用' STATE  FROM ( ");
		parser.addSQL(" select to_char(SHORT_CODE) SHORT_CODE from td_b_vshortcode ");
		parser.addSQL(" where 1 = 1 ");
		parser.addSQL(" and (contain_4= :CONTAIN_4 OR :CONTAIN_4 IS NULL) ");
		parser.addSQL(" and (contain_7= :CONTAIN_7 OR :CONTAIN_7 IS NULL) ");
		parser.addSQL(" and (SHORT_CODE LIKE '6'|| :SHORT_LENGTH ||'%' OR :SHORT_LENGTH IS NULL) ");
		parser.addSQL(" and (length = :SHORT_TYPE OR :SHORT_TYPE IS NULL) ");
		parser.addSQL(" minus ");
		parser.addSQL(" select SHORT_CODE from TF_F_RELATION_UU A "); // 根据vpmn的userid，查出所有网外号码的成员短号
		parser.addSQL(" where 1 = 1 ");
		parser.addSQL(" AND exists(select 1 from TF_F_USER_VPN C where C.VPN_NO = A.SERIAL_NUMBER_A ) ");
		parser.addSQL(" AND A.RELATION_TYPE_CODE = '20' ");
		parser.addSQL(" AND A.END_DATE > SYSDATE ");
		parser.addSQL(" AND length(A.SHORT_CODE)= :SHORT_TYPE ");
		parser.addSQL(" AND (A.SERIAL_NUMBER_A = :SERIAL_NUMBER_A OR :SERIAL_NUMBER_A IS NULL) ");
		parser.addSQL(" minus ");
		parser.addSQL(" select SHORT_CODE from TF_F_RELATION_UU uua where 1 = 1 "); // 根据vpmn的userid，查出所有网外号码的成员短号
		parser.addSQL(" and sysdate>=start_date and sysdate<=end_date ");
		parser.addSQL(" and relation_type_code = '41' "); // 网外号码
		parser.addSQL(" AND length(uua.SHORT_CODE)=  :SHORT_TYPE ");
		parser.addSQL(" AND uua.user_id_a = to_number( :USER_ID_A) ");
		parser.addSQL(" ) r ");

		IDataset resultset = Dao.qryByParse(parser, pagination, Route.getCrmDefaultDb());
		if (IDataUtil.isNotEmpty(resultset))
		{
			for (int i = 0, ilen = resultset.size(); i < ilen; i++)
			{
				IData resultData = resultset.getData(i);
				resultData.put("VPN_NAME", param.getString("VPN_NAME"));
				resultData.put("SERIAL_NUMBER_A", param.getString("SERIAL_NUMBER_M"));
			}
		}
		return resultset;

	}

	/*
	 * ADC校讯通 异网号码UU关系查询 liaolc 2014-04-17
	 */
	public static IDataset getXXTRelation(String mebMainNumber, String mebUserId, String roleCodeA, String roleCodeB, String relationTypeCode, String ecUserId) throws Exception
	{
		IData relaParam = new DataMap();
		relaParam.put("SERIAL_NUMBER_B", mebMainNumber);
		relaParam.put("USER_ID_B", mebUserId);
		relaParam.put("ROLE_CODE_A", roleCodeA);
		relaParam.put("ROLE_CODE_B", roleCodeB);
		relaParam.put("RELATION_TYPE_CODE", relationTypeCode);// 注销姓名
		relaParam.put("EC_USER_ID", ecUserId);// 注销姓名

		IDataset result = Dao.qryByCode("TF_F_RELATION_UU", "SEL_BY_USERIDA_SNA_TYPE1", relaParam);

		return result;
	}

	public static boolean GroupRelationUUS(String userIdA, Pagination page) throws Exception
	{

		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		SQLParser parser = new SQLParser(param);

		parser.addSQL(" SELECT COUNT(*) FROM TF_F_RELATION_UU A  WHERE  a.user_id_a=:USER_ID_A  ");
		parser.addSQL("  AND a.end_date>SYSDATE ");
		parser.addSQL("    AND a.RELATION_TYPE_CODE = '89' ");
		parser.addSQL("      AND ROWNUM<2 ");

		IDataset dataset = Dao.qryByParse(parser, page);

		if (dataset == null || dataset.size() == 0)
		{
			return false;
		}

		return true;
	}

	public static boolean isCMCCstaffUser(String userIdB) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);
		IDataset dataset = Dao.qryByCode("TF_F_RELATION_UU", "IS_CMCC_STAFF_USER", param);
		String count = dataset.getData(0).getString("CNT");

		return Integer.parseInt(count) > 0 ? true : false;
	}

	public static boolean isCMCCstaffUserNew(String userIdB) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);
		IDataset dataset = Dao.qryByCode("TF_F_RELATION_UU", "IS_CMCC_STAFF_USER_N", param);
		String count = dataset.getData(0).getString("CNT");

		return Integer.parseInt(count) > 0 ? true : false;
	}

	/**
	 * 宽带子账号开户 判断是否为主账号
	 * 
	 * @param param
	 * @return IDataset
	 * @throws Exception
	 * @author huangsl
	 */
	public static IDataset isMasterAccount(String userId, String relaTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userId);
		param.put("RELATION_TYPE_CODE", relaTypeCode);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_BY_CODE_B", param);
	}

	/**
	 * @dec 判断USER_ID_B2是不是在USER_ID_B所在的亲亲网
	 * @author huangsl
	 * @param USER_ID_B
	 *            USER_ID_B2
	 * @return
	 * @throws Exception
	 */
	public static IDataset judgeQinQinWang(String userIdB, String userIdB2) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);
		param.put("USER_ID_B2", userIdB2);
		return Dao.qryByCode("TD_S_COMMPARA", "judge_qinqinwang", param);
	}

	/**
	 * 根据USER_ID_A查询所有家庭网成员的产品、优惠等信息
	 * 
	 * @param userIdA
	 * @return
	 * @throws Exception
	 *             wangjx 2013-8-1
	 */
	public static IDataset qryAllFamilyUserInfo(String userIdA) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		return Dao.qryByCodeAllCrm("TF_F_RELATION_UU", "SEL_UUUDPD_BY_USERIDA", param, true);
	}

	/**
	 * 查询所有CRM库所有的用户优惠资料
	 * 
	 * @param userIdA
	 * @param relationTypeCode
	 * @return IDataset
	 * @throws Exception
	 *             wangjx 2013-7-23
	 */
	public static IDataset qryAllFriendByUIdAAllDB(String userIdA, String relationTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		return Dao.qryByCodeAllCrm("TF_F_RELATION_UU", "SEL_ALLFRIEND_BY_UIDA_CODE", param, true);
	}

	/**
	 * 根据SERIAL_NUMBER_B查询用户关系
	 * 
	 * @param serialNumberB
	 * @param relationTypeCode
	 * @return IDataset
	 * @throws Exception
	 *             wangjx 2013-7-23
	 */
	public static IDataset qryAllRelaUUBySnB(String serialNumberB, String relationTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER_B", serialNumberB);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_SNA_BY_SNB_RTYPE_ALL", param);
	}

	/**
	 * 查询该用户家庭网有效、失效的所有关系
	 * 
	 * @param userIdB
	 * @param relationTypeCode
	 * @param route
	 * @return IDataset
	 * @throws Exception
	 *            lizj 2019-8-7
	 */
	public static IDataset qryAllRelaUUByUidB2(String userIdB, String relationTypeCode, String route) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_ALL_BY_UIDB2", param, route);
	}
	
	/**
	 * 查询该用户家庭网有效、失效的所有关系
	 * 
	 * @param userIdA
	 * @param relationTypeCode
	 * @param route
	 * @return IDataset
	 * @throws Exception
	 *            lizj 2019-8-16
	 */
	public static IDataset qryAllRelaUUByUidA(String userIdA, String relationTypeCode, String route) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_ALL_BY_UIDA", param, route);
	}
	
	/**
	 * 查询该用户家庭网有效、失效的所有关系
	 * 
	 * @param userIdB
	 * @param relationTypeCode
	 * @param route
	 * @return IDataset
	 * @throws Exception
	 *             wangjx 2013-9-2
	 */
	public static IDataset qryAllRelaUUByUidB(String userIdB, String relationTypeCode, String route) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_ALL_BY_UIDB", param, route);
	}

	public static IDataset qryByRelaUserIdARoleCodeB(String userIdA, String relationTypeCode, String roleCodeB, Pagination pg) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		param.put("ROLE_CODE_B", roleCodeB);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_BY_RELA_USERIDA_ROLECODEB", param, pg);
	}

	public static IDataset qryByRelaUserIdB(String userIdB, String relationTypeCode, Pagination pg) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);
		param.put("RELATION_TYPE_CODE", relationTypeCode);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_BY_RELA_USERIDB", param, pg);
	}

	public static IDataset qryByRelaUserIdBRouteId(String userIdB, String relationTypeCode, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);
		param.put("RELATION_TYPE_CODE", relationTypeCode);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_BY_RELA_USERIDB", param, routeId);
	}

	/**
	 * 统计成员数量
	 * 
	 * @param userIdA
	 * @param relationTypeCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryCountByUserIdAAndRelationTypeCodeAllCrm(String userIdA, String relationTypeCode) throws Exception
	{
		IData param = new DataMap();

		param.put("USER_ID_A", userIdA);
		param.put("RELATION_TYPE_CODE", relationTypeCode);

		long recordCount = 0;

		// 遍历数据库
		for (String routeId : Route.getAllCrmDb())
		{
			StringBuilder sql = new StringBuilder(100);

			sql.append("SELECT COUNT(1) RECORDCOUNT ");
			sql.append("  FROM TF_F_RELATION_UU ");
			sql.append(" WHERE USER_ID_A = TO_NUMBER(:USER_ID_A) ");
			sql.append("   AND RELATION_TYPE_CODE = :RELATION_TYPE_CODE ");

			IDataset countList = Dao.qryBySql(sql, param, routeId);

			recordCount += Long.parseLong(countList.getData(0).getString("RECORDCOUNT"));
		}

		IData retData = new DataMap();

		retData.put("RECORDCOUNT", recordCount);

		return IDataUtil.idToIds(retData);
	}

	/**
	 * 查询所有CRM库无效的用户优惠资料
	 * 
	 * @param userIdA
	 * @param relationTypeCode
	 * @return
	 * @throws Exception
	 *             wangjx 2013-7-23
	 */
	public static IDataset qryEndFriendByUIdAAllDB(String userIdA, String relationTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		return Dao.qryByCodeAllCrm("TF_F_RELATION_UU", "SEL_ENDFRIEND_BY_UIDA_CODE", param, true);
	}

	/**
	 * 查询所有CRM库失效的家庭网成员关系
	 * 
	 * @param userIdB
	 * @param relationTypeCode
	 * @param route
	 * @return IDataset
	 * @throws Exception
	 *             wangjx 2013-9-2
	 */
	public static IDataset qryEndRelaUUByUidB(String userIdB, String relationTypeCode, String route) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_END_BY_UIDB", param, route);
	}

	/**
	 * 判断是否存在成员
	 * 
	 * @param userIdA
	 * @param relationTypeCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryExistsMebByUserIdA(String userIdA, String relationTypeCode) throws Exception
	{
		IData param = new DataMap();

		param.put("USER_ID_A", userIdA);
		param.put("RELATION_TYPE_CODE", relationTypeCode);

		return Dao.qryByCodeAllCrm("TF_F_RELATION_UU", "SEL_EXISTS_MEB_BY_USERIDA", param, false);
	}

	/**
	 * 查询所有CRM库有效的用户优惠资料
	 * 
	 * @param userIdA
	 * @param relationTypeCode
	 * @return IDataset
	 * @throws Exception
	 *             wangjx 2013-7-23
	 */
	public static IDataset qryFriendByUIdAAllDB(String userIdA, String relationTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		return Dao.qryByCodeAllCrm("TF_F_RELATION_UU", "SEL_FRIEND_BY_UIDA_CODE", param, true);
	}

	public static IDataset qryGroupPRBTByProductId(IData param) throws Exception
	{

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT a.serial_number_a, c.cust_name group_name, a.serial_number_b, a.start_date, ");
		parser.addSQL(" a.end_date,decode(d.plan_type_code,'G','是','P','否','否') plan_type_code,u.city_code,e.group_id,e.cust_manager_id ");
		parser.addSQL(" FROM tf_f_relation_uu a,tf_f_user u,tf_f_customer c,tf_f_user_payplan d,tf_f_cust_group e ");
		parser.addSQL(" where a.relation_type_code = '26' ");
		parser.addSQL("  and u.user_id = a.user_id_a ");
		parser.addSQL("  and u.partition_id = mod(a.user_id_a,10000) ");
		parser.addSQL("  and c.cust_id = u.cust_id ");
		parser.addSQL("  and u.cust_id = e.cust_id ");
		parser.addSQL("  and d.user_id = a.user_id_b ");
		parser.addSQL("  and d.user_id_a = a.user_id_a ");
		parser.addSQL("  and d.partition_id=mod(a.user_id_b,10000) ");
		// parser.addSQL("  AND a.serial_number_a = :SERIAL_NUMBER ");
		parser.addSQL("  AND a.user_id_a = :USER_ID_A ");
		if ("0".equals(param.getString("STATE")))
		{
			parser.addSQL("  AND a.end_date >= sysdate ");
			parser.addSQL("  AND d.end_date > sysdate ");
		} else
		{
			parser.addSQL("  AND a.end_date < sysdate ");
			parser.addSQL(" AND d.end_date = (select max(end_date) from tf_f_user_payplan g where  g.user_id = a.user_id_b  and g.partition_id = mod(a.user_id_b, 10000)  and g.user_id_a = a.user_id_a ) ");
		}

		IDataset dataset = Dao.qryByParseAllCrm(parser, true);
		if (IDataUtil.isNotEmpty(dataset))
		{
			for (int i = 0; i < dataset.size(); i++)
			{
				IData data = dataset.getData(i);
				String custMangerId = data.getString("CUST_MANAGER_ID");
				String manageName = UStaffInfoQry.getCustManageNameByCustManagerId(custMangerId);
				data.put("CUST_MANAGER_NAME", manageName);
			}
		}
		return dataset;
	}

	public static IDataset qryGroupPRBTBySN(IData param, String routeId) throws Exception
	{

		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT a.serial_number_a, c.cust_name group_name,  ");
		parser.addSQL(" a.serial_number_b, a.start_date,a.end_date,decode(d.plan_type_code,'G','是','P','否','否') plan_type_code,u.city_code,e.group_id,e.cust_manager_id ");
		parser.addSQL(" FROM tf_f_relation_uu a,tf_f_user u,tf_f_customer c,tf_f_user_payplan d,tf_f_cust_group e ");
		parser.addSQL(" where a.relation_type_code = '26' ");
		parser.addSQL("  and u.user_id = a.user_id_a ");
		parser.addSQL("  and u.partition_id = mod(a.user_id_a,10000) ");
		parser.addSQL("  and c.cust_id = u.cust_id ");
		parser.addSQL("  and u.cust_id = e.cust_id ");
		parser.addSQL("  and d.user_id = a.user_id_b ");
		parser.addSQL("  and d.user_id_a = a.user_id_a ");
		parser.addSQL("  and d.partition_id=mod(a.user_id_b,10000) ");
		parser.addSQL("  AND a.serial_number_b = :SERIAL_NUMBER_B ");
		if (param.getString("STATE").equals("0"))
		{
			parser.addSQL("  AND a.end_date >= sysdate ");
			parser.addSQL("  AND d.end_date > sysdate ");
		} else
		{
			parser.addSQL("  AND a.end_date < sysdate ");
			parser.addSQL(" AND d.end_date = (select max(end_date) from tf_f_user_payplan g where  g.user_id = a.user_id_b  and g.partition_id = mod(a.user_id_b, 10000)  and g.user_id_a = a.user_id_a ) ");
		}
		IDataset dataset = Dao.qryByParse(parser, routeId);
		if (IDataUtil.isNotEmpty(dataset))
		{
			for (int i = 0; i < dataset.size(); i++)
			{
				IData data = dataset.getData(i);
				String custMangerId = data.getString("CUST_MANAGER_ID");
				String manageName = UStaffInfoQry.getCustManageNameByCustManagerId(custMangerId);
				data.put("CUST_MANAGER_NAME", manageName);
			}
		}
		return dataset;
	}

	/**
	 * 集团彩铃成员订购信息查询
	 * 
	 * @param param
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryGroupPRBTInfo(IData param) throws Exception
	{

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT a.serial_number_a, c.cust_name group_name, a.serial_number_b, a.start_date, a.end_date, ");
		parser.addSQL(" decode(d.plan_type_code,'G','是','P','否','否') plan_type_code, u.city_code,e.group_id,e.cust_manager_id ");
		parser.addSQL(" FROM tf_f_relation_uu a,tf_f_user u,tf_f_customer c,tf_f_user_payplan d,tf_f_cust_group e ");
		parser.addSQL(" where a.relation_type_code = '26' ");
		parser.addSQL("  and u.user_id = a.user_id_a ");
		parser.addSQL("  and u.partition_id = mod(a.user_id_a,10000) ");
		parser.addSQL("  and c.cust_id = u.cust_id ");
		parser.addSQL("  and u.cust_id = e.cust_id ");
		parser.addSQL("  and d.user_id = a.user_id_b ");
		parser.addSQL("  and d.user_id_a = a.user_id_a ");
		parser.addSQL("  and d.partition_id = mod(a.user_id_b,10000) ");
		parser.addSQL("  AND a.serial_number_b = :SERIAL_NUMBER_B ");
		// parser.addSQL("  AND a.serial_number_a = :SERIAL_NUMBER ");
		parser.addSQL("  AND a.user_id_a = :USER_ID_A ");
		if ("0".equals(param.getString("STATE")))
		{
			parser.addSQL("  AND a.end_date >= sysdate ");
			parser.addSQL("  AND d.end_date > sysdate ");
		} else
		{
			parser.addSQL("  AND a.end_date < sysdate ");
			parser.addSQL(" AND d.end_date = (select max(end_date) from tf_f_user_payplan g where  g.user_id = a.user_id_b  and g.partition_id = mod(a.user_id_b, 10000)  and g.user_id_a = a.user_id_a ) ");
		}
		IDataset dataset = Dao.qryByParseAllCrm(parser, true);
		if (IDataUtil.isNotEmpty(dataset))
		{
			for (int i = 0; i < dataset.size(); i++)
			{
				IData data = dataset.getData(i);
				String custMangerId = data.getString("CUST_MANAGER_ID");
				String manageName = UStaffInfoQry.getCustManageNameByCustManagerId(custMangerId);
				data.put("CUST_MANAGER_NAME", manageName);
			}
		}

		return dataset;
	}

	/**
	 * 根据集团编码和成员服务号码查询集团成员订购信息
	 * 
	 * @param groupId
	 * @param serialNumberB
	 * @param pg
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryGrpAndMebInfo(String groupId, String userIdB, Pagination pg) throws Exception
	{
		IData param = new DataMap();
		param.put("GROUP_ID", groupId);
		param.put("USER_ID_B", userIdB);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_GRPMEBINFO_BY_GROUPID_USERIDB", param, pg);
	}

	public static IDataset qryGrpRelaUUByUserIdA(String userIdA, String relationTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_ALL_BY_USERIDA", param, Route.CONN_CRM_CG);
	}

	public static IDataset qryLastUUByUserIdB(String userIdB, String relationTypeCode, Pagination page) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID_B", userIdB);
		params.put("RELATION_TYPE_CODE", relationTypeCode);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_LASTUU_BY_USERIDB", params);
	}

	public static IDataset qryNextAllFamilyUserInfo(String userIdA) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		return Dao.qryByCodeAllCrm("TF_F_RELATION_UU", "SEL_NEXTUUUDPD_BY_USERIDA", param, true);
	}

	/**
	 * 查询所有有效UU表关系
	 * 
	 * @param userIdB
	 * @param relationTypeCode
	 * @param route
	 * @return IDataset
	 * @throws Exception
	 *             wangjx 2013-9-2
	 */
	public static IDataset qryNormalRelaUUByUidB(String userIdB, String relationTypeCode, String route) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_NORMAL_BY_UIDB", param, route);
	}

	public static IDataset qryRealAllByUserIdBForGrp(String userIdB) throws Exception
	{
		IData param = new DataMap();

		param.put("USER_ID_B", userIdB);

		StringBuilder sql = new StringBuilder(1000);

		sql.append(" SELECT A.USER_ID_B ");
		sql.append("   FROM TF_F_RELATION_UU A, TF_F_RELATION_UU B ");
		sql.append("  WHERE A.USER_ID_A = B.USER_ID_A ");
		sql.append("    AND A.RELATION_TYPE_CODE = '40' ");
		sql.append("    AND B.RELATION_TYPE_CODE = '40' ");
		sql.append("    AND B.USER_ID_B = TO_NUMBER(:USER_ID_B) ");
		sql.append("    AND B.PARTITION_ID = MOD(TO_NUMBER(:USER_ID_B), 10000) ");
		sql.append("    AND (SYSDATE BETWEEN A.START_DATE AND A.END_DATE) ");
		sql.append("    AND (SYSDATE BETWEEN B.START_DATE AND B.END_DATE) ");

		return Dao.qryBySql(sql, param);
	}

	public static IDataset qryRealUUBySnb(String serialNumberB) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER_B", serialNumberB);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_LAST_BY_SNB", param);
	}

	public static IDataset qryRealUUByUserIdB(String userIdB) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_BY_USERIDB", param);
	}

	/**
	 * 根据user_id_b查询成员所有订购关系，
	 * 
	 * @param inParams
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryRelaAllByUserIdB(String userIdB, Pagination page) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);

		StringBuilder sql = new StringBuilder(1000);

		sql.append("SELECT PARTITION_ID, TO_CHAR(USER_ID_A) USER_ID_A, SERIAL_NUMBER_A, ");
		sql.append("TO_CHAR(USER_ID_B) USER_ID_B, SERIAL_NUMBER_B, RELATION_TYPE_CODE, ");
		sql.append("ROLE_CODE_A, ROLE_CODE_B, ORDERNO, SHORT_CODE, ");
		sql.append("TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
		sql.append("TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE ");
		sql.append("FROM TF_F_RELATION_UU ");
		sql.append("WHERE PARTITION_ID = MOD(TO_NUMBER(:USER_ID_B), 10000) ");
		sql.append("AND USER_ID_B = TO_NUMBER(:USER_ID_B) ");
		sql.append("AND SYSDATE BETWEEN START_DATE AND END_DATE ");

		sql.append(" UNION ALL ");

		sql.append("SELECT PARTITION_ID, TO_CHAR(USER_ID_A) USER_ID_A, SERIAL_NUMBER_A, ");
		sql.append("TO_CHAR(USER_ID_B) USER_ID_B, SERIAL_NUMBER_B, RELATION_TYPE_CODE, ");
		sql.append("ROLE_CODE_A, ROLE_CODE_B, ORDERNO, SHORT_CODE, ");
		sql.append("TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
		sql.append("TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE ");
		sql.append("FROM TF_F_RELATION_BB ");
		sql.append("WHERE PARTITION_ID = MOD(TO_NUMBER(:USER_ID_B), 10000) ");
		sql.append("AND USER_ID_B = TO_NUMBER(:USER_ID_B) ");
		sql.append("AND SYSDATE BETWEEN START_DATE AND END_DATE ");

		IDataset infos = Dao.qryBySql(sql, param, page);
		if (IDataUtil.isEmpty(infos))
			return infos;
		for (int i = 0; i < infos.size(); i++)
		{
			IData map = infos.getData(i);
			map.put("RELATION_TYPE_NAME", URelaTypeInfoQry.getRoleTypeNameByRelaTypeCode(map.getString("RELATION_TYPE_CODE")));
		}

		return infos;
	}

	public static IDataset qryRelaBySerialNumberBAndRelationTypeCode(String relationTypeCode, String serialNumberB, Pagination pagination) throws Exception
	{
		IData param = new DataMap();

		param.put("RELATION_TYPE_CODE", relationTypeCode);
		param.put("SERIAL_NUMBER_B", serialNumberB);

		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT SERIAL_NUMBER_A, ");
		sql.append(" SERIAL_NUMBER_B, ");
		sql.append(" USER_ID_A, ");
		sql.append(" USER_ID_B, ");
		sql.append(" SHORT_CODE, ");
		sql.append(" to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date, ");
		sql.append(" to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date ");
		sql.append(" FROM tf_f_relation_uu ");
		sql.append(" WHERE 1 = 1 ");
		sql.append(" AND RELATION_TYPE_CODE = :RELATION_TYPE_CODE ");
		sql.append(" AND START_DATE <= SYSDATE ");
		sql.append(" AND END_DATE > SYSDATE ");
		sql.append(" AND PARTITION_ID = MOD(USER_ID_B, 10000) ");
		sql.append(" AND SERIAL_NUMBER_B = :SERIAL_NUMBER_B ");

		return Dao.qryBySql(sql, param);
	}

	public static IDataset qryRelaByUserIdAAndSerialNumberBForGrp(String userIdA, String serialnumberB) throws Exception
	{
		IData param = new DataMap();

		param.put("USER_ID_A", userIdA);
		param.put("SERIAL_NUMBER_B", serialnumberB);

		StringBuilder sql = new StringBuilder(1000);
		sql.append(" SELECT USER_ID_A, SERIAL_NUMBER_A, SERIAL_NUMBER_B, USER_ID_B, SHORT_CODE ");
		sql.append("  FROM TF_F_RELATION_UU ");
		sql.append("  WHERE USER_ID_A = :USER_ID_A ");
		sql.append("    AND RELATION_TYPE_CODE = '20' ");
		sql.append("    AND SERIAL_NUMBER_B = :SERIAL_NUMBER_B ");
		sql.append("    AND (SYSDATE BETWEEN START_DATE AND END_DATE) ");
		sql.append(" UNION ");
		sql.append(" SELECT USER_ID_A, SERIAL_NUMBER_A, SERIAL_NUMBER_B, USER_ID_B, SHORT_CODE ");
		sql.append("   FROM TF_F_RELATION_UU ");
		sql.append("  WHERE USER_ID_A = :USER_ID_A ");
		sql.append("    AND RELATION_TYPE_CODE = '20' ");
		sql.append("    AND SHORT_CODE = :SERIAL_NUMBER_B ");
		sql.append("    AND (SYSDATE BETWEEN START_DATE AND END_DATE) ");
		return Dao.qryBySql(sql, param);
	}

	/**
	 * 根据user_id_b查询成员所对应的集团用户信息
	 * 
	 * @param inParams
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryRelaByUserIdB(String userIdB, Pagination page) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);

		StringBuilder sql = new StringBuilder(1000);

		sql.append("SELECT PARTITION_ID, TO_CHAR(USER_ID_A) USER_ID_A, SERIAL_NUMBER_A, ");
		sql.append("TO_CHAR(USER_ID_B) USER_ID_B, SERIAL_NUMBER_B, RELATION_TYPE_CODE, ");
		sql.append("ROLE_CODE_A, ROLE_CODE_B, ORDERNO, SHORT_CODE, ");
		sql.append("TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
		sql.append("TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE ");
		sql.append("FROM TF_F_RELATION_UU ");
		sql.append("WHERE PARTITION_ID = MOD(TO_NUMBER(:USER_ID_B), 10000) ");
		sql.append("AND USER_ID_B = TO_NUMBER(:USER_ID_B) ");
		sql.append("AND SYSDATE BETWEEN START_DATE AND END_DATE ");

		IDataset infos = Dao.qryBySql(sql, param, page);
		if (IDataUtil.isEmpty(infos))
			return infos;
		for (int i = 0; i < infos.size(); i++)
		{
			IData map = infos.getData(i);
			map.put("RELATION_TYPE_NAME", URelaTypeInfoQry.getRoleTypeNameByRelaTypeCode(map.getString("RELATION_TYPE_CODE")));
		}

		return infos;
	}

	/**
	 * 根据成员用户ID查询UU关系
	 * 
	 * @param userIdB
	 * @param relationTyeCode
	 * @param roleCodeB
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryRelaByUserIdB(String userIdB, String relationTyeCode, String roleCodeB) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);
		param.put("RELATION_TYPE_CODE", relationTyeCode);
		param.put("ROLE_CODE_B", roleCodeB);

		StringBuilder sql = new StringBuilder(1000);

		sql.append("SELECT PARTITION_ID, TO_CHAR(USER_ID_A) USER_ID_A, TO_CHAR(USER_ID_B) USER_ID_B, ");
		sql.append("RELATION_TYPE_CODE, ROLE_CODE_A, ROLE_CODE_B, ORDERNO, SHORT_CODE, ");
		sql.append("TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
		sql.append("TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
		sql.append("TO_CHAR(INST_ID) INST_ID ");
		sql.append("FROM TF_F_RELATION_UU ");
		sql.append("WHERE USER_ID_B = TO_NUMBER(:USER_ID_B) ");
		sql.append("AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID_B), 10000) ");
		sql.append("AND RELATION_TYPE_CODE = :RELATION_TYPE_CODE ");
		sql.append("AND ROLE_CODE_B = :ROLE_CODE_B ");
		sql.append("AND END_DATE > LAST_DAY(TRUNC(SYSDATE)) + 1 - 1 / 24 / 3600 ");

		return Dao.qryBySql(sql, param);
	}

	/**
	 * 查询本月网内失效的所有成员 @yanwu
	 * 
	 * @param userIdB
	 * @param relationTypeCode
	 * @return IDataset
	 * @throws Exception
	 */
	public static IDataset qryRelaByUserIdAThisMonth(String userIdA, String relationTypeCode) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID_A", userIdA);
		params.put("RELATION_TYPE_CODE", relationTypeCode);

		IDataset uuSet = Dao.qryByCode("TF_F_RELATION_UU", "SEL_THISMONTH_BY_USERIDA", params);
		return uuSet;
	}

	public static IDataset qryRelaByUserIdBRelaTypeCode(String userIdB, String relationTypeCode, Pagination pagination) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID_B", userIdB);
		params.put("RELATION_TYPE_CODE", relationTypeCode);

		StringBuilder sql = new StringBuilder(1000);

		sql.append("SELECT PARTITION_ID, TO_CHAR(USER_ID_A) USER_ID_A, TO_CHAR(USER_ID_B) USER_ID_B, ");
		sql.append("RELATION_TYPE_CODE, ROLE_CODE_A, ROLE_CODE_B, ORDERNO, SHORT_CODE, ");
		sql.append("TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
		sql.append("TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE ");
		sql.append("FROM TF_F_RELATION_UU ");
		sql.append("WHERE PARTITION_ID = MOD(TO_NUMBER(:USER_ID_B), 10000) ");
		sql.append("AND USER_ID_B = TO_NUMBER(:USER_ID_B) ");
		sql.append("AND RELATION_TYPE_CODE = :RELATION_TYPE_CODE ");
		sql.append("AND SYSDATE < END_DATE ");

		return Dao.qryBySql(sql, params, pagination);
	}

	/**
	 * 根据USER_ID_B、RELATION_TYPE_CODE获取关系表数据
	 */
	public static IDataset qryRelaByUserIdBRelaTypeCodeForGrp(String userIdB, String relationTypeCode, Pagination pagination) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID_B", userIdB);
		params.put("RELATION_TYPE_CODE", relationTypeCode);

		StringBuilder sql = new StringBuilder(1000);

		sql.append("SELECT PARTITION_ID, TO_CHAR(USER_ID_A) USER_ID_A, TO_CHAR(USER_ID_B) USER_ID_B, ");
		sql.append("RELATION_TYPE_CODE, ROLE_CODE_A, ROLE_CODE_B, ORDERNO, SHORT_CODE, ");
		sql.append("TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
		sql.append("TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE ");
		sql.append("FROM TF_F_RELATION_UU ");
		sql.append("WHERE PARTITION_ID = MOD(TO_NUMBER(:USER_ID_B), 10000) ");
		sql.append("AND USER_ID_B = TO_NUMBER(:USER_ID_B) ");
		sql.append("AND RELATION_TYPE_CODE = :RELATION_TYPE_CODE ");
		sql.append("AND SYSDATE < END_DATE ");

		return Dao.qryBySql(sql, params, Route.CONN_CRM_CG);
	}

	/**
	 * 根据成员用户ID查询UU失效关系
	 * 
	 * @param userIdB
	 * @param relationTyeCode
	 * @param roleCodeB
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryRelaInfoByUserIdBEnd(String userIdB, String relationTyeCode, String roleCodeB) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);
		param.put("RELATION_TYPE_CODE", relationTyeCode);
		param.put("ROLE_CODE_B", roleCodeB);

		return Dao.qryByCodeParser("TF_F_RELATION_UU", "SEL_GRP_BY_USERIDB_ENDDATE", param);
	}

	/**
	 * 查询网外成员号码
	 * 
	 * @author tengg
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryRelaOutNetInfo(String userIdA, String serialNumberB, String shortCode, String relationTypeCode, Pagination page, String routeId) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("USER_ID_A", userIdA);
		inparams.put("SERIAL_NUMBER_B", serialNumberB);
		inparams.put("SHORT_CODE", shortCode);
		inparams.put("RELATION_TYPE_CODE", relationTypeCode);

		return Dao.qryByCodeParser("TF_F_RELATION_UU", "SEL_BY_OUTNET", inparams, page, routeId);
	}

	/**
	 * 查询UU crm all
	 * 
	 * @param userIdA
	 * @param userIdB
	 * @param relationTypeCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryRelationUU(String userIdA, String relationTypeCode) throws Exception
	{

		IData params = new DataMap();
		params.put("USER_ID_A", userIdA);
		params.put("RELATION_TYPE_CODE", relationTypeCode);

		SQLParser parser = new SQLParser(params);

		parser.addSQL(" SELECT A.PARTITION_ID, ");
		parser.addSQL(" TO_CHAR(A.USER_ID_A) USER_ID_A, ");
		parser.addSQL(" A.SERIAL_NUMBER_A, ");
		parser.addSQL(" TO_CHAR(A.USER_ID_B) USER_ID_B, ");
		parser.addSQL(" A.SERIAL_NUMBER_B, ");
		parser.addSQL(" A.RELATION_TYPE_CODE, ");
		parser.addSQL(" A.ROLE_CODE_A, ");
		parser.addSQL(" A.ROLE_CODE_B, ");
		parser.addSQL(" A.ORDERNO, ");
		parser.addSQL(" A.SHORT_CODE, ");
		parser.addSQL(" TO_CHAR(A.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
		parser.addSQL(" TO_CHAR(A.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE ");
		parser.addSQL(" FROM TF_F_RELATION_UU A,TF_F_USER_PAYPLAN B ");
		parser.addSQL(" WHERE 1 = 1 ");
		parser.addSQL(" AND A.USER_ID_A= :USER_ID_A ");
		parser.addSQL(" AND A.RELATION_TYPE_CODE = :RELATION_TYPE_CODE ");
		parser.addSQL(" AND sysdate BETWEEN A.START_DATE AND A.END_DATE ");
		parser.addSQL(" AND B.USER_ID = A.USER_ID_B ");
		parser.addSQL(" AND B.PARTITION_ID=MOD(TO_NUMBER(A.USER_ID_B),10000) ");
		parser.addSQL(" AND B.PLAN_TYPE_CODE= 'G' ");
		parser.addSQL(" AND B.START_DATE < SYSDATE ");
		parser.addSQL(" AND B.END_DATE > SYSDATE ");

		return Dao.qryByParseAllCrm(parser, true);
	}

	/**
	 * 查询UU crm all
	 * 
	 * @param userIdA
	 * @param userIdB
	 * @param relationTypeCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryRelationUUAll(String userIdA, String userIdB, String relationTypeCode) throws Exception
	{

		IData params = new DataMap();
		params.put("USER_ID_A", userIdA);
		params.put("USER_ID_B", userIdB);
		params.put("RELATION_TYPE_CODE", relationTypeCode);

		SQLParser parser = new SQLParser(params);

		parser.addSQL(" SELECT PARTITION_ID, ");
		parser.addSQL(" TO_CHAR(USER_ID_A) USER_ID_A, ");
		parser.addSQL(" SERIAL_NUMBER_A, ");
		parser.addSQL(" TO_CHAR(USER_ID_B) USER_ID_B, ");
		parser.addSQL(" SERIAL_NUMBER_B, ");
		parser.addSQL(" RELATION_TYPE_CODE, ");
		parser.addSQL(" ROLE_CODE_A, ");
		parser.addSQL(" ROLE_CODE_B, ");
		parser.addSQL(" ORDERNO, ");
		parser.addSQL(" SHORT_CODE, ");
		parser.addSQL(" TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
		parser.addSQL(" TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE ");
		parser.addSQL("FROM TF_F_RELATION_UU ");
		parser.addSQL("WHERE 1 = 1 ");
		parser.addSQL("AND USER_ID_A = TO_NUMBER(:USER_ID_A) ");
		parser.addSQL("AND USER_ID_B = TO_NUMBER(:USER_ID_B) ");
		parser.addSQL("AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID_B), 10000) ");
		parser.addSQL("AND RELATION_TYPE_CODE = :RELATION_TYPE_CODE ");
		parser.addSQL("AND SYSDATE BETWEEN START_DATE AND END_DATE ");
		parser.addSQL("AND END_DATE > LAST_DAY(TRUNC(SYSDATE)) + 1 - 1 / 24 / 3600 ");

		return Dao.qryByParseAllCrm(parser, true);
	}

	/**
	 * VPMN非6短号用户查询，适用于多个userIdA的查询
	 * 
	 * @param userIdA
	 * @param relationTypeCode
	 * @return
	 * @throws Exception
	 */

	public static IDataset qryRelaUUByInUIdaRemoveSix(String userIdAs, String relationTypeCode, Pagination page, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("RELATION_TYPE_CODE", relationTypeCode);

		StringBuilder sql = new StringBuilder(2000);

		sql.append("SELECT A.SERIAL_NUMBER_B, A.SHORT_CODE, A.SERIAL_NUMBER_A, B.VPN_NAME, ");
		sql.append("B.CUST_MANAGER, C.CITY_CODE ");
		sql.append("FROM TF_F_RELATION_UU A, TF_F_USER_VPN B, TF_F_USER C ");
		sql.append("WHERE A.USER_ID_A = B.USER_ID ");
		sql.append("AND C.USER_ID = B.USER_ID ");
		sql.append("AND B.PARTITION_ID = MOD(A.USER_ID_A, 10000) ");
		sql.append("AND C.PARTITION_ID = B.PARTITION_ID ");
		sql.append("AND A.USER_ID_A in ( ");
		sql.append(userIdAs);
		sql.append(") ");
		sql.append("AND A.RELATION_TYPE_CODE = :RELATION_TYPE_CODE ");
		sql.append("AND B.REMOVE_TAG = '0' ");
		sql.append("AND C.REMOVE_TAG = '0' ");
		sql.append("AND A.END_DATE > SYSDATE ");
		sql.append("AND (TRIM(A.SHORT_CODE) IS NULL OR SUBSTR(A.SHORT_CODE, 1, 1) != '6' OR ");
		sql.append("LENGTH(A.SHORT_CODE) < 3 OR LENGTH(A.SHORT_CODE) > 6) ");

		IDataset ds = Dao.qryBySql(sql, param, page, routeId);
		return ds;
	}

	/**
	 * 根据USER_ID_A、RELATION_TYPE_CODE查询所有CRM库内的UU表关系
	 * 
	 * @param userIdA
	 * @param relationTypeCode
	 * @return
	 * @throws Exception
	 *             wangjx 2013-7-19
	 */
	public static IDataset qryRelaUUByUIdAAllDB(String userIdA, String relationTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		return Dao.qryByCodeAllCrm("TF_F_RELATION_UU", "SEL_ALL_BY_USERIDA", param, true);
	}

	/**
	 * VPMN非6短号用户查询，这里本应使用qryByCodeAllCrm，但海南只有壹个库，考虑到分页，就使用qryByCode了
	 * 
	 * @param userIdA
	 * @param relationTypeCode
	 * @return
	 * @throws Exception
	 */

	public static IDataset qryRelaUUByUIdaRemoveSix(String userIdA, String relationTypeCode, Pagination pg, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		IDataset ds = Dao.qryByCode("TF_F_RELATION_UU", "SEL_REMOVESIX_BY_USERIDA", param, pg, routeId);
		return ds;
	}

	public static IDataset qryRelaUUByUserIdA(String userIdA, String relationTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_ALL_BY_USERIDA", param);
	}

	public static IDataset qryRelaUUByUserIdBAndEndDate(String userId, String end_date) throws Exception
	{
		IData uuParam = new DataMap();

		uuParam.put("USER_ID_B", userId);
		uuParam.put("END_DATE", end_date);

		IDataset uuSet = Dao.qryByCode("TF_F_RELATION_UU", "SEL_ALL_BY_USERIDB_ENDDATE", uuParam);
		return uuSet;

	}

	public static IDataset qryRelaUUInfo(String userIdA, String relationTypeCode, Pagination page) throws Exception
	{

		IData params = new DataMap();
		params.put("USER_ID_A", userIdA);
		params.put("RELATION_TYPE_CODE", relationTypeCode);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT 1 FROM tf_f_relation_uu");
		sql.append(" WHERE 1 = 1");
		sql.append(" AND user_id_a= :USER_ID_A");
		sql.append(" AND relation_type_code=:RELATION_TYPE_CODE");
		sql.append(" AND short_code=:SHORT_CODE");
		sql.append(" AND end_date > sysdate");
		return Dao.qryBySql(sql, params, page);
	}

	/**
	 * 查询所有库的集团成员
	 * 
	 * @param data
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryRelaUUInfoAllDb(IData data) throws Exception
	{
		SQLParser parser = new SQLParser(data);
		parser.addSQL(" SELECT partition_id, ");
		parser.addSQL(" to_char(user_id_a) user_id_a, ");
		parser.addSQL(" serial_number_a, ");
		parser.addSQL(" to_char(user_id_b) user_id_b, ");
		parser.addSQL(" serial_number_b, ");
		parser.addSQL(" relation_type_code, ");
		parser.addSQL(" role_code_a, ");
		parser.addSQL(" role_code_b, ");
		parser.addSQL(" orderno, ");
		parser.addSQL(" short_code, ");
		parser.addSQL(" to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date, ");
		parser.addSQL(" to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date ");
		parser.addSQL(" FROM tf_f_relation_uu uu ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" AND uu.user_id_a = TO_NUMBER(:USER_ID_A) ");
		parser.addSQL(" AND uu.SERIAL_NUMBER_B = :SERIAL_NUMBER_B ");
		parser.addSQL(" AND uu.SHORT_CODE = :SHORT_CODE ");
		parser.addSQL(" AND uu.ROLE_CODE_B = :ROLE_CODE_B ");
		parser.addSQL(" AND not (uu.relation_type_code='26' AND uu.ROLE_CODE_B = '9') ");
		parser.addSQL(" AND SYSDATE BETWEEN uu.start_date AND uu.end_date ");

		IDataset dataset = Dao.qryByParseAllCrm(parser, true);
		for (int iIndex = 0; iIndex < dataset.size(); iIndex++)
		{
			IData id = dataset.getData(iIndex);

			// relation
			String strRelaTypeCode = id.getString("RELATION_TYPE_CODE");
			String strRelaTypeName = URelaTypeInfoQry.getRoleTypeNameByRelaTypeCode(strRelaTypeCode);

			String strRoleBCode = id.getString("ROLE_CODE_B");
			String strRoleBName = URelaRoleInfoQry.getRoleBNameByRelaTypeCodeRoleCodeB(strRelaTypeCode, strRoleBCode);

			id.put("RELATION_TYPE_NAME", strRelaTypeName);
			id.put("ROLE_CODE_B_NAME", strRoleBName);
		}

		return dataset;
	}

	public static IDataset qryRelaUUInfoAllDb(String userIdA, String serialNumberB, String shortCode, Pagination page) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID_A", userIdA);
		params.put("SERIAL_NUMBER_B", serialNumberB);
		params.put("SHORT_CODE", shortCode);

		SQLParser parser = new SQLParser(params);
		parser.addSQL(" SELECT partition_id, ");
		parser.addSQL(" to_char(user_id_a) user_id_a, ");
		parser.addSQL(" serial_number_a, ");
		parser.addSQL(" to_char(user_id_b) user_id_b, ");
		parser.addSQL(" serial_number_b, ");
		parser.addSQL(" relation_type_code, ");
		parser.addSQL(" role_code_a, ");
		parser.addSQL(" role_code_b, ");
		parser.addSQL(" orderno, ");
		parser.addSQL(" short_code, ");
		parser.addSQL(" to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date, ");
		parser.addSQL(" to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date ");
		parser.addSQL(" FROM tf_f_relation_uu uu ");
		parser.addSQL(" WHERE uu.user_id_a = TO_NUMBER(:USER_ID_A) ");
		parser.addSQL(" AND uu.SERIAL_NUMBER_B = :SERIAL_NUMBER_B ");
		parser.addSQL(" AND uu.SHORT_CODE = :SHORT_CODE ");
		parser.addSQL(" AND SYSDATE BETWEEN uu.start_date AND uu.end_date ");

		return Dao.qryByParseAllCrm(parser, false);
	}

	public static IDataset qryRelaUUInfoByUserIdAB(String userIdA, String userIdB, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("USER_ID_B", userIdB);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT partition_id,to_char(user_id_a) user_id_a,serial_number_a,to_char(user_id_b) user_id_b,serial_number_b,relation_type_code,role_code_a,role_code_b,orderno,short_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(inst_id) inst_id ");
		parser.addSQL(" FROM tf_f_relation_uu where 1=1");
		parser.addSQL(" and user_id_a = TO_NUMBER(:USER_ID_A) ");
		parser.addSQL(" and user_id_b = :USER_ID_B");
		parser.addSQL(" and partition_id = mod(to_number(:USER_ID_B),10000)");
		parser.addSQL(" AND sysdate between start_date and end_date ");
		IDataset dataset = Dao.qryByParse(parser, routeId);
		return dataset;
	}

	/**
	 * 查询指定数据库的集团成员
	 * 
	 * @param data
	 * @param routeId
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryRelaUUInfoTheDb(IData data, Pagination pagination) throws Exception
	{
		SQLParser parser = new SQLParser(data);

		parser.addSQL(" SELECT /*+ index(UU IDX_TF_F_RELATION_UU_UID) */ partition_id, ");
		parser.addSQL(" to_char(user_id_a) user_id_a, ");
		parser.addSQL(" serial_number_a, ");
		parser.addSQL(" to_char(user_id_b) user_id_b, ");
		parser.addSQL(" serial_number_b, ");
		parser.addSQL(" relation_type_code, ");
		parser.addSQL(" role_code_a, ");
		parser.addSQL(" role_code_b, ");
		parser.addSQL(" orderno, ");
		parser.addSQL(" short_code, ");
		parser.addSQL(" to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date, ");
		parser.addSQL(" to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date ");
		parser.addSQL(" FROM tf_f_relation_uu uu ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" AND uu.user_id_a = TO_NUMBER(:USER_ID_A) ");
		parser.addSQL(" AND uu.SERIAL_NUMBER_B = :SERIAL_NUMBER_B ");
		parser.addSQL(" AND uu.SHORT_CODE = :SHORT_CODE ");
		parser.addSQL(" AND uu.ROLE_CODE_B = :ROLE_CODE_B ");
		parser.addSQL(" AND SYSDATE BETWEEN uu.start_date AND uu.end_date ");

		IDataset dataset = Dao.qryByParse(parser, pagination);

		String strRelaTypeName = "";
		String strRelaTypeCode = "";

		for (int iIndex = 0; iIndex < dataset.size(); iIndex++)
		{
			IData id = dataset.getData(iIndex);

			// relation
			if (StringUtils.isEmpty(strRelaTypeCode))
			{
				strRelaTypeCode = id.getString("RELATION_TYPE_CODE");
				strRelaTypeName = URelaTypeInfoQry.getRoleTypeNameByRelaTypeCode(strRelaTypeCode);
			}

			String strRoleBCode = id.getString("ROLE_CODE_B");
			String strRoleBName = URelaRoleInfoQry.getRoleBNameByRelaTypeCodeRoleCodeB(strRelaTypeCode, strRoleBCode);

			id.put("RELATION_TYPE_NAME", strRelaTypeName);
			id.put("ROLE_CODE_B_NAME", strRoleBName);
		}

		return dataset;
	}

	/**
	 * 查母VPMN下的子VPMN
	 * 
	 * @param param
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset qrySonVpmnSnByUserIdAForGrp(String userIdA) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT uu.serial_number_b ");
		parser.addSQL(" FROM tf_f_user_vpn vpn,tf_f_relation_uu uu ");
		parser.addSQL(" WHERE  vpn.USER_ID=uu.user_id_b ");
		parser.addSQL(" AND uu.user_id_a = to_number( :USER_ID_A ) ");
		parser.addSQL(" and vpn.PARTITION_ID=uu.partition_id ");
		parser.addSQL(" AND uu.relation_type_code = '40' ");
		parser.addSQL(" AND uu.end_date>SYSDATE ");
		parser.addSQL(" and vpn.Remove_Tag <> '1' ");
		return Dao.qryByParse(parser, Route.CONN_CRM_CG);
	}

	public static IDataset qryUU(String userIdA, String userIdB, String relationTypeCode, Pagination page) throws Exception
	{
		return qryUU(userIdA, userIdB, relationTypeCode, page, null);
	}

	public static IDataset qryUU(String userIdA, String userIdB, String relationTypeCode, Pagination page, String routeId) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID_A", userIdA);
		params.put("USER_ID_B", userIdB);
		params.put("RELATION_TYPE_CODE", relationTypeCode);

		SQLParser parser = new SQLParser(params);

		parser.addSQL("SELECT PARTITION_ID, TO_CHAR(USER_ID_A) USER_ID_A, ");
		parser.addSQL("SERIAL_NUMBER_A, TO_CHAR(USER_ID_B) USER_ID_B, ");
		parser.addSQL("SERIAL_NUMBER_B, RELATION_TYPE_CODE, ROLE_TYPE_CODE, ");
		parser.addSQL("ROLE_CODE_A, ROLE_CODE_B, ORDERNO, SHORT_CODE, ");
		parser.addSQL("TO_CHAR(INST_ID) INST_ID, ");
		parser.addSQL("TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
		parser.addSQL("TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
		parser.addSQL("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
		parser.addSQL("UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, ");
		parser.addSQL("RSRV_NUM2, RSRV_NUM3, TO_CHAR(RSRV_NUM4) RSRV_NUM4, ");
		parser.addSQL("TO_CHAR(RSRV_NUM5) RSRV_NUM5, RSRV_STR1, RSRV_STR2, ");
		parser.addSQL("RSRV_STR3, RSRV_STR4, RSRV_STR5, ");
		parser.addSQL("TO_CHAR(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
		parser.addSQL("TO_CHAR(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
		parser.addSQL("TO_CHAR(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
		parser.addSQL("RSRV_TAG1, RSRV_TAG2, RSRV_TAG3 ");
		parser.addSQL("FROM TF_F_RELATION_UU ");
		parser.addSQL("WHERE 1 = 1 ");
		parser.addSQL("AND USER_ID_A = TO_NUMBER(:USER_ID_A) ");
		parser.addSQL("AND USER_ID_B = TO_NUMBER(:USER_ID_B) ");
		parser.addSQL("AND PARTITION_ID = TO_NUMBER(MOD(TO_NUMBER(:USER_ID_B), 10000)) ");
		parser.addSQL("AND RELATION_TYPE_CODE = :RELATION_TYPE_CODE ");
		parser.addSQL("AND SYSDATE BETWEEN START_DATE AND END_DATE ");
		parser.addSQL("AND END_DATE > LAST_DAY(TRUNC(SYSDATE)) + 1 - 1 / 24 / 3600 ");

		return Dao.qryByParse(parser, page, routeId);
	}

	/**
	 * 获取新校讯通异网号与付费号UU关系
	 */

	public static IDataset qryUUBySnBUserIdRelatypeInfo(String outSN, String mainSn, String mebUserId, String relationTypeCode, String ecUserId) throws Exception
	{
		IData relaParam = new DataMap();
		relaParam.put("SERIAL_NUMBER_A", outSN);
		relaParam.put("SERIAL_NUMBER_B", mainSn);
		relaParam.put("USER_ID_B", mebUserId);
		relaParam.put("RELATION_TYPE_CODE", relationTypeCode);
		relaParam.put("EC_USER_ID", ecUserId);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_XXT_BY__SNA__SNB_USERIDB_TYPE", relaParam);
	}

	public static IDataset qryUUForGrp(String userIdA, String userIdB, String relationTypeCode, Pagination page) throws Exception
	{
		return qryUU(userIdA, userIdB, relationTypeCode, page, Route.CONN_CRM_CG);
	}

	public static IDataset qryUUInfoAllCrmByUserIdA(String userIdA, Pagination page) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID_A", userIdA);

		return Dao.qryByCodeParserAllCrm("TF_F_RELATION_UU", "SEL_MEMBER_BY_IDA", inparam, page, true);
	}

	/**
	 * 根据user_id_a查询家庭关系
	 * 
	 * @param userIdA
	 * @param routeId
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryUUPDByUIdARType(String userIdA, String relationTypeCode, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("RELATION_TYPE_CODE", relationTypeCode);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_ALLFRIEND_BY_UIDA_CODE", param, routeId);
	}

	/**
	 * 家庭网，全库查询
	 * 
	 * @param userIdA
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryUUPDListBy4DBAll(String userIdA, String realtionTypeCode) throws Exception
	{
		String[] connNames = Route.getAllCrmDb();
		if (connNames == null)
		{
			return qryUUPDByUIdARType(userIdA, realtionTypeCode, null);
		}

		IDataset listResult = new DatasetList();
		IDataset listTmp = null;

		String routeId = "";
		int count = connNames.length;

		for (int index = 0; index < count; index++)
		{
			routeId = connNames[index];

			listTmp = qryUUPDByUIdARType(userIdA, realtionTypeCode, routeId);

			if (!listResult.containsAll(listTmp))
			{
				listResult.addAll(listTmp);
			}
		}

		return listResult;
	}

	/**
	 * 获取新校讯通异网号与付费号UU关系
	 */

	public static IDataset qryXxtUUInfo(String outSn, String mebUserId, String GrpUserid) throws Exception
	{
		IData relaParam = new DataMap();
		relaParam.put("SERIAL_NUMBER_A", outSn);
		relaParam.put("USER_ID_B", mebUserId);
		relaParam.put("RSRV_STR1", GrpUserid);
		relaParam.put("ROLE_CODE_A", "1");
		relaParam.put("ROLE_CODE_B", "1");
		relaParam.put("USER_ID", "1");
		relaParam.put("RELATION_TYPE_CODE", "XT");// 注销姓名

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_BY_USERIDA_SNA_TYPE2", relaParam);
	}

	/**
	 * 根据编号查询挂账集团成员信息
	 * 
	 * @author liaoyi
	 * @param params
	 *            查询所需参数
	 * @param pagination
	 * @return IDataset 客户标志资料列表
	 * @throws Exception
	 */
	public static IDataset queryAccountUniteGrpMem(IData data) throws Exception
	{

		SQLParser parser = new SQLParser(data);
		if (ProvinceUtil.isProvince(ProvinceUtil.XINJ) || ProvinceUtil.isProvince(ProvinceUtil.SHXI))
		{
			parser.addSQL("SELECT PARTITION_ID,USER_ID_A,SERIAL_NUMBER_A,USER_ID_B,SERIAL_NUMBER_B,RELATION_TYPE_CODE,ROLE_TYPE_CODE,ROLE_CODE_A,ROLE_CODE_B,ORDERNO,SHORT_CODE,INST_ID,to_char(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,to_char(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,to_char(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3 FROM TF_F_RELATION_UU UU ");
		} else
		{
			parser.addSQL("SELECT PARTITION_ID,USER_ID_A,SERIAL_NUMBER_A,USER_ID_B,SERIAL_NUMBER_B SERIAL_NUMBER,RELATION_TYPE_CODE,ROLE_TYPE_CODE,ROLE_CODE_A,ROLE_CODE_B,ORDERNO,SHORT_CODE,INST_ID,to_char(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,to_char(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,to_char(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3 FROM TF_F_RELATION_UU UU ");
		}
		parser.addSQL(" WHERE 1=1");
		parser.addSQL(" AND UU.USER_ID_A = :USER_ID_A");
		parser.addSQL(" AND UU.USER_ID_B = :USER_ID_B");
		parser.addSQL(" AND SYSDATE BETWEEN UU.START_DATE AND UU.END_DATE");
		return Dao.qryByParse(parser);
	}

	/**
	 * 根据编号查询挂账集团信息
	 * 
	 * @author liaoyi
	 * @param params
	 *            查询所需参数
	 * @param pagination
	 * @return IDataset 客户标志资料列表
	 * @throws Exception
	 */
	public static IDataset queryAccountUniteGrpMems(IData data, Pagination pagination) throws Exception
	{

		SQLParser parser = new SQLParser(data);
		parser.addSQL("SELECT PARTITION_ID,USER_ID_A,SERIAL_NUMBER_A,USER_ID_B,SERIAL_NUMBER_B,RELATION_TYPE_CODE,ROLE_TYPE_CODE,ROLE_CODE_A,ROLE_CODE_B,ORDERNO,SHORT_CODE,INST_ID,to_char(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,to_char(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,to_char(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3 FROM TF_F_RELATION_UU UU ");
		parser.addSQL(" WHERE 1=1");
		parser.addSQL(" AND UU.USER_ID_A = :USER_ID_A");
		parser.addSQL(" AND SYSDATE BETWEEN UU.START_DATE AND UU.END_DATE");
		return Dao.qryByParse(parser, pagination);
	}

	/**
	 * 根据编号查询挂账集团信息
	 * 
	 * @author liaoyi
	 * @param params
	 *            查询所需参数
	 * @param pagination
	 * @return IDataset 客户标志资料列表
	 * @throws Exception
	 */
	public static IDataset queryAccountUniteGrpMemsCustname(IData data, Pagination pagination) throws Exception
	{

		SQLParser parser = new SQLParser(data);
		parser.addSQL("SELECT UU.PARTITION_ID,UU.USER_ID_A,UU.SERIAL_NUMBER_A,UU.USER_ID_B,UU.SERIAL_NUMBER_B,UU.RELATION_TYPE_CODE,UU.ROLE_TYPE_CODE,UU.ROLE_CODE_A,UU.ROLE_CODE_B,UU.ORDERNO,UU.SHORT_CODE,UU.INST_ID,to_char(UU.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,to_char(UU.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,to_char(UU.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,UU.UPDATE_STAFF_ID,UU.UPDATE_DEPART_ID,UU.REMARK,UU.RSRV_NUM1,UU.RSRV_NUM2,UU.RSRV_NUM3,UU.RSRV_NUM4,UU.RSRV_NUM5,UU.RSRV_STR1,UU.RSRV_STR2,UU.RSRV_STR3,UU.RSRV_STR4,UU.RSRV_STR5,UU.RSRV_DATE1,UU.RSRV_DATE2,UU.RSRV_DATE3,UU.RSRV_TAG1,UU.RSRV_TAG2,UU.RSRV_TAG3,C.CUST_NAME FROM TF_F_RELATION_UU UU,TF_F_USER U,TF_F_CUSTOMER C ");
		parser.addSQL(" WHERE 1=1");
		parser.addSQL(" AND UU.USER_ID_A = :USER_ID_A");
		parser.addSQL(" AND UU.USER_ID_B = U.USER_ID");
		parser.addSQL(" AND UU.RELATION_TYPE_CODE = 70");
		parser.addSQL(" AND U.PARTITION_ID = MOD(UU.USER_ID_B,10000)");
		parser.addSQL(" AND U.REMOVE_TAG = '0'");
		parser.addSQL(" AND C.PARTITION_ID = MOD(U.CUST_ID,10000)");
		parser.addSQL(" AND U.CUST_ID = C.CUST_ID");
		parser.addSQL(" AND SYSDATE BETWEEN UU.START_DATE AND UU.END_DATE");
		parser.addSQL(" ORDER BY UU.START_DATE");
		return Dao.qryByParse(parser, pagination);
	}

	/**
	 * 查询ADC成员订购信息
	 * 
	 * @@author fengsl
	 * @date 2013-02-26
	 * @param param
	 * @return
	 * @throws Exception
	 */

	public static IDataset queryAdcMebOrderInfo(String userIdB, String serialNumber, String groupId, String productId, Pagination pagination) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID_B", userIdB);
		params.put("SERIAL_NUMBER", serialNumber);
		params.put("GROUP_ID", groupId);
		params.put("PRODUCT_ID", productId);
		SQLParser parser = new SQLParser(params);
		parser.addSQL(" Select mu.user_id,uu.user_id_a, cg.group_id,cg.group_contact_phone PHONE,cg.cust_name GROUP_CUST_NAME,cp.cust_name MEM_CUST_NAME,pcg.product_id,pcg.product_name GROUP_PRODUCT_NAME,pmb.product_name MEM_PRODUCT_NAME,mu.serial_number MEM_SERIAL_NUMBER ,scg.data_name ROL_TYPE,smb.data_name ROL_NAME,uu.relation_type_code ");
		parser.addSQL(" From tf_f_cust_group cg,tf_f_user cgu,td_s_static scg,td_b_product pcg,tf_f_relation_uu uu,tf_f_user mu,tf_f_cust_person cp,td_s_static smb,td_b_product pmb ");
		parser.addSQL(" Where 1=1 ");
		parser.addSQL(" And uu.serial_number_b=:SERIAL_NUMBER ");
		parser.addSQL("And (uu.relation_type_code='89' or uu.relation_type_code='86') ");
		parser.addSQL(" And uu.User_Id_a=cgu.user_id ");
		parser.addSQL(" And cgu.cust_id=cg.cust_id ");
		parser.addSQL(" And uu.user_id_b=mu.user_id ");
		parser.addSQL(" And cp.cust_id=mu.cust_id ");
		parser.addSQL(" And cgu.remove_tag='0' ");
		parser.addSQL(" And mu.remove_tag='0' ");
		parser.addSQL(" And uu.end_date>last_day(trunc(sysdate))+1-1/24/3600 ");
		parser.addSQL(" And scg.type_id='TD_S_RELATION_ROLE_1_'||uu.relation_type_code ");
		parser.addSQL(" And scg.data_id=uu.role_code_a ");
		parser.addSQL(" And smb.type_id='TD_S_RELATION_ROLE_1_'||uu.relation_type_code ");
		parser.addSQL(" And smb.data_id=uu.role_code_b ");
		parser.addSQL(" And pcg.product_id=cgu.product_id ");
		parser.addSQL(" And pmb.product_id=mu.product_id ");
		parser.addSQL(" And uu.User_Id_a=:USER_ID_A ");
		parser.addSQL(" And cg.GROUP_ID=:GROUP_ID ");
		parser.addSQL(" And cgu.PRODUCT_ID=:PRODUCT_ID ");
		return Dao.qryByParse(parser, pagination);

	}

	/**
	 * 获取所有的家庭统付关系成员信息，不包括主号信息
	 * 
	 * @param userIdA
	 * @param relationTypeCode
	 * @param roleCodeB
	 * @return
	 * @throws Exception
	 * @CREATE BY GONGP@2014-5-27
	 */
	public static IDataset queryAllUnionPayMembers(String userIdA, String relationTypeCode, String roleCodeB) throws Exception
	{
		IData params = new DataMap();

		params.put("USER_ID_A", userIdA);
		params.put("RELATION_TYPE_CODE", relationTypeCode);
		params.put("ROLE_CODE_B", roleCodeB);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_FAMLIYINFO_HAIN2", params);
	}
	/**
	 * 获取所有的家庭统付关系成员信息,包括本月底截止的成员，不包括主号信息，
	 * 
	 * @param userIdA
	 * @param relationTypeCode
	 * @param roleCodeB
	 * @return
	 * @throws Exception
	 * @CREATE BY GONGP@2014-5-27
	 */
	public static IDataset queryAllMembersAndEndMonth(String userIdA, String relationTypeCode, String roleCodeB) throws Exception
	{
		IData params = new DataMap();
		
		params.put("USER_ID_A", userIdA);
		params.put("RELATION_TYPE_CODE", relationTypeCode);
		params.put("ROLE_CODE_B", roleCodeB);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_VALID_FMLUNPAY_MEMBERS_AND_THISMONTH", params);
	}

	/**
	 * 查询历史家庭产品关系
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryClusterHisProductRelation(String userIdB, Pagination pagination) throws Exception
	{

		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);

		SQLParser parser = new SQLParser(param);

		parser.addSQL(" SELECT t.* FROM tf_f_relation_uu t where t.relation_type_code=:RELATION_TYPE_CODE ");
		parser.addSQL(" and t.end_date < sysdate ");
		parser.addSQL(" and t.user_id_b=:USER_ID_B ");
		parser.addSQL(" AND t.partition_id=MOD(TO_NUMBER(:USER_ID_B),10000) ");

		return Dao.qryByParse(parser, pagination);
	}

	/**
	 * 根据UserIda查询被销户时被终止的数据
	 * 
	 * @param userIdA
	 * @param roleCodeB
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryDestroyRelaDataByUserIdA(String userIdA, String roleCodeB) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("ROLE_CODE_B", roleCodeB);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_DESTROY_END_DATE", param);
	}

	public static IDataset queryFamilySub(String serNum) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serNum);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_FOR_FAMILY_INTERFACE", param);
	}

	/**
	 * 获取有效的家庭统付成员关系
	 * 
	 * @param userIdA
	 * @param relaTypeCode
	 *            56
	 * @param RoleCodeB
	 *            2
	 * @return
	 * @throws Exception
	 * @CREATE BY GONGP@2014-6-18
	 */
	public static IDataset queryForeverValidUnionPayMembers(String userIdA, String relaTypeCode, String RoleCodeB) throws Exception
	{
		IData params = new DataMap();

		params.put("USER_ID_A", userIdA);
		params.put("RELATION_TYPE_CODE", relaTypeCode);
		params.put("ROLE_CODE_B", RoleCodeB);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_VALID_FMLUNPAY_MEMBERS", params);
	}

	/**
	 * 查询GPRS行业应用 成员信息
	 * 
	 * @param param
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryGPRSUserInfos(String userIdA, String serialNumberB, Pagination pagination) throws Exception
	{

		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("SERIAL_NUMBER_B", serialNumberB);

		SQLParser parser = new SQLParser(param);
		parser.addSQL("  SELECT TO_CHAR(A.USER_ID_A) USER_ID_A ,TO_CHAR(A.USER_ID_B) USER_ID_B ,A.SERIAL_NUMBER_A,A.SERIAL_NUMBER_B,A.RELATION_TYPE_CODE,TO_CHAR(B.USER_ID) USER_ID, ");
		parser.addSQL("  		B.DISCNT_CODE,B.SPEC_TAG,TO_CHAR(B.START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(B.END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE ");
		parser.addSQL(" FROM TF_F_RELATION_UU A , TF_F_USER_DISCNT B  ");
		parser.addSQL(" WHERE 1=1  ");
		parser.addSQL(" AND B.USER_ID = A.USER_ID_B ");
		parser.addSQL(" AND B.PARTITION_ID=MOD(A.USER_ID_B,10000) ");
		// parser.addSQL(" AND A.USER_ID_A = B.USER_ID_A ");
		parser.addSQL(" AND A.END_DATE > last_day(trunc(sysdate))+1-1/24/3600 ");
		parser.addSQL(" AND SYSDATE BETWEEN B.START_DATE AND B.END_DATE ");
		parser.addSQL(" AND A.USER_ID_A= :USER_ID_A ");
		parser.addSQL(" AND A.SERIAL_NUMBER_B= :SERIAL_NUMBER_B ");

		return null;// Dao.qryByParse(parser, pagination, Route.CONN_CRM_1);
	}

	/**
	 * 查询不能办理家庭统付的UU关系
	 * 
	 * @param userId
	 *            :user_id_b
	 * @param roleCodeB
	 *            :对应tf_f_relation_uu.role_code_b, 1-主号， 2-副号
	 * @param param_code
	 *            :对应td_s_commpara.param_code, 1-主号限制， 2-副号限制
	 * @return
	 * @throws Exception
	 * @CREATE BY GONGP@2014-5-21
	 */
	public static IDataset queryLimitUUInfos(String userId, String roleCodeB, String param_code) throws Exception
	{
		IData iparam = new DataMap();
		iparam.put("USER_ID", userId);
		iparam.put("ROLE_CODE_B", roleCodeB);
		iparam.put("PARAM_CODE", param_code);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_LIMIT_UU_BY_USERIDB", iparam);
	}

	public static IDataset queryMemProductByCode20(String userIdB, String serialNumber, String groupId, String productId, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("GROUP_ID", groupId);
		param.put("PRODUCT_ID", productId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" Select mu.user_id,uu.user_id_a, cg.group_id,cg.group_contact_phone PHONE,cg.cust_name GROUP_CUST_NAME,cp.cust_name MEM_CUST_NAME,pcg.product_id,pcg.product_name GROUP_PRODUCT_NAME,pmb.product_name MEM_PRODUCT_NAME,mu.serial_number MEM_SERIAL_NUMBER,uu.short_code SHORT_NUMBER,scg.data_name ROL_TYPE,smb.data_name ROL_NAME ");
		parser.addSQL(" From tf_f_cust_group cg,tf_f_user cgu,td_s_static scg,td_b_product pcg,tf_f_relation_uu uu,tf_f_user mu,tf_f_cust_person cp,td_s_static smb,td_b_product pmb ");
		parser.addSQL(" Where 1=1 ");
		parser.addSQL(" And uu.serial_number_b=:SERIAL_NUMBER ");
		parser.addSQL("And (uu.relation_type_code='20' Or uu.relation_type_code='21' ) ");
		parser.addSQL(" And uu.User_Id_a=cgu.user_id ");
		parser.addSQL(" And cgu.cust_id=cg.cust_id ");
		parser.addSQL(" And uu.user_id_b=mu.user_id ");
		parser.addSQL(" And cp.cust_id=mu.cust_id ");
		parser.addSQL(" And cgu.remove_tag='0' ");
		parser.addSQL(" And mu.remove_tag='0' ");
		parser.addSQL(" And uu.end_date>last_day(trunc(sysdate))+1-1/24/3600 ");
		parser.addSQL(" And scg.type_id='TD_S_RELATION_ROLE_1_'||uu.relation_type_code ");
		parser.addSQL(" And scg.data_id=uu.role_code_a ");
		parser.addSQL(" And smb.type_id='TD_S_RELATION_ROLE_1_'||uu.relation_type_code ");
		parser.addSQL(" And smb.data_id=uu.role_code_b ");
		parser.addSQL(" And pcg.product_id=cgu.product_id ");
		parser.addSQL(" And pmb.product_id=mu.product_id ");
		parser.addSQL(" And uu.User_Id_a=:USER_ID_A ");
		parser.addSQL(" And cg.GROUP_ID=:GROUP_ID ");
		parser.addSQL(" And cgu.PRODUCT_ID=:PRODUCT_ID ");
		return Dao.qryByParse(parser, pagination);
	}

	/**
	 * 查询用户订购集团彩铃成员产品
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryMemProductByCode26(String serialNumber, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" Select mu.user_id,uu.user_id_a, cg.group_id,cg.group_contact_phone PHONE,cg.cust_name GROUP_CUST_NAME,cp.cust_name MEM_CUST_NAME,pcg.product_name GROUP_PRODUCT_NAME,pmb.product_name MEM_PRODUCT_NAME,mu.serial_number MEM_SERIAL_NUMBER,uu.short_code SHORT_NUMBER,scg.data_name ROL_TYPE,smb.data_name ROL_NAME ");
		parser.addSQL(" From tf_f_cust_group cg,tf_f_user cgu,td_s_static scg,td_b_product pcg,tf_f_relation_uu uu,tf_f_user mu,tf_f_cust_person cp,td_s_static smb,td_b_product pmb ");
		parser.addSQL(" Where 1=1 ");
		parser.addSQL(" And uu.serial_number_b=:SERIAL_NUMBER ");
		parser.addSQL(" And uu.relation_type_code='26' ");
		parser.addSQL(" And uu.role_code_b='1' ");
		parser.addSQL(" And uu.User_Id_a=cgu.user_id ");
		parser.addSQL(" And cgu.cust_id=cg.cust_id ");
		parser.addSQL(" And uu.user_id_b=mu.user_id ");
		parser.addSQL(" And cp.cust_id=mu.cust_id ");
		parser.addSQL(" And cgu.remove_tag='0' ");
		parser.addSQL(" And mu.remove_tag='0' ");
		parser.addSQL(" And uu.end_date>last_day(trunc(sysdate))+1-1/24/3600 ");
		parser.addSQL(" And scg.type_id='TD_S_RELATION_ROLE_1_26' ");
		parser.addSQL(" And scg.data_id=uu.role_code_a ");
		parser.addSQL(" And smb.type_id='TD_S_RELATION_ROLE_1_26' ");
		parser.addSQL(" And smb.data_id=uu.role_code_b ");
		parser.addSQL(" And pcg.product_id=cgu.product_id ");
		parser.addSQL(" And pmb.product_id=mu.product_id ");
		return Dao.qryByParse(parser, pagination);
	}

	/**
	 * 查询用户订购移动总机成员产品
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */

	public static IDataset queryMemProductByUserID(String serialNumber, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" Select mu.user_id,uu.user_id_a, cg.group_id,cg.group_contact_phone PHONE,cg.cust_name GROUP_CUST_NAME,cp.cust_name MEM_CUST_NAME,pcg.product_name GROUP_PRODUCT_NAME,pmb.product_name MEM_PRODUCT_NAME,mu.serial_number MEM_SERIAL_NUMBER,uu.short_code SHORT_NUMBER,scg.data_name ROL_TYPE,smb.data_name ROL_NAME ");
		parser.addSQL(" From tf_f_cust_group cg,tf_f_user cgu,td_s_static scg,td_b_product pcg,tf_f_relation_uu uu,tf_f_user mu,tf_f_cust_person cp,td_s_static smb,td_b_product pmb ");
		parser.addSQL(" Where 1=1 ");
		parser.addSQL(" And uu.serial_number_b=:SERIAL_NUMBER ");
		parser.addSQL(" And uu.relation_type_code='25' ");
		parser.addSQL(" And uu.role_code_b<>'2' ");
		parser.addSQL(" And uu.User_Id_a=cgu.user_id ");
		parser.addSQL(" And cgu.cust_id=cg.cust_id ");
		parser.addSQL(" And uu.user_id_b=mu.user_id ");
		parser.addSQL(" And cp.cust_id=mu.cust_id ");
		parser.addSQL(" And cgu.remove_tag='0' ");
		parser.addSQL(" And mu.remove_tag='0' ");
		parser.addSQL(" And uu.end_date>last_day(trunc(sysdate))+1-1/24/3600 ");
		parser.addSQL(" And scg.type_id='TD_S_RELATION_ROLE_1_25' ");
		parser.addSQL(" And scg.data_id=uu.role_code_a ");
		parser.addSQL(" And smb.type_id='TD_S_RELATION_ROLE_1_25' ");
		parser.addSQL(" And smb.data_id=uu.role_code_b ");
		parser.addSQL(" And pcg.product_id=cgu.product_id ");
		parser.addSQL(" And pmb.product_id=mu.product_id ");
		return Dao.qryByParse(parser, pagination);
	}

	/**
	 * 根据编号查询挂账集团信息
	 * 
	 * @author liaoyi
	 * @param params
	 *            查询所需参数
	 * @param pagination
	 * @return IDataset 客户标志资料列表
	 * @throws Exception
	 */
	public static IDataset queryMemUU(IData data) throws Exception
	{

		SQLParser parser = new SQLParser(data);
		parser.addSQL(" SELECT u.SERIAL_NUMBER,u.USER_ID,cust.cust_name,cust.group_id ");
		parser.addSQL(" FROM TF_F_CUST_GROUP cust,TF_F_USER u,TF_F_RELATION_UU uu ");
		parser.addSQL(" WHERE 1=1");
		parser.addSQL(" AND cust.CUST_ID =u.CUST_ID ");
		parser.addSQL(" AND uu.USER_ID_B = TO_NUMBER(:USER_ID)");
		parser.addSQL(" AND uu.PARTITION_ID= MOD(TO_NUMBER(:USER_ID),10000) ");
		parser.addSQL(" AND end_date+0 > last_day(trunc(sysdate))+1-1/24/3600 ");
		parser.addSQL(" AND u.USER_ID = uu.USER_ID_A ");
		parser.addSQL(" AND uu.USER_ID_A = :USER_ID_A ");
		parser.addSQL(" AND u.PARTITION_ID = MOD(:USER_ID_A, 10000) ");
		parser.addSQL(" AND u.REMOVE_TAG='0' ");
		parser.addSQL(" AND u.product_id in ( SELECT product_id FROM TD_B_PTYPE_PRODUCT) ");
		return Dao.qryByParse(parser);
	}

	public static IDataset queryMenber4Test(String groupUserId, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("GROUP_USER_ID", groupUserId);

		SQLParser crmUuParser = new SQLParser(param);
		// crmUuParser.addSQL(" select t.serial_number_b,t.start_date,t.end_date
		// from uop_crm4.tf_f_relation_uu t where
		// t.user_id_a='"+groupUserId+"' and t.end_date>sysdate ");
		// //测试，上线需要修改
		crmUuParser.addSQL(" select t.serial_number_b,t.start_date,t.end_date  from uop_crm1.tf_f_relation_uu@DBLNK_NGCRMDB1 t where t.user_id_a=:GROUP_USER_ID and t.end_date>sysdate union ");
		crmUuParser.addSQL(" select t.serial_number_b,t.start_date,t.end_date  from uop_crm2.tf_f_relation_uu@DBLNK_NGCRMDB2 t where t.user_id_a=:GROUP_USER_ID and t.end_date>sysdate union ");
		crmUuParser.addSQL(" select t.serial_number_b,t.start_date,t.end_date  from uop_crm3.tf_f_relation_uu@DBLNK_NGCRMDB3 t where t.user_id_a=:GROUP_USER_ID and t.end_date>sysdate union ");
		crmUuParser.addSQL(" select t.serial_number_b,t.start_date,t.end_date  from uop_crm4.tf_f_relation_uu@DBLNK_NGCRMDB4 t where t.user_id_a=:GROUP_USER_ID and t.end_date>sysdate  ");
		return Dao.qryByParse(crmUuParser, pagination);

	}

	// 开户查询号码绑定关系 sunxin
	public static IDataset queryMMinfo(String serialNumber) throws Exception
	{

		IData param = new DataMap();
		param.put("SERIAL_NUMBER_B", serialNumber);

		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT T.* FROM TF_F_RELATION_MM T ");
		parser.addSQL(" WHERE T.SERIAL_NUMBER_B = :SERIAL_NUMBER_B");
		parser.addSQL(" AND T.CODE_STATE_CODE = '0' ");
		parser.addSQL(" AND SYSDATE BETWEEN T.START_DATE AND T.END_DATE ");
		return Dao.qryByParse(parser);

	}

	public static IDataset queryNewFamilySub(String userIdA) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_FOR_FAMILY_INTERFACE_NEW", param);
	}

	/**
	 * @author fengsl
	 * @date 2013-02-26
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryNextMonthBySnb(String serialNumberB, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER_B", serialNumberB);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_NEXTMONTH_BY_SNB", param, pagination);
	}

	/**
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryOneCardNormal(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_FOR_ONECARDNORMAL", param);
	}

	/**
	 * 根据UserIda查询成员信息
	 * 
	 * @param userIdA
	 * @param roleCodeB
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryRelationByUserIdA(String userIdA) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_ROLE_IDA", param);
	}

	/**
	 * 查询成员使用产品信息
	 * 
	 * @param param
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryRelationGroupInfo(IData param, Pagination pagination) throws Exception
	{

		IDataset memUseProductSet = new DatasetList();

		String serialNumber = param.getString("SERIAL_NUMBER");
		IDataset realUUInfoSet = queryUuBySerialNumberB(serialNumber, pagination);

		if (IDataUtil.isNotEmpty(realUUInfoSet))
		{
			for (int i = 0, rsize = realUUInfoSet.size(); i < rsize; i++)
			{
				IData relauuInfo = realUUInfoSet.getData(i); // serial_number_a,USER_ID_A,RELATION_TYPE_CODE,START_DATE,END_DATE
				String userID = relauuInfo.getString("USER_ID_A", "");

				IDataset grpCustProInfoset = GrpInfoQry.queryGrpProuctByUserId(userID, pagination);

				if (IDataUtil.isNotEmpty(grpCustProInfoset))
				{
					for (int n = 0, size = grpCustProInfoset.size(); n < size; n++)
					{
						IData memUseProduct = new DataMap();
						memUseProduct.putAll(relauuInfo);

						IData grpProCustInfo = grpCustProInfoset.getData(n); // GROUP_ID,CUST_NAME,PRODUCT_ID,CUST_MANAGER_ID
						String staffID = grpProCustInfo.getString("CUST_MANAGER_ID", "");
						if (!"".equals(staffID))
						{
							IDataset staffInfoset = StaffInfoQry.getStaffAreaInfoByID(staffID);
							IData staffInfo = staffInfoset.getData(0); // STAFF_NAME,SERIAL_NUMBER,AREA_NAME
							grpProCustInfo.putAll(staffInfo);
						}

						memUseProduct.putAll(grpProCustInfo);

						memUseProductSet.add(memUseProduct);
					}

				} else
				{
					memUseProductSet.add(relauuInfo);
				}
			}
		}

		return memUseProductSet;
	}

	/**
	 * 查询成员使用产品信息
	 * 
	 * @param param
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryRelationGroupInfo(String serialNumber, Pagination pagination) throws Exception
	{

		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);

		SQLParser parser = new SQLParser(param);
		parser.addSQL("Select Gp.Group_Id,Gp.Cust_Name,s.STAFF_NAME,s.SERIAL_NUMBER,a.AREA_NAME,Uu.Serial_Number_a,Uu.Relation_Type_Code,Uu.Start_Date,Uu.End_Date,u.Product_Id");
		parser.addSQL("  From Tf_f_Cust_Group Gp, Tf_f_Relation_Uu Uu, Tf_f_User u, td_m_staff s, td_m_area a");
		parser.addSQL(" Where Gp.Cust_Id = u.Cust_Id ");
		parser.addSQL("   And u.Partition_Id = Mod(Uu.User_Id_a, 10000) ");
		parser.addSQL("   And u.Remove_Tag = '0' ");

		parser.addSQL("   And u.User_Id = Uu.User_Id_a ");
		parser.addSQL("   And Uu.relation_type_code In('42','81','25','26','23','27','71','32','37','75','20','21','46','53','86','89','92','97') ");
		parser.addSQL("   And Sysdate Between Uu.Start_Date And Uu.End_Date ");
		parser.addSQL("   And Uu.Serial_Number_b = :SERIAL_NUMBER ");
		parser.addSQL("   And Gp.CUST_MANAGER_ID=s.STAFF_ID(+) ");
		parser.addSQL("   And s.CITY_CODE=a.AREA_CODE(+) ");
		IDataset out = Dao.qryByParse(parser, pagination);
		return out;
	}

	/**
	 * 查询UU关系
	 * 
	 * @author tengg
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryRelationInfo(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT user_id_a FROM tf_f_relation_uu ");
		parser.addSQL(" WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000) ");
		parser.addSQL("  AND user_id_b = :USER_ID");
		parser.addSQL("  AND (relation_type_code='30' or relation_type_code='97')");
		parser.addSQL("  AND role_code_b='2'");
		parser.addSQL("  AND end_date > SYSDATE");

		return Dao.qryByParse(parser);
	}

	/**
	 * 根据编号查询挂账集团信息
	 * 
	 * @author liaoyi
	 * @param params
	 *            查询所需参数
	 * @param pagination
	 * @return IDataset 客户标志资料列表
	 * @throws Exception
	 */
	public static IDataset queryRelationuus(IData data, Pagination pagination) throws Exception
	{

		SQLParser parser = new SQLParser(data);
		parser.addSQL("SELECT PARTITION_ID,USER_ID_A,SERIAL_NUMBER_A,USER_ID_B,SERIAL_NUMBER_B,RELATION_TYPE_CODE,ROLE_TYPE_CODE,ROLE_CODE_A,ROLE_CODE_B,ORDERNO,SHORT_CODE,INST_ID,to_char(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,to_char(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,to_char(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3 FROM TF_F_RELATION_UU WHERE 1=1 ");
		parser.addSQL(" AND RELATION_TYPE_CODE = :RELATION_TYPE_CODE");
		parser.addSQL(" AND SERIAL_NUMBER_A = :SERIAL_NUMBER_A");
		parser.addSQL(" AND SERIAL_NUMBER_B = :SERIAL_NUMBER_B");
		parser.addSQL(" AND sysdate BETWEEN start_date AND end_date");
		return Dao.qryByParse(parser, pagination);
	}

	/**
	 * 原SERIAL_NUMBER_B的条件都要通过USER_ID_B查询，提高效率
	 * 
	 * @param userIdB
	 * @param relationTypeCode
	 * @param route
	 * @return
	 * @throws Exception
	 *             wangjx 2013-8-31
	 */
	public static IDataset queryRelaUUByIdBRoute(String userIdB, String relationTypeCode, String route) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_BY_IDB_RTYPE", param, route);
	}

	/**
	 * 查询RELATION_UU表的关系
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 *             wangjx 2013-3-25
	 */
	public static IDataset queryRelaUUBySnb(String serialNumberB, String relationTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER_B", serialNumberB);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_SNA_BY_SNB_RTYPE", param);
	}

	/**
	 * 指定数据库进行查询
	 * 
	 * @param serialNumberB
	 * @param relationTypeCode
	 * @param route
	 * @return IDataset
	 * @throws Exception
	 *             wangjx 2013-8-31
	 */
	public static IDataset queryRelaUUBySnbRoute(String serialNumberB, String relationTypeCode, String route) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER_B", serialNumberB);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_SNA_BY_SNB_RTYPE", param, route);
	}

	/**
	 * 小栏框，是否加入四网
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset QueryRelaUUByUserId2(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT U.RELATION_TYPE_CODE  FROM TF_F_RELATION_UU U ");
		parser.addSQL(" WHERE U.RELATION_TYPE_CODE IN('10','11','12','13','14','15','16','17','18','19','20','21','22','46','56','57','58','59','60','63','64','65','67') ");
		parser.addSQL("  AND U.USER_ID_B= TO_NUMBER(:USER_ID) ");
		parser.addSQL("  AND U.PARTITION_ID= MOD(TO_NUMBER(:USER_ID), 10000) ");
		parser.addSQL("  AND U.END_DATE>SYSDATE ");
		IDataset resultset = Dao.qryByParse(parser);
		if (IDataUtil.isNotEmpty(resultset))
		{
			for (int i = 0, ilen = resultset.size(); i < ilen; i++)
			{
				IData resultData = resultset.getData(i);
				String relationTypeCode = resultData.getString("RELATION_TYPE_CODE");
				resultData.put("RELATION_TYPE_NAME", URelaTypeInfoQry.getRoleTypeNameByRelaTypeCode(relationTypeCode));
			}
		}
		return resultset;
	}

	public static IDataset querySaleTradeUU(String userIdB, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);

		SQLParser parser = new SQLParser(param);
		parser.addSQL("  SELECT T.USER_ID_B,T.SERIAL_NUMBER_A,T.SERIAL_NUMBER_B,T.START_DATE,T.END_DATE,T.RSRV_TAG1, ");
		parser.addSQL(" T.RSRV_TAG2,T.RSRV_DATE1,T.RSRV_DATE2, ");
		parser.addSQL(" SIGN((T.START_DATE + 1) - SYSDATE) EFF_TAG ");
		parser.addSQL("  FROM TF_F_RELATION_UU T ");
		parser.addSQL(" WHERE T.USER_ID_B =:USER_ID_B　");
		parser.addSQL(" AND T.PARTITION_ID = MOD(:USER_ID_B, 10000) ");
		parser.addSQL(" AND T.RELATION_TYPE_CODE='93' ");
		parser.addSQL(" AND T.END_DATE > SYSDATE ");
		return Dao.qryByParse(parser, pagination);
	}

	/**
	 * 根据USER_ID获取用户所有UU关系信息（包括失效的）
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserAllRelaByUserIdB(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userId);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_ALL_BY_USERB", param);
	}

	// 根据SN和eparchy_code查询user_id_a
	public static IDataset QueryUserIdBySnAndEparchy(String serialNumber, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select uu.user_id_a,uu.short_code from tf_f_relation_uu uu ");
		parser.addSQL(" where uu.serial_number_b=:SERIAL_NUMBER");
		parser.addSQL("  and uu.relation_type_code='20' ");
		parser.addSQL("  and uu.end_date>sysdate ");

		return Dao.qryByParse(parser, pagination);
	}

	/**
	 * 作用：根据成员USER_IDB查询成员的UU关系。
	 * 
	 * @param params
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserRelationUUBYUserIDB(IData params, String eparchyCode) throws Exception
	{
		// TODO
		// getVisit().setRouteEparchyCode( eparchyCode);
		IDataset result = Dao.qryByCodeParser("TF_F_RELATION_UU", "SEL_GRP_BY_USERB", params, eparchyCode);
		return result;
	}

	/**
	 * 查询成员使用产品信息 --UU关系
	 * 
	 * @author fuzn 2013-07-24
	 * @param serialNumber
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUuBySerialNumberB(String serialNumber, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER_B", serialNumber);
		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT uu.USER_ID_A,uu.RELATION_TYPE_CODE,to_char(uu.START_DATE,'yyyy-MM-dd hh24:mi:ss') START_DATE,to_char(uu.END_DATE,'yyyy-MM-dd hh24:mi:ss') END_DATE,uu.SERIAL_NUMBER_B,uu.SERIAL_NUMBER_A ");
		parser.addSQL("FROM TF_F_RELATION_UU uu ");
		parser.addSQL("WHERE uu.RELATION_TYPE_CODE IN('42','81','25','26','23','27','71','32','37','75','20','21','46','53','86','89','92','97') ");
		parser.addSQL("AND uu.SERIAL_NUMBER_B = :SERIAL_NUMBER_B ");
		parser.addSQL("AND SYSDATE BETWEEN uu.START_DATE AND uu.END_DATE ");
		IDataset dataset = Dao.qryByParse(parser, pagination);
		return dataset;
	}

	public static IData queryUUByUserIdAB(String userIdA, String userIdB, Pagination pagination) throws Exception
	{

		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("USER_ID_B", userIdB);
		IDataset idset = Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_AB", param, pagination, Route.CONN_CRM_CG);
		return (idset != null && idset.size() > 0) ? idset.getData(0) : null;
	}

	public static IDataset queryUUinfo(String userIdA, String relationTypeCode, Pagination pagination) throws Exception
	{

		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("RELATION_TYPE_CODE", relationTypeCode);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT 1 FROM tf_f_relation_uu");
		sql.append(" WHERE 1 = 1");
		sql.append(" AND user_id_a= :USER_ID_A");
		sql.append(" AND relation_type_code=:RELATION_TYPE_CODE");
		sql.append(" AND short_code=:SHORT_CODE");
		sql.append(" AND end_date > last_day(trunc(sysdate))+1-1/24/3600");
		return Dao.qryBySql(sql, param, pagination);
	}

	/**
	 * 根据user_id_b查询所有的未失效的关系(返回所有的列)
	 * 
	 * @param userIdB
	 * @return IDataset
	 * @throws Exception
	 * @author liuke
	 */
	public static IDataset queryValidRelaUUByUserIDB(String userIdB) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_BY_USERIDB_ALLCOL", param);
	}

	/**
	 * 根据成员sn查询uu表vpmn信息
	 * 
	 * @param sn
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryVpmnRelaBySn(String sn) throws Exception
	{
		IData params = new DataMap();
		params.put("SERIAL_NUMBER", sn);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_BY_GrpMebUUVpmnSN", params);
	}

	// 根据serial_num RELATION_TYPE_CODE在cg库 查询group_id关系
	public static IDataset selGrpidByRelaTypecode(String serial_num, String relationTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_num);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_GRPID_BY_RELATYPECODE", param, Route.CONN_CRM_CG);
	}

	public IDataset getElementFromrelationUUByUserID(String userId, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_ALLAB_BY_USERID", param, pagination);
	}

	public IDataset getUserIdB(String roleCodeB, String userId, String typeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("ROLE_CODE_B", "1");
		param.put("USER_ID_B", userId);
		param.put("RELATION_TYPE_CODE", typeCode);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USERIDB_BY_RELATION", param);
	}

	/**
	 * 查询 集团商务宽带成员 crm all
	 * 
	 * @param userIdA
	 * @param relationTypeCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryRelationUUAllForKDMem(String userIdA, String relationTypeCode, Pagination pagination) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID_B", userIdA);
		params.put("RELATION_TYPE_CODE", relationTypeCode);

		SQLParser parser = new SQLParser(params);
		parser.addSQL(" SELECT A.PARTITION_ID, ");
		parser.addSQL(" TO_CHAR(A.USER_ID_A) USER_ID_A, ");
		parser.addSQL(" A.SERIAL_NUMBER_A, ");
		parser.addSQL(" TO_CHAR(A.USER_ID_B) USER_ID_B, ");
		parser.addSQL(" A.SERIAL_NUMBER_B, ");
		parser.addSQL(" A.RELATION_TYPE_CODE, ");
		parser.addSQL(" A.ROLE_CODE_A, ");
		parser.addSQL(" A.ROLE_CODE_B, ");
		parser.addSQL(" A.ORDERNO, ");
		parser.addSQL(" A.SHORT_CODE, ");
		parser.addSQL(" TO_CHAR(A.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
		parser.addSQL(" TO_CHAR(A.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE ");
		parser.addSQL("FROM TF_F_RELATION_UU A, TF_F_RELATION_UU B ");
		parser.addSQL(" WHERE A.USER_ID_A = B.USER_ID_A ");
		parser.addSQL(" AND B.PARTITION_ID = MOD(B.USER_ID_B, 10000) ");
		parser.addSQL(" AND B.USER_ID_B = :USER_ID_B ");
		parser.addSQL(" AND B.ROLE_CODE_B = '1' ");
		parser.addSQL(" AND B.RELATION_TYPE_CODE = :RELATION_TYPE_CODE ");
		parser.addSQL(" AND B.END_DATE > SYSDATE ");
		parser.addSQL(" AND A.RELATION_TYPE_CODE = :RELATION_TYPE_CODE ");
		parser.addSQL(" AND A.ROLE_CODE_B = '2' ");
		parser.addSQL(" AND A.END_DATE > SYSDATE ");

		return Dao.qryByParse(parser, pagination);
	}

	/**
	 * 查询 集团商务宽带成员 crm all
	 * 
	 * @param userIdA
	 * @param relationTypeCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryRelationUUOneForKDMem(String userIdA, String userIdB, String relationTypeCode) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID_A", userIdA);
		params.put("USER_ID_B", userIdB);
		params.put("RELATION_TYPE_CODE", relationTypeCode);

		SQLParser parser = new SQLParser(params);
		parser.addSQL(" SELECT A.PARTITION_ID, ");
		parser.addSQL(" TO_CHAR(A.USER_ID_A) USER_ID_A, ");
		parser.addSQL(" A.SERIAL_NUMBER_A, ");
		parser.addSQL(" TO_CHAR(A.USER_ID_B) USER_ID_B, ");
		parser.addSQL(" A.SERIAL_NUMBER_B, ");
		parser.addSQL(" A.RELATION_TYPE_CODE, ");
		parser.addSQL(" A.ROLE_CODE_A, ");
		parser.addSQL(" A.ROLE_CODE_B, ");
		parser.addSQL(" A.ORDERNO, ");
		parser.addSQL(" A.SHORT_CODE, ");
		parser.addSQL(" TO_CHAR(A.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
		parser.addSQL(" TO_CHAR(A.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE ");
		parser.addSQL("FROM TF_F_RELATION_UU A, TF_F_RELATION_UU B ");
		parser.addSQL(" WHERE A.USER_ID_A = B.USER_ID_A ");
		parser.addSQL(" AND B.PARTITION_ID = MOD(B.USER_ID_B, 10000) ");
		parser.addSQL(" AND B.USER_ID_B = :USER_ID_A ");
		parser.addSQL(" AND B.ROLE_CODE_B = '1' ");
		parser.addSQL(" AND B.RELATION_TYPE_CODE = :RELATION_TYPE_CODE ");
		parser.addSQL(" AND B.END_DATE > SYSDATE ");
		parser.addSQL(" AND A.USER_ID_B = :USER_ID_B ");
		parser.addSQL(" AND A.RELATION_TYPE_CODE = :RELATION_TYPE_CODE ");
		parser.addSQL(" AND A.ROLE_CODE_B = '2' ");
		parser.addSQL(" AND A.END_DATE > SYSDATE ");

		return Dao.qryByParseAllCrm(parser, true);
	}

	/**
	 * 根据USER_ID_A和RELATION_TYPE_CODE统计成员数量
	 * 
	 * @param userIdA
	 * @param relationTypeCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryCountKDMemForAllCrm(String userIdA, String relationTypeCode) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID_B", userIdA);
		params.put("RELATION_TYPE_CODE", relationTypeCode);

		SQLParser parser = new SQLParser(params);
		parser.addSQL(" SELECT COUNT(*) RECORDCOUNT ");
		parser.addSQL(" FROM TF_F_RELATION_UU A, TF_F_RELATION_UU B ");
		parser.addSQL(" WHERE A.USER_ID_A = B.USER_ID_A ");
		parser.addSQL(" AND B.PARTITION_ID = MOD(B.USER_ID_B, 10000) ");
		parser.addSQL(" AND B.USER_ID_B = :USER_ID_B ");
		parser.addSQL(" AND B.ROLE_CODE_B = '1' ");
		parser.addSQL(" AND B.RELATION_TYPE_CODE = :RELATION_TYPE_CODE ");
		parser.addSQL(" AND B.END_DATE > SYSDATE ");
		parser.addSQL(" AND A.RELATION_TYPE_CODE = :RELATION_TYPE_CODE ");
		parser.addSQL(" AND A.ROLE_CODE_B = '2' ");
		parser.addSQL(" AND A.END_DATE > SYSDATE ");

		return Dao.qryByParseAllCrm(parser, true);
	}

	/**
	 * 根据USER_ID_A、RELATION_TYPE_CODE查询所有CRM库内的UU表关系
	 * 
	 * @param userIdA
	 * @param relationTypeCode
	 * @return
	 * @throws Exception
	 *             wangjx 2013-7-19
	 */
	public static IDataset qryRelaUUByUIdAAllDBWithOrder(String userIdA, String relationTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		return Dao.qryByCodeAllCrm("TF_F_RELATION_UU", "SEL_ALL_BY_USERIDA_WITHORDER", param, true);
	}

	public static IDataset qryRelaByUserIdbAndRelaTypeCode(String userIdB, String relaTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);
		param.put("RELATION_TYPE_CODE", relaTypeCode);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_ONE_USERINFO_BY_USERIDBTYPECODE", param);
	}

	/**
	 * CPE需求 查询主号下的副号个数 chenxy3 2015-08-21
	 * */
	public static IDataset getCheckSnNum(String mainSn) throws Exception
	{
		IData relaParam = new DataMap();
		relaParam.put("MAIN_SERIAL_NUMBER", mainSn);

		IDataset result = Dao.qryByCode("TF_F_RELATION_UU", "SEL_UU_CHECKSN_NUM", relaParam);

		return result;
	}

	/**
	 * 查询母集团下子集团短号
	 * 
	 * @param userIdB
	 * @param userIdA
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset getSubShortCodeExistByUserIdAB(String userIdB, String userIdA, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);
		param.put("USER_ID_A", userIdA);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_SHORTCODE_EXIST", param, pagination);
	}

	/**
	 * 查询母集团短号
	 * 
	 * @param userIdB
	 * @param userIdA
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset getParentShortCodeExistByUserIdAB(String userIdB, String userIdA, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);
		param.put("USER_ID_A", userIdA);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_SHORTCODE_EXIST_USERIDA", param, pagination);
	}

	/**
	 * 
	 * @param param
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryGfffMemRelaByUserIdA(IData param, Pagination page) throws Exception
	{
		return Dao.qryByCodeParser("TF_F_RELATION_UU", "SEL_GRPGFFF_MEMUU_BY_USER_IDA", param, page);
	}

	public static IDataset getUserIdB(String userId, String typeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("ROLE_CODE_B", "1");
		param.put("USER_ID_B", userId);
		param.put("RELATION_TYPE_CODE", typeCode);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USERIDB_BY_RELATION", param);
	}

	/**
	 * 705001,734201,9981011,724001,734001的成员关系
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryRelationBBAndUUByUserIdType(String userId, String relationTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userId);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		IDataset dataset = Dao.qryByCode("TF_F_RELATION_UU", "SEL_RELA90BB_RELAUU_BYUSERID", param);
		return dataset;
	}

	/**
	 * 根据USER_ID_A和RELATION_TYPE_CODE,ROLE_CODE查询UU用户关系表信息
	 */
	public static IDataset getUserRelationRole2(String userId, String relationTypeCode, String roleCodeB) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userId);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		param.put("ROLE_CODE_B", roleCodeB);

		IDataset userRelationInfos = Dao.qryByCode("TF_F_RELATION_UU", "SEL_BY_USERA_CODEB", param);
		return userRelationInfos;
	}

	public static IDataset qryGroupInfoByMemberUUSN(String userId, String relationTypeCode, String serialNum) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		param.put("SERIAL_NUMBER", serialNum);

		IDataset userRelationInfos = Dao.qryByCode("TF_F_RELATION_UU", "SEL_BY_MEMBER_SN", param);
		return userRelationInfos;
	}
	
	/**
	 * 根据主号查询所有的关联副设备
	 */
	public static IDataset qryRelationList(IData inParam) throws Exception {
		IDataset relationList = Dao.qryByCodeParser("TF_F_RELATION_UU","SEL_BY_SN_RELATION_TYPE_CODE_OM",inParam);
		return relationList;
	}
	
	public static IDataset qryRelaUUBySerNumAndSerNumB(String serialNumberA, String serialNumberB, String relationTypeCode) throws Exception
	{

		IData inparam = new DataMap();
		inparam.put("SERIAL_NUMBER_A", serialNumberA);
		inparam.put("SERIAL_NUMBER_B", serialNumberB);
		inparam.put("RELATION_TYPE_CODE", relationTypeCode);

		SQLParser parser = new SQLParser(inparam);
		parser.addSQL(" SELECT * ");
		parser.addSQL(" FROM tf_f_relation_uu uu  ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" AND SERIAL_NUMBER_A=:SERIAL_NUMBER_A ");
		parser.addSQL(" AND SERIAL_NUMBER_B=:SERIAL_NUMBER_B ");
		parser.addSQL(" AND uu.relation_type_code = :RELATION_TYPE_CODE ");
		parser.addSQL(" AND sysdate BETWEEN start_date AND end_date ");
		IDataset ds = Dao.qryByParse(parser);
		return ds;
	}
	
	public static IDataset qryWlwMebInfoByIDB(String userIdB, String relationTypeCode) throws Exception
	{
		IData iparam = new DataMap();
		iparam.put("USER_ID_B", userIdB);
		iparam.put("RELATION_TYPE_CODE", relationTypeCode);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_RELA_TYPE_FORWLWMEB", iparam);
	}
public static IDataset qrySerialNumberBySnBAndRelationType(String serialNumberB, String relationTypeCode) throws Exception
	{
		IData iparam = new DataMap();
		iparam.put("SERIAL_NUMBER_B", serialNumberB);
		iparam.put("RELATION_TYPE_CODE", relationTypeCode);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_RELATIONTRADE_BY_SNB", iparam);
	}	
	public static IDataset qryCountWlwMebByUserIdA(String userIdA) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		IDataset result = Dao.qryByCode("TF_F_RELATION_UU", "SEL_CNTUU_USERIDA_FOR_WLW", param);
		return result;
	}
	
	/**
	 * 查询所有生效的和商务统付成员号码
	 * @param userIdA
	 * @param relationTypeCode
	 * @param roleCodeB
	 * @return
	 * @throws Exception
	 * @author chenzg
	 * @date 2018-4-24
	 */
	public static IDataset queryAllValidPgUnionPayMembers(String userIdA, String relationTypeCode, String roleCodeB) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID_A", userIdA);
		params.put("RELATION_TYPE_CODE", relationTypeCode);
		params.put("ROLE_CODE_B", roleCodeB);
		SQLParser parser = new SQLParser(params);
		parser.addSQL("SELECT PARTITION_ID,");
		parser.addSQL("       TO_CHAR(USER_ID_A) USER_ID_A,");
		parser.addSQL("       SERIAL_NUMBER_A,");
		parser.addSQL("       TO_CHAR(USER_ID_B) USER_ID_B,");
		parser.addSQL("       SERIAL_NUMBER_B,");
		parser.addSQL("       RELATION_TYPE_CODE,");
		parser.addSQL("       ROLE_CODE_A,");
		parser.addSQL("       ROLE_CODE_B,");
		parser.addSQL("       ORDERNO,");
		parser.addSQL("       SHORT_CODE,");
		parser.addSQL("       TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,");
		parser.addSQL("       TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,");
		parser.addSQL("       TO_CHAR(INST_ID) INST_ID,");
		parser.addSQL("       NVL(RSRV_TAG1, 0) DEAL_TAG,");
		parser.addSQL("       DECODE(RSRV_TAG1, '1', '共享', '') RSRV_TAG1NAME");
		parser.addSQL("  FROM TF_F_RELATION_UU");
		parser.addSQL(" WHERE USER_ID_A = TO_NUMBER(:USER_ID_A)");
		parser.addSQL("   AND RELATION_TYPE_CODE = :RELATION_TYPE_CODE");
		parser.addSQL("   AND ROLE_CODE_B = :ROLE_CODE_B");
		parser.addSQL("   AND END_DATE > LAST_DAY(TRUNC(SYSDATE)) + 1");
		parser.addSQL(" ORDER BY START_DATE");
		return Dao.qryByParse(parser);
	}
	
	//获取所有RelaUU
	public static IDataset qryAllRelaUUByUserIdA(String userIdA ,String relationTypeCode,String roleCodeB) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID_A", userIdA);
		inparam.put("RELATION_TYPE_CODE", relationTypeCode);
		inparam.put("ROLE_CODE_B", roleCodeB);

		SQLParser parser = new SQLParser(inparam);
		parser.addSQL(" SELECT * ");
		parser.addSQL(" FROM tf_f_relation_uu uu  ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" AND USER_ID_A=:USER_ID_A ");
		parser.addSQL(" AND ROLE_CODE_B=:ROLE_CODE_B ");
		parser.addSQL(" AND uu.relation_type_code = :RELATION_TYPE_CODE ");
		IDataset ds = Dao.qryByParse(parser);
		return ds;
	}
	
	/**
	 * REQ201803160015关于优化商务宽带成员信息查询界面的需求
	 * 增加宽带地址信息
	 * @param userIdA
	 * @param relationTypeCode
	 * @param pagination
	 * @return
	 * @throws Exception
	 * @author chenzg
	 * @date 2018-3-26
	 */
	public static IDataset qryRelationUUAllForKDMemNew(String userIdA, String relationTypeCode, Pagination pagination) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID_B", userIdA);
		params.put("RELATION_TYPE_CODE", relationTypeCode);

		SQLParser parser = new SQLParser(params);
		parser.addSQL("SELECT A.PARTITION_ID,");
		parser.addSQL("       TO_CHAR(A.USER_ID_A) USER_ID_A,");
		parser.addSQL("       A.SERIAL_NUMBER_A,");
		parser.addSQL("       TO_CHAR(A.USER_ID_B) USER_ID_B,");
		parser.addSQL("       A.SERIAL_NUMBER_B,");
		parser.addSQL("       A.RELATION_TYPE_CODE,");
		parser.addSQL("       A.ROLE_CODE_A,");
		parser.addSQL("       A.ROLE_CODE_B,");
		parser.addSQL("       A.ORDERNO,");
		parser.addSQL("       A.SHORT_CODE,");
		parser.addSQL("       TO_CHAR(A.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,");
		parser.addSQL("       TO_CHAR(A.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,");
		parser.addSQL("       C.STAND_ADDRESS,");
		parser.addSQL("       D.PRODUCT_ID");
		parser.addSQL("  FROM TF_F_RELATION_UU  A,");
		parser.addSQL("       TF_F_RELATION_UU  B,");
		parser.addSQL("       TF_F_USER_WIDENET C,");
		parser.addSQL("       TF_F_USER_PRODUCT D");
		parser.addSQL(" WHERE A.USER_ID_A = B.USER_ID_A");
		parser.addSQL("   AND A.USER_ID_B = C.USER_ID");
		parser.addSQL("   AND C.USER_ID = D.USER_ID");
		parser.addSQL("   AND B.PARTITION_ID = MOD(B.USER_ID_B, 10000)");
		parser.addSQL("   AND B.USER_ID_B = :USER_ID_B");
		parser.addSQL("   AND B.ROLE_CODE_B = '1'");
		parser.addSQL("   AND B.RELATION_TYPE_CODE = '47'");
		parser.addSQL("   AND B.END_DATE > SYSDATE");
		parser.addSQL("   AND A.RELATION_TYPE_CODE = '47'");
		parser.addSQL("   AND A.ROLE_CODE_B = '2'");
		parser.addSQL("   AND A.END_DATE > SYSDATE");
		parser.addSQL("   AND C.END_DATE > SYSDATE");
		parser.addSQL("   AND D.END_DATE > SYSDATE");


		return Dao.qryByParse(parser, pagination);
	}

	/**
	 * @Description：查询是否指定时间之后加入亲亲网
	 * @param:@param string
	 * @param:@param string2
	 * @param:@param string3
	 * @param:@return
	 * @return IDataset
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-6-5下午03:54:48
	 */
	public static IDataset queryJoinFamily(String userId, String relationTypeCode,String startDate) throws Exception {
		IData params = new DataMap();
		params.put("USER_ID_B", userId);
		params.put("RELATION_TYPE_CODE", relationTypeCode);
		params.put("START_DATE", startDate);
		SQLParser parser = new SQLParser(params);
		produceSQL(parser);
		parser.addSQL(" AND   A.USER_ID_B = :USER_ID_B");
		parser.addSQL(" AND   A.RELATION_TYPE_CODE = :RELATION_TYPE_CODE");
		parser.addSQL(" AND A.START_DATE > to_date(:START_DATE,'yyyy-MM-dd hh24:mi:ss')");
		return Dao.qryByParse(parser);
	}

	/**
	 * @Description：查询是否指定时间之后增加亲亲网成员
	 * @param:@param userIdA
	 * @param:@param string
	 * @param:@return
	 * @return IDataset
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-6-5下午04:19:40
	 */
	public static IDataset queryAddFamily(String userIdA, String relationTypeCode, String startDate) throws Exception {
		IData params = new DataMap();
		params.put("USER_ID_A", userIdA);
		params.put("RELATION_TYPE_CODE", relationTypeCode);
		params.put("START_DATE", startDate);
		SQLParser parser = new SQLParser(params);
		produceSQL(parser);
		parser.addSQL(" AND   A.USER_ID_A = :USER_ID_A");
		parser.addSQL(" AND   A.RELATION_TYPE_CODE = :RELATION_TYPE_CODE");
		parser.addSQL(" AND   A.ROLE_CODE_B = '2' ");
		parser.addSQL(" AND   A.START_DATE > to_date(:START_DATE,'yyyy-MM-dd hh24:mi:ss')");
		return Dao.qryByParse(parser);
	}

	/**
	 * @Description：查询亲亲网用户列表
	 * @param:@param userIdA
	 * @param:@param string
	 * @param:@param string2
	 * @param:@return
	 * @return IDataset
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-6-5下午04:39:01
	 */
	public static IDataset queryFamilyMemList(String userIdA, String relationTypeCode) throws Exception{
			IData params = new DataMap();
	params.put("USER_ID_A", userIdA);
	params.put("RELATION_TYPE_CODE", relationTypeCode);
	SQLParser parser = new SQLParser(params);
	produceSQL(parser);
	parser.addSQL(" AND   A.USER_ID_A = :USER_ID_A");
	parser.addSQL(" AND   A.RELATION_TYPE_CODE = :RELATION_TYPE_CODE");
	return Dao.qryByParse(parser);
	}

	/**
	 * @Description：封装sql
	 * @param:@param parser
	 * @return void
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-6-5下午04:46:41
	 */
	private static void produceSQL(SQLParser parser) throws Exception {
		parser.addSQL("SELECT A.PARTITION_ID,");
		parser.addSQL("       TO_CHAR(A.USER_ID_A) USER_ID_A,");
		parser.addSQL("       A.SERIAL_NUMBER_A,");
		parser.addSQL("       TO_CHAR(A.USER_ID_B) USER_ID_B,");
		parser.addSQL("       A.SERIAL_NUMBER_B,");
		parser.addSQL("       A.RELATION_TYPE_CODE,");
		parser.addSQL("       A.ROLE_CODE_A,");
		parser.addSQL("       A.ROLE_CODE_B,");
		parser.addSQL("       A.ORDERNO,");
		parser.addSQL("       A.SHORT_CODE,");
		parser.addSQL("       TO_CHAR(A.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,");
		parser.addSQL("       TO_CHAR(A.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE");
		parser.addSQL("  FROM TF_F_RELATION_UU  A");
		parser.addSQL("  WHERE 1=1 ");
		parser.addSQL("  AND A.END_DATE>SYSDATE ");
	}
	/**
	 * 查询用户是否“多媒体桌面电话”集团产品成员
	 * @param userIdb
	 * @return
	 * @throws Exception
	 * @author chenzg
	 * @date 2018-6-1
	 */
	public static IDataset qryDesktopMemRela(String userIdb) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID_B", userIdb);
		SQLParser parser = new SQLParser(inparam);
		parser.addSQL(" SELECT uu.* ");
		parser.addSQL(" FROM tf_f_relation_uu uu ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" AND uu.USER_ID_B = :USER_ID_B ");
		parser.addSQL(" AND uu.relation_type_code = 'S1' ");
		parser.addSQL(" AND uu.end_date > sysdate ");
		IDataset ds = Dao.qryByParse(parser);
		return ds;
	}
	
	public static IDataset getUserApwlanHefamily(String userIdb) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_B", userIdb);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_USER_APWLAN_HEFAMILY", param);
	}
	/**
	 * k3
	 * @param userIdA
	 * @param serialNumberB
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryGroupInfoRela(String userIdA,String serialNumberB,String relation_type_code)throws Exception{
		IData param =new DataMap();
		param.put("USER_ID_A", userIdA);
		param.put("SERIAL_NUMBER_B", serialNumberB);
		param.put("RELATION_TYPE_CODE", relation_type_code);
		SQLParser sql =new SQLParser(param);
		sql.addSQL("SELECT * FROM tf_f_relation_uu a ");
		sql.addSQL("WHERE 1=1 ");
		sql.addSQL("AND a.USER_ID_A=:USER_ID_A ");
		sql.addSQL("AND a.RELATION_TYPE_CODE=:RELATION_TYPE_CODE ");
		sql.addSQL("AND a.SERIAL_NUMBER_B=TO_NUMBER(:SERIAL_NUMBER_B) ");
		sql.addSQL("AND a.end_date > sysdate ");
		return Dao.qryByParse(sql);
		
	}
	//REQ201812280023 关于增加TD一代固话销户提示的需求 wuhao5
	public static IDataset qryRelaUUBySerialNumberA(String serialNumberA, String relationTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER_A", serialNumberA);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_ALL_BY_SERIALNUMBERA", param);
	}
	/**
	 * 
	 * @description 根据userIdB和关系类型编码查询所有数据没有end_date的时间限制
	 * @param @return
	 * @param @throws Exception
	 * @return IDataset
	 * @author tanzheng
	 * @date 2019年3月20日
	 * @param userIdB
	 * @param relationTypeCode
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryRelationUUAllForExpire(String userIdB, String relationTypeCode) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID_B", userIdB);
		params.put("RELATION_TYPE_CODE", relationTypeCode);

		SQLParser parser = new SQLParser(params);
		parser.addSQL("SELECT A.PARTITION_ID,");
		parser.addSQL("       TO_CHAR(A.USER_ID_A) USER_ID_A,");
		parser.addSQL("       A.SERIAL_NUMBER_A,");
		parser.addSQL("       TO_CHAR(A.USER_ID_B) USER_ID_B,");
		parser.addSQL("       A.SERIAL_NUMBER_B,");
		parser.addSQL("       A.RELATION_TYPE_CODE,");
		parser.addSQL("       A.ROLE_CODE_A,");
		parser.addSQL("       A.ROLE_CODE_B,");
		parser.addSQL("       A.ORDERNO,");
		parser.addSQL("       A.SHORT_CODE,");
		parser.addSQL("       TO_CHAR(A.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,");
		parser.addSQL("       TO_CHAR(A.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE");
		parser.addSQL("  FROM TF_F_RELATION_UU  A");
		parser.addSQL(" WHERE A.USER_ID_B = :USER_ID_B");
		parser.addSQL("   AND A.RELATION_TYPE_CODE = :RELATION_TYPE_CODE");



		return Dao.qryByParse(parser);
	}
	/**
	 * k3
	 *	取消个人代付关系
	 */
	public static IDataset qryAllMebInfoRelaAndMain(IData param)throws Exception{
		SQLParser sql =new SQLParser(param);
		sql.addSQL("SELECT * FROM tf_f_relation_uu a ");
		sql.addSQL("WHERE 1=1 ");
		sql.addSQL("AND a.RELATION_TYPE_CODE=:RELATION_TYPE_CODE ");
		sql.addSQL("AND a.USER_ID_A=TO_NUMBER(:USER_ID_A) ");
		sql.addSQL("AND a.USER_ID_B=TO_NUMBER(:USER_ID_B) ");
		sql.addSQL("AND a.ROLE_CODE_B=:ROLE_CODE_B ORDER BY a.START_DATE DESC ");
		return Dao.qryByParse(sql);
	}
	
	public static IDataset getRelaUUInfoByUserida2(IData input) throws Exception
	{
		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_MEMBER_BY_IDA", input ,Route.CONN_CRM_CG);
	}
/**
     * @param shortCode
     * @param relationTypeCode
     * @param custId
     * @return
     * @throws Exception
     */
    public static IDataset qryRelationUUBycustIdAndShortCodeAndRale(String custId, String relationTypeCode , String shortCode) throws Exception
    {
        IData params = new DataMap();
        params.put("CUST_ID", custId);
        params.put("RELATION_TYPE_CODE", relationTypeCode);
        params.put("SHORT_CODE", shortCode);

        SQLParser parser = new SQLParser(params);
        parser.addSQL("SELECT uu.PARTITION_ID,          ");
        parser.addSQL("       uu.USER_ID_A,          ");
        parser.addSQL("       uu.SERIAL_NUMBER_A,          ");
        parser.addSQL("       uu.USER_ID_B,          ");
        parser.addSQL("       uu.SERIAL_NUMBER_B,          ");
        parser.addSQL("       uu.RELATION_TYPE_CODE,          ");
        parser.addSQL("       uu.ROLE_TYPE_CODE,          ");
        parser.addSQL("       uu.ROLE_CODE_A,          ");
        parser.addSQL("       uu.ROLE_CODE_B,          ");
        parser.addSQL("       uu.ORDERNO,           ");
        parser.addSQL("       uu.SHORT_CODE           ");
        parser.addSQL(" FROM TF_F_RELATION_UU uu , TF_F_USER u  ");
        parser.addSQL(" WHERE 1=1  ");
        parser.addSQL(" AND u.CUST_ID = :CUST_ID  ");
        parser.addSQL(" AND u.REMOVE_TAG = '0'   ");
        parser.addSQL(" AND uu.USER_ID_A = u.USER_ID  ");
        parser.addSQL(" AND uu.RELATION_TYPE_CODE = :RELATION_TYPE_CODE  ");
        parser.addSQL(" AND uu.SHORT_CODE = :SHORT_CODE  ");
        parser.addSQL(" AND SYSDATE BETWEEN uu.start_date AND uu.end_date ");
        return Dao.qryByParse(parser);
    }
    
    /*
     * 功能： 用户是否为亲亲网业务开户至今3个月内的主号用户
     *
     * 返回： 3个月内用户是有效的主号的关系记录
     *
     * */

 	public static IDataset qryMainNumberForThreeMonth(String userIdB, String relationTypeCode, String route) throws Exception {
 		IData param = new DataMap();
 		param.put("USER_ID_B", userIdB);
 		param.put("RELATION_TYPE_CODE", relationTypeCode);
 		return Dao.qryByCode("TF_F_RELATION_UU", "SEL_MAIN_NUMBER_FOR_3MONTH", param, route);
 	}
	
	
	//校验指定优惠
	public static IDataset qryPrincipalDiscnt(IData param)throws Exception{
		SQLParser sql =new SQLParser(param);
		sql.addSQL("SELECT * FROM TF_F_USER_DISCNT A ,TD_S_COMMPARA B ");
		sql.addSQL("WHERE A.DISCNT_CODE=B.PARA_CODE1 ");
		sql.addSQL("AND B.SUBSYS_CODE=:SUBSYS_CODE ");
		sql.addSQL("AND B.PARAM_ATTR=:PARAM_ATTR ");
		sql.addSQL("AND B.PARAM_CODE=:PARAM_CODE ");
		sql.addSQL("AND A.USER_ID=:USER_ID ");
		sql.addSQL("AND A.END_DATE>SYSDATE ");
		return Dao.qryByParse(sql);
	}
	
	//查询 RELATION_TYPE_CODE=61的全部用户 
	public static IDataset qryAllRelation(String userID)throws Exception{
		IData param = new DataMap();
		param.put("USER_ID_A", userID);
		SQLParser sql =new SQLParser(param);
		sql.addSQL("SELECT * FROM TF_F_RELATION_UU ");
		sql.addSQL("WHERE USER_ID_A=:USER_ID_A ");
		sql.addSQL("AND RELATION_TYPE_CODE='61' ");
		sql.addSQL("AND SYSDATE BETWEEN START_DATE AND END_DATE ");
		return Dao.qryByParse(sql);
	}
	
	//查询 RELATION_TYPE_CODE=61 且为副号的用户
	public static IDataset qryRoleCodeB(String userID)throws Exception{
		IData param = new DataMap();
		param.put("USER_ID_A", userID);
		SQLParser sql =new SQLParser(param);
		sql.addSQL("SELECT * FROM TF_F_RELATION_UU ");
		sql.addSQL("WHERE USER_ID_A=:USER_ID_A ");
		sql.addSQL("AND RELATION_TYPE_CODE='61' ");
		sql.addSQL("AND ROLE_CODE_B='2' ");
		sql.addSQL("AND SYSDATE BETWEEN START_DATE AND END_DATE ");
		return Dao.qryByParse(sql);
	}
	
	//查询主号
	public static IDataset qryMainSN(String userID)throws Exception{
		IData param = new DataMap();
		param.put("USER_ID_A", userID);
		SQLParser sql =new SQLParser(param);
		sql.addSQL("SELECT * FROM TF_F_RELATION_UU ");
		sql.addSQL("WHERE USER_ID_A=:USER_ID_A ");
		sql.addSQL("AND RELATION_TYPE_CODE='61' ");
		sql.addSQL("AND ROLE_CODE_B='1' ");
		sql.addSQL("AND SYSDATE BETWEEN START_DATE AND END_DATE ");
		return Dao.qryByParse(sql);
	}
	
	//查询虚拟产品信息
	public static IDataset qryDrProductId(String userID)throws Exception{
		IData param = new DataMap();
		param.put("USER_ID", userID);
		SQLParser sql =new SQLParser(param);
		sql.addSQL("SELECT * FROM TF_F_USER_PRODUCT ");
		sql.addSQL("WHERE USER_ID=:USER_ID ");
		sql.addSQL("AND SYSDATE BETWEEN START_DATE AND END_DATE ");
		return Dao.qryByParse(sql);
	}


    /**
     *
     * @param param
     * @throws Exception
     */
    public static IDataset getAllRelaUUByUserA(String userIdA) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID_A", userIdA);
        SQLParser parser = new SQLParser(inparams);
        parser.addSQL(" SELECT SERIAL_NUMBER_A, ");
        parser.addSQL(" SERIAL_NUMBER_B, ");
        parser.addSQL(" USER_ID_A, ");
        parser.addSQL(" USER_ID_B, ");
        parser.addSQL(" to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date, ");
        parser.addSQL(" to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date ");
        parser.addSQL(" FROM tf_f_relation_uu ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND START_DATE <= SYSDATE ");
        parser.addSQL(" AND END_DATE > SYSDATE ");
        parser.addSQL(" AND USER_ID_A =:USER_ID_A ");
        parser.addSQL(" AND PARTITION_ID = MOD(USER_ID_B, 10000) ");
        return Dao.qryByParse(parser);
    }

    /**
     *
     * @param param
     * @throws Exception
     */
    public static IDataset getAllRelaUUByUserB(String userIdB) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID_B", userIdB);
        SQLParser parser = new SQLParser(inparams);
        parser.addSQL(" SELECT SERIAL_NUMBER_A, ");
        parser.addSQL(" SERIAL_NUMBER_B, ");
        parser.addSQL(" USER_ID_A, ");
        parser.addSQL(" USER_ID_B, ");
        parser.addSQL(" to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date, ");
        parser.addSQL(" to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date ");
        parser.addSQL(" FROM tf_f_relation_uu ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND START_DATE <= SYSDATE ");
        parser.addSQL(" AND END_DATE > SYSDATE ");
        parser.addSQL(" AND USER_ID_B =:USER_ID_B ");
        parser.addSQL(" AND PARTITION_ID = MOD(USER_ID_B, 10000) ");
        return Dao.qryByParse(parser);
    }
	
}
