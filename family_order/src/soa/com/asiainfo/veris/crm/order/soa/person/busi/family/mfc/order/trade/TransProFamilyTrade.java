package com.asiainfo.veris.crm.order.soa.person.busi.family.mfc.order.trade;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.family.mfc.MfcCommonUtil;
import com.asiainfo.veris.crm.order.soa.person.busi.family.mfc.order.requestdata.TransProFamilyReqData;

public class TransProFamilyTrade extends BaseTrade implements ITrade
{

	private static final transient Logger log = Logger.getLogger(TransProFamilyTrade.class);

	
	@Override
	public void createBusiTradeData(BusiTradeData btd) throws Exception 
	{
		TransProFamilyReqData familyRD = (TransProFamilyReqData)btd.getRD();
		if (log.isDebugEnabled())
		{
			log.debug("11111111111111111111111111TransProFamilyTrade11111111111111familyRD="+familyRD);
		}
		String operType = familyRD.getOperType();
		if("02".equals(operType)||"04".equals(operType))
		{//注销/删除成员
			closeFamily(btd,familyRD);
		}
		else
		{//开通/新增成员/停复机
			addFamily(btd,familyRD);
		}
	}

	private void addFamily(BusiTradeData btd, TransProFamilyReqData familyRD) throws Exception 
	{
		IData familyData = new DataMap();
		String custSn = familyRD.getCustomerPhone();
		IDataset members = familyRD.getProductOrderMember();
		//查询是否存在家庭网
		IData inData = new DataMap();
		inData.put("PRODUCT_OFFERING_ID", familyRD.getProductOfferingID());
		IDataset relationUULists = MfcCommonUtil.getRelationUusByUserSnRole(custSn,"MF","1",inData);
		if (log.isDebugEnabled())
		{
			log.debug("1111111111111TransProFamilyTrade11111111addFamily111111relationUULists="+relationUULists);
		}
		if(DataUtils.isEmpty(relationUULists))
		{//不存在家庭网
			//调用CreateVirtulFamilySVC接口建家 intf
//			familyData = getOrDelVirtulFamilySVC(btd,TRADE_TYPE_CRTMF);
			
			//查询主号码是否存在Moffice表
			IDataset moffice = ResCall.getMphonecodeInfo(custSn);
			if(DataUtils.isNotEmpty(moffice))
			{//主号码绑定月功能资费
				addDiscnt(btd,familyRD,"1");
			}
			//登记台账前已经建家完成
			familyData.put("USER_ID", familyRD.getUserIdA());//虚拟家庭网用户ID
			familyData.put("SERIAL_NUMBER", familyRD.getSerialNumberA());//虚拟家庭网用户号码
			//主号码绑定家庭网UU关系
			IData input = new DataMap();
			input.put("MEM_TYPE", "1");//手机号码类型1-移动号码，2-固话号码
			input.put("MEM_NUMBER", custSn);//号码
			input.put("ROLE_CODE_B", "1");//1:主号码，2：副号码
			String effTime = familyRD.getEffTime();
			String expTime = familyRD.getExpTime();
			String finishTime = familyRD.getFinishTime();
			if(DataUtils.isNotEmpty(members))
			{
				effTime = members.getData(0).getString("EFF_TIME");
				expTime = members.getData(0).getString("EXP_TIME");
				finishTime = members.getData(0).getString("FINISH_TIME");
			}
			input.put("EFF_TIME", effTime);
			input.put("EXP_TIME", expTime);
			input.put("FINISH_TIME", finishTime);
			createRelationUU(input,familyData,btd);
		}
		else
		{
			IData relation = relationUULists.getData(0);
			familyData.put("USER_ID", relation.getString("USER_ID_A"));//虚拟家庭网用户ID
			familyData.put("SERIAL_NUMBER", relation.getString("SERIAL_NUMBER_A"));//虚拟家庭网用户号码
			if(StringUtils.isBlank(familyRD.getUserIdA()))
			{//成员新增的家庭组网编码需要重新赋值
				familyRD.setUserIdA(relation.getString("USER_ID_A"));
			}
		}
		
		//成员号码处理（如果是成员新增）
		
		if(DataUtils.isNotEmpty(members))
		{
			String routeSn = btd.getRD().getUca().getSerialNumber();
			for(int i=0;i<members.size();i++)
			{
				IData member = members.getData(i);
				if(custSn.equals(member.getString("MEM_NUMBER")))
				{//主号包括在成员号码列表内，不做处理
					continue;
				}
				if(routeSn.equals(member.getString("MEM_NUMBER")))
				{//成员号码路由需要将成员号码绑上对应的资费
					addDiscnt(btd,familyRD,"2");
				}
				member.put("ROLE_CODE_B", "2");//1:主号码，2：副号码
				createRelationUU(member,familyData,btd);
			}
		}
		addMainTradeData(btd,familyRD);
	}

