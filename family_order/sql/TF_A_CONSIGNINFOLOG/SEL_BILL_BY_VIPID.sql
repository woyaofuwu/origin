select adjust_fee, a_discnt, b_discnt,fee, balance,integrate_item,integrate_item_code,
adeb_fee,aimp_fee,aspay_fee,city_code,pay_name,serial_number,bill_id,user_id,bcyc_id,can_paytag,rsrv_info2,rsrv_info1
from (
	select to_char(nvl(c.adjust_fee,0)) adjust_fee,to_char(nvl(c.a_discnt,0)) a_discnt,
	       to_char(nvl(c.b_discnt,0)) b_discnt,to_char(nvl(c.fee,0)) fee,
	       to_char(nvl(c.balance,0)) balance,c.integrate_item,c.integrate_item_code,
	       to_char(nvl(a.adeb_fee,0)) adeb_fee,to_char(nvl(a.aimp_fee,0)) aimp_fee,
	       to_char(nvl(a.aspay_fee,0)) aspay_fee,a.city_code,a.pay_name,a.serial_number,
	       a.bill_id,a.user_id,a.bcyc_id,a.can_paytag,a.rsrv_info2,a.rsrv_info1
	 from tf_a_subconsigninfolog c, 
	 (
	   select adeb_fee,aimp_fee,d.aspay_fee aspay_fee,d.city_code city_code,d.pay_name pay_name,d.serial_number serial_number,bill_id,user_id,d.bcyc_id bcyc_id,can_paytag, d.recv_acyc_id recv_acyc_id,e.remark rsrv_info2,e.return_tag rsrv_info1 
	    from tf_a_consigninfolog d,tf_a_consignlog e
	    where e.vip_id =to_number(:VIP_ID) and e.acyc_id >= :START_ACYC_ID
	    AND e.acyc_id <= :END_ACYC_ID and d.mconsign_id = e.mconsign_id
	  )  a 
	  where  c.bill_id = a.bill_id and c.recv_acyc_id = a.recv_acyc_id
	  and c.bill_id >= to_number(:BILL_ID) order by c.bill_id
  ) where rownum <=1000