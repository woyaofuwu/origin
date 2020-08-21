
package com.asiainfo.veris.crm.order.web.frame.csview.group.common.util;

import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;

public class GroupCacheUtilHttpHandler extends CSBizHttpHandler
{

    public void saveSelectElementsCache() throws Exception
    {
        String userId = getData().getString("ID");
        String productId = getData().getString("PRODUCT_ID");
        String selectElementStr = getData().getString("SELECTED_ELEMENTS");
        String selectGrpPackagestr = getData().getString("SELECTED_GRPPACKAGE_LIST");
        String tradeTypeCode = getData().getString("TRADE_TYPE_CODE");
        IDataset selectElems = new DatasetList();
        if (!StringUtils.isEmpty(selectElementStr))
            selectElems = new DatasetList(selectElementStr);

        IDataset userGrpPackge = new DatasetList();
        if (!StringUtils.isEmpty(selectGrpPackagestr))
            userGrpPackge = new DatasetList(selectGrpPackagestr);

        IData selectElement = new DataMap();
        selectElement.put("SELECTED_ELEMENTS", selectElems);
        if (IDataUtil.isNotEmpty(userGrpPackge))
            selectElement.put("SELECTED_GRPPACKAGE_LIST", userGrpPackge);
        String cacheKey = CacheKey.getSelectElemtInfoKey(userId, productId, tradeTypeCode);
        SharedCache.set(cacheKey, selectElement, 1200);

    }
}
