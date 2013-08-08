<?php
	if (isset($_FILES['inputFile'])){
		//checks for errors and checks that file is uploaded
		if ($_FILES['inputFile']['error'] == UPLOAD_ERR_OK && is_uploaded_file($_FILES['inputFile']['tmp_name']))
		{
			echo file_get_contents($_FILES['inputFile']['tmp_name']);
		}
		else
		{
			echo 'Unable to load this file...';
		}
	} else {
		echo 'No file found...';
	}
?>