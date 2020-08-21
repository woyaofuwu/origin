
package com.asiainfo.veris.crm.order.soa.person.busi.ipexpress.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.IpExpressException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.ipexpress.order.requestdata.IpExpressRequestData;

public class IpExpressTrade extends BaseTrade implements ITrade
{

	@Override
	public void createBusiTradeData(BusiTradeData bd) throws Exception
	{
		// TODO Auto-generated method stub
		// 判断手机号码用户是否已经绑定过其他号码
		IpExpressRequestData ierd = (IpExpressRequestData) bd.getRD();
		String userId = ierd.getUca().getUserId();
		String serialNumber = ierd.getUca().getSerialNumber();
		String serialNumberA = "50" + serialNumber;
		IDataset bindInfos = RelaUUInfoQry.getUserRelationByUR(userId, "50");
		String userIdA = "";
		if (bindInfos.size() > 0)
		{// 已绑定过
			userIdA = bindInfos.getData(0).getString("USER_ID_A");
			serialNumberA = bindInfos.getData(0).getString("SERIAL_NUMBER_A");
		}
		else
		{// 需要新建虚拟用户
			userIdA = SeqMgr.getUserId();
			geneTradeUser(userIdA, serialNumberA, "111111", bd);
			geneTradeRelationIdA(serialNumber, userId, userIdA, bd);
			bd.addOpenUserAcctDayData(userIdA, "1");
			geneTradePayrelation(serialNumberA, userIdA, ierd.getUca(), bd);
			geneTradeProductIdA(serialNumberA, userIdA, bd);
			geneTradeResIdA(serialNumberA, userIdA, bd);
			geneTradeDiscnt(serialNumberA, userIdA, bd, "5901", "-1");
		}

		// 处理绑定的号码
		List<UserTradeData> ipUserInfos = ierd.getIpUserDatas();
		int delCount = 0, addCount = 0, count = RelaUUInfoQry.getAllMebByUSERIDA(userIdA, "50").size() - 1, updCount = 0;
		for (int i = 0, size = ipUserInfos.size(); i < size; i++)
		{
			UserTradeData ipUserInfo = ipUserInfos.get(i);

			// 关联inst_id
			String inst_id = SeqMgr.getInstId();

			String dealTag = ipUserInfo.getModifyTag();
			String userIdB = ipUserInfo.getUserId();
			String serialNumberB = ipUserInfo.getSerialNumber();
			if ("0".equals(dealTag))
			{
				// 新增绑定号码
				geneTradeUser(userIdB, serialNumberB, ipUserInfo.getUserPasswd(), bd);
				geneTradeAttr(ipUserInfo, bd, inst_id);
				geneTradeRelationIdB(serialNumberA, userIdA, ipUserInfo, bd);
				bd.addOpenUserAcctDayData(userIdB, "1");
				geneTradePayrelation(serialNumberB, userIdB, ierd.getUca(), bd);
				geneTradeDiscnt(serialNumberB, userIdB, bd, "487", ipUserInfo.getRsrvStr1());
				geneTradeProductIdB(userIdA, ipUserInfo, bd);
				geneTradeResIdB(userIdA, ipUserInfo, bd);
				geneTradeSvc(userIdA, ipUserInfo, bd, inst_id);
				addCount++;
			}
			else if ("1".equals(dealTag))
			{
				// 删除绑定号码
				delTradeSvc(ipUserInfo, bd);
				delTradeRes(ipUserInfo, bd);
				delTradeProduct(ipUserInfo, bd);
				delTradeDiscnt(ipUserInfo, bd);
				delTradePayrelation(ipUserInfo, bd);
				delTradeRelationIdB(ipUserInfo, bd);
				delTradeAttr(ipUserInfo, bd);
				delTradeUser(ipUserInfo, bd);

				delCount++;
			}
			else if ("2".equals(dealTag))
			{
				// 修改绑定号码信息，修改也只能改产品、密码、服务信息
				UcaData ipUca = UcaDataFactory.getUcaByUserId(userIdB);
				String newProductId = ipUserInfo.getRsrvStr1();
				// 密码
				if (!ipUserInfo.getUserPasswd().equals(ipUca.getUser().getUserPasswd()))
				{
					updTradeUser(ipUserInfo, bd);
					delTradeAttr(ipUserInfo, bd);
					geneTradeAttr(ipUserInfo, bd, inst_id);
					updCount++;
				}
				// 产品
				if (!ipUca.getProductId().equals(newProductId))
				{
					updTradeProduct(ipUserInfo, bd);
					updCount++;
				}

				// 服务
				updCount = updCount + updTradeSvc(ipUserInfo, bd);
			}
		}

		// 若该手机号码下已无绑定固定号码，删除虚拟用户信息
		if (delCount == count && count != 0 && addCount == 0)
		{
			// UserTradeData userInfoIdA = UcaDataFactory.getUcaByUserId(userIdA).getUser();
			// 需要兼容老数据缺失付费关系的问题
			IData userInfo = UcaInfoQry.qryUserInfoByUserId(userIdA);
			if (IDataUtil.isEmpty(userInfo))
			{
				CSAppException.apperr(CrmUserException.CRM_USER_189, userIdA);
			}
			UserTradeData userInfoIdA = new UserTradeData(userInfo);
			UcaData virtualUca = new UcaData();
			virtualUca.setUser(userInfoIdA);
			DataBusManager.getDataBus().setUca(virtualUca);
			delTradeRelationIdA(userIdA, bd);
			delVirtualTradeDiscnt(virtualUca, bd);
			delTradePayrelation(userInfoIdA, bd);
			delTradeRes(userInfoIdA, bd);
			delVirtualTradeProduct(virtualUca, bd);
			delVirtualUser(userInfoIdA, bd);

			// 老系统中还需要删除手机号码上的服务？需要确认
			IDataset commparas = new DatasetList();
			commparas = CommparaInfoQry.getCommparaInfoIPSvc("CSM", "112", "260", "1");
			int countCommpara = commparas.size();
			if (countCommpara > 0)
			{
				IData paraInfo = (IData) commparas.get(0);
				String svcId = paraInfo.getString("PARA_CODE2", "");
				List<SvcTradeData> stds = bd.getRD().getUca().getUserSvcBySvcId(svcId);
				SvcTradeData std = stds.get(0).clone();
				std.setModifyTag(BofConst.MODIFY_TAG_DEL);
				std.setEndDate(SysDateMgr.getSysTime());
				bd.add(serialNumber, std);
			}
		}
		if (updCount == 0 && addCount == 0 && delCount == 0)
		{
			CSAppException.apperr(IpExpressException.CRM_IPEXPRESS_12);
		}
	}

