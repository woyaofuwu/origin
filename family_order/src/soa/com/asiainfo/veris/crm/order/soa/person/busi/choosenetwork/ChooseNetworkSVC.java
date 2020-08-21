
package com.asiainfo.veris.crm.order.soa.person.busi.choosenetwork;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class ChooseNetworkSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    public IDataset getCommpara(IData input) throws Exception
    {
        // 读取合作方地区
        IData result = new DataMap();
        String subsysCode = "CSM";
        String paramAttr = "948";
        String eparchyCode = CSBizService.getVisit().getStaffEparchyCode();
        IDataset areaInfos = CommparaInfoQry.getCommByParaAttr(subsysCode, paramAttr, eparchyCode);
        result.put("AREA_INFOS", areaInfos);

        // 读取合作网络
        paramAttr = "947";
        IDataset netInfos = CommparaInfoQry.getCommByParaAttr(subsysCode, paramAttr, eparchyCode);
        result.put("NET_INFOS", netInfos);

        // 读取已办理了国漫一卡多号网络优选的用户信息
        String rsrvValueCode = "ROAM";
        String userId = input.getString("USER_ID");
        IDataset dataset = UserOtherInfoQry.getUserOtherInfo(userId, rsrvValueCode);
        result.put("DEPUTY_COUNT", "0");
        if (!dataset.isEmpty())
        {
            result.put("DEPUTY_COUNT", "1");
            result.put("CUR_STATE", dataset.getData(0).getString("RSRV_STR4"));
            result.put("SERVICE_TYPE", dataset.getData(0).getString("RSRV_STR8"));
            result.put("COOPER_AREA", dataset.getData(0).getString("RSRV_STR2"));
            result.put("COOPER_NET", dataset.getData(0).getString("RSRV_STR3"));
            result.put("VAILD_DATE", dataset.getData(0).getString("START_DATE"));
            result.put("INVAILD_DATE", dataset.getData(0).getString("END_DATE"));
            result.put("MAX_FEE", dataset.getData(0).getString("RSRV_STR5"));
            result.put("SUM_FEE", dataset.getData(0).getString("RSRV_STR6"));
        }
        result.put("DEPUTY_INFOS", dataset);
        IDataset rs = new DatasetList();
        rs.add(result);
        return rs;
    }

}
