package com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.base;

import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.utils.ElecLineUtil;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;


public abstract class ElecLineProtocolBase extends GroupBasePage {

    private static final transient Logger log = Logger.getLogger(ElecLineProtocolBase.class);

    public void initPage(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IData protocolInfo = new DataMap();
        IData accessoryInfo = new DataMap();
        IData info = new DataMap();

        IDataset lineInfos = new DatasetList();
        accessoryInfo.put("LINE_INFOS",lineInfos);

        String archivesId = pageData.getString("ARCHIVES_ID","");
        if(StringUtils.isEmpty(archivesId)){
            archivesId = pageData.getString("ARCHIVE_ID","");
        }
        info.put("SHOWBUTTON",pageData.getString("SHOWBUTTON","true"));
        info.put("BACK_BUTTON",pageData.getString("BACK_BUTTON","true"));

        info.put("IBSYSID",pageData.getString("IBSYSID"));
        info.put("NODE_ID",pageData.getString("NODE_ID"));
        info.put("USER_ID",pageData.getString("USER_ID"));
        info.put("OPER_TYPE",pageData.getString("OPER_TYPE"));

        if ("".equals(archivesId)) {
            info.put("SUBMITTYPE", "N");
            pageData.put("SUBMITTYPE", "N");
            accessoryInfo.put("CUST_ID",pageData.getString("CUST_ID"));
            protocolInfo.put("PRODUCT_ID", pageData.getString("PRODUCT_ID",""));
            protocolInfo.put("AGREEMENT_ID", pageData.getString("AGREEMENT_ID"));
            accessoryInfo.put("AGREEMENT_DEF_ID",pageData.getString("AGREEMENT_DEF_ID"));
        }else{
            info.put("SUBMITTYPE", "U");
            pageData.put("SUBMITTYPE", "U");
            info.put("ARCHIVES_ID", archivesId);
            IData archives = new DataMap();
            archives.put("ARCHIVES_ID", archivesId);
            archives.put("ARCHIVES_TYPE", "AG");
            IDataset protocolInfos = CSViewCall.call( this,"SS.AgreementInfoSVC.queryElectronicAgreementInfo", archives);
            protocolInfo = protocolInfos.first();

            IDataset accessoryInfoData = CSViewCall.call( this,"SS.AgreementInfoSVC.queryDetailAttrElectronicInfo", archives);


            IData groupMap = new DataMap();

            for (int i = 0; i < accessoryInfoData.size(); i++) {
                IData tempInfo = accessoryInfoData.getData(i);
                if("-1".equals(tempInfo.getString("ATTR_GROUP"))){
                    accessoryInfo.put(tempInfo.getString("ATTR_CODE"), tempInfo.getString("ATTR_VALUE"));
                    continue;
                }else{
                    String groupIndex = tempInfo.getString("GROUP_INDEX");
                    if(groupMap.get(groupIndex) == null){
                        groupMap.put(groupIndex, new DataMap());
                    }
                    groupMap.getData(groupIndex).put(tempInfo.getString("ATTR_CODE"), tempInfo.getString("ATTR_VALUE"));
                }
            }
            String[] keyNames = groupMap.getNames();
            for (String keyname : keyNames) {
                lineInfos.add(groupMap.getData(keyname));
            }
        }

        //取集团信息
        String groupId = pageData.getString("GROUP_ID");
        if(StringUtils.isNotBlank(groupId)){
            IData groupParam = new DataMap();
            groupParam.put("GROUP_ID",groupId);
            IData group = CSViewCall.callone(this,"SS.QryAgreementSVC.qryGrpInfoByGrpId",groupParam);

            protocolInfo.put("CUST_INFO",group);

            String custMgrId = group.getString("CUST_MANAGER_ID");
            if (StringUtils.isNotEmpty(custMgrId))
            {
                IData managerInfo = UCAInfoIntfViewUtil.qryCustManagerByCustManagerId(this, custMgrId);

                protocolInfo.put("CUST_MRG_INFO",managerInfo);
            }
        }

        initalPageValue(protocolInfo,accessoryInfo,pageData);

        setProtocolInfo(protocolInfo);
        setAccessoryInfo(accessoryInfo);
        setInfo(info);
    }

    protected abstract void initalPageValue(IData protocolInfo, IData accessoryInfo, IData pageData) throws Exception;

