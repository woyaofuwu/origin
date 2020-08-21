select a.serial_number para_code1, to_char(a.ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss') para_code2,
decode(a.MAKE_STATE,'0','定制状态','D','定制取消','其他状态') para_code3,
decode(a.MAKE_TERMINAL,'11000040','语音交易','终端交易') para_code4,a.COMMENDER_ID para_code5, 
a.UPDATE_TIME para_code6,a.UPDATE_STAFF_ID para_code7,a.UPDATE_DEPART_ID para_code8,
a.RSRV_STR1 para_code9,a.RSRV_STR2 para_code10,a.RSRV_STR3 para_code11,
 a.RSRV_DATE1 para_code12,a.RSRV_DATE2 para_code13,a.RSRV_NUM1 para_code14,
a.RSRV_NUM2 para_code15,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') para_code16,
to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') para_code17,a.cancel_staff_id para_code18,a.cancel_depart_id para_code19,
'' para_code20,'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,'' start_date,
'' end_date,'' eparchy_code,a.remark remark,'' update_staff_id,'' update_depart_id,'' update_time,
'' subsys_code, 0 param_attr,'' param_code,'' param_name
  from ucr_crm1.TF_A_BANKPACKET a ,tf_f_user b
 where (b.serial_number >= :PARA_CODE1 or :PARA_CODE1 is null)
   and (b.serial_number <= :PARA_CODE4 or :PARA_CODE4 is null)
   and b.remove_tag = '0'
   and a.user_id = b.user_id
   and sysdate between a.start_date and a.end_date
   and a.ACCEPT_DATE between to_date(:PARA_CODE2,'yyyy-mm-dd hh24:mi:ss') and to_date(:PARA_CODE3,'yyyy-mm-dd hh24:mi:ss')
   and (a.MAKE_STATE = :PARA_CODE6 or :PARA_CODE6 is null)
   and (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
   and (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
   and (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
   and (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
   and (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)