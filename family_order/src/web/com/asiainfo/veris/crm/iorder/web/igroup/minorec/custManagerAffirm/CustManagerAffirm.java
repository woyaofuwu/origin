
package com.asiainfo.veris.crm.iorder.web.igroup.minorec.custManagerAffirm;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.ScrDataTrans;
import com.asiainfo.veris.crm.iorder.web.igroup.util.WorkfromViewCall;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;

public abstract class CustManagerAffirm extends EopBasePage
{
    private static final Integer COND_FIRST_NUM = 1;
    
    private static final Integer COND_LAST_NUM = 10;
    
    private static final Integer MEB_COND_LAST_NUM = 10;

    public void initPage(IRequestCycle cycle) throws Exception
    {
        super.initPage(cycle);
        IData param = getData();
        String ibsysid = param.getString("IBSYSID");
        String nodeId = param.getString("NODE_ID");
        String busiformNodeId = param.getString("BUSIFORM_NODE_ID");
        String busiformId = param.getString("BUSIFORM_ID");
        String productId = param.getString("PRODUCT_ID");
        IData info = new DataMap();
        if ("VP66666".equals(productId))
        {
            IData productData = qryProductInfo(ibsysid, busiformId);
            productId = productData.getString("PRODUCT_ID");
            String exceptionStr = productData.getString("EXCEPTION_INFO");
            if (!"".equals(exceptionStr))
            {
                info.put("EXCEPTION", exceptionStr);
                setInfo(info);
                return;
            }
        }
        if (StringUtils.isNotBlank(ibsysid) && StringUtils.isNotBlank(productId))
        {
            // 查询产品属性
            getProductInfos(productId, ibsysid);
        }
        try
        {
            // 查询流程订单信息 存储key-EOS_COMMON_DATA
            IData subscribeData = WorkfromViewCall.qryWorkformSubscribeByIbsysid(this, ibsysid);
            subscribeData.put("NODE_ID", nodeId);
            subscribeData.put("BUSIFORM_NODE_ID", busiformNodeId);
            info.putAll(subscribeData);
            subscribeData.putAll(param);
            info.put("EOS_COMMON_DATA", ScrDataTrans.buildEosCommonData(subscribeData));
            info.put("NODE_ID", nodeId);
            String bpmTempletId = param.getString("BPM_TEMPLET_ID");
            info.put("BPM_TEMPLET_ID", bpmTempletId);
            info.put("IBSYSID", ibsysid);
            String productName = qryProductName(productId);
            info.put("PRODUCT_NAME", productName);
            info.put("PRODUCT_ID", productId);
            String staffId = getVisit().getStaffId();
            info.put("STAFF_ID", staffId);
            //获取客户信息
            IData custInfo = CSViewCall.callone(this, "SS.QuickOrderDataSVC.qryCustIdByIbsysid", info);
            String custId = custInfo.getString("CUST_ID");
            info.put("CUST_ID", custId);
            //默认根据 cust_id和product_id查询esp是否生成订单
            IDataset productTrades = qryTradeInfosByProductId(productId, custId);
            setProductTrades(productTrades);
            // 获取offer信息，查询CONTRACT_ID合同ID
            IData offerData = new DataMap();
            offerData.put("PRODUCT_ID", productId);
            offerData.put("IBSYSID", ibsysid);
            IDataset offerDataset = CSViewCall.call(this, "SS.QuickOrderDataSVC.getNewQuickorderData", offerData);
            StringBuilder offerInitStr = new StringBuilder();
            buildOfferStr(offerDataset.first(), offerInitStr);
            IData offerInitData = new DataMap(offerInitStr.toString());
            String contractId = offerInitData.getData("EC_COMMON_INFO").getData("CONTRACT_INFO").getString("CONTRACT_ID");
            info.put("CONTRACT_ID", contractId);
        }
        catch (Exception e)
        {
            info.put("EXCEPTION", "根据IBSYSID=" + ibsysid + "未查询到流程订单主表数据！");
        }
        setInfo(info);
        getStaffInfo();
    }

