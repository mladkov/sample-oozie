INSERT OVERWRITE TABLE ${DbTarget}.${TableTarget}
  SELECT * FROM ${DbSource}.${TableSource};
