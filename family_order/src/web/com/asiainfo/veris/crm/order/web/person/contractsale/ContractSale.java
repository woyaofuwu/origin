
package com.asiainfo.veris.crm.order.web.person.contractsale;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ContractSale extends PersonBasePage
{
    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @throws Exception
     * @date: 2014-7-29 下午04:19:14 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-29 chengxf2 v1.0.0 修改原因
     */
    private void checkByPackage(IData temp) throws Exception
    {
        IData cond = this.getData();
        IData contractInfo = temp.getData("CONTRACT_INFO");
        cond.put("CAMPN_TYPE", contractInfo.getString("CAMPN_TYPE"));
        cond.put("PACKAGE_ID", contractInfo.getString("SALEACTIVE_PACKAGE_ID"));
        IDataset ruleResultDataSet = CSViewCall.call(this, "CS.SaleActiveCheckSVC.checkByPackage", cond);
        this.setAjax(ruleResultDataSet.getData(0));
    }

    /** 资源校验接口 */
    public void checkResInfo(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        // 调用接口校验资源
        IData resInfo = CSViewCall.call(this, "SS.ContractSaleSVC.checkResInfo", param).getData(0);
        resInfo.put("DEVICE_MODEL_CODE", resInfo.getString("DEVICE_MODEL_CODE"));

        // IData resInfo = new DataMap();
        // resInfo.put("X_RESULTCODE", "0");
        // resInfo.put("DEVICE_MODEL_CODE", "P0001820");

        this.setAjax(resInfo);
    }

    public void getContractInfoById(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        // 调用服务获取合约计划,参数有DEVICE_MODE_CODE,EPARCHY_CODE
        IDataset set = CSViewCall.call(this, "CS.ProductInfoQrySVC.queryContractInfoById", data);
        if (IDataUtil.isEmpty(set))
        {
            // 报错
        }
        IData temp = set.getData(0);
        checkByPackage(temp); // 合约业务规则校验
        setContractinfo(temp.getData("CONTRACT_INFO"));
        setVoices(temp.getDataset("VOICES"));
        setGroups(temp.getDataset("SALE_GROUP"));
        setContractMonths(temp.getDataset("CONTRACT_MONTHS"));
        setConsumeLevels(temp.getDataset("CONSUME_LEVELS"));
        setConsumeElements(temp.getDataset("CONSUME_ELEMENTS"));
        // setContractDiscounts(temp.getDataset("CONTRACT_DISCOUNTS"));
    }

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {

    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();

        IDataset result = CSViewCall.call(this, "SS.ContractSaleRegSVC.tradeReg", pageData);
        this.setAjax(result);
    }

    public void queryCommparaDetail(IRequestCycle cycle) throws Exception
    {
        IData request = this.getData();
        IData param = new DataMap();
        param.put("PARAM_VALUE", "8801");
        param.put("PARA_CODE1", request.getString("PARA_CODE1"));
        param.put("PARA_CODE2", request.getString("PARA_CODE2"));
        param.put("EPARCHY_CODE", request.getString("EPARCHY_CODE"));
        IDataset infos = CSViewCall.call(this, "CS.SaleservComparaQrySVC.querySaleservCompara", param);
        if (IDataUtil.isNotEmpty(infos))
        {
            this.setAjax(infos.getData(0));
        }
    }

    /**
     * 根据机型查合约计划
     * 
     * @author anwx@asiainfo-linkage.com @ 2013-5-10
     * @param cycle
     * @throws Exception
     */
    public void queryContractBycode(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        // 调用服务获取合约计划,参数有DEVICE_MODE_CODE,EPARCHY_CODE,PRODUCT_TYPE_CODE
        IDataset contractsales = CSViewCall.call(this, "CS.ProductInfoQrySVC.queryContractSaleByResType", data);
        setContractsales(contractsales);
    }

    public abstract void setConsumeElement(IData consumeElement);

    public abstract void setConsumeElements(IDataset consumeElements);

    public abstract void setConsumeLevels(IDataset consumeLevels);

    public abstract void setContractDiscounts(IDataset contractDiscounts);

    public abstract void setContractinfo(IData contractinfo);

    public abstract void setContractMonths(IDataset contractMonths);

    public abstract void setContractsale(IData contractsale);

    public abstract void setContractsales(IDataset contractsales);

    public abstract void setGroup(IData group);

    public abstract void setGroups(IDataset groups);

    public abstract void setInfo(IData info);

    public abstract void setProductTypes(IDataset productTypes);

    public abstract void setVoice(IData voice);

    public abstract void setVoiceDiscnt(IData voiceDiscnt);

    public abstract void setVoices(IDataset voices);
}
