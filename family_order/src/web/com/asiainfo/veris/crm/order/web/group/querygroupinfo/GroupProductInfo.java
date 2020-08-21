
package com.asiainfo.veris.crm.order.web.group.querygroupinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class GroupProductInfo extends GroupBasePage
{
    public abstract IData getCondition();

    public abstract IDataset getInfos();

    public abstract IData getMebinfo();

    public abstract IDataset getMebinfos();

    /**
     * @Description: 初始化页面方法
     * @author jch
     * @date 2009-8-5
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
        setHintInfo("请输入查询条件~~!");
    }

    /**
     * @Description:查询集团产品信息
     * @author jch
     * @date 2009-8-5
     * @param cycle
     * @throws Exception
     */
    public void queryInfos(IRequestCycle cycle) throws Exception
    {
        queryGrpInfos(cycle);
        queryMebInfos(cycle);

    }

    public void queryGrpInfos(IRequestCycle cycle) throws Exception
    {
        IData param = getData("cond", true);
        String isFlag = "".equals(param.getString("IS_Flag", "")) ? "0" : "1"; // 是否查询历史 勾选为1
        param.put("IS_Flag", isFlag);
        param.put("GROUP_ID", getData().getString("POP_cond_GROUP_ID", ""));
        IDataOutput ido = CSViewCall.callPage(this, "SS.GroupInfoQuerySVC.qryProductInfo", param, getPagination("pageNav"));
        IDataset dataset = ido.getData();
        long tt = 0;
        tt = ido.getDataCount();
        setPageCounts(tt);

        if (IDataUtil.isEmpty(dataset))
        {
            setHintInfo("没有符合条件的查询结果~~！");
        }
        else
        {
            setHintInfo("查询成功~~！");
        }
        setCondition(getData("cond"));
        setInfos(dataset);

    }

    public void queryMebInfos(IRequestCycle cycle) throws Exception
    {
        IData param = getData("cond", true);
        String isFlag = "".equals(param.getString("IS_Flag", "")) ? "0" : "1"; // 是否查询历史 勾选为1
        param.put("IS_Flag", isFlag);
        param.put("GROUP_ID", getData().getString("POP_cond_GROUP_ID", ""));

        IDataOutput ido = CSViewCall.callPage(this, "SS.GroupInfoQuerySVC.qryProductMebInfo", param, getPagination("pageNav2"));
        IDataset dataset = ido.getData();
        long tt = 0;
        tt = ido.getDataCount();
        setPageCounts2(tt);

        setMebinfos(dataset);

    }

    public abstract void setCondition(IData condition);

    public abstract void setHintInfo(String hintInfo);

    public abstract void setInfos(IDataset infos);

    public abstract void setMebinfo(IData mebinfo);

    public abstract void setMebinfos(IDataset mebinfos);

    public abstract void setPageCounts(long pageCounts);

    public abstract void setPageCounts2(long pageCounts);

}
