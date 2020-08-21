
package com.asiainfo.veris.crm.order.web.group.querygroupinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public abstract class GroupPRBTQuery extends CSBasePage
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
        // 导出权限
        String staffId = ((CSBasePage) this).getVisit().getStaffId();
        boolean priv = StaffPrivUtil.isFuncDataPriv(staffId, "GROUPPRBTQRY_EXT");
        setPriv(priv);
    }

    /**
     * @Description:集团彩铃查询
     * @author wusf
     * @date 2009-8-4
     * @param cycle
     * @throws Exception
     */
    public void queryInfos(IRequestCycle cycle) throws Exception
    {
        IData param = getData("cond", true);
        String snA = param.getString("SERIAL_NUMBER", "");
        String snB = param.getString("SERIAL_NUMBER_B", "");
        IDataOutput ido = null;
        if (!"".equals(snA))
        {// 按产品编码和手机号码查询
            IData grpUserInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, snA);
            String grpUserId = grpUserInfo.getString("USER_ID");
            param.put("USER_ID_A", grpUserId);
            if (!"".equals(snB))
            {// 按产品编码和手机号码查询
                ido = CSViewCall.callPage(this, "SS.GroupInfoQuerySVC.qryGroupPRBTInfo", param, getPagination("pageNav"));
            }
            else
            {// 按产品编码查询
                ido = CSViewCall.callPage(this, "SS.GroupInfoQuerySVC.qryGroupPRBByProductId", param, getPagination("pageNav"));
            }
        }
        else if (!"".equals(snB) && "".equals(snA))
        {// 按手机号码查询
            param.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER_B", ""));
            ido = CSViewCall.callPage(this, "SS.GroupInfoQuerySVC.qryGroupPRBTBySN", param, getPagination("pageNav"));
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
        setCondition(getData("cond"));
        setInfos(dataset);
    }

    public abstract void setCondition(IData condition);

    public abstract void setHintInfo(String hintInfo);

    public abstract void setInfos(IDataset infos);

    public abstract void setPageCounts(long pageCounts);

    public abstract void setPriv(boolean priv);

}
