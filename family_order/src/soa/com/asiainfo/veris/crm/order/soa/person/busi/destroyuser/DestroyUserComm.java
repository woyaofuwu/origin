
package com.asiainfo.veris.crm.order.soa.person.busi.destroyuser;

import java.util.List;

import com.ailk.biz.util.StaticUtil;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmAccountException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.acct.UAcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustFamilyMebTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustFamilyTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ElementTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.IntegralAcctTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.IntegralPlanTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.NetNpTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ScoreRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ShareInfoTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ShareRelaTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ShareTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.WideNetTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.WidenetOtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.AcctDayDateUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustFamilyInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustFamilyMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.score.ScoreAcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.score.ScorePlanInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.score.ScoreRelationQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sharemeal.ShareInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestroynew.order.requestdata.DestroyUserNowRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.cpe.CPEActiveBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SmsTradeData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: DestroyUserComm.java
 * @Description: 销户业务公用类
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-4-9 下午4:22:13
 */
public class DestroyUserComm
{
    private final static Logger logger = Logger.getLogger(DestroyUserComm.class);

    // 增加服务订单数据
    private void addSvcStateData(BusiTradeData btd, String serialNumber, SvcStateTradeData nowData) throws Exception
    {
        List<SvcStateTradeData> svcStateDataList = btd.getTradeDatas(TradeTableEnum.TRADE_SVCSTATE);
        // 排重?
        // if (null != svcStateDataList && svcStateDataList.size() > 0)
        // {
        // for (int i = 0,size=svcStateDataList.size(); i <size ; i++)
        // {
        // SvcStateTradeData tradeData = svcStateDataList.get(i);
        // if (StringUtils.equals(tradeData.getUserId(), nowData.getUserId()) &&
        // StringUtils.equals(tradeData.getServiceId(), nowData.getServiceId()) &&
        // StringUtils.equals(tradeData.getStateCode(), nowData.getStateCode()))
        // {
        // svcStateDataList.remove(i);
        // i--;
        // }
        // }
        // }
        btd.add(serialNumber, nowData);
    }

    private void createEndAllShareInfoTradeByShareId(BusiTradeData<BaseTradeData> btd, String shareId) throws Exception
    {
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        String mainUserId = btd.getRD().getUca().getUserId();;
        IDataset allShareDataset = ShareInfoQry.queryAllShare(shareId, mainUserId);
        String firstDayNextAcct = AcctDayDateUtil.getFirstDayNextAcct(btd.getRD().getUca().getUserId());
        String lastDayTimeThisAcct = SysDateMgr.getLastSecond(firstDayNextAcct);
        if (IDataUtil.isNotEmpty(allShareDataset))
        {
        	for (int j = 0, jCount = allShareDataset.size(); j < jCount; j++)
            {
        		IData tempdData = allShareDataset.getData(j);
        		ShareTradeData shareTradeData = new ShareTradeData(tempdData);
                shareTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                shareTradeData.setEndDate(lastDayTimeThisAcct);
                btd.add(serialNumber, shareTradeData);
            }
        }

        IDataset allMemberShareDataset = ShareInfoQry.queryMember(mainUserId);
        for (int j = 0, jCount = allMemberShareDataset.size(); j < jCount; j++)
        {
            IData tempdData = allMemberShareDataset.getData(j);
            ShareRelaTradeData tempShareRelaTradeData = new ShareRelaTradeData(tempdData);
            tempShareRelaTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
            tempShareRelaTradeData.setEndDate(lastDayTimeThisAcct);
            btd.add(serialNumber, tempShareRelaTradeData);
            shareId = tempShareRelaTradeData.getShareId();
        }

        IDataset shareInfoDataset = ShareInfoQry.queryAllShareInfo(mainUserId);
        if (IDataUtil.isNotEmpty(shareInfoDataset))
        {
        	for (int j = 0, jCount = allShareDataset.size(); j < jCount; j++)
            {
        		IData tempdData = shareInfoDataset.getData(j);
        		ShareInfoTradeData shareInfoTradeData = new ShareInfoTradeData(tempdData);
                shareInfoTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                shareInfoTradeData.setEndDate(lastDayTimeThisAcct);
                btd.add(serialNumber, shareInfoTradeData);
            }
            
        }
    }

    /**
     * 终止用户属性信息台账数据
     * 
     * @param btd
     * @throws Exception
     */
    public void createEndAttrInfoTrade(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        String userId = btd.getRD().getUca().getUserId();
        List<AttrTradeData> attrList = btd.getRD().getUca().getUserAttrs();
        if (attrList.size() > 0)
        {
            String firstDayNextAcct = AcctDayDateUtil.getFirstDayNextAcct(userId);
            // 下账期第一天的前一秒
            String lastSecondFirstDayNextAcct = SysDateMgr.getLastSecond(firstDayNextAcct);
            for (int i = 0, size = attrList.size(); i < size; i++)
            {
                AttrTradeData tempTradeData = attrList.get(i);
                // 取modifytag==USER的，因为如果状态=USER 就代表该记录是没有被处理的，所以此节点要处理掉，
                // 如果是其他状态的，说明是被其他节点处理过了。就不要再处理了
                if (StringUtils.equals(BofConst.MODIFY_TAG_USER, tempTradeData.getModifyTag()))
                {
                    AttrTradeData data = tempTradeData.clone();
                    data.setModifyTag(BofConst.MODIFY_TAG_DEL);

                    String tempEndDate = tempTradeData.getEndDate();
                    String instType = tempTradeData.getInstType();
                    if (StringUtils.equals("S", instType))// 服务
                    {
                        data.setEndDate(btd.getRD().getAcceptTime());
                    }
                    else if (StringUtils.equals("D", instType))// 优惠
                    {
                        // 只处理失效时间大于等于下个月月初的数据
                        if (SysDateMgr.getTimeDiff(firstDayNextAcct, tempEndDate, SysDateMgr.PATTERN_STAND) <= 0)
                        {
                            data.setEndDate(lastSecondFirstDayNextAcct);
                        }
                        else
                        {
                            continue;
                        }
                    }
                    else
                    // 其他全部立即终止
                    {
                        data.setEndDate(btd.getRD().getAcceptTime());
                    }
                    btd.add(serialNumber, data);
                }
            }
        }
    }

    private void createEndCustFamilyMemTradeByUserIdB(String userIdB, BusiTradeData<BaseTradeData> btd) throws Exception
    {
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        IData userList = UcaInfoQry.qryUserInfoByUserId(userIdB);
        if (IDataUtil.isNotEmpty(userList))
        {
            String memCustId = userList.getString("CUST_ID");
            IDataset custFmyMemDataset = CustFamilyMebInfoQry.getFamilyMem(memCustId, "0");
            if (IDataUtil.isNotEmpty(custFmyMemDataset))
            {
                CustFamilyMebTradeData tempData = new CustFamilyMebTradeData(custFmyMemDataset.getData(0));
                tempData.setRemoveTag("1");
                tempData.setRemoveDate(btd.getRD().getAcceptTime());
                tempData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                btd.add(serialNumber, tempData);
            }
        }
    }

    private void createEndCustFamilyTradeByUserIdA(String userIdA, BusiTradeData<BaseTradeData> btd) throws Exception
    {
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        IData userList = UcaInfoQry.qryUserInfoByUserId(userIdA);
        if (IDataUtil.isNotEmpty(userList))
        {
            String homeCustId = userList.getString("CUST_ID");
            IDataset custFamilyDataset = CustFamilyInfoQry.getFamilyInfoByCustId(homeCustId, null);
            if (IDataUtil.isNotEmpty(custFamilyDataset))
            {
                for (int i = 0, size = custFamilyDataset.size(); i < size; i++)
                {
                    CustFamilyTradeData tempData = new CustFamilyTradeData(custFamilyDataset.getData(i));
                    tempData.setRemoveTag("1");
                    tempData.setRemoveDate(btd.getRD().getAcceptTime());
                    tempData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    btd.add(serialNumber, tempData);
                }
            }
            IDataset custFmyMemDataset = CustFamilyMebInfoQry.getAllFamilyMember(homeCustId);
            if (IDataUtil.isNotEmpty(custFmyMemDataset))
            {
                for (int i = 0, size = custFmyMemDataset.size(); i < size; i++)
                {
                    CustFamilyMebTradeData tempData = new CustFamilyMebTradeData(custFmyMemDataset.getData(i));
                    tempData.setRemoveTag("1");
                    tempData.setRemoveDate(btd.getRD().getAcceptTime());
                    tempData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    btd.add(serialNumber, tempData);
                }
            }
        }
    }

    // 终止用户优惠信息订单数据生成
    public void createEndDiscntInfoTrade(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        String userId = btd.getRD().getUca().getUserId();
        List<DiscntTradeData> discntList = btd.getRD().getUca().getUserDiscnts();// 直接从uca里面取,减少和数据库的交互
        if (discntList.size() > 0)
        {
            String firstDayNextAcct = AcctDayDateUtil.getFirstDayNextAcct(userId);
            // 下账期第一天的前一秒
            String lastSecondFirstDayNextAcct = SysDateMgr.getLastSecond(firstDayNextAcct);
            for (int i = 0, size = discntList.size(); i < size; i++)
            {
                DiscntTradeData tempTradeData = discntList.get(i);
                // 取modifytag==USER的，因为如果状态=USER 就代表该记录是没有被处理的，所以此节点要处理掉，
                // 如果是其他状态的，说明是被其他节点处理过了。就不要再处理了
                if (StringUtils.equals(BofConst.MODIFY_TAG_USER, tempTradeData.getModifyTag()))
                {
                    String tempEndDate = tempTradeData.getEndDate();
                    // 只处理失效时间大于等于下个月月初的数据
                    if (SysDateMgr.getTimeDiff(firstDayNextAcct, tempEndDate, SysDateMgr.PATTERN_STAND) <= 0)
                    {
                        DiscntTradeData disctnData = tempTradeData.clone();
                        disctnData.setEndDate(lastSecondFirstDayNextAcct);
                        disctnData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        btd.add(serialNumber, disctnData);
                    }

                }
            }
        }
    }
    
    // 终止宽带用户优惠信息订单数据生成
    public void createEndWidenetDiscntInfoTrade(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        String userId = btd.getRD().getUca().getUserId();
        List<DiscntTradeData> discntList = btd.getRD().getUca().getUserDiscnts();// 直接从uca里面取,减少和数据库的交互
        if (discntList.size() > 0)
        {
            String firstDayNextAcct = AcctDayDateUtil.getFirstDayNextAcct(userId);

            for (int i = 0, size = discntList.size(); i < size; i++)
            {
                DiscntTradeData tempTradeData = discntList.get(i);
                // 取modifytag==USER的，因为如果状态=USER 就代表该记录是没有被处理的，所以此节点要处理掉，
                // 如果是其他状态的，说明是被其他节点处理过了。就不要再处理了
                if (StringUtils.equals(BofConst.MODIFY_TAG_USER, tempTradeData.getModifyTag()))
                {
                    String tempEndDate = tempTradeData.getEndDate();
                    // 只处理失效时间大于等于下个月月初的数据
                    if (SysDateMgr.getTimeDiff(firstDayNextAcct, tempEndDate, SysDateMgr.PATTERN_STAND) <= 0)
                    {
                        DiscntTradeData disctnData = tempTradeData.clone();
                        disctnData.setEndDate(btd.getRD().getAcceptTime());
                        disctnData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        btd.add(serialNumber, disctnData);
                    }

                }
            }
        }
    }

