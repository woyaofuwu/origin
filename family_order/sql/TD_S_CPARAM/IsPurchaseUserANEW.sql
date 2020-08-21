SELECT a.user_id,a.Campn_Type,a.product_name,to_char(a.end_date, 'YYYY-MM-DD')end_date,a.product_id,A.START_DATE,A.PACKAGE_ID,
       A.PACKAGE_NAME
FROM   tf_f_user_sale_active a
WHERE  user_id = :USER_ID
And    (product_id = :PRODUCT_ID OR :PRODUCT_ID = '99')
AND    process_tag = :PROCESS_TAG
AND    end_date > SYSDATE
AND a.campn_type not in('YX04','YX07')
AND partition_id = mod(to_number(:USER_ID),10000)
AND NOT EXISTS
 (SELECT 1
          FROM TD_S_COMMPARA B
         WHERE B.PARAM_ATTR = '2006'
           AND A.PACKAGE_ID = B.PARA_CODE1
           AND TO_NUMBER(DECODE(B.PARA_CODE2,NULL,'2',B.PARA_CODE2)) > TO_NUMBER( DECODE(B.PARA_CODE2,NULL,'1',:STAR_LEVEL )) 
           AND SYSDATE BETWEEN B.START_DATE AND B.END_DATE)