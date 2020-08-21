Update tf_f_user_sale_active a
   Set 	a.start_Date  = add_months(a.start_Date, TO_NUMBER(:END_OFFDET)),
       	a.end_date    = add_months(a.end_date, TO_NUMBER(:END_OFFDET)),
        a.remark      = '终端集采退货营销活动前移[' || :TRADE_ID || ']',
        a.update_time = SYSDATE
 WHERE USER_ID = :USER_ID
   AND PARTITION_ID = mod(:USER_ID, 10000)
   AND PROCESS_TAG = '0'
   AND PRODUCT_ID NOT IN (SELECT b.PARA_CODE1 FROM TD_S_COMMPARA b WHERE b.PARAM_ATTR=155)
   AND RSRV_DATE2 > TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') --查询未开始的营销活动