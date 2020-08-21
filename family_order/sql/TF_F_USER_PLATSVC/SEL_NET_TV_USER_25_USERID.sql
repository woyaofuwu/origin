select c.user_id
  from tf_f_user_platsvc C
 where C.USER_ID = :USER_ID
   and c.partition_id = mod(:USER_ID, 10000)
   and TRUNC(C.FIRST_DATE, 'mm') = TRUNC(sysdate, 'mm')
   and TO_NUMBER(TO_CHAR(C.FIRST_DATE, 'DD')) >= 25
   and service_id = '40227762'
   and sysdate < c.end_date
union
select A.USER_ID from tf_f_user_saleactive_book a
where a.user_id=:USER_ID
AND A.PRODUCT_ID='69908030'
AND A.PACKAGE_ID='70012708'
AND A.DEAL_STATE_CODE='1'
and TRUNC(A.UPDATE_TIME, 'mm') = TRUNC(sysdate, 'mm')
and TO_NUMBER(TO_CHAR(A.UPDATE_TIME, 'DD')) >= 25
AND A.PROCESS_TAG='0'
AND A.PRODUCT_ID_B='69908030'
AND A.PACKAGE_ID_B='70012710'
and nvl(a.rsrv_date2,a.end_date)>sysdate