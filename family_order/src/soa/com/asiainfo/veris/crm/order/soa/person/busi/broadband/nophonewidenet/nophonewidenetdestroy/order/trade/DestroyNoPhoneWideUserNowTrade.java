
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidenetdestroy.order.trade;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidenetdestroy.order.requestdata.DestroyNoPhoneWideUserNowRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.ChangeSvcStateComm;
import com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.DestroyUserComm;

public class DestroyNoPhoneWideUserNowTrade extends BaseTrade implements ITrade
{
    /**
     * 修改主台帐字段
     * 
     * @author yuyj3
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
        
        destroyComm.createEndUserTrade(btd);// 用户
        destroyComm.createEndSvcInfoTrade(btd);// 服务
        destroyComm.createEndWidenetDiscntInfoTrade(btd);// 优惠
        destroyComm.createEndProductTrade(btd);// 产品
        destroyComm.createEndAttrInfoTrade(btd);// 属性
        destroyComm.createEndResInfoTrade(btd);// 资源
        destroyComm.createEndOtherTrade(btd);// 其他信息
        destroyComm.createEndPayRelationInfoTrade(btd);// 付费关系
        destroyComm.createEndUserWidenetTrade(btd);// 宽带账户资料
        destroyComm.createEndScoreAcctAndPlanTrade(btd);
        
        createEndCustTrade(btd);
        createEndCustPersonTrade(btd);
        createEndAccountTrade(btd);
        
        //几种宽带的业务类型合并了，把FTTH宽带的判断放在在下面的函数里面
    	createEndModemInfo(btd);

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
    public void createEndModemInfo(BusiTradeData btd) throws Exception
    {
    	//
        DestroyNoPhoneWideUserNowRequestData rd = (DestroyNoPhoneWideUserNowRequestData)btd.getRD();
    	
        IDataset wideList = WidenetInfoQry.getUserWidenetInfo(btd.getRD().getUca().getUserId());
        
        
        String widetype=wideList.getData(0).getString("RSRV_STR2","");
        
    	btd.getMainTradeData().setRsrvStr9(widetype);//宽带类型
    	btd.getMainTradeData().setRsrvStr6(rd.getModermReturn());//是否退还
    	
    	//只有FTTH宽带才有光猫
    	if ("1".equals(widetype) || "2".equals(widetype) || "6".equals(widetype) || "4".equals(widetype))
    	{
    		return ;
    	}
    	
    	// 将是否退光猫的操作保存在主台帐的rsrv_str1中
    	btd.getMainTradeData().setRsrvStr1(rd.getModermReturn());
    	//宽带类型
    	btd.getMainTradeData().setRsrvStr2(rd.getWideType());
    	
    	IDataset modemInfo = UserOtherInfoQry.getOtherInfoByCodeUserId(rd.getUca().getUserId(), "FTTH");;
		
    	if(IDataUtil.isNotEmpty(modemInfo))
		{
//    				rsrv_str1--光猫串号
//    				rsrv_str2--押金金额
//    				rsrv_str6--光猫型号
//    				rsrv_str7--押金状态  0,押金、1,已转移、2已退还、3,已沉淀
//    				rsrv_str8--BOSS押金转移流水
//    				rsrv_str9--移机、拆机未退光猫标志：1.移机未退光猫  2.拆机未退光猫
//    				rsrv_tag1--申领模式  0租赁，1购买，2赠送，3自备
//    				rsrv_tag2--光猫状态  1:申领，2:更改，3:退还，4:丢失
//    				rsrv_tag3--业务类型  1:开户，2:移机
//    				rsrv_date1--拆机时间，移机时间，，当拆机不退光猫的时候记录拆机时间，end_date就不修改了
			if(rd.getModermReturn().equals("1")) //个人宽带需要退光猫时截止光猫记录
			{
				OtherTradeData data = new OtherTradeData(modemInfo.first());
				data.setModifyTag(BofConst.MODIFY_TAG_DEL);
	            data.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD) + SysDateMgr.getFirstTime00000());
	            data.setRsrvStr7("2");
	            data.setRsrvTag2("3");
	            
	            btd.add(rd.getUca().getSerialNumber(), data);
			}
			else
			{ //不退光猫时，
		    	//
				OtherTradeData data = new OtherTradeData(modemInfo.first());
				data.setModifyTag(BofConst.MODIFY_TAG_UPD);
				data.setRemark("拆机不退光猫，90天内退，定时扫描");
	            
				data.setRsrvDate1(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD) + SysDateMgr.getFirstTime00000());
	            data.setRsrvStr9("2");
	            
	            btd.add(rd.getUca().getSerialNumber(), data);
			}
    		
		}
    			
    }
    
    /**
     * 终止客户信息台账表生成
     * 
     * @param btd
     * @throws Exception
     */
    public void createEndCustTrade(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        CustomerTradeData custData = btd.getRD().getUca().getCustomer();
        custData.setRemoveTag("1");
        custData.setRemoveDate(btd.getRD().getAcceptTime());
        custData.setRemark(btd.getRD().getRemark());
        custData.setModifyTag(BofConst.MODIFY_TAG_DEL);
        
        btd.add(btd.getRD().getUca().getSerialNumber(), custData);
    }
    
    
    /**
     * 终止个人客户信息台账表生成
     * 
     * @param btd
     * @throws Exception
     */
    public void createEndCustPersonTrade(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        CustPersonTradeData custPersonData = btd.getRD().getUca().getCustPerson();
        custPersonData.setRemoveTag("1");
        custPersonData.setRemoveDate(btd.getRD().getAcceptTime());
        custPersonData.setRemark(btd.getRD().getRemark());
        custPersonData.setModifyTag(BofConst.MODIFY_TAG_DEL);
        
        btd.add(btd.getRD().getUca().getSerialNumber(), custPersonData);
    }
    
    /**
     * 终止个人账户信息台账表生成
     * 
     * @param btd
     * @throws Exception
     */
    public void createEndAccountTrade(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        AccountTradeData accountTradeData = btd.getRD().getUca().getAccount();
        accountTradeData.setRemoveTag("1");
        accountTradeData.setRemoveDate(btd.getRD().getAcceptTime());
        accountTradeData.setRemark(btd.getRD().getRemark());
        accountTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
       
        btd.add(btd.getRD().getUca().getSerialNumber(), accountTradeData);
    }
    
}
