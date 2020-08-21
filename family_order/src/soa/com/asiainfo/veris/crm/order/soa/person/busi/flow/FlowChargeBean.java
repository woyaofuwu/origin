
package com.asiainfo.veris.crm.order.soa.person.busi.flow;

import com.ailk.biz.util.TimeUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BankPaymentManageException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnv;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnvManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;



public class FlowChargeBean extends CSBizBean
{
    /**
     * 流量经营业务接口
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset queryBalanceDetail(IData input) throws Exception
    {        
        String strSerialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");// 手机号码
        //String strUserID = IDataUtil.chkParam(input, "USER_ID");// 手机号码
		
        IDataset idsBalanceDetail = new DatasetList();
		IData idParam = new DataMap();
		idParam.put("SERIAL_NUMBER", strSerialNumber);
		//idParam.put("USER_ID", strUserID);
		idParam.put(Route.ROUTE_EPARCHY_CODE, "0898");
		//调用账务接口
		IDataset idsQryFPOneTimePay = CSAppCall.call("AM_OUT_queryBalanceDetail", idParam);
		if(IDataUtil.isNotEmpty(idsQryFPOneTimePay))
		{
			IData idQryFPOneTimePay = idsQryFPOneTimePay.first();
			idsBalanceDetail = idQryFPOneTimePay.getDataset("BALANCE_LIST");
			if(IDataUtil.isNotEmpty(idsBalanceDetail))
	        {
	        	for (int i = 0; i < idsBalanceDetail.size(); i++) 
	        	{
	        		IData idBalanceDetail = idsBalanceDetail.getData(i);
	        		/*long nBalance = idBalanceDetail.getLong("BALANCE", 0);
	        		nBalance = nBalance / 1024;
	        		idBalanceDetail.put("BALANCE", nBalance);
	        		
	        		long nInitFlow = idBalanceDetail.getLong("INIT_FLOW", 0);
	        		nInitFlow = nInitFlow / 1024;
	        		idBalanceDetail.put("INIT_FLOW", nInitFlow);*/
	        		
