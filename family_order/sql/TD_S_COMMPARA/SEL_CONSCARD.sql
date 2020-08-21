SELECT a.value_card_no para_code1,a.value_card_type_code para_code2,b.value_price para_code3, /*有价卡号 有价卡类型编码 面值编码*/
a.eparchy_code para_code4, a.staff_id para_code5,to_char(a.time_in,'yyyy-mm-dd hh24:mi:ss') para_code6, /*归属地州 归属员工 入库时间*/
a.staff_id_in para_code7, to_char(a.assign_time,'yyyy-mm-dd hh24:mi:ss') para_code8, a.assign_staff_id para_code9,  /*入库元员工 调拨员工 调拨时间*/
to_char(a.active_time,'yyyy-mm-dd hh24:mi:ss') para_code10, a.active_staff_id para_code11,
'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,
'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
from tf_r_valuecard a,td_m_resvalue b, ucr_roam.tg_cdr_scpv_new@DBLNK_BOSSBIL1 c
where a.value_card_type_code = 'a'
  AND a.sale_tag = '0'
  AND a.fee_tag = '0'
  AND b.value_code = a.value_code
  AND b.res_type_code ='3'
  AND a.eparchy_code = b.eparchy_code 
  AND a.value_card_no = c.card_no   
  AND c.deal_date >= to_date(:PARA_CODE1,'yyyy-mm-dd')
  AND c.deal_date <= to_date(:PARA_CODE2,'yyyy-mm-dd')
  AND a.city_code = :PARA_CODE3
  AND ((TRIM(:PARA_CODE4) IS NULL) OR a.staff_id = TRIM(:PARA_CODE4))
  AND (:PARA_CODE5 IS NOT NULL OR :PARA_CODE5 IS NULL)
  AND (:PARA_CODE6 IS NOT NULL OR :PARA_CODE6 IS NULL)
  AND (:PARA_CODE7 IS NOT NULL OR :PARA_CODE7 IS NULL)
  AND (:PARA_CODE8 IS NOT NULL OR :PARA_CODE8 IS NULL)
  AND (:PARA_CODE9 IS NOT NULL OR :PARA_CODE9 IS NULL)
  AND (:PARA_CODE10 IS NOT NULL OR :PARA_CODE10 IS NULL)