package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;

public class EsopInfoQryBean {
    /**
     * 对应老接口：ITF_EOS_QcsGrpBusi 获取esop数据初始化集团受理、变更、退订、界面
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset qryEsopGrpBusiInfo(IData data) throws Exception {
        String subTransCode = data.getString("X_SUBTRANS_CODE", "-1");
        // 返回数据定义
        IDataset results = new DatasetList();

        if (subTransCode.equals("GetEosInfoForClouder")) {
            results = getEosInfoForClouder(data);
        }

        return results;
    }

    public static IDataset getEosInfoForClouder(IData data) throws Exception {

        String ctrlClass = BizCtrlType.EsopBaseDataImpl;

        Object obj = GrpInvoker.invoker(ctrlClass, "actEsopInfo", new Object[] { data }, new Class[] { IData.class });
        return (IDataset) obj;

    }

}
