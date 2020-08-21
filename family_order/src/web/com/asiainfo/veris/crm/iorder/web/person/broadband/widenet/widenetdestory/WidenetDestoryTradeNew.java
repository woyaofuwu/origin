
package com.asiainfo.veris.crm.iorder.web.person.broadband.widenet.widenetdestory;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 用户立即销户
 */
public abstract class WidenetDestoryTradeNew extends PersonBasePage
{
    /**
     * 查询用户积分
     * 
     * @param cycle
     * @throws Exception
     */
    public void checkScore(IRequestCycle cycle) throws Exception
    {
        IDataset screDataset = new DatasetList();
        IData info = getData();
        screDataset = CSViewCall.call(this, "SS.DestroyUserNowSVC.queryUserScore", info);
        String score = screDataset.getData(0).getString("SCORE");
        setAjax("SCORE", score);
    }
    public void getThisTradeType(IData data) throws Exception
    {
    	data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
    	IDataset dataset = CSViewCall.call(this, "CS.WidenetInfoQuerySVC.getUserWidenetInfo", data);
        if(dataset != null && dataset.size()>0)
        {
        	//根据标志位判断是哪种宽带，kangyt 2016-3-18
        	String widetype = dataset.getData(0).getString("RSRV_STR2");
        	if ("1".equals(widetype))
        	{
        		data.put("TRADE_TYPE_CODE","605");//移动GPON宽带
        	}
        	else if ("3".equals(widetype))
        	{
        		data.put("TRADE_TYPE_CODE","605");//移动FTTH宽带
        	}
        	else if ("2".equals(widetype) || "5".equals(widetype) || "6".equals(widetype))
        	{
        		data.put("TRADE_TYPE_CODE","605");//海南铁通宽带
        	}
        	data.put("WIDE_TYPE_CODE",widetype);
        }
    }
    
