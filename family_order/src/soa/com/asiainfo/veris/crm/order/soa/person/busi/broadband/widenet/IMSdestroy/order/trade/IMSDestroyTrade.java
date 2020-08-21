
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.IMSdestroy.order.trade;

import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ImpuTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserImpuInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.IMSdestroy.order.requestdata.IMSDestroyRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.DestroyUserComm;

public class IMSDestroyTrade extends BaseTrade implements ITrade
{
    /**
     * 修改主台帐字段
     * @author zhengkai5
     * @param btd
     * @throws Exception
     */
    private void appendTradeMainData(BusiTradeData<BaseTradeData> btd) throws Exception
    {
    	IMSDestroyRequestData req = (IMSDestroyRequestData)btd.getRD();
        btd.getMainTradeData().setSubscribeType("0");
        btd.getMainTradeData().setRsrvStr1(req.getWideSerialNumber());  //拆机源由
        btd.getMainTradeData().setRsrvStr2(req.getRemoveReason());  //拆机源由
    }

    /**
     * 实现父类抽象方法
     */
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        // 用户相关资料处理
        this.createEndUserInfoTrade(btd);  
        
        //虚拟用户相关资料处理
        createEndVirtualUserInfoTrade(btd);

        appendTradeMainData(btd);
        
        deleteUserImpuTradeData(btd);
        
