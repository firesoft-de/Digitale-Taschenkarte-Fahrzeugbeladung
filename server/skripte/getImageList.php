<?php

/* 	Diese App stellt die Beladung von BOS Fahrzeugen in digitaler Form dar.
    Copyright (C) 2017  David Schlossarczyk

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    For the full license visit https://www.gnu.org/licenses/gpl-3.0. */
	
	//Header setzen
	header('Content-Type: application/json');	
	include 'util.php';
	
	//Datenbankversion abfragen
	$dbFile = fopen("db_Version.txt",'r');
	
	$dbVersion = fgets($dbFile);
	
	fclose($dbFile);
	
	//Mitgegebene Paramter abrufen
	$clientdbVersion = $_GET['dbVersion'];;
	
	//Datenbankzugangsdaten
	$pdo = createDatabaseHandler();
	
	//SQL Query zum Abfragen der Daten
	$queryString = "SELECT * FROM positionimage WHERE version > :clientdbversion AND version <= :dbversion";

	$stmt=$pdo->prepare($queryString);
	
	//Die Benutzereingaben sicher in den Querystring einfügen
	$stmt->bindParam(':clientdbversion', $clientdbVersion, PDO::PARAM_INT);
	$stmt->bindParam(':dbversion', $dbVersion, PDO::PARAM_INT);
	
	$stmt->execute();	
	$results = array();
	
	while($row=$stmt->fetch(PDO::FETCH_ASSOC)){
		$results["OUTPUT"][] = $row;
	}
	
	$json = json_encode($results,JSON_PRETTY_PRINT);
	print($json);
	
?>