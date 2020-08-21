
package com.asiainfo.veris.crm.iorder.web.person.broadband.nophonewidenet.nophonewidedestroy;

import com.ailk.org.apache.commons.lang3.StringUtils;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 用户立即销户
 */
public abstract class NoPhoneWideDestroyNew extends PersonBasePage
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
        data.put("TRADE_TYPE_CODE","685");
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
        	String serialNumber = data.getString("SERIAL_NUMBER");
        	IData param = new DataMap();
        	param.put("SERIAL_NUMBER", serialNumber);
        	 
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
        String modemDepost=info.getString("MODEM_FEE","0");
        if(!"0".equals(modemDepost)){
        	modemDepost=""+Integer.parseInt(modemDepost)/100;
        }
        info.put("MODEM_DEPOSIT",modemDepost);
        info.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
        //info.put("AUTH_SERIAL_NUMBER",data.getString("AUTH_SERIAL_NUMBER"));
        info.putAll(busiInfos.getData(0));
        
        setWideInfo(dataset.getData(0));

        //宽带预约拆机信息
        IDataset destroyInfos = CSViewCall.call(this, "SS.NoPhoneWideDestroyUserSVC.getDestroyInfo", data);
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
        //add by zhangxing3 for 候鸟月、季、半年套餐（海南）
        //宽带优惠信息
        IDataset userDiscntInfos = CSViewCall.call(this, "SS.NoPhoneWidenetMoveSVC.getUserDiscntInfo", data);
        if(IDataUtil.isNotEmpty(userDiscntInfos))
        {
            String houniaoTag = userDiscntInfos.getData(0).getString("HOUNIAO_TAG","");
            //有候鸟优惠报错
            if("1".equals(houniaoTag))
            {
                //报错
                CSViewException.apperr(CrmCommException.CRM_COMM_103,"业务受理限制:该用户含有有效的度假宽带套餐,不能办理该业务!");
            }
        }
        //add by zhangxing3 for 候鸟月、季、半年套餐（海南）
        
        //计算预约拆机的预约时间列表，根据宽带的到期时间推算
        onInitBookTimeList(cycle);
        onInitDestoryType(cycle);
        
        info.put("DESTORYTIME", "1");
        info.put("DESTORYTYPE", "1");
        info.put("MODEM_RETUAN", "0");
//        setInfo(info);

        String serialNumber = data.getString("SERIAL_NUMBER");
        if(StringUtils.isEmpty(serialNumber))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        IData topsetboxInfo = CSViewCall.callone(this, "SS.NoPhoneTopSetBoxSVC.queryTopSetBoxInfoByWsn", data);

        //add by zhangxing3 for "候鸟月、季、半年套餐（海南）"
        boolean installFeeTag = false;
        IDataset userinfo = CSViewCall.call(this, "SS.DestroyUserNowSVC.getUserInfoBySerailNumber", data);
        if(!userinfo.isEmpty())
        {
            //System.out.println("=============WidenetDestoryTradeSpec=============userinfo.first():"+userinfo.first());

            data.put("USER_ID", userinfo.first().getString("USER_ID",""));
            data.put("OPEN_DATE", userinfo.first().getString("OPEN_DATE",""));
        }
        //获取宽带装机费标识？？？
        //System.out.println("=============WidenetDestoryTradeSpec=============topsetboxInfo:"+topsetboxInfo);
        IDataset installFeeInfo = CSViewCall.call(this, "SS.NoPhoneWideDestroyUserSVC.queryWidenetInstallFee", data);
        if(!installFeeInfo.isEmpty())
        {
            if("1".equals(installFeeInfo.getData(0).getString("INSTALL_FEE_TAG", "0"))){
                installFeeTag = true;
            }
        }
        //System.out.println("=============WidenetDestoryTradeSpec=============houniaoTag:"+installFeeTag);
        if( installFeeTag ){
            if(IDataUtil.isNotEmpty(topsetboxInfo))
            {
                topsetboxInfo.put("FEE_MODE", "0");
                topsetboxInfo.put("FEE_TYPE_CODE", "512");
                topsetboxInfo.put("FEE", "12000");
            }
            else{
                topsetboxInfo = new DataMap();
                topsetboxInfo.put("FEE_MODE", "0");
                topsetboxInfo.put("FEE_TYPE_CODE", "512");
                topsetboxInfo.put("FEE", "12000");
            }
        }
        //add by zhangxing3 for "候鸟月、季、半年套餐（海南）"
        
        IDataset commissioningFee = CSViewCall.call(this, "SS.GponWidenetOrderDestorySVC.queryCommissioningFee", data);
        System.out.println("调测费违约金"+commissioningFee);
        
        if(IDataUtil.isEmpty(topsetboxInfo)){
        	topsetboxInfo = new DataMap();
        }
        
        if(IDataUtil.isNotEmpty(commissioningFee)){
        	
        	String wideModeFee = commissioningFee.getData(0).getString("WIDE_MODE_FEE","0");
        	if("1".equals(wideModeFee)){
        		info.put("MODEM_DEPOSIT", "0");
        	}

        	topsetboxInfo.put("COMMISSIONING_FEE_WIDENET",commissioningFee.getData(0).getString("COMMISSIONING_FEE_WIDENET",""));
        	topsetboxInfo.put("WIDE_MODE_FEE",wideModeFee);

        }else{
        	topsetboxInfo.put("COMMISSIONING_FEE_WIDENET","0");
        }
        setInfo(info);

        if(IDataUtil.isNotEmpty(topsetboxInfo))
        {
            setAjax(topsetboxInfo);
        }
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
        IData param = new DataMap();
        
        //将参数参入前台
        IData wideInfoParam=new DataMap();
        wideInfoParam.put("WIDE_TYPE", "");//此参数已经不用了
        setWideInfoParam(wideInfoParam);
        param.put("TRADE_TYPE_CODE", "685");//随便初始化一个值，具体的值需要根据手机号码来查询宽带类型
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
        data.put("SERIAL_NUMBER", data.getString("NOPHONE_SERIAL_NUMBER"));
        IDataset dataset = new DatasetList();
        
        if (!"1685".equals(data.getString("TRADE_TYPE_CODE", "")))
        {//立即拆机
        	dataset = CSViewCall.call(this, "SS.NoPhoneWideDestroyUserRegSVC.tradeReg", data);
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
    	param = CSViewCall.callone(this, "SS.NoPhoneWideDestroyUserSVC.checkDestroyTime", data);
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
}
