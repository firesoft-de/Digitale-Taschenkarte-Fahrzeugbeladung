<?php
	$dbFile = fopen("db_Version.txt");
	
	$dbVersion = fgets($dbFile);
	
	fclose($dbFile);
	
	print("Current DB Version:\n");
	print($dbVersion);
?>