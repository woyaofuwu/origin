select /*+ first_rows(1) */ *
from (
  select /*+ index(u IDX_TF_F_USER_SN) leading(u) hash_nl(u, city, c, p, r, s) */ 
        DECODE(SUBSTR(u.USER_STATE_CODESET, 1, 1),
              '0',
              '00',
              '1',
              '02',
              '2',
              '02',
              '3',
              '02',
              '4',
              '02',
              '5',
              '02',
              '7',
              '02',
              '6',
              '04',
              '8',
              '03',
              '9',
              '04',
              'A',
              '01',
              'B',
              '01',
              'E',
              '04',
              'F',
              '03',
              'G',
              '01',
              'I',
              '02',
              'J',
              '02',
              'K',
              '02',
              'L',
              '02',
              'M',
              '02',
              'N',
              '00',
              'O',
              '02',
              'P',
              '02',
              'Q',
              '02',
              '05') USER_STATE_CODESET, 
         u.remove_tag, 
         u.user_id, 
         u.user_passwd, 
         u.acct_tag,
         c.cust_id, 
         c.cust_name, 
         p.sex, 
         DECODE(c.pspt_type_code,'1','0',
         c.pspt_type_code) pspt_type_code, 
         c.pspt_id, 
         c.score_value, 
         c.eparchy_code, 
         u.serial_number, 
         TO_CHAR(s.START_DATE, 'yyyymmddhhmmss') STATUS_CHG_TIME, 
         r.imsi, 
         '898' province_code, 
         to_char(U.OPEN_DATE,'YYYYMMDDHH24MISS') OPR_TIME, decode(u.DEVELOP_DEPART_ID,'','0',u.DEVELOP_DEPART_ID) CHANNEL_CODE
  from  ucr_crm1.tf_f_user u
        ,uop_crm1.tf_f_customer c
        ,uop_crm1.tf_f_cust_person p
        ,uop_crm1.tf_f_user_res r
        ,uop_crm1.tf_f_user_svc s
  where  u.serial_number = :SERIAL_NUMBER
  and    u.open_date = (
          select /*+ first_rows(1) */open_date from (
            select /*+ index(t IDX_TF_F_USER_SN) */t.open_date 
            from uop_crm1.tf_f_user t
            where t.serial_number = :SERIAL_NUMBER 
            order by t.remove_tag, t.open_date
          ) 
          where rownum < 2
         )
  and    u.cust_id = c.cust_id
  and    c.partition_id = mod(u.cust_id, 10000)
  and    c.cust_id = p.cust_id
  and    r.partition_id = mod(u.user_id, 10000)
  and    r.user_id = u.user_id
  and    r.res_type_code = '1'
  and    r.end_date > sysdate
  and    s.partition_id  = mod(u.user_id, 10000)
  and    s.user_id = u.user_id
  order by s.start_date desc
)
where rownum < 2