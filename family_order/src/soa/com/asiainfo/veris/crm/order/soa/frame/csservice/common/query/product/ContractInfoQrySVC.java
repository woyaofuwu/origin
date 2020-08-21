
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * @author Administrator
 */
public class ContractInfoQrySVC extends CSBizService
{
    public static IDataset getContractByDevice(IData input) throws Exception
    {
        ContractInfoQry bean = (ContractInfoQry) BeanManager.createBean(ContractInfoQry.class);
        String deviceModelCode = input.getString("DEVICE_MODEL_CODE");
        String eparchyCode = input.getString("EPARCHY_CODE");
        IDataset dataset = bean.getContractByDevice(deviceModelCode, eparchyCode);

        return dataset;
    }

    public static IDataset getProductInfosById(IData input) throws Exception
    {
        IDataset rtSet = new DatasetList();
        IData rtData = new DataMap();
        IDataset saleGroups = new DatasetList();
        ContractInfoQry bean = (ContractInfoQry) BeanManager.createBean(ContractInfoQry.class);
        String contractId = input.getString("CONTRACT_ID");
        String eparchyCode = input.getString("EPARCHY_CODE");

        // 合约信息
        IDataset contractInfos = bean.getContractInfoById(contractId, eparchyCode);
        if (IDataUtil.isEmpty(contractInfos))
        {
            // 报错
        }
        rtData.put("CONTRACT_INFO", contractInfos.getData(0));

        // 语音营销资源
        IDataset voiceProdcuts = bean.getVoiceProductByContract(contractId, eparchyCode);
        rtData.put("VOICE_PRODUCT", voiceProdcuts);

        // 其他营销资源
        IDataset tempSaleGroups = bean.getSaleGroupsByContract(contractId, eparchyCode);
        int groupSize = tempSaleGroups.size();
        for (int i = 0; i < groupSize; i++)
        {
            IData saleGroup = new DataMap();
            IData tempSaleGroup = tempSaleGroups.getData(i);

            // 查询group下的营销资源
            String packageId = tempSaleGroup.getString("PACKAGE_ID");
            IDataset saleElements = bean.getSaleElementsByContractPackage(contractId, packageId, eparchyCode);
            if (IDataUtil.isNotEmpty(saleElements))
            {
                saleGroup.putAll(tempSaleGroup);
                saleGroup.put("SALE_ELEMENTS", saleElements);
                saleGroups.add(saleGroup);
            }
        }
        rtData.put("SALE_GROUP", saleGroups);

        // 合约时长
        IDataset contractMonths = bean.getContractMonthsInfo(contractId, eparchyCode);
        rtData.put("CONTRACT_MONTHS", contractMonths);
        // 最低消费
        IDataset consumeLimits = bean.getConsumeLimitInfo(contractId, eparchyCode);
        rtData.put("CONSUME_LIMIT", consumeLimits);

        rtSet.add(rtData);

        return rtSet;
    }
}
