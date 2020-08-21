
package com.asiainfo.veris.crm.order.web.group.querygroupinfo;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class GroupUserForOweInfoQry extends GroupBasePage
{
    public abstract IData getCondition();

    public abstract void setCondition(IData condition);

    public abstract IDataset getInfos();

    public abstract void setInfos(IDataset infos);

    public abstract void setHintInfo(String infos);

    public abstract void setInfoCount(long infoCount);

    private static Logger logger = Logger.getLogger(GroupUserForOweInfoQry.class);
    
    public void initial(IRequestCycle cycle) throws Exception
    {
        IData param = getData("cond", true);
        this.setCondition(param);
        setHintInfo("请输入查询条件~~!");
    }

    public void queryInfos(IRequestCycle cycle) throws Exception
    {
        IData condParam = getData("cond", true);
        IData inputParam = new DataMap();
        inputParam.put("CUST_MANAGER_ID", condParam.getString("CUST_MANAGER_ID"));
        inputParam.put("GROUP_ID", condParam.getString("GROUP_ID"));
        inputParam.put("CITY_CODE", condParam.getString("GRP_CITYCODE"));
        
        String eparchyCode = getTradeEparchyCode();
        inputParam.put(Route.ROUTE_EPARCHY_CODE,eparchyCode);
        
        IDataset infos = new DatasetList();
        
        IDataOutput dataOutput = null;
    		
        dataOutput = CSViewCall.callPage(this, "SS.GroupUserForOweInfoQrySVC.qryGrpUserOweInfo",
                inputParam, getPagination("PageNav"));
        
        if (null != dataOutput && dataOutput.getData().size() > 0)
        {
            setHintInfo("查询成功~~！");
            infos = dataOutput.getData();
            
            if(IDataUtil.isNotEmpty(infos))
            {
            	for (int i = 0, len = infos.size(); i < len; i++)
                {
            		IData info = infos.getData(i);
            		String grpUserId = info.getString("USER_ID","");
            		//String custManagerId = info.getString("CUST_MANAGER_ID","");
            		if(StringUtils.isNotBlank(grpUserId))
            		{
            			//IData ucaData = UCAInfoIntfViewUtil.qryGrpUCAInfoByGrpSn(this, serialNumber,false);
            			IData grpAcctInfo = UCAInfoIntfViewUtil.qryGrpDefAcctInfoByUserId(this,grpUserId,false);
            			if(IDataUtil.isNotEmpty(grpAcctInfo))
                        {
            	            info.put("ACCT_ID", grpAcctInfo.getString("ACCT_ID",""));
                        }
            			IData params = new DataMap();
            			params.put("USER_ID", grpUserId);
            			params.put(Route.ROUTE_EPARCHY_CODE,eparchyCode);
            			IDataset outParams = CSViewCall.call(this,"SS.GroupUserForOweInfoQrySVC.getGrpUserOweInfoByUserId",params);
            			
            			if(logger.isDebugEnabled())
            			{
            				logger.info("Query Grp UserInfo Result1:" + outParams);
            			}
            			if(IDataUtil.isNotEmpty(outParams))
            			{
            				IData acctInfos = outParams.getData(0);
            				info.put("PRODUCT_OWE", acctInfos.getString("OWE_FEE","0"));
            				info.put("PRODUCT_START_CYCLE", acctInfos.getString("USER_OWE_BEGIN_DATE",""));
            				info.put("PRODUCT_END_CYCLE", acctInfos.getString("USER_OWE_END_DATE",""));
            			}
            		}
            		
//            		if(StringUtils.isNotBlank(custManagerId))
//            		{
//            			String manageName = UStaffInfoQry.getCustManageNameByCustManagerId(custManagerId);
//            			info.put("CUST_MANAGER_NAME", manageName);
//            		}
                }
            }
            
        }
        else
        {
            setHintInfo("没有符合条件的查询结果~~！");
        }
        
        setCondition(inputParam);
        setInfos(infos);
        setInfoCount(dataOutput.getDataCount());
    }
}
