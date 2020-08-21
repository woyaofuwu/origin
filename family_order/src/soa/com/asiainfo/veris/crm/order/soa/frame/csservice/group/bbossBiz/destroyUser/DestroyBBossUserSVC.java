
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.destroyUser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.DealBBossRspInfoBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

public class DestroyBBossUserSVC extends GroupOrderService
{

    private static final long serialVersionUID = 1L;

    /*
     * 集团BBOSS业务注销OTT处理(一笔订单多笔台账模式)
     * @date 2013-04-18
     * @author xunyl
     */
    public final IDataset dealDelBBossBiz(IData map) throws Exception
    {
        // 1-获取反向接口标记
        String antiIntfFlag = map.getString(IntfField.ANTI_INTF_FLAG[0]);

        // 2- 调用bean封装成和商品数据结构一致的数据包
        IData returnVal = new DataMap();
        if ("1".equals(antiIntfFlag))
        {// 反向接口
         // 如果是归档报文，则进行归档处理
            if (DealBBossRspInfoBean.isBBossRspFile(map, true))
            {
                return IDataUtil.idToIds(DealBBossRspInfoBean.dealGrpRspFile(map));
            }

            // 判断是不是管理节点数据
            String busiSign = map.getString("BUSI_SIGN", "");
            if (IntfField.SubTransCode.BbossGrpManagerBiz.value.equals(busiSign) // BBOSS 业务流程管理接口
                    || IntfField.SubTransCode.BbossGrpManagerBiz.value.equals(busiSign + "_1_0"))
            {
                // 获取数据封装结果
                returnVal = DestroyBBossRevsUserDataBean.makeData(map);
            }
            else
            {
                // 非管理节点下发的反向注销，走一键注销接口
                return DestroyBBossRevsUserDataBean.makeDataOneKey(map);
            }

        }
        else
        {
            // 调用bean封装成和商品数据结构一致的数据包
            returnVal = DestroyBBossUserDataBean.makeData(map);
        }

        // 3- 调用订单处理类进行处理
        DestroyBBossUserBean bean = new DestroyBBossUserBean();
        return bean.crtOrder(returnVal);
    }
}
