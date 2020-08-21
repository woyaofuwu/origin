SELECT decode(brand_no,'1','全球通','3','动感地带','神州行') brand
  FROM tf_f_user_brandchange
 WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND sysdate BETWEEN start_date AND end_date