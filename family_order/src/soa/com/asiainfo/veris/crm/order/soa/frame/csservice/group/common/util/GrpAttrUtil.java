
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util;

import java.util.Iterator;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;

public class GrpAttrUtil
{

    public static IDataset compareData(IData newDs, IData oldDs, String str)
    {

        IDataset Infos = new DatasetList();
        if (IDataUtil.isNotEmpty(oldDs))
        {
            String[] ss = str.split(",");
            int num = ss.length;
            boolean tag = false;
            for (int i = 0; i < num; i++)
            {
                if (IDataUtil.isEmpty(newDs))
                {
                    tag = true;
                    break;
                }
                else if (!newDs.getString(ss[i], "").equals(oldDs.getString(ss[i], "").replace(" ", "")))
                {
                    tag = true;
                    break;
                }
            }
            if (tag)
            {
                oldDs.put("MODIFY_TAG", "1");
                Infos.add(oldDs);
                if (IDataUtil.isNotEmpty(newDs))
                {
                    newDs.put("MODIFY_TAG", "0");
                    Infos.add(newDs);
                }

            }
        }
        else
        {
            if (IDataUtil.isNotEmpty(newDs))
            {
                newDs.put("MODIFY_TAG", "0");
                Infos.add(newDs);
            }
        }
        return Infos;
    }

    /**
     * 处理产品、服务和资费的参数信息
     * 
     * @param tradeParamData
     * @param userId
     * @param attrType
     * @param relaInstId
     * @param inData
     * @return
     * @throws Exception
     */
    public static IDataset dealAttrParam(IData tradeParamData, String userId, String attrType, String relaInstId, IData inData) throws Exception
    {
        // 返回数据
        IDataset retParamList = new DatasetList();

        String modifyTag = inData.getString("MODIFY_TAG");
        String startDate = inData.getString("START_DATE");
        String endDate = inData.getString("END_DATE");

        // 获取用户参数列表
        IDataset userParamList = UserAttrInfoQry.getUserAttrByUserIdInstid(userId, attrType, relaInstId);

        // 根据参数标志处理
        if (TRADE_MODIFY_TAG.DEL.getValue().equals(modifyTag))
        {
            for (int i = 0, iSize = userParamList.size(); i < iSize; i++)
            {
                IData userParam = userParamList.getData(i);
                userParam.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                userParam.put("END_DATE", endDate);
            }
            retParamList = userParamList;
        }
        else
        {
            Iterator<String> iterator = tradeParamData.keySet().iterator();

            while (iterator.hasNext())
            {
                String tradeKey = iterator.next();
                String tradeValue = tradeParamData.getString(tradeKey);

                boolean isExist = false;

                IData map = new DataMap();
                for (int i = 0, iSize = userParamList.size(); i < iSize; i++)
                {
                    IData userParamData = userParamList.getData(i);
                    String attrCode = userParamData.getString("ATTR_CODE", "");
                    String attrValue = userParamData.getString("ATTR_VALUE", "");

                    if (attrCode.equals(tradeKey))// tradeKey相等,值不同
                    {
                        // 处理传空情况
                        if ("".equals(attrValue) && "".equals(tradeValue))
                        {
                            isExist = true;
                            break;
                        }

                        if (!attrValue.equals(tradeValue))// 如果值一样没有必要再拼数据
                        {
                            if (tradeKey != null && tradeKey.length() > 3 && tradeKey.substring(0, 3).equals("FEE"))
                            {
                                if ("".equals(tradeValue))
                                {
                                    tradeValue = "0";
                                }
                                else
                                {
                                    tradeValue = String.valueOf(100 * Integer.parseInt(tradeValue));
                                }
                            }
                            map.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                            map.put("INST_TYPE", attrType);
                            map.put("INST_ID", userParamData.getString("INST_ID"));
                            map.put("RELA_INST_ID", relaInstId);
                            map.put("ATTR_CODE", tradeKey);
                            map.put("ATTR_VALUE", tradeValue);
                            map.put("START_DATE", userParamData.getString("START_DATE"));
                            map.put("END_DATE", userParamData.getString("END_DATE"));
                            map.put("USER_ID", userId);
                            map.put("ELEMENT_ID", inData.getString("ELEMENT_ID"));

                            retParamList.add(map);
                        }

                        isExist = true;
                        break;
                    }
                }
                if (!isExist)
                {
                    if (tradeKey != null && tradeKey.length() > 3 && tradeKey.substring(0, 3).equals("FEE"))
                    {
                        if ("".equals(tradeValue))
                        {
                            tradeValue = "0";
                        }
                        else
                        {
                            tradeValue = String.valueOf(100 * Integer.parseInt(tradeValue));
                        }
                    }
                    map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                    map.put("INST_TYPE", attrType);
                    map.put("RELA_INST_ID", relaInstId);
                    map.put("INST_ID", SeqMgr.getInstId());
                    map.put("ATTR_CODE", tradeKey);
                    map.put("ATTR_VALUE", tradeValue);
                    map.put("START_DATE", startDate);
                    map.put("END_DATE", endDate);
                    map.put("USER_ID", userId);
                    map.put("ELEMENT_ID", inData.getString("ELEMENT_ID"));
                    retParamList.add(map);
                }
            }
        }

        return retParamList;
    }

