<?php
/***********************************
 * Connexions à la base de données *
 ***********************************/
include 'codesBD.php';

function dbConnect() {
	global $isConnectedToSqlServer;
	global $dataBaseSelected;
	
	// connexion au serveur mysql
	$isConnectedToSqlServer = mysql_connect(SERVER, LOGIN, PASSWORD) or die ('Error in connection to mysql : '.mysql_error);
	
	// sélection de la base de données utilisée
	if ($isConnectedToSqlServer)
		$dataBaseSelected = mysql_select_db(BASE) or die('Unable to select data base ' . BASE);
	else
		$dataBaseSelected = false;
	
	return $isConnectedToSqlServer && $dataBaseSelected;
}

function processSqlQuery($sqlQuery, $handler) {
	global $isConnectedToSqlServer;
	global $dataBaseSelected;
	
	if (!($isConnectedToSqlServer && $dataBaseSelected)) {
		dbConnect();
	}
	
	if ($isConnectedToSqlServer && $dataBaseSelected) {
		// exécution de la requête SQL
		$resp = mysql_query($sqlQuery) or die('SQL error !<br />'.$sqlQuery.'<br />'.mysql_error());
		
		// traitement de la réponse
		$handler($resp);
	}
}
?>