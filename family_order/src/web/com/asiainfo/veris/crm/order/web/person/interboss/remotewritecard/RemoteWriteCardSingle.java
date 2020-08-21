
package com.asiainfo.veris.crm.order.web.person.interboss.remotewritecard;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class RemoteWriteCardSingle extends PersonBasePage
{

    protected static Logger log = Logger.getLogger(RemoteWriteCardSingle.class);
   
	/**
	 * 异地写USIM卡
	 * @param cycle
	 * @throws Exception
	 */
    public void applyRemoteWrite(IRequestCycle cycle) throws Exception {
        IData params = getData();
        String changeCardTag = params.getString("CHANGE_CARD_TAG");
        if("2".equals(changeCardTag)){
        	params.put("BIZ_TYPE", "1016");//跨区换卡
		}else{
			params.put("BIZ_TYPE", "1012");//跨区补卡
		}
        IData simCardInfo = CSViewCall.call(this, "SS.RemoteWriteCardSVC.applyRemoteWrite", params).getData(0);
        if(simCardInfo!=null&&!simCardInfo.isEmpty()){
	    	simCardInfo.put("IS_RETURN", "1");	    	
        }else{
        	CSViewException.apperr(CrmCommException.CRM_COMM_103, "调用远程申请写卡数据失败");
        }
        setAjax(simCardInfo);        
    }
	/**
	 * 异地写卡结果回传及申请开通
	 * @param cycle
	 * @throws Exception
	 */
    public void applyResultActive(IRequestCycle cycle) throws Exception {
    	
        IData data = getData();
        IData resInfo = CSViewCall.call(this, "SS.RemoteWriteCardSVC.applyResultActive", data).getData(0);
        if (resInfo != null && !resInfo.isEmpty() && "0".equals(resInfo.getString("X_RESULTCODE"))) {
            if (resInfo != null) {
                resInfo.put("IS_RETURN", "1");
            }
        } else {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "跨区写卡激活失败");
        }
        setAjax(resInfo);
    }

    public void queryNewSimInfo(IRequestCycle cycle) throws Exception
    {
        IData params = getData();
        setNewCard(params);
    }

    public abstract IData getUserInfo();

    /**
     * 查询后设置页面信息
     */
    public void queryCustInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        /*if(data.getString("IDCARDNUM","").trim().equals("")){
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "证件号码不能为空！");
        }*/
        data.put("BIZ_TYPE", "1012");
        data.put("SERIAL_NUMBER", data.getString("MOBILENUM"));
        data.put("ID_ITEM_RANGE", data.getString("MOBILENUM"));
        data.put("IDTYPE", "01");
		IDataset cardType = CSViewCall.call(this, "SS.RemoteResetPswdSVC.queryCardType", data);

		if("1".equals(cardType.first().getString("result"))) {
		    CSViewException.apperr(CrmCommException.CRM_COMM_103, "本省号码无法办理异地业务！");
        }

        IDataset custInfos = CSViewCall.call(this, "SS.RemoteWriteCardSVC.queryRemoteWriteCustomer", data);
        if (IDataUtil.isEmpty(custInfos)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "查询用户资料（异地）为空！");
        }
        IData custInfo = custInfos.getData(0);

        /*        if(!custInfo.getString("IDCARDNUM","").trim().equals(data.getString("IDCARDNUM","").trim())){
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "扫描证件号码和归属省证件号码不一致，不能办理此业务！");
                }*/

        if (!"00".equals(custInfo.getString("USER_STATE_CODESET"))) {
            if ("02".equals(custInfo.getString("USER_STATE_CODESET"))) {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "该用户处于停机状态，不能办理此业务！");
            } else {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "530000:非正常在网用户不能办理远程写卡业务！");
            }
        }

        /*        IData resMPayInfo = CSViewCall.call(this, "SS.RemoteWriteCardSVC.queryMPayInfo", data).getData(0);
                if ("0".equals(resMPayInfo.getString("BIZ_STATE_CODE")) || "1".equals(resMPayInfo.getString("BIZ_STATE_CODE")))
                {
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "530042:该客户已经订购了手机支付业务，请客户直接联系归属地10086，由归属地客服接手写卡服务！");
                }*/

        IData custInfo1 = new DataMap();
        custInfo1.putAll(data);
        custInfo1.putAll(custInfo);
        IDataset outputq = CSViewCall.call(this, "SS.RemoteWriteCardSVC.getStaticValue", custInfo1);// data ) ;
        IData custInfo12 = outputq.getData(0);
        custInfo.putAll(custInfo12);
        data.put("PROVINCE_CODE", custInfo.getString("COP_SI_PROV_CODE"));
        setCondition(data);
        if("0".equals(cardType.getData(0).getString("IS_JXH"))){
        	custInfo.put("ISJXH", "是");
        }else{
        	custInfo.put("ISJXH", "否");
        }
        String sCardType = cardType.getData(0).getString("CARD_TYPE");
        IData authCustInfo = custInfo.getData("AUTH_CUST_INFO", new DataMap());
        authCustInfo.put("IS_JXH", cardType.getData(0).getString("IS_JXH"));
        authCustInfo.put("IS_SHIMING", cardType.getData(0).getString("IS_SHIMING"));
    	String cardName = this.getPageUtil().getStaticValue("TD_S_COMMPARA", new String[]{"SUBSYS_CODE","PARAM_ATTR","PARAM_CODE","PARA_CODE1"}, 
    			"PARAM_NAME", new String[]{"CSM","149","CARDTYPE",sCardType});
    	//对卡类型的处理配在数据库中  2不可办理 1提示信息但可办理 0可以办理
    	String cardRetn = this.getPageUtil().getStaticValue("TD_S_COMMPARA", new String[]{"SUBSYS_CODE","PARAM_ATTR","PARAM_CODE","PARA_CODE1"}, 
    			"PARA_CODE2", new String[]{"CSM","149","CARDTYPE",sCardType});
    	if(StringUtils.isEmpty(cardName)){
    		//没查到数据就设置默认值
    		authCustInfo.put("CARD_NAME", "非标准实体卡");
    		authCustInfo.put("CARD_RETN", "2");
    	}else{
    		//如果查到为2的数据则不设置查询后页面信息 不能继续办理
    		if(!"2".equals(cardRetn)){
    			setInfo(custInfo);
    		}
    		authCustInfo.put("CARD_NAME", cardName);
    		authCustInfo.put("CARD_RETN", cardRetn);//2拦截 1提示 0可以办理
    	}
        setAjax(authCustInfo);
    }

    public abstract void setCustInfo(IData editInfo);

    public abstract void setInfo(IData info);

    public abstract void setNewCard(IData a);

    public abstract void setOldCard(IData a);

    public abstract void setUserInfo(IData userInfo);

    public abstract void setCondition(IData condition);

    /*    *//**
             * 业务提交
             * 
             * @param cycle
             * @throws Exception
             */
    /*
     * public void writeCardActive(IRequestCycle cycle) throws Exception { IData data = getData(); IData resultInfo =
     * CSViewCall.call(this, "SS.RemoteWriteCardSVC.writeCardActive", data).getData(0); setAjax(resultInfo); }
     */

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData custInfo = data.getData("AUTH_CUST_INFO");
        String custInfoStr = data.getString("AUTH_CUST_INFO");
        if (IDataUtil.isNull(custInfo) && StringUtils.isNotBlank(custInfoStr)) {
            custInfo = new DataMap(custInfoStr);
        }
        if (IDataUtil.isNull(custInfo)) {
            custInfo = new DataMap();
        }
        data.put("AUTH_CUST_INFO", custInfo);
        data.put("SERIAL_NUMBER", data.getString("MOBILENUM"));

        //第一步，发起方空卡处理包括资源预占，ki,opc解密
        IDataset resinfos = CSViewCall.call(this, "SS.RemoteWriteCardSVC.applyResultActiveCallRes", data);

        if (resinfos != null && resinfos.size() > 0 && resinfos.getData(0).getString("X_RESULTCODE", "").trim().equals("0")) {
            System.out.println("RemoteWriteCardSingle.javaxxxxxxxxxxxxxxxxxxxx150(onTradeSubmit) " + resinfos.getData(0));
        } else {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, resinfos.getData(0).getString("X_RESULTINFO", "").trim());
        }

        //第三步，人像比对受理单编号与照片编号同步接口调用
        CSViewCall.call(this, "SS.RemoteWriteCardSVC.SynPicId", data);

        //第四步，调用接口通知归属省跨区补卡
       
       /* data.put("KI", "C314E921554A5B28CF60DAA661722B01");
        data.put("OPC", "860C0271B0CB8E50D135DF5B8B82179B"); */       
        
        data.put("KI", resinfos.getData(0).getString("KI", "").trim());
        data.put("OPC", resinfos.getData(0).getString("OPC", "").trim());        
        
        IDataset resultInfo = CSViewCall.call(this, "SS.RemoteWriteCardSVC.applyResultActive", data);
        if (resultInfo == null || resultInfo.isEmpty() || !"0000".equals(resultInfo.getData(0).getString("X_RSPCODE", ""))) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "跨区写卡激活失败："+resultInfo.getData(0).getString("X_RSPDESC", ""));
        }

