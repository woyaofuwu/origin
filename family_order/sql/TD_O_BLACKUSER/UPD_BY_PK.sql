UPDATE td_o_blackuser
   SET black_user_class_code=:BLACK_USER_CLASS_CODE,mob_phonecode=:MOB_PHONECODE,bank_acct_no=:BANK_ACCT_NO,join_cause=:JOIN_CAUSE,end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),update_time=TO_DATE(:UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS'),update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID,remark=:REMARK  
 WHERE pspt_type_code=:PSPT_TYPE_CODE
   AND pspt_id=:PSPT_ID
   AND start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')