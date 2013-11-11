package edu.louisiana.cacs.csce561.assignment2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.louisiana.cacs.csce561.assignment2.exception.IRException;
import edu.louisiana.cacs.csce561.assignment2.model.Document;
import edu.louisiana.cacs.csce561.assignment2.model.Term;
import edu.louisiana.cacs.csce561.assignment2.util.Configurator;
import edu.louisiana.cacs.csce561.assignment2.util.MyUtilities;

/**
 * <p>
 * It constructs the term document matrix and prints it to the configured 
 * output file.
 * </p>
 * @author rsunkara
 * @since October 2, 2013
 *
 */
public class TermDocumentMatrixConstructor {

	private static Log m_logger = LogFactory
			.getLog(TermDocumentMatrixConstructor.class);

	//Holds all the system properties
	private Configurator m_configurator = null;

	//List of documents supplied
	private List<Document> m_DocumentList = null;

	//Term Map
	private Map<String, Term> m_TermMap = null;

	//Represents input documents
	private File[] m_DocFiles = null;

	public TermDocumentMatrixConstructor(Configurator p_configurator) {
		m_configurator = p_configurator;
	}

	public Map<String, Term> constructMatrix() {
		try {
			preProcess();
			process();
			postProcess();
			
		} catch (IRException e) {
			m_logger.error(e.getMessage(),e);
		}
		return m_TermMap;
	}

	/**
	 * It fetches the list of input documents.
	 * @throws IRException 
	 */
	private void preProcess() throws IRException {
		m_logger.debug("In preProcess()");
		m_DocFiles = MyUtilities.getDocumentList(m_configurator
				.get_document_input_dir());
		if (m_DocFiles.length > 0) {
			m_DocumentList = new ArrayList<Document>();
			m_TermMap = new HashMap<String, Term>();
		}
		m_logger.debug("Exit preProcess()");
	}

	/**
	 * Parses each document found in the input directory
	 * @throws IRException 
	 */
	public void process() throws IRException{
		for (int xDocIndex = 0; xDocIndex < m_DocFiles.length; xDocIndex++) {
			m_DocumentList.add(parseDocument(m_DocFiles[xDocIndex], xDocIndex,
					m_DocFiles.length));
		}
	}
	
	/**
	 *  Parses the document and finds the documents associated for each term.
	 * @param xDocument
	 * @param p_DocIndex
	 * @param p_docSize
	 * @return
	 * @throws IRException 
	 */
	private Document parseDocument(File xDocument, int p_DocIndex, int p_docSize) throws IRException {
		Document xCurrentDoc = new Document();
		xCurrentDoc.setName(xDocument.getName());
		BufferedReader xCurrentDocReader = null;
		String xDocName = null;
		String xTempTerm = null;
		int xTermDocFrequency = 0;
		double xNormTermDocFrequency = 0;
		try {
			xCurrentDocReader = new BufferedReader(new FileReader(xDocument));
			String xCurrLineInDoc = null;
			xDocName = getDocumentName(xCurrentDocReader.readLine());
			xCurrentDoc.setName(xDocName);
			while ((xCurrLineInDoc = xCurrentDocReader.readLine()) != null) {
				String[] parsedStrings = xCurrLineInDoc.split("[\\s]");
				xTempTerm = parsedStrings[0].toLowerCase();
				xTermDocFrequency = Integer.parseInt(parsedStrings[1]);
				xNormTermDocFrequency = Double.parseDouble(parsedStrings[2]);
				Term xCurrentTerm = null;
				if (m_TermMap.containsKey(xTempTerm)) {
					xCurrentTerm = m_TermMap.get(xTempTerm);
				} else {
					xCurrentTerm = new Term();
					xCurrentTerm.setName(xTempTerm);
					xCurrentTerm
								.setIncidenceMatrixValues(new int[p_docSize]);
					xCurrentTerm
								.setTermDocFrequencies(new int[p_docSize]);
					xCurrentTerm
					.setNormTermDocFrequencies(new double[p_docSize]);
				}
					xCurrentTerm.setIndice(p_DocIndex, 1);
					xCurrentTerm.setTermDocFrequency(p_DocIndex, xTermDocFrequency);
					xCurrentTerm.setNormTermDocFrequency(p_DocIndex, xNormTermDocFrequency);
					m_TermMap.put(xTempTerm, xCurrentTerm);
			}
		} catch (FileNotFoundException e) {
			m_logger.error("Unable to find doc file",e);
			throw new IRException("Unable to find doc file");
		} catch (IOException e) {
			m_logger.error("Unable to load doc file",e);
			throw new IRException("Unable to load doc file");
		} finally {
			try {
				xCurrentDocReader.close();
			} catch (IOException e) {
				m_logger.error("Memory leak",e);
				throw new IRException("Memory leak");
			}
		}
		return xCurrentDoc;
	}

