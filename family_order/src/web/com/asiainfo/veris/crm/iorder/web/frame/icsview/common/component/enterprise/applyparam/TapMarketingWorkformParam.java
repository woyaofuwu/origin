package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.applyparam;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizTempComponent;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class TapMarketingWorkformParam extends BizTempComponent
{
	public abstract void setInfo(IData info);
	public abstract void setReason(IData reason);
	public abstract void setReasonList(IDataset reasonList);
	public abstract void setRowIndex(int rowIndex);
	public abstract String getOperCode();
	public abstract String getTempletId();
	
	public abstract void setDirectInfo(IData directInfo);
	public abstract void setUserInfo(IData userInfo);
	public abstract void setDirectLineList(IDataset directLineList);
	public abstract void setStaffInfo(IData staffInfo);
	public abstract void setTapMarketingList(IDataset tapMarketingList);
	public abstract void setTapMarketingLineList(IDataset tapMarketingLineList);
	public abstract void setAttachInfo(IData attachInfo);
	

    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        boolean isAjax = isAjaxServiceReuqest(cycle);
        String jsFile = "scripts/iorder/icsserv/component/enterprise/applyparam/TapMarketingWorkformParam.js";

        if (isAjax)
        {
            includeScript(writer, jsFile, false, false);
        }
        else
        {
            getPage().addResAfterBodyBegin(jsFile, false, false);
        }
        IData param = getPage().getData();
        String templetId = param.getString("TEMPLET_ID","");
        if(templetId.equals("ETAPMARKETINGENTERINGUPD")||templetId.equals("ETAPMARKETINGEXCITATION")){
        	IData paramMarke = new DataMap();
			paramMarke.put("RESPONSIBILITY_ID",getVisit().getStaffId());
			paramMarke.put("LINETYPE",param.getString("OFFER_CODE"));
			IDataset output = CSViewCall.call(this,"SS.TapMarketingSVC.selTapMarketingByConditionAll", paramMarke);
			if (null != output && output.size()>0){
	             for (int i = output.size()-1; i >=0; i--) {
	                    IData tempMarke = output.getData(i);
	                    if(Integer.parseInt(tempMarke.getString("RESULT_CODE","0"))>0){
	                    	output.remove(i);
	                    }else{
	                    	tempMarke.put("TEXT",tempMarke.getString("IBSYSID_TAPMARKETING","")+"|"+tempMarke.getString("LINENAME",""));
	                    }
	                    
	             }
	             
	                
	                
			 }
			
            setTapMarketingList(output);

        }
        
        
        IData staffInfo=new DataMap();
		staffInfo.put("staffName", getVisit().getStaffName());
		staffInfo.put("staffId", getVisit().getStaffId());
		staffInfo.put("staffNumber", getVisit().getSerialNumber());
		staffInfo.put("cityCode", getVisit().getCityCode());
		setStaffInfo(staffInfo);
    }
    
}