	private void addMainTradeData(BusiTradeData btd,TransProFamilyReqData familyRD) throws Exception 
	{
		btd.getMainTradeData().setRsrvStr1(familyRD.getProductCode());//产品编码
		btd.getMainTradeData().setRsrvStr2(familyRD.getAction());//操作类型
		btd.getMainTradeData().setRsrvStr3(familyRD.getCustomerPhone());//主号号码
		btd.getMainTradeData().setRsrvStr4(familyRD.getBusinessType());//业务类型
		btd.getMainTradeData().setRsrvStr5(familyRD.getBizVersion());//业务版本号
		btd.getMainTradeData().setRsrvStr6(familyRD.getIsSendType());//是否发送短信标记，默认0：发送
		String orderSource = familyRD.getOrderSource();
		if(StringUtils.isBlank(orderSource))
		{
			orderSource = familyRD.getOrderSourceID();
		}
		btd.getMainTradeData().setRsrvStr7(orderSource);//订单来源
	}

	private void closeFamily(BusiTradeData btd, TransProFamilyReqData familyRD) throws Exception 
	{
		String operType = familyRD.getOperType();
		String custSn = familyRD.getCustomerPhone();
		IDataset relationAll = new DatasetList();
		//主号UU关系
		IData inData = new DataMap();
		inData.put("PRODUCT_OFFERING_ID", familyRD.getProductOfferingID());
		IDataset relaUUDatas = MfcCommonUtil.getRelationUusByUserSnRole(custSn, "MF","1",inData);
		if (log.isDebugEnabled())
		{
			log.debug("1111111111111TransProFamilyTrade11111111closeFamily111111relaUUDatas="+relaUUDatas);
		}
		if(DataUtils.isNotEmpty(relaUUDatas))
		{//主号家庭网下的所有副号码UU关系
			String userIdA = relaUUDatas.getData(0).getString("USER_ID_A");
			relationAll= MfcCommonUtil.getSEL_USER_ROLEA(userIdA , "2" , "MF",inData);
		}
		if (log.isDebugEnabled())
		{
			log.debug("1111111111111TransProFamilyTrade11111111closeFamily111111relationAll="+relationAll);
		}
		if("02".equals(operType))
		{//业务注销
			//获取主号码UU表记录
			if(DataUtils.isNotEmpty(relaUUDatas))
			{
				if(DataUtils.isNotEmpty(relationAll))
				{//添加主副号UU记录
					relationAll.addAll(relaUUDatas);
				}
				else
				{
					relationAll = relaUUDatas;
				}
			}
			//删除所有UU关系记录
			deleteRelationUU(relationAll, btd ,new DataMap());
			//取消资费
			IDataset moffice = ResCall.getMphonecodeInfo(custSn);
			if(DataUtils.isNotEmpty(moffice))
			{//主号码删除资费
				delDiscnt(btd,familyRD,"1");
			}
		}
		else
		{//成员删除
			IDataset members = familyRD.getProductOrderMember();
			String routeSn = btd.getRD().getUca().getSerialNumber();
			if("0".equals(familyRD.getIsFull()))
			{//本省家庭网号码不存在，删除所有UU关系（包括主号UU关系）
				relationAll.addAll(relaUUDatas);
				IData member = new DataMap();
				if(DataUtils.isNotEmpty(members))
				{//存在成员列表，获取成员列表中的生失效时间更新UU表
					member = members.getData(0); 
				}
				deleteRelationUU(relationAll , btd , member);
			}
			else
			{//删除成员列表操作				
				if(DataUtils.isNotEmpty(members))
				{
					for(int i=0;i<members.size();i++)
					{					
						IData member = members.getData(i);
						String memberSn = member.getString("MEM_NUMBER");
						IDataset memberRelaUUDatas = MfcCommonUtil.getRelationUusByUserSnRole(memberSn , "MF" , "2",inData);
						if(DataUtils.isNotEmpty(memberRelaUUDatas))
						{
							//删除成员号码UU关系
							deleteRelationUU(memberRelaUUDatas , btd , member);
						}
					}
				}
			}
			for(int i=0;i<members.size();i++)
			{	
				if(routeSn.equals(members.getData(i).getString("MEM_NUMBER")))
				{//本次删除的副号码为路由号码的个付资费
					delDiscnt(btd,familyRD,"2");
				}
			}
		}
		addMainTradeData(btd , familyRD);
	}
	
