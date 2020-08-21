package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class LineAddrQrySVC extends CSBizService {

    private static final long serialVersionUID = 1L;

    /**
     * 查询快速开通地址
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset qryQuickOpenLineAddr(IData param) throws Exception {
        String standardAddr = param.getString("cond_addrStandard");
        String city = param.getString("cond_CITY");
        String area = param.getString("cond_AREA");
        return LineAddrQry.qryQuickOpenLineAddr(standardAddr, city, area, this.getPagination());
    }

    /**
     * 查询厚覆盖地址
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset qryCoverInfoAddr(IData param) throws Exception {
        String standardAddr = param.getString("STANDARD_ADDR");
        return LineAddrQry.qryCoverInfoAddr(standardAddr);
    }

    /**
     * 查询覆盖地址
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset queryLineCoverAddr(IData param) throws Exception {
        String standardAddr = param.getString("STANDARD_ADDR");
        String coverTag = param.getString("COVERTAG");
        return LineAddrQry.queryLineCoverAddr(standardAddr, coverTag, this.getPagination());
    }

    /**
     * 查询是否存在快开地址
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData qryExistQuickOpenLineAddr(IData param) throws Exception {
        String standardAddr = param.getString("STANDARD_ADDR");
        return LineAddrQry.qryExistQuickOpenLineAddr(standardAddr);
    }
}
