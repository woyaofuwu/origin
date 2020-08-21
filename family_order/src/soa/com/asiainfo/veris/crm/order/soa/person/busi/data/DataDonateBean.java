package com.asiainfo.veris.crm.order.soa.person.busi.data;

import org.apache.log4j.Logger;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ScoreException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class DataDonateBean extends CSBizBean {
	/**
     * 用户流量查询
     * 
     * @param pd
     * @param inparam
     * @return
     * @throws Exception
     * @author zx
     */
	public IDataset getUserDataInfosBySN(String serialNumber) throws Exception {
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		IDataOutput resultSetOut = CSAppCall.callAcct(
				"AM_OUT_qryAllCanTransferInfos", param, false);
        if ("0".equals(resultSetOut.getHead().getString("X_RESULTCODE")))
        {
			IDataset dataset = resultSetOut.getData();
			if (IDataUtil.isNotEmpty(dataset)) {
				IDataset returnset = dataset.getData(0).getDataset("PRESENTABLE_FLOWS");
				return returnset;
			}
			else{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用接口AM_OUT_qryAllCanTransferInfos异常:"+resultSetOut);
				return new DatasetList();
			}
        }
        else{
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用接口AM_OUT_qryAllCanTransferInfos异常:"+resultSetOut);
        	return new DatasetList();
        }
        
	}
    
    public IData queryObjCustInfo(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");

        if (StringUtils.isBlank(serialNumber))
        {
            // 服务号码不能为空
            CSAppException.apperr(ScoreException.CRM_SCORE_1);
        }

        IData user = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(user))
        {
            // 该服务号码用户信息不存在
            CSAppException.apperr(CrmUserException.CRM_USER_117, serialNumber);
        }

        // 查询用户积分
        String userId = user.getString("USER_ID");
        String custId = user.getString("CUST_ID");
        IData cust = UcaInfoQry.qryCustInfoByCustId(custId);

        // 查询流量转赠阀值--下限
        IDataset ids1 = CommparaInfoQry.getCommParas("CSM", "419", "20170419","MIN","0898");
        if(!ids1.isEmpty() && ids1.size()>0 )
        {
        	user.put("MIN", ids1.getData(0).getString("PARA_CODE2"));
        }
        IDataset ids2 = CommparaInfoQry.getCommParas("CSM", "419", "20170419","MAX","0898");
        // 查询流量转赠阀值--上限
        if(!ids2.isEmpty() && ids2.size()>0 )
        {
        	user.put("MAX", ids2.getData(0).getString("PARA_CODE2"));
        }
        
        IData backInfo = new DataMap();
        backInfo.put("OBJ_USER_INFO", user);
        backInfo.put("OBJ_CUST_INFO", cust);

        return backInfo;
    }
    
    public IDataset getCommInfo(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");

        if (StringUtils.isBlank(serialNumber))
        {
            // 服务号码不能为空
            CSAppException.apperr(ScoreException.CRM_SCORE_1);
        }

        IData user = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(user))
        {
            // 该服务号码用户信息不存在
            CSAppException.apperr(CrmUserException.CRM_USER_117, serialNumber);
        }

        // 查询用户流量
        IDataset backInfos = new DatasetList();
        
        backInfos = getUserDataInfosBySN(serialNumber);
/*        if(!backInfos.isEmpty() && backInfos.size()>0 )
        {
        	backInfos.getData(0).put("FM_BALANCE_ID", backInfos.getData(0).getString("PRESENTABLE_FLOW_CODE"));
        	backInfos.getData(0).put("BALANCE", backInfos.getData(0).getString("PRESENTABLE_FLOW_BALANCE"));
        }*/

        return backInfos;
    }
    static Logger logger=Logger.getLogger(DataDonateBean.class);
}
