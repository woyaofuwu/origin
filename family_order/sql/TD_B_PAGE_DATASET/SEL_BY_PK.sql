--IS_CACHE=Y
SELECT A.DATASET_ID, B.DATASET_TYPE, B.DATASET_KEY, B.DATASET_METHOD
  FROM TD_B_PAGE_DATASET A, TD_B_DATASET B
 WHERE A.DATASET_ID = B.DATASET_ID
   AND A.PAGE_ID = :PAGE_ID
   AND A.STATE = '1'
   AND B.STATE = '1'
