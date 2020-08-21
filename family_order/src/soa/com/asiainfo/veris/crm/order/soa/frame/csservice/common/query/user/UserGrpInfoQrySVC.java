
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;

public class UserGrpInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 判断ADC的业务接入号是否重复
     * 
     * @param ACCESSNUMBER
     *            ，GROUP_ID
     * @return
     * @throws Exception
     */
    public IDataset getDumpIdByajax(IData input) throws Exception
    {
        String strBizInCode = input.getString("ACCESSNUMBER");
        String groupId = input.getString("GROUP_ID");
        IDataset data = UserGrpInfoQry.getDumpIdByajax(strBizInCode, groupId);

        return data;
    }

    /**
     * 判断ADC的业务接入号是否重复
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getplatsvcBybizeservgroup(IData input) throws Exception
    {
        String strBizInCode = input.getString("ACCESSNUMBER");
        String groupId = input.getString("GROUP_ID");
        String strbiz_code = input.getString("BIZ_CODE");
        IDataset data = UserGrpInfoQry.getplatsvcBybizeservgroup(strbiz_code, strBizInCode, groupId);

        return data;
    }

    /**
     * edit by fengsl 20130514
     * 
     * @param input
     * @return
     * @throws Exception
     */

    public IDataset isChinaMobileNumber(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER", "");

        Boolean result = RouteInfoQry.isChinaMobileNumber(serialNumber);

        IData idata = new DataMap();
        idata.put("RESULT", result);

        IDataset resultSet = new DatasetList();
        resultSet.add(idata);

        return resultSet;
    }

    /**
     * 根据表名和SQLREF查询四个库的成员
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryTabSqlFromAllDb(IData input) throws Exception
    {
        String serial_number = input.getString("SERIAL_NUMBER");
        String remove_tag = input.getString("REMOVE_TAG");
        IDataset data = UserGrpInfoQry.qryTabSqlFromAllDb(serial_number, remove_tag);

        return data;
    }

    /**
     * 根据全网集团编码找客户信息
     * 
     * @author chenkh
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset queryCustGrpByGID(IData data) throws Exception
    {
        String groupCustCode = data.getString("MP_GROUP_CUST_CODE");

        return UserGrpInfoQry.queryCustGrpByGID(groupCustCode);
    }
    
    /**
     * 判断云MAS的服务代码是否重复
     * 
     * @param SERV_CODE
     * @return
     * @throws Exception
     */
    public IDataset getServCodeByajax(IData input) throws Exception
    {
        String servCode = input.getString("SERV_CODE");
        IData param = new DataMap();
        param.put("SERV_CODE", servCode);
        IDataset data = UserGrpInfoQry.getServCodeByajax(param);

        return data;
    }
}