    //查询号码的宽带类型后再执行业务规则校验
    public void checkBeforeTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
      //宽带地址信息
        IDataset dataset = CSViewCall.call(this, "CS.WidenetInfoQuerySVC.getUserWidenetInfo", data);
        String widetype = dataset.getData(0).getString("RSRV_STR2");
        data.put("WIDE_TYPE_CODE",widetype);

        
        IDataset infos = CSViewCall.call(this, "CS.CheckTradeSVC.checkBeforeTrade", data);
        setAjax(infos.getData(0));
    }
    
    /**
     * 获取调测费活动折旧费或赔偿金
     * @param clcle
     * @throws Exception
     */
    public void queryWidenetCommissioningFee(IRequestCycle clcle) throws Exception
    { 
        //BUS201907310012关于开发家庭终端调测费的需求
    	IData data = getData();
    	IData info = new DataMap();
        IDataset ids2=CSViewCall.call(this, "SS.GponWidenetOrderDestorySVC.queryWidenetCommissioningFee", data);
    	if (ids2!=null && ids2.size()>0)
    	{
    		info.put("COMMISSIONING_FEE_TAG", ids2.getData(0).getString("COMMISSIONING_FEE_TAG",""));
    		info.put("COMMISSIONING_FEE", ids2.getData(0).getString("COMMISSIONING_FEE",""));
    		info.put("LEFT_MONTHS", ids2.getData(0).getString("LEFT_MONTHS",""));

    	}
    	else
    	{
    		info.put("COMMISSIONING_FEE_TAG", "0");
    		info.put("COMMISSIONING_FEE", "0");
    		info.put("LEFT_MONTHS", "0");

    	}
    	setAjax(info);
    	//BUS201907310012关于开发家庭终端调测费的需求
    }
    
    /**
     * 查询宽带资料后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        String widetype ="";
      //宽带地址信息
        IDataset dataset = CSViewCall.call(this, "CS.WidenetInfoQuerySVC.getUserWidenetInfo", data);
        widetype = dataset.getData(0).getString("RSRV_STR2");
        String widetype_name="";//宽带类型名称
        if("1".equals(widetype))
        {
        	widetype_name="移动FTTB";
        }else if("2".equals(widetype))
        {
        	widetype_name="铁通ADSL";
        }else if("3".equals(widetype))
        {
        	widetype_name="移动FTTH";
        }else if("4".equals(widetype))
        {
        	widetype_name="校园宽带";
        }else if("5".equals(widetype))
        {
        	widetype_name="铁通FTTH";
        }else if("6".equals(widetype))
        {
        	widetype_name="铁通FTTB";
        }
//        
      //欠费信息
        IDataset busiInfos = CSViewCall.call(this, "SS.DestroyUserNowSVC.queryBusiInfo", data);
        //根据用户的宽带信息确认是哪种宽带，再确认trade_type_code
        
        
        IData info = new DataMap();
        if ("5".equals(widetype))
        {
        	widetype="3";
        }
        info.put("WIDE_TYPE_CODE", widetype);
        info.put("WIDE_TYPE_NAME", widetype_name);
        //如果是光纤拆机，则需要检查是否租用了光猫
        //if(data.getString("TRADE_TYPE_CODE").equals("625"))
        if("3".equals(widetype) || "5".equals(widetype) )//统一了typecode，只能用这个来判断宽带类型了
        {
        	String serialNumber = data.getString("AUTH_SERIAL_NUMBER");
        	IData param = new DataMap();
        	
        	if(serialNumber.indexOf("KD_")>-1) {//宽带账号
        		if(serialNumber.split("_")[1].length()>11)
        			param.put("SERIAL_NUMBER", serialNumber);//商务宽带
        		else
        			param.put("SERIAL_NUMBER", serialNumber.split("_")[1]);//个人账号
        	}
        	else {
        		if(serialNumber.length()>11)
        			param.put("SERIAL_NUMBER", "KD_"+serialNumber);
        		else
        			param.put("SERIAL_NUMBER", serialNumber);
        	}
        		
        	
        	IDataset userinfo = CSViewCall.call(this, "SS.DestroyUserNowSVC.getUserInfoBySerailNumber", param);
        	
        	if(!userinfo.isEmpty())
        	{
        		if(!userinfo.first().getString("RSRV_STR10","").equals("BNBD"))
        		{
        			//IDataset userOtherinfo = CSViewCall.call(this, "SS.DestroyUserNowSVC.queryUserOtherInfo", userinfo.first());
        			IDataset userOtherinfo = CSViewCall.call(this, "SS.DestroyUserNowSVC.queryUserModemRent", userinfo.first());
            		if(!userOtherinfo.isEmpty())
            		{
            			info.put("MODEM_CODE", userOtherinfo.getData(0).getString("RSRV_STR1", ""));
            			info.put("MODEM_FEE", userOtherinfo.getData(0).getString("RSRV_STR2", "0"));
            			info.put("MODEM_MODE", userOtherinfo.getData(0).getString("RSRV_TAG1", "0"));
            			info.put("MODEM_FEE_STATE", userOtherinfo.getData(0).getString("RSRV_STR7", "0"));
            			info.put("MODEM_START_DATE", userOtherinfo.getData(0).getString("START_DATE", "0"));
            			info.put("MODEM_END_DATE", userOtherinfo.getData(0).getString("END_DATE", "0"));
            		}
        		}
        		else
        		{
        			IDataset userOtherinfo = CSViewCall.call(this, "SS.DestroyUserNowSVC.queryGroupUserOtherInfo", userinfo.first());
            		if(!userOtherinfo.isEmpty())
            		{
            			info.put("MODEM_CODE", userOtherinfo.getData(0).getString("RSRV_STR1", ""));
            			info.put("MODEM_FEE", userOtherinfo.getData(0).getString("RSRV_STR2", "0"));
            			info.put("MODEM_MODE", userOtherinfo.getData(0).getString("RSRV_TAG1", "0"));
            			info.put("MODEM_FEE_STATE", userOtherinfo.getData(0).getString("RSRV_STR7", "0"));
            			info.put("MODEM_START_DATE", userOtherinfo.getData(0).getString("START_DATE", "0"));
            			info.put("MODEM_END_DATE", userOtherinfo.getData(0).getString("END_DATE", "0"));
            		}
        		}
        	}
        	String modemmode=info.getString("MODEM_MODE","");
        	if ("0".equals(modemmode))
        	{
        		info.put("MODEM_MODE_NAME","租赁");
        	}else if ("1".equals(modemmode))
        	{
        		info.put("MODEM_MODE_NAME","购买");
        	}else if ("2".equals(modemmode))
        	{
        		info.put("MODEM_MODE_NAME","赠送");
        	}else if ("3".equals(modemmode))
        	{
        		info.put("MODEM_MODE_NAME","自备");
        	}
        	//
        	String feestate=info.getString("MODEM_FEE_STATE","");
        	if ("0".equals(feestate))
        	{
        		info.put("MODEM_FEE_STATE_NAME","正常");
        	}else if ("1".equals(feestate))
        	{
        		info.put("MODEM_FEE_STATE_NAME","已转移");
        	}else if ("2".equals(feestate))
        	{
        		info.put("MODEM_FEE_STATE_NAME","已退还");
        	}else if ("3".equals(feestate))
        	{
        		info.put("MODEM_FEE_STATE_NAME","已沉淀");
        	}
        }
        info.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
        //info.put("AUTH_SERIAL_NUMBER",data.getString("AUTH_SERIAL_NUMBER"));
        info.putAll(busiInfos.getData(0));
        
        setWideInfo(dataset.getData(0));

        //宽带预约拆机信息
        IDataset destroyInfos = CSViewCall.call(this, "SS.GponWidenetOrderDestorySVC.getGponDestroyInfo", data);
        setDestroyInfo(destroyInfos.getData(0));
        //有预约拆机报错
        if(IDataUtil.isNotEmpty(destroyInfos))
        {
        	String destroyState = destroyInfos.getData(0).getString("DESTORY_STATE","");
        	if("已预约".equals(destroyState))
        	{
        		//报错
        		CSViewException.apperr(CrmCommException.CRM_COMM_103,"业务受理限制:该用户含有宽带预约拆机记录,不能办理该业务!");
        	}
        }
        
        
        //计算预约拆机的预约时间列表，根据宽带的到期时间推算
        onInitBookTimeList(cycle);
        onInitDestoryType(cycle);

        //add by zhangxing for 候鸟月、季、半年套餐（海南）
        //查询是否需要缴纳宽带装机费
        //System.out.println("==============WidenetDestoryTrade=================data:"+data);
        IDataset ids=CSViewCall.call(this, "SS.GponWidenetOrderDestorySVC.queryWidenetInstallFee", data);
        //System.out.println("==============WidenetDestoryTrade=================ids:"+ids);

        if (ids!=null && ids.size()>0)
        {
            info.put("INSTALL_FEE_TAG", ids.getData(0).getString("INSTALL_FEE_TAG",""));
        }
        else
        {
            info.put("INSTALL_FEE_TAG", "0");
        }
        //add by zhangxing for 候鸟月、季、半年套餐（海南）
        
        
        //BUS201907310012关于开发家庭终端调测费的需求
        IDataset ids2=CSViewCall.call(this, "SS.GponWidenetOrderDestorySVC.queryWidenetCommissioningFee", data);
    	if (ids2!=null && ids2.size()>0)
    	{
    		info.put("COMMISSIONING_FEE_TAG", ids2.getData(0).getString("COMMISSIONING_FEE_TAG",""));
    		info.put("COMMISSIONING_FEE", ids2.getData(0).getString("COMMISSIONING_FEE",""));
    		info.put("LEFT_MONTHS", ids2.getData(0).getString("LEFT_MONTHS",""));

    	}
    	else
    	{
    		info.put("COMMISSIONING_FEE_TAG", "0");
    		info.put("COMMISSIONING_FEE", "0");
    		info.put("LEFT_MONTHS", "0");

    	}
    	//BUS201907310012关于开发家庭终端调测费的需求
        
        info.put("DESTORYTIME", "1");
        info.put("DESTORYTYPE", "1");
        info.put("MODEM_RETUAN", "0");
        
        // start BUG20190102090436  IMS固话拆机问题优化   by xuzh5 2019-1-3 11:09:40
        String staffId =getVisit().getStaffId();
        boolean staffPriv = StaffPrivUtil.isPriv(staffId, "crm9D97",StaffPrivUtil.PRIV_TYPE_FUNCTION);  //IMS固话拆机功能权限
		String destoryFlg="1"; //有拆IMS固话的权限
        if(!staffPriv)
		{
        	destoryFlg="0";		//无拆IMS固话的权限
		}
        info.put("DESTORYFLG", destoryFlg);
        // end BUG20190102090436  IMS固话拆机问题优化   by xuzh5 2019-1-3 11:09:40
        
        setInfo(info);
        /*
         * 用于在前台进行提示的标识，判断用户是否办理了魔百和
         */
        IData param=new DataMap();
        param.put("WARM_TYPE", "0");
        param.put("ACTIVE_FLAG", "0");
        //add by zhangxing for 候鸟月、季、半年套餐（海南）
        param.put("INSTALL_FEE_TAG", info.getString("INSTALL_FEE_TAG", "0"));
        //add by zhangxing for 候鸟月、季、半年套餐（海南）
