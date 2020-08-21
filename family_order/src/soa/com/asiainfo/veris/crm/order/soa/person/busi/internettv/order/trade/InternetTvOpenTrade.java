package com.asiainfo.veris.crm.order.soa.person.busi.internettv.order.trade;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.internettv.order.requestdata.InternetTvOpenRequestData;

public class InternetTvOpenTrade extends BaseTrade implements ITrade
{
    @SuppressWarnings("unchecked")
	public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
    	InternetTvOpenRequestData reqData = (InternetTvOpenRequestData) btd.getRD();

        createTopSetBoxTradeData(btd, reqData);

        //追加主台账信息
        appendTradeMainData(btd, reqData);
        
        //构造办理平台服务other
        if(StringUtils.isNotBlank(reqData.getTopSetBoxPlatSvcPkgs())){
        	createPlatSvcTradeData(btd, reqData);
        }
        
        
    }
    
    /**
     * 生成平台服务other记录台账信息
     * @author lizj
     * @param btd
     * @throws Exception
     */
    private void createPlatSvcTradeData(BusiTradeData<BaseTradeData> btd, InternetTvOpenRequestData reqData) throws Exception
    {
    	OtherTradeData otherTradeData = new OtherTradeData();
        otherTradeData.setRsrvValueCode("TOPSET_PLATSVC");
        otherTradeData.setRsrvValue("平台服务信息");
        otherTradeData.setUserId(reqData.getUca().getUserId());
        otherTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
        otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTradeData.setInstId(SeqMgr.getInstId());
         //用户手机号码
        otherTradeData.setRsrvStr1(reqData.getNormalSerialNumber());
        //手机用户USER_ID
        otherTradeData.setRsrvStr2(reqData.getNormalUserId());
        //必选包（平台服务serviceId）
        otherTradeData.setRsrvStr3(reqData.getTopSetBoxPlatSvcPkgs());
        
        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD); 
        btd.add(reqData.getUca().getSerialNumber(), otherTradeData);
    }

    /**
     * 生成魔百和临时台账信息
     * 
     * @author yuyj3
     * @param btd
     * @throws Exception
     */
    private void createTopSetBoxTradeData(BusiTradeData<BaseTradeData> btd, InternetTvOpenRequestData reqData) throws Exception
    {
        OtherTradeData otherTradeData = new OtherTradeData();
        otherTradeData.setRsrvValueCode("TOPSETBOX");
        otherTradeData.setRsrvValue("魔百和开户临时信息");
        otherTradeData.setUserId(reqData.getUca().getUserId());
        otherTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
        otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTradeData.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值 
        
        //用户手机号码
        otherTradeData.setRsrvStr1(reqData.getNormalSerialNumber());
        
        //手机用户USER_ID
        otherTradeData.setRsrvStr2(reqData.getNormalUserId());
        
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
        }
        else
        {
        	//魔百和押金
            otherTradeData.setRsrvStr6("0");
        }

        
        //是否需要上门安装
        otherTradeData.setRsrvStr7(reqData.getArtificialServices());
        
        //宽带详细地址
        otherTradeData.setRsrvStr21(reqData.getDetailAddress());
        
        //给PBOSS自动预约派单与回单用
        otherTradeData.setRsrvStr22(reqData.getRsrvStr4());

        //标记是否生成 魔百和业务订单 0：未生成 1：已生成
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

      //start-wangsc10-20181119 REQ201809040036+关于开通IPTV业务服务的需求
        IDataset topSetBoxDepositDataIPTV=CommparaInfoQry.getCommParas("CSM", "182", "600", reqData.getTopSetBoxProductId(), "0898");
        if(IDataUtil.isNotEmpty(topSetBoxDepositDataIPTV) && StringUtils.isEmpty(reqData.getTopSetBoxSaleActiveId()))
        {
        	String PARA_CODE2 = topSetBoxDepositDataIPTV.getData(0).getString("PARA_CODE2");
        	if(PARA_CODE2 != null && !PARA_CODE2.equals("")){
        		if(PARA_CODE2.equals("IPTV")){
        			otherTradeData.setRsrvStr6("10000");//魔百和押金
                    otherTradeData.setRsrvTag2("0");
        		}
        	}
        }
        //end
        //BUS201907310012关于开发家庭终端调测费的需求
/*        if("79082918".equals(reqData.getTopSetBoxSaleActiveId2()) || "2918".equals(reqData.getTopSetBoxSaleActiveId2()))
        {
        	otherTradeData.setRsrvStr6(String.valueOf(reqData.getMoFee2()));
        }
        else
        {
        	otherTradeData.setRsrvStr6("0");
        	reqData.setTopSetBoxDeposit(0);
        }*/
        otherTradeData.setRsrvStr6("0");
        //BUS201907310012关于开发家庭终端调测费的需求 
        
        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD); 
        btd.add(reqData.getUca().getSerialNumber(), otherTradeData);
    }
    
    /**
     * 修改主台帐字段
     * 
     * @author yuyj3
     * @param btd
     * @throws Exception
     */
    private void appendTradeMainData(BusiTradeData<BaseTradeData> btd, InternetTvOpenRequestData reqData) throws Exception
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
        }
      //start-wangsc10-20181119 REQ201809040036+关于开通IPTV业务服务的需求
        IDataset topSetBoxDepositDataIPTV=CommparaInfoQry.getCommParas("CSM", "182", "600", reqData.getTopSetBoxProductId(), "0898");
        if(IDataUtil.isNotEmpty(topSetBoxDepositDataIPTV))
        {
        	String PARA_CODE2 = topSetBoxDepositDataIPTV.getData(0).getString("PARA_CODE2");
        	if(PARA_CODE2 != null && !PARA_CODE2.equals("")){
        		if(PARA_CODE2.equals("IPTV")){
        			btd.getMainTradeData().setRsrvStr10("IPTV_OPEN");//告诉服开，这是IPTV开户
        		}
        	}
        }
        //end
    }
}