	private void delTradeAttr(UserTradeData utd, BusiTradeData bd) throws Exception
	{
		String userId = utd.getUserId();
		UcaData ucaInfo = UcaDataFactory.getUcaByUserId(userId);
		List<AttrTradeData> attrInfos = ucaInfo.getUserAttrs();
		for (int i = 0, size = attrInfos.size(); i < size; i++)
		{
			AttrTradeData attrInfo = attrInfos.get(i).clone();
			attrInfo.setModifyTag(BofConst.MODIFY_TAG_DEL);
			attrInfo.setEndDate(SysDateMgr.getSysTime());
			bd.add(utd.getSerialNumber(), attrInfo);
		}
	}

	private void delTradeDiscnt(UserTradeData utd, BusiTradeData bd) throws Exception
	{
		String userId = utd.getUserId();
		UcaData ucaInfo = UcaDataFactory.getUcaByUserId(userId);
		List<DiscntTradeData> discntInfos = ucaInfo.getUserDiscnts();
		for (int i = 0, size = discntInfos.size(); i < size; i++)
		{
			DiscntTradeData discntInfo = discntInfos.get(i).clone();
			discntInfo.setModifyTag(BofConst.MODIFY_TAG_DEL);
			discntInfo.setEndDate(SysDateMgr.getSysTime());
			bd.add(utd.getSerialNumber(), discntInfo);
		}
	}

