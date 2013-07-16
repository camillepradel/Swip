<?php
include 'util/sessions.php';
	
initConnections();

if ($isConnected && isset($_POST['uri']) && isset($_POST['prefix'])) {
	dbConnect();
	
	// requête SQL listant les préfixes
	$sqlQuery = "insert into prefixes values('".$_POST['uri']."', '".$_POST['prefix']."')";
	
	// exécution de la requête SQL
	processSqlQuery($sqlQuery, function() {
		mysql_close();// fermeture de la connexion
	});
} else {
	echo "You have to be logged as an administrator to add prefixes...";
}
?>