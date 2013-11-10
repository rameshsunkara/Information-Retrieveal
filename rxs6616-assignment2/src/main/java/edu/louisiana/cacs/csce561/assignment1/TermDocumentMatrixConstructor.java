package edu.louisiana.cacs.csce561.assignment1;

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

import edu.louisiana.cacs.csce561.assignment1.exception.IRException;
import edu.louisiana.cacs.csce561.assignment1.model.Document;
import edu.louisiana.cacs.csce561.assignment1.model.Term;
import edu.louisiana.cacs.csce561.assignment1.util.Configurator;
import edu.louisiana.cacs.csce561.assignment1.util.MyUtilities;

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

	public void constructMatrix() {
		try {
			preProcess();
			process();
			postProcess();
		} catch (IRException e) {
			m_logger.error(e.getMessage(),e);
		}
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
		List<String> xTermList = new ArrayList<String>();
		try {
			xCurrentDocReader = new BufferedReader(new FileReader(xDocument));
			String xCurrDoc = null;
			while ((xCurrDoc = xCurrentDocReader.readLine()) != null) {
				String[] parsedStrings = xCurrDoc.split("[\\s.]");
				for (String s : parsedStrings) {
					s = s.toLowerCase();
					xTermList.add(s);
					Term xCurrentTerm = null;
					if (m_TermMap.containsKey(s)) {
						xCurrentTerm = m_TermMap.get(s);
					} else {
						xCurrentTerm = new Term();
						xCurrentTerm.setName(s);
						xCurrentTerm
								.setIncidenceMatrixValues(new int[p_docSize]);
					}
					xCurrentTerm.getDocumentList().add(xDocument.getName());
					xCurrentTerm.setIndice(p_DocIndex, 1);
					m_TermMap.put(s, xCurrentTerm);
				}
			}
			xCurrentDoc.setRawTermList(xTermList);
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

	/**
	 * Writes the term-document incidence matrix to file
	 * @throws IRException
	 */

	private void postProcess() throws IRException {
		m_logger.trace("Total no.of terms:"+m_TermMap.size());
		PrintWriter xFileWriter = null;
		try {
			String outputfile = m_configurator.get_term_document_file_path();
			xFileWriter = new PrintWriter(new File(outputfile));
			xFileWriter.print("\t\t\t");
			for(int i=0;i<m_DocFiles.length;i++){
				xFileWriter.format("\t %10s ",m_DocFiles[i].getName());
			}
			xFileWriter.println();
			Set<Entry<String, Term>> termSet = m_TermMap.entrySet();
			Iterator<Entry<String, Term>> itr = termSet.iterator();
			while(itr.hasNext()){
				Entry<String, Term> tt=itr.next();
				Term t = tt.getValue();
				xFileWriter.format("%15s",t.getName());
				for(int j=0;j<t.getIncidenceMatrixValues().length;j++){
					xFileWriter.format("\t %10d",t.getIncidenceMatrixValues()[j]);
				}
				xFileWriter.println("");
			}		
		} catch (FileNotFoundException e) {
			m_logger.error("Unable to write to output file",e);
			throw new IRException("Unable to write to  output  file");
		}finally{
			xFileWriter.flush();
			xFileWriter.close();
		}	
	}
}
