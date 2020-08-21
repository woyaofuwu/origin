
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.createnophonewideuser.order.trade;

import java.util.List;
import com.ailk.biz.util.Encryptor;
import com.ailk.common.util.DESUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.WideNetTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.ProductModuleCreator;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.PasswdMgr;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.createnophonewideuser.order.requestdata.NoPhoneWideUserCreateRequestData;

public class NoPhoneWideUserCreateTrade extends BaseTrade implements ITrade
{
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        NoPhoneWideUserCreateRequestData reqData = (NoPhoneWideUserCreateRequestData) btd.getRD();
        List<ProductModuleData> selectedElements = reqData.getProductElements();

        createTradeWidenet(btd, reqData);

        // 生成宽带用户
        createTradeUser(btd, reqData);

        // 处理客户核心资料
        createCustomerTradeData(btd);

        // 处理客户个人资料
        createCustPersonTradeData(btd);

        // 处理账户资料
        createAcctTradeData(btd);

        // 如果是实名制开户，需要拼用户其他表
        if (reqData.getRealName().equals("1"))
        {
            createOtherTradeData(btd);//
        }

        // 宽带用户产品台账
        createTradeProduct(btd, reqData);

        // 生成光猫信息台账，//申领模式  0租赁，1购买，2赠送,3自备  ，自备不生成other台账
        if (StringUtils.isNotBlank(reqData.getModemStyle()) && !"3".equals(reqData.getModemStyle()))
        {
            createModemTradeData(btd, reqData);
        }
        //海工商校园宽带开户 生成一条other台账
        if(null != reqData.getIsHGS() && "1".equals(reqData.getIsHGS())){
        	createHGSOtherTradeData(btd, reqData);
        }
        //REQ201807230029一机多宽业务需求1.0版本
        String paySn = btd.getRD().getPageRequestData().getString("PAY_SERIAL_NUMBER","");
        if(!StringUtils.isEmpty(paySn)){
            createOSMWOtherTradeData(btd, reqData);
        }
        //生成魔百和临时信息
        if (StringUtils.isNotBlank(reqData.getTopSetBoxProductId()))
        {
            createTopSetBoxTradeData(btd, reqData);
            
            // 生成用户关系 (绑定魔百和)
            createTradeRelationUU(btd, reqData);
            
        }

        // 生成付费关系
        createTradePayRelation(btd, reqData);

        // 构建产品
        ProductModuleCreator.createProductModuleTradeData(selectedElements, btd);

        //追加主台账信息
        appendTradeMainData(btd, reqData);

