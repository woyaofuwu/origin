--IS_CACHE=Y
SELECT EPARCHY_CODE, TAG_CODE, TAG_NAME, SUBSYS_CODE, TAG_INFO, TAG_CHAR, to_char(TAG_NUMBER) TAG_NUMBER, to_char(TAG_DATE,'yyyy-mm-dd hh24:mi:ss') TAG_DATE, to_char(TAG_SEQUID) TAG_SEQUID, USE_TAG, to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, REMARK, to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID
  FROM td_s_tag
 WHERE eparchy_code=:EPARCHY_CODE
   AND tag_code=:TAG_CODE
   AND use_tag=:USE_TAG