    /**
     * @Title: qryTradeInfosByProductId
     * @Description: 初始化时根据客户编码和产品编码查询是否已存在工单信息
     * @param productId
     * @return
     * @throws Exception
     *             IDataset
     * @author zhangzg
     * @date 2019年10月18日下午3:46:34
     */
    private IDataset qryTradeInfosByProductId(String productId, String custId) throws Exception
    {
        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        param.put("CUST_ID", custId);
        IDataset productInfos = CSViewCall.call(this, "SS.QuickOrderTradeSVC.qryTradeInfosByProductId", param);
        String productName = qryProductName(productId);
        if (IDataUtil.isNotEmpty(productInfos))
        {
            for (Object obj : productInfos)
            {
                IData data = (IData) obj;
                data.put("PRODUCT_NAME", productName);
            }
            return productInfos;
        }
        return new DatasetList();
    }

    /**
     * @Title: qryTradeInfoByUserId
     * @Description: 刷新页面订单信息
     * @param userId
     * @return
     * @throws Exception
     * @author zhangzg
     * @date 2019年10月18日下午3:46:55
     */
    public void qryTradeInfoByUserId(IRequestCycle cycle) throws Exception
    {
        IData params = getData();
        String productId = params.getString("PRODUCT_ID");
        String productName = qryProductName(productId);
        IDataset tradeInfos = CSViewCall.call(this, "SS.QuickOrderTradeSVC.qryTradeInfosByProductId", params);
        if (IDataUtil.isNotEmpty(tradeInfos))
        {
            for (Object obj : tradeInfos)
            {
                IData data = (IData) obj;
                data.put("PRODUCT_NAME", productName);
            }
        }
        setProductTrades(tradeInfos);
    }

    /**
     * @Title: qryMemberStateList
     * @Description: 查询成员工单信息
     * @param userIdA
     * @return
     * @throws Exception
     *             IDataset
     * @author zhangzg
     * @date 2019年10月19日下午3:09:43
     */
    public void qryMemberStateList(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset tradeInfos = CSViewCall.call(this, "SS.QuickOrderTradeSVC.qryMemberInfosTradeByUserId", data);
        IDataset dataInfos = CSViewCall.call(this, "SS.QuickOrderTradeSVC.qryMemberInfosDataByUserId", data);
        if (IDataUtil.isNotEmpty(tradeInfos))
        {
            for (Object obj : tradeInfos)
            {
                String memState = "未完工";
                IData tradeInfo = (IData) obj;
                String userIdB = tradeInfo.getString("USER_ID_B");
                if (IDataUtil.isNotEmpty(dataInfos))
                {
                    for (Object dataObj : dataInfos)
                    {
                        IData dataInfo = (IData) dataObj;
                        String userIdB2 = dataInfo.getString("USER_ID_B");
                        if (userIdB.equals(userIdB2))
                        {
                            memState = "已完工";
                            break;
                        }
                    }

                }
                tradeInfo.put("MEMBER_STATE", memState);
            }
        }
        setMemberInfos(tradeInfos);
    }

    /**
     * 
    * @Title: qryProductInfo 
    * @Description: 一单清ESP产品处理
    * @param ibsysId
    * @param busiformId
    * @return
    * @throws Exception IData
    * @author zhangzg
    * @date 2019年10月31日下午5:44:29
     */
    private IData qryProductInfo(String ibsysId, String busiformId) throws Exception
    {
        String productId = "";
        String exceptionStr = "";
        IDataset list = null;
        IData paramData = new DataMap();
        IData result = new DataMap();
        paramData.put("IBSYSID", ibsysId);
        // 获取父子流程关系，根据子流程的BUSIFORM_ID找到子流程的RECORD_NUM
        paramData.put("SUB_BUSIFORM_ID", busiformId);
        list = CSViewCall.call(this, "SS.WorkformReleBeanSVC.qryBySubBusiformId", paramData);
        if (DataUtils.isEmpty(list))
        {
            exceptionStr = "根据SUB_BUSIFORM_ID" + busiformId + "没有查询到父子流程关系信息！";
            result.put("EXCEPTION_INFO", exceptionStr);
            return result;
        }
        // 获取tf_b_eop_product 中的productId
        String recordNum = list.first().getString("RELE_VALUE");
        paramData.put("RECORD_NUM", recordNum);
        list = CSViewCall.call(this, "SS.WorkformProductSVC.qryEopProductByIbsysId", paramData);
        if (DataUtils.isEmpty(list))
        {
            exceptionStr = "根据IBSYSID" + ibsysId + "没有查询到定单产品信息！";
            result.put("EXCEPTION_INFO", exceptionStr);
            return result;
        }
        productId = list.first().getString("PRODUCT_ID");
        result.put("PRODUCT_ID", productId);
        result.put("EXCEPTION_INFO", exceptionStr);
        return result;
    }