    /**
     * 电子档案提交
     * @param cycle
     * @throws Exception
     */
    public void onSubmit(IRequestCycle cycle) throws Exception {
        IData pageData = getData();
        IData protocolInfoData = buildFDFInfo(pageData);

        IData data = new DataMap();
        data.put("AGREEMENT_DEF_ID",pageData.getString("AGREEMENT_DEF_ID"));
        data.put("PRODUCT_ID",pageData.getString("PRODUCT_ID"));
        IDataset tempIdInfo = CSViewCall.call( this, "SS.AgreementInfoSVC.queryElecAgrePdfInfo", data);
        if (IDataUtil.isEmpty(tempIdInfo)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103,"AGREEMENT_DEF_ID【"+pageData.getString("AGREEMENT_DEF_ID")+"】查询TD_B_ELEC_AGRE_PDF_DEF_REL合同模板ID不存在！");
        }
        IData pdfInfo = tempIdInfo.first();


        pageData.put("ARCHIVES_NAME", pdfInfo.getString("AGREEMENT_NAME"));
        pageData.put("ARCHIVES_TYPE", pdfInfo.getString("ARCHIVE_TYPE_CODE"));
        pageData.put("ELECTRONIC_TYPE_SUB", pdfInfo.getString("SUB_ARCHIVE_TYPE_CODE"));
        pageData.put("CONTRACT_CODE", pdfInfo.getString("CONTRACT_CODE"));


        protocolInfoData.put("TEMPLATE_ID",pdfInfo.getString("AGRE_PDF_ID"));

        IDataset result = CSViewCall.call( this,"SS.AgreementInfoSVC.buildElecTemplateToFile", protocolInfoData);
        IDataset pdfFile = new DatasetList();
        pdfFile.add(result.first());
        pageData.put("PDF_FILE", pdfFile);

