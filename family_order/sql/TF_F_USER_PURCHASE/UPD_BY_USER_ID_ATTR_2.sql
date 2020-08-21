UPDATE tf_f_user_purchase
   SET process_tag='2',end_date=TRUNC(sysdate+1)-1/24/3600,
       rsrv_str9=TO_CHAR(sysdate, 'YYYY-MM-DD HH24:MI:SS'),
       finish_date=sysdate,remark='购机业务取消'
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND purchase_attr=:PURCHASE_ATTR
   AND process_tag='0'
   AND start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')