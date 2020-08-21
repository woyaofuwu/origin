package com.asiainfo.veris.crm.iorder.soa.group.param.minorec.elecagreement;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

import java.util.Iterator;

public class AgreementInfoSVC extends CSBizService {
    private static final long serialVersionUID = 1L;

    public IDataset queryAgreementDefInfo(IData input) throws Exception {
        return AgreementInfoBean.queryAgreementDefInfo(input);
    }

    public IDataset preemptAgreementId(IData input) throws Exception {
        IDataset results = AgreementInfoBean.preemptAgreementId(input);
        if (IDataUtil.isNotEmpty(results)) {
            IData contractInfo = results.first();
            contractInfo.put("RES_STATE", "P");
            contractInfo.put("UPDATE_STAFF_ID", input.getString("STAFF_ID"));
            contractInfo.put("UPDATE_DEPART_ID", input.getString("DEPART_ID"));
            contractInfo.put("UPDATE_TIME", input.getString("UPDATE_TIME"));
            AgreementInfoBean.updadteAgreementId(contractInfo);
        }
        return results;
    }

    public IDataset queryElectronicAgreement(IData input) throws Exception {
        return AgreementInfoBean.queryElectronicAgreement(input);
    }

    public IDataset queryAgreementInfosByCIDAndPID(IData input) throws Exception {
        return AgreementInfoBean.queryAgreementInfosByCIDAndPID(input);
    }

    public IDataset queryElectronicArchives(IData input) throws Exception {
        return AgreementInfoBean.queryElectronicArchives(input);
    }

    public IDataset queryElectronicAgreAttach(IData input) throws Exception {
        return AgreementInfoBean.queryElectronicAgreAttach(input);
    }

    public IDataset addElecAgreementAttachInfo(IData input) throws Exception {
        IDataset attachInfos = new DatasetList(input.getString("attachInfos"));

        for (int i = 0; i < attachInfos.size(); i++) {
            IData attachInfo = attachInfos.getData(i);
            IDataset fileList = new DatasetList(attachInfo.getString("FILE_LIST"));
            if (DataUtils.isNotEmpty(fileList)) {
                attachInfo.put("ARCHIVES_ATTACH", fileList.toString());
                attachInfo.put("STATE_DESC", "pc");
                AgreementInfoBean.updateElecAgreementAttachInfo(attachInfo);
            }
        }
        return new DatasetList();
    }
    public IDataset queryElectronicAgreementInfo(IData input) throws Exception {
        IDataset agreementInfo = AgreementInfoBean.queryElectronicAgreement(input);
        if (IDataUtil.isEmpty(agreementInfo)) {
            agreementInfo = AgreementInfoBean.queryElectronicAgreAttach(input);
        }
        return agreementInfo;
    }

    public IDataset queryDetailAttrElectronicInfo(IData input) throws Exception {
        return AgreementInfoBean.queryDetailAttrElectronicInfo(input);
    }

    public IDataset queryElecAgreement(IData input) throws Exception {
        return AgreementInfoBean.queryElecAgreement(input, getPagination());
    }

    public IDataset updateElectronicInfo(IData input) throws Exception {
        IDataset agreementInfo = AgreementInfoBean.queryElectronicAgreement(input);
        updateElectronicSTATE(input, agreementInfo);
        IDataset attachInfo = AgreementInfoBean.queryElectronicAgreAttach(input);
        updateElectronicSTATE(input, attachInfo);
        return new DatasetList();
    }

    private void updateElectronicSTATE(IData input, IDataset attachInfo) throws Exception {
        if (IDataUtil.isNotEmpty(attachInfo)) {
            for (int i = 0; i < attachInfo.size(); i++) {
                IData param = new DataMap();
                param.put("ARCHIVES_STATE", input.getString("ARCHIVES_STATE"));
                param.put("ARCHIVES_ID", attachInfo.getData(i).getString("ARCHIVES_ID"));
                param.put("END_DATE",SysDateMgr.getSysDate());
                param.put("UPDATE_TIME",SysDateMgr.getSysDate());
                param.put("UPDATE_STAFF_ID",getVisit().getStaffId());
                param.put("UPDATE_DEPART_ID",getVisit().getDepartId());
                AgreementInfoBean.updateElectronicInfo(param);
            }
        }
    }

    public IDataset queryElecAgrePdfInfo(IData input) throws Exception {
        return AgreementInfoBean.queryElecAgrePdfInfo(input);
    }

