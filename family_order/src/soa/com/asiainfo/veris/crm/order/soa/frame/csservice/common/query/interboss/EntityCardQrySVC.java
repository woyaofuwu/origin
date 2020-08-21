
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.interboss;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class EntityCardQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset qryEntityCard(IData input) throws Exception
    {
        EntityInfoQry bean = new EntityInfoQry();
        String entityCardStatus = "";
        String entityCardRoamFlag = "";
        IDataset entityDataset = new DatasetList();
        entityDataset = bean.QueryEntityCard(input);
        IData entityData = new DataMap();

        if (entityDataset != null && entityDataset.size() > 0)
        {
            entityData = (IData) entityDataset.get(0);
            if ("0".equals(entityData.getString("X_RSPTYPE")) && "0000".equals(entityData.getString("X_RSPCODE")))
            {
                if ("00".equals(entityData.getString("RSLT")))
                {
                    if ("00".equals(entityData.getString("CARDSTATUS")))
                        entityCardStatus = "初始";
                    if ("01".equals(entityData.getString("CARDSTATUS")))
                        entityCardStatus = "入库";
                    if ("02".equals(entityData.getString("CARDSTATUS")))
                        entityCardStatus = "激活";
                    if ("03".equals(entityData.getString("CARDSTATUS")))
                        entityCardStatus = "失效";
                    if ("04".equals(entityData.getString("CARDSTATUS")))
                        entityCardStatus = "绑定";
                    if ("05".equals(entityData.getString("CARDSTATUS")))
                        entityCardStatus = "锁定";
                    if ("0".equals(entityData.getString("ROAMFLAG")))
                        entityCardRoamFlag = "可漫游";
                    if ("1".equals(entityData.getString("ROAMFLAG")))
                        entityCardRoamFlag = "不可漫游";
                    entityData.put("ENTITY_CARDSTATUS", entityCardStatus);
                    entityData.put("ENTITY_ROAMFLAG", entityCardRoamFlag);
                    // setHintInfo("查询数据成功！");
                }
                else if ("01".equals(entityData.getString("RSLT")))
                {
                    Utility.error("未查询到相关的数据！");
                }
                else if ("02".equals(entityData.getString("RSLT")))
                {
                    Utility.error("该卡为过期的卡！");
                }
            }
            else
            {
                Utility.error("未查询到相关的数据！");
            }
        }
        else
        {
            Utility.error("未查询到相关的数据！");
        }
        return entityDataset;
        // IDataset set1 = new DatasetList();
        // IData d1 = new DataMap();
        // IData d2 = new DataMap();
        // d1.put("aa", "11111111111111");
        // d2.put("bb", "1");
        // d1.put("CARD_NO", "卡号");
        // d1.put("ENTITY_CARDSTATUS", "卡类型");
        // d1.put("X_FACTORY_NAME", "ENTITY_CARDSTATUS");
        // d1.put("HOMEPROV", "HOMEPROV");
        // d1.put("ENTITY_ROAMFLAG", "ENTITY_ROAMFLAG");
        // d1.put("BINDINGNUM", "BINDINGNUM");
        // d1.put("EFFETITIME", "EFFETITIME");
        // d1.put("ABATETIME", "ABATETIME");
        // d1.put("X_STAFF_NAME_IN", "入库员工");
        // d1.put("SALE_TIME", "销售时间");
        // d1.put("X_SALE_STAFF_NAME", "销售员工");
        // d1.put("SALE_MONEY", "销售金额（元）");
        // d2.put("cardType", "卡类型");
        // d2.put("startCardNo", "开始卡号");
        // d2.put("endCardNo", "结束卡号");
        // d2.put("simPrice", "卡面值（元）");
        // d2.put("singlePrice", "单价（元）");
        // d2.put("totalPrice", "总价（元）");
        // d2.put("rowCount", "数量");
        // d2.put("valueCode", "valueCode");
        // d2.put("parvalue", "parvalue");
        // d2.put("activateInfo", "activateInfo");
        // d2.put("devicePrice", "devicePrice");
        // IDataset set2 = new DatasetList();
        // set1.add(d1);
        // set2.add(d2);
        // return set1;
    }
}
