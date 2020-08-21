package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetorderdestroycancel.order.trade;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class OrderDestroyCancelTrade extends BaseTrade implements ITrade
{
    /**
     * 实现父类抽象方法
     */
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
    	String serialNumber = btd.getRD().getUca().getSerialNumber();
    	
        List<DiscntTradeData> discntList = btd.getRD().getUca().getUserDiscnts();
        if (discntList.size() > 0)
        {
            for (int i = 0, size = discntList.size(); i < size; i++)
            {
            	DiscntTradeData disctnData = new DiscntTradeData(discntList.get(i).toData());
                disctnData.setEndDate(disctnData.getRsrvStr1());//恢复原有优惠时间
                disctnData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                disctnData.setRemark("宽带预约拆机取消，已恢复原有优惠时间");
                
                btd.add(serialNumber, disctnData);
            }
        }
        
        String mobileNumber = "";
    	if(serialNumber.startsWith("KD_")){
    		mobileNumber = serialNumber.substring(3);
    	}
    	IDataset userInfo = UserInfoQry.getUserInfoBySerailNumber("0", mobileNumber);
    	//找不到就是集团商务宽带，集团宽带没有候鸟
    	if(IDataUtil.isEmpty(userInfo)){
    		return;
    	}
    	UcaData ucaData = UcaDataFactory.getNormalUca(mobileNumber);
		String discntCodeList = "84013241,84013242";
        List<DiscntTradeData> discntList2 = ucaData.getUserDiscntsByDiscntCodeArray(discntCodeList);  
        if (discntList2.size() > 0)
        {
            for (int i = 0, size = discntList2.size(); i < size; i++)
            {
            	DiscntTradeData disctnData = new DiscntTradeData(discntList2.get(i).toData());
                disctnData.setEndDate(disctnData.getRsrvStr1());//恢复原有优惠时间
                disctnData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                disctnData.setRemark("宽带预约拆机取消，恢复原有优惠时间");
                
                btd.add(mobileNumber, disctnData);
            }
        }
    }
}
