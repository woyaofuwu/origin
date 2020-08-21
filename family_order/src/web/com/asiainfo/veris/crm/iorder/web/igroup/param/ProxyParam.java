package com.asiainfo.veris.crm.iorder.web.igroup.param;

import com.ailk.biz.view.BizHttpHandler;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.AcctDayException;
import com.asiainfo.veris.crm.order.pub.exception.FuncrightException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.exception.UUException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.group.grpimsutil.GrpImsUtilView;

public class ProxyParam extends BizHttpHandler {
	/**
	 * adc 判断服务代码是否可用
	 *
	 * @throws Exception
	 * @anthor zhanghy9
	 */
	public void getDumpIdByajax() throws Exception {
		IData data = getData();
		IData temp1 = new DataMap();
		// logger.debug("data="+data.toString());
		String strBizInCode = data.getString("ACCESSNUMBER", "");
		String group_id = data.getString("GROUP_ID", "");
		String bizcode = data.getString("BIZ_CODE", "");
		String flag = "true";

		temp1.put("ACCESSNUMBER", strBizInCode);
		temp1.put("GROUP_ID", group_id);
		IDataset idata = CSViewCall.call(this, "CS.UserGrpInfoQrySVC.getDumpIdByajax", temp1);
		if (IDataUtil.isNotEmpty(idata)) {
			flag = "false";
		}
		temp1.put("BIZ_CODE", bizcode);
		idata = CSViewCall.call(this, "CS.UserGrpInfoQrySVC.getplatsvcBybizeservgroup", temp1);
		if (IDataUtil.isNotEmpty(idata)) {
			flag = "false";
		}
		temp1.clear();
		temp1.put("ISCHECKAACCESSNUMBER", flag);

		// logger.debug("flag="+flag);

		IData result = new DataMap();
		result.put("result", temp1);

		setAjax(result);

	}

	/**
	 * 获取基本接入号
	 *
	 * @throws Exception
	 * @author zhaozj6 2018-01-22
	 * @desc HXYD-YZ-XTYH-20170712-151-PR17013245基本接入号生成问题
	 */
	public void getBaseBizInCode() throws Exception {
		IData bizCode = new DataMap();
		bizCode.put("SUBSYS_CODE", "CSM");
		bizCode.put("PARAM_ATTR", "9017");
		bizCode.put("PARAM_CODE", "bizincode");
		IDataset bizCodeset = CSViewCall.call(this, "CS.CommparaInfoQrySVC.getCommparaInfoByBizCode", bizCode);
		if (bizCodeset == null || bizCodeset.size() < 1) {
			CSViewException.apperr(ParamException.CRM_PARAM_410);
		} else {
			IData bizData = bizCodeset.getData(0);
			String baseBizInCode = bizData.getString("PARA_CODE1");
			bizCode.clear();
			bizCode.put("SUBSYS_CODE", "CSM");
			bizCode.put("PARAM_ATTR", "9019");
			bizCode.put("PARAM_CODE", "BIZ_IN_CODE");
			// 自动生成规则前已有的基本接入号
			IDataset existBizInCodeSet = CSViewCall.call(this, "CS.CommparaInfoQrySVC.getCommparaInfoByBizCode",
					bizCode);
			IData existBizInCodeMap = new DataMap();
			if (IDataUtil.isNotEmpty(existBizInCodeSet)) {
				for (int i = 0; i < existBizInCodeSet.size(); i++) {
					IData bizCodeMap = existBizInCodeSet.getData(i);
					String existBizInCode = bizCodeMap.getString("PARA_CODE1");
					existBizInCodeMap.put(existBizInCode, existBizInCode);
				}
			}

			String bizInCode = getBaseBizInCode2(existBizInCodeMap, baseBizInCode);
			setAjax("EXTEND_CODE", bizInCode);
		}
	}

