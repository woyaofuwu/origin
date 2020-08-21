
package com.asiainfo.veris.crm.order.web.group.param.mas;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.IntfPADCException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.UserCreditInfo.UserCreditInfoViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.fee.UserFeeIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.commparainfo.CommParaInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupEsopUtilView;
import com.asiainfo.veris.crm.order.web.group.param.GroupParamPage;

public abstract class UserParamInfo extends GroupParamPage
{

    public UserParamInfo()
    {
        super();
    }

    /**
     * 作用:判断服务代码是否可用
     * 
     * @author
     * @param cycle
     * @throws Exception
     */
    public void getDumpIdByajax(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData temp1 = new DataMap();
        String strBizInCode = data.getString("BIZ_IN_CODE", "");
        String group_id = data.getString("GROUP_ID", "");
        String flag = "true";
        temp1.put("ACCESSNUMBER", strBizInCode);
        temp1.put("GROUP_ID", group_id);
        IDataset idata = CSViewCall.call(this, "CS.UserGrpInfoQrySVC.getDumpIdByajax", temp1);
        if (IDataUtil.isNotEmpty(idata))
        {
            flag = "false";
        }

        setAjax("ISCHECKAACCESSNUMBER", flag);
    }

	
	 public void createNewBizCode(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String servType=data.getString("SERV_TYPE","");
        String bizCode=data.getString("BIZ_CODE","");
        IDataset commParam = CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndParamCodeParamCode1EparchyCode(this,"CSM", "2665", "SERVTYPE", servType, "ZZZZ");
        if(IDataUtil.isNotEmpty(commParam) && IDataUtil.isNotEmpty(commParam.getData(0))){
            IData paramInfo = commParam.getData(0);
            String para_code = paramInfo.getString("PARA_CODE2");
            if (StringUtils.isBlank(para_code)){
                CSViewException.apperr(CrmUserException.CRM_USER_783,"行业编码转换关系配置数据为空!");
            }
            StringBuilder bizcode=new StringBuilder(bizCode);
            bizcode.replace(6, 8, para_code);
            setAjax("BIZ_CODE",bizcode.toString());
            return;
        }
        setAjax("BIZ_CODE",bizCode);
    }
    /**
     * 作用:判断管理员手机号是否可用
     * 
     * @author
     * @param cycle
     * @throws Exception
     */
    public void getDumpSnByajax(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData temp = new DataMap();
        temp.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
        String flag = "true";

        IDataset result = CSViewCall.call(this, "CS.UserInfoQrySVC.getUserInfoBySnCrmOneDb", temp);
        if (IDataUtil.isNotEmpty(result))
        {
            flag = "false";
        }
        setAjax("IS_FLAGSN", flag);
    }

