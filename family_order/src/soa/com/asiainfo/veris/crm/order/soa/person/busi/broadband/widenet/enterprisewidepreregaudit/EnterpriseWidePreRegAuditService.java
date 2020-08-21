/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.enterprisewidepreregaudit;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.EnterpriseWideNetBookQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

/**
 * Copyright: Copyright (c) 2016 Asiainfo-Linkage
 * 
 * @ClassName: PreRegAuditService.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: lijun17
 * @date: 2016-5-21 下午03:04:14 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2016-5-21 lijun17 v1.0.0 修改原因
 */

public class EnterpriseWidePreRegAuditService extends CSBizService
{
	protected static Logger log = Logger.getLogger(EnterpriseWidePreRegAuditService.class);
    private static final long serialVersionUID = 1L;

    /**
     * @Function:
     * @Description: 企业宽带预约查询
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijun17
     * @date: 2016-5-21 下午04:50:21 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2016-5-21 lijun17 v1.0.0 修改原因
     */
    public IDataset getEnterpriseWideNetBookList(IData inData) throws Exception
    {
        String enterpriseName = inData.getString("cond_ENTERPRISE_NAME");
        String enterpriseAddr = inData.getString("cond_ENTERPRISE_ADDR");
        String startDate = inData.getString("cond_START_DATE");
        String endDate = inData.getString("cond_END_DATE");
        Pagination pagin = this.getPagination();
        return EnterpriseWideNetBookQuery.getEnterpriseWideNetBookList(enterpriseName, enterpriseAddr, startDate, endDate,pagin);
    }
}
