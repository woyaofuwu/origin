
package com.asiainfo.veris.crm.order.soa.person.busi.specialtrademgr.order.buildrequestdata;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.specialtrademgr.order.requestdata.OtherProtectPassInfoData;
import com.asiainfo.veris.crm.order.soa.person.busi.specialtrademgr.order.requestdata.ProtectPassInfoRequestData;

public class BuildProtectPassInfoRequestData extends BaseBuilder implements IBuilder
{

    private static Logger logger = Logger.getLogger(BuildProtectPassInfoRequestData.class);

    public void buildBusiRequestData(IData data, BaseReqData brd) throws Exception
    {

        ProtectPassInfoRequestData reqData = (ProtectPassInfoRequestData) brd;
        if (StringUtils.isNotEmpty(data.getString("PROTECT_USER_SVC")) && StringUtils.isNotBlank(data.getString("PROTECT_USER_SVC")) && StringUtils.isNotEmpty(data.getString("PROTECT_PASS")) && StringUtils.isNotBlank(data.getString("PROTECT_PASS")))
        {
            List<OtherProtectPassInfoData> otherInfos = new ArrayList<OtherProtectPassInfoData>();
            OtherProtectPassInfoData otherInfo = new OtherProtectPassInfoData();

            otherInfo.setAnswer_first(data.getString("ANSWER_FIRST"));
            otherInfo.setAnswer_second(data.getString("ANSWER_SECOND"));
            otherInfo.setAnswer_third(data.getString("ANSWER_THIRD"));
            otherInfo.setCustom_question_first(data.getString("CUSTOM_QUESTION_FIRST"));
            otherInfo.setCustom_question_second(data.getString("CUSTOM_QUESTION_SECOND"));
            otherInfo.setCustom_question_third(data.getString("CUSTOM_QUESTION_THIRD"));
            otherInfo.setQuestion_first(data.getString("QUESTION_FIRST"));
            otherInfo.setQuestion_second(data.getString("QUESTION_SECOND"));
            otherInfo.setQuestion_third(data.getString("QUESTION_THIRD"));
            otherInfo.setDepart_id(data.getString("DEPART_ID"));
            otherInfo.setStaff_id(data.getString("STAFF_ID"));
            otherInfo.setStart_date(data.getString("START_DATE"));
            otherInfo.setEmail(data.getString("EMAIL"));
            otherInfos.add(otherInfo);

            reqData.setOtherInfo(otherInfos);
        }
        else
        {
            boolean firstFlag = false;
            boolean secondFlag = false;
            boolean thirdFlag = false;

            if (data.getString("QUESTION_FIRST", "").equals(data.getString("QUESTION_SECOND", "")) && !"z".equals(data.getString("QUESTION_FIRST", "")))
            {
                firstFlag = true;
            }

            if (data.getString("QUESTION_FIRST", "").equals(data.getString("QUESTION_THIRD", "")) && !"z".equals(data.getString("QUESTION_FIRST", "")))
            {
                secondFlag = true;
            }

            if (data.getString("QUESTION_SECOND", "").equals(data.getString("QUESTION_THIRD", "")) && !"z".equals(data.getString("QUESTION_SECOND", "")))
            {
                thirdFlag = true;
            }

            if ((firstFlag) || (secondFlag) || (thirdFlag))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_1117);
            }

            List<OtherProtectPassInfoData> otherInfos = new ArrayList<OtherProtectPassInfoData>();
            OtherProtectPassInfoData otherInfo = new OtherProtectPassInfoData();

            otherInfo.setAnswer_first(data.getString("ANSWER_FIRST"));
            otherInfo.setAnswer_second(data.getString("ANSWER_SECOND"));
            otherInfo.setAnswer_third(data.getString("ANSWER_THIRD"));
            otherInfo.setCustom_question_first(data.getString("CUSTOM_QUESTION_FIRST"));
            otherInfo.setCustom_question_second(data.getString("CUSTOM_QUESTION_SECOND"));
            otherInfo.setCustom_question_third(data.getString("CUSTOM_QUESTION_THIRD"));
            otherInfo.setQuestion_first(data.getString("QUESTION_FIRST"));
            otherInfo.setQuestion_second(data.getString("QUESTION_SECOND"));
            otherInfo.setQuestion_third(data.getString("QUESTION_THIRD"));
            otherInfo.setDepart_id(data.getString("DEPART_ID"));
            otherInfo.setStaff_id(data.getString("STAFF_ID"));
            otherInfo.setStart_date(data.getString("START_DATE"));
            otherInfo.setEmail(data.getString("EMAIL"));
            otherInfos.add(otherInfo);

            reqData.setOtherInfo(otherInfos);
        }

        String flag = "false";
        IDataset userSvcInfos = UserSvcInfoQry.qrySvcInfoByUserIdSvcId(reqData.getUca().getUserId(), "3312");
        if (IDataUtil.isNotEmpty(userSvcInfos))
        {
            IDataset userOtherInfos = UserOtherInfoQry.getUserOtherInfoByAll(reqData.getUca().getUserId(), "SPWP");
            if (IDataUtil.isNotEmpty(userOtherInfos))
            {
                if (IDataUtil.isNotEmpty(userOtherInfos.getData(0)))
                {
                    flag = "true";
                }
            }
        }

        reqData.setFlag(flag);
    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new ProtectPassInfoRequestData();
    }

}
