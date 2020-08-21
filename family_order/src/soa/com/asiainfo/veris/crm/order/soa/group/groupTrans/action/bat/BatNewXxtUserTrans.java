
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaXxtInfoQry;
import com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat.util.GroupBatTransUtil;

public class BatNewXxtUserTrans implements ITrans
{
	private static final Logger log = Logger.getLogger(BatNewXxtUserTrans.class);
	
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
        String mainSn = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 订购手机号码
        IData svcData = batData.getData("svcData", new DataMap());
        String batchOperType = svcData.getString("BATCH_OPER_TYPE");
        String ecuserid = IDataUtil.getMandaData(condData, "USER_ID");
        if(batchOperType.equals("NEWXXTUSERREG_SPE")){
            if(StringUtils.isBlank(batData.getString("DATA8"))){
                CSAppException.apperr(BatException.CRM_BAT_96);;// 新和校园特殊批量模板需要选择业务订购确认方式
            }
        }

        IData userGrpInfo = UcaInfoQry.qryUserMainProdInfoBySn(mainSn);
        if (IDataUtil.isEmpty(userGrpInfo)){// 网外号码
            CSAppException.apperr(BatException.CRM_BAT_79);
        }

        //校验家长号码是否订购了和校园 start
    	String famSn  = batData.getString("SERIAL_NUMBER","");//订购手机号码
    	if (StringUtils.isBlank(famSn)){
    		CSAppException.apperr(BatException.CRM_BAT_87);
		}
    	
    	//排除异网和校园用户，异网和校园用户是（H+电话号码）
    	String SERIAL_NUMBER=batData.getString("SERIAL_NUMBER");
    	if(!batData.get("SERIAL_NUMBER").toString().trim().startsWith("H")){
    		//根据用户id去查找数据，判断是否已经实名制和已经激活
        	IData UserInfo=UcaInfoQry.getUserInfo(SERIAL_NUMBER);
        	
        	if(IDataUtil.isNotEmpty(UserInfo)){
        		String ACCT_TAG=UserInfo.get("ACCT_TAG").toString().trim();
        		String IS_REAL_NAME="";
        		if(UserInfo.get("IS_REAL_NAME")!=null){
        			IS_REAL_NAME=UserInfo.get("IS_REAL_NAME").toString().trim();
        		}
        		
        		if(ACCT_TAG.equals("2")){
        			//该成员为未激活状态
        			 CSAppException.apperr(BofException.CRM_BOF_025);
        		}
        		if(IS_REAL_NAME.equals("0")){
        			//该成员未实名制状态
        			CSAppException.apperr(BofException.CRM_BOF_026);
        		}
        		
        	}
    	}
    	
    	IDataset infosDataset = RelaXxtInfoQry.queryMemInfoBySNandUserIdA(famSn,IDataUtil.getMandaData(condData, "USER_ID"));
    	String operType = IDataUtil.chkParam(condData, "OPER_CODE");
        if ("01".equals(operType)){
        	if (IDataUtil.isNotEmpty(infosDataset)&&infosDataset.size()>0){//已经办理了和校园不能再办理
        	    CSAppException.apperr(GrpException.CRM_GRP_140,famSn);
            }
        }

        //校验服务号码
        String serviceSn = batData.getString("DATA1", "");
        // 校验家长号码是否有效
        if (StringUtils.isBlank(serviceSn) || !serviceSn.startsWith("1") || serviceSn.length() != 11
                || !StringUtils.isNumeric(serviceSn)) {
            CSAppException.apperr(GrpException.CRM_GRP_842,serviceSn);
        }

        IData xxtParam = new DataMap();
        xxtParam.put("SERIAL_NUMBER_A", famSn);
        xxtParam.put("SERIAL_NUMBER_B", serviceSn);
        xxtParam.put("EC_USER_ID", ecuserid);
        IDataset relaxxtInfos = RelaXxtInfoQry.queryXxtInfoBySnaGroup(xxtParam);

        String groupId = IDataUtil.chkParam(condData, "GROUP_ID");
        IData httpData = new DataMap();



