Update tf_f_user_sale_active a
   Set a.start_Date  = add_months(a.start_Date, TO_NUMBER(:END_OFFDET)),
       a.end_date    = add_months(a.end_date, TO_NUMBER(:END_OFFDET)),
       a.rsrv_Date1  = add_months(a.rsrv_Date1, TO_NUMBER(:END_OFFDET)),
       a.rsrv_Date2  = add_months(a.rsrv_Date2, TO_NUMBER(:END_OFFDET)),
       a.remark      = '终端集采退货营销活动前移[' || :TRADE_ID || ']',
       a.update_time = SYSDATE
 WHERE USER_ID = :USER_ID
   AND PARTITION_ID = mod(:USER_ID, 10000)
   AND PROCESS_TAG = '0'
   AND END_DATE > TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') --查询未开始的营销活动