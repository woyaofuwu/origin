SELECT /*+ index (a IDX_TF_F_USER_OTHER_VALUECODE)*/rsrv_str10 para_code1,rsrv_value para_code2,
decode(rsrv_str5,'1','已兑奖','未兑奖') para_code3,rsrv_str6 para_code4,
rsrv_str7 para_code5,rsrv_str8 para_code6, rsrv_str9 para_code7,
(SELECT param_name FROM td_s_commpara WHERE param_attr=1018 AND param_code=rsrv_str3 AND para_code1=rsrv_str2 AND (eparchy_code=rsrv_str7 OR eparchy_code='ZZZZ')) para_code8,
(SELECT param_name FROM td_s_commpara WHERE param_attr=1018 AND param_code='AA' AND para_code1=rsrv_str3 AND (eparchy_code=rsrv_str7 OR eparchy_code='ZZZZ')) para_code9,
(SELECT param_name FROM td_s_commpara WHERE param_attr=1019 AND param_code='GGK000' AND para_code1=rsrv_str1 AND (eparchy_code=rsrv_str7 OR eparchy_code='ZZZZ')) para_code10,
rsrv_str4 para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,''  para_code17,''  para_code18,''  para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
from tf_f_user_other a
where rsrv_value_code='GGTH' AND end_date>SYSDATE
   AND (rsrv_str10=:PARA_CODE1 OR :PARA_CODE1 IS NULL)
   AND (rsrv_value = :PARA_CODE2 OR :PARA_CODE2 IS NULL)
   AND :PARA_CODE3 IS NULL
   AND :PARA_CODE4 IS NULL   
   AND :PARA_CODE5 IS NULL
   AND :PARA_CODE6 IS NULL
   AND :PARA_CODE7 IS NULL
   AND :PARA_CODE8 IS NULL
   AND :PARA_CODE9 IS NULL
   AND :PARA_CODE10 IS NULL