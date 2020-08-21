/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.cpe;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * CPE立即销户
 * REQ201612260011_新增CPE终端退回和销户界面
 * @author zhuoyingzhi
 * @date 20170214
 */
public abstract class CPEDestroyUserNow extends PersonBasePage
{


    /**
     * 提交业务
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
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode()); 
        /**
         * SS.DestroyUserNowRegSVC.tradeReg
         * 这个接口是立即销户的接口_在生产上已经存在
         */
        data.put("TRADE_TYPE_CODE", "192");
        IDataset dataset = CSViewCall.call(this, "SS.DestroyUserNowRegSVC.tradeReg", data);
        setAjax(dataset);
        
        
    }
    
    
    public void init(IRequestCycle cycle) throws Exception{
    	
        IData info = new DataMap();
   
        this.setInfo(info);
    }
    /**
     * 认证结束之后，设置用户相关信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void setPageCustInfo(IRequestCycle cycle) throws Exception
    {
        IData pagedata = getData();
        
        IData result=new DataMap();       
	    
        //获取cpe串号信息
        IDataset otherInfos=CSViewCall.call(this, "SS.CPEActiveSVC.qryDeviceInfo", pagedata);
        if(IDataUtil.isNotEmpty(otherInfos)){
        	setOtherInfo(otherInfos.getData(0));
        }
        this.setAjax(result);
    }
    
    public abstract void setOtherInfo(IData otherInfo);
    public abstract void setInfo(IData info);


}
