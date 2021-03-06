select /*+ ORDERED USE_NL(B A C) INDEX( B IDX_TF_F_USER_VPN_TYPE) */
        a.user_id para_code1,a.serial_number para_code2,b.vpn_name para_code3,
       a.brand_code para_code4,a.city_code para_code5,
        to_char(a.open_date,'yyyy-mm-dd hh24:mi:ss') para_code6,
       to_char(a.destroy_time,'yyyy-mm-dd hh24:mi:ss') para_code7,
       decode(sign(c.end_date-sysdate),-1,'1','0') para_code8,
       '' para_code9,'' para_code10,
       '' para_code11,'' para_code12,'' para_code13,
       '' para_code14,'' para_code15,'' para_code16,'' para_code17,'' para_code18,
       '' para_code19,'' para_code20,'' para_code21,'' para_code22,'' para_code23,
       '' para_code24,'' para_code25,'' para_code26,'' para_code27,'' para_code28,
       '' para_code29,'' para_code30,'' start_date,'' end_date,'' eparchy_code,'' remark,
       '' update_staff_id,'' update_depart_id,'' update_time,
       '' subsys_code,0 param_attr,'' param_code,'' param_name
from tf_f_user_vpn b,tf_f_user a,tf_f_relation_uu c
where ((:PARA_CODE1 = '0' AND b.vpn_name like '%'||:PARA_CODE2||'%')
     OR (:PARA_CODE1 = '1' AND a.serial_number=:PARA_CODE2))
    AND b.user_id=a.user_id
    AND b.vpmn_type in ('0','2','3')
    AND a.user_id=c.user_id_a
    	and c.relation_type_code in ('20','21','25')
    	and c.partition_id=mod(c.user_id_b,10000)
    AND c.start_date<c.end_date
    AND (:PARA_CODE3 = '' OR :PARA_CODE3 IS NULL)
    AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)
    AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
    AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
    AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
    AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
    AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
    AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)