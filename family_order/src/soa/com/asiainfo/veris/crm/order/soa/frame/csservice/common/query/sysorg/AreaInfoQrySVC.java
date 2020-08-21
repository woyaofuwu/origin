
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;

public class AreaInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset getAreaByPk(IData inParam) throws Exception
    {
        String area_code = inParam.getString("AREA_CODE");
        String this_tag = inParam.getString("THIS_TAG");
        String use_tag = inParam.getString("USE_TAG");

        return UAreaInfoQry.qryAreaByPk(area_code, this_tag, use_tag);
    }

    /**
     * 根据AREA_CODE 查询AREA_NAME
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset getAreaNameByAreaCode(IData inParam) throws Exception
    {
        String areaCode = inParam.getString("AREA_CODE");
        String areaName = UAreaInfoQry.getAreaNameByAreaCode(areaCode);
        inParam.put("AREA_NAME", areaName);

        return IDataUtil.idToIds(inParam);
    }

    public IDataset qryAeraByAreaFrame(IData inParam) throws Exception
    {
        String areaFrame = inParam.getString("AREA_FRAME");

        return UAreaInfoQry.qryAeraLikeAreaFrame(areaFrame);
    }

}
