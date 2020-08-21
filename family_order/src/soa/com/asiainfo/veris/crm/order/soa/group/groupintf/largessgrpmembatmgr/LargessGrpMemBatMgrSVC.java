
package com.asiainfo.veris.crm.order.soa.group.groupintf.largessgrpmembatmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;



public class LargessGrpMemBatMgrSVC extends CSBizService
{
    
    private static final long serialVersionUID = 1L;

    /**
     * 畅享流量成员批量新增
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset createBatLargessLimitationMember(IData inParam) throws Exception
    {
        LargessGrpMemBatMgrBean bean = new LargessGrpMemBatMgrBean();

        return bean.createBatLargessLimitationMember(inParam);
    }
    
}