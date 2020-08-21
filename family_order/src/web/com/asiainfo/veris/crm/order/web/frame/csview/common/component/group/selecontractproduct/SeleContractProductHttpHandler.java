
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.selecontractproduct;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.custinfo.contractinfo.ContractInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.custinfo.contractproductinfo.ContractProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupEsopUtilView;

public class SeleContractProductHttpHandler extends CSBizHttpHandler
{

    /**
     * 通过集团标示查询集团合同信息
     *
     * @throws Exception
     */
    public void qryContractByCustIdForGrp() throws Exception
    {

        String custId = getData().getString("CUST_ID");
        if (StringUtils.isBlank(custId))
        {
            return;
        }
        IDataset contracts = ContractInfoIntfViewUtil.qryContractByCustIdForGrp(this, custId);
        this.setAjax(contracts);

    }

    /**
     * 通过集团USERID查询集团合同产品信息
     *
     * @throws Exception
     */
    public void qryContractByUserIdForGrp() throws Exception
    {

        String userId = getData().getString("USER_ID", "");
        IData userInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, userId, true);
        String contractId = userInfo.getString("CONTRACT_ID", "");
        String custId = userInfo.getString("CUST_ID", "");
        if (StringUtils.isBlank(custId))
        {
            return;
        }
        IData resultData = new DataMap();

        IDataset contracts = ContractInfoIntfViewUtil.qryContractByCustIdForGrp(this, custId);
        resultData.put("GRP_CONTRACT_LIST", contracts);
        if (StringUtils.isBlank(contractId))
        {
            setAjax(resultData);
            return;
        }

        IData contractData = ContractInfoIntfViewUtil.qryContractByContractIdForGrp(this, contractId);
        if (IDataUtil.isEmpty(contractData))
        {
            setAjax(resultData);
            return;
        }
        resultData.put("GRP_CONTRACT_INFO", contractData);

        IDataset contractprods = ContractProductInfoIntfViewUtil.qryContractProductByContIdForGrp(this, contractId);
        if (IDataUtil.isEmpty(contractprods))
        {
            setAjax(resultData);
            return;
        }
        resultData.put("GRP_CONTRACT_PRODUCT_INFOS", contractprods);
        setAjax(resultData);

    }

    public void qryContractProductByContractIdForGrp() throws Exception
    {
        String contractId = getData().getString("CONTRACT_ID", "");

        IData resultData = new DataMap();
        if (StringUtils.isBlank(contractId))
        {
            return;
        }

        IData contractData = ContractInfoIntfViewUtil.qryContractByContractIdForGrp(this, contractId);
        if (IDataUtil.isEmpty(contractData))
        {
            return;
        }
        resultData.put("GRP_CONTRACT_INFO", contractData);

        IDataset contractprods = ContractProductInfoIntfViewUtil.qryContractProductByContIdForGrp(this, contractId);
        if (IDataUtil.isEmpty(contractprods))
        {
            this.setAjax(resultData);
            return;
        }
        resultData.put("GRP_CONTRACT_PRODUCT_INFOS", contractprods);
        this.setAjax(resultData);
    }



    /**
     * 通过客户编码和产品编码，查询有该产品的合同信息
     * @throws Exception
     */
    public void qryContractByCustIdProductIdForGrp() throws Exception{
        String custId = getData().getString("CUST_ID");
        if (StringUtils.isBlank(custId))
        {
            return;
        }

        String productId = getData().getString("PRODUCT_ID");
        if (StringUtils.isBlank(productId))
        {
            return;
        }

        String eosStr = getData().getString("EOS");
        if (StringUtils.isBlank(productId))
        {
            return;
        }
        IData eosList = new DataMap();
        IData resultDataset = new DataMap();
        if (StringUtils.isNotEmpty(eosStr) && !"{}".equals(eosStr)) {
            IDataset eos = new DatasetList(eosStr);
            eosList = eos.getData(0);
            IDataset dataset = new DatasetList();
            IData inputParam = new DataMap();
            inputParam.put("NODE_ID", eosList.getString("NODE_ID", ""));
            inputParam.put("IBSYSID", eosList.getString("IBSYSID", ""));
            inputParam.put("SUB_IBSYSID", eosList.getString("SUB_IBSYSID", ""));
            inputParam.put("PRODUCT_ID", eosList.getString("PRODUCT_ID"));
            dataset.add(inputParam);
            resultDataset = GroupEsopUtilView.getEsopData(this, dataset);
        }

        StringBuilder lines = new StringBuilder();
        if(IDataUtil.isNotEmpty(resultDataset)){
            IDataset datalineData = resultDataset.getDataset("DLINE_DATA",new DatasetList());
            if (null != datalineData && datalineData.size() > 0)
            {
                for (int i = 0; i < datalineData.size(); i++)
                {
                    String line = datalineData.getData(i).getString("PRODUCTNO");
                    lines.append("'").append(line).append("'");
                    if (i != datalineData.size()-1){
                        lines.append(",");
                    }
                }
            }
        }
        IDataset contracts = ContractInfoIntfViewUtil.qryContractByCustIdProductIdForGrp(this, custId,productId,lines.toString());
        this.setAjax(contracts);
    }

}
