
package com.asiainfo.veris.crm.order.soa.group.minorec.queryAudit;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class QryEopOtherInfoSVC extends GroupOrderService
{
    private static final long serialVersionUID = 1L;
    
    /**
     * 
    * @Title: qryAuditStaffInfoByIbsysid 
    * @Description: 根绝IBSYSID查询稽核员工 
    * @param IBSYSID
    * @return
    * @throws Exception IDataset
    * @author zhangzg
    * @date 2019年11月11日上午11:39:59
     */
    public static IDataset qryAuditStaffInfoByIbsysid(IData param) throws Exception{
        IDataset results = QryEopOtherInfoBean.qryAuditStaffInfoByIbsysid(param);
        if(IDataUtil.isEmpty(results)) {
            results = QryEopOtherInfoBean.qryAuditStaffInfoHByIbsysid(param);
        }
        return results;
    }

}
