
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.group.querygroupinfo.QueryGrpBizAuditInfoBean;

public class GroupGrpBizAuditNoPassInfoExportTask extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData inParam, Pagination pg) throws Exception
    {
        IDataset dataset = new DatasetList();
    	
    	inParam.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId());
    	inParam.put("STATES", "1");	//审核不过，整改不通过
        if(CSBizBean.getVisit().getCityCode().equals("HNSJ"))
        {
        	inParam.remove("IN_STAFF_ID");
        }
        else if(StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "CRM_QUERYGRPAUDITNOPASS")){
        	inParam.put("IN_CITY_DODE", CSBizBean.getVisit().getCityCode());
        }
        
    	inParam.put(Route.USER_EPARCHY_CODE, "0898");
    	inParam.put(Route.ROUTE_EPARCHY_CODE, "0898");
    	QueryGrpBizAuditInfoBean memberFileBean = (QueryGrpBizAuditInfoBean) BeanManager.createBean(QueryGrpBizAuditInfoBean.class);
    	dataset = memberFileBean.queryGrpAuditInfos(inParam, pg);
    	
    	if(IDataUtil.isNotEmpty(dataset)){
        	for(int i=0;i<dataset.size();i++){
        		IData each = dataset.getData(i);
        		String rsrvTag1 = each.getString("RSRV_TAG1","");
        		if(StringUtils.isNotBlank(rsrvTag1) && "1".equals(rsrvTag1))
        		{
        			each.put("TOWSMS_DESC", "是");
        		}
        		else 
        		{
        			each.put("TOWSMS_DESC", "否");
        		}
        		each.put("ADD_DISCNTS_DESC", this.dealDisnctDesc(each.getString("ADD_DISCNTS", "")));
        		each.put("DEL_DISCNTS_DESC", this.dealDisnctDesc(each.getString("DEL_DISCNTS", "")));
        		each.put("MOD_DISCNTS_DESC", this.dealDisnctDesc(each.getString("MOD_DISCNTS", "")));
        		each.put("STATE_DESC", StaticUtil.getStaticValue("GROUP_BIZ_ORDERSTATE", each.getString("STATE", "")));
        		each.put("TRADE_TYPE_DESC", StaticUtil.getStaticValue(getVisit(),"TD_S_TRADETYPE","TRADE_TYPE_CODE","TRADE_TYPE",each.getString("TRADE_TYPE_CODE", "")));
        	}
        }

        return dataset;

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
    			String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(discntCode);
    			desc += StringUtils.isNotBlank(desc) ? ","+discntCode+"["+discntName+"]" : discntCode+"["+discntName+"]";
    		}
    	}
    	return desc;
    }
}
