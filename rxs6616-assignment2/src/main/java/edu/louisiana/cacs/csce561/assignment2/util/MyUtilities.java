package edu.louisiana.cacs.csce561.assignment2.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Contains utility methods used in this application.
 * @author rsunkara
 *
 */
public class MyUtilities {
	
	private static Log m_logger = LogFactory.getLog(MyUtilities.class);
	
	/**
	 * Loads the given properties file into Properties object.
	 * @param p_PropertiesFilePath
	 * @return
	 */
	public static Properties loadProperties(String p_PropertiesFilePath) {
		m_logger.debug("Enter loadProperties()");
		if (m_logger.isTraceEnabled()) {
			m_logger.trace("File passed:" + p_PropertiesFilePath);
		}
		Properties xProperties = null;
		InputStream xPropertiesSteam = null;
		try {
			xPropertiesSteam = new FileInputStream(p_PropertiesFilePath);
			xProperties = new Properties();
			xProperties.load(xPropertiesSteam);
		} catch (FileNotFoundException e) {
			m_logger.error("FileNotFoundException caught while loading:"
					+ p_PropertiesFilePath, e);
			return null;
		} catch (IOException e) {
			m_logger.error("IOException caught while loading:"
					+ p_PropertiesFilePath, e);
			return null;
		} finally {
			if (xPropertiesSteam != null)
				try {
					xPropertiesSteam.close();
				} catch (IOException e) {
					m_logger.error(
							"Potential Memory leak, Exception occured while closing stream",
							e);
				}
		}
		if(m_logger.isTraceEnabled()){
			m_logger.trace("No.of properties loaded:"+xProperties.size());
		}
		m_logger.debug("Exit loadProperties()");
		return xProperties;
	}

	/**
	 * Finds the list of files in the given directory path
	 * @param p_inputDir
	 * @return
	 */
	public static File[] getDocumentList(String p_inputDir){
		File f = new File(p_inputDir);		
		return f.listFiles();
	}	
	
	public static String getDocNameFromFileName(String p_StemmedFilename) {
		return p_StemmedFilename.substring(p_StemmedFilename.indexOf("_")+1, p_StemmedFilename.indexOf("."));
	}
	
	
	public static <K extends Comparable,V extends Comparable> Map<K,V> sortByValues(Map<K,V> map){
        List<Map.Entry<K,V>> entries = new LinkedList<Map.Entry<K,V>>(map.entrySet());
      
        Collections.sort(entries, new Comparator<Map.Entry<K,V>>() {

            public int compare(Entry<K, V> o1, Entry<K, V> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
      
        //LinkedHashMap will keep the keys in the order they are inserted
        //which is currently sorted on natural ordering
        Map<K,V> sortedMap = new LinkedHashMap<K,V>();
      
        for(Map.Entry<K,V> entry: entries){
            sortedMap.put(entry.getKey(), entry.getValue());
        }
      
        return sortedMap;
    }
}
