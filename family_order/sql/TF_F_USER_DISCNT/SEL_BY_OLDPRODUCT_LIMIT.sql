SELECT partition_id,to_char(user_id) user_id,to_char(user_id_a) user_id_a,discnt_code,spec_tag,relation_type_code,start_date,end_date 
  FROM tf_f_user_discnt a
 WHERE EXISTS(
 	   SELECT 1 FROM td_b_product_discnt b
	    WHERE a.discnt_code = b.discnt_code
		  AND b.product_id = TO_number(:RSRV_STR2)
		  AND a.end_date>SYSDATE
		  AND b.end_date>SYSDATE
 )
  AND NOT EXISTS(
 	   SELECT 1 FROM td_b_product_discnt c
	    WHERE a.discnt_code = c.discnt_code
		  AND c.product_id = TO_number(:PRODUCT_ID)
		  AND a.end_date>SYSDATE
		  AND c.end_date>SYSDATE
  )
  AND NOT EXISTS(
  	  SELECT 1 FROM tf_b_trade_discnt  d
	     WHERE d.trade_id=TO_NUMBER(:TRADE_ID)
		   AND d.accept_month=TO_NUMBER(SUBSTRB(:TRADE_ID,5,2))
		   AND d.modify_tag='1'
		   AND d.discnt_code = a.discnt_code
  )
 AND user_id=to_number(:USER_ID)
 AND partition_id = MOD(to_number(:USER_ID),10000)
 and a.end_date>(
  select exec_time from tf_b_trade where trade_id=TO_NUMBER(:TRADE_ID)
  and accept_month=TO_NUMBER(SUBSTRB(:TRADE_ID,5,2))
  and rownum<2
)