        IData resultData = new DataMap();
        if ("U".equals(pageData.getString("SUBMITTYPE"))) {
            IData archives = updateProtocolInfo(pageData);
            IDataset resultMap = CSViewCall.call( this, "SS.AgreementInfoSVC.updateElectronicArchive", archives);
            resultData.put("ARCHIVE_ID", pageData.getString("ARCHIVES_ID"));
            pageData.put("CONTRACT_START_DATE", resultMap.first().getString("START_DATE"));
            pageData.put("CONTRACT_END_DATE", resultMap.first().getString("END_DATE"));
        } else {
            //校验合同编码是否已经被使用
            String sub_archive_type = pdfInfo.getString("SUB_ARCHIVE_TYPE_CODE", "");
            if("AGCONTENT".equals(sub_archive_type)){
                IData input = new DataMap();
                input.put("AGREEMENT_ID",pageData.getString("AGREEMENT_ID"));
                IDataset agreementInfos = CSViewCall.call(this, "SS.AgreementInfoSVC.queryElectronicAgreement", input);
                if(DataUtils.isNotEmpty(agreementInfos)){
                    CSViewException.apperr(CrmCommException.CRM_COMM_103,"合同编码【"+pageData.getString("AGREEMENT_ID")+"】已经被使用，请重新占用合同ID！");
                }
            }

            IData archives = saveProtocolInfo(pageData);
            IDataset resultMap = CSViewCall.call( this,"SS.AgreementInfoSVC.createElectronicArchive", archives);
            resultData.put("ARCHIVE_ID", resultMap.first().getString("ARCHIVES_ID"));
            pageData.put("CONTRACT_START_DATE", resultMap.first().getString("START_DATE"));
            pageData.put("CONTRACT_END_DATE", resultMap.first().getString("END_DATE"));
        }
        resultData.put("AGREEMENT_DEF_ID", pageData.getString("AGREEMENT_DEF_ID"));
        resultData.put("URL", pageData.getString("page"));
        setBackValue(resultData,pageData);
        setAjax(resultData);
    }

    public void onPrint(IRequestCycle cycle) throws Exception {
        IData pageData = getData();
        IData protocolInfoData = buildFDFInfo(pageData);
        //查询模板ID
        IData data = new DataMap();
        data.put("AGREEMENT_DEF_ID",pageData.getString("AGREEMENT_DEF_ID"));
        IDataset tempIdInfo = CSViewCall.call( this,"SS.AgreementInfoSVC.queryElecAgrePdfdefrel", data);
        if (IDataUtil.isEmpty(tempIdInfo)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103,"AGREEMENT_DEF_ID【"+pageData.getString("AGREEMENT_DEF_ID")+"】查询TD_B_ELEC_AGRE_PDF_DEF_REL合同模板ID不存在！");
        }
        protocolInfoData.put("TEMPLATE_ID",tempIdInfo.first().getString("AGRE_PDF_ID"));

        data.clear();
        IDataset result = CSViewCall.call( this,"SS.AgreementInfoSVC.buildElecTemplateToFile", protocolInfoData);
        if(IDataUtil.isNotEmpty(result)){
            data.put("FILE_ID", result.first().getString("FILE_ID"));
            data.put("FILE_NAME", result.first().getString("FILE_NAME"));
        }
        setAjax(data);
    }

    protected abstract IData buildFDFInfo(IData pageData) throws Exception;

    protected abstract IData updateProtocolInfo(IData pageData) throws Exception;

    protected abstract IData saveProtocolInfo(IData pageData) throws Exception;

    protected void setBackValue(IData resultData, IData pageData) throws Exception{
        resultData.put("ARCHIVES_NAME", pageData.getString("ARCHIVES_NAME"));
        String agreementId = pageData.getString("AGREEMENT_ID");
        IData data = new DataMap();
        data.put("AGREEMENT_ID", agreementId);
        IDataset relList = CSViewCall.call(this, "SS.AgreementInfoSVC.queryElecRELByAgreementId", data);
        if(DataUtils.isNotEmpty(relList)){
            resultData.put("PRODUCT_ID", relList.first().getString("PRODUCT_ID"));
        }
        ElecLineUtil.setBackValue(resultData,pageData);
    }

    public void iniProtocolStaticVar() throws Exception {
        IDataset bInfo = pageutil.getStaticList("ELECLINEPAYMENTCYCLE");
        IData info = getInfo();
        if(IDataUtil.isNotEmpty(bInfo)){
            for (int i = 0; i < bInfo.size(); i++) {
                IData tempData = bInfo.getData(i);
                info.put(tempData.getString("CODE_NAME"),tempData.getString("CODE_VALUE"));
            }
        }
    }
    public void iniWayOfPayings() throws Exception {
        IDataset wayOfPayings = new DatasetList();
        IData transferAccounts = new DataMap();
        transferAccounts.put("TextField","转账");
        transferAccounts.put("ValueField","转账");
        IData check = new DataMap();
        check.put("TextField","支票");
        check.put("ValueField","支票");
        IData cash = new DataMap();
        cash.put("TextField","现金");
        cash.put("ValueField","现金");
        IData bankCollection = new DataMap();
        bankCollection.put("TextField","银行托收");
        bankCollection.put("ValueField","银行托收");
        wayOfPayings.add(transferAccounts);
        wayOfPayings.add(check);
        wayOfPayings.add(cash);
        wayOfPayings.add(bankCollection);
        getInfo().put("WAY_OF_PAYINGS",wayOfPayings);
    }

    //初始化页面变量
    public void iniAccessStaticVar() throws Exception {
        // TODO Auto-generated method stub
        IDataset bInfo = pageutil.getStaticList("ELECLINEPAYMENTCYCLE");
        IDataset paymentCycle = new DatasetList();
        if(IDataUtil.isNotEmpty(bInfo)){
            for (int i = 0; i < bInfo.size(); i++) {
                IData tempData = bInfo.getData(i);
                IData tempDate = new DataMap();
                tempDate.put("TextField", tempData.getString("CODE_DESC"));
                tempDate.put("ValueField", tempData.getString("CODE_DESC"));
                paymentCycle.add(tempDate);
            }
        }
        setPaymentCycle(paymentCycle);
    }



    protected void assignmentValue(IData pageData, String key1, String key2){
        if("1".equals(pageData.getString(key1))){
            pageData.put(key1, "1");
            pageData.put(key2, "0");
        }else{
            pageData.put(key1, "0");
            pageData.put(key2, "1");
        }
    }

    public abstract void setPaymentCycle(IDataset paymentCycle) throws Exception;
    public abstract void setInfo(IData info) throws Exception;
    public abstract IData getInfo() throws Exception;
    public abstract void setProtocolInfo(IData protocolInfo) throws Exception;
    public abstract void setAccessoryInfo(IData accessoryInfo) throws Exception;

}
