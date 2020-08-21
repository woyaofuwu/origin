
package com.asiainfo.veris.crm.order.soa.person.busi.singlenummultidevicemanage.order.trade;

import java.util.List;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.singlenummultidevicemanage.order.requestdata.SingleNumMultiDeviceManageRequestData;

/**
 * 一号多终端设备管理
 *
 * @author Administrator
 */
public class SingleNumMultiDeviceManageTrade extends BaseTrade implements ITrade {
    public void createBusiTradeData(BusiTradeData btd) throws Exception {
        SingleNumMultiDeviceManageRequestData reqData = (SingleNumMultiDeviceManageRequestData) btd.getRD();

        if ("01".equals(reqData.getOperCode())) {
            // 生成用户关系
            createTradeRelationUU(btd, reqData);
            // 主号码用户绑定资费
            createTradeDiscnt(btd, reqData);
        } else {
            // 生成用户关系
            delTradeRelationUU(btd, reqData);
        }

        //追加主台账信息
        appendTradeMainData(btd, reqData);
    }


    /**
     * 修改主台帐字段
     *
     * @param btd
     * @throws Exception
     * @author yuyj3
     */
    private void appendTradeMainData(BusiTradeData<BaseTradeData> btd, SingleNumMultiDeviceManageRequestData reqData) throws Exception {
        btd.getMainTradeData().setSubscribeType("0");
    }


    /**
     * 增加一号多终端UU关系
     *
     * @param btd
     * @param reqData
     * @throws Exception
     * @author yuyj3
     */
    private void createTradeRelationUU(BusiTradeData<BaseTradeData> btd, SingleNumMultiDeviceManageRequestData reqData) throws Exception {
        RelationTradeData rtd = new RelationTradeData();
        rtd.setUserIdA(reqData.getUca().getUserId());
        rtd.setUserIdB("-1");
        rtd.setSerialNumberA(reqData.getUca().getSerialNumber());
        rtd.setSerialNumberB(reqData.getSerialNmberB());
        rtd.setRelationTypeCode("OM");
        rtd.setRoleCodeA("0");
        rtd.setRoleCodeB("1");
        rtd.setRsrvTag1(reqData.getAuxType());

        //操作类型：01-业务添加 02-业务删除 04-业务暂停 05-业务恢复
        rtd.setRsrvStr1(reqData.getOperCode());
        rtd.setRsrvStr2(reqData.getAuxICCID());
        rtd.setRsrvStr3(reqData.getAuxEID());
        rtd.setRsrvStr4(reqData.getAuxIMEI());

        //接口过来的没有传昵称就设置一个默认昵称
        if (StringUtils.isNotBlank(reqData.getAuxNickName())) {
            rtd.setRsrvStr5(reqData.getAuxNickName());
        } else {
            int randomNum = (int) (Math.random() * 10000);
            rtd.setRsrvStr5("副设备" + randomNum);
        }


        rtd.setInstId(SeqMgr.getInstId());
        rtd.setModifyTag(BofConst.MODIFY_TAG_ADD);
        rtd.setStartDate(SysDateMgr.getSysTime());
        rtd.setEndDate(SysDateMgr.getTheLastTime());

        btd.add(reqData.getUca().getUser().getSerialNumber(), rtd);
    }

