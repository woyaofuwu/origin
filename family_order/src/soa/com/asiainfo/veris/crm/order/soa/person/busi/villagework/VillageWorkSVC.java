
package com.asiainfo.veris.crm.order.soa.person.busi.villagework;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.UserPccException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class VillageWorkSVC extends CSBizService
{
    private static Logger logger = Logger.getLogger(VillageWorkSVC.class);

    private static final long serialVersionUID = 1L;

    public void checkData(IData input, int maxNums) throws Exception
    {
        IDataset serialLists = new DatasetList(input.getString("serialData"));

        int userOtherservOldNums = input.getInt("X_CODING_STR");

        dealData(serialLists, userOtherservOldNums, maxNums);
    }

    public IDataset checkInfos(IData input) throws Exception
    {
        IDataset results = new DatasetList();
        int maxNums = 0;

        results = getNumInfos();
        if (IDataUtil.isNotEmpty(results) && results.size() == 1)
        {

            IData tmpData = (IData) results.get(0);
            maxNums = tmpData.getInt("TAG_NUMBER");
        }
        else
        {
            CSAppException.apperr(UserPccException.CRM_UserPccInfo_03);
        }

        checkData(input, maxNums);

        return results;
    }

    public void dealData(IDataset input, int userOtherservOldNums, int maxNums) throws Exception
    {
        String tag;// 增：0、删：1、改：2表示
        int addNums = 0;
        int delNums = 0;

        if (IDataUtil.isNotEmpty(input))
        {

            for (int i = 0; i < input.size(); i++)
            {

                IData data = input.getData(i);

                if (IDataUtil.isNotEmpty(data))
                {

                    tag = data.getString("tag");

                    if (tag.equals("1"))
                    {
                        delNums++;
                    }
                    else if (tag.equals("0"))
                    {
                        addNums++;
                    }
                }

            }
        }

        if (addNums == 0 && delNums == 0)
        {
            CSAppException.apperr(UserPccException.CRM_UserPccInfo_04);
        }

        if ((userOtherservOldNums + addNums - delNums) > maxNums)
        {
            CSAppException.apperr(UserPccException.CRM_UserPccInfo_05, maxNums);
        }

    }

    public IDataset getNumInfos() throws Exception
    {
        IDataset results = new DatasetList();

        results = ParamInfoQry.getTagInfoBySubSys("CS_NUM_VILLAGEWORKSPESRLNUMS", "CSM", "0", CSBizBean.getVisit().getStaffEparchyCode());

        return results;
    }

    public IDataset queryNumInfos(IData input) throws Exception
    {
        IDataset results = new DatasetList();

        if (StringUtils.isNotBlank(input.getString("USER_ID")) && StringUtils.isNotEmpty(input.getString("USER_ID")))
        {
            results = UserOtherInfoQry.getUserOtherservByPK(input.getString("USER_ID"), "A7", "0", null);
        }
        else if (StringUtils.isNotBlank(input.getString("AUTH_SERIAL_NUMBER")) && StringUtils.isNotEmpty(input.getString("AUTH_SERIAL_NUMBER")))
        {
            IData userList = UcaInfoQry.qryUserInfoBySn(input.getString("AUTH_SERIAL_NUMBER"));
            results = UserOtherInfoQry.getUserOtherservByPK(userList.getString("USER_ID"), "A7", "0", null);
        }
        return results;
    }
}
