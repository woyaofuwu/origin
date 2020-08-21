select PARTITION_ID para_code1,
       USER_ID para_code2,
       SP_ID para_code3,
       BIZ_TYPE_CODE para_code4,
       ORG_DOMAIN para_code5,
       OPR_SOURCE para_code6,
       SP_SVC_ID para_code7,
       START_DATE para_code8,
       END_DATE para_code9,
       BIZ_STATE_CODE para_code10,
       BILLFLG para_code11,
       REMARK para_code12,
       UPDATE_STAFF_ID para_code13,
       UPDATE_DEPART_ID para_code14,
       UPDATE_TIME para_code15,
       SERIAL_NUMBER para_code16,
       BIPCODE para_code17,
       FIRST_DATE para_code18,
       '' para_code19,
       '' para_code20,
       '' para_code21,
       '' para_code22,
       '' para_code23,
       '' para_code24,
       '' para_code25,
       '' para_code26,
       '' para_code27,
       '' para_code28,
       '' para_code29,
       '' para_code30,
       '' start_date,
       '' end_date,
       '' eparchy_code,
       '' remark,
       '' update_staff_id,
       '' update_depart_id,
       '' update_time,
       '' subsys_code,
       0 param_attr,
       '' param_code,
       '' param_name
  from tf_f_user_mbmp_sub
 WHERE start_date > add_months(sysdate, -3)
   AND end_date > SYSDATE
   and sp_id = '801234'
   AND user_id = TO_NUMBER(:PARA_CODE1)
   AND partition_id = MOD(TO_NUMBER(:PARA_CODE1), 10000)
   AND biz_state_code IN ('E', 'P')
   AND (:PARA_CODE2 = '' OR :PARA_CODE2 IS NULL)
   AND (:PARA_CODE3 = '' OR :PARA_CODE3 IS NULL)
   AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)
   AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
   AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
   AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
   AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
   AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
   AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)
UNION ALL
select PARTITION_ID para_code1,
       USER_ID para_code2,
       SP_ID para_code3,
       BIZ_TYPE_CODE para_code4,
       ORG_DOMAIN para_code5,
       OPR_SOURCE para_code6,
       SP_SVC_ID para_code7,
       START_DATE para_code8,
       END_DATE para_code9,
       BIZ_STATE_CODE para_code10,
       BILLFLG para_code11,
       REMARK para_code12,
       UPDATE_STAFF_ID para_code13,
       UPDATE_DEPART_ID para_code14,
       UPDATE_TIME para_code15,
       SERIAL_NUMBER para_code16,
       BIPCODE para_code17,
       FIRST_DATE para_code18,
       '' para_code19,
       '' para_code20,
       '' para_code21,
       '' para_code22,
       '' para_code23,
       '' para_code24,
       '' para_code25,
       '' para_code26,
       '' para_code27,
       '' para_code28,
       '' para_code29,
       '' para_code30,
       '' start_date,
       '' end_date,
       '' eparchy_code,
       '' remark,
       '' update_staff_id,
       '' update_depart_id,
       '' update_time,
       '' subsys_code,
       0 param_attr,
       '' param_code,
       '' param_name
  from tf_f_user_mbmp_sub
 WHERE start_date < SYSDATE
   AND end_date > SYSDATE
   and sp_id = '801234'
   AND user_id = TO_NUMBER(:PARA_CODE1)
   AND partition_id = MOD(TO_NUMBER(:PARA_CODE1), 10000)
   AND biz_state_code IN ('A', 'N')
   AND (:PARA_CODE2 = '' OR :PARA_CODE2 IS NULL)
   AND (:PARA_CODE3 = '' OR :PARA_CODE3 IS NULL)
   AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)
   AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
   AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
   AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
   AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
   AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
   AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)