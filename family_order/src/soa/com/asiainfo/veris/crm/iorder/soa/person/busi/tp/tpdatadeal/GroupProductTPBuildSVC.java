package com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tpdatadeal;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.person.busi.tp.base.ReqBuildService;
import com.asiainfo.veris.crm.order.pub.exception.TpOrderException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import org.apache.log4j.Logger;

/**
 *
 针对携出销户的规则限制，即含有xx优惠则不能携出，绝大部分是RELATION_TYPE_CODE=20的集团优惠
 优惠配置
 SELECT A.*
 FROM TD_S_COMMPARA A
 WHERE A.SUBSYS_CODE = 'CSM'
 AND A.PARAM_ATTR = 2005
 AND A.PARAM_CODE = 'OUTNPLIMIT'
 AND A.PARA_CODE2 = 'D'
取消则调用USER_ID为优惠表的USER_ID_A
 调用服务 CS.DestroyGroupMemberSvc.destroyGroupMember 数据 {"SERIAL_NUMBER":"089832830036","USER_ID":"1114080823537963"}
 SERIAL_NUMBER=13707532119,USER_ID=1106091500842804
 **/
public class GroupProductTPBuildSVC extends ReqBuildService {
    private static Logger logger = Logger.getLogger(GroupProductTPBuildSVC.class);
    /**CS.DestroyGroupMemberSvc.destroyGroupMember 已经校验 ，弃用*/
    public IDataset checkBefore111(IData input)throws Exception{
        String checkTag = input.getString("CHECK_TAG","").trim();
        String userId = input.getString("USER_ID");
        String serialNumber = input.getString("SERIAL_NUMBER");
        if(StringUtils.isBlank(checkTag) || '0'!=checkTag.charAt(0)){//只有0才校验
            return null;
        }
        if(StringUtils.isBlank(serialNumber)){
            CSAppException.apperr(TpOrderException.TP_ORDER_40001,"SERIAL_NUMBER");
        }
        if(StringUtils.isBlank(userId)){
            CSAppException.apperr(TpOrderException.TP_ORDER_40001,"USER_ID");
        }
        UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
        IData conParam = new DataMap();
        if("8001".equals(ucaData.getProductId())){ // 融合V网
            conParam.put("IF_CENTRETYPE", "2");
        }
        IDataset discntList = getUserDiscntRel(userId);
        conParam.put("CHK_FLAG", "BaseInfo");
        conParam.put("USER_ID", discntList.getData(0).getString("USER_ID_A"));
        conParam.put("SERIAL_NUMBER", serialNumber);
        conParam.put("TRADE_TYPE_CODE", getTradeTypeCode(input));
        CSAppCall.call( "CS.chkGrpMebDestory", input);//集团业务的check和个人业务的不一样
        return null;
    }
    @Override
    public IData initReqData(IData input) throws Exception {
//        String userId = input.getString("USER_ID");
        String serialNumber = input.getString("SERIAL_NUMBER");

        if(StringUtils.isBlank(serialNumber)){
            CSAppException.apperr(TpOrderException.TP_ORDER_40001,"SERIAL_NUMBER");
        }
//        if(StringUtils.isBlank(userId)){
//            CSAppException.apperr(TpOrderException.TP_ORDER_40001,"USER_ID");
//        }
        UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
        IDataset discntList = getUserDiscntRel(ucaData.getUserId());
        IData reqData = new DataMap();
        reqData.put("SERIAL_NUMBER",serialNumber);
        reqData.put("USER_ID",discntList.getData(0).getString("USER_ID_A"));//注意不要搞混
        return reqData;
    }
    public IDataset getUserDiscntRel(String userId)throws Exception{
        String relationTypeCode="20";
        IDataset discntList = UserDiscntInfoQry.queryUserDiscntByUserIdLast(userId,relationTypeCode);
        if(IDataUtil.isEmpty(discntList)){
            CSAppException.apperr(TpOrderException.TP_ORDER_400027,userId,relationTypeCode);
        }
        return discntList;
    }

    @Override
    public String getTradeTypeCode(IData input) throws Exception {
        return "3837";
    }
}
