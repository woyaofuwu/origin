package com.asiainfo.veris.crm.order.web.person.internetofthings;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryHSSUserState extends PersonBasePage {

	 public abstract void setInfo(IData info);

	 public abstract void setInfos(IDataset infos);
	 
	 public abstract void setCondition(IData info);
	/**
	 * 查询HSS信息
	 * @param cycle
	 * @throws Exception
	 */
    public void queryInfos(IRequestCycle cycle) throws Exception
    {
    	 IData condParams = this.getData("cond");

         IData queryData = new DataMap();

       	 String serNumber = condParams.getString("SERIAL_NUMBER", ""); //号码
         
         IData queryHSSUserStatusInfo=new DataMap();
         
         queryData.put("KIND_ID", "BIP3B620_T3000620_0_0");
         
         queryHSSUserStatusInfo.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS()); //操作时间
         queryHSSUserStatusInfo.put("OPR_SEQ", "8981BIP3B620"+SysDateMgr.getSysDateYYYYMMDDHHMMSS()+"000001");  //操作流水
         queryHSSUserStatusInfo.put("SERIAL_NUMBER", serNumber); //号码信息
         
         queryData.put("CONDITION_PARAM", queryHSSUserStatusInfo);

         IDataset queryInfos = CSViewCall.call(this, "SS.QueryHSSUserStateService.iBossQuery", queryData);

         IData ajax = new DataMap();
         if(null != queryInfos && queryInfos.size() > 0)
        	 setInfos(queryInfos);
         else {
             ajax.put("ERROR_DESC", "获取HSS用户状态无数据!");
             this.setAjax(ajax);
         }
    }
}