        for (int i = 0; i < relaxxtInfos.size(); i++) {
            IData info = relaxxtInfos.getData(i);

            String studentKey = info.getString("RSRV_STR1", "");

            if ("stu_name1".equals(studentKey) && !StringUtils.isBlank(batData.getString("DATA2"))) {
                CSAppException.apperr(GrpException.CRM_GRP_846,serviceSn,"学生一");
            }
            if ("stu_name2".equals(studentKey) && !StringUtils.isBlank(batData.getString("DATA3"))) {
                CSAppException.apperr(GrpException.CRM_GRP_846,serviceSn,"学生二");
            }
            if ("stu_name3".equals(studentKey) && !StringUtils.isBlank(batData.getString("DATA4"))) {
                CSAppException.apperr(GrpException.CRM_GRP_846,serviceSn,"学生三");
            }
        }
        //校验家长号码是否订购了和校园 end
        String discnt1 = batData.getString("DATA5");
        String discnt2 = batData.getString("DATA6");
        String discnt3 = batData.getString("DATA7");
        if(StringUtils.isNotBlank(discnt1)){
            IData discntinfo1 = DiscntInfoQry.getDiscntInfoByCode2(discnt1);
            String dm = discntinfo1.getString("MONTHS", "");
            String grpstr = discntinfo1.getString("RSRV_STR2", "");
            if(Integer.parseInt(dm)>1){
                CSAppException.apperr(GrpException.CRM_GRP_844,discntinfo1.getString("DISCNT_NAME"));//XX套餐不支持新和校园批量办理
            }
            if(StringUtils.isEmpty(grpstr)||!grpstr.contains("group_")){
                CSAppException.apperr(GrpException.CRM_GRP_746,discntinfo1.getString("DISCNT_CODE"));//排除非新和校园套餐
            }
            if(StringUtils.isEmpty(grpstr)||!grpstr.contains("group_1")){
                CSAppException.apperr(GrpException.CRM_GRP_873,"学生一","["+discntinfo1.getString("DISCNT_CODE")+"]"+discntinfo1.getString("DISCNT_NAME"));//学生一只能办理学生一对应套餐
            }
        }
        if(StringUtils.isNotBlank(discnt2)){
            IData discntinfo2 = DiscntInfoQry.getDiscntInfoByCode2(discnt2);
            String dm = discntinfo2.getString("MONTHS", "");
            String grpstr = discntinfo2.getString("RSRV_STR2", "");
            if(Integer.parseInt(dm)>1){
                CSAppException.apperr(GrpException.CRM_GRP_844,discntinfo2.getString("DISCNT_NAME"));//XX套餐不支持新和校园批量办理
            }
            if(StringUtils.isEmpty(grpstr)&&!grpstr.contains("group_")){
                CSAppException.apperr(GrpException.CRM_GRP_746,discntinfo2.getString("DISCNT_CODE"));//排除非新和校园套餐
            }
            if(StringUtils.isEmpty(grpstr)||!grpstr.contains("group_2")){
                CSAppException.apperr(GrpException.CRM_GRP_873,"学生二","["+discntinfo2.getString("DISCNT_CODE")+"]"+discntinfo2.getString("DISCNT_NAME"));//学生二只能办理学生二对应套餐
            }
        }
        if(StringUtils.isNotBlank(discnt3)){
            IData discntinfo3 = DiscntInfoQry.getDiscntInfoByCode2(discnt3);
            String dm = discntinfo3.getString("MONTHS", "");
            String grpstr = discntinfo3.getString("RSRV_STR2", "");
            if(Integer.parseInt(dm)>1){
                CSAppException.apperr(GrpException.CRM_GRP_844,discntinfo3.getString("DISCNT_NAME"));//XX套餐不支持新和校园批量办理
            }
            if(StringUtils.isEmpty(grpstr)&&!grpstr.contains("group_")){
                CSAppException.apperr(GrpException.CRM_GRP_746,discntinfo3.getString("DISCNT_CODE"));//排除非新和校园套餐
            }
            if(StringUtils.isEmpty(grpstr)||!grpstr.contains("group_3")){
                CSAppException.apperr(GrpException.CRM_GRP_873,"学生三","["+discntinfo3.getString("DISCNT_CODE")+"]"+discntinfo3.getString("DISCNT_NAME"));//学生三只能办理学生三对应套餐
            }
        }


        httpData.put("EC_ID", groupId);
        httpData.put("MOB_NUM", serviceSn);
        httpData.put("FEE_MOB_NUM", mainSn);
        httpData.put("OPR_CODE", "01");
        httpData.put("KIND_ID", "BIPXXT03_TX000002_0_0");

