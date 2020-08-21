/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.wirelessphone.createuser.order.trade;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UBankInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.fee.FeeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AcctConsignTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.FeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PostTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.PBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.trade.BaseCreateUserTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.wirelessphone.createuser.order.requestdata.CreateUserRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.wirelessphone.createuser.order.requestdata.DeviceInfoData;
import com.asiainfo.veris.crm.order.soa.person.busi.wirelessphone.createuser.order.requestdata.PostInfoData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CreateUserTrade.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-7-9 下午07:58:28 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-7-9 chengxf2 v1.0.0 修改原因
 */

public class CreateUserTrade extends BaseCreateUserTrade implements ITrade
{

    /**
     * @Function: createAcctConsignTradeData()
     * @Description: 托收信息
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-12 下午3:52:25 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-12 yxd v1.0.0 修改原因
     */
    private void createAcctConsignTradeData(BusiTradeData btd) throws Exception
    {
        CreateUserRequestData reqData = (CreateUserRequestData) btd.getRD();
        String serialNumber = reqData.getUca().getUser().getSerialNumber();
        if (reqData.getUca().getAccount().getPayModeCode().equals("1"))
        {
            AcctConsignTradeData acctConsignTD = new AcctConsignTradeData();
            acctConsignTD.setAcctId(reqData.getUca().getAcctId());
            acctConsignTD.setPayModeCode(reqData.getUca().getAccount().getPayModeCode());// 帐户付费类型：0-现金，1-托收，2-代扣
            acctConsignTD.setEparchyCode(BizRoute.getRouteId());
            acctConsignTD.setCityCode(CSBizBean.getVisit().getCityCode());
            acctConsignTD.setSuperBankCode(reqData.getSuperBankCode());
            acctConsignTD.setBankCode(reqData.getUca().getAccount().getBankCode());
            acctConsignTD.setBankAcctNo(reqData.getUca().getAccount().getBankAcctNo());
            String bankName = UBankInfoQry.getBankNameByBankCode(reqData.getUca().getAccount().getBankCode());
            acctConsignTD.setBankAcctName(bankName);
            acctConsignTD.setConsignMode("1");// 托收方式：默认为1
            acctConsignTD.setPaymentId("4");// 储值方式：默认为4
            acctConsignTD.setPayFeeModeCode("4");
            acctConsignTD.setActTag("1");
            acctConsignTD.setPriority("0");
            acctConsignTD.setInstId(SeqMgr.getInstId());
            acctConsignTD.setStartCycleId(SysDateMgr.getNowCyc());
            acctConsignTD.setEndCycleId(SysDateMgr.getEndCycle205012());
            acctConsignTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
            btd.add(serialNumber, acctConsignTD);
        }
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-9 下午10:14:41 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-9 chengxf2 v1.0.0 修改原因
     */
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        super.createBusiTradeData(btd);
        this.createUserResTradeData(btd);
        this.createRelationTradeData(btd);
        this.createPayrelationTradeData(btd);
        this.createProdctModuleTradeData(btd);
        this.createPostTradeData(btd);
        this.processUserPrepayTag(btd);
        this.processAcctTradeData(btd);
        this.createAcctConsignTradeData(btd);
        this.BaseCreateCustomerTradeDataA(btd);
        // 固话号码预占
        PBossCall.resPreOccupy(btd.getRD().getUca().getSerialNumber(), btd.getRD().getUca().getCustomer().getPsptId());
    }
    
