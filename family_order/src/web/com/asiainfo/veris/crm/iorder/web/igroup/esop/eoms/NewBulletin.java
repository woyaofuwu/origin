package com.asiainfo.veris.crm.iorder.web.igroup.esop.eoms;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizview.base.CSBasePage;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class NewBulletin extends CSBasePage
{
    public abstract void setInfos(IDataset infos);
    
    public abstract void setCondition(IData condition);
    
    public abstract void setNewInfo(IData newInfo);
    
    public void initPage(IRequestCycle cycle) throws Exception
    {
    }
    
    public void queryInfos(IRequestCycle cycle) throws Exception
    {
    	IData param = this.getData("cond");
    	
    	IData data = new DataMap();
    	String beginDate = param.getString("BEGIN_DATE", "");
    	String endDate = param.getString("END_DATE", "");
    	String replyState = param.getString("REPLY_STATE", "");
    	data.put("BEGIN_DATE", beginDate);
    	data.put("END_DATE", endDate);
    	data.put("REPLY_STATE", replyState);
    	data.put("SYS_TAG", "EOMS");
    	
        IDataset infos = CSViewCall.call(this, "SS.WorkformEomsSVC.qryBulletinReply", data);

    	setInfos(infos);
    }
    
    public void queryBulletinInfos(IRequestCycle cycle) throws Exception 
    {
    	IData param = this.getData();
    	String ibsysid = param.getString("IBSYSID", "");
    	String recordNum = param.getString("RECORD_NUM", "");
    	String subIbsysid = param.getString("SUB_IBSYSID", "");
    	String groupSeq = param.getString("GROUP_SEQ", "");
    			
    	IData data = new DataMap();
    	data.put("IBSYSID", ibsysid);
    	data.put("RECORD_NUM", recordNum);
    	data.put("OPER_TYPE", "newBulletin");
    	data.put("SUB_IBSYSID", subIbsysid);
    	data.put("GROUP_SEQ", groupSeq);
    	
        IDataset newInfos = CSViewCall.call(this, "SS.WorkformAttrSVC.qryAttrByOperTypeEoms", data);

    	data.put("OPER_TYPE", "confirmBulletin");
    	
    	IDataset confirmInfos = CSViewCall.call(this, "SS.WorkformAttrSVC.qryAttrByOperTypeEoms", data);
    	
    	IDataset resultInfos = new DatasetList();
    	resultInfos.addAll(newInfos);
    	resultInfos.addAll(confirmInfos);
    	
    	if(DataUtils.isEmpty(resultInfos))
    	{
    		return;
    	}
    	IData newInfo = new DataMap();
    	
    	for(int i = 0 ; i < resultInfos.size() ; i ++)
    	{
    		IData resultInfo = resultInfos.getData(i);
    		newInfo.put(resultInfo.getString("ATTR_CODE", ""), resultInfo.getString("ATTR_VALUE", ""));
    	}
    	
    	newInfo.put("DISABLED", true);
    	
    	this.setNewInfo(newInfo);
    }
}
