/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.broadband.widenet.prereg;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: PreReg.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-3-24 上午10:29:20 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-3-24 chengxf2 v1.0.0 修改原因
 */

public abstract class PreReg extends CSBasePage
{

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-24 上午10:54:58 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-24 chengxf2 v1.0.0 修改原因
     */
    public void initPreReg(IRequestCycle cycle) throws Exception
    {
        IDataset passTypeList = pageutil.getStaticList("TD_S_PASSPORTTYPE");
        for (int i = 0; i < passTypeList.size(); i++)
        {
            IData passType = passTypeList.getData(i);
            if (passType.containsValue("B") || passType.containsValue("D"))
            {
                passTypeList.remove(i); // 过滤证件类型并set到页面 过滤学生证和集团客户证件
            }
        }
        this.setPassTypeList(passTypeList);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-5-21 下午04:39:39 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-5-21 chengxf2 v1.0.0 修改原因
     */
    public void saveContent(IRequestCycle cycle) throws Exception
    {
        IData request = this.getData();
        IDataset output = CSViewCall.call(this, "SS.PreRegService.dealService", request);
        this.setAjax(output);
    }

    public abstract void setPassTypeList(IDataset passTypeList);
}
