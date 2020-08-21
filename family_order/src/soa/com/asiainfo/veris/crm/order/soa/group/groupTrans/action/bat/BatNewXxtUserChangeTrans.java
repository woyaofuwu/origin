
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.IntfPADCException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaXxtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat.util.GroupBatTransUtil;
import com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade.adcmas.DataUtil;

public class BatNewXxtUserChangeTrans implements ITrans
{
    private static Logger logger = Logger.getLogger(BatNewXxtUserChangeTrans.class);

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
        IData svcData = batData.getData("svcData", new DataMap());
        String mainSn = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 缴费号码
        String ecuserid = IDataUtil.getMandaData(condData, "USER_ID");
        String groupId = IDataUtil.chkParam(condData, "GROUP_ID");

        IData mebUserInfo = UcaInfoQry.qryUserInfoBySn(mainSn);

        String serviceSn = batData.getString("DATA1");
        String stuName1 = batData.getString("DATA2");
        String stuName2 = batData.getString("DATA3");
        String stuName3 = batData.getString("DATA4");
        String discnt1 = batData.getString("DATA5");
        String discnt2 = batData.getString("DATA6");
        String discnt3 = batData.getString("DATA7");
        String discnt4 = batData.getString("DATA8");
        String opercode = batData.getString("DATA9");// 0-新增 1-删除
        String mebUserId = mebUserInfo.getString("USER_ID");
        String platcode = "";
        boolean isParent = false;

        // 校验缴费号码填写的是否为空
        if (StringUtils.isBlank(mainSn) || !mainSn.startsWith("1") || mainSn.length() != 11 || !StringUtils.isNumeric(mainSn))
        {
            CSAppException.apperr(BatException.CRM_BAT_87);
        }

        // 校验缴费号码必须是移动号码
        IData userGrpInfo = UcaInfoQry.qryUserMainProdInfoBySn(mainSn);
        if (IDataUtil.isEmpty(userGrpInfo))
        {// 网外号码
            CSAppException.apperr(BatException.CRM_BAT_79);
        }

        // 校验选择的不能是包年或者包半年套餐(校讯通批量当时上线时的规定)
        if (StringUtils.isNotBlank(discnt1))
        {
            IData discntinfo1 = DiscntInfoQry.getDiscntInfoByCode2(discnt1);
            String dm = discntinfo1.getString("MONTHS", "");
            String grpstr = discntinfo1.getString("RSRV_STR2", "");
            if (Integer.parseInt(dm) > 1)
            {
                CSAppException.apperr(GrpException.CRM_GRP_844, discntinfo1.getString("DISCNT_NAME"));// XX套餐不支持新校讯通批量办理
            }
            if (StringUtils.isEmpty(grpstr) || !grpstr.contains("group_"))
            {
                CSAppException.apperr(GrpException.CRM_GRP_746, discntinfo1.getString("DISCNT_CODE"));// 排除非新校讯通套餐
            }
            if (StringUtils.isEmpty(grpstr) || !grpstr.contains("group_1"))
            {
                CSAppException.apperr(GrpException.CRM_GRP_873, "学生一", "[" + discntinfo1.getString("DISCNT_CODE") + "]" + discntinfo1.getString("DISCNT_NAME"));// 学生一只能办理学生一对应套餐
            }
        }
        if (StringUtils.isNotBlank(discnt2))
        {
            IData discntinfo2 = DiscntInfoQry.getDiscntInfoByCode2(discnt2);
            String dm = discntinfo2.getString("MONTHS", "");
            String grpstr = discntinfo2.getString("RSRV_STR2", "");
            if (Integer.parseInt(dm) > 1)
            {
                CSAppException.apperr(GrpException.CRM_GRP_844, discntinfo2.getString("DISCNT_NAME"));// XX套餐不支持新校讯通批量办理
            }
            if (StringUtils.isEmpty(grpstr) || !grpstr.contains("group_"))
            {
                CSAppException.apperr(GrpException.CRM_GRP_746, discntinfo2.getString("DISCNT_CODE"));// 排除非新校讯通套餐
            }
            if (StringUtils.isEmpty(grpstr) || !grpstr.contains("group_2"))
            {
                CSAppException.apperr(GrpException.CRM_GRP_873, "学生二", "[" + discntinfo2.getString("DISCNT_CODE") + "]" + discntinfo2.getString("DISCNT_NAME"));// 学生二只能办理学生二对应套餐
            }
        }
        if (StringUtils.isNotBlank(discnt3))
        {
            IData discntinfo3 = DiscntInfoQry.getDiscntInfoByCode2(discnt3);
            String dm = discntinfo3.getString("MONTHS", "");
            String grpstr = discntinfo3.getString("RSRV_STR2", "");
            if (Integer.parseInt(dm) > 1)
            {
                CSAppException.apperr(GrpException.CRM_GRP_844, discntinfo3.getString("DISCNT_NAME"));// XX套餐不支持新校讯通批量办理
            }
            if (StringUtils.isEmpty(grpstr) || !grpstr.contains("group_"))
            {
                CSAppException.apperr(GrpException.CRM_GRP_746, discntinfo3.getString("DISCNT_CODE"));// 排除非新校讯通套餐
            }
            if (StringUtils.isEmpty(grpstr) || !grpstr.contains("group_3"))
            {
                CSAppException.apperr(GrpException.CRM_GRP_873, "学生三", "[" + discntinfo3.getString("DISCNT_CODE") + "]" + discntinfo3.getString("DISCNT_NAME"));// 学生三只能办理学生三对应套餐
            }
        }

