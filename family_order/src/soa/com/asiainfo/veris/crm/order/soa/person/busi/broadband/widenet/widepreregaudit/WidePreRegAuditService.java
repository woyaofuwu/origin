/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widepreregaudit;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WideNetBookQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

/**
 * Copyright: Copyright (c) 2016 Asiainfo-Linkage
 * 
 * @ClassName: PreRegAuditService.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: lijun17
 * @date: 2016-5-21 下午03:04:14 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2016-5-21 lijun17 v1.0.0 修改原因
 */

public class WidePreRegAuditService extends CSBizService
{
	protected static Logger log = Logger.getLogger(WidePreRegAuditService.class);
    private static final long serialVersionUID = 1L;

    /**
     * @Function:
     * @Description: 宽带需求收集清单登记状态维护提交
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijun17
     * @date: 2016-5-21 下午04:50:21 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2016-5-21 lijun17 v1.0.0 修改原因
     */
    public IDataset dealService(IData input) throws Exception
    {
    	String eparchyCode = this.getTradeEparchyCode();
        String paramInfo = input.getString("PARAM_INFO");
        IDataset auditValueList = new DatasetList(paramInfo);
        if (DataSetUtils.isNotBlank(auditValueList))
        {
        	IDataset auditValueList1 = new DatasetList();//需要根据地址汇总一起改变的
        	IDataset auditValueList2 = new DatasetList();//只改变本条数据的
        	for(int i = 0 ; i < auditValueList.size() ; i++){
        		String regStatus = auditValueList.getData(i).getString("REG_STATUS");
        		if("5".equals(regStatus) || "6".equals(regStatus)){//已通知，已办理只改变本条数据状态
        			auditValueList2.add(auditValueList.getData(i));
        		}else{
        			auditValueList1.add(auditValueList.getData(i));
        		}
        	}
        	if(DataSetUtils.isNotBlank(auditValueList1)){
        		Dao.executeBatchByCodeCode("TF_F_WIDENET_BOOK", "UPD_PRE_STATUS", auditValueList1, eparchyCode);
        	}
            if(DataSetUtils.isNotBlank(auditValueList2)){
            	 Dao.executeBatchByCodeCode("TF_F_WIDENET_BOOK", "UPD_PRE_STATUS1", auditValueList2, eparchyCode);
            }
           
//            Dao.executeBatchByCodeCode("TF_F_WIDENET_BOOK", "UPD_PRE_STATUS", auditValueList, eparchyCode);
        }
        return auditValueList;
    }


    /**
     * @Function:
     * @Description: 宽带需求收集清单查询
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijun17
     * @date: 2016-5-21 下午04:50:21 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2016-5-21 lijun17 v1.0.0 修改原因
     */
    public IDataset getWideNetBookList(IData inData) throws Exception
    {
        String contactSn = inData.getString("cond_CONTACT_SN");
        String custName = inData.getString("cond_CUST_NAME");
        String serial_number = inData.getString("cond_SERIAL_NUMBER");
        String regStatus = inData.getString("cond_REG_STATUS");
        String set_addr = inData.getString("cond_SET_ADDR");
        String startDate = inData.getString("cond_START_DATE");
        String endDate = inData.getString("cond_END_DATE");
        String cityCode = getVisit().getCityCode();
        if(StringUtils.isEmpty(cityCode)){
        	cityCode = inData.getString("TRADE_CITY_CODE");
        }
        Pagination pagin = this.getPagination();
        return WideNetBookQuery.getWideNetPreRegBookList(contactSn, custName, serial_number, regStatus, set_addr, startDate, endDate, cityCode, pagin);
    }
    
    /**
     * @Function:
     * @Description: 宽带需求收集清单汇总查询
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijun17
     * @date: 2016-5-21 下午04:50:21 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2016-5-21 lijun17 v1.0.0 修改原因
     */
    public IDataset getWideNetBookCollectList(IData inData) throws Exception
    {
        IData param = new DataMap();
        String regStatus = inData.getString("collect_REG_STATUS");
        String set_addr = inData.getString("collect_SET_ADDR");
        String startDate = inData.getString("collect_START_DATE");
        String endDate = inData.getString("collect_END_DATE");
        Pagination pagin = this.getPagination();
        return WideNetBookQuery.getWideNetBookCollectList(regStatus, set_addr, startDate, endDate, pagin);
    }
    
    /**
     * 下发短信通知用户可办理
     * @param inData
     * @return
     * @throws Exception
     */
    public IDataset notifySms(IData input) throws Exception
    {
    	String eparchyCode = this.getTradeEparchyCode();
        String paramInfo = input.getString("PARAM_INFO");
        IDataset notifySmsList = new DatasetList(paramInfo);
        if (DataSetUtils.isNotBlank(notifySmsList))
        {
        	for(int i = 0 ; i < notifySmsList.size() ; i++){
        		IData param = new DataMap();
        		param.put("INST_ID", notifySmsList.getData(i).getString("INST_ID"));
				Dao.executeUpdateByCodeCode("TF_F_WIDENET_BOOK", "UPD_PRE_DREDGE_STATUS", param);
				String contactSn = notifySmsList.getData(i).getString("CONTACT_SN");
				IData userInfo = UcaInfoQry.qryUserInfoBySn(contactSn);
				IDataset preregInfo = this.getWidePreRegByInstId(notifySmsList.getData(i).getString("INST_ID"));
				if(DataSetUtils.isNotBlank(preregInfo)){
					String home_addr = preregInfo.getData(0).getString("HOME_ADDR");
					String noticeContent = "尊敬的客户，您好！您登记的位于"+home_addr+"的宽带需求，目前我公司已经完成建设，您可以到附近营业厅或拨打10086办理，感谢您的关注";
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
        }
        return notifySmsList;
    }
    
    public IDataset getWidePreRegByInstId(String instId) throws Exception
    {
    	IData param = new DataMap();
    	param.put("INST_ID", instId);
    	return Dao.qryByCode("TF_F_WIDENET_BOOK", "SEL_PREREG_BY_INST_ID", param);
    }
}
