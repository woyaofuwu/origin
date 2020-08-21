/**
 * 
 */

package com.asiainfo.veris.crm.order.web.frame.csview.common.bmpage;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.ivalues.ISubmitData;
import com.asiainfo.veris.crm.order.pub.frame.bmframe.submitdata.util.SubmitDataParseUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.common.bm.BmServiceClient;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: BmPage.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-3-13 上午11:00:50 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-3-13 chengxf2 v1.0.0 修改原因
 */
public abstract class BmPage extends CSBasePage
{

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-25 上午10:45:26 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-25 chengxf2 v1.0.0 修改原因
     */
    public void saveContent(IRequestCycle cycle) throws Exception
    {
        IData request = this.getData();
        ISubmitData submitData = SubmitDataParseUtil.parseSubmitData(request);
        String frameId = submitData.getAttrValue("FRAME_ID");
        IData frameInfo = BmServiceClient.getFrameInfo(this, frameId);
        if (IDataUtil.isNotEmpty(frameInfo))
        {
            String svcName = frameInfo.getString("WORKFLOW_CODE");
            BmServiceClient.dealService(this, request, svcName);
        }
    }

}
