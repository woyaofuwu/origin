
package com.asiainfo.veris.crm.order.soa.person.busi.wap.plat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.wap.BaseServiceForWap;
import com.asiainfo.veris.crm.order.soa.person.busi.wap.WapUtil;

public class QueryPlatSvcForWap extends BaseServiceForWap
{
    private boolean hasWlan = false;

    @Override
    protected void checkChildParams(IData param) throws Exception
    {
        // TODO Auto-generated method stub

    }

    @Override
    public IDataset handleBiz(IData param) throws Exception
    {
        IDataset resultList = new DatasetList();
        IData result = new DataMap();
        // 已经订购的平台服务
        result.put("ENJOYINFO", this.queryUserPlatSvc(param));
        // 查询用户可办理XXX业务列表
        result.put("UNENJOYINFO", this.queryUserCanOrderPlatSvc(param));

        if (this.hasWlan)
        {
            result.put("SUPERIMPOSE", "1");// 0:可叠加；1:不支持叠加。Wlan套餐专有
            hasWlan = false;
        }
        resultList.add(result);

        return resultList;
    }

    private IDataset queryUserCanOrderPlatSvc(IData data) throws Exception
    {
        IData param = new DataMap();
        IDataset result = new DatasetList();

        String bizType = data.getString("BIZTYPE");// 业务类型

        IDataset dataset = PlatSvcInfoQry.queryPlatSvcByUserIdForWap(uca.getUserId(), "CSM", "2690", bizType);
        if ((dataset != null) && (dataset.size() > 0))
        {
            int size = dataset.size();
            for (int i = 0; i < size; i++)
            {
                String spCode = dataset.getData(i).getString("SP_CODE");
                String bizCode = dataset.getData(i).getString("BIZ_CODE");
                String bizTypeCode = dataset.getData(i).getString("BIZ_TYPE_CODE");
                String platSvcDesc = dataset.getData(i).getString("ELEMENT_DESC");
                String productName = dataset.getData(i).getString("PARA_CODE2");
                String serviceId = dataset.getData(i).getString("PARA_CODE1");

                IData dataTmp = new DataMap();
                // 可办理产品的编码类型 01:产品编码；02:企业代码+业务代码
                dataTmp.put("ENCODTYPE", "02");
                dataTmp.put("PRODUNCTID", "");
                dataTmp.put("PRODUNCTNAME", productName);
                // 企业代码
                dataTmp.put("SPID", spCode);

                // 业务代码
                // 注册类平台服务的sp_code和biz_code均为REG_SP，不能唯一区分，统一通过biz_type_code区分，
                // 并且接口中BIZCODE字段设置为biz_type_code的值，其它非注册类仍然使用biz_code的值。
                if ("WLAN".equals(bizType))
                {
                    dataTmp.put("BIZCODE", bizTypeCode);
                }
                else
                {
                    // 可能存在浏览器中的特殊字符，预先转义。
                    dataTmp.put("BIZCODE", WapUtil.convertStr(bizCode, 1));
                }

                // 产品描述
                dataTmp.put("PRODUNCTDESC", platSvcDesc);

                // 是否支持叠加 0:可叠加；1:不支持叠加。Wlan套餐专有。
                if ("WLAN".equals(bizType))
                {
                    hasWlan = true;

                    // 高校WLAN有套餐选择，特殊处理

                    if ("92".equals(bizTypeCode))
                    {
                        param.clear();
                        param.put("SERVICE_ID", serviceId);
                        IDataset dataset2 = UserPlatSvcInfoQry.queryUserAttrByUserIdForWap(uca.getUserId(), "401", serviceId);
                        if ((dataset2 != null) && (dataset2.size() > 0))
                        {
                            for (int j = 0; j < dataset2.size(); j++)
                            {
                                IData dataTmp2 = new DataMap();
                                // 可办理产品的编码类型 01:产品编码；02:企业代码+业务代码
                                dataTmp2.put("ENCODTYPE", "02");

                                dataTmp2.put("PRODUNCTID", dataset2.getData(j).getString("PARA_CODE1", ""));// 这里放置优惠ID
                                dataTmp2.put("PRODUNCTNAME", dataset2.getData(j).getString("PARA_CODE2", ""));

                                // 企业代码
                                dataTmp2.put("SPID", spCode);

                                dataTmp2.put("BIZCODE", bizTypeCode);

                                dataTmp2.put("PRODUNCTDESC", dataset2.getData(j).getString("ELEMENT_DESC", ""));

                                result.add(dataTmp2);
                            }

                        }

                        continue;
                    }
                }

                result.add(dataTmp);
            }
        }

        return result;
    }

