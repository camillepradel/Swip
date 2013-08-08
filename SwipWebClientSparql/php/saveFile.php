<?php
include 'util/sessions.php';

initConnections();

if ($isConnected)
{
	if (isset($_POST['content']) && isset($_POST['subdirectory']) && isset($_POST['fileName'])) {
		$filePath = "/stockage/data/".$_POST['subdirectory'].$_POST['fileName'];
		$file = fopen($filePath, "w");
		if (!$file) {
			throw new Exception("Unable to create the file at path : \"".$filePath."\"");
		} else {
			fputs($file, $_POST['content']);
			fclose($file);
		}
	} else {
		throw new Exception('Any content to save...');
	}
} else {
	throw new Exception("You don't have administrator rights....");
}
?>