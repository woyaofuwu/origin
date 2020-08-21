package com.asiainfo.veris.crm.iorder.web.igroup.pagedata;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcConstants;

public class OpenEnterpriseMemberPageDataTrans extends PageDataTrans
{
    @Override
    public IData transformData() throws Exception
    {
        super.transformData();
        
        IData result = new DataMap();
        
        IDataset offerInfoList = transformOfferList(getOfferList());
        result.put("ELEMENT_INFO", offerInfoList);
        
        IData commonData = getCommonData();
        
        IDataset resList = transformResInfo(commonData);
        result.put("RES_INFO", resList);
        result.put("MEM_PREPAY_SIGN_CODE", commonData.getString("MEM_PREPAY_SIGN_CODE", ""));
        result.put("CUST_INFO_TELTYPE", commonData.getString("CUST_INFO_TELTYPE", ""));
        result.put("IS_EXPER", commonData.getString("IS_EXPER", "1")); //是否体验客户
        
        result.put("MEM_ACCT_INFO", getMemAccount());
        
        IData memCustInfo = getMemCustomer();
        result.put("MEM_CUST_INFO", memCustInfo);
        
        IData memSubscriber = getMemSubscriber();
        result.put("MEM_USER_INFO", memSubscriber);
        
        String ecCustId = getEcCustomer().getString("CUST_ID");
        if(StringUtils.isEmpty(ecCustId))
        {
            ecCustId = commonData.getString("CUST_ID");
        }
        result.put("CUST_ID", ecCustId);
        result.put("SERIAL_NUMBER", getSerialNumber());
        result.put("GROUP_ID", commonData.getString("GROUP_ID"));
        result.put("PRODUCT_ID", getProductId());
        
        result.put("ACCT_INFO", getEcAccount());
        result.put("USER_INFO", getEcSubscriber());
        
        IData payInfo = savePayInfoFrontData(); //付费关系
        result.put("PAY_INFO", payInfo);

        if("801110".equals(getProductId()))
        {
            //客户摄像
            result.put("custInfo_PIC_ID", memCustInfo.getString("PIC_ID",""));
            //经办人摄像
            result.put("custInfo_AGENT_PIC_ID", memCustInfo.getString("AGENT_PIC_ID",""));

            String batchId = commonData.getString("BATCH_ID");
            if(StringUtils.isNotEmpty(batchId))
            {
                //IMS固话批量开户标志
                result.put("BATCH_OPER_TYPE", "BATOPENGROUPMEM");
                result.put("BATCH_ID", batchId);
            }
        }

        // esop参数
        String eosStr = getOtherData().getString("EOS");
        if(StringUtils.isNotBlank(eosStr) && !"{}".equals(eosStr))
        {
            result.put("EOS", new DatasetList(eosStr));
        }
        
        return result;
    }
    
    public void setServiceName() throws Exception
    {
        setSvcName(EcConstants.EC_OPER_SERVICE.OPEN_ENTERPRISE_MEMBER.getValue());
    }

    /**
     * 设置付费关系
     * 
     * @return
     * @throws Exception
     */
    private IData savePayInfoFrontData() throws Exception
    {
        IData payInfo = new DataMap();
        payInfo.put("PLAN_TYPE_CODE", "T");
        payInfo.put("PLAN_MODE_CODE", "1");
        payInfo.put("NOTE_ITEMS", "A,B,C");
        return payInfo;
    }
}