    /**
     * 查询客户经理信息
     * 
     * @throws Exception
     */
    private void getStaffInfo() throws Exception
    {
        String staff = getVisit().getStaffId();
        IData param = new DataMap();
        param.put("STAFF_ID", staff);
        IData staffInfo = CSViewCall.callone(this, "SS.StaffDeptInfoQrySVC.getStaffInfo", param);
        setAuditInfo(staffInfo);
    }


    /**
     * 
    * @Title: getProductInfos 
    * @Description: 查询产品属性 TF_B_EOP_QUICKORDER_MEB
    * @param offerCode
    * @param ibsysid
    * @throws Exception void
    * @author zhangzg
    * @date 2019年10月31日下午5:43:05
     */
    private void getProductInfos(String offerCode, String ibsysid) throws Exception
    {
        // 查询子流程成员信息
        IData params = new DataMap();
        params.put("IBSYSID", ibsysid);
        params.put("PRODUCT_ID", offerCode);
        // 根据产品编码查询产品名称
        String productName = qryProductName(offerCode);
        IDataset productAttrs = CSViewCall.call(this, "SS.QuickOrderMemberSVC.qryMemInfoForCustManager", params);
        if (productAttrs != null && productAttrs.size() > 0)
        {
            for (Object obj : productAttrs)
            {
                IData data = (IData) obj;
                data.put("PRODUCT_NAME", productName);
                for (int i = COND_FIRST_NUM; i < MEB_COND_LAST_NUM; i++)
                {
                    String codingstr = data.getString("CODING_STR" + i);
                    if (codingstr == null || "".equals(codingstr))
                    {
                        break;
                    }
                    IData codingData = new DataMap(codingstr);
                    data.putAll(codingData);
                }
            }
        }
        setPattrs(productAttrs);
    }

