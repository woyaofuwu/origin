
package com.asiainfo.veris.crm.order.web.frame.csview.group.changeuserelement;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.uca.UCAInfoIntf;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.commparainfo.CommParaInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupProductUtilView;

public abstract class BaseInfo extends GroupBasePage
{

    public abstract IData getCondition();

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

        // 如果前台没有传递BUSITYPE默认按集团变更处理，这个开口为了给集团专线开通使用，也可统一BUSI_TYPE页面流传值的入口，后面只取这个赋值
        IData condition = new DataMap();
        String busiType = inParam.getString("BUSI_TYPE");
        if (StringUtils.isBlank(busiType))
            busiType = BizCtrlType.ChangeUserDis;
        condition.put("BUSI_TYPE", busiType);

        // 支持过滤产品树上的产品节点
        String productTreeLimitProducts = inParam.getString("PRODUCTTREE_LIMIT_PRODUCTS");
        if (StringUtils.isNotBlank(productTreeLimitProducts))
        {
            String productTreeLimitType = inParam.getString("PRODUCTTREE_LIMIT_TYPE", "1"); // 0为限制limit_products不展示。limit_type=1为只显示limit_products中的产品
            condition.put("PRODUCTTREE_LIMIT_TYPE", productTreeLimitType);
            condition.put("PRODUCTTREE_LIMIT_PRODUCTS", productTreeLimitProducts);
        }

        // 是否只查询铁通集团
        String ttGrpTag = inParam.getString("IS_TTGRP");
        if ("true".equals(ttGrpTag))
        {
            condition.put("IS_TTGRP", ttGrpTag);
        }

        setCondition(condition);
     // ESOP逻辑
        String ibsysid = inParam.getString("IBSYSID", "");
        String bpmId = inParam.getString("BPM_TEMPLET_ID", "");
		if (StringUtils.isNotEmpty(ibsysid)) {
			//专线变态处理，将新开通的专线添加在已存在用户下处理
			if ("DIRECTLINEOPEN".equals(bpmId)) {
			    initialEsop4Dataline();
			} else {
			    initialEsop();
			}
		}

    }

    /**
     * esop调入
     * 
     * @param cycle
     * @return
     * @throws Throwable
     */
    public void initialEsop() throws Throwable
    {
        IData inParam = getData();

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

        // 再原有的condition信息中增加返回信息
        IData conditionSuperData = getCondition();
        conditionSuperData.putAll(condition);
        setCondition(conditionSuperData);

        String busiType = conditionSuperData.getString("BUSI_TYPE", BizCtrlType.ChangeUserDis);
        setProductCtrlInfo(AttrBizInfoIntfViewUtil.qryNormalProductCtrlInfoByGrpProductIdAndBusiType(this, productId, busiType));

        IDataset userinfos = new DatasetList();
        userinfo.put("CHECKED", "true");
        userinfos.add(userinfo);
        setUseInfos(userinfos);

    }
    
    
    public void initialEsop4Dataline() throws Throwable
    {
        IData inParam = getData();
        IDataset users = new DatasetList();
        IData groupinfo = new DataMap();

        String ibsysid = inParam.getString("IBSYSID", "");
        if (StringUtils.isEmpty(ibsysid))
            return;
        IData condition = getEsopInitParam(inParam);
        String groupId = condition.getString("cond_GROUP_ID");
        productId = condition.getString("ESOP_PRODUCT_ID");
        if(StringUtils.isNotBlank(productId)){
			if ("7011".equals(productId) || "7012".equals(productId)|| "7010".equals(productId)) {
				IData custInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
				if(null != custInfo && custInfo.size() > 0){
					String custId = custInfo.getString("CUST_ID");
					groupinfo = UCAInfoIntf.qryGrpCustInfoByCustId(this, custId);
					users = UCAInfoIntfViewUtil.qryGrpUserInfoByCustIdAndProId(this, custId, productId);
				}
			}    
        }
        
        setGroupInfo(groupinfo);
        
        // 获取产品信息
        IData productInfo = GroupProductUtilView.getProductExplainInfo(this, productId);
        setProductInfo(productInfo);
        String productName = productInfo.getString("PRODUCT_NAME");
        condition.put("ESOP_PRODUCT_NAME", productName);

        // 再原有的condition信息中增加返回信息
        IData conditionSuperData = getCondition();
        
        conditionSuperData.putAll(condition);
        setCondition(conditionSuperData);

        String busiType = conditionSuperData.getString("BUSI_TYPE", BizCtrlType.ChangeUserDis);
        setProductCtrlInfo(AttrBizInfoIntfViewUtil.qryNormalProductCtrlInfoByGrpProductIdAndBusiType(this, productId, busiType));

        if (users.size() > 0) {
            users.getData(0).put("CHECKED", "true");
            setUserInfo(users.getData(0));
        }
        setUseInfos(users);

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
