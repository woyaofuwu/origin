package com.asiainfo.veris.crm.order.web.person.internetofthings;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;


public abstract class ChgEffectTime extends PersonBasePage{
	 
    public void getEffectTime(IRequestCycle cycle) throws Exception
    {
    	IData ajax = new DataMap();
        IData param = this.getData();
        String flag = param.getString("FLAG");
        IDataset testDiscnts = CSViewCall.call(this, "SS.ChgEffectTimeSVC.queryTestValidDiscnt", param);
        IData discnt = new DataMap();
        if("0".equals(flag)){
            if (testDiscnts != null && !testDiscnts.isEmpty())
            {
            	discnt = testDiscnts.getData(0);
            	discnt.put("FLAG", "0");
                discnt.put("START_DATE", discnt.getString("START_DATE").substring(0, 10));
                discnt.put("END_DATE", discnt.getString("END_DATE"));
            }
            else
            {
                ajax.put("ERROR_DESC", "该用户没有订购未生效测试期优惠，不能办理测试期变更业务");
                discnt.put("FLAG", "0");
                this.setAjax(ajax);
            }
        }
        if("1".equals(flag)){
            	IDataset normalDiscnts = CSViewCall.call(this, "SS.ChgEffectTimeSVC.queryNormalValidDiscnt", param);
            	if (normalDiscnts != null && !normalDiscnts.isEmpty()){
            		discnt = normalDiscnts.getData(0);
            		discnt.put("START_DATE", discnt.getString("START_DATE").substring(0, 10));
                    discnt.put("END_DATE", discnt.getString("END_DATE"));
            		discnt.put("FLAG", "1");
            	}else
                {
                    ajax.put("ERROR_DESC", "该用户没有订购未生效正常期优惠，不能办理正常期变更业务");
                    discnt.put("FLAG", "1");
                    this.setAjax(ajax);
                }
            	
            	if (testDiscnts != null && !testDiscnts.isEmpty())
                {
                	ajax.put("ERROR_DESC", "用户已订购了未生效测试期优惠，不能办理正常期变更业务");
                	this.setAjax(ajax);
                }
            }
       
        discnt.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
    	discnt.put("USER_ID", param.getString("USER_ID"));
    	setInfo(discnt);
    	
    	
    }
    
    public void getENDTime(IRequestCycle cycle) throws Exception
    {
    	
    	IData ajax = new DataMap();
        IData param = this.getData();
    	IDataset result = CSViewCall.call(this, "SS.ChgEffectTimeSVC.queryENDTime", param);
    	if (result != null && !result.isEmpty())
        {
         	ajax.put("END_DATE", result.getData(0).getString("END_DATE"));
        }
    	setInfo(ajax);
    	this.setAjax(ajax);
    	 
    	 
    }
    
    public abstract void setInfo(IData info);

    /**
     * 提交生成订单
     * 
     * @param cycle
     * @throws Exception
     */
    public void submitTrade(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData();
        IDataset result = CSViewCall.call(this, "SS.ChgTestPeriodRegSVC.tradeReg", param);
        this.setAjax(result);
    }
}
