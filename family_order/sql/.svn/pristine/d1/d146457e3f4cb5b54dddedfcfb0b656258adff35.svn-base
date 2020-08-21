SELECT a.user_id,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3
           FROM tf_F_user_other A
          where A.user_id = :USER_ID
            and A.rsrv_value_code = 'FTTH_GROUP'
            AND SYSDATE < A.END_DATE
            AND A.RSRV_STR3 = :KD_NUMBER