
package com.asiainfo.veris.crm.order.soa.group.param.adc;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMoList;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class UserParams
{

    /**
     * 构造函数
     */
    public UserParams()
    {

    }

    private void dataMsgCountLimit(IData data) throws Exception
    {

        data.put("LIMIT_MAX_ITEM_PRE_DAY", "100000");
        data.put("LIMIT_MAX_ITEM_PRE_MON", "100000");

    }

    /*
     * 根据service_id查询对应该服务的用户上行指令参数
     */
    public IDataset getMoList(IData param) throws Exception
    {
        String userId = param.getString("USER_ID", "");
        String serviceId = param.getString("SERVICE_ID", "");
        return UserGrpMoList.getuserMolistbyserverid(userId, serviceId);

    }

    public IDataset getServiceParam(IData params) throws Exception
    {

        // 不管是不是新增的服务，总会取已有的，或者取初始化的
        IData platsvcParam = this.getServicePlatsvcParam(params);

        // 将服务是否是新增的标志（取回来）
        String newTag = platsvcParam.getString("pam_INNER_SVC_TAG");
        IData checkmap = new DataMap();
        if ("NEW".equals(newTag))
        {
            checkmap.put("PARAM_VERIFY_SUCC", "false");// 当是新增服务时，参数自然没有
        }
        else
        {
            checkmap.put("PARAM_VERIFY_SUCC", "true");// 当是修改服务时，参数自然是已有，故下一步按钮是要放过的
        }

        IDataset moList = this.getMoList(params);
        IDataset paramDataset = new DatasetList();
        IData serparam = new DataMap();
        serparam.put("PLATSVC", platsvcParam);
        serparam.put("MOLIST", moList);
        serparam.put("ID", params.getString("SERVICE_ID", ""));

        paramDataset.add(0, checkmap);
        paramDataset.add(1, serparam);
        return paramDataset;
    }

    /**
     * 获取服务个性化参数显示信息
     *
     * @author zouli
     * @param params
     *            页面传入的参数
     * @return IDataset 执行返回结果
     * @throws Exception
     */
    public IData getServicePlatsvcParam(IData params) throws Exception
    {
        // log, "进入取初始化弹出窗口服务参数platsvc的方法");
        String userId = params.getString("USER_ID", "");
        String custId = params.getString("CUST_ID", "");
        String productid = params.getString("PRODUCT_ID", "");
        String packageId = params.getString("PACKAGE_ID", "");
        String serviceId = params.getString("SERVICE_ID", "");
        String eparchyCode = params.getString("EPARCHY_CODE", "");
        IData platsvc = new DataMap();
        IData staffinfo = new DataMap();
        boolean createsvc = true;// 该服务是新增 还是修改标识 默认为新增

        if (StringUtils.isNotBlank(userId))// 如果是从用户修改进来的 判断用户是否已经订购了该服务信息
        {
            // 此if语句进入，是说明已有用户ID，即是修改用户信息
            String userIdA = "-1";
            IDataset usersvc = UserSvcInfoQry.getUserSvcBycon(userId, userIdA, productid, packageId, serviceId);

            if (IDataUtil.isNotEmpty(usersvc))
            {
                createsvc = false;// 证明这个服务订购过，即应该是修改服务参数
            }
        }
        if (createsvc)// 为新加服务时，TD_B_ATTR_ITEMA表中取默认数据
        {
            String idType = "S";
            String attrObj = "0";
            IDataset dataset = AttrItemInfoQry.getAttrItemAByIDTO(serviceId, idType, attrObj, eparchyCode, null);
            platsvc = IDataUtil.hTable2StdSTable(dataset, "ATTR_CODE", "ATTR_INIT_VALUE");
            platsvc.put("OPER_STATE", "01");
            platsvc.put("MODIFY_TAG", "0"); // 标识新增
            platsvc.put("PLAT_SYNC_STATE", "1");
            platsvc.put("SERVICE_ID", serviceId); // 集团服务ID

            if (StringUtils.isNotEmpty(custId))
            {// mas没有这样取管理员号码
                IData data = UcaInfoQry.qryGrpInfoByCustId(custId);
                platsvc.put("ADMIN_NUM", data.getString("GROUP_MGR_SN", "")); // 管理员号码
                platsvc.put("CITY_CODE", data.getString("CITY_CODE", "")); // 归属市县
                platsvc.put("CUST_MANAGER_ID", data.getString("CUST_MANAGER_ID", "")); // 客户经理工号
                
                staffinfo= UStaffInfoQry.qryStaffInfoByPK(data.getString("CUST_MANAGER_ID", ""));
                if (IDataUtil.isNotEmpty(staffinfo))
                {
                    platsvc.put("STAFF_NAME", staffinfo.getString("STAFF_NAME", "")); // 客户经理名称
                    
                }
            }
            // 生成服务代码尾
            String svrlength = platsvc.getString("C_LENGTH", "");// 服务代码尾号长度
            String svrCodeTail = "";// 生成服务代码尾
            if (StringUtils.isNotBlank(svrlength))
            {
                svrCodeTail = getBizCodeTail(svrlength);
            }
            platsvc.put("SVR_CODE_END", svrCodeTail);
            platsvc.put("INNER_SVC_TAG", "NEW");// 将服务是否是新增的标志，带出去

            platsvc.put("IS_SPEC_AREA_DISPLAY", StaticUtil.getStaticValue("ADC_SPEC_AREA_DISPLAY", serviceId));

        }
        else
        // 修改服务时， 取用户platsvc数据
        {
            IData platsvcparam = UserGrpPlatSvcInfoQry.getuserPlatsvcbyserverid(userId, serviceId);// 取平台服务表已经存在的参数
            String si_base_in_code = platsvcparam.getString("SI_BASE_IN_CODE");// ADC SI 基本接入号 10657020
            String svr_code_head = platsvcparam.getString("RSRV_NUM4");// 服务代码头 106570200
            // 老资料基本接入号和服务代码头有可以为空，为空时查询ATTR表的配置补上
            if (StringUtils.isBlank(si_base_in_code))
            {
                si_base_in_code = AttrItemInfoQry.queryServiceItemA(serviceId, "SIBASE_INCODE");
            }
            if (StringUtils.isBlank(svr_code_head))
            {
                svr_code_head = AttrItemInfoQry.queryServiceItemA(serviceId, "SVR_CODE_HEAD");
            }
            platsvcparam.put("SIBASE_INCODE", si_base_in_code);
            platsvcparam.put("SIBASE_INCODE_A", platsvcparam.getString("SI_BASE_IN_CODE_A"));
            platsvcparam.put("SVR_CODE_HEAD", svr_code_head);

            // 查询在TF_F_USER_GRP_PLATSVC表里面存不下 然后存在attr纵表内的实例属性
            IDataset userattrdataset = UserAttrInfoQry.getuserAttrBySvcId(userId, serviceId);
            IData userattrdata = IDataUtil.hTable2StdSTable(userattrdataset, "ATTR_CODE", "ATTR_VALUE");
            platsvcparam.putAll(userattrdata);
            platsvc = platsvcparam;
            platsvc.put("MODIFY_TAG", "2"); // 标识修改 即已经存在的服务信息
            platsvc.put("SERVICE_ID", serviceId); // 集团服务ID
            platsvc.put("INNER_SVC_TAG", "OLD");// 将服务是修改标志，带出去
            if("100022".equals(serviceId) || "100024".equals(serviceId))
            {
                platsvc.put("SP_CODE", platsvc.getString("RSRV_STR2", ""));;//校讯通填写合作伙伴编码

            }
            else if ("100082".equals(serviceId) || "100083".equals(serviceId))
            {
                String [] strParamCode  = platsvc.getString("RSRV_STR4", "").split(",");
                String [] strParamValue = platsvc.getString("RSRV_STR2", "").split(",");
                
                for(int i=0;i<strParamCode.length;i++)
                {
                    platsvc.put(strParamCode[i], strParamValue[i]);
                    
                }
            }

            if ("04".equals(platsvc.getString("OPER_STATE")))// "OPER_STATE":"04" 表示暂停
            {
                platsvc.put("PLAT_SYNC_STATE", "P");
            }

            // 业务类型
            platsvc.put("SERVICE_TYPE", platsvc.getString("RSRV_NUM2", ""));
            // 白名单二次确认
            platsvc.put("WHITE_TOWCHECK", platsvc.getString("RSRV_NUM3", ""));
            // 模板短信管理
            platsvc.put("SMS_TEMPALTE", platsvc.getString("RSRV_NUM5", ""));
            // 端口类别
            platsvc.put("PORT_TYPE", platsvc.getString("RSRV_STR1", ""));

            platsvc.put("IS_SPEC_AREA_DISPLAY", StaticUtil.getStaticValue("ADC_SPEC_AREA_DISPLAY", serviceId));
        }
        dataMsgCountLimit(platsvc);
        platsvc.put("PRODUCT_ID", productid);
        platsvc = IDataUtil.replaceIDataKeyAddPrefix(platsvc, "pam_");

        return platsvc;
    }

    /**
     * 作用:自动生成服务代码尾
     *
     * @author liaolc 2014-07-24
     */
    public String getBizCodeTail(String sclen) throws Exception
    {
        int cLen = 0;
        String codeC = "";
        codeC = SeqMgr.getGrpMolist();
        codeC = "00000" + codeC;
        try
        {
            cLen = Integer.parseInt(sclen);
        }
        catch (Exception e)
        {
            cLen = 0;
        }

        codeC = codeC.substring((codeC.length() - cLen), codeC.length());

        return codeC;
    }

}
