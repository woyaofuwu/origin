
package com.asiainfo.veris.crm.order.soa.person.busi.bat.batextractnoactivation;

import com.ailk.biz.impexp.ImportTaskExecutor;
import com.ailk.cache.memcache.MemCacheFactory;
import com.ailk.cache.memcache.interfaces.IMemCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

public class ImportQueryExtractNoActivationInfoTask extends ImportTaskExecutor
{

    @Override
    /**
     * 批量数据导入处理，提供给MQ回调
     */
    public IDataset executeImport(IData arg0, IDataset arg1) throws Exception
    {
    	IData data = new DataMap();
		SQLParser parser = new SQLParser(data);
		data.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		parser.addSQL(" delete from  TF_F_USER_QUERY_IMPORT  WHERE UPDATE_STAFF_ID = :UPDATE_STAFF_ID ");
		Dao.executeUpdate(parser, Route.CONN_CRM_CG);
        IDataset tffBadUserImports = new DatasetList(); // TF_F_BADUSER_QUERY_IMPORT
        String batchId = SeqMgr.getBatchId();
        if (IDataUtil.isNotEmpty(arg1))
        {
        	if(arg1.size()>5000)
        	{
        		CSAppException.appError("2018042720", "导入数量不得大于5000!");
        	}
        	
            for (int i = 0; i < arg1.size(); i++)
            {
                IData tffBadUserImport = new DataMap();
                tffBadUserImport.clear();
                tffBadUserImport.put("BATCH_ID", batchId);
                tffBadUserImport.put("SERIAL_NUMBER", ((IData) arg1.get(i)).getString("SERIAL_NUMBER", ""));
                tffBadUserImport.put("CREATE_TIME", SysDateMgr.getSysTime());
                tffBadUserImport.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                tffBadUserImports.add(tffBadUserImport);
            }
            Dao.insert("TF_F_USER_QUERY_IMPORT", tffBadUserImports, Route.CONN_CRM_CG);
            IMemCache cache = MemCacheFactory.getCache("shc_cache");
            cache.set("com.ailk.personservice.busi.bat.batextractnoactivation.ImportQueryExtractNoActivationInfoTask.batchId", batchId);

        }

        return new DatasetList();
    }

}
