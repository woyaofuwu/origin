
package com.asiainfo.veris.crm.order.web.person.userpcc;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 处理错误信息下载链接
 */
public abstract class ComDownExport extends PersonBasePage
{
    private static Logger logger = Logger.getLogger(ComDownExport.class);

    public void downMSG(IRequestCycle cycle) throws Exception
    {
        IData info = getData();

        IDataset infos = new DatasetList();
        infos.add(info);

        setInfos(infos);

    }

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

}