	private boolean isThisProv(IData member) throws Exception
	{
		String memType = member.getString("MEM_TYPE");
		String eparchCode = getTradeEparchyCode().substring(0, 3);
		String memNum = member.getString("MEM_NUMBER");
		if("2".equals(memType))
		{//固话号码通过MEM_AREA_CODE判断地州
			String areaCode = member.getString("MEM_AREA_CODE");
			if(areaCode.equals(eparchCode))
			{
				return true;				
			}
		}
		else
		{//移动号码
			IDataset routeB = ResCall.getMphonecodeInfo(memNum);
			if(DataUtils.isNotEmpty(routeB))
			{//本省号码路由
				return true;	
			}
		}
		return false;
	}
	
	
	/**
	 * 签约UU关系
	 * param:号码信息,MEM_TYPE:号码类型,1-移动号码,2-固话号码;MEM_NUMBER:号码;ROLE_CODE_B:角色编码,1:主号码,2:副号码
	 * familyInfo:虚拟家庭网信息
	 * */
    private void createRelationUU(IData param , IData familyInfo , BusiTradeData btd) throws Exception
    {
    	TransProFamilyReqData familyData = (TransProFamilyReqData)btd.getRD();
    	//默认外省号码userIdb=snb
    	String isthisProv = "2";//1：本省，2：外省
    	String userIdB = param.getString("MEM_NUMBER");
    	String remark = "家庭网资料归档";
    	if(isThisProv(param))
    	{//本省号码查询用户ID
    		isthisProv = "1";
    		IData userInfo = UserInfoQry.getUserInfoBySN(userIdB);
    		userIdB = userInfo.getString("USER_ID");
    	}
    	
        RelationTradeData relationTD = new RelationTradeData();
        relationTD.setUserIdA(familyInfo.getString("USER_ID"));//家庭网虚拟ID
        relationTD.setSerialNumberA(familyInfo.getString("SERIAL_NUMBER"));//家庭网虚拟号码
        relationTD.setUserIdB(userIdB);
        relationTD.setSerialNumberB(param.getString("MEM_NUMBER"));//号码
        relationTD.setRelationTypeCode("MF");//家庭网
        relationTD.setInstId(SeqMgr.getInstId());
        relationTD.setRoleCodeB(param.getString("ROLE_CODE_B"));//区分主副省
        relationTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        if(MfcCommonUtil.PRODUCT_CODE_ZF.equals(familyData.getProductCode()) || MfcCommonUtil.PRODUCT_CODE_5G3.equals(familyData.getProductCode()) || MfcCommonUtil.PRODUCT_CODE_5G4.equals(familyData.getProductCode()) || MfcCommonUtil.PRODUCT_CODE_5G5.equals(familyData.getProductCode()) 
        		|| MfcCommonUtil.PRODUCT_CODE_TF6.equals(familyData.getProductCode()) || MfcCommonUtil.PRODUCT_CODE_TF7.equals(familyData.getProductCode()) || MfcCommonUtil.PRODUCT_CODE_TF8.equals(familyData.getProductCode()) 
        		|| MfcCommonUtil.PRODUCT_CODE_TF9.equals(familyData.getProductCode()) || MfcCommonUtil.PRODUCT_CODE_TF10.equals(familyData.getProductCode()) || MfcCommonUtil.PRODUCT_CODE_TF11.equals(familyData.getProductCode()))
        {
        	remark+="|"+familyData.getProductCode();
        }
        relationTD.setRemark(remark);//个付需要拼接产品编码
        relationTD.setRsrvStr1(isthisProv);//1：本省，2：外省
        relationTD.setRsrvStr2(familyData.getProductOfferingID());//业务订购实例ID
        relationTD.setRsrvStr3(param.getString("MEM_TYPE",""));//成员类型
        relationTD.setRsrvStr4(param.getString("MEM_AREA_CODE",""));//成员区号
        relationTD.setStartDate(SysDateMgr.getSysTime());//系统时间
        relationTD.setEndDate(SysDateMgr.decodeTimestamp(param.getString("EXP_TIME",SysDateMgr.END_DATE_FOREVER),SysDateMgr.PATTERN_STAND));//失效时间
        relationTD.setRsrvDate1(SysDateMgr.decodeTimestamp(param.getString("EFF_TIME",SysDateMgr.getSysTime()),SysDateMgr.PATTERN_STAND));//生效时间
        relationTD.setRsrvDate2(SysDateMgr.decodeTimestamp(param.getString("EXP_TIME",SysDateMgr.getSysTime()),SysDateMgr.PATTERN_STAND));//失效时间
        relationTD.setRsrvDate3(SysDateMgr.decodeTimestamp(param.getString("FINISH_TIME",SysDateMgr.getSysTime()),SysDateMgr.PATTERN_STAND));//归档时间
        relationTD.setRsrvStr5(familyData.getOperType());
        btd.add(btd.getRD().getUca().getSerialNumber(), relationTD);
    }
    
