package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.createRecepHallMember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.DealReceptionHallMemBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.MebCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.OutNetSNCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.createMember.CreateBBossMemDataBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.createMember.CreateBBossRevsMemDataBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

/**
 * @program: hain_order
 * @description: 集客大厅版新增成员
 * @author: zhangchengzhi
 * @create: 2018-09-28 18:45
 **/

public class CreateReceptionHallMemSVC extends GroupOrderService {

    private static final long serialVersionUID = 1L;

    /**
     * 新增成员
     *
     * @date 2018-09-28
     * @author zhangcz3
     */
    public final IDataset crtOrder(IData map) throws Exception {

    	// 1-获取反向接口标记
        String antiIntfFlag = map.getString(IntfField.ANTI_INTF_FLAG[0]);
        
        //网外号码处理   daidl
        IDataset ret=MebCommonBean.crtOutNetData(antiIntfFlag,map);
        if(IDataUtil.isNotEmpty(ret)&&ret.getData(0).getBoolean("FLAG")){
    		IData resultData = new DataMap();
    		resultData.put("RSPCODE", "00");
    		resultData.put("RSPDESC", "成功");
    		//OutNetSNCommonBean.recordMebLog(map,resultData);
    		return ret;
    	}
        
        
        IData returnVal;
        if ("1".equals(antiIntfFlag))
			
        {// 反向接口
			System.out.println("pxy-- CreateReceptionHallMemSVC" + map);
	        if (DealReceptionHallMemBean.isReceptionHallRspFile(map, false)) // 区别旧版 是否归档
	        {
	            return IDataUtil.idToIds(DealReceptionHallMemBean.dealMebRspFile(map)); //idata cast idataset ,更新台账
	        }
	        // 2-2 反向发起的签约关系同步报文  非归档则生成订单
	        returnVal = CreateBBossRevsMemDataBean.makeJKDTData(map);
        }
        else
        {
            returnVal = CreateBBossMemDataBean.makeJKDTData(map);
        }

        // 3- 调用订单处理类进行处理
        CreateReceptionHallMemBean bean = new CreateReceptionHallMemBean();
        return bean.crtOrder(returnVal);

    }

}