        // 校验集团是否订购了校讯通服务
        IDataset userPlatsvc = UserGrpPlatSvcInfoQry.getUserGrpPlatSvcByUserIdSvcID(ecuserid, "100022");
        if (userPlatsvc.isEmpty())
        {
            CSAppException.apperr(IntfPADCException.CRM_SAGM_10);

        }
        IData pltSvc = userPlatsvc.getData(0);

        // 校验缴费号码是否订购了这个集团的校讯通业务
        boolean isBB = DataUtil.isSnExistBB(ecuserid, mebUserId, batData.getString("PRODUCT_ID", ""));
        if (!isBB)
        {
            CSAppException.apperr(GrpException.CRM_GRP_144, mainSn);

        }
        // 校验家长号码是否有效
        if (StringUtils.isBlank(serviceSn) || !serviceSn.startsWith("1") || serviceSn.length() != 11 || !StringUtils.isNumeric(serviceSn))
        {
            CSAppException.apperr(GrpException.CRM_GRP_845, serviceSn);
        }
        // 如果是删除套餐，但是uu表没有关系，则报错
        boolean isUU = DataUtil.isOutSnExistUU(serviceSn, mebUserId, ecuserid);// 异网号码和计费号码是否存在UU关系
        if (!isUU && "1".equals(opercode))
        {
            CSAppException.apperr(IntfPADCException.CRM_SAGM_06);

        }

        // 校验对应的学生套餐是否已经被其他学校的同一个计费号码订购过
        IData xxtParam = new DataMap();
        xxtParam.put("SERIAL_NUMBER_A", mainSn);
        xxtParam.put("SERIAL_NUMBER_B", serviceSn);
        xxtParam.put("EC_USER_ID", ecuserid);
        IDataset relaxxtInfos = RelaXxtInfoQry.queryXxtInfoBySnaGroup(xxtParam);
        IDataset paramset = new DatasetList();
        IDataset Stuparamset = new DatasetList();

        for (int i = 0; i < relaxxtInfos.size(); i++)
        {
            IData info = relaxxtInfos.getData(i);

            String studentKey = info.getString("RSRV_STR1", "");

            if ("0".equals(opercode))
            {
                if ("stu_name1".equals(studentKey) && !StringUtils.isBlank(batData.getString("DATA2")))
                {
                    CSAppException.apperr(GrpException.CRM_GRP_846, serviceSn, "学生一");
                }
                if ("stu_name2".equals(studentKey) && !StringUtils.isBlank(batData.getString("DATA3")))
                {
                    CSAppException.apperr(GrpException.CRM_GRP_846, serviceSn, "学生二");
                }
                if ("stu_name3".equals(studentKey) && !StringUtils.isBlank(batData.getString("DATA4")))
                {
                    CSAppException.apperr(GrpException.CRM_GRP_846, serviceSn, "学生三");
                }
            }
        }

