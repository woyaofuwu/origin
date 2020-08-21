SELECT brand_code,to_char(score) score,to_char(score_value) score_value,
       (CASE WHEN (brand_code = 'G001' AND score_1 >= 1000) THEN '1'
             WHEN (brand_code = 'G010' AND score_1 >=  500) THEN '1'
        ELSE '0' END
       ) rsrv_str1
  FROM (
         SELECT brand_code,sum(score) score,sum(score_value) score_value,
                SUM(DECODE(score_type_code,'1',score,0)) score_1
           FROM tf_f_user_score_new
          WHERE user_id=TO_NUMBER(:USER_ID)
            AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
            AND brand_code LIKE :BRAND_CODE
         GROUP BY brand_code       
       )