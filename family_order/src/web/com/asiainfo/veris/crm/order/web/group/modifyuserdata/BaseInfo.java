
package com.asiainfo.veris.crm.order.web.group.modifyuserdata;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.commparainfo.CommParaInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupProductUtilView;

public abstract class BaseInfo extends GroupBasePage
{

    public IData getEsopInitParam(IData inParam) throws Exception
    {
        IData condition = new DataMap();
        IData inData = new DataMap();
        inData.put("NODE_ID", inParam.getString("NODE_ID", ""));
        inData.put("IBSYSID", inParam.getString("IBSYSID", ""));
        inData.put("SUB_IBSYSID", inParam.getString("SUB_IBSYSID", ""));
        inData.put("OPER_CODE", "13");
        inData.putAll(getData());
        IData httpResult = CSViewCall.callone(this, "SS.ESOPQcsGrpBusiIntfSvc.getEosInfo", inData);
        if (IDataUtil.isEmpty(httpResult))
            CSViewException.apperr(GrpException.CRM_GRP_508);
        /* 拼端到端台帐数据，tf_b_trade_ext表保存所需要数据 结束 */
        String groupId = httpResult.getString("GROUP_ID", "");
        String productId = httpResult.getString("PRODUCT_ID", "");
        String userId = httpResult.getString("USER_ID", "");

        /* 拼端到端台帐数据，tf_b_trade_ext表保存所需要数据 开始 */
        IData eosData = new DataMap();
        IDataset eos = new DatasetList();
        eosData.put("IBSYSID", inParam.getString("IBSYSID"));
        eosData.put("SUB_IBSYSID", inParam.getString("SUB_IBSYSID"));
        eosData.put("NODE_ID", inParam.getString("NODE_ID"));
        eosData.put("WORK_ID", inParam.getString("WORK_ID"));
        eosData.put("BPM_TEMPLET_ID", inParam.getString("BPM_TEMPLET_ID"));
        eosData.put("MAIN_TEMPLET_ID", inParam.getString("MAIN_TEMPLET_ID"));
        eosData.put("ATTR_CODE", "ESOP");
        eosData.put("ATTR_VALUE", inParam.getString("IBSYSID"));
        eosData.put("RSRV_STR1", inParam.getString("NODE_ID"));

        // 根据产品ID 判断td_s_compare表，如果有数据，就填04 没有就填 01
        IDataset dataset = CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndParamCodeEparchyCode(this, "CSM", "3369", productId, getTradeEparchyCode());
        if (IDataUtil.isEmpty(dataset))
        {
            eosData.put("RSRV_STR2", "01");
        }
        else
        {
            eosData.put("RSRV_STR2", "04");
        }
        eos.add(eosData);
        condition.put("EOS", eos);
        condition.put("ESOP_TAG", "ESOP");

        condition.put("ESOP_USER_ID", userId);
        condition.put("ESOP_PRODUCT_ID", productId);
        condition.put("cond_GROUP_ID", groupId);
        return condition;
    }

    public abstract IData getInfo();

    public abstract IData getProductInfo();// 产品信息

    /**
     * 作用：页面的初始化
     * 
     * @author luojh 2009-07-29
     * @param cycle
     * @throws Throwable
     */
    public void initial(IRequestCycle cycle) throws Throwable
    {
        IData inParam = getData();

        // inParam.put("IBSYSID", "222");
        String ibsysid = inParam.getString("IBSYSID", "");
        if (StringUtils.isEmpty(ibsysid))
            return;
        IData condition = getEsopInitParam(inParam);

        String grpUserId = condition.getString("ESOP_USER_ID");

        IData data = UCAInfoIntfViewUtil.qryGrpUCAInfoByUserId(this, grpUserId);

        if (IDataUtil.isEmpty(data))
        {
            return;
        }

        IData userinfo = data.getData("GRP_USER_INFO");
        IData groupinfo = data.getData("GRP_CUST_INFO");
        setGroupInfo(groupinfo);
        setUserInfo(userinfo);

        // 获取产品信息

        productId = userinfo.getString("PRODUCT_ID");
        IData productInfo = GroupProductUtilView.getProductExplainInfo(this, productId);
        setProductInfo(productInfo);
        String productName = productInfo.getString("PRODUCT_NAME");
        condition.put("ESOP_PRODUCT_NAME", productName);
        setProductCtrlInfo(AttrBizInfoIntfViewUtil.qryChgUsProductCtrlInfoByProductId(this, productId));

        IDataset userinfos = new DatasetList();
        userinfo.put("CHECKED", "true");
        userinfos.add(userinfo);
        setUseInfos(userinfos);
        setCondition(condition);

    }

    public abstract void setCompProductInfo(IData compProductInfo);// 组合产品信息

    public abstract void setCondition(IData condition);

    public abstract void setGroupInfo(IData groupInfo);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setProductCtrlInfo(IData productCtrlInfo);// 用户信息

    public abstract void setProductInfo(IData productInfo);// 产品信息

    public abstract void setUseInfos(IDataset useInfo);

    public abstract void setUserInfo(IData userInfo);// 用户信息

}
