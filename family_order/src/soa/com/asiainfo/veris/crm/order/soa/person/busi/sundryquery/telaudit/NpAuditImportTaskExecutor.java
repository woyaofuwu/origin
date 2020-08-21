
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.telaudit;

import java.util.Iterator;
import java.util.Map;

import com.ailk.biz.impexp.ImportTaskExecutor;
import com.ailk.cache.memcache.MemCacheFactory;
import com.ailk.cache.memcache.interfaces.IMemCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;

public class NpAuditImportTaskExecutor extends ImportTaskExecutor
{

    @Override
    public IDataset executeImport(IData paramIData, IDataset paramIDataset) throws Exception
    {

        IDataset succds = new DatasetList();
        if (IDataUtil.isNotEmpty(paramIDataset))
        {
            IData homeNetIdData = new DataMap();
            String batchId = SeqMgr.getBatchId();
            for (int i = 0, len = paramIDataset.size(); i < len; i++)
            {
                IData data = paramIDataset.getData(i);
                String npCodeStr = data.getString("NPCODE_LIST");
                String serviceType = data.getString("SERVICE_TYPE");
                String netType = data.getString("NET_TYPE");
                if (StringUtils.isNotBlank(npCodeStr))
                {

                    String[] npCodes = npCodeStr.split(",");
                    int npCodeSize = npCodes.length;
                    for (int j = 0; j < npCodeSize; j++)
                    {
                        String sn = npCodes[j];
                        String homeNetId = getHomenetid(sn, netType);
                        if (StringUtils.isNotBlank(homeNetId))
                        {
                            String key = serviceType + "|" + homeNetId;
                            if (homeNetIdData.containsKey(key))
                            {
                                homeNetIdData.put(key, homeNetIdData.getString(key) + "," + sn);
                            }
                            else
                            {
                                homeNetIdData.put(key, sn);
                            }
                        }

                    }
                }

            }

            Iterator itor = homeNetIdData.entrySet().iterator();

            String svcType = "", homeNetId = "";

            while (itor.hasNext())
            {
                Map.Entry entry = (Map.Entry) itor.next();
                String key = (String) entry.getKey();
                String[] keyArry = key.split("\\|");

                svcType = keyArry[0];
                homeNetId = keyArry[1];
                String snList = (String) entry.getValue();

                if (StringUtils.isNotBlank(snList))
                {
                    String[] npCodes = snList.split(",");
                    int len = npCodes.length;
                    for (int i = 0; i < len; i++)
                    {
                        IData tempData = new DataMap();
                        tempData.put("SERVICE_TYPE", svcType);
                        tempData.put("NPCODE_LIST", homeNetId + "|" + npCodes[i]);
                        tempData.put("CREATE_TIME", SysDateMgr.getSysTime());
                        tempData.put("SEND_TIMES", 0);
                        tempData.put("STATE", "000");
                        tempData.put("CANCEL_TAG", "0");
                        tempData.put("NPSYSID", SeqMgr.getNpAuditId());
                        succds.add(tempData);
                    }
                }

            }
            Dao.insert("TF_B_NP_AUDIT", succds, Route.CONN_UIF);
            IMemCache cache = MemCacheFactory.getCache("shc_cache");
            cache.set("com.ailk.personservice.busi.sundryquery.telaudit.NpAuditImportTaskExecutor.batchId", batchId);

        }
        return new DatasetList();
    }

    public String getHomenetid(String sn, String netType) throws Exception
    {
        IDataset ids = TradeNpQry.getValidTradeNpBySn(sn);
        String res = "";
        if (IDataUtil.isNotEmpty(ids))
        {
            IData data = ids.getData(0);

            String asp = data.getString("ASP");
            String areaCode = data.getString("AREA_CODE", "");
            if ("1".equals(asp))
            {
                res = "002";
            }
            else if ("2".equals(asp))
            {
                res = "003";
            }
            else if ("3".equals(asp))
            {
                res = "001";
            }

            if (StringUtils.isNotBlank(res))
            {
                if ("CDMA".equals(netType))
                {
                    res += "1";
                }
                else if ("CDMA2000".equals(netType))
                {
                    res += "2";
                }
                else if ("GSM".equals(netType))
                {
                    res += "3";
                }
                else if ("SCDMA".equals(netType))
                {
                    res += "4";
                }
                else if ("WCDMA".equals(netType))
                {
                    res += "5";
                }

                if (areaCode.length() == 3)
                {
                    res += areaCode;
                }
                else
                {
                    res += areaCode.substring(1);
                }

                res += "0";
            }

        }

        return res;
    }
}
