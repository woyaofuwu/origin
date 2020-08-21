
package com.asiainfo.veris.crm.order.web.frame.csview.group.bbossmanageinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.bbossattrgroup.GroupBBossUtilCommon;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.seqinfo.SeqMgrInfoIntfViewUtil;

public abstract class BbossManageCreate extends GroupBBossUtilCommon
{

    protected String productOperType;

    /**
     * 取消受理的响应事件,将主台帐数据，绑到tf_bh_trade 表
     */
    public void cancalData(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        String trade_id = data.getString("TRADE_ID");
        IData parm = new DataMap();
        parm.put("TRADE_ID", trade_id);
        // j2ee-ly callviewtobean("BbossManage.cancelTrade", parm);

        IData returnData = new DataMap();
        returnData.put("RESULT", "1");
        setAjax(returnData);

    }

    /*
     * @description 设置服务于资费参数
     * @author xunyl
     * @date 2013-07-02
     */
    protected IData getSvcDcnCond(String userId) throws Exception
    {
        // 1- 定义服务与资费参数对象
        IData svcDcnCond = new DataMap();

        // 2- 添加产品编号
        svcDcnCond.put("PRODUCT_ID", productId);

        // 3- 添加用户编号
        svcDcnCond.put("USER_ID", userId);

        // 4- 添加路由编号
        svcDcnCond.put("EPARCHY_CODE", getTradeEparchyCode());
        svcDcnCond.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());