	private void delVirtualTradeDiscnt(UcaData uca, BusiTradeData bd) throws Exception
	{
		List<DiscntTradeData> discntInfos = uca.getUserDiscnts();
		if (discntInfos != null && discntInfos.size() > 0)
		{
			for (int i = 0, size = discntInfos.size(); i < size; i++)
			{
				DiscntTradeData discntInfo = discntInfos.get(i).clone();
				discntInfo.setModifyTag(BofConst.MODIFY_TAG_DEL);
				discntInfo.setEndDate(SysDateMgr.getSysTime());
				bd.add(uca.getSerialNumber(), discntInfo);
			}
		}
	}

	private void delTradePayrelation(UserTradeData utd, BusiTradeData bd) throws Exception
	{
		String userId = utd.getUserId();
		IData payrelationInfos = UcaInfoQry.qryDefaultPayRelaByUserId(userId);

		if (IDataUtil.isNotEmpty(payrelationInfos))
		{
			PayRelationTradeData payrelationInfo = new PayRelationTradeData(payrelationInfos);
			payrelationInfo.setModifyTag(BofConst.MODIFY_TAG_DEL);
			payrelationInfo.setActTag("0");
			payrelationInfo.setEndCycleId(SysDateMgr.getNowCycle());
			bd.add(utd.getSerialNumber(), payrelationInfo);
		}
	}

	private void delTradeProduct(UserTradeData utd, BusiTradeData bd) throws Exception
	{
		String userId = utd.getUserId();
		UcaData ucaInfo = UcaDataFactory.getUcaByUserId(userId);
		List<ProductTradeData> productInfos = ucaInfo.getUserProducts();
		for (int i = 0, size = productInfos.size(); i < size; i++)
		{
			ProductTradeData productInfo = productInfos.get(i).clone();
			productInfo.setModifyTag(BofConst.MODIFY_TAG_DEL);
			productInfo.setEndDate(SysDateMgr.getSysTime());
			bd.add(utd.getSerialNumber(), productInfo);
		}
	}

	private void delVirtualTradeProduct(UcaData uca, BusiTradeData bd) throws Exception
	{
		// String userId = utd.getUserId();
		// UcaData ucaInfo = UcaDataFactory.getUcaByUserId(userId);
		List<ProductTradeData> productInfos = uca.getUserProducts();
		if (productInfos != null && productInfos.size() > 0)
		{
			for (int i = 0, size = productInfos.size(); i < size; i++)
			{
				ProductTradeData productInfo = productInfos.get(i).clone();
				productInfo.setModifyTag(BofConst.MODIFY_TAG_DEL);
				productInfo.setEndDate(SysDateMgr.getSysTime());
				bd.add(uca.getSerialNumber(), productInfo);
			}
		}
	}

	private void delTradeRelationIdA(String userIdA, BusiTradeData bd) throws Exception
	{
		IDataset relationInfos = RelaUUInfoQry.getUserRelationRole2(userIdA, "50", "1");
		if (IDataUtil.isNotEmpty(relationInfos))
		{
			RelationTradeData rtd = new RelationTradeData((IData) relationInfos.getData(0));
			rtd.setModifyTag(BofConst.MODIFY_TAG_DEL);
			rtd.setEndDate(SysDateMgr.getSysTime());
			bd.add(null, rtd);
		}
	}

	private void delTradeRelationIdB(UserTradeData utd, BusiTradeData bd) throws Exception
	{
		String userId = utd.getUserId();
		IDataset relationInfos = RelaUUInfoQry.qryByRelaUserIdB(userId, "50", null);
		RelationTradeData rtd = new RelationTradeData(relationInfos.getData(0));
		rtd.setModifyTag(BofConst.MODIFY_TAG_DEL);
		rtd.setEndDate(SysDateMgr.getSysTime());
		bd.add(bd.getRD().getUca().getSerialNumber(), rtd);
	}

	private void delTradeRes(UserTradeData utd, BusiTradeData bd) throws Exception
	{
		String userId = utd.getUserId();
		IDataset resInfos = UserResInfoQry.getUserResInfoByUserId(userId);
		if (IDataUtil.isNotEmpty(resInfos))
		{
			for (int i = 0, size = resInfos.size(); i < size; i++)
			{
				ResTradeData resInfo = new ResTradeData((IData) resInfos.get(i));
				resInfo.setModifyTag(BofConst.MODIFY_TAG_DEL);
				resInfo.setEndDate(SysDateMgr.getSysTime());
				bd.add(utd.getSerialNumber(), resInfo);
			}
		}
	}

