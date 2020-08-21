
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestory.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestory.order.requestdata.DestroyUserNowRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.ChangeSvcStateComm;
import com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.DestroyUserComm;

public class DestroyUserNowTrade extends BaseTrade implements ITrade
{
    /**
     * 修改主台帐字段
     * 
     * @author chenzm
     * @param btd
     * @throws Exception
     */
    private void appendTradeMainData(BusiTradeData<BaseTradeData> btd) throws Exception
    {

        btd.getMainTradeData().setSubscribeType("300");
    }

    /**
     * 实现父类抽象方法
     */
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {

        // 用户相关资料处理
        this.createEndUserInfoTrade(btd);
        // 修改用户主体服务
        this.ModifyMainSvcStateByUserid(btd);// 一定要放在服务状态变更订单生成之后做。

        appendTradeMainData(btd);
    }

    // 终止用户相关资料订单
    private void createEndUserInfoTrade(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        DestroyUserComm destroyComm = new DestroyUserComm();
        destroyComm.createEndRelationUUTrade(btd);// uu关系
        destroyComm.createEndUserTrade(btd);// 用户
        destroyComm.createEndSvcInfoTrade(btd);// 服务
        destroyComm.createEndWidenetDiscntInfoTrade(btd);// 优惠
        destroyComm.createEndProductTrade(btd);// 产品
        destroyComm.createEndAttrInfoTrade(btd);// 属性
        destroyComm.createEndResInfoTrade(btd);// 资源
        destroyComm.createEndOtherTrade(btd);// 其他信息
        destroyComm.createEndPayRelationInfoTrade(btd);// 付费关系
        destroyComm.createEndUserWidenetTrade(btd);// 宽带账户资料
        
        if(btd.getTradeTypeCode().equals("625")|| btd.getTradeTypeCode().equals("615"))
        {
        	EndOtherFTTHInfoBySerialNumber(btd);
        }

        if (btd.getTradeTypeCode().equals("635") || btd.getTradeTypeCode().equals("615") || btd.getTradeTypeCode().equals("7244"))
        {
            destroyComm.createEndUserWidenetOtherTrade(btd);
        }
               

    }

    /**
     * 构建服务状态变更订单表
     * 
     * @param btd
     * @throws Exception
     */
    private void ModifyMainSvcStateByUserid(BusiTradeData btd) throws Exception
    {
        ChangeSvcStateComm bean = new ChangeSvcStateComm();
        bean.modifyMainSvcStateByUserId(btd);
    }

    /**
     * 截止宽带光猫other表记录
     * @throws Exception
     */
    public void EndOtherFTTHInfoBySerialNumber(BusiTradeData btd) throws Exception
    {
    	DestroyUserNowRequestData rd = (DestroyUserNowRequestData)btd.getRD();
    	
    	//将是否退光猫的操作保存在主台帐的rsrv_str1中
    	btd.getMainTradeData().setRsrvStr1(rd.getModermReturn());
    	
    	String serialNumber = rd.getSerialNumberA();
    	
    	IData param = new DataMap();
    	param.put("SERIAL_NUMBER", serialNumber);
    	
    	IDataset userinfo = CSAppCall.call("SS.DestroyUserNowSVC.getUserInfoBySerailNumber", param);
    	if(!userinfo.isEmpty())
    	{
    		IDataset userOtherinfo = null;
    		if(userinfo.getData(0).getString("RSRV_STR10","").equals("BNBD"))//商务宽带，不论是否退光猫，都截止光猫记录
    		{
    			userOtherinfo = CSAppCall.call("SS.DestroyUserNowSVC.queryGroupUserOtherInfo", userinfo.first());
    			if(!userOtherinfo.isEmpty())
        		{
    	    		OtherTradeData data = new OtherTradeData(userOtherinfo.first());
    				data.setModifyTag(BofConst.MODIFY_TAG_DEL);
    	            data.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD) + SysDateMgr.getFirstTime00000());
    	            btd.add(serialNumber, data);
        		}
    		}
    		else
    		{	
    			userOtherinfo = CSAppCall.call("SS.DestroyUserNowSVC.queryUserOtherInfo", userinfo.first());
    			if(!userOtherinfo.isEmpty())
        		{
    				if(rd.getModermReturn().equals("1")) //个人宽带需要退光猫时截止光猫记录
        			{
    					OtherTradeData data = new OtherTradeData(userOtherinfo.first());
        				data.setModifyTag(BofConst.MODIFY_TAG_DEL);
        	            data.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD) + SysDateMgr.getFirstTime00000());
        	            btd.add(serialNumber, data);
        			}else{ //不退光猫时，将押金字段清零
        				OtherTradeData data = new OtherTradeData(userOtherinfo.first());
        				data.setModifyTag(BofConst.MODIFY_TAG_DEL);
        				String balance = data.getRsrvStr2();
        				data.setRemark("拆机不退光猫，押金沉淀"+balance+"分");
        				data.setRsrvStr2("0");//押金字段清零
        	            data.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD) + SysDateMgr.getFirstTime00000());
        	            btd.add(serialNumber, data);
        			}
    	    		
        		}
    			
    		}
    	}
    	
    	
    }
    
}
