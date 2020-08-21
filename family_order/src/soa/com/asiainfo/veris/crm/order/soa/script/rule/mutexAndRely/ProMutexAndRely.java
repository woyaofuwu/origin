
package com.asiainfo.veris.crm.order.soa.script.rule.mutexAndRely;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.mutexAndRely.ProductLimitInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchpInfoQry;

/**
 * 此类用于产品之间的互斥依赖关系的检验
 * 
 * @author liuxx3
 * @DATE 2014-05-15
 */
public class ProMutexAndRely extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    /**
     * 订购状态处理前后依赖关系 前后依赖关系为需要订购一个主产品并且成功开通以后 才能订购另外一个副产品 不能在一笔订单中主副产品同时订购
     * 
     * @author liuxx3
     * @Date 2014-06-23
     */
    private boolean proAroundRelyOrder(String productIdA, String productIdB, String groupId, IData productIdList, String operType) throws Exception
    {

        if ("1".equals(operType) || "7".equals(operType))
        {
            /*
             * 循环订单的产品集 逐一对比 如果已经订购并且开通了 主产品则会出现状态为EXIST 可以通过 如果订单中没有主产品没有则返回false 上层方法给出报错信息 如果只是在同笔订单中出现了主产品
             * 但是并没有先开通成功 则返回false 上层方法给出报错信息
             */
            for (int i = 0; i < productIdList.size(); i++)
            {
                String productId = productIdList.getString("PRODUCT_ID_" + (i + 1), "");
                String productOperType = productIdList.getString("PRO_OPER_TYPE" + (i + 1), "");
                if (productIdA.equals(productId) && "EXIST".equals(productOperType))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 总入口 根据不同操作类型再进入不同的方法
     * 
     * @author liuxx3
     * @Date 2014-05-15
     */

    public void proMutexAndRely(String operType, String groupId, IData productIdList) throws Exception
    {

        IData params = new DataMap();
        params.put("OPER_TYPE", operType);
        params.put("GROUP_ID", groupId);
        params.put("PRODUCT_ID_LIST", productIdList);

        // 订购状态和预受理处理流程
        if ("1".equals(operType) || "10".equals(operType))
        {
            proMutexAndRelyOrder(params);
        }
        // 取消处理流程
        else if ("2".equals(operType) || "11".equals(operType))
        {
            proMutexAndRelyOff(params);
        }
        // 修改产品组成关系处理流程
        else if ("7".equals(operType))
        {

            proMutexAndRelyChg(params);
        }
        // 暂停处理流程
        else if ("3".equals(operType))
        {
            proMutexAndRelyPause(params);
        }
        // 恢复处理流程
        else if ("4".equals(operType))
        {
            proMutexAndRelyRecover(params);
        }
    }

    /**
     * 修改产品组成关系处理流程
     * 
     * @author liuxx3
     * @Date 2014-05-15
     */
    private void proMutexAndRelyChg(IData inParams) throws Exception
    {

        // 获取传递进来操作类型 产品编码 商产品信息等参数
        IData productIdList = inParams.getData("PRODUCT_ID_LIST");

        if (IDataUtil.isEmpty(productIdList))
        {
            return;
        }

        for (int i = 0; i < productIdList.size(); i++)
        {
            // 循环产品ID集 依次取出产品操作类型进行判断
            String productOperType = productIdList.getString("PRO_OPER_TYPE_" + (i + 1), "");

            // 如果产品操作类型为1 则进入订购的处理流程
            if ("1".equals(productOperType))
            {
                proMutexAndRelyOrder(inParams);
            }

            // 如果产品操作类型为2 则进入取消的处理流程
            else if ("2".equals(productOperType))
            {
                proMutexAndRelyOff(inParams);
            }
        }

    }

    /**
     * 取消与预取消状态入口
     * 
     * @author liuxx3
     * @Date 2014-05-15
     */
    private void proMutexAndRelyOff(IData inParams) throws Exception
    {

        // 获取传递进来操作类型 产品编码 商产品信息等参数
        String groupId = inParams.getString("GROUP_ID");
        IData productIdList = inParams.getData("PRODUCT_ID_LIST");

        // 产品信息集合为空则退出处理流程
        if (IDataUtil.isEmpty(productIdList))
        {
            return;
        }

        // 产品信集不为空进入循环处理流程
        for (int i = 0; i < productIdList.size(); i++)
        {
            // 循环获取到的产品订购信息 取出产品ID 做为主产品处理 定义为productIdA
            String productIdA = productIdList.getString("PRODUCT_ID_" + (i + 1));

            String productOperType = productIdList.getString("PRO_OPER_TYPE_" + (i + 1), "");
            if (StringUtils.isBlank(productIdA)) 
            {
				break;
			}
            // 根据取出的产品ID在td_s_productlimit中查询 一次作为主产品PRODUCT_ID_A查询 查询该主产品是否存在依赖与它的副产品PRODUCT_ID_B
            IDataset productLimitInfos = ProductLimitInfoQry.getLimitTagB(productIdA);

            // 如果没有查询出来对应的副产品PRODUCT_ID_B 或者产品的操作类型不是取消的操作类型"2" 则进入下次循环
            if (IDataUtil.isEmpty(productLimitInfos) || !"2".equals(productOperType))
            {
                continue;
            }

            // 如果有查询出来对应的副产品PRODUCT_ID_B 并且产品的操作类型是取消的操作类型"2" 则进入循环体逐一处理
            for (int j = 0; j < productLimitInfos.size(); j++)
            {
                IData productLimitInfo = productLimitInfos.getData(j);
                String productIdB = productLimitInfo.getString("PRODUCT_ID_B");
                String limitTag = productLimitInfo.getString("LIMIT_TAG");

                // 不管是前后依赖 还是并行依赖 只要是存在依赖关系就进入 处理流程
                if (limitTag.equals("A") || limitTag.equals("Z"))
                {
                    proRelyOff(productIdA, productIdB, groupId, productIdList);
                }
            }

            // 循环结束方法结束
        }

    }

    /**
     * 订购状态处理方法
     * 
     * @author liuxx3
     * @Date 2014-05-15
     */
    private void proMutexAndRelyOrder(IData inParams) throws Exception
    {

        // 获取传递进来操作类型 产品编码 商产品信息等参数
        String groupId = inParams.getString("GROUP_ID");
        IData productIdList = inParams.getData("PRODUCT_ID_LIST");
        String operType = inParams.getString("OPER_TYPE");

        // 如果产品Id集合为空直接退出处理
        if (IDataUtil.isEmpty(productIdList))
        {
            return;
        }

        for (int i = 0; i < productIdList.size(); i++)
        {
            // 循环获取到的产品订购信息 取出产品ID 作为子产品productIdB
            String productIdB = productIdList.getString("PRODUCT_ID_" + (i + 1));
            // 从产品ID集中取出 产品操作类型
            String productOperType = productIdList.getString("PRO_OPER_TYPE_" + (i + 1), "");
            if (StringUtils.isBlank(productIdB)) 
            {
				break;
			}
            // 根据取出的产品ID在td_s_productlimit中查询一次作为PRODUCT_ID_B查询 取出对应的产品关系信息
            IDataset productLimitInfos = ProductLimitInfoQry.getLimitTagA(productIdB);

            // 如果产品关系信息为空 或者 产品操作类型不为1 则进入下一次循环
            if (IDataUtil.isEmpty(productLimitInfos) || !"1".equals(productOperType))
            {
                continue;
            }

            // 判断产品关系信息不为空并且产品操作类型是属于订购操作为“1” 进入循环流程 逐条处理产品的关系信息
            for (int j = 0; j < productLimitInfos.size(); j++)
            {
                IData productLimitInfo = productLimitInfos.getData(j);
                String productIdA = productLimitInfo.getString("PRODUCT_ID_A");
                String limitTag = productLimitInfo.getString("LIMIT_TAG");

                // 如果LIMIN_TAG为"S" 则表示互斥 进入互斥的处理流程
                if (limitTag.equals("S"))
                {
                    proMutexOrder(productIdA, productIdList, productIdB, groupId, operType);
                }

                // 如果LIMIN_TAG为"A" 则表示为前后依赖关系 进入前后依赖关系的处理流程
                else if (limitTag.equals("A"))
                {
                    boolean ft = proAroundRelyOrder(productIdA, productIdB, groupId, productIdList, operType);
                    if (ft == false)
                    {
                        CSAppException.apperr(ProductException.CRM_PRODUCT_233, productIdA, productIdB, productIdA, productIdB);
                    }
                }

                // 如果LIMIN_TAG为"Z" 则表示为并行依赖关系 进入并行依赖关系的处理流程
                else if (limitTag.equals("Z"))
                {
                    boolean ft = proParallelRelyOrder(productIdA, productIdList, productIdB, groupId, operType);
                    if (ft == false)
                    {
                        CSAppException.apperr(ProductException.CRM_PRODUCT_236, productIdA, productIdB);
                    }

                }
            }

            // 循环结束方法结束
        }

    }

    /**
     * 暂停状态入口
     * 
     * @author liuxx3
     * @Date 2014-05-15
     */
    private void proMutexAndRelyPause(IData inParams) throws Exception
    {

        // 获取传递进来操作类型 产品编码 商产品信息等参数
        IData productIdList = inParams.getData("PRODUCT_ID_LIST");
        String groupId = inParams.getString("GROUP_ID");

        // 产品信息集合为空则退出处理流程
        if (IDataUtil.isEmpty(productIdList))
        {
            return;
        }

        // 产品信集不为空进入循环处理流程
        for (int i = 0; i < productIdList.size(); i++)
        {
            // 循环产品ID的集合依次取出产品ID
            String productIdA = productIdList.getString("PRODUCT_ID_" + (i + 1), "");
            String productOperType = productIdList.getString("PRO_OPER_TYPE_" + (i + 1), "");

            // 根据取出的产品ID在td_s_productlimit中查询 一次作为主产品PRODUCT_ID_A查询 查询该主产品是否存在依赖与它的副产品PRODUCT_ID_B
            IDataset productLimitInfos = ProductLimitInfoQry.getLimitTagB(productIdA);

            // 如果没有查询出来对应的副产品PRODUCT_ID_B 或者产品的操作类型不是暂停的操作类型"3" 则进入下次循环
            if (IDataUtil.isEmpty(productLimitInfos) || !"3".equals(productOperType))
            {
                continue;
            }

            // 如果有查询出来对应的副产品PRODUCT_ID_B 并且产品的操作类型是暂停的操作类型"3" 则进入循环体逐一处理
            for (int j = 0; j < productLimitInfos.size(); j++)
            {
                IData productLimitInfo = productLimitInfos.getData(j);
                String productIdB = productLimitInfo.getString("PRODUCT_ID_B");
                String limitTag = productLimitInfo.getString("LIMIT_TAG");

                // 不管是前后依赖 还是并行依赖 只要是存在依赖关系就进入 处理流程
                if (limitTag.equals("A") || limitTag.equals("Z"))
                {
                    proRelyPause(productIdA, productIdB, groupId, productIdList);

                }
            }

        }

    }

    /**
     * 恢复与预取消恢复状态入口
     * 
     * @author liuxx3
     * @Date 2014-05-15
     */

    private void proMutexAndRelyRecover(IData inParams) throws Exception
    {

        // 获取传递进来操作类型 产品编码 商产品信息等参数
        IData productIdList = inParams.getData("PRODUCT_ID_LIST");
        String groupId = inParams.getString("GROUP_ID");

        // 产品信息集合为空则退出处理流程
        if (IDataUtil.isEmpty(productIdList))
        {
            return;
        }

        // 循环资料表中的副产品信息 逐一对比
        for (int i = 0; i < productIdList.size(); i++)
        {
            // 循环产品ID的集合依次取出产品ID以及产品操作类型
            String productIdB = productIdList.getString("PRODUCT_ID_" + (i + 1));
            String productOperType = productIdList.getString("PRO_OPER_TYPE_" + (i + 1), "");
            if (StringUtils.isBlank(productIdB)) 
            {
				break;
			}
            // 根据取出的产品ID在td_s_productlimit中查询 一次作为副产品PRODUCT_ID_B查询 查询该副产品是否存在依赖与它的主产品PRODUCT_ID_A
            IDataset productLimitInfos = ProductLimitInfoQry.getLimitTagA(productIdB);

            // 如果没有查询出来对应的主产品PRODUCT_ID_A 或者产品的操作类型不是恢复的操作类型"4" 则进入下次循环
            if (IDataUtil.isEmpty(productLimitInfos) || !"4".equals(productOperType))
            {
                continue;
            }

            // 如果有查询出来对应的副产品PRODUCT_ID_B 并且产品的操作类型是恢复的操作类型"4" 则进入循环体逐一处理
            for (int j = 0; j < productLimitInfos.size(); j++)
            {
                IData productLimitInfo = productLimitInfos.getData(j);
                String productIdA = productLimitInfo.getString("PRODUCT_ID_A");
                String limitTag = productLimitInfo.getString("LIMIT_TAG");

                // 不管是前后依赖 还是并行依赖 只要是存在依赖关系就进入 处理流程
                if ("A".equals(limitTag) || "Z".equals(limitTag))
                {
                    proRelyRecover(productIdA, productIdB, groupId, productIdList);
                }

            }

        }

    }

    /**
     * 订购状态处理互斥关系 互斥表示 2个产品为互斥关系 那么只能订购其中一个
     * 
     * @author liuxx3
     * @Date 2014-06-23
     */
    private void proMutexOrder(String productIdA, IData productIdList, String productIdB, String groupId, String operType) throws Exception
    {

        if (!"1".equals(operType) || !"7".equals(operType))
        {
            return;
        }
        // 循环订单中的产品ID集 逐一进行对比
        for (int i = 0; i < productIdList.size(); i++)
        {
            String productId = productIdList.getString("PRODUCT_ID_" + (i + 1), "");
            // 如果在产品ID集中有产品和查询出来产品ID一致 则出现提示信息 提示用户互斥产品不能订购
            if (productIdA.equals(productId))
            {
                CSAppException.apperr(ProductException.CRM_PRODUCT_235, productIdB, productIdA);
            }
        }

    }

    /**
     * 订购状态处理并行依赖关系 并行依赖关系为可以同时在一笔订单中一起订购 但是必须是主副产品一起订购 或者单独订购主产品 不能单独订购副产品
     * 
     * @author liuxx3
     * @Date 2014-06-23
     */
    private boolean proParallelRelyOrder(String productIdA, IData productIdList, String productIdB, String groupId, String operType) throws Exception
    {
        // 如果同一笔订单产品中有主产品 则通过 否则提示用户需要同时订购主产品
        if ("1".equals(operType) || "7".equals(operType))
        {
            // 只要在同批订单中有主产品存在 不管是开通还是没开通 只要勾选上有主产品 那就可以通过
            for (int i = 0; i < productIdList.size(); i++)
            {
                String productId = productIdList.getString("PRODUCT_ID_" + (i + 1), "");
                if (productIdA.equals(productId))
                {
                    return true;
                }

            }
        }
        return false;
    }

    /**
     * 取消与预取消依赖关系处理方法
     * 
     * @author liuxx3
     * @Date 2014-05-15
     */
    private void proRelyOff(String productIdA, String productIdB, String groupId, IData productIdList) throws Exception
    {

        // 查询出资料表中的副产品信息
        IDataset proMerchPInfos = UserGrpMerchpInfoQry.selProMerchinfoStatus(groupId, productIdB);

        // 如果没有副产品的信息则退出处理流程 说明在资料表中没有该产品对应的副产品信息
        if (IDataUtil.isEmpty(proMerchPInfos))
        {
            return;
        }

        // 循环资料表中的副产品信息 逐一对比
        for (int i = 0; i < proMerchPInfos.size(); i++)
        {
            IData proMerchPInfo = proMerchPInfos.getData(i);

            // 取出副产品在资料表对应的USER_ID
            String userId = proMerchPInfo.getString("USER_ID", "");

            // 对订单中的产品集循环
            for (int j = 0; j < productIdList.size(); j++)
            {
                // 取出订单中产品的产品ID
                String productId = productIdList.getString("PRODUCT_ID_" + (j + 1), "");

                // 取出订单中产品的产品操作类型
                String productOperType = productIdList.getString("PRO_OPER_TYPE_" + (j + 1), "");

                // 取出订单中产品的USER_ID
                String proUserId = productIdList.getString("USER_ID_" + (j + 1), "");

                // 判断如果对应的副产品和USER_ID一致 并且该产品处于没有取消的状态 即EXIST
                // 提示用户需要先取消副产品 才能取消主产品 也可以同时取消
                if (productId.equals(productIdB) && userId.equals(proUserId) && "EXIST".equals(productOperType))
                {
                    CSAppException.apperr(ProductException.CRM_PRODUCT_234, productIdB, productIdA, productIdB);
                }
            }
        }

    }

    /**
     * 暂停依赖关系处理方法
     * 
     * @author liuxx3
     * @Date 2014-05-15
     */
    private void proRelyPause(String productIdA, String productIdB, String groupId, IData productIdList) throws Exception
    {

        // 查询出资料表中的副产品信息
        IDataset proMerchPInfos = UserGrpMerchpInfoQry.selProMerchinfoStatus(groupId, productIdB);

        // 如果没有副产品的信息则退出处理流程 说明在资料表中没有该产品对应的副产品信息
        if (IDataUtil.isEmpty(proMerchPInfos))
        {
            return;
        }

        // 循环资料表中的副产品信息 逐一对比
        for (int i = 0; i < proMerchPInfos.size(); i++)
        {

            IData proMerchPInfo = proMerchPInfos.getData(i);

            // 取出副产品在资料表对应的USER_ID
            String userId = proMerchPInfo.getString("USER_ID", "");

            // 取出副产品在资料表对应的副产品状态
            String status = proMerchPInfo.getString("STATUS", "");

            // 对订单中的产品集循环
            for (int j = 0; j < productIdList.size(); j++)
            {

                // 取出订单中产品的产品ID
                String productId = productIdList.getString("PRODUCT_ID_" + (j + 1), "");

                // 取出订单中产品的产品操作类型
                String productOperType = productIdList.getString("PRO_OPER_TYPE_" + (j + 1), "");

                // 取出订单中产品的USER_ID
                String proUserId = productIdList.getString("USER_ID_" + (j + 1), "");

                // 判断如果产品是对应的副产品ID 并且用户编码USER_ID一样 在前台没有做暂停操作 或者在资料表仍然是开通状态的话 就出现提示信息
                if (productId.equals(productIdB) && userId.equals(proUserId) && ("EXIST".equals(productOperType) || "A".equals(status)))
                {

                    CSAppException.apperr(ProductException.CRM_PRODUCT_237, productIdB, productIdA, productIdB);

                }
            }

        }

    }

    /**
     * 恢复与预取消恢复依赖关系处理方法
     * 
     * @author liuxx3
     * @Date 2014-05-15
     */
    private void proRelyRecover(String productIdA, String productIdB, String groupId, IData productIdList) throws Exception
    {
        // 查询出资料表中的主产品信息
        IDataset proMerchPInfos = UserGrpMerchpInfoQry.selProMerchinfoStatus(groupId, productIdA);

        // 如果没有主产品的信息则退出处理流程 说明在资料表中没有该产品对应的主产品信息
        if (IDataUtil.isEmpty(proMerchPInfos))
        {
            return;
        }

        // 循环资料表中的主产品信息 逐一对比
        for (int i = 0; i < proMerchPInfos.size(); i++)
        {

            IData proMerchPInfo = proMerchPInfos.getData(i);

            // 取出副产品在资料表对应的主产品状态
            String status = proMerchPInfo.getString("STATUS", "");

            // 取出副产品在资料表对应的主产品的用户编码USER_ID
            String userId = proMerchPInfo.getString("USER_ID", "");

            // 对订单中的产品集循环
            for (int j = 0; j < productIdList.size(); j++)
            {
                // 取出订单中产品的产品ID
                String productId = productIdList.getString("PRODUCT_ID_" + (j + 1), "");

                // 取出订单中产品的产品操作类型
                String productOperType = productIdList.getString("PRO_OPER_TYPE_" + (j + 1), "");

                // 取出订单中产品的USER_ID
                String proUserId = productIdList.getString("USER_ID_" + (j + 1), "");

                // 判断如果产品是对应的主产品ID 并且用户编码USER_ID一样 在前台没有做恢复操作 或者在资料表仍然是暂停状态的话 就出现提示信息
                if (productId.equals(productIdB) && userId.equals(proUserId) && ("EXIST".equals(productOperType) || "N".equals(status)))
                {
                    CSAppException.apperr(ProductException.CRM_PRODUCT_238, productIdA, productIdB, productIdA);
                }

            }

        }
    }

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {

        String operType = databus.getString("OPER_TYPE");
        String groupId = databus.getString("GROUP_ID");
        IData productIdList = databus.getData("PRODUCT_ID_LIST");
        proMutexAndRely(operType, groupId, productIdList);

        return false;
    }

}
