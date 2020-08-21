
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changepasswd.order.buildrequest;

import org.apache.commons.lang.RandomStringUtils;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changepasswd.order.requestdata.WidenetPswChgRequestData;

public class BuildWidenetPswChgRequestData extends BaseBuilder implements IBuilder
{

    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        WidenetPswChgRequestData widenetPswReqData = (WidenetPswChgRequestData) brd;

        String passwd = "";
        String queryType = param.getString("QUERY_TYPE");
        if ("3".equals(queryType))
        {
            passwd = RandomStringUtils.randomNumeric(6);
        }
        else if ("1".equals(queryType))
        {
            passwd = param.getString("NEW_PASSWORD2");
        }
        else if ("4".equals(queryType))
        {
            passwd = param.getString("PASSWD");
        }
        else
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_25);
        }
        widenetPswReqData.setNewPasswd(passwd);
        widenetPswReqData.setQueryType(queryType);
    }

    public BaseReqData getBlankRequestDataInstance()
    {

        return new WidenetPswChgRequestData();
    }

}
