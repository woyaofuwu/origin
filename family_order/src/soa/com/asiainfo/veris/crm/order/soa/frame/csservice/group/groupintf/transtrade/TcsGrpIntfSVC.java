
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class TcsGrpIntfSVC extends GroupOrderService
{
    private static final long serialVersionUID = 1L;

    /*
     * @description 流量叠加包订购
     * @author xunyl
     * @date 2013-09-27
     */
    public static IDataset bbossPayBiz(IData data) throws Exception
    {
        return GrpBBossIntf.bbossPayBiz(data);
    }

    /*
     * @description 集团业务反向接口
     * @author xunyl
     * @date 2013-09-20
     */
    public static IDataset dealBbossGroupBiz(IData data) throws Exception
    {
        return GrpBBossIntf.dealBbossGroupBiz(data);
    }
    
    /*
     * @description  成员接口查询
     * @author chenyi
     * @date 2015-02-9
     */
    public static IDataset getOrderInfo (IData data) throws Exception
    {
         return GrpBBossIntf.getOrderInfo (data);
    }


    /**
     *
     * @description  EC查询 --- 集团客户业务规则校验
     * @param data
     * @return
     * @throws Exception
     * @data 2018-9-17
     *
     */
    public static IDataset EcCheckInfoBiz(IData data) throws Exception {
        return GrpBBossIntf.EcCheckInfoBiz(data);
    }

    /**
     * @param data
     * @return
     * @throws Exception
     * @description 成员业务反向接口 V2.0
     * @data 2018-9-17
     */
    public static IDataset dealReceptionHallMember(IData data) throws Exception {
        return GrpBBossIntf.dealReceptionHallMemberService(data);
    }

    /*
     * @description 成员业务反向接口
     * @author xunyl
     * @date 2013-09-20
     */
    public static IDataset dealBbossMemBiz(IData data) throws Exception
    {
        return GrpBBossIntf.dealBbossMemBiz(data);
    }

    /*
     * @description 商品订单处理失败通知业务反向接口
     * @author xunyl
     * @date 2013-09-20
     */
    public static IDataset dealBbossOrderDealFaildBiz(IData data) throws Exception
    {
        return GrpBBossIntf.dealBbossOrderDealFaildBiz(data);
    }

    /*
     * @description BBOSS向省BOSS下发工单开通业务
     * @author xunyl
     * @date 2013-09-20
     */
    public static IDataset dealBbossOrderOpenBiz(IData data) throws Exception
    {
        return GrpBBossIntf.dealBbossOrderOpenBiz(data);
    }
    
    /*
     * @description BBOSS向省BOSS下发工单开通业务
     * @author xunyl
     * @date 2018-11-13
     */
    public static IDataset dealJKDTBbossOrderOpenBiz(IData data) throws Exception
    {
        return GrpBBossIntf.dealJKDTBbossOrderOpenBiz(data);
    }


    /*
     * @description 工单流转状态同步反向接口(配合省协助受理/预受理也会涉及到工单流转，需要反馈配合省协助落实情况)
     * @author xunyl
     * @date 2013-09-20
     */
    public static IDataset dealBbossOrderStateBiz(IData data) throws Exception
    {
        return GrpBBossIntf.dealBbossOrderStateBiz(data);
    }

    /**
     * 管理节点接口 chenyi
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset synBBossGrpMgrBiz(IData data) throws Exception
    {
        return GrpBBossIntf.synBBossGrpMgrBiz(data);
    }

    /*
     * @description 商产品规格同步接口
     * @author xunyl
     * @date 2013-09-27
     */
    public static IDataset synBBossPoInfo(IData data) throws Exception
    {
        return GrpBBossIntf.synBBossPoInfo(data);
    }

    /**
     * 集客大厅产品规格信息同步
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset jkdtPoInfo(IData data) throws Exception
    {
        return GrpBBossIntf.jkdtPoInfo(data);
    }
    
    
    /**
     * @Function:
     * @Description: 对于集团下发的BIP4B257 T4101035 报文 直接返回成功
     * @author:chenyi
     * @date: 下午3:32:50 2014-9-21
     */
    public static IDataset dealBbossMebOrderOpenBiz(IData data) throws Exception
    {
        return GrpBBossIntf.dealBbossMebOrderOpenBiz(data);
    }
    
    /**
     * @Function:
     * @Description: 对于集团下发的报文 直接返回成功
     * @date: 2018-12-4
     */
    public static IDataset dealJKDTMemberOrderOpenBiz(IData data) throws Exception
    {
        return GrpBBossIntf.dealJKDTMemberOrderOpenBiz(data);
    }
    
    /**
     * @descripiton BBOSS下行附件入wd_f_ftpfile表，供下载用
     * @author xunyl
     * @date 2015-03-26
     */
    public static IDataset rigistFtpfileTab(IData data)throws Exception{
        return GrpBBossIntf.rigistFtpfileTab(data);
    }
    
    /**
     * 国际流量统付(集团)订购关系同步接口
     * @author cmw
     * @date 2017-01-09
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset synchGrpProdGprsRoam(IData data) throws Exception{
    	return GrpBBossIntf.synchGrpProdGprsRoam(data);
    }

    /**
     * @descripiton 违规信息同步接口，叠加包的暂停和恢复
     * @author wangzc7
     * @date 2017-08-01
     */
    public static IDataset synBbossMebFoul(IData data)throws Exception{
        return GrpBBossIntf.synBbossMebFoul(data);
    }

    /**
     * @description 集合大厅发起-集团业务反向接口
     * @author xunyl
     * @date 2013-09-20
     */
    public static IDataset dealJKDTGroupBiz(IData data) throws Exception
    {
        return GrpBBossIntf.dealJKDTGroupBiz(data);
    }


}