    /**
	 * 删除UU关系
	 * relaUUDatas:删除的号码集
	 * member:删除成员时更新时间及成员信息
	 * */
    private void deleteRelationUU(IDataset relaUUDatas , BusiTradeData btd,IData member) throws Exception
    {
    	TransProFamilyReqData familyRD = (TransProFamilyReqData)btd.getRD();
    	//查询号码记录
    	if (IDataUtil.isNotEmpty(relaUUDatas))
    	{
    		for (int i = 0; i < relaUUDatas.size(); i++)
    		{
    			RelationTradeData relationTD = new RelationTradeData(relaUUDatas.getData(i));
    			relationTD.setModifyTag("1");
    			relationTD.setEndDate(SysDateMgr.getSysTime());
    			relationTD.setRsrvStr2(familyRD.getProductOfferingID());//业务订购实例ID
    			relationTD.setRsrvStr5(familyRD.getOperType());
    			relationTD.setRsrvDate1(SysDateMgr.decodeTimestamp(familyRD.getEffTime(),SysDateMgr.PATTERN_STAND));//生效时间
 		        relationTD.setRsrvDate2(SysDateMgr.decodeTimestamp(familyRD.getExpTime(),SysDateMgr.PATTERN_STAND));//失效时间
 		        relationTD.setRsrvDate3(SysDateMgr.decodeTimestamp(familyRD.getFinishTime(),SysDateMgr.PATTERN_STAND));//归档时间
    			if(DataUtils.isNotEmpty(member))
    			{//单条删除更新UU表成员归档数据
    		        relationTD.setRsrvStr3(member.getString("MEM_TYPE",""));//成员类型
    		        relationTD.setRsrvStr4(member.getString("MEM_AREA_CODE",""));//成员区号
    		        relationTD.setRsrvDate1(SysDateMgr.decodeTimestamp(member.getString("EFF_TIME",SysDateMgr.getSysTime()),SysDateMgr.PATTERN_STAND));//生效时间
    		        relationTD.setRsrvDate2(SysDateMgr.decodeTimestamp(member.getString("EXP_TIME",SysDateMgr.getSysTime()),SysDateMgr.PATTERN_STAND));//失效时间
    		        relationTD.setRsrvDate3(SysDateMgr.decodeTimestamp(member.getString("FINISH_TIME",SysDateMgr.getSysTime()),SysDateMgr.PATTERN_STAND));//归档时间
    		        relationTD.setRemark("家庭网成员删除");
    			}
    			btd.add(btd.getRD().getUca().getSerialNumber(), relationTD);
    		}
    	}
    }
    
