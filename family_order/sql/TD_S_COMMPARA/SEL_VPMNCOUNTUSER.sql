/*1钻石卡2本地钻石3金卡4本地金卡5银卡6本地银卡7贵宾卡8本地贵宾卡*/
SELECT user_id_a para_code1, vpn_no para_code2, vpn_name para_code3,sum(count0) para_code4,
       sum(count1) para_code5, sum(count2) para_code6, sum(count3) para_code7, sum(count4) para_code8, 
       sum(count5) para_code9, sum(count6) para_code10,sum(count7) para_code11,sum(count8) para_code12,
       '' para_code13,'' para_code14,'' para_code15,
       '' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
       '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
       '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,'' remark,
       '' start_date,'' end_date,'' eparchy_code,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
from 
(
    select a.user_id_a,c.vpn_no,c.vpn_name,count(a.user_id_b) count0,0 count1,0 count2,0 count3,0 count4,0 count5,0 count6,0 count7,0 count8
      from tf_f_user_vpn c,tf_f_relation_uu a
     where ((SYSDATE BETWEEN a.start_date AND a.end_date) OR (a.end_date is null))
       and c.vpmn_type = '0'
       and c.user_id = a.user_id_a(+) 
       and a.relation_type_code = '20'
       and c.group_area = :PARA_CODE1
       and (:PARA_CODE2 is null or c.vpn_no = :PARA_CODE2)       
        AND (:PARA_CODE3 IS NOT NULL OR :PARA_CODE3 IS NULL)
        AND (:PARA_CODE4 IS NOT NULL OR :PARA_CODE4 IS NULL)
        AND (:PARA_CODE5 IS NOT NULL OR :PARA_CODE5 IS NULL)
        AND (:PARA_CODE6 IS NOT NULL OR :PARA_CODE6 IS NULL)
        AND (:PARA_CODE7 IS NOT NULL OR :PARA_CODE7 IS NULL)
        AND (:PARA_CODE8 IS NOT NULL OR :PARA_CODE8 IS NULL)
        AND (:PARA_CODE9 IS NOT NULL OR :PARA_CODE9 IS NULL)
        AND (:PARA_CODE10 IS NOT NULL OR :PARA_CODE10 IS NULL)
    group by a.user_id_a,c.vpn_no,c.vpn_name
    union all     
    select a.user_id_a,c.vpn_no,c.vpn_name,0 count0,SUM(DECODE(b.class_id,'4',1,0)) count1,0 count2,SUM(DECODE(b.class_id,'3',1,0)) count3,0 count4,SUM(DECODE(b.class_id,'2',1,0)) count5,0 count6,SUM(DECODE(b.class_id,'1',1,0)) count7, 0 count8
      from tf_f_relation_uu a,tf_f_cust_vip b,tf_f_user_vpn c 
     where a.user_id_b = b.user_id 
       and ((SYSDATE BETWEEN a.start_date AND a.end_date) OR (a.end_date is null))
       and b.vip_type_code = '0'
       and b.remove_tag = '0'
       and a.relation_type_code = '20'
       and c.vpmn_type = '0'
       and a.user_id_a = c.user_id
       and c.group_area = :PARA_CODE1
       and (:PARA_CODE2 is null or c.vpn_no = :PARA_CODE2)       
    group by a.user_id_a,c.vpn_no,c.vpn_name
    union all
    select a.user_id_a,c.vpn_no,c.vpn_name,0 count0,0 count1,SUM(DECODE(b.class_id,'4',1,0)) count2,0 count3, SUM(DECODE(b.class_id,'3',1,0)) count4,0 count5,SUM(DECODE(b.class_id,'2',1,0)) count6,0 count7,SUM(DECODE(b.class_id,'1',1,0)) count8
      from tf_f_relation_uu a,tf_f_cust_vip b,tf_f_user_vpn c 
     where a.user_id_b = b.user_id 
       and ((SYSDATE BETWEEN a.start_date AND a.end_date) OR (a.end_date is null))
       and b.vip_type_code = '0'
       and b.remove_tag = '0'
       and a.relation_type_code = '20'
       and c.vpmn_type = '0'
       and a.user_id_a = c.user_id
       and c.group_area = :PARA_CODE1
       and b.city_code = c.group_area
       and (:PARA_CODE2 is null or c.vpn_no = :PARA_CODE2)       
    group by a.user_id_a,c.vpn_no,c.vpn_name
) group by user_id_a,vpn_no,vpn_name