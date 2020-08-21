package com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.hotelcontract;

import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.frame.bcf.util.SysDateMgr4Web;
import com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.base.ElecLineProtocolBase;
import com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.utils.ElecLineUtil;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import org.apache.tapestry.IRequestCycle;

import java.util.Date;

public abstract class HotelChangeContract extends ElecLineProtocolBase {

    private final static String AGREEMENT_DEF_ID = "HotelChangeContract";

    private final static String B_NAME = "中国移动通信集团海南有限公司";

    private final static int prex = 3;

    private final static int DATE_LENGTH = 10;

    @Override
    public void initPage(IRequestCycle cycle) throws Exception {

        //转换移动端与PC端key值

        super.initPage(cycle);
    }

    protected void initalPageValue(IData protocolInfo, IData accessoryInfo, IData pageData) throws Exception{
        if("N".equals(pageData.getString("SUBMITTYPE"))){
            IData groupInfo = protocolInfo.getData("CUST_INFO",new DataMap());
            IData managerInfo = protocolInfo.getData("CUST_MRG_INFO",new DataMap());

            protocolInfo.put("A_NAME", groupInfo.getString("CUST_NAME"));
            protocolInfo.put("B_NAME", B_NAME);
            accessoryInfo.put("GROUP_NAME", groupInfo.getString("CUST_NAME"));
            accessoryInfo.put("A_NAME_2",groupInfo.getString("CUST_NAME"));
            accessoryInfo.put("GROUP_ID",groupInfo.getString("GROUP_ID"));
            accessoryInfo.put("GROUP_ADDR",groupInfo.getString("GROUP_ADDR"));
            accessoryInfo.put("GROUP_PHONE",groupInfo.getString("GROUP_CONTACT_PHONE"));

            protocolInfo.put("PRODUCT_ID", pageData.getString("PRODUCT_ID"));
            accessoryInfo.put("AGREEMENT_DEF_ID",AGREEMENT_DEF_ID);

            String sysTime = SysDateMgr4Web.getSysDate("yyyy年MM月dd日");
            protocolInfo.put("A_SIGN_DATE", sysTime);
            protocolInfo.put("B_SIGN_DATE", sysTime);

            accessoryInfo.put("BEGIN_DATE",SysDateMgr4Web.getSysDate(SysDateMgr4Web.PATTERN_STAND));
            accessoryInfo.put("END_DATE",SysDateMgr4Web.END_DATE_FOREVER);
        }else{

            protocolInfo.put("A_SIGN_DATE", accessoryInfo.getString("SYS_DATE_A"));
            protocolInfo.put("B_SIGN_DATE", accessoryInfo.getString("SYS_DATE_B"));
        }
        accessoryInfo.put("B_NAME_2",B_NAME);

        if((StringUtils.isBlank(accessoryInfo.getString("BEGIN_DATE"))||StringUtils.isBlank(accessoryInfo.getString("END_DATE")))&&StringUtils.isNotBlank(pageData.getString("AGREEMENT_ID"))){
            //查询开始和结束日期
            IData input = new DataMap();
            input.put("AGREEMENT_ID",pageData.getString("AGREEMENT_ID"));
            IDataset agreementInfos = CSViewCall.call(this,"SS.AgreementInfoSVC.queryElectronicAgreementInfo",input);
            if(DataUtils.isEmpty(agreementInfos)){
                CSViewException.apperr(CrmCommException.CRM_COMM_103,"未查询到协议信息！");
            }
            accessoryInfo.put("BEGIN_DATE", agreementInfos.first().getString("START_DATE"));
            accessoryInfo.put("END_DATE", agreementInfos.first().getString("END_DATE"));

        }

    }

    protected IData buildFDFInfo(IData pageData) throws Exception{
        //转换KEY值
        changeKey(pageData);

        IData contextData = buildProtocolInfo(pageData);
        return contextData;
    }

    private void changeKey(IData pageData) throws Exception{
        String[] keys = pageData.getNames();
        for(String key:keys){
            String value = pageData.getString(key);
            if(key != null &&(key.startsWith("pc_")||key.startsWith("ph_"))){
                String newKey = key.substring(prex);
                pageData.remove(key);
                pageData.put(newKey, value);
            }
        }

    }

