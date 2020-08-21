
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.plat.videomeeting;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class VideoMeetingQuerySVC extends CSBizService
{

    /**
     * 视频会议预约查询，接口名 ITF_CRM_VideoMeetingBookQry
     * 
     * @param IData
     * @return IDataset
     * @throws Exception
     * @author huanghui@asiainfo-linkage.com
     */
    public IDataset qryVideoMeetingBooking(IData param) throws Exception
    {
        return VideoMeetingQueryBean.qryVideoMeetingBooking(param);
    }

}