        deleteRelationUU(btd);
        
    }

    // 终止用户相关资料订单
    private void createEndUserInfoTrade(BusiTradeData<BaseTradeData> btd ) throws Exception
    {
        DestroyUserComm destroyComm = new DestroyUserComm();
        destroyComm.createEndRelationUUTrade(btd);// uu关系
        destroyComm.createEndProductTrade(btd);// 产品
        destroyComm.createEndUserTrade(btd);// 用户
        destroyComm.createEndResInfoTrade(btd);// 资源
        destroyComm.createEndSvcInfoTrade(btd);// 服务
        destroyComm.createEndPayRelationInfoTrade(btd);// 付费关系
        destroyComm.createEndUserWidenetTrade(btd);// 宽带账户资料
        destroyComm.createEndDiscntInfoTrade(btd);   //资费台账表
        destroyComm.createEndSvcStateInfoTrade(btd);  //服务状态表
        
    }
    
    public void deleteUserImpuTradeData(BusiTradeData btd) throws Exception 
    {
    	IMSDestroyRequestData req = (IMSDestroyRequestData)btd.getRD();
    	String userId = req.getUca().getUserId();
    	IDataset userImpuInfos = UserImpuInfoQry.getImpuInfoByUserId(userId);
    	if(userImpuInfos.size()>0)
    	{
    		for(int i = 0;i<userImpuInfos.size();i++)
    		{
    			ImpuTradeData impuTrade = new ImpuTradeData(userImpuInfos.getData(i));
    			impuTrade.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
    			impuTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
    			btd.add(req.getUca().getSerialNumber(), impuTrade);
     		}
    	}
    }
    
    
    /**
     * 删除虚拟用户相关信息
     * */
    public void createEndVirtualUserInfoTrade(BusiTradeData btd) throws Exception
    {
    	IMSDestroyRequestData req = (IMSDestroyRequestData)btd.getRD();
    	
    	String userId = btd.getRD().getUca().getUserId(); //固话号码userID
    	IDataset virtualUser = RelaUUInfoQry.getAllRelationByUidBRelaTypeRoleB(userId,"MS","2");
    	if(IDataUtil.isNotEmpty(virtualUser))
    	{
    		String userIdA = virtualUser.getData(0).getString("USER_ID_A");
    		IDataset userInfoA = UserInfoQry.getUserInfoByUserId(userIdA);
    		if(IDataUtil.isNotEmpty(userInfoA))
    		{
    			String serialNumberA = userInfoA.getData(0).getString("SERIAL_NUMBER");
    			
    			//删除虚拟用户台账数据
    			deleteVirtualUserTradeData(btd,userIdA,serialNumberA);
    			
    			//删除虚拟产品台账数据
    			deleteVirtualProductTradeData(btd,userIdA,serialNumberA);
    			
    			//删除虚拟用户资费台账数据
    			deleteVirtualDiscntTradeData(btd,userIdA,serialNumberA);
    			
    			//删除虚拟用户UU台账数据
    			deleteVirtualRelationUUTradeData(btd,userIdA,serialNumberA);
    		}
    		
        }
    }
    
    
    public void deleteVirtualUserTradeData(BusiTradeData bd, String userIdA , String serialNumberA) throws Exception 
    {
    	IDataset userInfos = UserInfoQry.getUserInfoBySn(serialNumberA,"0");
    	if(IDataUtil.isNotEmpty(userInfos))
    	{
    		UserTradeData userTD = new UserTradeData(userInfos.getData(0));
    		userTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
    		userTD.setRemoveTag("2");
    		userTD.setDestroyTime(bd.getRD().getAcceptTime());
    		userTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
    		bd.add(serialNumberA, userTD);
    	}
    }
    
    public void deleteVirtualProductTradeData(BusiTradeData bd, String userIdA, String serialNumberA) throws Exception 
    {
		IDataset userProductInfos = UserProductInfoQry.queryAllUserValidMainProducts(userIdA);
    	if(IDataUtil.isNotEmpty(userProductInfos))
    	{
    		for(int i=0;i<userProductInfos.size();i++){
    			ProductTradeData productTD = new ProductTradeData(userProductInfos.getData(i));
    			productTD.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
    			productTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
    			bd.add(serialNumberA, productTD);
    		}
    	}
    }
    
    
    public void deleteVirtualDiscntTradeData(BusiTradeData bd, String userIdA, String serialNumberA) throws Exception 
    {
		IDataset discntUsers = UserDiscntInfoQry.getAllDiscntInfo(userIdA);
    	if(IDataUtil.isNotEmpty(discntUsers))
    	{
    		for(int i=0;i<discntUsers.size();i++){
		    	DiscntTradeData discntTD = new DiscntTradeData(discntUsers.getData(0));
		        discntTD.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
		        discntTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
		        bd.add(serialNumberA, discntTD);
    		}
    	}
    }
    
    //删除虚拟用户的UU关系台账数据
    public void deleteVirtualRelationUUTradeData(BusiTradeData btd,String userIdA,String serialNumberA) throws Exception 
    {
    	IDataset relationAList = RelaUUInfoQry.getAllValidRelaByUserIDA(userIdA);
        if(IDataUtil.isNotEmpty(relationAList)){
        	for (int j = 0, size = relationAList.size(); j < size; j++)
            {
                RelationTradeData tempData = new RelationTradeData(relationAList.getData(j));
                tempData.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
                tempData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                btd.add(serialNumberA, tempData);
            }
        }
    }
    
    //中止relationUU表中关联的手机用户
    public void deleteRelationUU(BusiTradeData btd) throws Exception
    {
    	
    	IMSDestroyRequestData req = (IMSDestroyRequestData)btd.getRD();
    	String wideSeriaNumber = req.getWideSerialNumber();
    	if(StringUtils.isNotEmpty(wideSeriaNumber))
    	{
    		IDataset  userInfo = UserInfoQry.getUserinfo(wideSeriaNumber);
    		if(IDataUtil.isNotEmpty(userInfo))
    		{
    			IDataset relationList = RelaUUInfoQry.getAllRelationByUidBRelaTypeRoleB(userInfo.getData(0).getString("USER_ID"),"MS","1");
    			if(IDataUtil.isNotEmpty(relationList))
    			{
    				for (int i = 0; i < relationList.size(); i++) 
    				{
    					RelationTradeData tempData = new RelationTradeData(relationList.getData(i));
    					tempData.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
    					tempData.setModifyTag(BofConst.MODIFY_TAG_DEL);
    					btd.add(req.getUca().getSerialNumber(), tempData);
    				}
    			}
    		}
    	}
    }
}
