
package com.asiainfo.veris.crm.order.web.group.querygroupinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class QueryGrpBizAuditNoPassInfo extends GroupBasePage
{
    public abstract void setCondition(IData condition);
    public abstract void setInfo(IData info);
    public abstract void setInfos(IDataset infos);
    public abstract void setLogCount(long logCount);

    /**
     * 页面初始化
     * @param cycle
     * @throws Exception
     * @author chenzg
     * @date 2018-8-20
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
    	IData pgData = this.getData();
    	//业务办理员角色权限
        boolean isBizStaff = this.hasPriv("PRIV_AUDIT_BIZSTAFF");	
        //稽核人员角色
        boolean isAuditStaff = this.hasPriv("PRIV_AUDIT_AUDSTAFF");	
        pgData.put("IS_BIZSTAFF", isBizStaff);
        pgData.put("IS_AUDITSTAFF", isAuditStaff);
        this.setCondition(pgData);
    }
    /**
     * 查询集团业务稽核单信息
     * @param cycle
     * @throws Exception
     * @author chenzg
     * @date 2018-8-20
     */
    public void queryGrpAuditInfos(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("IN_STAFF_ID", this.getVisit().getStaffId());
        data.put("STATES", "1");	//审核不过，整改不通过
        if(this.getVisit().getCityCode().equals("HNSJ"))
        {
        	data.remove("IN_STAFF_ID");
        }
        else if(StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "CRM_QUERYGRPAUDITNOPASS")){
        	data.put("IN_CITY_DODE", this.getVisit().getCityCode());
        }
        
        IDataOutput output = CSViewCall.callPage(this, "SS.QueryGrpBizAuditInfoSVC.queryGrpAuditInfos", data, getPagination("LogNav"));        
        IDataset infos = output.getData();
        if(IDataUtil.isNotEmpty(infos)){
        	for(int i=0;i<infos.size();i++){
        		IData each = infos.getData(i);
        		each.put("ADD_DISCNTS_DESC", this.dealDisnctDesc(each.getString("ADD_DISCNTS", "")));
        		each.put("DEL_DISCNTS_DESC", this.dealDisnctDesc(each.getString("DEL_DISCNTS", "")));
        		each.put("MOD_DISCNTS_DESC", this.dealDisnctDesc(each.getString("MOD_DISCNTS", "")));
        		each.put("STATE_DESC", StaticUtil.getStaticValue("GROUP_BIZ_ORDERSTATE", each.getString("STATE", "")));
        		each.put("TRADE_TYPE_DESC", StaticUtil.getStaticValue(getVisit(),"TD_S_TRADETYPE","TRADE_TYPE_CODE","TRADE_TYPE",each.getString("TRADE_TYPE_CODE", "")));
        		//each.put("AUDIT_DISABLED", this.getAuditDisabled(each.getString("STATE")));
        	}
        }
        setInfos(infos);
        setLogCount(output.getDataCount());
        setCondition(data);
    }
    
    /**
     * 提交稽核处理
     * @param cycle
     * @throws Throwable
     * @author chenzg
     * @date 2018-8-20
     */
    public void submitReAudit(IRequestCycle cycle) throws Throwable
    {
    	IData pgData = this.getData();
        IData inparams = new DataMap();
        inparams.put("AUDIT_INFOS", pgData.getString("AUDIT_INFOS", "[]"));      
        inparams.put("USER_EPARCHY_CODE", "0898");
        inparams.put("TRADE_EPARCHY_CODE", "0898");
        inparams.put("ROUTE_EPARCHY_CODE", "0898");
        CSViewCall.call(this, "SS.QueryGrpBizAuditInfoSVC.dealSubmitReAudit", inparams);
        
        queryGrpAuditInfos(cycle);
    }
    /**
     * 取优惠名称信息
     * @param discntCodes
     * @return
     * @throws Exception
     * @author chenzg
     * @date 2018-8-20
     */
    private String dealDisnctDesc(String discntCodes) throws Exception{
    	String desc = "";
    	if(StringUtils.isNotBlank(discntCodes)){
    		String[] disnctArr = discntCodes.split(",");
    		for(String discntCode : disnctArr){
    			String discntName = UpcViewCall.queryOfferNameByOfferId(this, "D", discntCode);
    			desc += StringUtils.isNotBlank(desc) ? ","+discntCode+"["+discntName+"]" : discntCode+"["+discntName+"]";
    		}
    	}
    	return desc;
    }
    /**
     * 判断是否可以审核
     * @param state
     * @return
     * @author chenzg
     * @date 2018-8-29
     */
    private String getAuditDisabled(String state) {
    	String ret = "true";
    	//稽核不通过 、整改不通过的单可以再次整改
    	if("2".equals(state) || "32".equals(state)){
    		ret = "false";
    	}
		return ret;
	}
}
