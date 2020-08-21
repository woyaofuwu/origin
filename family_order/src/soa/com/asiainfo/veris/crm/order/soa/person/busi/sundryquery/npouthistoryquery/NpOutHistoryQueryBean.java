
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.npouthistoryquery;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;

public class NpOutHistoryQueryBean extends CSBizBean
{

    public IDataset getOutNpInfos(IData param, Pagination page) throws Exception
    {

        String npout_query_type = param.getString("NPOUT_QUERY_TYPE");

        String serial_number = param.getString("SERIAL_NUMBER");

        String startDate = param.getString("START_DATE");

        String endDate = param.getString("END_DATE");

        IDataset ids = new DatasetList();

        if ("0".equals(npout_query_type))
        {
            ids = TradeNpQry.getNpOutSuccInfo(serial_number, startDate, endDate, page);
        }

        if ("1".equals(npout_query_type))
        {
            ids = TradeNpQry.getNpOutFailInfos(serial_number, startDate, endDate, page);
        }

        if ("2".equals(npout_query_type))
        {

            ids = TradeNpQry.getNpSoaByUserIdStartDateEndDate(serial_number, startDate, endDate, page);
            IDataset newIds = new DatasetList();
            if (IDataUtil.isNotEmpty(ids))
            {
                for (int i = 0, len = ids.size(); i < len; i++)
                {
                    IData data = ids.getData(i);
                    IDataset list = TradeNpQry.getNpOutRefuseInfos(data.getString("USER_ID"));
                    if (IDataUtil.isNotEmpty(list))
                    {
                        data.putAll(list.getData(0));
                        newIds.add(data);
                    }
                }

            }

            ids = newIds;
        }

        if (IDataUtil.isNotEmpty(ids))
        {

            for (int i = 0, len = ids.size(); i < len; i++)
            {
                IData data = ids.getData(i);
                IDataset custinfos = TradeNpQry.queryGroupInfoByUseridCustid(data.getString("USER_CUST_ID"), data.getString("CUST_USER_ID"));
                if (IDataUtil.isNotEmpty(custinfos))
                {
                    data.put("GROUP_ID", custinfos.getData(0).getString("GROUP_ID"));
                    data.put("GROUP_CUST_NAME", custinfos.getData(0).getString("CUST_NAME"));
                    data.put("MEMBER_KIND", custinfos.getData(0).getString("DATA_NAME"));
                    data.put("CUST_MANAGER_NAME", custinfos.getData(0).getString("STAFF_NAME"));
                    data.put("CUST_MANAGER_PHONE", custinfos.getData(0).getString("SERIAL_NUMBER"));
                    data.put("CUST_CITY", custinfos.getData(0).getString("AREA_NAME"));
                }

                // user_id_b 老系统根本没有查询这个 这里肯定查不到数据
                IDataset vpmnInfos = TradeNpQry.queryVPMNInfoByUserid(data.getString("USER_ID_A"), data.getString("USER_USER_ID", ""));
                if (IDataUtil.isNotEmpty(vpmnInfos))
                {
                    data.put("VPN_NO", vpmnInfos.getData(0).getString("VPN_NO"));
                    data.put("VPN_NAME", vpmnInfos.getData(0).getString("VPN_NAME"));
                    data.put("VPN_MANAGER_NAME", vpmnInfos.getData(0).getString("STAFF_NAME"));
                    data.put("VPN_MANAGER_PHONE", vpmnInfos.getData(0).getString("SERIAL_NUMBER"));
                }
            }

        }

        return ids;
    }
    
    
    
    
    public IDataset getOutNpInfos(IData param) throws Exception
    {

        String npout_query_type = param.getString("NPOUT_QUERY_TYPE");

        String serial_number = param.getString("SERIAL_NUMBER");

        String startDate = param.getString("START_DATE");

        String endDate = param.getString("END_DATE");

        IDataset ids = new DatasetList();

        if ("0".equals(npout_query_type))
        {
            ids = TradeNpQry.getNpOutSuccInfo(serial_number, startDate, endDate, null);
        }

        if ("1".equals(npout_query_type))
        {
            ids = TradeNpQry.getNpOutFailInfos(serial_number, startDate, endDate, null);
        }

        if ("2".equals(npout_query_type))
        {

            ids = TradeNpQry.getNpSoaByUserIdStartDateEndDate(serial_number, startDate, endDate, null);
            IDataset newIds = new DatasetList();
            if (IDataUtil.isNotEmpty(ids))
            {
                for (int i = 0, len = ids.size(); i < len; i++)
                {
                    IData data = ids.getData(i);
                    IDataset list = TradeNpQry.getNpOutRefuseInfos(data.getString("USER_ID"));
                    if (IDataUtil.isNotEmpty(list))
                    {
                        data.putAll(list.getData(0));
                        newIds.add(data);
                    }
                }

            }

            ids = newIds;
        }

        if (IDataUtil.isNotEmpty(ids))
        {

            for (int i = 0, len = ids.size(); i < len; i++)
            {
                IData data = ids.getData(i);
                String brand_name = UBrandInfoQry.getBrandNameByBrandCode(data.getString("BRAND_CODE"));
                data.put("BRAND_NAME", brand_name);
                IDataset custinfos = TradeNpQry.queryGroupInfoByUseridCustid(data.getString("USER_CUST_ID"), data.getString("CUST_USER_ID"));
                if (IDataUtil.isNotEmpty(custinfos))
                {
                    data.put("GROUP_ID", custinfos.getData(0).getString("GROUP_ID"));
                    data.put("GROUP_CUST_NAME", custinfos.getData(0).getString("CUST_NAME"));
                    data.put("MEMBER_KIND", custinfos.getData(0).getString("DATA_NAME"));
                    data.put("CUST_MANAGER_NAME", custinfos.getData(0).getString("STAFF_NAME"));
                    data.put("CUST_MANAGER_PHONE", custinfos.getData(0).getString("SERIAL_NUMBER"));
                    data.put("CUST_CITY", custinfos.getData(0).getString("AREA_NAME"));
                }

                // user_id_b 老系统根本没有查询这个 这里肯定查不到数据
                IDataset vpmnInfos = TradeNpQry.queryVPMNInfoByUserid(data.getString("USER_ID_A"), data.getString("USER_USER_ID", ""));
                if (IDataUtil.isNotEmpty(vpmnInfos))
                {
                    data.put("VPN_NO", vpmnInfos.getData(0).getString("VPN_NO"));
                    data.put("VPN_NAME", vpmnInfos.getData(0).getString("VPN_NAME"));
                    data.put("VPN_MANAGER_NAME", vpmnInfos.getData(0).getString("STAFF_NAME"));
                    data.put("VPN_MANAGER_PHONE", vpmnInfos.getData(0).getString("SERIAL_NUMBER"));
                }
            }

        }

        return ids;
    }


}
