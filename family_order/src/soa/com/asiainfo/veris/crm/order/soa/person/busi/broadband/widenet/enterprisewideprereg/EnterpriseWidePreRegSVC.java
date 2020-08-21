package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.enterprisewideprereg;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

public class EnterpriseWidePreRegSVC extends CSBizService
{
	protected static Logger log = Logger.getLogger(EnterpriseWidePreRegSVC.class);

    private static final long serialVersionUID = 1L;
	
    //企业宽带预约接口
    public IData book(IData input) throws Exception
    {	
    	IData result = new DataMap();
		try {
			IData param = new DataMap();
			result.put("X_RESULTCODE", "0");
			result.put("X_RESULTINFO", "预约成功！");
			checkPramByKeys(input,"ENTERPRISE_NAME,ENTERPRISE_ADDR,AREA_CODE,CUST_NAME,CONTACT_SN");
			String eparchyCode = this.getTradeEparchyCode();
			if (IDataUtil.isNotEmpty(input)){
				param.put("ENTERPRISE_NAME",input.getString("ENTERPRISE_NAME",""));
				param.put("ENTERPRISE_ADDR",input.getString("ENTERPRISE_ADDR",""));
				param.put("AREA_CODE",input.getString("AREA_CODE",""));
				param.put("CUST_NAME",input.getString("CUST_NAME",""));
				param.put("CONTACT_SN",input.getString("CONTACT_SN",""));
				param.put("PRODUCT_NAME",input.getString("PRODUCT_NAME","企业宽带"));
				param.put("UPDATE_STAFF_ID",input.getString("UPDATE_STAFF_ID",""));
				param.put("REMARK",input.getString("REMARK",""));
				param.put("REG_DATE", SysDateMgr.getSysTime());
				param.put("REL_ID", SeqMgr.getInstId(eparchyCode));
				Dao.insert("TF_F_ENTERPRISE_WIDENET_BOOK", param, eparchyCode);
				this.notifySms(param);
			}
		} catch (Exception e) {
			result.put("X_RESULTCODE", "-1");
			result.put("X_RESULTINFO", "预约失败:"+e.getMessage());
		}
    	return result;
    }
    
    /**
     * 下发短信通知用户可办理
     * @param inData
     * @return
     * @throws Exception
     */
    public void notifySms(IData input) throws Exception
    {
		IData param = new DataMap();
		String contactSn = input.getString("CONTACT_SN","");
		IData userInfo = UcaInfoQry.qryUserInfoBySn(contactSn);
		IDataset preregInfo = this.getWidePreRegByRelId(input.getString("REL_ID",""));
		if(DataSetUtils.isNotBlank(preregInfo)){
			String enterprise_addr = preregInfo.getData(0).getString("ENTERPRISE_ADDR");
			String noticeContent = "尊敬的客户，您好！您登记的位于"+enterprise_addr+"的宽带需求，目前我公司已经完成建设，您可以到附近营业厅或拨打10086办理，感谢您的关注";
			IData smsData = new DataMap();
	        smsData.put("RECV_OBJECT", contactSn);
	        smsData.put("NOTICE_CONTENT", noticeContent);
	        smsData.put("BRAND_CODE", "");
	        if(IDataUtil.isEmpty(userInfo)){
	        	smsData.put("RECV_ID", "0");
	        }else{
	        	smsData.put("RECV_ID", userInfo.getString("USER_ID"));
	        }
	        SmsSend.insSms(smsData);
		}
    }
    
    public IDataset getWidePreRegByRelId(String relId) throws Exception
    {
    	IData param = new DataMap();
    	param.put("REL_ID", relId);
    	return Dao.qryByCode("TF_F_ENTERPRISE_WIDENET_BOOK", "SEL_PREREG_BY_REL_ID", param);
    }
    
    private void checkPramByKeys(IData data, String keyNamesStr) throws Exception
    {
        String keyNames[] = keyNamesStr.split(",");
        for (String strColName : keyNames)
        {
            IDataUtil.chkParam(data, strColName);
        }

    }
}
