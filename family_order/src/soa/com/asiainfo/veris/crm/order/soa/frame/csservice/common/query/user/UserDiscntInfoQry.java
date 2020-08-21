package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.biz.service.BizRoute;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.DiscntPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.OfferCfg;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.PackageElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.ProductElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.bof.util.OfferUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.FillUserElementInfoUtil;
import com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo.InterforResalQry;

/**
 * 
 */
public class UserDiscntInfoQry
{

	private final static Logger logger = Logger.getLogger(UserDiscntInfoQry.class);
	public static IDataset existsUserDiscntMultiNextMonth(String userId, String paramCode, String eparchyCode) throws Exception
	{
		IData data = new DataMap();
		data.put("USER_ID", userId);
		data.put("PARAM_CODE", paramCode);
		data.put("EPARCHY_CODE", eparchyCode);

		
		return Dao.qryByCode("TD_S_CPARAM", "ExistsUserDiscntMultiNextMonth", data);
	}

	public static IDataset existsUserDiscntMultiThisMonth(String userId, String paramCode, String eparchyCode) throws Exception
	{
		IData data = new DataMap();
		data.put("USER_ID", userId);
		data.put("PARAM_CODE", paramCode);
		data.put("EPARCHY_CODE", eparchyCode);

		return Dao.qryByCode("TD_S_CPARAM", "ExistsUserDiscntMultiThisMonth", data);
	}

	public static IDataset ExistsUserDiscntNotime(String userId, String paramCode, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PARAM_CODE", paramCode);
		param.put("EPARCHY_CODE", eparchyCode);
		return Dao.qryByCode("TD_S_CPARAM", "ExistsUserDiscntNotime", param);
	}

	/**
	 * @param userId
	 * @param discntCode
	 * @return
	 * @throws Exception
	 * @author wangww3
	 */
	public static IDataset getAllDiscntByUser(String userId, String discntCode) throws Exception
	{
		IData cond = new DataMap();
		cond.put("USER_ID", userId);
		cond.put("DISCNT_CODE", discntCode);

		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_ALLDISCNT_BY_USER", cond);

		FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userDiscnts, cond, null);

