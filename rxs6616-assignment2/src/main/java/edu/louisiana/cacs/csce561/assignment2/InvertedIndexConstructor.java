package edu.louisiana.cacs.csce561.assignment2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.louisiana.cacs.csce561.assignment2.exception.IRException;
import edu.louisiana.cacs.csce561.assignment2.model.Document;
import edu.louisiana.cacs.csce561.assignment2.model.Term;
import edu.louisiana.cacs.csce561.assignment2.util.Configurator;
import edu.louisiana.cacs.csce561.assignment2.util.MyUtilities;

/**
 * <p>
 * It constructs the inverted index and prints it to the configured 
 * output file.
 * </p>
 * @author rsunkara
 * @since October 2, 2013
 *
 */
public class InvertedIndexConstructor {

	private static Log m_logger = LogFactory
			.getLog(InvertedIndexConstructor.class);

	//Holds all the system properties
	private Configurator m_configurator = null;

	//List of documents supplied
	private List<Document> m_DocumentList = null;

	//Term Map
	private Map<String, Term> m_TermMap = null;

	//Represents input documents
	private File[] m_DocFiles = null;
	
	//Stores stop words
	private List<String> m_StopWordList = null;

	public InvertedIndexConstructor(Configurator p_configurator) {
		m_configurator = p_configurator;
	}

	public void constructInvertedIndex() {
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
	 * And also loads the stop words
	 * @throws IRException 
	 */
	private void preProcess() throws IRException {
		m_logger.trace("In preProcess()");
		m_DocFiles = MyUtilities.getDocumentList(m_configurator
				.get_document_input_dir());
		m_StopWordList = loadStopWords(m_configurator.get_regex_stop_words());
		m_logger.debug("Stop Words:"+m_StopWordList);
		if (m_DocFiles.length > 0) {
			m_DocumentList = new ArrayList<Document>();
			m_TermMap = new TreeMap<String, Term>();			
		}
		m_logger.trace("Exit preProcess()");
	}

	/**
	 * Reads the stop words
	 * @return
	 * @throws IRException
	 */
	private List<String> loadStopWords(String p_regex) throws IRException {
		m_logger.trace("In loadStopWords()");
		BufferedReader xStopWordReader = null;
		String xCurrWord = null;
		m_StopWordList = new ArrayList<String>();
		try{
			xStopWordReader = new BufferedReader(new FileReader(m_configurator.get_stopword_file_path()));
			while ((xCurrWord = xStopWordReader.readLine()) != null) {
				String[] parsedStrings = xCurrWord.split(p_regex);
				for (String s : parsedStrings) {
					s = s.toLowerCase();
					m_StopWordList.add(s);
				}
			}
		}catch(FileNotFoundException e){
			m_logger.error("Unable to find stop words file",e);
			throw new IRException("Unable to find stop words file");
		} catch (IOException e) {
			m_logger.error("Unable to load stop words file",e);
			throw new IRException("Unable to load stop words file");
		}finally{
			if(xStopWordReader!=null){
				try {
					xStopWordReader.close();
				} catch (IOException e) {
					m_logger.fatal("Memory leak",e);
					throw new IRException("Memory Leak");
				}
			}
		}
		m_logger.trace("Exit loadStopWords()");
		return m_StopWordList;
	}

	/**
	 * Parses each document found in the input directory
	 * @throws IRException 
	 */
	public void process() throws IRException {
		for (int xDocIndex = 0; xDocIndex < m_DocFiles.length; xDocIndex++) {
			m_DocumentList.add(parseDocument(m_DocFiles[xDocIndex], xDocIndex,
					m_DocFiles.length));
		}
	}
	
	/**
	 * Parses the document and finds the frequency of terms and its documents associated.
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
					if(m_StopWordList.contains(s)){
						continue;
					}
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
	 * Writes the inverted index to file
	 * @throws IRException
	 */
	private void postProcess() throws IRException {
		PrintWriter xFileWriter = null;
		try {
			String outputfile = m_configurator.get_inverted_index_file_path();
			xFileWriter = new PrintWriter(new File(outputfile));
			Set<Entry<String, Term>> termSet = m_TermMap.entrySet();
			Iterator<Entry<String, Term>> itr = termSet.iterator();
			while(itr.hasNext()){
				Entry<String, Term> tt=itr.next();
				Term t = tt.getValue();
				xFileWriter.format("%15s[%2d]",t.getName(),t.getDocumentList().size());
				Iterator<String> docItr = t.getDocumentList().iterator();
				while(docItr.hasNext()){
					xFileWriter.format("\t--> %10s",docItr.next());
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