    /**
     * 根据userIdA来终止相关优惠
     * 
     * @param userId
     * @param serialNumber
     *            加入到该号码的订单数据中
     */
    private void createEndDiscntTradeByUserIdA(String userIdA, BusiTradeData<BaseTradeData> btd, String serialNumber, String endDate) throws Exception
    {
        IDataset discntList = UserDiscntInfoQry.queryUserAllDiscntByUserIdA(userIdA);
        if(IDataUtil.isNotEmpty(discntList)){
        	for (int j = 0, size = discntList.size(); j < size; j++)
            {
                DiscntTradeData tempData = new DiscntTradeData(discntList.getData(j));
                if (StringUtils.equals("2", tempData.getSpecTag()))// 为什么只处理==2的？
                {
                    /*if (StringUtils.isNotEmpty(endDate))
                    {
                        tempData.setEndDate(btd.getRD().getAcceptTime());
                    }
                    else
                    {
                        tempData.setEndDate(endDate);
                    }*/
                    tempData.setEndDate(btd.getRD().getAcceptTime());
                    tempData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    btd.add(serialNumber, tempData);
                }
            }
        }
    }
    
    /***
     * 处理UU表所有成员可选优惠,立即失效 @author yanwu
     * @param btd
     * @throws Exception
     */
    private void createEndDiscntTradeByCommparaA(String userIdA, BusiTradeData<BaseTradeData> btd) throws Exception {
    	// 处理UU关系
        IDataset mebList = RelaUUInfoQry.getAllMebByUSERIDA(userIdA, "45");
        for (int i = 0, size = mebList.size(); i < size; i++)
        {
        	IData mebData = mebList.getData(i);
            String sn = mebData.getString("SERIAL_NUMBER_B");
            if( StringUtils.isNotBlank(sn) ){
            	//处理可选优惠,立即失效 @author yanwu
                createEndDiscntTradeByCommpara(btd, sn);
            }
        }
    }
    
    /***
     * 处理可选优惠,立即失效 @author yanwu
     * @param btd
     * @throws Exception
     */
    private void createEndDiscntTradeByCommpara(BusiTradeData<BaseTradeData> btd, String sn) throws Exception {
    	
    	UcaData mebUca = UcaDataFactory.getNormalUca(sn);
        mebUca.setAcctTimeEnv();// 分散账期
    	List<DiscntTradeData> userDiscntList = mebUca.getUserDiscnts();
        IDataset commparaList = CommparaInfoQry.getOnlyByAttr("CSM", "1009", mebUca.getUserEparchyCode());
        for (int j = 0, listSize = userDiscntList.size(); j < listSize; j++)
        {
            DiscntTradeData userDiscnt = userDiscntList.get(j);
            for (int k = 0, commparaSize = commparaList.size(); k < commparaSize; k++)
            {
                IData commpara = commparaList.getData(k);
                if (commpara.getString("PARAM_CODE", "").equals(userDiscnt.getElementId()))
                {
                    DiscntTradeData delDiscntTD = userDiscnt.clone();
                    delDiscntTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    delDiscntTD.setEndDate(btd.getRD().getAcceptTime());//SysDateMgr.getSysTime();
                    btd.add(mebUca.getSerialNumber(), delDiscntTD);
                    break;
                }
            }
        }
	}

    /**
     * 根据userIdA和userIdb来终止相关优惠
     * 
     * @param userId
     */
    private void createEndDiscntTradeByUserIdAB(String userIdA, String userIdB, String relationTypeCode, String endDate, BusiTradeData<BaseTradeData> btd, String serialNumber) throws Exception
    {
        IDataset discntUserIDABList = UserDiscntInfoQry.getAllUserDiscntByUserIdUserIdA(userIdB, userIdA);
        for (int j = 0, size = discntUserIDABList.size(); j < size; j++)
        {
            IData tempData = discntUserIDABList.getData(j);
            if (StringUtils.equals("2", tempData.getString("SPEC_TAG")))// 为什么只处理==2的？
            {
                DiscntTradeData tempDiscntTradeData = new DiscntTradeData(discntUserIDABList.getData(j));
                /*if (StringUtils.isEmpty(endDate))
                {
                    tempDiscntTradeData.setEndDate(btd.getRD().getAcceptTime());
                }
                else
                {
                    tempDiscntTradeData.setEndDate(endDate);
                }*/
                tempDiscntTradeData.setEndDate(btd.getRD().getAcceptTime());
                tempDiscntTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                btd.add(serialNumber, tempDiscntTradeData);
            }
        }
    }

    /**
     * 根据虚拟用户终止相关优惠
     * 
     * @param userId
     * @param serialNumber
     *            加入到该号码的订单数据中
     */
    private void createEndDiscntTradeByVirtualUserId(String userId, BusiTradeData<BaseTradeData> btd, String serialNumber, String endDate) throws Exception
    {
        IDataset discntList = UserDiscntInfoQry.getAllValidDiscntByUserId(userId);
        for (int j = 0, size = discntList.size(); j < size; j++)
        {
            DiscntTradeData tempData = new DiscntTradeData(discntList.getData(j));
            if (StringUtils.equals("2", tempData.getSpecTag()))
            {
                if (StringUtils.isEmpty(endDate))
                {
                    tempData.setEndDate(btd.getRD().getAcceptTime());
                }
                else
                {
                    tempData.setEndDate(endDate);
                }
                tempData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                btd.add(serialNumber, tempData);
            }
        }
    }

    /**
     * 终止用户元素信息信息台账数据
     */
    public void createEndElementInfo(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        String userId = btd.getRD().getUca().getUserId();
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        IDataset elementList = UserElementInfoQry.qryUserElement(userId);
        if (elementList.size() > 0)
        {
            String firstDayNextAcct = AcctDayDateUtil.getFirstDayNextAcct(userId);
            String lastSecondFirstDayNextAcct = SysDateMgr.getLastSecond(firstDayNextAcct);
            for (int i = 0, size = elementList.size(); i < size; i++)
            {
                String tempEndDate = elementList.getData(i).getString("END_DATE");
                // 只处理失效时间大于下个月月初的数据
                if (SysDateMgr.getTimeDiff(firstDayNextAcct, tempEndDate, SysDateMgr.PATTERN_STAND) <= 0)
                {
                    ElementTradeData data = new ElementTradeData(elementList.getData(i));
                    data.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    data.setEndDate(lastSecondFirstDayNextAcct);
                    btd.add(serialNumber, data);
                }
            }
        }
    }