    private void addDiscnt(BusiTradeData btd,TransProFamilyReqData familyRD,String mainTag) throws Exception
    {//资费绑定
    	IDataset discntInfo = MfcCommonUtil.qryByParam134("CSM", "2018", "KSJTW_MAIN_DIS", "", mainTag, familyRD.getProductCode());
    	if(DataUtils.isEmpty(discntInfo))
    	{//不存在该资费，不做处理
    		return;
    	}
    	DiscntTradeData dt = new DiscntTradeData();
		dt.setUserId(btd.getRD().getUca().getUserId());//绑定资费
		dt.setUserIdA(familyRD.getUserIdA());
		dt.setProductId("-1");
		dt.setPackageId("-1");
		dt.setElementId(discntInfo.getData(0).getString("PARA_CODE1"));
		dt.setSpecTag("0");
		dt.setModifyTag(BofConst.MODIFY_TAG_ADD);
		dt.setStartDate(SysDateMgr.decodeTimestamp(familyRD.getFinishTime(),SysDateMgr.PATTERN_STAND));
		dt.setEndDate(SysDateMgr.decodeTimestamp(familyRD.getExpTime(),SysDateMgr.PATTERN_STAND));
		dt.setInstId(SeqMgr.getInstId());
		dt.setRelationTypeCode("MF");
		dt.setRsrvStr1(familyRD.getProductOfferingID());//业务订购实例ID,用于唯一定位家庭
		btd.add(btd.getRD().getUca().getSerialNumber(), dt);
    }
    
    private void delDiscnt(BusiTradeData btd,TransProFamilyReqData familyRD,String mainTag) throws Exception
    {
    	if (log.isDebugEnabled())
    	{
    		log.debug("1111111111111TransProFamilyTrade11111111delDiscnt111111in=");
    	}
    	IDataset discntInfo = MfcCommonUtil.qryByParam134("CSM", "2018", "KSJTW_MAIN_DIS", "", mainTag, familyRD.getProductCode());
    	if(DataUtils.isEmpty(discntInfo))
    	{//不存在该资费，不做处理
    		return;
    	}

    	IData dealDiscntInfo = new DataMap();
    	IDataset delDiscntTradeDatas = MfcCommonUtil.queryDiscntInfoByUserIdAndDisCode(btd.getRD().getUca().getUserId(), discntInfo.getData(0).getString("PARA_CODE1"), familyRD.getProductOfferingID());
    	if (log.isDebugEnabled())
    	{
    		log.debug("1111111111111TransProFamilyTrade11111111delDiscnt111111delDiscntTradeDatas="+delDiscntTradeDatas);
    	}
    	if(DataUtils.isEmpty(delDiscntTradeDatas))
    	{//用户未订购
    		//不带预留1再次查询，因为存量数据预留1没有数据
    		delDiscntTradeDatas = MfcCommonUtil.queryDiscntInfoByUserIdAndDisCode(btd.getRD().getUca().getUserId(), discntInfo.getData(0).getString("PARA_CODE1"), null);
    		if (log.isDebugEnabled())
    		{
    			log.debug("1111111111111TransProFamilyTrade11111111delDiscnt111111delDiscntTradeDatas="+delDiscntTradeDatas);
    		}
    		if(DataUtils.isEmpty(delDiscntTradeDatas))
    		{//没有资费数据不做处理
    			return;
    		}
    		for(int i=0;i<delDiscntTradeDatas.size();i++)
    		{
    			IData delDiscntData = delDiscntTradeDatas.getData(i);
    			if(StringUtils.isBlank(delDiscntData.getString("RSRV_STR1")))
    			{//存量资费数据直接获取，不考虑哪个家庭
    				dealDiscntInfo = delDiscntData;
    				break;
    			}
    		}
    	}
    	else
    	{
    		dealDiscntInfo = delDiscntTradeDatas.getData(0);
    	}
    	if (log.isDebugEnabled())
    	{
    		log.debug("1111111111111TransProFamilyTrade11111111delDiscnt111111dealDiscntInfo="+dealDiscntInfo);
    	}
    	if(DataUtils.isNotEmpty(dealDiscntInfo))
    	{
    		DiscntTradeData delDiscntTradeData = new DiscntTradeData(dealDiscntInfo);
    		delDiscntTradeData.setEndDate(SysDateMgr.getSysTime());
    		delDiscntTradeData.setRemark("资费删除");
    		delDiscntTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
    		btd.add(btd.getRD().getUca().getSerialNumber(), delDiscntTradeData);
    	}
    }
}
