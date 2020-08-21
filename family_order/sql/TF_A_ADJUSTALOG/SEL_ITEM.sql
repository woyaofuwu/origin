select to_char(a.user_id) user_id, a.acyc_id,a.adjust_reason_code,
 to_char(b.detail_item_code) remark,b.adjust_fee adjust_fee 
  from tf_a_adjustalog a, tf_a_subadjustalog b 
where a.user_id =:USER_ID and acyc_id>=:START_ACYC_ID and acyc_id <=:END_ACYC_ID
 and a.adjust_id = b.adjust_id and
 a.adjust_reason_code in (select tag_number from td_s_tag c where 
     c.eparchy_code = a.eparchy_code
     and tag_code like 'ASM_ADIS_ADJUSTREASON%' AND USE_TAG='1')