        IDataset relaxxtInfoSs = RelaXxtInfoQry.queryMemInfoByOutSnMebUserIdGrpUserId(serviceSn, mainSn, ecuserid);
        for (int i = 0; i < relaxxtInfoSs.size(); i++)
        {
            IData info = relaxxtInfoSs.getData(i);
            String studentKey = info.getString("RSRV_STR1", "");
            String studentdiscnt = info.getString("ELEMENT_ID", "");
            String studentName = info.getString("NAME", "");
            IData Stuparam = new DataMap();
            Stuparam.put(studentKey, studentName);
            Stuparam.put("discnt", studentdiscnt);
            Stuparamset.add(Stuparam);
        }

        if (StringUtils.isNotBlank(discnt1) && StringUtils.isNotBlank(stuName1))
        {
            // 如果是退订，需要校验对应的学生名字和订购的套餐是否正确
            if ("1".equals(opercode))
            {
                IDataset dctDatasetAdd = DataHelper.filter(relaxxtInfoSs, "ELEMENT_ID=" + discnt1 + ",NAME=" + stuName1);
                if (IDataUtil.isEmpty(dctDatasetAdd))
                {
                    CSAppException.apperr(GrpException.CRM_GRP_878, serviceSn, "学生一", discnt1);

                }
            }
            IData paramdata = new DataMap();
            paramdata.put("stu_name1", stuName1);
            paramdata.put("discnt", discnt1);
            paramset.add(paramdata);
        }
        if (StringUtils.isNotBlank(discnt2) && StringUtils.isNotBlank(stuName2))
        {
            if ("1".equals(opercode))
            {
                IDataset dctDatasetAdd = DataHelper.filter(relaxxtInfoSs, "ELEMENT_ID=" + discnt2 + ",NAME=" + stuName2);
                if (IDataUtil.isEmpty(dctDatasetAdd))
                {
                    CSAppException.apperr(GrpException.CRM_GRP_878, serviceSn, "学生二", discnt2);

                }
            }
            IData paramdata = new DataMap();
            paramdata.put("stu_name2", stuName2);
            paramdata.put("discnt", discnt2);
            paramset.add(paramdata);
        }
        if (StringUtils.isNotBlank(discnt3) && StringUtils.isNotBlank(stuName3))
        {
            if ("1".equals(opercode))
            {
                IDataset dctDatasetAdd = DataHelper.filter(relaxxtInfoSs, "ELEMENT_ID=" + discnt3 + ",NAME=" + stuName3);
                if (IDataUtil.isEmpty(dctDatasetAdd))
                {
                    CSAppException.apperr(GrpException.CRM_GRP_878, serviceSn, "学生三", discnt3);

                }
            }
            IData paramdata = new DataMap();
            paramdata.put("stu_name3", stuName3);
            paramdata.put("discnt", discnt3);
            paramset.add(paramdata);
        }
        // 如果xxt表的数据数量与用户填写的套餐数目一致，切用户选择的是删除，则相当于平台编码02，退订
        if ("1".equals(opercode) && paramset.size() == relaxxtInfoSs.size())
        {
            platcode = "02";

        }
        // 如果用户选择的是订购，但是uu表里面没有数据，则相当于新加一个家长号码，则相当于平台编码01，订购
        else if ("0".equals(opercode) && !isUU)
        {
            platcode = "01";

        }
        else
        {
            platcode = "05";

        }
        if (StringUtils.isNotBlank(discnt1) && StringUtils.isNotBlank(stuName1))
        {
            IData paramdata = new DataMap();
            paramdata.put("stu_name1", stuName1);
            paramdata.put("discnt", discnt1);
            if ("0".equals(opercode))
            {
                // 如果对应的学生已经订购了套餐，新增时认为是删除旧的套餐，增加新的套餐
                if ("05".equals(platcode))
                {
                    IDataset distinctinfo = filterByEqualsCol(Stuparamset, "stu_name1", paramset, "stu_name1");
                    for (int j = 0; j <= distinctinfo.size() - 1; j++)
                    {
                        Stuparamset.remove(distinctinfo.getData(j));

                    }

                }
                Stuparamset.add(paramdata);
            }
            else if ("1".equals(opercode))
            {
                if (!"02".equals(platcode))
                {
                    Stuparamset.remove(paramdata);
                }

            }
        }
        if (StringUtils.isNotBlank(discnt2) && StringUtils.isNotBlank(stuName2))
        {
            IData paramdata = new DataMap();
            paramdata.put("stu_name2", stuName2);
            paramdata.put("discnt", discnt2);
            if ("0".equals(opercode) )
            {
                // 如果对应的学生已经订购了套餐，新增时认为是删除旧的套餐，增加新的套餐
                if ("05".equals(platcode))
                {
                    IDataset distinctinfo = filterByEqualsCol(Stuparamset, "stu_name2", paramset, "stu_name2");
                    for (int j = 0; j <= distinctinfo.size() - 1; j++)
                    {
                        Stuparamset.remove(distinctinfo.getData(j));

                    }

                }
                Stuparamset.add(paramdata);
            }
            else
            {
                if (!"02".equals(platcode))
                {
                    Stuparamset.remove(paramdata);
                }

            }
        }
        if (StringUtils.isNotBlank(discnt3) && StringUtils.isNotBlank(stuName3))
        {
            IData paramdata = new DataMap();
            paramdata.put("stu_name3", stuName3);
            paramdata.put("discnt", discnt3);
            if ("0".equals(opercode) )
            {
                // 如果对应的学生已经订购了套餐，新增时认为是删除旧的套餐，增加新的套餐
                if ("05".equals(platcode))
                {
                    IDataset distinctinfo = filterByEqualsCol(Stuparamset, "stu_name3", paramset, "stu_name3");
                    for (int j = 0; j <= distinctinfo.size() - 1; j++)
                    {
                        Stuparamset.remove(distinctinfo.getData(j));

                    }

                }
                Stuparamset.add(paramdata);
            }
            else
            {
                if (!"02".equals(platcode))
                {
                    Stuparamset.remove(paramdata);
                }

            }
        }
        // 校验对应的学生信息是否在信通测登记
        IData httpData = new DataMap();
        httpData.put("EC_ID", groupId);
        httpData.put("MOB_NUM", serviceSn);
        httpData.put("FEE_MOB_NUM", mainSn);
        httpData.put("OPR_CODE", "01"); // 新增
        httpData.put("KIND_ID", "BIPXXT03_TX000002_0_0");

