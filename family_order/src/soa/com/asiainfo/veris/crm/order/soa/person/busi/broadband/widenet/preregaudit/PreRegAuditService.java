/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.preregaudit;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.ivalues.INodeData;
import com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.ivalues.ISubmitData;
import com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.util.SubmitDataParseUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WideNetBookQuery;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: PreRegAuditService.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-3-27 下午03:04:14 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-3-27 chengxf2 v1.0.0 修改原因
 */

public class PreRegAuditService extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-4 下午04:50:21 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-4 chengxf2 v1.0.0 修改原因
     */
    public IDataset dealService(IData input) throws Exception
    {
        ISubmitData submitData = SubmitDataParseUtil.parseSubmitData(input);
        String eparchyCode = this.getTradeEparchyCode();
        IDataset auditValueList = getAuditValueList(submitData);
        if (DataSetUtils.isNotBlank(auditValueList))
        {
            Dao.executeBatchByCodeCode("TF_B_TRADE_WIDENETHN", "UPD_PRE_STATUS", auditValueList, eparchyCode);
        }
        return auditValueList;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-4 下午04:51:35 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-4 chengxf2 v1.0.0 修改原因
     */
    private IDataset getAuditValueList(ISubmitData submitData)
    {
        List<INodeData> nodeDataList = submitData.getNodeDataList();
        if (!nodeDataList.isEmpty())
        {
            INodeData nodeData = nodeDataList.get(0);
            if (nodeData.containsJsonArray("WIDENET_BOOK_AUDIT_DATA"))
            {
                return nodeData.getJsonArray("WIDENET_BOOK_AUDIT_DATA").getValue();
            }
        }
        return null;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-5-22 上午11:21:27 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-5-22 chengxf2 v1.0.0 修改原因
     */
    public IDataset getWideNetBookList(IData inData) throws Exception
    {
        String contactSn = inData.getString("cond_CONTACT_SN");
        String custName = inData.getString("cond_CUST_NAME");
        String auditStatus = inData.getString("cond_AUDIT_STATUS");
        String startDate = inData.getString("cond_START_DATE");
        String endDate = inData.getString("cond_END_DATE");
        Pagination pagin = this.getPagination();
        return WideNetBookQuery.getWideNetBookList(contactSn, custName, auditStatus, startDate, endDate, pagin);
    }
}
