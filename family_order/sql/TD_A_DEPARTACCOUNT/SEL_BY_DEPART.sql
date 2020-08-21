--IS_CACHE=Y
select a.depart_id,
       b.bank_code,
       b.bank,
       b.bank_acct_no,
       b.pay_name,
       a.depart_name,
       a.depart_kind_code,
       a.parent_depart_id,
       a.order_no,
       b.assure_no,
       b.para_code1,
       d.super_bank
FROM td_m_depart a,td_a_departaccount b,td_b_bank c,td_s_superbank d
WHERE a.depart_id=b.depart_id(+) AND a.validflag='0'
  AND (a.depart_kind_code=:DEPART_KIND_CODE1 OR a.depart_kind_code=:DEPART_KIND_CODE2)
  AND b.bank_code=c.bank_inner_code(+)
  AND c.super_bank_code=d.super_bank_code(+)