        if (!StringUtils.isBlank(batData.getString("DATA2")))
        {
            httpData.put("STU_NAME", batData.getString("DATA2"));
            checkStudentInfoforXXT(httpData);
        }
        if (!StringUtils.isBlank(batData.getString("DATA3")))
        {
            httpData.put("STU_NAME", batData.getString("DATA3"));
            checkStudentInfoforXXT(httpData);
        }
        if (!StringUtils.isBlank(batData.getString("DATA4")))
        {
            httpData.put("STU_NAME", batData.getString("DATA4"));
            checkStudentInfoforXXT(httpData);
        }
        StringBuilder Pointcode = new StringBuilder();
        for (int i = 0; i <= Stuparamset.size() - 1; i++)
        {
            IData Stuparam = Stuparamset.getData(i);
            if (StringUtils.isNotBlank(Stuparam.getString("stu_name1")))
            {
                svcData.put("stu_name1", Stuparam.getString("stu_name1"));

            }
            if (StringUtils.isNotBlank(Stuparam.getString("stu_name2")))
            {
                svcData.put("stu_name2", Stuparam.getString("stu_name2"));

            }
            if (StringUtils.isNotBlank(Stuparam.getString("stu_name3")))
            {
                svcData.put("stu_name3", Stuparam.getString("stu_name3"));

            }
            if (!Pointcode.toString().contains(Stuparam.getString("discnt")))
            {
                Pointcode.append(Stuparam.getString("discnt"));
                if (i != Stuparamset.size() - 1)
                {
                    Pointcode.append("||");
                }
            }

        }

        // 家长套餐的处理,如果用户已经订购了这个套餐，继续订购时报错

