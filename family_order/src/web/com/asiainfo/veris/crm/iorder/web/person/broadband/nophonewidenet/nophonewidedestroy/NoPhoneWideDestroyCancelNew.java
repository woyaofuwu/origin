
package com.asiainfo.veris.crm.iorder.web.person.broadband.nophonewidenet.nophonewidedestroy;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 用户立即销户
 */
public abstract class NoPhoneWideDestroyCancelNew extends PersonBasePage
{
   
           
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
 
      //欠费信息
        IDataset busiInfos = CSViewCall.call(this, "SS.DestroyUserNowSVC.queryBusiInfo", data);
        //根据用户的宽带信息确认是哪种宽带，再确认trade_type_code
        
        
        IData info = new DataMap();
        String widetypename="";
        if ("1".equals(widetype))
        {
        	widetypename="GPON宽带";
        }else if ("2".equals(widetype))
        {
        	widetypename="海南铁通ADSL宽带";
        }else if ("3".equals(widetype))
        {
        	widetypename="FTTH宽带";
        }else if ("5".equals(widetype))
        {
        	widetypename="海南铁通FTTH宽带";
        }else if ("6".equals(widetype))
        {
        	widetypename="海南铁通FTTB宽带";
        }
        info.put("WIDE_TYPE_CODE", widetype);
        info.put("WIDE_TYPE_NAME", widetypename);
        info.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
        info.putAll(busiInfos.getData(0));
        
        setWideInfo(dataset.getData(0));

        //宽带预约拆机信息
        IDataset destroyInfos = CSViewCall.call(this, "SS.GponWidenetOrderDestorySVC.getGponDestroyInfo", data);
        setDestroyInfo(destroyInfos.getData(0));
      
       
        setInfo(info);
       
        IData param = new DataMap();
        //将参数参入前台
        //param.put("TRADE_TYPE_CODE", "1686");//
        param.put("WIDE_TYPE_CODE", "1");
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
        param.put("TRADE_TYPE_CODE", "1686");//
        param.put("WIDE_TYPE_CODE", "1");
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
        data.put("SERIAL_NUMBER", data.getString("NOPHONE_SERIAL_NUMBER"));
        
        IDataset dataset = CSViewCall.call(this, "SS.NoPhoneWideDestroyCancelRegSVC.tradeReg", data);
        
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
    public abstract void setDestroyInfo(IData destroyInfo);

}
