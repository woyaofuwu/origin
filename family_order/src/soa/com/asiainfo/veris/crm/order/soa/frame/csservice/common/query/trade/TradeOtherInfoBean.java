
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

public class TradeOtherInfoBean 
{

    /**
     * @Description 查询预受理登记在TF_B_TRADE_OTHER中未操作的状态
     * @author jch
     * @date
     * @param param
     * @return IDataset
     * @throws Exception
     */
    public static IDataset queryBbossManageDetailInfo(String cond_GROUP_ID, String cond_OPERATE_FLAG, String cond_POSPECNUMBER, String cond_START_DATE, String cond_END_DATE, String TRADE_ID, String cond_PRODUCTSPECNUMBER, Pagination page)   throws Exception
    {
    	IDataset results =  TradeOtherInfoQry.queryBbossManageDetailInfo(cond_GROUP_ID, cond_OPERATE_FLAG, cond_POSPECNUMBER, cond_START_DATE, cond_END_DATE, TRADE_ID, cond_PRODUCTSPECNUMBER,page);
    	if (IDataUtil.isEmpty(results))
    	{
			return results;
		}
    	for (int i = 0, isize = results.size() ; i < isize; i++) 
    	{
    		IData result = results.getData(i);
    		String grpCustId = result.getString("CUST_ID");
            IData grpCustInfos = UcaInfoQry.qryGrpInfoByCustId(grpCustId);
            if (IDataUtil.isEmpty(grpCustInfos))
            {
                continue;
            }
			result.put("CUST_NAME", grpCustInfos.getString("CUST_NAME"));
		}
    	return results;
    }

}
