package com.asiainfo.veris.crm.order.soa.group.demo;

import com.ailk.biz.cache.BizEnvCache;
import com.ailk.biz.cache.CrmCacheTablesCache;
import com.ailk.biz.cache.CrmCodeCodeCache;
import com.ailk.biz.cache.DBSystemTimeCache;
import com.ailk.biz.cache.errcode.ErrorCodeCache;
import com.ailk.biz.cache.safe.ServicePrivCache;
import com.ailk.cache.localcache.CacheFactory;
import com.ailk.cache.localcache.interfaces.IReadOnlyCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TestRedInterface extends CSBizService {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public IData testRedInt(IData data) throws Exception {

        /*IDataset relaInfos = RelaUUInfoQry.queryValidRelaUUByUserIDB("1119081941694054");
        System.out.println(relaInfos.toString());

        String ibsysid = data.getString("IBSYSID");
        String busiformId = data.getString("BUSIFORM_ID");
        String nodeId = data.getString("NODE_ID");
        String svcName = data.getString("SVC_NAME");
        String stepId = data.getString("STEP_ID");
        
        IData param = new DataMap();
        IDataset eweList = EweNodeQry.qryEweByBusiformId(busiformId);
        param.putAll(eweList.first());
        IDataset eweNodeList = EweNodeQry.qryEweNodeByBusiformIdAndNodeId(busiformId, nodeId);
        
        if(IDataUtil.isEmpty(eweNodeList)) {
            IData inpram = new DataMap();
            inpram.put("IS_BH", "false");
            inpram.put("BUSIFORM_ID", busiformId);
            inpram.put("NODE_ID", nodeId);
            eweNodeList = EweNodeQry.qryByBusiformIdAndNodeId(inpram);
        }
        param.putAll(eweNodeList.first());
        param.put("IBSYSID", ibsysid);
        param.put("MOVE_ESOP", "false");
        param.put("STEP_ID",stepId);*/

        //CSAppCall.call(svcName, param);

        String tableName = data.getString("TABLE_NAME");
        String cacheName = data.getString("CACHE_NAME");

        IData returnData = new DataMap();
        //version = StringUtils.replaceChars(version, ":- ", "").substring(6, 12);
        if(StringUtils.isBlank(tableName) && StringUtils.isBlank(cacheName)){
            returnData.put("RESULT","未获取到表名或缓存名无需刷新缓存");
            return returnData;
        }

        IReadOnlyCache localCache = null;
        if(StringUtils.isNotBlank(tableName)){
            localCache = CacheFactory.getReadOnlyCache(CrmCacheTablesCache.class);

            String version = (String)localCache.get(tableName);
            if(version == null){
                returnData.put("RESULT",tableName+"表无缓存");
                return returnData;
            }else{
                StringBuilder qrySql = new StringBuilder();
                qrySql.append("UPDATE CACHE_TABLES T SET T.VERSION = SYSDATE WHERE T.TABLE_NAME= :TABLE_NAME");
                IData input = new DataMap();
                input.put("TABLE_NAME",tableName);
                Dao.executeUpdate(qrySql,input,Route.CONN_CRM_CEN);
                localCache.refresh();
                returnData.put("RESULT",tableName+"表缓存刷新成功！");
                return returnData;
            }

        }else{
            if("DBSystemTimeCache".equals(cacheName)){
                localCache = CacheFactory.getReadOnlyCache(DBSystemTimeCache.class);
            }else if("BizEnvCache".equals(cacheName)){
                localCache = CacheFactory.getReadOnlyCache(BizEnvCache.class);
            }else if("ErrorCodeCache".equals(cacheName)){
                localCache = CacheFactory.getReadOnlyCache(ErrorCodeCache.class);
            }else if("CrmCodeCodeCache".equals(cacheName)){
                localCache = CacheFactory.getReadOnlyCache(CrmCodeCodeCache.class);
            }else if("CrmCacheTablesCache".equals(cacheName)){
                localCache = CacheFactory.getReadOnlyCache(CrmCacheTablesCache.class);
                StringBuilder qrySql = new StringBuilder();
                qrySql.append("UPDATE CACHE_TABLES T SET T.VERSION = SYSDATE ");
                Dao.executeUpdate(qrySql,new DataMap(),Route.CONN_CRM_CEN);
            }else if("ServicePrivCache".equals(cacheName)){
                localCache = CacheFactory.getReadOnlyCache(ServicePrivCache.class);
            }

            if(localCache != null){
                localCache.refresh();
                returnData.put("RESULT",cacheName+"缓存刷新成功！");
                return returnData;
            }
        }

        return returnData;

        //IReadOnlyCache localCache = CacheFactory.getReadOnlyCache(CrmCacheTablesCache.class);



    }

}
