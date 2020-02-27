<!--#
# Yet Another UserAgent Analyzer
# Copyright (C) 2013-2020 Niels Basjes
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an AS IS BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
-->
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

	    die("\nDestination directory '$destinationDir' is not valid...\n\n");
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
