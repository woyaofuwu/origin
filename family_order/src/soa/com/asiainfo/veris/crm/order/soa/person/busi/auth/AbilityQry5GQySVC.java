package com.asiainfo.veris.crm.order.soa.person.busi.auth;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class AbilityQry5GQySVC extends CSBizService {
	static Logger logger = Logger.getLogger(AbilityQry5GQySVC.class);
	/**
	 * 5G金币，权益接口
	 * 2020-06-02
	 * wangsc10
	 */
	private static final long serialVersionUID = 1L;
	
	//CIP00152【可领取权益查询】接口
	public IData klqqycx(IData input) throws Exception {
		AbilityQry5GQyBean  bean = new AbilityQry5GQyBean();
		return bean.klqqycx(input);
	}

	//CIP00153【商品续订状态通知】接口
	public IData spxdzttz(IData input) throws Exception {
		AbilityQry5GQyBean  bean = new AbilityQry5GQyBean();
		return bean.spxdzttz(input);
	}
	
	//CIP00154【商品续订状态查询】接口
	public IData spxdztcx(IData input) throws Exception {
		AbilityQry5GQyBean  bean = new AbilityQry5GQyBean();
		return bean.spxdztcx(input);
	}
	
	//CIP00155【省（发起）权益商品退订】接口
	public IData qysptd(IData input) throws Exception {
		AbilityQry5GQyBean  bean = new AbilityQry5GQyBean();
		return bean.qysptd(input);
	}
	
	//CIP00156【金币余额查询】接口
	public IData jbyecx(IData input) throws Exception {
		AbilityQry5GQyBean  bean = new AbilityQry5GQyBean();
		return bean.jbyecx(input);
	}
	
	//CIP00157【金币明细查询】接口
	public IData jbmxcx(IData input) throws Exception {
		AbilityQry5GQyBean  bean = new AbilityQry5GQyBean();
		return bean.jbmxcx(input);
	}
	
	//CIP00158【金币充值】接口（受理类，生成台账）
	public IData jbcz(IData input) throws Exception {
		AbilityQry5GQyBean  bean = new AbilityQry5GQyBean();
		return bean.jbcz(input);
	}
	
	//CIP00159【金币交易超时查询】接口
	public IData jbjycscx(IData input) throws Exception {
		AbilityQry5GQyBean  bean = new AbilityQry5GQyBean();
		return bean.jbjycscx(input);
	}
	
	//CIP00160【金币兑换商品】接口（受理类，生成台账）
	public IData jbdhsp(IData input) throws Exception {
		AbilityQry5GQyBean  bean = new AbilityQry5GQyBean();
		return bean.jbdhsp(input);
	}
	
	//CIP00161【金币渠道订单查询】接口
	public IData jbqdddcx(IData input) throws Exception {
		AbilityQry5GQyBean  bean = new AbilityQry5GQyBean();
		return bean.jbqdddcx(input);
	}
	
	//CIP00162【金币可兑换商品列表查询】接口
	public IData jbkdhsplbcx(IData input) throws Exception {
		AbilityQry5GQyBean  bean = new AbilityQry5GQyBean();
		return bean.jbkdhsplbcx(input);
	}
	
	//CIP00163【金币可兑换商品详情查询】接口
	public IData jbkdhspxqcx(IData input) throws Exception {
		AbilityQry5GQyBean  bean = new AbilityQry5GQyBean();
		return bean.jbkdhspxqcx(input);
	}
	
	//CIP00164【金币可兑换商品关键词搜索】接口
	public IData jbkdhspgjcss(IData input) throws Exception {
		AbilityQry5GQyBean  bean = new AbilityQry5GQyBean();
		return bean.jbkdhspgjcss(input);
	}
	
	//CIP00165【用户权益订单查询】接口
	public IData yhqyddcx(IData input) throws Exception {
		AbilityQry5GQyBean  bean = new AbilityQry5GQyBean();
		return bean.yhqyddcx(input);
	}
	
	//CIP00166【权益订单详情查询】接口
	public IData qyddxqc(IData input) throws Exception {
		AbilityQry5GQyBean  bean = new AbilityQry5GQyBean();
		return bean.qyddxqc(input);
	}
		
	//CIP00167【可订购权益产品查询】接口
	public IData kdgqycpcx(IData input) throws Exception {
		AbilityQry5GQyBean  bean = new AbilityQry5GQyBean();
		return bean.kdgqycpcx(input);
	}

	//CIP00168【权益产品详情查询】接口
	public IData qycpxqcx(IData input) throws Exception {
		AbilityQry5GQyBean  bean = new AbilityQry5GQyBean();
		return bean.qycpxqcx(input);
	}
	
	//CIP00169【权益订单状态查询】接口
	public IData qyddztcx(IData input) throws Exception {
		AbilityQry5GQyBean  bean = new AbilityQry5GQyBean();
		return bean.qyddztcx(input);
	}
}