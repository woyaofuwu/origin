
package com.asiainfo.veris.crm.order.soa.person.busi.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QryBOSSInfoSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;


    /**
     * @param param
     * @return
     * @throws Exception
     * 一级BOSS业务办理记录查询
     */
    public IDataset qryBOSSTradeHistoryInfo(IData param) throws Exception {
        QryBOSSInfoBean bean = BeanManager.createBean(QryBOSSInfoBean.class);
        return bean.qryBOSSTradeHistoryInfo(param, getPagination());
    }
}