
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.baduserinfo;

import com.ailk.cache.memcache.MemCacheFactory;
import com.ailk.cache.memcache.interfaces.IMemCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;

public class QueryBadUserInfoSVC extends CSBizService
{
    private static final long BadUserInfoserialVersionUID = 4829236996987004886L;

    /**
     * 功能：不良信息号码用户资料查询 作者：GongGuang
     */
    public IDataset queryBadUserInfo(IData data) throws Exception
    {
        QueryBadUserInfoBean bean = (QueryBadUserInfoBean) BeanManager.createBean(QueryBadUserInfoBean.class);
        IDataset result = new DatasetList();
        if (!data.getString("SERIAL_NUMBER", "").equals(""))
        {
            result = bean.queryBadUserInfoBySN(data, getPagination());
        }
        else
        {
            IMemCache cache = MemCacheFactory.getCache("shc_cache");
            String batchId = cache.get("com.ailk.personservice.busi.sundryquery.baduserinfo.ImportQueryBadUserInfoTask.batchId").toString();
            if (!batchId.equals(""))
            {
                result = bean.queryBadUserInfoByBatchID(data, batchId, getPagination());
            }
        }
        
        //调用产商品接口查询品牌名称产品名称 duhj
        if(IDataUtil.isNotEmpty(result)){
        	
        	for (int i = 0; i < result.size(); i++)
            {
                IData res = result.getData(i);
                
                String productName = UProductInfoQry.getProductNameByProductId(res.getString("PRODUCT_ID"));
                String brandName=UBrandInfoQry.getBrandNameByBrandCode(res.getString("BRAND_CODE"));
                res.put("PRODUCT_NAME", productName);
                res.put("BRAND_NAME", brandName);
                           
            }
        	
        	
        	
        }
        
        return result;
    }
}