/*        String provCode = resultInfo.getData(0).getString("HSNDUNS", "");
        if (provCode.length() > 1) {
            data.put("HOME_PROV", provCode.substring(0, provCode.length() - 1));
        }*/
        
        data.put("HOME_PROV", data.getString("PROVINCE_CODE","").trim());

        //跨区补卡流程优化：前台操作写卡成功激活提交时，若调用网状网激活接口失败自动返销已经生成的换卡工单，调用资源返销已用卡资源（采用将第二步后移到调用iboss之后）
        //第二步，发起方登记台账，如果有问题还可以直接事务回退，中止业务受理
        IDataset tradeInfo = CSViewCall.call(this, "SS.RemoteWriteCardRegSVC.tradeReg", data);

        data.put("TRADE_ID", tradeInfo.getData(0).getString("TRADE_ID", ""));
        //第五步，归属省补卡业务成功受理后，记录业务发起方实名认证信息        
        CSViewCall.call(this, "SS.RemoteWriteCardSVC.logRealNameInfo", data);
        setAjax(tradeInfo);
        
        /*----------------------------------------如下是之前online的代码
        IData data = getData();
        //data.put("PIC_ID", "847f8595546c419faa8c0e9fc1bc1942");//847f8595546c419faa8c0e9fc1bc1942
        System.out.println("RemoteWriteCardSingle.javaxxxxxxxxxxxxxxxxxxxx143(onTradeSubmit) "+data);
        IDataset resinfos = CSViewCall.call(this, "SS.RemoteWriteCardSVC.applyResultActiveCallRes", data);
        System.out.println("RemoteWriteCardSingle.javaxxxxxxxxxxxxxxxxxxxx145(onTradeSubmit) "+resinfos);
        
        if (resinfos != null && resinfos.size() > 0 && resinfos.getData(0).getString("X_RESULTCODE", "").trim().equals("0")) {
            System.out.println("RemoteWriteCardSingle.javaxxxxxxxxxxxxxxxxxxxx150(onTradeSubmit) "+resinfos.getData(0));
        }else{
            CSViewException.apperr(CrmCommException.CRM_COMM_103, resinfos.getData(0).getString("X_RESULTINFO", "").trim()); 
        }
        
        //测试
        //data.put("ReqSeq", System.currentTimeMillis());
        
        IData param = new DataMap();
        param.put("TRANSACTION_ID", data.getString("ReqSeq"));
        param.put("SERIAL_NUMBER", data.getString("MOBILENUM"));
        param.put("OPER_CODE", getVisit().getStaffId());
        param.put("CHANNEL_ID", "CRM");
        param.put("CUST_NAME", data.getString("HIDDEN_CUST_NAME"));
        
        String starLevel = "";
        //因河南省不按照规范返回给海南省的星级字段为空，造成海南营业厅报错（客户星级不能为空）,海南针对此情况设置默认值做兼容处理
        if (data.getString("LEVEL") == null || data.getString("LEVEL", "").trim().equals("")) {
            data.put("LEVEL", "09");//默认09未评级
        }
        starLevel = data.getString("LEVEL","09");//默认09未评级
        
        String chargeFee = "1000";//按分计算
        if (StringUtils.equals(starLevel,"04") || StringUtils.equals(starLevel,"05")
                || StringUtils.equals(starLevel,"06") || StringUtils.equals(starLevel,"07")) {//该客户为四星及以上客户,卡费免费！
            chargeFee = "0";
        }
        
        param.put("CREDIT_LEVEL", starLevel);
        param.put("CHARGE_FEE", chargeFee);
        param.put("CARD_FEE", "0");
        param.put("CARD_NUMBER", data.getString("EMPTY_CARD_ID"));
        param.put("ICCID", data.getString("ICCID"));
        param.put("CUST_CERT_NO", data.getString("HIDDEN_IDCARDNUM"));
        param.put("ORD_CODE", data.getString("PIC_ID"));        
        param.put("IN_MODE_CODE", "0");     
        param.put("HOME_PROV", data.getString("PROVINCE_CODE","").trim());

        System.out.println("RemoteWriteCardSingle.javaxxxxxxxxxxxxxxxxxxxx166 "+param);   
        
        IData reCardResultInfo = CSViewCall.call(this, "SS.ChangeCardSVC.reCardResult", param).getData(0);        
        
        data.put("KI", resinfos.getData(0).getString("KI","").trim());
        data.put("OPC", resinfos.getData(0).getString("OPC","").trim());
        IDataset resultInfo = CSViewCall.call(this, "SS.RemoteWriteCardSVC.applyResultActive", data);

        log.error("RemoteWriteCardSingle.javaxxxxxxxxxxxxxxxxxxxx176" + reCardResultInfo);
        String returnCode = reCardResultInfo.getString("RETURN_CODE");
        String returnMessage = reCardResultInfo.getString("RETURN_MESSAGE");
        if (!"0000".equals(returnCode)) {
            CSViewException.apperr( CrmCommException.CRM_COMM_1, returnCode,returnMessage);
        }
        
        resultInfo.getData(0).put("TRADE_ID",reCardResultInfo.getString("TRADE_ID"));
        resultInfo.getData(0).put("ORDER_ID","0");
        log.error("RemoteWriteCardSingle.javaxxxxxxxxxxxxxxxxxxxx180" + resultInfo);
        System.out.println("RemoteWriteCardSingle.javaxxxxxxxxxxxxxxxxxxxx181 "+resultInfo);
        
        //调用东软人像ID同步
        data.put("TRADE_ID", resultInfo.getData(0).getString("TRADE_ID",""));
        //System.out.println("RemoteWriteCardSingle.javaxxxxxxxxxxxxxxxxxxxx185 "+reCardResultInfo.getString("TRADE_ID"));
        log.error("RemoteWriteCardSingle.javaxxxxxxxxxxxxxxxxxxxx186" + data);
        CSViewCall.call(this, "SS.RemoteWriteCardSVC.SynPicId", data);
        
        setAjax(resultInfo);
        */}

    // 写卡结果回传
    public void writeCardResultback(IRequestCycle cycle) throws Exception
    {
        IData params = getData("cond", true);
        IData resultInfo = CSViewCall.call(this, "SS.RemoteWriteCardSVC.writeCardResultback", params).getData(0);
        setAjax(resultInfo);
    }

    // 加载打印
    public void loadPrintData(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData returnData = new DataMap();
        IDataset rePrintDatas = CSViewCall.call(this, "SS.RemoteWriteCardSVC.loadPrintData", data);
        returnData.put("PRINT_DATA", rePrintDatas);
        setAjax(returnData);
    }

    /**
     * @Description 构建打印发票数据
     * @param cycle
     */
    public void printTrade(IRequestCycle cycle) throws Exception
    {
        IData inputData = this.getData();
        inputData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        inputData.put("STAFF_NAME", getVisit().getStaffName());
        inputData.put("DEPART_NAME", getVisit().getDepartName());
        // 服务返回结果集
        IDataset result = CSViewCall.call(this, "SS.RemoteWriteCardSVC.printTrade", inputData);
        // 设置页面返回数据
        setAjax(result);
    }

    /**
     * 实名制认证工单下发
     * @param cycle
     * @throws Exception
     */
    public void getTradeSend(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset result = CSViewCall.call(this, "SS.CreatePersonUserSVC.getTradeSend", data);
        setAjax(result.first());
    }

    /**
     * 实名认证检索功能
     * @param cycle
     * @throws Exception
     */
    public void getTradeQuery(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset result = CSViewCall.call(this, "SS.CreatePersonUserSVC.getTradeQuery", data);
        setAjax(result.first());
    }

    public void cmpPicInfo(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();
        param.putAll(data);
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());

        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.cmpPicInfo", param);
        setAjax(dataset.getData(0));
    }
    
    /**
     * 跨区补卡是否免人像比对和身份证可手动输入权限
     * 
     * @author dengyi
     * @param clcle
     * @throws Exception
     */
    public void kqbkDataRight(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();
        
        param.putAll(data);
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.kqbkDataRight", param);
        setAjax(dataset.getData(0));
    }
    
    /**
	 * 查询卡类型
	 * @param cycle
	 * @throws Exception
	 */
	public void qryCardType(IRequestCycle cycle) throws Exception{
		IData data = getData();
		String changeTag = data.getString("CHANGE_CARD_TAG");
		if ("2".equals(changeTag)) {
			data.put("BIZ_TYPE", "1016");
		}else {
			data.put("BIZ_TYPE", "1012");
		}
		data.put("SERIAL_NUMBER", data.getString("MOBILENUM"));
		data.put("ID_ITEM_RANGE", data.getString("MOBILENUM"));
		data.put("IDTYPE", "01");
		IDataset cardType = CSViewCall.call(this, "SS.RemoteResetPswdSVC.queryCardType", data);
		if("1".equals(cardType.first().getString("result"))) {
		    CSViewException.apperr(CrmCommException.CRM_COMM_103, "本省号码无法办理异地业务！");
        }
		if(IDataUtil.isNotEmpty(cardType)){
			IData cardInfo = cardType.getData(0);
			String sCardType = cardInfo.getString("CARD_TYPE");
			String isJXH = cardInfo.getString("IS_JXH");
			String cardName = this.getPageUtil().getStaticValue("TD_S_COMMPARA", new String[]{"SUBSYS_CODE","PARAM_ATTR","PARAM_CODE","PARA_CODE1"}, 
	    			"PARAM_NAME", new String[]{"CSM","149","CARDTYPE",sCardType});
	    	//对卡类型的处理配在数据库中  2不可办理 1提示信息但可办理 0可以办理
	    	String cardRetn = this.getPageUtil().getStaticValue("TD_S_COMMPARA", new String[]{"SUBSYS_CODE","PARAM_ATTR","PARAM_CODE","PARA_CODE1"}, 
	    			"PARA_CODE2", new String[]{"CSM","149","CARDTYPE",sCardType});
	    	if(StringUtils.isEmpty(cardName)){
	    		//没查到数据就设置默认值
	    		data.put("CARD_NAME", "非标准实体卡");
	    		data.put("CARD_RETN", "2");
	    	}else{
	    		data.put("CARD_NAME", cardName);
	    		data.put("CARD_RETN", cardRetn);//2拦截 1提示 0可以办理
	    	}
			data.putAll(cardInfo);
			if("0".equals(isJXH)&&!"2".equals(changeTag)){//好友号码查询 
				IDataset friendCounts = CSViewCall.call(this, "SS.RemoteResetPswdSVC.numCheckQuery", data);
				if(IDataUtil.isNotEmpty(friendCounts)){
					IData friendCount = friendCounts.getData(0);
					String rspCode = friendCount.getString("RSP_CODE");
					String count = friendCount.getString("NUM_COUNT");
					if ("00".equals(rspCode)) {
						data.put("NUM_COUNT", count);
					}
				}
			}
		}
		String isReadonly = StaticUtil.getStaticValue("CANCEL_READONLY",getVisit().getStaffId());
		if(StringUtils.isNotBlank(isReadonly)&&"1".equals(isReadonly)){
			data.put("READONLY", "1");
		}else {
			data.put("READONLY", "0");
		}
		setCondition(data);
		setAjax(data);
	}
	/**
	 * 鉴权
	 * @param cycle
	 * @throws Exception
	 */
	public void openResultAuth(IRequestCycle cycle) throws Exception{
		IData pageData = getData();
		IData ajaxData = new DataMap();
		ajaxData.put("CUST_NAME", pageData.getString("CUST_NAME"));
		ajaxData.put("IDCARDTYPE", "0");
		ajaxData.put("PSPT_TYPE_CODE", "0");
		ajaxData.put("IDCARDNUM", pageData.getString("PSPT_ID"));
		String bizTypeTag = pageData.getString("CHANGE_CARD_TAG");
		if("2".equals(bizTypeTag)){
			pageData.put("BIZ_TYPE", "1016");//跨区换卡
		}else{
			pageData.put("BIZ_TYPE", "1012");//跨区补卡
			pageData.put("password", pageData.getString("USER_PASSWD"));//跨区补卡
		}
		IDataset cardType = CSViewCall.call(this, "SS.RemoteResetPswdSVC.openResultAuthF", pageData);
		if(IDataUtil.isNotEmpty(cardType)){
			IData userInfo = cardType.getData(0);
			ajaxData.put("LEVEL", userInfo.getString("USER_STAR"));//用户星级
		}
		setInfo(ajaxData);
		cardType.getData(0).putAll(ajaxData);
		setAjax(cardType.getData(0));
	}
	/**
	 * 跨区换卡下发短信 k3
	 * @param cycle
	 * @throws Exception
	 */
	public void simpleCardNotice(IRequestCycle cycle) throws Exception{
		IData pageData = getData();
		pageData.put("BIZ_TYPE", "1016");
		IDataset cardType = CSViewCall.call(this, "SS.RemoteResetPswdSVC.simpleCardNotice", pageData);
		setAjax(cardType.getData(0));
	}
}
