
package com.asiainfo.veris.crm.order.web.group.cenpayspecial;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;



public abstract class CenpaySpecialMgr extends GroupBasePage
{
	public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
    
    public abstract void setCondition(IData condition);
    
    public abstract void setCenpayCount(long cenpayCount);

    public abstract void setCenpayList(IDataset cenpayList);
    
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
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryCenpaySpecialList(IRequestCycle cycle) throws Exception
    {
    	IDataset dataList = new DatasetList();
    	IData condData = getData("cond", true);
//    	String serialNumber = condData.getString("SERIAL_NUMBER", "");
//        if (StringUtils.isBlank(serialNumber))
//        {
//        	CSViewException.apperr(GrpException.CRM_GRP_33);
//        }

        IData data = new DataMap();
        data.put("SERIAL_NUMBER", condData.getString("SERIAL_NUMBER"));
        data.put("REMOVE_TAG", condData.getString("STATUS"));
        
        IDataOutput outPut = CSViewCall.callPage(this, 
        		"SS.CenpaySpecialMgrSVC.queryCenpaySpecialList", 
        		data, 
        		getPagination("ratioNavBar"));
        dataList = outPut.getData();
        long cenpayCount = outPut.getDataCount();
        
        if(IDataUtil.isNotEmpty(dataList))
        {
        	for(int i=0; i < dataList.size(); i++)
        	{
        		IData  cenpayData = dataList.getData(i);
        		String productId = cenpayData.getString("PRODUCT_ID","");
        		if(StringUtils.isNotBlank(productId))
        		{
        			String productName = ProductInfoIntfViewUtil.qryProductNameStrByProductId(this, 
        					productId, false);
        			if(StringUtils.isNotBlank(productName))
        			{
        				cenpayData.put("PRODUCT_NAME", productName);
        			}
        			else 
        			{
        				cenpayData.put("PRODUCT_NAME", "");
        			}
        		}
        	}
        }
        
        setCenpayList(dataList);
        setCenpayCount(cenpayCount);
        
        
//        IData grpParam = new DataMap();
//        grpParam.put("SERIAL_NUMBER", serialNumber);
//        // 查用户信息
//        IDataset userList = getGrpUserInfos(grpParam);
//        IData grpUserInfo = userList.getData(0);
//        String custId = grpUserInfo.getString("CUST_ID", "");
//        String userId = grpUserInfo.getString("USER_ID", "");
//        String productId = userList.getData(0).getString("PRODUCT_ID", "");
//        
//        if(!"7344".equals(productId) 
//        		|| !"7342".equals(productId) 
//        		|| !"7343".equals(productId))
//        {
//        	CSViewException.apperr(GrpException.CRM_GRP_906,serialNumber);
//        }
        
        setCondition(condData);

        // 设置返回数据
        setAjax(condData);
                
    }
    
    /**
     * 根据user_id删除记录
     * @param cycle
     * @throws Exception
     */
    public void delCenpaySpecialByUserId(IRequestCycle cycle) 
    	throws Exception
    {
    	IData data = getData();
    	
        IData inputData = new DataMap();
        IDataset resultList = new DatasetList();
        
        IDataset userIdList = new DatasetList(data.getString("USERID_LIST"));
        
        if(IDataUtil.isNotEmpty(userIdList))
        {
        	for(int i=0; i < userIdList.size(); i++)
			{
        		IData listData = userIdList.getData(i);
        		IData userData = new DataMap();
        		userData.put("USER_ID", listData.getString("USER_ID",""));
        		resultList.add(userData);
        	}
        	inputData.put("USERID_LIST", resultList);
        }
        IDataset infos = CSViewCall.call(this, 
        		"SS.CenpaySpecialMgrSVC.delCenpaySpecialByUserId", 
        		inputData);
        setCondition(data);
        setAjax(infos);
    }
    
    /**
     * 根据user_id修改记录
     * @param cycle
     * @throws Exception
     */
    public void updateCenpaySpecialByUserId(IRequestCycle cycle) 
    	throws Exception
    {
        IData pageData = getData();
        IData inputData = new DataMap();
        
        String userId = pageData.getString("USER_ID","");
        if(StringUtils.isBlank(userId))
        {
        	CSViewException.apperr(GrpException.CRM_GRP_907);
        }
        String removeTag = pageData.getString("EREMOVE_TAG","");
        inputData.put("USER_ID", userId);
        inputData.put("REMOVE_TAG", removeTag);
        
        IDataset infos = CSViewCall.call(this, 
        		"SS.CenpaySpecialMgrSVC.updateCenpaySpecialByUserId", 
        		inputData);
        setCondition(pageData);
        setAjax(infos);
    }
    
}
