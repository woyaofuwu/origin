package com.asiainfo.veris.crm.iorder.web.igroup.esop.accountmanagement;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.ScrDataTrans;
import com.asiainfo.veris.crm.iorder.web.igroup.util.WorkfromViewCall;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class ManagementerComfire extends EopBasePage {

    public abstract void setPattrs(IDataset pattrs) throws Exception;

    public abstract void setInfo(IData info) throws Exception;

    @Override
    public void initPage(IRequestCycle cycle) throws Exception {
        // TODO Auto-generated method stub
        super.initPage(cycle);
        
        IData data = getData();
        String ibsysid = data.getString("IBSYSID");
        String nodeId = data.getString("NODE_ID");
        String busiformNodeId = data.getString("BUSIFORM_NODE_ID");
        String busiformId = data.getString("BUSIFORM_ID");
        IData subscribeData = WorkfromViewCall.qryWorkformSubscribeByIbsysid(this, ibsysid);
        if(IDataUtil.isEmpty(subscribeData)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID=" + ibsysid + "未查询到流程订单主表数据！");

        }
        subscribeData.put("NODE_ID", nodeId);
        subscribeData.put("BUSIFORM_NODE_ID", busiformNodeId);
        subscribeData.put("BUSIFORM_ID", busiformId);
        IData info = new DataMap();
        info.put("EOS_COMMON_DATA", ScrDataTrans.buildEosCommonData(subscribeData));
        info.put("NODE_ID", nodeId);
        setInfo(info);

        getPattrs(subscribeData);
        
    }

    private void getPattrs(IData subscribeData) throws Exception {
        IData param = new DataMap();
        param.put("IBSYSID", subscribeData.getString("IBSYSID"));
        IDataset productSubList = CSViewCall.call(this, "SS.WorkformProductSubSVC.qryProductByIbsysid", param);
        if(IDataUtil.isNotEmpty(productSubList)) {
            IDataset pattrs = new DatasetList();
            for (int i = 0; i < productSubList.size(); i++) {
                IData productSub = productSubList.getData(i);
                String userId = productSub.getString("USER_ID");
                IData input = new DataMap();
                input.put("USER_ID", userId);
                IData pattr = CSViewCall.callone(this, "SS.GrpLineInfoQrySVC.queryDataLineUserInfo", input);
                pattrs.add(pattr);
            }
            setPattrs(pattrs);
        }
    }

}
