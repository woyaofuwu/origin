
package com.asiainfo.veris.crm.order.soa.person.busi.badness;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class BadnessManageSVC extends CSBizService
{

    /**
     * @Function: updateBadInfos
     * @Description: 不良信息举报归档
     * @date May 29, 2014 3:35:13 PM
     * @param data
     * @return
     * @throws Exception
     * @author longtian3
     */
    public IDataset archBadnessInfos(IData data) throws Exception
    {
        BadnessManageBean bean = new BadnessManageBean();
        return bean.archBadnessInfos(data);
    }

    /**
     * @Function: dealBadnessReleaseInfo
     * @Description: 解除黑名单
     * @date May 29, 2014 3:35:08 PM
     * @param data
     * @return
     * @throws Exception
     * @author longtian3
     */
    public IDataset dealBadnessReleaseInfo(IData data) throws Exception
    {
        BadnessManageBean bean = new BadnessManageBean();
        return bean.dealBadnessReleaseInfo(data);
    }

    // public IDataset updateBadInfos(IData data) throws Exception
    // {
    // BadnessManageBean bean = new BadnessManageBean();
    // return bean.updateBadInfos(data);
    // }

    /**
     * @Function: hastenBadnessInfos
     * @Description: 不良信息举报催办
     * @date May 29, 2014 3:34:54 PM
     * @param data
     * @return
     * @throws Exception
     * @author longtian3
     */
    public IDataset hastenBadnessInfos(IData data) throws Exception
    {
        BadnessManageBean bean = new BadnessManageBean();
        return bean.hastenBadnessInfos(data);
    }

    /**
     * @Function: restoreBadnessInfos
     * @Description: 不良信息举报回复
     * @date May 29, 2014 3:29:25 PM
     * @param data
     * @return
     * @throws Exception
     * @author longtian3
     */
    public IDataset restoreBadnessInfos(IData data) throws Exception
    {
        BadnessManageBean bean = new BadnessManageBean();
        return bean.restoreBadnessInfos(data);
    }

    /**
     * @Function: untreadBadnessInfos
     * @Description: 不良信息举报退回
     * @date May 29, 2014 3:34:44 PM
     * @param data
     * @return
     * @throws Exception
     * @author longtian3
     */
    public IDataset untreadBadnessInfos(IData data) throws Exception
    {
        BadnessManageBean bean = new BadnessManageBean();
        return bean.untreadBadnessInfos(data);
    }
}
