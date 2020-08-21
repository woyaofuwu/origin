select
sum(fee) PARA_CODE1,
fee_mode PARA_CODE2,
'' para_code3,'' para_code4, '' para_code5,'' para_code6,
'' para_code7,'' para_code8, '' para_code9, '' para_code10,
'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,
min(end_date+(select tag_number from td_s_tag where subsys_code='CSM' and tag_code='CS_NUM_BACKFOREGIFTDAYS' and eparchy_code='0991' and sysdate between start_date and end_date)) end_date,
'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
from
(select b.fee/100 fee,b.fee_type_code fee_mode,a.end_date end_date
from tf_f_user_svc a,td_b_product_tradefee b
where a.service_id in (15,19)
  and a.user_id=to_number(:PARA_CODE1)
  and a.partition_id=mod(to_number(:PARA_CODE1),10000)
  and a.end_date>sysdate-(select tag_number from td_s_tag where subsys_code='CSM' and tag_code='CS_NUM_BACKFOREGIFTDAYS' and (eparchy_code=:PARA_CODE2 or eparchy_code='ZZZZ') and sysdate between start_date and end_date)
  and b.pid = a.service_id
  and b.id_type = '1'
  and b.fee_mode = '1'
  and (b.eparchy_code=:PARA_CODE2 or b.eparchy_code = 'ZZZZ')
  ) f
where
   (:PARA_CODE3 = '' OR :PARA_CODE3 IS NULL)
   AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)
   AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
   AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
   AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
   AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
   AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
   AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)
group by fee_mode