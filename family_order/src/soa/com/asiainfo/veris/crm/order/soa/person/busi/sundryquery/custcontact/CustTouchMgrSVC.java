
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.custcontact;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class CustTouchMgrSVC extends CSBizService
{

    /**
     * 新版接触信息 添加接触信息明细的备注
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset modifyNewCustTouch(IData input) throws Exception
    {
        CustTouchMgrBean bean = (CustTouchMgrBean) BeanManager.createBean(CustTouchMgrBean.class);

        bean.modifyNewCustTouch(input);
        bean.modifyNewCustTouchLog(input);
        IDataset dataset = new DatasetList();
        IData data = new DataMap();
        data.put("RESULT_CODE", "0");
        data.put("RESULT_INFO", "OK");
        dataset.add(data);
        return dataset;
    }

}
