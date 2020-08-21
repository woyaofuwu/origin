
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttpresentdiscnt;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class CttPresentDiscntSVC extends CSBizService
{
    public IDataset queryPresentInfos(IData param) throws Exception
    {
    	IDataset lastResult = DiscntInfoQry.queryPresentInfos(param.getString("ACCT_ID"), param.getString("PRESENT_SERIAL_NUMBER"), param.getString("START_DATE"), param.getString("END_DATE"), this.getPagination());
        for(int i = 0;i<lastResult.size(); i++)
        {
        	lastResult.getData(i).put("DISCNT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(lastResult.getData(i).getString("DISCNT_CODE")));
        }
        return lastResult;
    }

    public IDataset querySendBackDiscnt(IData param) throws Exception
    {
        return UserDiscntInfoQry.getUserSendBackDiscnt(param.getString("SERIAL_NUMBER"), this.getPagination());
    }

    public IDataset queryUserPresentDiscnts(IData param) throws Exception
    {
        return UserDiscntInfoQry.getUserPresentDiscnts(param.getString("USER_ID"), param.getString("DISCNT_CODE"), param.getString("DISCNT_INST_ID"));
    }

    public void savePresentDiscnt(IData param) throws Exception
    {
        Dao.executeUpdateByCodeCode("TF_F_PRESENT_DISCNT", "INS_PRESENT_DISCNT", param);
    }

    public void updatePresentDiscnt(IData param) throws Exception
    {
        Dao.executeUpdateByCodeCode("TF_F_PRESENT_DISCNT", "UPD_BY_PK", param);
    }
}