        IDataset userInfoDiscnts = UserDiscntInfoQry.getAllUserDiscntByUserIdUserIdA(mebUserId, ecuserid);
        IDataset newDiscnts = DataHelper.filter(userInfoDiscnts, "DISCNT_CODE=" + "5911");
        if (IDataUtil.isNotEmpty(newDiscnts))
        {
            isParent= true;
        }
        if (StringUtils.isNotBlank(discnt4) && "5911".equals(discnt4))
        {
            if("0".equals(opercode))
            { 
                if("0".equals(Stuparamset.size()))
                {
                    CSAppException.apperr(GrpException.CRM_GRP_881);
                    
                }
                else
                {
                    Pointcode.append("||");
                    Pointcode.append("5911");
                }
                
            }
            else if ("1".equals(opercode))
            { 
                if(!isParent)
                {
                    CSAppException.apperr(GrpException.CRM_GRP_882);
                    
                }
                else if("02".equals(platcode))
                {
                    Pointcode.append("||");
                    Pointcode.append("5911");
                }
                
            }
            
        }
        else if(StringUtils.isBlank(discnt4))
        {
            if(isParent && !"02".equals(platcode))
            {
                Pointcode.append("||");
                Pointcode.append("5911");
                
            }
            
        }
        //教师套餐的处理
        
        IDataset discntsList = DataUtil.dealDiscnts(Pointcode.toString());
        if(discntsList.contains("5912") && !"1".equals(discntsList.size()))
        {
            CSAppException.apperr(GrpException.CRM_GRP_883);
            
        }
        
        svcData.put("BUSI_SIGN", "BIPXXT05_TX100005_1_0");
        svcData.put("MOB_NUM", serviceSn);
        svcData.put("ECID", groupId);
        svcData.put("FEE_MOB_NUM", mainSn);
        svcData.put("OPR_CODE", platcode);

        svcData.put("POINT_CODE", Pointcode.toString());
        svcData.put("BIZ_SERV_CODE", pltSvc.getString("SERV_CODE"));

    }

    public void checkStudentInfoforXXT(IData httpData) throws Exception
    {
        IData httpStr = new DataMap();
        httpStr.put("EC_ID", httpData.getString("EC_ID"));
        httpStr.put("MOB_NUM", httpData.getString("MOB_NUM"));
        httpStr.put("FEE_MOB_NUM", httpData.getString("FEE_MOB_NUM"));
        httpStr.put("STU_NAME", httpData.getString("STU_NAME"));
        httpStr.put("OPR_CODE", httpData.getString("OPR_CODE"));
        httpStr.put("KIND_ID", "BIPXXT03_TX000002_0_0");

        IDataset result = GroupBatTransUtil.checkStuInfoForXXTPlat(httpStr);
        if (IDataUtil.isEmpty(result))
        {
            CSAppException.apperr(GrpException.CRM_GRP_856);
        }
        else if (!("00".equals(result.getData(0).getString("X_RESULTCODE", "")))) // 校讯通平台校验失败
        {
            String errInfo = httpData.getString("STU_NAME") + ":" + result.getData(0).getString("X_RESULTINFO", "");
            CSAppException.apperr(GrpException.CRM_GRP_855, errInfo);
        }
    }

    protected void builderSvcData(IData batData) throws Exception
    {
    }

    // 根据条件判断调用服务
    protected void setSVC(IData batData) throws Exception
    {
        String svcName = "";
        IData svcData = batData.getData("svcData", new DataMap());
        svcName = "SS.TcsGrpIntfSVC.dealAdcMasMemBiz";
        svcData.put("REAL_SVC_NAME", svcName);
        svcData.put(Route.ROUTE_EPARCHY_CODE, "0898");
    }

    /**
     * 根据传入的2个col，比较两个IDataset对应col相同的IData 返回oriList中符合条件的数据
     * 
     * @param oriList
     * @param col
     * @param compareList
     * @param compareCol
     * @return
     * @throws Exception
     */
    public static IDataset filterByEqualsCol(IDataset oriList, String col1, IDataset compareList, String compareCol1) throws Exception
    {
        IDataset rtList = new DatasetList();
        for (int i = 0, size = oriList.size(); i < size; i++)
        {
            IData oriData = oriList.getData(i);
            String value1 = oriData.getString(col1);
            if (StringUtils.isBlank(value1))
            {
                continue;
            }
            for (int j = 0, size2 = compareList.size(); j < size2; j++)
            {
                IData compareData = compareList.getData(j);
                String compareValue1 = compareData.getString(compareCol1);
                if (value1.equals(compareValue1))
                {
                    rtList.add(oriData);
                }
            }
        }

        return rtList;
    }
}
