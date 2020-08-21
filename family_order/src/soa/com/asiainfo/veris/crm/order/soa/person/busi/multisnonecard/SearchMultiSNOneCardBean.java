
package com.asiainfo.veris.crm.order.soa.person.busi.multisnonecard;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.res.ResParaInfoQry;

public class SearchMultiSNOneCardBean extends CSBizBean
{

    /**
     * 取得开通国漫一卡多号地区列表
     * 
     * @return
     * @throws Exception
     * @author wangww
     */
    public IDataset getdeployState() throws Exception
    {
        String typeId = "DEPLOYSTATE";
        return StaticInfoQry.getStaticValueByTypeId(typeId);
    }

    /**
     * 从配置表中取得一卡多号号段前缀
     * 
     * @param param
     * @return
     * @throws Exception
     * @author wangww
     */
    public IDataset getPreforOneCardMutiNumber(IData param) throws Exception
    {
        String usercard = param.getString("USERCARD");
        String dataId = usercard;
        String typdId = "USERCARD";
        IData data = StaticInfoQry.getStaticInfoByTypeIdDataId(typdId, dataId);
        IDataset dataset = new DatasetList();
        dataset.add(data);
        if (null == dataset || 0 == dataset.size())
        {
            CSAppException.apperr(ParamException.CRM_PARAM_142, usercard);
            return null;
        }
        else
        {
            return dataset;
        }
    }

    /**
     * 从配置表中查询所选区域的副卡号码
     * 
     * @param params
     * @param p
     * @return
     * @throws Exception
     */
    public IDataset querySearchCard(IData params, Pagination p) throws Exception
    {
        IDataset paraCodeList = new DatasetList();
        // IDataset dataset = ResCall.getParaValue(paramValue1, eparchyCode, paraAttr, paraCode1);
        IDataset dataset = ResParaInfoQry.getResAreaInfo(params.getString("PARA_VALUE1"), null);
        if (dataset != null && dataset.size() > 0)
        {
            for (int i = 0; i < dataset.size(); i++)
            {
                IData paraCode = new DataMap();
                paraCode.put("PARA_CODE2", dataset.getData(i).getString("PARA_CODE2"));
                paraCodeList.add(paraCode);
            }
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "副卡号码未配置！");
        }
        return paraCodeList;
    }

}