        return svcDcnCond;
    }

    public void init(IRequestCycle cycle) throws Exception
    {
        // 1- 页面参数中获产品编号，用户编号
        IData data = getData();
        productId = data.getString("PRODUCT_ID2");
        String trade_id = data.getString("TRADE_ID");
        String user_id = data.getString("BBOSS_USER_ID");// BBOSS子产品用户ID
        String myFlag = data.getString("MYFLAG", "");
        String orderId = data.getString("ORDER_ID", "");
        setTradeId(trade_id);
        setBbossUserId(user_id);
        setMyFlag(myFlag);

        IData paramData = new DataMap();
        paramData.put("ORDER_ID", orderId);
        IDataset merchInfos = CSViewCall.call(this, "CS.TradeGrpMerchInfoQrySVC.qryMerchInfoByMainTradeOrderId", paramData);
        if (IDataUtil.isNotEmpty(merchInfos))
        {
            String merch_tradeId = merchInfos.getData(0).getString("TRADE_ID");
            setMerchTradeId(merch_tradeId);
        }

        // 2- 设置产品操作类型 受理
        productOperType = GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_ADD.getValue();
        // 3- 设置产品属性
        queryPOProductPlus(cycle);

        // 4- 设置服务与优惠组件参数
        IData svcDcnCond = this.getSvcDcnCond(user_id);
        setCond(svcDcnCond);

        queryPoattrachment(cycle);

        IData inparams = new DataMap();

        inparams.put("TRADE_ID", trade_id);
        inparams.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);
        IDataset tradeSet = CSViewCall.call(this, "CS.TradeInfoQrySVC.queryTradeSet", inparams);

        // 5- 读取是否需要合同，审批，联系人信息
        setProductCtrlInfo2(AttrBizInfoIntfViewUtil.qryCrtUsProductCtrlInfoByProductId(this, tradeSet.getData(0).getString("PRODUCT_ID"), false));

        // 6- 存商产品ID
        IData param = new DataMap();
        param.put("PRODUCT_ID", data.getString("PRODUCT_ID2"));
        param.put("MPRODUCT_ID", data.getString("PRODUCT_ID"));
        setParam(param);

        setCondition(data);

    }

    /**
     * 取商品级附件信息
     */
    public void queryPoattrachment(IRequestCycle cycle) throws Exception
    {

        IData inparm = new DataMap();
        inparm.put("TRADE_ID", getData().getString("TRADE_ID"));

        IDataset prodInfo = CSViewCall.call(this, "CS.TradeGrpMerchInfoQrySVC.qryAllMerchInfoByTradeId", inparm);

        IData cond = new DataMap();
        if (prodInfo != null && prodInfo.size() > 0)
        {
            cond.put("BUS_NEED_DEGREE", prodInfo.getData(0).getString("RSRV_STR5", ""));
        }
        this.setInfo(cond);

    }

    /**
     * @param
     * @desciption 产品的参数信息
     * @author fanti
     * @version 创建时间：2014年8月29日 下午11:09:17
     */
    public void queryPOProductPlus(IRequestCycle cycle) throws Exception
    {
        IDataset poProductPlusInfoList = new DatasetList();
        IData productParam = new DataMap();

        productParam.put("PRODUCT_ID", getData().getString("PRODUCT_ID2"));// 产品ID
        productParam.put("PRODUCT_OPER_TYPE", productOperType); // 产品操作类型
        productParam.put("BBOSS_STAGE", "4");// 阶段编码（1-缓存，2-集团受理，3-集团变更，4-预受理转正式受理，5-管理节点）
        productParam.put("TRADE_ID", getData().getString("TRADE_ID")); // 产品TRADE_ID

        poProductPlusInfoList = getProductPlusInfo(productParam, new DataMap());

        DataHelper.sort(poProductPlusInfoList, "READONLY", 2);

        // 设置订购信息的属性
        setPOProductPlus(poProductPlusInfoList);
    }

    /**
     * @author weixb3
     * @description 查询产品属性信息
     * @date 2013-09-27
     */
    public IDataset queryProdAttr(String productSpecNumber) throws Exception
    {
        IData params = new DataMap();
        params.put("PRODUCTSPECNUMBER", productSpecNumber);
        params.put("BIZ_TYPE", "1");// 业务类型：1-集团业务，2-成员业务
        IDataset ds = CSViewCall.call(this, "CS.BBossAttrQrySVC.qryBBossAttrByPospecBiztype", params);
        return ds;
    }

    /**
     * 响应确定提交参数，将需要更改的数据，更改
     * 
     * @author weixb3
     */
    public void serverBbossFlow(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.USER_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        CSViewCall.call(this, "CS.CreateBBossUserSVC.saveChgData", data);
    }

    public abstract void setBbossUserId(String bbossUserId);

    public abstract void setBossManageInfo(IDataset bossManageInfo);

    public abstract void setCondition(IData condition);

    public abstract void setFlowInfo(IDataset flowInfo); // 流程信息

    public abstract void setFlowPoint(String flowPoint);

    public abstract void setForcetag(String na);

    @Override
    public abstract void setInfo(IData info);

    public abstract void setInfos(IData infos);

    public abstract void setManageInfo(IDataset manageInfo);// 管理信息

    public abstract void setMerchTradeId(String merchtradeid);

    public abstract void setMyFlag(String myFlag);

    public abstract void setParam(IData param);

    public abstract void setPoInfos(IDataset poInfos);

    public abstract void setPoList(IDataset poList);

    // 产品的参数信息

    public abstract void setPOProductPlus(IDataset pOProductPlus);

    public abstract void setProductCtrlInfo2(IData productCtrlInfo);

    public abstract void setProductDisInfos(IDataset productDisInfos);

    public abstract void setProductId2(String productId2);

    public abstract void setProductInfos(IDataset productInfos);

    public abstract void setPurchaseInfos(IDataset purchaseInfos);

    public abstract void setTradeId(String tradeId);

    /**
     * chenyi 验证互联网专线所在省客户编码 13-12-2
     * 
     * @throws Exception
     */
    public void verifyInterNetGroupInfo(IRequestCycle cycle) throws Exception
    {
        String tradeId = getData().getString("MERCH_TRADE_ID");
        String group_id = getData().getString("GROUP_ID");
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", tradeId);
        IDataset ds = CSViewCall.call(this, "CS.TradeGrpMerchInfoQrySVC.qryAllMerchInfoByTradeId", inparams);
        if (IDataUtil.isNotEmpty(ds))
        {
            String bizMode = ds.getData(0).getString("BIZ_MODE");
            if ("4".equals(bizMode))
            {
                inparams.clear();
                inparams.put("GROUP_ID", group_id);
                IDataset groupInfo = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryGrpInfoByGrpId", inparams);

                // 可能直接写的集团对应的EC编码
                if (IDataUtil.isEmpty(groupInfo))
                {
                    inparams.clear();
                    inparams.put("MP_GROUP_CUST_CODE", group_id);
                    groupInfo = CSViewCall.call(this, "CS.GrpInfoQrySVC.getGroupByMpGroup", inparams);
                }

                if (IDataUtil.isEmpty(groupInfo) || (StringUtils.isBlank(groupInfo.getData(0).getString("MP_GROUP_CUST_CODE"))))
                {
                    CSViewException.apperr(CrmCommException.CRM_COMM_548);
                }
                else
                {
                    inparams.clear();
                    inparams.put("MP_GROUP_CUST_CODE", groupInfo.getData(0).getString("MP_GROUP_CUST_CODE"));
                    setAjax(inparams);
                }
            }
        }

    }
    /**
     * @description 获取一点支付业务成员附件等参数导入的批次号
     * @author xunyl
     * @date 2015-10-28
     */
    public void getBatchId(IRequestCycle cycle)throws Exception{
        //1- 序列生成批次号
        String batTaskId = SeqMgrInfoIntfViewUtil.qryBatchId(this,getTradeEparchyCode());
        
        //2- 返回序列号
        IData idata = new DataMap();
        idata.put("result", batTaskId);
        setAjax(idata);
    }
    
    /**
     * @description 获取一点支付业务成员附件的参数生成的文件名称
     * @param xunyl
     * @date 2015-10-28
     */
    public void getFileName(IRequestCycle cycle)throws Exception{
        //1- 获取批次号
        String batchTaskId= getData().getString("BATCH_TASK_ID");
        
        //2- 根据批次号获取文件名称
        String key = CacheKey.getBossBatchInfoKey(batchTaskId);
        String fileName = SharedCache.get(key).toString();
        
        //3- 返回文件名
        IData idata = new DataMap();
        idata.put("result", fileName);
        setAjax(idata);
    }

}