	private void delTradeSvc(UserTradeData utd, BusiTradeData bd) throws Exception
	{
		String userId = utd.getUserId();
		UcaData ucaInfo = UcaDataFactory.getUcaByUserId(userId);
		List<SvcTradeData> svcInfos = ucaInfo.getUserSvcs();
		for (int i = 0, size = svcInfos.size(); i < size; i++)
		{
			SvcTradeData svcInfo = svcInfos.get(i).clone();
			svcInfo.setModifyTag(BofConst.MODIFY_TAG_DEL);
			svcInfo.setEndDate(SysDateMgr.getSysTime());
			bd.add(utd.getSerialNumber(), svcInfo);
		}
	}

	private void delTradeUser(UserTradeData utd, BusiTradeData bd) throws Exception
	{
		String userId = utd.getUserId();
		UcaData ucaInfo = UcaDataFactory.getUcaByUserId(userId);
		UserTradeData delUtd = ucaInfo.getUser();
		delUtd.setModifyTag(BofConst.MODIFY_TAG_DEL);
		delUtd.setRemoveTag("2");
		delUtd.setDestroyTime(SysDateMgr.getSysTime());
		bd.add(utd.getSerialNumber(), delUtd);
	}

	private void delVirtualUser(UserTradeData utd, BusiTradeData bd) throws Exception
	{
		UserTradeData delUtd = utd.clone();
		delUtd.setModifyTag(BofConst.MODIFY_TAG_DEL);
		delUtd.setRemoveTag("2");
		delUtd.setDestroyTime(SysDateMgr.getSysTime());
		bd.add(utd.getSerialNumber(), delUtd);
	}

	private void geneTradeAttr(UserTradeData utd, BusiTradeData bd, String rela_instId) throws Exception
	{
		AttrTradeData atd = new AttrTradeData();
		atd.setUserId(utd.getUserId());
		atd.setInstType("S");
		atd.setInstId(SeqMgr.getInstId());
		atd.setAttrCode("PASSWD");
		atd.setAttrValue(utd.getUserPasswd());
		atd.setStartDate(SysDateMgr.getSysTime());
		atd.setEndDate(SysDateMgr.getTheLastTime());
		atd.setModifyTag(BofConst.MODIFY_TAG_ADD);
		atd.setRelaInstId(rela_instId);
		bd.add(utd.getSerialNumber(), atd);
	}

	private void geneTradeDiscnt(String serialNumber, String userId, BusiTradeData bd, String discntCode, String productId) throws Exception
	{
		DiscntTradeData dtd = new DiscntTradeData();
		dtd.setUserIdA(bd.getRD().getUca().getUserId());
		dtd.setUserId(userId);
		dtd.setPackageId("-1");
		dtd.setProductId("-1");//""productId
		dtd.setElementId(discntCode);
		dtd.setSpecTag("0");
		dtd.setRelationTypeCode("50");
		dtd.setCampnId("0");
		dtd.setInstId(SeqMgr.getInstId());
		dtd.setStartDate(SysDateMgr.getSysTime());
		dtd.setEndDate(SysDateMgr.getTheLastTime());
		dtd.setModifyTag(BofConst.MODIFY_TAG_ADD);
		dtd.setRemark(bd.getRD().getRemark());
		bd.add(serialNumber, dtd);
	}

	private void geneTradePayrelation(String serialNumber, String userId, UcaData ucaInfo, BusiTradeData bd) throws Exception
	{
		PayRelationTradeData ptd = new PayRelationTradeData();
		ptd.setUserId(userId);
		ptd.setAcctId(ucaInfo.getAcctId());
		ptd.setPayitemCode("-1");
		ptd.setAcctPriority("0");
		ptd.setUserPriority("0");
		ptd.setBindType("1");
		ptd.setActTag("1");
		ptd.setDefaultTag("1");
		ptd.setLimitType("0");
		ptd.setLimit("0");
		ptd.setComplementTag("0");
		ptd.setInstId(SeqMgr.getInstId());
		ptd.setModifyTag(BofConst.MODIFY_TAG_ADD);
		ptd.setStartCycleId(SysDateMgr.getNowCycle());
		ptd.setEndCycleId(SysDateMgr.getEndCycle20501231());
		ptd.setRemark(bd.getRD().getRemark());
		bd.add(serialNumber, ptd);
	}

