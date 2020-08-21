
package com.asiainfo.veris.crm.order.soa.group.vpmnmanagermgr;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class VpmnCustManagerMgrSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 分配VPMN客户经理权限
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public IData doDispatchVPMN(IData inparam) throws Exception
    {
        return VpmnCustManagerMgrBean.doDispatchVPMN(inparam);
    }

    /**
     * @Description:处理VPMN产品客户经理分配批量导入数据
     * @author sungq3
     * @date 2014-05-22
     * @return
     * @throws Exception
     */
    public boolean doThisVpmnInfo(IData inparam) throws Exception
    {
        return VpmnCustManagerMgrBean.doThisVpmnInfo(inparam);
    }

    /**
     * @Description:VPMN客户经理权限分配--将临时表数据导入到正式表中
     * @author sungq3
     * @date 2014-06-27
     * @param pd
     * @param import_id
     * @throws Exception
     */
    public boolean doThisVpmnManagerImport(IData inparam) throws Exception
    {
        return VpmnCustManagerMgrBean.doThisVpmnManagerImport(inparam);
    }

    /**
     * @Description:VPMN客户经理分配（从临时表导入正式表）
     * @author sungq3
     * @date 2014-06-27
     * @param pd
     * @param importId
     * @throws Exception
     */
    public boolean doThisVpmnManagerInfo(IData inparam) throws Exception
    {
        return VpmnCustManagerMgrBean.doThisVpmnManagerInfo(inparam);
    }
}
