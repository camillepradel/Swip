<?php
include 'util/sessions.php';
	
initConnections();

if ($isConnected && isset($_POST['uri'])) {
	dbConnect();
	
	// requête SQL listant les préfixes
	$sqlQuery = "delete from prefixes where URI = '".$_POST['uri']."'";
	
	// exécution de la requête SQL
	processSqlQuery($sqlQuery, function() {
		mysql_close();// fermeture de la connexion
	});
} else {
	echo "You have to be logged as an administrator to delete prefixes...";
}
?>