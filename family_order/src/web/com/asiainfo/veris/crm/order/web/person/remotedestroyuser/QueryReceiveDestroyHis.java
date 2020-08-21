package com.asiainfo.veris.crm.order.web.person.remotedestroyuser;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

/**
 * @author zhuhong
 * @date 2018/12/14
 */
public abstract class QueryReceiveDestroyHis extends PersonBasePage {
    public abstract void setDestroyInfoList(IDataset destroyInfoList);
    public abstract void setCount(long count);

    public void queryDestroyOrder(IRequestCycle cycle) throws Exception {
        IData params = getData();
        IDataOutput dataCount = CSViewCall.callPage(this, "SS.ReceiveDestroyUserSVC.queryDestroyOrderHis", params, getPagination("nav"));
        setCount(dataCount.getDataCount());
        IDataset destroyInfoList = new DatasetList();
        destroyInfoList=dataCount.getData();
        if(IDataUtil.isNotEmpty(destroyInfoList)){
   		 for(int k=0;k<destroyInfoList.size();k++){
   			 IData destroyInfo = destroyInfoList.getData(k);
   			 String create_date = destroyInfo.getString("CREATE_DATE");
   			 String update_time = destroyInfo.getString("UPDATE_TIME");
   			create_date = SysDateMgr.decodeTimestamp(create_date, SysDateMgr.PATTERN_STAND);
   			 update_time = SysDateMgr.decodeTimestamp(update_time, SysDateMgr.PATTERN_STAND);
   			destroyInfo.put("CREATE_DATE", create_date);
   			destroyInfo.put("UPDATE_TIME", update_time);
   		 }
   		 setDestroyInfoList(destroyInfoList);
   	 }
    }
    public void onInit(IRequestCycle cycle)throws Exception{
    	 IData param = new DataMap();
    	 param.put("DEAL_TAG","0");
    	 IDataset nowOrders = CSViewCall.call(this, "SS.RemoteDestroyUserSVC.qryNowDayOrder", param);
    	 if(IDataUtil.isNotEmpty(nowOrders)){
    		 for(int k=0;k<nowOrders.size();k++){
    			 IData nowDay = nowOrders.getData(k);
    			 String create_date = nowDay.getString("CREATE_DATE");
    			 String update_time = nowDay.getString("UPDATE_TIME");
    			 create_date = SysDateMgr.decodeTimestamp(create_date, SysDateMgr.PATTERN_STAND);
    			 update_time = SysDateMgr.decodeTimestamp(update_time, SysDateMgr.PATTERN_STAND);
    			 nowDay.put("CREATE_DATE", create_date);
    			 nowDay.put("UPDATE_TIME", update_time);
    		 }
    		 setDestroyInfoList(nowOrders);
    	 }
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception {
    }
}
