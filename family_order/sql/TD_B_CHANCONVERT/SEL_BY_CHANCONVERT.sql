--IS_CACHE=Y
SELECT * FROM TD_B_CHANCONVERT chan WHERE 1 = 1
 AND chan.CONVERT_ID = :CONVERT_ID
 AND chan.CHAN_ID = :CHAN_ID
 AND chan.EPARCHY_CODE = :EPARCHY_CODE
 AND chan.CONFIG_TYPE = :CONFIG_TYPE
 AND chan.START_DATE >= to_date(:START_DATE, 'yyyy-mm-dd')
 AND chan.END_DATE <= to_date(:END_DATE, 'yyyy-mm-dd')