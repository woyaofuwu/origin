
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.common;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.util.SaleActiveBreUtil;

/**
 * 用户ucr_crm1.tf_f_user_saleactive表中存在生效的(product_id、package_id)A,才能办理营销活动(product_id、package_id)B
 * 
 * @author yanwu
 */
public class CheckProductPackageActive extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1398097120421905086L;

    private static Logger logger = Logger.getLogger(CheckProductPackageActive.class);

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckExistsMultiActive() >>>>>>>>>>>>>>>>>>");
        }
        boolean Resualt = false;
        String strERROR = "用户需办理%s活动下的%s包方可办理此营销包";
        //获取办理营销活动
        String saleProductId = databus.getString("PRODUCT_ID");
        String slaePackageId = databus.getString("PACKAGE_ID");
        //String slaePackageName = databus.getString("PACKAGE_NAME");
        //String eparchyCode = databus.getString("EPARCHY_CODE");
        Resualt = true;
        //根据办理营销活动，捞配置依赖关系产品和包
        IDataset CommparaActives = CommparaInfoQry.getInfoParaCode1_2("CSM","9978",saleProductId,slaePackageId);
        if( IDataUtil.isNotEmpty(CommparaActives) ){
        	//获取用户所有生效营销活动
            IDataset userSaleActives = databus.getDataset("TF_F_USER_SALE_ACTIVE");
            if ( IDataUtil.isEmpty(userSaleActives) ) {
            	IData CommparaActive = CommparaActives.getData(0);
            	strERROR = CommparaActive.getString("PARAM_NAME","");
            	Resualt = true;
            }else{
            	//循环判断是否存在依赖产品和包
            	for (int index = 0, size = CommparaActives.size(); index < size; index++) {
            		IData CommparaActive = CommparaActives.getData(index);
            		String productId = CommparaActive.getString("PARA_CODE3");
                	String PackageId = CommparaActive.getString("PARA_CODE4");
                	strERROR = CommparaActive.getString("PARAM_NAME","");
                	/*String productName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_PRODUCT", new String[]
                	                     { "PRODUCT_ID" }, "PRODUCT_NAME", new String[]
                	                     { productId });
                	String packageName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_PACKAGE", new String[]
							             { "PACKAGE_ID" }, "PACKAGE_NAME", new String[]
							             { PackageId });
                	
                	strERROR = String.format(strERROR, productName, packageName);*/
                	//匹配用户是否存在生效产品和包,包含返回true
                	Resualt = SaleActiveBreUtil.getActivesByProductIdPackageIdB(userSaleActives, productId, PackageId);
                	if( !Resualt ){ 
                		break; //只要有，则终止！
                	}
                }
            }
        }else{
        	//没有配置，不走此规则
        	Resualt = false;
        }
        //不满足报错
        if( Resualt ){
        	//SaleActiveBreConst.ERROR_22
        	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 15020301, strERROR);
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 退出 CheckExistsMultiActive() >>>>>>>>>>>>>>>>>>");
        }

        return Resualt;
    }

}
