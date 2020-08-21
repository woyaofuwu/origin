package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.destroyRecepHallMember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.DealReceptionHallMemBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.MebCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.OutNetSNCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.destroyMember.DestroyBBossMemDataBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.destroyMember.DestroyBBossRevsMemDataBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

/**
 * @program: hain_order
 * @description: 集客大厅删除成员用户
 * @author: zhangchengzhi
 * @create: 2018-09-30 09:41
 **/

public class DestroyReceptionHallMemSVC extends GroupOrderService {

    private static final long serialVersionUID = 1L;

    public final IDataset dealReceptionHallMebBiz(IData map) throws Exception {

    	// 1-获取反向接口标记
        String antiIntfFlag = map.getString(IntfField.ANTI_INTF_FLAG[0]);
        
        //网外号码处理  daidl
    	IDataset ret=MebCommonBean.crtOutNetData(antiIntfFlag,map);
    	if(IDataUtil.isNotEmpty(ret)&&ret.getData(0).getBoolean("FLAG")){
    		IData resultData = new DataMap();
    		resultData.put("RSPCODE", "00");
    		resultData.put("RSPDESC", "成功");
    		//OutNetSNCommonBean.recordMebLog(map,resultData);
    		return ret;
    	}
    	// 2- 调用bean封装成和商品数据结构一致的数据包(区分正反向接口)
        IData returnVal=new DataMap();;

        if ("1".equals(antiIntfFlag))
        {// 反向接口
	        // 如果是归档报文，则进行归档处理
	        if (DealReceptionHallMemBean.isReceptionHallRspFile(map, false)) {
	            return IDataUtil.idToIds(DealReceptionHallMemBean.dealMebRspFile(map));
	        }
	
	        returnVal = DestroyBBossRevsMemDataBean.makeJKDTData(map);
        }
        else
        {
            returnVal = DestroyBBossMemDataBean.makeJKDTData(map);
        }
        // 3- 调用订单处理类进行处理
        DestroyReceptionHallMemBean bean = new DestroyReceptionHallMemBean();
        return bean.crtOrder(returnVal);
    }


}
