
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.developstaff;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.developstaff.DevelopStaffBean;

public class DevelopStaffSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 业务初始化 加载发展员工配置信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getDevelopStaffConfig(IData input) throws Exception
    {
        IDataset returnSet = new DatasetList();
        IData staffConfData = DevelopStaffBean.getDevelopStaffConfig(input);
        returnSet.add(staffConfData);
        return returnSet;
    }

}
