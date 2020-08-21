package com.asiainfo.veris.crm.iorder.web.igroup.esop.marketing;

import java.util.Calendar;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizview.base.CSBasePage;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class MarketingHandle extends CSBasePage  {
	public abstract void setInfos(IDataset infos);
	public abstract void setCondition(IData Condition);
	public abstract void setCount(long count);
	
	/**
	 * 查询
	 * @throws Exception
	 */
	public void queryAllMarketing(IRequestCycle cycle) throws Exception{
		IData data = getData();
		IDataOutput output = CSViewCall.callPage(this, "SS.TapMarketingSVC.selTapMarketingByIbsysidMarketing", data, getPagination("queryNav"));
		IDataset infos = output.getData();
		setCount(output.getDataCount());
		setInfos(infos);
		setCondition(data);
	}
	
	/**
	 * 试用营销活动到期处理提交
	 * @throws Exception
	 */
	public void submitInfos(IRequestCycle cycle) throws Exception{
		IData data = new DataMap(getData().getString("SUBMIT_PARAM"));
		data.put("RESULT_INFO", "等待稽核");
		CSViewCall.call(this, "SS.TapMarketingSVC.updateTapMarketingByIbsysidMarketing", data);
		insertAttach(data);	
	}
	
	/**
	 * 试用营销活动到期处理附件提交
	 * @throws Exception
	 */
	public void insertAttach(IData data) throws Exception
    {
		IDataset attachList = data.getDataset("ATTACH_LIST");
		String ibsysid = data.getString("IBSYSID_MARKETING");
        if(IDataUtil.isEmpty(attachList))
        {
            return ;
        }
        for(int i = 0, size = attachList.size(); i < size; i++)
        {
            IData attach = attachList.getData(i);
            IData wfAttach = new DataMap();
            wfAttach.put("IBSYSID", ibsysid);
            wfAttach.put("SUB_IBSYSID", ibsysid+"1");
            wfAttach.put("NODE_ID", "-1");
            wfAttach.put("ATTACH_TYPE", attach.getString("ATTACH_TYPE"));
            wfAttach.put("FILE_ID", attach.getString("FILE_ID"));
            wfAttach.put("DISPLAY_NAME", attach.getString("FILE_NAME"));
            wfAttach.put("ATTACH_NAME", attach.getString("FILE_NAME"));
            wfAttach.put("ATTACH_LENGTH", attach.getString("FILE_SIZE"));
            wfAttach.put("REMARK", attach.getString("REMARK"));
            wfAttach.put("GROUP_SEQ", "0");
            wfAttach.put("RECORD_NUM", "0");
            wfAttach.put("SEQ", i);
            wfAttach.put("VALID_TAG", "0");
            Calendar cale = Calendar.getInstance();
            int month = cale.get(cale.MONTH);
            wfAttach.put("ACCEPT_MONTH", month+1);
            CSViewCall.call(this, "SS.WorkformAttachSVC.insertAttach", wfAttach);
        }
    }
	
	/**
	 * 试用营销活动到期稽核提交
	 * @throws Exception
	 */
	public void submitInfosForAudit(IRequestCycle cycle) throws Exception{
		IData data = getData();
		String staffId = getVisit().getStaffId();
		data.put("AUDIT_STAFF_ID", staffId);
		CSViewCall.call(this, "SS.TapMarketingSVC.updateTapMarketingByIbsysidMarketingAudit", data);
	}
}
