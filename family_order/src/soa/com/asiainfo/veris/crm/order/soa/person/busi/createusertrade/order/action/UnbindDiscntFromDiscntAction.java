package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;


import com.ailk.bizservice.callpf.auto.expression.function.SystemFunctions;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOfferRelInfoQry;

import java.util.List;

/**
 * //根据产品变更解除后台绑定优惠，根据COMMPARA表param_attr=9226 的配置进行解除绑定 *
 * @author chenchunni
 */
public class UnbindDiscntFromDiscntAction implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {
        UcaData uca = btd.getRD().getUca();
        String UserId = uca.getUserId();
        String unbindMainDiscntCodeEndTime = ""; // 用户退订优惠的结束时间
        String unbindDiscntCodeStartDate = ""; // 用户退订优惠的开始时间

        // 获取当前订单优惠台账信息
        List<DiscntTradeData> orderDiscntTradeList = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);

        if (orderDiscntTradeList != null && orderDiscntTradeList.size() > 0) {// 本次有优惠变更
            // 遍历订单优惠台账信息
            for (int ij = 0; ij < orderDiscntTradeList.size(); ij++) {
                DiscntTradeData orderDiscntTradeData = orderDiscntTradeList.get(ij);
                // 只处理退订优惠的台账的信息
                if (BofConst.MODIFY_TAG_DEL.equals(orderDiscntTradeData.getModifyTag()) || BofConst.MODIFY_TAG_UPD.equals(orderDiscntTradeData.getModifyTag())) {

                    String unbindMainDiscntCode = orderDiscntTradeData.getElementId();
                    // 根据退订的优惠编码，在后台查询配置表：td_s_commpara ，param_attr=9226 获取退订该优惠关联退订的优惠
                    IDataset commparaInfos9226 = CommparaInfoQry.getCommparaAllColByParser("CSM", "9226", unbindMainDiscntCode, btd.getRD().getUca().getUserEparchyCode());

                    if (IDataUtil.isNotEmpty(commparaInfos9226)) {// 存在关联退订的优惠
                        // 遍历每个关联退订的优惠
                        for (int i = 0; i < commparaInfos9226.size(); i++) {
                            String unbindDiscntCode = commparaInfos9226.getData(i).getString("PARA_CODE1");//para_code1=后台关联绑定优惠
                            String continuous = commparaInfos9226.getData(i).getString("PARA_CODE2", "");//para_code2=绑定期限(数字代表几个月，null则到2050）
                            String effTime = commparaInfos9226.getData(i).getString("PARA_CODE3");//para_code3=0-立即生效 1-次月生效 2-绝对时间
                            String giftTime = commparaInfos9226.getData(i).getString("PARA_CODE4", "");//para_code4=1 该条配置失效
                            String endTime = commparaInfos9226.getData(i).getString("PARA_CODE5", "");//para_code5=绝对时间
                            String isaccumulation = commparaInfos9226.getData(i).getString("PARA_CODE6", "");//p6=0,多次变更时要累加;
                            String discntCode7 = commparaInfos9226.getData(i).getString("PARA_CODE7", "");//para_code7=后台绑定多个优惠校验
                            String discntCode8 = commparaInfos9226.getData(i).getString("PARA_CODE8", "");//REQ201810100028 关于《校园套餐月租费打折活动》判断
                            String isUnbindDiscnt = commparaInfos9226.getData(i).getString("PARA_CODE11", ""); // 是否退订关联的优惠，1：退订，非1：不退订
                            String unbindEndDateConfig = commparaInfos9226.getData(i).getString("PARA_CODE12", ""); // 退订关联的优惠的截至时间配置。1：与绑定的主优惠截止时间一致，非1：立即终止。

                            boolean flag = true; //允许办理条件
                            
                            if ("1".equals(giftTime)) {
                                flag = false;//1该条配置失效
                                continue;
                            }
                            // 2、退订主优惠，绑定的优惠是否需要同步取消
                            if (!"1".equals(isUnbindDiscnt)){
                                flag = false; // 非1，绑定的优惠不需要退订， 跳过。
                                continue;
                            }

                            // 3、本次退订的主优惠包含该关联优惠，则不再退订  （保证优惠台账表中数据的唯一性）
                            List<DiscntTradeData> discntTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
                            for (DiscntTradeData discntTradeData : discntTradeDatas) {
                                if (BofConst.MODIFY_TAG_DEL.equals(discntTradeData.getModifyTag()) || BofConst.MODIFY_TAG_UPD.equals(discntTradeData.getModifyTag())) {
                                    String discntCodeDel = discntTradeData.getElementId();//本次新办的该种优惠
                                    if (discntCodeDel.equals(unbindDiscntCode)) {
                                        flag = false;
                                        break;
                                    }
                                }
                            }

                            // 4、主优惠绑定的优惠，如果用户资料已经退订了该优惠，则不再退订。
                            if (flag){
                                // 查询用户优惠资料表是否存在该优惠
                                IDataset userDiscs = UserDiscntInfoQry.getValidDiscntByUser(UserId, unbindDiscntCode);
                                if (userDiscs != null && userDiscs.size() == 0) {// 用户已退订
                                    flag = false;
                                    continue;
                                }
                            }

                            if (flag) {// 生成退订关联优惠台账信息
                                String endData = "";
                                unbindMainDiscntCodeEndTime = orderDiscntTradeData.getEndDate();

                                if ("1".equals(unbindEndDateConfig)){ // 退订关联优惠的截止时间与主优惠截至时间一致
                                    endData = unbindMainDiscntCodeEndTime;
                                }else {  // 截止时间立即生效
                                    endData = SysDateMgr.getSysTime();
                                }
                                List<DiscntTradeData> userDiscntDatas = btd.getRD().getUca().getUserDiscntByDiscntId(unbindDiscntCode);
                                if (null != userDiscntDatas){
                                    DiscntTradeData discntTradeData = userDiscntDatas.get(0).clone();
                                    discntTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                                    discntTradeData.setEndDate(endData);
                                    discntTradeData.setRemark("9226配置套餐绑定："+unbindMainDiscntCode+"编码套餐绑定"+unbindDiscntCode+"套餐编码");

                                    btd.add(uca.getSerialNumber(), discntTradeData);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 号码必须是激活超过30天的  OPEN_30_DAY Y:30天外  N:30天内
     * */
    public IDataset checkIfUserOpen30Day(IData params) throws Exception{
    	 
    	SQLParser parser = new SQLParser(params); 
        parser.addSQL(" select t.*,t.rowid from tf_F_user t where t.USER_ID= :USER_ID and t.open_date <= sysdate -30 and t.remove_tag='0' "); 
        IDataset infos=  Dao.qryByParse(parser); 
        
    	return infos;
    }
}
