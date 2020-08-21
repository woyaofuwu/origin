package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.changeRecepHallMember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.changeMember.ChangeBBossMemDataBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.changeMember.changeBBossRevsMemDataBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.DealReceptionHallMemBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.createRecepHallMember.CreateReceptionHallMemBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.destroyRecepHallMember.DestroyReceptionHallMemBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

/**
 * @program: hain_order
 * @description:
 * @author: zhangchengzhi
 * @create: 2018-10-08 10:09
 **/

public class ChangeReceptionHallMemSVC extends GroupOrderService {

    private static final long serialVersionUID = 1L;

    IData productGoodInfos = new DataMap();// BBOSS侧的商产品信息

    /*
     * 集团BBOSS成员变更OTT处理(一笔订单多笔台账模式)
     * @date 2013-04-15
     * @author xunyl
     * @remark 成员变更时，牵涉到了成员新增，成员暂停与恢复，成员注销等操作，所以这里要分情况考虑选择调用不同的Order子类进行处理
     */
    public final IDataset crtOrder(IData map) throws Exception {

        // 1-获取反向接口标记
        String antiIntfFlag = map.getString(IntfField.ANTI_INTF_FLAG[0]);

        // 2- 定义处理标志，默认为调用成员变更的BEAN类处理
        String dealFlag = GroupBaseConst.MEM_CHANGE_DEAL_TYPE.OPER_CHANGE_MEM_CHANGE.getValue();

        // 3- 调用bean封装成和商品数据结构一致的数据包(区分正反向接口)
        IData returnVal;

        if ("1".equals(antiIntfFlag))
        {// 反向接口
            if (DealReceptionHallMemBean.isReceptionHallRspFile(map, false)) // 如果是归档报文，则进行归档处理
            {
                return IDataUtil.idToIds(DealReceptionHallMemBean.dealMebRspFile(map));
            }

            returnVal = changeBBossRevsMemDataBean.makeJKDTData(map);
        }
        else
        {
            returnVal = ChangeBBossMemDataBean.makeJKDTData(map);
            dealFlag = returnVal.getString("DEAL_TYPE");
            returnVal.remove("DEAL_TYPE");
        }
        // 3- 反向接口的数据或者正向接口成员变更的数据调用成员变更Bean类处理
        if (GroupBaseConst.MEM_CHANGE_DEAL_TYPE.OPER_CHANGE_MEM_CHANGE.getValue().equals(dealFlag))// 成员变更操作下的成员变更
        {
            ChangeReceptionHallMemBean bean = new ChangeReceptionHallMemBean();
            returnVal.put("MEB_VOUCHER_FILE_LIST", map.getString("MEB_VOUCHER_FILE_LIST", ""));
            returnVal.put("AUDIT_STAFF_ID", map.getString("AUDIT_STAFF_ID", ""));
            return bean.crtOrder(returnVal);
        } else if (GroupBaseConst.MEM_CHANGE_DEAL_TYPE.OPER_CHANGE_MEM_ADD.getValue().equals(dealFlag))// 成员变更操作下的成员新增
        {
            CreateReceptionHallMemBean bean = new CreateReceptionHallMemBean();
            returnVal.put("MEB_VOUCHER_FILE_LIST", map.getString("MEB_VOUCHER_FILE_LIST", ""));
            returnVal.put("AUDIT_STAFF_ID", map.getString("AUDIT_STAFF_ID", ""));
            return bean.crtOrder(returnVal);
        } else if (GroupBaseConst.MEM_CHANGE_DEAL_TYPE.OPER_CHANGE_MEM_DEL.getValue().equals(dealFlag))// 成员变更操作下的成员删除
        {
            DestroyReceptionHallMemBean bean = new DestroyReceptionHallMemBean();
            returnVal.put("MEB_VOUCHER_FILE_LIST", map.getString("MEB_VOUCHER_FILE_LIST", ""));
            returnVal.put("AUDIT_STAFF_ID", map.getString("AUDIT_STAFF_ID", ""));
            return bean.crtOrder(returnVal);
        }

        return new DatasetList();
    }
}