    public IDataset queryElecTemplateRele(IData input) throws Exception {
        return AgreementInfoBean.qryPdfReleByTemplateId(input);
    }

    public IDataset queryElecRELByAgreementId(IData input) throws Exception {
        return AgreementInfoBean.qryElecAgreementRel(input);
    }

    public IData updateElectronicRel(IData input) throws Exception{
        IData param = new DataMap();
        param.put("AGREEMENT_ID", input.getString("AGREEMENT_ID"));
        param.put("IBSYSID", input.getString("IBSYSID",""));
        param.put("VALID_TAG", input.getString("VALID_TAG"));
        AgreementInfoBean.updateAgreemnetRelState(param);
        return new DataMap();
    }

    public IDataset buildElecTemplateToFile(IData input) throws Exception {
        return ElecAgreementUtils.buildTemplateToFile(input.getString("TEMPLATE_ID"), input.getDataset("DATAS"),getVisit());
    }

    public IDataset uploadImage(IData input) throws Exception {
        //IDataset fileIds = ElecAgreementUtils.uploadImageToFtp(input);
        IData data = new DataMap();

        data.put("ARCHIVES_ATTACH",input.getString("ARCHIVES_ATTACH"));
        data.put("ARCHIVES_ID",input.getString("ARCHIVES_ID"));
        data.put("STATE_DESC",input.getString("STATE_DESC"));
        AgreementInfoBean.updateElecAgreementAttachInfo(data);

        return new DatasetList();
    }

    public IDataset queryAgArchivesByAgreementId(IData data) throws Exception {
        return AgreementInfoBean.queryAgArchivesByAgreementId(data);
    }

    public IData updateAgreementFinish(IData data) throws Exception{
        ElecAgreementUtils.checkParam(data, "AGREEMENT_ID");
        ElecAgreementUtils.checkParam(data, "ARCHIVES_STATE");

        String archivesState = data.getString("ARCHIVES_STATE");
        String archivesId = data.getString("ARCHIVES_ID");

        if("3".equals(archivesState)){//归档
            IData param = new DataMap();
            param.put("AGREEMENT_ID", data.getString("AGREEMENT_ID"));
            param.put("IBSYSID","");
            param.put("VALID_TAG", "1");
            AgreementInfoBean.updateAgreemnetRelState(param);
        }

        if(StringUtils.isNotBlank(archivesId)){
            AgreementInfoBean.updateArchivesState(data);
        }else{
            IDataset archivesList = new DatasetList();
            IDataset agArchives = AgreementInfoBean.queryElectronicAgreement(data);
            if(DataUtils.isNotEmpty(agArchives)){
                archivesList.addAll(agArchives);
            }
            IDataset attachArchives = AgreementInfoBean.queryElectronicAgreAttach(data);
            if(DataUtils.isNotEmpty(attachArchives)){
                archivesList.addAll(attachArchives);
            }
            for(int i=0;i<archivesList.size();i++){
                IData archivesData = archivesList.getData(i);
                IData arInput = new DataMap();
                arInput.put("ARCHIVES_ID",archivesData.getString("ARCHIVES_ID"));
                IDataset archiveInfos = AgreementInfoBean.queryElectronicArchives(arInput);
                //过滤已归档和失效协议
                if(DataUtils.isEmpty(archiveInfos)||"3".equals(archiveInfos.first().getString("ARCHIVES_STATE"))||"4".equals(archiveInfos.first().getString("ARCHIVES_STATE"))){
                    continue;
                }

                String arId = archivesData.getString("ARCHIVES_ID");
                IData input = new DataMap();
                input.put("ARCHIVES_ID", arId);
                input.put("ARCHIVES_STATE", data.getString("ARCHIVES_STATE"));
                AgreementInfoBean.updateArchivesState(input);
            }
        }
        return new DataMap();
    }

    public IDataset updateElectronicArchive(IData input) throws Exception {

        IDataset results = new DatasetList();
        String archives_id = input.getString("ARCHIVES_ID");
        AgreementInfoBean.updateElectronicInfo(input);// 更新电子档案主表
        IData archives_info = input.getData("ARCHIVES_INFO");
        String sysStart_date = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);
        String sub_archive_type = input.getString("ELECTRONIC_TYPE_SUB", "");// 子类型
        archives_info.put("ARCHIVES_ID", archives_id);