	/**
	 * 递归获取基本接入号
	 *
	 * @param baseBizInCode
	 * @return
	 * @throws Exception
	 * @author zhaozj6 2018-01-22
	 * @desc HXYD-YZ-XTYH-20170712-151-PR17013245基本接入号生成问题
	 */
	private String getBaseBizInCode2(IData existBizInCodeMap, String baseBizInCode) throws Exception {
		IData svrcodetailMap = CSViewCall.callone(this, "CS.SeqMgrSVC.getWlwBizCode", new DataMap());
		String seqId = svrcodetailMap.getString("seq_id", "");
		String bizInCode = baseBizInCode + seqId;
		if (existBizInCodeMap.containsKey(bizInCode)) {
			return getBaseBizInCode2(existBizInCodeMap, baseBizInCode);
		}

		return bizInCode;
	}

	/**
	 * mas校验服务代码
	 */

	public void checkAccessNumber() throws Exception {

		IData data = getData();
		IData temp1 = new DataMap();
		String strBizInCode = data.getString("ACCESSNUMBER", "");
		String group_id = data.getString("GROUP_ID", "");
		String bizcode = data.getString("BIZ_CODE", "");
		String extendcode = data.getString("BIZ_EXTEND_CODE", "");
		String flag = "true";

		temp1.put("ACCESSNUMBER", strBizInCode);
		temp1.put("GROUP_ID", group_id);
		temp1.put("EXTENDCODE", extendcode);
		IDataset idata = CSViewCall.call(this, "CS.UserGrpInfoQrySVC.getDumpIdByajax", temp1);
		if (IDataUtil.isNotEmpty(idata)) {
			// add by jinlt取消ADC的业务接入号是否重复判断
			// flag = "false";
		}
		temp1.put("BIZ_CODE", bizcode);
		idata = CSViewCall.call(this, "CS.UserGrpInfoQrySVC.getplatsvcBybizeservgroup", temp1);
		if (IDataUtil.isNotEmpty(idata)) {
			// add by jinlt取消ADC的业务接入号是否重复判断
			// flag = "false";
		}
		// add by jinlt HXYD-YZ-REQ-20160808-003-关于提交MAS和云MAS多级子账户优化需求 判断扩展码是否重复 start
		idata = CSViewCall.call(this, "CS.UserGrpInfoQrySVC.getExtendCodeByajax", temp1);
		if (IDataUtil.isNotEmpty(idata)) {
			flag = "false";
		}

		temp1.clear();
		temp1.put("ISCHECKAACCESSNUMBER", flag);

		IData result = new DataMap();
		result.put("result", temp1);

		setAjax(result);
	}

	/**
	 * 验证总机号码
	 * 
	 * @param bp
	 * @param data
	 * @return
	 * @throws Exception
	 * @author hemh 2018-01-30
	 */
	public void checkSuperTeleInfo() throws Exception {
		IData data = this.getData();

		IData result = new DataMap();

		// 查询号码的三户信息
		String serialNumber = data.getString("SERIAL_NUMBER");
		IData mebUCAInfoData = UCAInfoIntfViewUtil.qryMebUCAAndAcctDayInfoBySn(this, serialNumber);

		IData mebUserInfoData = mebUCAInfoData.getData("MEB_USER_INFO");
		IData mebCustInfoData = mebUCAInfoData.getData("MEB_CUST_INFO");
		IData mebAcctDayInfoData = mebUCAInfoData.getData("MEB_ACCTDAY_INFO");

		// 判断用户账期分布, 非自然月用户不让办理业务
		if (!GroupBaseConst.UserDaysDistribute.TRUE.getValue()
				.equals(mebAcctDayInfoData.getString("USER_ACCTDAY_DISTRIBUTION"))) {
			CSViewException.apperr(AcctDayException.CRM_ACCTDAY_15, serialNumber);
		}

		IData inparam = new DataMap();
		String eparchyCode = mebUserInfoData.getString("EPARCHY_CODE");
		inparam.put("USER_ID_B", mebUserInfoData.getString("USER_ID", ""));
		inparam.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);

		// 融合总机关系[25]
		inparam.put("RELATION_TYPE_CODE", "25");
		IData uuInfo_25 = CSViewCall.callone(this, "CS.RelaUUInfoQrySVC.getRelaUUInfoByUserRelarIdB", inparam);

