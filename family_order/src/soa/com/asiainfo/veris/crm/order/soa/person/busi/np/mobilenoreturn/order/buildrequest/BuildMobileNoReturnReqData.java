
package com.asiainfo.veris.crm.order.soa.person.busi.np.mobilenoreturn.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserNpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeBhQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.np.mobilenoreturn.order.requestdata.MobileNoReturnReqData;

public class BuildMobileNoReturnReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        MobileNoReturnReqData reqData = (MobileNoReturnReqData) brd;
        String userTagSet = reqData.getUca().getUser().getUserTagSet();
        String strUserNpTag = "", iLastTradeTypeCode = "0";

        if ("".equals(userTagSet) || StringUtils.isBlank(userTagSet))
        {
            strUserNpTag = "0";
        }
        else
        {
            strUserNpTag = userTagSet.substring(0, 1);
        }

        IDataset ids = TradeBhQry.getTradeBhInfoByUserIdTradeTypeCode(reqData.getUca().getSerialNumber(), "");
        if (IDataUtil.isNotEmpty(ids))
        {
            iLastTradeTypeCode = ids.getData(0).getString("TRADE_TYPE_CODE", "0");
        }
        if ("4".equals(strUserNpTag) || "48".equals(iLastTradeTypeCode))
        {
            reqData.setTagCode("0");
        }
        else
        {
            reqData.setTagCode("1");
        }

        // SEL_BY_USER_MAX_HAIN没有sql
        //IDataset userReses = UserResInfoQry.getUserResInfosByUserIdResTypeCode(reqData.getUca().getUser().getUserId(), "1");
        IDataset userReses = UserResInfoQry.getUserResInfosByUserIdResTypeCode(reqData.getUca().getUser().getUserId(), "1");
        if(!(userReses!=null&&userReses.size()>0)){	//如果现表没有，就去历史表里查
        	userReses = UserResInfoQry.getUserHResInfosByUserIdResTypeCode(reqData.getUca().getUser().getUserId(), "1");
        }
        if (IDataUtil.isEmpty(userReses))
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "获取USER_ID=[" + reqData.getUca().getUser().getUserId() + "], RES_TYPE_CODE = 1 的资源资料异常！");

        }
        reqData.setResCode(userReses.getData(0).getString("RES_CODE"));

        IDataset userInfos = UserInfoQry.getUserInfoByUserTagSetSn(reqData.getUca().getSerialNumber(), "4");
        if (IDataUtil.isNotEmpty(userInfos) && userInfos.size() != 1)
        {
            userInfos = UserInfoQry.getUserInfoByUserTagSetSn(reqData.getUca().getSerialNumber(), "2");
            if (IDataUtil.isNotEmpty(userInfos) && userInfos.size() != 1)
            {
                userInfos = UserInfoQry.getUserInfoByUserTagSetSn(reqData.getUca().getSerialNumber(), "5");

            }
        }

        if (userInfos.size() != 1)
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_209522);
        }

        String _userTagSet = userInfos.getData(0).getString("USER_TAG_SET");
        String _strUserNpTag = "";
        if (StringUtils.isBlank(_userTagSet))
        {
            _strUserNpTag = "0";
        }
        else
        {
            _strUserNpTag = userTagSet.substring(0, 1);
        }

        if ("4".equals(_strUserNpTag))
        {
            reqData.setUserNpTag("4");
            String newUserTagSet = _userTagSet + "5" + _userTagSet.substring(1);
            reqData.setNewUserTagSet(newUserTagSet);

        }
    }

    public UcaData buildUcaData(IData param) throws Exception
    {
        String userId = "";
        String serIalNumber = param.getString("SERIAL_NUMBER");
        IDataset ids = UserInfoQry.getLatestUserInfosBySerialNumber(serIalNumber);
        if (IDataUtil.isEmpty(ids))
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "115001 获取SERIAL_NUMBER=[" + serIalNumber + "]的用户资料异常！");
        }
        userId = ids.getData(0).getString("USER_ID");

        return UcaDataFactory.getDestroyUcaByUserId(userId);
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new MobileNoReturnReqData();
    }

}
