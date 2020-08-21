
package com.asiainfo.veris.crm.order.soa.person.busi.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

public class CustomerInfoBean extends CSBizBean
{

    /**
     * 查询person和customer基本信息（通过CUST_ID） used
     * 
     * @param pd
     * @return IDataset
     * @throws Exception
     */
    public IDataset qryPersonInfoByCustId(IData data, Pagination pagination) throws Exception
    {

        String custId = data.getString("CUST_ID");

        // person
        IData personInfo = UcaInfoQry.qryPerInfoByCustId(custId);

        /* group  
         * 判断如果cust_person个人用户表中没有则去集团用户查询cust_group  
         * add by huanghui 2014-09-23 
         */
        if(IDataUtil.isEmpty(personInfo))
        {
        	personInfo = UcaInfoQry.qryGrpInfoByCustId(custId);
        }
        
        if(IDataUtil.isEmpty(personInfo))
        {
        	return new DatasetList();
        }


        // customer
        IData customer = UcaInfoQry.qryCustomerInfoByCustId(custId);

        if (IDataUtil.isEmpty(customer))
        {
            return new DatasetList();
        }

        return IDataUtil.idToIds(customer);
    }
}
