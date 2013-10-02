package edu.louisiana.cacs.csce561.assignment1;

import java.io.File;

import edu.louisiana.cacs.csce561.assignment1.util.Configurator;

public class Main {

	public static void main(String []args){
		
		Configurator xConfigurator = new Configurator();
		String xPropertiesFilePath = System.getProperty("user.dir") + File.separator
				+ "src" + File.separator + "main" + File.separator
				+ "resources" + File.separator + "inforetrieval.properties";
		if(!xConfigurator.loadConfigValues(xPropertiesFilePath))
			return;
		
		TermDocumentMatrixConstructor xMatrixConstructor = new TermDocumentMatrixConstructor(xConfigurator);
		xMatrixConstructor.constructMatrix();
		
		//InvertedIndexConstructor xInvertedIndexConstructor = new InvertedIndexConstructor(xConfigurator);
		//xInvertedIndexConstructor.constructInvertedIndex();
	}

}
