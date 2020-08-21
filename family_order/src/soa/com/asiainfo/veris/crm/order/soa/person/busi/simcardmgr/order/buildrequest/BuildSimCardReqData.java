
package com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.buildrequest;
 
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.requestdata.SimCardBaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.requestdata.SimCardReqData;

public class BuildSimCardReqData extends BaseBuilder implements IBuilder// extends BaseBuildRequestData implements
{

    public void buildBusiRequestData(IData idata, BaseReqData brd) throws Exception
    {

        SimCardReqData simCardRD = (SimCardReqData) brd;
        String netTypeCode = simCardRD.getUca().getUser().getNetTypeCode();
        IDataset oldSimInfos = UserResInfoQry.getUserResInfosByUserIdResTypeCode(simCardRD.getUca().getUserId(), "1");
        simCardRD.setmPayTag("");
        //TODO huanghua 与产商品解耦---已解决
        IDataset mpayInfos = PlatSvcInfoQry.qryUserMPayInfo(simCardRD.getUca().getUserId(), "54");
        if (IDataUtil.isNotEmpty(mpayInfos))
        {
            IDataset mPayInfos = IBossCall.changCard2PayPlat(CSBizBean.getVisit(), simCardRD.getUca().getSerialNumber());
            if (IDataUtil.isNotEmpty(mPayInfos))
            {
                if ("0000".equals(mPayInfos.getData(0).getString("DEAL_RESULT")))
                {
                    simCardRD.setmPayTag("1");
                }
            }
        }
        simCardRD.setIsScore(idata.getString("IS_SCORE", "1"));// 积分兑换，默认不使用积分兑换
        simCardRD.setOpenMobileTag(idata.getString("OPEN_MOBILE_TAG", "1"));// 用户是否为停机用户补卡，并做开机处理，默认为不做开机处理
        simCardRD.setOldSimCardInfo(getSimInfo(oldSimInfos.getData(0).getString("IMSI"), netTypeCode));
        simCardRD.setNewSimCardInfo(getSimInfo(idata.getString("IMSI"), netTypeCode));
        simCardRD.setSimNoOccupyTag(idata.getString("SIM_NO_OCCUPY_TAG"));//SIM卡是否需要提前占用标志，默认占用
        simCardRD.setRemark(idata.getString("REMARK"));
        /**REQ201610200008 补换卡业务调整需求 20161102 CHENXY3*/
        simCardRD.setRemotecardType(idata.getString("REMOTECARD_TYPE",""));
        
        //一号一终端
        if(StringUtils.isNotBlank(idata.getString("NEW_EID",""))){
            simCardRD.setPrimarySerialNumber(idata.getString("PRIMARY_SERIAL_NUMBER"));
            simCardRD.setNewImei(idata.getString("NEW_IMEI"));
        	simCardRD.setNewEid(idata.getString("NEW_EID","")+"@"+idata.getString("NEW_IMEI",""));
            simCardRD.setOspOrderId(idata.getString("OSP_ORDER_ID",""));
            simCardRD.setOldEid(idata.getString("OLD_EID",""));
        }
    }

    public BaseReqData getBlankRequestDataInstance()
    {
        BaseReqData sard = new SimCardReqData();
        return sard;
    }

    private SimCardBaseReqData getSimInfo(String imsi, String netTypeCode) throws Exception
    {
        IDataset simInfos = ResCall.getSimCardInfo("1", "", imsi, "", netTypeCode);
        if (IDataUtil.isEmpty(simInfos))
        {
            CSAppException.apperr(CrmCardException.CRM_CARD_10);
        }
        IData simInfo = simInfos.getData(0);
        SimCardBaseReqData simCardInfo = new SimCardBaseReqData();
        simCardInfo.setImsi(imsi);
        simCardInfo.setKi(simInfo.getString("KI"));
        simCardInfo.setOpc(simInfo.getString("OPC"));
        simCardInfo.setSimCardNo(simInfo.getString("SIM_CARD_NO"));
        simCardInfo.setResTypeCode(simInfo.getString("RES_TYPE_CODE"));
        simCardInfo.setResKindCode(simInfo.getString("RES_KIND_CODE"));
        simCardInfo.setEmptyCardId(simInfo.getString("EMPTY_CARD_ID", ""));
        simCardInfo.setAgentSaleTag(simInfo.getString("RSRV_STR3", ""));// 白卡 RSRV_TAG1= 3（买断标识），sim卡空卡 RSRV_STR3 =1
        simCardInfo.setSaleMoney(simInfo.getString("RSRV_NUM1", ""));
        if ("07".equals(netTypeCode) )//物联网增加资源sim 2、3、4G标识
        {
        	simCardInfo.setResFlag4G(simInfo.getString("RSRV_STR5",""));
        }
        // 1（空卡买断标识）
        if (!"07".equals(netTypeCode) && StringUtils.isNotEmpty(simInfo.getString("EMPTY_CARD_ID", "")) && !"1U".equals(simInfo.getString("RES_TYPE_CODE")) && !"1X".equals(simInfo.getString("RES_TYPE_CODE")))
        {
        	IDataset emptyCardInfos = ResCall.getEmptycardInfo(simInfo.getString("EMPTY_CARD_ID"), "", "");
        	if(IDataUtil.isEmpty(emptyCardInfos)){
        		CSAppException.apperr(CrmCommException.CRM_COMM_103,"新卡的白卡信息不存在！");
        	}
            IData cardInfo = emptyCardInfos.getData(0);
            simCardInfo.setKi(cardInfo.getString("KI"));
            simCardInfo.setOpc(cardInfo.getString("OPC"));
            simCardInfo.setResTypeCode(cardInfo.getString("RES_TYPE_CODE"));
            simCardInfo.setResKindCode(cardInfo.getString("RES_KIND_CODE"));
            String rsrvTag1 = cardInfo.getString("RSRV_TAG1","");
            if("3".equals(rsrvTag1)){
            	simCardInfo.setAgentSaleTag("1");
            	simCardInfo.setSaleMoney(cardInfo.getString("RSRV_NUM1", ""));
            }
        }
        simCardInfo.setSimCardPasswd(simInfo.getString("PASSWORD", ""));// 密文
        simCardInfo.setSimCardPasswdKey(simInfo.getString("KIND", ""));// 密钥
        // simCardInfo.setFeeTag(simInfo.getString("FEE_TAG","0"));//0为未买断，1为新买断
        simCardInfo.setFlag4G("0");// 23G卡
        IDataset reSet = ResCall.qrySimCardTypeByTypeCode(simCardInfo.getResTypeCode());
        if (IDataUtil.isNotEmpty(reSet))
        {
        	String typeCode = reSet.getData(0).getString("NET_TYPE_CODE");
        	if("01".equals(typeCode)){
        		simCardInfo.setFlag4G("1");// 4G卡
        	}
        }
        return simCardInfo;
    }

}