    /**
     * 海南特殊处理资费的参数信息
     * 
     * @param tradeParamData
     * @param userId
     * @param attrType
     * @param relaInstId
     * @param inData
     * @return
     * @throws Exception
     */
    public static IDataset dealDiscntAttrParam(IData tradeParamData, String userId, String attrType, String relaInstId, IData inData, String acceptTime) throws Exception
    {
        // 返回数据
        IDataset retParamList = new DatasetList();

        String modifyTag = inData.getString("MODIFY_TAG");
        String startDate = inData.getString("START_DATE");
        String endDate = inData.getString("END_DATE");

        // 获取用户参数列表
        IDataset userParamList = UserAttrInfoQry.getUserAttrByUserIdInstid(userId, attrType, relaInstId);

        // 根据参数标志处理
        if (TRADE_MODIFY_TAG.DEL.getValue().equals(modifyTag))
        {
            for (int i = 0, iSize = userParamList.size(); i < iSize; i++)
            {
                IData userParam = userParamList.getData(i);
                userParam.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                userParam.put("END_DATE", endDate);
            }
            retParamList = userParamList;
        }
        else
        {
            Iterator<String> iterator = tradeParamData.keySet().iterator();

            while (iterator.hasNext())
            {
                String tradeKey = iterator.next();
                String tradeValue = tradeParamData.getString(tradeKey);

                boolean isExist = false;

                IData map = new DataMap();
                for (int i = 0, iSize = userParamList.size(); i < iSize; i++)
                {
                    IData userParamData = userParamList.getData(i);
                    String attrCode = userParamData.getString("ATTR_CODE", "");
                    String attrValue = userParamData.getString("ATTR_VALUE", "");

                    if (attrCode.equals(tradeKey))// tradeKey相等,值不同
                    {
                        // 处理传空情况
                        if ("".equals(attrValue) && "".equals(tradeValue))
                        {
                            isExist = true;
                            break;
                        }

                        if (!attrValue.equals(tradeValue))// 如果值一样没有必要再拼数据
                        {
                            // 修改资费属性 海南是删除一条在新增一条
                            // 获取失效属性的时间
                            String strCancelDate = ElementUtil.getCancelDate_chgUserElem(inData, acceptTime, startDate, endDate);

                            userParamData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                            userParamData.put("END_DATE", strCancelDate);

                            retParamList.add(userParamData);// 删除旧数据

                            if (tradeKey != null && tradeKey.length() > 3 && tradeKey.substring(0, 3).equals("FEE"))
                            {
                                if ("".equals(tradeValue))
                                {
                                    tradeValue = "0";
                                }
                                else
                                {
                                    tradeValue = String.valueOf(100 * Integer.parseInt(tradeValue));
                                }
                            }

                            // 获取失效时间下一秒
                            String newStartdate = SysDateMgr.getNextSecond(strCancelDate);
                            // 新增一条新的
                            map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                            map.put("INST_TYPE", attrType);
                            map.put("RELA_INST_ID", relaInstId);
                            map.put("INST_ID", SeqMgr.getInstId());
                            map.put("ATTR_CODE", tradeKey);
                            map.put("ATTR_VALUE", tradeValue);
                            map.put("START_DATE", newStartdate);
                            map.put("END_DATE", endDate);
                            map.put("USER_ID", userId);
                            map.put("ELEMENT_ID", inData.getString("ELEMENT_ID"));

                            retParamList.add(map);
                        }

                        isExist = true;
                        break;
                    }
                }
                if (!isExist)
                {
                    if (tradeKey != null && tradeKey.length() > 3 && tradeKey.substring(0, 3).equals("FEE"))
                    {
                        if ("".equals(tradeValue))
                        {
                            tradeValue = "0";
                        }
                        else
                        {
                            tradeValue = String.valueOf(100 * Integer.parseInt(tradeValue));
                        }
                    }
                    map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                    map.put("INST_TYPE", attrType);
                    map.put("RELA_INST_ID", relaInstId);
                    map.put("INST_ID", SeqMgr.getInstId());
                    map.put("ATTR_CODE", tradeKey);
                    map.put("ATTR_VALUE", tradeValue);
                    map.put("START_DATE", startDate);
                    map.put("END_DATE", endDate);
                    map.put("USER_ID", userId);
                    map.put("ELEMENT_ID", inData.getString("ELEMENT_ID"));
                    retParamList.add(map);
                }
            }
        }

        return retParamList;
    }

    /**
     * 把attr属性List转化为Map
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public static IData transAttrList2Map(IDataset paramDataset) throws Exception
    {
        IData map = new DataMap();

        for (int i = 0, iSize = paramDataset.size(); i < iSize; i++)
        {
            IData paramData = paramDataset.getData(i);
            if (paramData.containsKey("ATTR_CODE"))
            {
                String attrCode = paramData.getString("ATTR_CODE");
                String attrValue = paramData.getString("ATTR_VALUE", "");
                map.put(attrCode, attrValue);
            }
        }
        return map;
    }
}
