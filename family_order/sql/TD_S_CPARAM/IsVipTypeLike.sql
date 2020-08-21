SELECT COUNT(1) recordcount
FROM tf_f_cust_vip
WHERE user_id = to_number(:USER_ID)
  AND (instr(:VIP_TYPE_CODE,vip_type_code)>0 OR :VIP_TYPE_CODE = '*')
  AND (instr(:VIP_CLASS_ID,vip_class_id)>0 OR :VIP_CLASS_ID = '*')
  AND (vip_card_no LIKE :VIP_CARD_NO OR :VIP_CARD_NO = '*')
  AND remove_tag='0'