	private void geneTradeProductIdA(String serialNumber, String userIdA, BusiTradeData bd) throws Exception
	{
		ProductTradeData ptd = new ProductTradeData();
		ptd.setUserId(userIdA);
		ptd.setUserIdA("-1");
		ptd.setProductId("6010");
		ptd.setProductMode("00");
		ptd.setBrandCode("IP00");
		ptd.setMainTag("1");
		ptd.setInstId(SeqMgr.getInstId());
		ptd.setStartDate(SysDateMgr.getSysTime());
		ptd.setEndDate(SysDateMgr.getTheLastTime());
		ptd.setModifyTag(BofConst.MODIFY_TAG_ADD);
		ptd.setRemark(bd.getRD().getRemark());
		bd.add(serialNumber, ptd);
	}

	private void geneTradeProductIdB(String userIdA, UserTradeData utd, BusiTradeData bd) throws Exception
	{
		ProductTradeData ptd = new ProductTradeData();
		ptd.setUserId(utd.getUserId());
		ptd.setUserIdA(userIdA);
		ptd.setProductId(utd.getRsrvStr1());
		ptd.setProductMode("00");
		ptd.setBrandCode("IP01");
		ptd.setMainTag("1");
		ptd.setInstId(SeqMgr.getInstId());
		ptd.setStartDate(SysDateMgr.getSysTime());
		ptd.setEndDate(SysDateMgr.getTheLastTime());
		ptd.setModifyTag(utd.getModifyTag());
		ptd.setRemark(bd.getRD().getRemark());
		bd.add(utd.getSerialNumber(), ptd);
	}

	private void geneTradeRelationIdA(String serialNumber, String userId, String userIdA, BusiTradeData bd) throws Exception
	{
		RelationTradeData rtd = new RelationTradeData();
		String serialNumberA = "50" + serialNumber;
		rtd.setUserIdA(userIdA);
		rtd.setUserIdB(userId);
		rtd.setSerialNumberA(serialNumberA);
		rtd.setSerialNumberB(serialNumber);
		rtd.setRelationTypeCode("50");
		rtd.setRoleTypeCode("0");
		rtd.setRoleCodeA("0");
		rtd.setRoleCodeB("1");
		rtd.setShortCode(serialNumberA);
		rtd.setInstId(SeqMgr.getInstId());
		rtd.setStartDate(SysDateMgr.getSysTime());
		rtd.setEndDate(SysDateMgr.getTheLastTime());
		rtd.setModifyTag(BofConst.MODIFY_TAG_ADD);
		bd.add(null, rtd);
	}

	private void geneTradeRelationIdB(String serialNumberA, String userIdA, UserTradeData utd, BusiTradeData bd) throws Exception
	{
		RelationTradeData rtd = new RelationTradeData();
		rtd.setUserIdA(userIdA);
		rtd.setUserIdB(utd.getUserId());
		rtd.setSerialNumberA(serialNumberA);
		rtd.setSerialNumberB(utd.getSerialNumber());
		rtd.setRelationTypeCode("50");
		rtd.setRoleTypeCode("0");
		rtd.setRoleCodeA("0");
		rtd.setRoleCodeB("2");
		rtd.setShortCode(utd.getSerialNumber());
		rtd.setInstId(SeqMgr.getInstId());
		rtd.setStartDate(SysDateMgr.getSysTime());
		rtd.setEndDate(SysDateMgr.getTheLastTime());
		rtd.setModifyTag(utd.getModifyTag());
		bd.add(null, rtd);
	}

