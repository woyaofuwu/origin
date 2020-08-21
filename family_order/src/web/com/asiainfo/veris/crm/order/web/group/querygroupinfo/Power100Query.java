
package com.asiainfo.veris.crm.order.web.group.querygroupinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class Power100Query extends CSBasePage
{

    public abstract IData getCondition();

    public abstract IDataset getInfos();

    public abstract IData getMebinfo();

    public abstract IDataset getMebinfos();

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
     * 查询动力100集团产品信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryComboProductInfos(IRequestCycle cycle) throws Exception
    {
        IData condData = getData();
        // String custId = condData.getString("CUST_ID");
        IData param = new DataMap();
        param.put("CUST_ID", condData.getString("CUST_ID", ""));
        IDataOutput dataOutput = CSViewCall.callPage(this, "SS.GroupInfoQuerySVC.qryPower100InfoByCustId", param, getPagination("pageNav"));
        if (IDataUtil.isNotEmpty(dataOutput.getData()))
        {
            setInfos(dataOutput.getData());
            setCondition(condData);
            setPageCounts(dataOutput.getDataCount());
            setHintInfo("获取组合包信息成功~~!");
        }
        else
        {
            setHintInfo("获取组合包信息失败~~!");
        }
    }

    /**
     * 查询动力100子产品信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryProductInfos(IRequestCycle cycle) throws Exception
    {
        IData condData = getData();
        String userIdA = condData.getString("USER_ID_A");
        if (StringUtils.isNotEmpty(userIdA))
        {
            IData param = new DataMap();
            param.put("USER_ID_A", userIdA);
            IDataOutput dataOutput = CSViewCall.callPage(this, "SS.GroupInfoQuerySVC.qryPower100MemberProdInfosByUserIdA", param, getPagination("pageNav"));
            if (IDataUtil.isNotEmpty(dataOutput.getData()))
            {
                setMebinfos(dataOutput.getData());
                setPageCounts2(dataOutput.getDataCount());
                setHintInfo("获取产品信息成功~~!");
            }
            else
            {
                setHintInfo("获取产品信息失败~~!");
            }
        }
        else
        {
            setHintInfo("请选择产品包~~!");
        }
    }

    public abstract void setCondition(IData condition);

    public abstract void setHintInfo(String hintInfo);

    public abstract void setInfos(IDataset infos);

    public abstract void setMebinfo(IData mebinfo);

    public abstract void setMebinfos(IDataset mebinfos);

    public abstract void setPageCounts(long pageCounts);

    public abstract void setPageCounts2(long pageCounts);
}
