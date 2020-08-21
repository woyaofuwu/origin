
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.selectgroup;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class SelectGroupPopupPage extends GroupBasePage
{
    public abstract IData getInfo();

    /**
     * 作用：查询集团客户信息
     * 
     * @author luoy
     * @param cycle
     * @throws Throwable
     */
    public void queryGroupCusts(IRequestCycle cycle) throws Throwable
    {
        IDataset result = new DatasetList();
        IData conParams = getData("cond", true);
        String id = conParams.getString("groupId");
        String strQueryType = getData().getString("QueryType");
        long tt = 0;

        if (strQueryType.equals("0")) // 按集团客户编码
        {
            result.add(UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, id, false));
        }
        else if (strQueryType.equals("1"))// 按集团客户名称
        {
            IDataOutput dd = UCAInfoIntfViewUtil.qryGrpCustInfoByCustName(this, id, getPagination("ActiveNav"));
            result = dd.getData();
            tt = dd.getDataCount();
        }
        else if (strQueryType.equals("2"))// 按证件号
        {
            result = UCAInfoIntfViewUtil.qryGrpCustInfoByPsptId(this, conParams.getString("pstType"), conParams.getString("pstNum"), false);
        }
        else if (strQueryType.equals("3"))// 按集团服务号码
        {
            IData param = new DataMap();
            param.put("SERIAL_NUMBER", id);

            // 查用户信息
            IData grpUCA = UCAInfoIntfViewUtil.qryGrpUCAInfoByGrpSn(this, id, false);

            if (IDataUtil.isNotEmpty(grpUCA))
            {
                result.add(grpUCA.getData("GRP_CUST_INFO"));
            }
        }

        setInfos(result);
        setCondition(getData("cond", true));
        setInfosCount(tt);

    }

    public abstract void setCondition(IData condition);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfosCount(long count);
}