    /**
     * 增加一号多终端主号月功能费优惠
     *
     * @param btd
     * @param reqData
     * @throws Exception
     */
    private void createTradeDiscnt(BusiTradeData<BaseTradeData> btd, SingleNumMultiDeviceManageRequestData reqData) throws Exception {
        String user_id=reqData.getUca().getUserId();
		String serial_number=reqData.getUca().getSerialNumber();
		String inst_id = SeqMgr.getInstId();
		
    	//一号多终端主号月功能费优惠ID
        String discntCode = "20170998";
        //一号多终端主号月功能费优惠编码：20170998
        List<DiscntTradeData> discntTradeDataList = reqData.getUca().getUserDiscntByDiscntId("20170998");
        //如果用户是第一次开副设备，没有有效的一号多终端主号月功能费优惠  则新增
        if (null == discntTradeDataList || discntTradeDataList.size() == 0) {
            DiscntTradeData newDiscnt = new DiscntTradeData();
            newDiscnt.setUserId(user_id);
            newDiscnt.setUserIdA("-1");
            newDiscnt.setProductId("-1");
            newDiscnt.setPackageId("-1");
            //一号多终端主号月功能费优惠编码：20170998
            newDiscnt.setElementId(discntCode);
            newDiscnt.setInstId(inst_id);
            newDiscnt.setSpecTag("0");
            newDiscnt.setStartDate(SysDateMgr.getSysTime());
            newDiscnt.setEndDate(SysDateMgr.getTheLastTime());
            newDiscnt.setRemark("一号多终端主号月功能费优惠");
            newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
            btd.add(serial_number, newDiscnt);
        }
        
        //关于做好一号双终端业务相关问题优化改造的通知 第三个改造点：增加年底前资费优惠，20191231前免收业务功能费
        IDataset oneNumDualTerminalDiscnt = CommparaInfoQry.getCommpara("CSM", "620", "ONDT_DISCNT", "ZZZZ");
        if (IDataUtil.isNotEmpty(oneNumDualTerminalDiscnt)){
        	String discntCode2 = oneNumDualTerminalDiscnt.getData(0).getString("PARA_CODE1");//资费编码
        	List<DiscntTradeData> discntTradeDataList2 = reqData.getUca().getUserDiscntByDiscntId(discntCode2);
            if (null == discntTradeDataList2 || discntTradeDataList2.size() == 0) {
    			DiscntTradeData discntTradeData2 = new DiscntTradeData();
    			discntTradeData2.setUserId(user_id);
    			discntTradeData2.setUserIdA("-1");
    			discntTradeData2.setProductId("-1");
    			discntTradeData2.setPackageId("-1");
    	        discntTradeData2.setElementId(discntCode2);
    			discntTradeData2.setInstId(SeqMgr.getInstId());
    			discntTradeData2.setSpecTag("0");
    			discntTradeData2.setStartDate(SysDateMgr.getSysTime());
    			discntTradeData2.setEndDate("2019-12-31 23:59:59");
    			discntTradeData2.setRemark("一号双终端年底前免收业务功能费绑定");
    			discntTradeData2.setModifyTag(BofConst.MODIFY_TAG_ADD);//0:新增,1:删除,2:修改
    			btd.add(serial_number, discntTradeData2);	
    	   }
        }
        
    }


