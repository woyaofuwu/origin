
package com.asiainfo.veris.crm.order.web.group.querygroupinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class QueryVPMN extends GroupBasePage
{

    public abstract IData getCondition();

    public abstract IDataset getInfos();

    /**
     * @Description: 初始化页面方法
     * @author wusf
     * @date 2009-8-3
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
        setHintInfo("请输入查询条件~~!");
    }

    /**
     * @Description: vpmn资料
     * @throws Exception
     */
    public void queryInfos(IRequestCycle cycle) throws Exception
    {
        IData param = getData("cond", true);
        String sn = param.getString("SERIAL_NUMBER");
        IDataOutput ido = null;
        if ("0".equals(param.getString("QueryModeVPMN"))) // 查询方式：0-vpmn编码 1-成员服务号码
        { // 按VPMN编码

            IData userInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, param.getString("VPN_NO"));
            if (IDataUtil.isNotEmpty(userInfo))
            {
                param.put("USER_ID", userInfo.get("USER_ID"));
            }
            ido = CSViewCall.callPage(this, "SS.GroupInfoQuerySVC.qryVpmnInfo", param, getPagination("pageNav"));
        }
        else if ("1".equals(param.getString("QueryModeVPMN")))
        { // 成员服务号码
            IData userInfo = UCAInfoIntfViewUtil.qryMebUserInfoBySn(this, sn);
            if (IDataUtil.isNotEmpty(userInfo))
            {
                String mebUserId = userInfo.getString("USER_ID");
                param.put("MEB_USER_ID", mebUserId);
                IDataset dataset1 = CSViewCall.call(this, "SS.GroupInfoQuerySVC.qryVpmnInfoRelation", param);
                if (IDataUtil.isNotEmpty(dataset1))
                {
                    IData data = new DataMap();
                    data = dataset1.getData(0);
                    param.put("USER_ID", data.get("USER_ID_A"));
                    ido = CSViewCall.callPage(this, "SS.GroupInfoQuerySVC.qryVpmnInfo", param, getPagination("pageNav"));
                }
            }
        }
        else
        {
            setHintInfo("请输入查询条件！");
            return;
        }
        if (ido == null)
        {
            setHintInfo("没有符合条件的查询结果~~！");
            return;
        }
        IDataset dataset = ido.getData();
        if (IDataUtil.isEmpty(dataset))
        {
            setHintInfo("没有符合条件的查询结果~~！");
        }
        else
        {
            setHintInfo("查询成功~~！");
        }
        long tt = 0;
        tt = ido.getDataCount();
        setPageCounts(tt);
        setInfos(dataset);
        setCondition(getData("cond"));
    }

    public abstract void setCondition(IData condition);

    public abstract void setHintInfo(String hintInfo);

    public abstract void setInfos(IDataset infos);

    public abstract void setPageCounts(long pageCounts);
}
