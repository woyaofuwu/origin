
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class GrpExtInfoQrySVC extends CSBizService
{

    /**
     * 根据CUST_ID,BNUM_KIND_NEW,SERVICE_ID 查询查询基本接入号码 liaolc
     */
    private static final long serialVersionUID = 1L;

    /**
     * 作用：校难基本接入号是否可用
     * 
     * @param inparam
     * @return
     * @throws Exception
     *             liaolc 2013-08-27
     */
    public IDataset checkBaseServCode(IData inparam) throws Exception
    {
        IDataset data = GrpExtInfoQry.checkBaseServCode(inparam);

        return data;
    }
    public IDataset selectGroupExtendForTag(IData inparam) throws Exception
    {
        IDataset data = GrpExtInfoQry.selectGroupExtendForTag(inparam);

        return data;
    }

    public IDataset getComboBoxValue(IData input) throws Exception
    {
        IDataset data = GrpExtInfoQry.getComboBoxValue(input);

        return data;
    }

    public IDataset getEcInCodeListByECA(IData input) throws Exception
    {
        IDataset data = GrpExtInfoQry.getEcInCodeListByECA(input);
        return data;
    }

    public IDataset getWAPPUSHEcInCodeListByECA(IData input) throws Exception
    {
        IDataset data = GrpExtInfoQry.getWAPPUSHEcInCodeListByECA(input);
        return data;
    }

    /**
     * 查询集团扩展信息
     * 
     * @param input
     * @return
     * @throws Exception
     *             liaolc
     */
    public IDataset getExtendLists(IData input) throws Exception
    {
        String custId = input.getString("CUST_ID");
        IDataset data = GrpExtInfoQry.getExtendLists(custId);
        return data;
    }

    public IDataset getVPNNOByGROUPID(IData input) throws Exception
    {
        IDataset data = GrpExtInfoQry.getVPNNOByGROUPID(input);
        return data;
    }

    public IDataset getVPNNOByVPNNO(IData input) throws Exception
    {
        IDataset data = GrpExtInfoQry.getVPNNOByVPNNO(input);
        return data;
    }

    /**
     * 查询集团客户信息
     * 
     * @author chenkh
     * @param CUST_ID
     * @return
     * @throws Exception
     */
    public IDataset queryCustGroupInfoByCID(IData data) throws Exception
    {
        String custID = data.getString("CUST_ID");

        return GrpExtInfoQry.queryCustGroupInfoByCID(custID);
    }
    
    /**
     * 查询是否是测试集团的标识
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryGrpExtendTestByCustId(IData input) throws Exception
    {
        String custId = input.getString("CUST_ID");
        IDataset data = GrpExtInfoQry.queryGrpExtendTestByCustId(custId);
        return data;
    }
}
