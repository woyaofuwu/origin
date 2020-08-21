
package com.asiainfo.veris.crm.order.web.person.addgroupmemberbyorder;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class AuditGroupmemberByOrder extends GroupBasePage
{
    public abstract void setCondition(IData condition);
    public abstract void setInfo(IData info);
    public abstract void setInfos(IDataset infos);
    public abstract void setBookinfo(IData bookinfo);
    public abstract void setBookinfos(IDataset bookinfos);
    public abstract void setLogCount(long logCount);
    /**
     * 查询所有集团
     * @param cycle
     * @throws Exception
     * @author chenzg
     * @date 2018-8-20
     */
    public void queryGrpInfos(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();
        param.put("CITY_CODE", data.getString("CITY_CODE"));
        param.put("CUST_MANAGER_ID", data.getString("CUST_MANAGER_ID"));
        param.put("GROUP_ID", data.getString("GROUP_ID"));
        
        
        IDataOutput output = CSViewCall.callPage(this, "SS.AuditGroupmemberByOrderSVC.queryGrps", data, getPagination("LogNav"));        
        IDataset infos = output.getData();
        setInfos(infos);
        setLogCount(output.getDataCount());
        setCondition(data);
    }
    
    /**
     * 查询集团下的所有通讯录成员
     * @param cycle
     * @throws Throwable
     * @author chenzg
     * @date 2018-8-20
     */
    public void queryGrpBooktInfos(IRequestCycle cycle) throws Throwable
    {
    	IData pgData = this.getData();
    	String groupId = pgData.getString("GROUP_ID");
    	
    	IData param = new DataMap();
    	param.put("GROUP_ID", groupId.trim());
    	IDataOutput output = CSViewCall.callPage(this, "SS.AuditGroupmemberByOrderSVC.queryGrpBooktInfos", param, getPagination("LogNav"));        
        IDataset bookinfos = output.getData();
        //循环查询fileName
        for(int i=0;i<bookinfos.size();i++){
        	IData each = bookinfos.getData(i);
        	String fileId = each.getString("RSRV_STR3");
        	if(StringUtils.isNotEmpty(fileId)){
        		IData inparams = new DataMap();
            	inparams.put("FILE_ID", fileId);
            	IData fileData = CSViewCall.callone(this, "SS.ReqGroupmemberByOrderSVC.queryFileInfos", inparams);
            	if(IDataUtil.isNotEmpty(fileData)){
            		each.put("FILE_ID", fileData.getString("FILE_ID"));
                	each.put("FILE_NAME", fileData.getString("FILE_NAME"));
            	}else{
            		CSViewException.apperr(GrpException.CRM_GRP_713, "根据FILE_ID找不到表WD_F_FTPFILE对应的信息!");
            	}
            	
        	}
        }
        setBookinfos(bookinfos);
        setLogCount(output.getDataCount());
    	
    }
    
    /**
     * 提交稽核
     * @param cycle
     * @throws Throwable
     * @author chenzg
     * @date 2018-8-20
     */
    public void submitAudit(IRequestCycle cycle) throws Throwable
    {
    	IData pgData = this.getData();
    	String strAuditInfos = pgData.getString("AUDIT_INFOS");
    	IDataset auditInfos = new DatasetList(strAuditInfos);
    	if(IDataUtil.isNotEmpty(auditInfos)){
    		for(int i=0;i<auditInfos.size();i++){
    			IData each = auditInfos.getData(i);
        		IData param = new DataMap();
        		param.put("GROUP_ID", each.getString("GROUP_ID"));
            	param.put("EPARCHY_CODE", each.getString("EPARCHY_CODE"));
            	param.put("CITY_CODE", each.getString("CITY_CODE"));
            	
            	param.put("GROUP_CUST_NAME", each.getString("GROUP_CUST_NAME"));
            	param.put("CUST_MANAGER_ID", each.getString("CUST_MANAGER_ID"));
            	param.put("AUDIT_TIME", SysDateMgr.getSysTime());
            	param.put("AUDIT_STAFF_ID", getVisit().getStaffId());
            	param.put("AUDIT_DEPART_ID", getVisit().getDepartId());
            	param.put("AUDIT_REASON", each.getString("AUDIT_REASON"));
            	param.put("AUDIT_PASS_TAG", each.getString("AUDIT_PASS_TAG"));
        		CSViewCall.call(this, "SS.AuditGroupmemberByOrderSVC.submitAudit", param);
        		//稽核后update成员表是否通过字段
        		String rsrvTag3 = each.getString("AUDIT_PASS_TAG");
        		if(StringUtils.isNotEmpty(rsrvTag3)){
        			IData data = new DataMap();
            		data.put("RSRV_TAG3", rsrvTag3);
            		data.put("USER_ID", each.getString("USER_ID"));
            		CSViewCall.call(this, "SS.AuditGroupmemberByOrderSVC.updateMember", data);
        		}
        		
        	}
    	}
    	
    }
    
}
