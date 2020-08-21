
package com.asiainfo.veris.crm.order.soa.person.busi.view360;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.QryBOSSInfoDAO;

public class QryBOSSInfoBean extends CSBizBean
{
	Logger log = Logger.getLogger(QryBOSSInfoBean.class);


	/**
     * 根据SERIAL_NUMBER 查询业务历史信息
     * 
     * @throws Exception
     */
    public IDataset qryBOSSTradeHistoryInfo(IData data, Pagination pagination) throws Exception
    {
    	return QryBOSSInfoDAO.queryBOSSHistoryInfo(data, pagination);
    }
}