    // 终止用户其他信息台账登记
    public void createEndOtherTrade(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        String userId = btd.getRD().getUca().getUserId();
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        IDataset userOtherList = UserOtherInfoQry.queryUserAllValidInfos(userId);
        if (userOtherList.size() > 0)
        {
            String productId = btd.getRD().getUca().getProductId();
            for (int i = 0, size = userOtherList.size(); i < size; i++)
            {
                IData tempData = userOtherList.getData(i);
                String rsrvValueCode = tempData.getString("RSRV_VALUE_CODE");
                String rsrvStr1 = tempData.getString("RSRV_STR1");
                if (StringUtils.equals(rsrvValueCode, "PGRP") && StringUtils.equals(rsrvStr1, productId))// 跨省集团业务
                {
                    OtherTradeData data = new OtherTradeData(tempData);
                    data.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    data.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD) + SysDateMgr.getFirstTime00000());
                    btd.add(serialNumber, data);
                    if (StringUtils.equals("1", tempData.getString("RSRV_STR3", "")))
                    {
                        IData modParam = new DataMap();
                        modParam.put("UNIFY_PAY_CODE", tempData.getString("RSRV_STR5", ""));
                        modParam.put("ACCT_ID", btd.getRD().getUca().getAcctId());
                        modParam.put("END_DATE", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD) + SysDateMgr.getFirstTime00000());
                        Dao.update("TF_F_ACCT_UNIFYPAY", modParam);
                    }
                }
            }
        }
    }

    // 终止用户付费关系 ，只处理销户号码没有设计到统一付费的场景，如果有统一付费关系，需要调用createEndPayRelationTradeByRelaUU
    public void createEndPayRelationInfoTrade(BusiTradeData<BaseTradeData> btd) throws Exception// ////
    {
        String userId = btd.getRD().getUca().getUserId();
        String acctId = btd.getRD().getUca().getAcctId();
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        String nowAcycLastDay = AcctDayDateUtil.getCycleIdLastDayThisAcct(userId);
        String lastCycleIdLastAcct = AcctDayDateUtil.getCycleIdLastDayLastAcct(userId);

        // 取所有未失效的付费关系
        IDataset payRelationList = PayRelaInfoQry.queryUserAfterPayRelByCycId(userId, nowAcycLastDay);
        if (IDataUtil.isNotEmpty(payRelationList))
        {
            for (int i = 0, size = payRelationList.size(); i < size; i++)
            {
                PayRelationTradeData data = new PayRelationTradeData(payRelationList.getData(i));
                data.setEndCycleId(nowAcycLastDay);// 全部终止到本账期结束
                data.setModifyTag(BofConst.MODIFY_TAG_DEL);
                btd.add(serialNumber, data);
            }
        }
        IDataset acctList = PayRelaInfoQry.getAcctPayReltionNow(acctId, "1", "1", null);
        boolean bFindUser = false;
        if (IDataUtil.isNotEmpty(acctList))
        {
            for (int i = 0, size = acctList.size(); i < size; i++)
            {
                String tempUserId = acctList.getData(i).getString("USER_ID");
                // 过滤掉本身这条，且只处理下账期还有效的记录
                if (userId.equals(tempUserId))
                    continue;
                if (IDataUtil.isNotEmpty(UcaInfoQry.qryUserInfoByUserId(tempUserId)))
                {
                    bFindUser = true;
                    break;
                }
            }
        }

        if (!bFindUser)// 不存在有效的用户了，则需要将该账户所有的付费关系终止掉
        {
        	List<BaseTradeData> payRelationTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_PAYRELATION);
            IDataset acctAllList = PayRelaInfoQry.qryPayRelaByAcctID2(acctId, "0", "1", lastCycleIdLastAcct);
            if (IDataUtil.isNotEmpty(acctAllList))
            {
                for (int i = 0, size = acctAllList.size(); i < size; i++)
                {
                    IData tempAcctData = acctAllList.getData(i);
                    String tempUserId = tempAcctData.getString("USER_ID");
                    if (StringUtils.equals(userId, tempUserId))
                        continue;// 当前用户不需要再处理了
                    
                    //如果统付的副号用户已经删除过，不需要再删了。
                    if (payRelationTradeDatas != null && payRelationTradeDatas.size() > 0)
                    {
                    	boolean delFlag = true;
                        for (BaseTradeData payRelationTradeData : payRelationTradeDatas)
                        {
                        	String payUserId = payRelationTradeData.toData().getString("USER_ID");
                        	String payAcctId = payRelationTradeData.toData().getString("ACCT_ID");
                        	if(payUserId.equals(tempUserId) && acctId.equals(payAcctId)){
                        		delFlag = false;
                        	}
                        }
                        if(!delFlag){
                        	continue;
                        }
                    }

                    PayRelationTradeData data = new PayRelationTradeData(tempAcctData);
                    data.setEndCycleId(nowAcycLastDay);// 当前账期
                    data.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    btd.add(serialNumber, data);
                }
            }
        }
    }

    // 终止用户付费关系 //待完善
    public void createEndPayRelationTradeByRelaUU(BusiTradeData<BaseTradeData> btd) throws Exception// ////
    {
        String userId = btd.getRD().getUca().getUserId();
        String acctId = btd.getRD().getUca().getAcctId();
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        String nowAcycLastDay = AcctDayDateUtil.getCycleIdLastDayThisAcct(userId);
        String firstDayNextAcct = AcctDayDateUtil.getFirstDayNextAcct(userId);
        firstDayNextAcct = StringUtils.replace(firstDayNextAcct, "-", "");
        // 需要处理默认付费关系的两种UU关系：34-双卡统一付费，97-一卡付多号
        boolean isExistRelaUU34 = false;
        // 查询业务号码所有的有效关系
        IDataset userBRelaInfos = RelaUUInfoQry.queryValidRelaUUByUserIDB(userId);
        if (IDataUtil.isNotEmpty(userBRelaInfos))
        {
            for (int i = 0, count = userBRelaInfos.size(); i < count; i++)
            {
                IData userBRelaInfoData = userBRelaInfos.getData(i);
                String relationTypeCode = userBRelaInfoData.getString("RELATION_TYPE_CODE");
                String roleCodeB = userBRelaInfoData.getString("ROLE_CODE_B");
                String userIdA = userBRelaInfoData.getString("USER_ID_A");
                if (StringUtils.equals("34", relationTypeCode) || StringUtils.equals("97", relationTypeCode))
                {
                    isExistRelaUU34 = true;
                    if (StringUtils.equals("1", roleCodeB)) // 主号销户
                    {
                        this.createEndPayRelationInfoTrade(btd); // 处理主号自身的付费关系

                        // 查询和处理副号
                        IDataset relaUUDataset = RelaUUInfoQry.getRelationsByUserIdA(relationTypeCode, userIdA, "2");
                        if (IDataUtil.isEmpty(relaUUDataset))
                        {
                            continue;
                        }
                        IData relaUUData = relaUUDataset.getData(0);
                        String userIdB = relaUUData.getString("USER_ID_B");
                        String endDateRelaB = relaUUData.getString("END_DATE");
                        endDateRelaB = SysDateMgr.getDateForYYYYMMDD(endDateRelaB);
                        IData userBInfoData = UcaInfoQry.qryUserInfoByUserId(userIdB);
                        if (IDataUtil.isEmpty(userBInfoData))
                        {
                            CSAppException.apperr(CrmUserException.CRM_USER_17, userIdB);
                        }
                        String serialNumberB = userBInfoData.getString("SERIAL_NUMBER");
                        UcaData ucaDataUserB = UcaDataFactory.getNormalUca(serialNumberB);
                        String nowAcycLastDayB = AcctDayDateUtil.getCycleIdLastDayThisAcct(userIdB);
                        String firstDayNextAcctB = AcctDayDateUtil.getFirstDayNextAcct(userIdB);
                        firstDayNextAcctB = StringUtils.replace(firstDayNextAcctB, "-", "");
                        // 如果副号统付的付费关系是本账期末终止的不处理，说明副号在主号销户前已经终止UU关系
                        if (endDateRelaB.compareTo(nowAcycLastDayB) > 0)
                        {
                            // 终止副号当前有效的
                            IDataset payRelationList = PayRelaInfoQry.queryUserAfterPayRelByCycId(userIdB, nowAcycLastDayB);
                            if (IDataUtil.isNotEmpty(payRelationList))
                            {
                                for (int j = 0, size = payRelationList.size(); j < size; j++)
                                {
                                    IData tempPayRelationData = payRelationList.getData(j);
                                    if (!StringUtils.equals("1", tempPayRelationData.getString("DEFAULT_TAG")))
                                    {
                                        continue;
                                    }
                                    PayRelationTradeData data = new PayRelationTradeData(tempPayRelationData);
                                    if (data.getStartCycleId().compareTo(nowAcycLastDayB) > 0) // uu下账期开始
                                    {
                                        data.setEndCycleId(data.getStartCycleId());
                                        data.setActTag("0"); // 防止复机恢复
                                    }
                                    else
                                    {
                                        data.setEndCycleId(nowAcycLastDayB);
                                    }
                                    data.setModifyTag(BofConst.MODIFY_TAG_DEL);
                                    btd.add(serialNumberB, data);
                                }
                            }
                            // 为副号新增下账期生效的默认账户的付费关系
                            /** BUG20170711092530 7240销户报错bug
                            IData custInfoData = UcaInfoQry.qryCustomerInfoByCustId(userBInfoData.getString("CUST_ID"));
                            if (IDataUtil.isEmpty(custInfoData))
                            {
                                CSAppException.apperr(CustException.CRM_CUST_1, serialNumberB);
                            }

                            IData accountData = UcaInfoQry.qryAcctInfoByAcctId(custInfoData.getString("ACCT_ID"));
                            */
                            IDataset accountDatas = UAcctInfoQry.qryAcctInfoByCustId(userBInfoData.getString("CUST_ID"));
                            if (IDataUtil.isEmpty(accountDatas))
                            {
                                CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_1, serialNumberB);
                            }
                            IData accountData = accountDatas.getData(0);
                            PayRelationTradeData newPayRelaTrade = new PayRelationTradeData();
                            newPayRelaTrade.setUserId(userIdB);
                            newPayRelaTrade.setAcctId(accountData.getString("ACCT_ID"));
                            newPayRelaTrade.setPayitemCode("-1");
                            newPayRelaTrade.setAcctPriority("0");
                            newPayRelaTrade.setUserPriority("0");
                            newPayRelaTrade.setStartCycleId(firstDayNextAcctB);
                            newPayRelaTrade.setEndCycleId(SysDateMgr.getEndCycle20501231());
                            newPayRelaTrade.setActTag("1");
                            newPayRelaTrade.setDefaultTag("1");
                            newPayRelaTrade.setLimitType("0");
                            newPayRelaTrade.setLimit("0");
                            newPayRelaTrade.setComplementTag("0");
                            newPayRelaTrade.setBindType("0");
                            newPayRelaTrade.setAddupMethod("0");
                            newPayRelaTrade.setAddupMonths("0");
                            newPayRelaTrade.setRemark("主号销户，副号新增默认账户的付费关系");
                            newPayRelaTrade.setInstId(SeqMgr.getInstId());
                            newPayRelaTrade.setModifyTag(BofConst.MODIFY_TAG_ADD);
                            btd.add(serialNumberB, newPayRelaTrade);
                        }
                    }
                    else
                    {
                        // 销户号码是副号,终止副号当前有效的，并新增下账期初生效，下账期初失效的默认账户的付费关系
                        IDataset payRelationList = PayRelaInfoQry.queryUserAfterPayRelByCycId(userId, nowAcycLastDay);
                        if (IDataUtil.isNotEmpty(payRelationList))
                        {
                            for (int j = 0, size = payRelationList.size(); j < size; j++)
                            {
                                IData tempPayRelationData = payRelationList.getData(j);
                                // 如果副号统付的付费关系是本账期末终止的不处理，说明副号在销户前已经终止UU关系
                                PayRelationTradeData data = new PayRelationTradeData(tempPayRelationData);
                                String startCycleId = data.getStartCycleId();
                                if (startCycleId.compareTo(nowAcycLastDay) > 0) // uu下账期开始
                                {
                                    data.setEndCycleId(startCycleId);
                                    data.setActTag("0"); // 防止复机恢复
                                }
                                else
                                {
                                    data.setEndCycleId(nowAcycLastDay);
                                }
                                data.setModifyTag(BofConst.MODIFY_TAG_DEL);
                                btd.add(serialNumber, data);
                            }
                        }
                        // 为副号新增下账期初生效，下账期初失效的默认账户的付费关系，防止复机恢复统付关系
                        PayRelationTradeData newPayRelaTrade = new PayRelationTradeData();
                        newPayRelaTrade.setUserId(userId);
                        newPayRelaTrade.setAcctId(acctId);
                        newPayRelaTrade.setPayitemCode("-1");
                        newPayRelaTrade.setAcctPriority("0");
                        newPayRelaTrade.setUserPriority("0");
                        newPayRelaTrade.setStartCycleId(firstDayNextAcct);
                        newPayRelaTrade.setEndCycleId(firstDayNextAcct);
                        newPayRelaTrade.setActTag("1");
                        newPayRelaTrade.setDefaultTag("1");
                        newPayRelaTrade.setLimitType("0");
                        newPayRelaTrade.setLimit("0");
                        newPayRelaTrade.setComplementTag("0");
                        newPayRelaTrade.setRemark("主号销户，副号新增默认账户的付费关系");
                        newPayRelaTrade.setInstId(SeqMgr.getInstId());
                        newPayRelaTrade.setModifyTag(BofConst.MODIFY_TAG_ADD);
                        btd.add(serialNumber, newPayRelaTrade);
                    }
                }
                else if (StringUtils.equals("56", relationTypeCode)) // 56 统一付费关系，高级付费
                {
                    if (StringUtils.equals("1", roleCodeB))
                    {
                        // 主号销户，添加终止副号高级付费关系
                        // 查询和处理副号
                        IDataset relaUUDataset = RelaUUInfoQry.getRelationsByUserIdA(relationTypeCode, userIdA, "2");
                        if (IDataUtil.isEmpty(relaUUDataset))
                        {
                            continue;
                        }
                        for(int h =0; h<relaUUDataset.size();h++){
                        	String userIdB = relaUUDataset.getData(h).getString("USER_ID_B");
                        	IData userIdBInfoData = UcaInfoQry.qryUserInfoByUserId(userIdB);
                        	if (IDataUtil.isEmpty(userIdBInfoData))
                        	{
                        		CSAppException.apperr(CrmUserException.CRM_USER_17, userIdB);
                        	}
                        	String serialNumberB = userIdBInfoData.getString("SERIAL_NUMBER");
                        	UcaData ucaDataUserB = UcaDataFactory.getNormalUca(serialNumberB);
                        	String nowAcycLastDayB = AcctDayDateUtil.getCycleIdLastDayThisAcct(userIdB);
                        	IDataset payRelationList = PayRelaInfoQry.queryUserAfterPayRelByCycId(userIdB, nowAcycLastDayB);
                        	if (IDataUtil.isNotEmpty(payRelationList))
                        	{
                        		for (int j = 0, jCount = payRelationList.size(); j < jCount; j++)
                        		{
                        			IData tempPayRelationData = payRelationList.getData(j);
                        			String defaultTag = tempPayRelationData.getString("DEFAULT_TAG");
                        			String payItemCode = tempPayRelationData.getString("PAYITEM_CODE");
                        			if (StringUtils.equals("0", defaultTag) && StringUtils.equals("41000", payItemCode))
                        			{
                        				PayRelationTradeData payRelaTrade = new PayRelationTradeData(tempPayRelationData);
                        				payRelaTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        				payRelaTrade.setEndCycleId(nowAcycLastDay);
                        				btd.add(serialNumberB, payRelaTrade);
                        			}
                                }
                            }
                        }
                    }
                }else if(StringUtils.equals("CP", relationTypeCode)){
                	//BUG20190430154957手机号码销户但CPE副号付费关系未截止问题优化
                	if (StringUtils.equals("1", roleCodeB))
                    {
                        String acctIdForNormal = "";
                		IDataset payRelationListA = PayRelaInfoQry.qryValidPayRelationByUserId(userId, "1","1");
                        //System.out.println(">>>>>>>>>>>>>>>>>>payRelationListA:"+payRelationListA);

                        if (IDataUtil.isNotEmpty(payRelationListA))
                        {
                        	acctIdForNormal = payRelationListA.getData(0).getString("ACCT_ID", "");
                        }
                        //System.out.println(">>>>>>>>>>>>>>>>>>acctIdForNormal:"+acctIdForNormal);
                		// 主号销户，添加终止副号高级付费关系
                        // 查询和处理副号
                        IDataset relaUUDataset = RelaUUInfoQry.getRelationsByUserIdA(relationTypeCode, userIdA, "2");
                        if (IDataUtil.isEmpty(relaUUDataset))
                        {
                            continue;
                        }
                        for(int h =0; h<relaUUDataset.size();h++){
                        	String userIdB = relaUUDataset.getData(h).getString("USER_ID_B");
                        	IData userIdBInfoData = UcaInfoQry.qryUserInfoByUserId(userIdB);
                        	if (IDataUtil.isEmpty(userIdBInfoData))
                        	{
                        		CSAppException.apperr(CrmUserException.CRM_USER_17, userIdB);
                        	}
                        	String serialNumberB = userIdBInfoData.getString("SERIAL_NUMBER");
                        	String nowAcycLastDayB = AcctDayDateUtil.getCycleIdLastDayThisAcct(userIdB);
                        	IDataset payRelationList = PayRelaInfoQry.queryUserAfterPayRelByCycId(userIdB, nowAcycLastDayB);
                        	if (IDataUtil.isNotEmpty(payRelationList))
                        	{
                        		for (int j = 0, jCount = payRelationList.size(); j < jCount; j++)
                        		{
                        			IData tempPayRelationData = payRelationList.getData(j);
                        			String acctIdForCPE = tempPayRelationData.getString("ACCT_ID","");
                                    //System.out.println(">>>>>>>>>>>>>>>>>>acctIdForCPE:"+acctIdForCPE);

                        			if (StringUtils.equals(acctIdForNormal, acctIdForCPE))
                        			{
                        				PayRelationTradeData payRelaTrade = new PayRelationTradeData(tempPayRelationData);
                        				payRelaTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        				payRelaTrade.setEndCycleId(nowAcycLastDay);
                        				btd.add(serialNumber, payRelaTrade);//k3
                        			}
                                }
                            }
                        }
                    }
                	//BUG20190430154957手机号码销户但CPE副号付费关系未截止问题优化
                	
                	/**
                	 * REQ201612260011_新增CPE终端退回和销户界面_付费关系
                	 * @author zhuoyingzhi
                	 * 20160210
                	 */
                    //终止设备信息台帐
                    createEndCPEOtherByUserId(btd, userId);
                }
            }
        }

        // 如果用户不存在涉及到统付关系的UU关系
        if (IDataUtil.isEmpty(userBRelaInfos) || !isExistRelaUU34)
        {
            this.createEndPayRelationInfoTrade(btd);
        }
    }

    // 终止用户产品信息
    public void createEndProductTrade(BusiTradeData<BaseTradeData> btd) throws Exception
    {
    	MainTradeData mainTradeData = btd.getMainTradeData();
        String strBrandCode = mainTradeData.getBrandCode();
        if("PWLW".equals(strBrandCode) || "WLWG".equals(strBrandCode))
        {
        	String serialNumber = btd.getRD().getUca().getSerialNumber();
        	String strUserId = mainTradeData.getUserId();
        	IDataset result = RelaUUInfoQry.qryByRelaUserIdB(strUserId, "9A", null);
        	if (IDataUtil.isNotEmpty(result))
            {
        		
        		// 直接从uca里面取,减少和数据库的交互
                List<ProductTradeData> productList = btd.getRD().getUca().getUserProducts();
                if (productList.size() > 0)
                {
                    for (int i = 0, size = productList.size(); i < size; i++)
                    {
                        ProductTradeData tempTradeData = productList.get(i);
                        // 取modifytag==USER的，因为如果状态=USER 就代表该记录是没有被处理的，所以此节点要处理掉，
                        // 如果是其他状态的，说明是被其他节点处理过了。就不要再处理了
                        if (StringUtils.equals(BofConst.MODIFY_TAG_USER, tempTradeData.getModifyTag()))
                        {
                            ProductTradeData productData = tempTradeData.clone();
                            productData.setEndDate(btd.getRD().getAcceptTime());
                            //集团用户是否订购了流量池产品,服务Id是99010012
                            IData info = result.getData(0);
                            if(IDataUtil.isNotEmpty(info)){ //非物联网用户填受理时间，物联网用户填到月底
                            	String userIdA = info.getString("USER_ID_A");
                            	//辛芬芳 昨天 17:47 
                            	//你那个可以改成本月生效过吗
                            	//辛芬芳 昨天 17:47 
                            	//只要end——date 当月1日0点就算
                            	String endDate = SysDateMgr.getFirstDayOfThisMonth();
                            	IDataset svcInfos = UserSvcInfoQry.getGrpSvcInfoByUserIdEndDate(userIdA,"99010012",endDate);
                            	if(IDataUtil.isEmpty(svcInfos))
                            	{
                            		svcInfos = UserSvcInfoQry.getGrpSvcInfoByUserIdEndDate(userIdA,"99010013",endDate);
                            	}
                            	if(IDataUtil.isNotEmpty(svcInfos))
                            	{
                            		String strLastDay = SysDateMgr.getDateLastMonthSec(btd.getRD().getAcceptTime());
                            		productData.setRsrvDate1(strLastDay);
                            	}
                            }
                            productData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                            btd.add(serialNumber, productData);
                        }
                    }
                }
        		
            }else{
            	
            	// 直接从uca里面取,减少和数据库的交互
                List<ProductTradeData> productList = btd.getRD().getUca().getUserProducts();
                if (productList.size() > 0)
                {
                	
                    for (int i = 0, size = productList.size(); i < size; i++)
                    {
                        ProductTradeData tempTradeData = productList.get(i);
                        // 取modifytag==USER的，因为如果状态=USER 就代表该记录是没有被处理的，所以此节点要处理掉，
                        // 如果是其他状态的，说明是被其他节点处理过了。就不要再处理了
                        if (StringUtils.equals(BofConst.MODIFY_TAG_USER, tempTradeData.getModifyTag())
                        &&  "PWLW".equals(tempTradeData.getBrandCode()))
                        {
                            ProductTradeData productData = tempTradeData.clone();
                            productData.setEndDate(btd.getRD().getAcceptTime());
                            productData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                            btd.add(serialNumber, productData);
                        }
                    }
                    
                }
            	
            }
        	
        }else{
        	
        	String serialNumber = btd.getRD().getUca().getSerialNumber();
            // 直接从uca里面取,减少和数据库的交互
            List<ProductTradeData> productList = btd.getRD().getUca().getUserProducts();
            if (productList.size() > 0)
            {
                for (int i = 0, size = productList.size(); i < size; i++)
                {
                    ProductTradeData tempTradeData = productList.get(i);
                    // 取modifytag==USER的，因为如果状态=USER 就代表该记录是没有被处理的，所以此节点要处理掉，
                    // 如果是其他状态的，说明是被其他节点处理过了。就不要再处理了
                    if (StringUtils.equals(BofConst.MODIFY_TAG_USER, tempTradeData.getModifyTag()))
                    {
                        ProductTradeData productData = tempTradeData.clone();
                        productData.setEndDate(btd.getRD().getAcceptTime());
                        productData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        btd.add(serialNumber, productData);
                    }
                }
            }
            
        }
    	
    }

    /**
     * 终止用户relationuu关系台账表生成
     * 
     * @param btd
     * @throws Exception
     */
    public void createEndRelationUUTrade(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>进入createEndRelationUUTrade>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        }
        String userId = btd.getRD().getUca().getUserId();
        String serialNumber = btd.getRD().getUca().getSerialNumber();

        // 1、以当前userID作为关系表中的user_ID_B来查询所有未失效的关系数据
        IDataset userBRelaInfos = RelaUUInfoQry.queryValidRelaUUByUserIDB(userId);
        if (logger.isDebugEnabled())
        {
            logger.debug(">>>>>>>>>>>>>>>>>>>>>>当前用户USER_ID_B关系>>>>>>>>>>>>>>>>>>>" + userBRelaInfos.toString());
        }
        String endDateThisAcct = AcctDayDateUtil.getLastDayThisAcct(userId);
        endDateThisAcct = SysDateMgr.getLastSecond(endDateThisAcct);
        if (null != userBRelaInfos && userBRelaInfos.size() > 0)
        {
            for (int i = 0, sizeUserBRelaInfos = userBRelaInfos.size(); i < sizeUserBRelaInfos; i++)
            {
                IData tempData = userBRelaInfos.getData(i);
                String relationTypeCode = tempData.getString("RELATION_TYPE_CODE");
                String roleCodeB = tempData.getString("ROLE_CODE_B");
                String userIdA = tempData.getString("USER_ID_A");
                String mainPhoneNum = tempData.getString("SERIAL_NUMBER_B");
                String endDate = btd.getRD().getAcceptTime();

                // 1.1 判断该关系是否是 销户终止亲情关系 中配置的类型
                if (StringUtils.equals("45", relationTypeCode))
                {
                    // 家庭网关系结束时间终止到月底
                    endDate = endDateThisAcct;
                    // 1.11 判断是否角色编号是否是1（主角色）
                    if (StringUtils.equals("1", roleCodeB))
                    {
                        // 1.111 根据关系中的user_id_a查询出所有关系，并终止
                        this.createEndRelaTradeByUserIdA(userIdA, btd, serialNumber, endDate);
                        // 1.112根据user_id_a查询出所有有关系的优惠，并终止
                        this.createEndDiscntTradeByUserIdA(userIdA, btd, serialNumber, endDate);
                        //处理UU表所有成员，可选优惠,立即失效 @author yanwu
                        this.createEndDiscntTradeByCommparaA(userIdA, btd);
                        // 1.113对当前user_id_a做销户处理
                        this.createEndVirtualUserByUserIdA(userIdA, btd);
                        // 终止客户家庭表
                        this.createEndCustFamilyTradeByUserIdA(userIdA, btd);
                        // //1.12判断角色是否2、3 (副角色 )
                    
                        //销户后发送短信到副卡
                        this.RemindEndRelaTrade(userIdA,btd,mainPhoneNum );
                   
                    }
                    else if (StringUtils.equals("2", roleCodeB) || StringUtils.equals("3", roleCodeB))
                    {
                        // 终止userIdA与userIdB的优惠
                        this.createEndDiscntTradeByUserIdAB(userIdA, userId, relationTypeCode, endDate, btd, serialNumber);
                        //处理可选优惠,立即失效 @author yanwu
                        createEndDiscntTradeByCommpara(btd, serialNumber);
                        // 终止userIdA与userIdB的关系
                        RelationTradeData tempRelaTradeData = new RelationTradeData(tempData);
                        tempRelaTradeData.setEndDate(btd.getRD().getAcceptTime());
                        tempRelaTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        btd.add(serialNumber, tempRelaTradeData);
                        // 终止客户家庭表、客户家庭成员表
                        this.createEndCustFamilyMemTradeByUserIdB(userId, btd);
                    }
                } // 1.2 判断该关系是否是一卡多号关系
                else if (StringUtils.equals("30", relationTypeCode))
                {
                    // 1.21终止与userIdA有关的所有关系信息
                    this.createEndRelaTradeByUserIdA(userIdA, btd, serialNumber, endDate);

                    // 1.22终止与userIDA有关的所有优惠
                    IDataset userADiscntDataList = UserDiscntInfoQry.queryUserDiscntByUserIdA(userIdA);
                    for (int j = 0, size = userADiscntDataList.size(); j < size; j++)
                    {
                        String discntCode = userADiscntDataList.getData(j).getString("DISCNT_CODE");
                        IData discntData = DiscntInfoQry.getDiscntInfoByCode2(discntCode);
                        if (IDataUtil.isNotEmpty(discntData) && !StringUtils.equals(discntData.getString("ENABLE_TAG"), "0"))
                        {
                            endDate = endDateThisAcct;
                        }
                        DiscntTradeData tempDiscntTradeData = new DiscntTradeData(userADiscntDataList.getData(j));
                        tempDiscntTradeData.setEndDate(endDate);
                        tempDiscntTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        btd.add(serialNumber, tempDiscntTradeData);
                    }
                    // 1.23终止userIdA的用户信息
                    this.createEndVirtualUserByUserIdA(userIdA, btd);
                }
                else if (StringUtils.equals("34", relationTypeCode))
                {
                    // 处理双号统一付费
                    // 1.21终止与userIdA有关的所有关系信息
                    this.createEndRelaTradeByUserIdA(userIdA, btd, serialNumber, endDate);

                    // 1.22终止与userIDA有关的所有优惠
                    IDataset userADiscntDataList = UserDiscntInfoQry.queryUserDiscntByUserIdA(userIdA);
                    for (int j = 0, size = userADiscntDataList.size(); j < size; j++)
                    {
                        String discntCode = userADiscntDataList.getData(j).getString("DISCNT_CODE");
                        IData discntData = DiscntInfoQry.getDiscntInfoByCode2(discntCode);
                        if (IDataUtil.isNotEmpty(discntData) && !StringUtils.equals(discntData.getString("ENABLE_TAG"), "0"))
                        {
                            endDate = endDateThisAcct;
                        }
                        DiscntTradeData tempDiscntTradeData = new DiscntTradeData(userADiscntDataList.getData(j));
                        tempDiscntTradeData.setEndDate(endDate);
                        tempDiscntTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        btd.add(serialNumber, tempDiscntTradeData);
                    }
                    // 1.23终止userIdA的用户信息
                    this.createEndVirtualUserByUserIdA(userIdA, btd);
                }
                else if (StringUtils.equals("97", relationTypeCode))
                {
                    // 处理一卡付多号
                    // 1.11 判断是否角色编号是否是1（主角色）
                    if (StringUtils.equals("1", roleCodeB))
                    {
                        // 1.111 根据关系中的user_id_a查询出所有关系，并终止
                        this.createEndRelaTradeByUserIdA(userIdA, btd, serialNumber, endDate);
                        // 1.112根据user_id_a查询出所有有关系的优惠，并终止
                        this.createEndDiscntTradeByUserIdA(userIdA, btd, serialNumber, endDate);
                        // 1.113对当前user_id_a做销户处理
                        this.createEndVirtualUserByUserIdA(userIdA, btd);
                    } // 1.12判断角色是否2、3 (副角色 )
                    else if (StringUtils.equals("2", roleCodeB) || StringUtils.equals("3", roleCodeB))
                    {
                        // 终止userIdA与userIdB的优惠
                        this.createEndDiscntTradeByUserIdAB(userIdA, userId, relationTypeCode, endDate, btd, serialNumber);
                        // 终止userIdA与userIdB的关系
                        RelationTradeData tempRelaTradeData = new RelationTradeData(tempData);
                        tempRelaTradeData.setEndDate(endDate);
                        tempRelaTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);// BofConst.MODIFY_TAG_DEL
                        btd.add(serialNumber, tempRelaTradeData);
                    }
                }
                else if (StringUtils.equals("56", relationTypeCode))
                {
                    IDataset relationRoldBDataset = RelaUUInfoQry.getRelationsByUserIdA(relationTypeCode, userIdA, "2");
                    // 处理统一付费关系
                    // 1.11 判断是否角色编号是否是1（主角色）或是最后一个副号销户
                    if (StringUtils.equals("1", roleCodeB)
                            || (IDataUtil.isNotEmpty(relationRoldBDataset) 
                                    && relationRoldBDataset.size()==1))
                    {
                        // 1.111 根据关系中的user_id_a查询出所有关系，并终止
                        this.createEndRelaTradeByUserIdA(userIdA, btd, serialNumber, endDate);
                        this.createEndVirtualUserByUserIdA(userIdA, btd);
                    } // 1.12判断角色是否2(副角色 )
                    else if (StringUtils.equals("2", roleCodeB))
                    {
                        // 终止userIdA与userIdB的关系
                        RelationTradeData tempRelaTradeData = new RelationTradeData(tempData);
                        tempRelaTradeData.setEndDate(endDate);
                        tempRelaTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);// BofConst.MODIFY_TAG_DEL
                        btd.add(serialNumber, tempRelaTradeData);
                        // QR-20200528-03	号码销号代付关系的parelation表数据没有截止
                        // 如果副号都是截止到月底，则将主号也截止到月底 add by wuhao5
                        boolean notHaveNormalRole2 = true;
                        if ((IDataUtil.isNotEmpty(relationRoldBDataset) && relationRoldBDataset.size() > 0)) {
                            for (int j = 0;j < relationRoldBDataset.size();j++) {
                                IData relaTemp = relationRoldBDataset.getData(j);
                                String date1 = relaTemp.getString("END_DATE");
                                String date2 = SysDateMgr.getLastDateThisMonth();
                                if (SysDateMgr.compareTo(date1,date2) > 0
                                        && !serialNumber.equals(relaTemp.getString("SERIAL_NUMBER_B"))) {
                                    notHaveNormalRole2 = false;
                                }
                            }
                        }
                        if (notHaveNormalRole2) {
                            IDataset relaIds = RelaUUInfoQry.getRelationsByUserIdA(relationTypeCode, userIdA, "1");
                            if (IDataUtil.isNotEmpty(relaIds) && relaIds.size() > 0) {
                                RelationTradeData relationTradeData = new RelationTradeData(relaIds.getData(0));
                                relationTradeData.setEndDate(SysDateMgr.getLastDateThisMonth());
                                relationTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                                relationTradeData.setRemark("所有副号删除,截止主号统付至月底");
                                btd.add(serialNumber, relationTradeData);
                            }
                        }
                    }
                }else if (StringUtils.equals("47", relationTypeCode))
                {
                    // 1.111 根据关系中的user_id_a查询出所有关系，并终止
                    this.createEndRelaTradeByUserIdA(userIdA, btd, serialNumber, endDate);

                    // 1.112根据虚拟用户查询出所有有关系的优惠，并终止
                    this.createEndDiscntTradeByVirtualUserId(userIdA, btd, serialNumber, endDate);

                    // 1.113对当前虚拟用户做销户处理
                    this.createEndVirtualUserByUserIdA(userIdA, btd);

                    // 终止虚拟用户产品
                    this.createEndVirtualProductTrade(userIdA, btd);

                }
                else if (StringUtils.equals("78", relationTypeCode))
                {
                    // 1.111 根据关系中的user_id_a查询出所有关系，并终止
                    this.createEndRelaTradeByUserIdA(userIdA, btd, serialNumber, endDate);
                    // 1.112对当前user_id_a做销户处理
                    this.createEndVirtualUserByUserIdA(userIdA, btd);
                    // 1.112修改宽带账户资料表rsrv_str3
                    if (!userId.equals(tempData.getString("USER_ID_B")))
                    {
                        this.createModifyUserWidenetTrade(btd, tempData.getString("USER_ID_B"));
                    }
                }
                else if (StringUtils.equals("77", relationTypeCode))
                {
                    IDataset relaInfos = RelaUUInfoQry.qryRelaUUByUIdAAllDB(userIdA, relationTypeCode);
                    if (relaInfos.size() <= 2)
                    {
                        // 1.111 根据关系中的user_id_a查询出所有关系，并终止
                        this.createEndRelaTradeByUserIdA(userIdA, btd, serialNumber, endDate);
                        // 1.112对当前user_id_a做销户处理
                        this.createEndVirtualUserByUserIdA(userIdA, btd);
                        // 1.112修改宽带账户资料表rsrv_str3
                        if (!userId.equals(tempData.getString("USER_ID_B")))
                        {
                            this.createModifyUserWidenetTrade(btd, tempData.getString("USER_ID_B"));
                        }
                    }
                    else
                    {
                        if (StringUtils.equals("1", roleCodeB))
                        {
                            IDataset relRoleIDAList = RelaUUInfoQry.getUUInfoByUserIdA(userIdA);
                            if (IDataUtil.isNotEmpty(relRoleIDAList))
                            {
                                String userIdB = relRoleIDAList.getData(0).getString("USER_ID_B");

                                IDataset relationInfos = RelaUUInfoQry.getUUInfoByUserIdAB(userIdA, userIdB, "77", "2");
                                if (IDataUtil.isNotEmpty(relationInfos))
                                {
                                    IData relationInfo = relationInfos.getData(0);
                                    RelationTradeData tempRelaTradeData = new RelationTradeData(relationInfo);
                                    tempRelaTradeData.setEndDate(btd.getRD().getAcceptTime());
                                    tempRelaTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                                    btd.add(serialNumber, tempRelaTradeData);

                                    RelationTradeData relaTradeData = new RelationTradeData();
                                    relaTradeData.setUserIdA(userIdA);
                                    relaTradeData.setUserIdB(userIdB);
                                    relaTradeData.setSerialNumberA(relationInfo.getString("SERIAL_NUMBER_A"));
                                    relaTradeData.setInstId(SeqMgr.getInstId());
                                    relaTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                                    relaTradeData.setRelationTypeCode("77");
                                    relaTradeData.setSerialNumberB(relationInfo.getString("SERIAL_NUMBER_B"));
                                    relaTradeData.setRoleCodeA(relationInfo.getString("ROLE_CODE_A"));
                                    relaTradeData.setRoleCodeB("1");// 2表示副卡
                                    relaTradeData.setOrderno(relationInfo.getString("ORDER_NO"));
                                    relaTradeData.setStartDate(endDate);
                                    relaTradeData.setEndDate(SysDateMgr.getTheLastTime());
                                    btd.add(serialNumber, relaTradeData);
                                }
                            }
                        }
                        else
                        {

                            // 终止userIdA与userIdB的关系
                            RelationTradeData tempRelaTradeData = new RelationTradeData(tempData);
                            tempRelaTradeData.setEndDate(btd.getRD().getAcceptTime());
                            tempRelaTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                            btd.add(serialNumber, tempRelaTradeData);
                        }
                    }
                }else if(StringUtils.equals("MF", relationTypeCode)){
                	/**
                	 * 跨省家庭网的处理
                	 */
                	  if (StringUtils.equals("1", roleCodeB)){//判断是否角色编号是否是1（主角色）
                		  this.createEndRelaTradeByUserIdA(userIdA, btd, serialNumber, endDate);
                		  this.createEndDiscntTradeByUserIdA(userIdA, btd, serialNumber, endDate);
                          //处理UU表所有成员，可选优惠,立即失效 @author yanwu
                          this.createEndDiscntTradeByCommparaA(userIdA, btd);
                	  }
                	  else if (StringUtils.equals("2", roleCodeB)){
                		  continue;
                	  }
                }
                else if(StringUtils.equals("CP", relationTypeCode)){
            		/**
            		 * REQ201612260011_新增CPE终端退回和销户界面_关系
            		 * @author zhuoyingzhi
            		 * 20170213
            		 */
                    // 1.111 根据关系中的user_id_a查询出所有关系，并终止
                    this.createEndRelaTradeByUserIdA(userIdA, btd, serialNumber, endDate);
                    // 1.112对当前user_id_a做销户处理(执行的时候不会执行到"终止虚拟付费关系和账户信息"里面)
                    this.createEndVirtualUserByUserIdA(userIdA, btd);
                }
                else
                // 1.3不满足上述条件，按照普通的类型处理
                {
                    // 销户通用处理UU关系逻辑开关
                    String switchOne = StaticUtil.getStaticValue("DESTROY_UU_SWITCH","SWITCH_ONE");
                    // QR-20200528-03号码销号代付关系的parelation表数据没有截止 add by wuhao5 20200602
                    if ("1".equals(roleCodeB) && !StringUtils.equals(relationTypeCode, "70") && "1".equals(switchOne)) {
                        String nowAcycLastDay = AcctDayDateUtil.getCycleIdLastDayThisAcct(userId);
                        // 终止主号下绑定的所有uu关系及终止虚拟用户绑定的服务
                        this.createEndRelaTradeByUserIdA(userIdA, btd, serialNumber, endDate);
                        // 终止虚拟用户的账号资料，客户资料，用户资料
                        this.createEndVirtualUserByUserIdA(userIdA, btd);
                        // 终止主号绑定的payrelation
                        String acctIdForNormal = "";
                        IDataset payRelationListA = PayRelaInfoQry.qryValidPayRelationByUserId(userId, "1","1");
                        if (IDataUtil.isNotEmpty(payRelationListA))
                        {
                            acctIdForNormal = payRelationListA.getData(0).getString("ACCT_ID", "");
                        }
                        // 主号销户，添加终止副号高级付费关系
                        // 查询和处理副号
                        IDataset relaUUDataset = RelaUUInfoQry.getRelationsByUserIdA(relationTypeCode, userIdA, "2");
                        if (IDataUtil.isEmpty(relaUUDataset))
                        {
                            continue;
                        }
                        for(int h =0; h<relaUUDataset.size();h++){
                            String userIdB = relaUUDataset.getData(h).getString("USER_ID_B");
                            IData userIdBInfoData = UcaInfoQry.qryUserInfoByUserId(userIdB);
                            if (IDataUtil.isEmpty(userIdBInfoData))
                            {
                                CSAppException.apperr(CrmUserException.CRM_USER_17, userIdB);
                            }
                            String serialNumberB = userIdBInfoData.getString("SERIAL_NUMBER");
                            String nowAcycLastDayB = AcctDayDateUtil.getCycleIdLastDayThisAcct(userIdB);
                            IDataset payRelationList = PayRelaInfoQry.queryUserAfterPayRelByCycId(userIdB, nowAcycLastDayB);
                            if (IDataUtil.isNotEmpty(payRelationList))
                            {
                                for (int j = 0, jCount = payRelationList.size(); j < jCount; j++)
                                {
                                    // 获取副号的统付关系
                                    IData tempPayRelationData = payRelationList.getData(j);
                                    String depAcctId = tempPayRelationData.getString("ACCT_ID","");
                                    // 由此次销户的主号的ACCT_ID统付的payrelation关系截止到当前账期最后一天
                                    if (StringUtils.equals(acctIdForNormal, depAcctId))
                                    {
                                        PayRelationTradeData payRelaTrade = new PayRelationTradeData(tempPayRelationData);
                                        payRelaTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
                                        payRelaTrade.setEndCycleId(nowAcycLastDay);
                                        btd.add(serialNumber, payRelaTrade);
                                    }
                                }
                            }
                        }
                    }else {
                        // 终止用户的其他关系信息
                        RelationTradeData tempRelaTradeData = new RelationTradeData(tempData);
                        tempRelaTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        if (StringUtils.equals(relationTypeCode, "70"))
                        {
                            tempRelaTradeData.setEndDate(endDateThisAcct);
                        }
                        else
                        {
                            tempRelaTradeData.setEndDate(endDate);
                        }
                        btd.add(serialNumber, tempRelaTradeData);
                    }
                }
            }
        }
    }

    /*************************************** 工具方法 **************************************************/
    /**
     * 根据userIdA来终止相关关系
     * 
     * @param userIdA
     */
    private void createEndRelaTradeByUserIdA(String userIdA, BusiTradeData<BaseTradeData> btd, String serialNumber, String endDate) throws Exception
    {
        IDataset relationAList = RelaUUInfoQry.getAllValidRelaByUserIDA(userIdA);
        if(IDataUtil.isNotEmpty(relationAList)){
        	for (int j = 0, size = relationAList.size(); j < size; j++)
            {
                RelationTradeData tempData = new RelationTradeData(relationAList.getData(j));
                String sn = tempData.getSerialNumberB();
                /*if (StringUtils.isEmpty(endDate))
                {
                    tempData.setEndDate(btd.getRD().getAcceptTime());
                }
                else
                {
                    tempData.setEndDate(endDate);
                }*/
                tempData.setEndDate(btd.getRD().getAcceptTime());
                tempData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                btd.add(serialNumber, tempData);
            }
        }
        // 终止亲亲网服务
        this.createEndSvcTradeByRelaAndUserIdA(btd, userIdA, relationAList);
    }

    // 终止用户资源信息
    public void createEndResInfoTrade(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        String userId = btd.getRD().getUca().getUserId();
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        IDataset resList = UserResInfoQry.getUserResInfoByUserId(userId);
        if (resList.size() > 0)
        {
            for (int i = 0, size = resList.size(); i < size; i++)
            {
                ResTradeData data = new ResTradeData(resList.getData(i));
                data.setModifyTag(BofConst.MODIFY_TAG_DEL);
                data.setEndDate(btd.getRD().getAcceptTime());
                btd.add(serialNumber, data);
            }
        }
    }

    // 终止用户营销活动资料
    public void createEndSaleActiveTrade(BusiTradeData btd) throws Exception
    {
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        String userId = btd.getRD().getUca().getUserId();
        String eparchyCode = btd.getRD().getUca().getUserEparchyCode();
        IDataset userSaleActiveInfos = UserSaleActiveInfoQry.queryUserSaleActiveByTag(userId);
        if (IDataUtil.isNotEmpty(userSaleActiveInfos))
        {
            IDataset paramUserSaleProducts = CommparaInfoQry.getCommByParaAttr("CSM", "155", CSBizBean.getTradeEparchyCode());
            if (IDataUtil.isNotEmpty(paramUserSaleProducts))
            {
                for (int i = 0, count = userSaleActiveInfos.size(); i < count; i++)
                {
                    IData tempData = userSaleActiveInfos.getData(i);
                    String productId = tempData.getString("PRODUCT_ID");
                    boolean bFind = false;
                    for (int j = 0, jCount = paramUserSaleProducts.size(); j < jCount; j++)
                    {
                        String paraCode1 = paramUserSaleProducts.getData(j).getString("PARA_CODE1", "0");
                        if (StringUtils.equals(productId, paraCode1))
                        {
                            bFind = true;
                            break;
                        }
                    }
                    if (!bFind)
                    {
                        SaleActiveTradeData saleActiveTradeData = new SaleActiveTradeData(tempData);
                        saleActiveTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        saleActiveTradeData.setProcessTag("1");
                        saleActiveTradeData.setRemark("立即销户终止用户活动信息！");
                        saleActiveTradeData.setEndDate(btd.getRD().getAcceptTime());
                        if (StringUtils.isNotEmpty(tempData.getString("RSRV_DATE2")))
                        {
                            saleActiveTradeData.setRsrvDate2(btd.getRD().getAcceptTime());
                        }
                        btd.add(serialNumber, saleActiveTradeData);
                        //调账管接口
                        String relationTradeId = saleActiveTradeData.getRelationTradeId();
                        String actionCode = SaleActiveUtil.getEnableActiveActionCode(btd.getRD().getUca(), relationTradeId);
                        if (StringUtils.isNotEmpty(actionCode)){
                            String thisActiveStartDate = saleActiveTradeData.getStartDate();
                            String thisActiveEndDate = saleActiveTradeData.getEndDate();
                            String thisActiveMonth = saleActiveTradeData.getMonths();
                            int intervalMoths = Integer.parseInt(SaleActiveUtil.getIntervalMoths(thisActiveStartDate, thisActiveEndDate, thisActiveMonth));
                            //20150813 by songlm 销户时，不再调账务营销活动终止接口    参见8月11日邮件“关于复机时营销活动恢复问题”
                            //AcctCall.cancelDiscntDeposit(userId, relationTradeId, intervalMoths, eparchyCode);
                        }
                    }
                }
            }
            else
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_902);
            }
        }
    }

    // 终止用户积分账户和积分计划相关资料 NG4.0
    public void createEndScoreAcctAndPlanTrade(BusiTradeData btd) throws Exception
    {
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        String userId = btd.getRD().getUca().getUserId();
        String userEparchyCode = btd.getRD().getUca().getUserEparchyCode();
        // 终止积分账户
        IDataset integralAcctDataset = ScoreAcctInfoQry.queryScoreAcctInfoByUserId(userId, "10A", userEparchyCode);
        if (IDataUtil.isNotEmpty(integralAcctDataset))
        {
            for (int i = 0, count = integralAcctDataset.size(); i < count; i++)
            {
                IntegralAcctTradeData integralAcctTradeData = new IntegralAcctTradeData(integralAcctDataset.getData(i));
                integralAcctTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                integralAcctTradeData.setEndDate(btd.getRD().getAcceptTime());
                integralAcctTradeData.setStatus("10E");
                btd.add(serialNumber, integralAcctTradeData);
            }
        }

        // 终止积分计划
        IDataset integralAcctPlanDataset = ScorePlanInfoQry.queryScorePlanInfoByUserId(userId, "10A");
        if (IDataUtil.isNotEmpty(integralAcctPlanDataset))
        {
            for (int i = 0, count = integralAcctPlanDataset.size(); i < count; i++)
            {
                IntegralPlanTradeData planTradeData = new IntegralPlanTradeData(integralAcctPlanDataset.getData(i));
                planTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                planTradeData.setEndDate(btd.getRD().getAcceptTime());
                planTradeData.setStatus("10E");
                btd.add(serialNumber, planTradeData);
            }
        }

        // 终止积分账户关系
        IDataset scoreRelationDataset = ScoreRelationQry.queryEffectiveRelByUserId(userId, "1", userEparchyCode);
        if (IDataUtil.isNotEmpty(scoreRelationDataset))
        {
            for (int i = 0, count = scoreRelationDataset.size(); i < count; i++)
            {
                ScoreRelationTradeData scoreRelationTradeData = new ScoreRelationTradeData(scoreRelationDataset.getData(i));
                scoreRelationTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                scoreRelationTradeData.setEndDate(StringUtils.replace(StringUtils.substring(btd.getRD().getAcceptTime(), 0, 10), "-", ""));
                btd.add(serialNumber, scoreRelationTradeData);
            }
        }
    }

    /**
     * 终止共享关系
     * @author yanwu Modify
     * @param btd
     * @throws Exception
     */
    public void createEndShareRelaInfoTrade(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        String userId = btd.getRD().getUca().getUserId();
        String firstDayNextAcct = AcctDayDateUtil.getFirstDayNextAcct(userId);
        String lastDayTimeThisAcct = SysDateMgr.getLastSecond(firstDayNextAcct);
        // 判断是否已经存在共享关系
        IDataset userAllShareRelaDataset = ShareInfoQry.queryMemberRelaAB(userId);// 查询该号码共享关系
        if (IDataUtil.isNotEmpty(userAllShareRelaDataset))
        {
            for (int i = 0, count = userAllShareRelaDataset.size(); i < count; i++)
            {
                IData userShareRelaData = userAllShareRelaDataset.getData(i);
                String roleCode = userShareRelaData.getString("ROLE_CODE");
                String shareId = userShareRelaData.getString("SHARE_ID");
                String endDate = userShareRelaData.getString("END_DATE");
                // 已经取消的不再处理
                if (SysDateMgr.monthInterval(endDate, lastDayTimeThisAcct) <= 1)
                {
                    continue;
                }
                if (StringUtils.equals(roleCode, "01"))
                {
                    this.createEndAllShareInfoTradeByShareId(btd, shareId);
                    //@Modify yanwu
                	ShareRelaTradeData tempShareRelaTradeData = new ShareRelaTradeData(userShareRelaData);
                    tempShareRelaTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    tempShareRelaTradeData.setEndDate(lastDayTimeThisAcct);
                    btd.add(serialNumber, tempShareRelaTradeData);
                }
                else if (StringUtils.equals(roleCode, "02"))
                {
                	//@Modify yanwu
                	ShareRelaTradeData tempShareRelaTradeData = new ShareRelaTradeData(userShareRelaData);
                    tempShareRelaTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    tempShareRelaTradeData.setEndDate(lastDayTimeThisAcct);
                    btd.add(serialNumber, tempShareRelaTradeData);
                    /*IDataset tempShareRelaDataset = ShareInfoQry.queryRelaByShareIdAndRoleCode(shareId, "02");
                    if (IDataUtil.isNotEmpty(tempShareRelaDataset))
                    {
                        if (tempShareRelaDataset.size() == 1)
                        {
                            this.createEndAllShareInfoTradeByShareId(btd, shareId);
                        }
                        else
                        {
                            ShareRelaTradeData tempShareRelaTradeData = new ShareRelaTradeData(userShareRelaData);
                            tempShareRelaTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                            tempShareRelaTradeData.setEndDate(lastDayTimeThisAcct);
                            btd.add(serialNumber, tempShareRelaTradeData);
                        }
                    }*/
                }
            }
        }
    }

    // 终止用户服务信息订单数据生成
    public void createEndSvcInfoTrade(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>进入createEndSvcInfoTrade>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        }
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        List<SvcTradeData> svcList = btd.getRD().getUca().getUserSvcs();// 直接从uca里面取,减少和数据库的交互
        if (svcList.size() > 0)
        {
            for (int i = 0, size = svcList.size(); i < size; i++)
            {
                SvcTradeData tempTradeData = svcList.get(i);
                // 取modifytag==USER的，因为如果状态=USER 就代表该记录是没有被处理的，所以此节点要处理掉，
                // 如果是其他状态的，说明是被其他节点处理过了。就不要再处理了
                if (StringUtils.equals(BofConst.MODIFY_TAG_USER, tempTradeData.getModifyTag()))
                {
                    SvcTradeData svcData = tempTradeData.clone();
                    svcData.setEndDate(btd.getRD().getAcceptTime());
                    svcData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    btd.add(serialNumber, svcData);
                }
            }
        }
    }

    // 终止用户服务状态订单数据生成
    public void createEndSvcStateInfoTrade(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        String userId = btd.getRD().getUca().getUserId();
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        IDataset userSvcState = UserSvcStateInfoQry.getUserValidSvcStateByUserId(userId);
        if (IDataUtil.isNotEmpty(userSvcState))
        {
            for (int i = 0, size = userSvcState.size(); i < size; i++)
            {
                SvcStateTradeData svcStateData = new SvcStateTradeData(userSvcState.getData(i));
                svcStateData.setEndDate(btd.getRD().getAcceptTime());
                svcStateData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                this.addSvcStateData(btd, serialNumber, svcStateData);
            }
        }
    }

    // 家庭网主号销户，需要终止副号短号服务(831)，和亲亲网服务(830)
    public void createEndSvcTradeByRelaAndUserIdA(BusiTradeData<BaseTradeData> btd, String userIdA, IDataset relaListUserIdA) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>进入createEndSvcInfoTrade>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        }
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        // 终止亲亲网服务(830)
        IDataset svcListUserIdA = UserSvcInfoQry.getUserSvcByUserIdAB(userIdA, userIdA);
        if (IDataUtil.isNotEmpty(svcListUserIdA))
        {
            for (int i = 0, size = svcListUserIdA.size(); i < size; i++)
            {
                SvcTradeData svcData = new SvcTradeData(svcListUserIdA.getData(i));
                svcData.setEndDate(btd.getRD().getAcceptTime());
                svcData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                btd.add(serialNumber, svcData);
            }
        }
        // 终止亲亲网成员服务
        if (IDataUtil.isNotEmpty(relaListUserIdA))
        {
            for (int i = 0, count = relaListUserIdA.size(); i < count; i++)
            {
                String userId = relaListUserIdA.getData(i).getString("USER_ID_B");
                IDataset tempSvcDataset = UserSvcInfoQry.getUserSvcByUserIdAB(userId, userIdA);
                if (IDataUtil.isNotEmpty(tempSvcDataset))
                {
                    for (int j = 0, size = tempSvcDataset.size(); j < size; j++)
                    {
                        SvcTradeData svcData = new SvcTradeData(tempSvcDataset.getData(j));
                        svcData.setEndDate(btd.getRD().getAcceptTime());
                        svcData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        btd.add(serialNumber, svcData);
                    }
                }
            }
        }
    }

    /**
     * 终止用户携转资料
     * 
     * @throws Exception
     */
    public void createEndUserNetNpTrade(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        IDataset netNpDataList = UserInfoQry.getNetNPBySN(serialNumber, "0");
        if (null != netNpDataList && netNpDataList.size() > 0)
        {
            for (int i = 0, size = netNpDataList.size(); i < size; i++)
            {
                NetNpTradeData netNpData = new NetNpTradeData(netNpDataList.getData(i));
                netNpData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                netNpData.setState("0");// 登记流程中设置是1，但是翻看完工流程之后，发现状态没有变化，继续是0.
                netNpData.setNetnpStaffId(CSBizBean.getVisit().getStaffId());
                netNpData.setNetnpDepartId(CSBizBean.getVisit().getDepartId());
                netNpData.setPortOutDate(btd.getRD().getAcceptTime());
                netNpData.setTradeTypeCode(btd.getTradeTypeCode());
                netNpData.setCancelTag("0");
                btd.add(serialNumber, netNpData);
            }
        }
    }

    /**
     * 终止用户信息台账表生成
     * 
     * @param btd
     * @throws Exception
     */
    public void createEndUserTrade(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>进入createEndUserTrade>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        }
        // DestroyUserNowReqData destroyUserNowReqData = (DestroyUserNowReqData)btd.getRD();
        String tradeTypeCode = btd.getTradeTypeCode();
        String removeTag = "2";
        // String strRemoveReasonCode = destroyUserNowReqData.getRemoveReasonCode();
        if (StringUtils.equals("7230", tradeTypeCode)) // 欠费预销
        {
            removeTag = "3";
        }
        else if (StringUtils.equals("7240", tradeTypeCode) || StringUtils.equals("7241", tradeTypeCode) || StringUtils.equals("7242", tradeTypeCode) || StringUtils.equals("7243", tradeTypeCode) || StringUtils.equals("7244", tradeTypeCode)) // 欠费注销和3种宽带销户
        {
            removeTag = "4";
        }
        else if (StringUtils.equals("234", tradeTypeCode))// 遗失卡
        {
            removeTag = "5";
        }
        else if (StringUtils.equals("47", tradeTypeCode) || StringUtils.equals("48", tradeTypeCode))
        {
            // 携出欠费注销
            removeTag = "8";
        }

        UserTradeData userData = this.getUserTradeData(btd, btd.getRD().getUca().getUserId());
        userData.setRemoveCityCode(CSBizBean.getVisit().getCityCode());
        userData.setRemoveDepartId(CSBizBean.getVisit().getDepartId());
        userData.setRemoveEparchyCode(CSBizBean.getTradeEparchyCode());
        userData.setRemoveTag(removeTag);
        userData.setDestroyTime(btd.getRD().getAcceptTime());
        userData.setModifyTag(BofConst.MODIFY_TAG_DEL);
        
        /**
         * REQ201609280002 宽带功能优化 chenxy3 2016-11-29  
         * 记录销户原因
         * */
        if(StringUtils.equals("605", tradeTypeCode) || StringUtils.equals("615", tradeTypeCode) || StringUtils.equals("1605", tradeTypeCode))
        {
        	 DestroyUserNowRequestData rd = (DestroyUserNowRequestData)btd.getRD(); 
             userData.setRemoveReasonCode(rd.getDestoryReason());
        }
        if (StringUtils.equals("1", removeTag) || StringUtils.equals("3", removeTag))
        {
            userData.setPreDestroyTime(btd.getRD().getAcceptTime());
        }

        if (!StringUtils.equals("1", removeTag) && !StringUtils.equals("3", removeTag))
        {
            // 由于该表没有对应的订单表，所以直接在这里终止
            IData delParam = new DataMap();
            delParam.put("USER_ID", btd.getRD().getUca().getUserId());
            delParam.put("DESTROY_TAG", "1");// 代扣帐户
            delParam.put("DESTROY_TIME", btd.getRD().getAcceptTime());
            delParam.put("DESTROY_CITY_CODE", CSBizBean.getVisit().getCityCode());
            delParam.put("DESTROY_DEPART_ID", CSBizBean.getVisit().getDepartId());
            delParam.put("DESTROY_STAFF_ID", CSBizBean.getVisit().getStaffId());
            Dao.executeUpdateByCodeCode("TF_F_USER_DEDUCT", "DEL_DEDUCT", delParam);
        }
    }

    /**
     * 终止用户宽带账号资料
     * 
     * @author chenzm
     * @param btd
     * @throws Exception
     */
    public void createEndUserWidenetOtherTrade(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        IDataset wideList = WidenetOtherInfoQry.getUserWidenetOtherInfo(btd.getRD().getUca().getUserId());
        if (IDataUtil.isNotEmpty(wideList))
        {
            WidenetOtherTradeData widenetOtherTradeData = new WidenetOtherTradeData(wideList.getData(0));
            widenetOtherTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
            widenetOtherTradeData.setEndDate(btd.getRD().getAcceptTime());
            btd.add(serialNumber, widenetOtherTradeData);
        }
    }

    /**
     * 终止用户宽带账号资料
     * 
     * @author chenzm
     * @param btd
     * @throws Exception
     */
    public void createEndUserWidenetTrade(BusiTradeData<BaseTradeData> btd) throws Exception
    {
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        IDataset wideList = WidenetInfoQry.getUserWidenetInfo(btd.getRD().getUca().getUserId());
        if (IDataUtil.isNotEmpty(wideList))
        {
            WideNetTradeData widenetTradeData = new WideNetTradeData(wideList.getData(0));
            widenetTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
            widenetTradeData.setEndDate(btd.getRD().getAcceptTime());
            btd.add(serialNumber, widenetTradeData);
        }
    }

    // 终止虚拟用户产品信息
    public void createEndVirtualProductTrade(String userIdA, BusiTradeData<BaseTradeData> btd) throws Exception
    {
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        IDataset userProductList = UserProductInfoQry.queryMainProduct(userIdA);

        if (userProductList.size() > 0)
        {
            for (int i = 0, size = userProductList.size(); i < size; i++)
            {
                IData userProduct = userProductList.getData(i);
                ProductTradeData tempTradeData = new ProductTradeData(userProduct);
                tempTradeData.setEndDate(btd.getRD().getAcceptTime());
                tempTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                btd.add(serialNumber, tempTradeData);

            }
        }
    }

    /**
     * @methodName: createEndVirtualUserByUserIdA
     * @Description: 终止虚拟用户客户、账户、用户资料
     * @version: v1.0.0
     * @author: xiaozb
     * @date: 2014-4-4 上午10:34:07
     */
    private void createEndVirtualUserByUserIdA(String userIdA, BusiTradeData<BaseTradeData> btd) throws Exception
    {
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        String nowAcycLastDay = AcctDayDateUtil.getCycleIdLastDayThisAcct(userIdA);
        IData userList = UcaInfoQry.qryUserInfoByUserId(userIdA);
        if (IDataUtil.isNotEmpty(userList))
        {
            // 终止虚拟用户信息
            UserTradeData tempUserData = new UserTradeData(userList);
            tempUserData.setRemoveTag("2");
            tempUserData.setDestroyTime(btd.getRD().getAcceptTime());
            tempUserData.setModifyTag(BofConst.MODIFY_TAG_DEL);
            btd.add(serialNumber, tempUserData);

            // 终止虚拟客户信息，如果此客户下只有此虚拟用户userIdA，则删除此虚拟客户；否则，不处理
            IDataset allUserCustA = UserInfoQry.getAllUserInfoByCustId(userList.getString("CUST_ID"));
            if (IDataUtil.isNotEmpty(allUserCustA) && allUserCustA.size() == 1)
            {
                IData custInfoData = UcaInfoQry.qryCustomerInfoByCustId(userList.getString("CUST_ID"));
                if (IDataUtil.isNotEmpty(custInfoData))
                {
                    CustomerTradeData tempCustData = new CustomerTradeData(custInfoData);
                    tempCustData.setRemoveTag("1");
                    tempCustData.setRemoveDate(btd.getRD().getAcceptTime());
                    tempCustData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    btd.add(serialNumber, tempCustData);
                }
            }
            // 终止虚拟付费关系和账户信息
            // 取userIdA所有未失效的付费关系
            IDataset payRelationList = PayRelaInfoQry.queryUserAfterPayRelByCycId(userIdA, nowAcycLastDay);
            if (IDataUtil.isNotEmpty(payRelationList))
            {
                String acctId = payRelationList.getData(0).getString("ACCT_ID");
                PayRelationTradeData payRelationTradeData = new PayRelationTradeData(payRelationList.getData(0));
                payRelationTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                payRelationTradeData.setEndCycleId(nowAcycLastDay);
                btd.add(serialNumber, payRelationTradeData);
                IDataset payRelationListAcctA = PayRelaInfoQry.queryValidPayByAcctId(acctId);
                // 如果此账户下的付费关系只有与次虚拟用户的一条，则终止此虚拟账户
                if (IDataUtil.isNotEmpty(payRelationListAcctA) && payRelationListAcctA.size() == 1)
                {
                    IData acctData = UcaInfoQry.qryAcctInfoByAcctId(acctId);
                    if (IDataUtil.isNotEmpty(acctData))
                    {
                        AccountTradeData accountTradeData = new AccountTradeData(acctData);
                        accountTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        accountTradeData.setRemoveTag("1");
                        btd.add(serialNumber, accountTradeData);
                    }
                }
            }
        }
    }

    /**
     * 修改用户宽带账号资料（修改WIDENET表的RSRV_STR3为空，即无平行或家庭账号关系标识）
     * 
     * @author chenzm
     * @param btd
     * @throws Exception
     */
    public void createModifyUserWidenetTrade(BusiTradeData<BaseTradeData> btd, String userIdB) throws Exception
    {
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        IDataset wideList = WidenetInfoQry.getUserWidenetInfo(userIdB);
        if (IDataUtil.isNotEmpty(wideList))
        {
            WideNetTradeData widenetTradeData = new WideNetTradeData(wideList.getData(0));
            widenetTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
            widenetTradeData.setRsrvStr3("");
            btd.add(serialNumber, widenetTradeData);
        }
    }

    // 查询是否已经存在了用户付费关系订单数据
    private PayRelationTradeData getPayRelationTradeData(BusiTradeData btd, String userId) throws Exception
    {
        List<PayRelationTradeData> tradeDataList = btd.getTradeDatas(TradeTableEnum.TRADE_PAYRELATION);
        if (tradeDataList.size() > 0)
        {
            for (int i = 0, size = tradeDataList.size(); i < size; i++)
            {
                if (StringUtils.equals(userId, tradeDataList.get(i).getUserId()))
                {
                    return tradeDataList.get(i);
                }
            }
        }

        return null;
    }

    // 查询是否已经存在了用户订单数据
    private UserTradeData getUserTradeData(BusiTradeData btd, String userId) throws Exception
    {
        List<UserTradeData> tradeUserDataList = btd.getTradeDatas(TradeTableEnum.TRADE_USER);
        if (tradeUserDataList.size() > 0)
        {
            for (int i = 0, size = tradeUserDataList.size(); i < size; i++)
            {
                if (StringUtils.equals(userId, tradeUserDataList.get(i).getUserId()))
                {
                    return tradeUserDataList.get(i);
                }
            }
        }
        UserTradeData userData = btd.getRD().getUca().getUser();
        btd.add(btd.getRD().getUca().getSerialNumber(), userData);
        return userData;
    }

    /**
     * 判断当前关系类型是否是销户需要处理的家庭关系类型
     * 
     * @param paraRelaLists
     * @param relationTypeCode
     * @return
     * @throws Exception
     */
    private boolean judgeExistFamilyRela(String userEparchyCode, String relationTypeCode) throws Exception
    {
        // 查询销户需要终止的亲情关系配置数据
        IDataset paraRelaInfos = CommparaInfoQry.getCommparaByAttrCode1("CSM", "6022", "F", userEparchyCode, null);
        if (IDataUtil.isNotEmpty(paraRelaInfos))
        {
            for (int i = 0, size = paraRelaInfos.size(); i < size; i++)
            {
                if (StringUtils.equals(relationTypeCode, paraRelaInfos.getData(i).getString("PARAM_CODE")))
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * REQ201612260011_新增CPE终端退回和销户界面
     * <br/>
     * 终止设备信息台帐
     * @param btd
     * @param userIdA
     * @throws Exception
     * @author zhuoyingzhi
     * @date 20170214
     */
    public void createEndCPEOtherByUserId(BusiTradeData<BaseTradeData> btd,String userId)throws Exception{
	       CPEActiveBean bean = BeanManager.createBean(CPEActiveBean.class); 
	        //获取原来串号信息
	   		IDataset otherInfo=bean.qryCPEOtherInfo(userId,"CPE_DEVICE");
	   		if(IDataUtil.isNotEmpty(otherInfo)){
	   			OtherTradeData otherTradeData=new OtherTradeData(otherInfo.getData(0));
	   			
	   			otherTradeData.setEndDate(btd.getRD().getAcceptTime());
	   			
	   			otherTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
	   			//不退还原因
	   			otherTradeData.setRsrvStr5(btd.getRD().getPageRequestData().getString("REMOVE_REASON", ""));
	   			if(!"".equals(btd.getRD().getPageRequestData().getString("REMOVE_REASON", ""))){
	   				//不回退
	   				otherTradeData.setRsrvStr6("客户自报损坏或丢失");
	   			}
	   			btd.add(btd.getRD().getUca().getSerialNumber(), otherTradeData);
	   		}
	   		//为了发送锁定指令给平台
	        IDataset otherCpeLocationInfo=bean.qryCPEOtherInfo(userId,"CPE_LOCATION");
	        if(IDataUtil.isNotEmpty(otherCpeLocationInfo)){
					OtherTradeData cpeLocaton = new OtherTradeData(
							otherCpeLocationInfo.getData(0));
					String rsrvValue = cpeLocaton.getRsrvValue();
							// 0是锁定
							if ("0".equals(rsrvValue)) {
								// cpe锁定用户，销户时给服开送解锁的数据
								cpeLocaton.setRsrvValue("1");
							} else {
								// 解锁的cpe用户，销户时，CPE_LOCATION不送给服开，IsNeedPf为0表示不送服开，默认是1
								cpeLocaton.setIsNeedPf("0");
							}
						cpeLocaton.setEndDate(btd.getRD().getAcceptTime());
						cpeLocaton.setModifyTag(BofConst.MODIFY_TAG_DEL);
					btd.add(btd.getRD().getUca().getSerialNumber(), cpeLocaton);
	        }
	   		
    }    
    
    private void RemindEndRelaTrade(String userIdA,BusiTradeData<BaseTradeData> btd,String mainPhoneNum) throws Exception
    {
  	  String content="尊敬的用户，您加入的亲亲网主号"+ mainPhoneNum +"已销号，您加入的亲亲网将立即失效，"
  	  		+ "亲亲网原有成员间将不再享受语音互打优惠，拨打语音将会按照正常费用收取，请您关注，谢谢。【中国移动】";
        IDataset relationAList = RelaUUInfoQry.getAllValidRelaByUserIDA(userIdA);
   
        System.out.println("=hj==亲亲网销户："+relationAList.size());
        if(IDataUtil.isNotEmpty(relationAList)){
        	for (int j = 0, size = relationAList.size(); j < size; j++)
            {
        		IData data=relationAList.getData(j);
        		String UURoleType=data.getString("ROLE_CODE_B");
        		String VicePhoneNum=data.getString("SERIAL_NUMBER_B");
        		System.out.println("=hj==VicePhoneNum："+VicePhoneNum+"   UURoleType="+UURoleType);
        		if(UURoleType.equals("2")){
        			SmsTradeData std = new SmsTradeData();
        			//建立短信发送对象，并给各个字段赋值。可参考数据库sms表给字段赋默认值。
        	        std.setSmsNoticeId(SeqMgr.getSmsSendId());
        	        std.setEparchyCode(CSBizBean.getVisit().getStaffEparchyCode());
        	        std.setBrandCode("-1");
        	        std.setInModeCode(CSBizBean.getVisit().getInModeCode());
        	        std.setSmsNetTag("0");
        	        std.setChanId("11");
        	        std.setSendObjectCode("6");
        	        std.setSendTimeCode("1");
        	        std.setSendCountCode("1");
        	        std.setRecvObjectType("00");
        	        std.setRecvId("-1");
        	        std.setSmsTypeCode("20");
        	        std.setSmsKindCode("02");
        	        std.setNoticeContentType("0");
        	        std.setReferedCount("0");
        	        std.setForceReferCount("1");
        	        std.setForceObject("");
        	        std.setForceStartTime("");
        	        std.setForceEndTime("");
        	        std.setSmsPriority("50");
        	        std.setReferTime(SysDateMgr.getSysTime());
        	        std.setReferDepartId(CSBizBean.getVisit().getDepartId());
        	        std.setReferStaffId(CSBizBean.getVisit().getStaffId());
        	        std.setDealTime(SysDateMgr.getSysTime());
        	        std.setDealStaffid(CSBizBean.getVisit().getStaffId());
        	        std.setDealDepartid(CSBizBean.getVisit().getDepartId());
        	        std.setDealState("0");// 处理状态，0：未处理
        	        std.setRemark("");
        	        std.setRevc1("");
        	        std.setRevc2("");
        	        std.setRevc3("");
        	        std.setRevc4("");
        	        std.setMonth(SysDateMgr.getSysTime().substring(5, 7));
        	        std.setDay(SysDateMgr.getSysTime().substring(8, 10));
        	        std.setCancelTag("0");
        	        //发送号码
        	        std.setRecvObject(VicePhoneNum);
        	        // 短信
        	        std.setNoticeContent(content);
        	        // 发送号码
        	        std.setForceObject("10086");
        	        btd.add(mainPhoneNum, std);
        		}
            }
        }
    }
}


