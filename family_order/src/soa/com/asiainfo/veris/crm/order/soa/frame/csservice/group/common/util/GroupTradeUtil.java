
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.object.IColumnObject;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.table.TableHelp;

public final class GroupTradeUtil
{

    public static void addMap2RegData(IDataset idsReg, IData map, String tableName) throws Exception
    {
        // 得到表字段
        IColumnObject[] tableColumns = TableHelp.getTableColunms(tableName,Route.getJourDb(BizRoute.getRouteId()));

        // 初始化
        IColumnObject column;
        String fieldName = "";
        Boolean bNullable = false;
        String fieldValue = "";

        IData data = new DataMap();

        // 遍历所有字段依次加入
        for (IColumnObject tableColumn : tableColumns)
        {
            // 字段
            column = tableColumn;

            // 字段名
            fieldName = column.getColumnName();

            if ("ROWID".equals(fieldName))
            {
                continue;
            }
 
            //增加对SERVICE_ID为空的判断
            if (StringUtils.isEmpty(map.getString("SERVICE_ID"))&& "915001".equals(map.getString("PRODUCT_ID")))
            { 
            	 map.put("SERVICE_ID", "915001");  
            } 
            if(StringUtils.isEmpty(map.getString("USER_ID"))&& "915001".equals(map.getString("PRODUCT_ID"))){  
            	return;
           }

            // 字段值
            fieldValue = map.getString(fieldName, "");

            // 字段是否为空
            bNullable = column.isNullable();

            // 如果不允许为空,则提示异常
            if (bNullable == false && StringUtils.isBlank(fieldValue))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_13, tableName, fieldName+"字段不能为空！");
            }

            // 设置值
            data.put(fieldName, fieldValue);
        }

        if(tableName.equals(TradeTableEnum.TRADE_DISCNT.getValue()) || tableName.equals(TradeTableEnum.TRADE_SVC.getValue())  || tableName.equals(TradeTableEnum.TRADE_PLATSVC.getValue()))
        {
            data.put("PRODUCT_ID", map.getString("PRODUCT_ID"));
            data.put("PACKAGE_ID", map.getString("PACKAGE_ID"));
        }
        idsReg.add(data);
    }

}
