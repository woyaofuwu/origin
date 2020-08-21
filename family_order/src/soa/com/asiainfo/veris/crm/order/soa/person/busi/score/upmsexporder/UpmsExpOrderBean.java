
package com.asiainfo.veris.crm.order.soa.person.busi.score.upmsexporder;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.score.QryScoreInfo;

public class UpmsExpOrderBean extends CSBizBean
{
    /**
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryExpOrder(IData param, Pagination pagination) throws Exception
    {
        return QryScoreInfo.queryExpOrder(param.getString("SERIAL_NUMBER"), param.getString("ORDER_ID"), param.getString("PARENT_ID"), param.getString("SUB_ID"), param.getString("cond_EXPTYPE"), pagination);
    }
}
