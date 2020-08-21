package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

/**
 * REQ201705020028 超套限速不限量服务开发需求
 * 超套限速不限量 
 * @author
 */
public class OverSetSpeedLimitRegAction implements ITradeAction {
    private static Logger logger = Logger.getLogger(OverSetSpeedLimitRegAction.class);

    // @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        List<DiscntTradeData> discntUsers = btd.getRD().getUca().getUserDiscnts();
		List<DiscntTradeData> discntTrades = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        String userId = btd.getRD().getUca().getUserId();
        String NewProductId =btd.getRD().getUca().getUserNewMainProductId();
        String OldProductId =btd.getRD().getUca().getProductId();
        String NextProductId ="";
        if(btd.getRD().getUca().getUserNextMainProduct()!=null){
        	NextProductId=btd.getRD().getUca().getUserNextMainProduct().getProductId();
        }
        int count = 0;
        int sum = 188;
		IDataset discntConfig = CommparaInfoQry.getCommparaAllColByParser("CSM","8888","20170524","0898");
		if(IDataUtil.isNotEmpty(discntConfig)&&discntConfig.size() > 0 ){
			sum = discntConfig.getData(0).getInt("PARA_CODE1",0);	//188！
		}
        //10004445 4G自选套餐
        
        //变更为10004445 或者保持10004445
        if ("10004445".equals(NewProductId))
        {    
            IDataset UserDiscnts = UserDiscntInfoQry.getAllDiscntByUser(userId,"20170524");
            if (discntUsers != null && discntUsers.size() > 0)
            {
                for (DiscntTradeData discntTrade : discntUsers)
                {
                    String elementId = discntTrade.getElementId();
                    if (!BofConst.MODIFY_TAG_DEL.equals(discntTrade.getModifyTag()))
                    {
                    	//查是否属于4G自选套餐优惠 查价格
                    		IDataset discntConfigList = CommparaInfoQry.getCommparaAllColByParser("CSM",elementId,"20170524","0898");
                    		if(IDataUtil.isNotEmpty(discntConfigList)&&discntConfigList.size() > 0 ){
                    			count += discntConfigList.getData(0).getInt("PARA_CODE1",0);	//只要有一个符合，不在做判断！
                    		}
                    	}
                    // 如果删除
                    if (BofConst.MODIFY_TAG_DEL.equals(discntTrade.getModifyTag()))
                    {
                    	
                    }
                 }
            }
            if(IDataUtil.isNotEmpty(UserDiscnts)&&count<sum){
        		dealDiscntTrade(btd,UserDiscnts.getData(0));
        	}
            else if(IDataUtil.isEmpty(UserDiscnts)&&count>=sum){
            	addDiscntTrade(btd,null);
        	}
        }
        //之前是10004445  现变为其他
        else if("10004445".equals(OldProductId)&&!"10004445".equals(NewProductId)){
        	IDataset UserDiscnts = UserDiscntInfoQry.getAllDiscntByUser(userId,"20170524");
        	if(IDataUtil.isNotEmpty(UserDiscnts)&&UserDiscnts.size()>0){
        		dealDiscntTrade(btd,UserDiscnts.getData(0));
        	}
        }
        //有预约产品   之前是10004445  预约为其他
        else if("10004445".equals(OldProductId)&&!"10004445".equals(NextProductId)){
        	IDataset UserDiscnts = UserDiscntInfoQry.getAllDiscntByUser(userId,"20170524");
        	if(IDataUtil.isNotEmpty(UserDiscnts)&&UserDiscnts.size()>0){
        		updateNextDiscntTrade(btd,UserDiscnts.getData(0));
        	}
        }
        else  if ("10004445".equals(NextProductId))
        {    
            IDataset UserDiscnts = UserDiscntInfoQry.getAllDiscntByUser(userId,"20170524");
            if (discntUsers != null && discntUsers.size() > 0)
            {
                for (DiscntTradeData discntTrade : discntUsers)
                {
                    String elementId = discntTrade.getElementId();
                    if (!BofConst.MODIFY_TAG_DEL.equals(discntTrade.getModifyTag()))
                    {
                    	//查是否属于4G自选套餐优惠 查价格
                    		IDataset discntConfigList = CommparaInfoQry.getCommparaAllColByParser("CSM","20170524",elementId,"0898");
                    		if( discntConfigList.size() > 0 ){
                    			count += discntConfigList.getData(0).getInt("PARA_CODE1",0);	//只要有一个符合，不在做判断！
                    		}
                    	}
                    // 如果删除
                    if (BofConst.MODIFY_TAG_DEL.equals(discntTrade.getModifyTag()))
                    {
                    	
                    }
                 }
            }
            if(IDataUtil.isNotEmpty(UserDiscnts)&&count<sum){
            	updateNextDiscntTrade(btd,UserDiscnts.getData(0));
        	}
            else if(IDataUtil.isEmpty(UserDiscnts)&&count>=sum){
            	addNextDiscntTrade(btd,null);
        	}
        }
    }

    /**
     * 终止至预约时间 超套限速不限量
     * 
     * @throws Exception .
     */
    private void updateNextDiscntTrade(BusiTradeData bd, IData data) throws Exception
    {
    	DiscntTradeData delDiscnt = new DiscntTradeData(data);
    	delDiscnt.setEndDate(bd.getRD().getUca().getUserNextMainProduct().getStartDate());
    	delDiscnt.setModifyTag(BofConst.MODIFY_TAG_UPD);
        bd.add(bd.getRD().getUca().getSerialNumber(), delDiscnt);
    }
    /**
     * 新增至预约时间 超套限速不限量
     * 
     * @throws Exception
     */
    private void addNextDiscntTrade(BusiTradeData bd, DiscntTradeData data) throws Exception
    {
    	DiscntTradeData addDiscnt = bd.getRD().getUca().getUserDiscnts().get(0).clone();
    	addDiscnt.setStartDate(bd.getRD().getUca().getUserNextMainProduct().getStartDate());
    	addDiscnt.setPackageId("-1");
    	addDiscnt.setProductId("-1");
    	addDiscnt.setElementId("20170524");
    	addDiscnt.setInstId(SeqMgr.getInstId());
    	addDiscnt.setUserIdA("-1");
    	addDiscnt.setSpecTag("0");
    	addDiscnt.setRemark("REQ201705020028超套限速不限量服务开发需求");
    	addDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
    	addDiscnt.setEndDate(SysDateMgr.END_DATE_FOREVER);
        bd.add(bd.getRD().getUca().getSerialNumber(), addDiscnt);

    }
    /**
     * 删除超套限速不限量
     * 
     * @throws Exception btd.getRD().getUca().getUserNextMainProduct().
     */
    private void dealDiscntTrade(BusiTradeData bd, IData data) throws Exception
    {
    	DiscntTradeData delDiscnt = new DiscntTradeData(data);
    	delDiscnt.setEndDate(SysDateMgr.getSysTime());
    	delDiscnt.setModifyTag(BofConst.MODIFY_TAG_DEL);
        bd.add(bd.getRD().getUca().getSerialNumber(), delDiscnt);
    }

    /**
     * 新增超套限速不限量
     * 
     * @throws Exception
     */
    private void addDiscntTrade(BusiTradeData bd, DiscntTradeData data) throws Exception
    {
    	DiscntTradeData addDiscnt = bd.getRD().getUca().getUserDiscnts().get(0).clone();
    	addDiscnt.setStartDate(SysDateMgr.getSysTime());
    	addDiscnt.setPackageId("-1");
    	addDiscnt.setProductId("-1");
    	addDiscnt.setElementId("20170524");
    	addDiscnt.setUserIdA("-1");
    	addDiscnt.setSpecTag("0");
    	addDiscnt.setRemark("REQ201705020028超套限速不限量服务开发需求");
    	addDiscnt.setInstId(SeqMgr.getInstId());
    	addDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
    	addDiscnt.setEndDate(SysDateMgr.END_DATE_FOREVER);
        bd.add(bd.getRD().getUca().getSerialNumber(), addDiscnt);

    }

}
