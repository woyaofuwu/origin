
package com.asiainfo.veris.crm.order.soa.person.busi.badness;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.badness.BadnessInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class AccessoryListBean extends CSBizBean
{

    public IDataset queryAccessoryLists(IData data) throws Exception
    {
        return BadnessInfoQry.queryAccessoryLists(data.getString("INFO_RECV_ID"));
    }

    public IDataset queryUrlPara(IData param) throws Exception
    {
       return CommparaInfoQry.getCommNetInfo("CSM", param.getString("PARAM_ATTR"), param.getString("PARAM_CODE"));
    }

    public boolean saveBADNESSInfos(String TableName, IData inparams, String[] keys) throws Exception
    {

        return Dao.save(TableName, inparams, keys, Route.CONN_CRM_CEN);

    }
}