    /**
     * 
    * @Title: submit 
    * @Description: 客户经理确认提交
    * @param cycle
    * @throws Exception void
    * @author zhangzg
    * @date 2019年10月31日下午5:43:05
     */
    public void submit(IRequestCycle cycle) throws Exception
    {
        IData submitData = new DataMap(getData().getString("SUBMIT_PARAM"));
        IData data = getData();
        IData table = new DatasetList(getData().getString("ROWDATAS")).first();
        String userId = table.getString("USER_ID");
        String tradeId = table.getString("TRADE_ID");
        String serialNumber = table.getString("SERIAL_NUMBER");
        String productId = table.getString("PRODUCT_ID");
        String contractId = data.getString("CONTRACT_ID");
        String bpmTempletId = data.getString("BPM_TEMPLET_ID");
        String operType = data.getString("OPER_TYPE");
        IData flowData = submitData.getData("COMMON_DATA");
        String busiformId = flowData.getString("BUSIFORM_ID");
        String recordNum = "1";
        IData updateParams = new DataMap();
        updateParams.put("USER_ID", userId);
        updateParams.put("TRADE_ID", tradeId);
        updateParams.put("SERIAL_NUMBER", serialNumber);
        updateParams.put("PRODUCT_ID", productId);
        updateParams.put("CONTRACT_ID", contractId);
        updateParams.put("IBSYSID", flowData.getString("IBSYSID"));
        updateParams.put("BUSIFORM_ID", busiformId);
        updateParams.put("BPM_TEMPLET_ID", bpmTempletId);
        // 若当前流程为子流程 则查询父流程
        IData tmpInfos = CSViewCall.callone(this, "SS.QryAuditInfoSVC.qrySubRelaInfoByTemplet", updateParams);
        if (IDataUtil.isNotEmpty(tmpInfos))
        {
            IData recordNumInfos = CSViewCall.callone(this, "SS.QryAuditInfoSVC.qryEopProductByIbsysidAndBusiformId", updateParams);
            recordNum = recordNumInfos.getString("RECORD_NUM");
        }
        updateParams.put("RECORD_NUM", recordNum);
        // ESP产品开通更新成员数据
        if ("SUMBUSINESSTVOPEN".equals(bpmTempletId) || "CLOUDWIFIOPEN".equals(bpmTempletId))
        {
            updateParams.put("RSRV_STR3", "0");
            // 更新TF_B_EOP_QUICKORDER_MEB 表数据 EC_SERIAL_NUMBER
            CSViewCall.call(this, "SS.QuickOrderMemberSVC.updateMemSerialNumInfo", updateParams);
        }
        // ESP产品变更更新成员数据
        if ("SUMBUSINESSTVCHANGE".equals(bpmTempletId) || "CLOUDTAVERNCHANGE".equals(bpmTempletId))
        {
            // 查询产品变更最新节点的成员信息
            IDataset mebList = CSViewCall.call(this, "SS.QuickOrderMemberSVC.qryMemInfoForCustManager", updateParams);
            if (IDataUtil.isNotEmpty(mebList))
            {
                StringBuilder sb = new StringBuilder();
                for (Object obj : mebList)
                {
                    IData mebData = (IData) obj;
                    sb.append("'" + mebData.getString("SERIAL_NUMBER") + "',");
                }
                String serialNumStr = sb.toString().substring(0, sb.length() - 1);
                String updateTime = mebList.getData(0).getString("UPDATE_TIME");
                if (StringUtils.isNotBlank(operType) && "DelMeb".equals(operType))
                {
                    // 产品变更删除成员
                    updateParams.put("SERIAL_NUMBER_STR", serialNumStr);
                    updateParams.put("RSRV_STR3", "1");
                    // 处理历史成员数据 将删除成员号码置位无效
                    CSViewCall.call(this, "SS.QuickOrderMemberSVC.updateMemStateInfoForDel", updateParams);
                }
                else
                {
                    // 产品变更新增成员
                    // 将新增成员置为有效
                    updateParams.put("RSRV_STR3", "0");
                    CSViewCall.call(this, "SS.QuickOrderMemberSVC.updateMemSerialNumInfo", updateParams);
                    // 处理历史成员数据
                    updateParams.put("UPDATE_TIME", updateTime);
                    updateParams.put("RSRV_STR3", "1");
                    // 将历史数据中与新增成员号码相同的数据置为无效
                    CSViewCall.call(this, "SS.QuickOrderMemberSVC.updateMemStateInfoForAdd", updateParams);
                }
            }
        }
        // 更新TF_B_EOP_PRODUCT 、TF_F_USER 表数据 TRADE_ID 、USER_ID 、SERIAL_NUMBER 
        CSViewCall.call(this, "SS.QuickOrderDataSVC.updateEopProductInfo", updateParams);
        // 更新TF_F_USER 表数据 CONTRACT_ID
//        CSViewCall.call(this, "SS.QuickOrderDataSVC.updateUserInfo", updateParams);
        IData param = new DataMap();
        param.put("IBSYSID", flowData.getString("IBSYSID"));
        param.put("BUSIFORM_ID", flowData.getString("BUSIFORM_ID"));
        param.put("NODE_ID", flowData.getString("NODE_ID"));
        // 推动流程驱动
        IDataset result = CSViewCall.call(this, "SS.WorkformDriveSVC.execute", param);
        setAjax(result.first());
    }


    /**
     * @Title: qryProductName
     * @Description: 根据产品编码查询产品名称
     * @param productId
     * @return String
     * @author zhangzg
     * @throws Exception
     * @date 2019年10月19日下午6:13:59
     */
    private String qryProductName(String productId) throws Exception
    {
        IDataset productInfos = UpcViewCall.queryOfferByOfferCodeAndType(this, productId, "P");
        return productInfos.getData(0).getString("OFFER_NAME");
    }

