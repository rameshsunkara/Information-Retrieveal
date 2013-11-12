package edu.louisiana.cacs.csce561.assignment2;

import java.io.File;
import java.util.Map;
import java.util.Scanner;

import edu.louisiana.cacs.csce561.assignment2.model.Term;
import edu.louisiana.cacs.csce561.assignment2.util.Configurator;

/**
 * <p>Main class which calls required constructors like 
 *      1. Incidence Matrix Constructors
 *      2. Inverted Index Constructors.
 *      
 *  </p>
 *  <p>It also configures the system by reading the inforetrieval.properties.<p>
 * @author rsunkara
 * @since October 2,2013
 *
 */
public class Main {

	public static void main(String []args){
		Configurator xConfigurator = new Configurator();
		String xPropertiesFilePath = System.getProperty("user.dir") + File.separator
				+ "src" + File.separator + "main" + File.separator
				+ "resources" + File.separator + "inforetrieval.properties";
		//If any error in system configuration, stop the system
		if(!xConfigurator.loadConfigValues(xPropertiesFilePath))
			return;
		
		//Construct the term-document constructor
		TermDocumentMatrixConstructor xMatrixConstructor = new TermDocumentMatrixConstructor(xConfigurator);
		Map<String,Term> termMap = xMatrixConstructor.constructMatrix();
		
		Scanner scan = new Scanner(System.in);
		String searchQuery = null;
		do{
			System.out.println("Enter the search query. If you want to exit type XXX");
			searchQuery = scan.nextLine();
			if(searchQuery.equalsIgnoreCase("XXX"))
				break;
			
			SearchFiles searchFiles = new SearchFiles(xConfigurator, termMap,false);
			searchFiles.findRSV(searchQuery);
		}while(true);
		
		
		EngineEvaluator eval = new EngineEvaluator(xConfigurator,termMap);
		eval.evaluate();
	}
}
