SELECT COUNT(1) recordcount
FROM tf_f_cust_vip
WHERE user_id = to_number(:USER_ID)
  AND (instr(:VIP_TYPE_CODE,vip_type_code)>0 OR :VIP_TYPE_CODE = '*')
  AND (instr(:CLASS_ID,VIP_CLASS_ID)>0 OR :CLASS_ID = '*')
  AND remove_tag='0'