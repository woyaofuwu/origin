package com.asiainfo.veris.crm.iorder.web.family.common;

import com.ailk.biz.view.BizHttpHandler;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

/**
 * @ClassName CustInfoFieldIms
 * @Description TODO
 * @Author yuyz
 * @Date 2020/7/17 17:11
 * @Version 1.0
 */
public abstract  class CustInfoFieldIms extends PersonBasePage
{
    public abstract void setInitInfo(IData initInfo);

    public void init(IRequestCycle cycle) throws Exception {
       IData data = getData();
       setInitInfo(data);
    }
}
