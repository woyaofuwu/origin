
package com.asiainfo.veris.crm.order.soa.person.busi.singlenummultidevicemanage.order.action.reg;


import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;

/**
 * 一号多终端业务同步表
 *
 * @author yuyj3
 */
public class AddMDRPRealSyncAction implements ITradeAction {

    public void executeAction(BusiTradeData btd) throws Exception {
        List<RelationTradeData> relationTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_RELATION);

        if (null != relationTradeDatas) {
            for (RelationTradeData relationTradeData : relationTradeDatas) {
                IData realSyncData = new DataMap();
                //添加回退的情况直接接口后面进行处理，这里直接过滤  add by tanjl
                if ("03".equals(relationTradeData.getRsrvStr1())) {
                    continue;
                }

                String sysDateString = SysDateMgr.getSysDateYYYYMMDDHHMMSS();

                int randomNum = (int) (Math.random() * 10000);

                String seq = sysDateString + randomNum;
                String serialNumberA = relationTradeData.getSerialNumberA();

                //操作流水
                realSyncData.put("SEQ", seq);

                //对账日期
                realSyncData.put("REQ_DAY", SysDateMgr.getSysDateYYYYMMDD());

                //主号码
                realSyncData.put("PRI_MSISDN", serialNumberA);

                //操作类型：01-业务添加 02-业务删除 04-业务暂停 05-业务恢复
                realSyncData.put("OPR_CODE", relationTradeData.getRsrvStr1());

                //
                if ("01".equals(relationTradeData.getRsrvStr1())) {
                    //添加结果：OPR_CODE为01时必填 0--添加失败 1--添加成功
                    realSyncData.put("ADD_RESULT", "1");
                } else if ("02".equals(relationTradeData.getRsrvStr1())) {
                    //删除结果：OPR_CODE为02时必填 0--删除失败 1--删除成功
                    realSyncData.put("DEL_RESULT", "1");
                }

                //副号码类型：1-eSIM卡，2-实体卡
                realSyncData.put("AUX_TYPE", relationTradeData.getRsrvTag1());

                //副号MSISDN
                realSyncData.put("AUX_MSISDN", relationTradeData.getSerialNumberB());

                //副卡ICCID
                realSyncData.put("AUX_ICCID", relationTradeData.getRsrvStr2());

                //副卡EID：AUX_TYPE为1时必填
                realSyncData.put("EID", relationTradeData.getRsrvStr3());

                //副设备IMEI：AUX_TYPE为1时必填
                realSyncData.put("IMEI", relationTradeData.getRsrvStr4());

                //副设备昵称：ADD_RESULT为1时必填
                realSyncData.put("AUX_NICK_NAME", relationTradeData.getRsrvStr5());

                //创建时间
                realSyncData.put("CTEATE_TIME", sysDateString);

                //处理结果编码
                realSyncData.put("BIZ_ORDER_RESULT", "0000");
                //反馈时间
                realSyncData.put("RSP_TIME", sysDateString);
                //反馈结果
                realSyncData.put("RESULT_DESC", "0");
                //反馈次数
                realSyncData.put("RSP_COUNT", "0");
                
                //关于做好一号双终端业务相关问题优化改造的通知 第一个改造点：增加PriIMSI和AuxIMSI两个必填字段
				realSyncData.put("RSRV_STR1", btd.getMainTradeData().getRsrvStr1()); // 主号imsi
				realSyncData.put("RSRV_STR2", btd.getMainTradeData().getRsrvStr2()); // 副号imsi

                Dao.insert("TI_B_MDRP_REALSYNC", realSyncData, Route.CONN_CRM_CEN);
            }
        }
    }
}
