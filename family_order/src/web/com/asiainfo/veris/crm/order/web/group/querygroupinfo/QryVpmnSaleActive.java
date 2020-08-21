
package com.asiainfo.veris.crm.order.web.group.querygroupinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class QryVpmnSaleActive extends CSBasePage
{

    public abstract IData getCondition();

    public abstract void setCondition(IData condition);

    public abstract IDataset getInfos();

    public abstract void setInfos(IDataset infos);

    public abstract void setSaleInfo(IData saleInfo);

    public abstract void setHintInfo(String hintInfo);

    public abstract void setPageCounts(long pageCounts);

    /**
     * @Description: 初始化页面方法
     * @author wusf
     * @date 2009-8-5
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
        setHintInfo("请输入查询条件~~!");
    }

    /**
     * @Description: 集团V网营销活动查询
     * @author sungq3
     * @date 2014-04-29
     * @param cycle
     * @throws Exception
     */
    public void queryInfos(IRequestCycle cycle) throws Exception
    {
        String serial_number = getData().getString("cond_SERIAL_NUMBER", "");
        String activetype = getData().getString("cond_ACTIVE_TYPE", "");

        IData uisParam = new DataMap();
        uisParam.put("SERIAL_NUMBER", serial_number);
        uisParam.put("REMOVE_TAG", "0");
        uisParam.put("NET_TYPE_CODE", "00");
        IDataset userInfoset = CSViewCall.call(this, "CS.UserInfoQrySVC.queryUserInfoBySN", uisParam);
        IData userInfo = new DataMap();
        if (IDataUtil.isNotEmpty(userInfoset))
        {
            userInfo = (IData) userInfoset.get(0);
        }
        else
        {
            // /"推荐号码【"+serial_number+"】用户信息不存在!"
            setHintInfo("推荐号码【" + serial_number + "】用户信息不存在!");
            CSViewException.apperr(VpmnUserException.VPMN_USER_118, serial_number);
        }
        IData inparam = new DataMap();
        inparam.put("USER_ID_A", userInfo.getString("USER_ID", ""));
        inparam.put("ACTIVE_TYPE", activetype);
        inparam.put(Route.ROUTE_EPARCHY_CODE, userInfo.getString("EPARCHY_CODE"));
        IDataOutput vpmnOutput = CSViewCall.callPage(this, "SS.VpmnSaleActiveQrySVC.queryVPMNSaleActiveByUserIdAActype", inparam, getPagination("pageNav"));
        IDataset vpmnDataset = vpmnOutput.getData();

        int saleTotal = 0; // 赠送话费总金额
        IData uuparam = new DataMap();
        uuparam.put("USER_ID_A", userInfo.getString("USER_ID", ""));
        uuparam.put("ACTIVE_TYPE", activetype);
        IDataset totalSaleset = CSViewCall.call(this, "SS.VpmnSaleActiveQrySVC.getTotalSaleActiveByGtag", uuparam);
        if (IDataUtil.isNotEmpty(totalSaleset))
        {
            saleTotal = totalSaleset.size();
        }
        IData saleInfo = new DataMap();
        saleInfo.put("TOTAL", vpmnDataset.size());
        saleInfo.put("MONEY", saleTotal * 3);
        if (IDataUtil.isEmpty(vpmnDataset))
        {
            setHintInfo("没有符合条件的查询结果~~！");
        }
        else
        {
            for (int i = 0, size = vpmnDataset.size(); i < size; i++)
            {
                IData vpmnData = vpmnDataset.getData(i);
                String rsrvDate = vpmnData.getString("RSRV_DATE2", "");
                String giveData = vpmnData.getString("GIVE_DATE", "");
                String giveTag = vpmnData.getString("GIVE_TAG", "");
                vpmnData.put("RSRV_DATE2", !"".equals(rsrvDate) ? SysDateMgr.decodeTimestamp(rsrvDate, SysDateMgr.PATTERN_STAND_YYYYMMDD) : "");
                vpmnData.put("GIVE_DATE", !"".equals(giveData) ? SysDateMgr.decodeTimestamp(giveData, SysDateMgr.PATTERN_STAND_YYYYMMDD) : "");
                vpmnData.put("GIVE_TAG", "1".equals(giveTag) ? "已赠送" : "未赠送");
            }
            setHintInfo("查询成功~~！");
        }
        setSaleInfo(saleInfo);
        setCondition(getData());
        setInfos(vpmnDataset);
    }
}
