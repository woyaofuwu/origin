
package com.asiainfo.veris.crm.order.soa.person.busi.view360;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;

public class GetAdditionalPSPTInfoSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;
    protected static Logger log = Logger.getLogger(GetAdditionalPSPTInfoSVC.class);
    /**
     * @author luoz
     * @date 2013-07-30
     * @description 360用户查询，查询非正常用户，如果有多个用户的时候的查询方法。
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset qryAdditionalPSPTInfo(IData param) throws Exception
    {
        String strUserId = param.getString("USER_ID", "");
        if(log.isDebugEnabled())
        {
        	log.debug("------------zx1----------------SERIAL_NUMBER:"+strUserId);
        }
        IDataset additionalPSPTInfos = CustomerInfoQry.getPsptByUserid(strUserId);
        /*
        IDataset userDataset = CustomerInfoQry.getCustInfoPsptBySn(strSerialNumber);
        if (!userDataset.isEmpty())
        {
            for (Object obj : userDataset)
            {
                IData userData = (IData) obj;
                IData tempData1= new DataMap();
                tempData1.put("CUST_TYPE", "责任人");
                tempData1.put("CUST_NAME", userData.getString("CUST_NAME", ""));
                tempData1.put("PSPT_TYPE_CODE", userData.getString("PSPT_TYPE_CODE", ""));
                tempData1.put("PSPT_ID", userData.getString("PSPT_ID", ""));
                tempData1.put("PSPT_ADDR", userData.getString("PSPT_ADDR", ""));
                additionalPSPTInfos.add(tempData1);
                
                String agentCustName= userData.getString("AGENT_CUST_NAME", "");
                if(!agentCustName.isEmpty())
                {
                	IData tempData2= new DataMap();
                	tempData2.put("CUST_TYPE", "经办人");
                	tempData2.put("CUST_NAME", agentCustName);
                    tempData2.put("PSPT_TYPE_CODE", userData.getString("AGENT_PSPT_TYPE_CODE", ""));
                    tempData2.put("PSPT_ID", userData.getString("AGENT_PSPT_ID", ""));
                    tempData2.put("PSPT_ADDR", userData.getString("AGENT_PSPT_ADDR", ""));
                    additionalPSPTInfos.add(tempData2);
                }
                
                String usecustName= userData.getString("USE_CUST_NAME", "");
                if(!usecustName.isEmpty())
                {
                	IData tempData3= new DataMap();
                	tempData3.put("CUST_TYPE", "使用人");
                	tempData3.put("CUST_NAME", usecustName);
                    tempData3.put("PSPT_TYPE_CODE", userData.getString("USE_PSPT_TYPE_CODE", ""));
                    tempData3.put("PSPT_ID", userData.getString("USE_PSPT_ID", ""));
                    tempData3.put("PSPT_ADDR", userData.getString("USE_PSPT_ADDR", ""));
                    additionalPSPTInfos.add(tempData3);
                }
            }
        }
        */
        

        
        
        if(log.isDebugEnabled())
        {
        	log.debug("------------zx2----------------additionalPSPTInfos:"+additionalPSPTInfos.toString());
        }
        return additionalPSPTInfos;
    }

 
}