	        		Long nInitFee = idBalanceDetail.getLong("INIT_FEE", 0);
	        		nInitFee = nInitFee / 100;
	        		idBalanceDetail.put("INIT_FEE", nInitFee);
	        		
				}
	        }
		}
		return idsBalanceDetail;
    }
    
    /**
     * 流量商品接口
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset flowProdQry(IData input) throws Exception
    {
    	String OfferCode = input.getString("COMM_ID");
    	String OfferName = input.getString("COMM_NAME");
    	String strUserID = input.getString("USER_ID");
    	IDataset idsComparam1688 = UpcCall.qryDataInfos(OfferCode, OfferName);
    	IDataset idsInfos = new DatasetList();
    	if(IDataUtil.isNotEmpty(idsComparam1688))
		{
    		for(int i = 0; i < idsComparam1688.size(); i++)
        	{
        	    IData idComparam1688 = idsComparam1688.getData(i);
        	    //商品编码 商品名称 账本类型 话费(元) 流量(M) 生效时间 失效时间
        	    String strOfferCode = idComparam1688.getString("OFFER_CODE");
        	    String strOfferName = idComparam1688.getString("OFFER_NAME");
        	    String strFee = idComparam1688.getString("FEE");
        	    Long nPrice = Long.parseLong(strFee) / 100;
        	    String strInitValue = idComparam1688.getString("INIT_VALUE");
        	    String strCommType = idComparam1688.getString("QUARTER_TAG");//是否季包半年包0:不是，1：是
        	    String strQuarterTag = strCommType;
        	    if("0".equals(strQuarterTag))
        	    {
        	    	strQuarterTag = "否";
        	    }
        	    else if("1".equals(strQuarterTag))
        	    {
        	    	strQuarterTag = "是";
        	    }
        	    else
        	    {
        	    	strQuarterTag = "";
				}
        	    String strCarryOverTag = idComparam1688.getString("CARRY_OVER_TAG");//是否结转 0:不允许，1：允许（当月包下月可用） 
        	    if("0".equals(strCarryOverTag))
        	    {
        	    	strCarryOverTag = "否";
        	    }
        	    else if("1".equals(strCarryOverTag))
        	    {
        	    	strCarryOverTag = "是";
        	    }
        	    else
        	    {
        	    	strCarryOverTag = "";
				}
        	    String strResSwitch = idComparam1688.getString("RES_SWITCH"); //是否转赠 0:不允许，1：允许
        	    if("0".equals(strResSwitch))
        	    {
        	    	strResSwitch = "否";
        	    }
        	    else if("1".equals(strResSwitch))
        	    {
        	    	strResSwitch = "是";
        	    }
        	    else
        	    {
        	    	strResSwitch = "";
				}
        	    String strEanbleMode = idComparam1688.getString("ENABLE_MODE");//生效方式 0-立即生效1-下帐期生效2-次日生效3-可选立即或下帐期生效4-绝对时间 
        	    if("0".equals(strEanbleMode))
        	    {
        	    	strEanbleMode = "立即";
        	    }
        	    else if("1".equals(strEanbleMode))
        	    {
        	    	strEanbleMode = "下帐期";
        	    }
        	    else if("2".equals(strEanbleMode))
        	    {
        	    	strEanbleMode = "次日";
        	    }
        	    else if("3".equals(strEanbleMode))
        	    {
        	    	strEanbleMode = "配置错误";
        	    }
        	    else if("4".equals(strEanbleMode))
        	    {
        	    	strEanbleMode = "指定日期";
        	    }
        	    else
        	    {
        	    	strEanbleMode = "配置错误";
				}
        	    
        	    /** 根据配置计算开始时间 */
        	    String strEnableMode = idComparam1688.getString("ENABLE_MODE");
        	    String strAbsoluteEnableDate = idComparam1688.getString("ABSOLUTE_ENABLE_DATE");
        	    String strEnableOffset = idComparam1688.getString("ENABLE_OFFSET");
        	    String strEnableUnit = idComparam1688.getString("ENABLE_UNIT");
        	    
        	    // 获取用户账期
        		IDataset userAcctDays = UcaInfoQry.qryUserAcctDaysByUserId(strUserID);

        		String acctDay = "";
        		String firstDay = "";
        		String nextAcctDay = "";
        		String nextFirstDay = "";
        		String startDate = "";
        		String nextStartDate = "";
        		if (IDataUtil.isNotEmpty(userAcctDays))
        		{
        			//int size = userAcctDays.size();
        			IData userAcctDay = userAcctDays.first();
        			acctDay = userAcctDay.getString("ACCT_DAY");
        			firstDay = userAcctDay.getString("FIRST_DATE");
        			startDate = userAcctDay.getString("START_DATE");
        			AcctTimeEnv env = new AcctTimeEnv(acctDay, firstDay, nextAcctDay, nextFirstDay, startDate, nextStartDate);
        			AcctTimeEnvManager.setAcctTimeEnv(env);
        		}
        	    String strStartDate = "";
        	    if ("1".equals(strEnableMode))
        	    {
        	    	strStartDate = SysDateMgr.getFirstDayOfNextMonth();
        	    }
        	    else if("0".equals(strEnableMode) || "2".equals(strEnableMode) || "4".equals(strEnableMode))
        	    {
        	    	strStartDate = SysDateMgr.startDate(strEnableMode, strAbsoluteEnableDate, strEnableOffset, strEnableUnit);
        	    }
        	    
        	    
        	    /** 根据配置计算结束时间 */
        	    String strDisableMode = idComparam1688.getString("DISABLE_MODE");
        	    String strAbsoluteDisableDate = idComparam1688.getString("ABSOLUTE_DISABLE_DATE");
        	    String strDisableOffset = idComparam1688.getString("DISABLE_OFFSET");
        	    String strDisableUnit = idComparam1688.getString("DISABLE_UNIT");
        	    String strEndDate = SysDateMgr.endDate(strStartDate, strDisableMode, strAbsoluteDisableDate, strDisableOffset, strDisableUnit);
        	   
        	    idComparam1688.put("COMM_ID", strOfferCode);
        	    idComparam1688.put("COMM_NAME", strOfferName);
        	    idComparam1688.put("FEE", nPrice);
        	    idComparam1688.put("INIT_VALUE", strInitValue);
        	    idComparam1688.put("COMM_TYPE", strCommType);
        	    idComparam1688.put("QUARTER_TAG", strQuarterTag);
        	    idComparam1688.put("CARRY_OVER_TAG", strCarryOverTag);
        	    idComparam1688.put("RES_SWITCH", strResSwitch);
        	    idComparam1688.put("ENABLE_MODE", strEanbleMode);
        	    idComparam1688.put("START_DATE", strStartDate);
        	    idComparam1688.put("END_DATE", strEndDate);
        	    idsInfos.add(idComparam1688);
        	}
        	
		}
    	return idsInfos;
    }
    
    /**
     * 流量商品快速充值查询接口
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset qryFastPayInfos(IData input) throws Exception
    {
    	String strParaCpde3 = input.getString("PARA_CODE3", "1");
    	IDataset param1688 = CommparaInfoQry.getCommparaInfoByCode3A("CSM", "1688", "FlowCharge", strParaCpde3, "0898");
    	return param1688;
    }
    
    /**
     * 流量提取返销业务接口
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset queryCancelableFlowInfos(IData input) throws Exception
    {        
        String strSerialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");// 手机号码
        //String strUserID = IDataUtil.chkParam(input, "USER_ID");// 手机号码
        String strYYYYMM = TimeUtil.getSysDate("yyyyMM", true);
        
		IData idParam = new DataMap();
		idParam.put("SERIAL_NUMBER", strSerialNumber);
		//idParam.put("USER_ID", strUserID);
		idParam.put("CYCLE_ID", strYYYYMM);
		idParam.put("REMOVE_TAG", "0");
		idParam.put(Route.ROUTE_EPARCHY_CODE, "0898");
		//调用账务接口
		IDataset idsQryFPOneTimePay = CSAppCall.call("AM_OUT_queryCancelableFlowInfos", idParam);
		/*if(IDataUtil.isNotEmpty(idsQryFPOneTimePay))
		{
			IData idQryFPOneTimePay = idsQryFPOneTimePay.first();
			IDataset idsBalanceDetail = idQryFPOneTimePay.getDataset("BALANCE_LIST");
			return idsBalanceDetail;
		}*/
		return idsQryFPOneTimePay;
    }
    
    
    public IDataset flowThresholdNotice(IData param) throws Exception
    {
        if (IDataUtil.isNotEmpty(param)){
            Double threshold = param.getDouble("THRESHOLD",0);
            if (threshold > 100 || threshold < 0){
                CSAppException.apperr(CrmCommException.CRM_COMM_103,"阀值信息不符合要求，请修改！");
            }
        }else{
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"入参信息为空，请补充！");
        }
        // 通过服务号码获取用户资料信息
        IData userInfo = getUserInfo(param.getString("SERIAL_NUMBER"));
        //根据产品id找出全网对应的信息
        param.put("GOODS_ID", getCrmProductsInfo(param,userInfo));
        //查询全网统一APP服务编码
        param.put("SERVICEID_LIST", getServiceID(param, userInfo));
        param.put("KIND_ID", "BIP3B521_T3000521_0_0");
        IDataset resultDataset = IBossCall.dealInvokeUrl("BIP3B521_T3000521_0_0", "IBOSS", param);
        return resultDataset;
    }
    

    public IData getUserInfo(String serial_number) throws Exception
    {
        // 通过服务号码获取用户资料信息
        IDataset userInfos = UserInfoQry.getUserInfoBySn(serial_number,"0");
        if (IDataUtil.isEmpty(userInfos))
        {
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_223);// 用户资料不存在！
        }else{
            return userInfos.getData(0);
        }
        return new DataMap();
       
    }
    /***
      *  根据产品id找出全网对应的信息，PRODUCT_ID 与 PACKAGE_ID 会默认查-1
      * @param mainTrade
      * @param discntInfo
      * @return
      * @throws Exception
      */
    public String getCrmProductsInfo(IData param,IData userInfo) throws Exception {
        IData params = new DataMap();
        params.put("ELEMENT_ID", param.getString("BOSS_PRODUCTID"));
        params.put("ELEMENT_TYPE_CODE", "D");
        params.put("EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));
        params.put("PRODUCT_ID", "");
        params.put("PACKAGE_ID", "");
        IDataset result = Dao.qryByCodeParser("TD_B_CTRM_RELATION",
                "SEL_BY_PRODUCT_DEF", params, Route.CONN_CRM_CEN);
        
        IData crmProduct = new DataMap();
        if (IDataUtil.isNotEmpty(result)) {
            crmProduct = result.getData(0);
        }

        return IDataUtil.isNotEmpty(crmProduct) ? crmProduct.getString("CTRM_PRODUCT_ID") : "";
    }
    
    /**
     * 查询全网统一APP服务编码
     * @param data
     * @return
     * @throws Exception
     */
    public String getServiceID(IData data,IData userInfo) throws Exception
    {
       IDataset commparaInfo = CommparaInfoQry.getCommparaByCode1("CSM", "2017", data.getString("BOSS_PRODUCTID"), "IS_VIDEO_PKG",null);
       
       StringBuffer sb = new StringBuffer();
       //查询compara表里面的paramCode20,如果是绑定APP的情况则存在值，无需查询attr记录
       if (IDataUtil.isNotEmpty(commparaInfo) && 
               IDataUtil.isNotEmpty(commparaInfo.getData(0)) &&
               StringUtils.isNotBlank(commparaInfo.getData(0).getString("PARA_CODE20")))
       {
           sb.append(commparaInfo.getData(0).getString("PARA_CODE20"));
       }else{
           //目前只有18元走此分支
           //否则为一个优惠对应多个APP的情况，需查询ATTR表进行确认
           IData input=new DataMap();
           IDataset attrInfo= null;
           input.put("DISCNT_CODE",data.getString("BOSS_PRODUCTID"));
           input.put("USER_ID",userInfo.getString("USER_ID"));
           //查询attr属性，视频流量包的appId
           attrInfo=Dao.qryByCodeParser("TF_F_USER_ATTR", "SEL_BY_DISCNTATTR_EFFECTIVENOW", input);
           if(IDataUtil.isNotEmpty(attrInfo)){//查attr表是否有app列表，最终方案需要用到
               for(int a=0;a<attrInfo.size();a++){ 
                   String appId=attrInfo.getData(a).getString("ATTR_VALUE","");
                   if (!"-1".equals(appId))
                   {
                       sb.append(a != (attrInfo.size() - 1) ? appId+";" : appId);
                   }
               }          
           }
       }
       return sb.toString();
    }
}
