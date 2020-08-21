
package com.asiainfo.veris.crm.order.soa.person.busi.suppliercharge;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.FeeUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ResCorpInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.handgatheringfee.HandGatheringFeeBean;

public class SupplierChargeSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 查询供应商费用数据
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryFeeInfo(IData input) throws Exception
    {
        IDataset ds = ResCorpInfoQry.queryChnlFeeInfo(input.getString("CHNL_ID"), input.getString("FACTORY_CODE"), "0");
        if (IDataUtil.isNotEmpty(ds))
        {
            for (int i = 0, size = ds.size(); i < size; i++)
            {
                ds.getData(i).put("BALANCE", ds.get(i, "BALANCE", "0"));
            }
        }
        return ds;
    }

    /**
     * 加载卖场供应商信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset querySuppliers(IData input) throws Exception
    {
        IData returnData = new DataMap();
        IDataset returnSet = new DatasetList();
        IDataset marketSet = new DatasetList();
        String eparchyCode = input.getString("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        String cityCode = input.getString("CITY_CODE", getVisit().getCityCode());// 测试数据"HNHK"
        IDataset tmpSet = ResCorpInfoQry.getResCorpInfos("M", "", eparchyCode);
        /**
         * 过滤当前业务区的卖场
         */
        if (IDataUtil.isNotEmpty(tmpSet) && (!"HNSJ".equals(cityCode)) && (!"HNYD".equals(cityCode)) && (!"HNHN".equals(cityCode)))
        {
            for (int i = 0; i < tmpSet.size(); i++)
            {
                IData data = tmpSet.getData(i);
                if (StringUtils.equals(cityCode, data.getString("REMARK", "")))
                {
                    marketSet.add(data);
                }
            }
        }

        IDataset shopperSet = ResCorpInfoQry.getResCorpInfos("4", "", eparchyCode);
        returnData.put("MARKET_INFOS", marketSet);
        returnData.put("SHOPPER_INFOS", shopperSet);
        returnSet.add(returnData);
        return returnSet;
    }
    
    /**
     * 打印收据
     * 
     * @param data
     * @throws Exception
     */
    public IDataset printReceipt(IData input) throws Exception
    {
    	SupplierChargeBean bean = BeanManager.createBean(SupplierChargeBean.class);
        return bean.printReceipt(input);
    }

}
