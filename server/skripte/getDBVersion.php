<?php
	//Header setzen
	header('Content-Type: text/plain');

	$dbFile = fopen("db_Version.txt",'r');
	
	$dbVersion = fgets($dbFile);
	
	fclose($dbFile);
	
	echo $dbVersion;
?>