/**
 * @Title: BofTest.java
 * @Package com.ailk.groupservice.demo.bof
 * @Description: TODO(用一句话描述该文件做什么)
 * @author A18ccms A18ccms_gmail_com
 * @date Feb 21, 2014 3:30:33 PM
 * @version V1.0
 */

package com.asiainfo.veris.crm.order.soa.group.demo.bof;

import com.ailk.biz.process.BizProcess;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

public class TranTest11 extends BizProcess
{

    public static void main(String[] args)
    {
        // TODO Auto-generated method stub
        IData input = new DataMap();
        input.put("SN", "151111197501");
        // input.put("X_TRANS_CODE", "SS.TestSVC.tranSvc");
        TranTest11 test = new TranTest11();
        test.setGroup("saleserv");
        test.setRouteId("0731");
        test.start(input);
    }

    @Override
    public void run() throws Exception
    {

        IDataset idataset = CSAppCall.call("http://127.0.0.1:8080/groupserv/service", "SS.TestSVC.tranSvc", getInput(), false);

    }

}
