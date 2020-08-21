
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.filter;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order.requestdata.ScoreExchMobilSelfRequestData;

/**
 * 移动自有产品兑换输出转换
 * 
 * @author huangsl
 */
public class ScoreExchMobilSelfOutFilter implements IFilterOut
{

    @Override
    public IData transfterDataOut(IData input, BusiTradeData btd) throws Exception
    {
        ScoreExchMobilSelfRequestData reqData = (ScoreExchMobilSelfRequestData) btd.getRD();
        String itemId = reqData.getStackPackage().get(0).getItemInfo().get(0).getItemId();
        IDataset exchangeMobilTypes = CommparaInfoQry.getCommpara("CSM", "4502", itemId, reqData.getUca().getUserEparchyCode());
        IData exchangeMobilType = new DataMap();
        exchangeMobilType = exchangeMobilTypes.getData(0);
        String mobilType = exchangeMobilType.getString("PARA_CODE1");
        String startType = exchangeMobilType.getString("PARA_CODE5", "");// 叠加包生效时间区分字段
        String buisType = reqData.getStackPackage().get(0).getItemInfo().get(0).getBusiType();

		//REQ201811060020关于申请在省BOSS上关联配置积分兑换流量礼品ID的需求 ,配置按天计算套餐标识和套餐截止天数
      	String endTimeFlag=exchangeMobilType.getString("PARA_CODE6",""); //1表示按天计算结束时间
    	String endTimeLen=exchangeMobilType.getString("PARA_CODE7","1"); //结束时间的长度
        IData resultData = new DataMap();
        if ("03,04,05,06,07,08".indexOf(mobilType) > -1)
        {
            resultData.put("BUSI_TYPE", buisType);
            resultData.put("BUSI_RSLT_CODE", "00");
            resultData.put("BUSI_RSLT_DESC", "业务处理成功");
            IDataset giftInfos = CommparaInfoQry.queryScoreExchangePlat(itemId);
            if (IDataUtil.isEmpty(giftInfos))
            {
                return resultData;
            }
            IData gift = giftInfos.getData(0);
            if (IDataUtil.isEmpty(gift))
            {
                return resultData;
            }
            String month = gift.getString("PARA_CODE3");// 积分时长 单位：月
            String endDate = SysDateMgr.endDateOffset(SysDateMgr.getSysDate(), String.valueOf(month), "3");
            resultData.put("USE_STIME", SysDateMgr.decodeTimestamp(SysDateMgr.getSysTime(), SysDateMgr.PATTERN_STAND_SHORT));
            resultData.put("USE_ETIME", SysDateMgr.decodeTimestamp(endDate, SysDateMgr.PATTERN_STAND_SHORT));
            resultData.put("ORDER_ID", reqData.getOrderNo());

            return resultData;
        }
        else
        {
            // 业务处理成功填写
            resultData.put("BUSI_TYPE", buisType);
            resultData.put("BUSI_RSLT_CODE", "00");
            resultData.put("BUSI_RSLT_DESC", "业务处理成功");
            // 积分兑换GPRS优惠叠加包，针对时间的处理
            if ("99".equals(mobilType))
            {
                resultData.put("BUSI_TYPE", "02"); // 叠加包属于02编码，数据库配置为了区分配置成了99，所以这里强制修改成02
                if ("0".equals(startType))
                {// 0表示叠加包下月生效
                    resultData.put("USE_STIME", SysDateMgr.decodeTimestamp(SysDateMgr.getDateNextMonthFirstDay(reqData.getAcceptTime())+SysDateMgr.START_DATE_FOREVER, SysDateMgr.PATTERN_STAND_SHORT));
                    resultData.put("USE_ETIME", SysDateMgr.decodeTimestamp(SysDateMgr.getNextMonthLastDate(), SysDateMgr.PATTERN_STAND_SHORT));
                }
                else
                {
                    resultData.put("USE_STIME", SysDateMgr.decodeTimestamp(SysDateMgr.getSysTime(), SysDateMgr.PATTERN_STAND_SHORT));
                    resultData.put("USE_ETIME", SysDateMgr.decodeTimestamp(SysDateMgr.getLastDateThisMonth(), SysDateMgr.PATTERN_STAND_SHORT));
                }
            }
            else
            {
            	//关于下发近期积分商城相关省级配合改造的通知 by zhouyl5 20141016
    			String effectFlag = exchangeMobilType.getString("PARA_CODE3","");// "" or 0 :下账期生效     1：立即生效   （99叠加包 立即生效）
    			String validDate = exchangeMobilType.getString("PARA_CODE4","0");// 有效期 精确到月   （99叠加包1一个账期）
    			
    			if("1".equals(effectFlag)){
    				resultData.put("USE_STIME", SysDateMgr.decodeTimestamp(SysDateMgr.getSysTime(), SysDateMgr.PATTERN_STAND_SHORT));
    				resultData.put("USE_ETIME", SysDateMgr.decodeTimestamp(SysDateMgr.getAddMonthsNowday(Integer.parseInt(validDate),SysDateMgr.getLastDateThisMonth()), SysDateMgr.PATTERN_STAND_SHORT));
    				//按天计算结束时间 REQ201811060020关于申请在省BOSS上关联配置积分兑换流量礼品ID的需求
					if("1".equals(endTimeFlag)){
						int endTimeLen1 = new Integer(endTimeLen);
						int seconds = (60 * 60 * 24) * (endTimeLen1 - 1);					
						String strEndDate = SysDateMgr.getOtherSecondsOfSysDate(seconds);
						strEndDate = strEndDate.substring(0, 10);
						strEndDate = strEndDate + SysDateMgr.END_DATE;  
						resultData.put("USE_ETIME",strEndDate);
					}
				}else{
    				resultData.put("USE_STIME", SysDateMgr.decodeTimestamp(SysDateMgr.getAddMonthsNowday(Integer.parseInt(validDate),SysDateMgr.getFirstDayOfNextMonth()), SysDateMgr.PATTERN_STAND_SHORT));
    				resultData.put("USE_ETIME", SysDateMgr.decodeTimestamp(SysDateMgr.getAddMonthsNowday(Integer.parseInt(validDate),SysDateMgr.getNextMonthLastDate()), SysDateMgr.PATTERN_STAND_SHORT));
    			}
            }
            resultData.put("ORDER_ID", reqData.getOrderNo());
            return resultData;
        }
    }

}
