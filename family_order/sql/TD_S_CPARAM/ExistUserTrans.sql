SELECT COUNT(*) recordcount
 FROM tf_f_user_trans
WHERE user_id = :USER_ID
  AND process_tag='0'
  AND EXISTS(
   	   SELECT COUNT(*) FROM tf_a_payrelation a
	    WHERE a.acct_id IN(
   	   select acct_id from tf_a_payrelation
	    WHERE user_id= :USER_ID
                  and end_acyc_id>=(select acyc_id from td_a_acycpara b
                                       where sysdate between b.acyc_start_time and b.acyc_end_time  )
		)HAVING COUNT(*)>1
   )