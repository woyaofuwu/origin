package com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.nophonetopsetboxcreate.order.trade;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.nophonetopsetboxcreate.order.requestdata.NoPhoneTopSetBoxCreateRequestData;

public class NoPhoneTopSetBoxCreateTrade extends BaseTrade implements ITrade
{
    @SuppressWarnings("unchecked")
	public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
    	NoPhoneTopSetBoxCreateRequestData reqData = (NoPhoneTopSetBoxCreateRequestData) btd.getRD();

    	//生成魔百和临时台账信息
        createTradeOtherData(btd, reqData);

        //追加主台账信息
        appendTradeMainData(btd, reqData);
        
        // 生成用户关系 (绑定魔百和)
        createTradeRelationUU(btd, reqData);
    }

    /**
     * 生成魔百和临时台账信息
     * 
     * @author yuyj3
     * @param btd
     * @throws Exception
     */
    private void createTradeOtherData(BusiTradeData<BaseTradeData> btd, NoPhoneTopSetBoxCreateRequestData reqData) throws Exception
    {
        OtherTradeData otherTradeData = new OtherTradeData();
        otherTradeData.setRsrvValueCode("TOPSETBOX");
        otherTradeData.setRsrvValue("无手机宽带魔百和开户临时信息");
        otherTradeData.setUserId(reqData.getUca().getUserId());
        otherTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        
        otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
        otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTradeData.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值 
        
        //用户手机号码
        otherTradeData.setRsrvStr1(reqData.getSerialNumberB());
        
        //手机用户USER_ID
        //otherTradeData.setRsrvStr2(reqData.getUca().getUserId());//147号码userId(在NoPhoneTopSetBoxCreateRegSVC中有修改)
        
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
        
        //宽带详细地址
        otherTradeData.setRsrvStr21(reqData.getDetailAddress());
        
        //给PBOSS自动预约派单与回单用
        otherTradeData.setRsrvStr22(reqData.getRsrvStr4());
        
        //受理时长（月）
        otherTradeData.setRsrvStr28(reqData.getTopSetBoxTime());

        //时长费用（元） 
        //无手机宽带开户以分为单位，这里改为和开户一直    modify_by_duhj_kd
//        otherTradeData.setRsrvStr29(reqData.getTopSetBoxFee());
        otherTradeData.setRsrvStr29(Integer.parseInt(reqData.getTopSetBoxFee())*100+"");

        //结束时间
        /*String time = reqData.getTopSetBoxTime().trim(); 
        String endDate = SysDateMgr.getAddMonthsLastDay(Integer.parseInt(time));
        */
		
		//获取偏移n个月后的最后一天
		String endDate = SysDateMgr.getAddMonthsLastDayNoEnv(Integer.parseInt(reqData.getTopSetBoxTime())+1,SysDateMgr.getSysDate());
		otherTradeData.setRsrvStr30(endDate);
		
		
        //标记是否生成 魔百和业务订单 0：未生成 1：已生成
        otherTradeData.setRsrvTag1("0");
        
        //是否有魔百和营销活动。1：有，0：没有。无手机宽带不能办理营销互动
        otherTradeData.setRsrvTag2("0");

        //表示无手机宽带魔百和业务
        otherTradeData.setRsrvTag3("N");
        
        
        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD); 
        btd.add(reqData.getUca().getSerialNumber(), otherTradeData);
    }
    
    /**
     * 修改主台帐字段
     * @param btd
     * @throws Exception
     */
    private void appendTradeMainData(BusiTradeData<BaseTradeData> btd, NoPhoneTopSetBoxCreateRequestData reqData) throws Exception
    {
        btd.getMainTradeData().setSubscribeType("300");
        
        btd.getMainTradeData().setRsrvStr1(reqData.getWorkType());
        
        //标记用户订购了魔百和业务
        if (StringUtils.isNotBlank(reqData.getTopSetBoxProductId()))
        {
            //告知服开订购的魔百和业务
            btd.getMainTradeData().setRsrvStr3("1");
        }
        
        //生成魔百和临时信息
        if (StringUtils.isNotBlank(reqData.getTopSetBoxProductId()))
        {
        	//当有押金的时候才传该值
            if (reqData.getTopSetBoxDeposit() > 0)
            {
            	//新生成一个TradeId,魔百和转押金时调用此传入此tradeId，可以多单独返销
            	btd.getMainTradeData().setRsrvStr4(SeqMgr.getTradeId());
            }
            
            //受理时长（月）
            btd.getMainTradeData().setRsrvStr5(reqData.getTopSetBoxTime());

            //时长费用（元）
            //无手机宽带开户以分为单位，这里改为和开户一直    modify_by_duhj_kd
//            btd.getMainTradeData().setRsrvStr6(reqData.getTopSetBoxFee());
            btd.getMainTradeData().setRsrvStr6(Integer.parseInt(reqData.getTopSetBoxFee())*100+"");
            
            /*String time = reqData.getTopSetBoxTime().trim(); 
            String endDate = SysDateMgr.getAddMonthsLastDay(Integer.parseInt(time));
            //结束时间
            btd.getMainTradeData().setRsrvStr7(endDate);  */ 
            
            //时长费用（元）
            btd.getMainTradeData().setRsrvStr8(reqData.getSerialNumberB());
        }
    }
    
    /**
     * 增加魔百和UU关系
     * @param btd
     * @param reqData
     * @throws Exception
     * @author zhengkai
     */
    private void createTradeRelationUU(BusiTradeData<BaseTradeData> btd, NoPhoneTopSetBoxCreateRequestData reqData) throws Exception
    {
    	NoPhoneTopSetBoxCreateRequestData rd = (NoPhoneTopSetBoxCreateRequestData) btd.getRD();
    	//无手机宽带号码
    	String serialNumber = rd.getUca().getUser().getSerialNumber();
    	
    	String serialNumberB = rd.getSerialNumberB();
    	RelationTradeData rtd = new RelationTradeData();

        rtd.setUserIdA("147");   					   //147号码userId(在NoPhoneTopSetBoxCreateRegSVC中有修改)
        rtd.setUserIdB(reqData.getUca().getUserId());  // 无手机宽带服务号码userId
        rtd.setSerialNumberA(serialNumberB);           //无手机魔百和服务号码(147号码)
        rtd.setSerialNumberB(serialNumber);            //无手机宽带帐号 （带KD_）
        rtd.setRelationTypeCode("47");
        rtd.setRoleCodeA("0");
        rtd.setRoleCodeB("1");
        
        rtd.setInstId(SeqMgr.getInstId());
        rtd.setModifyTag(BofConst.MODIFY_TAG_ADD);
        rtd.setStartDate(SysDateMgr.getSysTime());
        rtd.setEndDate(SysDateMgr.getTheLastTime());
        
        btd.add(reqData.getUca().getUser().getSerialNumber(), rtd);
    }
}
