select count(*) recordcount from dual
where :FEE>=(select NVL(tag_number,0) from td_s_tag
 where subsys_code='CSM'
   and tag_code='CS_NUM_RANKCT3ADVPAY'
   and eparchy_code=:EPARCHY_CODE
   and sysdate<end_date)