SELECT *
  FROM tf_f_user_foregift
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND foregift_code = :FEE_TYPE_CODE
   AND money >= :FEE
