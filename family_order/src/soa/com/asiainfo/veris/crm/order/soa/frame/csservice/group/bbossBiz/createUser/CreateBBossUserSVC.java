
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.createUser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.DealBBossRspInfoBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

public class CreateBBossUserSVC extends GroupOrderService
{
    private static final long serialVersionUID = 1L;

    /*
     * 集团BBOSS业务开户OTT处理(一笔订单多笔台账模式)
     * @date 2013-04-15
     * @author xunyl
     */
    public final IDataset crtOrder(IData map) throws Exception
    {
        // 1-获取反向接口标记
        String antiIntfFlag = map.getString(IntfField.ANTI_INTF_FLAG[0]);

        // 2- 调用bean封装成和商品数据结构一致的数据包
        IData returnVal = new DataMap();
        if (StringUtils.equals("1", antiIntfFlag))
        {// 反向接口
            // 如果是归档报文，则进行归档处理
            if (DealBBossRspInfoBean.isBBossRspFile(map, true))
            {
                return IDataUtil.idToIds(DealBBossRspInfoBean.dealGrpRspFile(map));
            }

            // 获取数据封装结果
            returnVal = CreateBBossRevsUserDataBean.makeData(map);

            // 校验受理过程是否被迫中断
            if (null != returnVal.getString("BREAK_TYPE") && !StringUtils.equals("", returnVal.getString("BREAK_TYPE")))
            {
                return returnVal.getDataset("REP_DATA");
            }
        }
        else
        {
            // 获取数据封装结果
            returnVal = CreateBBossUserDataBean.makeData(map);
        }

        // 3- 调用订单处理类进行处理
        CreateBBossUserBean bean = new CreateBBossUserBean();
        return bean.crtOrder(returnVal);
    }
}
