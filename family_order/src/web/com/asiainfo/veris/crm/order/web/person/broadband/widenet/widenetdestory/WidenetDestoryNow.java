
package com.asiainfo.veris.crm.order.web.person.broadband.widenet.widenetdestory;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 用户立即销户
 */
public abstract class WidenetDestoryNow extends PersonBasePage
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

    /**
     * 查询宽带资料后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "CS.WidenetInfoQuerySVC.getUserWidenetInfo", data);
        IDataset busiInfos = CSViewCall.call(this, "SS.DestroyUserNowSVC.queryBusiInfo", data);
        
        IData info = new DataMap();
        //如果是光纤拆机，则需要检查是否租用了光猫
        if(data.getString("TRADE_TYPE_CODE").equals("625")||data.getString("TRADE_TYPE_CODE").equals("615"))
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
        			IDataset userOtherinfo = CSViewCall.call(this, "SS.DestroyUserNowSVC.queryUserOtherInfo", userinfo.first());
            		if(!userOtherinfo.isEmpty())
            		{
            			info.put("MODEM_CODE", userOtherinfo.getData(0).getString("RSRV_STR1", ""));
            			info.put("MODEM_FEE", userOtherinfo.getData(0).getString("RSRV_STR2", "0"));
            		}
        		}
        		else
        		{
        			IDataset userOtherinfo = CSViewCall.call(this, "SS.DestroyUserNowSVC.queryGroupUserOtherInfo", userinfo.first());
            		if(!userOtherinfo.isEmpty())
            		{
            			info.put("MODEM_CODE", userOtherinfo.getData(0).getString("RSRV_STR1", ""));
            			info.put("MODEM_FEE", userOtherinfo.getData(0).getString("RSRV_STR2", "0"));
            		}
        		}
        		
        	}
        	info.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
        	info.put("MODEM_RETUAN", "1");
        }
        info.putAll(busiInfos.getData(0));
        setInfo(info);
        setWideInfo(dataset.getData(0));

        
        /*
         * 用于在前台进行提示的标识
         */
        IData param=new DataMap();
        param.put("WARM_TYPE", "0");
        
        String warmTypeParam=data.getString("WIDE_TYPE","");
        if(warmTypeParam!=null&&warmTypeParam.equals("ADSL")){
        	IDataset checkResult=CSViewCall.call(this, "SS.DestroyUserNowSVC.checkUserExistsTopsetBox", data);
        	 param.put("WARM_TYPE", checkResult.getData(0).getString("WARM_TYPE",""));
        	
        }
        
        setAjax(param);

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
//        IData wideInfoParam=new DataMap();
//        wideInfoParam.put("WIDE_TYPE", data.getString("WIDE_TYPE"));
//        setWideInfoParam(wideInfoParam);
        
        if ("ADSL".equals(data.getString("WIDE_TYPE")) || "TTADSL".equals(data.getString("WIDE_TYPE")))
        {
            param.put("TRADE_TYPE_CODE", "624");
        }
        else if ("XIAN".equals(data.getString("WIDE_TYPE")))
        {
            param.put("TRADE_TYPE_CODE", "625");
        }
        else if ("GPON".equals(data.getString("WIDE_TYPE")))
        {
            param.put("TRADE_TYPE_CODE", "605");
        }
        else if ("SCHOOL".equals(data.getString("WIDE_TYPE")))
        {
            param.put("TRADE_TYPE_CODE", "635");
        }
        else if ("SPEC".equals(data.getString("WIDE_TYPE")))
        {
            param.put("TRADE_TYPE_CODE", "615");// 宽带特殊拆机
        }
        else if ("TTXIAN".equals(data.getString("WIDE_TYPE")))
        {
            param.put("TRADE_TYPE_CODE", "647");// 光改网络铁通ADSL融合
        }
        setInfo(param);
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
        IDataset dataset = CSViewCall.call(this, "SS.DestroyWidenetUserNowRegSVC.tradeReg", data);
        setAjax(dataset);
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
    
}
