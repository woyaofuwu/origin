
package com.asiainfo.veris.crm.order.web.group.querygroupinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public abstract class GroupBookQuery extends CSBasePage
{

    public IDataOutput getPersonBookInfoByCond(IData cond, Pagination pg) throws Exception
    {
        IDataOutput ido = null;
        setCondition(cond);

        IData condition = getData("cond", true);
        String queryType = cond.getString("cond_QUERY_TYPE");

        if (queryType != null && queryType.equals("0"))
        {// 根据个人手机号码查询
            ido = CSViewCall.callPage(this, "CS.UserGrpMebPlatSvcInfoQrySVC.getUserProductBySnUid", condition, pg);
        }
        if (queryType != null && queryType.equals("1"))
        {// 根据集团端口(服务号码)查询
            IData data = new DataMap();
            data.put("SERV_CODE", condition.getString("SERIAL_NUMBER"));
            ido = CSViewCall.callPage(this, "CS.UserGrpMebPlatSvcInfoQrySVC.getUserProductBySnUid", data, pg);
        }
        if (queryType != null && queryType.equals("2"))
        {// 根据集团用户手机号码
            IData infoData = new DataMap();
            IData userInfos = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, cond.getString("cond_SERIAL_NUMBER"), false);
            if (IDataUtil.isEmpty(userInfos))
                return ido;
            infoData.put("EC_USER_ID", userInfos.getString("USER_ID"));
            ido = CSViewCall.callPage(this, "CS.UserGrpMebPlatSvcInfoQrySVC.getUserProductBySnUid", infoData, pg);

        }
        return ido;
    }

    public void initial(IRequestCycle cycle) throws Exception
    {
        setHintInfo("请输入查询条件~~!");
    }

    // 查询按钮
    public void queryPersonBookInfo(IRequestCycle cycle) throws Exception
    {
        if (!getData("cond", true).containsKey("SERIAL_NUMBER"))
        {
            CSViewException.apperr(GrpException.CRM_GRP_626);
        }

        IDataOutput ido = getPersonBookInfoByCond(getData("cond", false), getPagination("pageNav"));
        if (ido == null)
        {
            setHintInfo("没有符合查询条件的记录~~!");
            return;
        }
        IDataset dataset = ido.getData();
        if (IDataUtil.isEmpty(dataset))
        {
            setHintInfo("没有符合查询条件的记录~~!");
        }
        else
        {
            setHintInfo("查询成功~~!");
        }
        long tt = 0;
        tt = ido.getDataCount();
        setPageCounts(tt);
        setInfos(dataset);
    }

    public abstract void setCondition(IData data);

    public abstract void setExist(String str);

    public abstract void setHintInfo(String hintInfo);

    public abstract void setInfo(IData data);

    public abstract void setInfos(IDataset dataset);

    public abstract void setPageCounts(long pageCounts);

}
