#!/bin/bash
echo "Building SMIS..."
mvn clean package -q
echo "Launching SMIS..."
java --module-path target/dependency --add-modules javafx.controls,javafx.fxml -jar target/StudentMIS-1.0.jar
