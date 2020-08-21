SELECT a.brand_code,to_char(a.score) score,to_char(a.score_value) score_value,
       (CASE WHEN (a.brand_code = 'G001' AND a.score_1 >= 1000) THEN '1'
             WHEN (a.brand_code = 'G010' AND a.score_1 >=  500) THEN '1'
        ELSE '0' END
       ) rsrv_str1,to_char(a.score_1) rsrv_str2,
       '' rsrv_str3,'' rsrv_str4,'' rsrv_str5 
  FROM (
         SELECT brand_code,sum(score) score,sum(score_value) score_value,
                SUM(DECODE(score_type_code,'1',score,0)) score_1
           FROM tf_f_user_score_new
          WHERE user_id=TO_NUMBER(:USER_ID)
            AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
            AND brand_code LIKE :BRAND_CODE
         GROUP BY brand_code       
       ) a