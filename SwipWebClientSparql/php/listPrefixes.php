<?php
include 'util/sessions.php';

dbConnect();

// requête SQL listant les préfixes
$sqlQuery = "select * from prefixes order by prefix";

// exécution de la requête SQL
processSqlQuery($sqlQuery, function ($req) {
	$list = array();

	while ($data = mysql_fetch_assoc($req)) // pour chaque entrée trouvée
	{
		$list[$data['URI']] = $data['prefix'];
	}

	mysql_close();// fermeture de la connexion

	echo json_encode($list);// traduction en JSON de la liste des préfixes
});
?>