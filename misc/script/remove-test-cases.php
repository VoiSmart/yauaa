<?php

function startsWith($str, $sub) {

   return (substr($str, 0, strlen($sub)) === $sub);
}

function endsWith($str, $sub) {

	return (substr($str, strlen($str) - strlen($sub)) === $sub);
}

function filterLineByLine($inputFilePath, $outputFilePath) {

	$inputFile  = fopen($inputFilePath,  "r") or die("\nUnable to open file '$inputFilePath' for reading!\n\n");

	$insideTestCase = false;
	$output = "";

    while (($line = fgets($inputFile)) !== false) {
        
        $trimmedLine = trim($line);

        if (startsWith($trimmedLine, "#"))
        	continue;

        // Controllo se si sta chiudendo un test
        if ($insideTestCase && startsWith($trimmedLine, "-"))
        	$insideTestCase = false;

        // Potrebbe essersi chiuso un test e riaperto un altro
        if (startsWith($trimmedLine, "- test:"))
        	$insideTestCase = true;

        // Se non sono in un test... copio!
        if (!$insideTestCase)
        	$output .= $line;
    }

    fclose($inputFile);

    // Tolgo gli spazi all'inizio e alla fine
    $output = trim($output);

    // Se il risultato è vuoto (o solo config) salto
    if (strlen($output) === 0 || "config:" === $output)
    	return false;

    // Scrivo rimettendo il "new line" finale
    return (file_put_contents($outputFilePath, $output . "\n") !== false);
}

function performCopy($sourceDir, $destinationDir) {

	if (!is_string($sourceDir) || !is_string($destinationDir)) {

	    die("\nUsage: [php -f] " . $argv[0] . " <source_dir> <destination_dir>\n\n");
	}

	if (!is_dir($sourceDir)) {

	    die("\nSource directory '$sourceDir' is not valid...\n\n");
	}

	if (!is_dir($destinationDir)) {

	    die("\nSource directory '$destinationDir' is not valid...\n\n");
	}

	echo("\nStarting script...\n\n");

	if (!endsWith($sourceDir, "/")) $sourceDir .= "/";
	if (!endsWith($destinationDir, "/")) $destinationDir .= "/";

	echo("Source dir : $sourceDir\n");
	echo("Desti. dir : $destinationDir\n");

	$filesTranslated = 0;

	$sourceFiles = scandir($sourceDir);

	foreach ($sourceFiles as $sourceFile) {

		$sourceFileCompletePath = $sourceDir . $sourceFile;

		if (!endsWith($sourceFile, ".yaml") || !is_file($sourceFileCompletePath))
			continue;

		$destinationFileCompletePath = $destinationDir . $sourceFile;

		if (filterLineByLine($sourceFileCompletePath, $destinationFileCompletePath))
			$filesTranslated++;
	}

	echo("\nFinished ($filesTranslated files translated)!\n\n");
}

performCopy(@$argv[1], @$argv[2]);

?>