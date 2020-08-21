select acct_id,pay_name,serial_number,bcyc_id,user_name,all_money,all_new_money,
	     integrate_item_code,integrate_item,fee,bill_id,balance,consign_id,aspay_fee,remark,
	     imp_fee,recv_acyc_id,a_discnt
 from (select b.acct_id,b.pay_name pay_name,b.serial_number serial_number,b.bcyc_id bcyc_id,
	b.user_name user_name,b.all_money,b.all_new_money,
	integrate_item_code,integrate_item,fee,to_char(a.bill_id) bill_id,
	to_char(balance) balance,to_char(a.imp_fee) imp_fee,a.recv_acyc_id,a.a_discnt,b.consign_id,b.aspay_fee,b.remark
	from tf_a_subconsigninfolog  a,
	(
	  select to_char(d.acct_id) acct_id,d.pay_name pay_name,d.serial_number serial_number,to_char(bill_id) bill_id,
	  d.bcyc_id bcyc_id, d.recv_acyc_id recv_acyc_id,d.usr_name user_name,to_char(nvl(d.all_money,0)) all_money,to_char(nvl(d.all_new_money,0)) all_new_money,
	  to_char(d.aspay_fee) aspay_fee,to_char(e.consign_id) consign_id,e.remark
	  from tf_a_consigninfolog d,tf_a_consignlog e
	  where d.mconsign_id =to_number(:CONSIGN_ID)  and d.mconsign_id = e.mconsign_id
	  and d.commit_tag='1'
	)  b
	where a.bill_id >= to_number(:BILL_ID)
	and a.bill_id = b.bill_id 
	and a.recv_acyc_id = b.recv_acyc_id
	order by a.bill_id
 ) where rownum <= 2000