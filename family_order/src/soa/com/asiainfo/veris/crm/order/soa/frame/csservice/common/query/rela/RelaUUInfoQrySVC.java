
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class RelaUUInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 依据user_id_a/relation_type_code 查询所有成员
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset getAllMebByUSERIDA(IData inparam) throws Exception
    {
        IDataset data = RelaUUInfoQry.getAllMebByUSERIDA(inparam.getString("USER_ID_A", ""), inparam.getString("RELATION_TYPE_CODE", ""));
        return data;
    }

    /**
     * 依据trade_id 查询子用户 chenyi
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset getUserRelationByTradeId(IData inparam) throws Exception
    {
        IDataset data = RelaUUInfoQry.getUserRelationByTradeId(inparam.getString("TRADE_ID"));
        return data;
    }

    /**
     * @author xunyl
     * @date 2013-03-14
     * @discription 根据tab_name,sql_ref,eparchy_code查询uu信息
     */
    public static IDataset getUUInfoForGrp(IData inparams) throws Exception
    {
        return RelaUUInfoQry.getUUInfoForGrp(inparams);
    }

    /*
     * ADC校讯通 异网号码UU关系查询 liaolc 2014-04-17
     */
    public static IDataset getXXTRelation(IData input) throws Exception
    {
        String mebMainNumber = input.getString("SERIAL_NUMBER", "");
        String mebUserId = input.getString("USER_ID_B", "");
        String roleCodeA = input.getString("ROLE_CODE_A", "");
        String roleCodeB = input.getString("ROLE_CODE_B", "");
        String relationTypeCode = input.getString("RELATION_TYPE_CODE", "");// 注销姓名
        String ecUserid = input.getString("EC_USER_ID", "");// 注销姓名

        IDataset result = RelaUUInfoQry.getXXTRelation(mebMainNumber, mebUserId, roleCodeA, roleCodeB, relationTypeCode,ecUserid);

        return result;
    }

    /*
     * @description 根据商品用户ID和产品用户ID查询UU关系表 @author xunyl @date 2013-03-18
     */
    public static IDataset qryUU(IData param) throws Exception
    {
        return RelaUUInfoQry.qryUU(param.getString("USER_ID_A", ""), param.getString("USER_ID_B", ""), param.getString("RELATION_TYPE_CODE", ""), null, null);
    }

    public IDataset checkMemRelaByUserIdb(IData input) throws Exception
    {
        String user_id_a = input.getString("USER_ID_A", "");
        String user_id_b = input.getString("USER_ID_B", "");
        String relation_type_code = input.getString("RELATION_TYPE_CODE", "");
        IDataset data = RelaUUInfoQry.checkMemRelaByUserIdb(user_id_a, user_id_b, relation_type_code, null);
        return data;
    }

    /**
     * 查询集团用户下的使用某短号的订购关系信息
     * 
     * @param iData
     * @return
     * @throws Exception
     */
    public IDataset chkShortCodeByUserIdAAndShortCodeAllCrm(IData iData) throws Exception
    {
        String userIdA = iData.getString("USER_ID_A");
        String shortCode = iData.getString("SHORT_CODE");
        String relaTypeCode = iData.getString("RELATION_TYPE_CODE");

        return RelaUUInfoQry.chkShortCodeByUserIdAAndShortCodeAllCrm(userIdA, shortCode, relaTypeCode);
    }

    /**
     * @author：SongYingli
     * @time：Mar 5, 2013 8:55:22 PM
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset fn_isVpmnMEM(IData input) throws Exception
    {
        String user_id_a = input.getString("USER_ID_A", "");
        String user_id_b = input.getString("USER_ID_B", "");
        IDataset data = RelaUUInfoQry.fn_isVpmnMEM(user_id_a, user_id_b, null);
        return data;
    }

    public IDataset getAllRelaUUInfoByUserIda(IData input) throws Exception
    {
        IDataset data = RelaUUInfoQry.getAllRelaUUInfoByUserIda(input.getString("USER_ID_A", ""), input.getString("RELATION_TYPE_CODE", ""), null);
        return data;
    }

    public IDataset getBBInfoByUserIdAB(IData input) throws Exception
    {
        String userIdA = input.getString("USER_ID_A", "");
        String userIdB = input.getString("USER_ID_B", "");

        IDataset data = RelaBBInfoQry.getBBInfoByUserIdAB(userIdA, userIdB);
        return data;
    }

    public IDataset getGrpMebBySNUIdA(IData param) throws Exception
    {
        String userIdA = param.getString("USER_ID_A");
        String serialNumber = param.getString("SERIAL_NUMBER");
        return RelaUUInfoQry.getGrpMebBySNUIdA(userIdA, serialNumber, null, null);
    }

    /**
     * 查询集团用户的网外信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getGrpOutinfo(IData param) throws Exception
    {
        String user_id_a = param.getString("USER_ID_A", "");
        return RelaUUInfoQry.getGrpOutinfo(user_id_a, getPagination());
    }

    /**
     * 查询成员网外号码UU关系
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getMemOutGrpNumber(IData param) throws Exception
    {
        String user_id_a = param.getString("USER_ID_A", "");
        String user_id_b = param.getString("USER_ID_B", "");
        String relation_type_code = param.getString("RELATION_TYPE_CODE", "");
        return RelaUUInfoQry.qryUU(user_id_a, user_id_b, relation_type_code, getPagination());
    }

    public IDataset getMotherVpmnShortCode(IData param) throws Exception
    {
        return RelaUUInfoQry.getMotherVpmnShortCode(param, this.getPagination());
    }

    /**
     * 通过USER_ID_B、RELATION_TYPE_CODE查询用户关系表
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getRelaByUserIdAndRelaTypeCode(IData param) throws Exception
    {
        String userIdB = param.getString("USER_ID");
        String relationTypeCode = param.getString("RELATION_TYPE_CODE");
        return RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userIdB, relationTypeCode);
    }

    /**
     * 根据USER_ID_B、RELATION_TYPE_CODE获取关系表数据
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getRelaByUserIdB(IData input) throws Exception
    {
        IDataset data = RelaUUInfoQry.qryRelaByUserIdB(input.getString("USER_ID_B"), null);
        return data;
    }

    public IDataset getRelaCoutByPK(IData input) throws Exception
    {
        String userIdA = input.getString("USER_ID_A", "");
        String relationTypeCode = input.getString("RELATION_TYPE_CODE", "");
        IDataset data = RelaUUInfoQry.getRelaCoutByPK(userIdA, relationTypeCode);
        return data;
    }

    public IDataset getRelaFKByUserIdB(IData input) throws Exception
    {
        String userIdA = input.getString("USER_ID_B", "");
        String relationTypeCode = input.getString("RELATION_TYPE_CODE", "");
        String roleCodeB = input.getString("ROLE_CODE_B", "");
        IDataset data = RelaUUInfoQry.getRelaFKByUserIdB(userIdA, relationTypeCode, roleCodeB);
        return data;
    }

    public IDataset getRelatInfosBySelUserIdA(IData input) throws Exception
    {
        String user_id_b = input.getString("USER_ID_B");
        String relation_type_code = input.getString("RELATION_TYPE_CODE");
        String role_code_b = input.getString("ROLE_CODE_B");
        return RelaUUInfoQry.getRelatInfosBySelUserIdA(user_id_b, relation_type_code, role_code_b);
    }

    /*	*//**
     * 根据USER_ID_B、RELATION_TYPE_CODE获取关系表数据
     * 
     * @param input
     * @return
     * @throws Exception
     */
    /*
     * public IDataset getRelationUU(IData input) throws Exception { IDataset data = RelaUUInfoQry.getRelationUU(input);
     * return data; }
     */
    public IDataset getRelationUU(IData input) throws Exception
    {
        IDataset data = RelaUUInfoQry.getRelationUU(input);
        return data;
    }

    /**
     * 获取集团\成员对应的网外关系
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getRelationUUbYUserIDa(IData input) throws Exception
    {
        String user_id_a = input.getString("USER_ID_A", "");
        String user_id_b = input.getString("USER_ID_B", "");
        String serial_number_b = input.getString("SERIAL_NUMBER_B", "");
        String relation_type = input.getString("RELATION_TYPE", "");
        IDataset data = RelaUUInfoQry.getRelationUUbYUserIDa(user_id_a, user_id_b, serial_number_b, relation_type, null);
        return data;
    }

    /**
     * 初始网外关系信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getRelationUUbYUserIDa2(IData input) throws Exception
    {
        IDataset data = RelaUUInfoQry.getRelationUUbYUserIDa2(input.getString("USER_ID", ""), null);
        return data;
    }

    // 查询该副卡号码的relation_uu记录
    public IDataset getRelationUUInfoByDeputySn(IData input) throws Exception
    {
        IDataset data = RelaUUInfoQry.getRelationUUInfoByDeputySn(input.getString("USER_ID_B", ""), input.getString("RELATION_TYPE_CODE", ""), null);
        return data;
    }

    /**
     * 查询RELATION_UU表的关系 根据SERIAL_NUMBER_B
     * 
     * @param iData
     * @return
     * @throws Exception
     */
    public IDataset getRelatsBySNB(IData iData) throws Exception
    {

        String serialNumberB = iData.getString("SERIAL_NUMBER_B");

        return RelaUUInfoQry.getRelatsBySNB(serialNumberB);
    }

    /**
     * @author fuzn
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getRelaUUByUserRoleA(IData input) throws Exception
    {
        IDataset data = RelaUUInfoQry.getSEL_USER_ROLEA(input.getString("USER_ID_A", ""), input.getString("ROLE_CODE_B", ""), input.getString("RELATION_TYPE_CODE", ""), null);
        return data;
    }

    /**
     * 根据user_id_a、role_code_b、relation_type_code查uu关系
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getRelaUUByUserRoleACount(IData input) throws Exception
    {
        String user_id_a = input.getString("USER_ID_A", "");
        String role_code_b = input.getString("ROLE_CODE_B", "");
        String relation_type_code = input.getString("RELATION_TYPE_CODE", "");
        IDataset data = RelaUUInfoQry.getUserRoleACount(user_id_a, role_code_b, relation_type_code, null);
        return data;
    }

    public IDataset getRelaUUInfoByRolForGrp(IData idata) throws Exception
    {
        return RelaUUInfoQry.getRelaUUInfoByRolForGrp(idata.getString("USER_ID_B", ""), idata.getString("RELATION_TYPE_CODE", ""), null);
    }

    /**
     * 根据userIda和relation_type_code查询
     * 
     * @author fuzn
     * @date 2013-03-18
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getRelaUUInfoByUserida(IData input) throws Exception
    {
        IDataset data = RelaUUInfoQry.getRelaUUInfoByUserIda(input.getString("USER_ID_A", ""), input.getString("RELATION_TYPE_CODE", ""), null);
        return data;
    }

    public IDataset getRelaUUInfoByUserIdA(IData input) throws Exception
    {
        String user_id_a = input.getString("USER_ID_A");
        String relation_type_code = input.getString("RELATION_TYPE_CODE");
        return RelaUUInfoQry.getRelaUUInfoByUserIdA(user_id_a, relation_type_code);
    }

    public IDataset getRelaUUInfoByUserIdaForGrp(IData input) throws Exception
    {

        String userIdA = input.getString("USER_ID_A");
        String relationTypeCode = input.getString("RELATION_TYPE_CODE");

        IDataset data = RelaUUInfoQry.getRelaUUInfoByUserIdaForGrp(userIdA, relationTypeCode, null);
        return data;
    }

    /**
     * 根据USER_ID_B查询UU关系
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public IDataset getRelaUUInfoByUseridB(IData idata) throws Exception
    {
        String eparchyCode = idata.getString(Route.ROUTE_EPARCHY_CODE, "");
        String user_id_b = idata.getString("USER_ID_B", "");
        IDataset data = RelaUUInfoQry.getRelaUUInfoByUseridB(user_id_b, eparchyCode, null);
        return data;
    }

    public IDataset getRelaUUInfoByUserIdBAndRelaTypeCode(IData input) throws Exception
    {
        String userIdB = input.getString("USER_ID_B");
        String relaTypeCode = input.getString("RELATION_TYPE_CODE");
        return RelaUUInfoQry.getRelaUUInfoByUserIdBAndRelaTypeCode(userIdB, relaTypeCode);
    }

    public IDataset getRelaUUInfoByUserIdBAndSDate(IData input) throws Exception
    {
        String userIdB = input.getString("USER_ID_B");
        String relaTypeCode = input.getString("RELATION_TYPE_CODE");
        String startDate = input.getString("START_DATE");
        String endDate = input.getString("END_DATE");
        return RelaUUInfoQry.getRelaUUInfoByUserIdBAndSDate(userIdB, relaTypeCode, startDate, endDate);
    }

    /**
     * 根据USER_ID_B查询UU关系
     * 
     * @author fuzn
     * @date 2013-03-20
     * @param idata
     * @return
     * @throws Exception
     */
    public IDataset getRelaUUInfoByUserRelarIdB(IData idata) throws Exception
    {
        IDataset data = RelaUUInfoQry.getRelaUUInfoByUserRelarIdB(idata.getString("USER_ID_B", ""), idata.getString("RELATION_TYPE_CODE", ""), null);
        return data;
    }

    /**
     * 根据主键查询UU表
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getSEL_BY_PK(IData input) throws Exception
    {
        String user_id_a = input.getString("USER_ID_A");
        String user_id_b = input.getString("USER_ID_B");
        String relation_type_code = input.getString("RELATION_TYPE_CODE");

        IDataset data = RelaUUInfoQry.qryUU(user_id_a, user_id_b, relation_type_code, null);
        return data;
    }

    public IDataset getUserOrderInCurMonthByUserIdB(IData input) throws Exception
    {
        String userIdB = input.getString("USER_ID_B");
        String relaTypeCode = input.getString("RELATION_TYPE_CODE");
        return RelaUUInfoQry.getUserOrderInCurMonthByUserIdB(userIdB, relaTypeCode);
    }

    public IDataset getUUByUserIdAB(IData input) throws Exception
    {

        String userIdA = input.getString("USER_ID_A", "");
        String userIdB = input.getString("USER_ID_B", "");
        String relationTypeCode = input.getString("RELATION_TYPE_CODE");
        String roleCodeB = input.getString("ROLE_CODE_B", "");
        String eparchycode = input.getString(Route.ROUTE_EPARCHY_CODE, this.getRouteId());

        IDataset data = RelaUUInfoQry.getUUByUserIdAB(userIdA, userIdB, roleCodeB, relationTypeCode, null, eparchycode);
        return data;
    }

    public IDataset getUUInfoByUserIdAB(IData input) throws Exception
    {
        String userIdA = input.getString("USER_ID_A", "");
        String userIdB = input.getString("USER_ID_B", "");
        String relationTypeCode = input.getString("RELATION_TYPE_CODE");

        IDataset data = RelaUUInfoQry.getUUInfoByUserIdAB(userIdA, userIdB, relationTypeCode, null);
        return data;
    }

    public IDataset getUUInfoByUserIdB(IData input) throws Exception
    {
        String userIdB = input.getString("USER_ID_B", "");
        IDataset dataSet = RelaUUInfoQry.getUUInfoByUserIdB(userIdB);
        return dataSet;
    }

    public IDataset getVpmnShortCode(IData param) throws Exception
    {
        return RelaUUInfoQry.getVpmnShortCode(param, this.getPagination());
    }

    /**
     * 根据USER_ID_A、RELATION_TYPE_CODE、ROLE_CODE_B查询UU关系
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset qryByRelaUserIdARoleCodeB(IData param) throws Exception
    {
        String userIdA = param.getString("USER_ID_A", "");
        String relationTypeCode = param.getString("RELATION_TYPE_CODE", "");
        String roleCodeB = param.getString("ROLE_CODE_B", "");

        return RelaUUInfoQry.qryByRelaUserIdARoleCodeB(userIdA, relationTypeCode, roleCodeB, getPagination());
    }

    /**
     * 根据USER_ID_B、RELATION_TYPE_CODE查询UU关系
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset qryByRelaUserIdB(IData param) throws Exception
    {
        String userIdB = param.getString("USER_ID_B", "");
        String relationTypeCode = param.getString("RELATION_TYPE_CODE", "");

        return RelaUUInfoQry.qryByRelaUserIdB(userIdB, relationTypeCode, getPagination());
    }

    public IDataset qryCountByUserIdAAndRelationTypeCodeAllCrm(IData iData) throws Exception
    {
        String userIdA = iData.getString("USER_ID_A");

        String relationTypeCode = iData.getString("RELATION_TYPE_CODE");

        return RelaUUInfoQry.qryCountByUserIdAAndRelationTypeCodeAllCrm(userIdA, relationTypeCode);
    }

    /**
     * 根据user_id_b查询成员所对应的集团用户信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryGrpUserAByUserB(IData input) throws Exception
    {
        String user_id_b = input.getString("USER_ID_B", "");
        IDataset data = RelaUUInfoQry.qryRelaByUserIdB(user_id_b, null);
        return data;
    }

    /**
     * 查询销户之前最后使用的uu关系
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryLastUUByUserIdB(IData input) throws Exception
    {
        String userIdB = input.getString("USER_ID_B", "");
        String relationTypeCode = input.getString("RELATION_TYPE_CODE");
        return RelaUUInfoQry.qryLastUUByUserIdB(userIdB, relationTypeCode, null);
    }

    public IDataset qryRelaBySerialNumberB(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER_B");
        String relationTypeCode = input.getString("RELATION_TYPE_CODE");

        return RelaUUInfoQry.queryRelaUUBySnb(serialNumber, relationTypeCode);
    }

    public IDataset qryRelaOutNetInfo(IData input) throws Exception
    {
        String userIdA = input.getString("USER_ID_A", "");
        String serialNumberB = input.getString("SERIAL_NUMBER_B");
        String relationTypeCode = input.getString("RELATION_TYPE_CODE");
        String shortCode = input.getString("SHORT_CODE");
        String routeId = input.getString(Route.ROUTE_EPARCHY_CODE);

        return RelaUUInfoQry.qryRelaOutNetInfo(userIdA, serialNumberB, shortCode, relationTypeCode, null, routeId);
    }

    public IDataset qryRelaUUByUIdAAllDB(IData input) throws Exception
    {
        String userIdA = input.getString("USER_ID_A", "");
        String relationTypeCode = input.getString("RELATION_TYPE_CODE", "");
        IDataset data = RelaUUInfoQry.qryRelaUUByUIdAAllDB(userIdA, relationTypeCode);
        return data;
    }

    public IDataset qryRelaUUByUIdaRemoveSix(IData input) throws Exception
    {
        String userIdA = input.getString("USER_ID_A", "");
        String relationTypeCode = input.getString("RELATION_TYPE_CODE", "");
        String sqlByIn = input.getString("SQL_BY_IN", "");

        if ("1".equals(sqlByIn))
        {
            return RelaUUInfoQry.qryRelaUUByInUIdaRemoveSix(userIdA, relationTypeCode, this.getPagination(), null);
        }
        else
        {
            return RelaUUInfoQry.qryRelaUUByUIdaRemoveSix(userIdA, relationTypeCode, this.getPagination(), null);
        }
    }

    /**
     * 根据服务号码查询成员归属集团信息
     */
    public IDataset qryRelaUUInfoAllDb(IData input) throws Exception
    {
        IDataset data = RelaUUInfoQry.qryRelaUUInfoAllDb(input);
        return data;
    }

    public IDataset qrySonVpmnSnByUserIdAForGrp(IData iData) throws Exception
    {
        String userIdA = iData.getString("USER_ID_A");
        return RelaUUInfoQry.qrySonVpmnSnByUserIdAForGrp(userIdA);
    }

    public IDataset qryUUInfoAllCrmByUserIdA(IData param) throws Exception
    {
        String userIdA = param.getString("USER_ID", "");
        return RelaUUInfoQry.qryUUInfoAllCrmByUserIdA(userIdA, null);
    }

    public IDataset queryNextMonthBySnb(IData input) throws Exception
    {
        IDataset data = RelaUUInfoQry.queryNextMonthBySnb(input.getString("SERIAL_NUMBER_B", ""), null);
        return data;
    }

    public IDataset queryRelationGroupInfo(IData input) throws Exception
    {
        return RelaUUInfoQry.queryRelationGroupInfo(input, getPagination());
    }

    /**
     * 通过USER_ID_B、RELATION_TYPE_CODE查询用户关系表
     * 
     * @param param
     * @return
     * @throws Exception
     *             wangjx 2013-10-19
     */
    public IDataset queryRelaUUByIdBRoute(IData param) throws Exception
    {
        String userId = param.getString("USER_ID");
        String relationTypeCode = param.getString("RELATION_TYPE_CODE");
        String routeId = param.getString("ROUTE_ID");
        return RelaUUInfoQry.queryRelaUUByIdBRoute(userId, relationTypeCode, routeId);
    }

    /**
     * 查询RELATION_UU表的关系 根据SERIAL_NUMBER_B 、RELATION_TYPE_CODE
     * 
     * @param iData
     * @return
     * @throws Exception
     */
    public IDataset queryRelaUUBySnb(IData iData) throws Exception
    {

        String serialNumberB = iData.getString("SERIAL_NUMBER");
        String relationTypeCode = iData.getString("RELATION_TYPE_CODE", "");

        return RelaUUInfoQry.queryRelaUUBySnb(serialNumberB, relationTypeCode);
    }

    /**
     * 查询成员使用产品信息 --UU关系
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryUuBySerialNumberB(IData param) throws Exception
    {
        String serialNumber = param.getString("SERIAL_NUMBER", "");
        return RelaUUInfoQry.queryUuBySerialNumberB(serialNumber, getPagination());
    }

    /**
     * 查询集团商务宽带成员 --UU关系
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryRelationUUAllForKDMem(IData param) throws Exception
    {
        String userIdA = param.getString("USER_ID_A", "");
        String relationTypeCode = param.getString("RELATION_TYPE_CODE", "");
        return RelaUUInfoQry.qryRelationUUAllForKDMem(userIdA, relationTypeCode,this.getPagination());
    }
    
    /**
     * 根据USER_ID_A和RELATION_TYPE_CODE统计成员数量
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryCountKDMemForAllCrm(IData param) throws Exception
    {
        String userIdA = param.getString("USER_ID_A", "");
        String relationTypeCode = param.getString("RELATION_TYPE_CODE", "");
        return RelaUUInfoQry.qryCountKDMemForAllCrm(userIdA, relationTypeCode);
    }
    
    /**
     * 查询母集团下子集团短号
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getSubShortCodeExistByUserIdAB(IData param) throws Exception
    {
        String userIdA = param.getString("USER_ID_A", "");
        String userIdB = param.getString("USER_ID_B", "");
        return RelaUUInfoQry.getSubShortCodeExistByUserIdAB(userIdB, userIdA, this.getPagination());
    }
    
    /**
     * 查询母集团短号
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getParentShortCodeExistByUserIdAB(IData param) throws Exception
    {
        String userIdA = param.getString("USER_ID_A", "");
        String userIdB = param.getString("USER_ID_B", "");
        return RelaUUInfoQry.getParentShortCodeExistByUserIdAB(userIdB, userIdA, this.getPagination());
    }
    
    /**
     * REQ201803160015关于优化商务宽带成员信息查询界面的需求
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryRelationUUAllForKDMemNew(IData param) throws Exception
    {
        String userIdA = param.getString("USER_ID_A", "");
        String relationTypeCode = param.getString("RELATION_TYPE_CODE", "");
        return RelaUUInfoQry.qryRelationUUAllForKDMemNew(userIdA, relationTypeCode,this.getPagination());
    }
    
    public IDataset getRelaUUInfoByUserida2(IData input) throws Exception
    {
        IDataset data = RelaUUInfoQry.getRelaUUInfoByUserida2(input);
        return data;
    }
}
