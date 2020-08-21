
package com.asiainfo.veris.crm.order.soa.person.busi.hintinfo;

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
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;

public class DiscntManageSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(DiscntManageSVC.class);

    public IData dealDiscntImport(IData input) throws Exception
    {
        DiscntManageBean bean = (DiscntManageBean) BeanManager.createBean(DiscntManageBean.class);
        IData ret = input;

        IDataset dataset = new DatasetList();
        IDataset failDataset = new DatasetList();// 校验失败数据存放

        String filePath = input.getString("FILE_PATH", "");

        if (filePath != null && !filePath.equals(""))
        {
            try
            {
                dataset = improtData(input);
            }
            catch (IOException e)
            {
                CSAppException.apperr(UserPccException.CRM_UserPccInfo_07);
            }

        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_29);
        }

        // 批量导入
        if (IDataUtil.isNotEmpty(dataset))
        {
            for (int i = 0; i < dataset.size(); i++)
            {
                IData data = dataset.getData(i);

                if (IDataUtil.isNotEmpty(data))
                {
                    boolean importResult = data.getBoolean("IMPORT_RESULT");// XML校验结果
                    if (!importResult)
                    {// 校验失败
                        failDataset.add(data);// 存放失败数据
                        continue;
                    }
                    else
                    {
                        IData temp = new DataMap();

                        String discntCode = data.getString("DISCNT_CODE");
                        temp.put("DISCNT_CODE", discntCode);
                        temp.put("DISCNT_EXPLAIN", data.getString("DISCNT_EXPLAIN"));
                        temp.put("UPDATE_DEPART_ID", data.getString("UPDATE_DEPART_ID").trim());
                        temp.put("UPDATE_STAFF_ID", data.getString("UPDATE_STAFF_ID").trim());
                        temp.put("UPDATE_TIME", SysDateMgr.getSysTime());

                        IData disInfo = UDiscntInfoQry.getDiscntInfoByPk(discntCode);
                        if (IDataUtil.isNotEmpty(disInfo))
                        {
                            bean.upDiscntInfo(temp);
                        }
                        else
                        {
                            bean.insertDiscntInfo(temp);
                        }

                    }

                }

            }

        }

        if (IDataUtil.isNotEmpty(failDataset))
        {
            ret.put("FailList", failDataset);
        }

        return ret;
    }

    public IDataset getHintOperation(IData input) throws Exception
    {
        String staffId = CSBizBean.getVisit().getStaffId();
        String funcright = "HINT_OPERATION";// 操作权限

        IDataset ret = StaffInfoQry.queryDataRightByIdNumCode(staffId, funcright, "1");

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
                IData array = ImpExpUtil.beginImport(null, strfileId, ExcelConfig.getSheets("import/hintinfo/DiscntImportList.xml"));
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

    public IDataset queryDiscntInfo(IData input) throws Exception
    {
        String discntCode = input.getString("DISCNT_CODE");
        String discntName = input.getString("DISCNT_NAME");

        IDataset ret = DiscntInfoQry.queryDiscntInfoByNameNoCache(discntCode, discntName, getPagination());

        return ret;
    }

    public IData upDiscntInfo(IData input) throws Exception
    {
        IData ret = new DataMap();

        DiscntManageBean bean = (DiscntManageBean) BeanManager.createBean(DiscntManageBean.class);
        input.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        input.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        input.put("UPDATE_TIME", SysDateMgr.getSysTime());

        bean.upDiscntInfo(input);

        return ret;
    }

}
