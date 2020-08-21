package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.changeRecepHallUser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.changeUser.ChangeBBossBatUserDataBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.changeUser.ChangeBBossRevsUserDataBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.changeUser.ChangeBBossUserDataBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.changeUser.CrdtMngBBossUserDataBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.DealReceptionHallMemBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.destroyRecepHallUser.DestroyJKDTUserBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

public class ChangeJKDTUserSVC extends GroupOrderService{
    private static final long serialVersionUID = -2282287814862668316L;

    /*
     * 集团BBOSS业务变更OTT处理(一笔订单多笔台账模式)
     * @date 2013-04-15
     * @author xunyl
     */
    public final IDataset crtOrder(IData map) throws Exception
    {
        // 1-获取反向接口标记
        String antiIntfFlag = map.getString(IntfField.ANTI_INTF_FLAG[0]);

        // 2- 调用bean封装成和商品数据结构一致的数据包(区分正反向接口和信控接口)
        IData returnVal = new DataMap();
        if ("1".equals(antiIntfFlag))
        {// 反向接口
            // 如果是归档报文，则进行归档处理
            if (DealReceptionHallMemBean.isReceptionHallRspFile(map, true))
            {
                return IDataUtil.idToIds(DealReceptionHallMemBean.dealGrpRspFile(map));
            }

            returnVal = ChangeBBossRevsUserDataBean.makeJKDTData(map);
            // 校验受理过程是否被迫中断
            if (null != returnVal.getString("BREAK_TYPE") && !"".equals(returnVal.getString("BREAK_TYPE")))
            {
                return returnVal.getDataset("REP_DATA");
            }
        }
        else if ("2".equals(antiIntfFlag))
        {// 信控接口

            returnVal = CrdtMngBBossUserDataBean.makeJKDTData(map);

        }
        else if ("3".equals(antiIntfFlag))
        {// 批量一键注销产品注销接口

            returnVal = ChangeBBossBatUserDataBean.makeData(map);
        }

        else
        {// 正向接口
            returnVal = ChangeBBossUserDataBean.makeJKDTData(map);
        }

        // 3- 获取商品操作类型
        IData merchInfo = returnVal.getData("MERCH_INFO");
        String merchOpType = merchInfo.getData("GOOD_INFO").getString("MERCH_OPER_CODE");

        // 4- 根据商品操作类型调用订单处理类进行处理(区分变更中的取消商品订购)
        if (GroupBaseConst.MERCH_STATUS.MERCH_CANCLE.getValue().equals(merchOpType))
        {
            DestroyJKDTUserBean bean = new DestroyJKDTUserBean();
            return bean.crtOrder(returnVal);
        }
        else
        {
            ChangeJKDTUserBean bean = new ChangeJKDTUserBean();
            return bean.crtOrder(returnVal);
        }
    }


}
