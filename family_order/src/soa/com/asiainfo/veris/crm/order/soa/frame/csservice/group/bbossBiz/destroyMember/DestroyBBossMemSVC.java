
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.destroyMember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.DealBBossRspInfoBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

public class DestroyBBossMemSVC extends GroupOrderService
{
    private static final long serialVersionUID = 1L;

    /*
     * 集团BBOSS成员业务注销OTT处理(一笔订单多笔台账模式)
     * @date 2013-04-25
     * @author xunyl
     */
    public final IDataset dealBBossMebBiz(IData map) throws Exception
    {
        // 1-获取反向接口标记
        String antiIntfFlag = map.getString(IntfField.ANTI_INTF_FLAG[0]);

        // 2- 调用bean封装成和商品数据结构一致的数据包(区分正反向接口)
        IData returnVal = new DataMap();

        if ("1".equals(antiIntfFlag))
        {// 反向接口
            // 如果是归档报文，则进行归档处理
            if (DealBBossRspInfoBean.isBBossRspFile(map, false))
            {
                return IDataUtil.idToIds(DealBBossRspInfoBean.dealMebRspFile(map));
            }

            returnVal = DestroyBBossRevsMemDataBean.makeData(map);
        }
        else
        {
            returnVal = DestroyBBossMemDataBean.makeData(map);
        }

        // 3- 调用订单处理类进行处理
        DestroyBBossMemBean bean = new DestroyBBossMemBean();
        return bean.crtOrder(returnVal);
    }

}
