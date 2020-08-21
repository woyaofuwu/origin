
package com.asiainfo.veris.crm.order.soa.person.busi.VPMNFilteIncomePhone;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.UnicomTransferQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class UnicomTransferSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 获得除三户资料以外的其它资料 外网号码段与，关联手机号
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    public IDataset loadOtherInfo(IData input) throws Exception
    {
        // 查询COMBOX列表
        IData queryParam = new DataMap();
        // 号码列表
        IData phone = new DataMap();
        // 封装结果
        IData retData = new DataMap();
        // 返回结果集
        IDataset retSet = new DatasetList();

        queryParam.put("PHONE_CODE_A", input.getString("SERIAL_NUMBER"));
        // 查询已关联数据
        IDataset resultSet = UnicomTransferQry.queryUnicomTransfer(queryParam);
        if (resultSet != null && resultSet.size() > 0)
        {
            phone = resultSet.getData(0);
        }

        // ------获取联通转接的号码号段-----
        IDataset phoneBeginDs = CommparaInfoQry.getCommparaAllCol("CSM", "1818", "1", CSBizBean.getTradeEparchyCode());
        if (phoneBeginDs == null)
        {
            phoneBeginDs = new DatasetList();
        }

        retData.put("UNION_PHONES", phone);
        retData.put("PHONE_BEGIN_LIST", phoneBeginDs);
        retSet.add(retData);

        return retSet;

    }

}
