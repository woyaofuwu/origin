package com.asiainfo.veris.crm.iorder.web.igroup.esop.myWorkForm;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class SummarizeEoms extends EopBasePage {

    public abstract void setCondition(IData cond);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfo(IData info);

    public abstract void setFileList(IDataset fileList);

    public abstract IDataset getOrders();

    public abstract IData getOrderDetail();

    public abstract void setOrderDetail(IData orderDetail);

    public abstract void setOrders(IDataset orders);

    public abstract void setProductInfos(IDataset productInfos);

    public abstract void setWorkSheet(IDataset worksheet);

    public void queryData(IRequestCycle cycle) throws Exception {
        IData data = this.getData();
        IDataset eomsInfos = CSViewCall.call(this, "SS.WorkFormSVC.querySummarizeEoms", data);

        setWorkSheet(eomsInfos);
        setCondition(data);
    }

    // 流程图查询
    public void intiLiuCheng(IRequestCycle cycle) throws Exception {

        IData data = getData();

        String bpm_templer_id = data.getString("BPM_TEMPLET_ID", "");
        String ibsysid = data.getString("IBSYSID", "");
        String is_finish = data.getString("is_finish", "");
        String productId = data.getString("PRODUCT_ID", "");
        String bizRange = data.getString("BIZRANGE", "");
        String recodeNum = data.getString("RECORD_NUM", "");
        String changeMdoe = data.getString("CHANGEMODE", "");
        if ("异楼搬迁".equals(changeMdoe) || "扩容".equals(changeMdoe)) {
            changeMdoe = "业务场景";
        }
        if (StringUtils.isEmpty(is_finish)) {
            is_finish = "false";
        }
        String bpmTemplerId = "";
        if ("EVIOPDIRECTLINEOPENPBOSS".equals(bpm_templer_id) && "7010".equals(productId)) {
            bpmTemplerId = "EMOS_VOIP_CREATE";
        }
        if ("EDIRECTLINEOPENPBOSS".equals(bpm_templer_id) && (("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId))||"70111".equals(productId)||"70112".equals(productId))) {
            bpmTemplerId = "EMOS_NETIN_CREATE";
        }
        if ("EDIRECTLINEOPENPBOSS".equals(bpm_templer_id) && ("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "本地市".equals(bizRange)) {
            bpmTemplerId = "EMOS_DATALINE_CREATE_THIS";
        }
        if ("EDIRECTLINEOPENPBOSS".equals(bpm_templer_id) && ("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "省内跨地市".equals(bizRange)) {
            bpmTemplerId = "EMOS_DATALINE_CREATE_SPAN";
        }
        if ("ECHANGERESOURCECONFIRM".equals(bpm_templer_id) && "7010".equals(productId)) {
            bpmTemplerId = "EMOS_VOIP_CHANGE_RESOURCE";
        }
        if ("ECHANGERESOURCECONFIRM".equals(bpm_templer_id) && ("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId))) {
            bpmTemplerId = "EMOS_NETIN_CHANGE_RESOURCE";
        }
        if ("ECHANGERESOURCECONFIRM".equals(bpm_templer_id) && ("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "本地市".equals(bizRange)) {
            bpmTemplerId = "EMOS_DATALINE_CHANGE_RESOURCE_THIS";
        }
        if ("ECHANGERESOURCECONFIRM".equals(bpm_templer_id) && ("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "省内跨地市".equals(bizRange)) {
            bpmTemplerId = "EMOS_DATALINE_CHANGE_RESOURCE_SPAN";
        }
        if ("EVIOPDIRECTLINECHANGEPBOSS".equals(bpm_templer_id) && "7010".equals(productId)) {
            bpmTemplerId = "EMOS_VOIP_CREATE_RESOURCE";
        }
        if ("ERESOURCECONFIRMZHZG".equals(bpm_templer_id) && ("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId))) {
            bpmTemplerId = "EMOS_NETIN_CREATE_RESOURCE";
        }
        if ("ERESOURCECONFIRMZHZG".equals(bpm_templer_id) && ("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "本地市".equals(bizRange)) {
            bpmTemplerId = "EMOS_DATALINE_CREATE_RESOURCE_THIS";
        }
        if ("ERESOURCECONFIRMZHZG".equals(bpm_templer_id) && ("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "省内跨地市".equals(bizRange)) {
            bpmTemplerId = "EMOS_DATALINE_CREATE_RESOURCE_SPAN";
        }
        if ("EVIOPDIRECTLINECANCELPBOSS".equals(bpm_templer_id) && "7010".equals(productId)) {
            bpmTemplerId = "EMOS_VOIP_DESTORY";
        }
        if ("EDIRECTLINECANCELPBOSS".equals(bpm_templer_id) && ("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId))) {
            bpmTemplerId = "EMOS_NETIN_DESTORY";
        }
        if ("EDIRECTLINECANCELPBOSS".equals(bpm_templer_id) && ("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId))) {
            bpmTemplerId = "EMOS_DATALINE_DESTORY";
        }
        if ("CREDITDIRECTLINEPARSE".equals(bpm_templer_id) && "7010".equals(productId)) {
            bpmTemplerId = "EMOS_VOIP_CREDIT_HALT";
        }
        if ("CREDITDIRECTLINEPARSE".equals(bpm_templer_id) && ("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId))) {
            bpmTemplerId = "EMOS_NETIN_CREDIT_HALT";
        }
        if ("CREDITDIRECTLINEPARSE".equals(bpm_templer_id) && ("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId))) {
            bpmTemplerId = "EMOS_DATALINE_CREDIT_HALT";
        }
        if ("CREDITDIRECTLINECONTINUE".equals(bpm_templer_id) && "7010".equals(productId)) {
            bpmTemplerId = "EMOS_VOIP_CREDIT_REPLY";
        }
        if ("CREDITDIRECTLINECONTINUE".equals(bpm_templer_id) && ("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId))) {
            bpmTemplerId = "EMOS_NETIN_CREDIT_REPLY";
        }
        if ("CREDITDIRECTLINECONTINUE".equals(bpm_templer_id) && ("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId))) {
            bpmTemplerId = "EMOS_DATALINE_CREDIT_REPLY";
        }
        if ("EVIOPDIRECTLINECHANGEPBOSS".equals(bpm_templer_id) && "7010".equals(productId) && "业务场景".equals(changeMdoe)) {
            bpmTemplerId = "EMOS_VOIP_CHANGE_SCENE";
        }
        if ("MANUALSTOP".equals(bpm_templer_id) && "7010".equals(productId) && "停机".equals(changeMdoe)) {
            bpmTemplerId = "EMOS_VOIP_CHANGE_HALT";
        }
        if ("MANUALBACK".equals(bpm_templer_id) && "7010".equals(productId) && "复机".equals(changeMdoe)) {
            bpmTemplerId = "EMOS_VOIP_CHANGE_REPLY";
        }
        if ("EVIOPDIRECTLINECHANGESIMPLE".equals(bpm_templer_id) && "7010".equals(productId) && "同楼搬迁".equals(changeMdoe)) {
            bpmTemplerId = "EMOS_VOIP_CHANGE_THETOWERMOVE";
        }
        if ("EVIOPDIRECTLINECHANGESIMPLE".equals(bpm_templer_id) && "7010".equals(productId) && "减容".equals(changeMdoe)) {
            bpmTemplerId = "EMOS_VOIP_CHANGE_SCENE";
        }
        if ("EVIOPDIRECTLINECHANGEPBOSS".equals(bpm_templer_id) && "7010".equals(productId) && "业务保障级别调整".equals(changeMdoe)) {
            bpmTemplerId = "EMOS_VOIP_CHANGE_RANK_ADJUSTMENT";
        }

        if ("EDIRECTLINECHANGEPBOSS".equals(bpm_templer_id) && ("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)) && "业务场景".equals(changeMdoe)) {
            bpmTemplerId = "EMOS_NETIN_CHANGE_SCENE";
        }
        if ("MANUALSTOP".equals(bpm_templer_id) && ("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)) && "停机".equals(changeMdoe)) {
            bpmTemplerId = "EMOS_NETIN_CHANGE_HALT";
        }
        if ("MANUALBACK".equals(bpm_templer_id) && ("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)) && "复机".equals(changeMdoe)) {
            bpmTemplerId = "EMOS_NETIN_CHANGE_REPLY";
        }
        if ("DIRECTLINECHANGESIMPLE".equals(bpm_templer_id) && ("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)) && "同楼搬迁".equals(changeMdoe)) {
            bpmTemplerId = "EMOS_NETIN_CHANGE_THETOWERMOVE";
        }
        if ("DIRECTLINECHANGESIMPLE".equals(bpm_templer_id) && ("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)) && "减容".equals(changeMdoe)) {
            bpmTemplerId = "EMOS_NETIN_CHANGE_SCENE";
        }
        if ("DIRECTLINECHANGESIMPLE".equals(bpm_templer_id) && ("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)) && "IP地址调整".equals(changeMdoe)) {
            bpmTemplerId = "EMOS_NETIN_CHANGE_IP_ADJUSTMENT";
        }
        if ("EDIRECTLINECHANGEPBOSS".equals(bpm_templer_id) && ("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)) && "业务保障级别调整".equals(changeMdoe)) {
            bpmTemplerId = "EMOS_NETIN_CHANGE_RANK_ADJUSTMENT";
        }

        if ("EDIRECTLINECHANGEPBOSS".equals(bpm_templer_id) && ("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "本地市".equals(bizRange) && "业务场景".equals(changeMdoe)) {
            bpmTemplerId = "EMOS_DATALINE_CHANGE_SCENE_THIS";
        }
        if ("MANUALSTOP".equals(bpm_templer_id) && ("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "本地市".equals(bizRange) && "停机".equals(changeMdoe)) {
            bpmTemplerId = "EMOS_DATALINE_CHANGE_HALT_THIS";
        }
        if ("MANUALBACK".equals(bpm_templer_id) && ("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "本地市".equals(bizRange) && "复机".equals(changeMdoe)) {
            bpmTemplerId = "EMOS_DATALINE_CHANGE_REPLY_THIS";
        }
        if ("DIRECTLINECHANGESIMPLE".equals(bpm_templer_id) && ("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "本地市".equals(bizRange) && "同楼搬迁".equals(changeMdoe)) {
            bpmTemplerId = "EMOS_DATALINE_CHANGE_THETOWERMOVE_THIS";
        }
        if ("DIRECTLINECHANGESIMPLE".equals(bpm_templer_id) && ("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "本地市".equals(bizRange) && "减容".equals(changeMdoe)) {
            bpmTemplerId = "EMOS_DATALINE_CHANGE_SCENE_THIS";
        }
        if ("EDIRECTLINECHANGEPBOSS".equals(bpm_templer_id) && ("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "本地市".equals(bizRange) && "业务保障级别调整".equals(changeMdoe)) {
            bpmTemplerId = "EMOS_DATALINE_CHANGE_RANK_ADJUSTMENT_THIS";
        }

        if ("EDIRECTLINECHANGEPBOSS".equals(bpm_templer_id) && ("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "省内跨地市".equals(bizRange) && "业务场景".equals(changeMdoe)) {
            bpmTemplerId = "EMOS_DATALINE_CHANGE_SCENE_SPAN";
        }
        if ("MANUALSTOP".equals(bpm_templer_id) && ("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "省内跨地市".equals(bizRange) && "停机".equals(changeMdoe)) {
            bpmTemplerId = "EMOS_DATALINE_CHANGE_HALT_SPAN";
        }
        if ("MANUALBACK".equals(bpm_templer_id) && ("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "省内跨地市".equals(bizRange) && "复机".equals(changeMdoe)) {
            bpmTemplerId = "EMOS_DATALINE_CHANGE_REPLY_SPAN";
        }
        if ("DIRECTLINECHANGESIMPLE".equals(bpm_templer_id) && ("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "省内跨地市".equals(bizRange) && "同楼搬迁".equals(changeMdoe)) {
            bpmTemplerId = "EMOS_DATALINE_CHANGE_THETOWERMOVE_SPAN";
        }
        if ("DIRECTLINECHANGESIMPLE".equals(bpm_templer_id) && ("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "省内跨地市".equals(bizRange) && "减容".equals(changeMdoe)) {
            bpmTemplerId = "EMOS_DATALINE_CHANGE_SCENE_SPAN";
        }
        if ("EDIRECTLINECHANGEPBOSS".equals(bpm_templer_id) && ("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) && "省内跨地市".equals(bizRange) && "业务保障级别调整".equals(changeMdoe)) {
            bpmTemplerId = "EMOS_DATALINE_CHANGE_RANK_ADJUSTMENT_SPAN";
        }
        if (StringUtils.isNotBlank(bpmTemplerId)) {
            IData charInfo = new DataMap();
            charInfo.put("BPM_TEMPLET_ID", bpmTemplerId);
            charInfo.put("IBSYSID", ibsysid);
            charInfo.put("IS_HIS", is_finish);
            charInfo.put("RECORD_NUM", recodeNum);

            IDataset chartInfos = CSViewCall.call(this, "SS.WorkformChartSVC.dealWorkformChartEoms", charInfo);
            if (DataUtils.isNotEmpty(chartInfos)) {
                IData resultCharInfo = chartInfos.getData(0);
                String xml = resultCharInfo.getString("XML_INFO", "");
                resultCharInfo.remove("XML_INFO");
                this.setCondition(resultCharInfo);
                charInfo.put("XML_INFO", xml);
            }
            this.setAjax(charInfo);

            // IDataset chartInfos = CSViewCall.call(this, "SS.WorkformChartInfoSVC.qryTempletData", charInfo);
            //
            // if (DataUtils.isNotEmpty(chartInfos)) {
            // IData resultCharInfo = chartInfos.getData(0);
            // String xml = resultCharInfo.getString("XML_INFO", "");
            // resultCharInfo.remove("XML_INFO");
            // this.setCondition(resultCharInfo);
            // charInfo.put("XML_INFO", xml);
            // }
            // this.setAjax(charInfo);
        }

    }

    // 查询工单节点详情
    public void queryNodeDetail(IRequestCycle cycle) throws Exception {
        IData data = this.getData();
        IData eomsInfos = CSViewCall.callone(this, "SS.WorkFormSVC.queryEomsNodeDetail", data);
        setInfo(eomsInfos);
    }

}