        btd.addOpenUserAcctDayData(reqData.getUca().getUserId(), "1");
        btd.addOpenAccountAcctDayData(reqData.getUca().getAcctId(), "1");
    }

    /**
     * 修改主台帐字段
     *
     * @author yuyj3
     * @param btd
     * @throws Exception
     */
    private void appendTradeMainData(BusiTradeData<BaseTradeData> btd, NoPhoneWideUserCreateRequestData reqData) throws Exception
    {
        btd.getMainTradeData().setSubscribeType("0");
        
        //身份证号码
        btd.getMainTradeData().setRsrvStr1(reqData.getUca().getCustPerson().getPsptId());

        //标记宽带开户的光猫模式
        if (StringUtils.isNotBlank(reqData.getModemStyle()))
        {
            btd.getMainTradeData().setRsrvStr2(reqData.getModemStyle());
        }
        
        //生成魔百和临时信息
        if (StringUtils.isNotBlank(reqData.getTopSetBoxProductId()))
        {
        	//告知服开订购的魔百和业务
            btd.getMainTradeData().setRsrvStr3("1");
            
            if (reqData.getTopSetBoxDeposit() > 0)
            {
                //新生成一个TradeId,魔百和转押金时调用此传入此tradeId，可以多单独返销
                btd.getMainTradeData().setRsrvStr4(SeqMgr.getTradeId());
            }
            
            //存放订购魔百和的产品
            btd.getMainTradeData().setRsrvStr5(reqData.getTopSetBoxProductId());
            
            //存放147手机号码
            btd.getMainTradeData().setRsrvStr6(reqData.getWidenetSn());
        }
        
         //两城两宽  标识
        String twoCityTag = btd.getRD().getPageRequestData().getString("TWO_CITY_SWITCH");
        String BjSerialNumber = btd.getRD().getPageRequestData().getString("BJ_SERIAL_NUMBER");
        if ("1".equals(twoCityTag))
        {
            btd.getMainTradeData().setRsrvStr7(twoCityTag); //两城两宽,北京移动号码
            btd.getMainTradeData().setRsrvStr8(BjSerialNumber); //两城两宽,北京移动号码
        }
        //经分统计需要(宽带类型)
        btd.getMainTradeData().setRsrvStr10(reqData.getWideType());
    }

    /**
     * 生成光猫台账信息
     *
     * @author yuyj3
     * @param btd
     * @throws Exception
     */
    private void createModemTradeData(BusiTradeData<BaseTradeData> btd, NoPhoneWideUserCreateRequestData reqData) throws Exception
    {
        OtherTradeData otherTradeData = new OtherTradeData();

        otherTradeData.setRsrvValueCode("FTTH");
        otherTradeData.setRsrvValue("FTTH光猫申领");
        otherTradeData.setUserId(reqData.getUca().getUserId());

        otherTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
        otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTradeData.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值

        //光猫押金
        otherTradeData.setRsrvStr2(String.valueOf(reqData.getModemDeposit())); 
        
        //调测费优惠
        otherTradeData.setRsrvStr25(btd.getRD().getPageRequestData().getString("WIDE_MODE_FEE"));

        //申领模式  0租赁，1购买，2赠送,3自备
        otherTradeData.setRsrvTag1(reqData.getModemStyle());

        //--押金状态  0,押金、1,已沉淀、2已退还
        otherTradeData.setRsrvStr7("0");

        //1:申领，2:更改，3:退还，4:丢失
        otherTradeData.setRsrvTag2("1");

        //业务类型 680:开户
        otherTradeData.setRsrvStr11("680");

        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        btd.add(reqData.getUca().getSerialNumber(), otherTradeData);
    }

    /**
     * 生成台帐付费表
     *
     * @author yuyj3
     * @param btd
     * @throws Exception
     */
    private void createTradePayRelation(BusiTradeData<BaseTradeData> btd, NoPhoneWideUserCreateRequestData reqData) throws Exception
    {
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
        payrelationTD.setInstId(SeqMgr.getInstId());
        payrelationTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        btd.add(btd.getRD().getUca().getSerialNumber(), payrelationTD);
    }

    private void createTradeProduct(BusiTradeData<BaseTradeData> btd, NoPhoneWideUserCreateRequestData reqData) throws Exception
    {
        ProductTradeData productTD = new ProductTradeData();
        productTD.setUserId(reqData.getUca().getUserId());
        productTD.setUserIdA("-1");
        productTD.setProductId(reqData.getMainProduct().getProductId());
        productTD.setProductMode(reqData.getMainProduct().getProductMode());
        productTD.setBrandCode(reqData.getMainProduct().getBrandCode());
        productTD.setInstId(SeqMgr.getInstId());
        productTD.setStartDate(reqData.getAcceptTime());
        productTD.setEndDate(SysDateMgr.getTheLastTime());
        productTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        productTD.setMainTag("1");

        btd.add(btd.getRD().getUca().getSerialNumber(), productTD);
    }

    /**
     * 用户子台账表拼串
     *
     * @param btd
     * @param reqData
     * @throws Exception
     * @author yuyj3
     */
    public void createTradeUser(BusiTradeData<BaseTradeData> btd, NoPhoneWideUserCreateRequestData reqData) throws Exception
    {
        NoPhoneWideUserCreateRequestData rd = (NoPhoneWideUserCreateRequestData) btd.getRD();
        String serialNumber = rd.getUca().getUser().getSerialNumber();
        String userPassMwd = rd.getUca().getUser().getUserPasswd(); //密码明文
        String user_id = rd.getUca().getUserId();
        UserTradeData userTD = rd.getUca().getUser().clone();

        String userPasswd = "";
        // 处理密码
        if (!"".equals(userPassMwd))
        {
            userPasswd = PasswdMgr.encryptPassWD(userPassMwd, user_id);  //密码密文
        }

        userTD.setOpenDate(reqData.getAcceptTime());
        userTD.setInDate(reqData.getAcceptTime());
        userTD.setAcctTag("2");//用户状态开户时设置为未激活，待回调后才激活@tanzheng@20171020
        //标记为无手机宽带
        userTD.setRsrvTag1("N");
        
        userTD.setUserPasswd(userPasswd);
        userTD.setRsrvStr2(userPassMwd);  //密码明文
        btd.add(serialNumber, userTD);
    }

    /**
     * 生成海工商宽带开户other台账
     *
     * @param btd
     * @param reqData
     * @throws Exception
     */
    private void createHGSOtherTradeData(BusiTradeData btd, NoPhoneWideUserCreateRequestData reqData) throws Exception {
    	OtherTradeData HGSOtherTradeData = new OtherTradeData();

    	HGSOtherTradeData.setRsrvValueCode("NOPHONE_HGS_WIDE_ADD");
    	HGSOtherTradeData.setRsrvValue("海工商无手机号码宽带开户");

    	HGSOtherTradeData.setUserId(reqData.getUca().getUserId());

    	HGSOtherTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
    	HGSOtherTradeData.setEndDate(SysDateMgr.getTheLastTime());
    	HGSOtherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
    	HGSOtherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
    	HGSOtherTradeData.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值

    	HGSOtherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        btd.add(reqData.getUca().getSerialNumber(), HGSOtherTradeData);

	}
    
    /**
     * 生成一机多宽无手机宽带开户other台账
     *
     * @param btd
     * @param reqData
     * @throws Exception
     */
    private void createOSMWOtherTradeData(BusiTradeData btd, NoPhoneWideUserCreateRequestData reqData) throws Exception {
        OtherTradeData osmwOtherTradeData = new OtherTradeData();

        osmwOtherTradeData.setRsrvValueCode("ONESN_MANYWIDE");
        osmwOtherTradeData.setRsrvValue("一机多宽无手机宽带开户");

        osmwOtherTradeData.setUserId(reqData.getUca().getUserId());
        osmwOtherTradeData.setRsrvStr1(btd.getRD().getPageRequestData().getString("PAY_SERIAL_NUMBER"));

        osmwOtherTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        osmwOtherTradeData.setEndDate(SysDateMgr.getTheLastTime());
        osmwOtherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
        osmwOtherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
        osmwOtherTradeData.setInstId(SeqMgr.getInstId());

        osmwOtherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        btd.add(reqData.getUca().getSerialNumber(), osmwOtherTradeData);

    }

    /**
     * 客户资料表
     *
     * @author yuyj3
     * @param btd
     * @throws Exception
     */
    public void createCustomerTradeData(BusiTradeData btd) throws Exception
    {
        NoPhoneWideUserCreateRequestData rd = (NoPhoneWideUserCreateRequestData) btd.getRD();
        String serialNumber = rd.getUca().getUser().getSerialNumber();
        CustomerTradeData customerTD = rd.getUca().getCustomer().clone();
        customerTD.setIsRealName(rd.getRealName()); // REQ201706200007 无手机宽带实名制判别的优化

        btd.add(serialNumber, customerTD);
    }


    /**
     * 生成台帐个人客户表
     *
     * @author yuyj3
     * @param btd
     * @throws Exception
     */
    public void createCustPersonTradeData(BusiTradeData btd) throws Exception
    {
        NoPhoneWideUserCreateRequestData rd = (NoPhoneWideUserCreateRequestData) btd.getRD();
        String serialNumber = rd.getUca().getUser().getSerialNumber();
        CustPersonTradeData custpersonTD = rd.getUca().getCustPerson().clone();

        btd.add(serialNumber, custpersonTD);
    }


    /**
     * 账户资料
     *
     * @author yuyj3
     * @param btd
     * @throws Exception
     */
    public void createAcctTradeData(BusiTradeData btd) throws Exception
    {
        NoPhoneWideUserCreateRequestData rd = (NoPhoneWideUserCreateRequestData) btd.getRD();
        String serialNumber = rd.getUca().getUser().getSerialNumber();
        AccountTradeData acctTD = rd.getUca().getAccount().clone();

        //先为待激活，完工后才改为激活状态
        acctTD.setAcctTag("2");

        btd.add(serialNumber, acctTD);
    }

    /**
     * 用户宽带台帐拼串
     *
     * @param btd
     * @param reqData
     * @throws Exception
     * @author yuyj3
     */
    private void createTradeWidenet(BusiTradeData<BaseTradeData> btd, NoPhoneWideUserCreateRequestData reqData) throws Exception
    {
        WideNetTradeData wtd = new WideNetTradeData();
        String ori = "000000000" + reqData.getUca().getUserId();
        String ori1 = ori.substring(ori.length() - 9, ori.length());
        String pwd = Encryptor.fnEncrypt(reqData.getUserPasswd(), ori1);
        wtd.setUserId(reqData.getUca().getUserId());
        wtd.setStandAddress(reqData.getStandAddress());
        wtd.setDetailAddress(reqData.getDetailAddress());
        wtd.setStandAddressCode(reqData.getStandAddressCode());
        wtd.setAcctPasswd(pwd);
        wtd.setContact(reqData.getContact());
        wtd.setContactPhone(reqData.getContactPhone());
        wtd.setPhone(reqData.getPhone());
        wtd.setInstId(SeqMgr.getInstId());
        wtd.setModifyTag(BofConst.MODIFY_TAG_ADD);
        wtd.setStartDate(reqData.getAcceptTime());
        wtd.setEndDate(SysDateMgr.getTheLastTime());
        wtd.setSuggestDate(reqData.getSuggestDate());
        wtd.setRsrvStr1(DESUtil.encrypt(reqData.getUserPasswd()));
        wtd.setRsrvStr4(reqData.getAreaCode());
        wtd.setRsrvStr2(reqData.getWideType());

        wtd.setRsrvTag1("N"); //标记为无手机宽带
        
        //add by zhangxing3 for REQ201809300014新增线上无手机宽带开户功能的需求—BOSS新增界面 
        String booktag  = btd.getRD().getPageRequestData().getString("BOOKTAG", "");
        if((null != booktag && "1".equals(booktag)) || "SD".equals(btd.getMainTradeData().getInModeCode())){
        	wtd.setRsrvTag2("N"); //标记N为先装后付
        }
        else
        {
        	wtd.setRsrvTag2("Y"); //标记Y为立即支付
        }
        //add by zhangxing3 for REQ201809300014新增线上无手机宽带开户功能的需求—BOSS新增界面 
        
        //设备号
        wtd.setRsrvNum1(reqData.getDeviceId());

        btd.add(btd.getRD().getUca().getSerialNumber(), wtd);
    }


    /**
     * 生成台帐其它资料表
     *
     * @author yuyj3
     * @param btd
     * @throws Exception
     */
    public void createOtherTradeData(BusiTradeData btd) throws Exception
    {
        NoPhoneWideUserCreateRequestData rd = (NoPhoneWideUserCreateRequestData) btd.getRD();
        String serialNumber = rd.getUca().getUser().getSerialNumber();
        String agentDepartId = CSBizBean.getVisit().getDepartId();

        OtherTradeData otherTD = new OtherTradeData();
        otherTD.setUserId(rd.getUca().getUserId());
        otherTD.setRsrvValueCode("CHRN");
        otherTD.setRsrvValue("实名制");

        otherTD.setRsrvStr1(CSBizBean.getVisit().getStaffId());
        otherTD.setRsrvStr2(SysDateMgr.getSysTime());
        otherTD.setRsrvStr3("1");
        otherTD.setRsrvStr4("10");
        otherTD.setRsrvStr5(CSBizBean.getVisit().getStaffName());
        otherTD.setStartDate(SysDateMgr.getSysTime());
        otherTD.setEndDate(SysDateMgr.getTheLastTime());
        otherTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        otherTD.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTD.setDepartId(agentDepartId);
        otherTD.setInstId(SeqMgr.getInstId());
        btd.add(serialNumber, otherTD);
    }
    
    /**
     * 增加魔百和UU关系
     * @param btd
     * @param reqData
     * @throws Exception
     * @author zhengkai
     */
    private void createTradeRelationUU(BusiTradeData<BaseTradeData> btd, NoPhoneWideUserCreateRequestData reqData) throws Exception
    {
    	NoPhoneWideUserCreateRequestData rd = (NoPhoneWideUserCreateRequestData) btd.getRD();
    	
    	String serialNumberB = rd.getWidenetSn();
    	RelationTradeData rtd = new RelationTradeData();

        rtd.setUserIdA("");    							//147号码userId (在NoPhoneWideUserCreateRegSVC中有修改)
        rtd.setUserIdB(reqData.getUca().getUserId());  // 无手机宽带服务号码userId
        rtd.setSerialNumberA(serialNumberB);           //无手机魔百和服务号码(147号码)
        rtd.setSerialNumberB(rd.getUca().getUser().getSerialNumber()); //无手机宽带帐号 （带KD_）
        rtd.setRelationTypeCode("47");
        rtd.setRoleCodeA("0");
        rtd.setRoleCodeB("1");
        
        rtd.setInstId(SeqMgr.getInstId());
        rtd.setModifyTag(BofConst.MODIFY_TAG_ADD);
        rtd.setStartDate(SysDateMgr.getSysTime());
        rtd.setEndDate(SysDateMgr.getTheLastTime());
        
        btd.add(reqData.getUca().getUser().getSerialNumber(), rtd);
        
    }
    
    /**
     * 生成魔百和临时台账信息
     * 
     * @author zhengkai5
     * @param btd
     * @throws Exception
     */
    private void createTopSetBoxTradeData(BusiTradeData<BaseTradeData> btd, NoPhoneWideUserCreateRequestData reqData) throws Exception
    {
        OtherTradeData otherTradeData = new OtherTradeData();
        otherTradeData.setRsrvValueCode("TOPSETBOX");
        otherTradeData.setRsrvValue("无手机魔百和开户临时信息");
        otherTradeData.setUserId(reqData.getUca().getUserId());
        otherTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        otherTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
        otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTradeData.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值 
        
        //用户手机号码
        otherTradeData.setRsrvStr1(reqData.getWidenetSn()); //147号码
        
        //手机用户USER_ID
       // otherTradeData.setRsrvStr2(reqData.getNormalUserId());  //147号码userID
        
        //魔百和产品ID
        otherTradeData.setRsrvStr3(reqData.getTopSetBoxProductId());
        
        //魔百和 基础平台服务
        otherTradeData.setRsrvStr4(reqData.getTopSetBoxBasePkgs());
        
        //魔百和可选平台服务
        otherTradeData.setRsrvStr5(reqData.getTopSetBoxOptionPkgs());
        
        if (reqData.getTopSetBoxDeposit() > 0)
        {
            //魔百和押金
            otherTradeData.setRsrvStr6(String.valueOf(reqData.getTopSetBoxDeposit()));
            
            //调测费优惠
            otherTradeData.setRsrvStr25(btd.getRD().getPageRequestData().getString("BOX_MODE_FEE"));
        }
        else
        {
          //魔百和押金
            otherTradeData.setRsrvStr6("0");
        }
        
        //是否需要上门安装
        otherTradeData.setRsrvStr7(reqData.getArtificialServices());
        
        otherTradeData.setRsrvStr20("1");
        
        //受理时长（月）
        otherTradeData.setRsrvStr28(reqData.getTopSetBoxTime());

        //时长费用（元）
        if(StringUtils.isNotBlank(reqData.getTopSetBoxFee())){
        	otherTradeData.setRsrvStr29(Integer.parseInt(reqData.getTopSetBoxFee())*100+"");
        }
        
       // otherTradeData.setRsrvStr29(reqData.getTopSetBoxFee());
        
        //受理时长（int）
        /*String time = reqData.getTopSetBoxTime().trim(); 
        int date = Integer.valueOf(time);
        String endDate = SysDateMgr.getAddMonthsLastDay(date,SysDateMgr.getSysDate());
        //魔百和结束时间 
        otherTradeData.setRsrvStr30(endDate);*/
        
        //宽带详细地址
        otherTradeData.setRsrvStr21(reqData.getDetailAddress());
        
        //标记是否生成 魔百和业务订单 0：未生成 Y：已生成 N:用户确认不开通,C 撤单返销
        otherTradeData.setRsrvTag1("0");
        
        //是否有魔百和营销活动。1：有，0：没有
        if (StringUtils.isNotEmpty(reqData.getTopSetBoxSaleActiveId()))
        {
            otherTradeData.setRsrvTag2("1");
        }
        else
        {
            otherTradeData.setRsrvTag2("0");
        }
        
        
        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD); 
        btd.add(reqData.getUca().getSerialNumber(), otherTradeData);
    }
}