    /**
     * 客户资料表
     * 
     * @author sunxin
     * @param btd
     * @throws Exception
     */
    public void BaseCreateCustomerTradeDataA(BusiTradeData btd) throws Exception
    {
    	List<CustomerTradeData> customerTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_CUSTOMER);
    	if (CollectionUtils.isNotEmpty(customerTradeDatas))
        {
    		IData idInput = btd.getRD().getPageRequestData();
    		for (CustomerTradeData customerTradeData : customerTradeDatas)
            {
                customerTradeData.setRsrvStr7(idInput.getString("AGENT_CUST_NAME", ""));// 经办人名称
                customerTradeData.setRsrvStr8(idInput.getString("AGENT_PSPT_TYPE_CODE", ""));// 经办人证件类型
                customerTradeData.setRsrvStr9(idInput.getString("AGENT_PSPT_ID", ""));// 经办人证件号码
                customerTradeData.setRsrvStr10(idInput.getString("AGENT_PSPT_ADDR", ""));// 经办人证件地址
            }
        }
        
    }

    /**   
	* @Function: 
	* @Description: 该方法的描述
	*
	* @param:
	* @return：
	* @throws：异常描述
	*
	* @version: v1.0.0
	* @author: chengxf2
	* @date: 2014-9-23 上午08:41:03  
	*
	* Modification History:
	* Date         Author          Version            Description
	*---------------------------------------------------------*
	* 2014-9-23      chengxf2        v1.0.0               修改原因
	*/
	private void processAcctTradeData(BusiTradeData btd) throws Exception
	{
		List<AccountTradeData> acctList = btd.get(TradeTableEnum.TRADE_ACCOUNT.getValue());
        for (AccountTradeData acctTD : acctList)
        {
            acctTD.setRsrvStr1(SeqMgr.getTransAcctId());
            acctTD.setRsrvStr3("3");////账单类型，默认为”海南客户化账单“，可以在账户资料变更界面修改
            acctTD.setNetTypeCode("12");
        }
	}

	/**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-9 下午08:01:43 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-9 chengxf2 v1.0.0 修改原因
     */
    public List<FeeTradeData> createFeeTrade(BaseReqData brd) throws Exception
    {
        String prepayTag = brd.getUca().getUser().getPrepayTag();
        String returnFeeTag = queryReturnFeeTag(brd);// 是否返还，0-返还 1-不返还，默认返还
        List<FeeTradeData> feeTradeDatas = new ArrayList<FeeTradeData>();
        List<FeeData> feeDatas = brd.getFeeList();
        if (feeDatas != null && feeDatas.size() > 0)
        {
            int size = feeDatas.size();
            for (int i = 0; i < size; i++)
            {
                FeeData feeData = feeDatas.get(i);
                String feeMode = feeData.getFeeMode();
                String feeTypeCode = feeData.getFeeTypeCode();
                if (StringUtils.equals("2", feeMode) && StringUtils.equals("0", feeTypeCode))
                {
                    float payFee = Float.parseFloat(feeData.getFee());
                    if ("0".equals(prepayTag) && payFee > 24000 && payFee < 50000 && "0".equals(returnFeeTag))
                    {
                        FeeTradeData feeTradeData = new FeeTradeData();
                        feeTradeData.setFeeTypeCode("203");// 203:商务电话TD座机缴费
                        feeTradeData.setFeeMode(feeData.getFeeMode());
                        feeTradeData.setFee("18000");
                        feeTradeData.setOldfee("18000");
                        feeTradeData.setDiscntGiftId("20001566");// 对应账务ACTION_CODE
                        feeTradeData.setUserId(brd.getUca().getUserId());
                        feeTradeDatas.add(feeTradeData);

                        feeTradeData = new FeeTradeData();
                        feeTradeData.setFeeTypeCode(feeData.getFeeTypeCode());
                        feeTradeData.setFeeMode(feeData.getFeeMode());
                        feeTradeData.setFee(String.valueOf(payFee - 18000));
                        feeTradeData.setOldfee(String.valueOf(payFee - 18000));
                        feeTradeData.setDiscntGiftId(feeData.getDiscntGiftId());
                        feeTradeData.setUserId(brd.getUca().getUserId());
                        feeTradeDatas.add(feeTradeData);

                        continue;
                    }
                    else if ("1".equals(prepayTag))
                    {
                        FeeTradeData feeTradeData = new FeeTradeData();
                        feeTradeData.setFeeMode("9");
                        feeTradeData.setFeeTypeCode(feeData.getFeeTypeCode());
                        feeTradeData.setOldfee(feeData.getOldFee());
                        if (payFee >= 24000 && payFee < 50000 && "0".equals(returnFeeTag))
                        {
                            feeTradeData.setFee("18000");
                        }
                        else if (payFee == 12000 && "0".equals(returnFeeTag))
                        {
                            feeTradeData.setFee("10000");
                        }
                        else
                        {
                            feeTradeData.setFee("0");
                        }
                        feeTradeData.setDiscntGiftId(feeData.getDiscntGiftId());
                        feeTradeData.setUserId(brd.getUca().getUserId());
                        if (payFee == 12000)
                        {
                            feeTradeData.setRsrvStr1(String.valueOf(payFee - 10000));
                        }
                        else
                        {
                            feeTradeData.setRsrvStr1("18000".equals(feeTradeData.getFee()) ? String.valueOf(payFee - 18000) : String.valueOf(payFee));
                        }
                        feeTradeDatas.add(feeTradeData);
                        continue;
                    }
                }
                FeeTradeData feeTradeData = new FeeTradeData();
                feeTradeData.setFeeTypeCode(feeData.getFeeTypeCode());
                feeTradeData.setFeeMode(feeData.getFeeMode());
                feeTradeData.setFee(feeData.getFee());
                feeTradeData.setOldfee(feeData.getOldFee());
                feeTradeData.setDiscntGiftId(feeData.getDiscntGiftId());
                feeTradeData.setUserId(brd.getUca().getUserId());
                feeTradeDatas.add(feeTradeData);
            }
        }
        return feeTradeDatas;
    }

    /**
     * @Function: createPayrelationTradeData()
     * @Description:
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-7-22 下午4:16:08 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-22 yxd v1.0.0 修改原因
     */
    private void createPayrelationTradeData(BusiTradeData btd) throws Exception
    {
        CreateUserRequestData reqData = (CreateUserRequestData) btd.getRD();
        String serialNumber = reqData.getUca().getUser().getSerialNumber();
        PayRelationTradeData payrelationTD = new PayRelationTradeData();
        payrelationTD.setUserId(reqData.getUca().getUserId());
        payrelationTD.setAcctId(reqData.getUca().getAcctId());
        payrelationTD.setPayitemCode("-1");
        payrelationTD.setAcctPriority("0");
        payrelationTD.setUserPriority("0");
        payrelationTD.setBindType("1");
        payrelationTD.setStartCycleId(SysDateMgr.getNowCycle());
        payrelationTD.setEndCycleId(SysDateMgr.getEndCycle20501231());
        payrelationTD.setActTag("1");
        payrelationTD.setDefaultTag("1");
        payrelationTD.setLimitType("0");
        payrelationTD.setLimit("0");
        payrelationTD.setComplementTag("0");
        payrelationTD.setRemark(reqData.getRemark());
        payrelationTD.setInstId(SeqMgr.getInstId());
        payrelationTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        btd.add(serialNumber, payrelationTD);
    }

    /**
     * @Function: createPostTradeData()
     * @Description: 邮寄信息
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-7-22 下午10:10:21 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-22 yxd v1.0.0 修改原因
     */
    public void createPostTradeData(BusiTradeData btd) throws Exception
    {
        CreateUserRequestData createUserRD = (CreateUserRequestData) btd.getRD();
        PostInfoData postInfo = createUserRD.getPostInfo();
        String serialNumber = createUserRD.getUca().getUser().getSerialNumber();
        if (postInfo.getPostInfoPostTag().equals("1"))
        {
            PostTradeData postTD = new PostTradeData();
            postTD.setId(createUserRD.getUca().getUserId()); // 标识:客户、用户或帐户标识
            postTD.setIdType("1");// 标识类型：0-客户，1-用户，2-帐户
            postTD.setPostName(postInfo.getPostInfoPostName());
            postTD.setPostTag(postInfo.getPostInfoPostTag());// 邮寄标志：0-不邮寄，1-邮寄
            postTD.setPostContent(StringUtils.isNotBlank(postInfo.getPostInfoPostTypeContent()) ? postInfo.getPostInfoPostTypeContent() : " ");
            postTD.setPostTypeset(postInfo.getPostInfoPostTypeSet());// 邮寄方式：0-邮政，2-Email
            postTD.setPostCyc(postInfo.getPostInfoPostCyc());// 邮寄周期：0-按月，1-按季，2-按年
            postTD.setPostAddress(postInfo.getPostInfoPostAddress());
            postTD.setPostCode(postInfo.getPostInfoPostCode());
            postTD.setEmail(postInfo.getPostInfoEmail());
            postTD.setFaxNbr(postInfo.getPostInfoFaxNbr());
            postTD.setCustType("0");
            postTD.setStartDate(SysDateMgr.getSysTime());// 预约开户需要处理时间，修改 sunxin
            postTD.setEndDate(SysDateMgr.getTheLastTime());
            postTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
            postTD.setInstId(SeqMgr.getInstId());
            btd.add(serialNumber, postTD);
        }
    }

    /**
     * @Function: createProdctModuleTradeData()
     * @Description: 同步手机号码所订购的服务（不包括主服务）
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-7-22 下午4:34:48 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-22 yxd v1.0.0 修改原因
     */
    public void createProdctModuleTradeData(BusiTradeData btd) throws Exception
    {
        CreateUserRequestData reqData = (CreateUserRequestData) btd.getRD();
        String serialNumber = reqData.getUca().getUser().getSerialNumber();
        // 查询手机号码所订购的在用服务
        IData userInfo = UcaInfoQry.qryUserInfoBySn(reqData.getSerialNumberBind());
        IDataset bindUserSVC = UserInfoQry.getBingdingElementInfo(userInfo.getString("USER_ID"));
        // 目前固话订购的服务
        List list = btd.get(TradeTableEnum.TRADE_SVC.getValue());
        // 存储同步服务
        SvcTradeData sychSVC = null;
        if (DataSetUtils.isNotBlank(bindUserSVC))
        {
        	String strSvcSd23 = "";
     		List<ProductTradeData> productTrades = btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);
     		if (CollectionUtils.isNotEmpty(productTrades) && productTrades.size() > 0)
     		{
     			for (int i = 0; i < productTrades.size(); i++) 
     			{
     				ProductTradeData productTradeData = productTrades.get(i);
     				
     				if (BofConst.MODIFY_TAG_ADD.equals(productTradeData.getModifyTag()) && "1".equals(productTradeData.getMainTag()))
    				{
     					strSvcSd23 = productTradeData.getStartDate();
     					break;
    				}
     			}
     		}
        	
            out: for (int i = 0; i < bindUserSVC.size(); i++)
            {
                IData data = bindUserSVC.getData(i);
                // 主体服务不做同步
                if ("1".equals(data.getString("MAIN_TAG")))
                    continue;
                if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(data.getString("ELEMENT_TYPE_CODE")))
                {
                    // 存在相同服务ID则不同步
                    for (int j = 0; j < list.size(); j++)
                    {
                        ProductModuleTradeData ptd = (ProductModuleTradeData) list.get(0);
                        if (data.getString("ELEMENT_ID").equals(ptd.getElementId()))
                        {
                            continue out;
                        }
                    }
                    // 查询当前主产品是否配置对应手机服务（配置则同步）
                    List productList = btd.get(TradeTableEnum.TRADE_PRODUCT.getValue());
                    boolean flag = false;
                    String prodId = "";
                    String pkgId = "";
                    if (CollectionUtils.isNotEmpty(productList))
                    {
                        for (Object obj : productList)
                        {
                            ProductTradeData ptd = (ProductTradeData) obj;
                            IDataset set = PkgElemInfoQry.getElementByProductId(ptd.getProductId(), BofConst.ELEMENT_TYPE_CODE_SVC, data.getString("ELEMENT_ID"));
                            if (DataSetUtils.isNotBlank(set))
                            {
                                flag = true;
                                prodId = set.getData(0).getString("PRODUCT_ID");
                                pkgId = set.getData(0).getString("PACKAGE_ID");
                                break;
                            }
                        }
                    }
                    // 若当前产品有配置，同步
                    if (flag)
                    {
                    	String strStartDate = SysDateMgr.getSysDate();
                    	String strElemntID = data.getString("ELEMENT_ID");
                    	if("23".equals(strElemntID) && StringUtils.isNotBlank(strSvcSd23))
                    	{
                    		strStartDate = strSvcSd23;
                    	}
                    	
                        sychSVC = new SvcTradeData();
                        sychSVC.setUserId(reqData.getUca().getUserId());
                        sychSVC.setProductId(prodId);
                        sychSVC.setPackageId(pkgId);
                        sychSVC.setElementId(data.getString("ELEMENT_ID"));
                        sychSVC.setMainTag("0");
                        sychSVC.setInstId(SeqMgr.getInstId());
                        sychSVC.setStartDate(strStartDate);
                        sychSVC.setEndDate(SysDateMgr.END_DATE_FOREVER);
                        sychSVC.setModifyTag(BofConst.MODIFY_TAG_ADD);
                        btd.add(serialNumber, sychSVC);
                    }
                }
            }
        }
    }

    /**
     * @Function: createRelationTradeData()
     * @Description: UU关系建立 绑定-A 固话-B
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-7-22 下午3:05:41 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-22 yxd v1.0.0 修改原因
     */
    @SuppressWarnings("unchecked")
    public void createRelationTradeData(BusiTradeData btd) throws Exception
    {
        CreateUserRequestData requestData = (CreateUserRequestData) btd.getRD();
        String serialNumberA = requestData.getSerialNumberBind();
        String serialNumberB = requestData.getUca().getUser().getSerialNumber();
        // 查询绑定号码信息-A
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumberA);
        String userIdA = userInfo.getString("USER_ID");
        String userIdB = requestData.getUca().getUser().getUserId();
        RelationTradeData relationTD = new RelationTradeData();
        relationTD.setUserIdA(userIdA);
        relationTD.setSerialNumberA(serialNumberA);
        relationTD.setUserIdB(userIdB);
        relationTD.setSerialNumberB(serialNumberB);
        relationTD.setRelationTypeCode("T2");
        relationTD.setRoleCodeA("0");
        relationTD.setRoleCodeB("1");
        relationTD.setRoleTypeCode("1");
        relationTD.setOrderno("");
        relationTD.setShortCode(serialNumberA);
        relationTD.setInstId(SeqMgr.getInstId());
        relationTD.setStartDate(SysDateMgr.getSysDate());
        relationTD.setEndDate(SysDateMgr.END_DATE_FOREVER);
        relationTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        btd.add(serialNumberB, relationTD);
    }

    /**
     * @Function: createUserResTradeData()
     * @Description: 资源台帐：固网号码，绑定157号码，SIM卡，设备 。
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-7-22 上午11:07:32 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-22 yxd v1.0.0 修改原因
     */
    @SuppressWarnings("unchecked")
    public void createUserResTradeData(BusiTradeData btd) throws Exception
    {
        CreateUserRequestData requestData = (CreateUserRequestData) btd.getRD();
        String serialNumber = requestData.getUca().getUser().getSerialNumber();
        String startDate = SysDateMgr.getSysDate();
        String endDate = SysDateMgr.END_DATE_FOREVER;
        // 固网号码：SERIAL_NUMBER
        ResTradeData resTD1 = new ResTradeData();
        resTD1.setUserId(requestData.getUca().getUserId());
        resTD1.setUserIdA("-1");
        resTD1.setResTypeCode("N");
        resTD1.setResCode(requestData.getSerialNumber());
        resTD1.setImsi("0");
        resTD1.setKi("");
        resTD1.setStartDate(startDate);
        resTD1.setEndDate(endDate);
        resTD1.setModifyTag(BofConst.MODIFY_TAG_ADD);
        resTD1.setInstId(SeqMgr.getInstId());
        btd.add(serialNumber, resTD1);

        // 绑定157号码：SERIAL_NUMBER_BIND
        ResTradeData resTD2 = new ResTradeData();
        resTD2.setUserId(requestData.getUca().getUserId());
        resTD2.setUserIdA("-1");
        resTD2.setResTypeCode("0");
        resTD2.setResCode(requestData.getSerialNumberBind());
        resTD2.setImsi("0");
        resTD2.setKi("");
        resTD2.setStartDate(startDate);
        resTD2.setEndDate(endDate);
        resTD2.setModifyTag(BofConst.MODIFY_TAG_ADD);
        resTD2.setInstId(SeqMgr.getInstId());
        btd.add(serialNumber, resTD2);

        // SIM_CARD_NO
        ResTradeData resTD3 = new ResTradeData();
        resTD3.setUserId(requestData.getUca().getUserId());
        resTD3.setUserIdA("-1");
        resTD3.setResTypeCode("1");
        resTD3.setResCode(requestData.getSimCardNo());
        resTD3.setImsi(requestData.getImsi());
        resTD3.setKi("");
        resTD3.setStartDate(startDate);
        resTD3.setEndDate(endDate);
        resTD3.setModifyTag(BofConst.MODIFY_TAG_ADD);
        resTD3.setInstId(SeqMgr.getInstId());
        resTD3.setRsrvStr1("0|1");
        resTD3.setRsrvStr4("");
        resTD3.setRsrvStr5("");
        resTD3.setRsrvStr3("");
        btd.add(serialNumber, resTD3);

        // 设备
        ResTradeData resTD4 = null;
        List<DeviceInfoData> deviceList = requestData.getDeviceList();
        if (CollectionUtils.isNotEmpty(deviceList))
        {
            for (Object obj : deviceList)
            {
                DeviceInfoData deviceInfo = (DeviceInfoData) obj;
                resTD4 = new ResTradeData();
                resTD4.setUserId(requestData.getUca().getUserId());
                resTD4.setUserIdA("-1");
                resTD4.setResTypeCode(deviceInfo.getDeviceTypeCode());// W
                resTD4.setResCode(deviceInfo.getDeviceCode());
                resTD4.setStartDate(startDate);
                resTD4.setEndDate(endDate);
                resTD4.setModifyTag(BofConst.MODIFY_TAG_ADD);
                resTD4.setInstId(SeqMgr.getInstId());
                resTD4.setRsrvStr1(deviceInfo.getSaleTypeCode());
                resTD4.setRsrvStr2(deviceInfo.getSaleTypeDesc());
                resTD4.setRsrvStr4(deviceInfo.getDeviceKindCode());
                resTD4.setRsrvTag1(deviceInfo.getUseTypeCode());
                resTD4.setRsrvNum1(deviceInfo.getDevicePrice());
                btd.add(serialNumber, resTD4);
                // 物品实占
                PBossCall.resMaterialOccupy(deviceInfo.getDeviceCode(), requestData.getUca().getUserId());
            }
        }
    }

    /**
     * @Function: processUserPrepayTag()
     * @Description: 处理预付费标志(目前只有商务电话才有后付费)
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-12 下午3:31:04 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-12 yxd v1.0.0 修改原因
     */
    @SuppressWarnings("unchecked")
    private void processUserPrepayTag(BusiTradeData btd) throws Exception
    {
        List<UserTradeData> svcList = btd.get(TradeTableEnum.TRADE_USER.getValue());
        CreateUserRequestData createUserRD = (CreateUserRequestData) btd.getRD();
        for (UserTradeData uTD : svcList)
        {
            //String prepayTag = StaticUtil.getStaticValue(getVisit(), "TD_B_PRODUCT", "PRODUCT_ID", "PREPAY_TAG", createUserRD.getMainProduct().getProductId());
        	String prepayTag = "";
        	IData prepayTagDataset = UpcCall.queryOfferByOfferId("P", createUserRD.getMainProduct().getProductId(), "Y");
			if(IDataUtil.isNotEmpty(prepayTagDataset)){
				prepayTag=prepayTagDataset.getString("PREPAY_TAG");
			}
			uTD.setPrepayTag(prepayTag);
            uTD.setRsrvTag1(createUserRD.getAreaType());//1:海口;2:农村;3:各市县城;4:铁通省分免费
            uTD.setRsrvTag2("0");//是否清算：0非清算；1清算 商务电话的都是默认为“非清算”；
            uTD.setRsrvTag3(createUserRD.getOpenType()); //受理类型：0非买断；1买断
            uTD.setRsrvStr5(SeqMgr.getTransUserId());//SEQ_TRANS_USER_ID
            uTD.setRsrvStr10(createUserRD.getGiftTelphone());//是否赠送话机：0否；1是
        }
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @throws Exception
     * @date: 2014-7-9 下午10:06:42 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-9 chengxf2 v1.0.0 修改原因
     */
    private String queryReturnFeeTag(BaseReqData brd) throws Exception
    {
        String returnFeeFlag = "0"; // 是否返还，0-返还 1-不返还，默认返还
        String productId = brd.getUca().getProductId();
        String eparchyCode = brd.getUca().getUser().getEparchyCode();
        IDataset dataset = CommparaInfoQry.getCommByParaAttr("CSM", "9721", eparchyCode);
        if (IDataUtil.isNotEmpty(dataset))
        {
            for (int i = 0; i < dataset.size(); i++)
            {
                if (productId.equals(dataset.get(i, "PARAM_CODE")))
                {
                    returnFeeFlag = dataset.getData(i).getString("PARA_CODE2", "0");
                    break;
                }
            }
        }
        return returnFeeFlag;
    }
}