		return userDiscnts;
	}
	public static IDataset getValidDiscntByUser(String userId, String discntCode) throws Exception
	{
		IData cond = new DataMap();
		cond.put("USER_ID", userId);
		cond.put("DISCNT_CODE", discntCode);

		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_VALID_DISCNT_BY_USER", cond);

		FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userDiscnts, cond, null);

		return userDiscnts;
	}
	public static IDataset getAllDiscntByUserRoute(String userId, String discntCode) throws Exception
	{
		IData cond = new DataMap();
		cond.put("USER_ID", userId);
		cond.put("DISCNT_CODE", discntCode);

		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_ALLDISCNT_BY_USER", cond,Route.getCrmDefaultDb());

		FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userDiscnts, cond, null);

		return userDiscnts;
	}

    public static IDataset getAllDiscntByUser_2(String userId, String discntCode) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", userId);
        cond.put("DISCNT_CODE", discntCode);
        IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_ALLDISCNT_BY_USER_2", cond);
        return userDiscnts;
    }
    
    public static IDataset getAllDiscntByUser_3(String userId, String discntCode) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", userId);
        cond.put("DISCNT_CODE", discntCode);
        IDataset userDiscnts = Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_ALLDISCNT_BY_USER_2", cond);
        return userDiscnts;
    }

	/**
	 * 根据user_id，discntCode 查询有效的资费 chenyi 13-10-28
	 * 
	 * @param userId
	 * @param discntCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset getAllDiscntByUserId(String userId, String discntCode) throws Exception
	{

		IData inparams = new DataMap();
		inparams.put("USER_ID", userId);
		inparams.put("DISCNT_CODE", discntCode);
		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_ALLDISCNT_BY_USER", inparams);

		FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userDiscnts, inparams, null);

		return userDiscnts;
	}

	public static IDataset getAllDiscntByUserIdAndRouteId(String userId, String discntCode, String routeId) throws Exception
	{

		IData inparams = new DataMap();
		inparams.put("USER_ID", userId);
		inparams.put("DISCNT_CODE", discntCode);
		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_ALLDISCNT_BY_USER", inparams, routeId);

		return userDiscnts;
	}

	public static IDataset getAllDiscntByUserIdAndRouteId_1(String userId, String discntCode, String routeId) throws Exception
	{

		IData inparams = new DataMap();
		inparams.put("USER_ID", userId);
		inparams.put("DISCNT_CODE", discntCode);
		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_ALLDISCNT_BY_USER_1", inparams, routeId);

		return userDiscnts;
	}

	public static IDataset getAllUserDiscntByUserIdUserIdA(String userId, String user_id_a) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("USER_ID_A", user_id_a);

		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_USERIDA_ALL", param);

		FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userDiscnts, param, null);

		return userDiscnts;
	}

	/**
	 * @Function: getAllValidDiscntByUserId
	 * @Description: 查询用户下所有的资费
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-26 下午2:01:23 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-26 updata v1.0.0 修改原因
	 */
	public static IDataset getAllValidDiscntByUserId(String user_id) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		IDataset dataset = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_NOW", param);

		if (IDataUtil.isNotEmpty(dataset))
		{
			FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(dataset, null, null);
			if (IDataUtil.isNotEmpty(dataset))
			{
				for (int i = 0; i < dataset.size(); i++)
				{
					IData data = dataset.getData(i);
					String discntCode = data.getString("DISCNT_CODE");
					String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(discntCode);
					data.put("DISCNT_NAME", discntName);
				}
			}
		}
		return dataset;
	}

	public static IDataset getAllValidDiscntByUserId(String user_id, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		IDataset dataset = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_NOW", param, eparchyCode);

		if (IDataUtil.isNotEmpty(dataset))
		{
			FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(dataset, null, null);
		}

		return dataset;
	}

	public static IDataset getByUIdPkId(String userId, String packageId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("GROUP_ID", packageId);

		IDataset dataset = Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_BY_PACKAGE_USERID", param);
		if (IDataUtil.isNotEmpty(dataset))
		{
			for (int i = 0; i < dataset.size(); i++)
			{
				IData data = dataset.getData(i);
				String discntCode = data.getString("DISCNT_CODE");
				String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(discntCode);
				data.put("DISCNT_NAME", discntName);
				data.put("PACKAGE_ID", data.getString("GROUP_ID"));
			}
		}
		return dataset;
	}

	public static IDataset getSaleActiveDiscntsByUIdPkId(String userId, String packageId, String instId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("GROUP_ID", packageId);
		param.put("OFFER_INS_ID", instId);

		IDataset dataset = Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_BY_GROUPID_USERID", param);
		if (IDataUtil.isNotEmpty(dataset))
		{
			for (int i = 0; i < dataset.size(); i++)
			{
				IData data = dataset.getData(i);
				String discntCode = data.getString("DISCNT_CODE");
				String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(discntCode);
				data.put("DISCNT_NAME", discntName);
			}
		}
		return dataset;
	}

	/**
	 * todo getVisit().setRouteEparchyCode(
	 * AppUtil.getAttribute("MEM_EPARCHY_CODE"));怎么处理
	 * 根据user_id,USER_ID_A、距现在的天数获取有用户优惠
	 * 
	 * @param inparams
	 * @return
	 * @throws Exception
	 */
	public static IDataset getByUserIdDiscodeLatestdays(IData inparams) throws Exception
	{

		// TODO getVisit().setRouteEparchyCode(
		// AppUtil.getAttribute("MEM_EPARCHY_CODE"));
		IData idata = new DataMap();
		idata.put("USER_ID", inparams.getString("USER_ID", ""));
		idata.put("DISCNT_CODE", inparams.getString("DISCNT_CODE", ""));
		idata.put("LATEST_DAYS", inparams.getString("LATEST_DAYS", ""));
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_DISCODE_LATESTDAYS", idata, "MEM_EPARCHY_CODE");
	}

	/**
	 * todo code_code 表里没有SEL_BY_USERID_SPEC 获取在个人业务加的集团优惠
	 * 
	 * @description
	 * @author xiaozp
	 * @date Nov 12, 2009
	 * @version 1.0.0
	 * @param inparams
	 * @return
	 */
	public static IDataset getByUserIdSpecialDiscnt(IData inparams) throws Exception
	{

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_SPEC", inparams);
	}

	/**
	 * 获取校园宽带优惠信息
	 * 
	 * @param discntTypeCode
	 * @return
	 * @throws Exception
	 * @author chenzm
	 */
	public static IDataset getCanSpecDiscnt(String discntTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("DISCNT_TYPE_CODE", discntTypeCode);

		return Dao.qryByCode("TD_B_DISCNT", "SEL_SPEC_DISCNT_BY_STAFF", param);
	}

	/**
	 * 获取某种校园宽带优惠信息
	 * 
	 * @param discntTypeCode
	 * @return
	 * @throws Exception
	 * @author chenzm
	 */
	public static IDataset getCanSpecDiscntByCode(String discntTypeCode, String discntCode) throws Exception
	{
		IData param = new DataMap();
		param.put("DISCNT_TYPE_CODE", discntTypeCode);
		param.put("DISCNT_CODE", discntCode);
		return Dao.qryByCode("TD_B_DISCNT", "SEL_SPEC_DISCNT_BY_CODE", param);
	}

	/**
	 * 获取到需要发送的短信内容
	 * 
	 * @param pd
	 * @param userData
	 * @return
	 * @throws Exception
	 */
	public static IDataset getCCSmsSendInfo(String brandCode, String productId, String discntCode) throws Exception
	{
		IData userData = new DataMap();
		userData.put("BRAND_CODE", brandCode);
		userData.put("PRODUCT_ID", productId);
		userData.put("DISCNT_CODE", discntCode);
		SQLParser parser = new SQLParser(userData);
		parser.addSQL("SELECT T.CCSMS_ID,T.CCSMS_CONTENT FROM TD_S_CCSMS T ");
		parser.addSQL(" WHERE SYSDATE BETWEEN T.START_DATE AND T.END_DATE ");
		parser.addSQL(" AND T.BRAND_CODE=:BRAND_CODE ");
		parser.addSQL(" AND T.PRODUCT_ID=:PRODUCT_ID ");
		parser.addSQL(" AND T.MAIN_DISCNT_CODE=:DISCNT_CODE ");

		return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
	}

	/**
	 * 获取TD_S_COMMPARA 1655关联配置数据
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getCommpara1655ByUserId(String userId) throws Exception
	{
		IData inparam = new DataMap();

		inparam.put("USER_ID", userId);

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_COMMPARA_BY_USERID", inparam);
	}

	public static IDataset getCountGprs(String userid) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userid);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_COUNT_GPRS", param);
	}

	public static IDataset getCountGprsDoning(String userid, String modifyTag) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userid);
		param.put("MODIFY_TAG", modifyTag);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_COUNT_GPRS_DONING", param);
	}

	/**
	 * @Function: getDerateDiscntInfos
	 * @Description: 从中心库 查询优惠绑定信息
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-26 下午2:03:36 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-26 updata v1.0.0 修改原因
	 */
	public static IDataset getDerateDiscntInfos(String discntCode, String epachyCode) throws Exception
	{

		IData inparams = new DataMap();
		inparams.put("SUBSYS_CODE", "CSM");
		inparams.put("PARAM_ATTR", "258");
		inparams.put("PARAM_CODE", discntCode);
		inparams.put("EPARCHY_CODE", epachyCode);
		return Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_ATTRPARAM_CODE", inparams, Route.CONN_CRM_CEN);
	}

	public static IDataset getDiscnt4ActByUserId1(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		return Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_PLATATTR_BY_USERID1", param);
	}

	public static IDataset getDiscnt4ActByUserId2(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		return Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_PLATATTR_BY_USERID2", param);
	}

	/**
	 * 通过PRODUCT_ID、SPEC_TAG、RELATION_TYPE_CODE查询有效用户优惠
	 * 
	 * @param userId
	 * @param specTag
	 * @param relationTypeCode
	 * @param productId
	 * @param routeId
	 * @return IDataset
	 * @throws Exception
	 *             wangjx 2013-11-14
	 */
	public static IDataset getDiscntBySpecRelaRoute(String userId, String specTag, String relationTypeCode, String productId, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SPEC_TAG", specTag);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		param.put("PRODUCT_ID", productId);

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_SPEC_RELA", param, routeId);
	}

	public static IDataset getDiscntByUserId(String user_id) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		IDataset dataset = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNT_FOR_SENDBACK", param);

		if (IDataUtil.isNotEmpty(dataset))
		{
			for (int i = 0; i < dataset.size(); i++)
			{
				dataset.getData(i).put("DISCNT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(dataset.getData(i).getString("DISCNT_CODE")));
			}
		}

		return dataset;
	}

	/**
	 * 根据SEL_BY_USERID_DISCNT20查询信息
	 * 
	 * @param userId
	 * @param paramAttr
	 * @param paramCode
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset getDiscntByUserIdDiscnt20(String userId, String paramAttr, String paramCode, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("DISCNT_CODE", paramCode);
		param.put("PARAM_ATTR", paramAttr);
		param.put("PARAM_CODE", paramCode);
		param.put("EPARCHY_CODE", eparchyCode);

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_DISCNT20", param);
	}

	/**
	 * 获取用户老集团成员VPMN优惠
	 * 
	 * @param userId
	 * @param relationTypeCode
	 * @param packageId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getDiscntByUserIdOldVpmn(String userId, String relationTypeCode, String packageId) throws Exception
	{
		IData inparam = new DataMap();

		inparam.put("USER_ID", userId);
		inparam.put("RELATION_TYPE_CODE", relationTypeCode);
		inparam.put("PACKAGE_ID", packageId);

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USER_ID_OLDVPMN", inparam);
	}

	public static IDataset getDiscntsByPMode(String userId, String productMode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PRODUCT_MODE", productMode);

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_PRODUCTMODE", param);
	}

	/**
	 * 亲亲网套餐升级@yanwu
	 * 
	 * @param userId
	 * @param productMode
	 * @return
	 * @throws Exception
	 */
	public static IDataset getDiscntsByPModeUpdate(String userId, String productMode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PRODUCT_MODE", productMode);

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_PRODUCTMODE_UP", param);
	}

	/**
	 * REQ201505120004 亲亲网升级界面优化@yanwu
	 * 
	 * @param userId
	 * @param productMode
	 * @return
	 * @throws Exception
	 */
	public static IDataset getDiscntsByPModeUpdate01(String userId, String productMode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PRODUCT_MODE", productMode);

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_PRODUCTMODE_UP01", param);
	}

	/**
	 * 根据用户ID，优惠编码查询优惠信息
	 */
	public static IDataset getDiscntsByUserIdDiscntCode(String userId, String discntCode, String eparchyCode) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID", userId);
		inparam.put("DISCNT_CODE", discntCode);
		IDataset result = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_ID_USERD_DISCNT", inparam, eparchyCode);
		return result;
	}

	/**
	 * 根据user_id，discntCode 查询用户表无效的资费 chenyi 13-10-28
	 * 
	 * @param userId
	 * @param discntCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset getEndDiscntByUserId(String userId, String discntCode) throws Exception
	{

		IData inparams = new DataMap();
		inparams.put("USER_ID", userId);
		inparams.put("DISCNT_CODE", discntCode);
		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_ENDDISTINCTINFO_BY_PK", inparams);

		return userDiscnts;
	}

	/**
	 * todo code_code 表里没有SEL_EXIST_BY_DISCNT_CODE
	 * 通过USER_ID、USER_ID_A、PRODUCT_ID、PACKAGE_ID、SERVICE_ID、INST_ID查询用户某条服务
	 * 
	 * @param params
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset getExistUserSingleProductDis(String user_id, String user_id_a, String product_id, String package_id, String element_id) throws Exception
	{
		if (StringUtils.isBlank(user_id) || StringUtils.isBlank(product_id) || StringUtils.isBlank(package_id) || StringUtils.isBlank(element_id))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "USER_ID、PRODUCT_ID、PACKAGE_ID、DISCNT_CODE不能为空");
		}

		IData params = new DataMap();
		params.put("USER_ID", user_id);
		params.put("USER_ID_A", user_id_a);
		params.put("PRODUCT_ID", product_id);
		params.put("PACKAGE_ID", package_id);
		params.put("DISCNT_CODE", element_id);
		IDataset discntInfos = Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_EXIST_BY_DISCNT_CODE", params);

		if (IDataUtil.isEmpty(discntInfos))
		{
			return new DatasetList();
		}

		FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(discntInfos, params, null);

		discntInfos = FillUserElementInfoUtil.filterUserElementsByProductIdPackageId(discntInfos, product_id, package_id);

		return discntInfos;
	}

	/**
	 * 查询用户订购的集团彩铃
	 * 
	 * @param inparams
	 * @return
	 * @throws Exception
	 */
	public static IDataset getGrpColorRingCount(String userId) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", userId);

		SQLParser parser = new SQLParser(params);

		parser.addSQL(" SELECT COUNT(1) RECORDCOUNT ");
		parser.addSQL(" FROM TF_F_USER_DISCNT D ");
		parser.addSQL(" WHERE D.DISCNT_CODE IN (62000401, 62000405, 62000406, 62000407, 62000408, 62000409, 62000410)");
		parser.addSQL(" AND USER_ID = :USER_ID ");
		parser.addSQL(" AND sysdate between start_date and end_date ");

		IDataset dataset = Dao.qryByParse(parser, Route.CONN_CRM_CG);
		return dataset;
	}

	/**
	 * todo code_code 表里没有SEL_GRP_MEB_ALL_DIS 获取集团成员所有未结束的优惠
	 * 
	 * @author wenjb
	 */
	public static IDataset getGrpMemAllDiscnt(IData inparams) throws Exception
	{

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_GRP_MEB_ALL_DIS", inparams);
	}

	/**
	 * @Function: getGrpUserDiscntByUserId
	 * @Description: 该函数的功能描述
	 * @param:参数描述
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-26 下午2:05:50 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-26 updata v1.0.0 修改原因
	 */
	public static IDataset getGrpUserDiscntByUserId(String user_id) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNTINFO_BY_USRID", param);
	}

	public static IDataset getM2MUserDisntCommparaByUserId(String userId, String paramAttr, String paramCode, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PARAM_ATTR", paramAttr);
		param.put("PARAM_CODE", paramCode);
		param.put("EPARCHY_CODE", eparchyCode);

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_M2MMEMBER", param);
	}

	public static IDataset getM2MUserDisntCommparaUnionByUserId(String userId, String paramAttr, String paramCode, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PARAM_ATTR", paramAttr);
		param.put("PARAM_CODE", paramCode);
		param.put("EPARCHY_CODE", eparchyCode);

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_M2MANDMEMBER", param);
	}

	public static IDataset getMainFamUserId(String userId, String productMode, String brandCode) throws Exception
	{
		IData cond = new DataMap();
		cond.put("USER_ID", userId);
		cond.put("PRODUCT_MODE", productMode);
		cond.put("BRAND_CODE", brandCode);

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USER_ID_FAM", cond);
	}

	/*
	 * 功能：通过集团PRODUCT_ID查询成员所有优惠信息 @author jiangmj 2009-09-10 @param ctx @param
	 * params @return @throws Exception
	 */
	public static IDataset getMembDiscntByGrpProductId(String productId, String eparchyCode, String tradeStaffId) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("PRODUCT_ID", productId);
		inparams.put("EPARCHY_CODE", eparchyCode);
		inparams.put("TRADE_STAFF_ID", tradeStaffId);

		SQLParser parser = new SQLParser(inparams);

		parser.addSQL("SELECT D.DISCNT_CODE, ");
		parser.addSQL("'[' || D.DISCNT_CODE || ']' || D.DISCNT_NAME DISCNT_NAME, ");
		parser.addSQL("P.PRODUCT_ID, ");
		parser.addSQL("P.PACKAGE_ID, ");
		parser.addSQL("D.START_DATE, ");
		parser.addSQL("D.END_DATE ");
		parser.addSQL("FROM TD_B_DISCNT          D, ");
		parser.addSQL("TD_B_PACKAGE_ELEMENT E, ");
		parser.addSQL("TD_B_PRODUCT_PACKAGE P, ");
		parser.addSQL("TD_B_PRODUCT         T ");
		parser.addSQL("WHERE D.DISCNT_CODE = E.ELEMENT_ID ");
		parser.addSQL("AND E.PACKAGE_ID = P.PACKAGE_ID ");
		parser.addSQL("AND T.PRODUCT_ID = P.PRODUCT_ID ");
		parser.addSQL("AND SYSDATE BETWEEN P.START_DATE AND P.END_DATE ");
		parser.addSQL("AND SYSDATE BETWEEN D.START_DATE AND D.END_DATE ");
		parser.addSQL("AND SYSDATE BETWEEN E.START_DATE AND E.END_DATE ");
		parser.addSQL("AND SYSDATE BETWEEN T.START_DATE AND T.END_DATE ");
		parser.addSQL("AND T.PRODUCT_MODE = 13 ");
		parser.addSQL("AND T.PRODUCT_ID LIKE :PRODUCT_ID || '%' ");
		parser.addSQL("AND E.ELEMENT_TYPE_CODE = 'D' ");
		parser.addSQL("AND D.OBJ_TYPE_CODE = '0' ");
		parser.addSQL("AND (D.EPARCHY_CODE = :EPARCHY_CODE OR D.EPARCHY_CODE = 'ZZZZ') ");
		parser.addSQL("AND NOT EXISTS ");
		parser.addSQL("(SELECT 1 ");
		parser.addSQL(" FROM TD_S_COMMPARA ");
		parser.addSQL("WHERE PARAM_ATTR IN (98, 158) ");
		parser.addSQL("AND SUBSYS_CODE = 'CSM' ");
		parser.addSQL("AND SYSDATE BETWEEN START_DATE AND END_DATE ");
		parser.addSQL("AND (EPARCHY_CODE = :EPARCHY_CODE OR EPARCHY_CODE = 'ZZZZ') ");
		parser.addSQL("AND TO_NUMBER(PARAM_CODE) = D.DISCNT_CODE)    ");
		parser.addSQL("ORDER BY D.DISCNT_CODE ");
		return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
	}

	/**
	 * 根据userId和userIdA，查询集团成员订购的优惠
	 * 
	 * @param partitionId
	 * @param userId
	 * @param userIdA
	 * @param productId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getMemberDiscntByUserIdUserIdA(String partitionId, String userId, String userIdA, String productId) throws Exception
	{
		IData param = new DataMap();
		param.put("PARTITION_ID", partitionId);
		param.put("USER_ID", userId);
		param.put("USER_ID_A", userIdA);
		param.put("PRODUCT_ID", productId);
		IDataset userDiscnts = Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_BY_USERID_PID_M", param, Route.CONN_CRM_CG);

		if (IDataUtil.isNotEmpty(userDiscnts))
		{
			FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userDiscnts, param, null);
		}

		return userDiscnts;
	}

	/**
	 * todo 在code_code 表里没有找到SEL_DISCNT_BY_USRID_USRIDA 获取集团成员的已有优惠
	 */
	public static IDataset getMemPrimaryDiscnt(IData inparams) throws Exception
	{

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNT_BY_USRID_USRIDA", inparams);
	}

	/**
	 * @Function: getNowVaildByUserIdDiscnt
	 * @Description: 根据user_id discnt_code，sysdate BETWEEN start_date AND
	 *               end_date 查询优惠信息
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: lijm3
	 * @date: 2013-5-16 上午11:29:27 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-5-16 lijm3 v1.0.0 修改原因
	 */
	public static IDataset getNowVaildByUserIdDiscnt(String user_id, String discnt_code) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("DISCNT_CODE", discnt_code);
		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_DISCNT_NOW_VALID", param);

		if (IDataUtil.isNotEmpty(userDiscnts))
		{
			FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userDiscnts, null, null);
		}

		return userDiscnts;
	}

	public static IDataset getRoamDiscntByPackageId(String userId, String packageId, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PACKAGE_ID", packageId);
		param.put("EPARCHY_CODE", eparchyCode);
		IDataset dataset = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_ROAMDISCNT_BY_PACKAGEID", param);
		return dataset;
	}

	public static IDataset getSmsMessageByTradeId(String tradeId) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		IDataset datasetinfo = Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_GPRS_CXB_SMSMESSAGE", param, Route.getJourDb());
		for (int i = 0; i < datasetinfo.size(); i++)
		{
			IData discntTrade = datasetinfo.getData(i);
			String elementId = discntTrade.getString("DISCNT_CODE");
			String disnctCodeType = UDiscntInfoQry.getDiscntTypeByDiscntCode(elementId);
			if (!"5".equals(disnctCodeType) || "LLCX".equals(disnctCodeType))
			{
				datasetinfo.remove(i);
				i--;
			}
		}
		return datasetinfo;
	}

	public static IDataset getSpecDiscnt(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_VALID", param);

		if (IDataUtil.isNotEmpty(userDiscnts))
		{
			FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userDiscnts, null, "");
		}

		return userDiscnts;
	}

	/**
	 * todo code_code 表里没有找到SEL_USERID_A_THISMONTH_DISCNT
	 * 根据集团编号GROUP_ID进行集团成员用户信息查询 add by zhangbs
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static IDataset getThisMonthDiscntByUseridA(IData data) throws Exception
	{

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USERID_A_THISMONTH_DISCNT", data, Route.CONN_CRM_CG);
	}

	public static IDataset getUserAllDiscntByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USERDISCNT_ALL", param);
	}

	public static IDataset getUserByDiscntCode(String userId, String discntCode) throws Exception
	{
		IData cond = new DataMap();
		cond.put("USER_ID", userId);
		cond.put("DISCNT_CODE", discntCode);

		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_BY_PK", cond);

		if (IDataUtil.isNotEmpty(userDiscnts))
		{
			FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userDiscnts, null, null);
		}

		return userDiscnts;
	}

	public static IDataset getUserColseDiscntByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USERDISCNT_CLOSE", param);
	}

	public static IDataset getUserDiscnt(String userId, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("EPARCHY_CODE", eparchyCode);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_IVRDISCNT", param);
	}

	public static IDataset getUserDiscntByCommparaValid(String userId, String paramCode, String paramAttr, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PARAM_ATTR", paramAttr);
		param.put("PARAM_CODE", paramCode);
		param.put("EPARCHY_CODE", eparchyCode);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_COMMPARA_VALID", param);
	}

	/**
	 * Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_COMMPARA_VALID" 语句中的TD_B_DISCNT
	 * 拆分
	 * 
	 * @param userId
	 * @param paramCode
	 * @param paramAttr
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserDiscntByCommparaValid_1(String userId, String paramCode, String paramAttr, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PARAM_ATTR", paramAttr);
		param.put("PARAM_CODE", paramCode);
		param.put("EPARCHY_CODE", eparchyCode);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_COMMPARA_VALID_1", param);
	}

	/**
	 * @Function: getUserDiscntByDiscntCode
	 * @Description: 通过USER_ID、USER_ID_A、DISCNT_CODE查询用户优惠
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-26 下午2:10:36 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-26 updata v1.0.0 修改原因
	 */
	public static IDataset getUserDiscntByDiscntCode(String user_id, String user_id_a, String discnt_code, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("USER_ID_A", user_id_a);
		param.put("DISCNT_CODE", discnt_code);
		return Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_USERID_A_DISCNT", param, pagination);
	}

	/**
	 * @Function: getUserDiscntByDiscntCode
	 * @Description: 该函数的功能描述
	 * @param:参数描述
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: lijm3
	 * @date: 2013-12-5 上午10:28:15 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-12-5 lijm3 v1.0.0 修改原因
	 */
	public static IDataset getUserDiscntByDiscntCode(String user_id, String user_id_a, String discnt_code, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("USER_ID_A", user_id_a);
		param.put("DISCNT_CODE", discnt_code);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USERID_A_DISCNT", param, eparchyCode);
	}

	public static IDataset getUserDiscntByPk(String userId) throws Exception
	{

		IData param = new DataMap();
		param.put("USER_ID", userId);
		IDataset userDiscnts = Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_BY_PK", param);

		if (IDataUtil.isNotEmpty(userDiscnts))
		{
			FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userDiscnts, null, null);
		}

		return userDiscnts;
	}

	/**
	 * @Function: getUserDiscntByPk
	 * @Description: 获取用户服务信息,
	 *               必需要USER_ID,PRODUCT_ID,PACKAGE_ID,DISCNT_CODE,不需要USER_ID_A
	 *               ,START_DATE
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-26 下午2:13:01 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-26 updata v1.0.0 修改原因
	 */
	public static IDataset getUserDiscntByPk(String user_id, String product_id, String package_id, String discnt_code) throws Exception
	{

		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("PRODUCT_ID", product_id);
		param.put("PACKAGE_ID", package_id);
		param.put("DISC", discnt_code);
		IDataset dataset = Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_USRDISCNT_BYID", param);
		return dataset;
	}

	public static IDataset getUserDiscntBySelUserIdA(String userIda) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIda);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USER_ID_A", param);
	}

	/**
	 * 通过PRODUCT_ID、SPEC_TAG、RELATION_TYPE_CODE查询有效用户优惠
	 * 
	 * @param userId
	 * @param specTag
	 * @param relationTypeCode
	 * @param productId
	 * @return
	 * @throws Exception
	 *             wangjx 2013-10-20
	 */
	public static IDataset getUserDiscntBySpecRela(String userId, String specTag, String relationTypeCode, String productId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SPEC_TAG", specTag);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		param.put("PRODUCT_ID", productId);

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_SPEC_RELA", param);
	}

	/**
	 * 根据开始时间，结束时间查询
	 * 
	 * @param userId
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserDiscntByStartEndDate(String userId, String startDate, String endDate) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", userId);
		params.put("START_DATE", startDate);
		params.put("END_DATE", endDate);

		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_STARTDATE_ENDDATE", params);

		if (IDataUtil.isNotEmpty(userDiscnts))
		{
			FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userDiscnts, null, null);
		}

		return userDiscnts;
	}

	/**
	 * todo code_code 表里没有SEL_BY_UIDA_UIDB 查询集团成员用户优惠信息
	 * 
	 * @param param
	 *            参数
	 * @param pagination
	 *            分页
	 * @return 用户订购商品优惠信息
	 * @throws Exception
	 * @author
	 */
	public static IDataset getUserDiscntByUIdAIdB(IData param, Pagination pagination) throws Exception
	{

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_UIDA_UIDB", param, pagination, Route.CONN_CRM_CG);
	}

	/**
	 * 获取国漫优惠
	 * 
	 * @param userId
	 * @param packageId
	 * @param elementId
	 * @param elementTypeCode
	 * @return
	 * @throws Exception
	 * @author wangww
	 */
	public static IDataset getUserDiscntByUserAndElementId(String userId, String packageId, String elementId, String elementTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PACKAGE_ID", packageId);
		param.put("ELEMENT_ID", elementId);
		param.put("ELEMENT_TYPE_CODE", elementTypeCode);
		return Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_BY_PKG_USER_ID", param);
	}

	/**
	 * 
	 * @param userId
	 * @param packageId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserDiscntByTheMonth(String userId, String packageId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PACKAGE_ID", packageId);
		return Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_BY_USERID_THEMONTH", param);
	}

	/**
	 * todo getVisit().setRouteEparchyCode(ConnMgr.CONN_CRM_CG);怎么处理
	 * 根据user_id查询集团用户的优惠
	 * 
	 * @param userId
	 *            用户编码
	 * @return IData 返回用户实例信息
	 */
	public static IData getUserDiscntByUserId(IData params) throws Exception
	{

		// TODO getVisit().setRouteEparchyCode(ConnMgr.CONN_CRM_CG);

		IDataset userdiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USER_ID_PRODUCT_ID", params);
		if (userdiscnts.size() > 0)
		{
			FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userdiscnts, params, null);
			userdiscnts = FillUserElementInfoUtil.filterUserElementsByProductIdPackageId(userdiscnts, params.getString("PRODUCT_ID"), null);
			if (IDataUtil.isEmpty(userdiscnts))
			{
				return new DataMap();
			}
			return userdiscnts.getData(0);
		} else
		{
			return new DataMap();
		}
	}

	/**
	 * 查询当前包下优惠
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserDiscntByUserId(IData param, String eparchyCode) throws Exception
	{
		String effectTime = param.getString("EFFECT_TIME");

		String conSql = " AND END_DATE> SYSDATE";

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select USER_ID,USER_ID_A,DISCNT_CODE,INST_ID,START_DATE ");
		parser.addSQL("	from tf_f_user_discnt   ");
		parser.addSQL(" where  user_id=:USER_ID   ");
		// parser.addSQL(" and PACKAGE_ID=:PACKAGE_ID    ");
		parser.addSQL(" and (user_id_a=:USER_ID_A  or  user_id_a='-1') ");
		// 下月生效可以加元素判断
		if (effectTime.equals("1"))
		{
			conSql = " AND END_DATE> to_date('" + SysDateMgr.getLastDateThisMonth() + "','yyyy-mm-dd hh24:mi:ss')";
		}
		parser.addSQL(conSql);
		IDataset dataset = Dao.qryByParse(parser, eparchyCode);

		FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(dataset, param, null);
		dataset = FillUserElementInfoUtil.filterUserElementsByProductIdPackageId(dataset, null, param.getString("PACKAGE_ID"));

		return dataset;
	}

	public static IDataset getUserDiscntByUserId(String userId, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("EPARCHY_CODE", eparchyCode);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_GGTH_BY_USERID", param);
	}

	/**
	 * @Function: getUserDiscntByUserId
	 * @Description:从集团库里 根据user_id查询集团用户的优惠
	 * @param:参数描述
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-26 下午2:16:09 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-26 updata v1.0.0 修改原因
	 */
	public static IData getUserDiscntByUserId(String user_id, String package_id, String discnt_code) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("PACKAGE_ID", package_id);
		param.put("ELEMENT_ID", discnt_code);

		IDataset userdiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USER_ID_PRODUCT_ID", param, Route.CONN_CRM_CG);
		if (userdiscnts.size() > 0)
		{
			return userdiscnts.getData(0);
		}
		return new DataMap();
	}

	/**
	 * @param inparams
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserDiscntByUserID(IData inparams) throws Exception
	{
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_DISCNT_VALIB", inparams, Route.CONN_CRM_CG);
	}

	/**
	 * 根据user_id、user_id_a查询集团用户的优惠
	 * 
	 * @return IData 返回用户优惠信息
	 * @author xiajj
	 */
	public static IDataset getUserDiscntByUserIdAB(String USER_ID, String user_ida) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", USER_ID);
		params.put("USER_ID_A", user_ida);
		IDataset userdiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USERDISCNT_USERID_USERIDA", params);

		FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userdiscnts, params, null);// 填充productId和packageId

		return userdiscnts;
	}

	/**
	 * @Function: getUserDiscntByUserIdAB
	 * @Description:从集团库中 根据user_id、user_id_a查询集团用户的优惠
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-26 下午2:19:45 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-26 updata v1.0.0 修改原因
	 */
	public static IDataset getUserDiscntByUserIdABForGrp(String user_id, String user_id_a) throws Exception
	{

		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("USER_ID_A", user_id_a);
		IDataset userdiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USERDISCNT_USERID_USERIDA", param, Route.CONN_CRM_CG);

		FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userdiscnts, param, null);// 填充productId和packageId

		return userdiscnts;
	}

	public static IDataset getUserDiscntByUserIdAfterLastYear(String userId, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("EPARCHY_CODE", eparchyCode);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_GGTH_BY_USERID_LASTYEAR", param);
	}

	public static IDataset getUserDiscntByUserIdUserIdA(String userId, String user_id_a) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("USER_ID_A", user_id_a);

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERIDA_FMY", param);
	}

	public static IDataset getUserDiscntForce(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_FORCEDISCNT", param);
	}

	/**
	 * @Function: getUserDiscntForModify45
	 * @Description: 根据user_id查询对应的用户实例化资料信息 对于vpmn业务直接查表TF_F_USER_VPN 得到用户个性化数据
	 *               UserDom::USER_VPN::TF_F_USER_VPN::SEL_BY_USERID
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-26 下午2:21:13 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-26 updata v1.0.0 修改原因
	 */
	public static int getUserDiscntForModify45(String user_id, String discnt_code) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("DISCNT_CODE", discnt_code);

		IDataset datas = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_FOR_MODIFY45", param);
		if (datas != null)
		{
			return datas.size();
		}
		return 0;
	}

	/**
	 * todo 是参数传进来的 根据sql_ref, eparchy_code查询用户优惠信息
	 * 
	 * @param inparams
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserDiscntInfo(IData inparams) throws Exception
	{

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_GRP_BYUSERID", inparams);

	}

	/**
	 * @Function: getUserDiscntInfoByAB
	 * @Description: 根据集团用户ID,成员用户ID及优惠编码查询
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-26 下午2:23:40 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-26 updata v1.0.0 修改原因
	 */
	public static IDataset getUserDiscntInfoByAB(String user_id, String discnt_code, String user_id_a) throws Exception
	{
		IData data = new DataMap();
		data.put("USER_ID", user_id);
		data.put("DISCNT_CODE", discnt_code);
		data.put("USER_ID_A", user_id_a);

		SQLParser parser = new SQLParser(data);
		parser.addSQL("SELECT PARTITION_ID, to_char(USER_ID) USER_ID, to_char(USER_ID_A) USER_ID_A, PRODUCT_ID, PACKAGE_ID, DISCNT_CODE, SPEC_TAG, RELATION_TYPE_CODE, to_char(INST_ID) INST_ID, to_char(CAMPN_ID) CAMPN_ID, to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2, RSRV_NUM3, to_char(RSRV_NUM4) RSRV_NUM4, to_char(RSRV_NUM5) RSRV_NUM5, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, to_char(RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, to_char(RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, to_char(RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, RSRV_TAG1, RSRV_TAG2, RSRV_TAG3");
		parser.addSQL(" FROM tf_f_user_discnt where 1=1");
		parser.addSQL(" and user_id = TO_NUMBER(:USER_ID)");
		parser.addSQL(" AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)");
		parser.addSQL(" AND discnt_code = :DISCNT_CODE");
		parser.addSQL(" AND user_id_a = TO_NUMBER(:USER_ID_A)");
		parser.addSQL(" order by start_date desc");
		IDataset dataset = Dao.qryByParse(parser);

		FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(dataset, data, null);

		return dataset;
	}

	public static IDataset getUserDiscntInfoByUserId(String userId) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_GRP_BY_USERID", inparam);
	}

	public static IDataset getUserDiscntInfoByUserIdAB(String userId, String user_id_a) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("USER_ID_A", user_id_a);

		IDataset discntInfos = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_USERIDA", param);
		if (IDataUtil.isEmpty(discntInfos))
			return discntInfos;
		/*
		 * for (int i = 0; i < discntInfos.size(); i++) { IData discnt =
		 * discntInfos.getData(i); String discntName =
		 * UDiscntInfoQry.getDiscntNameByDiscntCode
		 * (discnt.getString("DISCNT_CODE")); discnt.put("DISCNT_NAME",
		 * discntName); discnt.put("ELEMENT_NAME", discntName); }
		 */

		return discntInfos;
	}

	public static IDataset getUserDiscntMultiThisMonth(String userId, String paramCode, String eparchyCode) throws Exception
	{
		IData data = new DataMap();
		data.put("USER_ID", userId);
		data.put("PARAM_CODE", paramCode);
		data.put("EPARCHY_CODE", eparchyCode);

		return Dao.qryByCode("TD_S_CPARAM", "UserDiscntMultiThisMonth", data);
	}

	/**
	 * todo getVisit().setRouteEparchyCode( eparchycode); 怎么处理 获取产品信息
	 * 
	 * @author tengg
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserDiscntOfPID(IData params, String eparchycode) throws Exception
	{

		// TODO getVisit().setRouteEparchyCode( eparchycode);
		IDataset dataset = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_PID", params, eparchycode);
		return dataset;
	}

	public static IDataset getUserDiscntOfPID(String user_id, String partition_id, String product_id) throws Exception
	{

		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("PARTITION_ID", partition_id);
		param.put("PRODUCT_ID", product_id);

		IDataset dataset = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_PID", param);
		return dataset;
	}

	/**
	 * 根据优惠编码集获取优惠列表
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserDiscnts(IData data) throws Exception
	{
		SQLParser sqlParser = new SQLParser(data);
		sqlParser.addSQL("select inst_id,partition_id, user_id, user_id_a, discnt_code, spec_tag, relation_type_code, inst_id, campn_id, start_date, end_date,");
		sqlParser.addSQL(" update_time, update_staff_id, update_depart_id, remark, rsrv_num1, rsrv_num2, rsrv_num3, rsrv_num4, rsrv_num5, rsrv_str1, rsrv_str2, rsrv_str3,");
		sqlParser.addSQL(" rsrv_str4, rsrv_str5, rsrv_date1, rsrv_date2, rsrv_date3, rsrv_tag1, rsrv_tag2, rsrv_tag3");
		sqlParser.addSQL("  from TF_F_USER_DISCNT a");
		sqlParser.addSQL(" where partition_id = MOD(TO_NUMBER(:USER_ID),10000)");
		sqlParser.addSQL(" and user_id = :USER_ID");
		sqlParser.addSQL(" and a.discnt_code in(" + data.get("DISCNTCODES") + ")");
		sqlParser.addSQL(" and a.inst_id in(" + data.get("INSTIDS") + ")");

		return Dao.qryByParse(sqlParser);
	}

	public static IDataset getUserDiscntsBySelbySpec1(String userId, String trade_eparchy_code, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("TRADE_EPARCHY_CODE", trade_eparchy_code);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_SPEC_1", param, pagination);
	}

	public static IDataset getUserDiscntsBySelbySpecG1(String userId, String trade_eparchy_code, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("TRADE_EPARCHY_CODE", trade_eparchy_code);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_SPEC_G_1", param, pagination);
	}

	public static IDataset getUserDiscntsBySelbyUseridaSpectag(String userIda, String spectag, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", userIda);
		param.put("SPEC_TAG", spectag);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SET_BY_USERIDA_SPECTAG", param, pagination);
	}

	public static IDataset getUserDiscntsBySelForSpecfmystop(String user_id, String product_id) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("PRODUCT_ID", product_id);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_FOR_SPECFMYSTOP", param);
	}

	public static IDataset getUserDiscntsByUserId(String userId, String eparchyCode) throws Exception
	{

		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("EPARCHY_CODE", eparchyCode);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_DISCNT_LIMIT_NP2", param);
	}

	/**
	 * 获得用户当前的亲亲优惠信息
	 * 
	 * @param userId
	 * @param discntCode
	 * @return
	 * @throws Exception
	 * @author zhouwu
	 */
	public static IDataset getUserDiscntValidForever(String userId, String discntCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("DISCNT_CODE", discntCode);

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_VALID_FMYDISCNT", param);
	}

	public static IDataset getUserDisntsBylimitNp(String userId, String eparchyCode) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", userId); // 用户标识
		params.put("EPARCHY_CODE", eparchyCode); // 兑换优惠代码

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_DISCNT_LIMIT_NP", params);
	}

	/**
	 * @Function: getUserFamilyDiscntsByIdA
	 * @Description: 该函数的功能描述
	 * @param:参数描述
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: lijm3
	 * @date: 2013-10-20 下午9:23:23 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-10-20 lijm3 v1.0.0 修改原因
	 */
	public static IDataset getUserFamilyDiscntsByIdA(String user_id_a) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID_A", user_id_a);
		SQLParser sp = new SQLParser(param);
		sp.addSQL("select a.user_id_a, a.user_id,a.INST_ID,a.PRODUCT_ID,a.PACKAGE_ID,a.CAMPN_ID, c.serial_number, a.discnt_code, b.discnt_name, to_char(a.start_date, 'yyyy-mm-dd hh24:mi:ss') start_date, to_char(a.end_date, 'yyyy-mm-dd hh24:mi:ss') end_date, to_char(a.update_time, 'yyyy-mm-dd hh24:mi:ss') update_time, a.remark ");
		sp.addSQL("  from tf_f_user_discnt a, td_b_discnt b, tf_f_user c ");
		sp.addSQL(" where a.discnt_code = b.discnt_code and a.user_id = c.user_id(+) ");
		sp.addSQL("   and a.end_date > sysdate ");
		sp.addSQL("   and a.user_id_a = :USER_ID_A ");
		return Dao.qryByParse(sp);
	}

	/**
	 * todo code_code 表没有找到SEL_GDDISCNTS_BYUSERID
	 * 
	 * @description:根据user_id_a，user_id_b查询成员已订购的定制优惠
	 * @author wusf
	 * @date Sep 29, 2010
	 * @param data
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserGdDiscntsByUserId(IData data, String eparchyCode) throws Exception
	{

		IDataset dataset = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_GDDISCNTS_BYUSERID", data, eparchyCode);
		return dataset;
	}

	/**
	 * @param userId
	 * @param partitionId
	 * @param paramAttr
	 * @param paramCode
	 * @return
	 * @throws Exception
	 * @author wangww3
	 */
	public static IDataset getUserIdValid(String userId, String partitionId, String paramAttr, String paramCode) throws Exception
	{
		IData cond = new DataMap();
		cond.put("USER_ID", userId);
		cond.put("PARTITION_ID", partitionId);
		cond.put("PARAM_ATTR", paramAttr);
		cond.put("PARAM_CODE", paramCode);

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_COMMPARA_VALID", cond);
	}

	/**
	 * 获取用户的品牌、产品、主优惠信息
	 * 
	 * @param pd
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserInfoBySN(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SN", serialNumber);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNT_BY_SN_LINK_PRODUCT", param);
	}

	public static IDataset getUserLimitDiscntsByUserId(String user_id, String discnt_name, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("DISCNT_NAME", discnt_name);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_LIMIT_DISCNT_BY_USERID", param, pagination);
	}

	public static IDataset getUserNormalDiscntByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USERDISCNT_NORMAL", param);
	}
	
	public static IDataset getUserNormalDiscntByUserId2(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USERDISCNT_NORMAL2", param);
	}

	/**
	 * @Function: getUserPlatDiscnt
	 * @Description查询用户的平台业务优惠
	 * @param userId
	 * @param discntCode
	 *            优惠编码，用逗号加单引号分隔成多个，如'20091018','20081030'
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-26 下午2:42:13 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-26 updata v1.0.0 修改原因
	 */
	public static IDataset getUserPlatDiscnt(String userId, String discntCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("DISCNT_CODE", discntCode);
		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USER_PLATDISCNT", param);

		FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userDiscnts, param, null);

		return userDiscnts;
	}

	/**
	 * 获取用户赠送优惠信息
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */

	public static IDataset getUserPresentDiscnts(String userId, String discntCode, String instId) throws Exception
	{
		IData cond = new DataMap();
		cond.put("USER_ID", userId);
		cond.put("DISCNT_CODE", discntCode);
		cond.put("DISCNT_INST_ID", instId);
		return Dao.qryByCode("TF_F_PRESENT_DISCNT", "SEL_BY_DISCNT_CODE", cond);
	}

	public static IDataset getUserProdDisByUserIdProdIdPkgIdDisIdEndDate(String user_id, String product_id, String package_id, String discnt_code, String end_date) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("PRODUCT_ID", product_id);
		param.put("PACKAGE_ID", package_id);
		param.put("DISCNT_CODE", discnt_code);
		param.put("END_DATE", end_date);

		IDataset userDiscnts = Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_BY_USERID_PROD_PKG_DIS_ENDDATE", param);

		FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userDiscnts, param, null);

		userDiscnts = FillUserElementInfoUtil.filterUserElementsByProductIdPackageId(userDiscnts, product_id, package_id);

		return userDiscnts;
	}

	public static IDataset getUserProdDisByUserIdProdIdPkgIdDisIdStartDate(String user_id, String product_id, String package_id, String discnt_code, String start_date) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("PRODUCT_ID", product_id);
		param.put("PACKAGE_ID", package_id);
		param.put("DISCNT_CODE", discnt_code);
		param.put("START_DATE", start_date);

		return Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_BY_USERID_PROD_PKG_DIS_STARTDATE", param);
	}

	/**
	 * 查询一个用户定购某个集团用户的产品的优惠信息
	 * 
	 * @param params
	 *            参数信息
	 * @param pagination
	 * @throws Exception
	 * @author zhouli
	 */
	public static IDataset getUserProductDis(String userId, String userIdA) throws Exception
	{
		if (StringUtils.isBlank(userId))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "USER_ID不能为空");
		}

		IData params = new DataMap();
		params.put("USER_ID", userId);
		params.put("USER_ID_A", userIdA);

		IDataset resultset = Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_USERDISCNT_USERID_USERIDA", params);

		if (IDataUtil.isEmpty(resultset))
			return resultset;

		FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(resultset, params, null);// 填充productId和packageId

		for (int i = 0; i < resultset.size(); i++)
		{
			IData result = resultset.getData(i);
			String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(result.getString("DISCNT_CODE"));
			result.put("DISCNT_NAME", discntName);
			result.put("ELEMENT_NAME", discntName);
		}
		return resultset;
	}

	/**
	 * 查询一个物联网用户定购某个集团用户的产品的优惠信息
	 * 
	 * @param params
	 *            参数信息
	 * @param pagination
	 * @throws Exception
	 * @author zhouli
	 */
	public static IDataset getPwlwProductDis(String userId, String userIdA) throws Exception
	{
		if (StringUtils.isBlank(userId))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "USER_ID不能为空");
		}

		IData params = new DataMap();
		params.put("USER_ID", userId);
		params.put("USER_ID_A", userIdA);

		IDataset resultset = Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_USERDISCNT_USERID_END", params);

		if (IDataUtil.isEmpty(resultset))
			return resultset;

		FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(resultset, params, null);

		for (int i = 0; i < resultset.size(); i++)
		{
			IData result = resultset.getData(i);
			String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(result.getString("DISCNT_CODE"));
			result.put("DISCNT_NAME", discntName);
			result.put("ELEMENT_NAME", discntName);
		}
		return resultset;
	}

	/**
	 * todo code_code 表里没有SEL_BY_USERID_USERIDA_XINJ 查询一个用户定购某个集团用户的产品的优惠信息
	 * 
	 * @param params
	 *            参数信息
	 * @param pagination
	 * @throws Exception
	 * @author zhouli
	 */
	public static IDataset getUserProductDisctForXinj(String userId, String user_id_a, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("USER_ID_A", user_id_a);
		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_USERIDA_XINJ", param, pagination);

		FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userDiscnts, param, null);

		return userDiscnts;
	}

	/*
	 * 根据user_id_a查询销户时一起终止的优惠
	 */
	public static IDataset getUserRealEndDiscntByUserIDA(String userIdA) throws Exception
	{

		IData param = new DataMap();
		param.put("USER_ID_A", userIdA);
		IDataset userDiscnts = Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_END_RELADISCNT_A_1", param);
		return userDiscnts;
	}

	/**
	 * 获取用户所有有效的依赖358VPMN的优惠
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserRelyVpmnDiscnt(String userId) throws Exception
	{
		IData inparam = new DataMap();

		inparam.put("USER_ID", userId);

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_RELY_VPMN_DISCNT", inparam);
	}

	/**
	 * 根据USER_ID,USER_ID_A,RELATION_TYPE_CODE查询
	 * 
	 * @param userId
	 * @param userIdA
	 * @param relaitonTypeCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserRlDiscnt(String userId, String userIdA, String relaitonTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("USER_ID_A", userIdA);
		param.put("RELATION_TYPE_CODE", relaitonTypeCode);

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_RL_DISCNT", param);
	}

	/**
	 * 获取用户赠送优惠
	 * 
	 * @param serialNumber
	 * @return
	 * @throws Exception
	 */

	public static IDataset getUserSendBackDiscnt(String serialNumber, Pagination pagination) throws Exception
	{
		IData cond = new DataMap();
		cond.put("SERIAL_NUMBER", serialNumber);
		IDataset crmSet = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_PRESENT_BY_SN_4CTT_CRM", cond, pagination);
		int crmSetSize = crmSet.size();
		if (IDataUtil.isNotEmpty(crmSet))
		{
			IDataset paramSets = CommparaInfoQry.get1234By5000(cond);
			if (IDataUtil.isNotEmpty(paramSets))
			{
				for (int j = 0; j < crmSetSize; j++)
				{
					for (int i = 0; i < paramSets.size(); i++)
					{
						if (paramSets.getData(i).getString("PARAM_CODE").equals(crmSet.getData(j).get("DISCNT_CODE")))
						{
							crmSet.getData(j).put("IS_HAVE", "1");
							// crmSet.getData(j).put("M_MONEY",
							// paramSets.getData(i).getString("PARA_CODE1"));
							// crmSet.getData(j).put("M_COUNT",
							// paramSets.getData(i).getString("PARA_CODE2"));
							// crmSet.getData(j).put("T_MONEY",
							// paramSets.getData(i).getString("PARA_CODE3"));
							// crmSet.getData(j).put("T_COUNT",
							// paramSets.getData(i).getString("PARA_CODE4"));
							crmSet.getData(j).putAll(paramSets.getData(i));
							break;
						}
					}
				}
			}
			for (int n = 0; n < crmSetSize; n++)
			{
				if (!"1".equals(crmSet.getData(n).getString("IS_HAVE")))
				{
					crmSet.remove(n);
				}
			}
		}

		IDataset jourSet = Dao.qryByCode("TF_B_TRADE_DISCNT", "SEL_PRESENT_BY_SN_4CTT_JOUR", cond, pagination, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
		int jourSetSize = jourSet.size();
		if (IDataUtil.isNotEmpty(jourSet))
		{
			IDataset paramSets = CommparaInfoQry.get1234By5000(cond);
			if (IDataUtil.isNotEmpty(paramSets))
			{
				for (int j = 0; j < jourSetSize; j++)
				{
					for (int i = 0; i < paramSets.size(); i++)
					{
						if (paramSets.getData(i).getString("PARAM_CODE").equals(jourSet.getData(j).get("DISCNT_CODE")))
						{
							jourSet.getData(j).put("IS_HAVE", "1");
							// jourSet.getData(j).put("M_MONEY",
							// paramSets.getData(i).getString("M_MONEY"));
							// jourSet.getData(j).put("M_COUNT",
							// paramSets.getData(i).getString("M_COUNT"));
							// jourSet.getData(j).put("T_MONEY",
							// paramSets.getData(i).getString("T_MONEY"));
							// jourSet.getData(j).put("T_COUNT",
							// paramSets.getData(i).getString("T_COUNT"));
							jourSet.getData(j).putAll(paramSets.getData(i));
							break;
						}
					}
				}
			}
			for (int n = 0; n < jourSetSize; n++)
			{
				if (!"1".equals(jourSet.getData(n).getString("IS_HAVE")))
				{
					jourSet.remove(n);
				}
			}
		}

		// if(IDataUtil.isNotEmpty(crmSet))
		// {
		// returnSet.add(crmSet);
		// }
		if (IDataUtil.isNotEmpty(jourSet))
		{
			crmSet.add(jourSet);
		}

		// 根据discntcode查询次数discntname
		if (IDataUtil.isNotEmpty(crmSet))
		{
			for (int i = 0; i < crmSet.size(); i++)
			{
				String discnt_name = UDiscntInfoQry.getDiscntNameByDiscntCode(crmSet.getDataset(i).getData(i).getString("DISCNT_CODE"));
				crmSet.getDataset(i).getData(i).put("DISCNT_NAME", discnt_name);
			}
			return crmSet.getDataset(0);
		} else
			return new DatasetList();

	}

	/**
	 * 通过USER_ID、USER_ID_A、PRODUCT_ID、PACKAGE_ID、SERVICE_ID、INST_ID查询用户某条服务
	 * 
	 * @param params
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserSingleProductDis(String userId, String userIdA, String productId, String packgeId, String serviceId, String instId, Pagination pagination) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", userId);
		params.put("USER_ID_A", userIdA);
		params.put("PRODUCT_ID", productId);
		params.put("PACKAGE_ID", packgeId);
		params.put("DISCNT_CODE", serviceId);
		params.put("INST_ID", instId);
		IDataset discntInfos = Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_BY_DISCNT_CODE", params, pagination);

		if (IDataUtil.isEmpty(discntInfos))
			return discntInfos;

		FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(discntInfos, params, null);// 填充productId和packageId

		// 传了productId或者packageId 在过滤下
		discntInfos = FillUserElementInfoUtil.filterUserElementsByProductIdPackageId(discntInfos, productId, packgeId);

		return discntInfos;
	}

	/**
	 * @Function: getUserSingleProductDisParser
	 * @Description: 
	 *               通过USER_ID、USER_ID_A、PRODUCT_ID、PACKAGE_ID、SERVICE_ID、INST_ID查询用户某条服务
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-26 下午2:28:10 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-26 updata v1.0.0 修改原因
	 */
	public static IDataset getUserSingleProductDisParser(String user_id, String user_id_a, String product_id, String package_id, String discnt_code, String inst_id, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("USER_ID_A", user_id_a);
		param.put("PRODUCT_ID", product_id);
		param.put("PACKAGE_ID", package_id);
		param.put("DISCNT_CODE", discnt_code);
		param.put("INST_ID", inst_id);

		IDataset discntInfos = Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_BY_DISCNT_CODE", param, pagination);// 分页不准了

		if (IDataUtil.isEmpty(discntInfos))
			return discntInfos;

		FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(discntInfos, param, null);// 填充productId和packageId

		// 传了productId或者packageId 在过滤下
		discntInfos = FillUserElementInfoUtil.filterUserElementsByProductIdPackageId(discntInfos, product_id, package_id);

		return discntInfos;
	}

	/**
	 * @Function: getUserProductDiscntByUserIdAndInstId
	 * @Description: 通过USER_ID、INST_ID查询用户某条服务
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-26 下午2:28:10 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-26 updata v1.0.0 修改原因
	 */
	public static IDataset getUserProductDiscntByUserIdAndInstId(String user_id, String inst_id) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("INST_ID", inst_id);

		IDataset discntInfos = Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_BY_DISCNT_CODE", param);

		if (IDataUtil.isEmpty(discntInfos))
			return discntInfos;

		FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(discntInfos, param, null);// 填充productId和packageId

		return discntInfos;
	}

	/**
	 * 获取用户校园宽带优惠信息
	 * 
	 * @param userId
	 * @param discntTypeCode
	 * @return
	 * @throws Exception
	 * @author chenzm
	 */
	public static IDataset getUserSpecDiscnt(String userId, String discntTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("DISCNT_TYPE_CODE", discntTypeCode);
		IDataset userInfos = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_NOW_SPEC_DISCNT", param);
		if (IDataUtil.isNotEmpty(userInfos))
		{
			for (int i = 0; i < userInfos.size(); i++)
			{
				IData offer = userInfos.getData(i);
				String code = offer.getString("DISCNT_CODE", "");
				IDataset res1 = UDiscntInfoQry.queryDiscntTypeByDiscntCode(code);
				if (IDataUtil.isNotEmpty(res1))
				{
					for (int j = 0; j < res1.size(); j++)
					{
						if (!(discntTypeCode.equals(res1.getData(j).getString("FIELD_VALUE", ""))))
						{
							userInfos.remove(i);
							i--;
						} else
						{
							offer.put("DISCNT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(code));
						}
					}
				} else
				{
					userInfos = new DatasetList();
					break;
				}
			}
		}
		return userInfos;
	}

	/**
	 * 获取用户VPMN358优惠
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserVpmn358Discnt(String userId, String relationTypeCode, String packageId) throws Exception
	{
		IData inparam = new DataMap();

		inparam.put("USER_ID", userId);
		inparam.put("RELATION_TYPE_CODE", relationTypeCode);
		inparam.put("PACKAGE_ID", packageId);

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USER_ID_VPMN1", inparam);
	}

	/**
	 * 根据userId relationTypeCode packageId 获取VPMN优惠信息
	 * 
	 * @param userId
	 * @param relationTypeCode
	 * @param packageId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserVPMNDiscntUpc(String userId, String relationTypeCode, String packageId) throws Exception
	{
		IData param = new DataMap();
		param.clear();
		param.put("USER_ID", userId);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		// param.put("PACKAGE_ID", packageId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USER_ID_VPMN_1", param);
	}

	/**
	 * todo code_code 没有找到SEL_VALID_VPMN_DISCNT
	 * 根据USER_ID,RELATION_TYPE_CODE获取当前生效记录
	 * 
	 * @param inparams
	 * @return
	 * @throws Exception
	 */
	public static IDataset getValidDiscnt(IData inparams) throws Exception
	{

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_VALID_VPMN_DISCNT", inparams);
	}

	/**
	 * @param userId
	 * @param productId
	 * @return
	 * @throws Exception
	 * @author wangww3
	 */
	public static IDataset getVirUserDiscnts(String userId, String productId) throws Exception
	{
		IData cond = new DataMap();
		cond.put("USER_ID", userId);
		cond.put("PRODUCT_ID", productId);

		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BUNDLEDISCNT_BY_PRODUCTID", cond);

		FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userDiscnts, cond, null);

		userDiscnts = FillUserElementInfoUtil.filterUserElementsByProductIdPackageId(userDiscnts, productId, null);

		return userDiscnts;
	}

	/**
	 * 查358套是否已经订购
	 * 
	 * @param inparams
	 * @return
	 * @throws Exception
	 */
	public static IDataset getVpnSpecialChangeDiver(String user_id, String user_id_a, String diveStartDate, String diveEndDate) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("USER_ID_A", user_id_a);
		// param.put("PRODUCT_ID", product_id);
		// param.put("PACKAGE_ID", package_id);
		param.put("DIVE_START_DATE", diveStartDate);
		param.put("DIVE_END_DATE", diveEndDate);
		return Dao.qryByCodeParser("TF_F_USER_DISCNT", "VPN_SPECIALCHANGE_DIVER", param);
	}

	/**
	 * 作用：查询用户已删除的VPMN358优惠信息
	 * 
	 * @param pd
	 * @param params
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryDelUsrDiscnt358(String userId, String endDate, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("END_DATE", endDate);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USERING_DISCNT_THIS_MONTH_358", param, routeId);
	}

	public static IDataset qryDisInstId(IData dt) throws Exception
	{

		SQLParser parser = new SQLParser(dt);

		parser.addSQL(" SELECT d.inst_id ");
		parser.addSQL(" FROM   tf_f_user_discnt d,tf_f_user u ");
		parser.addSQL(" WHERE  d.user_id=u.user_id ");
		parser.addSQL("        AND d.user_id = :USER_ID AND d.relation_type_code = :RELATION_TYPE_CODE ");
		parser.addSQL("        AND d.end_date>sysdate AND u.remove_tag='0' ");

		IDataset resIds = Dao.qryByParse(parser);

		return resIds;
	}

	/**
	 * @Function: qryDisInstId
	 * @Description: 该函数的功能描述
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-26 下午2:28:52 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-26 updata v1.0.0 修改原因
	 */
	public static IDataset qryDisInstId(String user_id, String relation_type_code) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("RELATION_TYPE_CODE", relation_type_code);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT d.inst_id ");
		parser.addSQL(" FROM   tf_f_user_discnt d,tf_f_user u ");
		parser.addSQL(" WHERE  d.user_id=u.user_id ");
		parser.addSQL("        AND d.user_id = :USER_ID AND d.relation_type_code = :RELATION_TYPE_CODE ");
		parser.addSQL("        AND d.end_date>sysdate AND u.remove_tag='0' ");
		IDataset resIds = Dao.qryByParse(parser);
		return resIds;
	}

	/**
	 * @Function: qrySMSDiscntInfo
	 * @Description: 短信行业优惠号码查询
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-26 下午2:31:03 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-26 updata v1.0.0 修改原因
	 */
	public static IDataset qrySMSDiscntInfo(String discnt_code, String SMSQueryTypeStr, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("DISCNT_CODE", discnt_code);

		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT A.DISCNT_CODE, B.SERIAL_NUMBER SERIAL_NUMBER,TO_CHAR(A.START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE,");
		parser.addSQL("       TO_CHAR(A.END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE,TO_CHAR(B.USER_ID) USER_ID ");
		parser.addSQL("FROM TF_F_USER_DISCNT A,TF_F_USER B ");
		parser.addSQL("WHERE   B.USER_ID = A.USER_ID ");
		parser.addSQL("     AND B.PARTITION_ID = A.PARTITION_ID ");
		parser.addSQL("     AND A.DISCNT_CODE = TO_NUMBER( :DISCNT_CODE ) ");
		if (null != SMSQueryTypeStr)
		{
			if (SMSQueryTypeStr.equals("0"))
			{// 0-生效
				parser.addSQL("    AND SYSDATE BETWEEN START_DATE AND END_DATE ");
			} else if (SMSQueryTypeStr.equals("1"))
			{// 1-未生效
				parser.addSQL("    AND START_DATE>SYSDATE AND END_DATE>START_DATE  ");
			} else if (SMSQueryTypeStr.equals("2"))
			{// 2-已失效
				parser.addSQL("    AND END_DATE<SYSDATE AND END_DATE>START_DATE ");
			} else if (SMSQueryTypeStr.equals("3"))
			{// 3-全部 不加日期限制
				// todo nothing;
			}
		}

		return Dao.qryByParse(parser, pagination);
	}

	/**
	 * 查询用户所有优惠资料
	 * 
	 * @param userId
	 * @param relatioTypeCode
	 * @return IDataset
	 * @throws Exception
	 *             wangjx 2013-7-23
	 */
	public static IDataset qryUserDiscntByIdCodeFromDB(String userId, String relatioTypeCode, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("RELATION_TYPE_CODE", relatioTypeCode);

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_RELACODE_ALL", param, routeId);
	}

	/**
	 * 作用：查询用户当月已删除的VPMN358优惠信息
	 * 
	 * @param pd
	 * @param inParams
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryUserDiscntbyThisMonth(String userId, String relaTypeCode, String productId, String packageId, String endDate, String routeId) throws Exception
	{
		IData param = new DataMap();

		param.put("USER_ID", userId);
		param.put("RELATION_TYPE_CODE", relaTypeCode);
		param.put("PRODUCT_ID", productId);
		param.put("PACKAGE_ID", packageId);
		param.put("END_DATE", endDate);
		IDataset result = Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_USER_DISCNT_BY_THIS_MONTH", param, routeId);

		FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(result, param, null);

		result = FillUserElementInfoUtil.filterUserElementsByProductIdPackageId(result, productId, packageId);

		return result;
	}

	public static IDataset qryUserDiscntByUserId(String userId, Pagination pg) throws Exception
	{
		IData param = new DataMap();

		param.put("USER_ID", userId);

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_ALL_BY_USERID", param, pg);
	}

	/**
	 * 查询用户失效的优惠资料
	 * 
	 * @param userId
	 * @param relatioTypeCode
	 * @param routeId
	 * @return IDataset
	 * @throws Exception
	 *             wangjx 2013-9-2
	 */
	public static IDataset qryUserEndDiscntByIdCodeFromDB(String userId, String relatioTypeCode, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("RELATION_TYPE_CODE", relatioTypeCode);

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_RELACODE_END", param, routeId);
	}

	/**
	 * 查询用户有效的优惠资料
	 * 
	 * @param userId
	 * @param relatioTypeCode
	 * @param routeId
	 * @return IDataset
	 * @throws Exception
	 *             wangjx 2013-8-1
	 */
	public static IDataset qryUserNormalDiscntByIdCodeFromDB(String userId, String relatioTypeCode, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("RELATION_TYPE_CODE", relatioTypeCode);

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_RELACODE", param, routeId);
	}

	public static IDataset qryUserOwn(String userId, String type, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("DISCNT_TYPE_CODE", type);
		IDataset userInfos = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USERDISCNT_BY_TYPE", param, routeId);
		if (IDataUtil.isNotEmpty(userInfos))
		{
			for (int i = 0; i < userInfos.size(); i++)
			{
				IData offer = userInfos.getData(i);
				String code = offer.getString("ELEMENT_ID", "");
				IDataset res1 = UDiscntInfoQry.queryDiscntTypeByDiscntCode(code);
				if (IDataUtil.isNotEmpty(res1))
				{
					for (int j = 0; j < res1.size(); j++)
					{
						if (!(type.equals(res1.getData(j).getString("FIELD_VALUE", ""))))
						{
							userInfos.remove(i);
							i--;
						} else
						{
							offer.put("ELEMENT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(offer.getString("ELEMENT_ID")));
						}
					}
				} else
				{
					userInfos.remove(i);
					i--;
				}
			}
		}
		return userInfos;
	}

	public static IDataset queryAllUserDiscntsInSelectedElements(String userId, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		IDataset dataset = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_DISCNT_IN_SELECTED_ALL", param, eparchyCode);
		return dataset;
	}

	public static IDataset queryBindDiscntEquals(String USER_ID, String SP_CODE, String SERVICE_CODE, String EPARCHY_CODE, String DATA_SP_CODE) throws Exception
	{
		IData cond = new DataMap();
		cond.put("USER_ID", USER_ID);
		cond.put("SP_CODE", SP_CODE);
		cond.put("DATA_SP_CODE", DATA_SP_CODE);
		cond.put("SERVICE_CODE", SERVICE_CODE);
		cond.put("EPARCHY_CODE", EPARCHY_CODE);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNT_BY_SPDISCNTCODE2", cond, Route.CONN_CRM_CEN);
	}

	public static IDataset queryBindDiscntUnequals(String USER_ID, String SP_CODE, String SERVICE_CODE, String EPARCHY_CODE, String DATA_SP_CODE) throws Exception
	{
		IData cond = new DataMap();
		cond.put("USER_ID", USER_ID);
		cond.put("SP_CODE", SP_CODE);
		cond.put("DATA_SP_CODE", DATA_SP_CODE);
		cond.put("SERVICE_CODE", SERVICE_CODE);
		cond.put("EPARCHY_CODE", EPARCHY_CODE);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNT_BY_SPDISCNTCODE", cond, Route.CONN_CRM_CEN);
	}

	/**
	 * @Function: queryClusterHisProduct
	 * @Description: 查询历史家庭产品
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-26 下午2:32:09 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-26 updata v1.0.0 修改原因
	 */
	public static IDataset queryClusterHisProduct(String user_id, String relation_type_code) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("RELATION_TYPE_CODE", relation_type_code);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT t.*,x.discnt_name FROM tf_f_user_discnt t, td_b_discnt x where t.relation_type_code=:RELATION_TYPE_CODE  ");
		parser.addSQL(" and t.end_date < sysdate ");
		parser.addSQL(" and t.user_id =:USER_ID ");
		parser.addSQL(" and t.discnt_code = x.discnt_code ");
		return Dao.qryByParse(parser);
	}

	/**
	 * 查询用户申请办理手机保障服务相关的功能费优惠信息
	 * 
	 * @param params
	 *            USER_ID DISCNT_CODE 查询所需参数
	 * @return IDataset
	 * @throws Exception
	 */
	public static IDataset queryDiscntByUserIdAndDiscntCode(String userId, String discntCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("DISCNT_CODE", discntCode);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_ID_USERD_DISCNT", param);
	}

	/**
	 * 查询用户申请办理手机保障服务相关的功能费优惠信息
	 * 
	 * @param params
	 *            USER_ID DISCNT_CODE 查询所需参数
	 * @return IDataset
	 * @throws Exception
	 */
	public static IDataset queryDiscntByUserIdAndDiscntCode2(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_ID_USERD_DISCNT2", param);
	}

	public static IDataset queryDiscntByUserIdForIntf(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNT_BY_USERID_FOR_INTF", param);

	}

	public static IDataset queryDiscntByUserIdForIntf2(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		StringBuilder sql = new StringBuilder(1000);
		sql.append("SELECT B.DISCNT_CODE,B.INST_ID,  ");
		sql.append("       TO_CHAR(B.START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE, ");
		sql.append("       TO_CHAR(B.END_DATE, 'YYYY-MM-DD HH24:MI:SS') END_DATE ");
		sql.append("  FROM TF_F_USER_DISCNT B ");
		sql.append(" WHERE B.USER_ID = :USER_ID ");
		sql.append("   AND B.PARTITION_ID = MOD(:USER_ID, 10000) ");
		sql.append("   AND SYSDATE <= B.END_DATE ");
		sql.append("   AND NOT EXISTS (SELECT 1 ");
		sql.append("          FROM TD_S_COMMPARA C ");
		sql.append("         WHERE C.SUBSYS_CODE = 'CSM' ");
		sql.append("           AND C.PARAM_ATTR = '2' ");
		sql.append("           AND C.PARA_CODE1 = 'SJYYT' ");
		sql.append("           AND C.END_DATE > SYSDATE ");
		sql.append("           AND C.PARAM_CODE = B.DISCNT_CODE) ");

		return Dao.qryBySql(sql, param);

	}

	public static IDataset queryDiscntByUserIdVpmnActive(String user_id, String user_id_a, String product_id, String package_id, String subsys_code, String param_attr, String param_code, String eparchy_code) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("USER_ID_A", user_id_a);
		param.put("PRODUCT_ID", product_id);
		param.put("PACKAGE_ID", package_id);
		param.put("SUBSYS_CODE", subsys_code);
		param.put("PARAM_ATTR", param_attr);
		param.put("PARAM_CODE", param_code);
		param.put("EPARCHY_CODE", eparchy_code);
		IDataset dataset = queryDiscntByUserIdVpmnActive(param);
		IDataset result = new DatasetList();
		if (IDataUtil.isNotEmpty(dataset))
		{
			for (int i = 0; i < dataset.size(); i++)
			{
				IData data = dataset.getData(i);
				String discnCode = data.getString("DISCNT_CODE");
				IData temp = UDiscntInfoQry.getDiscntInfoByPk(discnCode);
				if (IDataUtil.isNotEmpty(temp))
				{
					result.add(data);
				}
			}
		}
		return result;
	}

	/**
	 * 根据USER_ID,INST_ID查询
	 * 
	 * @param USER_ID
	 * @param INST_ID
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryDiscntInfosByInstId(String USER_ID, String INST_ID) throws Exception
	{
		IData cond = new DataMap();
		cond.put("USER_ID", USER_ID);
		cond.put("INST_ID", INST_ID);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNT_BY_INSTID", cond);
	}

public static IDataset queryDiscntInfosByInstId(String USER_ID, String INST_ID,String string) throws Exception
	{
		return queryDiscntInfosByInstId(USER_ID, INST_ID);
	}	/**
	 * 根据USER_ID,EPARCHY_CODE,SERVICE_ID查询
	 * 
	 * @param USER_ID
	 * @param EPARCHY_CODE
	 * @param SERVICE_ID
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryDiscntsByCommpara(String USER_ID, String EPARCHY_CODE, String SERVICE_ID) throws Exception
	{
		IData cond = new DataMap();
		cond.put("USER_ID", USER_ID);
		cond.put("SERVICE_ID", SERVICE_ID);
		cond.put("EPARCHY_CODE", EPARCHY_CODE);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNT_BY_USERID_COMMPARA", cond);
	}

	/**
	 * 通过集团PRODUCT_ID查询成员所有优惠信息
	 * 
	 * @author fengsl
	 * @date 2013-02-26
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryDiscntsByProductId(String productId, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		param.put("PRODUCT_ID", productId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT d.discnt_name element_name, d.DISCNT_CODE element_id,pm.product_id_b product_id,");
		parser.addSQL(" pp.package_id PACKAGE_ID ,d.start_date,d.end_date,p.min_number,p.max_number,d.eparchy_code, ");
		parser.addSQL(" pe.end_enable_tag,pe.end_absolute_date,pe.end_offset,pe.end_unit");
		parser.addSQL(" FROM td_b_discnt d, td_b_package p, td_b_package_element pe, td_b_product_package pp, td_b_product_meb pm");
		parser.addSQL(" WHERE pm.product_id = :PRODUCT_ID  AND pe.force_tag='0' ");
		parser.addSQL(" AND p.package_id=pe.package_id  AND pp.product_id = pm.PRODUCT_ID_B AND SYSDATE BETWEEN pp.START_DATE AND pp.END_DATE ");
		parser.addSQL(" AND pe.package_id = pp.package_id  AND SYSDATE BETWEEN pe.START_DATE AND pe.END_DATE  AND pe.element_type_code = 'D' ");
		parser.addSQL(" AND d.DISCNT_CODE = pe.ELEMENT_ID  AND (d.eparchy_code=:EPARCHY_CODE OR d.eparchy_code='ZZZZ')  ");
		parser.addSQL(" AND not exists (select 1 from td_s_commpara  where param_attr in (98,158) ");
		parser.addSQL(" AND subsys_code='CSM' AND sysdate between start_date and end_date ");
		parser.addSQL(" AND (eparchy_code=:EPARCHY_CODE or eparchy_code='ZZZZ') AND to_number(PARAM_CODE)=D.discnt_code) ");
		parser.addSQL(" ORDER BY D.discnt_code ");
		IDataset discntList = Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
		// 对查询出来的资费进行权限处理
		DiscntPrivUtil.filterDiscntListByPriv(CSBizBean.getVisit().getStaffId(), discntList);
		return discntList;
	}

	public static IDataset queryDiscntsByUserIdProdIdPkgId(String user_id, String user_id_a, String product_id, String package_id) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("USER_ID_A", user_id_a);
		param.put("PRODUCT_ID", product_id);
		param.put("PACKAGE_ID", package_id);
		IDataset userDiscnts = Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_BY_USERID_PRO_PACKAGE", param);

		FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userDiscnts, param, null);

		userDiscnts = FillUserElementInfoUtil.filterUserElementsByProductIdPackageId(userDiscnts, product_id, package_id);

		return userDiscnts;
	}

	/**
	 * 根据USER_ID,EPARCHY_CODE,SERVICE_ID,SERVICE_CODE,SP_CODE查询
	 * 
	 * @param USER_ID
	 * @param EPARCHY_CODE
	 * @param SERVICE_ID
	 * @param SERVICE_CODE
	 * @param SP_CODE
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryDiscntsUnionCommpara(String USER_ID, String EPARCHY_CODE, String SERVICE_ID, String SERVICE_CODE, String SP_CODE) throws Exception
	{
		IData cond = new DataMap();
		cond.put("USER_ID", USER_ID);
		cond.put("SERVICE_ID", SERVICE_ID);
		cond.put("SERVICE_CODE", SERVICE_CODE);
		cond.put("EPARCHY_CODE", EPARCHY_CODE);
		cond.put("SP_CODE", SP_CODE);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNT_COMMPARA", cond);
	}

	/**
	 * 根据USER_ID查询
	 * @param USER_ID
	 * @return  IDataset   DISCNT_CODE
	 * @throws Exception
	 */
	public static IDataset queryDiscntsCodeByusrid(String USER_ID) throws Exception
	{
		IData cond = new DataMap();
		cond.put("USER_ID", USER_ID);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNTCODE_BYUSERID", cond);
	}

	public static IDataset queryFirstCallBindDiscnts(String userId, String param_attr, String param_code, String para_code5) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PARAM_ATTR", param_attr);
		param.put("PARAM_CODE", param_code);
		param.put("PARA_CODE5", para_code5);

		// 查询集团用户的优惠情况
		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT COUNT(1) IV_COUNT0,max(a.discnt_code) DISCNT_CODE, max(b.para_code1) SP_SERVICE_ID ");
		parser.addSQL("FROM tf_f_user_discnt a, td_s_commpara b ");
		parser.addSQL("WHERE a.discnt_code=b.para_code2 ");
		parser.addSQL("AND a.partition_id=MOD(:USER_ID,10000) ");
		parser.addSQL(" AND a.user_id=:USER_ID ");
		parser.addSQL(" AND a.end_date>a.start_date ");
		parser.addSQL(" AND a.end_date>trunc(last_day(Sysdate)+1)-1/24/60/60 ");
		parser.addSQL(" AND SYSDATE BETWEEN a.start_date AND a.end_date ");
		parser.addSQL(" AND b.param_attr=:PARAM_ATTR ");
		parser.addSQL(" AND b.param_code=:PARAM_CODE ");
		parser.addSQL(" And b.para_code5=:PARA_CODE5 ");
		parser.addSQL(" AND b.end_date>SYSDATE ");
		IDataset userBindDiscnts = Dao.qryByParse(parser);
		return userBindDiscnts;
	}

	public static IDataset queryInterRoamDayInfo(String userId, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("EPARCHY_CODE", eparchyCode);

		SQLParser parser = new SQLParser(param);
		// 第三代订单中心修改 lijun17
		// parser.addSQL("select c.*,to_char(u.START_DATE,'yyyy-mm-dd hh24:mi:ss') EFFECT_TIME,");
		// parser.addSQL(" to_char(u.END_DATE,'yyyy-mm-dd hh24:mi:ss') VALID_TIME,p.package_desc,case when");
		// parser.addSQL(" u.end_date>sysdate  then 1 else 2 end state from tf_f_user_discnt u,td_s_commpara c,td_b_package p");
		// parser.addSQL(" WHERE c.subsys_code='CSM' AND c.param_attr='2789' AND sysdate BETWEEN ");
		// parser.addSQL(" c.start_date AND c.end_date AND (c.eparchy_code=:EPARCHY_CODE OR c.eparchy_code='ZZZZ')");
		// parser.addSQL(" and c.param_code=p.package_id and sysdate BETWEEN p.start_date AND p.end_date");
		// parser.addSQL(" and u.user_id = TO_NUMBER(:USER_ID) AND u.partition_id = MOD(TO_NUMBER(:USER_ID),10000)");
		// parser.addSQL(" AND u.discnt_code = to_number(c.para_code1) order by u.end_date desc");
		parser.addSQL("select c.*,to_char(u.START_DATE,'yyyy-mm-dd hh24:mi:ss') EFFECT_TIME,");
		parser.addSQL(" to_char(u.END_DATE,'yyyy-mm-dd hh24:mi:ss') VALID_TIME,case when");
		parser.addSQL(" u.end_date>sysdate  then 1 else 2 end state from tf_f_user_discnt u,td_s_commpara c");
		parser.addSQL(" WHERE c.subsys_code='CSM' AND c.param_attr='2789' AND sysdate BETWEEN ");
		parser.addSQL(" c.start_date AND c.end_date AND (c.eparchy_code=:EPARCHY_CODE OR c.eparchy_code='ZZZZ')");
		parser.addSQL(" and u.user_id = TO_NUMBER(:USER_ID) AND u.partition_id = MOD(TO_NUMBER(:USER_ID),10000)");
		parser.addSQL(" AND u.discnt_code = to_number(c.para_code1) order by u.end_date desc");

		return Dao.qryByParse(parser);
	}

	/**
	 * 根据产品查询VPMN优惠信息，
	 * 
	 * @author fengsl
	 * @date 2013-02-26
	 * @param inparams
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryMembVPMNDiscntByGrpProductId(String userId, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		param.put("USER_ID", userId);

		SQLParser parser = new SQLParser(param);
		// CSAppEntity dao = new CSAppEntity( ConnMgr.CONN_CRMCEN);

		parser.addSQL(" SELECT d.discnt_name element_name,  t.element_id element_id, t.product_id, t.package_id,");
		parser.addSQL(" to_char(t.start_date, 'yyyy-mm-dd') start_date,to_char(t.end_date, 'yyyy-mm-dd') end_date, p.min_number,p.max_number ");
		parser.addSQL(" FROM tf_f_user_grp_package t,td_b_discnt d ,td_b_package p");
		parser.addSQL(" where 1=1 AND t.element_id = d.discnt_code AND t.element_type_code = 'D' AND p.package_id=t.package_id ");
		parser.addSQL(" and t.user_id = :USER_ID AND partition_id=mod(TO_NUMBER(:USER_ID),10000) ");
		parser.addSQL(" AND SYSDATE BETWEEN t.START_DATE AND t.END_DATE ORDER by t.element_type_code desc ");
		return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CEN);
	}

	/**
	 * @Function: queryOnlineDiscntBySn
	 * @Description: 根据手机号码判断是否已经办理了网龄优惠
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-26 下午2:33:08 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-26 updata v1.0.0 修改原因
	 */
	public static IDataset queryOnlineDiscntBySn(String serial_number, String package_id) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);
		param.put("PACKAGE_ID", package_id);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select 1 from tf_f_user a,tf_F_user_discnt b");
		parser.addSQL(" where a.user_id=b.user_id");
		parser.addSQL(" and a.remove_tag='0'");
		parser.addSQL(" and a.serial_number=:SERIAL_NUMBER");
		parser.addSQL(" and b.package_id= :PACKAGE_ID ");
		parser.addSQL(" and sysdate between b.start_date and b.end_date");

		return Dao.qryByParse(parser);
	}

	/**
	 * @Function: queryOnlinePeriod
	 * @Description: 根据用户的在网年龄查询对应的网龄优惠
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-26 下午2:33:52 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-26 updata v1.0.0 修改原因
	 */
	public static IDataset queryOnlinePeriod(String serial_number, String package_id) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);
		param.put("PACKAGE_ID", package_id);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT a.package_id,a.element_type_code,a.element_id,a.main_tag,a.default_tag,a.force_tag,a.enable_tag,to_char(a.start_absolute_date, 'yyyy-mm-dd hh24:mi:ss') start_absolute_date,a.start_offset,a.start_unit,a.end_enable_tag,to_char(a.end_absolute_date, 'yyyy-mm-dd hh24:mi:ss') end_absolute_date,a.end_offset,a.end_unit,a.cancel_tag,to_char(a.cancel_absolute_date, 'yyyy-mm-dd hh24:mi:ss') cancel_absolute_date,a.cancel_offset,a.cancel_unit,to_char(a.start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date, 'yyyy-mm-dd hh24:mi:ss') end_date,a.item_index,to_char(a.update_time, 'yyyy-mm-dd hh24:mi:ss') update_time,a.update_staff_id,a.update_depart_id,a.remark");
		parser.addSQL(" from td_b_package_element a,td_s_commpara b,tf_f_user c");
		parser.addSQL(" WHERE 1 = 1 ");
		parser.addSQL(" and c.serial_number=:SERIAL_NUMBER");
		parser.addSQL(" and c.remove_tag='0'");
		parser.addSQL(" and b.param_attr='1205'");
		parser.addSQL("	and b.subsys_code='CSM'");
		parser.addSQL(" and b.para_code1 < TO_NUMBER(sysdate - c.open_date) / 365");
		parser.addSQL(" and b.para_code2 > TO_NUMBER(sysdate - c.open_date) / 365");
		parser.addSQL(" and a.package_id= :PACKAGE_ID");
		parser.addSQL(" and a.element_id=b.param_code");

		return Dao.qryByParse(parser);
	}

	/**
	 * @Function: queryOnlyDiscntByUserID
	 * @Description:根据成员USER_ID和集团PRODUCT_ID查询成员的优惠
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-26 下午2:35:33 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-26 updata v1.0.0 修改原因
	 */
	public static IDataset queryOnlyDiscntByUserID(String serial_number, String product_id) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", serial_number);
		param.put("PRODUCT_ID", product_id);
		IDataset result = Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_BYGRP_USERID", param);
		return result;
	}

	public static IDataset queryProdDisctInfos(String USER_ID, String PARAM_ATTR) throws Exception
	{
		IData cond = new DataMap();
		cond.put("USER_ID", USER_ID);
		cond.put("PARAM_ATTR", PARAM_ATTR);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNT_BY_USERID_PARAMATTR", cond);
	}

	/**
	 * 查询集团客户已经订购了年包赠送流量优惠((目前版本写死))
	 * 
	 * @author liaoyi 2010-11-24
	 * @param cycle
	 * @throws Throwable
	 */
	public static IDataset querySaleActiveDiscnt(String userId, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		// 查询集团用户的优惠情况
		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT  count(1) RECORDCOUNT");
		parser.addSQL(" FROM  TF_F_USER_DISCNT d");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" AND d.USER_ID = :USER_ID");
		parser.addSQL(" AND d.partition_id = mod(d.user_id,10000)");
		parser.addSQL(" AND d.discnt_code in (" + "62400792,62400794,62400796,62400798,62400800," + "62400802,62400804,62400806,62400808,62400810," + "62400812,62400814,62400816,62400818,62400820)");
		parser.addSQL(" AND d.end_date > Sysdate");
		IDataset saleActiveInfos = Dao.qryByParse(parser, Route.CONN_CRM_CG);
		return saleActiveInfos;
	}
	
	/**
	 * 根据USER_ID和discnt_code获取用户所有优惠信息，包括已失效和未失效的
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserAllDiscntByUserIdAndDiscntCode(String userId, String discntCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("DISCNT_CODE", discntCode);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_ALL_BY_USERID_AND_DISCNT_CODE", param);
	}
	
	/**
	 * 根据USER_ID获取用户所有优惠信息
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserAllDiscntByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_ALL", param);
	}
	
	public static IDataset queryUserAllGrpDiscntByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_ALLGRP", param);
	}

	public static IDataset queryUserAllDiscntByUserIdA(String userIdA) throws Exception
	{
		IData cond = new DataMap();
		cond.put("USER_ID_A", userIdA);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_ALL_BY_USERIDA", cond);
	}

	/**
	 * 根据USER_ID获取用户所有优惠信息
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserAllDiscntByUserIdForGrp(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_ALL", param, Route.CONN_CRM_CG);

	}

	public static IDataset queryUserAllDiscntsByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_ALL_DISCNT_BY_USERID", param);
	}

	/**
	 * @Function: queryUserAllValidDiscnt
	 * @Description: 该函数的功能描述
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-26 下午2:35:49 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-26 updata v1.0.0 修改原因
	 */
	public static IDataset queryUserAllValidDiscnt(String userId) throws Exception
	{
		IData cond = new DataMap();
		cond.put("USER_ID", userId);
		IDataset dataset = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_INS_DESTROY_DISCNT", cond);
		return dataset;
	}

	public static IDataset queryUserCloseDiscntsByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_COLSE_DISCNT_BY_USERID", param);
	}

	/**
	 * @Function: queryUserDiscnt
	 * @Description: 查询用户已有优惠，用于产品变更前台展示
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-26 下午2:36:09 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-26 updata v1.0.0 修改原因
	 */
	public static IDataset queryUserDiscnt(String userId) throws Exception
	{

		IData param = new DataMap();

		param.put("USER_ID", userId);

		return Dao.qryByCode("TF_F_USER_DISCNT", "USER_DISCNT_SEL", param);
	}

	public static IDataset queryUserDiscntByCode(String userId, String discntCode) throws Exception
	{
		IData cond = new DataMap();
		cond.put("USER_ID", userId);
		cond.put("DISCNT_CODE", discntCode);

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_ALLDISCNT_BY_USER1", cond);
	}

	/**
	 * 根据user_id和param_attr查询用户优惠
	 * 
	 * @param userId
	 * @param paramAttr
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserDiscntByParamattr(String userId, String paramAttr) throws Exception
	{
		IData param = new DataMap();

		param.put("USER_ID", userId);
		param.put("PARAM_ATTR", paramAttr);

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_ATTR", param);
	}

	/**
	 * @Function: queryUserDiscntByUserId
	 * @Description: 根据USER_ID获取优惠信息
	 * @param: @param userId
	 * @param: @return
	 * @param: @throws Exception
	 * @return：IDataset
	 * @throws：
	 * @version: v1.0.0
	 * @author: Administrator
	 * @date: 3:56:06 PM Jul 25, 2013 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------* Jul 25,
	 *        2013 longtian3 v1.0.0
	 *        TODO:与queryUserNormalDiscntByUserId方法一样,由于QueryInfoBean类提交不了
	 *        ,先暂时新增这个方法编译过去
	 */
	public static IDataset queryUserDiscntByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_NORMAL_DISCNT_BY_USERID", param);
	}

	public static IDataset queryUserDiscntByUserIdA(String userIdA) throws Exception
	{
		IData cond = new DataMap();
		cond.put("USER_ID_A", userIdA);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERIDA", cond);
	}

	public static IDataset queryUserDiscntByUserIdABAndRelaType(String userIdA, String userId, String relationTypeCode) throws Exception
	{
		IData cond = new DataMap();
		cond.put("USER_ID_A", userIdA);
		cond.put("USER_ID", userId);
		cond.put("RELATION_TYPE_CODE", relationTypeCode);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_RELADISCNT_USERID_A", cond);
	}

	public static IDataset queryUserDiscntByUserIdAndInstId(IData iData) throws Exception
	{
		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_INSTID", iData);

		FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userDiscnts, iData, null);

		return userDiscnts;
	}

	public static IDataset queryUserDiscntByUserIdAndRelaTradeId(String userId, String tradeId, String campnId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("RELATION_TRADE_ID", tradeId);
		param.put("CAMPN_ID", campnId);

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_SALEACTIVE", param);
	}

	/**
	 * @param userId
	 * @param relation_type_code
	 * @return IDataset
	 * @throws Exception
	 *             wangjx 2013-5-28
	 */
	public static IDataset queryUserDiscntByUserIdLast(String userId, String relation_type_code) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("RELATION_TYPE_CODE", relation_type_code);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USER_ID_RL", param);
	}

	/**
	 * @param userId
	 * @param relation_type_code
	 * @return IDataset
	 * @throws Exception
	 *             wukw3 2015-4-15
	 */
	public static IDataset queryUserDiscntByUserIdRelation(String userId, String relation_type_code) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("RELATION_TYPE_CODE", relation_type_code);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USER_ID_R", param);
	}

	/**
	 * @param userId
	 * @param relation_type_code
	 * @param routeId
	 * @return
	 * @throws Exception
	 *             wangjx 2013-5-28
	 */
	public static IDataset queryUserDiscntByUserIdLast(String userId, String relation_type_code, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("RELATION_TYPE_CODE", relation_type_code);
		IDataset productList = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USER_ID_RL", param, routeId);
		return productList.isEmpty() ? null : productList;
	}

	/**
	 * @data 2013-9-23
	 * @param userId
	 * @param removeTag
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserDiscntByUserIdPID(String userId, String removeTag, Pagination pagination) throws Exception
	{

		IData param = new DataMap();
		param.put("PARA_CODE1", userId);
		param.put("PARA_CODE2", removeTag);

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNT_BY_USERID_PID", param, pagination);
	}

	/**
	 * @data 2013-9-23
	 * @param userId
	 * @param removeTag
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserDiscntByUserIdPPD(String userId, String removeTag, Pagination pagination) throws Exception
	{

		IData param = new DataMap();
		param.put("PARA_CODE1", userId);
		param.put("PARA_CODE2", removeTag);

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNT_BY_USERID_PPD", param, pagination);
	}

	/**
	 * @data 2013-9-23
	 * @param userId
	 * @param removeTag
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserDiscntByUserIdUPID(String userId, String removeTag, Pagination pagination) throws Exception
	{

		IData param = new DataMap();
		param.put("PARA_CODE1", userId);
		param.put("PARA_CODE2", removeTag);

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNT_BY_USERID_UPID", param, pagination);
	}

	/**
	 * @data 2013-9-23
	 * @param userId
	 * @param removeTag
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserDiscntByUserIdUPPD(String userId, String removeTag, Pagination pagination) throws Exception
	{

		IData param = new DataMap();
		param.put("PARA_CODE1", userId);
		param.put("PARA_CODE2", removeTag);

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNT_BY_USERID_UPPD", param, pagination);
	}

	public static IDataset queryUserDiscntForTime(String userId, String discntCode, String paraCode11, String paraCode12) throws Exception
	{
		IData cond = new DataMap();
		cond.put("USER_ID", userId);
		cond.put("DISCNT_CODE", discntCode);
		if (StringUtils.isNotBlank(paraCode11))
		{
			cond.put("PARA_CODE11", paraCode11);
		}
		cond.put("PARA_CODE12", paraCode12);

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNT_BY_STARTDATE", cond);
	}

	public static IDataset queryUserDiscntsForTime(String userId, String paraCode24, String paraCode11, String paraCode12, String eparchyCode) throws Exception
	{
		IData cond = new DataMap();
		cond.put("USER_ID", userId);
		cond.put("PARAM_CODE", paraCode24);
		if (StringUtils.isNotBlank(paraCode11))
		{
			cond.put("PARA_CODE11", paraCode11);
		}
		cond.put("PARA_CODE12", paraCode12);
		cond.put("EPARCHY_CODE", eparchyCode);

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNTS_BY_STARTDATE", cond);
	}

	/**
	 * @Function: queryUserDiscntsInSelectedElements
	 * @Description: 该函数的功能描述
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-26 下午2:37:20 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-26 updata v1.0.0 修改原因
	 */
	public static IDataset queryUserDiscntsInSelectedElements(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		IDataset dataset = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_DISCNT_IN_SELECTED", param);
		return dataset;
	}

	// 获取宽带续费（铁通）空套餐
	public static IData cttqueryUserDiscntsInSelectedElements(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		IDataset dataset = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_DISCNT_INNULL_SELECTED", param);

		if (dataset.size() > 0)
		{
			dataset.getData(0).put("ELEMENT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(dataset.getData(0).getString("ELEMENT_ID")));
		} else
		{
			return null;
		}

		return dataset.getData(0);
	}

	public static IDataset queryUserDiscntsInSelectedElements(String userId, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		IDataset dataset = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_DISCNT_IN_SELECTED", param, eparchyCode);
		return dataset;
	}

	public static IDataset queryUserDiscntsOfBroadBand(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		IDataset dataset = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_DISCNT_BROADBAND", param);
		return dataset;
	}

	/**
	 * @Function: queryUserEnableGprsDis
	 * @Description: 该函数的功能描述
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-26 下午2:37:31 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-26 updata v1.0.0 修改原因
	 */
	public static boolean queryUserEnableGprsDis(String userId, String eparchy_code) throws Exception
	{

		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("EPARCHY_CODE", eparchy_code);

		IDataset userDiscntCount = Dao.qryByCode("TF_F_USER_DISCNT", "USER_ENABLE_GPRS_DISCNT_SEL", param);

		return userDiscntCount == null || userDiscntCount.isEmpty() ? false : Integer.parseInt(userDiscntCount.getData(0).getString("RECORD_COUNT", "0")) > 0 ? true : false;

	}

	/**
	 * 获取用户是否存在正在兑换优惠
	 * 
	 * @param td
	 * @return
	 * @throws Exception
	 * @author:huangsl
	 * @date:2014-5-22
	 */
	public static IDataset queryUserExistExchangeDiscnt(String userId, String productId, String giftTypeCode) throws Exception
	{

		IData params = new DataMap();
		params.put("USER_ID", userId); // 用户标识
		params.put("USER_ID_A", "-1"); // 具体填值有待确认
		params.put("PACKAGE_ID", "-1"); // 包标识
		params.put("PRODUCT_ID", productId); // 产品标识
		params.put("DISCNT_CODE", giftTypeCode); // 兑换优惠代码

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_DISCNTCODE", params);
	}

	/**
	 * @Function: queryUserGdiscntCount
	 * @Description: 该函数的功能描述
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-26 下午2:38:14 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-26 updata v1.0.0 修改原因
	 */
	public static int queryUserGdiscntCount(String userId, String eparchy_code) throws Exception
	{

		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("EPARCHY_CODE", eparchy_code);

		IDataset userDiscntCount = Dao.qryByCode("TF_F_USER_DISCNT", "USER_GPRS_DISCNT_COUNT_SEL", param);

		return userDiscntCount == null || userDiscntCount.isEmpty() ? 0 : Integer.parseInt(userDiscntCount.getData(0).getString("RECORD_COUNT", "0"));
	}

	public static IDataset queryUserGPRSDiscnt(String userId, String eparchyCode) throws Exception
	{
		IData cond = new DataMap();
		cond.put("USER_ID", userId);
		cond.put("EPARCHY_CODE", eparchyCode);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_GPRSDISCNT", cond);
	}

	/**
	 * @Function: queryUserLastDiscntName
	 * @Description: 该函数的功能描述
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-26 下午2:38:49 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-26 updata v1.0.0 修改原因
	 */
	public static String queryUserLastDiscntName(String userId, String eparchy_code) throws Exception
	{

		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("EPARCHY_CODE", eparchy_code);

		IDataset userDiscntCount = Dao.qryByCode("TF_F_USER_DISCNT", "USER_LAST_GPRS_DISCNT_SEL", param);

		return userDiscntCount == null || userDiscntCount.isEmpty() ? "" : userDiscntCount.getData(0).getString("DISCNT_NAME");

	}

	public static IDataset queryUserMainDiscnt(String userId) throws Exception
	{
		IData cond = new DataMap();
		cond.put("USER_ID", userId);
		IDataset dataset = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_MAIN_DISCNT", cond);
		if (IDataUtil.isNotEmpty(dataset))
		{
			for (int i = 0; i < dataset.size(); i++)
			{
				IData data = dataset.getData(i);
				String discntCode = data.getString("ELEMENT_ID");
				String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(discntCode);
				data.put("ELEMENT_NAME", discntName);
			}
		}
		return dataset;
	}

	/**
	 * 查询动感地带校园音乐套餐用户主体优惠变更范围
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 * @author zhouwu
	 * @date 2014-07-09
	 */
	public static IDataset queryUserMainDiscntScope(String userId) throws Exception
	{
		IData param = new DataMap();

		param.put("USER_ID", userId);

		return Dao.qryByCode("TD_S_COMMPARA", "SEL_USER_MAIN_DISCNT_SCOPE", param);
	}

	public static IDataset queryUserNextMonthValidDiscnt(String userId) throws Exception
	{

		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_NM_ALLVALID", param);
	}

	public static IDataset queryUserNormalDic(String userId, String prouctId) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", userId);
		params.put("PRODUCT_ID", prouctId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_DISCNT_INTF_HAIN", params);
	}

	// 海南第三代订单中心改造
	public static IDataset queryUserNormalDicNow(String userId, String productId, String offerInstId) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", userId);
		IDataset userDiscntInfos = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_DISCNT_INTF_HAIN_NOW", params);
		IDataset result = new DatasetList();
		if (IDataUtil.isNotEmpty(userDiscntInfos))
		{
			int size = userDiscntInfos.size();
			OfferCfg offerCfg = OfferCfg.getInstance(productId, BofConst.ELEMENT_TYPE_CODE_PRODUCT);
			String productName = "";
			String productMode = "";
			if (offerCfg != null)
			{
				productName = offerCfg.getOfferName();
				productMode = offerCfg.getProductMode();
			}
			for (int i = 0; i < size; i++)
			{
				IData userDiscntInfo = userDiscntInfos.getData(i);
				IDataset discntOfferRelInfos = UserOfferRelInfoQry.queryUserOfferRelInfosByRelOfferInstId(userDiscntInfo.getString("INST_ID", ""));
				String groupId = "-1";
				String offerCode = "-1";
				String forceOfferType = BofConst.ELEMENT_TYPE_CODE_PRODUCT;
				if (IDataUtil.isNotEmpty(discntOfferRelInfos))
				{
					if (discntOfferRelInfos.size() > 1)
					{// 优惠有继承的情况 主产品取的是有效的那一条 老系统product_id为新的主产品 所以取开始时间较大那一条
						IDataset maxStartDatas = OfferUtil.findMPMaxStartDate(discntOfferRelInfos);
						for (Object obj : maxStartDatas)
						{
							IData maxStartData = (IData) obj;
							groupId = maxStartData.getString("GROUP_ID", "-1");
							offerCode = maxStartData.getString("OFFER_CODE", "");
						}
					} else
					{
						IData discntOfferRelInfo = discntOfferRelInfos.getData(0);
						if ("P".equals(discntOfferRelInfo.getString("OFFER_TYPE", "")))
						{
							groupId = discntOfferRelInfo.getString("GROUP_ID", "-1");
							offerCode = discntOfferRelInfo.getString("OFFER_CODE", "");
						} else if ("K".equals(discntOfferRelInfo.getString("OFFER_TYPE", "")))
						{
							String offerInsId = discntOfferRelInfo.getString("OFFER_INS_ID");
							IDataset userSaleActiveInfos = UserSaleActiveInfoQry.queryUserSaleActiveByInstId(offerInsId);
							offerCode = userSaleActiveInfos.getData(0).getString("PRODUCT_ID");
							groupId = discntOfferRelInfo.getString("OFFER_CODE", "");
							forceOfferType = discntOfferRelInfo.getString("OFFER_TYPE", "");
						}
					}
				}
				userDiscntInfo.put("PACKAGE_ID", groupId);
				userDiscntInfo.put("PRODUCT_ID", offerCode);
				userDiscntInfo.put("PRODUCT_NAME", productName);
				userDiscntInfo.put("PRODUCT_MODE", productMode);
				userDiscntInfo.put("OFFER_TYPE", forceOfferType);
				String forceProductId = "";
				IData elementInfo = null;
				if ("-1".equals(offerCode))
				{// 如果回填的product_id为-1，则查询主产品ID下元素的FORCE_TAG
					forceProductId = productId;
					elementInfo = ProductElementsCache.getElement(forceProductId, userDiscntInfo.getString("ELEMENT_ID", ""), BofConst.ELEMENT_TYPE_CODE_DISCNT);
				} else
				{
					if (BofConst.ELEMENT_TYPE_CODE_PACKAGE.equals(forceOfferType))
					{// 如果该元素是营销活动产品下的元素，则查询该营销包下的元素的FORCE_TAG
						forceProductId = groupId;
						elementInfo = PackageElementsCache.getElement(forceProductId, userDiscntInfo.getString("ELEMENT_ID", ""), BofConst.ELEMENT_TYPE_CODE_DISCNT);
					} else
					{// 否则查询回填的product_id下元素的FORCE_TAG
						forceProductId = offerCode;
						elementInfo = ProductElementsCache.getElement(forceProductId, userDiscntInfo.getString("ELEMENT_ID", ""), BofConst.ELEMENT_TYPE_CODE_DISCNT);
					}
				}
				if (IDataUtil.isEmpty(elementInfo))
				{
					userDiscntInfo.put("FORCE_TAG", "1");// 没有在产品模型下配置的不让删除
				} else
				{
					userDiscntInfo.put("FORCE_TAG", elementInfo.getString("ELEMENT_FORCE_TAG", ""));
				}
				OfferCfg discntCfg = OfferCfg.getInstance(userDiscntInfo.getString("ELEMENT_ID", ""), BofConst.ELEMENT_TYPE_CODE_DISCNT);
				if (discntCfg == null)
				{
					continue;
				}
				userDiscntInfo.put("ELEMENT_NAME", discntCfg.getOfferName());
				String discntTpyeCode = UDiscntInfoQry.getDiscntTypeByDiscntCode(userDiscntInfo.getString("ELEMENT_ID", ""));
				if (StringUtils.isEmpty(discntTpyeCode) || "".equals(discntTpyeCode) || discntTpyeCode == null)
				{
					discntTpyeCode = "-1";
				}
				userDiscntInfo.put("DISCNT_TYPE_CODE", discntTpyeCode);
				IDataset pricePlanInfos = UpcCall.qryPricePlanInfoByOfferId(userDiscntInfo.getString("ELEMENT_ID", ""), BofConst.ELEMENT_TYPE_CODE_DISCNT);
				String price = "";
				String billflg = "";
				if (IDataUtil.isNotEmpty(pricePlanInfos))
				{
					price = pricePlanInfos.getData(0).getString("FEE", "");
					billflg = pricePlanInfos.getData(0).getString("CYCLE_UNIT", "");
					if ("4".equals(billflg))
					{// 产商品"月"导成"4", 我们这边是"2"
						billflg = "2";
					}
				}
				userDiscntInfo.put("PRICE", price);
				userDiscntInfo.put("BILLFLG", billflg);
				result.add(userDiscntInfo);
			}
		}

		return result;
	}

	/**
	 * @Function: queryUserDiscntByUserId
	 * @Description: 根据USER_ID获取优惠信息
	 * @param: @param userId
	 * @param: @return
	 * @param: @throws Exception
	 * @return：IDataset
	 * @throws：
	 * @version: v1.0.0
	 * @author: Administrator
	 * @date: 3:56:06 PM Jul 25, 2013 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------* Jul 25,
	 *        2013 longtian3 v1.0.0 TODO:
	 */
	public static IDataset queryUserNormalDiscntsByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_NEW", param);
	}
	public static IDataset queryUserNormalDiscntsByUserIdNew1(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT a.PARTITION_ID, to_char(a.USER_ID) USER_ID, to_char(a.USER_ID_A) USER_ID_A, a.DISCNT_CODE, a.SPEC_TAG, a.RELATION_TYPE_CODE, to_char(a.INST_ID) INST_ID, ");
		sql.append("to_char(a.CAMPN_ID) CAMPN_ID, to_char(a.START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, to_char(a.END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
		sql.append("to_char(a.UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, a.UPDATE_STAFF_ID, a.UPDATE_DEPART_ID, a.REMARK, a.RSRV_NUM1, a.RSRV_NUM2, a.RSRV_NUM3, to_char(a.RSRV_NUM4) RSRV_NUM4, ");
		sql.append("to_char(a.RSRV_NUM5) RSRV_NUM5, a.RSRV_STR1, a.RSRV_STR2, a.RSRV_STR3, a.RSRV_STR4, a.RSRV_STR5, to_char(a.RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
		sql.append("to_char(a.RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, to_char(a.RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, a.RSRV_TAG1, a.RSRV_TAG2, a.RSRV_TAG3 ");
		sql.append("FROM tf_f_user_discnt a ");
		sql.append("left join tf_f_user_offer_rel b on a.inst_id = b.rel_offer_ins_id and b.end_date>sysdate ");
		sql.append("left join ucr_crm1.tf_f_user_product c on a.user_id = c.user_id and b.offer_code = c.product_id and c.product_mode='00' ");
		sql.append("WHERE a.user_id=TO_NUMBER(:USER_ID) ");
		sql.append("AND a.partition_id=MOD(TO_NUMBER(:USER_ID),10000)");
		sql.append(" AND Sysdate Between a.Start_Date And a.End_Date  ");
		sql.append("order by c.product_mode,b.group_id,a.discnt_code,a.start_date ");
		return Dao.qryBySql(sql, param);
	}
	/**
	 * todo code_code 表里没有SEL_USERDISCNT_BY_DISCNTCODE
	 * 根据营销活动办理进来的DISCNT_CODE查询用户是否存在跟绑定的优惠重复的优惠
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserServiceByDiscntCode(IData param) throws Exception
	{

		IDataset userDiscntInfo = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USERDISCNT_BY_DISCNTCODE", param);
		return userDiscntInfo;
	}

	public static IDataset queryUserSpecDic(String userId, String prouctId) throws Exception
	{

		IDataset userDics = new DatasetList();

		IData params = new DataMap();
		params.put("USER_ID", userId);
		params.put("PRODUCT_ID", prouctId);

		IDataset tempUserDics = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_SPEC_DISCNT_INTF_HAIN", params);

		for (int i = 0; i < tempUserDics.size(); i++)
		{
			IData dic = tempUserDics.getData(i);
			String elementId = dic.getString("ELEMENT_ID");
			IDataset elemConfigs = PkgElemInfoQry.getProductElementConfig(prouctId, elementId);

			if (IDataUtil.isEmpty(elemConfigs))
			{
				dic.put("FORCE_TAG", "1");// 没有在产品模型下配置的不让删除
			} else
			{
				String force_tag = elemConfigs.getData(0).getString("FORCE_TAG");
				dic.put("FORCE_TAG", force_tag);
			}
			userDics.add(dic);
		}
		return userDics;
	}

	public static IDataset queryUserTradeDiscntsOfBroadBand(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		IDataset dataset = Dao.qryByCode("TF_B_TRADE_DISCNT", "SEL_USER_DISCNT_BROADBAND_UNEFFECT", param);
		return dataset;
	}

	/**
	 * @Function: queryUserWlanDiscnt
	 * @Description: 该函数的功能描述
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-26 下午2:40:01 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-26 updata v1.0.0 修改原因
	 */
	public static IDataset queryUserWlanDiscnt(String userId, String eparchy_code) throws Exception
	{

		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PARAM_ATTR", "288");
		param.put("EPARCHY_CODE", eparchy_code);

		return Dao.qryByCode("TF_F_USER_DISCNT", "USER_WLAN_DISCNT_SEL", param);
	}

	/**
	 * 查成员乡情网资费
	 * 
	 * @param userId
	 * @param discntCode
	 * @return
	 * @throws Exception
	 */

	public static IDataset queryXQWDiscntInfoByUserIdAndDisCode(String userId, String discntCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("DISCNT_CODE", discntCode);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_XQWDISCNTINFO_BYUSERID", param);

	}

	public static IDataset quyUserDiscntByUserIdAndInstId(String userId, String instId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("INST_ID", instId);

		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_INSTID", param);

		FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userDiscnts, param, null);

		return userDiscnts;
	}

	public static IDataset salePrdCheck(String saleProductId, String userId, String eparchyCode) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("PRODUCT_ID", saleProductId);
		inparam.put("USER_ID", userId);
		inparam.put("EPARCHY_CODE", eparchyCode);
		IDataset discnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_SALEPROD_CHECK", inparam);

		return discnts;
	}
 
	public static IDataset getJkdtUserDis(IData map) throws Exception
	{
		IDataset discnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USER_ID", map);
		return discnts;
		
	}
	/**
	 * todo code_code 表里没有SEL_GPRSDISCNT_BY_USERID 根据USER_ID查询用户是否存在GPRS优惠
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 *             zhangchuan
	 */
	public IDataset getGprsDiscntByuser(IData data) throws Exception
	{

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_GPRSDISCNT_BY_USERID", data);
	}

	/**
	 * 获取用户为生效的VPMN358优惠
	 * 
	 * @param userId
	 * @param user_id_a
	 * @param relationTypeCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserNextEffectVpmn358Discnt(String userId, String user_id_a, String relationTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("USER_ID_A", user_id_a);
		param.put("RELATION_TYPE_CODE", relationTypeCode);

		SQLParser distparser = new SQLParser(param);

		distparser.addSQL(" SELECT * FROM TF_F_USER_DISCNT A                          ");
		distparser.addSQL("  WHERE 1=1                                                ");
		distparser.addSQL("    AND USER_ID = TO_NUMBER(:USER_ID)                      ");
		distparser.addSQL("    AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)     ");
		distparser.addSQL("    AND SPEC_TAG = '2'                                     ");
		distparser.addSQL("    AND END_DATE > SYSDATE                                 ");
		distparser.addSQL("    AND START_DATE > SYSDATE                               ");
		distparser.addSQL("    AND END_DATE > START_DATE                              ");
		distparser.addSQL("    AND RELATION_TYPE_CODE = TO_CHAR(:RELATION_TYPE_CODE)  ");
		distparser.addSQL("    AND A.DISCNT_CODE IN ('1285', '1286', '1391')          ");

		return Dao.qryByParse(distparser);
	}

	/**
	 * 互联网电视查询优惠使用
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getHLWDDiscntByUserId(String userId) throws Exception
	{

		IData inparams = new DataMap();

		IDataset platSvcInfos = UpcCall.querySpServiceBySpCodeAndBizTypeCodeAndOrgDomain("_51", "51", "CIBP");

		IDataset userDiscnts = new DatasetList();

		if (IDataUtil.isNotEmpty(platSvcInfos))
		{
			String spellParam = "";

			inparams.put("USER_ID", userId);

			for (int i = 0; i < platSvcInfos.size(); i++)
			{

				spellParam += ",:PLAT_SVC_ID" + i;
				inparams.put("PLAT_SVC_ID" + i, platSvcInfos.getData(i).getString("OFFER_ID"));
			}

			String reGex = ":PLAT_SVC_IDS";
			String replaceMent = spellParam.substring(1);

			String sqlStmt = InterforResalQry.getSqlByCode("TF_F_USER_DISCNT", "SEL_DISCNT_HLWD");
			StringBuilder sqlStmtNew = new StringBuilder(sqlStmt.replaceAll(reGex, replaceMent));

			userDiscnts = Dao.qryBySql(sqlStmtNew, inparams);
		}

		return userDiscnts;
	}

	/**
	 * @param userId
	 * @param timePoint
	 * @return
	 * @throws Exception
	 */
	public static IDataset getDiscntByUidDate(String userId, String timePoint) throws Exception
	{

		IData inparams = new DataMap();
		inparams.put("USER_ID", userId);
		inparams.put("TIME_POINT", timePoint);
		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_UID_DATE", inparams);
		return userDiscnts;
	}

	/**
	 * 大客户生日畅打
	 * 
	 * @param
	 * @return
	 * @throws Exception
	 */
	public static IDataset getVipBirthDiscnt(IData data) throws Exception
	{
		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_VIPBIRTH_DISCNT", data);

		return userDiscnts;
	}

	/**
	 * 根据判断当前时间所在自然年内是否存在XX优惠
	 * 
	 * @param userId
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserDiscntByYear(String userId, String discntId) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", userId);
		params.put("DISCNT_CODE", discntId);

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNT_BY_YEAR", params);
	}

	public static IDataset queryUserProductDiscntByProdIdAndUserId(String userId, String productId) throws Exception
	{
		IData param = new DataMap();
		param.put("PRODUCT_ID", productId);
		param.put("USER_ID", userId);
		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_NOW", param);

		if (IDataUtil.isNotEmpty(userDiscnts))
		{
			FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userDiscnts, null, null);
			userDiscnts = FillUserElementInfoUtil.filterUserElementsByProductIdPackageId(userDiscnts,productId,null);
			if (IDataUtil.isNotEmpty(userDiscnts))
			{
				for (int i = 0; i < userDiscnts.size(); i++)
				{
					IData data = userDiscnts.getData(i);
					String discntCode = data.getString("DISCNT_CODE");
					String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(discntCode);
					data.put("DISCNT_NAME", discntName);
				}
			}
		}
		return userDiscnts;
		//Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_PRODUCT_DISCNT", param);
	}

	public static IDataset queryUserValidDiscntByPackageId(String userId, String packageId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PACKAGE_ID", packageId);

		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "QUERY_PACKAGE_DISCNT_VALID", param);

		FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userDiscnts, param, null);

		userDiscnts = FillUserElementInfoUtil.filterUserElementsByProductIdPackageId(userDiscnts, null, packageId);

		return userDiscnts;
	}

	public static IDataset getUserDisntsBylimitNpWithCreditClass(String userId, String eparchyCode, String starLevel) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", userId); // 用户标识
		params.put("EPARCHY_CODE", eparchyCode);
		params.put("STAR_LEVEL", starLevel);
		IDataset ds = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_DISCNT_LIMIT_NP_STAR_LEVEL", params);
		if (IDataUtil.isNotEmpty(ds))
		{
			for (int i = 0; i < ds.size(); i++)
			{
				IData data = ds.getData(i);
				IData returnName = UpcCall.queryOfferByOfferId("D", data.getString("DISCNT_CODE"));
				if (IDataUtil.isNotEmpty(returnName))
				{
					data.put("DISCNT_NAME", returnName.getString("OFFER_NAME", ""));
				} else
				{
					data.put("DISCNT_NAME", "");
				}
			}

		}

		return ds;
		// return Dao.qryByCode("TF_F_USER_DISCNT",
		// "SEL_USER_DISCNT_LIMIT_NP_STAR_LEVEL", params);
	}

	public static IDataset queryInvalidUserDiscntInPackage(String userId, String pakcageId, String elementTypeCode) throws Exception
	{

		IData params = new DataMap();
		params.put("USER_ID", userId); // 用户标识
		params.put("PACKAGE_ID", pakcageId);
		params.put("ELEMENT_TYPE_CODE", elementTypeCode);

		return Dao.qryByCode("TF_F_USER_DISCNT", "QRY_INVALID_ELEMENT_IN_PACKAGE_USER", params);

	}

	/**
	 * 查询集团成员是否有满足的折扣套餐
	 * 
	 * @param meb_user_id
	 * @param discnt_code
	 * @return
	 * @throws Exception
	 */
	public static int getDiscntByMUIdToCommpara(String meb_user_id, String discnt_code, String grp_user_id) throws Exception
	{

		IData inparams = new DataMap();
		inparams.put("MEB_USER_ID", meb_user_id);
		inparams.put("GRP_USER_ID", grp_user_id);
		inparams.put("DISCNT_CODE", discnt_code);
		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNT_BY_COMMPARA", inparams);

		return userDiscnts == null || userDiscnts.isEmpty() ? 0 : Integer.parseInt(userDiscnts.getData(0).getString("RECORD_COUNT", "0"));

	}

	public static IDataset queryUserDiscntToCommparaByUID(String user_id) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		return Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_DISCNT_BY_COMMPARASVC", param);
	}

	/**
	 * @author yanwu
	 * @param user_id
	 * @param discntId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserDiscntV(String user_id, String discntId) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", user_id);
		params.put("DISCNT_CODE", discntId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USER_DISCNT_V", params);
	}

	/**
	 * 查询是否含有未生效的优惠
	 * 
	 * @param user_id
	 * @param discntId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getFutureUserDiscnt(String user_id, String discntId) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", user_id);
		params.put("DISCNT_CODE", discntId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_FUTURE_DISCNT", params);
	}

	public static IDataset getFXDiscntByUserId(String userId) throws Exception
	{

		IData inparams = new DataMap();
		inparams.put("USER_ID", userId);
		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNT_FX", inparams);

		return userDiscnts;
	}

	public static IDataset getFXDiscntByUserIdA(String userId) throws Exception
	{
		IData inparams = new DataMap();
		inparams.put("USER_ID", userId);
		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNT_FXA", inparams);
		return userDiscnts;
	}

	/**
	 * 获取VPMN用户的discntCode
	 * 
	 * @param userId
	 * @param discntCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryVpmnUserDiscntByUserId(String userId, String discntCode) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", userId); // 用户标识
		params.put("DISCNT_CODE", discntCode);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_VPMN_DISCNT_BY_USER_ID", params);
	}

	/**
	 * 统计用户当月含有几个日流量包优惠
	 * 
	 * @param user_id
	 * @author songlm
	 * @throws Exception
	 */
	public static String getUserDiscntMonthCount(String user_id) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", user_id);
		IDataset results = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_DISCNT_MONTH_COUNT", params);
		return results == null || results.isEmpty() ? "0" : results.getData(0).getString("COUNT_NUM", "0");
	}

	/**
	 * 新增2款实体流量卡套餐
	 * 
	 * @param
	 * @author
	 * @throws
	 */
	public static String getUserDiscntMonthCountNew(String userId, String discntCode) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", userId);
		params.put("DISCNT_CODE", discntCode);
		IDataset results = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_UDISCNT_MONTH_CNT", params);
		return results == null || results.isEmpty() ? "0" : results.getData(0).getString("COUNT_NUM", "0");
	}

	/**
	 * 更新 新资费开始时间，这次增加的目的是用户宽带产品变更后更新营销活动的受理预约日期
	 * 
	 * @author kangyt
	 * @param trade_id
	 * @param start_date
	 * @throws Exception
	 */
	public static void updStartDateByUseridInstid(String user_id, String inst_id, String discode, String start_date) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("INST_ID", inst_id);
		param.put("DISCNT_CODE", discode);
		param.put("START_DATE", start_date);
		Dao.executeUpdateByCodeCode("TF_F_USER_DISCNT", "UPD_STARTDATE_BY_USERID_INSTID", param);
	}

	/**
	 * 更新旧资费结束时间，这次增加的目的是用户宽带产品变更后更新营销活动的终止日期，更加预约日期计算
	 * 
	 * @author kangyt
	 * @param trade_id
	 * @param start_date
	 * @throws Exception
	 */
	public static void updEndDateByUseridInstid(String user_id, String inst_id, String discode, String end_date) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("INST_ID", inst_id);
		param.put("DISCNT_CODE", discode);
		param.put("END_DATE", end_date);
		Dao.executeUpdateByCodeCode("TF_F_USER_DISCNT", "UPD_ENDDATE_BY_USERID_INSTID", param);
	}

	/**
	 * 查询用户订购的所有优惠
	 * 
	 * @param inparams
	 * @return
	 * @throws Exception
	 */
	public static IDataset getAllDiscntInfo(String userId) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", userId);

		SQLParser parser = new SQLParser(params);

		parser.addSQL(" SELECT * ");
		parser.addSQL(" FROM TF_F_USER_DISCNT ");
		parser.addSQL(" where USER_ID = TO_NUMBER(:USER_ID) ");
		parser.addSQL(" AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
		parser.addSQL(" AND end_date > sysdate ");

		IDataset dataset = Dao.qryByParse(parser);

		if (IDataUtil.isNotEmpty(dataset))
		{
			// 填充PRODUCT_ID、PACKAGE_ID
			FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(dataset, null, null);// 填充productId和packageId
		}

		return dataset;
	}
	/**
	 * 查询用户订购的所有优惠
	 * 
	 * @param inparams
	 * @return
	 * @throws Exception
	 */
	public static IDataset getAllDiscntInfo1(String userId) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", userId);

		SQLParser parser = new SQLParser(params);

		parser.addSQL(" SELECT * ");
		parser.addSQL(" FROM TF_F_USER_DISCNT ");
		parser.addSQL(" where USER_ID = TO_NUMBER(:USER_ID) ");
		parser.addSQL(" AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
		parser.addSQL(" AND end_date > sysdate ");

		IDataset dataset = Dao.qryByParse(parser);
		return dataset;
	}


	/**
	 * 查询用户已开优惠信息
	 * 
	 * @param user_id
	 * @param user_id_a
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserNormalDiscntsByUserIdNew(String user_id) throws Exception
	{

		IData iData = new DataMap();
		iData.put("USER_ID", user_id);

		SQLParser dctparser = new SQLParser(iData);

		dctparser.addSQL(" SELECT PARTITION_ID,to_char(USER_ID) USER_ID,DISCNT_CODE,to_char(INST_ID) INST_ID,to_char(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,to_char(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE ");
		dctparser.addSQL("   FROM tf_f_user_discnt ");
		dctparser.addSQL("  WHERE user_id = TO_NUMBER(:USER_ID) ");
		dctparser.addSQL("    AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000) ");
		dctparser.addSQL("    AND Sysdate Between Start_Date And trunc(last_day(END_DATE)+1)- 1/24/60/60 ");
		dctparser.addSQL("  ORDER BY Start_Date ");
		IDataset resultset = Dao.qryByParse(dctparser);
		return resultset;
	}

	public static IDataset queryDiscntByUserIdVpmnActive(IData param) throws Exception
	{
		SQLParser dctparser = new SQLParser(param);

		dctparser.addSQL(" SELECT U.PARTITION_ID PARTITION_ID, TO_CHAR(U.USER_ID) USER_ID, TO_CHAR(U.USER_ID_A) USER_ID_A, U.DISCNT_CODE DISCNT_CODE, U.SPEC_TAG SPEC_TAG, U.RELATION_TYPE_CODE RELATION_TYPE_CODE, TO_CHAR(U.INST_ID) INST_ID, TO_CHAR(U.CAMPN_ID) CAMPN_ID, TO_CHAR(U.START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE, TO_CHAR(U.END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE,TO_CHAR(U.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,U.UPDATE_STAFF_ID,U.UPDATE_DEPART_ID,U.REMARK,U.RSRV_NUM1,U.RSRV_NUM2,U.RSRV_NUM3,TO_CHAR(U.RSRV_NUM4),TO_CHAR(U.RSRV_NUM5),U.RSRV_STR1,U.RSRV_STR2,U.RSRV_STR3,U.RSRV_STR4,U.RSRV_STR5,TO_CHAR(U.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,TO_CHAR(U.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,TO_CHAR(U.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,U.RSRV_TAG1,U.RSRV_TAG2,U.RSRV_TAG3,C.PARAM_CODE PARAM_CODE,C.PARAM_NAME PARAM_NAME ");
		dctparser.addSQL(" FROM TF_F_USER_DISCNT U, TD_S_COMMPARA C ");
		dctparser.addSQL(" WHERE 1=1 ");
		dctparser.addSQL(" and U.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)");
		dctparser.addSQL(" AND U.USER_ID = TO_NUMBER(:USER_ID) ");
		dctparser.addSQL(" AND U.USER_ID_A = TO_NUMBER(:USER_ID_A) ");
		// dctparser.addSQL(" AND U.PRODUCT_ID = TO_NUMBER(:PRODUCT_ID) ");
		// dctparser.addSQL(" AND U.PACKAGE_ID = TO_NUMBER(:PACKAGE_ID) ");
		dctparser.addSQL(" AND SYSDATE BETWEEN U.START_DATE AND U.END_DATE ");
		dctparser.addSQL(" AND U.DISCNT_CODE = TO_NUMBER(C.PARAM_CODE) ");
		dctparser.addSQL(" AND C.SUBSYS_CODE = :SUBSYS_CODE ");
		dctparser.addSQL(" AND C.PARAM_ATTR = :PARAM_ATTR  ");
		dctparser.addSQL(" AND C.PARAM_CODE = :PARAM_CODE ");
		dctparser.addSQL(" AND (C.EPARCHY_CODE = :EPARCHY_CODE  OR C.EPARCHY_CODE = 'ZZZZ') ");
		IDataset resultset = Dao.qryByParse(dctparser);

		FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(resultset, param, null);
		resultset = FillUserElementInfoUtil.filterUserElementsByProductIdPackageId(resultset, param.getString("PRODUCT_ID"), param.getString("PACKAGE_ID"));

		return resultset;
	}

	/**
	 * 国际流量统付首次激活修改截止时间
	 * 
	 * @param endDate
	 * @param userId
	 * @param productId
	 * @param discntCode
	 * @param routeId
	 * @return
	 * @throws Exception
	 */
	public static int updateEndDateByUserIdProductIdDiscntCode(String endDate, String userId, String discntCode, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("END_DATE", endDate);
		param.put("USER_ID", userId);
		param.put("DISCNT_CODE", discntCode);
		return Dao.executeUpdateByCodeCode("TF_F_USER_DISCNT", "UPD_ENDDATE_BY_USERID_PRODUCTID_DISCNTCODE", param, routeId);
	}

	/**
	 * @param userId
	 * @param discntCode
	 * @return
	 * @throws Exception
	 * @author wukw3
	 */
	public static IDataset getOldDiscntByUser(String userId) throws Exception
	{
		IData cond = new DataMap();
		cond.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_OLDDISCNT_BY_USER", cond);
	}

	/**
	 * modify by duhj
	 * 
	 * @param data
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserDiscntSByUserId(IData data, Pagination pagination) throws Exception
	{
		String user_id = data.getString("USER_ID", "");
		String partition_id = data.getString("PARTITION_ID", "");
		if ("".equals(user_id) || "".equals(partition_id))
		{
			return new DatasetList();
		}

		IDataset result = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_DISCNTS_BY_USERID", data, pagination);
		return result;

	}

	/**
	 * 根据tradeId userid 查询所有的用户优惠
	 * 
	 * @author fangwz
	 * @param tradeId
	 *            userid
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserDiscntByUseridAndTag(String userid, String param, String epachycode) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", userid);
		params.put("PARAM_CODE", param);
		params.put("EPARCHY_CODE", epachycode);
		return Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_DISCNT_BY_USERID_TAG", params);
	}

	/**
	 * 查询物联网用户的优惠
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryWlwUserDiscntInfo(String userId) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_WLW_USER_DISCNT_BYUSERID", params);
	}

	/**
	 * 查询物联网成员的优惠-按时间排序取
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryWlwOneMebDiscntInfo(String userId) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID_A", userId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_WLW_ONEMEBDISCNT_BYUSERID", params);
	}

	/**
	 * 查询物联网成员的优惠
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryWlwMebDiscntInfo(String userId) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID_A", userId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_WLW_MEBDISCNT_BYUSERIDA", params);
	}

	/**
	 * 查询物联网成员的是否都订购了流量套餐
	 * 
	 * @param userId
	 *            不要了
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryWlwMebDiscntCount(String userId) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID_A", userId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_WLW_MEB_COUNT_BYUSERIDA", params);
	}

	/**
	 * 查询集团物联网的未订购流量套餐的成员
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryWlwMebNotExistDiscnt(String userId) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID_A", userId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_WLW_CKECK_MEB_BYUSERIDA", params);
	}

	/**
	 * 查询集团物联网成员订购的流量套餐是否一致
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryWlwMebNumDiscnt(String userId) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID_A", userId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_WLWMEB_NUM_BYUSERIDA", params);
	}

	/**
     * 根据生效时间范围查询用户正常资费
     * @author chenmw3
     * @date 2017-06-09
     * @param userId
     * @param qryStartDate
     * @param qryEndDate
     * @return
     * @throws Exception
     */
    public static IDataset getUserNormalDiscntByUserIdDateScope(String userId, String qryStartDate, String qryEndDate) throws Exception{
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("QRY_START_DATE", qryStartDate);
        param.put("QRY_END_DATE", qryEndDate);
        return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USERDISCNT_NORMAL_DATESCOPE", param);
    }
	public static  IDataset getAllDiscntByUD(String userId, String discntCode) throws Exception
	{

		IData inparams = new DataMap();
		inparams.put("USER_ID", userId);
		inparams.put("DISCNT_CODE", discntCode);
		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_ALLDISCNT_BY_UD", inparams);

		FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userDiscnts, inparams, null);

		return userDiscnts;
	}
	
	public static IDataset queryUserAllDiscntsByUserIdPCC(IData data) throws Exception
	{
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_ALL_BY_USERIDPCC", data);
	}
	
	public static IDataset queryUserAllDiscntsByUserIdShareMeal(IData data) throws Exception
	{
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_ALL_BY_USERIDSHAREMEAL", data);
	}
	public static IDataset queryUserAllDiscntsByUserIdShareMealT(IData data) throws Exception
	{
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_ALL_BY_USERIDSHAREMEALT", data);
	}
	

		public static  IDataset getAllDiscntsByUD(String userId, String discntCode) throws Exception
	{

		IData iData = new DataMap();
		iData.put("USER_ID", userId);
		//iData.put("DISCNT_CODE", discntCode);
		if("84008840".equals(discntCode) || "84009642".equals(discntCode))
		{
			iData.put("DISCNT_CODE1", "84008840");
			iData.put("DISCNT_CODE2", "84009642");

		}
		if("84008841".equals(discntCode) || "84009643".equals(discntCode))
		{
			iData.put("DISCNT_CODE1", "84008841");
			iData.put("DISCNT_CODE2", "84009643");
		}
		if("84008842".equals(discntCode) || "84009644".equals(discntCode))
		{
			iData.put("DISCNT_CODE1", "84008842");
			iData.put("DISCNT_CODE2", "84009644");
		}
		if("84008843".equals(discntCode) || "84009645".equals(discntCode))
		{
			iData.put("DISCNT_CODE1", "84008843");
			iData.put("DISCNT_CODE2", "84009645");
		}
		SQLParser dctparser = new SQLParser(iData);

		dctparser.addSQL(" SELECT PARTITION_ID,to_char(USER_ID) USER_ID,DISCNT_CODE,to_char(INST_ID) INST_ID,to_char(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,to_char(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE ");
		dctparser.addSQL("   FROM tf_f_user_discnt ");
		dctparser.addSQL("  WHERE user_id = TO_NUMBER(:USER_ID) ");
		dctparser.addSQL("    AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000) ");
		dctparser.addSQL("    AND (DISCNT_CODE = :DISCNT_CODE1 or DISCNT_CODE = :DISCNT_CODE2  )");
		dctparser.addSQL("  ORDER BY Start_Date ");
		IDataset resultset = Dao.qryByParse(dctparser);
		return resultset;
	}
	
	public static  IDataset getBindPlatSvcByUD(String userId,String tradeTypeCode) throws Exception
	{

		IData iData = new DataMap();
		iData.put("USER_ID", userId);
		iData.put("TRADE_TYPE_CODE", tradeTypeCode);
		SQLParser dctparser = new SQLParser(iData);

		dctparser.addSQL(" select t.para_code1 service_id,s.start_date,s.end_Date ");
		dctparser.addSQL("    from TD_S_COMMPARA t, tf_f_user_discnt s ");
		dctparser.addSQL("  where t.param_attr = '909' ");
		dctparser.addSQL("     and t.param_code = :TRADE_TYPE_CODE ");
		dctparser.addSQL("   and s.user_id = :USER_ID ");
		dctparser.addSQL("   and s.discnt_code = t.para_code2 ");
		dctparser.addSQL("    and sysdate between s.start_date and s.end_date ");
		dctparser.addSQL("  and sysdate between t.start_date and t.end_date ");
		dctparser.addSQL("  and t.para_code8 = 'FUBAO' ");
		IDataset resultset = Dao.qryByParse(dctparser);
		return resultset;
	}
	public static  IDataset getFubaoDiscntByUSD(String userId,String tradeTypeCode,String serviceId) throws Exception
	{

		IData iData = new DataMap();
		iData.put("USER_ID", userId);
		iData.put("TRADE_TYPE_CODE", tradeTypeCode);
		iData.put("SERVICE_ID", serviceId);
		SQLParser dctparser = new SQLParser(iData);

		dctparser.addSQL(" select s.discnt_code,s.start_date,s.end_Date ");
		dctparser.addSQL("    from TD_S_COMMPARA t, tf_f_user_discnt s ");
		dctparser.addSQL("  where t.param_attr = '909' ");
		dctparser.addSQL("     and t.param_code = :TRADE_TYPE_CODE ");
		dctparser.addSQL("   and s.user_id = :USER_ID ");
		dctparser.addSQL("   and t.para_code1 = :SERVICE_ID ");
		dctparser.addSQL("   and s.discnt_code = t.para_code2 ");
		//dctparser.addSQL("    and s.end_date > sysdate ");
		dctparser.addSQL("  and sysdate between t.start_date and t.end_date ");
		dctparser.addSQL("  and t.para_code8 = 'FUBAO' ");
		IDataset resultset = Dao.qryByParse(dctparser);
		return resultset;
	}
	
	public static IDataset getUserIMSDiscnt(String user_id, String param_attr,String eparchy_code) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", user_id);
		params.put("PARAM_ATTR", param_attr);
		params.put("EPARCHY_CODE", eparchy_code);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USER_COMMPARA_CODE", params);
	}
	
	/**
	 * 查询多媒体桌面电话产品用户是否办理了“自定义费用套餐[800109]”
	 * @param userId
	 * @return
	 * @throws Exception
	 * @author chenzg
	 * @date 2018-6-1
	 */
	public static IDataset queryDeskTopUserDiscnt(String userId) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID", userId);
		SQLParser parser = new SQLParser(inparam);
		parser.addSQL("SELECT A.*");
		parser.addSQL("  FROM TF_F_USER_DISCNT A");
		parser.addSQL(" WHERE A.PARTITION_ID = MOD(:USER_ID, 10000)");
		parser.addSQL("   AND A.USER_ID = :USER_ID");
		parser.addSQL("   AND A.DISCNT_CODE IN (800109)");
		parser.addSQL("   AND A.END_DATE > SYSDATE");
		IDataset ds = Dao.qryByParse(parser);
		return ds;
	}
	/**
	 * 查询用户的尊享语音包优惠
	 * @param userId
	 * @return
	 * @throws Exception
	 * @author chenzg
	 * @date 2018-6-1
	 */
	public static IDataset queryUserZunXDiscnt(String userId) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID", userId);
		SQLParser parser = new SQLParser(inparam);
		parser.addSQL("SELECT A.*");
		parser.addSQL("  FROM TF_F_USER_DISCNT A");
		parser.addSQL(" WHERE A.PARTITION_ID = MOD(:USER_ID, 10000)");
		parser.addSQL("   AND A.USER_ID = :USER_ID");
		parser.addSQL("   AND A.DISCNT_CODE IN (84011045,84011046,84011047,84011048)");	//暂时先写死啦
		parser.addSQL("   AND A.END_DATE > SYSDATE");
		IDataset ds = Dao.qryByParse(parser);
		return ds;
	}
	/**
	 * 查询用户是否办理了1000元海洋通基础套餐
	 * @param userId
	 * @return
	 * @throws Exception
	 * @author chenzg
	 * @date 2018-6-8
	 */
	public static IDataset queryUserHytDisncts(String userId) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID", userId);
		SQLParser parser = new SQLParser(inparam);
		parser.addSQL("SELECT A.*");
		parser.addSQL("  FROM TF_F_USER_DISCNT A");
		parser.addSQL(" WHERE A.PARTITION_ID = MOD(:USER_ID, 10000)");
		parser.addSQL("   AND A.USER_ID = :USER_ID");
		parser.addSQL("   AND A.DISCNT_CODE IN (20180639)");	//1000元海洋通基础套餐 暂时先写死啦
		parser.addSQL("   AND A.END_DATE > SYSDATE");
		IDataset ds = Dao.qryByParse(parser);
		return ds;
	}
	public static IDataset getUserMobileDiscnt(String userId) throws Exception {

		IData data = new DataMap();
		data.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_MOBILE_DISCNT_NEW", data);
	}
	public static IDataset getUserDisntsBylimitNp1910(String userId, String eparchyCode) throws Exception {
		IData params = new DataMap();
		params.put("USER_ID", userId); // 用户标识
		params.put("EPARCHY_CODE", eparchyCode); // 兑换优惠代码

		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_DISCNT_LIMIT_NP1910", params);
	}

	public static IDataset getUserCOMDiscnt(String user_id, String param_attr,String param_code, String start_date, String eparchy_code) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", user_id);
		params.put("PARAM_ATTR", param_attr);
		params.put("PARAM_CODE", param_code);
		params.put("START_DATE", start_date);
		params.put("EPARCHY_CODE", eparchy_code);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USER_COMMPARACODE", params);
	}
	public static IDataset getAllDiscntUID(String userId) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", userId);

		SQLParser parser = new SQLParser(params);

		parser.addSQL(" SELECT * ");
		parser.addSQL(" FROM TF_F_USER_DISCNT ");
		parser.addSQL(" where USER_ID = TO_NUMBER(:USER_ID) ");
		parser.addSQL(" AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
		parser.addSQL(" AND discnt_code in  ('84014240','84014241','84014242') ");

		IDataset dataset = Dao.qryByParse(parser);

		if (IDataUtil.isNotEmpty(dataset))
		{
			// 填充PRODUCT_ID、PACKAGE_ID
			FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(dataset, null, null);// 填充productId和packageId
		}

		return dataset;
	}
	
	/**
     * 
     * @param userId
     * @param userIdA
     * @param discntCode
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getUserDiscntByUserIdBAndA(String userId, String userIdA, 
    		String discntCode, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("USER_ID_A", userIdA);
		param.put("DISCNT_CODE", discntCode);
		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNT_INFO_USERID_A", param, eparchyCode);
		
		FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(userDiscnts, param, null);
		
		return userDiscnts;
	}
    
    /**
     * 获取物联网的NB测试套餐
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset getUserNBTestDiscntByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNT_NBTEST_BYUSERID", param);
		return userDiscnts;
	}
    
    /**
     * 获取物联网的NB年包套餐
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset getUserNBPckPDiscntByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_DISCNT_NBPACKAGE_BYUSERID", param);
		return userDiscnts;
	}
    
	public static  IDataset getAllDiscntByTag3(String userId, String discntCode) throws Exception
	{

		IData params = new DataMap();
		params.put("USER_ID", userId);
		params.put("DISCNT_CODE", discntCode);

		SQLParser parser = new SQLParser(params);

		parser.addSQL(" SELECT * ");
		parser.addSQL(" FROM TF_F_USER_DISCNT ");
		parser.addSQL(" where USER_ID = TO_NUMBER(:USER_ID) ");
		parser.addSQL(" AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
		parser.addSQL(" AND discnt_code = :DISCNT_CODE ");
		parser.addSQL(" AND (rsrv_tag3 <> '1' or rsrv_tag3 is null ) ");
		parser.addSQL(" AND END_DATE > SYSDATE");

		IDataset dataset = Dao.qryByParse(parser);

		FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(dataset, null, null);

		return dataset;
	}
	
	/**
     * 
     * @param userId
     * @param paraCode1
     * @return
     * @throws Exception
     */
    public static IDataset getUser20GDiscntByUserId(String userId,String paraCode1) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PARA_CODE1", paraCode1);
		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER20GDISCNT_BY_USERID", param);
		return userDiscnts;
	}
    public static IDataset getDiscntByEndDate(IData param) throws Exception
    {
        IDataset dataset = Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_BY_DISCNT_ENDDATE", param);
        return dataset;
    }
    
    public static IDataset getInstIdByUserId(IData param) throws Exception
	{
		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_INSTID_BY_USERID", param);
		return userDiscnts;
	}
	
	public static IDataset queryUserAllDisnctByCode(String userId, String discntCode) throws Exception {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("DISCNT_CODE", discntCode);
        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT * FROM TF_F_USER_DISCNT  ");
        parser.addSQL(" where USER_ID = TO_NUMBER(:USER_ID) ");
        parser.addSQL(" AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        parser.addSQL(" AND discnt_code = :DISCNT_CODE ");
        parser.addSQL(" ORDER BY END_DATE DESC ");

        IDataset dataset = Dao.qryByParse(parser, Route.CONN_CRM_CG);
        
        return dataset;
    }
		/**REQ201910140021_关于和教育互动业务成员批量变更归属学校的需求
	 * add by sundz
	 * @param userId
	 * @param userIdA
	 * @return
	 * @throws Exception
	 */
	public static IDataset getDiscntByUserIdAndUserId(String userId,String userIdA) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("USER_ID_A", userIdA);
		IDataset dataset = Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_ALL_BY_USERIDA_AND_USERID", param);
		return dataset;
	}

	/**
	 * 取用户有效的优惠，包括未生效的。
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserValidDiscnt(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		IDataset userDiscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_VALID", param);

		return userDiscnts;
	}

}
