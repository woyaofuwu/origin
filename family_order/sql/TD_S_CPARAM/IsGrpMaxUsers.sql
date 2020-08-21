SELECT COUNT(1) recordcount FROM dual
WHERE (SELECT  max_users FROM tf_f_user_vpn WHERE user_id=:USER_ID)
<=(SELECT COUNT(*) FROM tf_f_relation_uu WHERE end_date>SYSDATE AND user_id_a=:USER_ID)