package com.asiainfo.veris.crm.order.soa.group.esop.eopintf;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweNodeQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformOtherBean;

public class WorkformUpdataAutoTimeSVC extends CSBizService {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public void updataAutoTime(IData data) throws Exception {

        String ibsysid = data.getString("IBSYSID");
        String busiformNodeId = data.getString("BUSIFORM_NODE_ID");
        String subBiSn = data.getString("SUB_BI_SN");

        IDataset nodeDatas = EweNodeQry.qryByBusiformNodeId(busiformNodeId);
        if(DataUtils.isEmpty(nodeDatas)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据BUSIFORM_NODE_ID=[" + busiformNodeId + "]未获取到节点信息！");
        }
        
        IDataset otherList = WorkformOtherBean.qryLastInfoByIbsysidAndAttrCode(ibsysid, "IS_AUTOBACK");
        if(DataUtils.isNotEmpty(otherList) && "1".equals(otherList.first().getString("ATTR_VALUE"))) {
            IDataset autoList = WorkformOtherBean.qryLastInfoByIbsysidAndAttrCode(ibsysid, "BACK_TIME");
            if(DataUtils.isNotEmpty(otherList)) {
                String autoTime = autoList.first().getString("ATTR_VALUE");
                if(autoTime==null||"".equals(autoTime)){
                    return;
                }
                autoTime = SysDateMgr.date2String(SysDateMgr.string2Date(autoTime, "yyyy-MM"), SysDateMgr.PATTERN_STAND);
                EweNodeQry.updEweNodeByPk(busiformNodeId, autoTime);
            }
        }


    }
}
