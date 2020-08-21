SELECT
a.booking_id para_code1,
decode(a.subscribe_state,'0','已预约','1','预约确认','2','已修改','3','服务鉴权','4','已资料调度','5','已完成','6','已取消','未知') para_code2,
decode(a.subscribe_type,'0','归属地','2','入网地','未知') para_code3,
a.Org_Serial_Number para_code4,
a.cust_name para_code5,
decode(a.pspt_type_code,'00','身份证','01','VIP卡','未知') para_code6,
a.pspt_number para_code7,
a.contact_name para_code8, a.contact_mobile para_code9, a.contact_phone para_code10, a.contact_fax para_code11,
a.contact_email para_code12,a.org_staff_id para_code13,a.org_staff_name para_code14,a.Org_Staff_Mobile para_code15,
a.Org_Staff_Phone para_code16,a.Org_Staff_Fax para_code17,a.org_staff_email para_code18,a.dest_staff_id para_code19,a.dest_staff_name para_code20,
a.dest_staff_mobile para_code21,a.dest_staff_phone para_code22,a.dest_staff_fax para_code23,a.dest_staff_email para_code24,
decode(a.need_destroy,'0','是','1','否','未知') para_code25,
decode(a.need_move_score,'1','是','0','否','未知') para_code26,decode(a.need_spec_num,'1','是','0','否','未知')  para_code27,
decode(a.check_tag,'1','是','0','否','未知') para_code28,a.reason para_code29,to_char(a.confirm_open_date,'yyyymmdd') para_code30,
to_char(a.base_line,'yyyymmdd') start_date,to_char(a.dead_line,'yyyymmdd') end_date,
a.success_moved_score eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
  FROM tf_f_spanopen a
 WHERE (:PARA_CODE1 = '' OR :PARA_CODE1 IS NULL OR a.booking_id = :PARA_CODE1)
   AND (:PARA_CODE2 = '' OR :PARA_CODE2 IS NULL OR a.subscribe_type = :PARA_CODE2)
   AND (:PARA_CODE3 = '' OR :PARA_CODE3 IS NULL OR a.subscribe_state = :PARA_CODE3)
   AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL OR a.Org_Serial_Number = :PARA_CODE4)
   AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
   AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
   AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
   AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
   AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
   AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)