        if ("AGCONTENT".equals(sub_archive_type)) {
            AgreementInfoBean.updateElectronicAgreement(archives_info);
        } else {
            AgreementInfoBean.updateElectronicAgreAttach(archives_info);
        }

        IDataset products = input.getDataset("PRODUCTS",new DatasetList());
        if ("AGCONTENT".equals(sub_archive_type)) {
            //只有正文做关联
            IData oldRel = new DataMap();
            oldRel.put("VALID_TAG", "1");
            oldRel.put("ARCHIVES_ID", archives_id);
            AgreementInfoBean.updateElectronicRelEnd(oldRel);
            for (int i = 0; i < products.size(); i++) {
                IData rel = new DataMap();
                rel.put("REL_ID", SeqMgr.getArchivesId());
                rel.put("CUST_ID", input.getString("CUST_ID"));
                rel.put("USER_ID", input.getString("USER_ID", "-1"));// 1客户,
                rel.put("ARCHIVES_ID", archives_id);
                rel.put("CREATE_TIME", sysStart_date);
                rel.put("CREATE_STAFF_ID", input.getString("UPDATE_STAFF_ID"));
                rel.put("CREATE_DEPART_ID", input.getString("UPDATE_DEPART_ID"));
                rel.put("UPDATE_TIME", sysStart_date);
                rel.put("UPDATE_STAFF_ID", input.getString("UPDATE_STAFF_ID"));
                rel.put("UPDATE_DEPART_ID", input.getString("UPDATE_DEPART_ID"));
                rel.put("PRODUCT_ID", String.valueOf(products.get(i)));
                rel.put("VALID_TAG", "0");
                AgreementInfoBean.insertElectronicRel(rel);
            }
        }