    /*
     * 获取集团客户信息
     */
    public IData getGroupBaseInfo(String groupId, String custId) throws Exception
    {

        IData result = null;
        if (StringUtils.isNotEmpty(custId))
            result = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, custId);
        else
            result = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);

        return result;

    }

    /*
     * 根据MAS 基本接入号属性，查询基本接入号列表
     */
    public void getMasEcCodeListByA(IRequestCycle cycle) throws Throwable
    {
        IData data = getData();
        String strCodeA = data.getString("EC_BASE_IN_CODE_A");
        if (!(strCodeA.equals("02") || strCodeA.equals("03"))) {
            strCodeA = "01";
        }
        String strCustId = data.getString("CUST_ID");

        String bizTypeCode = data.getString("BIZ_TYPE_CODE");
        IData param = new DataMap();
        param.put("CUST_ID", strCustId);
        param.put("BNUM_KIND_NEW", strCodeA);
        if ("002".equals(bizTypeCode))
        {
            param.put("EXTEND_TAG", "BNUMBMMS");
        }
		else if ("003".equals(bizTypeCode)) 
		{
            param.put("EXTEND_TAG", "WAP-PUSH");
        } 
        else
        {
            param.put("EXTEND_TAG", "BNUMB");
        }

        IDataset result;
        if(("003".equals(bizTypeCode))){
            result = result = CSViewCall.call(this, "CS.GrpExtInfoQrySVC.getWAPPUSHEcInCodeListByECA", param);
        }else{
            result = CSViewCall.call(this, "CS.GrpExtInfoQrySVC.getEcInCodeListByECA", param);
        }
        setAjax(result);
    }

    /**
     * 获取敏感词汇
     * 
     * @param cycle
     * @throws Exception
     * @author chenlei
     */
    public void getSensitiveTextByajax(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String productId = data.getString("PRODUCT_ID");

        // 中英文签名敏感字符过滤产品配置
        IDataset proDatas = CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndParamCode(this, "CGM", "4387", productId);
        ;

        IData tmp = new DataMap();
        IDataset flagParamset = new DatasetList();
        if (IDataUtil.isNotEmpty(proDatas))
        {
            tmp.put("IN_PRODUCT", "true");// 在判断敏感字符产品列表之内
            String textEcgnZh = data.getString("TEXT_ECGN_ZH");// 中文签名
            String textEcgnEn = data.getString("TEXT_ECGN_EN");// 英文签名

            // 敏感字符
            IDataset sensitiveWords = CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndParamCode(this, "CGM", "4386", "ADCZHEN");
            tmp.put("HAS_ZH", "false");
            tmp.put("HAS_EN", "false");

            if (IDataUtil.isNotEmpty(sensitiveWords))
            {
                for (int i = 0; i != sensitiveWords.size(); ++i)
                {

                    if (textEcgnZh.indexOf(sensitiveWords.getData(i).getString("PARAM_NAME")) != -1)
                    {
                        tmp.put("HAS_ZH", "true");// 含有中文敏感字符
                        tmp.put("PARAM_NAME", sensitiveWords.getData(i).getString("PARAM_NAME"));
                        break;
                    }

                    if (textEcgnEn.indexOf(sensitiveWords.getData(i).getString("PARAM_NAME")) != -1)
                    {
                        tmp.put("HAS_EN", "true");// 含有英文敏感字符
                        tmp.put("PARAM_NAME", sensitiveWords.getData(i).getString("PARAM_NAME"));
                        break;
                    }

                }
            }
        }
        else
        {
            tmp.put("IN_PRODUCT", "false");// 在判断敏感字符产品列表之外
        }
        flagParamset.add(tmp);
        setAjax(flagParamset);
    }

    /**
     * 作用：查询一个服务是否被用户已经订购过 或者是一个默认或必须服务 目的:用来判断是否已经通过基类查询过服务
     * 
     * @author
     * @param cycle
     * @param Throwable
     */
    public void getServiceParamsByajax(IRequestCycle cycle) throws Exception
    {
        IData ajaxdata = getData();
        IData serviceInparam = new DataMap();
        serviceInparam.put("USER_ID", ajaxdata.getString("USER_ID", ""));
        serviceInparam.put("PRODUCT_ID", ajaxdata.getString("PRODUCT_ID", ""));
        serviceInparam.put("PACKAGE_ID", ajaxdata.getString("PACKAGE_ID", ""));
        serviceInparam.put("SERVICE_ID", ajaxdata.getString("SERVICE_ID", ""));
        serviceInparam.put("EPARCHY_CODE", ajaxdata.getString("EPARCHY_CODE", ""));
        IDataset serviceparamset = CSViewCall.call(this, "SS.MasUserParamsSvc.getServiceParam", serviceInparam);

        // add for esop
        IData esopParams = GroupEsopUtilView.getEsopParams(this, ajaxdata, true);
        if (IDataUtil.isNotEmpty(esopParams))
        {
            esopParams = IDataUtil.replaceIDataKeyAddPrefix(esopParams, "pam_");
            if (IDataUtil.isNotEmpty(serviceparamset) && serviceparamset.getData(1).get("PLATSVC") instanceof IData)
            {
                serviceparamset.getData(1).getData("PLATSVC").putAll(esopParams);
            }
        }
        // end esop

        setAjax(serviceparamset);
    }

    /*
     * 获取用户欠费信息
     */
    public void grpUserOweFeeByajax(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData acctData = new DataMap();
        String userId = data.getString("USER_ID", "");

        // 查询用户的信用等级信息
        String credeitValue = UserCreditInfoViewUtil.qryUserCredeitValue(this, userId);

        // 查询用户欠费信息
        IData oweFeeData = UserFeeIntfViewUtil.qryGrpUserOweFeeInfo(this, userId);
        double lastOweFee = Double.valueOf(oweFeeData.getString("LAST_OWE_FEE"));// 往月欠费
        double relaFee = Double.valueOf(oweFeeData.getString("REAL_FEE"));// 实时话费
        double acctBalance = Double.valueOf(oweFeeData.getString("ACCT_BALANCE"));// 实时结余

        acctData.put("OWE_FEE", String.format("%1$3.2f", (lastOweFee + relaFee) / 100.0));
        acctData.put("ACCT_BALANCE", String.format("%1$3.2f", acctBalance / 100.0));
        acctData.put("CREDIT_VALUE", credeitValue);

        setAjax(acctData);

    }

    /*
     * 初始服务新增 参数界面
     */
    public void initSvcParamsInfo(IRequestCycle cycle) throws Throwable
    {
        // 服务参数入口方法
        IData data = getData();
        String itemIndex = data.getString("ITEM_INDEX");
        this.setItemIndex(itemIndex);
        String serviceId = data.getString("ELEMENT_ID");
        this.setServiceId(serviceId);
        String cancleflag = data.getString("CANCLE_FLAG", "");
        this.setCancleFlag(cancleflag);
        String grpUserEparchyCode = data.getString("GRP_USER_EPARCHYCODE");
        this.setGrpUserEparchyCode(grpUserEparchyCode);

        String groupId = data.getString("GROUP_ID", "");
        String custId = data.getString("CUST_ID", "");
        IData groupDate = getGroupBaseInfo(groupId, custId);
        this.setGroupDate(groupDate);
        String staffId = this.getVisit().getStaffId();
        boolean bool = StaffPrivUtil.isFuncDataPriv(staffId, "GROUPMENBER_MODIFY_PRV");
        this.setHasModifyPrv(bool);

    }

    public abstract void setButtenName(String buttenName);

    public abstract void setCancleFlag(String cancleFlag);

    public abstract void setCheckInfo(IData checkInfo);

    public abstract void setComboBox(int cobobox);

    public abstract void setComboBoxValue(IDataset dataset);

    public abstract void setGroupDate(IData groupDate);

    public abstract void setGrpUserEparchyCode(String grpUserEparchyCode);

    public abstract void setItemIndex(String itemIndex);

    public abstract void setMasincode(IDataset masIncode);

    public abstract void setMoinfo(IData moinfo);

    public abstract void setMoinfoDetail(IData moinfoDetail);

    public abstract void setMoListinfos(IDataset moListinfos);

    public abstract void setParamVerifySucc(String paramVerifySucc);

    public abstract void setPlatsvcparam(IData info);

    public abstract void setServiceId(String serviceId);

    public abstract void setSvcotherlists(IDataset temp);
    
    public abstract void setHasModifyPrv(boolean hasModifyPrv);

}
