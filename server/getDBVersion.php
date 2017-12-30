<?php
	$dbFile = fopen("db_Version.txt",'r');
	
	$dbVersion = fgets($dbFile);
	
	fclose($dbFile);
	
	echo "Current DB Version:" . PHP_EOL . $dbVersion;
?>