
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;

public class QueryVpmnShortCodeExportTask extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData inParam, Pagination pg) throws Exception
    {
        String qType = inParam.getString("cond_QueryType", "");
        String serialNumberA = inParam.getString("cond_SERIAL_NUMBER_A", "");
        String serialNumber = inParam.getString("cond_SERIAL_NUMBER", "");
        String MserialNumberA = inParam.getString("cond_MSERIAL_NUMBER_A");

        String shortType = inParam.getString("cond_ShortType");
        String contain_4 = inParam.getString("cond_Contain_4").equals("3") ? "" : inParam.getString("cond_Contain_4");
        String contain_7 = inParam.getString("cond_Contain_7").equals("3") ? "" : inParam.getString("cond_Contain_7");
        String shortlength = inParam.getString("cond_SHORT_LENGTH", "");
        IDataset shortset = new DatasetList();
        String vpn_name = "";

        IData MotherID = new DataMap();
        if ("0".equals(qType))
        {// 按VPMN编码查询
            // 根据集团编号获取集团用户信息
            IData param = new DataMap();
            param.put("SERIAL_NUMBER", serialNumberA);
            IData userinfo = UcaInfoQry.qryUserMainProdInfoBySnForGrp(serialNumberA);
            if (IDataUtil.isNotEmpty(userinfo))
            {
                String IDA = userinfo.getString("USER_ID");
                MotherID = GetMotherIDAByIDB(IDA); // 通过子集团ID获取母集团ID
            }
            else
            {
                return shortset;
            }
            vpn_name = GetName(userinfo.getString("USER_ID"));
            if (MotherID.size() > 0)
            {
                shortset = GetDataByMother(shortType, shortlength, contain_4, contain_7, MotherID.getString("USER_ID_A"), MotherID.getString("SERIAL_NUMBER_A"), pg);
            }
            else
            {
                shortset = display(serialNumberA, shortType, shortlength, contain_4, contain_7, vpn_name, userinfo.getString("USER_ID"), pg);// 前台需要显示的数据
            }
        }
        else if ("1".equals(qType))
        {// 按手机号码查询
            String serial_number_a = "";
            String routeId = RouteInfoQry.getEparchyCodeBySnForCrm(serialNumber);
            IData userinfo = UcaInfoQry.qryUserInfoBySn(serialNumber, routeId);
            if (IDataUtil.isNotEmpty(userinfo))
            {
                String memUserId = userinfo.getString("USER_ID", "");
                // 获取用户与用户关系信息
                IData userrelationparam = new DataMap();
                userrelationparam.put("USER_ID_B", memUserId);
                userrelationparam.put("RELATION_TYPE_CODE", "20");

                IDataset userrelations = RelaUUInfoQry.getRelaUUInfoByUserIdBAndRelaTypeCode(memUserId, "20", routeId);
                if (IDataUtil.isEmpty(userrelations))
                {
                    return shortset;
                }
                // 20的不止8000的vpmn产品，所以需要再判断start
                IDataset ds8000 = getGrpUserInfosByProductId(userrelations, "8000");
                if (IDataUtil.isEmpty(ds8000))
                {
                    return shortset;
                }
                // 20的不止8000的vpmn产品，所以需要再判断end
                IData uuinfo = (IData) ds8000.getData(0);
                serial_number_a = uuinfo.getString("SERIAL_NUMBER_A", "");
                String grpUserId = uuinfo.getString("USER_ID_A");
                MotherID = GetMotherIDAByIDB(grpUserId);

                if (MotherID.size() > 0)
                {// m;
                    shortset = GetDataByMother(shortType, shortlength, contain_4, contain_7, MotherID.getString("USER_ID_A"), MotherID.getString("SERIAL_NUMBER_A"), pg);
                }
                else
                {
                    vpn_name = GetName(grpUserId);
                    shortset = display(serial_number_a, shortType, shortlength, contain_4, contain_7, vpn_name, grpUserId, pg);// 前台需要显示的数据
                }

            }
        }
        else if ("3".equals(qType))
        {// 按母VPMN编码查询
            // 获取用户信息
            IData Userinfo = UcaInfoQry.qryUserMainProdInfoBySnForGrp(MserialNumberA);
            if (IDataUtil.isNotEmpty(Userinfo))
            {
                shortset = GetDataByMother(shortType, shortlength, contain_4, contain_7, Userinfo.getString("USER_ID"), MserialNumberA, pg);
            }
        }

        return shortset;
    }

    /**
     * @Description:通过子集团ID获取母集团ID
     * @author zhouhua
     * @date 2012-10-08
     * @param
     * @throws Exception
     */
    private IData GetMotherIDAByIDB(String IDA) throws Exception
    {
        // 获取母集团ID
        IData reinfo = new DataMap();
        IDataset GroupVpmnInfos = RelaUUInfoQry.getGrpRelaUUInfoByUserIdBAndRelaTypeCode(IDA, "40");
        if (IDataUtil.isEmpty(GroupVpmnInfos))
        {
            return reinfo;
        }
        IData groupVpmn = (IData) GroupVpmnInfos.get(0);
        reinfo.put("USER_ID_A", groupVpmn.getString("USER_ID_A", ""));
        reinfo.put("SERIAL_NUMBER_A", groupVpmn.getString("SERIAL_NUMBER_A", ""));
        return reinfo;

    }

    /**
     * 获取VPNNAME
     * 
     * @param USER_ID
     * @return
     * @throws Exception
     */
    private String GetName(String userId) throws Exception
    {
        IDataset GroupVpmnInfo = UserVpnInfoQry.qryUserVpnByUserId(userId);
        String name = "";
        if (IDataUtil.isNotEmpty(GroupVpmnInfo))
        {
            name = GroupVpmnInfo.getData(0).getString("VPN_NAME");
        }
        return name;
    }

    /**
     * @Description:通过母vpmn取该母下的所有短号信息
     * @author zhouhua
     * @date 2012-10-08
     * @param
     * @throws Exception
     */
    private IDataset GetDataByMother(String shortType, String shortlength, String contain_4, String contain_7, String MotherID, String MSNA, Pagination pg) throws Exception
    {
        // 按母VPMN编码查询 MotherID
        IData inparam = new DataMap();
        String name = GetName(MotherID);
        inparam.put("USER_ID_A", MotherID);
        inparam.put("SHORT_TYPE", shortType);
        inparam.put("SHORT_LENGTH", shortlength);
        inparam.put("CONTAIN_4", contain_4);
        inparam.put("CONTAIN_7", contain_7);
        inparam.put("VPN_NAME", name);
        inparam.put("SERIAL_NUMBER_M", MSNA);
        inparam.put("tag", "0");

        IDataset shortset = RelaUUInfoQry.getMotherVpmnShortCodeExport(inparam, pg);
        return shortset;
    }

    /**
     * @Description:显示普通vpmn表单
     * @author zhouhua
     * @date 2012-10-08
     * @param
     * @throws Exception
     */
    private IDataset display(String serialNumberA, String shortType, String shortlength, String contain_4, String contain_7, String vpn_name, String user_id, Pagination pg) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("SERIAL_NUMBER_A", serialNumberA);
        inparams.put("SHORT_TYPE", shortType);
        inparams.put("SHORT_LENGTH", shortlength);
        inparams.put("CONTAIN_4", contain_4);
        inparams.put("CONTAIN_7", contain_7);
        inparams.put("VPN_NAME", vpn_name);
        inparams.put("SERIAL_NUMBER_M", serialNumberA);
        inparams.put("tag", "0");
        inparams.put("USER_ID_A", user_id);

        IDataset shortset = RelaUUInfoQry.getVpmnShortCodeExport(inparams, pg);
        return shortset;

    }

    /**
     * 从一个集团用户信息集里查出指定productId的集合
     * 
     * @param dataset
     * @param productId
     * @return
     * @throws Exception
     */

    public IDataset getGrpUserInfosByProductId(IDataset source, String productId) throws Exception
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
