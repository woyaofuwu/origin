
package com.asiainfo.veris.crm.order.soa.person.busi.towndataautoinput;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class TownDataAutoInputSVC extends CSBizService
{
    private static Logger logger = Logger.getLogger(TownDataAutoInputSVC.class);

    private static final long serialVersionUID = 1L;

    public IDataset tradeReg(IData input) throws Exception
    {

        TownDataAutoInputBean bean = (TownDataAutoInputBean) BeanManager.createBean(TownDataAutoInputBean.class);

        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", input.getString("OPER_CODE"));
        data.put("RSRV_STR6", input.getString("PATITION_CODE"));// 分区编号
        data.put("RSRV_STR7", input.getString("NEW_LITTLE_CODE"));// 小区编号
        data.put("RSRV_STR8", input.getString("NEW_LAC_INFO"));// 小区归属LAC信息
        data.put("RSRV_STR9", input.getString("OLD_LITTLE_CODE"));// 小区编号(老值)
        data.put("RSRV_STR10", input.getString("OLD_LAC_INFO"));// 小区归属LAC信息(老值)
        bean.insMainTrade(data);

        return new DatasetList();
    }

}