		if (IDataUtil.isNotEmpty(uuInfo_25)) {
			CSViewException.apperr(GrpException.CRM_GRP_276);
		}

		// 融合总机关系[E3]
		inparam.put("RELATION_TYPE_CODE", "E3");
		IData uuInfo_E3 = CSViewCall.callone(this, "CS.RelaUUInfoQrySVC.getRelaUUInfoByUserRelarIdB", inparam);

		if (IDataUtil.isNotEmpty(uuInfo_E3)) {
			CSViewException.apperr(GrpException.CRM_GRP_276);
		}

		// VPMN关系
		inparam.put("RELATION_TYPE_CODE", "20");
		IData uuInfo_20 = CSViewCall.callone(this, "CS.RelaUUInfoQrySVC.getRelaUUInfoByUserRelarIdB", inparam);
		// VPCN关系
		inparam.put("RELATION_TYPE_CODE", "21");
		IData uuInfo_21 = CSViewCall.callone(this, "CS.RelaUUInfoQrySVC.getRelaUUInfoByUserRelarIdB", inparam);
		// 湖南的要求: VPMN或VPCN集团用户号码
		if (IDataUtil.isEmpty(uuInfo_20) || IDataUtil.isEmpty(uuInfo_21)) {
			// CSViewException.apperr(GrpException.CRM_GRP_432);
		}

		// 湖南判断总机号码是否属于VPMN集团,如果是,取VPMN短号
		if (IDataUtil.isNotEmpty(uuInfo_20)) {
			mebUserInfoData.put("FLAG", "1");
			mebUserInfoData.put("EXCHANGE_SHORT_SN", uuInfo_20.getString("SHORT_CODE", ""));
		} else {
			mebUserInfoData.put("FLAG", "0");
		}
		mebUserInfoData.put("EXCHANGETELE_SN", serialNumber);
		mebUserInfoData.put("CUST_NAME", mebCustInfoData.getString("CUST_NAME", ""));

		result.put("AJAX_DATA", mebUserInfoData);

