SELECT COUNT(1) recordcount
FROM tf_f_user
 WHERE user_id=:USER_ID
  AND f_csb_encrypt((SELECT tag_info FROM td_s_tag WHERE tag_code='CS_INF_DEFAULTPWD'
      AND eparchy_code=:TRADE_EPARCHY_CODE),:USER_ID)  = user_passwd