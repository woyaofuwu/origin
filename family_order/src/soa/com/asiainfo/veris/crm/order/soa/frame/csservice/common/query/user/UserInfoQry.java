package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.Encryptor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UUserTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.FillUserElementInfoUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupProductUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.PasswdAssistant;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.PasswdMgr;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

public class UserInfoQry
{

	/**
	 * 判读号码装态 USER_RESULT_CODE 0 有效成员用户 RESULT_CODE_DETAIL 0 本地市移动号码
	 * RESULT_CODE_DETAIL 1 非本地市的移动号码 2 非移动号码 USER_RESULT_CODE 1 无效用户号码
	 * RESULT_CODE_DETAIL 0 本省移动号段内号码 1 本省非移动号段内号码 2 其它 USER_RESULT_CODE 2
	 * 有效集团用户
	 * 
	 * @param serialNumber
	 * @return
	 * @throws Exception
	 */
	public static IData checkMebUserInfoBySn(String serialNumber) throws Exception
	{
		String userReusltCode = "";
		String reusltCodeDetail = "";
		IData userInfo = null;
		IData result = new DataMap();
		boolean ifLocalSn = false;// 是否本省号码段内号码
		boolean ifMsisdSn = false;// 是否移动号码
		String eparchyCode = "";

		IData moffice = RouteInfoQry.getMofficeInfoBySn(serialNumber);
		if (IDataUtil.isNotEmpty(moffice))
			ifLocalSn = true;
		IData msisdInfo = MsisdnInfoQry.getMsisonBySerialnumber(serialNumber, null);
		if (IDataUtil.isNotEmpty(msisdInfo))
			ifMsisdSn = true;

		// 通过号码查询用户信息
		if (ifLocalSn)
		{
			eparchyCode = moffice.getString("EPARCHY_CODE", "");
			userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber, eparchyCode);
		} else
		{
			// 如果没有，遍历所有CRM库查
			String[] connNames = Route.getAllCrmDb();
			if (connNames != null)
			{

				int count = connNames.length;

				for (int index = 0; index < count; index++)
				{
					eparchyCode = connNames[index];

					userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber, eparchyCode);

					if (IDataUtil.isNotEmpty(userInfo))
					{
						break;
					}
				}
			}
		}

		// 存在有效用户
		if (IDataUtil.isNotEmpty(userInfo))
		{
			String pmode = userInfo.getString("PRODUCT_MODE", "");
			// 集团用户
			if ("10".equals(pmode))
			{
				userReusltCode = "2";
				result.put("USER_RESULT_CODE", userReusltCode);
				result.put("USER_INFO", userInfo);
				return result;
			}
			// 非集团用户
			userReusltCode = "0";
			if (ifMsisdSn)
			{
				reusltCodeDetail = "0";
				String staffEparchyCode = CSBizBean.getVisit().getStaffEparchyCode();
				if (!eparchyCode.equals(staffEparchyCode))
				{
					reusltCodeDetail = "1";
				}
			} else
			{
				reusltCodeDetail = "2";
			}
			result.put("USER_RESULT_CODE", userReusltCode);
			result.put("RESULT_CODE_DETAIL", reusltCodeDetail);
			result.put("USER_INFO", userInfo);
			return result;

		}

		// 号码不存在有效的用户
		userReusltCode = "1";
		if (!ifLocalSn)
		{
			reusltCodeDetail = "2";
		} else if (ifMsisdSn)
		{
			reusltCodeDetail = "0";
		} else
		{
			reusltCodeDetail = "1";
		}
		result.put("USER_RESULT_CODE", userReusltCode);
		result.put("RESULT_CODE_DETAIL", reusltCodeDetail);
		return result;
	}
    /**
     * @Function: getSN
     * @Description: 根据服务号码查询成员A
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-27 上午10:32:10 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-27 updata v1.0.0 修改原因
     */
    public static IDataset getSNA(String serial_number_a, String relation_type_code) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER_A", serial_number_a);
      
        param.put("RELATION_TYPE_CODE", relation_type_code);

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT USER_ID_A,USER_ID_B,RELATION_TYPE_CODE,SERIAL_NUMBER_B  FROM tf_f_relation_uu  u   WHERE u.serial_number_a=:SERIAL_NUMBER_A    AND u.relation_type_code=:RELATION_TYPE_CODE ");
        return Dao.qryBySql(sql, param);
    }

	/**
	 * 判断用户密码是否正确
	 * 
	 * @param userId
	 * @param passwd
	 * @return true 正确 false 错误
	 * @throws Exception
	 */
	public static boolean checkUserPassWd(String userId, String passwd) throws Exception
	{
		// 根据userid得到用户资料
		IData data = UcaInfoQry.qryUserInfoByUserId(userId);

		if (IDataUtil.isEmpty(data))
		{
			return false;
		}

		// 移动用户 加密因子为USER_ID后9位
		if (userId.length() < 16)
		{ // 小于16位的用户补0后截取
			userId = "0000000000000000" + userId;
			userId = userId.substring(userId.length() - 16);
		}
		// String key = userId.substring(7);
		String encrypt = Encryptor.fnEncrypt(passwd, PasswdMgr.genUserId(userId));

		String userPasswd = data.getString("USER_PASSWD", "");

		// 是否一致
		if (userPasswd.equals(encrypt))
		{
			return true;
		}

		// 兼容老的加密因子
		String genKey = PasswdAssistant.getUserPasswdKey(userId);
		if (StringUtils.isNotBlank(genKey))
		{
			encrypt = Encryptor.fnEncrypt(passwd, genKey);
			if (StringUtils.isBlank(encrypt))
				encrypt = "";
			if (StringUtils.equals(encrypt, userPasswd))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * 校验用户服务密码 注意这里面指定了路由是 CG
	 * 
	 * @param userId
	 * @param userPwd
	 * @return
	 * @throws Exception
	 */
	public static boolean checkUserPassword(String userId, String userPwd) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("USER_PASSWD", userPwd);
		IDataset userDataset = Dao.qryByCode("TF_F_USER", "CHECK_USER_PASSWD", param, Route.CONN_CRM_CG);
		if (IDataUtil.isNotEmpty(userDataset))
		{
			String rs = userDataset.getData(0).getString("RS", "");
			return "0".equals(rs) ? true : false;
		}
		return false;
	}

	public static IDataset existUserVIPREDNODEAL4AUTOPAYBySN(IData param) throws Exception
	{

		return Dao.qryByCode("TF_F_USER", "SEL_BY_SN_VIP_RED_NODEAL4AUTOPAY", param);
	}
	
	public static IDataset queryUserGroupId(String userid) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userid);
		return Dao.qryByCode("TF_F_USER", "SEL_CUST_GROUP_ID", param);
	}

	/**
	 * @Function: getAllDestroy
	 * @Description: 该函数的功能描述
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午9:57:49 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset getAllDestroyUserInfoBySn(String serial_number) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);

		return Dao.qryByCode("TF_F_USER", "SEL_BY_ALL_DESTROY", param);
	}

	/**
	 * 根据CUST_ID查询所有在网用户
	 * 
	 * @author chenzm
	 * @param cust_id
	 * @return IDataset
	 * @throws Exception
	 */
	public static IDataset getAllNormalUserInfoByCustId(String cust_id) throws Exception
	{
		IData inParams = new DataMap();
		inParams.put("CUST_ID", cust_id);
		return Dao.qryByCode("TF_F_USER", "SEL_BY_CUSTID_REMOVETAG", inParams);
	}
	public static IDataset getAllNormalUserInfoByCustId_2(String cust_id) throws Exception
	{
		IData inParams = new DataMap();
		inParams.put("CUST_ID", cust_id);
		return Dao.qryByCode("TF_F_USER", "SEL_BY_CUSTID_REMOVETAG_2", inParams);
	}

	/**
	 * 根据CUST_ID查询所有用户
	 * 
	 * @author chenzm
	 * @param cust_id
	 * @return IDataset
	 * @throws Exception
	 */
	public static IDataset getAllUserInfoByCustId(String cust_id) throws Exception
	{
		IData inParams = new DataMap();
		inParams.put("CUST_ID", cust_id);
		return Dao.qryByCode("TF_F_USER", "SEL_BY_CUSTID_ALLUSER", inParams);
	}

	/**
	 * 根据证件号码查询所有用户
	 * 
	 * @author chenzm
	 * @param pspt_type_code
	 * @param pspt_id
	 * @param remove_tag
	 * @param eparchy_code
	 * @return IDataset
	 * @throws Exception
	 */
	public static IDataset getAllUserInfoByPsptId(String pspt_type_code, String pspt_id, String remove_tag, String eparchy_code) throws Exception
	{
		IData data = new DataMap();
		data.put("PSPT_TYPE_CODE", pspt_type_code);
		data.put("PSPT_ID", pspt_id);
		data.put("REMOVE_TAG", remove_tag);
		data.put("EPARCHY_CODE", eparchy_code);
		return Dao.qryByCode("TF_F_USER", "SEL_BY_USRPID", data);
	}
	
	/**
	 * 通过手机号码查询所有的用户
	 * 
	 * @param serial_number
	 * @return
	 * @throws Exception
	 * @author wangww
	 */
	public static IDataset getAllUserInfoBySn(String serial_number) throws Exception
	{
		IData userInfo = UcaInfoQry.qryUserMainProdInfoBySn(serial_number);
		IDataset dataset = new DatasetList();
		if (IDataUtil.isNotEmpty(userInfo))
		{
			dataset.add(userInfo);
		}
		return dataset;
	}

	/**
	 * 根据集团编号PRODUCT_ID，GROUP_ID查询BBOSS办理过的子产品信息
	 * 
	 * @author liuxx3
	 * @date 2014-07-03
	 */
	public static IDataset getBBossProByESOPInfo(String groupId, String productId, Pagination pg) throws Exception
	{
		IData param = new DataMap();
		param.put("GROUP_ID", groupId);
		param.put("PRODUCT_ID", productId);

		SQLParser parser = new SQLParser(param);

		parser.addSQL("SELECT   ");
		parser.addSQL("U.PARTITION_ID,  ");
		parser.addSQL("to_char(U.USER_ID) USER_ID,  ");
		parser.addSQL("to_char(U.CUST_ID) CUST_ID,  ");
		parser.addSQL("to_char(U.USECUST_ID) USECUST_ID,  ");
		parser.addSQL("UP.PRODUCT_ID,  ");
		parser.addSQL("U.EPARCHY_CODE,  ");
		parser.addSQL("U.CITY_CODE,  ");
		parser.addSQL("U.CITY_CODE_A,  ");
		parser.addSQL("U.USER_PASSWD,  ");
		parser.addSQL("U.USER_DIFF_CODE,  ");
		parser.addSQL("U.USER_TYPE_CODE,  ");
		parser.addSQL("U.USER_TAG_SET,  ");
		parser.addSQL("U.USER_STATE_CODESET,  ");
		parser.addSQL("U.NET_TYPE_CODE,  ");
		parser.addSQL("U.SERIAL_NUMBER,  ");
		parser.addSQL("U.CONTRACT_ID,  ");
		parser.addSQL("U.ACCT_TAG,  ");
		parser.addSQL("U.PREPAY_TAG,  ");
		parser.addSQL("U.MPUTE_MONTH_FEE,  ");
		parser.addSQL("to_char(U.MPUTE_DATE,  'yyyy-mm-dd  hh24:mi:ss') MPUTE_DATE,  ");
		parser.addSQL("to_char(U.FIRST_CALL_TIME, 'yyyy-mm-dd  hh24:mi:ss') FIRST_CALL_TIME,  ");
		parser.addSQL("to_char(U.LAST_STOP_TIME, 'yyyy-mm-dd  hh24:mi:ss') LAST_STOP_TIME,  ");
		parser.addSQL("to_char(U.CHANGEUSER_DATE, 'yyyy-mm-dd  hh24:mi:ss') CHANGEUSER_DATE,  ");
		parser.addSQL("U.IN_NET_MODE,  ");
		parser.addSQL("to_char(U.IN_DATE,'yyyy-mm-dd  hh24:mi:ss') IN_DATE,  ");
		parser.addSQL("U.IN_STAFF_ID,  ");
		parser.addSQL("U.IN_DEPART_ID,  ");
		parser.addSQL("U.OPEN_MODE,  ");
		parser.addSQL("to_char(U.OPEN_DATE, 'yyyy-mm-dd  hh24:mi:ss') OPEN_DATE,  ");
		parser.addSQL("U.OPEN_STAFF_ID,  ");
		parser.addSQL("U.OPEN_DEPART_ID,  ");
		parser.addSQL("U.DEVELOP_STAFF_ID,  ");
		parser.addSQL("to_char(U.DEVELOP_DATE, 'yyyy-mm-dd  hh24:mi:ss') DEVELOP_DATE,  ");
		parser.addSQL("U.DEVELOP_DEPART_ID,  ");
		parser.addSQL("U.DEVELOP_CITY_CODE,  ");
		parser.addSQL("U.DEVELOP_EPARCHY_CODE,  ");
		parser.addSQL("U.DEVELOP_NO,  ");
		parser.addSQL("to_char(U.ASSURE_CUST_ID) ASSURE_CUST_ID,  ");
		parser.addSQL("U.ASSURE_TYPE_CODE,  ");
		parser.addSQL("to_char(U.ASSURE_DATE,'yyyy-mm-dd  hh24:mi:ss') ASSURE_DATE,  ");
		parser.addSQL("U.REMOVE_TAG,  ");
		parser.addSQL("to_char(U.PRE_DESTROY_TIME,'yyyy-mm-dd  hh24:mi:ss') PRE_DESTROY_TIME,  ");
		parser.addSQL("to_char(U.DESTROY_TIME,  'yyyy-mm-dd  hh24:mi:ss') DESTROY_TIME,  ");
		parser.addSQL("U.REMOVE_EPARCHY_CODE,  ");
		parser.addSQL("U.REMOVE_CITY_CODE,  ");
		parser.addSQL("U.REMOVE_DEPART_ID,  ");
		parser.addSQL("U.REMOVE_REASON_CODE,  ");
		parser.addSQL("to_char(U.UPDATE_TIME, 'yyyy-mm-dd  hh24:mi:ss') UPDATE_TIME,  ");
		parser.addSQL("U.UPDATE_STAFF_ID,  ");
		parser.addSQL("U.UPDATE_DEPART_ID,  ");
		parser.addSQL("U.REMARK,  ");
		parser.addSQL("U.RSRV_NUM1,  ");
		parser.addSQL("U.RSRV_NUM2,  ");
		parser.addSQL("U.RSRV_NUM3,  ");
		parser.addSQL("to_char(U.RSRV_NUM4) RSRV_NUM4,  ");
		parser.addSQL("to_char(U.RSRV_NUM5) RSRV_NUM5,  ");
		parser.addSQL("U.RSRV_STR1,  ");
		parser.addSQL("U.RSRV_STR2,  ");
		parser.addSQL("U.RSRV_STR3,  ");
		parser.addSQL("U.RSRV_STR4,  ");
		parser.addSQL("U.RSRV_STR5,  ");
		parser.addSQL("U.RSRV_STR6,  ");
		parser.addSQL("U.RSRV_STR7,  ");
		parser.addSQL("U.RSRV_STR8,  ");
		parser.addSQL("U.RSRV_STR9,  ");
		parser.addSQL("U.RSRV_STR10,  ");
		parser.addSQL("to_char(U.RSRV_DATE1, 'yyyy-mm-dd  hh24:mi:ss') RSRV_DATE1,  ");
		parser.addSQL("to_char(U.RSRV_DATE2, 'yyyy-mm-dd  hh24:mi:ss') RSRV_DATE2,  ");
		parser.addSQL("to_char(U.RSRV_DATE3, 'yyyy-mm-dd  hh24:mi:ss') RSRV_DATE3,  ");
		parser.addSQL("U.RSRV_TAG1,  ");
		parser.addSQL("U.RSRV_TAG2,  ");
		parser.addSQL("U.RSRV_TAG3  ");
		parser.addSQL("FROM TF_F_CUST_GROUP        G,  ");
		parser.addSQL("TF_F_USER     U,  ");
		parser.addSQL("TF_F_USER_PRODUCT      UP  ");
		parser.addSQL("WHERE G.CUST_ID = U.CUST_ID  ");
		parser.addSQL("AND U.REMOVE_TAG = '0'  ");
		parser.addSQL("AND UP.PRODUCT_ID = :PRODUCT_ID  ");
		parser.addSQL("AND G.GROUP_ID = :GROUP_ID  ");
		parser.addSQL("AND UP.USER_ID = U.USER_ID  ");

		return Dao.qryByParse(parser);
	}

	public static IDataset getBindEndStartDate(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_BIND_USER", "SEL_MAXSTARTDATE_BY_USERID", param);
	}

	/**
	 * 终端绑定用户接口
	 * 
	 * @author huangsl
	 */
	public static IDataset getBindEndUser(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_BIND_USER", "SEL_BY_USERID", param);
	}

	/**
	 * @Function: getBingdingElementInfo()
	 * @Description: 获取用户所订购的元素信息
	 * @param:
	 * @return：
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: yxd
	 * @date: 2014-7-22 下午5:11:50 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2014-7-22 yxd v1.0.0 修改原因
	 */
	public static IDataset getBingdingElementInfo(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		IDataset elementList = Dao.qryByCode("TF_F_USER_SVC", "SEL_SVC_DISCNT_ELE_BY_USERID", param);
		if (IDataUtil.isNotEmpty(elementList))
		{
			FillUserElementInfoUtil.fillUserElementProductIdAndPackageId(elementList, param, null);// 填充productId和packageId

			ElemInfoQry.fillElementName(elementList);
		}

		return elementList;
		// return Dao.qryByCode("TF_F_USER", "SEL_BY_USERID_ALLELEMENTS",
		// param);
	}

	public static IDataset getCheckUserInfo(String serialNumber, String removeTag, Pagination page) throws Exception
	{
		IData data = new DataMap();
		data.put("SERIAL_NUMBER", serialNumber);
		if (!"".equals(removeTag))
		{
			data.put("REMOVE_TAG", removeTag);
		}
		// IDataset dataset = Dao.qryByCodeParser("TF_F_USER",
		// "SEL_BY_SN_LINK_CUST_PRODUCT", data, page);
		SQLParser parser = new SQLParser(data);
		parser.addSQL(" (select u.user_id, u.cust_id, p.cust_name, u.eparchy_code, u.city_code, u.serial_number, u.remove_tag, u.destroy_time,u.open_date, u.in_staff_id ");
		parser.addSQL(" from tf_f_user u, tf_f_cust_person p ");
		parser.addSQL(" where p.cust_id=u.cust_id  ");
		parser.addSQL(" and p.partition_id = mod(u.cust_id,10000) ");
		parser.addSQL(" and u.partition_id = mod(u.user_id,10000) ");
		parser.addSQL(" and u.serial_number=:SERIAL_NUMBER");
		// parser.addSQL(" order by u.destroy_time desc,u.open_date desc ");
		parser.addSQL(" union ");
		parser.addSQL(" select a.user_id, a.cust_id, b.cust_name, a.eparchy_code, a.city_code, a.serial_number, a.remove_tag, a.destroy_time,a.open_date, a.in_staff_id ");
		parser.addSQL(" from tf_fh_user a, tf_f_cust_person b ");
		parser.addSQL(" where b.cust_id=a.cust_id  ");
		parser.addSQL(" and b.partition_id = mod(a.cust_id,10000) ");
		parser.addSQL(" and a.partition_id = mod(a.user_id,10000) ");
		parser.addSQL(" and a.serial_number=:SERIAL_NUMBER)");
		parser.addSQL(" order by destroy_time desc,open_date desc ");
		return Dao.qryByParse(parser);

	}

	public static IDataset getCustomerBySn(IData inparam, Pagination pagination) throws Exception
	{
		return Dao.qryByCode("TF_F_CUSTOMER", "SEL_ALL_BY_SN", inparam, pagination);
	}

	public static IDataset getCustomerBySnNormal(IData inparams, Pagination page) throws Exception
	{
		return Dao.qryByCode("TF_F_CUSTOMER", "SEL_ALL_BY_SN_NORMAL", inparams, page);
	}

	/**
	 * 品牌变更调用营销管理接口数据准备
	 * 
	 * @param userId
	 * @param tradeId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getDataForSaleByChangeBrand(String userId, String tradeId) throws Exception
	{
		
		//拆分sql  原sql  "TF_F_USER", "SEL_BRAND_CHANGE_BY_USER_ID"
		
		IData param = new DataMap();

		param.put("USER_ID_A", userId);
		param.put("USER_ID_B", userId);
		param.put("USER_ID_C", userId);
		param.put("TRADE_ID_A", tradeId);

        IData data = new DataMap();
        data.put("USER_ID", userId);
        data.put("TRADE_ID_A", tradeId);

        IDataset  res=Dao.qryByCode("TF_F_USER", "SEL_BRAND_CHANGE_BY_USER_ID_NEW1", param);
        if(IDataUtil.isNotEmpty(res)){
        	for(int i=0;i<res.size();i++){
        		IData temp=res.getData(i);
                data.put("CUST_ID", temp.getString("CUST_ID"));
                IDataset  res2=Dao.qryByCode("TF_F_USER", "SEL_BRAND_CHANGE_BY_USER_ID_NEW2", data,Route.getJourDb(BizRoute.getRouteId()));
                if(IDataUtil.isNotEmpty(res2)){
                temp.put("BRAND_CODE", res2.getData(0).getString("BRAND_CODE",""));
                temp.put("OLD_BRAND_CODE", res2.getData(0).getString("OLD_BRAND_CODE",""));
                temp.put("UPDATE_TIME", res2.getData(0).getString("UPDATE_TIME",""));
                }
        	}
        }
		
		
		
		return res;
	}

	public static IDataset getDestroyedUserInfo(String serial_number) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);
		return Dao.qryByCode("TF_F_USER", "SEL_ALL_BY_NPRESTORE", param);
	}

	public static IDataset getDestroyUserBySn(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		return Dao.qryByCode("TF_F_USER", "SEL_DESTORYUSER_BY_SN", param);
	}

	/**
	 * @Function: getDestroyUserInfoBySn
	 * @Description: 该函数的功能描述
	 * @param:参数描述
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午10:03:54 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset getDestroyUserInfoBySn(String serial_number) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);

		return Dao.qryByCode("TF_F_USER", "SEL_BY_SN_DESTROY", param);
	}

	/**
	 * 获取非正常状态(REMOVE_TAG!='0')用户信息
	 *
	 * @param serialNumber
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUnnormalUserInfoBySn(String serialNumber) throws Exception {
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		return Dao.qryByCode("TF_F_USER", "SEL_BY_SN_UNNORMAL", param);
	}

	/**
	 * @Function: getDiscntsByUserId
	 * @Description: 根据user_id获取成员未订购的优惠信息
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午10:06:50 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset getDiscntsByUserId(String product_id, String eparchy_code, String user_id_a, String user_id_b, String trade_staff_id) throws Exception
	{

		IData param = new DataMap();
		param.put("PRODUCT_ID", product_id);
		param.put("EPARCHY_CODE", eparchy_code);
		param.put("USER_ID_A", user_id_a);
		param.put("USER_ID_B", user_id_b);
		param.put("TRADE_STAFF_ID", trade_staff_id);

		SQLParser parser = new SQLParser(param);

		parser.addSQL(" SELECT '' user_id,d.discnt_code,d.discnt_name,d.discnt_explain,pm.product_id_b,pp.package_id,p.package_name,'' start_date,'' end_date, ");
		parser.addSQL("      p.min_number,p.max_number,e.default_tag,e.force_tag ");
		parser.addSQL("   FROM tf_f_user_product u, td_b_product_meb pm,td_b_product_package pp,td_b_package p,td_b_discnt d,td_b_package_element e ");
		parser.addSQL("  where 1=1 ");
		parser.addSQL("  and u.product_id = pm.product_id ");
		parser.addSQL("  and pm.product_id_b = pp.product_id ");
		parser.addSQL("  and pp.package_id = e.package_id ");
		parser.addSQL("  and pp.package_id = p.package_id ");
		parser.addSQL("  and e.element_id = d.discnt_code ");
		parser.addSQL("  and (d.eparchy_code = 'ZZZZ' or d.eparchy_code = :EPARCHY_CODE) ");
		parser.addSQL("  and e.element_type_code = 'D' ");
		parser.addSQL("  and u.main_tag = '1' ");
		parser.addSQL("  and u.user_id = :USER_ID_A ");
		parser.addSQL("  and u.partition_id = mod(TO_NUMBER(:USER_ID_A),10000) ");
		parser.addSQL("  and sysdate between e.start_date and e.end_date ");
		parser.addSQL("  and sysdate between d.start_date and d.end_date ");

		IDataset dataset = Dao.qryByParse(parser, Route.CONN_CRM_CG);

		if (IDataUtil.isEmpty(dataset))
		{
			return new DatasetList();
		}

		/*
		 * for (int i = dataset.size() - 1; i >= 0; i--) { boolean isPriv =
		 * StaffPrivUtil.isDistPriv(trade_staff_id,
		 * dataset.getData(i).getString("DISCNT_CODE")); if (!isPriv) {
		 * dataset.remove(i); } }
		 */

		return dataset;
	}

	/**
	 * @Function: getGprsUserInfo
	 * @Description: 根据CustId、ProductId查询gprs用户信息
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午10:08:28 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset getGprsUserInfo(String cust_id) throws Exception
	{
		IData param = new DataMap();
		param.put("CUST_ID", cust_id);

		return Dao.qryByCode("TF_F_USER", "SEL_BY_CUSTID_PRDID_GPRSINFO", param);
	}

	/**
	 * @Function: getGrpDiscntsByUserId
	 * @Description: 该函数的功能描述
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午10:09:37 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset getGrpDiscntsByUserId(String user_id) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);

		IDataset dataset = Dao.qryByCode("TF_F_USER", "SEL_DISCNTS_BY_USERID", param);
		return dataset;
	}

	/**
	 * 查询集团用户订购的产品及资费信息
	 * 
	 * @param groupId
	 * @param productId
	 * @param removeTag
	 * @return
	 * @throws Exception
	 */
	public static IDataset getGrpUserDiscntInfoByGrpId(String groupId, String productId, String removeTag) throws Exception
	{
		IData param = new DataMap();
		param.put("GROUP_ID", groupId);
		param.put("PRODUCT_ID", productId);
		param.put("REMOVE_TAG", removeTag);

		return Dao.qryByCodeParser("TF_F_USER", "SEL_BY_GROUPID_PRODUCTID_REMOVETAG", param, Route.CONN_CRM_CG);
	}

	/**
	 * 查询集团用户订购的产品信息
	 * 
	 * @param groupId
	 * @param removeTag
	 * @return
	 * @throws Exception
	 */
	public static IDataset getGrpUserInfoByGrpId(String groupId, String removeTag) throws Exception
	{
		IData param = new DataMap();
		param.put("GROUP_ID", groupId);
		param.put("REMOVE_TAG", removeTag);

		return Dao.qryByCodeParser("TF_F_USER", "SEL_BY_GROUPID_REMOVETAG", param, Route.CONN_CRM_CG);
	}

	/**
	 * 查询集团用户订购的产品信息
	 * 
	 * @param serialNumber
	 * @param removeTag
	 * @return
	 * @throws Exception
	 */
	public static IDataset getGrpUserInfoBySN(String serialNumber, String removeTag) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("REMOVE_TAG", removeTag);

		return Dao.qryByCodeParser("TF_F_USER", "SEL_BY_SERIALNUMBER_REMOVETAG", param, Route.CONN_CRM_CG);
	}

	/**
	 * @Function: getGrpUserInfoByUserId
	 * @Description: 根据用户USER_ID查询用户信息
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午10:10:00 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IData getGrpUserInfoByUserId(String user_id, String remove_tag, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("REMOVE_TAG", remove_tag);

		IDataset idata = Dao.qryByCode("TF_F_USER", "SEL_BY_USERID", param, eparchyCode);

		return idata.size() > 0 ? idata.getData(0) : null;
	}

	/**
	 * @Function: getGrpUserInfoByUserIdForGrp
	 * @Description: 从集团 库 根据用户USER_ID查询用户信息
	 * @param:参数描述
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午10:11:43 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IData getGrpUserInfoByUserIdForGrp(String user_id, String remove_tag) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("REMOVE_TAG", remove_tag);

		IDataset idata = Dao.qryByCode("TF_F_USER", "SEL_BY_USERID", param, Route.CONN_CRM_CG);
		return idata.size() > 0 ? idata.getData(0) : null;
	}

	/**
	 * todo code_code 表里没有 根据集团编号、VPMN的USER_ID和PRODUCT_ID查询客户资料
	 * 
	 * @author xiajj
	 * @param params
	 *            查询所需参数
	 * @param eparchyCode
	 *            地州编码
	 * @param pagination
	 * @return IDataset 客户资料列表
	 * @throws Exception
	 */
	public static IDataset getGrpVpmnUserInfoByGrpId(IData idata, Pagination page) throws Exception
	{

		IDataset result = Dao.qryByCode("TF_F_USER", "SEL_USER_BY_PRODUCT", idata, page, Route.CONN_CRM_CG);
		return result;
	}

	/**
	 * 根据user_id和状态查询查询用户最新的携转信息
	 * 
	 * @param userId
	 * @param state
	 * @return
	 * @throws Exception
	 */
	public static IDataset getLastUserNetnpInfo(String serialNumber, String state) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("STATE", state);
		return Dao.qryByCode("TF_F_USER_NETNP", "SEL_BY_NETNP_NUM_LAST", param, Route.CONN_CRM_CEN);
	}

	public static IDataset getLatestUserInfosBySerialNumber(String serial_number) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);
		return Dao.qryByCode("TF_F_USER", "SEL_BY_SN_LATEST", param);
	}
	   public static IDataset getLatestUserInfosBySerialNumber_1(String serial_number) throws Exception
	    {
	        IData param = new DataMap();
	        param.put("SERIAL_NUMBER", serial_number);
	        return Dao.qryByCode("TF_F_USER", "SEL_BY_SN_LATEST_1", param);
	    }

	public static IData getMebUserInfoBySN(String serialNumber) throws Exception
	{
		IData userGrpInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);

		IData retult = null;

		if (IDataUtil.isNotEmpty(userGrpInfo))
		{
			String pmode = userGrpInfo.getString("PRODUCT_MODE");

			retult = userGrpInfo;

			if ("10".equals(pmode)) // 暂改
			{
				retult.put("IsGrpSn", "Yes");
			}
		}

		return retult;
	}

	/**
	 * @Function: getMemInfo
	 * @Description: 查询集团成员的信息
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午10:14:08 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset getMemInfo(String serial_number_a, String user_id_mem, Pagination pagination) throws Exception
	{

		IData param = new DataMap();
		param.put("SERIAL_NUMBER_A", serial_number_a);
		param.put("USER_ID_MEM", user_id_mem);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT A.*, ");
		parser.addSQL(" B.CUST_NAME, ");
		parser.addSQL(" B.PSPT_TYPE_CODE, ");
		parser.addSQL(" B.PSPT_ID, ");
		parser.addSQL(" b.POST_CODE POST_CODE, ");
		parser.addSQL(" b.POST_ADDRESS ADDR ");
		parser.addSQL(" FROM TF_F_USER A, TF_F_CUST_PERSON B ");
		parser.addSQL(" WHERE 1 = 1 ");
		parser.addSQL(" AND A.REMOVE_TAG = '0' ");
		parser.addSQL(" AND B.REMOVE_TAG = '0' ");
		parser.addSQL(" AND B.CUST_ID = A.CUST_ID ");
		parser.addSQL(" AND B.PARTITION_ID = MOD(a.CUST_ID, 10000) ");
		parser.addSQL(" AND A.SERIAL_NUMBER = :SERIAL_NUMBER_A ");
		parser.addSQL(" AND A.USER_ID = :USER_ID_MEM ");
		parser.addSQL(" AND A.PARTITION_ID = MOD(to_number(:USER_ID_MEM), 10000) ");
		parser.addSQL(" AND ROWNUM = 1 ");

		return Dao.qryByParse(parser, pagination);
	}

	/**
	 * @Function: getMemInfo2
	 * @Description:查询集团用户的网外信息
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午10:15:15 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset getMemInfo2(String serial_number_a, String user_id_mem, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER_A", serial_number_a);
		param.put("USER_ID_MEM", user_id_mem);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT A.*,B.CUST_NAME,B.PSPT_TYPE_CODE,B.PSPT_ID,'' POST_CODE,'' ADDR FROM TF_F_USER A,TF_F_CUSTOMER B  ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" AND A.REMOVE_TAG='0' ");
		parser.addSQL(" AND B.REMOVE_TAG='0' ");
		parser.addSQL(" AND A.CUST_ID=B.CUST_ID ");
		parser.addSQL(" AND A.SERIAL_NUMBER=:SERIAL_NUMBER_A");
		parser.addSQL(" AND A.USER_ID=:USER_ID_MEM");
		parser.addSQL(" AND A.PARTITION_ID=MOD(to_number(:USER_ID_MEM), 10000)");
		parser.addSQL(" AND ROWNUM='1'");

		return Dao.qryByParse(parser, pagination);
	}

	// /**
	// * 根据SERIAL_NUMBER,REMOVE_TAG,NET_TYPE_CODE查询用户信息
	// */
	// public static IDataset getUserInfoBySn(IData inparams) throws Exception
	// {
	//
	// return getUserInfoBySn(inparams, null);
	// }

	// /**
	// * 根据SERIAL_NUMBER,REMOVE_TAG,NET_TYPE_CODE查询用户信息
	// */
	// public static IDataset getUserInfoBySn(IData inparams, String routeId)
	// throws Exception
	// {
	//
	// return Dao.qryByCodeParser("TF_F_USER", "SEL_BY_SN", inparams, routeId);
	// }

	public static IDataset getMofficeBySN(IData inparams) throws Exception
	{
		return Dao.qryByCode("TD_M_MOFFICE", "SEL_BY_NUM", inparams,Route.CONN_RES);
	}

	/**
	 * @Function: getNetNPBySN
	 * @Description: 查询携入携出地的地州编码和user_id
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午10:26:17 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因 mod liuke 2013-07-20 增加指定中心路由
	 */
	public static IDataset getNetNPBySN(String serial_number, String state) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);
		param.put("STATE", state);

		return Dao.qryByCode("TF_F_USER_NETNP", "SEL_BY_NETNP_NUM", param, Route.CONN_CRM_CEN);
	}

	// /**
	// * @Function: getUserInfoBySn
	// * @Description: 根据SERIAL_NUMBER,REMOVE_TAG,NET_TYPE_CODE查询用户信息
	// * @param
	// * @return：返回结果描述
	// * @throws：异常描述
	// * @version: v1.0.0
	// * @author: updata
	// * @date: 2013-4-27 上午10:48:57 Modification History: Date Author Version
	// Description
	// * ---------------------------------------------------------* 2013-4-27
	// updata v1.0.0 修改原因
	// */
	// public static IDataset getUserInfoBySn(String serial_number, String
	// remove_tag, String net_type_code) throws
	// Exception
	// {
	// IData param = new DataMap();
	// param.put("SERIAL_NUMBER", serial_number);
	// param.put("REMOVE_TAG", remove_tag);
	// param.put("NET_TYPE_CODE", net_type_code);
	//
	// return getUserInfoBySn(param, null);
	// }

	// /**
	// * @Function: getUserInfoBySn
	// * @Description: 根据SERIAL_NUMBER,REMOVE_TAG,NET_TYPE_CODE查询用户信息
	// * @param
	// * @return：返回结果描述
	// * @throws：异常描述
	// * @version: v1.0.0
	// * @author: updata
	// * @date: 2013-4-27 上午10:50:08 Modification History: Date Author Version
	// Description
	// * ---------------------------------------------------------* 2013-4-27
	// updata v1.0.0 修改原因
	// */
	// public static IDataset getUserInfoBySn(String serial_number, String
	// remove_tag, String net_type_code, String
	// routeId) throws Exception
	// {
	//
	// IData param = new DataMap();
	// param.put("SERIAL_NUMBER", serial_number);
	// param.put("REMOVE_TAG", remove_tag);
	// param.put("NET_TYPE_CODE", net_type_code);
	//
	// return getUserInfoBySn(param, routeId);
	// }

	public static IDataset getNetNPBySN(String serial_number, String state, Pagination page) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);
		param.put("STATE", state);

		return Dao.qryByCode("TF_F_USER_NETNP", "SEL_BY_NETNP_NUM", param, page, Route.CONN_CRM_CEN);
	}

	/**
	 * 根据user_id和状态查询用户携转信息
	 * 
	 * @param userId
	 * @param state
	 * @return
	 * @throws Exception
	 */
	public static IDataset getNetNPByUserId(String userId, String state) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("STATE", state);
		return Dao.qryByCode("TF_F_USER_NETNP", "SEL_NETNP_BY_USER", param, Route.CONN_CRM_CEN);
	}

	/**
	 * @Des 携号转网预警信息查询
	 * @param userId
	 *            ,tradeAttr
	 * @return
	 * @author huangsl
	 * @throws Exception
	 */
	public static IDataset getNpWarningInfo(String userId, String tradeAttr) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("TRADE_ATTR", tradeAttr);

		return Dao.qryByCode("TF_F_USER", "SEL_USER_INTF", param);
	}

	public static IDataset getOCSInfo(String userId, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_OCS", "SEL_BY_USERID", param, eparchyCode);
	}

	public static IDataset getOnesrPidMorePhoneByPsptId(IData param) throws Exception
	{

		SQLParser parser = new SQLParser(param);

		parser.addSQL(" SELECT a.serial_number serial_number,b.cust_name cust_name,b.pspt_type_code pspt_type_code, ");
		parser.addSQL(" b.pspt_id pspt_id,a.remove_tag remove_tag,TO_CHAR(a.open_date, ''YYYY-MM-DD HH24:MI:SS'') open_date, ");
		parser.addSQL(" TO_CHAR(a.destroy_time, ''YYYY-MM-DD HH24:MI:SS'') destroy_time,TO_CHAR(a.user_id) user_id, ");
		parser.addSQL(" d.BRAND_CODE BRAND_CODE,a.in_staff_id in_staff_id,a.in_depart_id in_depart_id,a.open_staff_id open_staff_id, ");
		parser.addSQL(" a.open_depart_id open_depart_id FROM tf_f_customer b, tf_f_user a,TF_F_USER_PRODUCT d  WHERE a.cust_id = b.cust_id ");
		parser.addSQL(" AND a.USER_ID = d.USER_ID AND a.USER_ID = d.USER_ID ");
		parser.addSQL(" AND b.pspt_id = :PSPT_ID ");
		parser.addSQL(" AND a.remove_tag like ''%'' || :REMOVE_TAG || ''%'' ");
		parser.addSQL(" AND b.pspt_type_code like ''%'' || :PSPT_TYPE_CODE || ''%'' ");
		parser.addSQL(" AND b.partition_id = mod(b.cust_id, 10000) ");
		parser.addSQL(" AND d.PARTITION_ID = MOD(TO_NUMBER(d.USER_ID), 10000) AND a.eparchy_code = :EPARCHY_CODE  AND b.eparchy_code = :EPARCHY_CODE ");
		parser.addSQL(" AND d.MAIN_TAG = '1' AND SYSDATE BETWEEN d.START_DATE AND d.END_DATE AND rownum <= 30   ");
		parser.addSQL(" ORDER BY a.open_date ");

		return Dao.qryByParse(parser);
	}

	/**
	 * @methodName: getOnlineUserInfoBySN
	 * @Description: 根据serial_number查询在网用户信息
	 * @version: v1.0.0
	 * @author: xiaozb
	 * @date: 2014-7-10 下午5:11:08
	 */
	public static IDataset getOnlineUserInfoBySN(String serial_number, String net_type_code) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);
		param.put("NET_TYPE_CODE", net_type_code);
		return Dao.qryByCode("TF_F_USER", "SEL_ONLINE_USER_BY_SN", param);
	}

	public static IDataset getOtherInfo(String userId, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER", "SEL_OTHER_BY_USERID_ALL", param, eparchyCode);
	}

	/**
	 * 根据PSPT_TYPE_CODE,PSPT_ID,EPARCHY_CODE查询欠费销号用户数
	 * 
	 * @author chenzm
	 * @param pspt_type_code
	 * @param pspt_id
	 * @param eparchy_code
	 * @return IDataset
	 * @throws Exception
	 */
	public static IDataset getQfxhUserInfoByPspt(String pspt_type_code, String pspt_id, String eparchy_code) throws Exception
	{
		IData inParams = new DataMap();
		inParams.put("PSPT_TYPE_CODE", pspt_type_code);
		inParams.put("PSPT_ID", pspt_id);
		inParams.put("EPARCHY_CODE", eparchy_code);
		return Dao.qryByCode("TF_F_USER", "SEL_USER_NUM_BY_PSPT_QFXH", inParams);
	}

	/**
	 * 获取指定证件号已实名制开户的user_id
	 * 
	 * @author sunxin
	 * @param inparams
	 *            查询所需参数
	 * @param pd
	 * @return IDataset
	 * @throws Exception
	 */
	public static IDataset getRealNameUserCountByPspt(String custName, String psptId) throws Exception
	{
		// 加上对测试证件的过滤，该证件在测试环境存在100多万，sql语句执行不了。
		if ((StringUtils.equals("海南移动", custName) || StringUtils.equals("测试用户", custName)) && (StringUtils.equals("460001194910011234", psptId) || StringUtils.equals("460025199011260016", psptId)))
		{
			return new DatasetList();
		}

		IData param = new DataMap();
        param.put("CUST_NAME", custName);
        param.put("PSPT_ID", psptId);
        IDataset realAllCounts = new DatasetList();
        for(int i =0; i<10; i++){
        	String sqlSring = "SEL_REALNAME_SN_BY_PSPT_PART" + i;
        	IDataset realCounts = Dao.qryByCodeParser("TF_F_USER", sqlSring, param);// sql改造，user里没有band
        	if(IDataUtil.isNotEmpty(realCounts) && realCounts.size()>0){
        		realAllCounts.add(realCounts);
        	}
        }
        return realAllCounts;
	}

	/**
	 * 获取使用人证件号码已实名制开户的数量
	 * 
	 * @author yanwu
	 * @param inparams
	 * 
	 * @param pd
	 * @return IDataset
	 * @throws Exception
	 */
	public static int getRealNameUserCountByUsePspt(String custName, String psptId, String serialNumber) throws Exception
	{
		// 加上对测试证件的过滤，该证件在测试环境存在100多万，sql语句执行不了。
		if ((StringUtils.equals("海南移动", custName) || StringUtils.equals("测试用户", custName)) && (StringUtils.equals("460001194910011234", psptId) || StringUtils.equals("460025199011260016", psptId)))
		{
			return 0;
		}

		IData param = new DataMap();
		param.put("CUST_NAME", custName);
		param.put("PSPT_ID", psptId);
		if (StringUtils.isNotBlank(serialNumber))
		{
			param.put("SERIAL_NUMBER", serialNumber);
		} else
		{
			param.put("SERIAL_NUMBER", "");
		}
		IDataset realCounts = Dao.qryByCode("TF_F_USER", "SEL_REALNAME_USER_BY_USEPSPT", param);// sql改造，user里没有band

		if (IDataUtil.isNotEmpty(realCounts))
		{
			return realCounts.getData(0).getInt("REAL_COUNT");
		} else
		{
			return 0;
		}

	}

	/**
	 * 获取无手机宽带开户证件号码已实名制开户的数量
	 * 
	 * @param custName
	 * @param psptId
	 * @param strNetTypeCode
	 * @return
	 * @throws Exception
	 * @author yuyj3
	 */
	public static int getRealNameUserCountByNoPhoneWidenet(String psptId) throws Exception
	{
		// 加上对测试证件的过滤，该证件在测试环境存在100多万，sql语句执行不了。
		if ((StringUtils.equals("460001194910011234", psptId) || StringUtils.equals("460025199011260016", psptId)))
		{
			return 0;
		}

		IData param = new DataMap();
		param.put("PSPT_ID", psptId);
		IDataset realCounts = Dao.qryByCode("TF_F_USER", "SEL_REALNAME_USER_BY_NOPHONE_WIDENET", param);

		if (IDataUtil.isNotEmpty(realCounts))
		{
			return realCounts.getData(0).getInt("REAL_COUNT");
		} else
		{
			return 0;
		}

	}

	/**
	 * 获取铁通证件号码已实名制开户的数量
	 * 
	 * @author yanwu
	 * @param inparams
	 * 
	 * @param pd
	 * @return IDataset
	 * @throws Exception
	 */
	public static int getRealNameUserCountByPsptCtt(String custName, String psptId, String strNetTypeCode) throws Exception
	{
		// 加上对测试证件的过滤，该证件在测试环境存在100多万，sql语句执行不了。
		if ((StringUtils.equals("海南移动", custName) || StringUtils.equals("测试用户", custName)) && (StringUtils.equals("460001194910011234", psptId) || StringUtils.equals("460025199011260016", psptId)))
		{
			return 0;
		}

		IData param = new DataMap();
		param.put("NET_TYPE_CODE", strNetTypeCode);
		param.put("CUST_NAME", custName);
		param.put("PSPT_ID", psptId);
		IDataset realCounts = Dao.qryByCode("TF_F_USER", "SEL_REALNAME_BY_PSPTCTT", param);// sql改造，user里没有band

		if (IDataUtil.isNotEmpty(realCounts))
		{
			return realCounts.getData(0).getInt("REAL_COUNT");
		} else
		{
			return 0;
		}

	}
	
	public static int getRealNameUserCountByPspt2(String custName, String psptId, String strNetTypeCode) throws Exception
	{
		int count = 0;
        // 加上对测试证件的过滤，该证件在测试环境存在100多万，sql语句执行不了。
        if ((StringUtils.equals("海南移动", custName) || StringUtils.equals("测试用户", custName)) && (StringUtils.equals("460001194910011234", psptId) || StringUtils.equals("460025199011260016", psptId)))
        {
            return count;
        }
        
        IData param = new DataMap();
        param.put("CUST_NAME", custName);
        param.put("PSPT_ID", psptId);
        param.put("NET_TYPE_CODE", strNetTypeCode);
        for(int i =0; i<10; i++){
        	String sqlSring = "SEL_REALNAME_SN_BY_PSPT_PART" + i;
        	IDataset realCounts = Dao.qryByCodeParser("TF_F_USER", sqlSring, param);// sql改造，user里没有band
        	if(IDataUtil.isNotEmpty(realCounts) && realCounts.size()>0){
        		count = count + realCounts.size();
        	}
        }
        
        return count;

	}
	
	public static int getRealNameUserCountByPspt3(String custName, String psptId, String strNetTypeCode) throws Exception
	{
		int count = 0;
        // 加上对测试证件的过滤，该证件在测试环境存在100多万，sql语句执行不了。
        if ((StringUtils.equals("海南移动", custName) || StringUtils.equals("测试用户", custName)) && (StringUtils.equals("460001194910011234", psptId) || StringUtils.equals("460025199011260016", psptId)))
        {
            return count;
        }
        
        IData param = new DataMap();
        param.put("CUST_NAME", custName);
        param.put("PSPT_ID", psptId);
        param.put("NET_TYPE_CODE", strNetTypeCode);
        for(int i =0; i<10; i++){
        	String sqlSring = "SEL_REALNAME_SN_BY_PSPT_PARTNEW" + i;
        	IDataset realCounts = Dao.qryByCodeParser("TF_F_USER", sqlSring, param);// sql改造，user里没有band
        	if(IDataUtil.isNotEmpty(realCounts) && realCounts.size()>0){
        		count = count + realCounts.size();
        	}
        }
        
        return count;

	}
	
	//单个证件入网当月一证2户数量限制功能
	public static int getRealNameUserCountByDay(String custName, String psptId) throws Exception
	{
		int count = 0;
        if ((StringUtils.equals("海南移动", custName) || StringUtils.equals("测试用户", custName)) && (StringUtils.equals("460001194910011234", psptId) || StringUtils.equals("460025199011260016", psptId)))
        {
            return count;
        }
        
        IData param = new DataMap();
        param.put("CUST_NAME", custName);
        param.put("PSPT_ID", psptId);
        for(int i =0; i<10; i++){
        	String sqlSring = "SEL_REALNAME_SN_BY_PSPT_DAY" + i;
        	IDataset realCounts = Dao.qryByCodeParser("TF_F_USER", sqlSring, param);
        	if(IDataUtil.isNotEmpty(realCounts) && realCounts.size()>0){
        		count = count + realCounts.size();
        	}
        }
        
        return count;

	}

	public static int getRealNameTDUserCountByDay(String custName, String psptId) throws Exception
	{
		int count = 0;
		if ((StringUtils.equals("海南移动", custName) || StringUtils.equals("测试用户", custName)) && (StringUtils.equals("460001194910011234", psptId) || StringUtils.equals("460025199011260016", psptId)))
		{
			return count;
		}

		IData param = new DataMap();
		param.put("CUST_NAME", custName);
		param.put("PSPT_ID", psptId);
		for(int i =0; i<10; i++){
			String sqlSring = "SEL_TD_REALNAME_SN_BY_PSPT_DAY" + i;
			IDataset realCounts = Dao.qryByCode("TF_F_USER", sqlSring, param);
			if(IDataUtil.isNotEmpty(realCounts) && realCounts.size()>0){
				count = count + realCounts.size();
			}
		}

		return count;

	}
	
	
	/**
	 * 获取指定证件号已实名制开户的数量
	 * 
	 * @author wukw3
	 * @param inparams
	 *            查询所需参数
	 * @param pd
	 * @return IDataset
	 * @throws Exception
	 */
	public static int getRealNameUserCountByPspt2(String custName, String psptId) throws Exception
	{
		int count = 0;
        // 加上对测试证件的过滤，该证件在测试环境存在100多万，sql语句执行不了。
        if ((StringUtils.equals("海南移动", custName) || StringUtils.equals("测试用户", custName)) && (StringUtils.equals("460001194910011234", psptId) || StringUtils.equals("460025199011260016", psptId)))
        {
            return count;
        }
        
        IData param = new DataMap();
        param.put("CUST_NAME", custName);
        param.put("PSPT_ID", psptId);
        for(int i =0; i<10; i++){
        	String sqlSring = "SEL_REALNAME_SN_BY_PSPT_PART" + i;
        	IDataset realCounts = Dao.qryByCodeParser("TF_F_USER", sqlSring, param);// sql改造，user里没有band
        	if(IDataUtil.isNotEmpty(realCounts) && realCounts.size()>0){
        		count = count + realCounts.size();
        	}
        }
        
        return count;

	}

	/**
	 * 获取指定证件号已实名制开户的无线固话数量
	 *
	 * @author chenchunni
	 * @param inparams
	 *            查询所需参数
	 * @param pd
	 * @return IDataset
	 * @throws Exception
	 */
	public static int getRealNameTDUserCountByPspt(String custName, String psptId) throws Exception
	{
		int count = 0;
		// 加上对测试证件的过滤，该证件在测试环境存在100多万，sql语句执行不了。
		if ((StringUtils.equals("海南移动", custName) || StringUtils.equals("测试用户", custName)) && (StringUtils.equals("460001194910011234", psptId) || StringUtils.equals("460025199011260016", psptId)))
		{
			return count;
		}

		IData param = new DataMap();
		param.put("CUST_NAME", custName);
		param.put("PSPT_ID", psptId);
		for(int i =0; i<10; i++){
			String sqlSring = "SEL_TD_REALNAME_SN_BY_PSPT_PART" + i;
			IDataset realCounts = Dao.qryByCode("TF_F_USER", sqlSring, param);// sql改造，user里没有band
			if(IDataUtil.isNotEmpty(realCounts) && realCounts.size()>0){
				count = count + realCounts.size();
			}
		}

		return count;

	}
	
	/**
	 * 获取指定证件号已实名制开户的数量
	 * 
	 * @author wukw3
	 * @param inparams
	 *            查询所需参数
	 * @param pd
	 * @return IDataset
	 * @throws Exception
	 */
	public static int getRealNameUserCountByPspt2New(String custName, String psptId, String userType) throws Exception
	{
		int count = 0;
        // 加上对测试证件的过滤，该证件在测试环境存在100多万，sql语句执行不了。
        if ((StringUtils.equals("海南移动", custName) || StringUtils.equals("测试用户", custName)) && (StringUtils.equals("460001194910011234", psptId) || StringUtils.equals("460025199011260016", psptId)))
        {
            return count;
        }
        
        IData param = new DataMap();
        param.put("CUST_NAME", custName);
        param.put("PSPT_ID", psptId);
        for(int i =0; i<10; i++){
        	String sqlSring;
        	if("0".equals(userType)){
        		sqlSring = "SEL_MOBILE_REALNAME_SN_BY_PSPT_PART" + i;
        	}else if("1".equals(userType)){
        		sqlSring = "SEL_PWLW_REALNAME_SN_BY_PSPT_PART" + i;
        	}else {
        		sqlSring = "SEL_REALNAME_SN_BY_PSPT_PART" + i;
        	}
        	IDataset realCounts = Dao.qryByCodeParser("TF_F_USER", sqlSring, param,Route.CONN_CRM_CG);// sql改造，user里没有band
        	if(IDataUtil.isNotEmpty(realCounts) && realCounts.size()>0){
        		count = count + realCounts.size();
        	}
        }
        
        return count;

	}

	/**
	 * 获取指定证件号已实名制开户的user_id
	 * 
	 * @author sunxin
	 * @param inparams
	 *            查询所需参数
	 * @param pd
	 * @return IDataset
	 * @throws Exception
	 */
	public static int getRealNameUserLimitByPspt(String custName, String psptId) throws Exception
	{
		IData param = new DataMap();
		param.put("CUST_NAME", custName);
		param.put("PSPT_ID", psptId);
		IDataset ds = Dao.qryByCode("TF_F_CUSTOMER", "SEL_REALNAME_LIMIT_BY_PSPT", param);
		int Lcount = 5;
		if (ds.size() != 0)
		{
			Lcount = ds.getData(0).getInt("LIMIT_COUNT", 5);
		} else
		{
			IDataset dsdefault = CommparaInfoQry.getCommparaAllCol("CSM", "7637", "0", "0898");
			Lcount = Integer.parseInt(dsdefault.getData(0).getString("PARA_CODE1", "5"));
		}
		return Lcount;
	}

	/**
	 * 获取指定证件号已实名制开户无线固话的user_id
	 *
	 * @author chenchunni
	 * @param inparams
	 *            查询所需参数
	 * @param pd
	 * @return IDataset
	 * @throws Exception
	 */
	public static int getRealNameTDUserLimitByPspt(String custName, String psptId) throws Exception
	{
		IData param = new DataMap();
		param.put("CUST_NAME", custName);
		param.put("PSPT_ID", psptId);
		IDataset ds = Dao.qryByCode("TF_F_CUSTOMER", "SEL_TD_REALNAME_LIMIT_BY_PSPT", param);
		int Lcount = 5;
		if (ds.size() != 0)
		{
			Lcount = ds.getData(0).getInt("LIMIT_COUNT", 5);
		} else
		{
			IDataset dsdefault = CommparaInfoQry.getCommparaAllCol("CSM", "7637", "0", "0898");
			Lcount = Integer.parseInt(dsdefault.getData(0).getString("PARA_CODE1", "5"));
		}
		return Lcount;
	}
	
	/**
	 * 优化单位证件开户阀值权限设置需求
	 * 
	 * @author mengqx
	 */
	public static int getRealNameUserLimitByPsptNew(String custName, String psptId, String userType) throws Exception
	{
		IData param = new DataMap();
		param.put("CUST_NAME", custName);
		param.put("PSPT_ID", psptId);
		param.put("RSRV_STR1", userType);
		IDataset ds = Dao.qryByCode("TF_F_CUSTOMER", "SEL_REALNAME_LIMIT_BY_PSPT_RSRV_STR1", param,Route.CONN_CRM_CG);
		int Lcount = 5;
		if (ds.size() != 0)
		{
			Lcount = ds.getData(0).getInt("LIMIT_COUNT", 5);
		} else
		{
			IDataset dsdefault = CommparaInfoQry.getCommparaAllCol("CSM", "7637", "0", "0898");
			Lcount = Integer.parseInt(dsdefault.getData(0).getString("PARA_CODE1", "5"));
		}
		return Lcount;
	}

	/**
	 * @Function: getRelaUserInfoByCstId
	 * @Description:根据CustId 查询集团成员信息查询 或者根据集团编号CustId、电话号码 查询集团成员订购关系
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午10:30:23 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset getRelaUserInfoByCstId(String cust_id, String user_id_a, String user_id_b, Pagination page) throws Exception
	{
		IData param = new DataMap();
		param.put("CUST_ID", cust_id);
		param.put("USER_ID_A", user_id_a);
		param.put("USER_ID_B", user_id_b);

		IDataset idataset = Dao.qryByCodeParser("TF_F_USER", "SEL_BY_CUSTID_RELATION_UU", param);
		if (IDataUtil.isEmpty(idataset))
		{
			return idataset;
		}

		for (int i = 0; i < idataset.size(); i++)
		{
			IData idata = idataset.getData(i);
			String userIdA = idata.getString("USER_ID_A");
			// 查集团用户
			IData userGroup = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userIdA);
			if (IDataUtil.isNotEmpty(userGroup))
			{
				String productId = userGroup.getString("PRODUCT_ID");
				idata.put("PRODUCT_ID", productId);
			}
		}

		return idataset;

	}
	public static IDataset selUserInfo(String userId) throws Exception
    {
        IData inParams = new DataMap();
        inParams.put("USER_ID", userId);
        return Dao.qryByCode("TF_F_USER", "SEL_ALL_BY_USERID", inParams);
    }

	public static IDataset getRsrvStr1(IData data) throws Exception
	{
		SQLParser parser = new SQLParser(data);
		parser.addSQL("SELECT trim(to_char(TO_NUMBER(NVL(SUBSTR(NVL(MAX(TO_NUMBER(U.RSRV_STR1)),'000000000000000000000'),16,6),'0'))+1,'000000')) BANK_STR");
		parser.addSQL("  FROM TF_F_USER U,TF_F_USER_PRODUCT UP");
		parser.addSQL(" WHERE 1 = 1");
		parser.addSQL("   AND U.USER_ID = UP.USER_ID");
		parser.addSQL("   AND U.PARTITION_ID = UP.PARTITION_ID");
		parser.addSQL("   AND U.RSRV_STR1 like '5%'");
		parser.addSQL("   AND U.RSRV_STR2 = :SUPER_BANK_CODE");
		parser.addSQL("   AND UP.MAIN_TAG = '1'");
		parser.addSQL("   AND UP.PRODUCT_ID = 7000");
		parser.addSQL("   AND U.eparchy_code = :EPARCHY_CODE");
		return Dao.qryByParse(parser);
	}

	/**
	 * @Function: getSN
	 * @Description: 根据服务号码查询成员A,和成员B
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午10:32:10 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset getSN(String serial_number_a, String serial_number_b, String relation_type_code) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER_A", serial_number_a);
		param.put("SERIAL_NUMBER_B", serial_number_b);
		param.put("RELATION_TYPE_CODE", relation_type_code);

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT USER_ID_A,USER_ID_B,RELATION_TYPE_CODE  FROM tf_f_relation_uu  u   WHERE u.serial_number_a=:SERIAL_NUMBER_A   AND u.serial_number_b =:SERIAL_NUMBER_B  AND u.relation_type_code=:RELATION_TYPE_CODE ");
		return Dao.qryBySql(sql, param);
	}

	/**
	 * @Des 缴费通使用情况查询
	 * @param userId
	 *            ,stateCode
	 * @return
	 * @author huangsl
	 * @throws Exception
	 */
	public static IDataset getSnPaymentInfo(String userId, String stateCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("STATE_CODE", stateCode);

		return Dao.qryByCode("TD_S_COMMPARA", "SEL_SNPAYMENT_USE_HINT", param);
	}

	/**
	 * @param serialNumber
	 * @param specType
	 * @param specFlag
	 * @return
	 * @throws Exception
	 */
	public static IDataset getSpecVpmnUserInfoBySn(String serialNumber, String specType, String specFlag) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("SPEC_TYPE", specType);
		param.put("SPEC_FLAG", specFlag);

		return Dao.qryByCode("TF_F_USER", "SEL_SPEC_VPMN_USER", param);
	}

	/**
	 * 根据user_id查询固话用户
	 * 
	 * @author chenzm
	 * @param cust_id
	 * @return IDataset
	 * @throws Exception
	 */
	public static IDataset getTelephoneInfoByUserId(String userId) throws Exception
	{
		IData inParams = new DataMap();
		inParams.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_TELEPHONE", "SEL_ALL_BY_USERID", inParams);
	}

	public static IDataset getTestCardCustId(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		SQLParser parser = new SQLParser(param);

		parser.addSQL(" SELECT U.CUST_ID FROM TF_F_USER U,TF_F_OWN_SERIALNUMBER_MANAGE M  ");
		parser.addSQL(" WHERE M.SERIAL_NUMBER=:SERIAL_NUMBER AND  M.SERIAL_NUMBER=U.SERIAL_NUMBER ");
		parser.addSQL(" AND TO_CHAR(M.START_DATE,'YYYY-MM-DD HH24:MI:SS')=  TO_CHAR(U.OPEN_DATE,'YYYY-MM-DD HH24:MI:SS') ");
		parser.addSQL(" AND U.REMOVE_TAG='0' ");

		return Dao.qryByParse(parser);
	}

	public static IDataset getTradeUserInfoByUserIdAndTag(IData inparam) throws Exception
	{
		IData data = new DataMap();
		data.put("USER_ID", inparam.get("USER_ID"));
		data.put("REMOVE_TAG", inparam.get("REMOVE_TAG"));
		SQLParser parser = new SQLParser(data);

		parser.addSQL(" SELECT *  ");
		parser.addSQL(" FROM TF_F_USER ");
		parser.addSQL(" WHERE USER_ID =:USER_ID  ");
		parser.addSQL(" AND REMOVE_TAG =:REMOVE_TAG  ");

		return Dao.qryByParse(parser, null, Route.CONN_CRM_CG);
	}

	public static IDataset getUserAndProductInfoBySn(String serial_number, String remove_tag, String net_type_code, String routeId) throws Exception
	{
		// 得到用户信息
		IDataset idsUser = getUserInfoBySn(serial_number, remove_tag, net_type_code);
		if (IDataUtil.isEmpty(idsUser))
		{
			return idsUser;
		}

		// 得到用户产品信息
		String userId = "";
		IData mapUser = null;
		IData idsProduct = null;

		for (int row = 0; row < idsUser.size(); row++)
		{
			mapUser = idsUser.getData(row);

			userId = mapUser.getString("USER_ID");

			idsProduct = UcaInfoQry.qryMainProdInfoByUserId(userId, routeId);

			// 不为空
			if (IDataUtil.isNotEmpty(idsProduct))
			{
				mapUser.put("PRODUCT_ID", idsProduct.getString("PRODUCT_ID"));
				mapUser.put("BRAND_CODE", idsProduct.getString("BRAND_CODE"));
			} else
			{
				idsProduct = UcaInfoQry.qryMainProdInfoByUserId(userId);
				if (IDataUtil.isNotEmpty(idsProduct))
				{
					mapUser.put("PRODUCT_ID", idsProduct.getString("PRODUCT_ID"));
					mapUser.put("BRAND_CODE", idsProduct.getString("BRAND_CODE"));
				}

			}
		}

		return idsUser;
	}

	/**
	 * 根据user_id查询用户品牌信息
	 * 
	 * @param user_id
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserBrand(String user_id) throws Exception
	{

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT B.PARAM_CODE,B.PARA_CODE1 BRAND_TYPE,DECODE(B.PARA_CODE1,'0','全球通','1','神州行','2','动感地带','3','神州行','中国移动') BRAND_NAME" + " FROM TF_F_USER A,TD_S_COMMPARA B " + " WHERE A.USER_ID='" + user_id + "'");
		sql.append(" AND A.PARTITION_ID=MOD(TO_NUMBER('" + user_id + "'),10000) " + " AND A.REMOVE_TAG='0'" + " AND B.PARAM_ATTR='998'" + " AND A.BRAND_CODE=B.PARAM_CODE");
		return Dao.qryBySql(sql, new DataMap());

	}

	/**
	 * @Function: getUserByService
	 * @Description: 根据serial_number、SERVICE_ID查询用户和客户信息
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午10:33:13 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset getUserByService(String serial_number, String service_id) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);
		param.put("SERVICE_ID", service_id);

		return Dao.qryByCode("TF_F_USER", "SEL_BY_SERVICE", param);
	}

	public static IDataset getUserBySnABNORMAL(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		return Dao.qryByCode("TF_F_USER", "SEL_ALL_BY_SN_ABNORMAL", param);
	}

	/**
	 * todo code_code 表里没有SEL_USER_BY_UU_A 根据USER_ID查询用户下的成员用户资料
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 * @author xiajj
	 */
	public static IDataset getUserByUUa(IData param) throws Exception
	{

		IDataset userattrs = Dao.qryByCode("TF_F_USER", "SEL_USER_BY_UU_A", param, Route.CONN_CRM_CG);
		return userattrs;
	}

	/**
	 * @Function: getUserCustInfo
	 * @Description: 根据serial_number查询用户和客户信息
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午10:34:52 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset getUserCustInfo(String serial_number, String eparchy_code) throws Exception
	{

		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);
		param.put("EPARCHY_CODE", eparchy_code);

		return Dao.qryByCode("TF_F_USER", "SEL_BY_USERINFO", param);
	}

	public static IDataset getUserDefaultPassWd(String serialNumber, String remove_tag) throws Exception
	{
		IData data = new DataMap();
		data.put("SERIAL_NUMBER", serialNumber);
		data.put("REMOVE_TAG", remove_tag);
		IDataset dataset = Dao.qryByCode("TF_F_USER", "SEL_BY_SERP", data);
		return dataset;
	}

	/**
	 * @Function: getUserID
	 * @Description:从集团库 根据PRODUCT_ID和CUST_ID获取用户信息:USER_ID可不传
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午10:36:08 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset getUserID(String user_id, String cust_id, String product_id) throws Exception
	{
		IData param = new DataMap();
		param.put("CUST_ID", cust_id);
		param.put("USER_ID", user_id);
		param.put("PRODUCT_ID", product_id);

		SQLParser parser = new SQLParser(param);
		/*
		 * parser.addSQL("SELECT USER_ID,SERIAL_NUMBER,PRODUCT_ID,CUST_ID FROM TF_F_USER "
		 * ); parser.addSQL(" WHERE remove_tag='0'");
		 * parser.addSQL(" AND PRODUCT_ID=:PRODUCT_ID");
		 * parser.addSQL(" AND CUST_ID=:CUST_ID");
		 * parser.addSQL(" AND USER_ID=:USER_ID");
		 * parser.addSQL(" AND ROWNUM <= 1 ");
		 * parser.addSQL(" ORDER BY USER_ID DESC");
		 */

		parser.addSQL("SELECT A.USER_ID,A.SERIAL_NUMBER,B.PRODUCT_ID,A.CUST_ID ");
		parser.addSQL("FROM TF_F_USER A,TF_F_USER_PRODUCT B ");
		parser.addSQL("WHERE remove_tag='0' ");
		parser.addSQL("AND A.USER_ID = B.USER_ID ");
		parser.addSQL("AND A.PARTITION_ID = B.PARTITION_ID ");
		parser.addSQL("AND B.PRODUCT_ID=:PRODUCT_ID ");
		parser.addSQL("AND A.CUST_ID=:CUST_ID ");
		parser.addSQL("AND A.USER_ID=:USER_ID ");
		parser.addSQL("AND A.PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000) ");
		parser.addSQL("AND ROWNUM <= 1  ");
		parser.addSQL("ORDER BY A.USER_ID DESC ");

		return Dao.qryByParse(parser, Route.CONN_CRM_CG);

	}

	/**
	 * @Function: getUserinfo
	 * @Description: 该函数的功能描述
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午10:37:05 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset getUserinfo(String serial_number) throws Exception
	{

		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);

		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT * ");
		parser.addSQL("FROM TF_F_USER R, TF_F_CUSTOMER C ");
		parser.addSQL("WHERE 1 = 1 ");
		parser.addSQL("AND R.CUST_ID = C.CUST_ID ");
		parser.addSQL("AND C.PARTITION_ID = MOD(R.CUST_ID, 10000) ");
		parser.addSQL("AND R.SERIAL_NUMBER = :SERIAL_NUMBER ");

		return Dao.qryByParse(parser);
	}

	/**
	 * 根据tab_name,sql_ref,eparchy_code查询用户信息
	 * 
	 * @data 2013-7-23
	 * @param parm
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserInfo(IData inparams) throws Exception
	{
		return Dao.qryByCode("TF_F_USER", "SEL_BY_USERID", inparams);
	}

	public static IDataset getUserInfoByAcctTag(String userId, String acctTag) throws Exception
	{
		IData param = new DataMap();
		param.put("VUSER_ID", userId);
		param.put("VACCT_TAG", acctTag);
		return Dao.qryByCode("TF_F_USER", "SEL_BY_ACCT_TAG", param);
	}

	/**
	 * @Function: getUserInfoByCandB
	 * @Description: 根据CUST_ID,BRAND_CODE查询集团的用户信息
	 * @param:参数描述
	 * @return：返回结果描述
	 * @throws：异常描述
	 */
	public static IDataset getUserInfoByCandB(String cust_id, String brand_code, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("CUST_ID", cust_id);
		param.put("BRAND_CODE", brand_code);

		return Dao.qryByCode("TF_F_USER", "SEL_BY_CUSTID_BRANDCODE", param, eparchyCode);
	}

	/**
	 * @Function: getUserInfoByCstId
	 * @Description: 根据CustId查询用户信息
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午10:40:17 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset getUserInfoByCstId(String cust_id, Pagination page) throws Exception
	{
		IData param = new DataMap();
		param.put("CUST_ID", cust_id);

		return Dao.qryByCode("TF_F_USER", "SEL_BY_CUSTID_REMOVETAG", param, page);

	}

	/**
	 * @Function: getUserInfoByCstId
	 * @Description: 根据CustId查询用户信息
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午10:42:00 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset getUserInfoByCstId(String cust_id, String eparchyCode, Pagination page) throws Exception
	{

		// getVisit().setRouteEparchyCode( eparchyCode);
		IData param = new DataMap();
		param.put("CUST_ID", cust_id);

		return Dao.qryByCode("TF_F_USER", "SEL_BY_CUSTID_REMOVETAG", param, page, eparchyCode);
	}

	/**
	 * @Function: getUserInfoByCstIdForGrp
	 * @Description: 从集团库 根据CustId查询用户信
	 * @param:参数描述
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午10:42:19 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset getUserInfoByCstIdForGrp(String cust_id, Pagination page) throws Exception
	{
		return getUserInfoByCstIdForGrpHasPriv(cust_id, "false", page);
	}

	/**
	 * 从集团库 根据CustId查询用户信 支持产品权限过滤
	 * 
	 * @param cust_id
	 * @param privForProduct
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserInfoByCstIdForGrpHasPriv(String cust_id, String privForProduct, Pagination page) throws Exception
	{
		IData param = new DataMap();
		param.put("CUST_ID", cust_id);

		IDataset resultset = Dao.qryByCode("TF_F_USER", "SEL_BY_CUSTID_REMOVETAG", param, page, Route.CONN_CRM_CG);
		if (IDataUtil.isEmpty(resultset))
			return resultset;

		if (StringUtils.isNotBlank(privForProduct) && privForProduct.equals("true"))
		{
			ProductPrivUtil.filterProductListByPriv(CSBizBean.getVisit().getStaffId(), resultset);
		}

		for (int i = 0; i < resultset.size(); i++)
		{
			IData result = resultset.getData(i);
			result.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(result.getString("PRODUCT_ID")));
		}
		return resultset;
	}

	/**
	 * @Function: getUserInfoByCstIdProId
	 * @Description: 根据CustId、ProductId查询用户信息
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午10:43:55 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset getUserInfoByCstIdProId(String cust_id, String product_id, Pagination page) throws Exception
	{
		IData param = new DataMap();
		param.put("CUST_ID", cust_id);
		param.put("PRODUCT_ID", product_id);

		return Dao.qryByCode("TF_F_USER", "SEL_BY_CUSTID_PRODUCTID", param, page);
	}

	/**
	 * @Function: getUserInfoByCstIdProIdForGrp
	 * @Description:从集团库 根据CustId、ProductId查询用户信息
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午10:44:22 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset getUserInfoByCstIdProIdForGrp(String cust_id, String product_id, Pagination page) throws Exception
	{
		IData param = new DataMap();
		param.put("CUST_ID", cust_id);
		param.put("PRODUCT_ID", product_id);

		IDataset resultset = Dao.qryByCode("TF_F_USER", "SEL_BY_CUSTID_PRODUCTID", param, page, Route.CONN_CRM_CG);
		if (IDataUtil.isEmpty(resultset))
			return resultset;

		for (int i = 0; i < resultset.size(); i++)
		{
			IData result = resultset.getData(i);
			result.put("EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(result.getString("EPARCHY_CODE")));
			result.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(result.getString("PRODUCT_ID")));
			result.put("BRAND_NAME", UBrandInfoQry.getBrandNameByBrandCode(result.getString("BRAND_CODE")));
		}
		return resultset;
	}

	/**
	 *GROUP_ID PRODUCT_ID USER_ID 精确查询用户信息
	 * 
	 * @author liuxx3
	 *@date 2014 08-28
	 */
	public static IDataset getUserInfoByCstIdProIdForGrpNew(String cust_id, String product_id, String userId, Pagination page) throws Exception
	{
		IData param = new DataMap();
		param.put("CUST_ID", cust_id);
		param.put("PRODUCT_ID", product_id);
		param.put("USER_ID", userId);

		IDataset resultset = Dao.qryByCodeParser("TF_F_USER", "SEL_BY_CUSTID_PRODUCTID_USERID", param, page, Route.CONN_CRM_CG);
		if (IDataUtil.isEmpty(resultset))
			return resultset;

		for (int i = 0; i < resultset.size(); i++)
		{
			IData result = resultset.getData(i);
			result.put("EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(result.getString("EPARCHY_CODE")));
			result.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(result.getString("PRODUCT_ID")));
			result.put("BRAND_NAME", UBrandInfoQry.getBrandNameByBrandCode(result.getString("BRAND_CODE")));
		}
		return resultset;
	}

	/**
	 * @Function: getUserInfoByCustId
	 * @Description: 根据CUST_ID，查用户信息
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午10:46:08 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset getUserInfoByCustId(String cust_id, String user_diff_code) throws Exception
	{
		IData param = new DataMap();
		param.put("CUST_ID", cust_id);
		param.put("USER_DIFF_CODE", user_diff_code);

		return Dao.qryByCode("TF_F_USER", "SEL_BY_CUST_ID", param);
	}

	/**
	 * @Function: getUserInfoByCustID
	 * @Description: 根据CUST_ID查询集团的用户信息
	 * @param:参数描述
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午10:46:53 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset getUserInfoByCustID(String cust_id, String eparchyCode) throws Exception
	{

		// getVisit().setRouteEparchyCode( eparchyCode);
		IData param = new DataMap();
		param.put("CUST_ID", cust_id);
		IDataset resList = Dao.qryByCode("TF_F_USER", "SEL_BY_CUSTID_REMOVETAG", param, eparchyCode);
		return resList;
	}

	/**
	 * 根据集团客户标识查询失效的用户信息
	 * 
	 * @param custId
	 * @param pg
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserInfoByCustIdEndForGrp(String custId, Pagination pg) throws Exception
	{
		IData param = new DataMap();
		param.put("CUST_ID", custId);

		return Dao.qryByCode("TF_F_USER", "SEL_BY_CUSTID_ENDDATE", param, pg, Route.CONN_CRM_CG);
	}

	/**
	 * @Function: getUserInfoByCusts
	 * @Description: 根据cust_id的集合获取user信息
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午10:47:31 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset getUserInfoByCusts(String cust_id) throws Exception
	{
		IData param = new DataMap();
		param.put("CUST_ID", cust_id);
		return Dao.qryByCode("TF_F_USER", "SEL_BY_CUSTS", param);
	}

	/**
	 * 查询用户UU关系 订购产品等信息
	 * 
	 * @author liuxx3
	 * @date 2014-07-04
	 */
	public static IDataset getUserInfoByGPUID(IData param, Pagination pg) throws Exception
	{
		return Dao.qryByCode("TF_F_USER", "SEL_USERINFO_BY_GPUID", param, pg);
	}

	public static IDataset getUserInfoByMoveTagUserCodeSet(String sn, String codeSet, String removeTag, String limitTime) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", sn);
		param.put("USER_STATE_CODESET", codeSet);
		param.put("LIMIT_TIME", limitTime);
		param.put("REMOVE_TAG", removeTag);
		return Dao.qryByCode("TF_F_USER", "SEL_STOP_BY_USERID", param);
	}

	public static IDataset getUserInfoByNumber(IData inparams) throws Exception
	{
		return Dao.qryByCode("TF_F_USER", "SEL_BY_NUMBER", inparams);
	}

	/**
	 * 根据PSPT_TYPE_CODE,PSPT_ID,EPARCHY_CODE查询已开户用户数
	 * 
	 * @author chenzm
	 * @param pspt_type_code
	 * @param pspt_id
	 * @param eparchy_code
	 * @return IDataset
	 * @throws Exception
	 */
	public static IDataset getUserInfoByPsptEx(String pspt_type_code, String pspt_id, String eparchy_code) throws Exception
	{
		IData inParams = new DataMap();
		inParams.put("PSPT_TYPE_CODE", pspt_type_code);
		inParams.put("PSPT_ID", pspt_id);
		inParams.put("EPARCHY_CODE", eparchy_code);
		return Dao.qryByCode("TF_F_USER", "SEL_USER_NUM_BY_PSPT_EX", inParams);
	}

	/**
	 * @Function: getUserInfoBySerailNumber
	 * @Description: 该函数的功能描述
	 * @param:参数描述
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: lijm3
	 * @date: 2013-7-18 上午10:28:11 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-7-18 lijm3 v1.0.0 修改原因
	 */
	public static IDataset getUserInfoBySerailNumber(String remove_tag, String serial_number) throws Exception
	{
		IData param = new DataMap();
		param.put("REMOVE_TAG", remove_tag);
		param.put("SERIAL_NUMBER", serial_number);

		return Dao.qryByCode("TF_F_USER", "SEL_BY_SNO", param);
	}

	public static IDataset getUserInfoBySerialNumber(String serial_number, String remove_tag, String net_type_code) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);
		param.put("REMOVE_TAG", remove_tag);
		param.put("NET_TYPE_CODE", net_type_code);
		return Dao.qryByCodeParser("TF_F_USER", "SEL_BY_SN1", param);
	}

	/**
	 * 根据SERIAL_NUMBER,REMOVE_TAG,NET_TYPE_CODE,EPARCHY_CODE查询用户信息
	 */
	public static IDataset getUserInfoBySn(IData inparams) throws Exception
	{

		return Dao.qryByCode("TF_F_USER", "SEL_BY_SN_EPARCHYCODE", inparams);
	}

	/**
	 * 根据用户手机号码查询用户资料表，不关联用户产品资料表
	 * 
	 * @param serialnumber
	 * @param removeTag
	 * @return
	 * @throws Exception
	 *             wangjx 2013-7-22
	 */
	public static IDataset getUserInfoBySn(String serialNumber, String removeTag) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("REMOVE_TAG", removeTag);

		return Dao.qryByCode("TF_F_USER", "SEL_USER_BY_SN", param);
	}

	public static IDataset getUserInfoBySn(String serial_number, String remove_tag, String net_type_code) throws Exception
	{

		return getUserInfoBySn(serial_number, remove_tag, net_type_code, null);
	}

	public static IDataset getUserInfoBySn(String serial_number, String remove_tag, String net_type_code, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);
		param.put("REMOVE_TAG", remove_tag);
		param.put("NET_TYPE_CODE", net_type_code);
		return Dao.qryByCodeParser("TF_F_USER", "SEL_BY_SN", param, routeId);
	}

	/**
	 * @Function: getUserInfoBySN
	 * @Description: 据号码查询用户数据根
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: lim
	 * @date: 2013-4-27 上午10:55:04 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 lim v1.0.0 修改原因
	 */
	public static IData getUserInfoBySN(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);

		String routeId = RouteInfoQry.getEparchyCodeBySnForCrm(serialNumber);
		if (StringUtils.isEmpty(routeId))
		{
			return null;
		}

		IData userGrpInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber, Route.CONN_CRM_CG);

		IData retult = null;

		if (IDataUtil.isNotEmpty(userGrpInfo))
		{
			retult = userGrpInfo;

			retult.put("IsGrpSn", "Yes");
		} else
		{
			userGrpInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber, routeId);

			if (IDataUtil.isEmpty(userGrpInfo))
			{
				return null;
			}

			retult = userGrpInfo;

		}

		return retult;
	}

	public static IDataset getUserInfoBySN(String serial_number, String remove_tag, String net_type_code) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);
		param.put("REMOVE_TAG", remove_tag);
		param.put("NET_TYPE_CODE", net_type_code);
		return Dao.qryByCode("TF_F_USER", "SEL_BY_SN", param);
	}

	/**
	 * 获取用户资料信息
	 * 
	 * @param pd
	 * @param inparams
	 * @return
	 * @throws Exception
	 * @author:chenzg
	 * @date:2010-10-15
	 */
	public static IData getUserInfoBySn2(IData inparams) throws Exception
	{
		inparams.put("NET_TYPE_CODE", "00");
		inparams.put("REMOVE_TAG", "0");
		IDataset dataset = Dao.qryByCode("TF_F_USER", "SEL_BY_SN", inparams);
		return IDataUtil.isNotEmpty(dataset) ? dataset.getData(0) : new DataMap();
	}

	/**
	 * 根据SERIAL_NUMBER 查询
	 * 
	 * @data 2013-7-23
	 * @param parm
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserInfoBySn3(IData param) throws Exception
	{

		return Dao.qryByCode("TF_F_USER", "SEL_BAD_USER_BY_SN", param);
	}

	public static IDataset getUserInfoBySnAll(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("REMOVE_TAG", "0");

		IDataset dataset = Dao.qryByCodeAllCrm("TF_F_USER", "SEL_BAD_USER_BY_SN_NEW", param, true);

		if (IDataUtil.isEmpty(dataset))
		{
			return null;
		}

		for (int i = 0, size = dataset.size(); i < size; i++)
		{
			IData userInfo = dataset.getData(i);

			userInfo.put("EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(userInfo.getString("EPARCHY_CODE")));
			userInfo.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(userInfo.getString("PRODUCT_ID")));
			userInfo.put("BRAND_NAME", UBrandInfoQry.getBrandNameByBrandCode(userInfo.getString("BRAND_CODE")));
		}

		return dataset;
	}

	/**
	 * @Function: getUserInfoBySnCrmOneDbR
	 * @Description: 该函数的功能描述
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午10:57:59 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset getUserInfoBySnCrmOneDb(String serial_number, String remove_tag, String net_type_code) throws Exception
	{

		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);
		param.put("REMOVE_TAG", remove_tag);
		param.put("NET_TYPE_CODE", net_type_code);

		IDataset userInfos = new DatasetList();
		userInfos = getUserInfoBySn(serial_number, remove_tag, net_type_code);
		if (IDataUtil.isEmpty(userInfos))
		{
			userInfos = Dao.qryByCodeAllCrm("TF_F_USER", "SEL_BY_SN", param, false);
		}

		return userInfos;
	}

	/**
	 * @Function: getUserInfoBySnDestroyAll
	 * @Description: 根据SERIAL_NUMBER查询取所有非正常用户信息
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午10:58:34 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset getUserInfoBySnDestroyAll(String serial_number, Pagination page) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);

		return Dao.qryByCode("TF_F_USER", "SEL_BY_SN_DESTROY_ALL", param, page);
	}

	public static IDataset getUserInfoBySnNetTypeCode(String serial_number, String remove_tag, String net_type_code) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);
		param.put("REMOVE_TAG", remove_tag);
		param.put("NET_TYPE_CODE", net_type_code);
		return Dao.qryByCodeParser("TF_F_USER", "SEL_BY_SN_NETTYPECODE", param);
	}

	public static IDataset getUserInfoBySnNetTypeCode2(String serial_number, String remove_tag) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);
		param.put("REMOVE_TAG", remove_tag);
		return Dao.qryByCodeParser("TF_F_USER", "SEL_BY_SN_NETTYPECODE2", param);
	}

	public static IDataset getUserInfoBySNOpenDate(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);

		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_SNOPENDATE", param);
	}

	/**
	 * @Function: getUserInfoBySnPidForGrp
	 * @Description: 从集团库 根据SERIAL_NUMBER,REMOVE_TAG,NET_TYPE_CODE查询集团用户信息
	 * @param:参数描述
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午11:00:53 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset getUserInfoBySnPidForGrp(String serial_number) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT  ");
		parser.addSQL(" to_char(USER_ID) USER_ID ,to_char(CUST_ID) CUST_ID ");
		parser.addSQL(" FROM tf_f_user t ");
		parser.addSQL(" WHERE 1 = 1 ");
		parser.addSQL(" AND t.serial_number = :SERIAL_NUMBER ");
		parser.addSQL(" AND t.remove_tag = '0' ");
		parser.addSQL(" AND EXISTS (SELECT 1 ");
		parser.addSQL(" FROM TD_B_PRODUCT p ");
		parser.addSQL(" WHERE p.PRODUCT_ID = t.PRODUCT_ID ");
		parser.addSQL(" AND p.PRODUCT_MODE IN (10, 11)) ");
		return Dao.qryByParse(parser, Route.CONN_CRM_CG);
	}

	public static IDataset getUserInfoByUserCodeSet(String sn, String codeSet) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", sn);
		param.put("USER_STATE_CODESET", codeSet);
		return Dao.qryByCode("TF_F_USER", "SEL_BY_CODESET", param);
	}

	/**
	 * @Function: getGrpUserInfoByUserId
	 * @Description: 根据用户USER_ID查询用户信息
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午10:10:00 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset getUserInfoByUserId(String user_id, String remove_tag, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("REMOVE_TAG", remove_tag);

		return Dao.qryByCode("TF_F_USER", "SEL_BY_USERID", param, eparchyCode);
	}

	public static IDataset getUserInfoByUserIdCodeState(String userId, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("USER_STATE_CODESET", "0");

		return Dao.qryByCode("TF_F_USER", "SEL_BY_USERID_STATE_K", param, routeId);
	}

	/**
	 * 根据用户ID查询用户资料表，不关联用户产品资料表
	 * 
	 * @param userId
	 * @param removeTag
	 * @return
	 * @throws Exception
	 *             wangjx 2013-7-22
	 */
	public static IDataset getUserInfoByUserIdTag(String userId, String removeTag) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("REMOVE_TAG", removeTag);

		return Dao.qryByCode("TF_F_USER", "SEL_USER_BY_USERIDTAG", param, Route.CONN_CRM_CG);
	}

	public static IDataset getUserInfoByUserTagSetSn(String serial_number, String userTagSet) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);
		param.put("USER_TAG_SET", userTagSet);
		return Dao.qryByCode("TF_F_USER", "SEL_BY_SN_USERTAGSET", param);
	}

	/**
	 * 获取用户重要信息异动信息
	 * 
	 * @param user_id
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserInfoChgByUserId(String user_id) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);

		IDataset iDataset = Dao.qryByCode("TF_F_USER_INFOCHANGE", "SEL_BY_USERID", param);
		return iDataset;
	}

	public static IDataset getUserInfoChgByUserIdCurvalid(String user_id) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);

		IDataset iDataset = Dao.qryByCode("TF_F_USER_INFOCHANGE", "SEL_BY_USERID_CURVALID", param);
		return iDataset;
	}

	public static IDataset getUserInfoChgByUserIdNxtvalid(String user_id) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);

		IDataset iDataset = Dao.qryByCode("TF_F_USER_INFOCHANGE", "SEL_BY_USERID_NXTVALID", param);
		return iDataset;
	}

	public static IDataset getUserInfoForPayMent(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("REMOVE_TAG", "0");
		param.put("NET_TYPE_CODE", "00");
		param.put("SERIAL_NUMBER", serialNumber);
		return Dao.qryByCode("TF_F_USER", "SEL_BY_SN", param);
	}

	/**
	 * todo code_code 表里没有 根据CustId、ProductId查询用户信息
	 * 
	 * @author xiajj
	 */
	public static IDataset getUserInfoForVpn(IData inparams, Pagination page) throws Exception
	{

		return Dao.qryByCode("TF_F_USER", "SEL_BY_CUSTID_PRODUCTID_2", inparams, page);
	}

	public static IDataset getUserInfoLastStop(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		SQLParser parser = new SQLParser(param);

		parser.addSQL(" SELECT A.SERIAL_NUMBER,a.USER_PASSWD,a.CUST_ID,A.USER_ID,A.USER_STATE_CODESET,to_char(A.LAST_STOP_TIME,'yyyyMMddhh24miss') STATUS_CHG_TIME FROM TF_F_USER A WHERE 1=1 ");
		parser.addSQL("     AND a.serial_number=:SERIAL_NUMBER ");
		parser.addSQL("     AND A.LAST_STOP_TIME = (SELECT MAX(B.LAST_STOP_TIME) FROM TF_F_USER B WHERE B.SERIAL_NUMBER=:SERIAL_NUMBER ) ");
		return Dao.qryByParse(parser);
	}

	public static IDataset getUserInfosByCodeSet(String serial_number, String remove_tag, String user_state_codeset) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);
		param.put("REMOVE_TAG", remove_tag);
		param.put("USER_STATE_CODESET", user_state_codeset);
		return Dao.qryByCode("TF_F_USER", "SEL_BY_SN_CODESET", param);
	}

	public static IDataset getUserInfoVpmnSnByCPB(String custId, String productId, String brandCode) throws Exception
	{
		IData param = new DataMap();
		param.put("CUST_ID", custId);
		param.put("PRODUCT_ID", productId);
		param.put("BRAND_CODE", brandCode);
		return Dao.qryByCodeParser("TF_F_USER", "SEL_VPMNSN_BY_CPB", param);
	}

	public static IDataset getUserMaxLastStopBySn(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		return Dao.qryByCode("TF_F_USER", "SEL_MAX_LASTSTOP_BYSN", param);
	}

	public static IDataset getUserOcsByUserId(String user_id, Pagination page) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		return Dao.qryByCode("TF_F_USER_OCS", "SEL_OCSSTATE_BY_USERID", param, page);
	}

	/**
	 * @Function: getUserOpenNumByPsptId
	 * @Description: 根据证件号码获取开户个数
	 * @param: @param psptId
	 * @param: @param eparchyCode
	 * @param: @param rowNum
	 * @param: @return
	 * @param: @throws Exception
	 * @return：int
	 * @throws：
	 * @version: v1.0.0
	 * @author: Administrator
	 * @date: 下午02:08:32 2013-9-21 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-9-21 longtian3 v1.0.0 TODO:
	 */
	public static int getUserOpenNumByPsptId(String psptId, String eparchyCode, String rowNum, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("PSPT_ID", psptId);
		param.put("EPARCHY_CODE", eparchyCode);
		param.put("ROWNUM", rowNum);

		IDataset ids = Dao.qryByCode("TF_F_USER", "SEL_USER_NUM_BY_PSPTNEW", param, routeId);

		return ids.getData(0).getInt("OPEN_NUM");
	}

	/**
	 * @Function: getUserPProduct
	 * @Description: 从集团库 根据产品类型查询用户订购了那些类型的产品
	 * @param:参数描述
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午11:11:25 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset getUserPProduct(String parent_ptype_code, String trade_staff_id, String cust_id) throws Exception
	{

		IData param = new DataMap();
		param.put("PARENT_PTYPE_CODE", parent_ptype_code);
		param.put("TRADE_STAFF_ID", trade_staff_id);
		param.put("CUST_ID", cust_id);

		IDataset userProductList = Dao.qryByCode("TF_F_USER", "SEL_BY_CUSTID_PRODUCT_TYPE", param, Route.CONN_CRM_CG);

		// 根据权限过滤产品
		// ProductPrivUtil.filterProductListByPriv(
		// trade_staff_id,userProductList);

		IDataset dataset = GroupProductUtil.getProductTypeCodeAndName(userProductList);

		return dataset;

	}

	/**
	 * @Function: getUserProduct
	 * @Description: 从集团库 根据产品类型查询用户订购了那些产品
	 * @param:参数描述
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午11:14:15 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset getUserProduct(String vpnFlag, String trade_staff_id, String cust_id) throws Exception
	{

		// TODO data.put("TRADE_STAFF_ID", getVisit().getStaffId());

		IData param = new DataMap();
		param.put("TRADE_STAFF_ID", trade_staff_id);
		param.put("CUST_ID", cust_id);

		if ("VPCN_VPMN".equals(vpnFlag))
		{
			param.put("PRODUCT_VPMN", "8000");
			param.put("PRODUCT_VPCN", "8010");
			return Dao.qryByCode("TF_F_USER", "SEL_BY_PRODUCT_TYPE_VPN", param, Route.CONN_CRM_CG);
		}
		return Dao.qryByCode("TF_F_USER", "SEL_BY_PRODUCT_TYPE", param, Route.CONN_CRM_CG);
	}

	/**
	 * @Function: getUserProductBySN
	 * @Description: 从集团库
	 * @param:参数描述
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午11:15:44 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset getUserProductBySN(String user_id, String trade_staff_id) throws Exception
	{

		// TODO data.put("TRADE_STAFF_ID", getVisit().getStaffId());

		IData param = new DataMap();
		param.put("TRADE_STAFF_ID", trade_staff_id);
		param.put("USER_ID", user_id);

		return Dao.qryByCode("TF_F_USER", "SEL_BY_PRODUCT_TYPE_SN", param, Route.CONN_CRM_CG);
	}

	/**
	 * 根据产品类型查询用户订购了那些产品
	 * 
	 * @param data
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserProductByType(String parentTypeCode, String custid, String staffid) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("TRADE_STAFF_ID", staffid);
		inparam.put("CUST_ID", custid);
		inparam.put("PRODUCT_TYPE_CODE", parentTypeCode);

		IDataset userProductList = Dao.qryByCode("TF_F_USER", "SEL_BY_CUSTID_PRODUCT_TYPE", inparam, Route.CONN_CRM_CG);

		// 根据权限过滤产品
		// ProductPrivUtil.filterProductListByPriv( staffid,userProductList);

		// 根据产品类型过滤产品
		GroupProductUtil.filterProductByProductTypeCode(userProductList, parentTypeCode);

		return userProductList;

	}

	/**
	 * TODO code_code表里没有 根据营销产品类型查询用户订购了那些产品
	 * 
	 * @param data
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserProductSale(String cust_id, String trade_staff_id) throws Exception
	{
		IData data = new DataMap();
		data.put("CUST_ID", cust_id);
		data.put("TRADE_STAFF_ID", trade_staff_id);

		return Dao.qryByCode("TF_F_USER", "SEL_BY_PRODUCT_TYPE_SALE", data, Route.CONN_CRM_CG);
	}

	public static IDataset getUserPrsinfoByuserid(String user_id, String strRouteEparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);

		return Dao.qryByCode("TF_F_USER", "SEL_USER_PRSINFO_BYUSERID", param, strRouteEparchyCode);
	}

	public static IDataset getUserResInfoByUserId(String userId, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_RES", "SEL_FOR_SIMCARD", param, routeId);
	}

	public static IDataset getUserSaleActive(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_ALL_BY_USERID", param);
	}

	/**
	 * @Function: getUsersBySn
	 * @Description: 该函数的功能描述
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午11:17:16 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset getUsersBySn(String serial_number) throws Exception
	{

		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);

		return Dao.qryByCode("TF_F_USER", "SEL_USERS_BY_SN", param);
	}

	/**
	 * 查询用户状态信息
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserStateInfo(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		return Dao.qryByCode("TF_F_USER", "SEL_BY_USERID_STATE", param);
	}

	public static IDataset getUserWapSession(String serialNumber, String sessionId) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("SESSION_ID", sessionId);
		return Dao.qryByCode("TF_B_WAP_SESSION", "SEL_CHECK_USER_VOUCHER", param);
	}

	public static IDataset getUserWholeinfo(IData param) throws Exception
	{

		return Dao.qryByCode("TF_F_USER_WHOLEINFO", "SEL_BY_SN_HNAN", param);
	}

	/**
	 * @param simCardNo
	 * @return
	 * @throws Exception
	 * @CREATE BY GONGP@2014-6-27
	 */
	public static IDataset getValidUserInfoByResCode(String simCardNo) throws Exception
	{

		IData param = new DataMap();
		param.put("RES_TYPE_CODE", "1");
		param.put("RES_CODE", simCardNo);

		return Dao.qryByCode("TF_F_USER", "SEL_BY_RESCODE_VALIDUSER", param);

	}

	/**
	 * @Function: getVirtualUserInfoByCusts
	 * @Description: 根据cust_id的集合获取组合产品的虚拟user信息
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午11:18:10 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset getVirtualUserInfoByCusts(String cust_id) throws Exception
	{

		IData param = new DataMap();
		param.put("CUST_ID", cust_id);

		return Dao.qryByCode("TF_F_USER", "SEL_VIRTUAL_BY_CUSTS", param);
	}

	public static IDataset getWideUserOpenAfter25(String serialNumber, String removeTag) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("REMOVE_TAG", removeTag);
		return Dao.qryByCode("TF_F_USER", "SEL_WIDENET_USER_25", param);
	}

	public static IDataset getNetTvUserOpenAfter25(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_NET_TV_USER_25_USERID", param);
	}

	public static IDataset getWideUsersBySerialNumber(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", "KD_" + serialNumber);
		return Dao.qryByCode("TF_F_USER", "SEL_WIDEUSER_BY_SN_KD", param);
	}

	/**
	 * 根据custId在历史用户资料表查询用户信息
	 * 
	 * @param custId
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryAllUserInfoByCustIdFromHis(String custId) throws Exception
	{
		return qryAllUserInfoByCustIdFromHis(custId, null);
	}

	/**
	 * 根据custId、routeId在历史用户资料表查询用户信息
	 * 
	 * @param custId
	 * @param routeId
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryAllUserInfoByCustIdFromHis(String custId, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("CUST_ID", custId);

		StringBuilder sql = new StringBuilder(2500);

		sql.append("SELECT U.PARTITION_ID, TO_CHAR(U.USER_ID) USER_ID, TO_CHAR(CUST_ID) CUST_ID, ");
		sql.append("TO_CHAR(USECUST_ID) USECUST_ID, EPARCHY_CODE, CITY_CODE, CITY_CODE_A, ");
		sql.append("USER_PASSWD, USER_DIFF_CODE, USER_TYPE_CODE, USER_TAG_SET, ");
		sql.append("USER_STATE_CODESET, NET_TYPE_CODE, SERIAL_NUMBER, CONTRACT_ID, ACCT_TAG, ");
		sql.append("PREPAY_TAG, MPUTE_MONTH_FEE, ");
		sql.append("TO_CHAR(MPUTE_DATE, 'yyyy-mm-dd hh24:mi:ss') MPUTE_DATE, ");
		sql.append("TO_CHAR(FIRST_CALL_TIME, 'yyyy-mm-dd hh24:mi:ss') FIRST_CALL_TIME, ");
		sql.append("TO_CHAR(LAST_STOP_TIME, 'yyyy-mm-dd hh24:mi:ss') LAST_STOP_TIME, ");
		sql.append("TO_CHAR(CHANGEUSER_DATE, 'yyyy-mm-dd hh24:mi:ss') CHANGEUSER_DATE, ");
		sql.append("IN_NET_MODE, TO_CHAR(IN_DATE, 'yyyy-mm-dd hh24:mi:ss') IN_DATE, ");
		sql.append("IN_STAFF_ID, IN_DEPART_ID, OPEN_MODE, ");
		sql.append("TO_CHAR(OPEN_DATE, 'yyyy-mm-dd hh24:mi:ss') OPEN_DATE, OPEN_STAFF_ID, ");
		sql.append("OPEN_DEPART_ID, DEVELOP_STAFF_ID, ");
		sql.append("TO_CHAR(DEVELOP_DATE, 'yyyy-mm-dd hh24:mi:ss') DEVELOP_DATE, ");
		sql.append("DEVELOP_DEPART_ID, DEVELOP_CITY_CODE, DEVELOP_EPARCHY_CODE, DEVELOP_NO, ");
		sql.append("TO_CHAR(ASSURE_CUST_ID) ASSURE_CUST_ID, ASSURE_TYPE_CODE, ");
		sql.append("TO_CHAR(ASSURE_DATE, 'yyyy-mm-dd hh24:mi:ss') ASSURE_DATE, REMOVE_TAG, ");
		sql.append("TO_CHAR(PRE_DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') PRE_DESTROY_TIME, ");
		sql.append("TO_CHAR(DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') DESTROY_TIME, ");
		sql.append("REMOVE_EPARCHY_CODE, REMOVE_CITY_CODE, REMOVE_DEPART_ID, ");
		sql.append("REMOVE_REASON_CODE, ");
		sql.append("TO_CHAR(U.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
		sql.append("U.UPDATE_STAFF_ID, U.UPDATE_DEPART_ID, U.REMARK, U.RSRV_NUM1, U.RSRV_NUM2, ");
		sql.append("U.RSRV_NUM3, TO_CHAR(U.RSRV_NUM4) RSRV_NUM4, ");
		sql.append("TO_CHAR(U.RSRV_NUM5) RSRV_NUM5, U.RSRV_STR1, U.RSRV_STR2, U.RSRV_STR3, ");
		sql.append("U.RSRV_STR4, U.RSRV_STR5, U.RSRV_STR6, RSRV_STR7, RSRV_STR8, RSRV_STR9, ");
		sql.append("RSRV_STR10, TO_CHAR(U.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
		sql.append("TO_CHAR(U.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
		sql.append("TO_CHAR(U.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, U.RSRV_TAG1, ");
		sql.append("U.RSRV_TAG2, U.RSRV_TAG3 ");
		sql.append("FROM TF_FH_USER U ");// 销户用户表
		sql.append("WHERE U.CUST_ID = :CUST_ID ");
		IDataset ids = Dao.qryBySql(sql, param, routeId);
		return ids;
	}

	/**
	 * 根据User_id、routeId在历史用户资料表查询用户信息
	 * 
	 * @param userId
	 * @param routeId
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryAllUserInfoBySnFromHis(String serialNumber) throws Exception
	{

		return qryAllUserInfoBySnFromHis(serialNumber, null);
	}

	/**
	 * 根据User_id、routeId在历史用户资料表查询用户信息
	 * 
	 * @param userId
	 * @param routeId
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryAllUserInfoBySnFromHis(String serialNumber, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);

		StringBuilder sql = new StringBuilder(2500);

		sql.append("SELECT U.PARTITION_ID, TO_CHAR(U.USER_ID) USER_ID, TO_CHAR(CUST_ID) CUST_ID, ");
		sql.append("TO_CHAR(USECUST_ID) USECUST_ID, EPARCHY_CODE, CITY_CODE, CITY_CODE_A, ");
		sql.append("USER_PASSWD, USER_DIFF_CODE, USER_TYPE_CODE, USER_TAG_SET, ");
		sql.append("USER_STATE_CODESET, NET_TYPE_CODE, SERIAL_NUMBER, CONTRACT_ID, ACCT_TAG, ");
		sql.append("PREPAY_TAG, MPUTE_MONTH_FEE, ");
		sql.append("TO_CHAR(MPUTE_DATE, 'yyyy-mm-dd hh24:mi:ss') MPUTE_DATE, ");
		sql.append("TO_CHAR(FIRST_CALL_TIME, 'yyyy-mm-dd hh24:mi:ss') FIRST_CALL_TIME, ");
		sql.append("TO_CHAR(LAST_STOP_TIME, 'yyyy-mm-dd hh24:mi:ss') LAST_STOP_TIME, ");
		sql.append("TO_CHAR(CHANGEUSER_DATE, 'yyyy-mm-dd hh24:mi:ss') CHANGEUSER_DATE, ");
		sql.append("IN_NET_MODE, TO_CHAR(IN_DATE, 'yyyy-mm-dd hh24:mi:ss') IN_DATE, ");
		sql.append("IN_STAFF_ID, IN_DEPART_ID, OPEN_MODE, ");
		sql.append("TO_CHAR(OPEN_DATE, 'yyyy-mm-dd hh24:mi:ss') OPEN_DATE, OPEN_STAFF_ID, ");
		sql.append("OPEN_DEPART_ID, DEVELOP_STAFF_ID, ");
		sql.append("TO_CHAR(DEVELOP_DATE, 'yyyy-mm-dd hh24:mi:ss') DEVELOP_DATE, ");
		sql.append("DEVELOP_DEPART_ID, DEVELOP_CITY_CODE, DEVELOP_EPARCHY_CODE, DEVELOP_NO, ");
		sql.append("TO_CHAR(ASSURE_CUST_ID) ASSURE_CUST_ID, ASSURE_TYPE_CODE, ");
		sql.append("TO_CHAR(ASSURE_DATE, 'yyyy-mm-dd hh24:mi:ss') ASSURE_DATE, REMOVE_TAG, ");
		sql.append("TO_CHAR(PRE_DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') PRE_DESTROY_TIME, ");
		sql.append("TO_CHAR(DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') DESTROY_TIME, ");
		sql.append("REMOVE_EPARCHY_CODE, REMOVE_CITY_CODE, REMOVE_DEPART_ID, ");
		sql.append("REMOVE_REASON_CODE, ");
		sql.append("TO_CHAR(U.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
		sql.append("U.UPDATE_STAFF_ID, U.UPDATE_DEPART_ID, U.REMARK, U.RSRV_NUM1, U.RSRV_NUM2, ");
		sql.append("U.RSRV_NUM3, TO_CHAR(U.RSRV_NUM4) RSRV_NUM4, ");
		sql.append("TO_CHAR(U.RSRV_NUM5) RSRV_NUM5, U.RSRV_STR1, U.RSRV_STR2, U.RSRV_STR3, ");
		sql.append("U.RSRV_STR4, U.RSRV_STR5, U.RSRV_STR6, RSRV_STR7, RSRV_STR8, RSRV_STR9, ");
		sql.append("RSRV_STR10, TO_CHAR(U.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
		sql.append("TO_CHAR(U.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
		sql.append("TO_CHAR(U.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, U.RSRV_TAG1, ");
		sql.append("U.RSRV_TAG2, U.RSRV_TAG3 ");
		sql.append("FROM TF_FH_USER U ");// 销户用户表
		sql.append("WHERE U.SERIAL_NUMBER = :SERIAL_NUMBER ");
		sql.append(" ORDER BY OPEN_DATE desc");

		IDataset ids = Dao.qryBySql(sql, param, routeId);
		return ids;
	}

	/**
	 * @Function: qryBBOSSMemPayPlan
	 * @Description: 获取集团成员BBOSS成员产品优惠
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午11:20:25 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset qryBBOSSMemPayPlan(String group_id, String product_id) throws Exception
	{

		IData param = new DataMap();
		param.put("GROUP_ID", group_id);
		param.put("PRODUCT_ID", product_id);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT pl.*   ");
		parser.addSQL(" FROM tf_f_user u ,tf_f_cust_group p,tf_f_user_payplan pl  ");
		parser.addSQL(" WHERE u.cust_id=p.cust_id and u.remove_tag='0' and u.user_id=pl.user_id  ");
		parser.addSQL("       and u.brand_code='BOSG' ");
		parser.addSQL("       and p.group_id=:GROUP_ID ");
		parser.addSQL("       and u.product_id=:PRODUCT_ID ");
		return Dao.qryByParse(parser);
	}

	/**
	 * 查询用户归属城市
	 * 
	 * @param userId
	 * @param userPwd
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryCityInfoByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCodeParser("TF_F_USER_CITY", "SEL_BY_USERID", param);
	}

	public static IDataset qryGrpAdcUserByCustId(String custId, Pagination pg) throws Exception
	{
		IData param = new DataMap();

		param.put("CUST_ID", custId);

		return Dao.qryByCode("TF_F_USER", "SEL_ADC_USER_BY_CUSTID", param, pg, Route.CONN_CRM_CG);
	}

	/**
	 * 查询集团用户信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryGrpUserInfoByGroupIdProdIdSN(String groupId, String productId, String serialNumber, String netTypeCode, String removeTag) throws Exception
	{
		IData param = new DataMap();
		param.put("GROUP_ID", groupId);
		param.put("PRODUCT_ID", productId);
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("NET_TYPE_CODE", netTypeCode);
		param.put("REMOVE_TAG", removeTag);
		return Dao.qryByCodeParser("TF_F_USER", "SEL_GRP_USER_BY_GROUPID_PRODID_SN", param, Route.CONN_CRM_CG);
	}

	public static IDataset qryPower100MemberProdInfosByUserIdA(IData inparam, Pagination pagination) throws Exception
	{
		IDataset dataset = Dao.qryByCode("TF_F_USER", "SEL_POWER100_MEMPRODUCTS_BY_USERIDA", inparam, pagination);
		if (IDataUtil.isNotEmpty(dataset))
		{
			for (int i = 0; i < dataset.size(); i++)
			{
				IData data = dataset.getData(i);
				String productName = UProductInfoQry.getProductNameByProductId(data.getString("PRODUCT_ID"));
				data.put("PRODUCT_NAME", productName);
			}
		}
		return dataset;
	}

	/**
	 * @Function: qrySnCn
	 * @Description: 该函数的功能描述
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午11:22:25 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset qrySnCn(String user_id, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select p.cust_name,u.* ");
		parser.addSQL(" from tf_f_user u,tf_f_cust_person p ");
		parser.addSQL(" where u.cust_id=p.cust_id and rownum <2 ");
		parser.addSQL(" and u.user_id = :USER_ID ");
		return Dao.qryByParse(parser, pagination);
	}

	/**
	 * 根据客户编码查询用户和主产品信息
	 * 
	 * @param custId
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryUserAndProductByCustIdForGrp(String custId, Pagination pg) throws Exception
	{
		IData param = new DataMap();
		param.put("CUST_ID", custId);

		return Dao.qryByCode("TF_F_USER", "SEL_USER_PRODUCT_BY_CUSTID", param, pg, Route.CONN_CRM_CG);
	}

	/**
	 * 根据用户编码查询用户和主产品信息
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryUserAndProductByUserIdForGrp(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		return Dao.qryByCode("TF_F_USER", "SEL_USER_PRODUCT_BY_USERID", param, Route.CONN_CRM_CG);
	}

	/**
	 * @Function: qryUserByGroupIdAndProductId
	 * @Description: 根据group_id,product_id 查询用户信息
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午11:25:26 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset qryUserByGroupIdAndProductId(String group_id, String product_id) throws Exception
	{
		IData param = new DataMap();
		param.put("GROUP_ID", group_id);
		param.put("PRODUCT_ID", product_id);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select u.user_id,u.product_id from tf_f_user u ,tf_f_cust_group g ");
		parser.addSQL(" where u.cust_id = g.cust_id and g.group_id = :GROUP_ID and u.product_id = :PRODUCT_ID and u.remove_tag = '0'");

		return Dao.qryByParse(parser);

	}

	public static IDataset qryUserByGroupIdAndProductIdForGrp(String group_id, String product_id) throws Exception
	{
		IData param = new DataMap();
		param.put("GROUP_ID", group_id);
		param.put("PRODUCT_ID", product_id);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select u.user_id,p.product_id from tf_f_user u ,tf_f_cust_group g ,tf_f_user_product p");
		parser.addSQL(" where u.cust_id = g.cust_id and g.group_id = :GROUP_ID and p.product_id = :PRODUCT_ID and u.remove_tag = '0'");
		parser.addSQL(" and u.user_id = p.user_id and p.partition_id = mod(u.user_id,10000) and p.main_tag = '1' ");
		return Dao.qryByParse(parser, Route.CONN_CRM_CG);

	}

	/**
	 * @Function: qryUserBySerialNumber
	 * @Description: 根据服务代码获取用户信息
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午11:26:20 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset qryUserBySerialNumber(String serial_number) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);

		SQLParser uparser = new SQLParser(param);
		uparser.addSQL("SELECT U.USER_ID, C.CUST_NAME MEM_CUST_NAME, P.PRODUCT_NAME MEM_PRODUCT_NAME ");
		uparser.addSQL("  FROM TF_F_CUST_PERSON C, TF_F_USER U, TD_B_PRODUCT P ");
		uparser.addSQL(" WHERE C.CUST_ID = U.CUST_ID ");
		uparser.addSQL("   AND U.PRODUCT_ID = P.PRODUCT_ID ");
		uparser.addSQL("   AND U.SERIAL_NUMBER = :SERIAL_NUMBER ");
		uparser.addSQL("   AND U.REMOVE_TAG = '0' ");
		return Dao.qryByParse(uparser);
	}

	/**
	 * @param pd
	 * @param data
	 * @return
	 * @throws Exception
	 * @author liaolc
	 */
	public static IDataset qryUserInfo(IData data) throws Exception
	{

		SQLParser parser = new SQLParser(data);
		parser.addSQL("SELECT U.USER_ID,U.CUST_ID,G.GROUP_ID,P.PRODUCT_ID,P.BRAND_CODE,U.EPARCHY_CODE FROM TF_F_USER U,TF_F_USER_PRODUCT P,TF_F_CUST_GROUP G WHERE 1=1");
		parser.addSQL(" AND U.CUST_ID = G.CUST_ID");
		parser.addSQL(" AND P.PARTITION_ID =U.PARTITION_ID");
		parser.addSQL(" AND  P.USER_ID = U.USER_ID");
		parser.addSQL(" AND P.MAIN_TAG = '1'");
		parser.addSQL(" AND U.REMOVE_TAG = '0'");
		parser.addSQL(" AND P.PRODUCT_ID = :PRODUCT_ID");
		parser.addSQL(" AND G.GROUP_ID = :GROUP_ID");
		parser.addSQL(" AND SYSDATE BETWEEN P.START_DATE AND P.END_DATE");
		return Dao.qryByParse(parser, Route.CONN_CRM_CG);
	}

	/**
	 * @Function: qryUserInfo
	 * @Description: 集团信息查询
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午11:28:03 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IData qryUserInfo(String serial_number, String cust_id, String product_id) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);
		param.put("CUST_ID", cust_id);
		param.put("PRODUCT_ID", product_id);

		IDataset dataset = Dao.qryByCode("TF_F_USER", "SEL_BY_CUSTID_PRDID_SERNUM", param);
		return dataset.size() > 0 ? (IData) dataset.get(0) : null;
	}

	/**
	 * 根据集团[CUST_ID]查询集团用户信息
	 * 
	 * @param custId
	 * @param pg
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryUserInfoByCustIdForGrp(String custId, Pagination pg) throws Exception
	{
		IData param = new DataMap();

		param.put("CUST_ID", custId);

		return Dao.qryByCode("TF_F_USER", "SEL_BY_CUSTID_REMOVETAG", param, pg, Route.CONN_CRM_CG);
	}

	/**
	 * 根据custId、productId、userId获取用户信息
	 * 
	 * @param custId
	 * @param productId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryUserInfoByCustIdProdIdUserIdLike(String custId, String productId, String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("CUST_ID", custId);
		param.put("PRODUCT_ID", productId);
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER", "SEL_BY_CUSTID_PRODUCTID_LIKE", param, Route.CONN_CRM_CG);
	}

	public static IDataset qryUserInfoByCusts(String custId) throws Exception
	{
		IData param = new DataMap();
		param.put("CUST_ID", custId);
		StringBuilder sb = new StringBuilder();

		sb.append(" SELECT PARTITION_ID,																													");
		sb.append("        TO_CHAR(USER_ID) USER_ID,                                              ");
		sb.append("        TO_CHAR(CUST_ID) CUST_ID,                                              ");
		sb.append("        TO_CHAR(USECUST_ID) USECUST_ID,                                        ");
		sb.append("        EPARCHY_CODE,                                                          ");
		sb.append("        CITY_CODE,                                                             ");
		sb.append("        CITY_CODE_A,                                                           ");
		sb.append("        USER_PASSWD,                                                           ");
		sb.append("        USER_DIFF_CODE,                                                        ");
		sb.append("        USER_TYPE_CODE,                                                        ");
		sb.append("        USER_TAG_SET,                                                          ");
		sb.append("        USER_STATE_CODESET,                                                    ");
		sb.append("        NET_TYPE_CODE,                                                         ");
		sb.append("        SERIAL_NUMBER,                                                         ");
		sb.append("        CONTRACT_ID,                                                           ");
		sb.append("        ACCT_TAG,                                                              ");
		sb.append("        PREPAY_TAG,                                                            ");
		sb.append("        MPUTE_MONTH_FEE,                                                       ");
		sb.append("        TO_CHAR(MPUTE_DATE, 'yyyy-mm-dd hh24:mi:ss') MPUTE_DATE,               ");
		sb.append("        TO_CHAR(FIRST_CALL_TIME, 'yyyy-mm-dd hh24:mi:ss') FIRST_CALL_TIME,     ");
		sb.append("        TO_CHAR(LAST_STOP_TIME, 'yyyy-mm-dd hh24:mi:ss') LAST_STOP_TIME,       ");
		sb.append("        TO_CHAR(CHANGEUSER_DATE, 'yyyy-mm-dd hh24:mi:ss') CHANGEUSER_DATE,     ");
		sb.append("        IN_NET_MODE,                                                           ");
		sb.append("        TO_CHAR(IN_DATE, 'yyyy-mm-dd hh24:mi:ss') IN_DATE,                     ");
		sb.append("        IN_STAFF_ID,                                                           ");
		sb.append("        IN_DEPART_ID,                                                          ");
		sb.append("        OPEN_MODE,                                                             ");
		sb.append("        TO_CHAR(OPEN_DATE, 'yyyy-mm-dd hh24:mi:ss') OPEN_DATE,                 ");
		sb.append("        OPEN_STAFF_ID,                                                         ");
		sb.append("        OPEN_DEPART_ID,                                                        ");
		sb.append("        DEVELOP_STAFF_ID,                                                      ");
		sb.append("        TO_CHAR(DEVELOP_DATE, 'yyyy-mm-dd hh24:mi:ss') DEVELOP_DATE,           ");
		sb.append("        DEVELOP_DEPART_ID,                                                     ");
		sb.append("        DEVELOP_CITY_CODE,                                                     ");
		sb.append("        DEVELOP_EPARCHY_CODE,                                                  ");
		sb.append("        DEVELOP_NO,                                                            ");
		sb.append("        TO_CHAR(ASSURE_CUST_ID) ASSURE_CUST_ID,                                ");
		sb.append("        ASSURE_TYPE_CODE,                                                      ");
		sb.append("        TO_CHAR(ASSURE_DATE, 'yyyy-mm-dd hh24:mi:ss') ASSURE_DATE,             ");
		sb.append("        REMOVE_TAG,                                                            ");
		sb.append("        TO_CHAR(PRE_DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') PRE_DESTROY_TIME,   ");
		sb.append("        TO_CHAR(DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') DESTROY_TIME,           ");
		sb.append("        REMOVE_EPARCHY_CODE,                                                   ");
		sb.append("        REMOVE_CITY_CODE,                                                      ");
		sb.append("        REMOVE_DEPART_ID,                                                      ");
		sb.append("        REMOVE_REASON_CODE,                                                    ");
		sb.append("        TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,             ");
		sb.append("        UPDATE_STAFF_ID,                                                       ");
		sb.append("        UPDATE_DEPART_ID,                                                      ");
		sb.append("        REMARK,                                                                ");
		sb.append("        RSRV_NUM1,                                                             ");
		sb.append("        RSRV_NUM2,                                                             ");
		sb.append("        RSRV_NUM3,                                                             ");
		sb.append("        TO_CHAR(RSRV_NUM4) RSRV_NUM4,                                          ");
		sb.append("        TO_CHAR(RSRV_NUM5) RSRV_NUM5,                                          ");
		sb.append("        RSRV_STR1,                                                             ");
		sb.append("        RSRV_STR2,                                                             ");
		sb.append("        RSRV_STR3,                                                             ");
		sb.append("        RSRV_STR4,                                                             ");
		sb.append("        RSRV_STR5,                                                             ");
		sb.append("        RSRV_STR6,                                                             ");
		sb.append("        RSRV_STR7,                                                             ");
		sb.append("        RSRV_STR8,                                                             ");
		sb.append("        RSRV_STR9,                                                             ");
		sb.append("        RSRV_STR10,                                                            ");
		sb.append("        TO_CHAR(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,               ");
		sb.append("        TO_CHAR(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,               ");
		sb.append("        TO_CHAR(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,               ");
		sb.append("        RSRV_TAG1,                                                             ");
		sb.append("        RSRV_TAG2,                                                             ");
		sb.append("        RSRV_TAG3                                                              ");
		sb.append("   FROM TF_F_USER A                                                            ");
		sb.append("  WHERE 1 = 1                                                                  ");
		sb.append("    AND A.CUST_ID IN (                                               		  ");
		sb.append(param.getString("CUST_ID"));
		sb.append("    					)                                               		  ");

		return Dao.qryBySql(sb, param);
	}

	/**
	 * @Function: qryUserInfoBySerialNumber
	 * @Description: 查询用户信息
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午11:29:19 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset qryUserInfoBySerialNumber(String serial_number) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);

		SQLParser parserMu = new SQLParser(param);
		parserMu.addSQL("Select u.user_id,cp.cust_name MEM_CUST_NAME,f_sys_Getcodename('product_id',u.product_id,'','') MEM_PRODUCT_NAME ,sr.relation_type_name ROL_TYPE,'1' ROL_NAME From tf_f_user u ,tf_f_cust_person cp, td_s_relation sr ");
		parserMu.addSQL("Where u.serial_number=:SERIAL_NUMBER  ");
		parserMu.addSQL("And u.remove_tag='0' ");
		parserMu.addSQL("And u.cust_id=cp.cust_id ");
		parserMu.addSQL("And sr.relation_type_code='89'  ");
		return Dao.qryByParse(parserMu);

	}

	public static IDataset qryUserInfoBySnAndBrandCode(String serialNumber, String openDate) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("OPEN_DATE", openDate);
		return Dao.qryByCode("TF_F_USER", "SEL_BY_SN_BRANDCODE", param);
	}

	/**
	 * 根据手机号码，网别，以及用户状态，查询可使用的移动手机号码，现阶段供BBOSS使用
	 * 
	 * @param serialNumber
	 * @param netTypeCode
	 * @param removeTag
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryUserInfoBySnNetTag(String serialNumber, String removeTag, String netTypeCode, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("NET_TYPE_CODE", netTypeCode);
		param.put("REMOVE_TAG", removeTag);

		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT PARTITION_ID, ");
		parser.addSQL("USER_ID, ");
		parser.addSQL("CUST_ID, ");
		parser.addSQL("USECUST_ID, ");
		parser.addSQL("EPARCHY_CODE, ");
		parser.addSQL("CITY_CODE, ");
		parser.addSQL("CITY_CODE_A, ");
		parser.addSQL("USER_PASSWD, ");
		parser.addSQL("USER_DIFF_CODE, ");
		parser.addSQL("USER_TYPE_CODE, ");
		parser.addSQL("USER_TAG_SET, ");
		parser.addSQL("USER_STATE_CODESET, ");
		parser.addSQL("NET_TYPE_CODE, ");
		parser.addSQL("SERIAL_NUMBER, ");
		parser.addSQL("CONTRACT_ID, ");
		parser.addSQL("ACCT_TAG, ");
		parser.addSQL("PREPAY_TAG, ");
		parser.addSQL("MPUTE_MONTH_FEE, ");
		parser.addSQL("MPUTE_DATE, ");
		parser.addSQL("FIRST_CALL_TIME, ");
		parser.addSQL("LAST_STOP_TIME, ");
		parser.addSQL("CHANGEUSER_DATE, ");
		parser.addSQL("IN_NET_MODE, ");
		parser.addSQL("IN_DATE, ");
		parser.addSQL("IN_STAFF_ID, ");
		parser.addSQL("IN_DEPART_ID, ");
		parser.addSQL("OPEN_MODE, ");
		parser.addSQL("OPEN_DATE, ");
		parser.addSQL("OPEN_STAFF_ID, ");
		parser.addSQL("OPEN_DEPART_ID, ");
		parser.addSQL("DEVELOP_STAFF_ID, ");
		parser.addSQL("DEVELOP_DATE, ");
		parser.addSQL("DEVELOP_DEPART_ID, ");
		parser.addSQL("DEVELOP_CITY_CODE, ");
		parser.addSQL("DEVELOP_EPARCHY_CODE, ");
		parser.addSQL("DEVELOP_NO, ");
		parser.addSQL("ASSURE_CUST_ID, ");
		parser.addSQL("ASSURE_TYPE_CODE, ");
		parser.addSQL("ASSURE_DATE, ");
		parser.addSQL("REMOVE_TAG, ");
		parser.addSQL("PRE_DESTROY_TIME, ");
		parser.addSQL("DESTROY_TIME, ");
		parser.addSQL("REMOVE_EPARCHY_CODE, ");
		parser.addSQL("REMOVE_CITY_CODE, ");
		parser.addSQL("REMOVE_DEPART_ID, ");
		parser.addSQL("REMOVE_REASON_CODE, ");
		parser.addSQL("UPDATE_TIME, ");
		parser.addSQL("UPDATE_STAFF_ID, ");
		parser.addSQL("UPDATE_DEPART_ID, ");
		parser.addSQL("REMARK, ");
		parser.addSQL("RSRV_NUM1, ");
		parser.addSQL("RSRV_NUM2, ");
		parser.addSQL("RSRV_NUM3, ");
		parser.addSQL("RSRV_NUM4, ");
		parser.addSQL("RSRV_NUM5, ");
		parser.addSQL("RSRV_STR1, ");
		parser.addSQL("RSRV_STR2, ");
		parser.addSQL("RSRV_STR3, ");
		parser.addSQL("RSRV_STR4, ");
		parser.addSQL("RSRV_STR5, ");
		parser.addSQL("RSRV_STR6, ");
		parser.addSQL("RSRV_STR7, ");
		parser.addSQL("RSRV_STR8, ");
		parser.addSQL("RSRV_STR9, ");
		parser.addSQL("RSRV_STR10, ");
		parser.addSQL("RSRV_DATE1, ");
		parser.addSQL("RSRV_DATE2, ");
		parser.addSQL("RSRV_DATE3, ");
		parser.addSQL("RSRV_TAG1, ");
		parser.addSQL("RSRV_TAG2, ");
		parser.addSQL("RSRV_TAG3 ");
		parser.addSQL("FROM TF_F_USER T ");
		parser.addSQL("WHERE 1=1 ");
		parser.addSQL("AND T.SERIAL_NUMBER = :SERIAL_NUMBER ");
		parser.addSQL("AND T.REMOVE_TAG= :REMOVE_TAG ");
		parser.addSQL("AND T.NET_TYPE_CODE = :NET_TYPE_CODE ");
		IDataset userInfo = Dao.qryByParse(parser, routeId);

		if (IDataUtil.isNotEmpty(userInfo))
		{
			return userInfo;
		} else
		{
			return new DatasetList();
		}
	}

	public static IDataset qryUserInfoBySNNew(String serialNumber, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		return Dao.qryByCode("TF_F_USER", "SEL_BY_SN_NEW", param, routeId);
	}

	/**
	 * 根据User_id在历史用户资料表查询用户信息
	 * 
	 * @param userId
	 * @param routeId
	 * @return
	 * @throws Exception
	 */
	public static IData qryUserInfoByUserIdFromHis(String userId) throws Exception
	{
		return qryUserInfoByUserIdFromHis(userId, null);
	}

	/**
	 * 根据User_id、routeId在历史用户资料表查询用户信息
	 * 
	 * @param userId
	 * @param routeId
	 * @return
	 * @throws Exception
	 */
	public static IData qryUserInfoByUserIdFromHis(String userId, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		StringBuilder sql = new StringBuilder(2500);

		sql.append("SELECT U.PARTITION_ID, TO_CHAR(U.USER_ID) USER_ID, TO_CHAR(CUST_ID) CUST_ID, ");
		sql.append("TO_CHAR(USECUST_ID) USECUST_ID, EPARCHY_CODE, CITY_CODE, CITY_CODE_A, ");
		sql.append("USER_PASSWD, USER_DIFF_CODE, USER_TYPE_CODE, USER_TAG_SET, ");
		sql.append("USER_STATE_CODESET, NET_TYPE_CODE, SERIAL_NUMBER, CONTRACT_ID, ACCT_TAG, ");
		sql.append("PREPAY_TAG, MPUTE_MONTH_FEE, ");
		sql.append("TO_CHAR(MPUTE_DATE, 'yyyy-mm-dd hh24:mi:ss') MPUTE_DATE, ");
		sql.append("TO_CHAR(FIRST_CALL_TIME, 'yyyy-mm-dd hh24:mi:ss') FIRST_CALL_TIME, ");
		sql.append("TO_CHAR(LAST_STOP_TIME, 'yyyy-mm-dd hh24:mi:ss') LAST_STOP_TIME, ");
		sql.append("TO_CHAR(CHANGEUSER_DATE, 'yyyy-mm-dd hh24:mi:ss') CHANGEUSER_DATE, ");
		sql.append("IN_NET_MODE, TO_CHAR(IN_DATE, 'yyyy-mm-dd hh24:mi:ss') IN_DATE, ");
		sql.append("IN_STAFF_ID, IN_DEPART_ID, OPEN_MODE, ");
		sql.append("TO_CHAR(OPEN_DATE, 'yyyy-mm-dd hh24:mi:ss') OPEN_DATE, OPEN_STAFF_ID, ");
		sql.append("OPEN_DEPART_ID, DEVELOP_STAFF_ID, ");
		sql.append("TO_CHAR(DEVELOP_DATE, 'yyyy-mm-dd hh24:mi:ss') DEVELOP_DATE, ");
		sql.append("DEVELOP_DEPART_ID, DEVELOP_CITY_CODE, DEVELOP_EPARCHY_CODE, DEVELOP_NO, ");
		sql.append("TO_CHAR(ASSURE_CUST_ID) ASSURE_CUST_ID, ASSURE_TYPE_CODE, ");
		sql.append("TO_CHAR(ASSURE_DATE, 'yyyy-mm-dd hh24:mi:ss') ASSURE_DATE, REMOVE_TAG, ");
		sql.append("TO_CHAR(PRE_DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') PRE_DESTROY_TIME, ");
		sql.append("TO_CHAR(DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') DESTROY_TIME, ");
		sql.append("REMOVE_EPARCHY_CODE, REMOVE_CITY_CODE, REMOVE_DEPART_ID, ");
		sql.append("REMOVE_REASON_CODE, ");
		sql.append("TO_CHAR(U.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
		sql.append("U.UPDATE_STAFF_ID, U.UPDATE_DEPART_ID, U.REMARK, U.RSRV_NUM1, U.RSRV_NUM2, ");
		sql.append("U.RSRV_NUM3, TO_CHAR(U.RSRV_NUM4) RSRV_NUM4, ");
		sql.append("TO_CHAR(U.RSRV_NUM5) RSRV_NUM5, U.RSRV_STR1, U.RSRV_STR2, U.RSRV_STR3, ");
		sql.append("U.RSRV_STR4, U.RSRV_STR5, U.RSRV_STR6, RSRV_STR7, RSRV_STR8, RSRV_STR9, ");
		sql.append("RSRV_STR10, TO_CHAR(U.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
		sql.append("TO_CHAR(U.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
		sql.append("TO_CHAR(U.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, U.RSRV_TAG1, ");
		sql.append("U.RSRV_TAG2, U.RSRV_TAG3 ");
		sql.append("FROM TF_FH_USER U ");// 销户用户表
		sql.append("WHERE U.USER_ID = TO_NUMBER(:USER_ID) ");
		sql.append("AND U.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");

		IDataset ids = Dao.qryBySql(sql, param, routeId);

		if (IDataUtil.isEmpty(ids))
		{
			return null;
		}

		IData userInfo = ids.getData(0);
		userInfo.put("USER_TYPE", UUserTypeInfoQry.getUserTypeByUserTypeCode(userInfo.getString("USER_TYPE_CODE")));
		userInfo.put("EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(userInfo.getString("EPARCHY_CODE")));
		userInfo.put("CITY_NAME", UAreaInfoQry.getAreaNameByAreaCode(userInfo.getString("CITY_CODE")));

		return userInfo;
	}

	/**
	 * 根据serialNumber获取积分是否限制
	 * 
	 * @author huangsl
	 * @param serialNumber
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryUserScoreLimit(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		return Dao.qryByCode("TF_F_USER", "SEL_SPE_BY_SN_1", param);
	}

	/**
	 * @Function: qryUsrInfo
	 * @Description: 得到用户资料
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午11:30:01 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IData qryUsrInfo(String serial_number) throws Exception
	{

		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);

		IDataset ids = new DatasetList();
		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT u.user_id,u.brand_code,d.brand ");
		parser.addSQL("FROM tf_f_user u,td_s_brand d ");
		parser.addSQL(" WHERE 1=1");
		parser.addSQL(" AND u.serial_number=:SERIAL_NUMBER");
		parser.addSQL(" AND u.remove_tag='0' and u.brand_code=d.brand_code ");
		parser.addSQL(" AND net_type_code = '00' ");
		ids = Dao.qryByParse(parser);
		IData info = ids.size() > 0 ? (IData) ids.getData(0) : new DataMap();
		return info;
	}

	public static IDataset queryAllUserInfoBySn(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		return Dao.qryByCode("TF_F_USER", "SEL_BY_SN_ALL2", param);
	}

	/**
	 * @Function: queryMaxSerialMumber
	 * @Description: 查询成员号码
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午11:31:10 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset queryMaxSerialMumber(String product_id, String cust_id) throws Exception
	{
		IData param = new DataMap();
		param.put("PRODUCT_ID", product_id);
		param.put("CUST_ID", cust_id);

		StringBuilder sqlMaxSN = new StringBuilder();
		sqlMaxSN.append("SELECT max(SERIAL_NUMBER) SERIAL_NUMBER FROM ( ");
		sqlMaxSN.append(" SELECT max(SERIAL_NUMBER) SERIAL_NUMBER FROM TF_F_USER a,TF_F_USER_PRODUCT b  WHERE a.CUST_ID =:CUST_ID  AND b.PRODUCT_ID=:PRODUCT_ID AND a.user_id = b.user_id AND B.PARTITION_ID = MOD(A.USER_ID,10000)");
		sqlMaxSN.append(" UNION ALL ");
		sqlMaxSN.append(" SELECT max(SERIAL_NUMBER) SERIAL_NUMBER FROM TF_B_TRADE WHERE CUST_ID =:CUST_ID AND PRODUCT_ID=:PRODUCT_ID");
		sqlMaxSN.append(" )");
		SQLParser parser = new SQLParser(param);
		parser.addSQL(sqlMaxSN.toString());
		return Dao.qryByParse(parser);
	}

	/**
	 * 查询手机开卡归属地
	 * 
	 * @param serialNumber
	 * @return
	 * @throws Exception
	 * @CREATE BY GONGP@2014-7-28
	 */
	public static IDataset queryPhoneCity(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);

		String sql = " SELECT f_res_getcodename('area_code', 'HAIN', '', '') EPARCHY_NAME,  " + "  f_res_getcodename('area_code', decode(a.city_code, 'HNSJ', 'HNHK', 'HNFW', 'HNHK', 'HNKH', 'HNHK', 'HNHN', 'HNHK', 'HNYD', 'HNHK', 'HNKF', 'HNHK', a.city_code), '', '') CITY_NAME1,  " // 开卡归属地/通话归属地
				+ "  f_res_getcodename('area_code', decode(y.city_code, 'HNSJ', 'HNHK', 'HNFW', 'HNHK', 'HNKH', 'HNHK', 'HNHN', 'HNHK', 'HNYD', 'HNHK', 'HNKF', 'HNHK', y.city_code), '', '') CITY_NAME2  " // 通话归属地
				+ "  from tf_f_user a, (select c.city_code, c.user_id  from tf_f_user b, tf_f_user_city c " + "  where b.serial_number = :SERIAL_NUMBER " + "  and b.remove_tag = '0' " + "  and b.user_id = c.user_id " + "  and c.start_date < sysdate " + "  and c.end_date > sysdate) y " + "  where a.serial_number = :SERIAL_NUMBER  " + "  and a.remove_tag = '0' " + "  and a.user_id = y.user_id (+)  ";
		return Dao.qryBySql(new StringBuilder(sql), param);
	}

	public static IDataset queryPower100InfoByCustId(IData inparam, Pagination pagination) throws Exception
	{
		IDataset dataset = Dao.qryByCode("TF_F_USER", "SEL_POWER100_BY_GRPCUST_ID", inparam, pagination);
		if (IDataUtil.isNotEmpty(dataset))
		{
			for (int i = 0; i < dataset.size(); i++)
			{
				IData data = dataset.getData(i);
				String productName = UProductInfoQry.getProductNameByProductId(data.getString("PRODUCT_ID"));
				data.put("PRODUCT_NAME", productName);
			}
		}
		return dataset;
	}

	/**
	 * @Function: queryRealNameForRes
	 * @Description: 查询用户实名制息 提供给资源使用
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: sunxin
	 */
	public static IDataset queryRealNameForRes(String PSPT_ID) throws Exception
	{
		IData param = new DataMap();
        param.put("PSPT_ID", PSPT_ID);
        IDataset realAllCounts = new DatasetList();
        for(int i =0; i<10; i++){
        	String sqlSring = "SEL_REALNAME_SN_BY_PSPT_PART" + i;
        	IDataset realCounts = Dao.qryByCodeParser("TF_F_USER", sqlSring, param);// sql改造，user里没有band
        	if(IDataUtil.isNotEmpty(realCounts) && realCounts.size()>0){
        		realAllCounts.add(realCounts);
        	}
        }
        return realAllCounts;
	}

	public static IDataset querySnByUsrpid(IData inparams, Pagination pagination) throws Exception
	{

		inparams.put("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
		String removeTag = inparams.getString("REMOVE_TAG", "");
		if ("X".equals(removeTag))
			inparams.put("REMOVE_TAG", "");
		// //按手机号码查
		if ("1".equals(inparams.get("QUERY_MODE")))
		{
			IDataset userInfos = Dao.qryByCode("TF_F_USER", "SEL_BY_SN_USERINFO", inparams, pagination);
			return WideNetUtil.getWideNumberAttrInfos(userInfos);
		}
		// 按用户名称查询（模糊匹配）
		if ("2".equals(inparams.get("QUERY_MODE")))
		{

			IDataset userInfos =  Dao.qryByCode("TF_F_USER", "SEL_LIKE_CUST_NAME", inparams, pagination);
			return WideNetUtil.getWideNumberAttrInfos(userInfos);
		}
		// 按用户名称查询（精确匹配）
		else if ("3".equals(inparams.get("QUERY_MODE")))
		{
			IDataset userInfos =   Dao.qryByCode("TF_F_USER", "SEL_BY_CUST_NAME", inparams, pagination);
			return WideNetUtil.getWideNumberAttrInfos(userInfos);
		}
		// 按证件查询（模糊匹配）
		else if ("4".equals(inparams.get("QUERY_MODE")))
		{
			IDataset userInfos =   Dao.qryByCode("TF_F_USER", "SEL_LIKE_USRPID", inparams, pagination);
			return WideNetUtil.getWideNumberAttrInfos(userInfos);
		}
		// 按证件查询(精确匹配)
		else if ("5".equals(inparams.get("QUERY_MODE")))
		{
			IDataset userInfos =   Dao.qryByCode("TF_F_USER", "SEL_BY_USRPID", inparams, pagination);
			return WideNetUtil.getWideNumberAttrInfos(userInfos);
		}

		// REQ201512300001 客户资料关联查询优化--业务挑刺问题 2016-01-04
		// 按客户名称+证件号码方式查询（精确匹配）
		else if ("7".equals(inparams.get("QUERY_MODE")))
		{
			IDataset userInfos =   Dao.qryByCode("TF_F_USER", "SEL_BY_PSPTID_CUSTNAME", inparams, pagination);
			return WideNetUtil.getWideNumberAttrInfos(userInfos);
		}
		return null;
	}

	public static IDataset querySnByUsrpid4Export(IData inparams, String eparchy_code) throws Exception
	{

		inparams.put("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
		String removeTag = inparams.getString("REMOVE_TAG", "");
		if ("X".equals(removeTag))
			inparams.put("REMOVE_TAG", "");
		// //按手机号码查
		// if("1".equals(inparams.get("QUERY_MODE")))
		// {
		// return
		// Dao.qryByCode("TF_F_USER","SEL_BY_SN_USERINFO",inparams,pagination);
		// }
		// 按用户名称查询（模糊匹配）
		if ("1".equals(inparams.get("QUERY_MODE")))
		{
			return Dao.qryByCode("TF_F_USER", "SEL_LIKE_CUST_NAME", inparams, eparchy_code);
		}
		// 按用户名称查询（精确匹配）
		else if ("2".equals(inparams.get("QUERY_MODE")))
		{
			return Dao.qryByCode("TF_F_USER", "SEL_BY_CUST_NAME", inparams, eparchy_code);
		}
		// 按证件查询（模糊匹配）
		else if ("3".equals(inparams.get("QUERY_MODE")))
		{
			return Dao.qryByCode("TF_F_USER", "SEL_LIKE_USRPID", inparams, eparchy_code);
		}
		// 按证件查询(精确匹配)
		else if ("4".equals(inparams.get("QUERY_MODE")))
		{
			return Dao.qryByCode("TF_F_USER", "SEL_BY_USRPID", inparams, eparchy_code);
		}
		return null;
	}

	/**
	 * @Function: queryTradeAlreadyBackFinish
	 * @Description: 用户查询已经完工的信控发起的恢复工单,用于处理信控重复发起工单处理，但crm已经处理完 只是资料还没有同步到账务
	 *               导致信控重复发起工单的判断
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午11:32:27 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset queryTradeAlreadyBackFinish(String user_id) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);

		return Dao.qryByCode("TF_F_USER", "SEL_CREDITGRP_BACKFINISH", param);
	}

	/**
	 * @Function: queryTradeAlreadyFinish
	 * @Description: 用户查询已经完工的信控发起的暂停工单,用于处理信控重复发起工单处理，但crm已经处理完 只是资料还没有同步到账务
	 *               导致信控重复发起工单的判断
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午11:33:13 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset queryTradeAlreadyFinish(String user_id) throws Exception
	{

		IData param = new DataMap();
		param.put("USER_ID", user_id);

		return Dao.qryByCode("TF_F_USER", "SEL_CREDITGRP_FINISH", param);
	}

	public static IDataset queryUserAndProductByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		IDataset users = Dao.qryByCode("TF_F_USER", "SEL_BY_PK2", param);
		return users;
	}
	//start--wangsc10--20181204
	public static IDataset queryUserOpendateByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		IDataset users = Dao.qryByCode("TF_F_USER", "SEL_OPENDATE_BY_PK", param);
		return users;
	}
	//end

	/**
	 * @Function: queryUserBrandState
	 * @Description:查询用户有效品牌
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午11:34:25 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset queryUserBrandState(String user_id, Pagination page) throws Exception
	{

		IData param = new DataMap();
		param.put("USER_ID", user_id);

		return Dao.qryByCode("TF_F_USER", "SEL_BY_USERID_BRAND_STATE", param, page);
	}

	/**
	 * 查询USER表最近一次销户用户记录
	 * 
	 * @param serialNumber
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserBySnDestroyLatest(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);

		return Dao.qryByCode("TF_F_USER", "SEL_BY_SN_DESTROY_LATEST", param);
	}

	/**
	 * @Function: queryUserCustIDByNumber
	 * @Description: 查询客户CustID信息
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午11:36:00 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset queryUserCustIDByNumber(String serial_number) throws Exception
	{

		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select  b.cust_id GROUP_CUST_ID,a.user_id_a,a.user_id_b from tf_f_relation_uu a, tf_f_user b where a.serial_number_b =:SERIAL_NUMBER and a.end_date > sysdate  and b.product_id='7120' and b.remove_tag=0 and b.user_id=a.user_id_a ");
		return Dao.qryByParse(parser);
	}

	/**
	 * @Function: queryUserCustIDByNumber7121
	 * @Description: 查询客户CustID信息
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午11:36:31 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset queryUserCustIDByNumber7121(String serial_number) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select  b.cust_id GROUP_CUST_ID,a.user_id_a,a.user_id_b from tf_f_relation_uu a, tf_f_user b where a.serial_number_b =:SERIAL_NUMBER and a.end_date > sysdate  and b.product_id='7121' and b.remove_tag=0 and b.user_id=a.user_id_a ");
		return Dao.qryByParse(parser);
	}

	public static IDataset queryUserDiscntUserInfos(String SERIAL_NUMBER) throws Exception
	{
		IData cond = new DataMap();
		cond.put("SERIAL_NUMBER", SERIAL_NUMBER);
		return Dao.qryByCode("TF_F_USER", "SEL_DISCNTCODE_BY_SERIANUM", cond);
	}

	/**
	 * @Function: queryUserHSState
	 * @Description: 查询同一个客户下面其他的用户是有高额停机
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午11:38:29 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset queryUserHSState(String user_id, String cust_id, Pagination page) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("CUST_ID", cust_id);

		return Dao.qryByCode("TF_F_USER", "SEL_BY_CUSTID_STATE_2", param, page);
	}

	/**
	 * @Function: queryUserInfo
	 * @Description: 查询用户信息
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午11:39:05 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IData queryUserInfo(String serial_number) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT A.CUST_NAME, C.PSPT_TYPE_CODE, C.PSPT_ID, C.PSPT_END_DATE, C.PSPT_ADDR, B.BRAND_CODE, B.PRODUCT_ID");
		parser.addSQL(" FROM TF_F_CUSTOMER A, TF_F_USER B, TF_F_CUST_PERSON C");
		parser.addSQL(" WHERE 1=1");
		parser.addSQL(" AND B.CUST_ID = A.CUST_ID");
		parser.addSQL(" AND A.CUST_ID = C.CUST_ID");
		parser.addSQL(" AND B.REMOVE_TAG = '0'");
		parser.addSQL(" AND B.SERIAL_NUMBER = :SERIAL_NUMBER");
		IDataset userInfo = Dao.qryByParse(parser);
		return userInfo.size() == 0 ? null : userInfo.getData(0);
	}

	/**
	 * @Function: queryUserInfoByCustProid
	 * @Description: 根据custid、productId查询用户信息
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午11:40:36 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset queryUserInfoByCustProid(String product_id, String cust_id, String eparchyCode) throws Exception
	{

		IData param = new DataMap();
		param.put("PRODUCT_ID", product_id);
		param.put("CUST_ID", cust_id);
		// getVisit().setRouteEparchyCode( eparchyCode);
		IDataset dateset = Dao.qryByCode("TF_F_USER", "SEL_BY_CUSTID_PRODUCTID", param, eparchyCode);
		return dateset.size() > 0 ? dateset : null;
	}

	/**
	 * @Function: queryUserState
	 * @Description:查询同一个客户下面其他的用户是否为黑名单停机用户
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午11:42:18 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IDataset queryUserState(String user_id, String cust_id, Pagination page) throws Exception
	{

		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("CUST_ID", cust_id);

		return Dao.qryByCode("TF_F_USER", "SEL_BY_CUSTID_STATE", param, page);
	}

	/**
	 * @Function: queryUserUserIdProductId
	 * @Description: 该函数的功能描述
	 * @param
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 * @author: updata
	 * @date: 2013-4-27 上午11:43:10 Modification History: Date Author Version
	 *        Description
	 *        ---------------------------------------------------------*
	 *        2013-4-27 updata v1.0.0 修改原因
	 */
	public static IData queryUserUserIdProductIdForGrp(String user_id, String product_id) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", user_id);
		param.put("PRODUCT_ID", product_id);

		IDataset idset = Dao.qryByCode("TF_F_USER", "SEL_BY_PK2", param, Route.CONN_CRM_CG);
		return (idset != null && idset.size() > 0) ? idset.getData(0) : null;
	}

	public static int updataUserInfoChange(String userId) throws Exception
	{
		IData delParam = new DataMap();
		delParam.put("END_DATE", SysDateMgr.getSysTime());
		delParam.put("USER_ID", userId);
		delParam.put("DATE_JUST", SysDateMgr.getSysTime());
		int count = Dao.executeUpdateByCodeCode("TF_F_USER_INFOCHANGE", "UPD_ENDDATE", delParam);
		return count;
	}

	public static int updataWidenetUserInfoDelete(String userId, String strFinishDate, String tradeEparchyCode, String tradeCityCode, String tradeDepartId, String removeReson, String remark) throws Exception
	{
		IData delParam = new DataMap();
		delParam.put("USER_ID", userId);
		delParam.put("REMOVE_TAG", "2");
		delParam.put("FINISH_DATE", strFinishDate);
		delParam.put("REMOVE_EPARCHY_CODE", tradeEparchyCode);
		delParam.put("REMOVE_CITY_CODE", tradeCityCode);
		delParam.put("REMOVE_DEPART_ID", tradeDepartId);
		delParam.put("REMOVE_REASON_CODE", removeReson);
		delParam.put("REMARK", remark);
		int count = Dao.executeUpdateByCodeCode("TF_F_USER", "DESTORYNOW_BY_USERID_WITH_FINISH_DATE", delParam);
		return count;
	}

	public static int updataWidenetUserRelationDelete(String userId) throws Exception
	{
		IData delParam = new DataMap();
		delParam.put("USER_ID", userId);
		int count = Dao.executeUpdateByCodeCode("TF_F_RELATION_UU", "UPD_PREDESTROY_USER_RELA", delParam);
		return count;
	}

	/**
	 * 更改用户主体服务
	 * 
	 * @param userStateCodeset
	 * @param lastStopTime
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static int updateSNByUserId(String userStateCodeset, String lastStopTime, String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_STATE_CODESET", userStateCodeset);
		param.put("LAST_STOP_TIME", lastStopTime);
		param.put("USER_ID", userId);
		return Dao.executeUpdateByCodeCode("TF_F_USER", "UPD_SVCSTATECODE", param);
	}

	/**
	 * 根据userId修改TF_F_USER_TRUNK表
	 * 
	 * @param userStateCodeset
	 * @param lastStopTime
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static int updateTrunkByUserId(String userId, String userIdA) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("USER_ID_A", userIdA);
		return Dao.executeUpdateByCodeCode("TF_F_USER", "UPD_TRUNK_USERID", param);
	}

	/**
	 * todo code_code 表里没有SEL_USERPACKAGE_BYUSERIDA 查询VPMN集团用户可选服务
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IDataset getServElementByVpmnPackage(IData data, String userIdA) throws Exception
	{

		data.put("ELEMENT_TYPE_CODE", "S");
		// TODO data.put("TRADE_STAFF_ID", getVisit().getStaffId());
		data.put("ELEMENT_ID", "860");
		data.put("USER_ID_A", userIdA);
		return Dao.qryByCode("TF_F_USER", "SEL_USERPACKAGE_BYUSERIDA", data, Route.CONN_CRM_CG);
	}

	/**
	 * todo code_code表里没有
	 * 
	 * @Function: getUserProductNoPriv
	 * @Description: 该函数的功能描述
	 * @param:参数描述
	 * @return：返回结果描述
	 * @throws：异常描述
	 * @version: v1.0.0
	 */

	public IDataset getUserProductNoPriv(IData data) throws Exception
	{

		// TODO data.put("TRADE_STAFF_ID", getVisit().getStaffId());
		// TODO String vpnFlag = AppCtx.getAttribute("VPN_FLAG");
		String vpnFlag = "";
		if ("VPCN_VPMN".equals(vpnFlag))
		{
			data.put("PRODUCT_VPMN", "8000");
			data.put("PRODUCT_VPCN", "8010");
			return Dao.qryByCode("TF_F_USER", "SEL_BY_PRODUCT_VPN_NO_PRIV", data, Route.CONN_CRM_CG);
		}
		return Dao.qryByCode("TF_F_USER", "SEL_BY_PRODUCT_NO_PRIV", data, Route.CONN_CRM_CG);
	}

	/**
	 * todo code_code表里没有 根据产品类型查询用户订购了那些产品
	 * 
	 * @param data
	 * @param eparchyCode
	 * @return
	 * @throws Exception
	 */
	public IDataset getUserProductS(IData data) throws Exception
	{

		// AppUtil.setEparchy(BaseConnMgr.GRP_DB);
		// TODO data.put("TRADE_STAFF_ID", getVisit().getStaffId());
		return Dao.qryByCode("TF_F_USER", "SEL_BY_PRODUCT_TYPE_S", data, Route.CONN_CRM_CG);
	}

	/**
	 * TODO code_code表里没有 获取用户可选的优惠
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IDataset getVpmnDiscntElementByPackage(IData data, String userIdA) throws Exception
	{

		data.put("ELEMENT_TYPE_CODE", "D");
		// TODO data.put("TRADE_STAFF_ID", getVisit().getStaffId());
		data.put("USER_ID_A", userIdA);
		return Dao.qryByCode("TF_F_USER", "SEL_USERDISCNT_BTUSERIDA", data, Route.CONN_CRM_CG);
	}

	/**
	 * 合户查询
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static IDataset querySameAcctInfo(IData data) throws Exception
	{
		return Dao.qryByCode("TF_F_USER", "SEL_ALL_INFO_BY_SERIALNUMBER", data);
	}

	public static IDataset getUserInfoByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER", "SEL_BY_PK", param);
	}

	public static IDataset getWideUserInfoBySN(IData param) throws Exception
	{
		return Dao.qryByCode("TF_F_USER_WIDENET", "SEL_BY_SERIAL_NUMBER_WIDENET", param);
	}

	public static IDataset getUserAvgPayFee(String userId, String monthsNum) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID", userId);
		inparam.put("MONTHS_NUM", monthsNum);

		return Dao.qryByCode("TD_S_COMMPARA", "SEL_USER_AVGPAYFEE_NG", inparam);
	}

	public static IDataset getUserAvgPayFee2(String userId, String monthsNum) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("USER_ID", userId);
		inparam.put("MONTHS_NUM", monthsNum);

		return Dao.qryByCode("TD_S_COMMPARA", "SEL_USER_AVGPAYFEE_NG2", inparam);
	}

	public static IDataset getUserDonateScore(String sn) throws Exception
	{
		IData inparam = new DataMap();
		inparam.put("SERIAL_NUMBER", sn);
		inparam.put("TRADE_TYPE_CODE", "340");
		return Dao.qryByCode("TD_S_CPARAM", "IsBeyongDonateLimitNew", inparam, Route.getJourDb(CSBizBean.getTradeEparchyCode()));// 转为jour用户，duhj
	}

	public static IDataset queryAllUserBySerialNumberOrUserId(String serialNumber, String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("USER_ID", userId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT ");
		parser.addSQL(" U.PARTITION_ID, TO_CHAR(U.USER_ID) USER_ID, TO_CHAR(U.CUST_ID) CUST_ID, ");
		parser.addSQL(" TO_CHAR(U.USECUST_ID) USECUST_ID, EPARCHY_CODE, CITY_CODE, CITY_CODE_A, ");
		parser.addSQL(" USER_PASSWD, USER_DIFF_CODE, USER_TYPE_CODE, USER_TAG_SET, ");
		parser.addSQL(" USER_STATE_CODESET, NET_TYPE_CODE, SERIAL_NUMBER, CONTRACT_ID, ACCT_TAG, ");
		parser.addSQL(" PREPAY_TAG, MPUTE_MONTH_FEE, ");
		parser.addSQL(" TO_CHAR(MPUTE_DATE, 'yyyy-mm-dd hh24:mi:ss') MPUTE_DATE, ");
		parser.addSQL(" TO_CHAR(FIRST_CALL_TIME, 'yyyy-mm-dd hh24:mi:ss') FIRST_CALL_TIME, ");
		parser.addSQL(" TO_CHAR(LAST_STOP_TIME, 'yyyy-mm-dd hh24:mi:ss') LAST_STOP_TIME, ");
		parser.addSQL(" TO_CHAR(CHANGEUSER_DATE, 'yyyy-mm-dd hh24:mi:ss') CHANGEUSER_DATE, ");
		parser.addSQL(" IN_NET_MODE, TO_CHAR(IN_DATE, 'yyyy-mm-dd hh24:mi:ss') IN_DATE, ");
		parser.addSQL(" IN_STAFF_ID, IN_DEPART_ID, OPEN_MODE, ");
		parser.addSQL(" TO_CHAR(OPEN_DATE, 'yyyy-mm-dd hh24:mi:ss') OPEN_DATE, OPEN_STAFF_ID, ");
		parser.addSQL(" OPEN_DEPART_ID, DEVELOP_STAFF_ID, ");
		parser.addSQL(" TO_CHAR(DEVELOP_DATE, 'yyyy-mm-dd hh24:mi:ss') DEVELOP_DATE, ");
		parser.addSQL(" DEVELOP_DEPART_ID, DEVELOP_CITY_CODE, DEVELOP_EPARCHY_CODE, DEVELOP_NO, ");
		parser.addSQL(" TO_CHAR(ASSURE_CUST_ID) ASSURE_CUST_ID, ASSURE_TYPE_CODE, ");
		parser.addSQL(" TO_CHAR(ASSURE_DATE, 'yyyy-mm-dd hh24:mi:ss') ASSURE_DATE, REMOVE_TAG, ");
		parser.addSQL(" TO_CHAR(PRE_DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') PRE_DESTROY_TIME, ");
		parser.addSQL(" TO_CHAR(DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') DESTROY_TIME, ");
		parser.addSQL(" REMOVE_EPARCHY_CODE, REMOVE_CITY_CODE, REMOVE_DEPART_ID, ");
		parser.addSQL(" REMOVE_REASON_CODE, ");
		parser.addSQL(" TO_CHAR(U.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
		parser.addSQL(" U.UPDATE_STAFF_ID, U.UPDATE_DEPART_ID, U.REMARK, U.RSRV_NUM1, U.RSRV_NUM2, ");
		parser.addSQL(" U.RSRV_NUM3, TO_CHAR(U.RSRV_NUM4) RSRV_NUM4, ");
		parser.addSQL(" TO_CHAR(U.RSRV_NUM5) RSRV_NUM5, U.RSRV_STR1, U.RSRV_STR2, U.RSRV_STR3, ");
		parser.addSQL(" U.RSRV_STR4, U.RSRV_STR5, RSRV_STR6, RSRV_STR7, RSRV_STR8, RSRV_STR9, ");
		parser.addSQL(" RSRV_STR10, TO_CHAR(U.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
		parser.addSQL(" TO_CHAR(U.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
		parser.addSQL(" TO_CHAR(U.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, U.RSRV_TAG1, ");
		parser.addSQL(" U.RSRV_TAG2, U.RSRV_TAG3 ");
		parser.addSQL(" FROM TF_F_USER U ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" AND U.SERIAL_NUMBER= :SERIAL_NUMBER ");
		parser.addSQL(" AND U.USER_ID= :USER_ID ");
		parser.addSQL(" AND U.PARTITION_ID= MOD(TO_NUMBER( :USER_ID ),10000) ");
		parser.addSQL(" UNION ALL ");
		parser.addSQL(" SELECT ");
		parser.addSQL(" U.PARTITION_ID, TO_CHAR(U.USER_ID) USER_ID, TO_CHAR(U.CUST_ID) CUST_ID, ");
		parser.addSQL(" TO_CHAR(U.USECUST_ID) USECUST_ID, EPARCHY_CODE, CITY_CODE, CITY_CODE_A, ");
		parser.addSQL(" USER_PASSWD, USER_DIFF_CODE, USER_TYPE_CODE, USER_TAG_SET, ");
		parser.addSQL(" USER_STATE_CODESET, NET_TYPE_CODE, SERIAL_NUMBER, CONTRACT_ID, ACCT_TAG, ");
		parser.addSQL(" PREPAY_TAG, MPUTE_MONTH_FEE, ");
		parser.addSQL(" TO_CHAR(MPUTE_DATE, 'yyyy-mm-dd hh24:mi:ss') MPUTE_DATE, ");
		parser.addSQL(" TO_CHAR(FIRST_CALL_TIME, 'yyyy-mm-dd hh24:mi:ss') FIRST_CALL_TIME, ");
		parser.addSQL(" TO_CHAR(LAST_STOP_TIME, 'yyyy-mm-dd hh24:mi:ss') LAST_STOP_TIME, ");
		parser.addSQL(" TO_CHAR(CHANGEUSER_DATE, 'yyyy-mm-dd hh24:mi:ss') CHANGEUSER_DATE, ");
		parser.addSQL(" IN_NET_MODE, TO_CHAR(IN_DATE, 'yyyy-mm-dd hh24:mi:ss') IN_DATE, ");
		parser.addSQL(" IN_STAFF_ID, IN_DEPART_ID, OPEN_MODE, ");
		parser.addSQL(" TO_CHAR(OPEN_DATE, 'yyyy-mm-dd hh24:mi:ss') OPEN_DATE, OPEN_STAFF_ID, ");
		parser.addSQL(" OPEN_DEPART_ID, DEVELOP_STAFF_ID, ");
		parser.addSQL(" TO_CHAR(DEVELOP_DATE, 'yyyy-mm-dd hh24:mi:ss') DEVELOP_DATE, ");
		parser.addSQL(" DEVELOP_DEPART_ID, DEVELOP_CITY_CODE, DEVELOP_EPARCHY_CODE, DEVELOP_NO, ");
		parser.addSQL(" TO_CHAR(ASSURE_CUST_ID) ASSURE_CUST_ID, ASSURE_TYPE_CODE, ");
		parser.addSQL(" TO_CHAR(ASSURE_DATE, 'yyyy-mm-dd hh24:mi:ss') ASSURE_DATE, REMOVE_TAG, ");
		parser.addSQL(" TO_CHAR(PRE_DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') PRE_DESTROY_TIME, ");
		parser.addSQL(" TO_CHAR(DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') DESTROY_TIME, ");
		parser.addSQL(" REMOVE_EPARCHY_CODE, REMOVE_CITY_CODE, REMOVE_DEPART_ID, ");
		parser.addSQL(" REMOVE_REASON_CODE, ");
		parser.addSQL(" TO_CHAR(U.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
		parser.addSQL(" U.UPDATE_STAFF_ID, U.UPDATE_DEPART_ID, U.REMARK, U.RSRV_NUM1, U.RSRV_NUM2, ");
		parser.addSQL(" U.RSRV_NUM3, TO_CHAR(U.RSRV_NUM4) RSRV_NUM4, ");
		parser.addSQL(" TO_CHAR(U.RSRV_NUM5) RSRV_NUM5, U.RSRV_STR1, U.RSRV_STR2, U.RSRV_STR3, ");
		parser.addSQL(" U.RSRV_STR4, U.RSRV_STR5, RSRV_STR6, RSRV_STR7, RSRV_STR8, RSRV_STR9, ");
		parser.addSQL(" RSRV_STR10, TO_CHAR(U.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
		parser.addSQL(" TO_CHAR(U.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
		parser.addSQL(" TO_CHAR(U.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, U.RSRV_TAG1, ");
		parser.addSQL(" U.RSRV_TAG2, U.RSRV_TAG3 ");
		parser.addSQL(" FROM TF_FH_USER U ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" AND U.SERIAL_NUMBER= :SERIAL_NUMBER ");
		parser.addSQL(" AND U.USER_ID= :USER_ID ");
		parser.addSQL(" AND U.PARTITION_ID= MOD(TO_NUMBER( :USER_ID ),10000) ");

		return Dao.qryByParse(parser);
	}

	/**
	 * @description 根据SERIAL_NUMBER查询对应改的有效用户信息
	 * @author xunyl
	 * @date 2015-06-02
	 */
	public static IDataset getEffUserInfoBySn(String serialNumber, String removeTag, String routeId) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("REMOVE_TAG", removeTag);

		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT PARTITION_ID,USER_ID,");
		parser.addSQL("CUST_ID,USECUST_ID,EPARCHY_CODE,");
		parser.addSQL("CITY_CODE,CITY_CODE_A,USER_PASSWD,");
		parser.addSQL("USER_DIFF_CODE,USER_TYPE_CODE,USER_TAG_SET,");
		parser.addSQL("USER_STATE_CODESET,NET_TYPE_CODE,SERIAL_NUMBER,");
		parser.addSQL("CONTRACT_ID,ACCT_TAG,PREPAY_TAG,");
		parser.addSQL("MPUTE_MONTH_FEE,MPUTE_DATE,FIRST_CALL_TIME,");
		parser.addSQL("LAST_STOP_TIME,CHANGEUSER_DATE,IN_NET_MODE,");
		parser.addSQL("IN_DATE,IN_STAFF_ID,IN_DEPART_ID,");
		parser.addSQL("OPEN_MODE,OPEN_DATE,OPEN_STAFF_ID,");
		parser.addSQL("OPEN_DEPART_ID,DEVELOP_STAFF_ID,DEVELOP_DATE,");
		parser.addSQL("DEVELOP_DEPART_ID,DEVELOP_CITY_CODE,DEVELOP_EPARCHY_CODE,");
		parser.addSQL("DEVELOP_NO,ASSURE_CUST_ID,ASSURE_TYPE_CODE,");
		parser.addSQL("ASSURE_DATE,REMOVE_TAG,PRE_DESTROY_TIME,");
		parser.addSQL("DESTROY_TIME,REMOVE_EPARCHY_CODE,REMOVE_CITY_CODE,");
		parser.addSQL("REMOVE_DEPART_ID,REMOVE_REASON_CODE,UPDATE_TIME,");
		parser.addSQL("UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,");
		parser.addSQL("RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,");
		parser.addSQL("RSRV_NUM4,RSRV_NUM5,RSRV_STR1,");
		parser.addSQL("RSRV_STR2,RSRV_STR3,RSRV_STR4,");
		parser.addSQL("RSRV_STR5,RSRV_STR6,RSRV_STR7,");
		parser.addSQL("RSRV_STR8,RSRV_STR9,RSRV_STR10,");
		parser.addSQL("RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,");
		parser.addSQL("RSRV_TAG1,RSRV_TAG2,RSRV_TAG3 ");
		parser.addSQL("from TF_F_USER t ");
		parser.addSQL("where 1=1 ");
		parser.addSQL("and t.SERIAL_NUMBER = :SERIAL_NUMBER ");
		parser.addSQL("and t.REMOVE_TAG = :REMOVE_TAG");

		return Dao.qryByParse(parser, routeId);
	}

	/**
	 * 查询到相同证件的userId
	 * 
	 * @param psptTypeCode
	 * @param psptId
	 * @return
	 * @throws Exception
	 */
	public static IDataset querySameDocumentUserIds(String psptTypeCode, String psptId) throws Exception
	{
		IData param = new DataMap();
		param.put("PSPT_TYPE_CODE", psptTypeCode);
		param.put("PSPT_ID", psptId);

		return Dao.qryByCode("TF_F_USER", "QRY_SAME_DOC_USER_ID", param);
	}

	/**
	 * 查询到相同证件的userId
	 * 
	 * @param psptTypeCode
	 * @param psptId
	 * @return
	 * @throws Exception
	 */
	public static IDataset querySameDocumentUserIdsByPsptId(String psptId) throws Exception
	{
		IData param = new DataMap();
		param.put("PSPT_ID", psptId);

		return Dao.qryByCode("TF_F_USER", "QRY_SAME_DOC_USER_ID_BY_DOC_ID", param);
	}

	/**
	 * 根据user_id和cust_id查询TF_F_USER表数据
	 */
	public static IDataset queryUserInfoByUseridAndCustid(IData input) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", input.getString("USER_ID", ""));
		param.put("CUST_ID", input.getString("CUST_ID", ""));

		return Dao.qryByCode("TF_F_USER", "SEL_USER_BY_USERID_CUSTID", param);
	}

	public static IDataset queryAllUserByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT ");
		parser.addSQL(" U.PARTITION_ID, TO_CHAR(U.USER_ID) USER_ID, TO_CHAR(U.CUST_ID) CUST_ID, ");
		parser.addSQL(" TO_CHAR(U.USECUST_ID) USECUST_ID, EPARCHY_CODE, CITY_CODE, CITY_CODE_A, ");
		parser.addSQL(" USER_PASSWD, USER_DIFF_CODE, USER_TYPE_CODE, USER_TAG_SET, ");
		parser.addSQL(" USER_STATE_CODESET, NET_TYPE_CODE, SERIAL_NUMBER, CONTRACT_ID, ACCT_TAG, ");
		parser.addSQL(" PREPAY_TAG, MPUTE_MONTH_FEE, ");
		parser.addSQL(" TO_CHAR(MPUTE_DATE, 'yyyy-mm-dd hh24:mi:ss') MPUTE_DATE, ");
		parser.addSQL(" TO_CHAR(FIRST_CALL_TIME, 'yyyy-mm-dd hh24:mi:ss') FIRST_CALL_TIME, ");
		parser.addSQL(" TO_CHAR(LAST_STOP_TIME, 'yyyy-mm-dd hh24:mi:ss') LAST_STOP_TIME, ");
		parser.addSQL(" TO_CHAR(CHANGEUSER_DATE, 'yyyy-mm-dd hh24:mi:ss') CHANGEUSER_DATE, ");
		parser.addSQL(" IN_NET_MODE, TO_CHAR(IN_DATE, 'yyyy-mm-dd hh24:mi:ss') IN_DATE, ");
		parser.addSQL(" IN_STAFF_ID, IN_DEPART_ID, OPEN_MODE, ");
		parser.addSQL(" TO_CHAR(OPEN_DATE, 'yyyy-mm-dd hh24:mi:ss') OPEN_DATE, OPEN_STAFF_ID, ");
		parser.addSQL(" OPEN_DEPART_ID, DEVELOP_STAFF_ID, ");
		parser.addSQL(" TO_CHAR(DEVELOP_DATE, 'yyyy-mm-dd hh24:mi:ss') DEVELOP_DATE, ");
		parser.addSQL(" DEVELOP_DEPART_ID, DEVELOP_CITY_CODE, DEVELOP_EPARCHY_CODE, DEVELOP_NO, ");
		parser.addSQL(" TO_CHAR(ASSURE_CUST_ID) ASSURE_CUST_ID, ASSURE_TYPE_CODE, ");
		parser.addSQL(" TO_CHAR(ASSURE_DATE, 'yyyy-mm-dd hh24:mi:ss') ASSURE_DATE, REMOVE_TAG, ");
		parser.addSQL(" TO_CHAR(PRE_DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') PRE_DESTROY_TIME, ");
		parser.addSQL(" TO_CHAR(DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') DESTROY_TIME, ");
		parser.addSQL(" REMOVE_EPARCHY_CODE, REMOVE_CITY_CODE, REMOVE_DEPART_ID, ");
		parser.addSQL(" REMOVE_REASON_CODE, ");
		parser.addSQL(" TO_CHAR(U.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
		parser.addSQL(" U.UPDATE_STAFF_ID, U.UPDATE_DEPART_ID, U.REMARK, U.RSRV_NUM1, U.RSRV_NUM2, ");
		parser.addSQL(" U.RSRV_NUM3, TO_CHAR(U.RSRV_NUM4) RSRV_NUM4, ");
		parser.addSQL(" TO_CHAR(U.RSRV_NUM5) RSRV_NUM5, U.RSRV_STR1, U.RSRV_STR2, U.RSRV_STR3, ");
		parser.addSQL(" U.RSRV_STR4, U.RSRV_STR5, RSRV_STR6, RSRV_STR7, RSRV_STR8, RSRV_STR9, ");
		parser.addSQL(" RSRV_STR10, TO_CHAR(U.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
		parser.addSQL(" TO_CHAR(U.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
		parser.addSQL(" TO_CHAR(U.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, U.RSRV_TAG1, ");
		parser.addSQL(" U.RSRV_TAG2, U.RSRV_TAG3 ");
		parser.addSQL(" FROM TF_F_USER U ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" AND U.USER_ID= :USER_ID ");
		parser.addSQL(" AND U.PARTITION_ID= MOD(TO_NUMBER( :USER_ID ),10000) ");
		parser.addSQL(" UNION ALL ");
		parser.addSQL(" SELECT ");
		parser.addSQL(" U.PARTITION_ID, TO_CHAR(U.USER_ID) USER_ID, TO_CHAR(U.CUST_ID) CUST_ID, ");
		parser.addSQL(" TO_CHAR(U.USECUST_ID) USECUST_ID, EPARCHY_CODE, CITY_CODE, CITY_CODE_A, ");
		parser.addSQL(" USER_PASSWD, USER_DIFF_CODE, USER_TYPE_CODE, USER_TAG_SET, ");
		parser.addSQL(" USER_STATE_CODESET, NET_TYPE_CODE, SERIAL_NUMBER, CONTRACT_ID, ACCT_TAG, ");
		parser.addSQL(" PREPAY_TAG, MPUTE_MONTH_FEE, ");
		parser.addSQL(" TO_CHAR(MPUTE_DATE, 'yyyy-mm-dd hh24:mi:ss') MPUTE_DATE, ");
		parser.addSQL(" TO_CHAR(FIRST_CALL_TIME, 'yyyy-mm-dd hh24:mi:ss') FIRST_CALL_TIME, ");
		parser.addSQL(" TO_CHAR(LAST_STOP_TIME, 'yyyy-mm-dd hh24:mi:ss') LAST_STOP_TIME, ");
		parser.addSQL(" TO_CHAR(CHANGEUSER_DATE, 'yyyy-mm-dd hh24:mi:ss') CHANGEUSER_DATE, ");
		parser.addSQL(" IN_NET_MODE, TO_CHAR(IN_DATE, 'yyyy-mm-dd hh24:mi:ss') IN_DATE, ");
		parser.addSQL(" IN_STAFF_ID, IN_DEPART_ID, OPEN_MODE, ");
		parser.addSQL(" TO_CHAR(OPEN_DATE, 'yyyy-mm-dd hh24:mi:ss') OPEN_DATE, OPEN_STAFF_ID, ");
		parser.addSQL(" OPEN_DEPART_ID, DEVELOP_STAFF_ID, ");
		parser.addSQL(" TO_CHAR(DEVELOP_DATE, 'yyyy-mm-dd hh24:mi:ss') DEVELOP_DATE, ");
		parser.addSQL(" DEVELOP_DEPART_ID, DEVELOP_CITY_CODE, DEVELOP_EPARCHY_CODE, DEVELOP_NO, ");
		parser.addSQL(" TO_CHAR(ASSURE_CUST_ID) ASSURE_CUST_ID, ASSURE_TYPE_CODE, ");
		parser.addSQL(" TO_CHAR(ASSURE_DATE, 'yyyy-mm-dd hh24:mi:ss') ASSURE_DATE, REMOVE_TAG, ");
		parser.addSQL(" TO_CHAR(PRE_DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') PRE_DESTROY_TIME, ");
		parser.addSQL(" TO_CHAR(DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') DESTROY_TIME, ");
		parser.addSQL(" REMOVE_EPARCHY_CODE, REMOVE_CITY_CODE, REMOVE_DEPART_ID, ");
		parser.addSQL(" REMOVE_REASON_CODE, ");
		parser.addSQL(" TO_CHAR(U.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
		parser.addSQL(" U.UPDATE_STAFF_ID, U.UPDATE_DEPART_ID, U.REMARK, U.RSRV_NUM1, U.RSRV_NUM2, ");
		parser.addSQL(" U.RSRV_NUM3, TO_CHAR(U.RSRV_NUM4) RSRV_NUM4, ");
		parser.addSQL(" TO_CHAR(U.RSRV_NUM5) RSRV_NUM5, U.RSRV_STR1, U.RSRV_STR2, U.RSRV_STR3, ");
		parser.addSQL(" U.RSRV_STR4, U.RSRV_STR5, RSRV_STR6, RSRV_STR7, RSRV_STR8, RSRV_STR9, ");
		parser.addSQL(" RSRV_STR10, TO_CHAR(U.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
		parser.addSQL(" TO_CHAR(U.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
		parser.addSQL(" TO_CHAR(U.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, U.RSRV_TAG1, ");
		parser.addSQL(" U.RSRV_TAG2, U.RSRV_TAG3 ");
		parser.addSQL(" FROM TF_FH_USER U ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" AND U.USER_ID= :USER_ID ");
		parser.addSQL(" AND U.PARTITION_ID= MOD(TO_NUMBER( :USER_ID ),10000) ");

		return Dao.qryByParse(parser);
	}

	public static IDataset queryBackUserByDepartId(String departId, String isExtendTime, Pagination pagination) throws Exception
	{
		IData param = new DataMap();
		param.put("DEVELOP_DEPART_ID", departId);
		param.put("IS_EXTEND_TIME", isExtendTime);

		if (isExtendTime.equals("1"))
		{
			return Dao.qryByCodeParser("TF_F_USER", "QRY_BACK_USER_BY_DEPART_ID", param, pagination);
		} else
		{
			return Dao.qryByCodeParser("TF_F_USER", "QRY_BACK_USER_BY_DEPART_ID_FOR_NULL", param, pagination);
		}
	}

	public static void extendBackUserByDepartId(IDataset params) throws Exception
	{
		Dao.executeBatchByCodeCode("TF_F_USER", "EXTEND_BACK_USER_STAT_DATE", params);
	}

	public static IDataset queryBackUserBySerialNumber(String serialNumber) throws Exception
	{
		IData param = new DataMap();

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT A.SERIAL_NUMBER, A.USER_ID, TO_CHAR(A.OPEN_DATE, 'YYYY-MM-DD HH24:MI:SS') AS OPEN_DATE, A.DEVELOP_DATE, A.VALUEN2, B.AREA_CODE ");
		parser.addSQL(" FROM TS_S_USER_BACK A, TD_M_DEPART B ");
		parser.addSQL(" WHERE A.TAG = '0' AND A.DEVELOP_DEPART_ID = B.DEPART_ID AND B.VALIDFLAG = '0' ");
		parser.addSQL(" AND B.END_DATE > SYSDATE ");
		parser.addSQL(" AND A.SERIAL_NUMBER IN ( ");
		parser.addSQL(serialNumber);
		parser.addSQL(" ) ");

		return Dao.qryByParse(parser);
	}

	public static void saveBackUserExtendLog(IDataset params) throws Exception
	{
		Dao.executeBatchByCodeCode("TF_F_USER", "SAVE_BACK_USER_EXTEND_LOG", params);
	}

	public static IDataset queryBackUser(IData param, Pagination pagination) throws Exception
	{

		String isExtendTime = param.getString("IS_EXTEND_TIME", "");

		if (isExtendTime.equals("1"))
		{
			if (param.getString("TRADE_STAFF_ID", "").equals("") && param.getString("START_DATE", "").equals("") && param.getString("END_DATE", "").equals(""))
			{ // 针对需要之前的号码
				return Dao.qryByCodeParser("TF_F_USER", "QRY_BACK_USER_FOR_EXTEND_NEW", param, pagination);
			} else
			{
				if (!param.getString("START_DATE", "").equals(""))
				{
					param.put("ACCEPT_MONTH", param.getString("START_DATE", "").substring(5, 7));
				}

				return Dao.qryByCodeParser("TF_F_USER", "QRY_BACK_USER_FOR_EXTEND", param, pagination);
			}
		} else
		{
			return Dao.qryByCodeParser("TF_F_USER", "QRY_BACK_USER_FOR_NULL", param, pagination);
		}
	}

	public static IDataset getWideUserIVRInfoBySN(IData param) throws Exception
	{
		return Dao.qryByCode("TF_F_USER_WIDENET", "SEL_BYS_WIDENETIVR", param);
	}

	/**
	 * 20160601 <br/>
	 * 通过手机号码查询tf_f_user_4gtag信息
	 * 
	 * @param serial_number
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUser4GInfoBySerialNumber(String serial_number) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);

		SQLParser parser = new SQLParser(param);
		parser.addSQL("  select t.serial_number,t.lte_flow,t.month from tf_f_user_4gtag t ");
		parser.addSQL("  where  1=1 and ");
		parser.addSQL("  t.serial_number = :SERIAL_NUMBER");

		IDataset dataset = Dao.qryByParse(parser);

		return dataset;
	}

	/**
	 * 查询集团用户 角色
	 * 
	 * @param serialNumber
	 * @return
	 * @throws Exception
	 * @author hx
	 */
	public static IDataset getGrpRole(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT * FROM TF_F_RELATION_UU UU");
		parser.addSQL(" WHERE UU.SERIAL_NUMBER_B=:SERIAL_NUMBER ");
		parser.addSQL(" AND UU.RELATION_TYPE_CODE='20' ");
		parser.addSQL(" AND UU.ROLE_CODE_B='2' ");

		return Dao.qryByParse(parser);
	}

	/**
	 * REQ201607110001 关于优化过户界面限制的需求 获取集团单位过户,使用人默认使用次数.
	 * 
	 * @param custName
	 * @param psptId
	 * @return
	 * @throws Exception
	 * @author zhuoyingzhi
	 */
	public static int getGroupUsePersonDefaultCount(String custName, String psptId) throws Exception
	{
		IData param = new DataMap();
		param.put("CUST_NAME", custName);
		param.put("PSPT_ID", psptId);
		IDataset ds = Dao.qryByCode("TF_F_CUSTOMER", "SEL_REALNAME_LIMIT_BY_PSPT", param);
		int Lcount = 5;
		if (ds.size() != 0)
		{
			Lcount = ds.getData(0).getInt("LIMIT_COUNT", 5);
		} else
		{
			IDataset dsdefault = CommparaInfoQry.getCommparaAllCol("CSM", "8640", "0", "0898");
			Lcount = Integer.parseInt(dsdefault.getData(0).getString("PARA_CODE1", "5"));
		}
		return Lcount;
	}

	/**
	 * REQ201607110001 关于优化过户界面限制的需求 通过证件号码查询，证件号码使用次数
	 * 
	 * @param psptId
	 * @return
	 * @throws Exception
	 * @author zhuoyingzhi
	 */
	public static int getRealNameUserCountByUsePspt3(String psptId) throws Exception
	{
		// 加上对测试证件的过滤，该证件在测试环境存在100多万，sql语句执行不了。
		if ((StringUtils.equals("460001194910011234", psptId) || StringUtils.equals("460025199011260016", psptId)))
		{
			return 0;
		}
		IData param = new DataMap();
		param.put("PSPT_ID", psptId);
		IDataset realCounts = Dao.qryByCode("TF_F_USER", "SEL_REALNAME_USER_BY_USEPSPT3", param);

		if (IDataUtil.isNotEmpty(realCounts))
		{
			return realCounts.getData(0).getInt("REAL_COUNT");
		} else
		{
			return 0;
		}

	}

	/**
	 * 判断号码是否已销户
	 * 
	 * @param serial_number
	 * @return
	 * @throws Exception
	 */
	public static IDataset checkUserIsCancel(String serial_number) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select * from  tf_f_user  where SERIAL_NUMBER=:SERIAL_NUMBER order by UPDATE_TIME DESC ");
		return Dao.qryByParse(parser);
	}

	/**
	 * 判断号码是否为移动用户
	 * 
	 * @param serial_number
	 * @return
	 * @throws Exception
	 */
	public static IDataset checkUserIsMoblie(String serial_number) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select * from  res_numseg_hlr  where :SERIAL_NUMBER BETWEEN START_NUM AND END_NUM ");
		return Dao.qryByParse(parser, Route.CONN_RES);
	}

	/**
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset getTTWideNetBySn(String serial_number) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serial_number);
		return Dao.qryByCode("TF_F_USER", "SEL_BY_TTWIDENET", param);
	}

	public static IDataset getUserInfoAndproductInfoByuserInfo(String USER_ID) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", USER_ID);
		return Dao.qryByCode("TF_F_USER", "SEL_BY_USERINFOBYID", param);
	}
	
	public static IDataset getUserInfoForSyncUser(String USER_ID) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", USER_ID);
		return Dao.qryByCode("TF_F_USER", "SEL_FOR_SYNCUSERINFO", param);
	}
	
	
	/**
	 * 查询物联网数据是否同步
	 * @param cust_id
	 * @param page
	 * @return
	 * @throws Exception
	 * @author chenhh6
	 */
	public static IDataset queryUserInfoByCustId(String cust_id) throws Exception
	{
		IData param = new DataMap();
		param.put("CUST_ID", cust_id);
		return Dao.qryByCode("TF_F_CUST_GROUP_EXTEND", "SEL_BY_CUSTID_QUERYUSERINFO", param);
	}
	
	/**
	 * 宽带业务查询
	 * @param inparams
	 * @param pagination
	 * @return
	 * @throws Exception
	 */
	public static IDataset broadquerySnByUsrpid(IData inparams, Pagination pagination) throws Exception
	{
		IDataset dataset = new DatasetList();
		// 按证件查询(精确匹配)
		if ("1".equals(inparams.get("QUERY_MODE")))
		{
			dataset = Dao.qryByCode("TF_F_USER", "SEL_BY_BROAD_USRPID", inparams, pagination);
		}
		// 按客户名称+证件号码方式查询（精确匹配）
		else if ("2".equals(inparams.get("QUERY_MODE")))
		{
			dataset = Dao.qryByCode("TF_F_USER", "SEL_BY_BROAD_PSPTID_CUSTNAME", inparams, pagination);
		}
		
		return dataset;
	}

	public static IDataset getYearAfterIndate(String serialNumber) throws Exception
    {
        IData inParams = new DataMap();
        inParams.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TF_F_USER", "SEL_YEAR_AFTER_INDATE", inParams);
    }
	
	/**
	 * 开学抢红包送终端活动开发需求--新增活动规则
	 * @author zhangxing3
	 */
    public static IDataset getUserInfoByPsptProdID(String strPsptID,String strProductId) throws Exception
    {
        IData param = new DataMap();
        param.put("PSPT_ID", strPsptID);
        param.put("PRODUCT_ID", strProductId);
        return Dao.qryByCode("TF_F_USER", "SEL_USERINFO_BY_PSPTID_PRODID", param);
    }
	
	/**
	 * 通过手机号码判断是否红名单号码 <br/>
	 * 
	 * @param phoneNumber
	 * @return
	 * @throws Exception
	 */
	public static IDataset getTfOReduserInfo(IData param) throws Exception {

		 //红名单校验
        SQLParser parser2 = new SQLParser(param); 
        parser2.addSQL(" select t.* from TF_O_REDUSER_ACT t where t.SERIAL_NUMBER=:SERIAL_NUMBER and sysdate between START_DATE and END_DATE"); 
        IDataset infos2=  Dao.qryByParse(parser2,Route.CONN_CRM_CG); 
		return infos2;
		
	}

	/**
	 * 根据手机号码，查询tf_f_user表里remove_tag=0，acct_tag=2的用户数据
	 * wuhao5
	 * @param sn
	 * @return
	 * @throws Exception
	 */
	public static IData getActiveUserBySN(String sn) throws Exception {
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", sn);
		return Dao.qryByCodeOnlyOne("TF_F_USER", "SEL_ACTIVE_USERINFO_BY_SN", param);
	}
	public static IData getUserByWideIms(String userId, String relationTypeCode) throws Exception {
		IData param = new DataMap();
		param.put("USER_ID_B", userId);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		return Dao.qryByCodeOnlyOne("TF_F_USER", "SEL_USERINFO_BY_RELA_UU_PWI", param);
	}

	/**
	 * 根据固话Id,查询手机用户信息
	 * yuyz
	 * @param userId
	 * @param relationTypeCode
	 * @param roleCodeB
	 * @return
	 * @throws Exception
	 */
	public static IData getUserByImsAndRoleCode(String userId, String relationTypeCode,String roleCodeB) throws Exception {
		IData param = new DataMap();
		param.put("USER_ID_B", userId);
		param.put("ROLE_CODE_B",roleCodeB);
		param.put("RELATION_TYPE_CODE", relationTypeCode);
		return Dao.qryByCodeOnlyOne("TF_F_USER", "SEL_USERINFO_BY_RELA_UU_ROLEB", param);
	}

	public static IData qryUserInfoByUserId(String userId,String removeTag)throws  Exception{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("REMOVE_TAG", removeTag);
		return Dao.qryByCodeOnlyOne("TF_F_USER", "SEL_USER_BY_USERIDTAG", param);
	}

}