        if (IDataUtil.isNotEmpty(products)) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < products.size(); i++) {
                sb.append("," + String.valueOf(products.get(i)));
            }
            IData dataRel = new DataMap();
            dataRel.put("AGREEMENT_ID", archives_info.getString("AGREEMENT_ID"));
            IDataset agreemtnRels = AgreementInfoBean.qryElecAgreementRel(dataRel);
            if(DataUtils.isNotEmpty(agreemtnRels)){
                IData agreemtnRel = agreemtnRels.first();
                agreemtnRel.put("PRODUCT_ID", sb.substring(1));
                agreemtnRel.put("UPDATE_TIME", sysStart_date);
                agreemtnRel.put("UPDATE_STAFF_ID", input.getString("UPDATE_STAFF_ID"));
                agreemtnRel.put("UPDATE_DEPART_ID", input.getString("UPDATE_DEPART_ID"));
                AgreementInfoBean.updadteElecAgreementRel(agreemtnRel);
            }else{
                IData agreemtnRel = new DataMap();
                agreemtnRel.put("REL_ID", SeqMgr.getArchivesId());
                agreemtnRel.put("AGREEMENT_ID", archives_info.getString("AGREEMENT_ID"));
                agreemtnRel.put("PRODUCT_ID", sb.substring(1));
                agreemtnRel.put("CREATE_TIME", sysStart_date);
                agreemtnRel.put("CREATE_STAFF_ID", input.getString("UPDATE_STAFF_ID"));
                agreemtnRel.put("CREATE_DEPART_ID", input.getString("UPDATE_DEPART_ID"));
                agreemtnRel.put("UPDATE_TIME", sysStart_date);
                agreemtnRel.put("UPDATE_STAFF_ID", input.getString("UPDATE_STAFF_ID"));
                agreemtnRel.put("UPDATE_DEPART_ID", input.getString("UPDATE_DEPART_ID"));
                agreemtnRel.put("VALID_TAG", "0");
                AgreementInfoBean.insertElecAgreementRel(agreemtnRel);
            }
        }

        IDataset archives_attrs = input.getDataset("ARCHIVES_ATTRS");
        if (IDataUtil.isNotEmpty(archives_attrs))// 属性
        {
            // 如果存在属性变更，将\属性的结束时间全部结束掉
            // 根据档案编码，全部结束掉
            IData attr = new DataMap();
            attr.put("END_DATE", SysDateMgr.getLastMonthLastDate());
            attr.put("ARCHIVES_ID", archives_id);
            AgreementInfoBean.updateElectronicAttrEnd(attr);

            IDataset params = new DatasetList();
            for (int i = 0; i < archives_attrs.size(); i++) {
                IData temp = archives_attrs.getData(i);
                String keyNames[] = temp.getNames();
                for (int key = 0; key < keyNames.length; key++) {
                    Object attr_value = temp.get(keyNames[key]);

                    if (attr_value instanceof IDataset) // 说明是子列表
                    {
                        IDataset attr_values = (DatasetList) attr_value;
                        for (int j = 0; j < attr_values.size(); j++) {
                            IData attr_valuesTemp = attr_values.getData(j);

                            String attr_Names[] = attr_valuesTemp.getNames();
                            for (int attr_key = 0; attr_key < attr_Names.length; attr_key++) {
                                IData archives_attr = new DataMap();

                                archives_attr.put("ATTR_CODE", attr_Names[attr_key]);
                                archives_attr.put("ATTR_VALUE", attr_valuesTemp.getString(attr_Names[attr_key]));
                                archives_attr.put("GROUP_INDEX", j);
                                archives_attr.put("ATTR_GROUP", keyNames[key]);
                                archives_attr.put("ARCHIVES_ID", archives_id);
                                archives_attr.put("START_DATE", sysStart_date);
                                archives_attr.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
                                // 新增一份新的
                                params.add(archives_attr);
                            }
                        }
                    } else // 说明是单个对象属性
                    {
                        IData paramattr = new DataMap();
                        paramattr.put("ATTR_CODE", keyNames[key]);
                        paramattr.put("ATTR_VALUE", temp.getString(keyNames[key]));
                        paramattr.put("ARCHIVES_ID", archives_id);
                        paramattr.put("ATTR_GROUP", "-1");//
                        paramattr.put("START_DATE", sysStart_date);
                        paramattr.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
                        // 新增一份新的
                        params.add(paramattr);
                    }
                }
            }
            if (params.size() > 0) {
                AgreementInfoBean.insertElectronicAttr(params);
            }
        }

        input.put("AGREEMENT_ID", archives_info.getString("AGREEMENT_ID"));
        results.add(input);
        return results;
    }

    /**
     * 创建电子档案库 将电子档案数据入库电子档案表 电子档案类型有 电子协议，集团客户有效证件，集团客户业务授权书，
     */
    public IDataset createElectronicArchive(IData input) throws Exception {

        IDataset resulet = new DatasetList();
        String archives_id = SeqMgr.getArchivesId();
        String sysStart_date = input.getString("UPDATE_TIME");

        String archives_name = input.getString("ARCHIVES_NAME", "");
        String archive_type = input.getString("ARCHIVES_TYPE");
        String sub_archive_type = input.getString("ELECTRONIC_TYPE_SUB", "");

        String start_date = input.getString("START_DATE", sysStart_date);

        String end_date = input.getString("END_DATE", SysDateMgr.END_DATE_FOREVER);

        String archives_state = input.getString("ARCHIVES_STATE", "1");
        String cust_id = input.getString("CUST_ID");

        IDataset archives_attrs = input.getDataset("ARCHIVES_ATTRS");
        IData archives_info = input.getData("ARCHIVES_INFO");

        IData param = new DataMap();
        param.put("ARCHIVES_ID", archives_id);
        param.put("ARCHIVES_NAME", archives_name);
        param.put("ARCHIVES_TYPE", archive_type);
        param.put("START_DATE", start_date);
        param.put("END_DATE", end_date);
        param.put("ARCHIVES_STATE", archives_state);
        param.put("SUB_ARCHIVES_TYPE", sub_archive_type);
        param.put("ARCHIVES_ATTACH", input.getString("ARCHIVES_ATTACH"));// 协议附件
        param.put("CREATE_TIME", sysStart_date);
        param.put("CREATE_STAFF_ID", input.getString("UPDATE_STAFF_ID"));
        param.put("CREATE_DEPART_ID", input.getString("UPDATE_DEPART_ID"));
        param.put("UPDATE_TIME", sysStart_date);
        param.put("UPDATE_STAFF_ID", input.getString("UPDATE_STAFF_ID"));
        param.put("UPDATE_DEPART_ID", input.getString("UPDATE_DEPART_ID"));

        if ("AGCONTENT".equals(sub_archive_type)) // 如果是电子协议类型且为正文
        {
            param.put("AGREEMENT_ID", archives_info.getString("AGREEMENT_ID"));
            param.put("A_NAME", archives_info.getString("A_NAME"));
            param.put("A_ADDRESS", archives_info.getString("A_ADDRESS"));
            param.put("A_HEADER", archives_info.getString("A_HEADER"));
            param.put("A_CONTACT_PHONE", archives_info.getString("A_CONTACT_PHONE"));
            param.put("A_BANK", archives_info.getString("A_BANK"));
            param.put("B_NAME", archives_info.getString("B_NAME"));
            param.put("B_ADDRESS", archives_info.getString("B_ADDRESS"));
            param.put("B_HEADER", archives_info.getString("B_HEADER"));
            param.put("B_CONTACT_PHONE", archives_info.getString("B_CONTACT_PHONE"));
            param.put("B_BANK", archives_info.getString("B_BANK"));
            param.put("B_BANK_ACCT_NO", archives_info.getString("B_BANK_ACCT_NO"));
            param.put("B_SIGN_DATE", archives_info.getString("B_SIGN_DATE"));
            param.put("B_SIGN_MAN", archives_info.getString("B_SIGN_MAN"));
            param.put("A_SIGN_MAN", archives_info.getString("A_SIGN_MAN"));
            param.put("A_SIGN_DATE", archives_info.getString("A_SIGN_DATE"));
            param.put("A_BANK_ACCT_NO", archives_info.getString("A_BANK_ACCT_NO"));
            param.put("PDF_FILE", archives_info.getString("PDF_FILE"));
            param.put("CONTRACT_CODE", archives_info.getString("CONTRACT_CODE"));
            param.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
            AgreementInfoBean.insertElectronicAgreement(param);
        } else {
            param.put("AGREEMENT_ID", archives_info.getString("AGREEMENT_ID"));
            param.put("PDF_FILE", archives_info.getString("PDF_FILE"));
            param.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
            AgreementInfoBean.insertElectronicAttach(param);
        }

        IDataset products = input.getDataset("PRODUCTS",new DatasetList());
        if ("AGCONTENT".equals(sub_archive_type)) // 如果是电子协议类型且为正文
        {
            //只有正文做关联
            for (int i = 0; i < products.size(); i++) {
                IData rel = new DataMap();
                rel.put("REL_ID", SeqMgr.getArchivesId());
                rel.put("CUST_ID", cust_id);
                rel.put("USER_ID", input.getString("USER_ID", "-1"));// 1客户,
                rel.put("ARCHIVES_ID", archives_id);
                rel.put("CREATE_TIME", sysStart_date);
                rel.put("CREATE_STAFF_ID", input.getString("UPDATE_STAFF_ID"));
                rel.put("CREATE_DEPART_ID", input.getString("UPDATE_DEPART_ID"));
                rel.put("UPDATE_TIME", sysStart_date);
                rel.put("UPDATE_STAFF_ID", input.getString("UPDATE_STAFF_ID"));
                rel.put("UPDATE_DEPART_ID", input.getString("UPDATE_DEPART_ID"));
                rel.put("PRODUCT_ID", String.valueOf(products.get(i)));
                rel.put("VALID_TAG", "0");
                AgreementInfoBean.insertElectronicRel(rel);
            }
        }
        if (IDataUtil.isNotEmpty(products)) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < products.size(); i++) {
                sb.append("," + String.valueOf(products.get(i)));
            }
            IData dataRel = new DataMap();
            dataRel.put("AGREEMENT_ID", archives_info.getString("AGREEMENT_ID"));
            IDataset agreemtnRels = AgreementInfoBean.qryElecAgreementRel(dataRel);
            if (IDataUtil.isNotEmpty(agreemtnRels)) {
                IData agreemtnRel = agreemtnRels.first();
                agreemtnRel.put("PRODUCT_ID", sb.substring(1));
                agreemtnRel.put("UPDATE_TIME", sysStart_date);
                agreemtnRel.put("UPDATE_STAFF_ID", input.getString("UPDATE_STAFF_ID"));
                agreemtnRel.put("UPDATE_DEPART_ID", input.getString("UPDATE_DEPART_ID"));
                agreemtnRel.put("VALID_TAG", "0");
                AgreementInfoBean.updadteElecAgreementRel(agreemtnRel);
            } else {
                IData agreemtnRel = new DataMap();
                agreemtnRel.put("REL_ID", SeqMgr.getArchivesId());
                agreemtnRel.put("AGREEMENT_ID", archives_info.getString("AGREEMENT_ID"));
                agreemtnRel.put("PRODUCT_ID", sb.substring(1));
                agreemtnRel.put("CREATE_TIME", sysStart_date);
                agreemtnRel.put("CREATE_STAFF_ID", input.getString("UPDATE_STAFF_ID"));
                agreemtnRel.put("CREATE_DEPART_ID", input.getString("UPDATE_DEPART_ID"));
                agreemtnRel.put("UPDATE_TIME", sysStart_date);
                agreemtnRel.put("UPDATE_STAFF_ID", input.getString("UPDATE_STAFF_ID"));
                agreemtnRel.put("UPDATE_DEPART_ID", input.getString("UPDATE_DEPART_ID"));
                agreemtnRel.put("VALID_TAG", "0");
                AgreementInfoBean.insertElecAgreementRel(agreemtnRel);
            }
        }

        if (IDataUtil.isNotEmpty(archives_attrs))// 属性
        {
            IDataset params = new DatasetList();
            for (int i = 0; i < archives_attrs.size(); i++) {

                IData temp = archives_attrs.getData(i);
                String keyNames[] = temp.getNames();
                for (int key = 0; key < keyNames.length; key++) {
                    Object attr_value = temp.get(keyNames[key]);

                    if (attr_value instanceof IDataset) // 说明是子列表
                    {
                        IDataset attr_values = (DatasetList) attr_value;
                        for (int j = 0; j < attr_values.size(); j++) {
                            IData attr_valuesTemp = attr_values.getData(j);

                            String attr_Names[] = attr_valuesTemp.getNames();
                            for (int attr_key = 0; attr_key < attr_Names.length; attr_key++) {
                                IData archives_attr = new DataMap();

                                archives_attr.put("ATTR_CODE", attr_Names[attr_key]);
                                archives_attr.put("ATTR_VALUE", attr_valuesTemp.getString(attr_Names[attr_key]));
                                archives_attr.put("GROUP_INDEX", j);
                                archives_attr.put("ATTR_GROUP", keyNames[key]);
                                archives_attr.put("ARCHIVES_ID", archives_id);
                                archives_attr.put("START_DATE", sysStart_date);
                                archives_attr.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
                                params.add(archives_attr);
                            }

                        }
                    } else // 说明是单个对象属性
                    {
                        IData paramattr = new DataMap();
                        paramattr.put("ATTR_CODE", keyNames[key]);
                        paramattr.put("ATTR_VALUE", temp.getString(keyNames[key]));
                        paramattr.put("ARCHIVES_ID", archives_id);
                        paramattr.put("ATTR_GROUP", "-1");//
                        paramattr.put("START_DATE", sysStart_date);
                        paramattr.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
                        params.add(paramattr);
                    }
                }
            }
            if (params.size() > 0) {
                AgreementInfoBean.insertElectronicAttr(params);
            }
        }
        AgreementInfoBean.insertElectronicArchives(param);
        resulet.add(param);
        return resulet;
    }

    public IData insertLog(IData input)  throws Exception{
        IDataset dataset = input.getDataset("LOG_LIST");
        for(Object obj:dataset){
            IData data = (IData)obj;
            data.put("LOG_ID",SeqMgr.getArchivesId());
        }
        AgreementInfoBean.insertLog(dataset);
        return new DataMap();
    }

    public IData getAgreementId(IData param) throws Exception{
        return AgreementInfoBean.getAgreementId();
    }

    public IData checkAgreementBpm(IData data) throws Exception{

        IData result = new DataMap();
        String agreementId = data.getString("AGREEMENT_ID");
        String archives_id = data.getString("ARCHIVES_ID");
        String ibsysid = data.getString("IBSYSID","");

        IData input = new DataMap();
        input.put("AGREEMENT_ID",agreementId);
        input.put("VALID_TAG","0");
        IDataset agreementRels = AgreementInfoBean.qryElecAgreementRel(input);
        if(StringUtils.isBlank(archives_id)){//新增协议
            if(DataUtils.isEmpty(agreementRels)){
                result.put("RESULT","true");
            }else{
                result.put("RESULT","false");
                result.put("INFO","存在未归档主体协议或变更申请单，无法再次新增");
            }
        }else{//修改协议
            if(DataUtils.isEmpty(agreementRels)){
                result.put("RESULT","false");
                result.put("INFO","协议已归档，无法修改");
            }else{
                String relIbsysid = agreementRels.first().getString("RSRV_STR1");
                if(StringUtils.isBlank(relIbsysid)){//协议不在流程中
                    result.put("RESULT","true");
                }else{
                    if(ibsysid.equals(relIbsysid)){
                        result.put("RESULT","true");
                    }else{
                        result.put("RESULT","false");
                        result.put("INFO","协议存在流程中且未归档,请待协议归档后再做变更，流程订单号："+relIbsysid);
                    }
                }
            }
        }
        return result;
    }

    public IDataset getAgreementUser(IData input) throws Exception{
        if(StringUtils.isBlank(input.getString("AGREEMENT_ID"))){
            return new DatasetList();
        }

        return AgreementInfoBean.getAgreementUser(input);

    }

    public IData queryAgreementInfoAndProductInfo(IData input) throws Exception {
        String agreementID = input.getString("AGREEMENT_ID");
        IData data = new DataMap();
        if(StringUtils.isBlank(agreementID)){
            return data;
        }

        //主体协议
        IDataset agreementInfos = AgreementInfoBean.queryAgArchivesByAgreementId(input);
        if(DataUtils.isNotEmpty(agreementInfos)){
            IData agreementInfo = agreementInfos.first();

            IData agParam = new DataMap();
            agParam.put("ARCHIVES_ID",agreementInfo.getString("ARCHIVES_ID"));
            IData archivesData = AgreementInfoBean.queryArchivesState(agParam);

            String fileList = "";
            if(DataUtils.isNotEmpty(archivesData)){
                fileList = archivesData.getString("ARCHIVES_ATTACH");

                agreementInfo.put("ARCHIVES_STATE_NAME",StaticUtil.getStaticValue("ELECSTATE",archivesData.getString("ARCHIVES_STATE")));
                agreementInfo.put("ARCHIVES_STATE",archivesData.getString("ARCHIVES_STATE"));
            }
            if(StringUtils.isNotBlank(fileList)){
                agreementInfo.put("ARCHIVES_ATTACH",new DatasetList(fileList));
            }
            data.put("AGREEMENT_INFO",agreementInfo);

            //产品信息
            IDataset productList = AgreementInfoBean.getAgreementProductInfos(agParam);
            if(DataUtils.isNotEmpty(productList)){
                Iterator<Object> it = productList.iterator();
                while(it.hasNext()){
                    IData productData = (IData)it.next();
                    String productId = productData.getString("PRODUCT_ID");
                    if("-1".equals(productId)){
                        it.remove();
                    }
                    productData.put("START_DATE",agreementInfo.getString("START_DATE"));
                    productData.put("END_DATE",agreementInfo.getString("END_DATE"));
                    if(StringUtils.isNotBlank(productId)){
                        productData.put("PRODUCT_NAME",UProductInfoQry.getProductNameByProductId(productId));
                    }
                }
                data.put("PRODUCT_LIST",productList);
            }
        }

        //附加协议
        IDataset agreementAttachInfos = AgreementInfoBean.queryElectronicAgreAttach(input);
        if(DataUtils.isNotEmpty(agreementAttachInfos)){
            for(int i = 0;i<agreementAttachInfos.size();i++){
                IData agreementAttachInfo = agreementAttachInfos.getData(i);
                IData agParam = new DataMap();
                agParam.put("ARCHIVES_ID",agreementAttachInfo.getString("ARCHIVES_ID"));
                IData archivesData = AgreementInfoBean.queryArchivesState(agParam);

                agreementAttachInfo.putAll(archivesData);

                String fileList = "";
                if(DataUtils.isNotEmpty(archivesData)){
                    fileList = archivesData.getString("ARCHIVES_ATTACH");

                    agreementAttachInfo.put("ARCHIVES_STATE_NAME",StaticUtil.getStaticValue("ELECSTATE",archivesData.getString("ARCHIVES_STATE")));
                }
                if(StringUtils.isNotBlank(fileList)){
                    agreementAttachInfo.put("ARCHIVES_ATTACH",new DatasetList(fileList));
                }

            }
            data.put("AGREEMENT_ATTACH_INFO",agreementAttachInfos);
        }

        return data;
    }

    /**
     ** 电子档案统计 根据时间段查询出有效档案
     * @param input
     * @return IDataset
     * @Date 2019年12月23日
     * @author xieqj
     */
    public IDataset elecAgreementSumReportForm(IData input) throws Exception {
        return AgreementInfoBean.elecAgreementSumReportForm(input, getPagination());
    }

}