    /**
     * 
    * @Title: buildOfferStr 
    * @Description: 查询TF_B_EOP_QUICKORDER_DATA 处理字符串
    * @param quickorderCond
    * @param offerInitStr void
    * @author zhangzg
    * @date 2019年10月31日下午5:41:59
     */
    private void buildOfferStr(IData quickorderCond, StringBuilder offerInitStr)
    {
        for (int i = COND_FIRST_NUM; i <= COND_LAST_NUM; i++)
        {
            if (StringUtils.isNotBlank(quickorderCond.getString("CODING_STR" + i)))
            {
                offerInitStr.append(quickorderCond.getString("CODING_STR" + i));
            }
        }
    }
    
    /**
     * 查询流程订单信息 TF_B_EOP_SUBSCRIBE
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryInfosByIbsysid(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String ibsysid = param.getString("IBSYSID");
        IData workformData = WorkfromViewCall.qryWorkformSubscribeByIbsysid(this, ibsysid);
        String groupId = "";
        if (IDataUtil.isNotEmpty(workformData))
        {
            groupId = workformData.getString("GROUP_ID");
        }
        else
        {
            this.setAjax("error_message", "根据工单号未查到对应工单, 请核实");
            return;
        }
        // 根据 集团编码查询集团信息
        if (StringUtils.isNotBlank(groupId))
        {
            IData group = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
            group.put("IBSYSID", ibsysid);
            setGroupInfo(group);
        }
    }
    /**
     * 查询流程业务信息 TF_B_EOP_ATTR
     * 
     * @param cycle
     * @throws Exception
     */
    public void qryByIbsysidProductNo(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String ibsysid = param.getString("IBSYSID");
        String productNo = param.getString("PRODUCTNO");
        String nodeId = param.getString("NODE_ID");
        String productId = param.getString("PRODUCT_ID");
        IData data = new DataMap();
        data.put("IBSYSID", ibsysid);
        data.put("PRODUCTNO", productNo);
        data.put("NODE_ID", nodeId);
        IData productInfo = CSViewCall.callone(this, "SS.WorkformAttrSVC.qryByIbsysidProductNoNodeId", data);
        String productName = qryProductName(productId);
        productInfo.put("PRODUCT_ID", productId);
        productInfo.put("PRODUCT_NAME", productName);
        setProductInfo(productInfo);
    }
    /**
     * 查询产品子表 TF_B_EOP_PRODUCT_EXT
     * 
     * @param ibsysid
     * @return
     * @throws Exception
     */
    public IDataset getMemberList(String ibsysid) throws Exception
    {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        IDataset productExts = CSViewCall.call(this, "SS.WorkformProductExtSVC.qryProductByIbsysid", param);
        IDataset infos = new DatasetList();
        for (int i = 0; i < productExts.size(); i++)
        {
            IData productSub = productExts.getData(i);
            String reCordeNum = productSub.getString("RECORD_NUM");
            param.put("RECORD_NUM", reCordeNum);
            param.put("NODE_ID", "apply");
            // 根据工单号 ibsysid record_num node_id 查询TF_B_EOP_ATTR
            IDataset atts = CSViewCall.call(this, "SS.SubscribeViewInfoSVC.qryEweAttributesByNodeIdIbsysid", param);
            IData attr = new DataMap();
            for (int j = 0; j < atts.size(); j++)
            {
                String key = atts.getData(j).getString("ATTR_CODE");
                String value = atts.getData(j).getString("ATTR_VALUE");
                attr.put(key, value);
            }
            infos.add(attr);
        }
        return infos;
    }

    public abstract void setInAttr(IData inAttr);

    public abstract void setPattr(IData pattr);

    public abstract void setInfo(IData info);

    public abstract void setAuditInfo(IData info);

    public abstract void setPattrs(IDataset pattrs);

    public abstract void setProductTrades(IDataset productTrades);

    public abstract void setProductInfo(IData productInfo);

    public abstract void setMemberInfos(IDataset memberInfos);

}
