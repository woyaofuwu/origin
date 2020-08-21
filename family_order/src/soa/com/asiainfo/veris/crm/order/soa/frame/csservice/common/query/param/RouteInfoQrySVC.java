
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;

public class RouteInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 根据号码查询路由地州
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getEparchyCodeBySn(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");
        String eparchyCode = RouteInfoQry.getEparchyCodeBySn(serialNumber);

        IData data = new DataMap();
        data.put("EPARCHY_CODE", eparchyCode);

        IDataset dataset = new DatasetList();
        dataset.add(data);

        return dataset;
    }

    /**
     * 根据号码查询局数据
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getMofficeInfoBySn(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");

        IData data = RouteInfoQry.getMofficeInfoBySn(serialNumber);

        IDataset dataset = IDataUtil.idToIds(data);

        return dataset;
    }

    public IDataset qryEparchyCodeBySnForCrm(IData inputData) throws Exception
    {
        String serialNumber = inputData.getString("SERIAL_NUMBER");

        String eparchyCode = RouteInfoQry.getEparchyCodeBySnForCrm(serialNumber);

        IData map = new DataMap();
        map.put("EPARCHY_CODE", eparchyCode);

        return IDataUtil.idToIds(map);
    }

    public IDataset qryMofficeInfo(IData input) throws Exception
    {
        String strQueryType = input.getString("cond_QUERY_TYPE");
        IDataset result = null;

        if (strQueryType.equals("0")) // 按地州编码查询
        {
            String eparchyCode = input.getString("EPARCHY_CODE");
            result = RouteInfoQry.getMofficeInfoByEparchy(eparchyCode, getPagination());
        }
        else if (strQueryType.equals("1")) // 按手机号段查询
        {
            String snBegin = input.getString("SERIALNUMBER_S");
            String snEnd = input.getString("SERIALNUMBER_E");

            result = RouteInfoQry.getMofficeInfoBySn(snBegin, snEnd, getPagination());
        }

        if (result.size() < 1)
        {
            return new DatasetList();
        }

        return result;
    }
}
