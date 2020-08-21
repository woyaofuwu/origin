
package com.asiainfo.veris.crm.order.web.person.addgroupmemberbyorder;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class ReqGroupmemberByOrder extends GroupBasePage
{
    public abstract void setCondition(IData condition);
    public abstract void setInfo(IData info);
    public abstract void setInfos(IDataset infos);
    public abstract void setLogCount(long logCount);
    /**
     * 集团通讯录成员查询
     * @param cycle
     * @throws Exception
     * @author 
     * @date 2018-8-20R
     */
    public void queryGrpBooktInfos(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();
        param.put("GROUP_ID", data.getString("GROUP_ID"));
        param.put("GROUP_CUST_NAME", data.getString("GROUP_CUST_NAME"));
        param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        
        IDataOutput output = CSViewCall.callPage(this, "SS.ReqGroupmemberByOrderSVC.queryGrpBooktInfos", data, getPagination("LogNav"));        
        IDataset infos = output.getData();
        //循环查询fileName
        for(int i=0;i<infos.size();i++){
        	IData each = infos.getData(i);
        	String fileId = each.getString("RSRV_STR3");
        	if(StringUtils.isNotEmpty(fileId)){
        		IData inparams = new DataMap();
            	inparams.put("FILE_ID", fileId);
            	IData fileData = CSViewCall.callone(this, "SS.ReqGroupmemberByOrderSVC.queryFileInfos", inparams);
            	if(IDataUtil.isNotEmpty(fileData)){
            		each.put("FILE_ID", fileData.getString("FILE_ID"));
                	each.put("FILE_NAME", fileData.getString("FILE_NAME"));
            	}
        	}
        }
        setInfos(infos);
        setLogCount(output.getDataCount());
        setCondition(data);
    }
    
    /**
     * 注销处理
     * @param cycle
     * @throws Throwable
     * @author chenzg
     * @date 2018-8-20
     */
    public void submitCancel(IRequestCycle cycle) throws Throwable
    {
    	IData pgData = this.getData();
    	String strCancelInfos = pgData.getString("CANCEL_INFOS");
    	IDataset cancelInfos = new DatasetList(strCancelInfos);
    	if(IDataUtil.isNotEmpty(cancelInfos)){
    		for(int i=0;i<cancelInfos.size();i++){
    			IData each = cancelInfos.getData(i);
        		IData inparams = new DataMap();
        		inparams.put("USER_ID", each.getString("USER_ID"));
        		CSViewCall.call(this, "SS.ReqGroupmemberByOrderSVC.submitCancel", inparams);
        	}
    	}
    	
    }
    /**
     * 更新附件
     * @param cycle
     * @throws Throwable
     * @author chenzg
     * @date 2018-8-20
     */
    public void updateUpload(IRequestCycle cycle) throws Throwable
    {
    	IData pgData = this.getData();
    	String strCancelInfos = pgData.getString("UPDATE_INFOS");
    	IDataset cancelInfos = new DatasetList(strCancelInfos);
    	if(IDataUtil.isNotEmpty(cancelInfos)){
    		for(int i=0;i<cancelInfos.size();i++){
    			IData each = cancelInfos.getData(i);
        		IData inparams = new DataMap();
        		inparams.put("USER_ID", each.getString("USER_ID"));
        		inparams.put("RSRV_STR3", each.getString("FILE_ID"));
        		CSViewCall.call(this, "SS.ReqGroupmemberByOrderSVC.updateUpload", inparams);
        	}
    	}
    	
    }
        
}
