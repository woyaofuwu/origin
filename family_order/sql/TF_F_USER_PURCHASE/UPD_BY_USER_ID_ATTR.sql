UPDATE tf_f_user_purchase
   SET process_tag=:PROCESS_TAG,end_date=trunc(add_months(sysdate,1),'MM')-1/3600/24,finish_date=trunc(add_months(sysdate,1),'MM')-1/3600/24
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND purchase_attr=:PURCHASE_ATTR
   AND process_tag='0'
   AND start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')