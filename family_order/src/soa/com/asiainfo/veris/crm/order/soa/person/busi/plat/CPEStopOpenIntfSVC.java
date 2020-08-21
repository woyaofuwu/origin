/* $Id: InternationalGPRSIntfBean.java,v 1.2 2014/01/24 08:35:57 dengshu Exp $ */

package com.asiainfo.veris.crm.order.soa.person.busi.plat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

/**
 *
 */
public class CPEStopOpenIntfSVC extends CSBizService
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;



    // 暂停
    private static final String OPERATE_CODE_SUSPEND = "0";

    // 恢复
    private static final String OPERATE_CODE_RESTART = "1";

    /**
     * GPRS服务暂停接口
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public IDataset ServiceSuspend(IData param) throws Exception
    {
        //String serialNumber = IDataUtil.chkParam(param, "SERIAL_NUMBER");
        
    	String serialNumber = param.getString("SERIAL_NUMBER");

        if (StringUtils.isBlank(serialNumber))
        {
        	String userId = IDataUtil.chkParam(param, "USER_ID");
        	
            IData userInfo = UcaInfoQry.qryUserInfoByUserId(userId);
            if (userInfo != null)
            {
            	param.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
            }
        	
        }
  
        //处理AEE到期执行参数
        if(StringUtils.isNotBlank(param.getString("DEAL_COND"))){
        	IData condData = new DataMap(param.getString("DEAL_COND"));
        	param.putAll(condData);
        }
        
        String open_code = IDataUtil.chkParam(param, "OPEN_CODE");
        // 服务暂停必须传入SEND_FLAG 参照GPRS暂停，60G封顶恢复传4
        String send_flag = "";
        if (OPERATE_CODE_SUSPEND.equals(open_code))
        {
        	send_flag = IDataUtil.chkParam(param, "SEND_FLAG");
        }

        String svcName = "";

        param.put("SERV_TYPE", "0");//按国内GPRS写死
        if (OPERATE_CODE_SUSPEND.equals(open_code))//sendFLag只允许规定的参数
        {
            // 暂停
            param.put("OPER_CODE", "04");
            if(send_flag.equals("4")){
            	param.put("REMARK", "FD");//action中判断是否需要封顶恢复,只有当前接口暂停才做恢复
            }
            
            svcName = "SS.ServiceOperRegSVC.tradeReg";
        }
        else if (OPERATE_CODE_RESTART.equals(open_code))
        {
            // 恢复
            param.put("OPER_CODE", "05");
            svcName = "SS.ServiceOperRegSVC.tradeReg";
        }
        // 服务用20
        param.put("SERVICE_ID", 220);
            
        IDataset dataset = CSAppCall.call(svcName, param);
        IData executeData = new DataMap();
        executeData.put("TRADE_ID", dataset.getData(0).getString("TRADE_ID"));
        executeData.put("X_RESULTINFO", "TRADE OK");
        IDataset result = new DatasetList();
        result.add(executeData);
        return result;
    }

}