        if(!StringUtils.isBlank(batData.getString("DATA2")))
        {
            httpData.put("STU_NAME", batData.getString("DATA2"));
           // checkStudentInfoforXXT(httpData);
        }
        if(!StringUtils.isBlank(batData.getString("DATA3")))
        {
            httpData.put("STU_NAME", batData.getString("DATA3"));
          //  checkStudentInfoforXXT(httpData);
        }
        if(!StringUtils.isBlank(batData.getString("DATA4")))
        {
            httpData.put("STU_NAME", batData.getString("DATA4"));
           // checkStudentInfoforXXT(httpData);
        }


    }

    public void checkStudentInfoforXXT(IData httpData)throws Exception
    {
        IData httpStr = new DataMap();
        httpStr.put("EC_ID", httpData.getString("EC_ID"));
        httpStr.put("MOB_NUM", httpData.getString("MOB_NUM"));
        httpStr.put("FEE_MOB_NUM", httpData.getString("FEE_MOB_NUM"));
        httpStr.put("STU_NAME", httpData.getString("STU_NAME"));
        httpStr.put("OPR_CODE", httpData.getString("OPR_CODE"));
        httpStr.put("KIND_ID", "BIPXXT03_TX000002_0_0");

        String kindId = httpStr.getString("KIND_ID");
        log.info("test_guonj_BatNewXxtUserTrans3_start");
        log.info("test_guonj_BatNewXxtUserTrans3_param"+httpStr);
        IDataset result = GroupBatTransUtil.checkStuInfoForXXTPlat(httpStr);
        log.info("test_guonj_BatNewXxtUserTrans3_end");
        log.info("test_guonj_BatNewXxtUserTrans3_result="+httpData.getString("STU_NAME")+httpData.getString("MOB_NUM")+"."+result);
        if (IDataUtil.isEmpty(result))
        {
            CSAppException.apperr(GrpException.CRM_GRP_856);
        }
//        else if (!("00".equals(result.getData(0).getString("X_RESULTCODE", "")))) //和校园平台校验失败
//        {
//            String errInfo=httpData.getString("STU_NAME")+":"+result.getData(0).getString("X_RESULTINFO", "");
//            CSAppException.apperr(GrpException.CRM_GRP_855,errInfo);
//        }
    }

    protected void builderSvcData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());
        IData condData = batData.getData("condData", new DataMap());
        String operType = IDataUtil.chkParam(condData, "OPER_CODE");

        String stuName1 = batData.getString("DATA2");
        String stuName2 = batData.getString("DATA3");
        String stuName3 = batData.getString("DATA4");
        String discnt1 = batData.getString("DATA5");
        String discnt2 = batData.getString("DATA6");
        String discnt3 = batData.getString("DATA7");
        String discnt4="";
        if(("NEWXXTUSERREG_SPE").equals(svcData.getString("BATCH_OPER_TYPE")))
        {
            discnt4 = batData.getString("DATA9");
            
        }
        else 
        {
            discnt4 =  batData.getString("DATA8");
            
        }

        boolean isOutNet = condData.getBoolean("IS_OUT_NET", false);
        svcData.put("IF_SMS",true);//校讯通批量接口下发短信
        if (isOutNet){// 网外号码
        	CSAppException.apperr(BatException.CRM_BAT_79);
        }else{  //批量退订
            if("02".equals(operType)){
                svcData.put("USER_ID", IDataUtil.getMandaData(condData, "USER_ID"));// 集团USER_ID
                svcData.put("SERIAL_NUMBER", IDataUtil.getMandaData(batData, "SERIAL_NUMBER"));// 付费号码
                svcData.put("X_SUBTRANS_CODE", "");
                svcData.put("REMARK", "批量退订");
            }else{//批量新增
                //拼接学生参数
                IDataset resultset = new DatasetList();
                IData result = new DataMap();
                IDataset productParamAttrset = new DatasetList();
                IData productParamAttr1 = new DataMap();
                IData productParamAttr2 = new DataMap();
                IData productParamAttr3 = new DataMap();
                IData productParamAttr4 = new DataMap();
                IData productParamAttr5 = new DataMap();
                IDataset stuDataset = new DatasetList();

                productParamAttr1.put("ATTR_VALUE", "on");
                productParamAttr1.put("ATTR_CODE", "NOTIN_ctag0");
                productParamAttr2.put("ATTR_VALUE", IDataUtil.getMandaData(batData, "DATA1"));
                productParamAttr2.put("ATTR_CODE", "NOTIN_OUT_SN0");
                productParamAttr3.put("ATTR_VALUE", IDataUtil.getMandaData(batData, "SERIAL_NUMBER"));
                productParamAttr3.put("ATTR_CODE", "NOTIN_SERIAL_NUMBER");
                productParamAttr4.put("ATTR_VALUE", "01".equals(operType) ? "0":"02".equals(operType) ? "2":"1");
                productParamAttr4.put("ATTR_CODE", "NOTIN_OPER_TYPE0");
                productParamAttr5.put("ATTR_CODE", "NOTIN_STU_PARAM_LIST0");
                if(StringUtils.isNotBlank(stuName1)&&StringUtils.isNotBlank(discnt1)){
                    IData stuData1 = new DataMap();
                    stuData1.put("STUD_KEY", "stu_name1");
                    stuData1.put("STUD_NAME", IDataUtil.getMandaData(batData, "DATA2"));//学生一
                    stuData1.put("ELEMENT_ID", IDataUtil.getMandaData(batData, "DATA5"));//资费一
                    stuData1.put("tag", "0");
                    stuDataset.add(stuData1);
                }
                if(StringUtils.isNotBlank(stuName2)&&StringUtils.isNotBlank(discnt2)){
                    IData stuData2 = new DataMap();
                    stuData2.put("STUD_KEY", "stu_name2");
                    stuData2.put("STUD_NAME", IDataUtil.getMandaData(batData, "DATA3"));//学生二
                    stuData2.put("ELEMENT_ID", IDataUtil.getMandaData(batData, "DATA6"));//资费二
                    stuData2.put("tag", "0");
                    stuDataset.add(stuData2);
                }
                if(StringUtils.isNotBlank(stuName3)&&StringUtils.isNotBlank(discnt3)){
                    IData stuData3 = new DataMap();
                    stuData3.put("STUD_KEY", "stu_name3");
                    stuData3.put("STUD_NAME", IDataUtil.getMandaData(batData, "DATA4"));//学生三
                    stuData3.put("ELEMENT_ID", IDataUtil.getMandaData(batData, "DATA7"));//资费三
                    stuData3.put("tag", "0");
                    stuDataset.add(stuData3);
                }                    
                productParamAttr5.put("ATTR_VALUE", stuDataset);

                productParamAttrset.add(productParamAttr1);
                productParamAttrset.add(productParamAttr2);
                productParamAttrset.add(productParamAttr3);
                productParamAttrset.add(productParamAttr4);
                productParamAttrset.add(productParamAttr5);
                result.put("PRODUCT_ID", condData.getString("PRODUCT_ID","10009150"));
                result.put("PRODUCT_PARAM", productParamAttrset);
                resultset.add(result);
                //拼接服务参数
                IDataset elementinfoset = new DatasetList();
                IData elementinfo1 = new DataMap();
                IDataset attrparamset = new DatasetList();
                IData attrparam1 = new DataMap();
                IData attrparam2 = new DataMap();
                IData platsvc = new DataMap();
                attrparam1.put("PARAM_VERIFY_SUCC", "true");
                attrparam2.put("CANCLE_FLAG", "false");
                attrparam2.put("ID", "915001");
                platsvc.put("pam_BIZ_IN_CODE", "10657018002279");
                platsvc.put("pam_OPER_STATE", "01");
                platsvc.put("pam_SERVICE_ID", "915001");
                platsvc.put("pam_BIZ_CODE", "AHI3911602");
                platsvc.put("pam_BIZ_IN_CODE_A", "01");
                platsvc.put("pam_EXPECT_TIME", "");
                platsvc.put("pam_BIZ_ATTR", "1");
                platsvc.put("pam_PLAT_SYNC_STATE", "1");
                platsvc.put("pam_MODIFY_TAG", "0");
                platsvc.put("pam_BIZ_NAME", "和校园");
                platsvc.put("pam_GRP_PLAT_SYNC_STATE", "1");
                attrparam2.put("PLATSVC", platsvc);

                attrparamset.add(attrparam1);
                attrparamset.add(attrparam2);
                elementinfo1.put("INST_ID","");
                elementinfo1.put("START_DATE","");
                elementinfo1.put("ELEMENT_TYPE_CODE","S");
                elementinfo1.put("MODIFY_TAG","0");
                elementinfo1.put("PRODUCT_ID","915001");
                elementinfo1.put("END_DATE","");
                elementinfo1.put("PACKAGE_ID","91500101");
                elementinfo1.put("ELEMENT_ID","915001");
                elementinfo1.put("ATTR_PARAM",attrparamset);
                elementinfoset.add(elementinfo1);

                IData elementinfo2 = new DataMap();
                elementinfo2.put("INST_ID", "");
                elementinfo2.put("START_DATE", "");
                elementinfo2.put("ELEMENT_TYPE_CODE", "S");
                elementinfo2.put("MODIFY_TAG", "0");
                elementinfo2.put("PRODUCT_ID", "915001");
                elementinfo2.put("END_DATE", "");
                elementinfo2.put("PACKAGE_ID", "91500101");
                elementinfo2.put("ELEMENT_ID", "10002201");
                elementinfoset.add(elementinfo2);
                //拼接选择的资费参数
                if(StringUtils.isNotBlank(stuName1)&&StringUtils.isNotBlank(discnt1)){
                    IData discntData1 = new DataMap();
                    discntData1.put("INST_ID", "");
                    discntData1.put("START_DATE", "");
                    discntData1.put("ELEMENT_TYPE_CODE", "D");
                    discntData1.put("MODIFY_TAG", "0");
                    discntData1.put("PRODUCT_ID", "915001");
                    discntData1.put("END_DATE", "");
                    discntData1.put("PACKAGE_ID", "91500102");
                    discntData1.put("ELEMENT_ID", IDataUtil.getMandaData(batData, "DATA5"));
                    elementinfoset.add(discntData1);
                }
                if(StringUtils.isNotBlank(stuName2)&&StringUtils.isNotBlank(discnt2)){
                    IData discntData1 = new DataMap();
                    discntData1.put("INST_ID", "");
                    discntData1.put("START_DATE", "");
                    discntData1.put("ELEMENT_TYPE_CODE", "D");
                    discntData1.put("MODIFY_TAG", "0");
                    discntData1.put("PRODUCT_ID", "915001");
                    discntData1.put("END_DATE", "");
                    discntData1.put("PACKAGE_ID", "91500102");
                    discntData1.put("ELEMENT_ID", IDataUtil.getMandaData(batData, "DATA6"));
                    elementinfoset.add(discntData1);
                }
                if(StringUtils.isNotBlank(stuName3)&&StringUtils.isNotBlank(discnt3)){
                    IData discntData1 = new DataMap();
                    discntData1.put("INST_ID", "");
                    discntData1.put("START_DATE", "");
                    discntData1.put("ELEMENT_TYPE_CODE", "D");
                    discntData1.put("MODIFY_TAG", "0");
                    discntData1.put("PRODUCT_ID", "915001");
                    discntData1.put("END_DATE", "");
                    discntData1.put("PACKAGE_ID", "91500102");
                    discntData1.put("ELEMENT_ID", IDataUtil.getMandaData(batData, "DATA7"));
                    elementinfoset.add(discntData1);
                }
                if(StringUtils.isNotBlank(discnt4) && "5911".equals(discnt4))
                {
                    IData discntData1 = new DataMap();
                    discntData1.put("INST_ID", "");
                    discntData1.put("START_DATE", "");
                    discntData1.put("ELEMENT_TYPE_CODE", "D");
                    discntData1.put("MODIFY_TAG", "0");
                    discntData1.put("PRODUCT_ID", "915001");
                    discntData1.put("END_DATE", "");
                    discntData1.put("PACKAGE_ID", "91500102");
                    discntData1.put("ELEMENT_ID", "5911");
                    elementinfoset.add(discntData1);
                    
                }

                svcData.put("USER_ID", IDataUtil.getMandaData(condData, "USER_ID"));// 集团USER_ID
                svcData.put("SERIAL_NUMBER", IDataUtil.getMandaData(batData, "SERIAL_NUMBER"));// 付费号码
                svcData.put("PLAN_TYPE_CODE", "P");// 个人付款
                svcData.put("REMARK", "和校园批量导入");
                svcData.put("MEM_ROLE_B", "1");
                svcData.put("EFFECT_NOW", "true");
                svcData.put("ELEMENT_INFO", elementinfoset);
                svcData.put("PRODUCT_PARAM_INFO", resultset);// 产品参数
                String batchOperType = svcData.getString("BATCH_OPER_TYPE");
                if(batchOperType.equals("NEWXXTUSERREG_SPE")){
                    svcData.put("IS_SECCONFIRM_TAG", false);// 新和校园特殊批量不需要二次确认短信
                }else{
                    svcData.put("IS_SECCONFIRM_TAG", true);// 新和校园批量需要二次确认短信
                }
            }
        }
    }

    // 根据条件判断调用服务
    protected void setSVC(IData batData) throws Exception
    {
        String svcName = "";
        IData condData = batData.getData("condData", new DataMap());
        IData svcData = batData.getData("svcData", new DataMap());

        String operType = IDataUtil.chkParam(condData, "OPER_CODE");

        if ("01".equals(operType)){// 成员新增
            svcName = "CS.CreateGroupMemberSvc.createGroupMember";
        }else if ("02".equals(operType)){
            svcName = "CS.DestroyGroupMemberSvc.destroyGroupMember";
        }else{
            svcName = "CS.ChangeMemElementSvc.changeMemElement";
        }

        svcData.put("REAL_SVC_NAME", svcName);
    }
}