		setAjax(result);

	}

	/**
	 * 验证短号
	 * 
	 * @param bp
	 * @param data
	 * @author hemh 2018-01-31
	 */
	public void validchk() throws Exception {
		IData data = this.getData();
		String short_code = data.getString("SHORT_CODE");
		String grp_user_id = data.getString("USER_ID");
		IData datatemp = new DataMap();
		datatemp.put("SHORT_CODE", short_code);
		datatemp.put("USER_ID", grp_user_id);
		datatemp.put("EPARCHY_CODE", data.getString("MEB_EPARCHY_CODE"));

		IData result = new DataMap();
		String userId = data.getString("USER_ID", ""); // 集团用户ID

		// 1、查询集团用户信息
		IData grpUserInfoData = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, userId);
		datatemp.put("USECUST_ID", grpUserInfoData.getString("CUST_ID", ""));
		GrpImsUtilView grpImsUtilView = new GrpImsUtilView();
		boolean flag = grpImsUtilView.checkImsShortCode(this, datatemp);
		datatemp.put("RESULT", flag);
		result.put("AJAX_DATA", datatemp);
		setAjax(result);

	}

	/**
     * @description 多媒体桌面电话成员新增短号码验证
     * @author hemh
     * @date 2018-02-02
     */
    public void Deskvalidchk() throws Exception
    {
    	IData data = this.getData();
        IData result = new DataMap();
        IData datatemp = new DataMap();
        datatemp.put("SHORT_CODE", data.getString("SHORT_CODE"));
        datatemp.put("USER_ID", data.getString("USER_ID"));
        GrpImsUtilView grpImsUtilView = new GrpImsUtilView();
        IDataset idataset = null;//grpImsUtilView.checkShortCodeForVnet(this, datatemp);
        boolean flag = true;
        if (IDataUtil.isNotEmpty(idataset))
        {
            flag = idataset.getData(0).getBoolean("RESULT");
            String error_msg = idataset.getData(0).getString("ERROR_MESSAGE");
            if (!flag)
            {
                datatemp.put("ERROR_MESSAGE", error_msg);
            }
        }
        datatemp.put("RESULT", flag);
        result.put("AJAX_DATA", datatemp);
        setAjax(result);
    }
	
	/**
	 * 查询开户代理商
	 * 
	 * @param cycle
	 * @throws Exception
	 * @author hemh 2018-02-02
	 */
	public void queryAgent() throws Exception {
		IData data = getData();
		data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
		IDataset dataset = CSViewCall.call(this, "SS.ChooseAgentSVC.queryAgent", data);
		setAjax(dataset);
	}
	
	/**
     * 成员新增短号验证
     * 
     * @param
     * @param indata
     * @throws Exception
     * @author hemh 2018-02-03
     */
    public void shortNumValidateSuperTeleMember() throws Exception
    {
    	IData indata = getData();
        IData inparam = new DataMap();
        inparam.put("SHORT_CODE", indata.getString("SHORT_NUM"));
        inparam.put("USER_ID", indata.getString("USER_ID_A"));
        IData validateData = CSViewCall.callone(this, "CS.GroupImsUtilSVC.shortCodeValidateSupTeleMeb", inparam);

        IData returnData = new DataMap();
        returnData.put("AJAX_DATA", validateData);
        setAjax(returnData);
    }
    
    /**
     * 校验是否可以做为话务员,只有多媒体桌面电话的普通成员才能做为话务员
     * 
     * @param
     * @param indata
     * @author hemh 2018-02-03
     */
    public void checkSuperTelOper() throws Exception
    {
    	IData indata = getData();
        IData param = new DataMap();
        param.put("CUST_ID", indata.getString("CUST_ID", ""));
        param.put("PRODUCT_ID", "2222");// 多媒体桌面电话

        IData userData = CSViewCall.callone(this, "CS.UserInfoQrySVC.getUserInfoByCstIdProIdForGrp", param);
        IData ajaxData = new DataMap();

        if (IDataUtil.isNotEmpty(userData))
        {
            int attendantNum = 0;
            param.clear();
            param.put("USER_ID", userData.getString("USER_ID", ""));
            IDataset userAttrList = CSViewCall.call(this, "CS.UserAttrInfoQrySVC.getUserAttrByUserIda", param);
            for (int i = 0; i < userAttrList.size(); i++)
            {
                IData userAttrData = userAttrList.getData(i);
                if ("BGAttendantNum".equals(userAttrData.getString("ATTR_CODE")))
                {
                    attendantNum = userAttrData.getInt("ATTR_VALUE");
                }
            }

            param.clear();
            param.put("PRODUCT_ID", "6130");
            String relationTypeCode = "";
            IData relaTypeCodeData = CSViewCall.callone(this, "CS.ProductCompInfoQrySVC.getRelaTypeCodeByProductId", param);
            if (IDataUtil.isNotEmpty(relaTypeCodeData))
            {
                relationTypeCode = relaTypeCodeData.getString("RELATION_TYPE_CODE");
            }

            param.clear();
            param.put("USER_ID_A", indata.getString("cond_EC_USER_ID", ""));
            param.put("RELATION_TYPE_CODE", relationTypeCode);
            param.put("ROLE_CODE_B", "3");// 话务员角色

            int bgAttendantNum = 0;

            // 融合总机关系
            IDataset relaInfos = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.getAllMebByUSERIDA", param);
            bgAttendantNum = relaInfos.size();
            if (bgAttendantNum + 1 > attendantNum)
            {
                ajaxData.put("RESULT", "false");
                ajaxData.put("ERROR_MESSAGE", "话务员数已经达到最大数,请修改集团话务员数之后在新增话务员!");
            }
            else
            {
                param.clear();
                param.put("USER_ID_B", indata.getString("MEB_USER_ID"));
                param.put("RELATION_TYPE_CODE", "E1");
                param.put(Route.ROUTE_EPARCHY_CODE, indata.getString("MEB_EPARCHY_CODE"));
                // 多媒体桌面电话关系
                IDataset uuInfos_E1 = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.getRelaUUInfoByUserRelarIdB", param);
                IDataset uuInfos_E1_Role = DataHelper.filter(uuInfos_E1, "ROLE_CODE_B=1");
                if (IDataUtil.isNotEmpty(uuInfos_E1_Role))
                {
                    ajaxData.put("RESULT", "true");
                }
                else
                {
                    ajaxData.put("RESULT", "false");
                    ajaxData.put("ERROR_MESSAGE", "此号码不能做为话务员号码,话务员号码要是多媒体桌面电话的普通成员!");

                }
            }
        }
        else
        {
            ajaxData.put("RESULT", "false");
            ajaxData.put("ERROR_MESSAGE", "查询集团资源数【话务员数】信息异常,业务不能继续!");
        }

        IData returnData = new DataMap();
        returnData.put("AJAX_DATA", ajaxData);
        setAjax(returnData);

    }
    /**
     * 查询主叫一号通号码个人用户信息
     * 
     * @author hemh 2018-02-06
     * 
     */
    public void querySerialnumberInfo() throws Exception
    {
    	IData data = getData();
    	boolean ctFlag = false;
        IData outResult = new DataMap();
        IData paramInfo = new DataMap();

        String memEparchCode = data.getString("MEB_EPARCHY_CODE"); // 成员所属地市编码
        String memSn = data.getString("MEB_SERIAL_NUMBER", "");
        String user_id = data.getString("MEB_USER_ID", "");

        // 查询成员用户信息
        String strMebSn = data.getString("SERIAL_NUMBER");
        IData mebUserInfoData = UCAInfoIntfViewUtil.qryMebUserInfoBySn(this, strMebSn);
        String strEparchCode = mebUserInfoData.getString("EPARCHY_CODE");
        String user_id_b = mebUserInfoData.getString("USER_ID", "");
        // 没有长途拨打权限的不能加外地号码作为副号码
        if (!ctFlag)
        {
            if (!memEparchCode.equals(strEparchCode))
            {
                CSViewException.apperr(FuncrightException.CRM_FUNCRIGHT_9, memSn, strMebSn);

            }
        }

        // 查询服务号码账期信息
        IData userAcctDayData = UCAInfoIntfViewUtil.qryMebUserAcctDayInfoUserId(this, user_id_b, strEparchCode);
        // 判断用户账期分布, 非自然月用户不让办理业务
        if (!GroupBaseConst.UserDaysDistribute.TRUE.getValue().equals(userAcctDayData.getString("USER_ACCTDAY_DISTRIBUTION")))
        {
            CSViewException.apperr(AcctDayException.CRM_ACCTDAY_15, strMebSn);
        }

        IData inparam = new DataMap();
        inparam.put("USER_ID_B", user_id_b);
        inparam.put("RELATION_TYPE_CODE", "E2");
        inparam.put(Route.ROUTE_EPARCHY_CODE, strEparchCode);

        IDataset relainfos = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.getUUByUserIdAB", inparam);

        if (IDataUtil.isEmpty(relainfos))
        {
            CSViewException.apperr(UUException.CRM_UU_82, strMebSn, memSn);
        }

        inparam.clear();
        inparam.put("USER_ID_A", relainfos.getData(0).getString("USER_ID_B"));
        inparam.put("USER_ID_B", user_id);
        inparam.put("RELATION_TYPE_CODE", "BY");
        inparam.put(Route.ROUTE_EPARCHY_CODE, strEparchCode);

        IDataset brelainfos = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.getUUByUserIdAB", inparam);

        if (IDataUtil.isEmpty(brelainfos))
        {
            CSViewException.apperr(UUException.CRM_UU_83, strMebSn, memSn, memSn);
        }

        paramInfo.put("ZUSER_ID", user_id_b);

        outResult.put("AJAX_DATA", paramInfo);
        setAjax(outResult);
    }
    
    /**
     * 查询被叫一号通号码个人用户信息
     * 
     * @author hemh 2018-02-06
     */

    public void queryBSerialnumberInfo() throws Exception
    {
    	boolean ctFlag = false;
    	IData data = getData();
        IData outResult = new DataMap();
        IData paramInfo = new DataMap();

        String memEparchCode = data.getString("MEB_EPARCHY_CODE"); // 成员所属地市编码
        String memSn = data.getString("MEB_SERIAL_NUMBER", "");

        // 查询成员用户信息
        String strMebSn = data.getString("BSERIAL_NUMBER");
        IData mebUserInfoData = UCAInfoIntfViewUtil.qryMebUserInfoBySn(this, strMebSn);
        String strEparchCode = mebUserInfoData.getString("EPARCHY_CODE");
        String user_id_b = mebUserInfoData.getString("USER_ID", "");
        // 没有长途拨打权限的不能加外地号码作为副号码
        if (!ctFlag)
        {
            if (!memEparchCode.equals(strEparchCode))
            {
                CSViewException.apperr(FuncrightException.CRM_FUNCRIGHT_9, memSn, strMebSn);

            }
        }
        // 查询服务号码账期信息
        IData userAcctDayData = UCAInfoIntfViewUtil.qryMebUserAcctDayInfoUserId(this, user_id_b, strEparchCode);
        // 判断用户账期分布, 非自然月用户不让办理业务
        if (!GroupBaseConst.UserDaysDistribute.TRUE.getValue().equals(userAcctDayData.getString("USER_ACCTDAY_DISTRIBUTION")))
        {
            CSViewException.apperr(AcctDayException.CRM_ACCTDAY_15, strMebSn);
        }

        IData inparam = new DataMap();
        inparam.put("USER_ID_B", user_id_b);
        inparam.put("RELATION_TYPE_CODE", "E2");
        inparam.put(Route.ROUTE_EPARCHY_CODE, strEparchCode);

        IDataset relainfos = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.getUUByUserIdAB", inparam);

        if (IDataUtil.isNotEmpty(relainfos))
        {
            CSViewException.apperr(UUException.CRM_UU_38, strMebSn);
        }

        inparam.clear();
        inparam.put("USER_ID_B", user_id_b);
        inparam.put("RELATION_TYPE_CODE", "96");
        inparam.put(Route.ROUTE_EPARCHY_CODE, strEparchCode);

        IDataset zrelainfos = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.getUUByUserIdAB", inparam);

        if (IDataUtil.isNotEmpty(zrelainfos))
        {
            for (int i = 0; i < zrelainfos.size(); i++)
            {
                inparam.clear();
                String zuser_id_a = zrelainfos.getData(i).getString("USER_ID_A", "");
                inparam.put("USER_ID_B", zuser_id_a);
                inparam.put("RELATION_TYPE_CODE", "E2");
                inparam.put(Route.ROUTE_EPARCHY_CODE, strEparchCode);
                relainfos.clear();

                relainfos = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.getUUByUserIdAB", inparam);

                if (IDataUtil.isNotEmpty(relainfos))
                {
                    CSViewException.apperr(UUException.CRM_UU_37, strMebSn, zuser_id_a);
                }
            }
        }

        inparam.clear();
        inparam.put("USER_ID_B", user_id_b);
        inparam.put("RELATION_TYPE_CODE", "BY");
        inparam.put(Route.ROUTE_EPARCHY_CODE, strEparchCode);

        IDataset brelainfos = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.getUUByUserIdAB", inparam);
        if (IDataUtil.isNotEmpty(brelainfos))
        {
            for (int i = 0; i < brelainfos.size(); i++)
            {
                inparam.clear();
                String buser_id_a = brelainfos.getData(i).getString("USER_ID_A", "");
                inparam.put("USER_ID_B", buser_id_a);
                inparam.put("RELATION_TYPE_CODE", "E2");
                inparam.put(Route.ROUTE_EPARCHY_CODE, strEparchCode);
                relainfos.clear();

                relainfos = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.getUUByUserIdAB", inparam);

                if (IDataUtil.isNotEmpty(relainfos))
                {
                    CSViewException.apperr(UUException.CRM_UU_36, strMebSn, buser_id_a);
                }
            }
        }
        paramInfo.put("BUSER_ID", user_id_b);
        outResult.put("AJAX_DATA", paramInfo);
        setAjax(outResult);
    }
}