//        String warmTypeParam=data.getString("WIDE_TYPE","");
//        if(warmTypeParam!=null&&warmTypeParam.equals("ADSL"))
        
    	IDataset checkResult=CSViewCall.call(this, "SS.DestroyUserNowSVC.checkUserExistsTopsetBoxNew", data);
    	if (checkResult!=null && checkResult.size()>0)
    	{
    		param.put("WARM_TYPE", checkResult.getData(0).getString("WARM_TYPE",""));
    	}
    	//把判断有营销活动或包年套餐的放在业务规则校验去
//        IDataset pactive= CSViewCall.call(this, "SS.DestroyUserNowSVC.checkSaleActiveYear", data);
//        if (pactive!=null && pactive.size()>0)
//    	{
//    		param.put("ACTIVE_FLAG", pactive.getData(0).getString("ACTIVE_FLAG","0"));
//    	}
    	
    	 /*
         * 标识IMS固话
         * */
    	if(StringUtils.isEmpty(data.getString("SERIAL_NUMBER")))
        {
    		data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
    	if(data.getString("SERIAL_NUMBER").startsWith("KD_"))
    	{
    		data.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER").replace("KD_", ""));
    	}
        
    	data.put(Route.ROUTE_EPARCHY_CODE, "0898");
    	data.put("EPARCHY_CODE", "0898");
    	IData imsInfo = null;
        try {
        	imsInfo = CSViewCall.callone(this,"SS.ChangeSvcStateSVC.getIMSInfoBySerialNumber", data);
		} catch (Exception e) {
		}
		if (IDataUtil.isNotEmpty(imsInfo)) {
			
			String serialNumber = imsInfo.getString("SERIAL_NUMBER_B");
			String userId = imsInfo.getString("USER_ID_B");
			data.put("USER_ID", userId);
			IData userProductInfo = CSViewCall.callone(this,"SS.GetUser360ViewSVC.qryUserProductInfoByUserId",data);
			if (IDataUtil.isNotEmpty(userProductInfo)) {
				String brandCode = userProductInfo.getString("BRAND_CODE");
				String productName = userProductInfo.getString("RSRV_STR5");
				imsInfo.put("IMS_TAG", "1");
				imsInfo.put("IMS_BRAND", brandCode);
				imsInfo.put("IMS_PRODUCT", productName);
				imsInfo.put("IMS_SERIAL_NUMBER", serialNumber);
			}
		}else {
			imsInfo = new DataMap();
			imsInfo.put("IMS_TAG","0");
		}
		setIMSInfo(imsInfo);
        setAjax(param);

    }
    //初始化拆机方式下拉框
    public void onInitDestoryType(IRequestCycle clcle) throws Exception
    { 
    	IDataset iset = new DatasetList();
        IData dt=new DataMap();
        dt.put("DATA_ID", "1");
        dt.put("DATA_NAME", "立即拆机");
        iset.add(dt);
        IData dt2=new DataMap();
        dt2.put("DATA_ID", "2");
        dt2.put("DATA_NAME", "预约拆机");
        iset.add(dt2);
        setTypeList(iset);
    }
    
    /**
     * 初始化预约拆机时，预约时间下拉选择框
     * @param clcle
     * @throws Exception
     */
    public void onInitBookTimeList(IRequestCycle clcle) throws Exception
    { 
    	//modify by zhangyc5 on 20160525
    	IData data = getData();
    	IDataset bookTimeList = CSViewCall.call(this, "SS.DestroyUserNowSVC.getBookTimeList", data);
        setTimeList(bookTimeList);
    }
    /**
     * 初始化方法
     * 
     * @author chenzm
     * @param clcle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle clcle) throws Exception
    {    	
        IData data = getData();
        IData param = new DataMap();
        
        //将参数参入前台
        IData wideInfoParam=new DataMap();
        wideInfoParam.put("WIDE_TYPE", "");//此参数已经不用了
        setWideInfoParam(wideInfoParam);
        param.put("TRADE_TYPE_CODE", "605");//随便初始化一个值，具体的值需要根据手机号码来查询宽带类型
        param.put("DESTORYTYPE", "1");
        param.put("WIDE_TYPE_CODE", "1");
        param.put("MODEM_RETUAN", "0");
        setInfo(param);
        
        onInitDestoryType(clcle);
        
        //
        
    }

    /**
     * 业务提交（onTradeSubmit cssubmit组件中默认的提交action方法）
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {

            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        IDataset dataset = new DatasetList();
        
        if (!"1605".equals(data.getString("TRADE_TYPE_CODE", "")))
        {//立即拆机
        	dataset = CSViewCall.call(this, "SS.WidenetDestroyNewRegSVC.tradeReg", data);
        }else
        {//预约拆机
        	//data.put("TRADE_TYPE_CODE", "1605");
        	dataset = CSViewCall.call(this, "SS.OrderDestoryGponRegSVC.tradeReg", data);
        }
        
        
        setAjax(dataset);
    }

    /**
     * 校验预约销号时间
     * @param cycle
     * @throws Exception
     */
    public void checkDestroyTime(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();

    	IData param = new DataMap();
    	param = CSViewCall.callone(this, "SS.DestroyUserNowSVC.checkDestroyTime", data);
        setAjax(param);
    }
    public abstract void setInfo(IData info);

    /**
     * 认证结束之后，设置用户相关信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void setPageCustInfo(IRequestCycle cycle) throws Exception
    {
        IData pagedata = getData();
        IData custInfo = new DataMap(pagedata.getString("CUST_INFO"));
        IData userInfo = new DataMap(pagedata.getString("USER_INFO"));
        IData acctInfo = new DataMap(pagedata.getString("ACCT_INFO"));

        IData info = new DataMap();
        info.put("USER_ID", userInfo.getString("USER_ID"));
        info.put(Route.ROUTE_EPARCHY_CODE, userInfo.getString("EPARCHY_CODE"));
        IDataset feeInfos = CSViewCall.call(this, "CS.UserOwenInfoQrySVC.getOweFeeByUserId", info);
        if (IDataUtil.isNotEmpty(feeInfos))
        {
            acctInfo.put("LEFT_MONEY", (feeInfos.getData(0).getDouble("ACCT_BALANCE", 0.0) / 100.0));// 余额
        }
        else
        {
            acctInfo.put("LEFT_MONEY", "0");// 余额
        }

        // 用户欠费信息
        acctInfo.put("OWE_FEE", "0");
        acctInfo.put("FOREGIFT", "0");

        IData pageInfo = new DataMap();
        pageInfo.putAll(custInfo);
        pageInfo.putAll(userInfo);
        pageInfo.putAll(acctInfo);

    }

    public abstract void setWideInfo(IData custInfo);
    public abstract void setWideInfoParam(IData wideInfoParam);
    public abstract void setDestroyInfo(IData destroyInfo);
    public abstract void setTypeList(IDataset typeList);
    public abstract void setTimeList(IDataset timeList);
    public abstract void setIMSInfo(IData iMSinfo);
}
