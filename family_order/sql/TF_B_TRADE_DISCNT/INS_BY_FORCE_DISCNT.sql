INSERT INTO tf_b_trade_discnt(trade_id,accept_month,id,discnt_code,modify_tag,start_date,end_date)
SELECT	e.trade_id,e.accept_month,a.user_id,'1',a.discnt_code,'1',a.start_date,TRUNC(e.start_date)-1/24/3600	FROM	tf_f_user_discnt a,tf_b_trade_product e
 WHERE	a.user_id=:USER_ID
	 AND	a.spec_tag='0'
	 AND	sysdate<a.end_date
	 AND	e.trade_id=:TRADE_ID
	 AND	e.accept_month=:ACCEPT_MONTH
	 AND	e.user_id=a.user_id
	 AND	e.modify_tag='2'
	 AND	NOT EXISTS(SELECT	1 FROM tf_b_trade_discnt b    --排除已经删除的优惠
										WHERE b.trade_id=:TRADE_ID
										  AND b.accept_month=:ACCEPT_MONTH
											AND b.modify_tag='1')
	 AND	NOT EXISTS(SELECT 1 FROM td_b_product_discnt c  --不是原产品的必选优惠
	 								  WHERE c.product_id=e.old_product_id
	 								    AND c.discnt_code=a.discnt_code 
	 								    AND SYSDATE<c.end_date
	 								    AND (c.force_tag='1' or c.forcegroup_tag='1'))
	 AND	EXISTS(SELECT 1 FROM td_b_product_discnt d  --是其它产品的必选优惠(排除新产品)
							  WHERE d.product_id>e.product_id
							    AND d.product_id>e.product_id 
							    AND d.discnt_code=a.discnt_code   
							    AND SYSDATE<d.end_date
							    AND (d.force_tag='1' or d.forcegroup_tag='1'))