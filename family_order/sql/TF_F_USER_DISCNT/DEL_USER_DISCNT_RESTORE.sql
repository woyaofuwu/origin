DELETE FROM tf_f_user_discnt a
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND PACKAGE_ID IN (SELECT T.PACKAGE_ID
                        FROM TF_F_USER_SALE_ACTIVE T
                       WHERE T.USER_ID = TO_NUMBER(:USER_ID)
                         AND T.END_DATE > SYSDATE)