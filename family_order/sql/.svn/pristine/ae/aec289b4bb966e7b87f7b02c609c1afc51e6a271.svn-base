--IS_CACHE=Y
SELECT d.PACKAGE_ID AS PACKAGE_ID, d.ELEMENT_TYPE_CODE AS ELEMENT_TYPE_CODE,
       d.ELEMENT_ID AS ELEMENT_ID, d.LIMIT_TAG AS LIMIT_TAG,
       TO_CHAR(d.START_DATE, 'YYYY-MM-DD HH24:MI:SS') AS START_DATE,
       TO_CHAR(d.END_DATE, 'YYYY-MM-DD HH24:MI:SS') AS END_DATE,
       TO_CHAR(d.UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') AS UPDATE_TIME,
       d.UPDATE_STAFF_ID AS UPDATE_STAFF_ID, d.UPDATE_DEPART_ID AS UPDATE_DEPART_ID,
       d.REMARK AS REMARK
FROM   TD_B_ELEMENT_PACKAGE_LIMIT d
where  d.end_date >= sysdate
  and  d.start_date <= sysdate
  and  (d.eparchy_code = :EPARCHY_CODE or eparchy_code = 'ZZZZ')
  and  d.element_type_code = :ELEMENT_TYPE_CODE
  and  d.element_id > -1
  and  d.limit_tag = :LIMIT_TAG
  and  d.package_id = :PACKAGE_ID