    /**
     * 删除一号多终端UU关系
     *
     * @param btd
     * @param reqData
     * @throws Exception
     * @author yuyj3
     */
    private void delTradeRelationUU(BusiTradeData<BaseTradeData> btd, SingleNumMultiDeviceManageRequestData reqData) throws Exception {
        IData inputParam = new DataMap();
        inputParam.put("USER_ID", btd.getRD().getUca().getUserId());
        inputParam.put("RELATION_TYPE_CODE", "OM");
        inputParam.put("SERIAL_NUMBER_B", reqData.getSerialNmberB());

        IDataset relaUUInfos = RelaUUInfoQry.qryRelationList(inputParam);
        if (IDataUtil.isEmpty(relaUUInfos)) {
            CSAppException.appError("-1", "该主号码跟副号码不存在有效的一号多终端关系!");
        }

        RelationTradeData rtd = new RelationTradeData(relaUUInfos.getData(0));

        rtd.setEndDate(SysDateMgr.getSysDate());
        if (!"".equals(reqData.getReqType())) {
            rtd.setRsrvStr1("03");
        } else {
            rtd.setRsrvStr1("02");
        }
        rtd.setModifyTag(BofConst.MODIFY_TAG_DEL);

        btd.add(reqData.getUca().getUser().getSerialNumber(), rtd);

        IData inParam = new DataMap();
        inParam.put("USER_ID", btd.getRD().getUca().getUserId());
        inParam.put("RELATION_TYPE_CODE", "OM");

        //所有有效的一号多终端记录
        IDataset allSNMDRelaUUInfos = RelaUUInfoQry.qryRelationList(inParam);
        String delAuxDeivceSerNum = reqData.getSerialNmberB();
        if (IDataUtil.isNotEmpty(allSNMDRelaUUInfos)) {
            //查询是否有一号多终端删除未完工工单
            IDataset maintTradeInfos = TradeInfoQry.getMainTradeBySN(reqData.getUca().getSerialNumber(), "396");
            if (IDataUtil.isNotEmpty(maintTradeInfos)) {
                for (int i = 0; i < maintTradeInfos.size(); i++) {
                    String tradeId = maintTradeInfos.getData(i).getString("TRADE_ID");
                    IDataset tradeRelaInfos = TradeRelaInfoQry.getTradeRelaByTradeIdRelaType(tradeId, "OM");
                    if (IDataUtil.isNotEmpty(tradeRelaInfos)) {
                        for (int j = 0; j < tradeRelaInfos.size(); j++) {
                            IData tradeRelaInfo = tradeRelaInfos.getData(j);
                            //表示要删除的副设备
                            if ("02".equals(tradeRelaInfo.getString("RSRV_STR1"))) {
                                delAuxDeivceSerNum = delAuxDeivceSerNum + "," + tradeRelaInfo.getString("SERIAL_NUMBER_B");
                            }
                        }
                    }
                }
            }

            boolean isAllDel = true;
            for (int k = 0; k < allSNMDRelaUUInfos.size(); k++) {
                IData allSNMDRelaUUInfo = allSNMDRelaUUInfos.getData(k);
                if (!delAuxDeivceSerNum.contains(allSNMDRelaUUInfo.getString("SERIAL_NUMBER_B"))) {
                    isAllDel = false;
                    break;
                }
            }

            if (isAllDel) {
                delTradeDiscnt(btd, reqData);
            }
        } else {
            delTradeDiscnt(btd, reqData);
        }

    }


    /**
     * 增加一号多终端主号月功能费优惠
     *
     * @param btd
     * @param reqData
     * @throws Exception
     */
    private void delTradeDiscnt(BusiTradeData<BaseTradeData> btd, SingleNumMultiDeviceManageRequestData reqData) throws Exception {
    	String serial_number=reqData.getUca().getSerialNumber();
    	//一号多终端主号月功能费优惠编码：20170998
        List<DiscntTradeData> discntTradeDataList = reqData.getUca().getUserDiscntByDiscntId("20170998");
        if (null != discntTradeDataList && discntTradeDataList.size() > 0) {
            for (int i = 0; i < discntTradeDataList.size(); i++) {
                DiscntTradeData discntTradeData = discntTradeDataList.get(i);

                discntTradeData.setEndDate(SysDateMgr.getSysDate());
                discntTradeData.setRemark("一号多终端主号月功能费优惠退订");
                discntTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);

                btd.add(serial_number, discntTradeData);
            }
        }

        //关于做好一号双终端业务相关问题优化改造的通知 第三个改造点：增加年底前资费优惠，20191231前免收业务功能费
        IDataset oneNumDualTerminalDiscnt = CommparaInfoQry.getCommpara("CSM", "620", "ONDT_DISCNT", "ZZZZ");
        if (IDataUtil.isNotEmpty(oneNumDualTerminalDiscnt)){
        	String discntCode2 = oneNumDualTerminalDiscnt.getData(0).getString("PARA_CODE1");//资费编码
        	List<DiscntTradeData> discntTradeDataList2 = reqData.getUca().getUserDiscntByDiscntId(discntCode2);
            if (null != discntTradeDataList2 && discntTradeDataList2.size() > 0) {
                for (int i = 0; i < discntTradeDataList2.size(); i++) {
    				DiscntTradeData discntTradeData2 = discntTradeDataList.get(i);
    				discntTradeData2.setEndDate(SysDateMgr.getSysDate());
    				discntTradeData2.setRemark("一号双终端年底前免收业务功能费");
    				discntTradeData2.setModifyTag(BofConst.MODIFY_TAG_DEL);//0:新增,1:删除,2:修改
    				
    				btd.add(serial_number, discntTradeData2);	
                }
            }
        }
    }
}
