package edu.louisiana.cacs.csce561.assignment1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.louisiana.cacs.csce561.assignment1.util.Configurator;
import edu.louisiana.cacs.csce561.assignment1.util.MyUtilities;
import edu.louisiana.cacs.csce561.assignment1.vo.Document;
import edu.louisiana.cacs.csce561.assignment1.vo.Term;

public class TermDocumentMatrixConstructor {

	private static Log m_logger = LogFactory
			.getLog(TermDocumentMatrixConstructor.class);

	private Configurator m_configurator = null;

	private List<Document> m_DocumentList = null;

	private Map<String, Term> m_TermList = null;

	private File[] m_DocFiles = null;

	public TermDocumentMatrixConstructor(Configurator p_configurator) {
		m_configurator = p_configurator;
	}

	public void constructMatrix() {
		preProcess();
		process();
		postProcess();
	}

	private void preProcess() {
		m_logger.trace("In preProcess()");
		m_DocFiles = MyUtilities.getDocumentList(m_configurator
				.get_document_input_dir());
		if (m_DocFiles.length > 0) {
			m_DocumentList = new ArrayList<Document>();
			m_TermList = new HashMap<String, Term>();
		}
		m_logger.trace("Exit preProcess()");
	}

	public void process() {
		for (int xDocIndex = 0; xDocIndex < m_DocFiles.length; xDocIndex++) {
			m_DocumentList.add(parseDocument(m_DocFiles[xDocIndex], xDocIndex,
					m_DocFiles.length));
		}
	}
	
	private Document parseDocument(File xDocument, int p_DocIndex, int p_docSize) {
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
					if (m_TermList.containsKey(s)) {
						xCurrentTerm = m_TermList.get(s);
					} else {
						xCurrentTerm = new Term();
						xCurrentTerm.setName(s);
						xCurrentTerm
								.setIncidenceMatrixValues(new int[p_docSize]);
					}
					xCurrentTerm.getDocumentList().add(xDocument.getName());
					xCurrentTerm.setIndice(p_DocIndex, 1);
					m_TermList.put(s, xCurrentTerm);
				}
			}
			xCurrentDoc.setRawTermList(xTermList);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				xCurrentDocReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return xCurrentDoc;
	}

	private void postProcess() {
		System.out.println(m_TermList);
		System.out.println(m_TermList.size());
	}
}
