package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.createRecepHallUser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.DealReceptionHallMemBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.createUser.CreateBBossRevsUserDataBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.createUser.CreateBBossUserDataBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.createUser.CreateJKDTUserBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

public class CreateJKDTUserSVC extends GroupOrderService{
    /*
     * 集客大厅
     * @date
     * @author
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
            if (DealReceptionHallMemBean.isReceptionHallRspFile(map, true))
            {
                return IDataUtil.idToIds(DealReceptionHallMemBean.dealGrpRspFile(map));
            }

            // 获取数据封装结果
            returnVal = CreateBBossRevsUserDataBean.makeJKDTData(map);

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
        CreateJKDTUserBean bean = new CreateJKDTUserBean();
        return bean.crtOrder(returnVal);
    }
}
