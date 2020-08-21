
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.userinfo;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class QuerySnByUsrpidBean extends CSBizBean
{
    private static final transient Logger logger = Logger.getLogger(QuerySnByUsrpidBean.class);

    /**
     * 合版本 duhj 2017/5/3
     * 用户资料模糊查询,原方法在CommParaMgr中
     * 
     * @author chenhao 2009-4-7
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset querySnByUsrpid(IData inparams, Pagination pagination) throws Exception
    {

        IDataset idsResult = UserInfoQry.querySnByUsrpid(inparams, pagination);
        if(IDataUtil.isNotEmpty(idsResult))
        {
        	for (int i = 0; i < idsResult.size(); i++)
        	{
        		IData idResult = idsResult.getData(i);
        		try 
        		{
            		String strUserID = idResult.getString("USER_ID", "");
        			IDataset idsOweInfo = AcctCall.queryUserEveryBalanceFeeNew(strUserID, "1");
            		if (IDataUtil.isNotEmpty(idsOweInfo)) 
        			{
        				String strBalance = idsOweInfo.first().getString("RSRV_NUM3", "0"); //客户余额
        				double balanceInt = Double.parseDouble(strBalance) / 100;
        				idResult.put("RSRV_NUM3", balanceInt);
        			}
            		else
            		{
            			idResult.put("RSRV_NUM3", "0");
    				}
				} 
        		catch (Exception e) 
        		{
        			idResult.put("RSRV_NUM3", "0");
        			if(logger.isInfoEnabled()) logger.info(e);
				}
			}
        }
        return idsResult;
    }
    
    /**
     * 宽带业务查询
     * @param inparams
     * @param pagination
     * @return
     * @throws Exception
     * @author chenhh6
     */
    public IDataset broadquerySnByUsrpid(IData inparams, Pagination pagination) throws Exception
    {
        IDataset idsResult = UserInfoQry.broadquerySnByUsrpid(inparams, pagination);
        return idsResult;
    }
}
