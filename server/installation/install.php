<?php			

	//Seite aufsetzen
	echo('<!doctype html> <html lang="de"> <head> <title>Digitale Taschenkarte - Beladung</title> <meta charset="utf-8" content="text/html"> </head>');

	echo('<body>');
		
	echo('<b>Willkommen zur Installation der Datenbank für die Digitale Taschenkarte - Beladung</b> <br/> <br/>');
	
	echo('- Lade Daten aus der config <br/>');

	//Datenbankzugangsdaten
	$dbFile = fopen(__DIR__ .  "/config/access.txt",'r');

	$db_server = fgets($dbFile);	
	$db_name = fgets($dbFile);	
	$db_user = fgets($dbFile);
	$db_password = fgets($dbFile);

	fclose($dbFile);
	
	$db_server = trim(preg_replace('/\s+/', ' ', $db_server));
	$db_name = trim(preg_replace('/\s+/', ' ', $db_name));
	$db_user = trim(preg_replace('/\s+/', ' ', $db_user));
	$db_password = trim(preg_replace('/\s+/', ' ', $db_password));

	echo('- Initialisiere Datenbankobjekt <br/>');

	//Zugangsobjekt erzeugen
	$pdo = new PDO('mysql:host=' . $db_server.';dbname=' . $db_name, $db_user , $db_password);

	//SQL Query zum Abfragen der Daten konstruieren
	$queryString;

	
	echo('- Erstelle Datenbanktabelle equipment <br/>');

	//Tabelle equipment anlegen
	$queryString = "CREATE TABLE `equipment` (`id` int(11) NOT NULL, `categoryId` int(11) NOT NULL, `positionID` int(11) NOT NULL,
					`groupId` int(11) NOT NULL DEFAULT '0', `name` text NOT NULL, `setName` text NOT NULL, `description` text NOT NULL,
					`position` text NOT NULL, `notes` text NOT NULL, `keywords` text NOT NULL, `version` int(11) NOT NULL, PRIMARY KEY (`id`),
					UNIQUE KEY `id` (`id`)) ENGINE=InnoDB DEFAULT CHARSET=latin1";

	//Datenbankabfrage vorbereiten
	$stmt=$pdo->prepare($queryString);

	//Statment schließen
	$stmt->closeCursor();

	//SQL Abfrage ausführen
	$stmt->execute();



	echo('- Erstelle Datenbanktabelle tray <br/>');

	//Tabelle tray anlegen
	$queryString = "CREATE TABLE `tray` (`id` int(11) NOT NULL, `groupId` int(11) NOT NULL, `name` text NOT NULL, `description` text NOT NULL,
					`descriptionTwo` text NOT NULL, `positions` text NOT NULL, `version` int(11) NOT NULL, PRIMARY KEY (`id`),
					UNIQUE KEY `id` (`id`)) ENGINE=InnoDB DEFAULT CHARSET=latin1";

	//Datenbankabfrage vorbereiten
	$stmt=$pdo->prepare($queryString);

	//Statment schließen
	$stmt->closeCursor();

	//SQL Abfrage ausführen
	$stmt->execute();


	echo('- Erstelle Datenbanktabelle positionimage <br/>');

	//Tabelle positionimage anlegen
	$queryString = "CREATE TABLE `positionimage` (`id` int(11) NOT NULL, `path` text NOT NULL, `categoryId` int(11) NOT NULL, `groupId` int(11) NOT NULL, 
					`version` int(11) NOT NULL, PRIMARY KEY (`id`), UNIQUE KEY `id` (`id`)) ENGINE=InnoDB DEFAULT CHARSET=latin1";

	//Datenbankabfrage vorbereiten
	$stmt=$pdo->prepare($queryString);

	//Statment schließen
	$stmt->closeCursor();

	//SQL Abfrage ausführen
	$stmt->execute();


	echo('- Erstelle Datenbanktabelle groupx <br/>');

	//Tabelle groupx anlegen
	$queryString = "CREATE TABLE `groupx` (`id` int(11) NOT NULL, `name` text COLLATE latin1_german1_ci NOT NULL, `version` int(11) NOT NULL,
					PRIMARY KEY (`id`), UNIQUE KEY `id` (`id`)) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_german1_ci";

	//Datenbankabfrage vorbereiten
	$stmt=$pdo->prepare($queryString);

	//Statment schließen
	$stmt->closeCursor();

	//SQL Abfrage ausführen
	$stmt->execute();
	

	echo('<br/> <br/> <b>Installation abgeschlossen</b>');
	echo('</body> </html>');
?>