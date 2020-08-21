SELECT SUM(sums) sums,SUM(month_num) month_sum
  FROM (
		SELECT SUM(batch_count) sums,0 month_num
		  FROM tf_b_trade_bat a
		 WHERE a.accept_date >= TRUNC(TO_DATE(:ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss'))
		   AND a.accept_date <  TRUNC(TO_DATE(:ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss'))+1
		   AND a.batch_oper_type IN (SELECT c.batch_oper_type
									   FROM td_b_batchtype b,td_b_batchtype c
									  WHERE b.batch_oper_type = :BATCH_OPER_TYPE
										AND b.class_code = c.class_code
									) 
		   AND a.remove_tag = '0'
		   AND (a.eparchy_code = :EPARCHY_CODE OR :EPARCHY_CODE IS NULL)
		UNION ALL   
		SELECT 0 sums,SUM(batch_count) month_num
		  FROM tf_b_trade_bat a
		 WHERE a.accept_date >= TRUNC(TO_DATE(:ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss'),'mm')
		   AND a.accept_date <  LAST_DAY(TO_DATE(:ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss'))+1
		   AND a.batch_oper_type IN (SELECT c.batch_oper_type
									   FROM td_b_batchtype b,td_b_batchtype c
									  WHERE b.batch_oper_type = :BATCH_OPER_TYPE
										AND b.class_code = c.class_code
									) 
		   AND a.remove_tag = '0'
		   AND (a.eparchy_code = :EPARCHY_CODE OR :EPARCHY_CODE IS NULL)
       )