    protected IData buildProtocolInfo(IData pageData) throws Exception{
        IData archivesInfo = new DataMap();

        //转换单选框
        transChangeValueForPDF(pageData);

        IDataset archiveslist = new DatasetList();

        String aSignDate = pageData.getString("BEGIN_DATE");
        String bSignDate = pageData.getString("END_DATE");

        if(StringUtils.isNotEmpty(aSignDate)){
            ElecLineUtil.setPDFDateField(aSignDate, pageData, "BEGIN_YEAR", "BEGIN_MONTH", "BEGIN_DAY");
        }
        if(StringUtils.isNotEmpty(bSignDate)){
            ElecLineUtil.setPDFDateField(bSignDate, pageData, "END_YEAR", "END_MONTH", "END_DAY");
        }

        IData input = new DataMap();
        input.put("AGREEMENT_DEF_ID", pageData.getString("AGREEMENT_DEF_ID"));
        IDataset relaList = CSViewCall.call(this, "SS.AgreementInfoSVC.queryElecTemplateRele", input);
        for(int i= 0;i<relaList.size();i++){
            IData relaData = relaList.getData(i);
            String attrCode = relaData.getString("PDF_ELE_CODE");
            String attrValue = pageData.getString(attrCode);
            if(StringUtils.isNotBlank(attrValue)){
                archivesInfo.put(attrCode, attrValue);
            }else if("0".equals(relaData.getString("PDF_ELE_TYPE"))){
                archivesInfo.put(attrCode, ElecLineUtil.DUFUALT_VALUE);
            }
        }

        archivesInfo.put("AGREEMENT_ID",pageData.getString("AGREEMENT_ID"));
        archivesInfo.put("A_NAME",pageData.getString("A_NAME"));
        archivesInfo.put("B_NAME",B_NAME);
        archivesInfo.put("A_NAME2",pageData.getString("A_NAME_2"));
        archivesInfo.put("A_HEADER",pageData.getString("CONTACTS_A"));
        archivesInfo.put("B_HEADER",pageData.getString("CONTACTS_B"));
        archivesInfo.put("DATE_1", pageData.getString("SYS_DATE_A"));

        archivesInfo.put("DTAE_2", pageData.getString("SYS_DATE_B"));


        for (String key : archivesInfo.getNames()) {
            IData tempData = new DataMap();
            if(StringUtils.isNotEmpty(archivesInfo.getString(key))){
                tempData.put(key, archivesInfo.getString(key));
                archiveslist.add(tempData);
            }
        }
        IData datas = new DataMap();
        datas.put("DATAS", archiveslist);
        return datas;
    }

    private void transChangeValueForPDF(IData pageData) throws Exception{
        //变更方式
        String dealType = pageData.getString("DEAL_TYPE");
        String operType = pageData.getString("OPER_TYPE");

        boolean flag = false;
        if("1".equals(dealType)){
            if(StringUtils.isNotBlank(operType)&&!BizCtrlType.MinorecCreateUser.equals(operType)){
                flag = true;
            }
            pageData.put("ADD", "1");
        }else if("2".equals(dealType)){
            if(StringUtils.isNotBlank(operType)&&(BizCtrlType.MinorecDestroyUser.equals(operType)||BizCtrlType.MinorecCreateUser.equals(operType))){
                flag = true;
            }
            pageData.put("CHANGE", "1");
        }else if("3".equals(dealType)){
            if(StringUtils.isNotBlank(operType)&&!BizCtrlType.MinorecDestroyUser.equals(operType)){
                flag = true;
            }
            pageData.put("DESTROY", "1");
        }

        if(flag){
            CSViewException.apperr(CrmCommException.CRM_COMM_103,"您选择的变更方式与协议不一致，请重新选择！");
        }

        //是否开通豪华定制包
        String TVpkg = pageData.getString("TV_PKG");
        if("2".equals(TVpkg)){
            pageData.put("TV_PKG_YES", "1");
        }else if("1".equals(TVpkg)){
            pageData.put("TV_PKG_NO", "1");
        }

        //证件类型
        String cardType = pageData.getString("CARD_TYPE");
        if("1".equals(cardType)){
            pageData.put("ID_CARD", "1");//身份证
        }else if("2".equals(cardType)){
            pageData.put("OTHER_CARD", "1");//其他证件
        }

        //云酒馆开通业务版本
        String CWOpenType = pageData.getString("CW_OPEN_TYPE");
        if("1".equals(CWOpenType)){
            pageData.put("CW_STANDARD", "1");
        }else if("2".equals(CWOpenType)){
            pageData.put("CW_ELIT", "1");
        }else if("3".equals(CWOpenType)){
            pageData.put("CW_LUXURY", "1");
        }

        if("1".equals(pageData.getString("BUSINESS_TV"))){//和商务TV
            pageData.put("BUSINESS_TV_1", "1");
        }
        if("1".equals(pageData.getString("EPS_BBD"))){//企业宽带
            pageData.put("EPS_BBD_1", "1");
        }
        if("1".equals(pageData.getString("CON_CNT"))){//融合通信
            pageData.put("CON_CNT_1", "1");
        }
        if("1".equals(pageData.getString("CLOUD_WINE"))){//云酒管
            pageData.put("CLOUD_WINE_1", "1");
        }
        if("1".equals(pageData.getString("CLOUD_WIFI"))){//云WiFi安审版
            pageData.put("CLOUD_WIFI_1", "1");
        }
    }

