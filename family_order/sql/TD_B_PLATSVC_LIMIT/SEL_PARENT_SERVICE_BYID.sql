--IS_CACHE=Y
select distinct (SERVICE_ID_L) SERVICE_ID_L
  from td_b_platsvc_limit t
 where t.biz_type_code = :BIZ_TYPE_CODE
   and (t.oper_code = '06' OR t.OPER_CODE = 'ZZ')