	private void geneTradeResIdA(String serialNumber, String userIdA, BusiTradeData bd) throws Exception
	{
		ResTradeData rtd = new ResTradeData();
		rtd.setUserId(bd.getRD().getUca().getUserId());
		rtd.setUserIdA(userIdA);
		rtd.setResTypeCode("G");
		rtd.setResCode("50" + serialNumber);
		rtd.setInstId(SeqMgr.getInstId());
		rtd.setStartDate(SysDateMgr.getSysTime());
		rtd.setEndDate(SysDateMgr.getTheLastTime());
		rtd.setModifyTag(BofConst.MODIFY_TAG_ADD);
		rtd.setRemark(bd.getRD().getRemark());
		bd.add(serialNumber, rtd);
	}

	private void geneTradeResIdB(String userIdA, UserTradeData utd, BusiTradeData bd) throws Exception
	{
		ResTradeData rtd = new ResTradeData();
		rtd.setUserId(utd.getUserId());
		rtd.setUserIdA(userIdA);
		rtd.setResTypeCode("T");
		rtd.setResCode(utd.getSerialNumber());
		rtd.setInstId(SeqMgr.getInstId());
		rtd.setStartDate(SysDateMgr.getSysTime());
		rtd.setEndDate(SysDateMgr.getTheLastTime());
		rtd.setModifyTag(utd.getModifyTag());
		rtd.setRemark(bd.getRD().getRemark());
		bd.add(utd.getSerialNumber(), rtd);
	}

	private void geneTradeSvc(String userIdA, UserTradeData utd, BusiTradeData bd, String instId) throws Exception
	{
		String[] tempPackageSvcs = utd.getRsrvStr2().split("~");
		String productId = utd.getRsrvStr1();
		for (int j = 0, tempSize = tempPackageSvcs.length; j < tempSize; j++)
		{
			String[] tempPackageSvc = tempPackageSvcs[j].split("@");
			String packageId = tempPackageSvc[0];
			String svcId = tempPackageSvc[1];
			SvcTradeData std = new SvcTradeData();
			std.setUserIdA(userIdA);
			std.setUserId(utd.getUserId());
			std.setProductId(productId);
			std.setPackageId(packageId);
			std.setElementId(svcId);

			/*IData packageInfo = PkgElemInfoQry.getElementByElementId(packageId, svcId);
			if (IDataUtil.isEmpty(packageInfo))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "未获取到订购的服务包信息！");
			}*/
			// std.setMainTag("0");MAIN_TAG要从td_b_package_element获取
			std.setMainTag("1");
			std.setInstId(instId);
			std.setStartDate(SysDateMgr.getSysTime());
			std.setEndDate(SysDateMgr.getTheLastTime());
			std.setModifyTag(utd.getModifyTag());
			bd.add(utd.getSerialNumber(), std);
			instId = SeqMgr.getInstId();
		}
	}

	private void geneTradeUser(String userId, String serialNumber, String pwd, BusiTradeData bd) throws Exception
	{
		UserTradeData utd = bd.getRD().getUca().getUser().clone();
		UcaData ucaInfo = bd.getRD().getUca();
		utd.setUserId(userId);
		utd.setEparchyCode(CSBizBean.getTradeEparchyCode());
		utd.setCityCode(CSBizBean.getVisit().getCityCode());
		utd.setUserPasswd(pwd);
		utd.setUserTypeCode("0");
		utd.setUserStateCodeset("0");
		utd.setNetTypeCode("00");
		utd.setMputeMonthFee("0");
		utd.setInStaffId(CSBizBean.getVisit().getStaffId());
		utd.setInDepartId(CSBizBean.getVisit().getDepartId());
		utd.setOpenMode("0");
		utd.setOpenStaffId(CSBizBean.getVisit().getStaffId());
		utd.setOpenDepartId(CSBizBean.getVisit().getDepartId());
		utd.setSerialNumber(serialNumber);
		utd.setPrepayTag("0");
		utd.setOpenDate(SysDateMgr.getSysTime());
		utd.setDevelopStaffId(CSBizBean.getVisit().getStaffId());
		utd.setDevelopCityCode(CSBizBean.getVisit().getCityCode());
		utd.setDevelopDepartId(CSBizBean.getVisit().getDepartId());
		utd.setDevelopEparchyCode(CSBizBean.getTradeEparchyCode());
		utd.setDevelopDate(SysDateMgr.getSysTime());
		utd.setModifyTag(BofConst.MODIFY_TAG_ADD);
		utd.setRemoveTag("0");
		bd.add(serialNumber, utd);
	}