    public IData saveProtocolInfo(IData pageData) throws Exception {

        IData archives = new DataMap();

        ElecLineUtil.setCommInfoField(archives,pageData,getVisit());

        IData archivesInfo = buildSaveProtocolInfo(pageData);

        archives.put("ARCHIVES_INFO", archivesInfo);

        IDataset archivesAttrs = ElecLineUtil.buildSaveAccessoryInfo(pageData);

        IDataset products = new DatasetList();
        archivesAttrs = buildSaveAccessoryInfo(archivesAttrs,pageData,products);
        archives.put("ARCHIVES_ATTRS", archivesAttrs);

        if(DataUtils.isEmpty(products)){
            CSViewException.apperr(CrmCommException.CRM_COMM_103,"所选产品为空，请选择产品后再提交！");
        }

        archives.put("PRODUCTS", products);

        //协议开始结束时间
        String aSignDate = pageData.getString("BEGIN_DATE");
        String bSignDate = pageData.getString("END_DATE");

        Date endDate = null;
        if(StringUtils.isNotEmpty(aSignDate)){
            archives.put("START_DATE", SysDateMgr4Web.date2String(SysDateMgr4Web.string2Date(aSignDate, "yyyy年MM月dd日"),SysDateMgr4Web.PATTERN_STAND));
        }

        //取正文结束时间
        Date endDate2 = null;
        String agreementId = pageData.getString("AGREEMENT_ID");
        IData input = new DataMap();
        input.put("AGREEMENT_ID", agreementId);
        IDataset agcontentDatas = CSViewCall.call(this, "SS.AgreementInfoSVC.queryAgArchivesByAgreementId", input);

        if(DataUtils.isEmpty(agcontentDatas)){
            CSViewException.apperr(CrmCommException.CRM_COMM_103,"未查询到合同正文信息，请先添加正文信息！");
        }else{
            pageData.put("AGREEMENT_ARCHIVE_NAME", agcontentDatas.first().getString("ARCHIVES_NAME"));
            String endData = agcontentDatas.first().getString("END_DATE");
            if(StringUtils.isNotBlank(endData)){
                endDate2 = SysDateMgr4Web.string2Date(endData.substring(0, DATE_LENGTH), SysDateMgr4Web.PATTERN_STAND_YYYYMMDD);
            }
        }

        if(StringUtils.isNotEmpty(bSignDate)){
            aSignDate = SysDateMgr4Web.date2String(SysDateMgr4Web.string2Date(aSignDate, "yyyy年MM月dd日"),SysDateMgr4Web.PATTERN_STAND);

            Date endDate1 = SysDateMgr4Web.string2Date(bSignDate, "yyyy年MM月dd日");
            endDate = endDate1;
            //取大的作为结束时间
            if(endDate2 != null){
                int i = endDate2.compareTo(endDate1);
                if(i >= 0){
                    endDate = endDate2;
                }else{
                    endDate = endDate1;
                }
            }

        }else{
            endDate = endDate2;
        }

        if(endDate != null){
            archives.put("END_DATE", SysDateMgr4Web.date2String(endDate, SysDateMgr4Web.PATTERN_STAND));
        }

        //协议状态
        archives.put("ARCHIVES_STATE", "0");//待审核

        return archives;
    }

    private IData buildSaveProtocolInfo(IData pageData) throws Exception{
        IData archivesInfo = new DataMap();
        archivesInfo.put("AGREEMENT_ID", pageData.getString("AGREEMENT_ID"));
        archivesInfo.put("PDF_FILE", pageData.getDataset("PDF_FILE"));
        archivesInfo.put("CONTRACT_CODE", pageData.getString("CONTRACT_CODE"));
        return archivesInfo;
    }

