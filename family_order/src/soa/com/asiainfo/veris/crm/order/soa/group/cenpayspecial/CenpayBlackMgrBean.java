
package com.asiainfo.veris.crm.order.soa.group.cenpayspecial;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;


public class CenpayBlackMgrBean extends CSBizBean
{
	
    /**
     * 自由充产品黑名单用户管理分页查询
     * 
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset queryCenpayBlackList(IData param, Pagination pagination) 
    	throws Exception
    {
        IDataset infos = CenpayBlackMgrQry.queryCenpayBlackList(param, pagination);
        return infos;
    }

    /**
     * 根据UserId查询记录
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryCenpayBlackByUserId(IData param) 
    	throws Exception
    {
        IDataset infos = CenpayBlackMgrQry.queryCenpayBlackByUserId(param);
        return infos;
    }
    
    /**
     * 根据user_id删除记录
     * @param input
     * @throws Exception
     */
    public void delCenpayBlackByUserId(IData input) 
    	throws Exception
    {
    	if(IDataUtil.isNotEmpty(input))
    	{
    		IDataset userIdList = input.getDataset("USERID_LIST");
    		if(IDataUtil.isNotEmpty(userIdList))
    		{
    			for(int i=0; i < userIdList.size(); i++)
    			{
    				IData userIdData = userIdList.getData(i);
    				String userId = userIdData.getString("USER_ID","");
    				if(userId != null && !"".equals(userId))
    				{
    					IData inputData = new DataMap(); 
    					inputData.put("USER_ID", userId);
    					int returnInt = CenpayBlackMgrQry.delCenpayBlackByUserId(userId);
    				}
    			}
    		}
    	}
    }
    
    /**
     * 根据user_id修改记录
     * @param input
     * @throws Exception
     */
    public void updateCenpayBlackByUserId(IData input) 
    	throws Exception
    {
    	if(IDataUtil.isNotEmpty(input))
    	{
    		String userId = input.getString("USER_ID","");
    		String blackTag = input.getString("BLACK_TAG","");
    		if(StringUtils.isNotBlank(userId))
    		{
    			int returnInt = CenpayBlackMgrQry.updateCenpayBlackByUserId(userId, blackTag);
    		}
    	}
    }
    
    /**
     * 新增黑名单用户
     * @param cenpayData
     * @return
     * @throws Exception
     */
    public boolean addCenpayBlack(IData cenpayData) 
    	throws Exception
    {
    	boolean flag = false;
    	if(IDataUtil.isNotEmpty(cenpayData))
    	{
    		flag = CenpayBlackMgrQry.addCenpayBlack(cenpayData);
    	}
    	return flag;
    }
}
