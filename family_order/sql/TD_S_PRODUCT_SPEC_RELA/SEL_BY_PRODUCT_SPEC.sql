--IS_CACHE=Y
select a.PROD_SPEC_TYPE,
       a.PRODUCT_ID,
       to_char(a.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       to_char(a.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       to_char(a.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       a.UPDATE_STAFF_ID,
       a.UPDATE_DEPART_ID,
       a.REMARK
  from td_s_product_spec_rela a
 where prod_spec_type = :PROD_SPEC_TYPE
   and sysdate between a.start_date AND a.end_date