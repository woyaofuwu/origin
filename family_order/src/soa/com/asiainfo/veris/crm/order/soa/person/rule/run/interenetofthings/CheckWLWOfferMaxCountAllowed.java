package com.asiainfo.veris.crm.order.soa.person.rule.run.interenetofthings;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 每个用户可相应订购一个流量用尽关停产品；I00010101604
 * 1、国漫流量用尽关停功能产品依赖于国际漫游基础服务产品（I00010100421）；前台
 * 2、国漫流量用尽关停功能产品依赖于国际漫游通用流量产品套餐包（I00010100400）下子产品，国际漫游定向流量产品套餐包（I00010100410）下子产品或国际漫游流量产品套餐包（I00010100430）下子产品； 
 * 3、国漫流量用尽关停功能产品，只能与上述一类资费产品共存，不支持多个套餐；
 * @author XueBo
 *
 */
public class CheckWLWOfferMaxCountAllowed extends BreBase implements IBREScript {

	private static final Logger logger = Logger.getLogger(CheckWLWOfferMaxCountAllowed.class);
	
	private static final long serialVersionUID = 1L;

	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
		
		if(logger.isDebugEnabled()){
			logger.debug("<<<<<<<<<<<<<<<<<<<<<<<CheckWLWOfferMaxCountAllowed校验开始<<<<<"+databus);
		}
		//增量和存量数据
		IDataset alldiscnt = databus.getDataset("TF_F_USER_DISCNT_AFTER");
		//取配置信息
		IDataset configinfos = CommparaInfoQry.getCommByParaAttr("CSM", "4546", "ZZZZ");
		if(IDataUtil.isEmpty(configinfos)){
			BreTipsHelp.addNorTipsInfo(databus,BreFactory.TIPS_TYPE_ERROR, 888888,"数据库未配置 CheckWLWOfferMaxCountAllowed 类的规则的数据！请配置后重试！");
			return true;
		}
		//配置可能有多条，需要循环
		int count=0;
		if(IDataUtil.isNotEmpty(alldiscnt)){
			for(Object object : configinfos ){
				IData data=(IData) object;//一条
				String errormsg=data.getString("PARA_CODE25");//字段25，放该配置的报错信息。
				String limitoffer=getOfferCode(data.getString("PARAM_CODE"));//已转本地discnt_code
				int limitnum=Integer.parseInt(data.getString("PARA_CODE2"));//字段2，放的是限制产品数量
				String[] limitPkg = new String[30];//互斥产品包数组（集团）
				for(int i=1;i<25;i++){//字符串字段只有25个
					if(i==2) continue;
					String col="PARA_CODE";
					limitPkg[i]=data.getString(col+i);
				}
				//此次限制的产品+依赖的包下产品，不能超过2个元素
				if(limitoffer!=null&& !limitoffer.equals("")){
					outer:for(int i=0;i<alldiscnt.size();i++){
						String modify_tag=alldiscnt.getData(i).getString("MODIFY_TAG");//操作类型
						String discnt_code=alldiscnt.getData(i).getString("DISCNT_CODE");//操作优惠编码
						//取新增和存量。退订和修改均不操作
						if(BofConst.MODIFY_TAG_ADD.equals(modify_tag)||BofConst.MODIFY_TAG_USER.equals(modify_tag)){
							/*//本产品
							if(limitoffer.equals(discnt_code)){
								count++;
								continue;
							}*/
							//依赖产品
							for(int a=0;a<limitPkg.length;a++){
								if(limitPkg[a]!=null&& !"".equals(limitPkg[a])){
									String[] loaclcodes=getPkgOfferCode(limitPkg[a]);//本地优惠
									if(contains(discnt_code,loaclcodes)){
										count++;
										continue outer;
									}
								}
							}
						}
					}
				}
				if(count>limitnum){
					BreTipsHelp.addNorTipsInfo(databus,BreFactory.TIPS_TYPE_ERROR, 888888 ,errormsg);
					return true;
				}
				count=0;//一条配置循环完成，初始化
			}
		}
		//上述通过，返回正确
		return false;
	}
	/**
	 * 取本地优惠数组
	 * @param Ipkg 集团包ID
	 * @return 
	 * @throws Exception
	 */
	public String[] getPkgOfferCode(String Ipkg) throws Exception{
		IDataset pkginfos = CommparaInfoQry.getCommparaByAttrCode2("CSM", "9013", Ipkg, "ZZZZ", null);
		if(!pkginfos.isEmpty()){
			String[] result =new String[pkginfos.size()];
			for (int i = 0; i < pkginfos.size(); i++) {
				result[i]=pkginfos.getData(i).getString("PARAM_CODE");
			}
			return result;
		}
		return null;
	}
	/**
	 * 获取本地offer code
	 * @param Iproduct
	 * @return
	 * @throws Exception
	 */
	public String getOfferCode(String Iproduct) throws Exception{
		IDataset productinfos = CommparaInfoQry.getCommparaByAttrCode1("CSM", "9014", Iproduct, "ZZZZ", null);
		if(DataUtils.isNotEmpty( productinfos)){
			return productinfos.first().getString("PARAM_CODE");
		}else{
			return "";//确保不空指针
		}
		
	}
	/**
	 * 判断有没有在这个数组里面
	 * @param offercode
	 * @param relcode
	 * @return true在 false不在
	 */
	public boolean contains(String offercode,String[] relcode){
		for(int i=0;i<relcode.length;i++){
			if(relcode[i].equals(offercode)){
				return true;
			}
		}
		return false;
	}
}
