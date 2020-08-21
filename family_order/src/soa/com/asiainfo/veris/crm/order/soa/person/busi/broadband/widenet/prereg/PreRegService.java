/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.prereg;

import java.util.List;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.ivalues.INodeData;
import com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.ivalues.ISubmitData;
import com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.util.SubmitDataParseUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: PreRegService.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-3-25 下午04:37:11 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-3-25 chengxf2 v1.0.0 修改原因
 */

public class PreRegService extends CSBizService
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
     * @date: 2014-4-4 下午04:44:32 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-4 chengxf2 v1.0.0 修改原因
     */
    private IData buildWideNetBookValue(ISubmitData submitData)
    {
        List<INodeData> nodeDataList = submitData.getNodeDataList();
        if (!nodeDataList.isEmpty())
        {
            INodeData nodeData = nodeDataList.get(0);
            if (nodeData.containsJsonObject("WIDENET_BOOK_DATA"))
            {
                return nodeData.getJsonObject("WIDENET_BOOK_DATA").getValue();
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
     * @date: 2014-4-4 下午04:49:54 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-4 chengxf2 v1.0.0 修改原因
     */
    public IData dealService(IData input) throws Exception
    {
        ISubmitData submitData = SubmitDataParseUtil.parseSubmitData(input);
        String eparchyCode = this.getTradeEparchyCode();
        IData wideNetBookValue = buildWideNetBookValue(submitData);
        if (IDataUtil.isNotEmpty(wideNetBookValue))
        {
            wideNetBookValue.put("AUDIT_STATUS", "1");
            wideNetBookValue.put("INST_ID", SeqMgr.getInstId(eparchyCode));
            wideNetBookValue.put("REG_DATE", SysDateMgr.getSysTime());
            wideNetBookValue.put("UPDATE_STAFF_ID", getVisit().getStaffId());
            wideNetBookValue.put("UPDATE_DEPART_ID", getVisit().getDepartId());
            Dao.insert("TF_F_WIDENET_BOOK", wideNetBookValue, eparchyCode);
        }
        return wideNetBookValue;
    }

}