    /**
     * 查询用户已有的平台服务
     * 
     * @param data
     * @return
     * @throws Exception
     */
    private IDataset queryUserPlatSvc(IData data) throws Exception
    {
        IData param = new DataMap();
        IDataset result = new DatasetList();

        String bizType = data.getString("BIZTYPE");// 业务类型

        IDataset dataset = UserPlatSvcInfoQry.queryUserPlatSvcByUserIdForWap(this.uca.getUserId(), bizType);

        if ((dataset != null) && (dataset.size() > 0))
        {
            int size = dataset.size();
            for (int i = 0; i < size; i++)
            {
                String spCode = dataset.getData(i).getString("SP_CODE");
                String bizCode = dataset.getData(i).getString("BIZ_CODE");
                String bizTypeCode = dataset.getData(i).getString("BIZ_TYPE_CODE");
                String startDate = dataset.getData(i).getString("START_DATE");
                String endDate = dataset.getData(i).getString("END_DATE");
                String productName = dataset.getData(i).getString("PARA_CODE2");
                String serviceId = dataset.getData(i).getString("PARA_CODE1");

                IData userPlatSvc = new DataMap();
                // 可办理产品的编码类型 01:产品编码；02:企业代码+业务代码
                userPlatSvc.put("ENCODTYPE", "02");
                userPlatSvc.put("PRODUNCTID", "");
                userPlatSvc.put("PRODUNCTNAME", productName);
                // 企业代码
                userPlatSvc.put("SPID", spCode);

                // 业务代码
                // 注册类平台服务的sp_code和biz_code均为REG_SP，不能唯一区分，统一通过biz_type_code区分，
                // 并且接口中BIZCODE字段设置为biz_type_code的值，其它非注册类仍然使用biz_code的值。
                if ("WLAN".equals(bizType))
                {
                    userPlatSvc.put("BIZCODE", bizTypeCode);

                    // 高校WLAN有套餐选择，特殊处理

                    if ("92".equals(bizTypeCode))
                    {
                        param.clear();
                        param.put("USER_ID", uca.getUserId());
                        param.put("SERVICE_ID", serviceId);
                        IDataset dataset2 = UserPlatSvcInfoQry.queryUserAttrByUserIdForWap(uca.getUserId(), "401", serviceId);
                        if ((dataset2 != null) && (dataset2.size() > 0))
                        {
                            userPlatSvc.put("PRODUNCTID", dataset2.getData(0).getString("PARA_CODE1", ""));// 这里放置优惠ID
                            userPlatSvc.put("PRODUNCTNAME", dataset2.getData(0).getString("PARA_CODE2", ""));
                        }

                    }
                }
                else
                {
                    // 可能存在浏览器中的特殊字符，预先转义。
                    userPlatSvc.put("BIZCODE", WapUtil.convertStr(bizCode, 1));
                }
                // 开始时间 格式为：YYYYMMDDHH24MMSS
                userPlatSvc.put("OPRBEGTIME", startDate);
                // 失效时间 格式为：YYYYMMDDHH24MMSS
                userPlatSvc.put("OPRENDTIME", endDate);

                result.add(userPlatSvc);
            }
        }

        return result;
    }

}
