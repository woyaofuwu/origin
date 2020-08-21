
package com.asiainfo.veris.crm.order.soa.person.busi.activesalecardopen;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class ActiveSaleCardOpenSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 进行校验判断
     * 
     * @param pd
     * @param inData
     * @return
     * @throws Exception
     */
    public void checkInfo(IData inData) throws Exception
    {
        IData data = new DataMap();
        String staffDepartId = CSBizBean.getVisit().getDepartId();
        String cityCode =CSBizBean.getVisit().getCityCode();
        String inModeCode =CSBizBean.getVisit().getInModeCode();

        String developDepartId = inData.getString("DEVELOP_DEPART_ID", "");
        if (!"2".equals(inData.getString("ACCT_TAG")))
            CSAppException.apperr(CrmUserException.CRM_USER_1106);
        //短厅不判断买断部门 sunxin
        if("HNSJ".equals(cityCode)&&"5".equals(inModeCode))
        	return;
        if (!staffDepartId.equals(developDepartId))
        {
            if (!StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "SYS_CRM_ACTIVE_SALECARD"))
                CSAppException.apperr(CrmUserException.CRM_USER_1105);

        }

        
    }

}
