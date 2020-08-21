
package com.asiainfo.veris.crm.order.soa.group.groupintf.querytrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;

public class GrpRelaUUInfoQryIntf
{
    /**
     * 集团成员关系查询 通过用户A的用户标识和用户B的服务号码及关系类型获取用户与用户的关系
     * 
     * @param data
     * @return
     * @throws Exception
     */

    public static IDataset getRelaInfoBySnaSnbRelatypecode(IData data) throws Exception
    {
        String serialNumberA = IDataUtil.getMandaData(data, "SERIAL_NUMBER_A");
        String serialNumberB = IDataUtil.getMandaData(data, "SERIAL_NUMBER_B");
        String relationTypeCode = IDataUtil.getMandaData(data, "RELATION_TYPE_CODE");
        // 判断成员号码
        UcaData memUcaData = new UcaData();
        IData uca = UCAQryIntf.getMemberUCAAndStateBySerialNumber(serialNumberB);
        memUcaData = (UcaData) uca.get("UCADATA");
        String routeId = memUcaData.getUserEparchyCode();
        IDataset uuInfo = RelaUUInfoQry.getMemberUserRelation(serialNumberA, serialNumberB, relationTypeCode, routeId);

        return uuInfo;
    }

    /**
     * 长短号互查
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getRelaAndVpnInfoBySnOrShortcode(IData data) throws Exception
    {
        String paraCode1 = IDataUtil.getMandaData(data, "PARA_CODE1");
        String paraCode2 = IDataUtil.getMandaData(data, "PARA_CODE2");
        String paraCode3 = data.getString("PARA_CODE3", null);
        IData result = new DataMap();
        IDataset infos = new DatasetList();
        if ("0".equals(paraCode1)) // 成员长号
        {
            String mebSn = paraCode2;
            UcaData memUcaData = new UcaData();
            IData uca = UCAQryIntf.getMemberUCAAndStateBySerialNumber(mebSn);
            memUcaData = (UcaData) uca.get("UCADATA");
            String usrIdB = memUcaData.getUserId();
            String memEparchCode = memUcaData.getUserEparchyCode();
            //
            IDataset uuInfos = RelaUUInfoQry.qryByRelaUserIdBRouteId(usrIdB, "20", memEparchCode);
            if (IDataUtil.isNotEmpty(uuInfos))
            {
                // 20的不止8000的vpmn产品，所以需要再判断
                IDataset ds8000 = getGrpUserInfosByProductId(uuInfos, "8000");
                if (IDataUtil.isNotEmpty(ds8000))
                {
                    IData uuInfo = ds8000.getData(0);
                    result.put("SERIAL_NUMBER", uuInfo.getString("SERIAL_NUMBER_B"));
                    result.put("SHORT_CODE", uuInfo.getString("SHORT_CODE"));
                    result.put("START_DATE", uuInfo.getString("START_DATE"));
                    result.put("END_DATE", uuInfo.getString("END_DATE"));
                    result.put("PARA_CODE3", uuInfo.getString("SERIAL_NUMBER_B"));
                    result.put("PARA_CODE4", uuInfo.getString("SHORT_CODE"));
                    result.put("PARA_CODE5", uuInfo.getString("START_DATE"));
                    result.put("PARA_CODE6", uuInfo.getString("END_DATE"));
                    String grpUserId = uuInfo.getString("USER_ID_A");
                    IDataset vpnInfos = UserVpnInfoQry.qryUserVpnByUserId(grpUserId);
                    if (IDataUtil.isNotEmpty(vpnInfos))
                    {
                        IData vpnInfo = vpnInfos.getData(0);
                        result.put("PARA_CODE1", vpnInfo.getString("VPN_NO"));
                        result.put("PARA_CODE2", vpnInfo.getString("VPN_NAME"));
                    }
                    infos.add(result);
                }
            }
        }
        else if ("1".equals(paraCode1)) // vpn_no和短号
        {
            String vpnNo = paraCode2;
            String shortCode = paraCode3;
            IDataset vpnInfos = UserVpnInfoQry.queryVpnInfoByVpnNo(vpnNo);
            if (IDataUtil.isNotEmpty(vpnInfos))
            {
                IData vpnInfo = vpnInfos.getData(0);
                String grpUserId = vpnInfo.getString("USER_ID");
                IDataset uuInfos = RelaUUInfoQry.qryGrpRelaUUByUserIdA(grpUserId, "20");
                if (IDataUtil.isNotEmpty(uuInfos))
                {
                    IDataset shortCodeInfos = DataHelper.filter(uuInfos, "SHORT_CODE=" + shortCode);
                    if (IDataUtil.isNotEmpty(shortCodeInfos))
                    {
                        IData uuInfo = shortCodeInfos.getData(0);
                        result.put("SERIAL_NUMBER", uuInfo.getString("SERIAL_NUMBER_B"));
                        result.put("SHORT_CODE", uuInfo.getString("SHORT_CODE"));
                        result.put("START_DATE", uuInfo.getString("START_DATE"));
                        result.put("END_DATE", uuInfo.getString("END_DATE"));
                        result.put("PARA_CODE3", uuInfo.getString("SERIAL_NUMBER_B"));
                        result.put("PARA_CODE4", uuInfo.getString("SHORT_CODE"));
                        result.put("PARA_CODE5", uuInfo.getString("START_DATE"));
                        result.put("PARA_CODE6", uuInfo.getString("END_DATE"));
                        result.put("PARA_CODE1", vpnInfo.getString("VPN_NO"));
                        result.put("PARA_CODE2", vpnInfo.getString("VPN_NAME"));
                        infos.add(result);
                    }
                }
            }

        }
        else if ("2".equals(paraCode1)) // vpn_name和短号
        {
            String vpnName = paraCode2;
            String shortCode = paraCode3;
            IDataset vpnInfos = UserVpnInfoQry.qryUserVpnByVpnName(vpnName);
            if (IDataUtil.isNotEmpty(vpnInfos))
            {
                for (int i = 0; i < vpnInfos.size(); i++)
                {
                    IData vpnInfo = vpnInfos.getData(i);
                    String grpUserId = vpnInfo.getString("USER_ID");
                    IDataset uuInfos = RelaUUInfoQry.qryGrpRelaUUByUserIdA(grpUserId, "20");
                    if (IDataUtil.isNotEmpty(uuInfos))
                    {
                        IDataset shortCodeInfos = DataHelper.filter(uuInfos, "SHORT_CODE=" + shortCode);
                        if (IDataUtil.isNotEmpty(shortCodeInfos))
                        {
                            IData uuInfo = shortCodeInfos.getData(0);
                            IData uudata = new DataMap();
                            uudata.put("SERIAL_NUMBER", uuInfo.getString("SERIAL_NUMBER_B"));
                            uudata.put("SHORT_CODE", uuInfo.getString("SHORT_CODE"));
                            uudata.put("START_DATE", uuInfo.getString("START_DATE"));
                            uudata.put("END_DATE", uuInfo.getString("END_DATE"));
                            uudata.put("PARA_CODE3", uuInfo.getString("SERIAL_NUMBER_B"));
                            uudata.put("PARA_CODE4", uuInfo.getString("SHORT_CODE"));
                            uudata.put("PARA_CODE5", uuInfo.getString("START_DATE"));
                            uudata.put("PARA_CODE6", uuInfo.getString("END_DATE"));
                            uudata.put("PARA_CODE1", vpnInfo.getString("VPN_NO"));
                            uudata.put("PARA_CODE2", vpnInfo.getString("VPN_NAME"));
                            infos.add(uudata);
                        }
                    }
                }
            }

        }
        return infos;
    }

    /**
     * 从一个集团用户信息集里查出指定productId的集合
     * 
     * @param dataset
     * @param productId
     * @return
     * @throws Exception
     */

    public static IDataset getGrpUserInfosByProductId(IDataset source, String productId) throws Exception
    {
        IDataset ds = new DatasetList();
        for (int i = 0, cout = source.size(); i < cout; i++)
        {
            IData map = source.getData(i);
            String grpUserId = map.getString("USER_ID_A");
            IData userInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(grpUserId);
            if (IDataUtil.isNotEmpty(userInfo))
            {
                if (productId.equals(userInfo.getString("PRODUCT_ID")))
                {
                    ds.add(map);
                }
            }
        }
        return ds;
    }
}
