package com.asiainfo.veris.crm.order.web.person.evaluecard;

import org.apache.tapestry.IRequestCycle;


import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class UpDownRsaPublicKey extends PersonBasePage{
	public abstract void setCondition(IData cond);
	public abstract void setInfos(IDataset infos);
	public abstract void setCount(long count);
    /**
     * 安全密钥上行更新
     * add by huping 20161009
     * @param clcle
     * @throws Exception
     */
    public void upRsaPublicKey(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset result = CSViewCall.call(this, "SS.TelValueCardSVC.UpRsaPublicKey", data);
        setCondition(data);
        
        if (result != null && !result.isEmpty() && "00".equals(result.getData(0).getString("UPDATE_RESULT"))) {
        	data.put("IS_SUCCESS", "0");
		}else { 
			data.put("IS_SUCCESS", "1");
		}
        setAjax(data);
    }
    
    /**
     * 查询安全密钥
     * add by huping
     * @param clcle
     * @throws Exception
     */
    public void qryRsaPublicKey(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataOutput result = CSViewCall.callPage(this, "SS.TelValueCardSVC.QryRsaPublicKey", data,getPagination("queryNav"));
        setCount(result.getDataCount());
        setInfos(result.getData());  
        setAjax(result.getData());
    }
    
    public boolean hasRsaKey(String rightcode) throws Exception{
        IData param = new DataMap();
        param.put("SUBSYS_CODE", "CSM");
        param.put("PARAM_ATTR", "1119");
        param.put("PARA_CODE1", this.getVisit().getStaffId());
        param.put("EPARCHY_CODE", this.getVisit().getLoginEparchyCode());
    	IDataset result = CSViewCall.call(this, "CS.CommparaInfoQrySVC.getCommparaInfoByParacode1AndAttr", param);
    	if(IDataUtil.isNotEmpty(result)){
    		return true;
    	}
		return false;
    }
}