    private IDataset buildSaveAccessoryInfo(IDataset archivesAttrs,IData pageData,IDataset products) throws Exception{

        IData data = new DataMap();
        data.put("AGREEMENT_ID", pageData.getString("AGREEMENT_ID"));
        data.put("VALID_TAG", "0");//有效标记
        IDataset AgrementInfos = CSViewCall.call(this, "SS.AgreementInfoSVC.queryElecRELByAgreementId", data);
        //新增协议时做校验
        if(DataUtils.isNotEmpty(AgrementInfos) && !"U".equals(pageData.getString("SUBMITTYPE"))){
            CSViewException.apperr(CrmCommException.CRM_COMM_103,"协议存在未归档正文或变更单,无法添加新的变更单！");
        }
	    /*if(!"3".equals(AgrementInfos.first().getString("ARCHIVES_STATE"))){
            CSViewException.apperr(CrmCommException.CRM_COMM_103,"协议正文未归档，请直接修改正文信息！");
        }*/

        IData input = new DataMap();
        input.put("AGREEMENT_DEF_ID", pageData.getString("AGREEMENT_DEF_ID"));
        IDataset relaList = CSViewCall.call(this, "SS.AgreementInfoSVC.queryElecTemplateRele", input);
        for(int i= 0;i<relaList.size();i++){

            IData archivesInfo = new DataMap();
            IData relaData = relaList.getData(i);
            String attrCode = relaData.getString("PDF_ELE_CODE");
            String attrValue = pageData.getString(attrCode);
            if(StringUtils.isBlank(attrValue)){
                continue;
            }
            archivesInfo.put(attrCode, attrValue);
            archivesAttrs.add(archivesInfo);

            IData productInfo = new DataMap();
            if("CON_CNT".equals(attrCode) && "1".equals(attrValue)){//融合通信
                productInfo.put("PRODUCT_ID", "2222");
                //productInfo.put("PRODUCT_ID", "VP998001");
            }else if("EPS_BBD".equals(attrCode)&&"1".equals(attrValue)){//企业宽带
                productInfo.put("PRODUCT_ID", "7341");
            }else if("BUSINESS_TV".equals(attrCode)&&"1".equals(attrValue)){//和商务TV
                productInfo.put("PRODUCT_ID", "380700");
            }else if("SD_WAN".equals(attrCode)&&"1".equals(attrValue)){//SD-WAN
                //productInfo.put("PRODUCT_ID", "");
            }else if("CLOUD_WINE".equals(attrCode)&&"1".equals(attrValue)){//云酒管
                productInfo.put("PRODUCT_ID", "921015");
            }else if("CLOUD_WIFI".equals(attrCode) &&"1".equals(attrValue)){//云WIFI安审版
                productInfo.put("PRODUCT_ID", "380300");
            }else if("CC_VNET".equals(attrCode)&&"1".equals(attrValue)){//融合V网
                productInfo.put("PRODUCT_ID", "8001");
            }/*else if("CC_CRBT".equals(attrCode)&&"1".equals(attrValue)){//多媒体彩铃
                productInfo.put("PRODUCT_ID", "2222");
            }*/
            if(DataUtils.isNotEmpty(productInfo)){
                archivesAttrs.add(productInfo);
                products.add(productInfo.getString("PRODUCT_ID"));
            }
        }

        return archivesAttrs;
    }

