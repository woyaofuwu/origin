
package com.asiainfo.veris.crm.order.soa.person.busi.onecardncodes;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryRelationUUSVC extends CSBizService
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    protected static Logger log = Logger.getLogger(QueryRelationUUSVC.class);

    public IDataset queryRelationInfos(IData input) throws Exception
    {
        String sn = input.getString("SERIAL_NUMBER");
        QueryRelationBean bean = (QueryRelationBean) BeanManager.createBean(QueryRelationBean.class);
        return bean.queryRelationInfos(sn);
    }

}
