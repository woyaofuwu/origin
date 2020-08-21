
package com.asiainfo.veris.crm.order.soa.person.busi.rejectmessage;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.UserPccException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.SvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class RejectMessageSVC extends CSBizService
{
    private static Logger logger = Logger.getLogger(RejectMessageSVC.class);

    private static final long serialVersionUID = 1L;

    private static final String QUERY_TAG = "1109";

    private static final String QUERY_TYPE_FLAG = "0";

    private static final String QUERY_TYPE_PARAM = "1";

    public IDataset checkUserService(IData param) throws Exception
    {

        IDataset result = new DatasetList();
        IDataset serialList = param.getDataset("rejectMList");
        String serviceId = param.getString("SERVICE_ID");

        if (IDataUtil.isNotEmpty(serialList))
        {
            for (int i = 0; i < serialList.size(); i++)
            {
                if (StringUtils.isNotBlank(serialList.getData(i).getString("SERIAL_NUMBER")))
                {
                    IData userInfo = UcaInfoQry.qryUserInfoBySn(serialList.getData(i).getString("SERIAL_NUMBER"));
                    if (IDataUtil.isNotEmpty(userInfo))
                    {
                        IDataset userSInfo = UserInfoQry.getUserByService(serialList.getData(i).getString("SERIAL_NUMBER"), serviceId);

                        if (IDataUtil.isEmpty(userSInfo))
                        {
                            IData tempData = new DataMap();
                            tempData.put("SERIAL_NUMBER", serialList.getData(i).getString("SERIAL_NUMBER"));
                            result.add(tempData);
                        }
                    }
                }
            }
        }

        return result;
    }

    public IDataset dealImport(String filePath, String imTemplate) throws Exception
    {
        IDataset dealData = new DatasetList();

        if (StringUtils.isNotBlank(filePath) && StringUtils.isNotBlank(imTemplate))
        {
            try
            {
                dealData = improtData(imTemplate, filePath);
            }
            catch (IOException e)
            {
                CSAppException.apperr(UserPccException.CRM_UserPccInfo_01);
            }

        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_29);
        }

        return dealData;
    }

    public IDataset getAllServiceInfo(IData input) throws Exception
    {
        IDataset results = new DatasetList();

        IDataset commFlag = CommparaInfoQry.getCommpara("CSM", QUERY_TAG, QUERY_TYPE_FLAG, "0898");

        if (IDataUtil.isNotEmpty(commFlag))
        {

            boolean opearFalg = false;

            IData commFlagInfo = commFlag.getData(0);
            if ("1".equals(commFlagInfo.getString("PARA_CODE1", "")))
            {
                opearFalg = true;
            }

            if (opearFalg)
            {
                IDataset commparaInfos = CommparaInfoQry.getCommpara("CSM", QUERY_TAG, QUERY_TYPE_PARAM, "0898");
                for (int i = 0, size = commparaInfos.size(); i < size; i++)
                {
                    IData inpara = new DataMap();
                    IData commparaInfo = commparaInfos.getData(i);
                    inpara.put("SERVICE_NAME", commparaInfo.getString("PARAM_NAME"));
                    inpara.put("SERVICE_ID", commparaInfo.getString("PARA_CODE1"));
                    results.add(inpara);
                }
            }
            else
            {
                results = USvcInfoQry.qryOffersWithOfferTypeFilter(BofConst.ELEMENT_TYPE_CODE_SVC);
            }

        }
        else
        {
            results = USvcInfoQry.qryOffersWithOfferTypeFilter(BofConst.ELEMENT_TYPE_CODE_SVC);
        }

        return results;
    }

    /**
     * 导入文件
     */
    public IDataset importClick(IData param) throws Exception
    {

        return dealImport(param.getString("FILE_PATH", ""), "import/RejectMessageImport.xml");
    }

    public IDataset improtData(String imTemplate, String fileId) throws Exception
    {
        IDataset set = new DatasetList();
        if (StringUtils.isNotBlank(fileId))
        {
            String[] fileIds = fileId.split(",");
            ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());

            for (String strfileId : fileIds)
            {
                IData array = ImpExpUtil.beginImport(null, strfileId, ExcelConfig.getSheets(imTemplate));
                IDataset[] suc = (IDataset[]) array.get("right");// 解析成功的数据
                IDataset[] err = (IDataset[]) array.get("error");// 解析失败的数据
                if (IDataUtil.isNotEmpty(err[0]))
                {
                    CSAppException.apperr(UserPccException.CRM_UserPccInfo_08);
                }

                if (suc[0].size() >= 10000)
                {
                    CSAppException.apperr(UserPccException.CRM_UserPccInfo_10);
                }

                set.addAll(suc[0]);
            }
        }

        return set;
    }

}
