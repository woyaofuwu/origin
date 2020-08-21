package com.asiainfo.veris.crm.iorder.soa.group.ecintegration.check;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserImpuInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.group.common.query.GroupQueryBean;
import com.asiainfo.veris.crm.order.soa.group.groupunit.VpnUnit;


public class EcIntegrationMebChkImsVpmnBean extends EcIntegrationMebChkBaseBean {
	
	@Override
    public void checkOther(IData mebInfo) throws Exception
    {

    	// 校验短号
        IData userRuleResult = shortCodeValidateVpn(mebInfo.getString("SHORT_CODE"));

        if(IDataUtil.isEmpty(userRuleResult))
        {
            if(StringUtils.isNotEmpty(ecUserId))
            {

                //调用 业务规则校验 成员号码
                String tradeTypeCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ATTR_BIZ", new String[]
                        { "ID", "ID_TYPE", "ATTR_OBJ", "ATTR_CODE" }, "ATTR_VALUE", new String[]
                        { productId, "P", BizCtrlType.CreateMember, "TradeTypeCode" });

                userRuleResult = checkMebBaseInfoRule(ecUserId,mebInfo.getString("SERIAL_NUMBER"),BizCtrlType.CreateMember,tradeTypeCode,productId);

                if( "8000".equals(productId))
                {
                    // 调用业务规则，校验短号
                    IData param = new DataMap();
                    param.put("SHORT_CODE", mebInfo.getString("SHORT_CODE"));
                    param.put("USER_ID_A", ecUserId);
                    IData retData = VpnUnit.shortCodeValidateVpn(param);

                    if (IDataUtil.isNotEmpty(retData))
                    {
                        if (!retData.getBoolean("RESULT"))
                        {
                            userRuleResult.put(IMPORT_RESULT, false);
                            userRuleResult.put(IMPORT_ERROR, "入表前短号验证失败，请重新输入短号!"+retData.getString("ERROR_MESSAGE"));
                        }
                    }
                }
            }
            else
            {
                if( "8001".equals(productId))
                {
                    //融合VPMN 产品，如果是固话号码，要先办理 多媒体桌面电话 ，手机号码无此校验
                    userRuleResult = checkSnValideByVpn(mebInfo);
                }
                else if( "2222".equals(productId))
                {
                    // 多媒体桌面电视 的成员号码，必须为固话号码
                    userRuleResult = checkSnValideByIMS(mebInfo);
                }
                else if( "8000".equals(productId))
                {
                    // 集团VPMN 产品 成员号码校验
                    userRuleResult = checkSnValideByGroupVpmn(mebInfo);
                }
            }
		}
        mebInfo.putAll(userRuleResult);
    }
    
    public IData shortCodeValidateVpn(String shortCode) throws Exception{
    	IData checkResult = new DataMap();
    	//短号码校验
	   	if(StringUtils.isEmpty(shortCode)) {
	   		 checkResult.put(IMPORT_RESULT, false);
	   		 checkResult.put(IMPORT_ERROR, "短号码为空，请输入后再验证！");
	         return checkResult;
	   	}else if(shortCode.indexOf(" ")!=-1) {
	   		 checkResult.put(IMPORT_RESULT, false);
	   		 checkResult.put(IMPORT_ERROR, "短号码中含有空格，请去掉！");
	         return checkResult;
	   	}else if(shortCode.length()< 3 || shortCode.length() > 6) {
	   		checkResult.put(IMPORT_RESULT, false);
	   		checkResult.put(IMPORT_ERROR, "短号长度只能为3~6，请检查!");
	         return checkResult;
	   	}else if (!shortCode.startsWith("6")){
	   		checkResult.put(IMPORT_RESULT, false);
	   		checkResult.put(IMPORT_ERROR, "短号码必须以【6】开头，请检查!");
	   		return checkResult;
        }else if (shortCode.startsWith("60")){
        	checkResult.put(IMPORT_RESULT, false);
        	checkResult.put(IMPORT_ERROR, "短号码不能以【60】开头，请检查！");
        	return checkResult;
        }
	   	IDataset dataset = ParamInfoQry.getCommparaByCode("BMS", "259", "", "0898");
        if (IDataUtil.isNotEmpty(dataset))
        {
            IData commData = dataset.getData(0);
            StringBuilder strBuffer = new StringBuilder();
            strBuffer.append(commData.getString("PARA_CODE23"));
            strBuffer.append(commData.getString("PARA_CODE24"));
            strBuffer.append(commData.getString("PARA_CODE25"));

            String str = strBuffer.toString();
            if (0 <= str.indexOf("|" + shortCode + "|"))
            {
            	checkResult.put(IMPORT_RESULT, false);
            	checkResult.put(IMPORT_ERROR, "短号码不能是特殊代码，短号为【" + shortCode + "】请检查！");
            	return checkResult;
            }
        }
	    return checkResult;
    }


    public IData checkSnValideByVpn(IData mebInfo) throws Exception
	{
		IData checkResult = new DataMap();
		String serialNumber = mebInfo.getString("SERIAL_NUMBER");
		IDataset userInfos = UserInfoQry.getUserInfoBySn(serialNumber, "0");
		if(IDataUtil.isEmpty(userInfos))
		{
			checkResult.put(IMPORT_RESULT, false);
			checkResult.put(IMPORT_ERROR, "根据手机号码没有获取到有效的用户信息！");
			return checkResult;
		}

        IData userInfo =  userInfos.first();

        String userStateCodeSet = userInfo.getString("USER_STATE_CODESET", "");
        if (!userStateCodeSet.equals("0") && !userStateCodeSet.equals("N") && !userStateCodeSet.equals("00")) {
            checkResult.put(IMPORT_RESULT, false);
            checkResult.put(IMPORT_ERROR, "该用户处于非正常状态，无法办理此业务！");
            return checkResult;
        }

		// 1、 不能重复受理   查用户关系表，判断是否有uu关系
        IDataset idsUUVpmn = RelaUUInfoQry.getUserRelationVpmnByUserIdB(userInfo.getString("USER_ID"));
        if (IDataUtil.isNotEmpty(idsUUVpmn))
        {
           /* String ecUserID = idsUUVpmn.first().getString("USER_ID_A");
            IData ecUserProduct = UcaInfoQry.qryMainProdInfoByUserIdForGrp(ecUserID);
            if(IDataUtil.isNotEmpty(ecUserProduct) && "8001".equals(ecUserProduct.getString("PRODUCT_ID")))
            {*/

            checkResult.put(IMPORT_RESULT, false);
            checkResult.put(IMPORT_ERROR, "该客户已是VPMN成员，一个成员只能加入一个V网!");
            return checkResult;
        }

        // 2、 固话号码 先要受理 多媒体桌面电话
        String netTypeCode = userInfo.getString("NET_TYPE_CODE");
        if("05".equals(netTypeCode))
        {
            // 05 为 固话号码
            String userId = userInfo.getString("USER_ID", "");
            IData inparams = new DataMap();

            // 查用户关系表，判断是否有uu关系
            inparams.put("USER_ID_B", userId);
            inparams.put("RELATION_TYPE_CODE", "S1");
            IDataset idsUU = GroupQueryBean.qryRelationUUByUserIdB(inparams);
            if (IDataUtil.isNotEmpty(idsUU))
            {
                // 多媒体桌面电话 不允许重复订购，一个集团只能订购一条； 一个成员也只能订购一个集团的 多媒体桌面电话 ；
                String custIdUu = idsUU.first().getString("CUST_ID");
                if( !custId.equals(custIdUu))
                {
                    IDataset groupUu = CustomerInfoQry.getCustomerByCustID(custIdUu);
                    if(IDataUtil.isNotEmpty(groupUu))
                    {
                        checkResult.put(IMPORT_RESULT, false);
                        checkResult.put(IMPORT_ERROR, "该号码已开通GROUP_ID【" + groupUu.first().getString("GROUP_ID") + "】的多媒体桌面电话，不能添加本集团的融合V网产品");
                        return checkResult;
                    }
                    else
                    {
                        checkResult.put(IMPORT_RESULT, false);
                        checkResult.put(IMPORT_ERROR, "该号码已开通CUST_ID【"+custIdUu+"】的多媒体桌面电话，不能添加本集团的融合V网产品");
                        return checkResult;
                    }
                }
            }
            else
            {
                // 没有开通多媒体桌面电话 ,需要在前台校验，该号码是否 是添加到多媒体桌面电话
                checkResult.put(IMPORT_FLAG, false);
                checkResult.put(IMPORT_ERROR, "此号码未开通【多媒体桌面电话】，也未添加至【多媒体桌面电话的成员列表】中，如需开通【融合V网】，请先添加至【多媒体桌面电话】的成员列表中！");
                return checkResult;
            }
        }

        // 3、校验短号，是否已受理
        String shortCode = mebInfo.getString("SHORT_CODE");
        IDataset shortCodeUU = RelaUUInfoQry.qryRelationUUBycustIdAndShortCodeAndRale(custId,"20",shortCode);
        if(IDataUtil.isNotEmpty(shortCodeUU))
        {
            checkResult.put(IMPORT_RESULT, false);
            checkResult.put(IMPORT_ERROR, "该短号【"+shortCode+"】已订购该产品，请更换短号！");
            return checkResult;
        }

		return checkResult;
	}

    /**
     * 集团v网 ，成员受理校验
     * @param 、 SHORT_CODE  SERIAL_NUMBER
     * */
    public IData checkSnValideByGroupVpmn(IData mebInfo) throws Exception
	{
	    String err = "";
		IData checkResult = new DataMap();
		String serialNumber = mebInfo.getString("SERIAL_NUMBER");
		IDataset userInfos = UserInfoQry.getUserInfoBySn(serialNumber, "0");

		if(IDataUtil.isEmpty(userInfos))
		{
			checkResult.put(IMPORT_RESULT, false);
			checkResult.put(IMPORT_ERROR, "根据手机号码没有获取到有效的用户信息！");
			return checkResult;
		}

        IData userInfo = userInfos.first();
        String netTypeCode = userInfo.getString("NET_TYPE_CODE");
        if (!"00".equals(netTypeCode))
        {
            err = "网别不是（00）手机号的用户不能加入VPMN集团！";
            checkResult.put(IMPORT_RESULT, false);
            checkResult.put(IMPORT_ERROR, err);
            return checkResult;
        }

        String userStateCodeSet = userInfo.getString("USER_STATE_CODESET", "");
        if (!userStateCodeSet.equals("0") && !userStateCodeSet.equals("N") && !userStateCodeSet.equals("00")) {
            checkResult.put(IMPORT_RESULT, false);
            checkResult.put(IMPORT_ERROR, "该用户处于非正常状态，无法办理此业务！");
            return checkResult;
        }

		// 1、 不能重复受理   查用户关系表，判断是否有uu关系
        IDataset idsUUVpmn = RelaUUInfoQry.getUserRelationVpmnByUserIdB(userInfo.getString("USER_ID"));
        if (IDataUtil.isNotEmpty(idsUUVpmn))
        {
            String strUserIda = idsUUVpmn.first().getString("USER_ID_A", "");

            // 查询集团用户信息
            IDataset idUsers = UserInfoQry.getUserInfoByUserIdTag(strUserIda,"0");
            if (IDataUtil.isEmpty(idUsers))
            {
                err = "判断VPMN成员时，根据集团用户标识查询集团用户信息不存在！USER_ID=" + strUserIda;
                checkResult.put(IMPORT_RESULT, false);
                checkResult.put(IMPORT_ERROR, err);
                return checkResult;
            }

            // 得到集团客户标识
            String strCustId = idUsers.first().getString("CUST_ID");

            // 查询集团客户信息
            IDataset groupUu = CustomerInfoQry.getCustomerByCustID(strCustId);
            if (IDataUtil.isEmpty(groupUu))
            {
                err = "判断VPMN成员时，根据集团客户标识查询集团客户信息不存在！CUST_ID=" + strCustId;
                checkResult.put(IMPORT_RESULT, false);
                checkResult.put(IMPORT_ERROR, err);
                return checkResult;
            }

            // 得到集团客户编码、名称
            String strGroupId = groupUu.first().getString("GROUP_ID");
            String strGroupName = groupUu.first().getString("CUST_NAME");

            err = "该客户已是【" + strGroupId + strGroupName + "】的VPMN成员。一个成员只能加入一个V网，不能再次加入！";
            checkResult.put(IMPORT_RESULT, false);
            checkResult.put(IMPORT_ERROR, err);
            return checkResult;
        }

        //2、 判断是否VOLTE用户
        IDataset svcDataset = UserSvcInfoQry.getUserSvcByUserIdAndSvcId(userInfo.getString("USER_ID"), "190");
        if(IDataUtil.isNotEmpty(svcDataset))
        {
            //VOLTE用户
            checkResult.put(IMPORT_RESULT, false);
            checkResult.put(IMPORT_ERROR, "由于您是VOLTE用户，不可以办理【国开行用户开通锚定功能服务86128】。");
            return checkResult;
        }

        //3、 办理VPMN成员新增业务已达2次，请于下账期办理。
        IData map = new DataMap();
        map.put("USER_ID", userInfo.getString("USER_ID"));
        map.put("START_DATE", DiversifyAcctUtil.getFirstTimeThisAcct(userInfo.getString("USER_ID")));
        map.put("END_DATE", DiversifyAcctUtil.getLastTimeThisAcctday(userInfo.getString("USER_ID"), null));
        map.put("TRADE_TYPE_CODE", "3034");
        IDataset results2 = TradeInfoQry.getHisMainTradeByUserIdAndDate(map);
        if (IDataUtil.isNotEmpty(results2) && results2.size() > 1)
        {
            err = "您" + DiversifyAcctUtil.getUserAcctDescMessage(userInfo.getString("USER_ID"), "0") + "办理VPMN成员新增业务已达2次，请于下账期办理。";
            checkResult.put(IMPORT_RESULT, false);
            checkResult.put(IMPORT_ERROR, err);
            return checkResult;
        }

        // 3、校验短号，是否已受理
        String shortCode = mebInfo.getString("SHORT_CODE");
        IDataset shortCodeUU = RelaUUInfoQry.qryRelationUUBycustIdAndShortCodeAndRale(custId,"20",shortCode);
        if(IDataUtil.isNotEmpty(shortCodeUU))
        {
            checkResult.put(IMPORT_RESULT, false);
            checkResult.put(IMPORT_ERROR, "该短号【"+shortCode+"】已订购该产品，请更换短号！");
            return checkResult;
        }

		return checkResult;
	}


	/**
     * 多媒体桌面电话 成员号码 特殊校验：
     *
     *  多媒体桌面电话只能添加固话号码
     * */
	public IData checkSnValideByIMS(IData mebInfo)throws Exception
    {
        IData checkResult = new DataMap();
        String serialNumber = mebInfo.getString("SERIAL_NUMBER");
        IDataset userInfos = UserInfoQry.getUserInfoBySn(serialNumber, "0");

        if(IDataUtil.isEmpty(userInfos))
        {
            checkResult.put(IMPORT_RESULT, false);
            checkResult.put(IMPORT_ERROR, "根据手机号码没有获取到有效的用户信息！");
            return checkResult;
        }

        // 1、 固话号码判断
        IData userInfo = userInfos.first();

        String userStateCodeSet = userInfo.getString("USER_STATE_CODESET", "");
        if (!userStateCodeSet.equals("0") && !userStateCodeSet.equals("N") && !userStateCodeSet.equals("00")) {
            checkResult.put(IMPORT_RESULT, false);
            checkResult.put(IMPORT_ERROR, "该用户处于非正常状态，无法办理此业务！");
            return checkResult;
        }


        String netTypeCode = userInfo.getString("NET_TYPE_CODE");
        if(!"05".equals(netTypeCode))
        {

            checkResult.put(IMPORT_ERROR, "该号码不是固话号码，不能受理【多媒体桌面电话】产品");
            checkResult.put(IMPORT_RESULT, false);
            return checkResult;
        }

        // 2、 用户校验
        IDataset RelauuInfos = RelaUUInfoQry.getUserRelationByUserIdB(userInfo.getString("USER_ID"));
        if (IDataUtil.isNotEmpty(RelauuInfos))
        {
            boolean vFlag = false;
            for (int i = 0; i < RelauuInfos.size(); i++)
            {
                IData RelauuInfo = (IData) RelauuInfos.get(i);
                String relationTypeCode = RelauuInfo.getString("RELATION_TYPE_CODE");

                // 需要过滤普通V网情况
                if ("20".equals(relationTypeCode))
                {
                    // 判断是否融合V网成员
                    checkResult.put(IMPORT_ERROR, "该用户还是【融合V网成员】，请退出【融合V网】之后，再退出【桌面电话成员】！");
                    checkResult.put(IMPORT_RESULT, false);
                    vFlag = true;
                    break;
                }
                else if ("S1".equals(relationTypeCode))
                {
                    // 判断是否多媒体桌面电话成员
                    checkResult.put(IMPORT_ERROR, "该用户已开通【多媒体桌面电话】成员，不能重复开通！");
                    checkResult.put(IMPORT_RESULT, false);
                    vFlag = true;
                    break;
                }
                else if ("S2".equals(relationTypeCode))
                {
                    // 判断是否融合一号通成员
                    checkResult.put(IMPORT_ERROR, "该用户还是【融合一号通成员】，请退出【融合一号通】之后，再退出【桌面电话成员】！");
                    checkResult.put(IMPORT_RESULT, false);
                    vFlag = true;
                    break;
                }
                else if ("S3".equals(relationTypeCode))
                {
                    // 判断是否融合总机成员
                    checkResult.put(IMPORT_ERROR, "该用户还是【融合总机成员】，请退出【融合总机】之后，再退出【桌面电话成员】！");
                    checkResult.put(IMPORT_RESULT, false);
                    vFlag = true;
                    break;
                }
            }
            if(vFlag)
            {
                return checkResult;
            }

        }

        // 3、固定终端校验
        IDataset userImpu = UserImpuInfoQry.queryUserImpuInfo(userInfo.getString("USER_ID"));
        if(IDataUtil.isNotEmpty(userImpu))
        {
            String RSRV_STR1 = userImpu.getData(0).getString("RSRV_STR1", "");
            // 判断当前号码是否是无卡PC客户端IMS用户
            if (!"0".equals(RSRV_STR1))
            {
                checkResult.put(IMPORT_RESULT, false);
                checkResult.put(IMPORT_ERROR, "【固定终端IMS用户】才能新增为桌面电话成员!");
                return checkResult;
            }
        }


        // 5、校验短号，是否已受理
        String shortCode = mebInfo.getString("SHORT_CODE");
        IDataset shortCodeUU = RelaUUInfoQry.qryRelationUUBycustIdAndShortCodeAndRale(custId,"S1",shortCode);
        if(IDataUtil.isNotEmpty(shortCodeUU))
        {
            checkResult.put(IMPORT_RESULT, false);
            checkResult.put(IMPORT_ERROR, "该短号【"+shortCode+"】已订购该产品，请更换短号！");
            return checkResult;
        }

        return checkResult;
    }


    /**
     *
     * 调用公共业务规则，校验成员号码
     *
     * */
    public IData checkMebBaseInfoRule(String grpUserId, String mebSN, String operType, String tradeTypeCode, String ecOfferCode) throws Exception
    {
        IData result = new DataMap();
        try {
            String svc = "";
            if (BizCtrlType.CreateMember.equals(operType)) {
                svc = "CS.chkGrpMebOrder";
            } else if (BizCtrlType.ChangeMemberDis.equals(operType)) {
                svc = "CS.chkGrpMebChg";
            } else if (BizCtrlType.DestoryMember.equals(operType)) {
                svc = "CS.chkGrpMebDestory";
            }

            IData conParam = new DataMap();
            if ("8001".equals(ecOfferCode))  // 融合V网
            {
                conParam.put("IF_CENTRETYPE", "2");
            }
            conParam.put("CHK_FLAG", "BaseInfo");
            conParam.put("USER_ID", grpUserId);
            conParam.put("SERIAL_NUMBER", mebSN);
            conParam.put("TRADE_TYPE_CODE", tradeTypeCode);
            conParam.put("PRODUCT_ID", ecOfferCode);
            CSAppCall.call( svc, conParam);
        }
        catch (Exception e)
        {
            result.put(IMPORT_RESULT,false);
            result.put(IMPORT_ERROR,e.getMessage().length()>100 ? e.getMessage().substring(0,100) : e.getMessage());
        }

	    return result;
    }
}
