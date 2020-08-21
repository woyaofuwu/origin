
package com.asiainfo.veris.crm.order.web.frame.csview.group.changememelement;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class BaseInfo extends GroupBasePage
{

    public abstract IData getInfo();

    /**
     * 作用：页面的初始化
     * 
     * @author luoy 2009-07-29
     * @param cycle
     * @throws Throwable
     */
    public void initial(IRequestCycle cycle) throws Throwable
    {

        /* 端到端判断，页面初始化开始 */
        IData param = getData();
        String ibsysid = param.getString("IBSYSID", "");
        if (!"".equals(ibsysid))
        {
            IData inData = new DataMap();
            inData.putAll(getData());
            inData.put("NODE_ID", param.getString("NODE_ID", ""));
            inData.put("IBSYSID", param.getString("IBSYSID", ""));
            inData.put("SUB_IBSYSID", param.getString("SUB_IBSYSID", ""));
            inData.put("OPER_CODE", "15");
            IDataset httResultSetDataset = CSViewCall.call(this, "SS.ESOPQcsGrpBusiIntfSvc.getEosInfo", inData);
            if (IDataUtil.isEmpty(httResultSetDataset))
                CSViewException.apperr(GrpException.CRM_GRP_508);

            /* 拼端到端台帐数据，tf_b_trade_ext表保存所需要数据 开始 */
            IData eosData = new DataMap();
            IDataset eos = new DatasetList();
            eosData.put("IBSYSID", param.getString("IBSYSID"));
            eosData.put("SUB_IBSYSID", param.getString("SUB_IBSYSID"));
            eosData.put("NODE_ID", param.getString("NODE_ID"));
            eosData.put("WORK_ID", param.getString("WORK_ID"));
            eosData.put("BPM_TEMPLET_ID", param.getString("BPM_TEMPLET_ID"));
            eosData.put("MAIN_TEMPLET_ID", param.getString("MAIN_TEMPLET_ID"));
            eosData.put("ATTR_CODE", "ESOP");
            eosData.put("ATTR_VALUE", param.getString("IBSYSID"));
            eosData.put("RSRV_STR1", param.getString("NODE_ID"));
            eosData.put("RSRV_STR2", "01");
            eos.add(eosData);
            IData condition = new DataMap();
            condition.put("EOS", eos);
            condition.put("ESOP_TAG", "ESOP");
            condition.put("MEM_FINISH", "true");
            condition.put("IBSYSID", ibsysid);
            setCondition(condition);
            /* 拼端到端台帐数据，tf_b_trade_ext表保存所需要数据 结束 */
        }
    }

    public abstract void setCondition(IData condition);

    public abstract void setGroupInfo(IData groupUserInfo);

    public abstract void setHidden(String hidden);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setMebUseInfo(IData info);

    public abstract void setProductCtrlInfo(IData productCtrlInfo);

    public abstract void setProductInfo(IData productInfo);// 产品信息

    public abstract void setUserInfo(IData userInfo);// 用户信息
}
