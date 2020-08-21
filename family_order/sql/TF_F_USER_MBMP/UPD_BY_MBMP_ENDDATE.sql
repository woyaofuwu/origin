UPDATE tf_f_user_mbmp
   SET biz_state_code='E',end_date=
     (select start_date-1/24/3600 
       from tf_f_user_mbmp
       WHERE partition_id=MOD(:USER_ID,10000)
       AND user_id=:USER_ID
			 AND biz_type_code=:BIZ_TYPE_CODE
			 AND biz_state_code=:BIZ_STATE_CODE
			 AND end_date+0>SYSDATE
       AND rowid = (select  max(rowid) 
       from tf_f_user_mbmp
       WHERE partition_id=MOD(:USER_ID,10000)
       AND user_id=:USER_ID
			 AND biz_type_code=:BIZ_TYPE_CODE
			 AND biz_state_code=:BIZ_STATE_CODE
			 AND end_date+0>SYSDATE)
		 )
 WHERE partition_id=MOD(:USER_ID,10000)
  AND user_id=:USER_ID
  AND biz_type_code=:BIZ_TYPE_CODE
  AND biz_state_code=:BIZ_STATE_CODE
  AND end_date+0>SYSDATE
 AND rowid = (select min(rowid)
       from tf_f_user_mbmp
       WHERE partition_id=MOD(:USER_ID,10000)
       AND user_id=:USER_ID
			 AND biz_type_code=:BIZ_TYPE_CODE
			 AND biz_state_code=:BIZ_STATE_CODE
			 AND end_date+0>SYSDATE)