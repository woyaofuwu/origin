
package com.asiainfo.veris.crm.order.web.group.vpmnmanagermgr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class VpmnGroupQuery extends CSBasePage
{
    public abstract IData getCond();

    public abstract void setCond(IData cond);

    public abstract IDataset getInfos();

    public abstract void setInfos(IDataset infos);

    public abstract void setPageCounts(long pageCounts);

    public abstract void setHintInfo(String hintInfo);

    /**
     * @Description:页面初始化
     * @author sungq3
     * @date 2014-05-17
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
        setHintInfo("请输入查询条件~~!");
    }

    /**
     * @Description:查询VPMN集团信息
     * @author sungq3
     * @date 2014-05-17
     * @param cycle
     * @throws Exception
     */
    public void qryVpmnGroupInfo(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String groupId = param.getString("con_GROUP_ID");
        String vpnNo = param.getString("con_VPN_NO");
        String vpnName = param.getString("con_VPN_NAME");
        IData inparam = new DataMap();
        inparam.put("GROUP_ID", groupId);
        inparam.put("VPN_NO", vpnNo);
        inparam.put("VPN_NAME", vpnName);
        IDataOutput vpnOutput = CSViewCall.callPage(this, "CS.CustManagerInfoQrySVC.qryVpmnGroupInfo", inparam, getPagination("pageNav"));
        IDataset vpnDataset = vpnOutput.getData();
        if (IDataUtil.isEmpty(vpnDataset))
        {
            setHintInfo("没有查询到符合条件的VPMN集团信息~~!");
        }
        setInfos(vpnDataset);
        setCond(param);
        setPageCounts(vpnOutput.getDataCount());
        // 记录客户经理操作日志
        insOperLog("VPMN集团信息查询", "QRY", "输入参数为:" + inparam, getVisit().getStaffId(), getVisit().getDepartId(), getVisit().getCityCode(), getVisit().getRemoteAddr());
    }

    private void insOperLog(String oper_mod, String oper_type, String oper_desc, String staff_id, String depart_id, String city_id, String ip_addr) throws Exception
    {
        IData logData = new DataMap();
        logData.put("OPER_MOD", oper_mod);
        logData.put("OPER_TYPE", oper_type);
        logData.put("OPER_DESC", oper_desc);
        logData.put("STAFF_ID", staff_id);
        logData.put("DEPART_ID", depart_id);
        logData.put("CITY_ID", city_id);
        logData.put("IP_ADDR", ip_addr);
        // CSViewCall.call(this, "CS.CustManagerInfoQrySVC.insertOperLog", logData);
    }
}
