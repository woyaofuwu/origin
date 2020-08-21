SELECT COUNT(1) RECORD_COUNT
  FROM tf_f_user_discnt d
 WHERE d.partition_id = MOD(TO_NUMBER(:MEB_USER_ID), 10000)
   AND d.user_id = TO_NUMBER(:MEB_USER_ID)
   AND d.end_date > SYSDATE
   AND EXISTS (SELECT 1
          FROM td_s_commpara c,TF_F_USER_GRP_PACKAGE p
         WHERE c.subsys_code = 'CSM'
           AND c.param_attr = '6013'
           AND c.param_code = 'GPWP'
           AND c.para_code1 = to_char(:DISCNT_CODE)
           AND c.para_code2 = to_char(d.discnt_code)
           AND sysdate BETWEEN c.start_date AND c.end_date
           AND c.eparchy_code = '0898'
           AND p.partition_id = MOD(TO_NUMBER(:GRP_USER_ID), 10000)
           AND p.user_id = TO_NUMBER(:GRP_USER_ID)
           AND p.element_id = c.para_code1
           AND p.product_id = c.para_code3
           AND p.package_id = c.para_code4
           AND sysdate BETWEEN p.start_date AND p.end_date
           )
		   
   