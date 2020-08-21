
package com.asiainfo.veris.crm.order.soa.group.grppaymark;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

public class BreakGrpPayMarkBean extends CSBizBean
{
    private Logger logger = Logger.getLogger(BreakGrpPayMarkBean.class);
    
    /**
     * 根据cust_id查询标准集团下的产品
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset queryProductByCustId(IData data) throws Exception
    {
        if(logger.isDebugEnabled())
        {
            logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 BreakGrpPayMarkBean()  >>>>>>>>>>>>>>>>>>");
        }
        IDataset infos = BreakGrpPayMarkQry.queryProductByCustId(data);
        return infos;
    }
    
    /**
     * 不截止代付关系的分页查询
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryBreakGrpPayMarkInfo(IData param, Pagination pagination) 
    	throws Exception
    {
        IDataset infos = BreakGrpPayMarkQry.qryBreakGrpPayMarkInfo(param, pagination);
        return infos;
    }
    
}
