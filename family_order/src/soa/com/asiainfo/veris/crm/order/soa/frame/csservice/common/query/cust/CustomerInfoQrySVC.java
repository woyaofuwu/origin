
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

public class CustomerInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * @author fuzn
     * @date 2013-03-14
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getAccountUniteByCustName(IData input) throws Exception
    {
        IDataset data = CustomerInfoQry.getAccountUniteByCustName(input, getPagination());

        return data;
    }

    /**
     * 根据挂账集团名称，获取挂账集团客户信息
     * 
     * @author fuzn
     * @date 2013-05-21
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getAccountUniteByCustPersonName(IData input) throws Exception
    {
        // param.put("EPARCHY_CODE", eparchyCode);
        String eparchyCode = input.getString("EPARCHY_CODE");
        IDataset data = CustomerInfoQry.getAccountUniteByCustPersonName(input, getPagination(), eparchyCode);

        return data;
    }

    /**
     * 根据SERIAL_NUMBER查客户信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getCustAndUserInfoBySn(IData input) throws Exception
    {
        IDataset dataset = CustomerInfoQry.getCustAndUserInfoBySn(input, null);

        return dataset;
    }
    
    /**
     * 根据SERIAL_NUMBER查客户信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getCustInfoBySn(IData input) throws Exception
    {
        IDataset dataset = CustomerInfoQry.getCustInfoBySn(input, null);

        return dataset;
    }

    public IDataset getCustInfoByCId(IData input) throws Exception
    {
        IDataset dataset = IDataUtil.idToIds(UcaInfoQry.qryCustomerInfoByCustId(input.getString("CUST_ID")));

        return dataset;
    }

    public IDataset getCustInfoByCustID(IData input) throws Exception
    {
        String custId = input.getString("CUST_ID");
        IDataset dataset = IDataUtil.idToIds(UcaInfoQry.qryCustomerInfoByCustId(custId));
        return dataset;
    }

    /**
     * 客户信息查询
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getCustInfoByPspt(IData input) throws Exception
    {
        String psptTypeCode = input.getString("PSPT_TYPE_CODE");
        String psptId = input.getString("PSPT_ID");
        IDataset data = CustomerInfoQry.getCustInfoByPspt(psptTypeCode, psptId);
        return data;
    }

    /**
     * 客户信息查询
     * 
     * @param input
     * @return
     * @throws Exception
     *             wangjx 2013-3-28
     */
    public IDataset getCustUserInfoByPspt(IData input) throws Exception
    {
        IDataset dataset = CustomerInfoQry.getCustUserInfoByPspt(input);

        return dataset;
    }

    /**
     * update by yangsh6 客户标识是唯一索引，返还结果最多是一条记录，不会是多条记录，所以在查不到个人信息的时候可以去查集团信息（如果返还是多条记录，就不能这么做了）
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData selCustInfoByCustID(IData input) throws Exception
    {
        IData resultData = new DataMap();
        IData paramData = new DataMap();
        String xgameModel = input.getString("X_GETMODE", "1");
        String custId = input.getString("CUST_ID", "");
        if (!"".equals(custId) && "1".equals(xgameModel))
        {
            // xia
            paramData.put("CUST_ID", custId);
            IData data = UcaInfoQry.qryCustomerInfoByCustId(custId);// TF_F_CUSTOMER
            if (IDataUtil.isNotEmpty(data))
            {

                paramData.clear();
                paramData.put("CUST_ID", data.getString("CUST_ID", ""));
                IData personCust = UcaInfoQry.qryPerInfoByCustId(custId); // TF_F_CUST_PERSON

                if (IDataUtil.isNotEmpty(personCust))
                {
                    resultData.putAll(personCust);
                    resultData.putAll(data);
                }
                else
                {
                    paramData.clear();
                    paramData.put("CUST_ID", data.getString("CUST_ID", ""));
                    IData groupCust = UcaInfoQry.qryGrpInfoByCustId(data.getString("CUST_ID", ""));
                    if (IDataUtil.isEmpty(groupCust))
                    {
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "客户标识号获取客户个人或客户集团资料无数据！");
                    }
                    resultData.putAll(groupCust);
                    resultData.putAll(data);
                }
            }
            else
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据标识未找到客户资料");
            }
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "[CUST_ID]不能为空!");
        }
        return resultData;
    }

}
