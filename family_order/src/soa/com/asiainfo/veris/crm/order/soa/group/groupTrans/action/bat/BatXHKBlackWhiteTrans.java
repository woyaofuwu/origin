
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.IntfIAGWException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaXxtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBlackWhiteInfoQry;
import com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat.util.GroupBatTransUtil;

public class BatXHKBlackWhiteTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {
        // 初始化数据

        // 校验请求参数
        checkRequestDataSub(batData);

        // 构建服务请求数据
        builderSvcData(batData);

        // 根据条件判断调用服务
        setSVC(batData);
    }

    protected void checkRequestDataSub(IData batData) throws Exception
    {
        IData condData = batData.getData("condData", new DataMap());
        String oprCode = IDataUtil.chkParam(condData, "OPER_CODE");
        String grpUserId = IDataUtil.chkParam(condData, "USER_ID");// 集团用户ID
        String mainSn = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码(学护卡号码)

        IData userGrpInfo = UcaInfoQry.qryUserMainProdInfoBySn(mainSn);
        if (IDataUtil.isEmpty(userGrpInfo))
        {// 网外号码
            CSAppException.apperr(BatException.CRM_BAT_79);
        }

        //校验学护卡15元包 start
        boolean isAddSpecDis = isAddSpecDis(batData);//判断是否订购学护卡15元包
        if (isAddSpecDis)
        {
        	String famSn  = batData.getString("DATA1","");//家长号码
        	if (StringUtils.isBlank(famSn))
        	{
        		CSAppException.apperr(BatException.CRM_BAT_93);
			}
        	boolean isFamSnAddXXTDis = isFamSnAddXXTDis(famSn);//订购学护卡15元包,判断家长号码是否订购过校讯通10资费
        	if (!isFamSnAddXXTDis)
        	{
        		CSAppException.apperr(BatException.CRM_BAT_94,famSn);
			}
		}
        //校验学护卡15元包 end

        IDataset blackwhite = UserBlackWhiteInfoQry.getBlackWhiteInfoBySnEcuserid(mainSn, grpUserId);// 查sn在blackwhite的记录
        boolean isBW = IDataUtil.isNotEmpty(blackwhite) ? true : false;

        if ("01".equals(oprCode) && isBW)//新增
        {
            CSAppException.apperr(IntfIAGWException.CRM_IGU_08); // ("用户已在名单内")
        }
        if ("02".equals(oprCode) && !isBW)//退订
        {
            CSAppException.apperr(IntfIAGWException.CRM_IGU_09);// ("用户不在名单内")
        }
        if ("08".equals(oprCode) && !isBW)//变更
        {
            CSAppException.apperr(IntfIAGWException.CRM_IGU_09);// ("用户不在名单内")
        }

        String groupId = IDataUtil.chkParam(condData, "GROUP_ID");
        IData httpData = new DataMap();
        IDataset selectedElement = new DatasetList(IDataUtil.chkParam(condData, "SELECTED_ELEMENTS"));
        String bizServCode = "";

        IData elementMap = null;
        IDataset attrParamList = null;
        IData attrParamMap = null;
        for(int i=0; i < selectedElement.size(); i++) {
            elementMap = selectedElement.getData(i);
            if ("574401".equals(elementMap.getString("ELEMENT_ID", ""))) {
                attrParamList = elementMap.getDataset("ATTR_PARAM");
                for (int j = 0; j < attrParamList.size(); j++) {
                    attrParamMap = attrParamList.getData(j);
                    if (!IDataUtil.isEmpty(attrParamMap.getData("PLATSVC"))) {
                        bizServCode = attrParamMap.getData("PLATSVC").getString("pam_BIZ_IN_CODE");
                    }
                }
            }
        }

        httpData.put("EC_ID", groupId);
        httpData.put("MOB_NUM", mainSn);
        httpData.put("OPR_CODE", "01");
        httpData.put("BIZ_SERV_CODE", bizServCode);
        httpData.put("KIND_ID", "BIPXHK02_TX000001_0_0");

        checkMemberInfoforXHK(httpData);

    }

    public void checkMemberInfoforXHK(IData httpData)throws Exception
    {
        IData httpStr = new DataMap();
        httpStr.put("EC_ID", httpData.getString("EC_ID"));
        httpStr.put("MOB_NUM", httpData.getString("MOB_NUM"));
        httpStr.put("OPR_CODE", httpData.getString("OPR_CODE"));
        httpStr.put("BIZ_SERV_CODE", httpData.getString("BIZ_SERV_CODE"));
        httpStr.put("KIND_ID", "BIPXHK02_TX000001_0_0");

        String kindId = httpStr.getString("KIND_ID");

        IDataset result = GroupBatTransUtil.checkMemberForXHK(httpStr);
        if (IDataUtil.isEmpty(result))
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "调用学护卡平台异常！");
        }
        else if (!("00".equals(result.getData(0).getString("X_RESULTCODE", "")))) //学护卡平台失败台校验失败
        {
            String errInfo="学护卡平台校验失败：【"+ httpData.getString("MOB_NUM") + ":"+result.getData(0).getString("X_RESULTINFO", "") + "】";
            CSAppException.apperr(GrpException.CRM_GRP_713,errInfo);
        }
    }

    protected void builderSvcData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());
        IData condData = batData.getData("condData", new DataMap());

        boolean isOutNet = condData.getBoolean("IS_OUT_NET", false);
        if (isOutNet)
        {// 网外号码
        	CSAppException.apperr(BatException.CRM_BAT_79);
        }
        else
        {
            IData productParamAttr1 = new DataMap();
            IDataset productParamAttrset = new DatasetList();
            IDataset resultset = new DatasetList();
            IData result = new DataMap();

            svcData.put("USER_ID", IDataUtil.getMandaData(condData, "USER_ID"));// 集团USER_ID
            svcData.put("SERIAL_NUMBER", IDataUtil.getMandaData(batData, "SERIAL_NUMBER"));// 成员SN
            svcData.put("MEM_ROLE_B", condData.getString("PLAN_TYPE_CODE", "1"));// 成员角色
            svcData.put("PLAN_TYPE_CODE", condData.getString("PLAN_TYPE_CODE", "P"));// 个人付款
            svcData.put("RES_INFO", condData.getDataset("RES_INFO", new DatasetList("[]")));
            svcData.put("EFFECT_NOW", condData.getString("EFFECT_NOW", "true"));
            svcData.put("PRODUCT_ID", condData.getString("PRODUCT_ID", ""));// 集团产品ID
            svcData.put("ELEMENT_INFO", condData.getDataset("SELECTED_ELEMENTS", new DatasetList("[]")));

            productParamAttr1.put("ATTR_VALUE", batData.getString("DATA1",""));
            productParamAttr1.put("ATTR_CODE", "NOTIN_FAM_SN");

            productParamAttrset.add(productParamAttr1);

            result.put("PRODUCT_ID", condData.getString("PRODUCT_ID","10005744"));
            result.put("PRODUCT_PARAM", productParamAttrset);
            resultset.add(result);
            svcData.put("PRODUCT_PARAM_INFO", resultset);// 产品参数
        }

    }

    // 根据条件判断调用服务
    protected void setSVC(IData batData) throws Exception
    {
        String svcName = "";
        IData condData = batData.getData("condData", new DataMap());
        IData svcData = batData.getData("svcData", new DataMap());

        String operType = IDataUtil.chkParam(condData, "OPER_CODE");

        if ("01".equals(operType))
        {// 成员新增
            svcName = "CS.CreateGroupMemberSvc.createGroupMember";
        }
        else if ("02".equals(operType))
        {// 成员删除
            svcName = "CS.DestroyGroupMemberSvc.destroyGroupMember";
        }
        else
        {// 成员变更
            svcName = "CS.ChangeMemElementSvc.changeMemElement";
        }

        svcData.put("REAL_SVC_NAME", svcName);
    }
	/**
	 * 判断是否订购学护卡15元包
	 * @param batData
	 * @return
	 * @throws Exception
	 */
    public boolean isAddSpecDis(IData batData) throws Exception
    {
        IData condData = batData.getData("condData", new DataMap());
        String selectedElements = condData.getString("SELECTED_ELEMENTS");
        IDataset selectedElementslist = new DatasetList(selectedElements);
        boolean flag = false ;
        for (int i = 0; i < selectedElementslist.size(); i++)
        {
            String mebServId = selectedElementslist.getData(i).getString("ELEMENT_ID");
            String elementTypeCode = selectedElementslist.getData(i).getString("ELEMENT_TYPE_CODE");
            String modifyTag = selectedElementslist.getData(i).getString("MODIFY_TAG");
             if ("32000001".equals(mebServId)&&TRADE_MODIFY_TAG.Add.getValue().equals(modifyTag)&&"D".equals(elementTypeCode))// ，如果该成员用户要订购学护卡15元包,则必须是订购过校讯通10元套餐的用户) {
             {
             	flag = true;
            	break;
			 }
        }
        return flag;
    }
    /**
     * 订购学护卡15元包,判断家长号码是否订购过校讯通10资费
     * @param batData
     * @return
     * @throws Exception
     */
    public boolean isFamSnAddXXTDis(String famSn) throws Exception{
     	boolean flag = false ;
     	IDataset infosDataset = RelaXxtInfoQry.qryMemInfoBySNandUserIdA(famSn);
    	if (IDataUtil.isNotEmpty(infosDataset))
    	{
    	    IDataset limitDiscnt = StaticUtil.getStaticList("EDC_DISCNT_15_LIMIT");
            for (int j = 0, jsize = infosDataset.size(); j < jsize; j++)
            {
                IData map = infosDataset.getData(j);
                String disId = map.getString("ELEMENT_ID", "");
                IDataset filterResult = DataHelper.filter(limitDiscnt, "DATA_ID=" + disId);
                if (IDataUtil.isNotEmpty(filterResult))
                {
                	flag = true;
                	break;
                }

            }
    	}
    	return flag;
    }

}
