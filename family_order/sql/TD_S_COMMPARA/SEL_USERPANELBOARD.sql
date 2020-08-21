select to_char(b.BCOUNT) para_code1,
       b.BUSSNO para_code2,
       '' para_code3,
       '' para_code4,
       '' para_code5,
       '' para_code6,
       '' para_code7,
       '' para_code8,
       '' para_code9,
       '' para_code10,
       '' para_code11,
       '' para_code12,
       '' para_code13,
       '' para_code14,
       '' para_code15,
       '' para_code16,
       '' para_code17,
       '' para_code18,
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
       '' update_time,
       '' start_date,
       '' end_date,
       '' eparchy_code,
       '' remark,
       '' update_staff_id,
       '' update_depart_id,
       '' subsys_code,
       0 param_attr,
       '' param_code,
       '' param_name
  from (select count(a.IBSYSID) as bcount, a.bussno
          from tl_b_ucbiztrade a 
         where substr(a.ibsysid, 0, 6) between to_char(:PARA_CODE1) and to_char(:PARA_CODE2)
           and (a.iditemrange = :PARA_CODE3)
           and (a.origdomain = :PARA_CODE4)
           and (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
           and (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
           and (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
           and (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
           and (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)
         group by a.bussno
         order by bcount desc) b
 where rownum < to_number(:PARA_CODE5)