
package com.asiainfo.veris.crm.order.soa.person.busi.userpcc;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.UserPccException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

public class UserPccImportSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(UserPccImportSVC.class);

    public IData dealPccImport(IData input) throws Exception
    {
        UserPccImportBean bean = (UserPccImportBean) BeanManager.createBean(UserPccImportBean.class);
        IData ret = input;

        IDataset dataset = new DatasetList();
        IDataset dealData = new DatasetList();
        IDataset failDataset = new DatasetList();// 校验失败数据存放

        String filePath = input.getString("FILE_PATH", "");
        if (StringUtils.isNotBlank(filePath))
        {
            try
            {
                dataset = improtData(input);
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

        int size = 0;
        int dealSize = 500;

        IDataset dealSizeList = CommparaInfoQry.getCommparaCode1("CSM", "1199", "0", getVisit().getStaffEparchyCode());
        if (dealSizeList != null && dealSizeList.size() > 0)
        {
            IData pPwdData = dealSizeList.getData(0);
            dealSize = pPwdData.getInt("PARA_CODE1");

        }

        // 批量导入
        if (IDataUtil.isNotEmpty(dataset))
        {
            for (int i = 0; i < dataset.size(); i++)
            {
                size++;

                IData data = dataset.getData(i);

                if (StringUtils.isBlank(data.getString("USER_GRADE")) || StringUtils.isEmpty(data.getString("USER_GRADE")))
                {
                    data.put("IMPORT_ERROR", "手机号码：" + data.getString("SERIAL_NUMBER").trim() + "的用户等级为空！");
                    failDataset.add(data);// 存放失败数据
                }

                if (StringUtils.isBlank(data.getString("BILL_TYPE")) || StringUtils.isEmpty(data.getString("BILL_TYPE")))
                {
                    data.put("BILL_TYPE", "1");
                }

                if (StringUtils.isBlank(data.getString("USER_BILLCYCLEDATE")) || StringUtils.isEmpty(data.getString("USER_BILLCYCLEDATE")))
                {
                    data.put("USER_BILLCYCLEDATE", "1");
                }

                if (StringUtils.isBlank(data.getString("OPER_CODE")) || StringUtils.isEmpty(data.getString("OPER_CODE")))
                {
                    data.put("OPER_CODE", "0");
                }

                IData userDataset = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER").trim(), getVisit().getStaffEparchyCode());

                if (IDataUtil.isNotEmpty(userDataset))
                {
                    IDataset userRes = UserResInfoQry.queryUserSimInfo(userDataset.getString("USER_ID"), "1", getVisit().getStaffEparchyCode());

                    if (IDataUtil.isNotEmpty(userRes))
                    {
                        IData temp = new DataMap();
                        temp.put("USER_ID", userDataset.getString("USER_ID").trim());// 用户ID
                        temp.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER").trim());// 手机号码
                        temp.put("SERIAL_NUMBER_A", data.getString("SERIAL_NUMBER_A", "").trim());// 用户用于接收短信通知的手机号码，用于PCRF向用户发送短信通知或提醒
                        temp.put("IMSI", userRes.getData(0).getString("IMSI", ""));// 用户的IMSI
                        temp.put("BILL_TYPE", data.getString("BILL_TYPE", "1").trim());// 用户的计费方式：0:在线计费;1:离线计费;
                        temp.put("USER_GRADE", data.getString("USER_GRADE"));// 用户等级及用户类型，如：金牌手机用户、银牌数据卡用户等
                        temp.put("USER_STATUS", data.getString("USER_STATUS", "").trim());// 用户配额状态1：套餐内2：套餐外3：超封顶；4-50用于集团公司统一扩展，51-100用于各省公司扩展
                        temp.put("USER_BILLCYCLEDATE", data.getString("USER_BILLCYCLEDATE", "1").trim());// 用户起帐日期，用户配额在账单日期后被复位；1-28取值；97表示每月倒数第三天；98表示每月倒数第二天；99表示每月最后一天
                        temp.put("INSERT_DATE", SysDateMgr.addSecond(SysDateMgr.getSysTime(), i));// 数据插入时间
                        temp.put("OPER_CODE", data.getString("OPER_CODE", "0").trim());// 操作代码；0：新增签约；1删除签约；2：修改签约信息；3：修改用户配额状态
                        temp.put("REMARK", "");
                        temp.put("DEAL_STATE", "0");
                        dealData.add(temp);

                    }
                    else
                    {
                        data.put("IMPORT_ERROR", "手机号码：" + data.getString("SERIAL_NUMBER").trim() + "的用户IMSI信息不存在！");
                        failDataset.add(data);// 存放失败数据
                        continue;
                    }

                }
                else
                {
                    data.put("IMPORT_ERROR", "手机号码：" + data.getString("SERIAL_NUMBER").trim() + "的用户信息不存在！");
                    failDataset.add(data);// 存放失败数据
                    continue;
                }

                if (size == dealSize)
                {
                    size = 0;
                    bean.batUserPccInfo(dealData);
                    dealData.clear();
                }
            }

            if (IDataUtil.isNotEmpty(dealData))
            {
                for (int i = 0; i < size; i++)
                {
                    IData inData = dealData.getData(i);
                    bean.batUserPccInfo(inData);
                }

            }

        }

        if (IDataUtil.isNotEmpty(failDataset))
        {
            ret.put("FailList", failDataset);
        }

        return ret;
    }

    public IDataset improtData(IData param) throws Exception
    {
        String fileId = param.getString("FILE_PATH");
        IDataset set = new DatasetList();
        if (StringUtils.isNotBlank(fileId))
        {
            String[] fileIds = fileId.split(",");
            ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());

            for (String strfileId : fileIds)
            {
                IData array = ImpExpUtil.beginImport(null, strfileId, ExcelConfig.getSheets("import/UserPccImport.xml"));
                IDataset[] suc = (IDataset[]) array.get("right");// 解析成功的数据
                IDataset[] err = (IDataset[]) array.get("error");// 解析失败的数据
                if (IDataUtil.isNotEmpty(err[0]))
                {
                    CSAppException.apperr(UserPccException.CRM_UserPccInfo_08);
                }
                set.addAll(suc[0]);
            }
        }

        return set;
    }

}
