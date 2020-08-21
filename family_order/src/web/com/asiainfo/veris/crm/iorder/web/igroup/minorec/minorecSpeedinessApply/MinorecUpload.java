package com.asiainfo.veris.crm.iorder.web.igroup.minorec.minorecSpeedinessApply;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.ScrDataTrans;
import com.asiainfo.veris.crm.iorder.web.igroup.util.WorkfromViewCall;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import org.apache.tapestry.IRequestCycle;

public abstract class MinorecUpload extends EopBasePage {

    public abstract void setInfo(IData info) throws Exception;

    public abstract void setPattrInfo(IData pattrInfo) throws Exception;

    public abstract IData getGroupInfo() throws Exception;

    /**
     * 初始化方法
     *
     * @param cycle
     * @throws Exception
     */

    public void initPage(IRequestCycle cycle) throws Exception {

        super.initPage(cycle);
        IData param = getData();

        IData info = new DataMap();
        // 获取EOP_SUBSCRIBE表数据
        IData subscribeData = WorkfromViewCall.qryWorkformSubscribeByIbsysid(this, param.getString("IBSYSID"));
        if (IDataUtil.isEmpty(subscribeData)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "该工单IBSYSID=【" + param.getString("IBSYSID") + "】不存在！");
        }
        subscribeData.put("NODE_ID", param.getString("NODE_ID"));
        subscribeData.put("BUSIFORM_NODE_ID", param.getString("BUSIFORM_NODE_ID"));
        info.put("EOS_COMMON_DATA", ScrDataTrans.buildEosCommonData(subscribeData));
        info.put("IBSYSID",param.getString("IBSYSID"));
        info.put("BUSIFORM_ID",param.getString("BUSIFORM_ID"));
        info.put("BUSIFORM_NODE_ID",param.getString("BUSIFORM_NODE_ID"));
        info.put("BPM_TEMPLET_ID",subscribeData.getString("BPM_TEMPLET_ID"));
        if("MINORECSPEEDINESSCHANGE".equals(subscribeData.getString("BPM_TEMPLET_ID")) || "COMPLEXPROCESSCHANGE".equals(subscribeData.getString("BPM_TEMPLET_ID"))){//变更主流程
            String productId = param.getString("PRODUCT_ID");

            String bpmTempletId = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "PDATA_ID", new String[] { "MINOREC_XN_PRODUCT", productId });
            info.put("cond_TEMPLET_ID",bpmTempletId);
            param.put("cond_TEMPLET_ID",bpmTempletId);
        }else{
            info.put("cond_TEMPLET_ID",subscribeData.getString("BPM_TEMPLET_ID"));
        }
        info.put("NODE_ID",param.getString("NODE_ID"));
        info.put("URGENCY_LEVEL",subscribeData.getString("FLOW_LEVEL"));
        info.put("TITLE",subscribeData.getString("FLOW_DESC"));

        //操作类型
        info.put("OPER_TYPE",subscribeData.getString("RSRV_STR7"));

        IDataset temletList = new DatasetList();
        IDataset openTempletList = StaticUtil.getStaticList("MINOREC_BPM_TEPMENTID_CHANGE");
        if(DataUtils.isNotEmpty(openTempletList)){
            temletList.addAll(openTempletList);
        }
        IDataset changeTempletList = StaticUtil.getStaticList("MINOREC_BPM_TEPMENTID");
        if(DataUtils.isNotEmpty(changeTempletList)){
            temletList.addAll(changeTempletList);
        }

        info.put("TEMPLET_LIST",temletList);

        queryContractInfo(param);

        querySynchrostateESP(info);

        setInfo(info);

    }

    public void queryContractInfo(IData param) throws Exception{

        IData group = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, param.getString("GROUP_ID"));
        String custId = group.getString("CUST_ID");
        param.put("CUST_ID", custId);
        param.put("EPARCHY_CODE", group.getString("EPARCHY_CODE"));
        IDataset offerList = CSViewCall.call(this, "SS.QuickOrderMemberSVC.queryAuditQuickMemberData", param);
        if (IDataUtil.isNotEmpty(offerList)) {
            String bpmTempletId = param.getString("BPM_TEMPLET_ID");
            if("MINORECSPEEDINESSCHANGE".equals(bpmTempletId) || "COMPLEXPROCESSCHANGE".equals(bpmTempletId)) {//变更主流程
                bpmTempletId = param.getString("cond_TEMPLET_ID");
            }
            if ("YIDANQINGSHANGPU".equals(bpmTempletId)||"YIDANQINGSHANGPUCHANGE".equals(bpmTempletId)) {// 判断是否是商铺类产品，是的话是否包含集团V网的电子协议
                IData productInfo = new DataMap();
                for (Object object : offerList) {
                    IData offerData = (IData) object;
                    IData ecCommonInfo = offerData.getData("EC_COMMON_INFO");
                    IData contractInfo = ecCommonInfo.getData("CONTRACT_INFO");
                    if ("8000".equals(contractInfo.getString("OFFER_IDS"))) {// 回显集团V网的电子协议信息
                        //KEY不同不会覆盖值
                        productInfo.put("CONTRACT_NAME_VW", contractInfo.getString("CONTRACT_NAME"));
                        productInfo.put("CONTRACT_ID_VW", contractInfo.getString("CONTRACT_ID"));
                        productInfo.put("CONTRACT_END_DATE_VW", contractInfo.getString("CONTRACT_END_DATE"));
                        productInfo.put("CONTRACT_WRITE_DATE_VW", contractInfo.getString("CONTRACT_WRITE_DATE"));
                        productInfo.put("OFFER_IDS_VW", contractInfo.getString("OFFER_IDS"));
                        productInfo.put("GROUP_VW", "true");
                    } else {// 回显其他产品的电子协议信息
                        productInfo.put("CONTRACT_NAME", contractInfo.getString("CONTRACT_NAME"));
                        productInfo.put("CONTRACT_ID", contractInfo.getString("CONTRACT_ID"));
                        productInfo.put("CONTRACT_END_DATE", contractInfo.getString("CONTRACT_END_DATE"));
                        productInfo.put("CONTRACT_WRITE_DATE", contractInfo.getString("CONTRACT_WRITE_DATE"));
                        productInfo.put("OFFER_IDS", contractInfo.getString("OFFER_IDS"));
                        productInfo.put("GROUP_HOTEL","true");
                    }
                }
                setPattrInfo(productInfo);

            } else {// 其他流程只需获取其中一个电子协议信息即可
                IData productInfo = new DataMap();
                IData ecCommonInfo = offerList.first().getData("EC_COMMON_INFO");
                IData contractInfo = ecCommonInfo.getData("CONTRACT_INFO");
                productInfo.put("CONTRACT_NAME", contractInfo.getString("CONTRACT_NAME"));
                productInfo.put("CONTRACT_ID", contractInfo.getString("CONTRACT_ID"));
                productInfo.put("CONTRACT_END_DATE", contractInfo.getString("CONTRACT_END_DATE"));
                productInfo.put("CONTRACT_WRITE_DATE", contractInfo.getString("CONTRACT_WRITE_DATE"));
                productInfo.put("OFFER_IDS", contractInfo.getString("OFFER_IDS"));
                productInfo.put("GROUP_HOTEL","true");
                setPattrInfo(productInfo);
            }

        }
    }

    @Override
    public void buildOtherSvcParam(IData param) throws Exception {
        super.buildOtherSvcParam(param);
    }

    /**
     ** 查询客户是否已经同步ESP平台
     * @param info
     * @throws Exception
     * @Date 2019年12月8日
     * @author xieqj
     */
    public void querySynchrostateESP(IData info) throws Exception {
        IData groupInfo = getGroupInfo();
        IData cond = new DataMap();
        cond.put("CUST_ID", groupInfo.getString("CUST_ID"));
        IData result = CSViewCall.callone(this, "CC.group.IGroupQuerySV.queryGroupSynchrostateESP", cond);
        IDataset resultEsp = result.getDataset("DATAS");

        IData espSnyInfo = new DataMap();
        if (resultEsp != null && resultEsp.size() != 0) {
            IData synchroinfo = (IData) resultEsp.get(0);
            espSnyInfo.putAll(synchroinfo);
        } else {
            espSnyInfo.put("SYN_ESP", "0");
            espSnyInfo.put("SYN_ESPSTATE", "未同步");
        }
        info.put("espSny", espSnyInfo);
    }
}
