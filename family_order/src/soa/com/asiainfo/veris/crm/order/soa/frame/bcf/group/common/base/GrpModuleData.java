
package com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Obj2Xml;
import com.asiainfo.veris.crm.order.soa.frame.bcf.log.LogBaseBean;

public final class GrpModuleData
{
    private IData wd = new DataMap();

    public IDataset getAskPrintInfo()
    {
        return getMySet("ASKPRINT_INFO");
    }

    public IDataset getElementInfo()
    {
        return getMyElementSet("ELEMENT_INFO");
    }

    public IDataset getEosParam()
    {
        return getMySet("EOS");
    }

    public IDataset getGrpPackageInfo()
    {
        return getMySet("GRP_PACKAGE_INFO");
    }

    public void getMoudleInfo(IData map)
    {
        wd = map;
    }

    // 特殊处理,规则和其他地方需要元素信息,不能delete
    private IDataset getMyElementSet(String key)
    {

        IDataset ids = wd.getDataset(key);

        if (IDataUtil.isNull(ids))
        {
            ids = new DatasetList();
        }

        return ids;
    }

    private IData getMyMap(String key)
    {
        IData id = wd.getData(key);

        if (IDataUtil.isNull(id))
        {
            id = new DataMap();
        }
        else
        {
            wd.remove(key);
        }

        return id;
    }

    private IDataset getMySet(String key)
    {

        IDataset ids = wd.getDataset(key);

        if (IDataUtil.isNull(ids))
        {
            ids = new DatasetList();
        }
        else
        {
            wd.remove(key);
        }

        return ids;
    }

    // 集团 海南传的是List 湖南传的是map
    public IDataset getPlanInfo()
    {
        return getMySet("PLAN_INFO");
    }

    public String getPlanTypeCode()
    {
        return wd.getString("PLAN_TYPE_CODE");
    }

    public IDataset getPostInfo()
    {
        return getMySet("POST_INFO");
    }

    public IDataset getProductParamInfo()
    {
        return getMySet("PRODUCT_PARAM_INFO");
    }
    
    public IDataset getBlackWhiteInfo()
    {
        return getMySet("BLACK_WHITE_INFO");
    }

    public IDataset getResInfo()
    {
        return getMySet("RES_INFO");
    }
    
    public IData getDevelopStaffInfo()
    {
        return getMyMap("DEVELOP_STAFF_INFO");
    }

    private void logToFile(String strLogFile) throws Exception
    {
        Obj2Xml.toFile(LogBaseBean.LOG_PATH, strLogFile, wd);
    }
}
