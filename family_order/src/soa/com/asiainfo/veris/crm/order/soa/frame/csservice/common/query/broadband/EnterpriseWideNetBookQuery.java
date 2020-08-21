package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: WideNetBookInfoQuery.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-3-31 上午09:44:34 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-3-31 chengxf2 v1.0.0 修改原因
 */

public class EnterpriseWideNetBookQuery
{
    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-31 上午09:52:00 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-31 chengxf2 v1.0.0 修改原因
     */
    public static IDataset getEnterpriseWideNetBookList(String enterpriseName, String enterpriseAddr, String startDate, String endDate,Pagination pagin) throws Exception
    {
        IData param = new DataMap();
        param.put("ENTERPRISE_NAME", enterpriseName); //企业名称
        param.put("ENTERPRISE_ADDR", enterpriseAddr); //企业地址
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        return Dao.qryByCode("TF_F_ENTERPRISE_WIDENET_BOOK", "SEL_BY_PK1", param, pagin);
    }
}
