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
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class ConfiAttach extends ElecLineProtocolBase {
    
    private final static String AGREEMENT_DEF_ID = "ConfiAttach";
    
    private final static String B_NAME = "中国移动通信集团海南有限公司";

    private final static int prex = 3;

	protected void initalPageValue(IData protocolInfo, IData accessoryInfo, IData pageData) throws Exception{
		if("N".equals(pageData.getString("SUBMITTYPE"))){
            IData groupInfo = protocolInfo.getData("CUST_INFO",new DataMap());
            IData managerInfo = protocolInfo.getData("CUST_MRG_INFO",new DataMap());

			protocolInfo.put("CUST_FULL_NAME", groupInfo.getString("CUST_NAME"));
			protocolInfo.put("B_NAME", B_NAME);
			accessoryInfo.put("B_NAME2",B_NAME);
			accessoryInfo.put("AGREEMENT_DEF_ID",AGREEMENT_DEF_ID);
			accessoryInfo.put("DATE",SysDateMgr4Web.getSysDate("yyyy年MM月dd日"));
			accessoryInfo.put("CUST_ADDRESS",groupInfo.getString("GROUP_ADDR"));
			accessoryInfo.put("CUST_PHONE_PERSON",groupInfo.getString("JURISTIC_NAME"));
			accessoryInfo.put("CORPORATION",groupInfo.getString("JURISTIC_NAME"));
			accessoryInfo.put("CUST_PHONE",groupInfo.getString("GROUP_CONTACT_PHONE"));
            accessoryInfo.put("CUST_FULL_NAME", groupInfo.getString("CUST_NAME"));
        }else{
            if(StringUtils.isBlank(accessoryInfo.getString("DATE"))){
                accessoryInfo.put("DATE",SysDateMgr4Web.getSysDate("yyyy年MM月dd日"));
            }
        }

    }

    protected IData buildFDFInfo(IData pageData) throws Exception{
        changeKey(pageData);
        IData contextData = buildProtocolInfo(pageData);
        return contextData;
    }

	protected IData buildProtocolInfo(IData pageData) throws Exception{
	    
	    
		IData archivesInfo = new DataMap();
		IDataset archiveslist = new DatasetList();

		archivesInfo.put("AGREEMENT_ID",pageData.getString("AGREEMENT_ID"));
		archivesInfo.put("CUST_FULL_NAME",pageData.getString("CUST_FULL_NAME",ElecLineUtil.DUFUALT_VALUE));
		archivesInfo.put("B_NAME",B_NAME);
		archivesInfo.put("A_HEADER",pageData.getString("A_HEADER",ElecLineUtil.DUFUALT_VALUE));
		archivesInfo.put("B_HEADER",pageData.getString("B_HEADER",ElecLineUtil.DUFUALT_VALUE));

		archivesInfo.put("CORPORATION",pageData.getString("CORPORATION",ElecLineUtil.DUFUALT_VALUE));
		archivesInfo.put("CUST_ADDRESS",pageData.getString("CUST_ADDRESS",ElecLineUtil.DUFUALT_VALUE));
		archivesInfo.put("CUST_PHONE_PERSON",pageData.getString("CUST_PHONE_PERSON",ElecLineUtil.DUFUALT_VALUE));
		archivesInfo.put("CUST_PHONE",pageData.getString("CUST_PHONE",ElecLineUtil.DUFUALT_VALUE));
		archivesInfo.put("DATE",pageData.getString("DATE",ElecLineUtil.DUFUALT_VALUE));
		
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

	public IData saveProtocolInfo(IData pageData) throws Exception {
        // TODO Auto-generated method stub
        IData archives = new DataMap();

        ElecLineUtil.setCommInfoField(archives,pageData,getVisit());

		IData archivesInfo = ElecLineUtil.buildSaveProtocolInfo(pageData);
		setProtocolInfo(archivesInfo,pageData);
		archives.put("ARCHIVES_INFO", archivesInfo);

		IDataset archivesAttrs = ElecLineUtil.buildSaveAccessoryInfo(pageData);
		archivesAttrs = buildSaveAccessoryInfo(archivesAttrs,pageData);
		archives.put("ARCHIVES_ATTRS", archivesAttrs);
		
		//取开始结束时间
		String agreementId = pageData.getString("AGREEMENT_ID");
        IData input = new DataMap();
        input.put("AGREEMENT_ID", agreementId);
        IDataset agcontentDatas = CSViewCall.call(this, "SS.AgreementInfoSVC.queryAgArchivesByAgreementId", input);
        
        if(DataUtils.isEmpty(agcontentDatas)){
            CSViewException.apperr(CrmCommException.CRM_COMM_103,"未查询到合同正文信息，请先添加正文信息！");
        }else{
            archives.put("START_DATE", agcontentDatas.first().getString("START_DATE"));
            archives.put("END_DATE", agcontentDatas.first().getString("END_DATE"));
        }
        
        //协议状态
        archives.put("ARCHIVES_STATE", "0");//待审核

        return archives;
    }
	
	private IDataset buildSaveAccessoryInfo(IDataset archivesAttrs,IData pageData) throws Exception{
        
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
        }
        
        return archivesAttrs;
    }
	
	private void setProtocolInfo(IData archivesInfo,IData pageData) throws Exception{
	    
	    archivesInfo.put("A_NAME", pageData.getString("CUST_NAME"));
	    archivesInfo.put("B_NAME", B_NAME);
	    archivesInfo.put("A_ADDRESS", pageData.getString("A_ADDRESS"));
	    archivesInfo.put("B_ADDRESS", pageData.getString("B_ADDRESS"));
        archivesInfo.put("A_HEADER", pageData.getString("A_HEADER"));
        archivesInfo.put("B_HEADER", pageData.getString("B_HEADER"));
        archivesInfo.put("A_CONTACT_PHONE", pageData.getString("A_CONTACT_PHONE"));
        archivesInfo.put("B_CONTACT_PHONE", pageData.getString("B_CONTACT_PHONE"));
        archivesInfo.put("A_BANK", pageData.getString("A_BANK"));
        archivesInfo.put("B_BANK", pageData.getString("B_BANK"));
        archivesInfo.put("A_SIGN_DATE", SysDateMgr4Web.getSysDate());
        archivesInfo.put("B_SIGN_DATE", SysDateMgr4Web.getSysDate());
	}


	public IData updateProtocolInfo(IData pageData) throws Exception {
		// TODO Auto-generated method stub
		IData archives = new DataMap();
		archives.put("ARCHIVES_ID", pageData.getString("ARCHIVES_ID"));

		ElecLineUtil.setCommInfoField(archives,pageData,getVisit());

        IData archivesInfo = ElecLineUtil.buildSaveProtocolInfo(pageData);
        setProtocolInfo(archivesInfo,pageData);
        archives.put("ARCHIVES_INFO", archivesInfo);

        IDataset archivesAttrs = ElecLineUtil.buildSaveAccessoryInfo(pageData);
        archivesAttrs = buildSaveAccessoryInfo(archivesAttrs,pageData);
        archives.put("ARCHIVES_ATTRS", archivesAttrs);
        
        //取开始结束时间
        String agreementId = pageData.getString("AGREEMENT_ID");
        IData input = new DataMap();
        input.put("AGREEMENT_ID", agreementId);
        IDataset agcontentDatas = CSViewCall.call(this, "SS.AgreementInfoSVC.queryAgArchivesByAgreementId", input);
        
        if(DataUtils.isEmpty(agcontentDatas)){
            CSViewException.apperr(CrmCommException.CRM_COMM_103,"未查询到合同正文信息，请先添加正文信息！");
        }else{
            archives.put("START_DATE", agcontentDatas.first().getString("START_DATE"));
            archives.put("END_DATE", agcontentDatas.first().getString("END_DATE"));
        }
        
        //协议状态
        archives.put("ARCHIVES_STATE", "0");//待审核

        return archives;
	}

}