	private String getDocumentName(String p_firstLineInDoc) {
		String[] parsedStrings = p_firstLineInDoc.split("[\\s]");
		return parsedStrings[1].substring(0, parsedStrings[1].indexOf("."));
	}

	/**
	 * Writes the term-document incidence matrix to file
	 * @throws IRException
	 */

	private void postProcess() throws IRException {
		m_logger.trace("Total no.of terms:"+m_TermMap.size());
		PrintWriter xIncidenceValWriter = null;
		PrintWriter xDocFreqValWriter = null;
		PrintWriter xNormDocFreqValWriter = null;
		String emptyString = "";
		try {
			xIncidenceValWriter = new PrintWriter(new File(m_configurator.get_term_document_incidence_file_path()));
			xDocFreqValWriter = new PrintWriter(new File(m_configurator.get_term_document_doc_freq_file_path()));
			xNormDocFreqValWriter = new PrintWriter(new File(m_configurator.get_term_document_norm_doc_file_path()));
			
			xIncidenceValWriter.format("%-15s",emptyString);
			xDocFreqValWriter.format("%-15s",emptyString);
			xNormDocFreqValWriter.format("%-15s",emptyString);
			
			for(int i=0;i<m_DocFiles.length;i++){
				xIncidenceValWriter.format("%-10s ",MyUtilities.getDocNameFromFileName(m_DocFiles[i].getName()));
				xDocFreqValWriter.format("%-10s ",MyUtilities.getDocNameFromFileName(m_DocFiles[i].getName()));
				xNormDocFreqValWriter.format("%-10s ",MyUtilities.getDocNameFromFileName(m_DocFiles[i].getName()));
			}
			
			xIncidenceValWriter.println();
			xDocFreqValWriter.println();
			xNormDocFreqValWriter.println();
			
			Set<Entry<String, Term>> termSet = m_TermMap.entrySet();
			Iterator<Entry<String, Term>> itr = termSet.iterator();
			while(itr.hasNext()){
				Entry<String, Term> tt=itr.next();
				Term t = tt.getValue();
				
				xIncidenceValWriter.format("%-15s",t.getName());
				xDocFreqValWriter.format("%-15s",t.getName());
				xNormDocFreqValWriter.format("%-15s",t.getName());
				
				for(int j=0;j<t.getIncidenceMatrixValues().length;j++){
					xIncidenceValWriter.format("%d%-10s",t.getIncidenceMatrixValues()[j],emptyString);
					xDocFreqValWriter.format("%d%-10s",t.getTermDocFrequencies()[j],emptyString);
					xNormDocFreqValWriter.format("%.2f%-7s",t.getNormTermDocFrequencies()[j],emptyString);
				}
				xIncidenceValWriter.println();
				xDocFreqValWriter.println();
				xNormDocFreqValWriter.println();
			}		
		} catch (FileNotFoundException e) {
			m_logger.error("Unable to write to output file",e);
			throw new IRException("Unable to write to  output  file");
		}finally{
			xIncidenceValWriter.flush();
			xIncidenceValWriter.close();
			
			xDocFreqValWriter.flush();
			xDocFreqValWriter.close();
			
			xNormDocFreqValWriter.flush();
			xNormDocFreqValWriter.close();
		}	
	}

	
}