	private void updTradeProduct(UserTradeData utd, BusiTradeData bd) throws Exception
	{
		String userId = utd.getUserId();
		UcaData ucaInfo = UcaDataFactory.getUcaByUserId(userId);
		List<ProductTradeData> ptds = ucaInfo.getUserProducts();
		ProductTradeData ptdOld = ptds.get(0).clone();
		ProductTradeData ptdNew = ptds.get(0).clone();
		ptdOld.setModifyTag(BofConst.MODIFY_TAG_DEL);
		ptdOld.setEndDate(SysDateMgr.getLastSecond(SysDateMgr.getSysTime()));
		bd.add(utd.getSerialNumber(), ptdOld);
		ptdNew.setProductId(utd.getRsrvStr1());
		ptdNew.setModifyTag(BofConst.MODIFY_TAG_ADD);
		ptdNew.setStartDate(SysDateMgr.getSysTime());
		bd.add(utd.getSerialNumber(), ptdNew);
	}

	private int updTradeSvc(UserTradeData utd, BusiTradeData bd) throws Exception
	{
		int updCount = 0;
		String userId = utd.getUserId();
		UcaData ucaInfo = UcaDataFactory.getUcaByUserId(userId);
		List<SvcTradeData> oldstds = ucaInfo.getUserSvcs();
		String[] tempPackageSvcs = utd.getRsrvStr2().split("~");
		String productId = utd.getRsrvStr1();

		// 老服务不在新增服务中，则删除
		for (int i = 0, size = oldstds.size(); i < size; i++)
		{
			SvcTradeData delstd = oldstds.get(i).clone();
			int delTag = 1;
			for (int j = 0, newSize = tempPackageSvcs.length; j < newSize; j++)
			{
				String[] tempPackageSvc = tempPackageSvcs[j].split("@");
				String packageId = tempPackageSvc[0];
				String svcId = tempPackageSvc[1];
				if (svcId.equals(delstd.getElementId()))
				{
					delTag = 0;
					break;
				}
			}
			if (delTag == 1)
			{
				delstd.setModifyTag(BofConst.MODIFY_TAG_DEL);
				delstd.setEndDate(SysDateMgr.getLastSecond(SysDateMgr.getSysTime()));
				bd.add(utd.getSerialNumber(), delstd);
				updCount++;
			}
		}

		// 新增服务不在老服务中，则新增
		for (int i = 0, size = tempPackageSvcs.length; i < size; i++)
		{
			String[] tempPackageSvc = tempPackageSvcs[i].split("@");
			String packageId = tempPackageSvc[0];
			String svcId = tempPackageSvc[1];
			int addTag = 1;
			for (int j = 0, sizeOld = oldstds.size(); j < sizeOld; j++)
			{
				SvcTradeData delstd = oldstds.get(j).clone();
				if (svcId.equals(delstd.getElementId()))
				{
					addTag = 0;
					break;
				}
			}
			if (addTag == 1)
			{
				SvcTradeData addstd = oldstds.get(0).clone();
				addstd.setModifyTag(BofConst.MODIFY_TAG_ADD);
				addstd.setStartDate(SysDateMgr.getSysTime());
				addstd.setProductId("-1");//productId
				addstd.setPackageId("-1");//packageId
				addstd.setElementId(svcId);
				bd.add(utd.getSerialNumber(), addstd);
				updCount++;
			}
		}
		return updCount;
	}

	private void updTradeUser(UserTradeData utd, BusiTradeData bd) throws Exception
	{
		String userId = utd.getUserId();
		UcaData ucaInfo = UcaDataFactory.getUcaByUserId(userId);
		UserTradeData updUtd = ucaInfo.getUser();
		updUtd.setModifyTag(BofConst.MODIFY_TAG_UPD);
		updUtd.setUserPasswd(utd.getRsrvStr3());
		bd.add(utd.getSerialNumber(), updUtd);
	}
}