    public IData updateProtocolInfo(IData pageData) throws Exception {
        // TODO Auto-generated method stub
        IData archives = new DataMap();
        archives.put("ARCHIVES_ID", pageData.getString("ARCHIVES_ID"));

        ElecLineUtil.setCommInfoField(archives,pageData,getVisit());

        IData archivesInfo = buildSaveProtocolInfo(pageData);

        archives.put("ARCHIVES_INFO", archivesInfo);

        IDataset archivesAttrs = ElecLineUtil.buildSaveAccessoryInfo(pageData);

        IDataset products = new DatasetList();
        archivesAttrs = buildSaveAccessoryInfo(archivesAttrs,pageData,products);
        archives.put("ARCHIVES_ATTRS", archivesAttrs);

        if(DataUtils.isEmpty(products)){
            CSViewException.apperr(CrmCommException.CRM_COMM_103,"所选产品为空，请选择产品后再提交！");
        }

        archives.put("PRODUCTS", products);

        //协议开始结束时间
        String aSignDate = pageData.getString("BEGIN_DATE");
        String bSignDate = pageData.getString("END_DATE");

        Date endDate = null;
        if(StringUtils.isNotEmpty(aSignDate)){
            archives.put("START_DATE", SysDateMgr4Web.date2String(SysDateMgr4Web.string2Date(aSignDate, "yyyy年MM月dd日"),SysDateMgr4Web.PATTERN_STAND));
        }

        //取正文结束时间
        Date endDate2 = null;
        String agreementId = pageData.getString("AGREEMENT_ID");
        IData input = new DataMap();
        input.put("AGREEMENT_ID", agreementId);
        IDataset agcontentDatas = CSViewCall.call(this, "SS.AgreementInfoSVC.queryAgArchivesByAgreementId", input);

        if(DataUtils.isEmpty(agcontentDatas)){
            CSViewException.apperr(CrmCommException.CRM_COMM_103,"未查询到合同正文信息，请先添加正文信息！");
        }else{

            pageData.put("AGREEMENT_ARCHIVE_NAME", agcontentDatas.first().getString("ARCHIVES_NAME"));
            String endData = agcontentDatas.first().getString("END_DATE");
            if(StringUtils.isNotBlank(endData)){
                endDate2 = SysDateMgr4Web.string2Date(endData.substring(0, DATE_LENGTH), SysDateMgr4Web.PATTERN_STAND_YYYYMMDD);
            }
        }

        if(StringUtils.isNotEmpty(bSignDate)){
            aSignDate = SysDateMgr4Web.date2String(SysDateMgr4Web.string2Date(aSignDate, "yyyy年MM月dd日"),SysDateMgr4Web.PATTERN_STAND);

            Date endDate1 = SysDateMgr4Web.string2Date(bSignDate, "yyyy年MM月dd日");
            endDate = endDate1;
            //取大的作为结束时间
            if(endDate2 != null){
                int i = endDate2.compareTo(endDate1);
                if(i >= 0){
                    endDate = endDate2;
                }else{
                    endDate = endDate1;
                }
            }

        }else{
            endDate = endDate2;
        }

        if(endDate != null){
            archives.put("END_DATE", SysDateMgr4Web.date2String(endDate, SysDateMgr4Web.PATTERN_STAND));
        }

        return archives;
    }

    @Override
    protected void setBackValue(IData resultData, IData pageData) throws Exception {
        super.setBackValue(resultData, pageData);
        //正文名称
        resultData.put("AGREEMENT_ARCHIVE_NAME", pageData.getString("AGREEMENT_ARCHIVE_NAME"));
    }

    @Override
    public void onSubmit(IRequestCycle cycle) throws Exception {
        checkedParam(getData());

        super.onSubmit(cycle);
    }

    public void checkedParam(IData pageData) throws Exception{
        if(DataUtils.isEmpty(pageData)){
            CSViewException.apperr(CrmCommException.CRM_COMM_103,"提交参数不能为空！");
        }
        String[] names = pageData.getNames();
        String productIn = pageData.getString("PRODUCT_ID");
        StringBuilder products = new StringBuilder();
        for(String name : names){
            if(("pc_CON_CNT".equals(name)||"ph_CON_CNT".equals(name)) && "1".equals(pageData.get(name))){
                products.append("VP998001,");
            }else if(("pc_EPS_BBD".equals(name)||"ph_EPS_BBD".equals(name)) && "1".equals(pageData.get(name))){
                products.append("7341,");
            }else if(("pc_BUSINESS_TV".equals(name)||"ph_BUSINESS_TV".equals(name)) && "1".equals(pageData.get(name))){
                products.append("380700,");
            }else if(("pc_CLOUD_WINE".equals(name)||"ph_CLOUD_WINE".equals(name)) && "1".equals(pageData.get(name))){
                products.append("921015,");
            }else if(("pc_CLOUD_WIFI".equals(name)||"ph_CLOUD_WIFI".equals(name)) && "1".equals(pageData.get(name))){
                products.append("380300,");
            }
        }

        String productstr = products.toString();
        if(StringUtils.isEmpty(productstr)){
            CSViewException.apperr(CrmCommException.CRM_COMM_103,"所选产品不正确，请修改后再提交！");
        }

        String[] product = productstr.split(",");
        if("VP99999".equals(productIn)){
            for(String p : product){
                if("380700".equals(p)||"921015".equals(p)||"380300".equals(p)){
                    CSViewException.apperr(CrmCommException.CRM_COMM_103,"所选产品不正确，请修改后再提交！");
                }
            }
        }else if("VP998001".equals(productIn)||"7341".equals(productIn)||"380700".equals(productIn)||"921015".equals(productIn)||"380300".equals(productIn)){
            if(product.length !=1 || !product[0].equals(productIn)){
                CSViewException.apperr(CrmCommException.CRM_COMM_103,"所选产品不正确，请修改后再提交！");
            }
        }
    }

}
