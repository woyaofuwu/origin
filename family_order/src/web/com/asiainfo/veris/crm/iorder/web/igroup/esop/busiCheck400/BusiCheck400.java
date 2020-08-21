package com.asiainfo.veris.crm.iorder.web.igroup.esop.busiCheck400;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public abstract class BusiCheck400 extends EopBasePage {

    public abstract void setCondition(IData cond);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfo(IData info);

    public abstract void setInfosCount(long infosCount);

    /**
     * 专线开通统计报表查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryBusiInfos(IRequestCycle cycle) throws Exception {

        IData data = this.getData();
        IDataOutput output = CSViewCall.callPage(this, "SS.WorkFormSVC.queryBusiInfos", data, this.getPagination("olcomnav"));
        IDataset eomsInfos = output.getData();
        if (IDataUtil.isNotEmpty(eomsInfos)) {
            for (Object object : eomsInfos) {
                IData busiInfo = (IData) object;
                IData groupInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, busiInfo.getString("GROUP_ID"));
                String custMgrId = groupInfo.getString("CUST_MANAGER_ID");
                if (StringUtils.isNotEmpty(custMgrId)) {
                    IData managerInfo = UCAInfoIntfViewUtil.qryCustManagerByCustManagerId(this, custMgrId);
                    busiInfo.put("GROUP_MGR_CUST_NAME", managerInfo.getString("CUST_MANAGER_NAME"));
                }
                busiInfo.put("CUST_MANAGER_ID", groupInfo.getString("CUST_MANAGER_ID"));
            }

        }
        setInfosCount(output.getDataCount());
        this.setInfos(eomsInfos);
        this.setCondition(data);
    }

}
