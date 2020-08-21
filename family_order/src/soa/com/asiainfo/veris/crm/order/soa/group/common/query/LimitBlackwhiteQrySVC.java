
package com.asiainfo.veris.crm.order.soa.group.common.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class LimitBlackwhiteQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     *成员主动销户时 删除用户受限表数据
     */

    public IDataset deleteLimitBlackWhite(IData inparam) throws Exception
    {
        String serialNumber = inparam.getString("SERIAL_NUMBER");
        IDataset data = LimitBlackwhiteBean.deleteLimitBlackWhite(serialNumber);

        return data;
    }

    /**
     * 集团名称
     * 
     * @param data
     * @return
     * @throws Exception
     */

    public IDataset queryByCustName(IData inparam) throws Exception
    {
        String custName = inparam.getString("CUST_NAME");
        IDataset data = LimitBlackwhiteBean.queryByCustName(custName, this.getPagination());

        return data;
    }

    /**
     * 根据ec接入号，获取用户受限表信息
     * 
     * @param data
     * @return
     * @throws Exception
     */

    public IDataset queryByEcBizInCode(IData inparam) throws Exception
    {
        String ecBizInCode = inparam.getString("BIZ_IN_CODE");
        IDataset data = LimitBlackwhiteBean.queryByEcBizInCode(ecBizInCode, this.getPagination());

        return data;
    }

    /**
     * 根据SERIAL_NUMBER查询受限信息
     */

    public IDataset queryBySn(IData inparam) throws Exception
    {
        String serialNumber = inparam.getString("SERIAL_NUMBER");
        IDataset data = LimitBlackwhiteBean.queryBySn(serialNumber, this.getPagination());

        return data;
    }

    /**
     * 根据手机号、业务编码进行名单记录删除
     * 
     * @param inparam
     * @throws Exception
     */
    public IDataset deleteLimitBlackWhite2(IData inparam) throws Exception
    {
        // LimitBlackwhiteBean bean = (LimitBlackwhiteBean)BeanManager.createBean(LimitBlackwhiteBean.class);
        LimitBlackwhiteBean.deleteLimitBlackWhite(inparam.getString("SERIAL_NUMBER"), inparam.getString("BIZ_IN_CODE"), "");
        return new DatasetList();
    }
}
