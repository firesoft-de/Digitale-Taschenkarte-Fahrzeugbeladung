<?php
	$dbFile = fopen("db_Version.txt",'r');
	
	$dbVersion = fgets($dbFile);
	
	fclose($dbFile);
	
	echo $dbVersion;
?>