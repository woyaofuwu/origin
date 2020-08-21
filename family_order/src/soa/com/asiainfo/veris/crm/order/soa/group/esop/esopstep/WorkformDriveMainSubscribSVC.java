package com.asiainfo.veris.crm.order.soa.group.esop.esopstep;

import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweNodeQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformModiTraceBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformOtherBean;

public class WorkformDriveMainSubscribSVC extends CSBizService {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public IDataset driveMainSubscribe(IData data) throws Exception {
        IDataset results = new DatasetList();
        String ibsysid = data.getString("IBSYSID");
        String nodeId = data.getString("NODE_ID");
        
        IData input = new DataMap();
        input.put("IBSYSID", ibsysid);
        IDataset modiTraceList = WorkformModiTraceBean.qryModiTraceByIbsysid(input);
        if(DataUtils.isEmpty(modiTraceList)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据IBSYSID=" + ibsysid + "未查询到流程关联表TF_B_EOP_MODI_TRACE信息！");
        }
        IData modiTrace = modiTraceList.first();
        String mainIbsysid = modiTrace.getString("MAIN_IBSYSID");
        /*boolean flag = false;
        IDataset applyOtherList = WorkformOtherBean.qryByIbsysidNodeId(ibsysid, "apply");
        if(DataUtils.isEmpty(applyOtherList)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据IBSYSID=" + ibsysid + "未查询TF_B_EOP_OTHER表信息！");
        }
        for (int i = 0; i < applyOtherList.size(); i++) {
            IData applyOther = applyOtherList.getData(i);
            String attrCode = applyOther.getString("ATTR_CODE");
            String attrValue = applyOther.getString("ATTR_VALUE");
            if("ACCEPTTANCE_PERIOD".equals(attrCode) && "2".equals(attrValue)) {
                flag = true;
            }
        }
        
        boolean isUpdata = false;
        //审核不通过或未审核需改变计费方式
        IDataset auditOtherList = WorkformOtherBean.qryByIbsysidNodeId(ibsysid, "applyAudit");
        if(DataUtils.isNotEmpty(auditOtherList)) {
            for (int i = 0; i < auditOtherList.size(); i++) {
                String attrCode = auditOtherList.getData(i).getString("ATTR_CODE");
                String attrValue = auditOtherList.getData(i).getString("ATTR_VALUE");
                if("AUDIT_RESULT".equals(attrCode) && "0".equals(attrValue)) {
                    isUpdata = true;
                }
            }
        } else {
            isUpdata = true;
            //updataAcceptanceperiod(mainIbsysid);
        }
        
        if(flag && isUpdata) {
            updataAcceptanceperiod(mainIbsysid);
        }*/

        //插异步表，驱动专线开通流程SS.EweAsynSVC.saveAsynInfo
        IDataset eweSubDatas = EweNodeQry.qryEweByIbsysid(mainIbsysid, "1");
        if(DataUtils.isEmpty(eweSubDatas)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据IBSYSID=" + ibsysid + "未查询到子流程！");
        }
        for (int i = 0; i < eweSubDatas.size(); i++) {
            IData asynData = new DataMap();
            asynData.put("BUSIFORM_ID", eweSubDatas.getData(i).getString("BUSIFORM_ID"));
            asynData.put("NODE_ID", "waitAudit");
            asynData.put("EPARCHY_CODE", eweSubDatas.getData(i).getString("EPARCHY_CODE"));
            asynData.put("ACCEPT_DEPART_ID", eweSubDatas.getData(i).getString("ACCEPT_DEPART_ID"));
            asynData.put("UPDATE_DEPART_ID", eweSubDatas.getData(i).getString("ACCEPT_DEPART_ID"));
            asynData.put("ACCEPT_STAFF_ID", eweSubDatas.getData(i).getString("ACCEPT_STAFF_ID"));
            asynData.put("UPDATE_STAFF_ID", eweSubDatas.getData(i).getString("ACCEPT_STAFF_ID"));
            CSAppCall.call("SS.EweAsynSVC.saveAsynInfo", asynData);
        }
        return results;
    }

    private boolean updataAcceptanceperiod(String ibsysid) throws Exception {
        IDataset mainOtherList = WorkformOtherBean.qryByIbsysidNodeId(ibsysid, "apply");
        if(DataUtils.isEmpty(mainOtherList)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据专线开通流程IBSYSY=" + ibsysid + ",未获取到tf_b_eop_other表信息！");
        }
        //审核不通过，计费方式变为"下账期生效"
        String acceptanceperiod = "1";
        String subIbsysId = SeqMgr.getSubIbsysId();
        for (int i = 0; i < mainOtherList.size(); i++) {
            IData mainOther = mainOtherList.getData(i);
            mainOther.put("SUB_IBSYSID", subIbsysId);
            mainOther.put("SEQ", SeqMgr.getAttrSeq());
            mainOther.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
            mainOther.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
            mainOther.put("ACCEPT_TIME", SysDateMgr.getSysTime());
            mainOther.put("UPDATE_TIME", SysDateMgr.getSysTime());
            mainOther.put("INSERT_TIME", SysDateMgr.getSysTime());
            if("ACCEPTTANCE_PERIOD".equals(mainOther.getString("ATTR_CODE"))) {
                mainOther.put("ATTR_VALUE", acceptanceperiod);
            }
        }
        int[] num = WorkformOtherBean.insertWorkformOther(mainOtherList);
        if(num.length > 0) {
            return true;
        } else {
            return false;
        }

    }

}
