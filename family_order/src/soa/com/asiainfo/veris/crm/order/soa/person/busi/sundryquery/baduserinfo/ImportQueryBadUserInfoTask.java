
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.baduserinfo;

import com.ailk.biz.impexp.ImportTaskExecutor;
import com.ailk.cache.memcache.MemCacheFactory;
import com.ailk.cache.memcache.interfaces.IMemCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

public class ImportQueryBadUserInfoTask extends ImportTaskExecutor
{

    @Override
    /**
     * 批量数据导入处理，提供给MQ回调
     */
    public IDataset executeImport(IData arg0, IDataset arg1) throws Exception
    {
        IDataset tffBadUserImports = new DatasetList(); // TF_F_BADUSER_QUERY_IMPORT
        String batchId = SeqMgr.getBatchId();
        if (IDataUtil.isNotEmpty(arg1))
        {
            for (int i = 0; i < arg1.size(); i++)
            {
                IData tffBadUserImport = new DataMap();
                tffBadUserImport.clear();
                tffBadUserImport.put("BATCH_ID", batchId);
                tffBadUserImport.put("SERIAL_NUMBER", ((IData) arg1.get(i)).getString("SERIAL_NUMBER", ""));
                tffBadUserImport.put("CREATE_TIME", SysDateMgr.getSysTime());
                tffBadUserImports.add(tffBadUserImport);
            }
            Dao.insert("TF_F_BADUSER_QUERY_IMPORT", tffBadUserImports, this.getTradeEparchyCode());
            IMemCache cache = MemCacheFactory.getCache("shc_cache");
            cache.set("com.ailk.personservice.busi.sundryquery.baduserinfo.ImportQueryBadUserInfoTask.batchId", batchId);

        }

        return new DatasetList();
    }

}
