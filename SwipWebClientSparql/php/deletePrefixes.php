<?php
session_start();
include "fonctions.php";
include "codesBD.php";

if (isset($_SESSION['id']) &&
	isset($_SESSION['mdp']) &&
	isset($_POST['uri']) &&
	verifAcces($_SESSION['id'], $_SESSION['mdp'])) {
	
	// connexion au serveur mysql
	mysql_connect(SERVER, LOGIN, PASSWORD) or die ('Error in connection to mysql : '.mysql_error);
	
	// sélection de la base de données utilisée
	mysql_select_db(BASE) or die('Unable to select data base ' . BASE);
	
	// requête SQL listant les préfixes
	$sqlQuery = "delete from prefixes where URI = '".$_POST['uri']."'";
	
	// exécution de la requête SQL
	mysql_query($sqlQuery) or die('SQL error !<br />'.$sqlQuery.'<br />'.mysql_error());
	
	mysql_close();// fermeture de la connexion
} else {
	echo "You have to be logged as an administrator to delete prefixes...";
}
?>