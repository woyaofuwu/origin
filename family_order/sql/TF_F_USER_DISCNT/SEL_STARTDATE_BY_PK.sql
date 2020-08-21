select user_id,to_char(start_date,'yyyy-MM-dd') start_date, to_char(end_date,'yyyy-MM-dd') end_date
           from tf_F_user_discnt
          where user_id = TO_NUMBER(:USER_ID)
            and partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
            and discnt_code = TO_NUMBER(:DISCNT_CODE)
            and package_id = '-1'
            and product_id = '-1'
            and end_date > sysdate