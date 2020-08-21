UPDATE tf_f_user_discnt a
   SET a.end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),update_time=sysdate
 WHERE a.user_id = TO_NUMBER(:USER_ID)
   AND a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND SYSDATE < a.end_date+0
   AND discnt_code in (select b.discnt_code from td_b_siservice_discnt b where b.product_id=:PRODUCT_ID
                       AND (b.org_domain=:ORG_DOMAIN OR :ORG_DOMAIN = '*')
                       AND (b.cop_id=:COP_ID OR :COP_ID = '-1')
                       AND (b.biz_code=:BIZ_CODE OR :BIZ_CODE = '*')
                       AND sysdate BETWEEN b.start_date AND b.end_date)