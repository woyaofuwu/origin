
package com.asiainfo.veris.crm.order.web.group.querygroupinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class QueryVpmnShortCode extends GroupBasePage
{

    /**
     * @Description:显示普通vpmn表单
     * @author zhouhua
     * @date 2012-10-08
     * @param
     * @throws Exception
     */
    private IDataOutput getVpmnShortCode(String serialNumberA, String shortType, String shortlength, String contain_4, String contain_7, String vpn_name, String user_id) throws Exception
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

        IDataOutput ido = CSViewCall.callPage(this, "CS.RelaUUInfoQrySVC.getVpmnShortCode", inparams, getPagination("pageNav")); // j2ee替换老的SQLString方法

        return ido;

    }

    public abstract IData getCondition();

    /**
     * @Description:通过母vpmn取该母下的所有短号信息
     * @author zhouhua
     * @date 2012-10-08
     * @param
     * @throws Exception
     */
    private IDataOutput getDataByMother(String shortType, String shortlength, String contain_4, String contain_7, String MotherID, String MSNA) throws Exception
    {
        // 按母VPMN编码查询 MotherID
        IData inparam = new DataMap();
        String name = getVpnNameByUserId(MotherID);
        inparam.put("USER_ID_A", MotherID);
        inparam.put("SHORT_TYPE", shortType);
        inparam.put("SHORT_LENGTH", shortlength);
        inparam.put("CONTAIN_4", contain_4);
        inparam.put("CONTAIN_7", contain_7);
        inparam.put("VPN_NAME", name);
        inparam.put("SERIAL_NUMBER_M", MSNA);
        inparam.put("tag", "0");

        IDataOutput ido = CSViewCall.callPage(this, "CS.RelaUUInfoQrySVC.getMotherVpmnShortCode", inparam, getPagination("pageNav")); // j2ee替换老的minusSQLString方法

        return ido;

    }

    public abstract IDataset getInfos();

    /**
     * @Description:通过子集团ID获取母集团ID
     * @author zhouhua
     * @date 2012-10-08
     * @param
     * @throws Exception
     */
    private IData getMotherIDAByIDB(String userIdB) throws Exception
    {
        // 获取母集团ID
        IData reinfo = new DataMap();
        IDataset GroupVpmnInfos = RelationUUInfoIntfViewUtil.qryGrpRelaUUInfosByUserIdBAndRelationTypeCode(this, userIdB, "40", false);

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
    private String getVpnNameByUserId(String USER_ID) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", USER_ID);

        IData GroupVpmnInfo = UCAInfoIntfViewUtil.qryUserVpnInfoByUserId(this, USER_ID);
        String name = "";
        if (IDataUtil.isNotEmpty(GroupVpmnInfo))
        {
            name = GroupVpmnInfo.getString("VPN_NAME");
        }

        return name;

    }

    /**
     * @Description: 初始化页面方法
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
        setHintInfo("请输入查询条件~~!");
    }

    /**
     * @Description: 短号查询
     * @author lixiuyu
     * @date 2010-12-22
     * @param cycle
     * @throws Exception
     */
    public void queryInfos(IRequestCycle cycle) throws Exception
    {
        IData inparams = getData("cond", true);
        String qType = inparams.getString("QueryType", "");

        String serialNumberA = inparams.getString("SERIAL_NUMBER_A", "");
        String serialNumber = inparams.getString("SERIAL_NUMBER", "");
        // String groupid = inparams.getString("GROUP_ID", "");
        String MserialNumberA = inparams.getString("MSERIAL_NUMBER_A");

        String shortType = inparams.getString("ShortType"); // 短号位长
        String contain_4 = inparams.getString("Contain_4").equals("3") ? "" : inparams.getString("Contain_4");
        String contain_7 = inparams.getString("Contain_7").equals("3") ? "" : inparams.getString("Contain_7");
        String shortlength = inparams.getString("SHORT_LENGTH", "");

        String vpn_name = "";

        IData motherInfo = new DataMap();
        IDataOutput ido = null;
        if ("0".equals(qType))
        {// 按VPMN编码查询
            StringUtils.isNotBlank(serialNumberA);
            // 根据集团编号获取集团用户信息
            IData param = new DataMap();

            param.put("SERIAL_NUMBER", serialNumberA);
            IData userinfo = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, serialNumberA);

            if (IDataUtil.isEmpty(userinfo))
            {
                CSViewException.apperr(VpmnUserException.VPMN_USER_1, serialNumberA);
            }
            String vpmnUserId = userinfo.getString("USER_ID");
            motherInfo = getMotherIDAByIDB(vpmnUserId); // 通过子集团ID获取母集团信息
            vpn_name = getVpnNameByUserId(vpmnUserId);
            if (IDataUtil.isNotEmpty(motherInfo))
            {
                ido = getDataByMother(shortType, shortlength, contain_4, contain_7, motherInfo.getString("USER_ID_A"), motherInfo.getString("SERIAL_NUMBER_A"));
            }
            else
            {
                ido = getVpmnShortCode(serialNumberA, shortType, shortlength, contain_4, contain_7, vpn_name, vpmnUserId);// 前台需要显示的数据
            }
        }
        else if ("1".equals(qType))
        {// 按手机号码查询
            StringUtils.isNotBlank(serialNumber);
            String serial_number_a = "";
            IData userinfo = UCAInfoIntfViewUtil.qryMebUserInfoBySn(this, serialNumber);
            if (IDataUtil.isNotEmpty(userinfo))
            {
                // 获取用户与用户关系信息
                IData userrelationparam = new DataMap();
                userrelationparam.put("USER_ID_B", userinfo.getString("USER_ID", ""));
                userrelationparam.put("RELATION_TYPE_CODE", "20");

                IDataset userrelations = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdBAndRelationTypeCode(this, userinfo.getString("USER_ID", ""), "20", Route.getCrmDefaultDb(), false);

                if (IDataUtil.isEmpty(userrelations))
                {
                    CSViewException.apperr(VpmnUserException.VPMN_USER_144, serialNumber);
                }
                // 20的不止8000的vpmn产品，所以需要再判断start
                IDataset ds8000 = getGrpUserInfosByProductId(userrelations, "8000");
                if (IDataUtil.isEmpty(ds8000))
                {
                    CSViewException.apperr(VpmnUserException.VPMN_USER_144);
                }
                // 20的不止8000的vpmn产品，所以需要再判断end

                IData uuinfo = (IData) ds8000.getData(0);
                String grpUserId = uuinfo.getString("USER_ID_A");
                serial_number_a = uuinfo.getString("SERIAL_NUMBER_A");
                motherInfo = getMotherIDAByIDB(grpUserId); // 获取成员所属母vpmn的信息

                if (IDataUtil.isNotEmpty(motherInfo))
                {// m;
                    ido = getDataByMother(shortType, shortlength, contain_4, contain_7, motherInfo.getString("USER_ID_A"), motherInfo.getString("SERIAL_NUMBER_A"));
                }
                else
                {
                    vpn_name = getVpnNameByUserId(grpUserId);
                    ido = getVpmnShortCode(serial_number_a, shortType, shortlength, contain_4, contain_7, vpn_name, grpUserId);// 前台需要显示的数据
                }
            }
        }
        else if ("3".equals(qType))
        {// 按母VPMN编码查询
            StringUtils.isNotBlank(MserialNumberA);
            IDataset vpn = new DatasetList(); // vpn集
            // 获取用户信息
            inparams.clear();
            inparams.put("SERIAL_NUMBER", MserialNumberA);
            IData Userinfo = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, MserialNumberA);
            if (IDataUtil.isNotEmpty(Userinfo))
            {
                ido = getDataByMother(shortType, shortlength, contain_4, contain_7, Userinfo.getString("USER_ID"), MserialNumberA);

            }
            else
            {
                CSViewException.apperr(VpmnUserException.VPMN_USER_142, MserialNumberA);
            }
        }
        IDataset dataset = ido.getData();
        if (IDataUtil.isEmpty(dataset))
        {
            setHintInfo("没有符合条件的查询结果~~！");
        }
        else
        {
            setHintInfo("查询成功~~！");
        }
        long tt = 0;
        tt = ido.getDataCount();
        setPageCounts(tt);
        setCondition(getData("cond"));
        setInfos(dataset);

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
            IData userInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, grpUserId);
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

    public abstract void setCondition(IData condition);

    public abstract void setHintInfo(String hintInfo);

    public abstract void setInfos(IDataset infos);

    public abstract void setPageCounts(long pageCounts);

}
