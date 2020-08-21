SELECT serial_number para_code1,
'' para_code2,'' para_code3,
'' para_code4, '' para_code5,
'' para_code6, '' para_code7, '' para_code8, '' para_code9, '' para_code10,
'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
from tf_f_user a
where product_id=to_number(:PRODUCT_ID)
  and remove_tag='0' and eparchy_code=:EPARCHY_CODE
  AND NOT EXISTS (SELECT /*+ index(b,IDX_TF_F_USER_SVC_SERVID)*/ 1 FROM tf_f_user_svc b WHERE b.service_id=TO_number(:SERVICE_ID) AND b.rsrv_str1=a.serial_number and b.end_date>sysdate)
   and rownum<21