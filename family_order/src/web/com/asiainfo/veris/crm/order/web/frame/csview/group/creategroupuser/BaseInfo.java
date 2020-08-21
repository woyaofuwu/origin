
package com.asiainfo.veris.crm.order.web.frame.csview.group.creategroupuser;

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
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.commparainfo.CommParaInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupProductUtilView;

public abstract class BaseInfo extends GroupBasePage
{

    public void cleanupAfterRender(IRequestCycle cycle)
    {
        super.cleanupAfterRender(cycle);
        setCondition(null);
    }

    public abstract IData getProductInfo();// 产品信息

    /**
     * 作用：页面初始化
     * 
     * @author zhujm 2009-03-06
     * @param cycle请求参数
     * @throws Throwable
     * @throws Throwable
     */
    public void initial(IRequestCycle cycle) throws Throwable
    {
        IData param = getData();

        String ibsysid = param.getString("IBSYSID", "");
        String productIdStr = param.getString("PRODUCT_ID", "");
        
        IData condition = new DataMap();
        String grpUserEparchyCode = getTradeEparchyCode();
        condition.put("GRP_USER_EPARCHYCODE", grpUserEparchyCode);

        String ttGrp = param.getString("IS_TTGRP", "false");
        condition.put("IS_TTGRP", ttGrp);
        condition.put("CATALOG_ID", "1000");// 如果变动大  就走配置

        if (!StringUtils.isEmpty(ibsysid))
        {
            IData inData = new DataMap();
            inData.put("NODE_ID", param.getString("NODE_ID", ""));
            inData.put("IBSYSID", param.getString("IBSYSID", ""));
            inData.put("SUB_IBSYSID", param.getString("SUB_IBSYSID", ""));
            inData.put("OPER_CODE", "13");
            IDataset httResultSetDataset = CSViewCall.call(this, "SS.ESOPQcsGrpBusiIntfSvc.getEosInfo", inData);
            if (IDataUtil.isEmpty(httResultSetDataset))
                CSViewException.apperr(GrpException.CRM_GRP_508);
            IData httpResult = httResultSetDataset.getData(0);

            /* 拼端到端台帐数据，tf_b_trade_ext表保存所需要数据 结束 */
            String groupId = httpResult.getString("GROUP_ID", "");
            String productId = httpResult.getString("PRODUCT_ID", productIdStr);
            String userId = httpResult.getString("USER_ID", "");

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

            // 根据产品ID 判断td_s_compare表，如果有数据，就填04 没有就填 01
            IDataset dataset = CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndParamCodeEparchyCode(this, "CSM", "3369", productId, grpUserEparchyCode);
            if (IDataUtil.isEmpty(dataset))
            {
                eosData.put("RSRV_STR2", "01");
            }
            else
            {
                eosData.put("RSRV_STR2", "04");
            }
            eosData.put("RSRV_STR3", param.getString("SUB_IBSYSID"));
            eos.add(eosData);
            condition.put("EOS", eos);
            condition.put("ESOP_TAG", "ESOP");

            param.put("USER_ID", userId);
            param.put("PRODUCT_ID", productId);
            condition.put("ESOP_USER_ID", userId);
            condition.put("ESOP_PRODUCT_ID", productId);
            condition.put("PRODUCT_ID", productId);
            condition.put("IBSYSID", ibsysid);
            condition.put("cond_GROUP_ID", groupId);

            IData result = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
            getData().put("cond_GROUP_ID", groupId);
            getData().put("CUST_ID", result.getString("CUST_ID"));
            getData().put("PRODUCT_ID", productId);
            setGroupInfo(result);

            // queryGrpUserInfos(cycle);

            IData productInfo = GroupProductUtilView.getProductExplainInfo(this, productId);
            String productName = productInfo.getString("PRODUCT_NAME");
            condition.put("ESOP_PRODUCT_NAME", productName);

        }
        setCondition(condition);
    }

    public void queryGrpUserInfos(IRequestCycle cycle) throws Throwable
    {
        String custid = getData().getString("CUST_ID", "");
        IDataset userinfos = UCAInfoIntfViewUtil.qryGrpUserInfoByCustIdAndProId(this, custid, getData().getString("PRODUCT_ID"), false);
        setUseInfos(userinfos);

    }

    public abstract void setCompProductInfo(IData compProductInfo);// 组合产品信息

    public abstract void setCondition(IData condition);

    public abstract void setGroupInfo(IData groupInfo);

    public abstract void setProductCtrlInfo(IData productCtrlInfo);

    public abstract void setProductInfo(IData productInfo);// 产品信息

    public abstract void setUseInfos(IDataset useInfo);

}
