
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class BBossAttrQrySVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 根据attrcode查询TD_S_BBOSS_ATTR参数表的配置信息
     * 
     * @author ft
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset qryBBossAttrByAttrCode(IData param) throws Exception
    {
        String attr_code = param.getString("ATTR_CODE", "");
        return BBossAttrQry.qryBBossAttrByAttrCode(attr_code);
    }

    /**
     * 根据属性组编号查询属性组包含的所有属性，并且按照显示顺序INDEX由小到大输出
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset qryBBossAttrByGroupAttrBizType(IData param) throws Exception
    {
        String groupAttr = param.getString("GROUP_ATTR");
        String bizType = param.getString("BIZ_TYPE");

        return BBossAttrQry.qryBBossAttrByGroupAttrBizType(groupAttr, bizType);
    }

    /**
     * 根据产品编码和业务类型查询bbossattr表配置信息
     * 
     * @author ft
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset qryBBossAttrByPospecBiztype(IData param) throws Exception
    {
        String productSpecNumber = param.getString("PRODUCTSPECNUMBER", "");
        String bizType = param.getString("BIZ_TYPE");

        return BBossAttrQry.qryBBossAttrByPospecBiztype(productSpecNumber, bizType);
    }

    /**
     * 根据产品编码、操作类型、业务类型查询bbossattr配置表信息
     * 
     * @author ft
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset qryBBossAttrByPospecOpertypeBiztype(IData param) throws Exception
    {

        String product_id = param.getString("PRODUCT_ID");
        String operType = param.getString("OPERTYPE");
        String bizType = param.getString("BIZTYPE");

        return BBossAttrQry.qryBBossAttrByPospecOpertypeBiztype(product_id, operType, bizType);
    }
    
    /**
     * 根据departCode查询代理商记录是否存在
     * 
     * @param param
     * @return
     * @throws Exception
     * @author chenhh6
     */
    public static IDataset qryBBossDepartCode(IData param) throws Exception
    {
        String departCode = param.getString("DEPART_CODE", "");
        return BBossAttrQry.qryBBossDepartCode(departCode);
    }
}
