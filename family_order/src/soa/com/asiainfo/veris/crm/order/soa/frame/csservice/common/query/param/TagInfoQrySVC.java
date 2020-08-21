
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class TagInfoQrySVC extends CSBizService
{
    /**
     * 序列化
     */
    private static final long serialVersionUID = 1L;

    public IDataset getCsmTagInfo(IData input) throws Exception
    {
        String sDefault = input.getString("S_DEFAULT");
        String tagCode = input.getString("TAG_CODE");
        String sType = input.getString("S_TYPE");
        String subsysCode = input.getString("SUBSYS_CODE");
        IData query = TagInfoQry.getCsmTagInfo(subsysCode, tagCode, sType, sDefault);
        IDataset ds = new DatasetList();
        ds.add(query);
        return ds;
    }

    /**
     * @author weixb3
     * @Description 获取tag表数据
     * @throws Exception
     * @param cycle
     */
    public IDataset getSysTagInfo(IData input) throws Exception
    {
        return TagInfoQry.getSysTagInfo(input);
    }

    public IDataset getTagInfo(IData input) throws Exception
    {
        String USE_TAG = "0";
        String TAG_CODE = input.getString("TAG_CODE", "");
        String EPARCHY_CODE = CSBizBean.getTradeEparchyCode();

        return TagInfoQry.getTagInfo(EPARCHY_CODE, TAG_CODE, USE_TAG, null);
    }

    /**
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getTagInfoBySubSys(IData input) throws Exception
    {
        String eparchCode = input.getString("EPARCHY_CODE");
        String tagCode = input.getString("TAG_CODE");
        String useTag = input.getString("USE_TAG");
        String subsysCode = input.getString("SUBSYS_CODE");
        IDataset query = TagInfoQry.getTagInfoBySubSys(eparchCode, tagCode, useTag, subsysCode, null);
        return query;
    }

    public IDataset getTagInfosByTagCode(IData input) throws Exception
    {

        String eparchy_code = input.getString("EPARCHY_CODE");
        String tag_code = input.getString("TAG_CODE");
        String subsys_code = input.getString("SUBSYS_CODE");
        String use_tag = input.getString("USE_TAG");

        return TagInfoQry.getTagInfosByTagCode(eparchy_code, tag_code, subsys_code, use_tag);
    }

}
