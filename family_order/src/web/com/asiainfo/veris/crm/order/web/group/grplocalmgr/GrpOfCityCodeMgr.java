
package com.asiainfo.veris.crm.order.web.group.grplocalmgr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class GrpOfCityCodeMgr extends GroupBasePage
{
    /**
     * 页面初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData condData = getData("cond", true);
        setCondition(condData);
    }
   
    /**
     * 根据group_id查询集团基本信息
     * 
     * @param cycle
     * @throws Throwable
     */
    public void getGroupBaseInfo(IRequestCycle cycle) throws Throwable
    {
        IData custInfo = queryGroupCustInfo(cycle);
        if(IDataUtil.isEmpty(custInfo))
    	{
    		CSViewException.apperr(CrmCommException.CRM_COMM_103, "查询集团客户资料不存在!");
    	}
    	
        String custId = custInfo.getString("CUST_ID", "");
        String groupId = custInfo.getString("GROUP_ID", "");
        String inStaffId = custInfo.getString("IN_STAFF_ID", "");
        //集团客户经理
        String custManagerId = custInfo.getString("CUST_MANAGER_ID", "");
        if(StringUtils.isBlank(inStaffId))
        {
        	CSViewException.apperr(CrmCommException.CRM_COMM_103, "未获取到创建该集团[" + groupId + "]的工号,业务不能继续!");
        }
        if(StringUtils.isBlank(custManagerId))
        {
        	CSViewException.apperr(CrmCommException.CRM_COMM_103, "未获取到该集团[" + groupId + "]的客户经理工号,业务不能继续!");
        }
        
        //当前调整的工号
        String vStaffId = getVisit().getStaffId();
        if(StringUtils.isNotBlank(custManagerId) && StringUtils.isNotBlank(vStaffId)
        			&& !StringUtils.equals(custManagerId, vStaffId))
        {
        	if(StringUtils.isNotBlank(vStaffId) && !vStaffId.startsWith("HNSJ")
            		&& !vStaffId.startsWith("HNHN"))
            {
            	CSViewException.apperr(CrmCommException.CRM_COMM_103, 
            			"不是该集团[" + groupId + "]的客户经理或不是HNSJ或HNHN的工号,不能办理该集团局向调整!");
            }
        }
        
        String  createCityCode = StaticUtil.getStaticValue(getVisit(),"TD_M_STAFF","STAFF_ID","CITY_CODE", inStaffId);
        if(StringUtils.isBlank(createCityCode))
        {
        	CSViewException.apperr(CrmCommException.CRM_COMM_103, "未获取到创建该工号[" + inStaffId + "]的业务区,业务不能继续!");
        }
        custInfo.put("CREATE_CITY_CODE", createCityCode);
        
        IData param = new DataMap();
        param.put("CUST_ID", custId);
        IDataset extendInfo = CSViewCall.call(this, "CS.GrpExtInfoQrySVC.queryGrpExtendTestByCustId", param);
        if(IDataUtil.isEmpty(extendInfo))
        {
        	CSViewException.apperr(CrmCommException.CRM_COMM_103, "该集团[" + groupId + "]不是测试集团,业务不能继续!");
        }
        
        setInfo(custInfo);
        
        IDataset tradeInfos = new DatasetList();
        setInfos(tradeInfos);
        
    }

    /**
     * 根据集团编码查询集团客户相关信息
     * 
     * @param cycle
     * @return
     * @throws Throwable
     */
    public IData queryGroupCustInfo(IRequestCycle cycle) throws Exception
    {
        IData conParams = getData("cond", true);
        String groupId = conParams.getString("GROUP_ID");
        String custId = conParams.getString("CUST_ID");

        IData custInfo = null;

        if (StringUtils.isNotEmpty(custId))
        {
            custInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, custId);
        }
        else
        {
            custInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
        }
        return custInfo;
    }
   
    
    /**
     * 提交方法
     * 
     * @param cycle
     * @throws Throwable
     */
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Throwable
    {
        IData condData = getData();

        String custId = condData.getString("CUST_ID");
        IData custInfo = null;
        if (StringUtils.isNotEmpty(custId))
        {
            custInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, custId);
        }
        if(IDataUtil.isEmpty(custInfo))
    	{
    		CSViewException.apperr(CrmCommException.CRM_COMM_103, "查询集团客户资料不存在!!");
    	}
    	
        String changeCityCode = condData.getString("CHANGE_CITY_CODE");
        if(StringUtils.isBlank(changeCityCode))
        {
        	CSViewException.apperr(CrmCommException.CRM_COMM_103, "未取到修改后的业务区,业务不能继续!");
        }
        
        //集团客户经理
        String custManagerId = custInfo.getString("CUST_MANAGER_ID", "");
        String groupId = custInfo.getString("GROUP_ID", "");
        if(StringUtils.isBlank(custManagerId))
        {
        	CSViewException.apperr(CrmCommException.CRM_COMM_103, "未获取到该集团[" + groupId + "]的客户经理工号,业务不能继续!");
        }
        //当前调整的工号
        String vStaffId = getVisit().getStaffId();
        if(StringUtils.isNotBlank(custManagerId) && StringUtils.isNotBlank(vStaffId)
        			&& !StringUtils.equals(custManagerId, vStaffId))
        {
        	if(StringUtils.isNotBlank(vStaffId) && !vStaffId.startsWith("HNSJ")
            		&& !vStaffId.startsWith("HNHN"))
            {
            	CSViewException.apperr(CrmCommException.CRM_COMM_103, 
            			"不是该集团[" + groupId + "]的客户经理或不是HNSJ或HNHN的工号,不能办理该集团局向调整!");
            }
        }
        
        
        IData paramData = new DataMap();
        paramData.put("CUST_ID", custId);
        IDataset productInfos = CSViewCall.call(this, "SS.GrpOfCityCodeMgrSvc.queryProductInfoByCustId", paramData);
        if(IDataUtil.isEmpty(productInfos))
        {
        	CSViewException.apperr(CrmCommException.CRM_COMM_103, "该集团客户[" + custId + "]下未订购任何产品,业务不能继续!");
        }
        
        IDataset retDataset = null;
        
        for(int i = 0; i < productInfos.size(); i++)
        {
        	IData productInfo = productInfos.getData(i);
        	String userId = productInfo.getString("USER_ID");
            String serialNumber = productInfo.getString("SERIAL_NUMBER");
            
            // 调用服务数据
            IData svcData = new DataMap();
            svcData.put("CUST_ID", custId);
            svcData.put("CHANGE_CITY_CODE", changeCityCode);
            svcData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
            svcData.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
            svcData.put("USER_ID", userId);
            svcData.put("SERIAL_NUMBER", serialNumber);
            if(i==0)
            {
            	svcData.put("SYN_TAG", "1");
            }
            // 调用服务
            retDataset = CSViewCall.call(this, "SS.GrpOfCityCodeMgrSvc.crtTrade", svcData);
            
        }
        
        // 设置返回数据
        setAjax(retDataset);

    }

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
    
    public abstract void setCondition(IData condition);

}
