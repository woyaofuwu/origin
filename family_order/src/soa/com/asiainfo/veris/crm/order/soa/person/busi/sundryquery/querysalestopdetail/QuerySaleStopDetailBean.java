

package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.querysalestopdetail;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
//import com.wade.container.util.StringUtil;
//import com.asiainfo.veris.crm.resource.pub.util.StringUtil;

public class QuerySaleStopDetailBean extends CSBizBean
{
	public static IDataset querySaleStopDetail(String AREA_CODE, String TRADE_STAFF_ID, String startDate, String endDate,Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_CITY_CODE", AREA_CODE);
        param.put("TRADE_STAFF_ID", TRADE_STAFF_ID);
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        return Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_SALESTOP_INFO_BYPARAMS", param,pagination);
    }
	public IDataset queryDetailInfo(IData inparams, Pagination pagination) throws Exception
    {
    	IDataset detailinfo=querySaleStopDetail(inparams.getString("AREA_CODE", ""),inparams.getString("TRADE_STAFF_ID", ""),inparams.getString("START_DATE", "")+ SysDateMgr.START_DATE_FOREVER,inparams.getString("END_DATE", "")+ SysDateMgr.END_DATE,pagination);
        if(IDataUtil.isNotEmpty(detailinfo))
        {
        	for(int i=0,size =detailinfo.size();i<size;i++)
        	{
        		IData temp = detailinfo.getData(i);
        		String departId = StaticUtil.getStaticValue(getVisit(), "TD_M_STAFF", "STAFF_ID", "DEPART_ID", temp.getString("TRADE_STAFF_ID"));
        		temp.put("STAFF_DEPART", StaticUtil.getStaticValue(getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_NAME", departId));
        		String rsrvStr4 = temp.getString("REMARK");
        		if(StringUtils.isNotBlank(rsrvStr4))
        		{
        			String[] fees = rsrvStr4.split("\\|");
        			if(fees.length>0)temp.put("FEE1", fees[0]);//应收违约金
        			if(fees.length>1)temp.put("FEE2", fees[1]);//实收违约成本金
        			if(fees.length>2)temp.put("FEE3", fees[2]);//实收违约金
        			if(fees.length>3)temp.put("FEE4", fees[3]);//实收总违约金
        			if(fees.length>4)temp.put("FEE5", fees[4]);//减免违约成本金
        			if(fees.length>5)temp.put("FEE6", fees[5]);//减免违约金
        			if(fees.length>6)temp.put("APPROVAL_NO", fees[6]);//审批工单号
        			if(fees.length>7){
        				if("0".equals(fees[7]))
        				{
        					temp.put("PAGE_NAME", "营销活动终止");//终止营销活动界面名称
        				}
        				else if("1".equals(fees[7]))
        				{
        					temp.put("PAGE_NAME", "宽带营销活动终止");//终止营销活动界面名称
        				}
        				else
        				{
        					temp.put("PAGE_NAME", "其他");//终止营销活动界面名称
        				}
        			}
        		}
        	}
        }
    	return detailinfo;
    }
    
}

