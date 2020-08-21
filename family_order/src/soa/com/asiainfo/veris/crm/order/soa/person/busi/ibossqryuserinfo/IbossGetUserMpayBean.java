
package com.asiainfo.veris.crm.order.soa.person.busi.ibossqryuserinfo;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class IbossGetUserMpayBean extends CSBizBean
{

    /**
     * 获取用户手机支付平台服务信息
     * 
     * @param pd
     * @param inparams
     *            IDVALUE
     * @return IDataset
     * @throws Exception
     */
    public IData getUserMpay(IData data) throws Exception
    {

        IData result = new DataMap();

        IData idata = new DataMap();

        IData users = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER", ""));

        if (users == null || users.size() < 1)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "2107:手机号对应的用户不存在");
        }

        String userid = users.getString("USER_ID");
        result.put("USER_ID", userid);
        idata.clear();
        idata.put("USER_ID", userid);
        idata.put("BIZ_TYPE_CODE", "54");
        
        UcaData uca = UcaDataFactory.getUcaByUserId(userid);
        List<PlatSvcTradeData> userPlatSvcs = new ArrayList<PlatSvcTradeData>();
        
        IDataset datas = UpcCall.querySpServiceAndProdByCond("698000", "00000001", "54", null);
        
        if(IDataUtil.isNotEmpty(datas))
        {
        	for(int i =0;i<datas.size();i++)
        	{
        		IData platSvcParam = datas.getData(i);
        		String serviceId = platSvcParam.getString("SERVICE_ID");
        		List<PlatSvcTradeData> temps = uca.getUserPlatSvcByServiceId(serviceId);
        		if(ArrayUtil.isNotEmpty(temps))
        		{
        			userPlatSvcs.addAll(temps);
        		}
        	}
        }
        
        if (userPlatSvcs == null || userPlatSvcs.size() < 1)
        {
            result.put("IDVALUE", data.getString("IDVALUE"));
            result.put("BIZ_TYPE_CODE", "54");
            result.put("BIZ_STATE_CODE", "2");
        }
        else
        {
            String sTmp = userPlatSvcs.get(0).getBizStateCode();

            result.put("IDVALUE", data.getString("IDVALUE"));
            result.put("BIZ_TYPE_CODE", "54");
            if ("A".equals(sTmp))
                result.put("BIZ_STATE_CODE", "0");
            else if ("N".equals(sTmp))
                result.put("BIZ_STATE_CODE", "1");
            else if ("E".equals(sTmp))
                result.put("BIZ_STATE_CODE", "2");
            else
                result.put("BIZ_STATE_CODE", "2");
        }
        return result;
    }

}
